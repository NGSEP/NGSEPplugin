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
import ngsep.vcf.VCFConverter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * 
 * @author Jorge Duitama, Juan Camilo QUintero, Claudia Perea, Paulo Izquierdo
 *
 */
public class SyncVCFConverter {

	private String file;
	private String outputFile;
	private boolean structure = false;
	private boolean fasta = false;
	private boolean matrix = false;
	private boolean hapMap = false;
	private boolean spagedi = false;
	private boolean plink = false;
	private boolean haploview = false;
	private boolean emma = false;
	private boolean powerMarker = false;
	private boolean eigensoft = false;
	private boolean flapJack = false;
	private boolean darwin = false;
	private boolean rrBLUP = false;
	private boolean joinMap = false;
	private boolean treeMix = false;
	private String idParent1 = null;
	private String idParent2 = null;
	private String populationFile=null;
	
	private IProgressMonitor progressMonitor;
	private String logName;
	private FileHandler logFile;
	private String nameProgressBar;
	
	private final Job job = new Job("VCF Converter Process") {
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			Logger log = LoggingHelper.createLogger(logName, logFile);
			try {
				monitor.beginTask(getNameProgressBar(), 500);
				VCFConverter converter = new VCFConverter();
				log.info("Started VCF Converter");
				log.info("Passing parameters");
				converter.setPrintEigensoft(eigensoft);
				converter.setPrintEmma(emma);
				converter.setPrintFasta(fasta);
				converter.setPrintFlapjack(flapJack);
				converter.setPrintHaploview(haploview);
				converter.setPrintHapmap(hapMap);
				converter.setPrintMatrix(matrix);
				converter.setPrintPlink(plink);
				converter.setPrintPowerMarker(powerMarker);
				converter.setPrintSpagedi(spagedi);
				converter.setPrintStructure(structure);
				converter.setPrintDarwin(darwin);
				converter.setPrintrrBLUP(rrBLUP);
				converter.setPrintJoinMap(joinMap);
				if(idParent1!=null)converter.setIdParent1(idParent1);
				if(idParent2!=null)converter.setIdParent2(idParent2);
				converter.setPrintTreeMix(treeMix);
				if(populationFile!=null)converter.setPopulationFile(populationFile);
				log.info("Information processing");
				log.info("converting vcf file");
				converter.process(file, outputFile);
				converter.setProgressNotifier(new DefaultProgressNotifier(progressMonitor));
				log.info("Process finished");
			} catch (Exception e) {
				log.info("Error executing VCF Converter: ");
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

	public String getFile() {
		return file;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public boolean isStructure() {
		return structure;
	}

	public boolean isFasta() {
		return fasta;
	}

	public boolean isMatrix() {
		return matrix;
	}

	public boolean isHapMap() {
		return hapMap;
	}

	public boolean isSpagedi() {
		return spagedi;
	}

	public boolean isPlink() {
		return plink;
	}

	public boolean isHaploview() {
		return haploview;
	}

	public boolean isEmma() {
		return emma;
	}

	public boolean isPowerMarker() {
		return powerMarker;
	}

	public boolean isEigensoft() {
		return eigensoft;
	}

	public boolean isFlapJack() {
		return flapJack;
	}

	public boolean isrrBLUP() {
		return rrBLUP;
	}
	
	public Job getJob() {
		return job;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public void setStructure(boolean structure) {
		this.structure = structure;
	}

	public void setFasta(boolean fasta) {
		this.fasta = fasta;
	}

	public void setMatrix(boolean matrix) {
		this.matrix = matrix;
	}

	public void setHapMap(boolean hapMap) {
		this.hapMap = hapMap;
	}

	public void setSpagedi(boolean spagedi) {
		this.spagedi = spagedi;
	}

	public void setPlink(boolean plink) {
		this.plink = plink;
	}

	public void setHaploview(boolean haploview) {
		this.haploview = haploview;
	}

	public void setEmma(boolean emma) {
		this.emma = emma;
	}

	public void setPowerMarker(boolean powerMarker) {
		this.powerMarker = powerMarker;
	}

	public void setEigensoft(boolean eigensoft) {
		this.eigensoft = eigensoft;
	}

	public void setFlapJack(boolean flapJack) {
		this.flapJack = flapJack;
	}

	public void setrrBLUP(boolean rrBLUP) {
		this.rrBLUP = rrBLUP;
	}

	public String getLogName() {
		return logName;
	}

	public FileHandler getLogFile() {
		return logFile;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

	public void setLogFile(FileHandler logFile) {
		this.logFile = logFile;
	}

	public String getNameProgressBar() {
		return nameProgressBar;
	}

	public void setNameProgressBar(String nameProgressBar) {
		this.nameProgressBar = nameProgressBar;
	}

	public boolean isDarwin() {
		return darwin;
	}

	public void setDarwin(boolean darwin) {
		this.darwin = darwin;
	}

	public boolean isJoinMap() {
		return joinMap;
	}

	public void setJoinMap(boolean joinMap) {
		this.joinMap = joinMap;
	}

	public boolean isTreeMix() {
		return treeMix;
	}

	public void setTreeMix(boolean treeMix) {
		this.treeMix = treeMix;
	}

	public String getPopulationFile() {
		return populationFile;
	}

	public void setPopulationFile(String populationFile) {
		this.populationFile = populationFile;
	}

	public String getIdParent1() {
		return idParent1;
	}

	public void setIdParent1(String idParent1) {
		this.idParent1 = idParent1;
	}

	public String getIdParent2() {
		return idParent2;
	}

	public void setIdParent2(String idParent2) {
		this.idParent2 = idParent2;
	}
}
