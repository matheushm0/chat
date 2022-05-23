package chat.tuples;

import net.jini.core.entry.Entry;

public class Message implements Entry {
	
	private static final long serialVersionUID = 1L;

	public String username;
	public String content;
	public String roomName;
	
	public String pmSender;
	public String pmReceiver;
	public Boolean isPrivate;
	
	public String type;
	
	public Message() {}		
}
