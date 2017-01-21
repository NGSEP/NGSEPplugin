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
import ngsep.discovery.BAMRelativeAlleleCountsCalculator;

/**
 * Creation and execution of BAM relative allele counts calculator
 * @author Jorge Duitama
 *
 */
public class SyncRelativeAlleleCountsCalculator extends Job {
	//Instance of the model class with the optional parameters already set
	private BAMRelativeAlleleCountsCalculator instance;
	
	//Parameters to set before the execution of the process
	private String inputFile=null;
	private String outputFile=null;

	//Attributes to set the logger
	private String logName;
	
	//Name for the progress bar
	private String nameProgressBar;
	
	/**
	 * Creates a VCFSummaryStatisticsCalculator job with the given name
	 * @param name Name of the job
	 */
	public SyncRelativeAlleleCountsCalculator(String name) {
		super(name);
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		FileHandler logFile = null;
		PrintStream out = null;
		Logger log = null;
		
		try {
			//Create log
			logFile = new FileHandler(logName, false);
			log = LoggingHelper.createLogger(logName, logFile);
			instance.setLog(log);
			
			//Start progress bar
			monitor.beginTask(nameProgressBar, 50000);
			log.info("Started relative allele counts calculator");
			log.info("Min rd: "+instance.getMinRD()+". Max RD: "+instance.getMaxRD()+". Min BQ: "+ instance.getMinBaseQualityScore());
			//Create progress notifier and set it to listen the model class 
			instance.setProgressNotifier(new DefaultProgressNotifier(monitor));
			if (inputFile != null && outputFile != null) {
				log.info("Processing alignments file");
				instance.runProcess(inputFile);
				out = new PrintStream(outputFile);
				instance.printResults(out);
			}
			log.info("Process finished");
			monitor.done();
		} catch (Exception e) {
			log.info("Error running relative allele counts calculator: ");
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

	public BAMRelativeAlleleCountsCalculator getInstance() {
		return instance;
	}

	public void setInstance(BAMRelativeAlleleCountsCalculator instance) {
		this.instance = instance;
	}

	public String getInputFile() {
		return inputFile;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
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
