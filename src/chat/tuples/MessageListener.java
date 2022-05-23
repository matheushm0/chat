package chat.tuples;

import javax.swing.JTextArea;

import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

public class MessageListener extends Thread {

	JavaSpace space;
	
	JTextArea chatArea;
	
	String username;
	String roomName;

	public MessageListener(JavaSpace space, JTextArea chatArea, String username, String roomName) {
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
					if (msg.roomName.equalsIgnoreCase(roomName)) {
						
						if (msg.type.contentEquals("connected") || msg.type.contentEquals("disconnected")) {
							chatArea.append(msg.content);
							chatArea.setCaretPosition(chatArea.getLineStartOffset(chatArea.getLineCount() - 1));
						}
						else {
							if (!msg.username.equalsIgnoreCase(username) && !msg.isPrivate) {
								chatArea.append("\n" + msg.username + ": " + msg.content);
								chatArea.setCaretPosition(chatArea.getLineStartOffset(chatArea.getLineCount() - 1));
							}

							if (!msg.username.equalsIgnoreCase(username) && msg.isPrivate) {
								if (msg.pmReceiver.equalsIgnoreCase(username)) {
									chatArea.append("\n** Mensagem Privada de " + msg.pmSender + ": " + msg.content + " **");
									chatArea.setCaretPosition(chatArea.getLineStartOffset(chatArea.getLineCount() - 1));
								}
							}
						}
					}

					Thread.sleep(10);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
