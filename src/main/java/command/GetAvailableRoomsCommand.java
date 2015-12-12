package command;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.Server;
import server.ServerApplication;

public class GetAvailableRoomsCommand implements ICommand, Serializable {
	private static final long serialVersionUID = 1L;
	private Server serverInvoker;
	private static Logger logger = Logger.getLogger(Server.class
			.getName());
	
	public void setServerInvoker(Server invoker) {
		this.serverInvoker = invoker;
	}
    
    public void execute() {
    	String availableRooms = ServerApplication.getAvailableRooms();
    	try {
			serverInvoker.sendMessage(availableRooms);
		} catch (IOException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
    }
}
