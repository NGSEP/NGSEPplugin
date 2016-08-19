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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import net.sf.ngsep.utilities.DefaultProgressNotifier;
import net.sf.ngsep.utilities.LoggingHelper;
import ngsep.genome.GenomicRegionSortedCollection;
import ngsep.sequences.QualifiedSequenceList;
import ngsep.variants.GenomicVariant;
import ngsep.vcf.IndividualSampleVariantsMerge;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * 
 * @author Juan Camilo Quintero
 *
 */
public class SyncDetermineVariants extends Job {

	private String outputFile;
	private final List<String> listFiles = new ArrayList<String>();
	private QualifiedSequenceList sequenceNames;
	private String logName;
	
	public SyncDetermineVariants(String name) {
		super(name);
	}

	protected IStatus run(IProgressMonitor monitor) {
		FileHandler logFile = null;
		Logger log = null;
		try {
			logFile = new FileHandler(logName, false);
			log = LoggingHelper.createLogger(logName, logFile);
			monitor.beginTask("Determine list of variants",listFiles.size());
			log.info("Started Determine Variants");
			IndividualSampleVariantsMerge merge = new IndividualSampleVariantsMerge();
			merge.setLog(log);
			merge.setProgressNotifier(new DefaultProgressNotifier(monitor));
			log.info("Determining listo of variants");
			GenomicRegionSortedCollection<GenomicVariant> variants = merge.mergeVariants(listFiles, sequenceNames);
			merge.printVariants(outputFile, variants);
			log.info("Process finished");
		} catch (Exception e) {
			log.info("Error executing determine variants: ");
			String message = LoggingHelper.serializeException(e);
			log.severe(message);
			return new Status(IStatus.ERROR, "NGSEP List VD" , "Error while executing Determine List of Variants");
		} finally {
			LoggingHelper.closeLogger(log);
		}
		return Status.OK_STATUS;

	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	

	public QualifiedSequenceList getSequenceNames() {
		return sequenceNames;
	}

	public void setSequenceNames(QualifiedSequenceList sequenceNames) {
		this.sequenceNames = sequenceNames;
	}

	public List<String> getListFiles() {
		return Collections.unmodifiableList(listFiles);
	}

	public void setListFiles(List<String> listFiles) {
		this.listFiles.clear();
		this.listFiles.addAll(listFiles);
	}

	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

}
