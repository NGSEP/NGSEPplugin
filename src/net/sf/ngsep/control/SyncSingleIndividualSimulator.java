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
import ngsep.genome.ReferenceGenome;
import ngsep.simulation.SingleIndividualSimulator;

/**
 * 
 * @author Jorge Duitama
 *
 */
public class SyncSingleIndividualSimulator extends Job {
	

	//Instance of the model class with the optional parameters already set
	private SingleIndividualSimulator instance;
	
	//Parameters to set before the execution of the process
	private String inputFile = null;
	private String outputPrefix = null;

	//Attributes to set the logger
	private String logName;
	
	//Name for the progress bar
	private String nameProgressBar;
	
	/**
	 * Creates a Single Individual Simulator Job with the given name
	 * @param name of the job
	 */
	public SyncSingleIndividualSimulator(String name) {
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
			log.info("Started Single Individual Simulator");
			log.info("Input file: "+inputFile);
			log.info("Output prefix: "+outputPrefix);
			log.info("STRs file: "+instance.getStrsFile());
			log.info("SNV rate: "+instance.getSnvRate());
			log.info("Indel rate: "+instance.getIndelRate());
			log.info("Mutated STR fraction: "+instance.getMutatedSTRFraction());
			log.info("Sample ID: "+instance.getSampleId());
			log.info("Ploidy: "+instance.getPloidy());
			log.info("STR unit column index: "+instance.getStrUnitIndex());
			//Create progress notifier and set it to listen the model class 
			instance.setProgressNotifier(new DefaultProgressNotifier(monitor));
			if (inputFile != null && outputPrefix != null) {
				log.info("Simulating individual");
				instance.setGenome(new ReferenceGenome(inputFile));
				instance.loadSTRs();
				instance.simulateVariants();
				instance.buildAssembly();
				try (PrintStream outGenome = new PrintStream(outputPrefix+".fa")) {
					instance.saveIndividualGenome(outGenome);
				}
				try (PrintStream outVariants = new PrintStream(outputPrefix+".vcf")) {
					instance.saveVariants(outVariants);
				}
			}
			log.info("Process finished");
			monitor.done();
		} catch (Exception e) {
			log.info("Error running Single Individual Simulator: ");
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
	public SingleIndividualSimulator getInstance() {
		return instance;
	}

	/**
	 * @param instance the instance to set
	 */
	public void setInstance(SingleIndividualSimulator instance) {
		this.instance = instance;
	}

	/**
	 * @return the inputFile
	 */
	public String getInputFile() {
		return inputFile;
	}

	/**
	 * @param inputFile the inputFile to set
	 */
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	/**
	 * @return the outputPrefix
	 */
	public String getOutputPrefix() {
		return outputPrefix;
	}

	/**
	 * @param outputPrefix the outputPrefix to set
	 */
	public void setOutputPrefix(String outputPrefix) {
		this.outputPrefix = outputPrefix;
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
