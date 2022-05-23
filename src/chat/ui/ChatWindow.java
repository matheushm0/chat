package chat.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
	
	private JPanel connectedUsersPanel;
	private JScrollPane usersScrollPane;
	
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
		
		this.connectedUsersPanel = new JPanel();
		this.usersScrollPane = new JScrollPane();
		
		this.chatArea = new JTextArea();
		this.chatScroll = new JScrollPane();
		
		this.chatButton = new JButton();
		this.chatTextField = new JTextField();		
	}
	
	private void setUpGUI() {
		this.setResizable(false);
		this.setSize(800, 530);
		this.setTitle("Sala " + roomName);
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
		
		JLabel usersLabel = new JLabel("Usuarios Logados");
		usersLabel.setFont(new Font("Arial", Font.BOLD, 14));
		usersLabel.setBounds(65, 30, 200, 60);
		
		connectedUsersPanel.setBackground(Color.WHITE);
		connectedUsersPanel.setLayout(new BoxLayout(connectedUsersPanel, BoxLayout.Y_AXIS));
		
		usersScrollPane.setViewportView(connectedUsersPanel);
		usersScrollPane.setBounds(15, 70, 230, 400);
		
		JLabel chatLabel = new JLabel("Chat");
		chatLabel.setFont(new Font("Arial", Font.BOLD, 14));
		chatLabel.setBounds(500, 30, 200, 60);
		
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
		chatScroll.setBounds(265, 70, 500, 350);
		
		chatTextField.setBounds(265, 430, 410, 40);

		chatButton.setText("Enviar");
		chatButton.setBounds(685, 430, 80, 39);
		
		chatArea.append("\n----- Bem-vindo a sala de chat " + roomName + " -----" 
				+ "\n----- Para enviar mensagens privadas digite '/p nomeDoUsuario mensagem' -----");
		
		
		this.add(quitRoomButton);
		this.add(usersLabel);
		this.add(usersScrollPane);
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
			Message msg = new Message();
			
			msg.username = username;
			msg.roomName = roomName;
			
			if (message.startsWith("/p")) {
				msg.isPrivate = true;
				msg.pmSender = username;
				
				String[] sp = message.split(" ", 3);
				
				try {
					msg.pmReceiver = sp[1];
					msg.content = sp[2];
				} catch (Exception e) {
					msg.content = message;
					msg.isPrivate = false;
				}
			}
			else {
				msg.content = message;
				msg.isPrivate = false;	
			}
						
			chatArea.append("\n" + username + ": " + message);
			
			space.write(msg, null, Lease.FOREVER);
			
			Message template = new Message();
			space.take(template, null, Lease.FOREVER);
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
		
		try {
			space.take(template, null, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
