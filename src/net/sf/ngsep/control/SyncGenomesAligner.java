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
import ngsep.genome.GenomesAligner;

/**
 * Job for genomes aligner
 * @author Jorge Duitama
 */
public class SyncGenomesAligner extends Job {

	private GenomesAligner instance;
	
	//Attributes to set the logger
	private String logName;
		
	//Name for the progress bar
	private String nameProgressBar;
	
	private String genome1File;
	private String transcriptome1File;
	private String genome2File;
	private String transcriptome2File;
	
	public SyncGenomesAligner(String name) {
		super(name);
		// TODO Auto-generated constructor stub
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
			log.info("Started neighbor joining calculation");
			
			instance.setLog(log);
			instance.setProgressNotifier(new DefaultProgressNotifier(monitor));
			instance.loadGenome(genome1File, transcriptome1File);
			instance.loadGenome(genome2File, transcriptome2File);
			instance.alignGenomes();
			instance.printAlignmentResults();
			log.info("Process finished");
			monitor.done();
		} catch (Exception e) {
			log.info("Error Calculating neighbor joining: ");
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
	public GenomesAligner getInstance() {
		return instance;
	}

	/**
	 * @param instance the instance to set
	 */
	public void setInstance(GenomesAligner instance) {
		this.instance = instance;
	}
	
	/**
	 * @return the genome1File
	 */
	public String getGenome1File() {
		return genome1File;
	}

	/**
	 * @param genome1File the genome1File to set
	 */
	public void setGenome1File(String genome1File) {
		this.genome1File = genome1File;
	}

	/**
	 * @return the transcriptome1File
	 */
	public String getTranscriptome1File() {
		return transcriptome1File;
	}

	/**
	 * @param transcriptome1File the transcriptome1File to set
	 */
	public void setTranscriptome1File(String transcriptome1File) {
		this.transcriptome1File = transcriptome1File;
	}

	/**
	 * @return the genome2File
	 */
	public String getGenome2File() {
		return genome2File;
	}

	/**
	 * @param genome2File the genome2File to set
	 */
	public void setGenome2File(String genome2File) {
		this.genome2File = genome2File;
	}

	/**
	 * @return the transcriptome2File
	 */
	public String getTranscriptome2File() {
		return transcriptome2File;
	}

	/**
	 * @param transcriptome2File the transcriptome2File to set
	 */
	public void setTranscriptome2File(String transcriptome2File) {
		this.transcriptome2File = transcriptome2File;
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
