package chat.tuples;

import javax.swing.JTextArea;

import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

public class MessageListenter extends Thread {
	
	JavaSpace space;
	JTextArea chatArea;
	String username;
	
	public MessageListenter(JavaSpace space, JTextArea chatArea, String username) {
		this.space = space;
		this.chatArea = chatArea;
		this.username = username;
	}

	@Override
	public void run() {
		while (true) {
		    Message template = new Message();
		    Message msg;
			
			try {		
				msg = (Message) space.take(template, null, 60 * 1000);
	            
//	            if (msg == null) {
//	                System.out.println("Tempo de espera esgotado. Encerrando...");
//	                System.exit(0);
//	            }
	                        
	            if (msg != null) {    
	            	if (!msg.username.equalsIgnoreCase(username)) {
	                	chatArea.append("\n" + msg.username + ": " + msg.content);
		            	chatArea.setCaretPosition(chatArea.getLineStartOffset(chatArea.getLineCount() - 1));
	            	}
	            	else {
	            		space.write(msg, null, Lease.FOREVER);
	            	}
	            }
	            
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
}
