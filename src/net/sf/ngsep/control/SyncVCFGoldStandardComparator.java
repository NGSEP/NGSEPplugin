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
import ngsep.benchmark.VCFGoldStandardComparator;

/**
 * Job for VCF gold standard comparator
 * @author Jorge Duitama
 */
public class SyncVCFGoldStandardComparator extends Job {

	private VCFGoldStandardComparator instance;
	
	//Attributes to set the logger
	private String logName;
		
	//Name for the progress bar
	private String nameProgressBar;
	
	private String goldStandardFile;
	private String testFile;
	private String outputFile;
	
	public SyncVCFGoldStandardComparator(String name) {
		super(name);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		//Create log
		FileHandler logFile = null;
		PrintStream out = null;
		Logger log = null;
		try {
			out = new PrintStream(outputFile);
			logFile = new FileHandler(logName, false);
			log = LoggingHelper.createLogger(logName, logFile);
			//Start progress bar
			monitor.beginTask(getNameProgressBar(), 1000);
			log.info("Started VCF gold standard comparator");
			
			instance.setLog(log);
			instance.setProgressNotifier(new DefaultProgressNotifier(monitor));
			
			
			instance.compareFiles(goldStandardFile,testFile);
			instance.printStatistics (out);
			log.info("Process finished");
			monitor.done();
		} catch (Exception e) {
			log.info("Error comparing VCF files: ");
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

	/**
	 * @return the instance
	 */
	public VCFGoldStandardComparator getInstance() {
		return instance;
	}

	/**
	 * @param instance the instance to set
	 */
	public void setInstance(VCFGoldStandardComparator instance) {
		this.instance = instance;
	}

	/**
	 * @return the goldStandardFile
	 */
	public String getGoldStandardFile() {
		return goldStandardFile;
	}

	/**
	 * @param goldStandardFile the goldStandardFile to set
	 */
	public void setGoldStandardFile(String goldStandardFile) {
		this.goldStandardFile = goldStandardFile;
	}

	/**
	 * @return the testFile
	 */
	public String getTestFile() {
		return testFile;
	}

	/**
	 * @param testFile the testFile to set
	 */
	public void setTestFile(String testFile) {
		this.testFile = testFile;
	}

	/**
	 * @return the outputFile
	 */
	public String getOutputFile() {
		return outputFile;
	}

	/**
	 * @param outputFile the outputFile to set
	 */
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
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

	/**
	 * @return the nameProgressBar
	 */
	public String getNameProgressBar() {
		return nameProgressBar;
	}

	/**
	 * @param nameProgressBar the nameProgressBar to set
	 */
	public void setNameProgressBar(String nameProgressBar) {
		this.nameProgressBar = nameProgressBar;
	}

	
}
