package chat.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

public class MainWindow extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	//UI
	private JPanel chatList;
	private JScrollPane chatScrollPane;
//	private List<JCheckBox> topicsCheckBox;
	private JButton confirmButton;
	private JButton newChatRoomButton;
	private JButton updateButton;
	private JTextField usernameField;
	
	public MainWindow() {		
		initComponents();
		setUpGUI();
		setUpNewChatRoomButton();
	}
	
	private void initComponents() {
		this.chatList = new JPanel();
		this.chatScrollPane = new JScrollPane();
		
		this.usernameField = new JTextField();
		
		this.updateButton = new JButton();
		this.confirmButton = new JButton();
		this.newChatRoomButton = new JButton();		
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
		confirmButton.addActionListener(this);
		
		this.add(sensorLabel);
		this.add(chatScrollPane);
		this.add(usernameLabel);
		this.add(usernameField);
		this.add(updateButton);
		this.add(confirmButton);
		this.add(newChatRoomButton);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (usernameField.getText().isEmpty()) {
			usernameField.setBorder(new LineBorder(Color.RED, 1));

			return;
		}
		else {
			usernameField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
		}
		
		this.setVisible(false);
		
		new ChatWindow(usernameField.getText());
	}
	
	public void setUpNewChatRoomButton() {
		ActionListener al = new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae) {
				
				String roomName;
				
				while (true) {
					roomName = JOptionPane.showInputDialog(getParent(), "Digite o nome da sala");

					if (roomName == null) {
						break;
					}
					
					if (roomName.isEmpty()) {
						JOptionPane.showMessageDialog(null, "O nome da sala não pode ser vazio", "Erro",
								JOptionPane.ERROR_MESSAGE);
					} else {
						MainWindow.this.setVisible(false);
						
						//TODO ADICIONAR NOME DA SALA
						new ChatWindow(usernameField.getText());
						
						break;
					}
				}
			}
		};
		
		newChatRoomButton.addActionListener(al);
	}	
}
