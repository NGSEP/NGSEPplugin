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

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import net.sf.ngsep.utilities.DefaultProgressNotifier;
import net.sf.ngsep.utilities.LoggingHelper;
import ngsep.sequences.QualifiedSequenceList;
import ngsep.vcf.ConsistentVCFFilesMerge;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * 
 * @author Jorge Duitama, Juan Camilo Quintero
 *
 */
public class SyncMergeVCF extends Job {
	
	private String outputFile;
	private List<String> listFiles = new ArrayList<String>();
	private QualifiedSequenceList sequenceNames;
	private String logName;
	
	private String nameProgressBar;

	public SyncMergeVCF(String name) {
		super(name);
	}
	
	protected IStatus run(IProgressMonitor monitor) {
		FileHandler logFile = null;
		PrintStream out = null;
		Logger log = null;
		try {
			logFile = new FileHandler(logName, false);
			log = LoggingHelper.createLogger(logName, logFile);
			monitor.beginTask(getNameProgressBar(), 500);
			log.info("Started Merge VCF");
			out = new PrintStream(outputFile);
			log.info("Merging files");
			ConsistentVCFFilesMerge merge = new ConsistentVCFFilesMerge();
			merge.setLog(log);
			merge.setProgressNotifier(new DefaultProgressNotifier(monitor));
			merge.mergeFiles(sequenceNames, listFiles, out);
			log.info("Process finished");
		} catch (IOException e) {
			log.info("Error executing merge vcf: ");
			String message = LoggingHelper.serializeException(e);
			log.severe(message);
			return new Status(IStatus.ERROR, "NGSEP Merge" , "Error while executing Merge VCF population");
		} finally {
			LoggingHelper.closeLogger(log);
			if (out != null) {
				out.flush();
				out.close();
			}
		}
		return Status.OK_STATUS;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public List<String> getListFiles() {
		return listFiles;
	}

	public void setListFiles(List<String> listFiles) {
		this.listFiles = listFiles;
	}
	
	public QualifiedSequenceList getSequenceNames() {
		return sequenceNames;
	}

	public void setSequenceNames(QualifiedSequenceList sequenceNames) {
		this.sequenceNames = sequenceNames;
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
