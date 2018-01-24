package weixinclient;

import java.awt.Component;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;

public class ImageCellRender extends DefaultListCellRenderer {  
	  
    private static final long serialVersionUID = 1L;  
  
    public Component getListCellRendererComponent(JList<? extends Object> list,  
            Object value, int index, boolean isSelected, boolean cellHasFocus) {  
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);  
          
            try {
            	String str=(String)value;
            
            	Image img = ImageIO.read(this.getClass().getResource("/images/icon.jpg"));
            	if (MessageController.getInstance().havemessage(str)) img = ImageIO.read(this.getClass().getResource("/images/icon2.png"));
                Image smallImg =img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);//缩小
                ImageIcon icon = new ImageIcon(smallImg);  
                String text="<html>"+(String)value+"<br/>"+"无个性不签名！"+" <html/>";
                setIcon(icon);  
                setText(text); 
      
            } catch (Exception e) {  
                e.printStackTrace();  
            }  

        return this;  
    }  
}  