package com.cs.logfile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cs.service.LogFileServcie;
import com.cs.service.LogFileServcieImpl;

public class LogFileMainTest {
	
	static final Logger logger = LogManager.getLogger(LogFileMainTest.class);


	public static void main(String[] args) {

		logger.info("Started processing of file in main");
		LogFileServcie logfileService = new LogFileServcieImpl();
		logfileService.processLogFiles(args[0]);
	}

}
