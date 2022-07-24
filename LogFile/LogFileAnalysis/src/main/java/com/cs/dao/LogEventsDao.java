package com.cs.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cs.dto.LogEventsDO;

public class LogEventsDao {

	static final Logger logger = LogManager.getLogger(LogEventsDao.class);

	public boolean checkIfTableExist(Connection c) {
		try {
			PreparedStatement stmt = c.prepareStatement("SELECT 1 FROM LOG_EVENTS");
			stmt.execute();

		} catch (SQLException sqlEx) {
			logger.error("LOG_EVENTS table does not exist in db");
			return false;
		}
		return true;
	}

	public boolean createTable(Connection conn) {
		logger.info("Started creating table");
		String createLogEventQry = "CREATE TABLE LOG_EVENTS (ID VARCHAR(20) NOT NULL, DURATION INT NOT NULL, TYPE VARCHAR(50), HOST VARCHAR(50) )";
		try {
			PreparedStatement stmt = conn.prepareStatement(createLogEventQry);
			stmt.execute();

		} catch (SQLException ex) {
			logger.error("Error in creating LOG_EVENTS table : "+ex.getMessage());
		}
		logger.info("Successfully created LOG_EVENTS table");
		return true;
	}

	public int[] insertLogEvents(List<LogEventsDO> logEvents, Connection conn) {
		logger.info("Started inserting log events in table");

		String inserLogEvents = "INSERT INTO LOG_EVENTS (ID, DURATION, TYPE, HOST) VALUES (?,?,?,?)";
		int[] recordsUpdated = null;
		try {
			PreparedStatement insertStmt = conn.prepareStatement(inserLogEvents);
			for (LogEventsDO logEventsDO : logEvents) {
				insertStmt.setString(1, logEventsDO.getId());
				insertStmt.setInt(2, logEventsDO.getDuration());
				insertStmt.setString(3, logEventsDO.getType());
				insertStmt.setString(4, logEventsDO.getHost());

				insertStmt.addBatch();
			}
			recordsUpdated = insertStmt.executeBatch();
		} catch (SQLException sqlEx) {
			logger.error("Error in inserting data in LOG_EVENTS table : "+logEvents.toString()+" "+sqlEx.getMessage());
		}
		logger.info("Successfully inserted records in  table : "+recordsUpdated.length);
		return recordsUpdated;
	}

}
