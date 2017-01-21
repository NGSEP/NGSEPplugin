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
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import net.sf.ngsep.utilities.DefaultProgressNotifier;
import net.sf.ngsep.utilities.LoggingHelper;
import ngsep.transcriptome.Transcriptome;
import ngsep.vcf.AlleleSharingStatsCalculator;

/**
 * 
 * @author Jorge Duitama
 *
 */
public class SyncAlleleSharingStatistics extends Job {
	
	private AlleleSharingStatsCalculator calculator;
	private String vcfFile;
	private String populationFile;
	private String outFile;
	private String [] groups1;
	private String [] groups2;
	
	//Attributes to set the logger
	private String logName;
	
	//Name for the progress bar
	private String nameProgressBar;
	
	public SyncAlleleSharingStatistics(String name) {
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
			log.info("Started Allele sharing statistics");
			Transcriptome genes = calculator.getTranscriptome();
			if(genes==null) log.info("Window size: "+calculator.getWindowSize()+" step size: "+calculator.getStepSize());
			else log.info("Chromosomes: "+genes.getSequenceNames().size());
			//Create progress notifier and set it to listen to the model class
			calculator.setLog(log);
			calculator.setProgressNotifier(new DefaultProgressNotifier(monitor));
			if (vcfFile != null && populationFile!=null && outFile != null && groups1 != null && groups2!= null) {
				log.info("processing vcf file...");
				out = new PrintStream(outFile);
				calculator.loadSamplesDatabase(populationFile, groups1, groups2);
				Map<String,List<Double>> stats = calculator.calculateSharingStatistics(vcfFile);
				calculator.printSharingStats(stats, out);
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
	
	
	public AlleleSharingStatsCalculator getCalculator() {
		return calculator;
	}
	public void setCalculator(AlleleSharingStatsCalculator calculator) {
		this.calculator = calculator;
	}
	public String getVcfFile() {
		return vcfFile;
	}
	public void setVcfFile(String vcfFile) {
		this.vcfFile = vcfFile;
	}
	public String getPopulationFile() {
		return populationFile;
	}
	public void setPopulationFile(String populationFile) {
		this.populationFile = populationFile;
	}
	public String getOutFile() {
		return outFile;
	}
	public void setOutFile(String outFile) {
		this.outFile = outFile;
	}
	public String[] getGroups1() {
		return groups1;
	}
	public void setGroups1(String[] groups1) {
		this.groups1 = groups1;
	}
	public String[] getGroups2() {
		return groups2;
	}
	public void setGroups2(String[] groups2) {
		this.groups2 = groups2;
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
