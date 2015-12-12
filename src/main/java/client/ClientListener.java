package client;

import java.io.IOException;

import javax.swing.JOptionPane;

public class ClientListener implements Runnable {

	private String historyIncom = "#hstr";
	private String userMessage = "";
	private String historyMessage = "";

	public void setUserMessage(String message) {
		if (!(message.length() < 1) && (!(message.length() > 1000))) {
			userMessage = message;
		}
	}

	public String getUserMessage() {
		return userMessage;
	}

	public void setHistoryMessage(String message) {
		historyMessage = message;
	}

	public String getHistoryMessage() {
		return historyMessage;
	}

	public ClientListener() {
	}

	public void run() {
		while (Client.getIn() != null) {
			String message = getMessageFromServer();

			if (isEmptyHistoryCommand(message)) {
				continue;
			}

			if (isConnectionSuccessfull(message)) {
				ClientLoginFrame.setConnectLabel(message);
				continue;
			}
			if (isBye(message)) {
				ClientSender.shutDown();
			}

			loginValidation(message);
			if (messageSeporatorHistoryOrUserMessage(message)) {
				runHistoryView(getHistoryMessage());
			} else {
				setIncommingMessageForChatView(getUserMessage());
			}
		}
	}

	public String getMessageFromServer() {
		String message = "";
		try {
			message = (String) Client.getIn().readObject();
		} catch (ClassNotFoundException classNFexcp) {
			JOptionPane.showMessageDialog(null,
					"Error 2.\nClass not found exception.");
		} catch (IOException ioExcp) {
			JOptionPane.showMessageDialog(null, "Sorry. Server is dead.");
			ClientSender.shutDown();
		}
		return message;
	}

	public boolean isEmptyHistoryCommand(String message) {
		return message.equals("#hstr>");
	}

	public boolean isBye(String message) {
		return message.equals("bye");
	}

	public boolean isConnectionSuccessfull(String message) {
		return message
				.equals("Connection successfull. Please, input your login..");
	}

	public void runHistoryView(String message) {
		History.getInstance();
		History.setHistoryTextArea(message);
	}

	public void setIncommingMessageForChatView(String message) {
		ClientChatFrame.setIncomingMessage(message + "\n");
		ClientChatFrame.getIncomingMessagesTextArea().setCaretPosition(
				ClientChatFrame.getIncomingMessagesTextArea().getDocument()
						.getLength());
	}

	public void loginValidation(String message) {
		if (message.equals("Identification failed. Input correct login")) {
			Client.setLoginIsValid(false);
		} else {
			Client.setLoginIsValid(true);
		}
	}

	public boolean messageSeporatorHistoryOrUserMessage(String message) {
		String[] args = message.split(">");
		if (args.length > 1 && args[0].trim().equals(historyIncom)) {
			setHistoryMessage(args[1]);
			return true;
		} else {
			setUserMessage(message);
			return false;
		}
	}

}