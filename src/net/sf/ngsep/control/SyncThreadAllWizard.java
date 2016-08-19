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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.view.MainVariantsDetector;
import ngsep.discovery.VariantsDetector;
import ngsep.genome.ReferenceGenome;

import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Daniel Felipe Cruz, Jorge Duitama
 *
 */
public class SyncThreadAllWizard implements Runnable {

	private SyncThreadJobsPool wizardPool;


	private SyncDetermineVariants syncDetermineVariants;
	
	private String currentFolder;
	private String varsFolder;
	
	private String variantsPrefix;

	private List<Job> jobsWizardStep1;
	private int numProcs;
	
	private Map<String,Object> commandsVD;
	
	private boolean bothGQ = false ;
	private boolean stopFail;



	public SyncThreadAllWizard(String currentFolder, String varsFolder) {
		
		this.currentFolder = currentFolder;
		this.varsFolder = varsFolder;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		String mainWizardLogName = currentFolder+File.separator+"MainWizard.log";
		FileHandler mainWizardLogFile = null;
		try {
			mainWizardLogFile = new FileHandler(mainWizardLogName, false);
		} catch (IOException e) {
			System.err.println("Could not create log file");
			e.printStackTrace();
		}
		Logger logWizard = LoggingHelper.createLogger(mainWizardLogName, mainWizardLogFile);
		

		try {
		
			
// Step 1 Mappping and VD for each sample			
			wizardPool = new SyncThreadJobsPool(numProcs,jobsWizardStep1);
			wizardPool.setStopFail(stopFail);
			wizardPool.run();
			Map<Integer, String> errorsStep1 = wizardPool.getJobErrors();
			
			
			if(errorsStep1.size()>0 && stopFail){
				logWizard.log(Level.SEVERE,"Wizard Aborted at step 1");
				return;
			}

			logWizard.info("Wizard Completed at step 1");
			
			
// Step 2 Determine List of variants			
			String outputListVars = varsFolder + File.separator +variantsPrefix+"_variants.vcf";
			String loglistVars = LoggingHelper.getLoggerFilename(outputListVars,"DV");

			List<String> listVCFs = new ArrayList<String>();
			ReferenceGenome genome = null;
			SampleData sd = null;
			for (int i=0;i<jobsWizardStep1.size();i++) {
				SyncWizardMapVD job = (SyncWizardMapVD)jobsWizardStep1.get(i);
				sd = job.getSampleData();
				if(genome == null) genome = job.getDetectingJob().getVd().getGenome();
				if(errorsStep1.get(i)==null) listVCFs.add(sd.getVcfFile());
				//Put list of variant for further genotyping
				sd.setVariantsFile(outputListVars);
			}

			syncDetermineVariants = new SyncDetermineVariants("DetermineVariants");
			syncDetermineVariants.setLogName(loglistVars);
			syncDetermineVariants.setListFiles(listVCFs);
			//all samples must share the same reference.
			syncDetermineVariants.setSequenceNames(genome.getSequencesMetadata());
			syncDetermineVariants.setOutputFile(outputListVars);
			syncDetermineVariants.schedule();
			syncDetermineVariants.join();
			
			logWizard.info("Wizard Completed at step 2");
			
// Step 3 VD Genotyping with known variants list
			
			List<Job> jobsWizardStep3 = new ArrayList<Job>();
			
			sd = null;
			
			for (int i=0;i<jobsWizardStep1.size();i++) {
				SyncWizardMapVD jobStep1 = (SyncWizardMapVD)jobsWizardStep1.get(i);
				sd = jobStep1.getSampleData();
				if(errorsStep1.get(i)!=null) continue;
				VariantsDetector vdSample = new VariantsDetector();
				vdSample.setSampleId(sd.getSampleId());
				vdSample.setAlignmentsFile(sd.getSortedBamFile());
				
				String vcfFileGT = sd.getVcfFileGT();
				if(vcfFileGT!=null)
					vdSample.setOutVars(new PrintStream(vcfFileGT));

				MainVariantsDetector.copyCommonParams(commandsVD,vdSample);
								
				// Set list of knowns variants 
				vdSample.setKnownVariantsFile(outputListVars);
				
				//If user selected SV detection for step 1, it will be used for the genotyping, regardless step 3 goes only with SNPs and small indels
				String svFile = sd.getSvFile();
				if(svFile!=null) vdSample.setKnownSVsFile(svFile);
				
				// Deactivate SV detection, if selected it was performed in step 1 
				vdSample.setFindRepeats(false);
				vdSample.setRunRDAnalysis(false);
				vdSample.setRunRPAnalysis(false);
				
				
				
				//check if GQ applies
				if(!bothGQ)
					vdSample.setMinQuality((short) 0);
				
				SyncDetector vdJobSample = new SyncDetector(sd.getSampleId()+" GT");
				vdJobSample.setGT(true);
				vdJobSample.setSampleData(sd);
				vdJobSample.setVd(vdSample);
				jobsWizardStep3.add(vdJobSample);
			}
			wizardPool = new SyncThreadJobsPool(numProcs,jobsWizardStep3);
			wizardPool.run();
			Map<Integer, String> errorsStep3 = wizardPool.getJobErrors();
			
			if(errorsStep3.size()>0 && stopFail){
				logWizard.log(Level.SEVERE,"Wizard Aborted at step 3");
				return;
			}
			
			logWizard.info("Wizard Completed at step 3");
			
// Step 4 Merge pop VCF
			
			SyncMergeVCF syncMerge = new SyncMergeVCF("MergeVCF");
			
			String popVCF = varsFolder + File.separator + variantsPrefix+".vcf";
			

			
			String logFilename = LoggingHelper.getLoggerFilename(popVCF,"MVCF");
			syncMerge.setLogName(logFilename);
			listVCFs.clear();
			for (int i=0;i<jobsWizardStep3.size();i++) {
				if(errorsStep3.get(i)==null) {
					SyncDetector jobStep3 = (SyncDetector)jobsWizardStep3.get(i);
					listVCFs.add(jobStep3.getSampleData().getVcfFileGT());
				}
			}		
			syncMerge.setListFiles(listVCFs);
			syncMerge.setSequenceNames(genome.getSequencesMetadata());
			syncMerge.setOutputFile(popVCF);
			syncMerge.setNameProgressBar(variantsPrefix+" Population");
			syncMerge.schedule();
			
			logWizard.info("Wizard Completed at step 4");
		} catch (Exception e) {
			String message = LoggingHelper.serializeException(e);
			logWizard.severe(e.getMessage());
			logWizard.log(Level.SEVERE, message);			
			
		}finally{
			LoggingHelper.closeLogger(logWizard);
			
		}

	}



	public String getVariantsPrefix() {
		return variantsPrefix;
	}

	public void setVariantsPrefix(String variantsPrefix) {
		this.variantsPrefix = variantsPrefix;
	}

	public List<Job> getJobsWizard() {
		return jobsWizardStep1;
	}

	public void setJobsWizard(List<Job> jobsWizard) {
		this.jobsWizardStep1 = jobsWizard;
	}

	public int getNumProcs() {
		return numProcs;
	}

	public void setNumProcs(int numProcs) {
		this.numProcs = numProcs;
	}

	public Map<String, Object> getCommandsVD() {
		return commandsVD;
	}

	public void setCommandsVD(Map<String, Object> commandsVD) {
		this.commandsVD = commandsVD;
	}

	public boolean isBothGQ() {
		return bothGQ;
	}

	public void setBothGQ(boolean bothGQ) {
		this.bothGQ = bothGQ;
	}

	public boolean isStopFail() {
		return stopFail;
	}

	public void setStopFail(boolean stopFail) {
		this.stopFail = stopFail;
	}

}
