package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import xml.XmlParser;

public class ServerHandler {
	private static Logger logger = Logger.getLogger(MessageDBAdapter.class.getName());


	public void begin() {
		ServerSocket ss = null;
		ServerApplication serverApp = new ServerApplication();
		try {
			XmlParser.parseXmlFile();
			ss = new ServerSocket(XmlParser.getPort());
		} catch (IOException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
		
		ConnectionsPool.getInstance();

		while (true) {
			try {
				Socket socket = ss.accept();
				if (socket != null) {
					serverApp.startNewThread(socket);
				}
			} catch (IOException e) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}
		}
	}

	public static void main(String[] args) {
		ServerHandler sh = new ServerHandler();
		logger.info("Waiting clients...");
		sh.begin();
	}
}