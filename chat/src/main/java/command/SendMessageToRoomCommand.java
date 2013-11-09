package command;

import java.io.Serializable;

import server.Server;
import server.ServerApplication;

public class SendMessageToRoomCommand implements ICommand, Serializable {
	private static final long serialVersionUID = 1L;
	private String message;
	private Server serverInvoker;
	
	public SendMessageToRoomCommand(String message) {
		this.message = message;
	}
	
	public void setServerInvoker(Server invoker) {
		this.serverInvoker = invoker;
	}
	
	public void execute() {
		int room = serverInvoker.getCurRoom();
		String login = serverInvoker.getLogin();
		ServerApplication.sendMessageToRoom(room, String.format("%s> %s", login, message));
	}
}
