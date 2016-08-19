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

/**
 * 
 * @author Jorge Duitama, Daniel Cruz
 *
 */
public class SampleData {
	private String readGroupId;
	private String sampleId;
	private String fastq1;
	private String fastq2;
	
	private String referenceFile;
	
	private String samFile;
	private String sortedBamFile;
	private String mapLogFile;
	
	private String vcfFile;
	private String vdLogFile;
	
	private String vcfFileGT;
	private String vdGTLogFile;
	
	private String svFile;
	
	private String variantsFile;

	public String getReadGroupId() {
		return readGroupId;
	}

	public void setReadGroupId(String readGroupId) {
		this.readGroupId = readGroupId;
	}

	public String getSampleId() {
		return sampleId;
	}

	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}

	public String getFastq1() {
		return fastq1;
	}

	public void setFastq1(String fastq1) {
		this.fastq1 = fastq1;
	}

	public String getFastq2() {
		return fastq2;
	}

	public void setFastq2(String fastq2) {
		this.fastq2 = fastq2;
	}

	public String getSamFile() {
		return samFile;
	}

	public void setSamFile(String samFile) {
		this.samFile = samFile;
	}

	public String getSortedBamFile() {
		return sortedBamFile;
	}

	public void setSortedBamFile(String sortedBamFile) {
		this.sortedBamFile = sortedBamFile;
	}

	public String getReferenceFile() {
		return referenceFile;
	}

	public void setReferenceFile(String referenceFile) {
		this.referenceFile = referenceFile;
	}

	public String getVcfFile() {
		return vcfFile;
	}

	public void setVcfFile(String vcfFile) {
		this.vcfFile = vcfFile;
	}

	public String getVariantsFile() {
		return variantsFile;
	}

	public void setVariantsFile(String variantsFile) {
		this.variantsFile = variantsFile;
	}

	
	public String getSvFile() {
		return svFile;
	}

	public void setSvFile(String svFile) {
		this.svFile = svFile;
	}

	public String getVdLogFile() {
		return vdLogFile;
	}

	public void setVdLogFile(String vdLogFile) {
		this.vdLogFile = vdLogFile;
	}

	public String getVcfFileGT() {
		return vcfFileGT;
	}

	public void setVcfFileGT(String vcfFileGT) {
		this.vcfFileGT = vcfFileGT;
	}

	public String getVdGTLogFile() {
		return vdGTLogFile;
	}

	public void setVdGTLogFile(String vdGTLogFile) {
		this.vdGTLogFile = vdGTLogFile;
	}

	public String getMapLogFile() {
		return mapLogFile;
	}

	public void setMapLogFile(String mapLogFile) {
		this.mapLogFile = mapLogFile;
	}
	
	
}
