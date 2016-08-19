/*******************************************************************************
 * NGSEP - Next Generation Sequencing Experience Platform
 * Copyright 2016 Jorge Duitama
 *
 * This file is part of NGSEP.
 *
 *     NGSEP is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     NGSEP is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with NGSEP.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.sf.ngsep.utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * 
 * @author Jorge Duitama
 *
 */
public class LoggingHelper {
	
	public static final String MESSAGE_PROGRESS_NOBAR = "Please refresh the log in the output folder to keep track of this process";
	public static final String MESSAGE_PROGRESS_BAR = "Please check the progress bar to keep track of this process";
	//Output file has the absolute path to the file that will be generated with the process
	public static String getLoggerFilename(String outputFile, String suffix) {
		String loggerName = outputFile;
		if (loggerName.contains("."))
			loggerName = loggerName.substring(0,loggerName.lastIndexOf("."));
		return loggerName+"_"+suffix+".log";
	}
	
	// This method is used to catch the exception thrown by a catch when an
	// error occurs and display process log
	public static String serializeException(Exception e) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		e.printStackTrace(printWriter);
		String srtMessage = writer.toString();
		return srtMessage;
	}
	
	public static Logger createLogger(String name, FileHandler file) {
		Logger log = Logger.getLogger(name);
		log.setLevel(Level.ALL);
		SimpleFormatter formatter = new SimpleFormatter();
		file.setFormatter(formatter);
		log.addHandler(file);
		return log;
	}

	public static void closeLogger(Logger log) {
		List<Handler> hs = Arrays.asList(log.getHandlers());
		for (Handler h : hs) {
			h.flush();
			h.close();
			log.removeHandler(h);
		}
	}
		
}
