package chat.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class MainWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	//UI
	private JPanel chatList;
	private JScrollPane chatScrollPane;
//	private List<JCheckBox> topicsCheckBox;
	private JButton confirmButton;
	private JButton newChatRoomButton;
	private JButton updateButton;
//	private JLabel errorLabel;
	private JTextField usernameField;
	
	public MainWindow() {
		initComponents();
		setUpGUI();
	}
	
	private void initComponents() {
		this.chatList = new JPanel();
		this.chatScrollPane = new JScrollPane();
		
		this.usernameField = new JTextField();
		
		this.updateButton = new JButton();
		this.confirmButton = new JButton();
		this.newChatRoomButton = new JButton();
		
//		this.errorLabel = new JLabel();
	}
	
	private void setUpGUI() {
		this.setResizable(false);
		this.setSize(400, 550);
		this.setTitle("Chat");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(new JLabel());	
		
		JLabel sensorLabel = new JLabel("Lista de salas");
		sensorLabel.setFont(new Font("Arial", Font.BOLD, 14));
		sensorLabel.setBounds(150, 0, 200, 60);

		chatList.setBackground(Color.WHITE);
		chatList.setLayout(new BoxLayout(chatList, BoxLayout.Y_AXIS));
		
		chatScrollPane.setViewportView(chatList);
		chatScrollPane.setBounds(40, 50, 310, 300);
		
		JLabel usernameLabel = new JLabel("Nome de usuário");
		usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
		usernameLabel.setBounds(40, 340, 200, 60);
		
		usernameField.setBounds(40, 385, 310, 25);
		
		updateButton.setText("Atualizar");
		updateButton.setBounds(40, 430, 100, 30);
		
		newChatRoomButton.setText("Nova Sala");
		newChatRoomButton.setBounds(145, 430, 100, 30);
		
		confirmButton.setText("Confirmar");
		confirmButton.setBounds(250, 430, 100, 30);
//		confirmButton.addActionListener(this);
		
		this.add(sensorLabel);
		this.add(chatScrollPane);
		this.add(usernameLabel);
		this.add(usernameField);
		this.add(updateButton);
		this.add(confirmButton);
		this.add(newChatRoomButton);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		new MainWindow();
	}
	
}
