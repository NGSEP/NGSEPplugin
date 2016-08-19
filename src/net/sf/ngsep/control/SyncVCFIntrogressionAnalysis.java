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

import java.io.PrintStream;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import net.sf.ngsep.utilities.DefaultProgressNotifier;
import net.sf.ngsep.utilities.LoggingHelper;
import ngsep.vcf.VCFWindowIntrogressionAnalysis;

/**
 * Creation and execution of VCF Introgression Analysis process
 * @author Jorge Duitama
 *
 */
public class SyncVCFIntrogressionAnalysis extends Job {
	
	//Instance of the model class with the optional parameters already set
	private VCFWindowIntrogressionAnalysis instance;
	
	//Parameters to set before the execution of the process
	private String inputFile=null;

	//Attributes to set the logger
	private String logName;
	
	//Name for the progress bar
	private String nameProgressBar;

	/**
	 * Creates a VCFSummaryStatisticsCalculator job with the given name
	 * @param name Name of the job
	 */
	public SyncVCFIntrogressionAnalysis(String name) {
		super(name);
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		//Create log
		FileHandler logFile = null;
		PrintStream out = null;
		Logger log = null;
		try {
			logFile = new FileHandler(logName, false);
			log = LoggingHelper.createLogger(logName, logFile);
			//Start progress bar
			monitor.beginTask(getNameProgressBar(), 1000);
			log.info("Started VCF introgression analysis");
			
			instance.setLog(log);
			instance.setProgressNotifier(new DefaultProgressNotifier(monitor));
			if (inputFile != null) {
				instance.runIntrogressions(inputFile);
			}
			log.info("Process finished");
			monitor.done();
		} catch (Exception e) {
			log.info("Error Calculating VCF Summary Statistics: ");
			String message = LoggingHelper.serializeException(e);
			log.severe(message);
		} finally {
			LoggingHelper.closeLogger(log);
			if (out != null) {
				out.flush();
				out.close();
			}
		}
		return Status.OK_STATUS;
	}
	
	//Setters and getters

	public String getInputFile() {
		return inputFile;
	}
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}
	
	
	
	public VCFWindowIntrogressionAnalysis getInstance() {
		return instance;
	}

	public void setInstance(VCFWindowIntrogressionAnalysis instance) {
		this.instance = instance;
	}

	public String getLogName() {
		return logName;
	}
	public void setLogName(String logName) {
		this.logName = logName;
	}
	public String getNameProgressBar() {
		return nameProgressBar;
	}
	public void setNameProgressBar(String nameProgressBar) {
		this.nameProgressBar = nameProgressBar;
	}
}
