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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import net.sf.ngsep.utilities.DefaultProgressNotifier;
import net.sf.ngsep.utilities.PlotUtils;
import ngsep.discovery.CoverageStatisticsCalculator;
import net.sf.ngsep.utilities.LoggingHelper;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.xeiam.xchart.Chart;

/**
 * 
 * @author Daniel Cruz, Juan Camilo Quintero, Juan Fernando de la Hoz
 *
 */
public class SyncCalculateCoverageStatistics {
	private IProgressMonitor progressMonitor;
	
	private CoverageStatisticsCalculator coverageCalculator;
	private String alifile;
	private String outputFile;
	private String logName;
	private FileHandler logFile;
	private boolean multipleAlignments = false;
	private String alignments;
	private String nameProgressBar;

	private final Job job = new Job("Coverage Statistics Calculator Process") {
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			Logger log = LoggingHelper.createLogger(logName, logFile);
			progressMonitor = monitor;
			PrintStream outFileS = null;
			try {
				monitor.beginTask(getNameProgressBar(), 100);
				log.info("Started Coverage Statistics Calculator");
				String outFile = outputFile + ".stats";
				coverageCalculator = new CoverageStatisticsCalculator();
				setCoverageCalculator(coverageCalculator);
				log.info("OutPut File:" + outFile);
				log.info("OutPut Image:" + outputFile + ".png");
				if (multipleAlignments) {
					log.info("Multiple alignments selected");
					alignments = "Multiple Alignments";
				} else {
					log.info("Unique alignments selected");
					alignments = "Unique Alignments";
				}
				try{
					outFileS = new PrintStream(outFile);
					coverageCalculator.setOutFile(outFileS);
					log.info("Processing reads from file: " + alifile);
					coverageCalculator.processFile(alifile);
					outFileS.flush();
				}finally{	
					outFileS.close();
				}
				
				// get data to plot
				ArrayList<Double> coverage = new ArrayList<Double>();
				ArrayList<Integer> dataX = new ArrayList<Integer>();
				log.info("Plotting Coverage Statistics");
				File file = new File(outFile);
				if (file.exists()) {
					FileReader frOne = new FileReader(file);
					BufferedReader br = new BufferedReader(frOne);
					String lineRead ;	String arrayTmp[];
					while ((lineRead = br.readLine()) != null) {
						arrayTmp = lineRead.split("\t");
						if (!arrayTmp[0].equals("More")) {
							if (multipleAlignments) {
								coverage.add(Double.parseDouble(arrayTmp[1]));
							} else {
								coverage.add(Double.parseDouble(arrayTmp[2]));
							}
						} 
					}
					br.close();	frOne.close();
					int lastPeak = PlotUtils.getLastPeak(coverage);
					coverage.subList(lastPeak*2, coverage.size()).clear();
					for (int i = 0; i < coverage.size(); i++)  dataX.add(i) ; 
					
					log.info("Drawing chart to "+ outputFile + ".png");
					Chart chart = PlotUtils.createBarChart("Coverage Statistics", "Coverage", "Number of reference calls");
					PlotUtils.addSample(alignments, chart, dataX, coverage);
					PlotUtils.manageLegend(chart, 1);
					PlotUtils.saveChartPNG(chart, outputFile);
				}
				log.info("Finalized Statistics!!!!");
				monitor.done();
			} catch (Exception e) {
				log.info("Error executing Coverage statistics: ");
				String message = LoggingHelper.serializeException(e);
				log.severe(message);
			} finally {
				LoggingHelper.closeLogger(log);
				if (outFileS != null) {
					outFileS.flush();
					outFileS.close();
				}
			}
			return Status.OK_STATUS;
		}
	};

	public void runJob() {
		job.schedule();
	}

	public CoverageStatisticsCalculator getCoverageCalculator() {
		return coverageCalculator;
	}

	public void setCoverageCalculator(CoverageStatisticsCalculator coverageCalculator) {
		this.coverageCalculator = coverageCalculator;
		this.coverageCalculator.setProgressNotifier(new DefaultProgressNotifier(progressMonitor));
/*		this.coverageCalculator.setProgressNotifier(new ProgressNotifier() {
			@Override
			public boolean keepRunning(int progress) {
				if (progress > lastProgress) {
					int advance = progress - lastProgress;
					progressMonitor.worked(advance);
					lastProgress = progress;
				}
				return !progressMonitor.isCanceled();
			}
		});*/
	}

	public String getAlifile() {
		return alifile;
	}

	public void setAlifile(String alifile) {
		this.alifile = alifile;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public boolean isMultipleAlignments() {
		return multipleAlignments;
	}

	public void setMultipleAlignments(boolean multipleAlignments) {
		this.multipleAlignments = multipleAlignments;
	}

	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

	public FileHandler getLogFile() {
		return logFile;
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
}
