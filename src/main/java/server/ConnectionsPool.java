package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import xml.XmlParser;

public final class ConnectionsPool {

	private BlockingQueue<Connection> pool = new LinkedBlockingQueue<Connection>();
	private String host;
	private int port;
	private static final int MINIMUM_CAPACITY = 10;
	private boolean failedConnection;
	private static Logger logger = Logger.getLogger(MessageDBAdapter.class.getName());

	private ConnectionsPool() {
		initPool();
	}

	private static class HelperPool {
		public static final ConnectionsPool HELPER_POOL = new ConnectionsPool();
	}
	
	public static ConnectionsPool getInstance() {
		return HelperPool.HELPER_POOL;
	}

	private void initPool() {
		XmlParser.parseXmlFile();

		this.host = XmlParser.getDBhost();
		this.port = XmlParser.getDBport();
		for (int i = 0; i < MINIMUM_CAPACITY; i++) {
			try {
				pool.add(DriverManager.getConnection("jdbc:derby://" + host
						+ ":" + port + "/ChatHistory"));
			} catch (SQLException e) {
				failedConnection = true;
			}
		}
	}
	
	public boolean isConnectionFailed() {
		return this.failedConnection;
	}

	private void expandPool() {
		for (int i = 0; i < MINIMUM_CAPACITY; i++) {
			try {
				if (pool.size() >= MINIMUM_CAPACITY) {
					return;
				}
				pool.add(DriverManager.getConnection("jdbc:derby://" + host
						+ ":" + port + "/ChatHistory"));

			} catch (SQLException e) {
				failedConnection = true;
			}

		}
	}

	public Connection getConnection() {
		Connection conn = null;
		try {
			if (pool.size() == 0) {
				expandPool();
			}
			conn = pool.take();
		} catch (InterruptedException e) {
			conn = null;
		}
		return conn;
	}

	public void returnConnection(Connection connect) {
		try {
			if(pool.size() > MINIMUM_CAPACITY * 2) {
				try {
					connect.close();
				} catch (SQLException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
				return;
			}
			pool.put(connect);
		} catch (InterruptedException e) {
			logger.warning(Arrays.toString(e.getStackTrace()));
		}
	}

	public void close() {
		for (Connection c : pool) {
			try {
				c.close();
			} catch (SQLException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

}


