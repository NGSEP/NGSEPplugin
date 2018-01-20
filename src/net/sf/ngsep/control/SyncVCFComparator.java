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
import ngsep.vcf.VCFComparator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * 
 * @author Jorge Duitama, Juan Camilo Quintero
 *
 */
public class SyncVCFComparator extends Job {
	
	//Instance of the model class with the optional parameters already set
	private VCFComparator instance;
	
	//Parameters to set just before the execution of the process
	private String inputFile1=null;
	private String inputFile2=null;
	
	private String outputFile=null;

	
	//Attributes to set the logger
	private String logName;
	
	//Name for the progress bar
	private String nameProgressBar;

	public SyncVCFComparator(String name) {
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
			log.info("Started VCF Comparison");
					
			//Create progress notifier and set it to listen to the model class 
			instance.setProgressNotifier(new DefaultProgressNotifier(monitor));
			if (inputFile1 != null && inputFile2!=null && outputFile != null) {
				log.info("processing vcf file...");
				out = new PrintStream(outputFile);
				instance.calculateDifferences(inputFile1, inputFile2);
				instance.printReport(out);
			}
			log.info("Process finished");
		} catch (Exception e) {
			String message = LoggingHelper.serializeException(e);
			if(log!=null)log.severe(message);
		} finally {
			LoggingHelper.closeLogger(log);
			if (out != null) {
				out.flush();
				out.close();
			}
		}
		return Status.OK_STATUS;
	}

	
	public VCFComparator getInstance() {
		return instance;
	}

	public void setInstance(VCFComparator instance) {
		this.instance = instance;
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

	public String getInputFile1() {
		return inputFile1;
	}

	public void setInputFile1(String inputFile1) {
		this.inputFile1 = inputFile1;
	}

	public String getInputFile2() {
		return inputFile2;
	}

	public void setInputFile2(String inputFile2) {
		this.inputFile2 = inputFile2;
	}


	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
}
