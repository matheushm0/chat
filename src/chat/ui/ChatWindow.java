package chat.ui;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

import chat.tuples.Message;
import chat.tuples.MessageListener;
import chat.tuples.User;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

public class ChatWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	//CONNECTION
	private JavaSpace space;
	private String username;
	private String roomName;
	
	//UI
	private JButton quitRoomButton;
	
	private JTextArea chatArea;
	private JScrollPane chatScroll;
	
	private JTextField chatTextField;	
	private JButton chatButton;
		
	public ChatWindow(String username, String roomName, JavaSpace space) {		
		this.username = username;
		this.roomName = roomName;	
		
		this.space = space;
		
		initComponents();
		setUpGUI();
		setUpChat();	
		
		startThread();	
		
		setUpQuitRoomButton();		
	}
	
	private void initComponents() {
		this.quitRoomButton = new JButton();
		
		this.chatArea = new JTextArea();
		this.chatScroll = new JScrollPane();
		
		this.chatButton = new JButton();
		this.chatTextField = new JTextField();		
	}
	
	private void setUpGUI() {
		this.setResizable(false);
		this.setSize(550, 530);
		this.setTitle("Sala " + roomName + " - Usuario: " + username);
		this.setContentPane(new JLabel());	

		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				removeUserFromRoom(username, roomName);
				ChatWindow.this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
		
		quitRoomButton.setText("Sair");
		quitRoomButton.setBounds(15, 18, 100, 25);

		JLabel chatLabel = new JLabel("SALA " + roomName.toUpperCase());
		chatLabel.setFont(new Font("Arial", Font.BOLD, 14));
		chatLabel.setBounds(225, 1, 200, 60);
		
		chatArea.setEditable(false);
		chatArea.setColumns(20);
		chatArea.setRows(5);		
		chatArea.setWrapStyleWord(true);
		chatArea.setLineWrap(true);
		chatArea.setFont(chatArea.getFont().deriveFont(12f));
		chatArea.setMargin(new Insets(10, 10, 10, 10));
		
		DefaultCaret caret = (DefaultCaret) chatArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		chatScroll.setViewportView(chatArea);
		chatScroll.setBounds(15, 70, 500, 350);
		
		chatTextField.setBounds(15, 430, 410, 40);

		chatButton.setText("Enviar");
		chatButton.setBounds(435, 430, 80, 39);
		
		chatArea.append("\n----- Bem-vindo a sala de chat " + roomName + " -----" 
				+ "\n----- Para enviar mensagens privadas digite '/p nomeDoUsuario mensagem' -----"
				+ "\n----- Para ver a lista de usuários conectados digite '/usuarios' -----\n");
		
		try {
			Message msg = new Message();
			msg.username = username;
			msg.roomName = roomName;
			msg.type = "connected";
			msg.content = "\n----- " + username + " se conectou a sala! -----";
			
			space.write(msg, null, Lease.FOREVER);
			
			Message msgTemplate = new Message();
			space.take(msgTemplate, null, Lease.FOREVER);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		this.add(quitRoomButton);
		this.add(chatLabel);
		this.add(chatScroll);
		this.add(chatTextField);
		this.add(chatButton);
		this.setVisible(true);
	}
	
	private void setUpChat() {
		ActionListener actionListener = new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae) {
				sendChatMessage();
			}
		};
		
		KeyListener keyListener = new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendChatMessage();
				}
			}
		};
		
		chatButton.addActionListener(actionListener);
		chatTextField.addKeyListener(keyListener);
	}
	
	public void sendChatMessage() {
		String message = chatTextField.getText();
		
		chatTextField.setText("");
		
		try {
			if (message.startsWith("/usuarios")) {
				chatArea.append("\n" + username + ": " + message);

				retrieveUsersList();				
			}
			else {
				Message msg = new Message();
				
				msg.username = username;
				msg.roomName = roomName;
				msg.type = "chat";
				
				if (message.startsWith("/p")) {
					msg.isPrivate = true;
					msg.pmSender = username;
					
					String[] sp = message.split(" ", 3);
					
					try {
						msg.pmReceiver = sp[1];
						msg.content = sp[2];
						
						chatArea.append("\n** Mensagem Privada enviada para " + sp[1] + ": " + sp[2] + " **");
					} catch (Exception e) {
						msg.content = message;
						msg.isPrivate = false;
					}
				}
				else {
					msg.content = message;
					msg.isPrivate = false;	
					
					chatArea.append("\n" + username + ": " + message);
				}
				
				space.write(msg, null, Lease.FOREVER);
				
				Message template = new Message();
				space.take(template, null, Lease.FOREVER);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		updateChatPosition();
	}
	
	private void updateChatPosition() {
		try {
			chatArea.setCaretPosition(chatArea.getLineStartOffset(chatArea.getLineCount() - 1));
		} catch (BadLocationException e) {
			System.out.println("BadLocationException - updateChatPosition()");
		}
		
		chatTextField.grabFocus();
	}
	
	public void startThread() {
		MessageListener messageListener = new MessageListener(space, chatArea, username, roomName);
		messageListener.start();
	}
	
	private void setUpQuitRoomButton() {
		ActionListener al = new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae) {
				swapWindows();
			}
		};
		
		quitRoomButton.addActionListener(al);
	}
	
	private void swapWindows() {
		this.removeAll();
		this.setVisible(false);
		
		new MainWindow();
		
		removeUserFromRoom(username, roomName);
	}
	
	private void removeUserFromRoom(String username, String roomName) {
		User template = new User();
		template.name = username;
		template.roomName = roomName;
		
		User user;
		
		try {
			user = (User) space.take(template, null, 1000);
			
			if (user != null) {
			
				Message msg = new Message();
				msg.username = username;
				msg.roomName = roomName;
				msg.type = "disconnected";
				msg.content = "\n----- " + username + " se desconectou da sala! -----";
				
				space.write(msg, null, Lease.FOREVER);
				
				Message msgTemplate = new Message();
				space.take(msgTemplate, null, Lease.FOREVER);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void retrieveUsersList() {		
		User template = new User();
		template.roomName = roomName;
		
		User user;
		
		List<User> users = new ArrayList<User>();
		
		try {
			while (true) {
				user = (User) space.take(template, null, 1000);
				
				if (user != null) {
					users.add(user);
				} else {					
					break;
				}	
			}
			
			if (!users.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				
				sb.append("\n\n----- Usuarios Conectados -----\n");
				
				
				for (User connectedUser : users) {	
					sb.append("\n" + connectedUser.name);
					
					space.write(connectedUser, null, Lease.FOREVER);
				}
				
				sb.append("\n\n-----------------------------------------\n");
				
				chatArea.append(sb.toString());
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
