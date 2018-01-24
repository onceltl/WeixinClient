package weixinclient;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class MainWindow extends JFrame
{
	  private JPanel toolbar;
	  private JButton searchButton,connectButton,sendButton,messageButton,startconnectButton;
	  private JButton loginButton,loginCerteinButton,addButton,sendFileButton;
	  private ClientThread clientthread;
	  private JDialog messagedialog,roomdialog,logindialog;
	  private TextArea text;
	  private JTextField textfield,ipaddrtext,porttext;
	  private JTextField passwordtext,idtext;
	  public PrintWriter writer;
	  public JScrollPane searchscrollpane,friendscrollpane;
	  public JPanel friendspanel;
	  public JDialog usersdialog;
	  public JFrame waitdialog;
	  public JList<String> allusers,friendslists;
	  public DefaultListModel<String> usersmodel,friendsmodel;
	  public Map<String,String> map;
	  public boolean islogin;
	  public boolean isconnection;
	  public String username;
	  public FileDialog opendialog;
	  public OutputStream sendstream;
	  public static void main(String[] args){
		  MainWindow f=new MainWindow();
		  f.setVisible(true);
	  }
	  public MainWindow(){
		  addWindowListener(new WindowAdapter() {  
			  public void windowClosing(WindowEvent e) {  
				  super.windowClosing(e);  
				  if (clientthread!=null&&clientthread.socket!=null)
					 try { clientthread.socket.close();
					 
					 }catch(Exception ex) {
						 System.out.println("close error.");
					 }
			  }  
		  }); 
		  waitdialog=new JFrame();
		  waitdialog.setTitle("等待文件传输");
		  waitdialog.setSize(400, 100);
		  
		  usersdialog=new JDialog(this,"搜索用户",true);
		  usersdialog.setSize(100,500);
		  searchscrollpane=new JScrollPane();
		  usersmodel =new DefaultListModel<String>();
		  allusers=new JList<String>(usersmodel);
		  searchscrollpane.setViewportView(allusers);
		  addButton=new JButton("添加");
		  usersdialog.add(addButton,BorderLayout.SOUTH);
		  usersdialog.add(searchscrollpane);
		 
		  logindialog=new JDialog(this,"登录",true);
		  logindialog.setLocation(100,50);  
		  idtext=new JTextField("用户名",20);
		  passwordtext=new JTextField("密码",20);
		  loginCerteinButton=new JButton("确认");
		  logindialog.add(idtext,BorderLayout.NORTH);	  
		  logindialog.add(passwordtext);
		  logindialog.add(loginCerteinButton,BorderLayout.SOUTH);
		  logindialog.pack();
		  

		  roomdialog=new JDialog(this,"建立连接",true);
		  roomdialog.setLocation(100,50);  
		  ipaddrtext=new JTextField("192.168.221.128",20);
		  porttext=new JTextField("8000",20);
		  connectButton=new JButton("连接");
		  roomdialog.add(ipaddrtext,BorderLayout.NORTH);	  
		  roomdialog.add(porttext);
		  roomdialog.add(connectButton,BorderLayout.SOUTH);
		  roomdialog.pack();
		  
		  
		  messagedialog=new JDialog(this,"聊天窗口",true);
		  messagedialog.setLocation(100,50);  
		  textfield=new JTextField(20);
		  text=new TextArea();
		  text.setEditable(false);
		  sendButton=new JButton("发送");
		  JPanel messagebar=new JPanel();
		  messagebar.add(textfield);
		  messagebar.add(sendButton);
		  sendFileButton=new JButton("传输文件");
		  messagebar.add(sendFileButton);
		  messagedialog.add(text);	  
		  messagedialog.add(messagebar,BorderLayout.SOUTH);
		  messagedialog.pack();
		  setTitle("微信");
		  MyItemListener lis=new MyItemListener();
		  toolbar=new JPanel();

		  messageButton=new JButton("消息");
		  startconnectButton=new JButton("连接");
		  loginButton=new JButton("登录");
		  searchButton=new JButton("搜索");
		  toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));
		//  toolbar.add(messageButton);
		  toolbar.add(startconnectButton);
		  toolbar.add(loginButton);
		  toolbar.add(searchButton);
		  sendFileButton.addActionListener(lis);
		  startconnectButton.addActionListener(lis);
		  messageButton.addActionListener(lis);
		  sendButton.addActionListener(lis);
		  connectButton.addActionListener(lis);
		  loginButton.addActionListener(lis);
		  loginCerteinButton.addActionListener(lis);
		  searchButton.addActionListener(lis);
		  addButton.addActionListener(lis);
		  //
		  friendscrollpane=new JScrollPane();
		  friendsmodel =new DefaultListModel<String>();
		  friendslists=new JList<String>(friendsmodel);
		  friendslists.addMouseListener(new MouseAdapter() {
	            public void mouseClicked(MouseEvent mouseEvent) {
	                if (mouseEvent.getClickCount() == 2) {
	                    int index = friendslists.getSelectedIndex();
	                    if (index >= 0) {
	                    	showmessagedialog();
	                    }
	                }
	            }
		  });
		  friendslists.setCellRenderer(new ImageCellRender());
		  friendslists.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
		  friendscrollpane.setViewportView(friendslists);
		  add(toolbar,BorderLayout.SOUTH);
		  add(friendscrollpane);
		  this.setSize(300,700);
		  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		  setResizable(false);
		  islogin=false;
		  map=new TreeMap<String,String>();
		  isconnection=false;
		  opendialog = new FileDialog(this, "打开", FileDialog.LOAD);
	        
		 try { 
		  Image img = ImageIO.read(this.getClass().getResource("/images/weixinicon.png"));
    
		  this.setIconImage(img);
		 }catch(Exception e) {
			 System.out.println("No icon.");
		 }
	  }
	  public void connection() {
		    roomdialog.setVisible(false);
		  	clientthread=new ClientThread(ipaddrtext.getText(),porttext.getText(),this);
			clientthread.start();
			isconnection=true;
	  }
	  public void startconnection() {
		  roomdialog.setLocation(this.getX() + this.getWidth()/2 - roomdialog.getWidth()/2, this.getY() +this.getHeight()/2 - roomdialog.getHeight()/2);
		  roomdialog.setVisible(true);
	  }
	  public void showmessagedialog() {
		  if (!islogin) {
			  JOptionPane.showMessageDialog(this, "未登录", "错误",JOptionPane.ERROR_MESSAGE);
			  return;
		  }
		  if (friendslists.getSelectedValue()==null) {
			  JOptionPane.showMessageDialog(this, "请选中好友", "错误",JOptionPane.ERROR_MESSAGE);
			  return;
		  }
		  String name=friendslists.getSelectedValue();
		  MessageController.getInstance().delname(name);
		  String str=map.get(name);
			 if (str==null) str="";
		  text.setText(str);
		  friendslists.repaint();
		  messagedialog.setLocation(this.getX() + this.getWidth()/2 - messagedialog.getWidth()/2, this.getY() +this.getHeight()/2 - messagedialog.getHeight()/2);
		  messagedialog.setVisible(true);
	  }
	  public void test() {
		  messagedialog.setLocation(this.getX() + this.getWidth()/2 - messagedialog.getWidth()/2, this.getY() +this.getHeight()/2 - messagedialog.getHeight()/2);
		  messagedialog.setVisible(true);
	
	  }
	  public void showlogindialog() {
		  if(!isconnection) {
			  JOptionPane.showMessageDialog(this, "请先连接服务器", "错误",JOptionPane.ERROR_MESSAGE);
			  return;
		  }
		  logindialog.setLocation(this.getX() + this.getWidth()/2 - logindialog.getWidth()/2, this.getY() +this.getHeight()/2 - logindialog.getHeight()/2);
		  logindialog.setVisible(true);
	  }
	  public void showfriends(String datas[]) {
		  
		  islogin=true;
		  username=idtext.getText();
		  this.setTitle(username);
		  friendsmodel.clear();  
		  	for (int i=1;i<datas.length;i++)
		  		friendsmodel.addElement(datas[i]);
		  	
		  		friendslists.setModel(friendsmodel);
		  	logindialog.setVisible(false);
	  }
	  public void sendmessage() {
		  writer.write("sendmessage"+"@"+username+"@"+friendslists.getSelectedValue()+"@"+textfield.getText()+"\n");
		  writer.flush();
		  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		  String str=map.get(friendslists.getSelectedValue());
		  if (str==null) str="";
		  str+=username+" "+df.format(new Date())+"    "+textfield.getText()+"\n";
		  map.put(friendslists.getSelectedValue(), str);
		  text.append(username+" "+df.format(new Date())+"    "+textfield.getText()+"\n");
	  }
	  public void recivemessage(String datas[]) {
		  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		  String str=map.get(datas[1]);
		 if (str==null) str="";
		 	
		 str=str+datas[1]+" "+df.format(new Date())+"    "+datas[2]+"\n";
		 if (!messagedialog.isVisible()||friendslists.getSelectedValue()
	 				==null||!friendslists.getSelectedValue().equals(datas[1]))
	 			MessageController.getInstance().addname(datas[1]);
		 else text.setText(str);
	 				
		 map.put(datas[1], str);
		 friendslists.repaint();
	  }
	  public void donerecivefile(String filename,String recivename) {
		  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		  String str=map.get(recivename);
		 if (str==null) str="";
		 str=str+recivename+" "+df.format(new Date())+"    recive file:"+filename+"\n";
		 if (!messagedialog.isVisible()||friendslists.getSelectedValue()
	 				==null||!friendslists.getSelectedValue().equals(recivename))
	 			MessageController.getInstance().addname(recivename);
		 else text.setText(str);
		 map.put(recivename, str);
		 friendslists.repaint(); 
	  }
	  public void waitforfile() {
		  waitdialog.setLocation(this.getX() + this.getWidth()/2 - waitdialog.getWidth()/2, this.getY() +this.getHeight()/2 - waitdialog.getHeight()/2);
		  waitdialog.setVisible(true);
	  }
	  public void asklogin() {
		  if (islogin) {
			  JOptionPane.showMessageDialog(this, "已经登录", "错误",JOptionPane.ERROR_MESSAGE);
			  return;
		  }
		  writer.write("login@"+idtext.getText()+"@"+passwordtext.getText());
		  writer.flush();
	  }
	  public void asksearch() {
		  if (!islogin) {
			  JOptionPane.showMessageDialog(this, "未登录", "错误",JOptionPane.ERROR_MESSAGE);
			  return;
		  }
		  writer.write("search");
		  writer.flush();
	  }
	  public void showsearchusers(String datas[]) {
		  	usersmodel.clear();  
		  	for (int i=1;i<datas.length;i++)
		  		usersmodel.addElement(datas[i]);
			  allusers.setModel(usersmodel);
			  usersdialog.setLocation(this.getX() + this.getWidth()/2 - usersdialog.getWidth()/2, this.getY() +this.getHeight()/2 - usersdialog.getHeight()/2);
			  usersdialog.setVisible(true);
	  }
	  public void addfreinds() {
		  if (username.equals(allusers.getSelectedValue())) {
			  JOptionPane.showMessageDialog(this, "不能添加自己为好友", "错误",JOptionPane.ERROR_MESSAGE);
			  return;
		  }
		  for (int i=0;i<friendsmodel.getSize();i++)
			  if (friendsmodel.getElementAt(i).equals(allusers.getSelectedValue()))
			  {
				  JOptionPane.showMessageDialog(this, "已经是好友", "错误",JOptionPane.ERROR_MESSAGE);
				  return;
			  }
		  friendsmodel.addElement(allusers.getSelectedValue());
		  writer.write("sync");
		  for (int i=0;i<friendsmodel.getSize();i++)
			  writer.write("@"+friendsmodel.getElementAt(i));
		  writer.flush();
	  }
	  public void sendfile() {

	        opendialog.setVisible(true);
	        String dirpath = opendialog.getDirectory();
            String fileName = opendialog.getFile();
            
            if (dirpath == null || fileName == null)
                return;
            
            File file = new File(dirpath, fileName);

            try {
                //Buffered.zReader bufr = new BufferedReader(new FileReader(file));
            	 BufferedInputStream buffersend=new BufferedInputStream(new FileInputStream(file));
             	
                writer.write("sendfile@"+friendslists.getSelectedValue()+"@"+fileName+"@"+file.length());
                writer.flush();
                int waittime=0;
                for (int i=1;i<=100000;i++)
                	waittime++;
                byte[] buffer = new byte[65536];
            	int len=0;
            	while((len=buffersend.read(buffer, 0, 65536/2))>0) {
            		sendstream.write(buffer, 0, len);
            		sendstream.flush();
            	}
             
            	buffersend.close();
    
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
      		  	String str=map.get(friendslists.getSelectedValue());
      		  	if (str==null) str="";
      		  	str+=username+" "+df.format(new Date())+"  sendfile:  "+fileName+"\n";
      		  	map.put(friendslists.getSelectedValue(), str);
      		  	text.append(username+" "+df.format(new Date())+"  sendfile:  "+fileName+"\n");
            } catch (FileNotFoundException e1) {
            	System.out.println("没有文件");
            } catch (IOException e1) {
            	System.out.println("文件读取失败");
            }
	  }
	  private class MyItemListener implements ActionListener{
		  public void actionPerformed(ActionEvent e){
			  Object obj=e.getSource();
			if (obj==connectButton)
			  connection();
			if (obj==startconnectButton)
				startconnection();
			if (obj==messageButton) 
				test();
			if (obj==sendButton) 
				sendmessage();
			if (obj==loginButton)
				showlogindialog();
			if (obj==loginCerteinButton) 
				asklogin();
			if (obj==sendFileButton)
				sendfile();
			if (obj==searchButton) 
				asksearch();
			if (obj==addButton)
				addfreinds();
		  }
	  }
}
