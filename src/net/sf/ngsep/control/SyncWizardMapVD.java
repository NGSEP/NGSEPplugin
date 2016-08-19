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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * 
 * @author Daniel Cruz
 *
 */
public class SyncWizardMapVD extends Job {


	private SampleData sampleData;



	private SyncMapRead mappingJob;

	private SyncDetector detectingJob;

	private String outputDirectory;



	public SyncWizardMapVD(String name) {
		super(name);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		String jobId=null;
		if (sampleData.getSampleId().equalsIgnoreCase(sampleData.getReadGroupId())) {
			jobId = sampleData.getSampleId();
		} else {
			jobId = sampleData.getSampleId()+"-"+sampleData.getReadGroupId();
		}	

		String logName = outputDirectory+File.separator+jobId+"Wizard1.log";
		FileHandler logFile;
		try {
			logFile = new FileHandler(logName, false);
		} catch (IOException e) {
			System.err.println("Could not create log file");
			e.printStackTrace();
			return Status.CANCEL_STATUS;
		}
		Logger log = LoggingHelper.createLogger(logName, logFile);
		
		IStatus jobStatus = null;
		
		
		
		try {
			log.info("Wizard Mapping Process starting with sample "+sampleData.getSampleId());

			jobStatus = mappingJob.run(monitor);
			
			if(jobStatus.getSeverity() == IStatus.ERROR||jobStatus.getSeverity() == IStatus.CANCEL){
				log.info("Wizard Mapping Error with sample "+sampleData.getSampleId()+" "+jobStatus.getMessage());
				throw new Exception(jobStatus.getException());
			}	
			
			log.info("Wizard Variants Detection Process starting with sample "+sampleData.getSampleId());

			jobStatus = detectingJob.run(monitor);
			
			if(jobStatus.getSeverity() == IStatus.ERROR||jobStatus.getSeverity() == IStatus.CANCEL){
				log.info("Wizard Detecting Error with sample "+sampleData.getSampleId()+" "+jobStatus.getMessage());
				throw new Exception(jobStatus.getException());
			}
			

			log.info("WizardMapVD Process finished with sample "+sampleData.getSampleId());
			monitor.done();
		} catch (Exception e) {
			log.info("Error executing WizardMapVD Process Sample: "+jobId);
			String message = LoggingHelper.serializeException(e);
			log.severe(message);
//			return Status.CANCEL_STATUS;
			return jobStatus;
		} finally {
			LoggingHelper.closeLogger(log);
		}
		return Status.OK_STATUS;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public SampleData getSampleData() {
		return sampleData;
	}

	public void setSampleData(SampleData sampleData) {
		this.sampleData = sampleData;
	}

	public SyncMapRead getMappingJob() {
		return mappingJob;
	}

	public void setMappingJob(SyncMapRead mappingJob) {
		this.mappingJob = mappingJob;
	}

	public SyncDetector getDetectingJob() {
		return detectingJob;
	}

	public void setDetectingJob(SyncDetector detectingJob) {
		this.detectingJob = detectingJob;
	}




}
