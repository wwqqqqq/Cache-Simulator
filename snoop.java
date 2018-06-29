//package snoop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Queue;
import java.util.LinkedList;
public class snoop {	
	/*****����panel2~panel5******/
	static Mypanel panel2 =new Mypanel();
	static Mypanel panel3 =new Mypanel();
	static Mypanel panel4 =new Mypanel();
	static Mypanel panel5 =new Mypanel();
	static Queue<String> actionQueue = new LinkedList<String>();
	static boolean optimize;
	/*********memory�ı���*********/
	static String[] Mem_ca={
			"Memory","","","Memory","","","Memory","",""
	};
	
	/*********memory�е�����*********/
	static String[][] Mem_Content ={
			{"0","","","10","","","20","",""},{"1","","","11","","","21","",""},{"2","","","12","","","22","",""},
			{"3","","","13","","","23","",""},{"4","","","14","","","24","",""},{"5","","","15","","","25","",""},
			{"6","","","16","","","26","",""},{"7","","","17","","","27","",""},{"8","","","18","","","28","",""},
			{"9","","","19","","","29","",""}
	};

	
	static JComboBox<String> Mylistmodel1_1 = new JComboBox<>(new Mylistmodel());
	static class Mylistmodel extends AbstractListModel<String> implements ComboBoxModel<String>{		
		private static final long serialVersionUID = 1L;
		String selecteditem=null;
		private String[] test={"ֱ��ӳ��","��·������","��·������"};
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
		private String[] test={"��","д"};
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
		JLabel label=new JLabel("���ʵ�ַ");
		JLabel label_2=new JLabel("Process1");
		
		JTextField jtext=new JTextField("");
		JButton button=new JButton("ִ��");
		JComboBox<String> Mylistmodel = new JComboBox<>(new Mylistmodel2());
		
		
		/*********cache�еı���*********/
		String[] Cache_ca={"Cache","��/д","Ŀ���ַ"};
		/*********cache�е�����*********/
		String[][] Cache_Content = {
				{"0"," "," "},{"1"," "," "},{"2"," "," "},{"3"," "," "}
		};
		/************cache�Ĺ���ģ��***********/
		JTable table_1 = new JTable(Cache_Content,Cache_ca); 
		JScrollPane scrollPane = new JScrollPane(table_1);
		/*
		/************memory�Ĺ���ģ��**********
		JTable table_2 = new JTable(Mem_Content,Mem_ca); 
		JScrollPane scrollPane2 = new JScrollPane(table_2);
		*/
		public Mypanel(){
			super();
			setSize(350, 250);
			setLayout(null);
			
			/*****���ԭ��********/
			add(jtext);
			add(label);
			add(label_2);
			add(button);
			add(Mylistmodel);
			add(scrollPane);
			//add(scrollPane2);
			
			/****����ԭ����С������********/
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
			
			//scrollPane2.setFont(new Font("",1,15));
			//scrollPane2.setBounds(10, 190, 310, 180);
			
			button.setFont(new Font("",1,15));
			button.setBounds(220,50, 100, 35);
			
			/******��Ӱ�ť�¼�********/
			button.addActionListener(this);
		}

		public void execute(int process_id, int address, int optype, int wayindex) {
			//Mylistmodel.getIndex() {"ֱ��ӳ��","��·������","��·������"}
			//Mylistmodel2.getIndex() {"��","д"} 
			//CPU: 1-Read Hit, 2-Read Miss, 3-Write Hit, 4-Write Miss
			//BUS: 1-Read Miss, 2-Invalidate, 3-Write Miss
			if(optype==0) {
				actionQueue.offer("*******************\nprocess "+process_id+" ��ʼ���жԵ�ַ"+address+"�Ķ�����");
			}
			else {
				actionQueue.offer("*******************\nprocess "+process_id+" ��ʼ���жԵ�ַ"+address+"��д����");
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
			if(cacheAddr == (groupAddr+1)*groupSize) {
				// miss!
				if(optype==0) {
					actionQueue.offer("process "+process_id+" �ڶ���ַ"+address+"ʱ����read miss");
				}
				else {
					actionQueue.offer("process "+process_id+" ��д��ַ"+address+"ʱ����write miss");
				}
				//actionQueue.offer("process "+process_id+"����ַ"+address+"����read miss");
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
						actionQueue.offer("process "+process_id+" ��cache��"+cacheAddr+"(���ڴ��"+Cache_Content[cacheAddr][2]+")д�ص��ڴ�");
					}
				}
				if(optype==0) {
					//read miss
					//place read miss on bus
					
					actionQueue.offer("process "+process_id+" places read miss on bus (address="+address+").");
					int modified = 0;
					if(process_id!=1) modified = modified + panel2.busEvent(1, 1, address,process_id);
					if(process_id!=2) modified = modified + panel3.busEvent(2, 1, address,process_id);
					if(process_id!=3) modified = modified + panel4.busEvent(3, 1, address,process_id);
					if(process_id!=4) modified = modified + panel5.busEvent(4, 1, address,process_id);
					if(modified>0 && optimize == true) {
						//use cache block from other cache
					}
					else {
						//load from memory
						actionQueue.offer("�ڴ潫�� "+address+"����process "+process_id+"��Ӧ��˽��cache�� "+cacheAddr+"��");
					}
					//actionQueue.offer("�ڴ潫�� "+address+"����process "+process_id+"��Ӧ��˽��cache�� "+cacheAddr+"��");
					//actionQueue.offer("process "+process_id+"���ڴ���load��"+address+"��˽��cache�Ŀ�"+cacheAddr+"��");
					Cache_Content[cacheAddr][1] = "Shared";
					Cache_Content[cacheAddr][2] = ""+address;
					actionQueue.offer("process "+process_id+" ��cache�� "+cacheAddr+"��ֵ\n*******************\n");
				}
				else {
					//write miss
					int modified = 0;
					actionQueue.offer("process "+process_id+" places write miss on bus (address="+address+").");
					if(process_id!=1) modified = modified + panel2.busEvent(1, 3, address,process_id);
					if(process_id!=2) modified = modified + panel3.busEvent(2, 3, address,process_id);
					if(process_id!=3) modified = modified + panel4.busEvent(3, 3, address,process_id);
					if(process_id!=4) modified = modified + panel5.busEvent(4, 3, address,process_id);
					if(modified>0 && optimize == true) {
						//use cache block from other cache
					}
					else {
						//load from memory
						actionQueue.offer("�ڴ潫�� "+address+"����process "+process_id+"��Ӧ��˽��cache�� "+cacheAddr+"��");
					}
					//actionQueue.offer("process "+process_id+" ���ڴ���load��"+address+"��˽��cache�Ŀ�"+cacheAddr+"��");
					Cache_Content[cacheAddr][1] = "Modified";
					Cache_Content[cacheAddr][2] = ""+address;
					actionQueue.offer("process "+process_id+" дcache��"+cacheAddr+"��ֵ\n*******************\n");
				}
				
			}
			else {
				if(optype==0) {
					//read hit
					actionQueue.offer("process "+process_id+": read hit");
					actionQueue.offer("process "+process_id+" ��cache�� "+cacheAddr+"��ֵ\n*******************\n");
				}
				else {
					//write hit
					actionQueue.offer("process "+process_id+": write hit");
					if(Cache_Content[cacheAddr][1].equals("Shared")) {
						//place invalidate on bus
						actionQueue.offer("process "+process_id+" places invalidate on bus (address="+address+").");
						if(process_id!=1) panel2.busEvent(1, 2, address,process_id);
						if(process_id!=2) panel3.busEvent(2, 2, address,process_id);
						if(process_id!=3) panel4.busEvent(3, 2, address,process_id);
						if(process_id!=4) panel5.busEvent(4, 2, address,process_id);
						Cache_Content[cacheAddr][1] = "Modified";
					}
					actionQueue.offer("process "+process_id+" дcache��"+cacheAddr+"��ֵ\n*******************\n");
				}
			}
		}

		public int busEvent(int process_id, int request, int address, int source) {
			//BUS: 1-Read Miss, 2-Invalidate, 3-Write Miss
			int i;
			for(i=0;i<4;i++) {
				if(Cache_Content[i][1].equals(" ")) {
					continue;
				}
				if(Integer.parseInt(Cache_Content[i][2]) == address) {
					break;
				}
			}
			if(i==4) return 0;
			int ret = 0;
			if(request == 1) {
				if(Cache_Content[i][1].equals("Shared")) {
					//serve read miss from shared cache or memory
				}
				else {
					//write back
					if(optimize==true) {
						actionQueue.offer("process "+process_id+" ��cache��"+i+"(���ڴ��"+Cache_Content[i][2]+")д�ص��ڴ棬ͬʱ���͸�process "+source);
						ret = 1;
					}
					else {
						actionQueue.offer("process "+process_id+" ��cache��"+i+"(���ڴ��"+Cache_Content[i][2]+")д�ص��ڴ�");
					}
					//serve read miss
					Cache_Content[i][1] = "Shared";
				}
			}
			else if(request == 2) {
				//invalidate
				Cache_Content[i][2] = " ";
				Cache_Content[i][1] = " ";
				actionQueue.offer("process "+process_id+" �е�cache��"+i+"��invalidate");
			}
			else {
				//write miss
				if(Cache_Content[i][1].equals("Shared")) {
				}
				else {
					//write back
					if(optimize==true) {
						actionQueue.offer("process "+process_id+" ��cache��"+i+"(���ڴ��"+Cache_Content[i][2]+")д�ص��ڴ棬ͬʱ���͸�process "+source);
						ret = 1;
					}
					else {
						actionQueue.offer("process "+process_id+" ��cache��"+i+"(���ڴ��"+Cache_Content[i][2]+")д�ص��ڴ�");
					}	
					//serve write miss
				}
				//invalidate
				Cache_Content[i][2] = " ";
				Cache_Content[i][1] = " ";
				actionQueue.offer("process "+process_id+" �е�cache��"+i+"��invalidate");
			}
			return ret;
		}
		
		
		public void init(){
			/******Mypanel�ĳ�ʼ��******/
			jtext.setText("");
			Mylistmodel.setSelectedItem(null);
			while(actionQueue.peek()!=null) {
				actionQueue.poll();
			} 
			for(int i=0;i<=3;i++)
				for(int j=1;j<=2;j++)
					Cache_Content[i][j]=" ";
			for(int i=0;i<=9;i++)
				for(int j=1;j<=2;j++)
					Mem_Content[i][j]=" ";
			setVisible(false);
			setVisible(true);
		}
		
		public void actionPerformed(ActionEvent e){
			/******��д�Լ��Ĵ�����*******/
			//JButton button=new JButton("ִ��");
			//JButton button1_1=new JButton("��λ");
			if(e.getSource()==panel2.button) {
				//ִ��
				
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
				//ִ��
				
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
				//ִ��
				
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
				//ִ��
				
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
			/*else if(e.getSource()==button1_1) {
				//��λ
				System.out.println("RESET!");
				//���action����
				while(actionQueue.peek()!=null) {
					actionQueue.poll();
				}
				//����ڴ�
				for(int i=0;i<=3;i++)
						for(int j=1;j<=2;j++)
					Cache_Content[i][j]=" ";
				for(int i=0;i<=9;i++)
					for(int j=1;j<=2;j++)
						Mem_Content[i][j]=" ";
			}*/
			
			
			/**********��ʾˢ�º������********/
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
		JFrame myjf = new JFrame("��cacheһ����ģ��֮Ŀ¼��");
		myjf.setSize(1500, 600);
		myjf.setLayout(null);
		myjf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container C1 = myjf.getContentPane();
		
		JTable table_2 = new JTable(Mem_Content,Mem_ca); 
		JScrollPane scrollPane2 = new JScrollPane(table_2);
		
		/*****�½�panel1*****/
		JPanel panel1 = new JPanel();

		C1.add(panel2);
		C1.add(panel3);
		C1.add(panel4);
		C1.add(panel5);
		C1.add(scrollPane2);
		panel2.setBounds(10, 100, 350, 200);
		panel3.setBounds(360, 100, 350, 200);
		panel4.setBounds(720, 100, 350, 200);
		panel5.setBounds(1080, 100, 350, 200);
		scrollPane2.setBounds(200,350,1000,180);
		scrollPane2.setFont(new Font("",1,15));
		//scrollPane2.setBounds(100, 250, 310, 180);
		
		/********����ÿ��Mypanel�Ĳ�ͬ�Ĳ���************/
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
		
		
		//panel2.table_2.getColumnModel().getColumn(0).setHeaderValue("Memory1");
		//panel3.table_2.getColumnModel().getColumn(0).setHeaderValue("Memory2");
		//panel4.table_2.getColumnModel().getColumn(0).setHeaderValue("Memory3");
		//panel5.table_2.getColumnModel().getColumn(0).setHeaderValue("Memory4");
		
		for(int i=0;i<10;i++){
			//panel3.Mem_Content[i][0]=String.valueOf((Integer.parseInt(panel3.Mem_Content[i][0])+10));
			//panel4.Mem_Content[i][0]=String.valueOf((Integer.parseInt(panel3.Mem_Content[i][0])+20));
			//panel5.Mem_Content[i][0]=String.valueOf((Integer.parseInt(panel3.Mem_Content[i][0])+30));
		}
		/********����ͷ��panel*****/
		panel1.setBounds(10, 10, 1500, 100);
		panel1.setLayout(null);
		
		JLabel label1_1=new JLabel("ִ�з�ʽ:����ִ��");
		label1_1.setFont(new Font("",1,20));
		label1_1.setBounds(15, 15, 200, 40);
		panel1.add(label1_1);
		
		//JComboBox<String> Mylistmodel1_1 = new JComboBox<>(new Mylistmodel());
		Mylistmodel1_1.setBounds(220, 15, 150, 40);
		Mylistmodel1_1.setFont(new Font("",1,20));
		panel1.add(Mylistmodel1_1);
		
		JButton button1_1=new JButton("��λ");
		JButton button2 = new JButton("�Ż�");
		JButton button3 = new JButton("���Ż�");
		button1_1.setBounds(400, 15, 70, 40);
		button2.setBounds(500, 15, 70, 40);
		button3.setBounds(600, 15, 100, 40);
		
		/**********��λ��ť�¼�����ʼ����***********/
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

