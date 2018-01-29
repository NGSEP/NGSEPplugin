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

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import net.sf.picard.sam.SortSam;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * 
 * @author Juan Camilo Quintero
 * @author Jorge Duitama
 *
 */
public class SyncSortAlignment extends Job {
	
	private String inputFile;
	private String outputFile;
	
	//Attributes to set the logger
	private String logName;
	
	//Name for the progress bar
	private String nameProgressBar;
	
	public SyncSortAlignment(String name) {
		super(name);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		FileHandler logFile;
		try {
			logFile = new FileHandler(logName, false);
		} catch (IOException e) {
			System.err.println("Could not create log file");
			e.printStackTrace();
			return Status.CANCEL_STATUS;
		}
		Logger log = LoggingHelper.createLogger(logName, logFile);
		try {
			log.info("Started Sort Sam");
			String tmpDirName = SpecialFieldsHelper.buildSuggestedOutputPrefix(nameProgressBar);
			sortAlignments(tmpDirName, inputFile,outputFile,log);
			log.info("Process finished");
		} catch (Exception e) {
			String message = LoggingHelper.serializeException(e);
			log.severe(message);
		} finally {
			LoggingHelper.closeLogger(log);
		}
		return Status.OK_STATUS;

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

	public static void sortAlignments(String tmpDirName, String inFile, String outFile, Logger log) throws IOException {
		String [] args = new String [5];
		File f = new File (outFile);
		File sortDirectory = new File(f.getParentFile().getAbsolutePath() + File.separator+tmpDirName+"_tmpDir");
		if (!sortDirectory.exists()) {
			if (!sortDirectory.mkdirs()) {
				throw new IOException("Could not create temporary directory for sorting");
			} else {
				log.info("Temporary directory created");
			}
		}
		args[0] = "SORT_ORDER=coordinate";
		args[1] = "TMP_DIR=" + sortDirectory.getAbsolutePath();
		args[2] = "I=" + inFile;
		args[3] = "O=" + outFile;
		args[4] = "CREATE_INDEX=true";
		log.info("Sorting alignments");
		new SortSam().instanceMain(args);
		log.info("Sorted alignments. Deleting temporary directory");
		sortDirectory.delete();
	}
	

}
