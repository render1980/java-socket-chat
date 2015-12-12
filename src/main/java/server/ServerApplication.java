package server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerApplication {

	private static Map<Integer, List<Server>> roomsServers = new ConcurrentHashMap<Integer, List<Server>>(100);
	private static final int ROOMS_AMOUNT = 5;
	private static List<String> logins = new ArrayList<String>();
	private static Logger logger = Logger.getLogger(ServerApplication.class
			.getName());

	public ServerApplication() {
		init();
	}

	public void startNewThread(Socket clientSocket) {
		Server server = new Server(clientSocket);
		Thread thread = new Thread(server);
		thread.start();
		// add new server to default room
		List<Server> roomServers = roomsServers.get(0);
		roomServers.add(server);
	}

	private void init() {
		for (int i = 0; i < ROOMS_AMOUNT; i++) {
			roomsServers.put(i, new ArrayList<Server>());
		}
	}

	public static void removeServer(Server server) {
		Iterator<Entry<Integer, List<Server>>> entries = roomsServers
				.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<Integer, List<Server>> pairs = entries.next();
			List<Server> roomServers = pairs.getValue();
			roomServers.remove(server);
		}
	}

	public static int bindServerToRoom(Server server, int room) {
		List<Server> roomServers = roomsServers.get(room);
		if (roomServers == null) {
			return -1;
		}
		removeServer(server);
		if (!roomServers.contains(server)) {
			roomServers.add(server);
			server.setCurRoom(room);
		}
		return 0;
	}

	public static void sendMessageToRoom(int room, String message) {
		List<Server> roomServers = roomsServers.get(room);
		for (int i = 0; i < roomServers.size(); i++) {

			try {
				roomServers.get(i).sendMessage(message);
			} catch (IOException e) {

				int tempRoom = roomServers.get(i).getCurRoom();
				String tempLogin = roomServers.get(i).getLogin();
				removeServer(roomServers.get(i));
				ServerApplication.sendMessageToRoom(tempRoom,
						String.format("User %s has left", tempLogin));
				logger.info(String.format("User %s exit chat", tempLogin));
				PrivateMessageSender
						.removeAdresseeFromAllPrivateAdressees(tempLogin);
				try {
					Server.removeLogin(tempLogin);
				} catch (LoginExistsException e1) {
					logger.log(Level.WARNING, e.getMessage(), e);
				}

				i--;
			}
		}
	}

	public static String getAvailableRooms() {
		Set<Integer> rooms = roomsServers.keySet();
		StringBuffer sb = new StringBuffer();
		sb.append("available rooms: \n");
		for (Integer room : rooms) {
			sb.append(String.format("* #%d%n", room));
		}
		return sb.toString();
	}

	public static Map<Integer, List<Server>> getRoomsServers() {
		return roomsServers;
	}

	public static List<String> getLogins() {
		return logins;
	}

	public static void setLogins(List<String> logins) {
		ServerApplication.logins = logins;
	}
}