package command;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.Server;
import server.ServerApplication;

public class ChangeRoomCommand implements ICommand, Serializable {

	private static final long serialVersionUID = 1L;
	private int prevRoom;
    private int nextRoom;
    private Server serverInvoker;
    private static Logger logger = Logger.getLogger(Server.class.getName());
    
    
    
    public ChangeRoomCommand(int nextRoom) {
        this.nextRoom = nextRoom;
    }

    public void setServerInvoker(Server invoker) {
    	this.serverInvoker = invoker;
    	this.prevRoom = serverInvoker.getCurRoom();
    }
   
    public void execute() { 
        int result = ServerApplication.bindServerToRoom(serverInvoker, nextRoom);
        if (result == -1) {
        	try {
				serverInvoker.sendMessage("There is no such room!");
			} catch (IOException e) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}
            return;
        }
        String login = serverInvoker.getLogin();
        ServerApplication.sendMessageToRoom(prevRoom, String.format("User %s has left us", login));
        ServerApplication.sendMessageToRoom(nextRoom, String.format("User %s has joined us", login)); 
        log();
        
        try {
			serverInvoker.sendMessage("you have entered in room #" + nextRoom);
		} catch (IOException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
    }
   
    private void log() {
        String login = serverInvoker.getLogin();
        if (login != null) {
            if (prevRoom > 0 & nextRoom > 0) {
                logger.info(String.format("User %s has moved from room #%d to room #%d",
                                login, prevRoom, nextRoom));
            } else if (prevRoom == 0 & nextRoom > 0) {
                logger.info(String.format("User %s has moved from general chat to room #%d",
                                login, nextRoom));
            } else if (prevRoom > 0 & nextRoom == 0) {
                logger.info(String.format("User %s has moved from room #%d to general chat",
                                login, prevRoom));
            }
        }
    }
}
