package chat.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
import chat.tuples.MessageListenter;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

public class ChatWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	//CONNECTION
	private JavaSpace space;
	private String username;
	private String roomName;
	
	//UI
	private JPanel usersPanel;
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
	}
	
	private void initComponents() {
		this.usersPanel = new JPanel();
		this.usersScrollPane = new JScrollPane();
		
		this.chatArea = new JTextArea();
		this.chatScroll = new JScrollPane();
		
		this.chatButton = new JButton();
		this.chatTextField = new JTextField();
	}
	
	private void setUpGUI() {
		this.setResizable(false);
		this.setSize(800, 500);
		this.setTitle("Sala " + roomName);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(new JLabel());	
		
		JLabel usersLabel = new JLabel("Usuarios Logados");
		usersLabel.setFont(new Font("Arial", Font.BOLD, 14));
		usersLabel.setBounds(65, 0, 200, 60);
		
		usersPanel.setBackground(Color.WHITE);
		usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.Y_AXIS));
		
		usersScrollPane.setViewportView(usersPanel);
		usersScrollPane.setBounds(15, 40, 230, 400);
		
//		for (String subscribedTopic : subscribedTopics) {
//			JLabel topic = new JLabel(subscribedTopic);
//			topic.setFont(new Font("Arial", Font.PLAIN, 12));	
//			topic.setAlignmentX(Component.CENTER_ALIGNMENT);
//			topic.setBorder(new EmptyBorder(5,0,0,0));
//
//			
//			topicPanel.add(topic);
//		}
		
		JLabel chatLabel = new JLabel("Chat");
		chatLabel.setFont(new Font("Arial", Font.BOLD, 14));
		chatLabel.setBounds(500, 0, 200, 60);
		
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
		chatScroll.setBounds(265, 40, 500, 350);
		
		chatTextField.setBounds(265, 400, 410, 40);

		chatButton.setText("Enviar");
		chatButton.setBounds(685, 400, 80, 39);
		
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
			msg.content = message;
			msg.roomName = roomName;
			
			chatArea.append("\n" + msg.username + ": " + msg.content);
			
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
		MessageListenter messageListenter = new MessageListenter(space, chatArea, username, roomName);

		messageListenter.start();
	}
}
