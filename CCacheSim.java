import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

//import com.sun.javafx.tk.Toolkit;

//import jdk.nashorn.internal.runtime.RecompilableScriptFunctionData;

import java.util.Scanner;

@SuppressWarnings("unchecked") 
public class CCacheSim extends JFrame implements ActionListener{

	private JPanel panelTop, panelLeft, panelRight, panelBottom;
	private JButton execStepBtn, execAllBtn, fileBotton;
	private JComboBox csBox, bsBox, wayBox, replaceBox, prefetchBox, writeBox, allocBox;
	private JFileChooser fileChoose = new JFileChooser();
	
	private JLabel labelTop,labelLeft,rightLabel,bottomLabel,fileLabel,fileAddrBtn, stepLabel1, stepLabel2,
		    csLabel, bsLabel, wayLabel, replaceLabel, prefetchLabel, writeLabel, allocLabel;
	private JLabel results[];//,missRate[],missTime[];


    //参数定义
	private String cachesize[] = { "2KB", "8KB", "32KB", "128KB", "512KB", "2MB" };
	private String blocksize[] = { "16B", "32B", "64B", "128B", "256B" };
	private String way[] = { "直接映象", "2路", "4路", "8路", "16路", "32路" };
	private String replace[] = { "LRU", "FIFO", "RAND" };
	private String pref[] = { "不预取", "不命中预取" };
	private String write[] = { "写回法", "写直达法" };
	private String alloc[] = { "按写分配", "不按写分配" };
	private String typename[] = { "读数据", "写数据", "读指令" };
	private String hitname[] = {"不命中", "命中" };
	private boolean is_step=false;
	
	//右侧结果显示
	private String rightLable[]={"访问总次数：","读指令次数：","读数据次数：","写数据次数："};
	
	//打开文件
	private File file;
	
	//分别表示左侧几个下拉框所选择的第几项，索引从 0 开始
	private int csIndex, bsIndex, wayIndex, replaceIndex, prefetchIndex, writeIndex, allocIndex;
	
	//内部类CacheBlock
	private class CacheBlock {
		int tag;		//address tag 
		boolean dirty;
		long enter_time;
		long used_time;

		public CacheBlock(int tag) {
			this.tag = tag;
			dirty = false;
			enter_time = -1L;
			used_time = -1L;
		}
	}

	//内部类Cache
	private class Cache {
		private CacheBlock cache[][];
		private int cacheSize;		//Cache大小
		private int blockSize;		//块大小
		private int blockNum;		//总块数
		private int blockNumInGroup;//每组中的块数
		private int groupNum;		//组数
		private long groupFIFOTime[];

		public Cache(int csize, int bsize){
			cacheSize = csize;
			blockSize = bsize;
			blockNum = cacheSize / blockSize;
			blockNumInGroup = (int)Math.pow(2, wayIndex);
			groupNum = blockNum / blockNumInGroup;

			cache = new CacheBlock[groupNum][blockNumInGroup];

			for(int i=0;i<groupNum;i++) {
				for(int j=0;j<blockNumInGroup;j++) {
					cache[i][j] = new CacheBlock(-1);
				}
			}

			groupFIFOTime = new long[groupNum];
		}

		public void print_info(){
			System.out.println("CacheSize: "+cacheSize);
			System.out.println("BlockSize: "+blockSize);
			System.out.println("groupNum: "+groupNum);
			System.out.println("blockNumInGroup: "+blockNumInGroup);
		}

		public int getgroupNum(){
			return groupNum;
		}

		public int getblocksize(){
			return blockSize;
		}

		//read, write 函数均只考虑hit的情况
		public boolean read(int tag, int index, int inblockAddr) {
			boolean hit = false;
			if(index>=groupNum){
				System.out.println("Out of range! tag = "+tag+" Index = "+index);
			}
			for(int i=0;i<blockNumInGroup;i++) {
				if(cache[index][i].used_time==-1L) continue;
				else if(cache[index][i].tag == tag && hit==false) {
					//hit
					cache[index][i].used_time=0L;
					hit = true;
				}
				else {//if(is_prefetch==false){
					cache[index][i].used_time++;
				}
			}
			return hit;
		}

		public boolean write(int tag, int index, int inblockAddr) {
			boolean hit = false;
			if(index>=groupNum){
				System.out.println("Out of range! tag = "+tag+" Index = "+index);
			}
			for(int i=0;i<blockNumInGroup;i++) {
				if(cache[index][i].used_time==-1L) continue;
				else if(cache[index][i].tag == tag && hit==false) {
					//hit
					cache[index][i].used_time=0L;
					cache[index][i].dirty = true; //标记脏块

					if(writeIndex == 0){
						//write back
						//等之后把该块换出时再写入内存
					}
					else if(writeIndex == 1) {
						//write through
						//hit时，直接将结果写入内存，不标记脏块
						cache[index][i].dirty = false;
					}
					hit = true; //hit
				}
				else{// if(cache[index][i].used_time != -1L){
					cache[index][i].used_time++;
				}
			}
			return hit; 
		}


		public void loadCacheBlock(int tag, int index) {
			if(replaceIndex == 0) {
				//LRU
				int lruBlock = 0;
				for(int i=0;i<blockNumInGroup;i++) {
					if(cache[index][i].used_time == -1L){
						lruBlock = i;
						break;
					}
					else if(cache[index][lruBlock].used_time < cache[index][i].used_time){
						lruBlock = i; //找出上次被使用的时间最久的块
					}
				}
				loadToCache(tag, index, lruBlock);
			}
			else if(replaceIndex == 1) {
				//FIFO
				int fifoBlock = 0;
				for(int i=0;i<blockNumInGroup;i++) {
					if(cache[index][i].enter_time==-1L) {
						fifoBlock = i;
						break;
					}
					else if(cache[index][fifoBlock].enter_time > cache[index][i].enter_time) {
						fifoBlock = i;
					}
				}
				loadToCache(tag, index, fifoBlock);
			}
			else if(replaceIndex == 2) {
				//random
				int randBlock=0,i;
				for(i=0;i<blockNumInGroup;i++) {
					if(cache[index][i].enter_time==-1L) {
						randBlock = i;
						break;
					}
				}
				if(i==blockNumInGroup)
					randBlock = (int)(Math.random()*blockNumInGroup);
				//System.out.println(randBlock);
				loadToCache(tag, index, randBlock);
			}
		}

		private void loadToCache(int tag, int index, int groupAddr) {
			if(writeIndex == 0 && cache[index][groupAddr].dirty) {
				//脏块被换出，结果写入内存
			}

			cache[index][groupAddr].tag = tag;
			cache[index][groupAddr].enter_time = 0L;
			cache[index][groupAddr].enter_time = groupFIFOTime[index];
			cache[index][groupAddr].used_time = 0L;
			groupFIFOTime[index]++;
		}
	}

	Cache uCache; // 先只考虑一个Cache

	private int readInstMissTime, readInstHitTime;
	private int readDataMissTime, readDataHitTime;
	private int writeDataMissTime, writeDataHitTime;

	private int INSTRUCTION_MAX_SIZE = 1500000;
	private int ops[];
	private int address[];
	private int ip;
	private int i_count;

	
	/*
	 * 构造函数，绘制模拟器面板
	 */
	public CCacheSim(){
		super("Cache Simulator");
		draw();
	}
	
	
	//响应事件，共有三种事件：
	//   1. 执行到底事件
	//   2. 单步执行事件
	//   3. 文件选择事件
	public void actionPerformed(ActionEvent e){

		
		if (e.getSource() == execAllBtn) {
			is_step=false;
			simExecAll();
			int totalMissTime=readDataMissTime+writeDataMissTime+readInstMissTime;
			int totalHitTime=readDataHitTime+writeDataHitTime+readInstHitTime;
			int total = totalHitTime + totalMissTime;
			int[] total_time = {total,readInstHitTime+readInstMissTime,readDataHitTime+readDataMissTime,writeDataHitTime+writeDataMissTime};
			int[] miss_time = {totalMissTime,readInstMissTime,readDataMissTime,writeDataMissTime};
			String[] names = {"访问总次数","读指令次数","读数据次数","写数据次数"};
			for(int i=0;i<4;i++)
			{
				String miss_rate = (total_time[0]==0)?"0.00%":String.format("%.2f%%", (double)miss_time[i]/total_time[i]*100);
				results[i].setText(names[i]+": "+total_time[i]+"        不命中次数: "+miss_time[i]+"        不命中率: "+miss_rate);
			}
			stepLabel1.setVisible(false);
			stepLabel2.setVisible(false);
			
		}
		if (e.getSource() == execStepBtn) {
			is_step=true;
			String[] opname = {"读指令","读数据","写数据"};
			int addr = (require_prefetch)?(prefaddr):(address[ip]);
			stepLabel1.setText("访问类型: "+opname[op]+"      地址: "+addr);
			stepLabel1.setVisible(true);
			simExecStep();
			int totalMissTime=readDataMissTime+writeDataMissTime+readInstMissTime;
			int totalHitTime=readDataHitTime+writeDataHitTime+readInstHitTime;
			int total = totalHitTime + totalMissTime;
			int[] total_time = {total,readInstHitTime+readInstMissTime,readDataHitTime+readDataMissTime,writeDataHitTime+writeDataMissTime};
			int[] miss_time = {totalMissTime,readInstMissTime,readDataMissTime,writeDataMissTime};
			String[] names = {"访问总次数","读指令次数","读数据次数","写数据次数"};
			for(int i=0;i<4;i++)
			{
				String miss_rate = (total_time[i]==0)?"0.00%":String.format("%.2f%%", (double)miss_time[i]/total_time[i]*100);
				results[i].setText(names[i]+": "+total_time[i]+"        不命中次数: "+miss_time[i]+"        不命中率: "+miss_rate);
			}
			String hitStatus=(hit)?"命中":"不命中";
			stepLabel2.setText("块号: "+blockAddr+"    块内地址: "+inblockAddr+"    索引: "+index+"    命中情况: "+hitStatus);
			stepLabel2.setVisible(true);
		}
		if (e.getSource() == fileBotton){
			int fileOver = fileChoose.showOpenDialog(null);
			if (fileOver == 0) {
				   String path = fileChoose.getSelectedFile().getAbsolutePath();
				   System.out.println(path);
				   fileAddrBtn.setText(path);
				   file = new File(path);
				   boolean canread=file.canRead();
				   System.out.println(canread);
				   readFile();
				   initCache();
			}
		}
	}
	
	/*
	 * 初始化 Cache 模拟器
	 */
	public void initCache() {
		readInstHitTime = 0;
		readInstMissTime = 0;
		readDataHitTime = 0;
		readDataMissTime = 0;
		writeDataHitTime = 0;
		writeDataMissTime = 0;
		ip = 0;
		/*
		cachesize[] = { "2KB", "8KB", "32KB", "128KB", "512KB", "2MB" };
		blocksize[] = { "16B", "32B", "64B", "128B", "256B" };
		*/
		int csize, bsize;
		switch(csIndex) {
			case 0: csize=1024*2;break;
			case 1: csize=1024*8;break;
			case 2: csize=1024*32;break;
			case 3: csize=1024*128;break;
			case 4: csize=1024*512;break;
			case 5: csize=1024*1024*2;break;
			default:csize=0;
		}
		switch(bsIndex) {
			case 0: bsize=16;break;
			case 1: bsize=32;break;
			case 2: bsize=64;break;
			case 3: bsize=128;break;
			case 4: bsize=256;break;
			default:bsize=0;
		}
		uCache = new Cache(csize, bsize);
		uCache.print_info();
	}
	
	/*
	 * 将指令和数据流从文件中读入
	 */
	public void readFile()  {
		try {
			Scanner s = new Scanner(file);
			ops = new int[INSTRUCTION_MAX_SIZE];
			address = new int[INSTRUCTION_MAX_SIZE];
			System.out.println(ops.length);
			i_count = 0;
			while(s.hasNextLine()) {
				String line = s.nextLine();
				if(line.trim().length()==0) continue;
				String[] items=line.split(" ");
				if(line.indexOf("\t")!=-1) {
					String[] t = line.split("\t");
					items = t[0].split(" ");
				}
				//System.out.println(line+": "+items[0]);
				ops[i_count] = Integer.parseInt(items[0].trim());
				address[i_count] = Integer.parseInt(items[1].trim(), 16);

				i_count++;
			}
			s.close();
			System.out.println("read file done!");
		}
		catch(Exception e) {
			e.printStackTrace();  
		}
	}
	
	/*
	 * 模拟单步执行
	 */
	//private boolean prefmiss;
	//private boolean prefhit;
	private boolean require_prefetch;
	private int prefaddr;
	//private int preftype;
	private int op,blockAddr,inblockAddr,index,tag;
	private boolean hit;
	public void simExecStep() {
		if(ip==0) {
			initCache();
			hit = false;
			require_prefetch=false;
			blockAddr=0;
			prefaddr=0;
		}
		if(require_prefetch==false) {
			op = ops[ip];
			blockAddr = address[ip]/(uCache.blockSize);
			tag = blockAddr/(uCache.groupNum);
			index = blockAddr % (uCache.groupNum);
			inblockAddr = address[ip]%(uCache.blockSize);
			ip++;
		}
		else
		{
			blockAddr = blockAddr+1;
			tag = blockAddr / (uCache.getgroupNum());
			index = blockAddr % (uCache.getgroupNum());
		}


		hit = false;
		if(op==0) {
			//read data
			hit = uCache.read(tag,index,inblockAddr);
			if(hit) {
				readDataHitTime++;
			}
			else {
				readDataMissTime++;
				uCache.loadCacheBlock(tag, index);
			}
		}
		else if(op==2) {
			//read instruction
			hit = uCache.read(tag,index,inblockAddr);
			if(hit) {
				readInstHitTime++;
			}
			else {
				readInstMissTime++;
				uCache.loadCacheBlock(tag, index);
			}
		}
		else if(op==1) {
			hit = uCache.write(tag, index, inblockAddr);
			if(hit) {
				writeDataHitTime++;
			}
			else {
				writeDataMissTime++;
				if(allocIndex==0){
					//write allocate 写失效时，先把所写单元调入Cache，再写
					uCache.loadCacheBlock(tag, index);
					uCache.write(tag, index, inblockAddr);
				}
				else if(allocIndex==1) {
					//no-write allocate 绕写法，写失效时，直接写入下一级存储，不把块调入Cache
				}
			}
		}
		
		if(hit==false && prefetchIndex ==1 && op!=1 && require_prefetch==false) {
			//当Cache Miss时，预取下一块内存
			require_prefetch=true;
			prefaddr = address[ip-1]+uCache.getblocksize();
		}
		else {
			require_prefetch=false;
		}
		//ip++;

		
	}
	
	/*
	 * 模拟执行到底
	 */
	public void simExecAll() {
		while(ip<i_count || require_prefetch==true) {
			simExecStep();
		}
		int totalMissTime=readDataMissTime+writeDataMissTime+readInstMissTime;
		int totalHitTime=readDataHitTime+writeDataHitTime+readInstHitTime;
		int total = totalHitTime + totalMissTime;
		System.out.println("total: "+total);
		System.out.println("totalHitTime: "+totalHitTime);
		System.out.println("totalMissTime: "+totalMissTime);
		ip = 0;
		
	}

	
	public static void main(String[] args) {
		new CCacheSim();
	}
	
	/**
	 * 绘制 Cache 模拟器图形化界面
	 */
	public void draw() {
		//模拟器绘制面板
		setLayout(new BorderLayout(5,5));
		panelTop = new JPanel();
		panelLeft = new JPanel();
		panelRight = new JPanel();
		panelBottom = new JPanel();
		panelTop.setPreferredSize(new Dimension(800, 50));
		panelLeft.setPreferredSize(new Dimension(300, 450));
		panelRight.setPreferredSize(new Dimension(500, 450));
		panelBottom.setPreferredSize(new Dimension(800, 100));
		panelTop.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		panelLeft.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		panelRight.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		panelBottom.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		//*****************************顶部面板绘制*****************************************//
		labelTop = new JLabel("Cache Simulator");
		labelTop.setAlignmentX(CENTER_ALIGNMENT);
		panelTop.add(labelTop);

		
		//*****************************左侧面板绘制*****************************************//
		labelLeft = new JLabel("Cache 参数设置");
		labelLeft.setPreferredSize(new Dimension(300, 40));
		
		//cache 大小设置
		csLabel = new JLabel("总大小");
		csLabel.setPreferredSize(new Dimension(120, 30));
		csBox = new JComboBox(cachesize);
		csBox.setPreferredSize(new Dimension(160, 30));
		csBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				csIndex = csBox.getSelectedIndex();
			}
		});
		
		//cache 块大小设置
		bsLabel = new JLabel("块大小");
		bsLabel.setPreferredSize(new Dimension(120, 30));
		bsBox = new JComboBox(blocksize);
		bsBox.setPreferredSize(new Dimension(160, 30));
		bsBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				bsIndex = bsBox.getSelectedIndex();
			}
		});
		
		//相连度设置
		wayLabel = new JLabel("相联度");
		wayLabel.setPreferredSize(new Dimension(120, 30));
		wayBox = new JComboBox(way);
		wayBox.setPreferredSize(new Dimension(160, 30));
		wayBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				wayIndex = wayBox.getSelectedIndex();
			}
		});
		
		//替换策略设置
		replaceLabel = new JLabel("替换策略");
		replaceLabel.setPreferredSize(new Dimension(120, 30));
		replaceBox = new JComboBox(replace);
		replaceBox.setPreferredSize(new Dimension(160, 30));
		replaceBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				replaceIndex = replaceBox.getSelectedIndex();
			}
		});
		
		//欲取策略设置
		prefetchLabel = new JLabel("预取策略");
		prefetchLabel.setPreferredSize(new Dimension(120, 30));
		prefetchBox = new JComboBox(pref);
		prefetchBox.setPreferredSize(new Dimension(160, 30));
		prefetchBox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				prefetchIndex = prefetchBox.getSelectedIndex();
			}
		});
		
		//写策略设置
		writeLabel = new JLabel("写策略");
		writeLabel.setPreferredSize(new Dimension(120, 30));
		writeBox = new JComboBox(write);
		writeBox.setPreferredSize(new Dimension(160, 30));
		writeBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				writeIndex = writeBox.getSelectedIndex();
			}
		});
		
		//调块策略
		allocLabel = new JLabel("写不命中调块策略");
		allocLabel.setPreferredSize(new Dimension(120, 30));
		allocBox = new JComboBox(alloc);
		allocBox.setPreferredSize(new Dimension(160, 30));
		allocBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				allocIndex = allocBox.getSelectedIndex();
			}
		});
		
		//选择指令流文件
		fileLabel = new JLabel("选择指令流文件");
		fileLabel.setPreferredSize(new Dimension(120, 30));
		fileAddrBtn = new JLabel();
		fileAddrBtn.setPreferredSize(new Dimension(210,30));
		fileAddrBtn.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		fileBotton = new JButton("浏览");
		fileBotton.setPreferredSize(new Dimension(70,30));
		fileBotton.addActionListener(this);
		
		panelLeft.add(labelLeft);
		panelLeft.add(csLabel);
		panelLeft.add(csBox);
		panelLeft.add(bsLabel);
		panelLeft.add(bsBox);
		panelLeft.add(wayLabel);
		panelLeft.add(wayBox);
		panelLeft.add(replaceLabel);
		panelLeft.add(replaceBox);
		panelLeft.add(prefetchLabel);
		panelLeft.add(prefetchBox);
		panelLeft.add(writeLabel);
		panelLeft.add(writeBox);
		panelLeft.add(allocLabel);
		panelLeft.add(allocBox);
		panelLeft.add(fileLabel);
		panelLeft.add(fileAddrBtn);
		panelLeft.add(fileBotton);
		
		//*****************************右侧面板绘制*****************************************//
		//模拟结果展示区域
		rightLabel = new JLabel("模拟结果");
		rightLabel.setPreferredSize(new Dimension(500, 40));
		results = new JLabel[4];
		for (int i=0; i<4; i++) {
			results[i] = new JLabel("");
			results[i].setPreferredSize(new Dimension(500, 40));
		}
		
		stepLabel1 = new JLabel();
		stepLabel1.setVisible(false);
		stepLabel1.setPreferredSize(new Dimension(500, 40));
		stepLabel2 = new JLabel();
		stepLabel2.setVisible(false);
		stepLabel2.setPreferredSize(new Dimension(500, 40));
		
		panelRight.add(rightLabel);
		for (int i=0; i<4; i++) {
			panelRight.add(results[i]);
		}
		
		panelRight.add(stepLabel1);
		panelRight.add(stepLabel2);


		//*****************************底部面板绘制*****************************************//
		
		bottomLabel = new JLabel("执行控制");
		bottomLabel.setPreferredSize(new Dimension(800, 30));
		execStepBtn = new JButton("步进");
		execStepBtn.setLocation(100, 30);
		execStepBtn.addActionListener(this);
		execAllBtn = new JButton("执行到底");
		execAllBtn.setLocation(300, 30);
		execAllBtn.addActionListener(this);
		
		panelBottom.add(bottomLabel);
		panelBottom.add(execStepBtn);
		panelBottom.add(execAllBtn);

		add("North", panelTop);
		add("West", panelLeft);
		add("Center", panelRight);
		add("South", panelBottom);
		setSize(820, 620);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}

}
