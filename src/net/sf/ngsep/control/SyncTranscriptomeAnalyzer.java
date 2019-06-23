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
import ngsep.transcriptome.TranscriptomeAnalyzer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Jorge Duitama
 */
public class SyncTranscriptomeAnalyzer extends Job {

	private String logName;
	private String nameProgressBar;
	
	private String transcriptomeMap;
	private String outputPrefix;
	
	private TranscriptomeAnalyzer instance;
	
	public SyncTranscriptomeAnalyzer(String name) {
		super(name);
	}

	protected IStatus run(IProgressMonitor monitor) {
		//Create log
		FileHandler logFile = null;
		Logger log = null;
		try {
			logFile = new FileHandler(logName, false);
			log = LoggingHelper.createLogger(logName, logFile);
			instance.setLog(log);
			
			monitor.beginTask(getNameProgressBar(), 100);
			log.info("Transcriptome Analyzer");
			
			instance.setProgressNotifier(new DefaultProgressNotifier(monitor));
			
		
			instance.processTranscriptome(transcriptomeMap, outputPrefix);
			log.info("Process finished");
			monitor.done();
		} catch (Exception e) {
			String message = LoggingHelper.serializeException(e);
			log.severe(message);
		} finally {
			LoggingHelper.closeLogger(log);
		}

		return Status.OK_STATUS;

	}

	public String getTranscriptomeMap() {
		return transcriptomeMap;
	}

	public void setTranscriptomeMap(String transcriptomeMap) {
		this.transcriptomeMap = transcriptomeMap;
	}

	public String getOutputPrefix() {
		return outputPrefix;
	}

	public void setOutputPrefix(String outputPrefix) {
		this.outputPrefix = outputPrefix;
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

	public TranscriptomeAnalyzer getInstance() {
		return instance;
	}

	public void setInstance(TranscriptomeAnalyzer instance) {
		this.instance = instance;
	}
}
