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

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import net.sf.ngsep.utilities.DefaultProgressNotifier;
import net.sf.ngsep.utilities.LoggingHelper;
import ngsep.discovery.VariantsDetector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * 
 * @author Jorge Duitama, Juan Camilo Quintero
 *
 */
public class SyncDetector extends Job {

	public SyncDetector(String name) {
		super(name);
	}
	private VariantsDetector vd;
	
	private String logName;
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		FileHandler logFile;
		try {
			logFile = new FileHandler(logName, false);
		} catch (IOException e) {
			System.err.println("Could not create log file");
			e.printStackTrace();
			return Status.CANCEL_STATUS;
		}
		Logger log = LoggingHelper.createLogger(logName, logFile);
		try {
			monitor.beginTask(vd.getSampleId(), 100);
			vd.setProgressNotifier(new DefaultProgressNotifier(monitor));
			vd.setLog(log);
			log.info("Output vars filename: "+vd.getOutVarsFilename());
			vd.processAll();
			monitor.done();
			
		} catch (Exception e) {
			String message = LoggingHelper.serializeException(e);
			log.severe(message);
			return new Status(IStatus.ERROR, vd.getSampleId(), "Error while executing variants detector "+vd.getSampleId(),e);
		} finally {
			LoggingHelper.closeLogger(log);
		}
		return Status.OK_STATUS;
		
	}

	public VariantsDetector getVd() {
		return vd;
	}

	public void setVd(VariantsDetector vd) {
		this.vd = vd;
	}

	/**
	 * @return the logName
	 */
	public String getLogName() {
		return logName;
	}

	/**
	 * @param logName the logName to set
	 */
	public void setLogName(String logName) {
		this.logName = logName;
	}

	
}
