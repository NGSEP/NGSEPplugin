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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.SpecialFieldsHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * 
 * @author Jorge Duitama, Juan Camilo Quintero
 *
 */
public class SyncMapRead extends Job {

	// Part of the bowtie2 command with common parameters for many samples
	private List<String> commandArray = new ArrayList<String>();
	private String outputDirectory;
	private boolean SkipSorting = false;
	private boolean keepUnSorted = false;	
	private SampleData sampleData;
	

	public SyncMapRead(String name) {
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
		
//		String logName = outputDirectory+File.separator+jobId+"Map.log";
		String logName = sampleData.getMapLogFile();
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
			monitor.beginTask(jobId, 120);
			String readGroupId = sampleData.getReadGroupId();
			String sampleId = sampleData.getSampleId();
			commandArray.add("--rg");
			commandArray.add("SM:" +sampleId);
			commandArray.add("--rg-id");
			commandArray.add(readGroupId);
			if (sampleData.getFastq2() != null && sampleData.getFastq1()!=null) {
				commandArray.add("-1");
				commandArray.add(SpecialFieldsHelper.maskWhiteSpaces(sampleData.getFastq1()));
				commandArray.add("-2");
				commandArray.add(SpecialFieldsHelper.maskWhiteSpaces(sampleData.getFastq2()));
			} else {
				commandArray.add("-U");
				commandArray.add(SpecialFieldsHelper.maskWhiteSpaces(sampleData.getFastq1()));
			}
			commandArray.add("-S"); 
			commandArray.add(SpecialFieldsHelper.maskWhiteSpaces(sampleData.getSamFile()));
			logCommand(commandArray,log);
			System.out.println(commandArray.toString());
			
			Process p = Runtime.getRuntime().exec(commandArray.toArray(new String[0]));
			
			log.info("Started Map Reads");
			int exitValue = 0;
			while (true) {
				// Wait 30 seconds
				Thread.sleep(30000);
				// report 1 unit of progress
				monitor.worked(1);
				// End process if user requests so
				if (monitor.isCanceled()) {
					p.destroy();
					log.info("Process canceled by user");
					return Status.CANCEL_STATUS;
				}
				try {
					exitValue = p.exitValue();
					log.info("Process terminated with exit value " + exitValue);
					break;
				} catch (IllegalThreadStateException e) {
					log.info("Process still working");
				}

			}
			InputStream stdoutStream = p.getInputStream();
			StringBuffer buffer = new StringBuffer();
			while (true) {
				int message = stdoutStream.read();
				if (message == -1) break;
				buffer.append((char) message);
			}
			if(buffer.length()>0) log.info(buffer.toString());
			stdoutStream.close();
			InputStream stderrStream = p.getErrorStream(); 
			StringBuffer bufferOne = new StringBuffer();
			while (true) {
				int failure = stderrStream.read();
				if (failure == -1) break;
				bufferOne.append((char) failure);
			}
			if (bufferOne.length() > 0) log.info(bufferOne.toString());
			stderrStream.close();
//			if (exitValue != 0) return Status.CANCEL_STATUS;
			if (exitValue != 0) return new Status(IStatus.ERROR, sampleData.getSampleId(), "Error while mapping sample "+sampleData.getSampleId(),new Exception("Exit value: "+exitValue));
			if (!SkipSorting) {
				log.info("Sorting alignments from file: "+sampleData.getSamFile());
				if (FieldValidator.isFileExistenceWithData(sampleData.getSamFile())) {
					SyncSortAlignment.sortAlignments(jobId, sampleData.getSamFile(),sampleData.getSortedBamFile(),log);
				} else {
					log.severe("Alignments file "+sampleData.getSamFile()+" could not be opened or is empty");
				}	
			}
			if (!keepUnSorted) {
				log.info("Deleting SAM file");
				File f = new File(sampleData.getSamFile());
				f.delete();
			}
			log.info("Process finished");
			monitor.done();
		} catch (Exception e) {
			log.info("Error executing map reads: ");
			String message = LoggingHelper.serializeException(e);
			log.severe(message);
//			return Status.CANCEL_STATUS;
			return new Status(IStatus.ERROR, sampleData.getSampleId(), "Error while mapping sample",e);
		} finally {
			LoggingHelper.closeLogger(log);
		}
		return Status.OK_STATUS;
	}

	

	private void logCommand(List<String> commandArray, Logger log) {
		log.info("Array with command items");
		for(String s:commandArray) {
			log.info(s);
		}
		
	}

	public boolean isSkipSorting() {
		return SkipSorting;
	}

	public void setSkipSorting(boolean skipSorting) {
		SkipSorting = skipSorting;
	}

	public boolean isKeepUnSorted() {
		return keepUnSorted;
	}

	public void setKeepUnSorted(boolean keepUnSorted) {
		this.keepUnSorted = keepUnSorted;
	}

	public SampleData getSampleData() {
		return sampleData;
	}

	public void setSampleData(SampleData sampleData) {
		this.sampleData = sampleData;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public void setCommandArray(List<String> commandArray) {
		this.commandArray.clear();
		this.commandArray.addAll(commandArray);
	}
	
	public void setMapCommandArray(Map<String,Object> commandMap) {
		this.commandArray.clear();
		
		//Mapping main
		this.commandArray.add((String)commandMap.get("mainCMD"));
				
		this.commandArray.add((String)commandMap.get("indexCMD"));
		this.commandArray.add((String)commandMap.get("indexArg"));
		
		
		if(commandMap.get("minimunInsIzeCMD")!=null){
			this.commandArray.add((String)commandMap.get("minimunInsIzeCMD"));
			this.commandArray.add((String)commandMap.get("minimunInsIzeArg"));
		}
		
		if(commandMap.get("maximunInsIzeCMD")!=null){
			this.commandArray.add((String)commandMap.get("maximunInsIzeCMD"));
			this.commandArray.add((String)commandMap.get("maximunInsIzeArg"));
		}
		
		if(commandMap.get("kalignmentCMD")!=null){
			this.commandArray.add((String)commandMap.get("kalignmentCMD"));
			this.commandArray.add((String)commandMap.get("kalignmentArg"));
		}	
		
		this.commandArray.add((String)commandMap.get("addTextCMD"));
		
		this.commandArray.add((String)commandMap.get("platformCMD")+(String)commandMap.get("platformArg"));
		
		
		if (commandMap.get("reportAllAlignmentCMD")!=null){
			this.commandArray.add((String)commandMap.get("reportAllAlignmentCMD"));
		}
		//Mapping Ali
		
		if(commandMap.get("inputCMD")!=null){
			this.commandArray.add((String)commandMap.get("inputCMD"));
		}
				
		if(commandMap.get("phredCMD")!=null){
			this.commandArray.add((String)commandMap.get("phredCMD"));
		}
		
		if(commandMap.get("trim5CMD")!=null){
			this.commandArray.add((String)commandMap.get("trim5CMD"));
			this.commandArray.add((String)commandMap.get("trim5Arg"));
		}
		
		if(commandMap.get("trim3CMD")!=null){
			this.commandArray.add((String)commandMap.get("trim3CMD"));
			this.commandArray.add((String)commandMap.get("trim3Arg"));
		}
		

		if(commandMap.get("nCMD")!=null){
			this.commandArray.add((String)commandMap.get("nCMD"));
			this.commandArray.add((String)commandMap.get("nArg"));
		}
		
		if(commandMap.get("lCMD")!=null){
			this.commandArray.add((String)commandMap.get("lCMD"));
			this.commandArray.add((String)commandMap.get("lArg"));
		}
		
		if(commandMap.get("gbarCMD")!=null){
			this.commandArray.add((String)commandMap.get("gbarCMD"));
			this.commandArray.add((String)commandMap.get("gbarArg"));
		}
		
		
		if(commandMap.get("dpadCMD")!=null){
			this.commandArray.add((String)commandMap.get("dpadCMD"));
			this.commandArray.add((String)commandMap.get("dpadArg"));
		}
		
		if(commandMap.get("dCMD")!=null){
			this.commandArray.add((String)commandMap.get("dCMD"));
			this.commandArray.add((String)commandMap.get("dArg"));
		}
		
		if(commandMap.get("rCMD")!=null){
			this.commandArray.add((String)commandMap.get("rCMD"));
			this.commandArray.add((String)commandMap.get("rArg"));
		}
		
		if(commandMap.get("iCMD")!=null){
			this.commandArray.add((String)commandMap.get("iCMD"));
			this.commandArray.add((String)commandMap.get("iArg"));
		}
		
		if(commandMap.get("nceilCMD")!=null){
			this.commandArray.add((String)commandMap.get("nceilCMD"));
			this.commandArray.add((String)commandMap.get("nceilArg"));
		}
		
		if(commandMap.get("nofwCMD")!=null){
			this.commandArray.add((String)commandMap.get("nofwCMD"));
		}
		
		if(commandMap.get("norcCMD")!=null){
			this.commandArray.add((String)commandMap.get("norcCMD"));
		}
		
							
	}
	
	
	
}
