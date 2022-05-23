package chat.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import chat.tuples.ChatRoom;
import chat.tuples.Lookup;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

public class MainWindow extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	//CONNECTION
	private JavaSpace space;
	
	//UI
	private JPanel chatList;
	private JScrollPane chatScrollPane;
	
	private ButtonGroup roomButtonGroup;	
	
	private JButton confirmButton;
	private JButton newChatRoomButton;
	private JButton updateButton;
	
	private JTextField usernameField;
	
	private JLabel errorLabel;
	
	public MainWindow() {		
		initComponents();
		
		Lookup finder = new Lookup(JavaSpace.class);
		this.space = (JavaSpace) finder.getService();
		
		initSpace();
		
		setUpGUI();
		setUpNewChatRoomButton();
		setUpUpdateRoomButton();
	}
	
	private void initComponents() {
		this.chatList = new JPanel();
		this.chatScrollPane = new JScrollPane();
		
		this.usernameField = new JTextField();
		
		this.updateButton = new JButton();
		this.confirmButton = new JButton();
		this.newChatRoomButton = new JButton();		
		
		this.roomButtonGroup = new ButtonGroup();
		
		this.errorLabel = new JLabel();
	}

	public void initSpace() {
		try {
			System.out.println("Procurando pelo servico JavaSpace...");

			if (space == null) {
				System.out.println("O servico JavaSpace nao foi encontrado. Encerrando...");
				System.exit(-1);
			}

			System.out.println("O servico JavaSpace foi encontrado.");
			System.out.println(space);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setUpGUI() {
		this.setResizable(false);
		this.setSize(400, 580);
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
		if (!validateFields()) {
			return;
		}
		
		this.removeAll();
		this.setVisible(false);
		
		String roomName = roomButtonGroup.getSelection().getActionCommand();

		new ChatWindow(usernameField.getText(), roomName, space);
	}
	
	private boolean validateFields() {
		boolean isValid = true;
		
		if (usernameField.getText().isEmpty()) {
			usernameField.setBorder(new LineBorder(Color.RED, 1));

			isValid = false;
		} else {
			usernameField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
		}
		 
		if (!validateButtonGroup(roomButtonGroup)) {
			isValid = false;
			
			errorLabel.setText("Você deve escolher uma sala antes de confirmar!");
			errorLabel.setForeground(Color.RED);
			errorLabel.setFont(new Font("Arial", Font.BOLD, 12));
			errorLabel.setBounds(55, 455, 300, 60);
			
			this.add(errorLabel);	
		} else {
			errorLabel.setForeground(this.getBackground());
		} 					

		return isValid;
	}
	
	private boolean validateButtonGroup(ButtonGroup buttonGroup) {
		for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
			AbstractButton button = buttons.nextElement();

			if (button.isSelected()) {
				return true;
			}
		}

		return false;
	}
	
	private void setUpNewChatRoomButton() {
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
					} 
						
					if (usernameField.getText().isEmpty()) {
						JOptionPane.showMessageDialog(null, "O nome de usuário não pode ser vazio", "Erro",
								JOptionPane.ERROR_MESSAGE);
					} 

					if (!usernameField.getText().isEmpty() && !roomName.isEmpty()) {
						
						if (!verifyIfRoomExists(roomName)) {
							MainWindow.this.removeAll();
							MainWindow.this.setVisible(false);

							new ChatWindow(usernameField.getText(), roomName, space);

							ChatRoom chatRoom = new ChatRoom();
							
							chatRoom.name = roomName;
							
							try {
								space.write(chatRoom, null, Lease.FOREVER);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							break;
						} else {
							JOptionPane.showMessageDialog(null, "Já existe uma sala com o nome: " + roomName, "Erro",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		};
		
		newChatRoomButton.addActionListener(al);
	}	
	
	private boolean verifyIfRoomExists(String roomName) {
		ChatRoom template = new ChatRoom();
		template.name = roomName;
		
		ChatRoom chatRoom;
		
		try {
			chatRoom = (ChatRoom) space.read(template, null, 1000);
			
			if (chatRoom != null) {
				return true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private void setUpUpdateRoomButton() {
		ActionListener al = new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae) {
				updateAvailableRooms();
			}
		};
		
		updateButton.addActionListener(al);
	}
	
	private void updateAvailableRooms() {		
		ChatRoom template = new ChatRoom();
		ChatRoom chatRoom;
		
		List<ChatRoom> rooms = new ArrayList<ChatRoom>();
		roomButtonGroup = new ButtonGroup();
		chatList.removeAll();
		
		try {
			while (true) {
				chatRoom = (ChatRoom) space.take(template, null, 3 * 1000);
				
				if (chatRoom != null) {
					rooms.add(chatRoom);
				} else {					
					break;
				}	
			}
			
			if (rooms.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Não existe nenhuma sala criada, tente novamente mais tarde",
						"Aviso", JOptionPane.INFORMATION_MESSAGE);
			} else {
				for (ChatRoom room : rooms) {				
					JRadioButton radioButton = new JRadioButton(room.name);
					radioButton.setFont(new Font("Arial", Font.PLAIN, 12));
					radioButton.setBackground(new Color(0, 0, 0, 0));
					radioButton.setOpaque(false);
					radioButton.setActionCommand(room.name);

					roomButtonGroup.add(radioButton);
					chatList.add(radioButton);

					SwingUtilities.updateComponentTreeUI(chatList);
					
					space.write(room, null, Lease.FOREVER);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
