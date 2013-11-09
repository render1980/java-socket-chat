package server;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageDBAdapter {
	private ConnectionsPool pool = ConnectionsPool.getInstance();
	private static Logger logger = Logger.getLogger(MessageDBAdapter.class.getName());

	public MessageDBAdapter() {
	}

	public byte insertClient(String login) {
		if (pool.isConnectionFailed()) {
			return -1;
		}

		Connection connection = pool.getConnection();
		String insertClient = "insert into Client(login) values(?)";
		PreparedStatement insertStatement = null;
		byte state = 0;
		try {
			connection.setAutoCommit(false);
			insertStatement = connection.prepareStatement(insertClient);
			insertStatement.setString(1, login);
			insertStatement.executeUpdate();
			connection.commit();
		} catch (SQLIntegrityConstraintViolationException ex) {
			try {
				connection.rollback();
				state = -1;
			} catch (SQLException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			pool.returnConnection(connection);
			try {
				if (insertStatement != null) {
					insertStatement.close();
				}
			} catch (SQLException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return state;
	}

	public byte insertMessage(String fromLogin, String toLogin, String message) {
		if (pool.isConnectionFailed()) {
			return -1;
		}

		int fromId = getClientIdByLogin(fromLogin);
		int toId = getClientIdByLogin(toLogin);

		java.util.Date date = new java.util.Date();
		Date sqlDate = new java.sql.Date(date.getTime());
		String insertMessage = "insert into History(from_, to_, date_, message) values(?, ?, ?, ?)";
		PreparedStatement insertStatement = null;
		Connection connection = pool.getConnection();
		byte state = 0;

		try {
			connection.setAutoCommit(false);
			insertStatement = connection.prepareStatement(insertMessage);
			insertStatement.setInt(1, fromId);
			insertStatement.setInt(2, toId);
			insertStatement.setDate(3, sqlDate);
			insertStatement.setString(4, message);
			insertStatement.executeUpdate();
			connection.commit();
		} catch (SQLException ex) {
			try {
				connection.rollback();
				logger.log(Level.SEVERE, ex.getMessage(), ex);
				state = -1;
			} catch (SQLException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		} finally {
			pool.returnConnection(connection);
			try {
				if (insertStatement != null) {
					insertStatement.close();
				}
			} catch (SQLException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return state;
	}

	private int getClientIdByLogin(String login) {

		int clientId = 0;
		String getClientId = String.format(
				"select client_id from Client where login = '%s'", login);

		Connection connection = pool.getConnection();
		Statement statement = null;
		ResultSet result = null;
		try {
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			result = statement.executeQuery(getClientId);
			if (result.next()) {
				clientId = result.getInt(1);
			}

			connection.commit();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			pool.returnConnection(connection);
			try {
				if (statement != null) {
					statement.close();
				}
				if (result != null) {
					result.close();
				}
			} catch (SQLException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}

		}
		return clientId;
	}

	public String getMessages(String login) {
		if (pool.isConnectionFailed()) {
			return "#hstr>private message service is not available now..";
		}

		int clientId = getClientIdByLogin(login);

		StringBuffer sb = new StringBuffer();
		String getMessages = "select"
				+ "(select c.login from Client c where c.client_id = h.from_),"
				+ "(select c.login from Client c where c.client_id = h.to_),"
				+ "h.date_, h.message " + "from History h "
				+ "where ? in (from_, to_) " + "order by h.date_ desc";

		Connection connection = pool.getConnection();
		PreparedStatement selectStatement = null;
		ResultSet result = null;
		try {
			connection.setAutoCommit(false);
			selectStatement = connection.prepareStatement(getMessages);
			selectStatement.setInt(1, clientId);
			result = selectStatement.executeQuery();
			sb.append("#hstr>");
			while (result.next()) {
				String fromLogin = result.getString(1);
				String toLogin = result.getString(2);
				java.sql.Date date = result.getDate(3);
				String message = result.getString(4);

				sb.append(String.format("%nfrom %s to %s %tD", fromLogin,
						toLogin, date));
				sb.append(String.format("%n-------------%n%s%n-------------%n",
						message));
			}
			connection.commit();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			pool.returnConnection(connection);
			try {
				if (selectStatement != null) {
					selectStatement.close();
				}
				if (result != null) {
					result.close();
				}
			} catch (SQLException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return sb.toString();
	}
}
