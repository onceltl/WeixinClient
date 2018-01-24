package weixinclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

public class ClientThread extends Thread{
	  String ipaddr;
	  int port;
	  MainWindow mainwindow;
	  boolean isinfilesend;
	  int recivesize=0,filesize;
	  OutputStream bufferwriter;
	  String recivename,filename;
	  public Socket socket;
	  
	  public ClientThread(String ipaddr,String port,MainWindow mainwindow) {
		  this.ipaddr=ipaddr;
		  this.port=Integer.parseInt(port);
		  this.mainwindow=mainwindow;
		  isinfilesend=false;
	  
	  }
	  public void run(){
		  try {
			  socket=new Socket(ipaddr,port);
			  JOptionPane.showMessageDialog(mainwindow, "连接成功", "提示",JOptionPane.INFORMATION_MESSAGE);
			  BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			  InputStream recivestream=socket.getInputStream();
		      mainwindow.writer=new PrintWriter(socket.getOutputStream());
		      mainwindow.sendstream=socket.getOutputStream();
		      while(!this.isInterrupted()){
			        if (isinfilesend) {
			        	byte[] buffer = new byte[65536];
			        	int len=recivestream.read(buffer, 0,65536/2);
			        	recivesize+=len;
			        	bufferwriter.write(buffer, 0, len);
			        //	System.out.println(len+" "+recivesize+" "+filesize);
			        	if (filesize==recivesize) {
                        	System.out.println("right");
                        	mainwindow.waitdialog.setVisible(false);
			        		bufferwriter.close();
                        	isinfilesend=false;
                        	mainwindow.donerecivefile(filename,recivename);
                        }
                        continue;
			        }
			        String buffer=reader.readLine();
			        if (buffer==null) break;
			        String[] datas = buffer.split("@");
			    	switch (datas[0]) {
						case "success!":
							JOptionPane.showMessageDialog(mainwindow, "登录成功", "提示",JOptionPane.INFORMATION_MESSAGE);
							mainwindow.showfriends(datas);
							break;
						case "failed!":
						  	JOptionPane.showMessageDialog(mainwindow, "密码错误", "错误",JOptionPane.ERROR_MESSAGE);
							break;
						case "recivefile":
							isinfilesend=true;
							recivesize=0;
							recivename=datas[1];
							filename=datas[2];
							bufferwriter= new FileOutputStream(new File(datas[2]));
							filesize=Integer.parseInt(datas[3]);
							mainwindow.waitforfile();
							break;
						case "search":
							mainwindow.showsearchusers(datas);
							break;
						case "recivemessage":
							mainwindow.recivemessage(datas);
							break;
						default:
							System.out.println(socket.getPort()+"Bad data: " +buffer);
			    	}
			  }
		      reader.close();
		      mainwindow.writer.close();
		      mainwindow.sendstream.close();
		      socket.close();
		 }catch(Exception e) {
			 mainwindow.isconnection=false;
			 JOptionPane.showMessageDialog(mainwindow, "连接失败", "错误",JOptionPane.ERROR_MESSAGE);
			 return;
		 }
	  }
}
