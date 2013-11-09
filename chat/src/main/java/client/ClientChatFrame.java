package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.JCheckBox;

public class ClientChatFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static JTextArea incomingMessagesTextArea;
	private JTextArea userWriteMessagesTextArea;
	private JCheckBox privateMessageCheckBox;

	public ClientChatFrame() {
		setResizable(false);
		setBounds(100, 100, 538, 385);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setVisible(false);

		JButton sendMessage = new JButton("Send");
		sendMessage.setBounds(358, 321, 66, 23);
		sendMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String tempMessage = userWriteMessagesTextArea.getText();
				String[] args = tempMessage.split(" ");

				userWriteMessagesTextArea.setText("");
				if (tempMessage.length() < 1) {
					return;
				} else if (tempMessage.length() > 1000) {
					JOptionPane.showMessageDialog(null,
							"The message more 1000 characters");
				} else if (privateMessageCheckBox.isSelected()
						&& !privateMessageIsEmpty(tempMessage.substring(
								args[0].length(), tempMessage.length()))) {
					String priveteMessage = "private ";
					ClientSender.sendMessage(priveteMessage.concat(tempMessage));
				} else {
					ClientSender.sendMessage(tempMessage);
				}
			}
		});
		contentPane.add(sendMessage);

		JScrollPane incomingMessagesScrollPane = new JScrollPane();
		incomingMessagesScrollPane.setViewportBorder(null);
		incomingMessagesScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		incomingMessagesScrollPane.setBounds(10, 11, 504, 264);
		contentPane.add(incomingMessagesScrollPane);

		setIncomingMessagesTextArea(new JTextArea());
		incomingMessagesScrollPane
				.setViewportView(getIncomingMessagesTextArea());
		getIncomingMessagesTextArea().setEnabled(true);
		getIncomingMessagesTextArea().setEditable(false);
		getIncomingMessagesTextArea().setLineWrap(true);
		getIncomingMessagesTextArea().setWrapStyleWord(true);

		JButton historyButton = new JButton("History");
		historyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientSender.sendMessage("ls -history");
			}
		});
		historyButton.setBounds(434, 321, 80, 23);
		contentPane.add(historyButton);

		JScrollPane userMessageScroll = new JScrollPane();
		userMessageScroll
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		userMessageScroll.setBounds(10, 292, 338, 52);
		contentPane.add(userMessageScroll);

		userWriteMessagesTextArea = new JTextArea();
		userMessageScroll.setViewportView(userWriteMessagesTextArea);
		userWriteMessagesTextArea.setBorder(UIManager.getBorder("Menu.border"));
		userWriteMessagesTextArea.setLineWrap(true);
		userWriteMessagesTextArea.setWrapStyleWord(true);

		privateMessageCheckBox = new JCheckBox("private message");
		privateMessageCheckBox.setBounds(358, 287, 139, 23);
		contentPane.add(privateMessageCheckBox);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				ClientSender.sendMessage("bye");

			}
		});
	}

	public static void setIncomingMessage(String message) {
		getIncomingMessagesTextArea().append(message);
	}

	public static JTextArea getIncomingMessagesTextArea() {
		return incomingMessagesTextArea;
	}

	public static void setIncomingMessagesTextArea(
			JTextArea incomingMessagesTextArea) {
		ClientChatFrame.incomingMessagesTextArea = incomingMessagesTextArea;
	}

	public boolean privateMessageIsEmpty(String message) {
		return  (message.trim().length()< 1) ;
	}
}
