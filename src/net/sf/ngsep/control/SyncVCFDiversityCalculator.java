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

import net.sf.ngsep.utilities.LoggingHelper;
import ngsep.vcf.VCFDiversityCalculator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * 
 * @author Jorge Duitama
 *
 */
public class SyncVCFDiversityCalculator extends Job {
	
	//Instance of the model class with the optional parameters already set
	private VCFDiversityCalculator instance;


	//Parameters to set just before the execution of the process
	private String vcfFile;
	private String samplesFile;
	private String outputFile;

	//Attributes to set the logger
	private String logName;

	//Name for the progress bar
	private String nameProgressBar;


	/**
	 * Creates a VCFDiversityCalculator job with the given name
	 * @param name Name of the job
	 */
	public SyncVCFDiversityCalculator(String name) {
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
			monitor.beginTask(getNameProgressBar(), 500);
			log.info("Starting VCF Diversity Calculator");

			//TODO: Create progress notifier and set it to listen to the model class 
			//instance.setProgressNotifier(new DefaultProgressNotifier(monitor));
			if (vcfFile != null && outputFile != null) {

				if(samplesFile!=null) {
					log.info("Reading populations file...");
					instance.loadSamplesFile(samplesFile);
				}
				
				log.info("Processing VCF file...");
				out = new PrintStream(outputFile);
				instance.processFile(vcfFile, out);
				log.info("Process finished");		
			}
			
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

	
	public VCFDiversityCalculator getInstance() {
		return instance;
	}

	public void setInstance(VCFDiversityCalculator instance) {
		this.instance = instance;
	}

	public String getVcfFile() {
		return vcfFile;
	}

	public void setVcfFile(String vcfFile) {
		this.vcfFile = vcfFile;
	}

	public String getSamplesFile() {
		return samplesFile;
	}

	public void setSamplesFile(String samplesFile) {
		this.samplesFile = samplesFile;
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

