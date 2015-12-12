package command;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.MessageDBAdapter;
import server.Server;

public class GetPrivateMessagesHistoryCommand implements ICommand, Serializable {
	private static final long serialVersionUID = 1L;
	private Server serverInvoker;
	private static Logger logger = Logger.getLogger(Server.class
			.getName());
   
    public void setServerInvoker(Server invoker) {
		this.serverInvoker = invoker;
	}
   
    public void execute() {
    	String login = serverInvoker.getLogin();
    	MessageDBAdapter messageDBAdapter = new MessageDBAdapter();
    	String messageHistory = messageDBAdapter.getMessages(login);
    	try {
			serverInvoker.sendMessage(messageHistory);
		} catch (IOException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
    }
}
