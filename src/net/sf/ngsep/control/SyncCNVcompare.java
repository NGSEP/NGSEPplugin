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

import java.util.logging.FileHandler;
import java.util.logging.Logger;

import net.sf.ngsep.utilities.DefaultProgressNotifier;
import net.sf.ngsep.utilities.LoggingHelper;
import ngsep.discovery.rd.CNVseqAlgorithm;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Creation and execution of the job CNV Compare, an implementation of the CNV-seq algorithm (Xie and Tammi, 2009) 
 * @author Juan Fernando De la Hoz, Jorge Duitama
 *
 */
public class SyncCNVcompare extends Job{
	//Instance of the model class with the optional parameters already set
	private CNVseqAlgorithm CNVcompare;

	//Attributes to set the logger
	private String logName;
	private FileHandler logFile;
	
	//Name for the progress bar
	private String nameProgressBar;

	/**
	 * Creates a VCFSummaryStatisticsCalculator job with the given name
	 * @param name Name of the job
	 */
	public SyncCNVcompare(String name) {
		super(name);
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		//Create log
		Logger log = LoggingHelper.createLogger(logName, logFile);
		
		try {
			//Start progress bar
			monitor.beginTask(nameProgressBar, 12);
			log.info("Started Read Depth Comparator");
			
			//Create progress notifier and set it to listen the model class 
			CNVcompare.setProgressNotifier(new DefaultProgressNotifier(monitor));

			if (CNVcompare.getBamXfile()!=null && CNVcompare.getBamYfile()!= null && CNVcompare.getReference()!=null && CNVcompare.getOutFile()!=null) {
				log.info("processing input files.");
				CNVcompare.loadFiles(log);
				CNVcompare.runCNVseq(log);
			}
			log.info("Process finished.");
		} catch (Exception e) {
			log.info("Error Comparing Read Depth in Samples: ");
			String message = LoggingHelper.serializeException(e);
			log.severe(message);
		} finally {
			LoggingHelper.closeLogger(log);
		}
		return Status.OK_STATUS;
	}
	
	//Setters and getters
	public String getLogName() {
		return logName;
	}	public FileHandler getLogFile() {
		return logFile;
	}	public void setLogFile(FileHandler logFile) {
		this.logFile = logFile;
	}	public void setLogName(String logName) {
		this.logName = logName;
	}	public String getNameProgressBar() {
		return nameProgressBar;
	}	public void setNameProgressBar(String nameProgressBar) {
		this.nameProgressBar = nameProgressBar;
	}	public CNVseqAlgorithm getCNVcompare() {
		return CNVcompare;
	}	public void setCNVcompare(CNVseqAlgorithm cNVcompare) {
		CNVcompare = cNVcompare;
	}
}
