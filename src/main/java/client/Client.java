package client;

import xml.XmlParser;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
	private static Socket requestSocket;
	private String message;
	private ClientListener listener = null;
	private static ObjectOutputStream out;
	private static ObjectInputStream in;
	private static String host;
	private static int port;
	private static String clientLogin;

	public static String getClientLogin() {
		return clientLogin;
	}

	private static boolean loginIsValid = true;
	private static Logger logger = Logger.getLogger(Client.class.getName());

	public static boolean getLoginIsValid() {
		return loginIsValid;
	}

	public static void setLoginIsValid(boolean valid) {
		loginIsValid = valid;
	}

	public static Socket getRequestSocket() {
		return requestSocket;
	}

	public static void setRequestSocket(Socket requestSocket) {
		Client.requestSocket = requestSocket;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ClientListener getListener() {
		return listener;
	}

	public static ObjectInputStream getIn() {
		return in;
	}

	public static void setIn(ObjectInputStream in) {
		Client.in = in;
	}

	public Client(ClientListener listener) {
		this.listener = listener;
	}

	public void startClient(String host, int port) {
		run(host, port);
	}

	void run(String host, int port) {
		try {

			boolean serverIsNotAvailable = false;
			try {
				setRequestSocket(new Socket(host, port));
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "Server is unavailable.");
				serverIsNotAvailable = true;
				System.exit(0);
			}
			if (!serverIsNotAvailable) {
				setOut(new ObjectOutputStream(getRequestSocket()
						.getOutputStream()));
				setIn(new ObjectInputStream(getRequestSocket().getInputStream()));
				ClientLoginFrame frame = new ClientLoginFrame();
				frame.setVisible(true);
				Thread thread = new Thread(getListener());
				thread.start();
				try {
					thread.join();
				} catch (InterruptedException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}

			}

		} catch (UnknownHostException unknownHost) {
			JOptionPane.showMessageDialog(null,
					"You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			logger.log(Level.SEVERE, ioException.getMessage(), ioException);
		} finally {
			try {
				getRequestSocket().close();
				getIn().close();
				getOut().close();
			} catch (IOException ioException) {
				logger.log(Level.SEVERE, ioException.getMessage(), ioException);
			}
		}
	}

	public static void main(final String args[]) {
		Client client = new Client(new ClientListener());
		XmlParser.parseXmlFile();
		client.startClient(XmlParser.getHost(), XmlParser.getPort());
	}

	public static ObjectOutputStream getOut() {
		return out;
	}

	public static void setOut(ObjectOutputStream out) {
		Client.out = out;
	}

}
