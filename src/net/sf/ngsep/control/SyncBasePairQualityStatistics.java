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
 * @author Juan Camilo Quintero, Daniel Cruz, Juan Fernando de la Hoz
 *
 */
public class SyncBasePairQualityStatistics extends Job{

	private BasePairQualityStatisticsCalculator positStatsCalculator;
	private String outputPrefx;
	private String aliFile;
	private boolean uniqueAlignments;
	
	private String logName;
	private String nameProgressBar;
	
	public SyncBasePairQualityStatistics(String name) {
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
			monitor.beginTask(getNameProgressBar(), 5000);
			log.info("Started Quality Statistics Calculator");
			String outputFile = outputPrefx + ".stats";
			log.info("Output File:" + outputFile);
			positStatsCalculator.setLog(log);
			positStatsCalculator.setProgressNotifier(new DefaultProgressNotifier(monitor));
			
			log.info("Processing reads from file: "+aliFile);
			positStatsCalculator.processFile(aliFile);
			log.info("Printing results to " + outputFile);
			out = new PrintStream(outputFile);
			positStatsCalculator.printStatistics(out);
			out.flush();
			// get data to plot
			ArrayList<Double> dataY = new ArrayList<Double>();
			ArrayList<Integer> dataX = new ArrayList<Integer>();
			String alignmentsText;
			if (uniqueAlignments) {
				log.info("Unique alignments selected");
				alignmentsText = "Unique Alignments";
			} else {
				log.info("Multiple alignments selected");
				alignmentsText = "Multiple Alignments";
			}
			double [] percentages = positStatsCalculator.calculatePercentages(uniqueAlignments);
			for(int i=0;i<percentages.length;i++) {
				dataX.add(i+1);
				dataY.add(percentages[i]);
			}
			
			log.info("Drawing chart to "+ outputPrefx + ".png");
			Chart chart = PlotUtils.createBarChart("Quality statistics", "Read Position (5'to 3')", "Percentage of non reference calls");
			PlotUtils.addSample(alignmentsText, chart, dataX, dataY);
			PlotUtils.manageLegend(chart, 4);
			PlotUtils.saveChartPNG(chart, outputPrefx);
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

	public BasePairQualityStatisticsCalculator getPositStatsCalculator() {
		return positStatsCalculator;
	}

	public void setPositStatsCalculator(BasePairQualityStatisticsCalculator positStatsCalculator) {
		this.positStatsCalculator = positStatsCalculator;
	}

	public String getAliFile() {
		return aliFile;
	}

	public void setAliFile(String aliFile) {
		this.aliFile = aliFile;
	}

	public String getOutputFile() {
		return outputPrefx;
	}

	public void setOutputFile(String outputFile) {
		this.outputPrefx = outputFile;
	}
	
	public boolean isUniqueAlignments() {
		return uniqueAlignments;
	}

	public void setUniqueAlignments(boolean uniqueAlignments) {
		this.uniqueAlignments = uniqueAlignments;
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
