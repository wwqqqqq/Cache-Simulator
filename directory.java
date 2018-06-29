import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Queue;
import java.util.LinkedList;
public class directory {	
	/*****创建panel2~panel5******/
	static Mypanel panel2 =new Mypanel();
	static Mypanel panel3 =new Mypanel();
	static Mypanel panel4 =new Mypanel();
	static Mypanel panel5 =new Mypanel();
	static Queue<String> actionQueue = new LinkedList<String>();
	static boolean optimize;
	
	static JComboBox<String> Mylistmodel1_1 = new JComboBox<>(new Mylistmodel());
	static class Mylistmodel extends AbstractListModel<String> implements ComboBoxModel<String>{		
		private static final long serialVersionUID = 1L;
		String selecteditem=null;
		private String[] test={"直接映射","两路组相联","四路组相联"};
		public String getElementAt(int index){
			return test[index];
		}
		public int getSize(){
			return test.length;
		}
		public void setSelectedItem(Object item){
			selecteditem=(String)item;
		}
		public Object getSelectedItem( ){
			return selecteditem;
		}
		public int getIndex() {
			for (int i = 0; i < test.length; i++) {
				if (test[i].equals(getSelectedItem()))
					return i;
			}
			return 0;
		}
		
	}
	static class Mylistmodel2 extends AbstractListModel<String> implements ComboBoxModel<String>{		
		private static final long serialVersionUID = 1L;
		String selecteditem=null;
		private String[] test={"读","写"};
		public String getElementAt(int index){
			return test[index];
		}
		public int getSize(){
			return test.length;
		}
		public void setSelectedItem(Object item){
			selecteditem=(String)item;
		}
		public Object getSelectedItem( ){
			return selecteditem;
		}
		public int getIndex() {
			for (int i = 0; i < test.length; i++) {
				if (test[i].equals(getSelectedItem()))
					return i;
			}
			return 0;
		}
		
	}
	
	static class Mypanel extends JPanel implements ActionListener {
		private static final long serialVersionUID = 1L;
		JLabel label=new JLabel("访问地址");
		JLabel label_2=new JLabel("Process1");
		
		JTextField jtext=new JTextField("");
		JButton button=new JButton("执行");
		JComboBox<String> Mylistmodel = new JComboBox<>(new Mylistmodel2());
		
		
		/*********cache中的标题*********/
		String[] Cache_ca={"Cache","读/写","目标地址"};
		/*********cache中的内容*********/
		String[][] Cache_Content = {
				{"0"," "," "},{"1"," "," "},{"2"," "," "},{"3"," "," "}
		};
		/*********memory的标题*********/
		String[] Mem_ca={
				"Memory","",""
		};
		
		/*********memory中的内容*********/
		String[][] Mem_Content ={
				{"0","",""},{"1","",""},{"2","",""},{"3","",""},{"4","",""},{"5","",""},{"6","",""},{"7","",""},
				{"8","",""},{"9","",""}
		};

		/************cache的滚动模版***********/
		JTable table_1 = new JTable(Cache_Content,Cache_ca); 
		JScrollPane scrollPane = new JScrollPane(table_1);
		/************memory的滚动模版***********/
		JTable table_2 = new JTable(Mem_Content,Mem_ca); 
		JScrollPane scrollPane2 = new JScrollPane(table_2);
		
		public Mypanel(){
			super();
			setSize(350, 400);
			setLayout(null);
			
			/*****添加原件********/
			add(jtext);
			add(label);
			add(label_2);
			add(button);
			add(Mylistmodel);
			add(scrollPane);
			add(scrollPane2);
			
			/****设置原件大小与字体********/
			label_2.setFont(new Font("",1,16));
			label_2.setBounds(10, 10, 100, 30);
			
			label.setFont(new Font("",1,16));
			label.setBounds(10, 50, 100, 30);
			
			jtext.setFont(new Font("",1,15));
			jtext.setBounds(100, 50, 50, 30);
			
			Mylistmodel.setFont(new Font("",1,15));
			Mylistmodel.setBounds(160, 50, 50, 30);
			
			scrollPane.setFont(new Font("",1,15));
			scrollPane.setBounds(10, 90, 310, 90);
			
			scrollPane2.setFont(new Font("",1,15));
			scrollPane2.setBounds(10, 190, 310, 180);
			
			button.setFont(new Font("",1,15));
			button.setBounds(220,50, 100, 35);
			
			/******添加按钮事件********/
			button.addActionListener(this);
		}
		
		public void init(){
			/******Mypanel的初始化******/
			while(actionQueue.peek()!=null) {
				actionQueue.poll();
			}
			jtext.setText(" ");
			Mylistmodel.setSelectedItem(null);
			for(int i=0;i<=3;i++)
				for(int j=1;j<=2;j++)
					Cache_Content[i][j]=" ";
			for(int i=0;i<=9;i++)
				for(int j=1;j<=2;j++)
					Mem_Content[i][j]=" ";
			setVisible(false);
			setVisible(true);
			//System.out.println("Initialization success");
			
		}

		public void write_back(int process_id, int addr, boolean replace) {
			String proc = ""+process_id;
			if(addr/10==0) {
				if(panel2.Mem_Content[addr%10][2].equals(proc)) {
					panel2.Mem_Content[addr%10][2] = "";
					panel2.Mem_Content[addr%10][1] = "";
				}
				else {
					String[] items = panel2.Mem_Content[addr%10][2].split("proc");
					panel2.Mem_Content[addr%10][2] = items[0]+items[1];
				}
			}
			else if(addr/10==1) {
				if(panel3.Mem_Content[addr%10][2].equals(proc)) {
					panel3.Mem_Content[addr%10][2] = "";
					panel3.Mem_Content[addr%10][1] = "";
				}
				else {
					String[] items = panel3.Mem_Content[addr%10][2].split("proc");
					panel3.Mem_Content[addr%10][2] = items[0]+items[1];
				}
			}
			else if(addr/10==2) {
				if(panel4.Mem_Content[addr%10][2].equals(proc)) {
					panel4.Mem_Content[addr%10][2] = "";
					panel4.Mem_Content[addr%10][1] = "";
				}
				else {
					String[] items = panel4.Mem_Content[addr%10][2].split("proc");
					panel4.Mem_Content[addr%10][2] = items[0]+items[1];
				}
			}
			else {
				if(panel4.Mem_Content[addr%10][2].equals(proc)) {
					panel4.Mem_Content[addr%10][2] = "";
					panel4.Mem_Content[addr%10][1] = "";
				}
				else {
					String[] items = panel4.Mem_Content[addr%10][2].split("proc");
					panel4.Mem_Content[addr%10][2] = items[0]+items[1];
				}
			}
			actionQueue.offer("Directory: Set presence["+process_id+"]=0 (address = "+addr+")");
			actionQueue.offer("Directory: Modified -> Uncached (address = "+addr+")");
		}

		public void execute(int process_id, int address, int optype, int wayindex) {
			if(optype==0) {
				actionQueue.offer("*******************\nprocess "+process_id+", address = "+address+", op = read");
			}
			else {
				actionQueue.offer("*******************\nprocess "+process_id+", address = "+address+", op = write");
			}
			int cacheAddr;
			int groupNum, groupSize;
			if(wayindex==0) {
				groupNum = 4;
				groupSize = 1;
			}
			else if(wayindex==1) {
				groupNum = 2;
				groupSize = 2;
			}
			else {
				groupNum = 1;
				groupSize = 4;
			}
			int groupAddr = address % groupNum;
			for(cacheAddr = groupAddr * groupSize; cacheAddr < (groupAddr+1)*groupSize; cacheAddr++) {
				if(Cache_Content[cacheAddr][1].equals(" ")) {
					continue;
				}
				if(Integer.parseInt(Cache_Content[cacheAddr][2]) == address) {
					break;
				}
			}
			//本地cache的三种状态: Modified, Shared, Invalid
			//共享cache的三种状态: Modified, Owned, Shared, Uncached
			if(cacheAddr == (groupAddr+1)*groupSize) {
				//miss
				if(optype==0) {
					actionQueue.offer("Process "+process_id+", address = "+address+": read miss");
				}
				else {
					actionQueue.offer("Process "+process_id+", address = "+address+": write miss");
				}
				for(cacheAddr = groupAddr * groupSize; cacheAddr < (groupAddr+1)*groupSize; cacheAddr++) {
					if(Cache_Content[cacheAddr][1].equals(" ")) {
						break;
					}
				}
				if(cacheAddr == (groupAddr + 1)*groupSize) {
					//have to replace a block
					cacheAddr = groupAddr * groupSize;
					cacheAddr = cacheAddr + (int)(Math.random()*groupSize);
					if(Cache_Content[cacheAddr][1].equals("Modified")) {
						//write back
						actionQueue.offer("Process "+process_id+": write back cacheblock "+cacheAddr);
						int addr = Integer.parseInt(Cache_Content[cacheAddr][2]);
						write_back(process_id, addr, true);
					}
					else {
						int addr = Integer.parseInt(Cache_Content[cacheAddr][2]);
						write_back(process_id, addr, true);
					}
				}
				if(optype==0) {
					//read miss
					if(address/10==0) panel2.directory(process_id, 1, address);
					else if(address/10==1) panel3.directory(process_id, 1, address);
					else if(address/10==2) panel4.directory(process_id, 1, address);
					else panel5.directory(process_id, 1, address);
					Cache_Content[cacheAddr][1] = "Shared";
					Cache_Content[cacheAddr][2] = ""+address;
					actionQueue.offer("Process "+process_id+" read address "+address+" from local cache block "+cacheAddr+"\n*******************\n\n");
				}
				else {
					//write miss
					if(address/10==0) panel2.directory(process_id, 2, address);
					else if(address/10==1) panel3.directory(process_id, 2, address);
					else if(address/10==2) panel4.directory(process_id, 2, address);
					else panel5.directory(process_id, 2, address);
					Cache_Content[cacheAddr][1] = "Modified";
					Cache_Content[cacheAddr][2] = ""+address;
					actionQueue.offer("Process "+process_id+" write data to local cache block "+cacheAddr+"\n*******************\n\n");
				}
			}
			else {
				//hit
				if(optype==0) {
					//read hit
					actionQueue.offer("Process "+process_id+", address = "+address+": read hit");
					actionQueue.offer("Process "+process_id+" read address "+address+" from local cache block "+cacheAddr+"\n*******************\n\n");
				}
				else {
					//write hit
					actionQueue.offer("Process "+process_id+", address = "+address+": write hit");
					if(Cache_Content[cacheAddr][1].equals("Shared")) {
						if(address/10==0) panel2.directory(process_id, 3, address);
						else if(address/10==1) panel3.directory(process_id, 3, address);
						else if(address/10==2) panel4.directory(process_id, 3, address);
						else panel5.directory(process_id, 3, address);
						Cache_Content[cacheAddr][1] = "Modified";
					}
					actionQueue.offer("Process "+process_id+" write data to local cache block "+cacheAddr+"\n*******************\n\n");
				}
			}
		}

		public void directory(int source, int msg_type, int address) {
			//read miss - 1, write miss - 2, invalidate - 3, write back - 4
			int addr = address%10;
			String local_cache;
			String state;
			if(address/10==0) {
				local_cache = panel2.Mem_Content[address%10][2];
				state = panel2.Mem_Content[address%10][1];
			}
			else if(address/10==1) {
				local_cache = panel3.Mem_Content[address%10][2];
				state = panel3.Mem_Content[address%10][1];
			}
			else if(address/10==2) {
				local_cache = panel4.Mem_Content[address%10][2];
				state = panel4.Mem_Content[address%10][1];
			}
			else {
				local_cache = panel5.Mem_Content[address%10][2];
				state = panel5.Mem_Content[address%10][1];
			}
			if(state.equals("Modified")) {
				if(msg_type == 1) {
					//if(address/10==0) panel2.process_msg(5);
					if(local_cache.equals("1")) panel2.process_msg(0,1,5,address);
					else if(local_cache.equals("2")) panel3.process_msg(0,2,5,address);
					else if(local_cache.equals("3")) panel4.process_msg(0,3,5,address);
					else panel5.process_msg(0,4,5,address);
					Mem_Content[addr][1]="Owned";
					Mem_Content[addr][2] = Mem_Content[addr][2]+source;
					if(optimize==true) {
						actionQueue.offer("Directory: Fetch data from process "+local_cache+" and simultaneously load the block to process "+source);
					}
					else {
						actionQueue.offer("Directory: Fetch data from process "+local_cache);
						actionQueue.offer("Directory: Data reply to process "+source);
					}
					actionQueue.offer("Directory: Set presence["+source+"]=1");
					actionQueue.offer("Directory: Modified -> Owned");
				}
				else if(msg_type==2 || msg_type==3) {
					if(local_cache.equals("1")&&source!=1) panel2.process_msg(0,1,6,address);
					else if(local_cache.equals("2")&&source!=2) panel3.process_msg(0,2,6,address);
					else if(local_cache.equals("3")&&source!=3) panel4.process_msg(0,3,6,address);
					else if(local_cache.equals("4")&&source!=4) panel5.process_msg(0,4,6,address);
					Mem_Content[addr][2] = ""+source;
					if(optimize==true) {
						actionQueue.offer("Directory: Fetch and invalidate data from process "+local_cache+" and simultaneously load the block to process "+source);
					}
					else {
						actionQueue.offer("Directory: Fetch-invalidate to process "+local_cache);
						actionQueue.offer("Directory: Data reply to process "+source);
					}
					actionQueue.offer("Directory: Set presence["+local_cache+"]=0, presence["+source+"]=1");
				}
				else if(msg_type==4) {
					//write back
					//Mem_Content[addr][2] = "";
					//actionQueue.offer("Directory: Set presence["+local_cache+"]=0");
				}
			}
			else if(state.equals("")) {
				if(msg_type == 1) {
					//if(address/10==0) panel2.process_msg(5);
					actionQueue.offer("Directory: Get block from memory.");
					actionQueue.offer("Directory: Data reply to process "+source);
					Mem_Content[addr][1]="Shared";
					Mem_Content[addr][2] = ""+source;
					actionQueue.offer("Directory: Set presence["+source+"]=1");
					actionQueue.offer("Directory: Uncached -> Shared");
				}
				else if(msg_type==2) {
					actionQueue.offer("Directory: Get block from memory.");
					actionQueue.offer("Directory: Data reply to process "+source);
					Mem_Content[addr][1]="Modified";
					Mem_Content[addr][2] = ""+source;
					actionQueue.offer("Directory: Set presence["+source+"]=1");
					actionQueue.offer("Directory: Uncached -> Modified");
				}
			}
			else if(state.equals("Shared")) {
				if(msg_type == 1) {
					actionQueue.offer("Directory: Data reply to process "+source);
					Mem_Content[addr][2] = Mem_Content[addr][2] +source;
					actionQueue.offer("Directory: Set presence["+source+"]=1");
				}
				else if(msg_type==2 || msg_type==3) {
					actionQueue.offer("Directory: Invalidate message to all sharers, clear presence bits.");
					if(local_cache.indexOf("1")!=-1&&source!=1) panel2.process_msg(0,1,3,address);
					if(local_cache.indexOf("2")!=-1&&source!=2) panel3.process_msg(0,2,3,address);
					if(local_cache.indexOf("3")!=-1&&source!=3) panel4.process_msg(0,3,3,address);
					if(local_cache.indexOf("4")!=-1&&source!=4) panel5.process_msg(0,4,3,address);
					actionQueue.offer("Directory: Data reply to process "+source);
					Mem_Content[addr][1]="Modified";
					Mem_Content[addr][2] = ""+source;
					actionQueue.offer("Directory: Set presence["+source+"]=1");
					actionQueue.offer("Directory: Shared -> Modified");
				}
			}
			else {
				//Owned
				if(msg_type == 1) {
					actionQueue.offer("Directory: Data reply to process "+source);
					Mem_Content[addr][2] = Mem_Content[addr][2] +source;
					actionQueue.offer("Directory: Set presence["+source+"]=1");
				}
				else if(msg_type==2 || msg_type==3) {
					actionQueue.offer("Directory: Invalidate all sharers, clear presence bits.");
					if(local_cache.indexOf("1")!=-1&&source!=1) panel2.process_msg(0,1,3,address);
					if(local_cache.indexOf("2")!=-1&&source!=2) panel3.process_msg(0,2,3,address);
					if(local_cache.indexOf("3")!=-1&&source!=3) panel4.process_msg(0,3,3,address);
					if(local_cache.indexOf("4")!=-1&&source!=4) panel5.process_msg(0,4,3,address);
					actionQueue.offer("Directory: Data reply to process "+source);
					Mem_Content[addr][1]="Modified";
					Mem_Content[addr][2] = ""+source;
					actionQueue.offer("Directory: Set presence["+source+"]=1");
					actionQueue.offer("Directory: Owned -> Modified");
				}
			}
		}

		public void process_msg(int source, int dest, int msg_type, int address) {
			//read miss - 1, write miss - 2
			//invalidate - 3, acknowledgement - 4
			//fetch - 5, fetch & invalidate - 6
			//data block reply - 7, data block write back - 8
			int cacheAddr;
			for(cacheAddr=0;cacheAddr<4;cacheAddr++) {
				if(Cache_Content[cacheAddr][2].equals(""+address))
					break;
			}
			if(cacheAddr==4) return;
			if(Cache_Content[cacheAddr][1].equals(" ")) {
				//invalid
			}
			else if(Cache_Content[cacheAddr][1].equals("Modified")) {
				if(source==0 && msg_type==5) {
					actionQueue.offer("Process "+dest+": write back cacheblock "+cacheAddr);
					Cache_Content[cacheAddr][1] = "Shared";
					if(address/10==0) panel2.directory(dest, 4, address);
					else if(address/10==1) panel3.directory(dest, 4, address);
					else if(address/10==2) panel4.directory(dest, 4, address);
					else panel5.directory(dest, 4, address);
				}
				else if(source==0 && msg_type==6) {
					Cache_Content[cacheAddr][1] = " ";
					Cache_Content[cacheAddr][2] = " ";
					actionQueue.offer("CacheBlock "+cacheAddr+" in Process "+dest+" invalidated.");
				}
			}
			else if(Cache_Content[cacheAddr][1].equals("Shared")) {
				if(source==0 && msg_type==3) {
					Cache_Content[cacheAddr][1] = " ";
					Cache_Content[cacheAddr][2] = " ";
					actionQueue.offer("CacheBlock "+cacheAddr+" in Process "+dest+" invalidated.");
				}
			}
		}
		
		public void actionPerformed(ActionEvent e){
			/******编写自己的处理函数*******/
			if(e.getSource()==panel2.button) {
				//执行
				
				if(actionQueue.peek()==null) {
					int address = Integer.parseInt(jtext.getText().trim());
					int optype = Mylistmodel.getSelectedIndex();
					int wayindex = Mylistmodel1_1.getSelectedIndex();
					//action queue is empty
					execute(1, address, optype, wayindex);
					System.out.println(actionQueue.poll());
				}
				else {
					System.out.println(actionQueue.poll());
				}
			}
			else if(e.getSource()==panel3.button) {
				//执行
				
				if(actionQueue.peek()==null) {
					int address = Integer.parseInt(jtext.getText().trim());
					int optype = Mylistmodel.getSelectedIndex();
					int wayindex = Mylistmodel1_1.getSelectedIndex();
					//action queue is empty
					execute(2, address, optype, wayindex);
					System.out.println(actionQueue.poll());
				}
				else {
					System.out.println(actionQueue.poll());
				}
			}
			else if(e.getSource()==panel4.button) {
				//执行
				
				if(actionQueue.peek()==null) {
					int address = Integer.parseInt(jtext.getText().trim());
					int optype = Mylistmodel.getSelectedIndex();
					int wayindex = Mylistmodel1_1.getSelectedIndex();
					//action queue is empty
					execute(3, address, optype, wayindex);
					System.out.println(actionQueue.poll());
				}
				else {
					System.out.println(actionQueue.poll());
				}
			}
			else if(e.getSource()==panel5.button) {
				//执行
				
				if(actionQueue.peek()==null) {
					int address = Integer.parseInt(jtext.getText().trim());
					int optype = Mylistmodel.getSelectedIndex();
					int wayindex = Mylistmodel1_1.getSelectedIndex();
					//action queue is empty
					execute(4, address, optype, wayindex);
					System.out.println(actionQueue.poll());
				}
				else {
					System.out.println(actionQueue.poll());
				}
			}
			
			
			/**********显示刷新后的数据********/
			panel2.setVisible(false);
			panel2.setVisible(true);
			panel3.setVisible(false);
			panel3.setVisible(true);					
			panel4.setVisible(false);
			panel4.setVisible(true);
			panel5.setVisible(false);
			panel5.setVisible(true);
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame myjf = new JFrame("多cache一致性模拟之目录法");
		myjf.setSize(1500, 600);
		myjf.setLayout(null);
		myjf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container C1 = myjf.getContentPane();
		
		/*****新建panel1*****/
		JPanel panel1 = new JPanel();

		C1.add(panel2);
		C1.add(panel3);
		C1.add(panel4);
		C1.add(panel5);
		panel2.setBounds(10, 100, 350, 400);
		panel3.setBounds(360, 100, 350, 400);
		panel4.setBounds(720, 100, 350, 400);
		panel5.setBounds(1080, 100, 350, 400);
		
		
		/********设置每个Mypanel的不同的参数************/
		panel2.label_2.setText("Process1");
		panel3.label_2.setText("Process2");
		panel4.label_2.setText("Process3");
		panel5.label_2.setText("Process4");
		panel2.table_1.getColumnModel().getColumn(0).setHeaderValue("cache1");
		panel2.Cache_ca[0]="Cache1";
		panel3.table_1.getColumnModel().getColumn(0).setHeaderValue("cache2");
		panel3.Cache_ca[0]="Cache2";
		panel4.table_1.getColumnModel().getColumn(0).setHeaderValue("cache3");
		panel4.Cache_ca[0]="Cache3";
		panel5.table_1.getColumnModel().getColumn(0).setHeaderValue("cache4");
		panel5.Cache_ca[0]="Cache4";
		
		
		panel2.table_2.getColumnModel().getColumn(0).setHeaderValue("Memory1");
		panel3.table_2.getColumnModel().getColumn(0).setHeaderValue("Memory2");
		panel4.table_2.getColumnModel().getColumn(0).setHeaderValue("Memory3");
		panel5.table_2.getColumnModel().getColumn(0).setHeaderValue("Memory4");
		
		for(int i=0;i<10;i++){
			panel3.Mem_Content[i][0]=String.valueOf((Integer.parseInt(panel3.Mem_Content[i][0])+10));
			panel4.Mem_Content[i][0]=String.valueOf((Integer.parseInt(panel3.Mem_Content[i][0])+20));
			panel5.Mem_Content[i][0]=String.valueOf((Integer.parseInt(panel3.Mem_Content[i][0])+30));
		}
		/********设置头部panel*****/
		panel1.setBounds(10, 10, 1500, 100);
		panel1.setLayout(null);
		
		JLabel label1_1=new JLabel("执行方式:单步执行");
		label1_1.setFont(new Font("",1,20));
		label1_1.setBounds(15, 15, 200, 40);
		panel1.add(label1_1);
		
		//JComboBox<String> Mylistmodel1_1 = new JComboBox<>(new Mylistmodel());
		Mylistmodel1_1.setBounds(220, 15, 150, 40);
		Mylistmodel1_1.setFont(new Font("",1,20));
		panel1.add(Mylistmodel1_1);
		
		JButton button1_1=new JButton("复位");
		JButton button2 = new JButton("优化");
		JButton button3 = new JButton("不优化");
		button1_1.setBounds(400, 15, 70, 40);
		button2.setBounds(500, 15, 70, 40);
		button3.setBounds(600, 15, 100, 40);
		
		/**********复位按钮事件（初始化）***********/
		button1_1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				panel2.init();
				panel3.init();
				panel4.init();
				panel5.init();
				Mylistmodel1_1.setSelectedItem(null);
				optimize = false;
			}
		});

		button2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				panel2.init();
				panel3.init();
				panel4.init();
				panel5.init();
				Mylistmodel1_1.setSelectedItem(null);
				optimize = true;
			}
		});
		button3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				panel2.init();
				panel3.init();
				panel4.init();
				panel5.init();
				Mylistmodel1_1.setSelectedItem(null);
				optimize = false;
			}
		});
		
		/*panel2.Mem_Content[1][1]="11";*/
		panel1.add(button1_1);
		panel1.add(button2);
		panel1.add(button3);
		C1.add(panel1);
		myjf.setVisible(true);
		

		
	}

	
}

