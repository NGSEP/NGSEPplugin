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
import ngsep.genome.ReferenceGenome;
import ngsep.vcf.VCFFunctionalAnnotator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * 
 * @author Juan Camilo Quintero
 * @author Jorge Duitama
 */
public class SyncVCFFunctionalAnnotator extends Job {

	private String logName;
	private String nameProgressBar;
	
	private String transcriptomeMap;
	private ReferenceGenome genome;
	private String variantsFile;
	private String outputFile;
	
	private VCFFunctionalAnnotator instance;
	
	public SyncVCFFunctionalAnnotator(String name) {
		super(name);
	}

	protected IStatus run(IProgressMonitor monitor) {
		//Create log
		FileHandler logFile = null;
		PrintStream out = null;
		Logger log = null;
		try {
			logFile = new FileHandler(logName, false);
			log = LoggingHelper.createLogger(logName, logFile);
			instance.setLog(log);
			
			monitor.beginTask(getNameProgressBar(), 500);
			log.info("Variants Functional Annotator");
			
			instance.setProgressNotifier(new DefaultProgressNotifier(monitor));
			
			instance.loadMap(transcriptomeMap,genome);
			out = new PrintStream(outputFile);
			instance.annotate(variantsFile, out);
			log.info("Process finished");
			monitor.done();
		} catch (Exception e) {
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

	public String getTranscriptomeMap() {
		return transcriptomeMap;
	}

	public void setTranscriptomeMap(String transcriptomeMap) {
		this.transcriptomeMap = transcriptomeMap;
	}

	public String getVariantsFile() {
		return variantsFile;
	}

	public void setVariantsFile(String variantsFile) {
		this.variantsFile = variantsFile;
	}

	public ReferenceGenome getGenome() {
		return genome;
	}

	public void setGenome(ReferenceGenome genome) {
		this.genome = genome;
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

	public VCFFunctionalAnnotator getInstance() {
		return instance;
	}

	public void setInstance(VCFFunctionalAnnotator instance) {
		this.instance = instance;
	}
}
