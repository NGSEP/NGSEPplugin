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
package net.sf.ngsep.view;

import java.io.IOException;
import java.util.ArrayList;

import net.sf.ngsep.utilities.PlotUtils;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.alignments.BasePairQualityStatisticsCalculator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.xeiam.xchart.Chart;

/**
 * 
 * @author Juan Camilo Quintero
 *
 */
public class MainPlotQualityStatistics {
	protected Shell shell;
	private Text txtFile;
	private Text txtOutputText;
	private Label lblFile;
	private Button btnFile;
	private Label lbloutputFile;
	private Button btnOutputText;
	private Label lblGraphicaloutput;
	private Button btnUniqueAlignments;
	private Button btnPlotQualityStatistics;
	private Button btnCancel;
	private String aliFile;
	private Display display;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Draws all the contents on the window, suggests a name for the output image
	 */
	protected void createContents() {
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setSize(833, 272);
		shell.setText("Plot Quality Statistics");
		shell.setLocation(150, 200);
		
		MouseListenerNgsep mouse = new MouseListenerNgsep();
		Font tfont = new Font(Display.getCurrent(), "Arial", 10, SWT.BOLD);

		lblFile = new Label(shell, SWT.NONE);
		lblFile.setBounds(10, 10, 167, 21);
		lblFile.setText("(*)File:");

		txtFile = new Text(shell, SWT.BORDER);
		txtFile.setBounds(205, 10, 545, 21);
		txtFile.addMouseListener(mouse);
		
		btnFile = new Button(shell, SWT.NONE);
		btnFile.setBounds(761, 10, 21, 25);
		btnFile.setText("...");
		btnFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, aliFile,txtFile);
			}
		});

		lbloutputFile = new Label(shell, SWT.NONE);
		lbloutputFile.setBounds(10, 51, 167, 21);
		lbloutputFile.setText("(*)Output File:");

		txtOutputText = new Text(shell, SWT.BORDER);
		txtOutputText.setBounds(205, 51, 545, 21);
		txtOutputText.addMouseListener(mouse);
		if (aliFile != null && aliFile.length()>0) {
			txtFile.setText(aliFile);
			String srtOutPutFileOne = aliFile.substring(0,aliFile.lastIndexOf("."));
			txtOutputText.setText(srtOutPutFileOne);
		}
		
		btnOutputText = new Button(shell, SWT.NONE);
		btnOutputText.setBounds(761, 51, 21, 25);
		btnOutputText.setText("...");
		btnOutputText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, aliFile,txtOutputText);
			}
		});

		lblGraphicaloutput = new Label(shell, SWT.NONE);
		lblGraphicaloutput.setBounds(10, 107, 167, 21);
		lblGraphicaloutput.setText("Graphical Output");
		lblGraphicaloutput.setFont(tfont);

		Button btnMultiplealignments = new Button(shell, SWT.RADIO);
		btnMultiplealignments.setBounds(10, 139, 167, 21);
		btnMultiplealignments.setText("Multiple alignments");

		btnUniqueAlignments = new Button(shell, SWT.RADIO);
		btnUniqueAlignments.setBounds(180, 139, 167, 21);
		btnUniqueAlignments.setText("Unique alignments");
		btnUniqueAlignments.setSelection(true);
		
		btnPlotQualityStatistics = new Button(shell,SWT.NONE);
		btnPlotQualityStatistics.setBounds(205, 200, 110, 25);
		btnPlotQualityStatistics.setText("Plot");
		btnPlotQualityStatistics.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
					proceed();
			}
		});

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(336, 200, 110, 25);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
	}

	/**
	 * Main execution of the Class, it checks if the user introduced correctly the files,
	 * takes the information from the ReadPos.stats file and stores it in ArrayLists,
	 * and finally creates and saves histograms  
	 */
	public void proceed() {
		// Check for empty fields
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		ArrayList<String> errors = new ArrayList<String>();
		if (txtFile.getText() == null || txtFile.getText().equals("")) {
			errors.add(FieldValidator.buildMessage(lblFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtFile.setBackground(oc);
		}
		if (txtOutputText.getText() == null|| txtOutputText.getText().equals("")) {
			errors.add(FieldValidator.buildMessage(lbloutputFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutputText.setBackground(oc);
		}
		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shell,"Plot Quality Statistics Missing");
			return;
		}
				
		// obtain the information for plotting
		boolean uniqueAlignments = btnUniqueAlignments.getSelection();
		String alignmentsText = uniqueAlignments ? "Unique Alignments":"Multiple Alignments";
		ArrayList<Integer> dataX = new ArrayList<Integer>();
		ArrayList<Double> dataY = new ArrayList<Double>();
		double[] percentages;
		try {
			percentages = BasePairQualityStatisticsCalculator.calculatePercentages(txtFile.getText(), uniqueAlignments);
		} catch (IOException e) {
			MessageDialog.openError(shell,"Plot Quality Statistics Error", e.getMessage());
			e.printStackTrace();
			return;
		}
		for(int i=0;i<percentages.length;i++) {
			dataX.add(i+1);
			dataY.add(percentages[i]);
		}
		
		// plot
		try{
			Chart chart = PlotUtils.createBarChart("Quality statistics", "Read Position (5'to 3')", "Percentage of non reference calls");
			PlotUtils.addSample(alignmentsText, chart, dataX, dataY);
			PlotUtils.manageLegend(chart, 4);
			PlotUtils.saveChartPNG(chart, txtOutputText.getText());
			MessageDialog.openInformation(shell,"Plot Quality Statistics is running",LoggingHelper.MESSAGE_PROGRESS_NOBAR);
			shell.dispose();
		} catch (Exception e){
			MessageDialog.openError(shell,"Plot Quality Statistics Error", e.getMessage());
			e.printStackTrace();
			return;
		}
	}

	public String getAliFile() {
		return aliFile;
	}

	public void setAliFile(String aliFile) {
		this.aliFile = aliFile;
	}

}
