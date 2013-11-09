package command;

import java.io.Serializable;
import server.PrivateMessageSender;
import server.Server;

public class SendPrivateMessageCommand implements ICommand, Serializable {
	private static final long serialVersionUID = 1L;
	private String addresseeLogin;
	private String message;
	private Server serverInvoker;

	public SendPrivateMessageCommand(String addresseeLogin, String message) {
		this.addresseeLogin = addresseeLogin;
		this.message = message;
	}

	public void setServerInvoker(Server invoker) {
		this.serverInvoker = invoker;
	}

	public void execute() {
		if (serverInvoker.getLogin().equals(addresseeLogin)) {
			return;
		}
		PrivateMessageSender messageSender = new PrivateMessageSender(
				serverInvoker);
		messageSender.sendPrivateMessage(addresseeLogin, message);
	}
}
