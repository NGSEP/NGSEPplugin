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

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import net.sf.ngsep.utilities.DefaultProgressNotifier;
import net.sf.ngsep.utilities.PlotUtils;
import ngsep.alignments.BasePairQualityStatisticsCalculator;
import net.sf.ngsep.utilities.LoggingHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.xeiam.xchart.Chart;

/**
 * 
 * @author Juan Camilo Quintero
 * @author Daniel Cruz
 * @author Juan Fernando de la Hoz
 * @author Jorge Duitama
 *
 */
public class SyncBasePairQualityStatistics extends Job{

	private BasePairQualityStatisticsCalculator instance;
	
	private String alignmentsFile;
	private String statsOutputFile;
	private String plotFile;
	private boolean plotUniqueAlignments;
	
	//Attributes to set the logger
	private String logName;
	
	//Name for the progress bar
	private String nameProgressBar;
	
	public SyncBasePairQualityStatistics(String name) {
		super(name);
	}

	protected IStatus run(IProgressMonitor monitor) {
		FileHandler logFile = null;
		Logger log = null;
		PrintStream out=null;
		try {
			//Create log
			logFile = new FileHandler(logName, false);
			log = LoggingHelper.createLogger(logName, logFile);
			instance.setLog(log);
			
			monitor.beginTask(getNameProgressBar(), 5000);
			log.info("Started Quality Statistics Calculator");
			
			instance.setProgressNotifier(new DefaultProgressNotifier(monitor));
			
			
			log.info("Processing reads from file: "+alignmentsFile);
			
			instance.processFile(alignmentsFile);
			
			
			log.info("Printing counts to " + statsOutputFile);
			out = new PrintStream(statsOutputFile);
			instance.printStatistics(out);
			out.flush();
			out.close();
			log.info("Plotting image to file:" + plotFile+".png");
			
			if (plotUniqueAlignments) {
				log.info("Unique alignments selected");	
			} else {
				log.info("Multiple alignments selected");
			}
			plotQualityStatistics(statsOutputFile, plotFile, plotUniqueAlignments);
			log.info("Finalized Statistics!!!!");
			monitor.done();
		} catch (Exception e) {
			log.info("Error executing Quality statistics: ");
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

	public static void plotQualityStatistics(String statsFile, String plotFile, boolean plotUniqueAlignments) throws IOException {
		double [] percentages = BasePairQualityStatisticsCalculator.calculatePercentages(statsFile, plotUniqueAlignments);
		// get data to plot
		ArrayList<Double> dataY = new ArrayList<Double>();
		ArrayList<Integer> dataX = new ArrayList<Integer>();
		for(int i=0;i<percentages.length;i++) {
			dataX.add(i+1);
			dataY.add(percentages[i]);
		}
		
		
		Chart chart = PlotUtils.createBarChart("Quality statistics", "Read Position (5' to 3')", "Percentage of non reference calls");
		String legendText = plotUniqueAlignments ? "Unique Alignments":"Multiple Alignments";
		PlotUtils.addSample(legendText, chart, dataX, dataY);
		PlotUtils.manageLegend(chart, 4);
		PlotUtils.saveChartPNG(chart, plotFile);
	}

	public BasePairQualityStatisticsCalculator getInstance() {
		return instance;
	}

	public void setInstance(BasePairQualityStatisticsCalculator instance) {
		this.instance = instance;
	}

	public String getAlignmentsFile() {
		return alignmentsFile;
	}

	public void setAlignmentsFile(String alignmentsFile) {
		this.alignmentsFile = alignmentsFile;
	}

	public String getStatsOutputFile() {
		return statsOutputFile;
	}

	public void setStatsOutputFile(String statsOutputFile) {
		this.statsOutputFile = statsOutputFile;
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
