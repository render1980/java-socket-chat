package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class ClientLoginFrame extends JFrame {
	private static final long serialVersionUID = 7821115884120138887L;
	private JTextField loginField;
    private  static JLabel labelConnectInfo;
    private static Logger logger = Logger.getLogger(ClientLoginFrame.class.getName());
    
	public ClientLoginFrame() {
		final ClientChatFrame chat = new ClientChatFrame();
		setResizable(false);
		setBounds(100, 100, 371, 197);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton connectButton = new JButton("Login");
		connectButton.setBounds(252, 135, 103, 23);
		connectButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String tempLogin = loginField.getText();
				loginField.setText(""); 

				if (tempLogin.trim().length() < 1) {
					JOptionPane.showMessageDialog(null, "Your login is empty!");
				} else if (tempLogin.length() > 100) {
					JOptionPane.showMessageDialog(null,
							"Your login very long!\nTry enter again.");
				} else if(tempLogin.trim().contains(" ")) {
					JOptionPane.showMessageDialog(null,
							"Login can't consist of more than one word!\nTry enter again.");
				}

				else {
			
					ClientSender.sendMessage("#lgn " + tempLogin);
					try {
						Thread.sleep(50);
						} catch (InterruptedException e2) {		
							logger.log(Level.SEVERE, e2.getMessage(), e2);		
						}
					if (Client.getLoginIsValid()) {
						
						dispose();
						chat.setVisible(true);
					} else {
						JOptionPane.showMessageDialog(null,
								"This login is already in chat."
										+ "\nPlease enter different login.");
					}
				}

			}
		});
		contentPane.add(connectButton);

		loginField = new JTextField();
		loginField.setBounds(99, 60, 227, 20);
		contentPane.add(loginField);
		loginField.setColumns(10);

		JLabel lblLogin = new JLabel("Login:");
		lblLogin.setBounds(42, 63, 47, 14);
		contentPane.add(lblLogin);
		
		setLabelConnectInfo(new JLabel(""));
		getLabelConnectInfo().setBounds(10, 11, 345, 37);
		contentPane.add(getLabelConnectInfo());

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				ClientSender.sendMessage("anonymus");
				ClientSender.sendMessage("bye");
				ClientSender.shutDown();
			}
		});
	}
	
	public static void setConnectLabel(String connectMessage){
		getLabelConnectInfo().setText(connectMessage);
	}
	public static JLabel getLabelConnectInfo() {
		return labelConnectInfo;
	}
	public static void setLabelConnectInfo(JLabel labelConnectInfo) {
		ClientLoginFrame.labelConnectInfo = labelConnectInfo;
	}

	

}
