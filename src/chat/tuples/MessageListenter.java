package chat.tuples;

import javax.swing.JTextArea;

import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

public class MessageListenter extends Thread {

	JavaSpace space;
	JTextArea chatArea;
	String username;
	String roomName;

	public MessageListenter(JavaSpace space, JTextArea chatArea, String username, String roomName) {
		this.space = space;
		this.chatArea = chatArea;
		this.username = username;
		this.roomName = roomName;
	}

	@Override
	public void run() {
		while (true) {
		    Message template = new Message();
		    Message msg;
			
			try {		
				msg = (Message) space.read(template, null, Lease.FOREVER);
	                        
	            if (msg != null) {    
	            	if (!msg.username.equalsIgnoreCase(username) 
	            			&& msg.roomName.equalsIgnoreCase(roomName)) {
	                	chatArea.append("\n" + msg.username + ": " + msg.content);
		            	chatArea.setCaretPosition(chatArea.getLineStartOffset(chatArea.getLineCount() - 1));
	            	}
	            }
	            
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
}
