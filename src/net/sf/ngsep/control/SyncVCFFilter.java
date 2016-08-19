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

import net.sf.ngsep.utilities.DefaultProgressNotifier;
import net.sf.ngsep.utilities.LoggingHelper;
import ngsep.vcf.VCFFilter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * 
 * @author Jorge Duitama, Juan Camilo Quintero
 *
 */
public class SyncVCFFilter extends Job {
	
	//Instance of the model class with the optional parameters already set
	private VCFFilter populationVCF;
	
	//Parameters to set just before the execution of the process
	private String inputFile=null;
	private String outputFile=null;
	
	//Attributes to set the logger
	private String logName;
	private FileHandler logFile;
	
	//Name for the progress bar
	private String nameProgressBar;

	/**
	 * Creates a PopulationVCFFilter job with the given name
	 * @param name Name of the job
	 */
	public SyncVCFFilter(String name) {
		super(name);
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		//Create log
		Logger log = LoggingHelper.createLogger(logName, logFile);
		PrintStream out = null;
		try {
			
			//Start progress bar
			monitor.beginTask(getNameProgressBar(), 500);
			log.info("Started Population VCF Filter");
					
			//Create progress notifier and set it to listen to the model class
			populationVCF.setLog(log);
			populationVCF.setProgressNotifier(new DefaultProgressNotifier(monitor));
			if (inputFile != null && outputFile != null) {
				log.info("processing vcf file...");
				out = new PrintStream(outputFile);
				populationVCF.processVariantsFile(inputFile, out);
			}
			log.info("Process finished");
		} catch (Exception e) {
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

	public VCFFilter getPopulationVCF() {
		return populationVCF;
	}

	public void setPopulationVCF(VCFFilter populationVCF) {
		this.populationVCF = populationVCF;
	}

	public FileHandler getLogFile() {
		return logFile;
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

	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
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

}
