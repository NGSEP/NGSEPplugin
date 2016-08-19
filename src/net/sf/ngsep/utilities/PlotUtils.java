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
package net.sf.ngsep.utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.BitmapEncoder.BitmapFormat;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.SwingWrapper;
import com.xeiam.xchart.StyleManager.ChartType;
import com.xeiam.xchart.StyleManager.LegendPosition;
import com.xeiam.xchart.VectorGraphicsEncoder;
import com.xeiam.xchart.VectorGraphicsEncoder.VectorGraphicsFormat;

/**
 * Utilities to create bar plots, show them and save them to files
 * @author Juan Fernando De la Hoz
 */
public class PlotUtils {
	
	/**
	 * Creates a Chart object as a bar chart
	 * @param name Title
	 * @param axisX String, axis for the X label
	 * @param axisY String, axis for the Y label
	 * @return Chart, graphical object
	 */
	public static Chart createBarChart(String name, String axisX, String axisY){
		Chart chart = new ChartBuilder().chartType(ChartType.Bar).width(800).height(800).title(name).xAxisTitle(axisX).yAxisTitle(axisY).build();
		return chart;
	}
	
	/**
	 * Creates a Chart object as a line plot
	 * @param name Title
	 * @param axisX String, axis for the X label
	 * @param axisY String, axis for the Y label
	 * @return Chart, graphical object
	 */
	public static Chart createLineChart(String name, String axisX, String axisY){
		Chart chart = new ChartBuilder().chartType(ChartType.Line).width(800).height(800).title(name).xAxisTitle(axisX).yAxisTitle(axisY).build();
		chart.getStyleManager().setYAxisMin(0);
		return chart;
	}
	
	/**
	 * Creates a Chart object as a points (scatter) chart
	 * @param name Title
	 * @param axisX String, axis for the X label
	 * @param axisY String, axis for the Y label
	 * @return Chart, graphical object
	 */
	public static Chart createPointsChart(String name, String axisX, String axisY){
		Chart chart = new ChartBuilder().chartType(ChartType.Scatter).width(800).height(800).title(name).xAxisTitle(axisX).yAxisTitle(axisY).build();
		return chart;
	}
	
	/**
	 * Adds another set of data to a chart
	 * @param name String, name of the series
	 * @param chart Object
	 * @param dataX Collection<?>, data for the x axis
	 * @param dataY Collection<? extends Number>, data for the y axis
	 * @return Chart graphical Object
	 */
	public static Chart addSample (String name, Chart chart, Collection<?> dataX, Collection<? extends Number> dataY){
		chart.addSeries(name, dataX, dataY);
		return chart;
	}
	
	/**
	 * Adds or removes the legend box with the series information in a chart. 
	 * @param chart Chart object
	 * @param postion Int, 1-4 to locate legend in corners or the quadrants of the Cartesian coordinate system, 5 outside right, 0 for no legend
	 * @return Chart
	 */
	public static Chart manageLegend(Chart chart, int position){
		// Customize Chart
		if (position == 0){
			chart.getStyleManager().setLegendVisible(false);
		} else if(position == 1){
			chart.getStyleManager().setLegendPosition(LegendPosition.InsideNE);
		} else if(position == 2){
			chart.getStyleManager().setLegendPosition(LegendPosition.InsideNW);
		} else if(position == 3){
			chart.getStyleManager().setLegendPosition(LegendPosition.InsideSW);
		} else if(position == 4){
			chart.getStyleManager().setLegendPosition(LegendPosition.InsideSE);
		} else if(position == 5){
			chart.getStyleManager().setLegendPosition(LegendPosition.OutsideE);
		}
		return chart;
	}
	
	/**
	 * Overlaps the bars of a chart
	 * @param chart	Chart object
	 */
	public static Chart overlapBars(Chart chart){
		chart.getStyleManager().setBarsOverlapped(true);
		return chart;
	}
	
	/**
	 * Saves the chart as PNG or JPG
	 * @param chart Object
	 * @param outputFile String name of the file, String
	 * @post the bar chart is saved to a file
	 */
	public static void saveChartPNG (Chart chart, String outputFile) throws IOException {
		BitmapEncoder.saveBitmapWithDPI(chart, outputFile, BitmapFormat.PNG, 300);
	}
	public static void saveChartJPG (Chart chart, String outputFile) throws IOException {
	    BitmapEncoder.saveBitmapWithDPI(chart, outputFile, BitmapFormat.JPG, 300);
	}
	public static void saveChartPDF (Chart chart, String outputFile) throws IOException {
		//not really working
		VectorGraphicsEncoder.saveVectorGraphic(chart, outputFile, VectorGraphicsFormat.PDF);
	}
	
	/**
	 * Shows the chart in a new window, ISSUE: if the new window is closed, the whole program is closed too
	 * @param Chart object
	 * @post the chart is shown in a new window
	 */
	public static void showChart (Chart chart){
		// Show it
		new SwingWrapper(chart).displayChart();
	}
	
	/**
	 * calculates the last peak of a histogram (as a bar plot) that equals the average of all peaks
	 * @param ArrayList with height of bars
	 * @return position of the peak in the ArrayList, Integer
	 */
	public static int getLastPeak(ArrayList<? extends Number> ydata) {
		double avgY = 0;
		int n = ydata.size();
		for (int i = 0; i < n; i++) {
			avgY += ydata.get(i).doubleValue();
		}
		avgY /= n;
		for (int i = n - 1; i >= 0; i--) {
			if (ydata.get(i).doubleValue() > avgY)
				return i;
		}
		return n / 2;
	}
}
