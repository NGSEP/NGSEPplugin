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
package net.sf.ngsep.control;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import net.sf.ngsep.utilities.LoggingHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * 
 * @author Juan Camilo Quintero
 *
 */
public class SyncIndexBowtie {
	private List<String> commandArray;
	private String logName;
	private FileHandler logFile;
	private String nameProgressBar;

	private final Job job = new Job("Create Index Bowtie Process") {
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			Logger log = LoggingHelper.createLogger(logName, logFile);
			try {
				monitor.beginTask(getNameProgressBar(), 500);
				log.info("Started Create Index Bowtie");
				Process p = Runtime.getRuntime().exec(commandArray.toArray(new String[0]));
				InputStream stdoutStream = p.getInputStream();
				StringBuffer buffer = new StringBuffer();
				while (true) {
					int message = stdoutStream.read();
					if (message == -1) break;
					monitor.worked(1);
					// End process if user requests so
					if (monitor.isCanceled()) {
						p.destroy();
						log.info("Process canceled by user");
						return Status.CANCEL_STATUS;
					}
					buffer.append((char) message);
				}
				if(buffer.length()>0) log.info(buffer.toString());
				stdoutStream.close();
				InputStream stderrStream = p.getErrorStream();
				StringBuffer bufferOne = new StringBuffer();
				while (true) {
					int failure = stderrStream.read();
					if (failure == -1)
						break;
					bufferOne.append((char) failure);
				}
				if (bufferOne.length() > 0) log.severe(bufferOne.toString());
				stderrStream.close();
				log.info("Process finished");
				monitor.done();
			} catch (Exception e) {
				log.info("Error executing Create Index Bowtie: ");
				String message = LoggingHelper.serializeException(e);
				log.severe(message);
			} finally {
				LoggingHelper.closeLogger(log);
			}
			return Status.OK_STATUS;
		}
	};

	public void runJob() {
		job.schedule();
	}

	

	public String getLogName() {
		return logName;
	}

	public FileHandler getLogFile() {
		return logFile;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

	public void setLogFile(FileHandler logFile) {
		this.logFile = logFile;
	}

	public String getNameProgressBar() {
		return nameProgressBar;
	}

	public void setNameProgressBar(String nameProgressBar) {
		this.nameProgressBar = nameProgressBar;
	}



	public void setCommandArray(List<String> commandArray) {
		this.commandArray = new ArrayList<String>(commandArray);
	}

}
