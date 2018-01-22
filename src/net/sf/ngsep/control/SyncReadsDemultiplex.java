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
import ngsep.sequences.ReadsDemultiplex;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Execution job for the Demultiplex function
 * @author Juan Fernando De la Hoz
 */
public class SyncReadsDemultiplex extends Job {

	private ReadsDemultiplex instance;
	
	// variables that are not attributes of ReadsDemultiplex
	private String fastqFile1;
	private String fastqFile2;
	private String indexFile;
	
	// Process followers
	private String logName;
	private String nameProgressBar;
	
	/**
	 * Creates a ReadsDemultiplex job with the given name
	 * @param name Name of the job
	 */
	public SyncReadsDemultiplex(String name) {
		super(name);
	}
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		FileHandler logFile = null;
		Logger log = null;
		try {
			//Create log
			logFile = new FileHandler(logName, false);
			log = LoggingHelper.createLogger(logName, logFile);
			instance.setLog(log);
			
			//Start progress bar
			monitor.beginTask(nameProgressBar, 30000);
			log.info("Started reads demultiplexing");
			instance.setProgressNotifier(new DefaultProgressNotifier(monitor));
			
			if (indexFile!=null) {	
				log.info("Loading Index.");
				instance.loadIndex(indexFile);
				String flowCell = instance.getFlowcell();
				String lane = instance.getLane();
				if(instance.getLaneFilesDescriptor()!=null){
					log.info("Demultiplexing fastq files described in lanes file: "+instance.getLaneFilesDescriptor());
					instance.demultiplexGroup();
				} else if(fastqFile1!=null && flowCell!=null && lane!=null){
					log.info("Loading Lane Info.");
					instance.loadLaneInfo(flowCell, lane);
					if(fastqFile2!=null){
						log.info("processing FASTQ files.");
						instance.demultiplex(fastqFile1, fastqFile2);
					} else {
						log.info("processing FASTQ file.");
						instance.demultiplex(fastqFile1);
					}
				}		
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
	public void setFastqFile1(String fastqFile) {
		this.fastqFile1 = fastqFile;
	}
	public void setFastqFile2(String fastqFile2) {
		this.fastqFile2 = fastqFile2;
	}
	public void setIndexFile(String indexFile) {
		this.indexFile = indexFile;
	}
	public void setLogName(String logName) {
		this.logName = logName;
	}
	public void setNameProgressBar(String nameProgressBar) {
		this.nameProgressBar = nameProgressBar;
	}
	public void setInstance(ReadsDemultiplex instance) {
		this.instance = instance;
	}

}
