package command;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.LoginExistsException;
import server.PrivateMessageSender;
import server.Server;
import server.ServerApplication;

public class IdentifyClientCommand implements ICommand, Serializable {
	private static final long serialVersionUID = 1L;
	private String login;
	private Server serverInvoker;
	private PrivateMessageSender messageSender;
	private static Logger logger = Logger.getLogger(Server.class.getName());
	
	private static final String IDENTIFICATION_FAILED = "Identification failed. Input correct login";
	
	public IdentifyClientCommand(String login) {
		this.login = login;
	}
	
	public void setServerInvoker(Server invoker) {
		this.serverInvoker = invoker;
		this.messageSender = new PrivateMessageSender(serverInvoker);
	}

	public void execute() {
		boolean identified = checkLogin(login);
		if (!identified) {
			try {
				serverInvoker.sendMessage(IDENTIFICATION_FAILED);
			} catch (IOException e) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}
		}
		else {
			try {
				Server.addLogin(login);
			} catch (LoginExistsException e) {
				logger.log(Level.WARNING, e.getMessage(), e);
				try {
					serverInvoker.sendMessage(IDENTIFICATION_FAILED);
				} catch (IOException e1) {
					logger.log(Level.WARNING, e.getMessage(), e);
				}
				return;
			}
			messageSender.insertClient(login);
			serverInvoker.setLogin(login);
			
			logger.info(String.format("User %s has joined us", login));
			ServerApplication.bindServerToRoom(serverInvoker, 0);
			try {
				serverInvoker.sendMessage("Identification successful! Welcome, " + login);
			} catch (IOException e) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}
			ServerApplication.sendMessageToRoom(0, String.format("User %s has joined us", login));
		}
	}
	
	private boolean checkLogin(String login) {
		if (login == null || login.length() == 0) {
			return false;
		}
		return true;
	}
}
