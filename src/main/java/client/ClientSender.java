package client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import command.ICommand;

public class ClientSender {

	private static Logger logger = Logger.getLogger(ClientSender.class.getName());
	
	public static void sendMessage(final String msg) {
		ICommand command = CommandParser.getCommand(msg, Client.getClientLogin());
		try {
			Client.getOut().writeObject(command);
			Client.getOut().flush();
		} catch (IOException ioException) {
			logger.log(Level.SEVERE, ioException.getMessage(), ioException);
		}
	}

	public static void shutDown() {
		System.exit(0);
	}
}
