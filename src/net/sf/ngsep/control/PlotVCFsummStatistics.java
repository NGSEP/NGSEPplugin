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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.xeiam.xchart.Chart;

import net.sf.ngsep.utilities.PlotUtils;
import ngsep.main.io.ParseUtils;

/**
 * @author Juan Fernando De la Hoz
 */
public class PlotVCFsummStatistics {
	
	private String inputStats;
	private String outputPrfx;
	
	public PlotVCFsummStatistics(String input, String output){
		inputStats = input;
		outputPrfx = output;
	}
	
	public void plotAlleleFreqDistribution() throws IOException {
		ArrayList <Double> total = new ArrayList<Double>();
		ArrayList <Double> coding = new ArrayList<Double>();
		ArrayList <Double> synonymous = new ArrayList<Double>();
		ArrayList <Double> missense = new ArrayList<Double>();
		ArrayList <Double> nonsense = new ArrayList<Double>();
		double totalSum = 0;
		double synonymousSum = 0;
		double missenseSum = 0;
		double nonsenseSum = 0;
		// Variables
		try ( FileReader fileRead = new FileReader(inputStats);
			  BufferedReader bufferRead = new BufferedReader(fileRead);) {
			String line;
			int j = 0;
			boolean findTable = false;
			
			// Parse table
			while ((line = bufferRead.readLine()) != null) {
				if (line.equals("")) {
					findTable = false;
				} if (findTable){
					String [] lineArray = line.split("\t");
					
					double nextTotal = Double.parseDouble(lineArray[1]);
					total.add(nextTotal);
					totalSum += nextTotal;
					double nextSyn = Double.parseDouble(lineArray[2]);
					synonymous.add(nextSyn);
					synonymousSum += nextSyn;
					double nextMis = Double.parseDouble(lineArray[3]);
					missense.add(nextMis);
					missenseSum += nextMis;
					double nextNon = Double.parseDouble(lineArray[4]);
					nonsense.add(nextNon);
					nonsenseSum += nextNon;
					coding.add( synonymous.get(j) + missense.get(j) + nonsense.get(j) );
					j++;
				} if (line.startsWith("MAF DISTRIBUTIONS BIALLELIC SNVs")){	
					findTable = true;
					bufferRead.readLine();
				}
			}
		}
		
		// Calculate percentages
		ArrayList <String> maf = new ArrayList<String>();
		ArrayList <Double> totalPrcnt = new ArrayList<Double>();
		ArrayList <Double> codingPrcnt = new ArrayList<Double>();
		ArrayList <Double> synonymousPrcnt = new ArrayList<Double>();
		ArrayList <Double> missensePrcnt = new ArrayList<Double>();
		ArrayList <Double> nonsensePrcnt = new ArrayList<Double>();
		
		DecimalFormat fmt = ParseUtils.ENGLISHFMT;
		for (int i = 0 ; i < total.size() ; i++){
			int nextMAF50 = 50*i/(total.size()-1);
			//if(nextMAF50%5==0) maf.add(fmt.format(0.01*nextMAF50));
			//else maf.add(" ");
			maf.add(fmt.format(0.01*nextMAF50));
			//maf.add(lineArray[0]);
			totalPrcnt.add(Math.max(0.01, safeDoubleRatio(total.get(i) * 100,totalSum)));
			codingPrcnt.add(Math.max(0.01, safeDoubleRatio(coding.get(i) * 100,synonymousSum + missenseSum + nonsenseSum)));
			synonymousPrcnt.add(Math.max(0.01, safeDoubleRatio(synonymous.get(i) * 100, synonymousSum)));
			missensePrcnt.add(Math.max(0.01, safeDoubleRatio(missense.get(i) * 100, missenseSum)));
			nonsensePrcnt.add(Math.max(0.01, safeDoubleRatio(nonsense.get(i) * 100, nonsenseSum)));
		}
		
		// Plot
		if (totalSum != 0){
			Chart chart = PlotUtils.createBarChart("Allele Frequency Distribution", "Minor Allele Frequency", "Percentage of SNPs");
			PlotUtils.addSample("total", chart, maf, totalPrcnt);
			if (synonymousSum > 0) PlotUtils.addSample("Synonymous", chart, maf, synonymousPrcnt);	
			if (missenseSum > 0) PlotUtils.addSample("Missense/Stop lost", chart, maf, missensePrcnt);
			if (nonsenseSum > 0) PlotUtils.addSample("Stop gained/Start lost", chart, maf, nonsensePrcnt);
			PlotUtils.manageLegend(chart, 1);
			PlotUtils.saveChartPNG(chart, outputPrfx + "_MAFdistribution");
		}
	}

	public void plotSNPsPerSampleStats() throws IOException {
		
		// Variables
		File statsFile = new File(inputStats);
		FileReader fileRead = new FileReader(statsFile);
		BufferedReader bufferRead = new BufferedReader(fileRead);
		String line ;	
		String lineArray[]; 
		int i = 0;
//		ArrayList <String> samples = new ArrayList<String>();
		ArrayList <Integer> samples = new ArrayList<Integer>();							// samples as numbers, not strings
		ArrayList <Integer> synonymous = new ArrayList<Integer>();
		ArrayList <Integer> misSense = new ArrayList<Integer>();
		ArrayList <Integer> nonSense = new ArrayList<Integer>();
		boolean findTable = false;
		
		// Parse table
		while ((line = bufferRead.readLine()) != null) {
			if (line.equals("")) {
				findTable = false;
			} if (findTable){
				lineArray = line.split("\t");
//				samples.add(lineArray[0]);			
				samples.add(i+1);														// samples as numbers, not strings
				synonymous.add(Integer.parseInt(lineArray[6]));
				misSense.add(Integer.parseInt(lineArray[7]) + synonymous.get(i));
				nonSense.add(Integer.parseInt(lineArray[8]) + misSense.get(i));
				i ++ ;
			} if (line.equals("SNP COUNTS PER SAMPLE")){	
				findTable = true;
				bufferRead.readLine();
			}
		}
		bufferRead.close(); fileRead.close();
		
		// Plot
		Chart chart = PlotUtils.createBarChart("SNPs by Type", "Accessions", "Amount of SNPs");
		PlotUtils.addSample("stop gained/start lost", chart, samples, nonSense);
		PlotUtils.addSample("missense/stop lost", chart, samples, misSense);
		PlotUtils.addSample("synonymous", chart, samples, synonymous);
		PlotUtils.overlapBars(chart);
		PlotUtils.manageLegend(chart, 5);
		PlotUtils.saveChartPNG(chart, outputPrfx + "_SNPtypePerSample");
	}

	public void plotIndelsPerSampleStats() throws IOException {
		
		// Variables
		File statsFile = new File(inputStats);
		FileReader fileRead = new FileReader(statsFile);
		BufferedReader bufferRead = new BufferedReader(fileRead);
		String line ;	
		String lineArray[]; 
		int i = 0;
		ArrayList <String> samples = new ArrayList<String>();
		ArrayList <Integer> homo_coding = new ArrayList<Integer>();										//not yet clear
		ArrayList <Integer> hetero_frmshift = new ArrayList<Integer>();									//not yet clear
		boolean findTable = false;
		
		// Parse table
		while ((line = bufferRead.readLine()) != null) {
			if (line.equals("")) {
				findTable = false;
			} if (findTable){
				lineArray = line.split("\t");
				samples.add(lineArray[0]);																//should add(i+1) and print xAxisTicks (see PlotUtils.overlapBars())
				homo_coding.add(Integer.parseInt(lineArray[3]));										//not yet clear (3/5)
				hetero_frmshift.add(Integer.parseInt(lineArray[4]) + homo_coding.get(i));				//not yet clear (4/6) (if coding - frameshift, should not +)
				i ++ ;
			} if (line.equals("BIALLELIC INDEL COUNTS PER SAMPLE")){
				findTable = true;
				bufferRead.readLine();
			}
		}
		bufferRead.close(); fileRead.close();
		
		// Plot
		Chart chart = PlotUtils.createBarChart("Indels by Type", "Accessions", "Amount of Indels");
		PlotUtils.addSample("heterozygous - frameshift", chart, samples, hetero_frmshift);				//not yet clear
		PlotUtils.addSample("homozygous - coding", chart, samples, homo_coding);						//not yet clear
		PlotUtils.overlapBars(chart);																	//not yet clear (if coding - frameshift, would not work)
		PlotUtils.manageLegend(chart, 5);
		PlotUtils.saveChartPNG(chart, outputPrfx + "_IndelsPerSample");
	}

	public void plotOtherVarPerSampleStats() throws IOException {
		
		// Variables
		File statsFile = new File(inputStats);
		FileReader fileRead = new FileReader(statsFile);
		BufferedReader bufferRead = new BufferedReader(fileRead);
		String line ;	
		String lineArray[]; 
		int i = 0;
		ArrayList <String> samples = new ArrayList<String>();
		ArrayList <Integer> homozygous = new ArrayList<Integer>();
		ArrayList <Integer> heterozygous = new ArrayList<Integer>();
		boolean findTable = false;
		
		// Parse table
		while ((line = bufferRead.readLine()) != null) {
			if (line.equals("")) {
				findTable = false;
			} if (findTable){
				lineArray = line.split("\t");
				samples.add(lineArray[0]);
				homozygous.add(Integer.parseInt(lineArray[3]));
				heterozygous.add(Integer.parseInt(lineArray[4]) + homozygous.get(i));
				i ++ ;
			} if (line.equals("BIALLELIC STR COUNTS PER SAMPLE")){	
				findTable = true;
				bufferRead.readLine();
			}
		}
		bufferRead.close(); fileRead.close();
		
		// Plot
		Chart chart = PlotUtils.createBarChart("Other Variants by Type", "Accessions", "Amount of Variants");
		PlotUtils.addSample("heterozygous variants", chart, samples, heterozygous);
		PlotUtils.addSample("homozygous variants", chart, samples, homozygous);
		PlotUtils.overlapBars(chart);
		PlotUtils.manageLegend(chart, 5);
		PlotUtils.saveChartPNG(chart, outputPrfx + "_OtherVarsPerSample");
	}

	@SuppressWarnings("unchecked")
	public void plotGenotypeCallStats(int column, String variant) throws IOException {
		
		// Variables
		File statsFile = new File(inputStats);
		FileReader fileRead = new FileReader(statsFile);
		BufferedReader bufferRead = new BufferedReader(fileRead);
		String line ;	
		String lineArray[];
		ArrayList <Integer> samples = new ArrayList<Integer>();
		ArrayList <Integer> genotyped = new ArrayList<Integer>();
		boolean findTable = false;
		int total = 0;
		
		// Parse table
		while ((line = bufferRead.readLine()) != null) {
			if (line.equals("")) {
				findTable = false;
			} if (findTable){
				lineArray = line.split("\t");
				samples.add(Integer.parseInt(lineArray[0]));
				genotyped.add(Integer.parseInt(lineArray[column]));
				total += Integer.parseInt(lineArray[column]);
			} if ( line.equals("SAMPLES GENOTYPED DISTRIBUTIONS")){	
				findTable = true;
				bufferRead.readLine();
				bufferRead.readLine();
			}
		}
		bufferRead.close(); fileRead.close();
		
		// Calculate data
		int len = genotyped.size() - 1;
		if (total != 0) {
			
			ArrayList <Integer> cummulativeVars = (ArrayList<Integer>) genotyped.clone();
			ArrayList <Integer> cummulativeCalls = (ArrayList<Integer>) genotyped.clone();
			ArrayList <Double> missingData = new ArrayList<Double>();
			
			cummulativeCalls.set(len, genotyped.get(len) * (len + 1));
			for(int i = len - 1 ; i >= 0 ; i --){
				cummulativeVars.set(i, cummulativeVars.get(i+1) + cummulativeVars.get(i));
				cummulativeCalls.set(i, cummulativeCalls.get(i+1) + (genotyped.get(i) * (i+1) ));
			} 
			for(int i = 0; i <= len ; i++){
				missingData.add( (1.0 - (Double.valueOf(cummulativeCalls.get(i)) / ( Double.valueOf(cummulativeVars.get(i)) * (len+1) ))) * 100 ) ;
			}
			
			// Plot bars
			Chart bars = PlotUtils.createBarChart(variant + " per Number of Accessions Genotyped", "Minimum number of accessions genotyped", "Number of " + variant);
			PlotUtils.addSample("Number of " + variant, bars, samples, cummulativeVars);
			PlotUtils.manageLegend(bars, 0);
			PlotUtils.saveChartPNG(bars, outputPrfx + "_" + variant + "AccPerSample");
			
			// Plot line
			Chart linePlot = PlotUtils.createLineChart("Percentage of " + variant + " Missing Data", "Minimum number of accessions genotyped", "Percentage of missing data");
			PlotUtils.addSample("matrix filled", linePlot, samples, missingData);
			PlotUtils.manageLegend(linePlot, 0);
			PlotUtils.saveChartPNG(linePlot, outputPrfx + "_" + variant + "MissingData");		
			
		}
	}
	private double safeDoubleRatio(double numerator, double denominator) {
		if(denominator==0) return 0;
		return ((double)numerator)/denominator;
	}
}
