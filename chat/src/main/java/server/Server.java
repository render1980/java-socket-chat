package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import command.ICommand;

public class Server implements Runnable, Serializable {

	private static final long serialVersionUID = 1L;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Socket clientSocket;
	private String login = null;
	private int curRoom = 0;
	private Map<String, Server> privateAdressees = new HashMap<String, Server>();
	private static Logger logger = Logger.getLogger(MessageDBAdapter.class
			.getName());

	public Server(Socket newSocket) {
		this.clientSocket = newSocket;
	}

	public void run() {
		try {
			init();
			getCommandsFromClient();
		} catch (IOException ioException) {
			return;
		}
	}

	private void init() throws IOException {
		out = new ObjectOutputStream(clientSocket.getOutputStream());
		in = new ObjectInputStream(clientSocket.getInputStream());
		sendMessage("Connection successfull. Please, input your login..");
	}

	private void getCommandsFromClient() throws IOException {
		ICommand cmd = null;

		while (true) {
			try {
				cmd = (ICommand) in.readObject();
				cmd.setServerInvoker(this);
				cmd.execute();
			} catch (ClassNotFoundException e) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}
		}
	}

	public void sendMessage(final String msg) throws IOException {
		try {
			out.writeObject(msg);
			out.flush();
		} catch (SocketException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw new IOException();
		}
	}

	public void putNewPrivateAdressee(String privateAddressee,
			Server addresseeServer) {
		privateAdressees.put(privateAddressee, addresseeServer);
	}

	public void deleteAdresseeFromList(String adrLogin) {
		privateAdressees.remove(adrLogin);
	}

	public Map<String, Server> getPrivateAdressees() {
		return privateAdressees;
	}

	public static void addLogin(String login) throws LoginExistsException {
		if (!ServerApplication.getLogins().contains(login)) {
			ServerApplication.getLogins().add(login);
		} else {
			throw new LoginExistsException("This login is already exists");
		}
	}

	public static void removeLogin(String login) throws LoginExistsException {
		if (ServerApplication.getLogins().contains(login)) {
			ServerApplication.getLogins().remove(login);
		}
	}

	public static boolean noUserWithLogin(String login) {
		if (ServerApplication.getLogins().contains(login)) {
			return false;
		}
		return true;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public int getCurRoom() {
		return this.curRoom;
	}

	public void setCurRoom(int room) {
		this.curRoom = room;
	}

	public void closeConnection() {
		try {
			sendMessage("bye");
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		try {
			in.close();
			out.close();
		} catch (IOException ioException) {
			logger.log(Level.SEVERE, ioException.getMessage(), ioException);
		}
	}
}