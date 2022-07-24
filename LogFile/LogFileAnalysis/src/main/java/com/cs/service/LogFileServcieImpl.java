package com.cs.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cs.dao.LogEventsDao;
import com.cs.dto.LogEventsDO;
import com.cs.dto.LogFileDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LogFileServcieImpl implements LogFileServcie {

	static final Logger logger = LogManager.getLogger(LogFileServcieImpl.class);

	@Override
	public boolean processLogFiles(String filePath) {
		logger.info("Starting processing of file ");
		Map<String, LogFileDTO> logFileMap = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			Class.forName("org.hsqldb.jdbcDriver");
			Connection c = DriverManager.getConnection(
					"jdbc:hsqldb:hsql://localhost/logfileanalysis/logfileanalysis;ifexists=true", "SA", "password");
			logger.info("Connection to database successful ");
			LogEventsDao logDao = new LogEventsDao();
			boolean tableExist = logDao.checkIfTableExist(c);
			if (!tableExist) {
				logger.debug("Log_Events Table does not exist in db");
				logDao.createTable(c);
			}

			List<LogEventsDO> logEventsList = new ArrayList<>(11);
			for (String line; (line = br.readLine()) != null;) {
				ObjectMapper mapper = new ObjectMapper();
				LogFileDTO logFileDto = mapper.readValue(line, LogFileDTO.class);
				String logId = logFileDto.getId();
				if (logFileMap.containsKey(logId)) {
					LogFileDTO existingLogDto = logFileMap.get(logId);

					if (!existingLogDto.getState().equalsIgnoreCase(logFileDto.getState())) {
						Long diff = existingLogDto.getTimestamp() - logFileDto.getTimestamp();

						diff = diff < 0 ? diff * (-1) : diff;
						if (diff > 4) {
							LogEventsDO logEventsDo = new LogEventsDO();
							logEventsDo.setId(logId);
							logEventsDo.setDuration(diff.intValue());
							logEventsDo.setType(existingLogDto.getType());
							logEventsDo.setHost(existingLogDto.getHost());

							logEventsList.add(logEventsDo);
							if (logEventsList.size() == 2) {
								int[] insertedrows = logDao.insertLogEvents(logEventsList, c);
								System.out.println(insertedrows);
								logEventsList.clear();
							}
							logFileMap.remove(logId);
						}
					}

				} else {
					logFileMap.put(logId, logFileDto);
				}
			}
		} catch (FileNotFoundException ex) {
			logger.error("File not found at path : " + filePath + " : " + ex.getMessage());
			return false;
		} catch (IOException ioEx) {
			logger.error("Error in reading file at paht : " + filePath + " : " + ioEx.getMessage());
			return false;
		} catch (ClassNotFoundException clsEx) {
			logger.error("Error in loading database drivers :" + clsEx.getMessage());
			return false;
		} catch (SQLException sqlEx) {
			logger.error("Error in connecting to database :" + sqlEx.getMessage());
			return false;
		}
		logger.info("File processing completed ");
		return true;
	}

}
