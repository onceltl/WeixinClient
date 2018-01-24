package weixinclient;

import java.util.Set;
import java.util.TreeSet;

public class MessageController {
	private static MessageController INSTANCE = new MessageController();
	Set<String> names;
	private MessageController(){
		names=new TreeSet<String>();
	}
	public void addname(String name) {
		names.add(name);
	}
	public void delname(String name) {
		names.remove(name);
	}
	public boolean havemessage(String name) {
		return names.contains(name);
	}
	public static MessageController getInstance() {
		return INSTANCE;
	}
}
