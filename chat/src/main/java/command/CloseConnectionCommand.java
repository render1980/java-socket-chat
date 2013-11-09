package command;

import static server.ServerApplication.removeServer;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.LoginExistsException;
import server.PrivateMessageSender;
import server.Server;
import server.ServerApplication;

public class CloseConnectionCommand implements ICommand, Serializable {
	private static final long serialVersionUID = 1L;
	private Server serverInvoker;
	private static Logger logger = Logger.getLogger(Server.class.getName());

	public void setServerInvoker(Server invoker) {
		this.serverInvoker = invoker;
	}

	public void execute() {
		serverInvoker.closeConnection();
		removeServer(serverInvoker);
		PrivateMessageSender.removeAdresseeFromAllPrivateAdressees(serverInvoker
				.getLogin());
		try {
			Server.removeLogin(serverInvoker.getLogin());
		} catch (LoginExistsException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
		ServerApplication.sendMessageToRoom(0,
				String.format("User %s has left", serverInvoker.getLogin()));
		logger.info(String.format("User %s exit chat", serverInvoker.getLogin()));
	}
}
