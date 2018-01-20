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
 * @author Daniel Cruz
 * @author Juan Camilo Quintero
 * @author Juan Fernando de la Hoz
 * @author Jorge Duitama
 *
 */
public class SyncCoverageStatisticsCalculator extends Job {
	
	private CoverageStatisticsCalculator instance;
	
	private String alignmentsFile;
	private String plotFile;
	private boolean plotUniqueAlignments;
	
	//Attributes to set the logger
	private String logName;
	
	//Name for the progress bar
	private String nameProgressBar;
	

	/**
	 * Creates a CoverageStatisticsCalculator job with the given name
	 * @param name Name of the job
	 */
	public SyncCoverageStatisticsCalculator(String name) {
		super(name);
	}
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		FileHandler logFile = null;
		Logger log = null;
		try {
			//Create log
			logFile = new FileHandler(logName, false);
			log = LoggingHelper.createLogger(logName, logFile);
			instance.setLog(log);
			
			monitor.beginTask(getNameProgressBar(), 100);
			log.info("Started Coverage Statistics Calculator");
			
			instance.setProgressNotifier(new DefaultProgressNotifier(monitor));
			log.info("Processing reads from file: " + alignmentsFile);
			log.info("Output statistics:" + instance.getOutFilename());
			instance.processFile(alignmentsFile);
			log.info("Plotting image to file:" + plotFile+".png");
			if (plotUniqueAlignments) {
				log.info("Unique alignments selected");
			} else {
				log.info("Multiple alignments selected");
			}
			
			plotStatistics(instance.getOutFilename(), plotFile, plotUniqueAlignments);
			log.info("Finalized Statistics!!!!");
			monitor.done();
		} catch (Exception e) {
			log.info("Error executing Coverage statistics: ");
			String message = LoggingHelper.serializeException(e);
			log.severe(message);
		} finally {
			LoggingHelper.closeLogger(log);
		}
		return Status.OK_STATUS;
	}
	
	public static void plotStatistics(String statsFile, String plotFile, boolean uniqueAlignments) throws IOException {
		int col = (uniqueAlignments) ? 2 : 1 ;
		String legendText = uniqueAlignments ? "Unique Alignments":"Multiple Alignments" ;

		ArrayList<Double> coverage = new ArrayList<Double>();
		ArrayList<Integer> xdata = new ArrayList<Integer>();
		File file = new File(statsFile);
		if (file.exists()) {
			try (FileReader fread = new FileReader(file);
				BufferedReader bfr = new BufferedReader(fread)) {
				String line;
				while ((line = bfr.readLine()) != null) {
					String [] arrayLine = line.split("\t");
					if (arrayLine.length==3) {
						coverage.add(Double.parseDouble(arrayLine[col]));
					}
				}
			}
		}
		int lastPeak = PlotUtils.getLastPeak(coverage);
		coverage.subList(lastPeak*2, coverage.size()).clear();
		for (int i = 0; i < coverage.size(); i++)  xdata.add(i);
		Chart chart = PlotUtils.createBarChart("Coverage Statistics", "Coverage", "Number of reference calls");
		PlotUtils.addSample(legendText, chart, xdata, coverage);
		PlotUtils.manageLegend(chart, 1);
		PlotUtils.saveChartPNG(chart, plotFile);
	}
	
	public CoverageStatisticsCalculator getInstance() {
		return instance;
	}
	public void setInstance(CoverageStatisticsCalculator instance) {
		this.instance = instance;
	}
	
	public String getAlignmentsFile() {
		return alignmentsFile;
	}
	public void setAlignmentsFile(String alignmentsFile) {
		this.alignmentsFile = alignmentsFile;
	}
	
	public String getPlotFile() {
		return plotFile;
	}
	public void setPlotFile(String plotFile) {
		this.plotFile = plotFile;
	}
	public boolean isPlotUniqueAlignments() {
		return plotUniqueAlignments;
	}
	public void setPlotUniqueAlignments(boolean plotUniqueAlignments) {
		this.plotUniqueAlignments = plotUniqueAlignments;
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
}
