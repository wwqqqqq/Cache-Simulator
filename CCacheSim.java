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


    //��������
	private String cachesize[] = { "2KB", "8KB", "32KB", "128KB", "512KB", "2MB" };
	private String blocksize[] = { "16B", "32B", "64B", "128B", "256B" };
	private String way[] = { "ֱ��ӳ��", "2·", "4·", "8·", "16·", "32·" };
	private String replace[] = { "LRU", "FIFO", "RAND" };
	private String pref[] = { "��Ԥȡ", "������Ԥȡ" };
	private String write[] = { "д�ط�", "дֱ�﷨" };
	private String alloc[] = { "��д����", "����д����" };
	private String typename[] = { "������", "д����", "��ָ��" };
	private String hitname[] = {"������", "����" };
	private boolean is_step=false;
	
	//�Ҳ�����ʾ
	private String rightLable[]={"�����ܴ�����","��ָ�������","�����ݴ�����","д���ݴ�����"};
	
	//���ļ�
	private File file;
	
	//�ֱ��ʾ��༸����������ѡ��ĵڼ�������� 0 ��ʼ
	private int csIndex, bsIndex, wayIndex, replaceIndex, prefetchIndex, writeIndex, allocIndex;
	
	//�ڲ���CacheBlock
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

	//�ڲ���Cache
	private class Cache {
		private CacheBlock cache[][];
		private int cacheSize;		//Cache��С
		private int blockSize;		//���С
		private int blockNum;		//�ܿ���
		private int blockNumInGroup;//ÿ���еĿ���
		private int groupNum;		//����
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

		//read, write ������ֻ����hit�����
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
					cache[index][i].dirty = true; //������

					if(writeIndex == 0){
						//write back
						//��֮��Ѹÿ黻��ʱ��д���ڴ�
					}
					else if(writeIndex == 1) {
						//write through
						//hitʱ��ֱ�ӽ����д���ڴ棬��������
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
						lruBlock = i; //�ҳ��ϴα�ʹ�õ�ʱ����õĿ�
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
				//��鱻���������д���ڴ�
			}

			cache[index][groupAddr].tag = tag;
			cache[index][groupAddr].enter_time = 0L;
			cache[index][groupAddr].enter_time = groupFIFOTime[index];
			cache[index][groupAddr].used_time = 0L;
			groupFIFOTime[index]++;
		}
	}

	Cache uCache; // ��ֻ����һ��Cache

	private int readInstMissTime, readInstHitTime;
	private int readDataMissTime, readDataHitTime;
	private int writeDataMissTime, writeDataHitTime;

	private int INSTRUCTION_MAX_SIZE = 1500000;
	private int ops[];
	private int address[];
	private int ip;
	private int i_count;

	
	/*
	 * ���캯��������ģ�������
	 */
	public CCacheSim(){
		super("Cache Simulator");
		draw();
	}
	
	
	//��Ӧ�¼������������¼���
	//   1. ִ�е����¼�
	//   2. ����ִ���¼�
	//   3. �ļ�ѡ���¼�
	public void actionPerformed(ActionEvent e){

		
		if (e.getSource() == execAllBtn) {
			is_step=false;
			simExecAll();
			int totalMissTime=readDataMissTime+writeDataMissTime+readInstMissTime;
			int totalHitTime=readDataHitTime+writeDataHitTime+readInstHitTime;
			int total = totalHitTime + totalMissTime;
			int[] total_time = {total,readInstHitTime+readInstMissTime,readDataHitTime+readDataMissTime,writeDataHitTime+writeDataMissTime};
			int[] miss_time = {totalMissTime,readInstMissTime,readDataMissTime,writeDataMissTime};
			String[] names = {"�����ܴ���","��ָ�����","�����ݴ���","д���ݴ���"};
			for(int i=0;i<4;i++)
			{
				String miss_rate = (total_time[0]==0)?"0.00%":String.format("%.2f%%", (double)miss_time[i]/total_time[i]*100);
				results[i].setText(names[i]+": "+total_time[i]+"        �����д���: "+miss_time[i]+"        ��������: "+miss_rate);
			}
			stepLabel1.setVisible(false);
			stepLabel2.setVisible(false);
			
		}
		if (e.getSource() == execStepBtn) {
			is_step=true;
			String[] opname = {"��ָ��","������","д����"};
			int addr = (require_prefetch)?(prefaddr):(address[ip]);
			stepLabel1.setText("��������: "+opname[op]+"      ��ַ: "+addr);
			stepLabel1.setVisible(true);
			simExecStep();
			int totalMissTime=readDataMissTime+writeDataMissTime+readInstMissTime;
			int totalHitTime=readDataHitTime+writeDataHitTime+readInstHitTime;
			int total = totalHitTime + totalMissTime;
			int[] total_time = {total,readInstHitTime+readInstMissTime,readDataHitTime+readDataMissTime,writeDataHitTime+writeDataMissTime};
			int[] miss_time = {totalMissTime,readInstMissTime,readDataMissTime,writeDataMissTime};
			String[] names = {"�����ܴ���","��ָ�����","�����ݴ���","д���ݴ���"};
			for(int i=0;i<4;i++)
			{
				String miss_rate = (total_time[i]==0)?"0.00%":String.format("%.2f%%", (double)miss_time[i]/total_time[i]*100);
				results[i].setText(names[i]+": "+total_time[i]+"        �����д���: "+miss_time[i]+"        ��������: "+miss_rate);
			}
			String hitStatus=(hit)?"����":"������";
			stepLabel2.setText("���: "+blockAddr+"    ���ڵ�ַ: "+inblockAddr+"    ����: "+index+"    �������: "+hitStatus);
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
	 * ��ʼ�� Cache ģ����
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
	 * ��ָ������������ļ��ж���
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
	 * ģ�ⵥ��ִ��
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
					//write allocate дʧЧʱ���Ȱ���д��Ԫ����Cache����д
					uCache.loadCacheBlock(tag, index);
					uCache.write(tag, index, inblockAddr);
				}
				else if(allocIndex==1) {
					//no-write allocate ��д����дʧЧʱ��ֱ��д����һ���洢�����ѿ����Cache
				}
			}
		}
		
		if(hit==false && prefetchIndex ==1 && op!=1 && require_prefetch==false) {
			//��Cache Missʱ��Ԥȡ��һ���ڴ�
			require_prefetch=true;
			prefaddr = address[ip-1]+uCache.getblocksize();
		}
		else {
			require_prefetch=false;
		}
		//ip++;

		
	}
	
	/*
	 * ģ��ִ�е���
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
	 * ���� Cache ģ����ͼ�λ�����
	 */
	public void draw() {
		//ģ�����������
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

		//*****************************����������*****************************************//
		labelTop = new JLabel("Cache Simulator");
		labelTop.setAlignmentX(CENTER_ALIGNMENT);
		panelTop.add(labelTop);

		
		//*****************************���������*****************************************//
		labelLeft = new JLabel("Cache ��������");
		labelLeft.setPreferredSize(new Dimension(300, 40));
		
		//cache ��С����
		csLabel = new JLabel("�ܴ�С");
		csLabel.setPreferredSize(new Dimension(120, 30));
		csBox = new JComboBox(cachesize);
		csBox.setPreferredSize(new Dimension(160, 30));
		csBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				csIndex = csBox.getSelectedIndex();
			}
		});
		
		//cache ���С����
		bsLabel = new JLabel("���С");
		bsLabel.setPreferredSize(new Dimension(120, 30));
		bsBox = new JComboBox(blocksize);
		bsBox.setPreferredSize(new Dimension(160, 30));
		bsBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				bsIndex = bsBox.getSelectedIndex();
			}
		});
		
		//����������
		wayLabel = new JLabel("������");
		wayLabel.setPreferredSize(new Dimension(120, 30));
		wayBox = new JComboBox(way);
		wayBox.setPreferredSize(new Dimension(160, 30));
		wayBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				wayIndex = wayBox.getSelectedIndex();
			}
		});
		
		//�滻��������
		replaceLabel = new JLabel("�滻����");
		replaceLabel.setPreferredSize(new Dimension(120, 30));
		replaceBox = new JComboBox(replace);
		replaceBox.setPreferredSize(new Dimension(160, 30));
		replaceBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				replaceIndex = replaceBox.getSelectedIndex();
			}
		});
		
		//��ȡ��������
		prefetchLabel = new JLabel("Ԥȡ����");
		prefetchLabel.setPreferredSize(new Dimension(120, 30));
		prefetchBox = new JComboBox(pref);
		prefetchBox.setPreferredSize(new Dimension(160, 30));
		prefetchBox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				prefetchIndex = prefetchBox.getSelectedIndex();
			}
		});
		
		//д��������
		writeLabel = new JLabel("д����");
		writeLabel.setPreferredSize(new Dimension(120, 30));
		writeBox = new JComboBox(write);
		writeBox.setPreferredSize(new Dimension(160, 30));
		writeBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				writeIndex = writeBox.getSelectedIndex();
			}
		});
		
		//�������
		allocLabel = new JLabel("д�����е������");
		allocLabel.setPreferredSize(new Dimension(120, 30));
		allocBox = new JComboBox(alloc);
		allocBox.setPreferredSize(new Dimension(160, 30));
		allocBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				allocIndex = allocBox.getSelectedIndex();
			}
		});
		
		//ѡ��ָ�����ļ�
		fileLabel = new JLabel("ѡ��ָ�����ļ�");
		fileLabel.setPreferredSize(new Dimension(120, 30));
		fileAddrBtn = new JLabel();
		fileAddrBtn.setPreferredSize(new Dimension(210,30));
		fileAddrBtn.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		fileBotton = new JButton("���");
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
		
		//*****************************�Ҳ�������*****************************************//
		//ģ����չʾ����
		rightLabel = new JLabel("ģ����");
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


		//*****************************�ײ�������*****************************************//
		
		bottomLabel = new JLabel("ִ�п���");
		bottomLabel.setPreferredSize(new Dimension(800, 30));
		execStepBtn = new JButton("����");
		execStepBtn.setLocation(100, 30);
		execStepBtn.addActionListener(this);
		execAllBtn = new JButton("ִ�е���");
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
