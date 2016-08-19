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
 * Execution organizer for the GBS Cornell Deconvolution class
 * @author Juan Fernando De la Hoz
 */
public class SyncDeconvolution {

	private ReadsDemultiplex deconvoluter;
	
	// variables for execution
	private String fastqFile;
	private String fastqFile2;
	private String indexFile;
	private String laneDescriptorFile;
	private String outputDir;
	private String flowCell;
	private String lane;
	private String trimSequence = null;
	private boolean uncompressOutput;
	
	// Process followers
	private String logName;
	private FileHandler logFile;
	private String nameProgressBar;
	
	private final Job job = new Job("Samples Deconvolution") {
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			Logger log = LoggingHelper.createLogger(logName, logFile);
			
			try {
				monitor.beginTask(nameProgressBar, 30000);
				log.info("Started Sample Deconvolution");
				deconvoluter = new ReadsDemultiplex();
				deconvoluter.setProgressNotifier(new DefaultProgressNotifier(monitor));
				deconvoluter.setLog(log);
				deconvoluter.setUncompressedOutput(uncompressOutput);
				if(trimSequence!=null) deconvoluter.setTrimSequence(trimSequence);
				
				if (indexFile!=null && outputDir!=null) {	
					deconvoluter.setOutDirectory(outputDir);
					log.info("Loading Index.");
					deconvoluter.loadIndex(indexFile);
					if(laneDescriptorFile!=null){
						log.info("Loading Lane Descriptor File.");
						log.info("processing FASTQ file(s).");
						deconvoluter.demultiplexGroup(laneDescriptorFile);
					} else if(fastqFile!=null && flowCell!=null && lane!=null){
						log.info("Loading Lane Info.");
						deconvoluter.loadLaneInfo(flowCell, lane);
						if(fastqFile2!=null){
							log.info("processing FASTQ files.");
							deconvoluter.demultiplex(fastqFile, fastqFile2);
						} else {
							log.info("processing FASTQ file.");
							deconvoluter.demultiplex(fastqFile);
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
	};
	
	public void runJob() {
		job.schedule();
	}
	public void setFastqFile(String fastqFile) {
		this.fastqFile = fastqFile;
	}
	public void setFastqFile2(String fastqFile2) {
		this.fastqFile2 = fastqFile2;
	}
	public void setIndexFile(String indexFile) {
		this.indexFile = indexFile;
	}
	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}
	public void setLaneDescriptorFile(String laneDescriptorFile) {
		this.laneDescriptorFile = laneDescriptorFile;
	}
	public void setFlowCell(String flowCell) {
		this.flowCell = flowCell;
	}
	public void setLane(String lane) {
		this.lane = lane;
	}
	public void setTrimSequence(String trimSequence) {
		this.trimSequence = trimSequence;
	}
	public void setUncompressOutput(Boolean uncompressOutput) {
		this.uncompressOutput = uncompressOutput;
	}
	public void setLogName(String logName) {
		this.logName = logName;
	}
	public void setLogFile(FileHandler logFile) {
		this.logFile = logFile;
	}
	public void setNameProgressBar(String nameProgressBar) {
		this.nameProgressBar = nameProgressBar;
	}

}
