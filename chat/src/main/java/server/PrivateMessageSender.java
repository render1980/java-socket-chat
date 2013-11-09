package server;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrivateMessageSender implements Serializable {

	private static final long serialVersionUID = 1L;
	private Server serverSender;
	private String senderLogin;
	private String dD = " : ";
	private MessageDBAdapter privateMessageDBConnector = new MessageDBAdapter();
	private static Logger logger = Logger.getLogger(PrivateMessageSender.class
			.getName());

	public PrivateMessageSender(Server serverSender) {
		this.serverSender = serverSender;
		this.senderLogin = serverSender.getLogin();
	}

	public void setLogin(String login) {
		senderLogin = login;
	}

	public void sendPrivateMessage(String privateAddressee,
			String privateMessage) {
		senderLogin = serverSender.getLogin();
		Map<String, Server> privateAdressees = serverSender
				.getPrivateAdressees();
		if (privateAdressees.containsKey(privateAddressee)) {
			Server addresseeServer = privateAdressees.get(privateAddressee);

			// insert message in db
			byte success = privateMessageDBConnector.insertMessage(senderLogin,
					privateAddressee, privateMessage);
			if (success == -1) {
				try {
					this.serverSender
							.sendMessage("private message service is not available now..");
				} catch (IOException e) {
					logger.log(Level.WARNING, e.getMessage(), e);
				}
				return;
			}
			try {
				addresseeServer.sendMessage("PRIVATE MESSAGE FROM "
						+ senderLogin + dD + privateMessage);
			} catch (IOException e) {
				logger.log(Level.WARNING, e.getMessage(), e);
				ServerApplication.sendMessageToRoom(
						addresseeServer.getCurRoom(), "");
				return;
			}
			try {
				serverSender.sendMessage("PRIVATE MESSAGE FOR "
						+ privateAddressee + dD + privateMessage);
			} catch (IOException e) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}

		} else if (Server.noUserWithLogin(privateAddressee)) {
			try {
				serverSender.sendMessage("There is no user with name "
						+ privateAddressee + " in our chat!");
			} catch (IOException e) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}
		} else {
			Server addresseeServer = sendPrivateMessageToUser(senderLogin,
					privateAddressee, privateMessage);
			if (addresseeServer != null) {
				try {
					serverSender.sendMessage("PRIVATE MESSAGE FOR "
							+ privateAddressee + dD + privateMessage);
				} catch (IOException e) {
					logger.log(Level.WARNING, e.getMessage(), e);
				}
				serverSender.putNewPrivateAdressee(privateAddressee,
						addresseeServer);
			}
		}
	}

	public Server sendPrivateMessageToUser(String messageSender,
			String addresseeLogin, String message) {
		Map<Integer, List<Server>> roomsServers = ServerApplication
				.getRoomsServers();
		for (int roomNumber = 0; roomNumber < roomsServers.size(); roomNumber++) {
			List<Server> servers = roomsServers.get(roomNumber);
			for (Server server : servers) {
				if (addresseeLogin.equals(server.getLogin())) {
					// insert message in db
					byte success = privateMessageDBConnector.insertMessage(
							messageSender, addresseeLogin, message);
					if (success == -1) {
						try {
							serverSender
									.sendMessage("private message service is not available now..");
						} catch (IOException e) {
							logger.log(Level.WARNING, e.getMessage(), e);
						}
					} else {
						try {
							server.sendMessage("PRIVATE MESSAGE FROM "
									+ messageSender + dD + message);
						} catch (IOException e) {
							try {
								serverSender
								.sendMessage("There is no such user  in our chat!");
								ServerApplication.sendMessageToRoom(
										server.getCurRoom(), "");
							} catch (IOException e1) {
								logger.log(Level.WARNING, e1.getMessage(), e1);
							}
							return null;
						}
						return server;
					}
				}
			}
		}
		return null;
	}

	public static void removeAdresseeFromAllPrivateAdressees(String login) {
		Map<Integer, List<Server>> roomsServers = ServerApplication
				.getRoomsServers();
		for (int roomNumber = 0; roomNumber < roomsServers.size(); roomNumber++) {
			List<Server> roomServers = roomsServers.get(roomNumber);
			for (Server server : roomServers) {
				server.deleteAdresseeFromList(login);
			}
		}
	}

	public void insertClient(String login) {
		privateMessageDBConnector.insertClient(login);
	}

	public String getMessageHistory(String login) {
		return privateMessageDBConnector.getMessages(login);
	}
}
