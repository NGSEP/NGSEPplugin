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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.PlotUtils;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;

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
 * @author Juan Camilo Quintero, Juan Fernando de la Hoz
 *
 */
public class MainPlotCoverageStatistics {
	protected Shell shell;
	private Text txtFile;
	private Text txtOutputFile;
	private Label lblFile;
	private Button btnFile;
	private Button btnOutputFile;
	private Label lblOutputFile;
	private Label lblGraphicalOutput;
	private Button btnMultiplealignments;
	private Button btnCoverageStatistics;
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
	private void createContents() {
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setSize(833, 257);
		shell.setText("Plot Coverage  Statistics");
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

		lblOutputFile = new Label(shell, SWT.NONE);
		lblOutputFile.setBounds(10, 51, 167, 21);
		lblOutputFile.setText("(*)Output File:");
		
		txtOutputFile = new Text(shell, SWT.BORDER);
		txtOutputFile.setBounds(205, 51, 545, 21);
		txtOutputFile.addMouseListener(mouse);
		if (aliFile != null && !aliFile.equals("")) {
			txtFile.setText(aliFile);
			String srtOutPutFileOne = aliFile.substring(0,aliFile.lastIndexOf("."));
			txtOutputFile.setText(srtOutPutFileOne);
		}
		
		btnOutputFile = new Button(shell, SWT.NONE);
		btnOutputFile.setBounds(761, 51, 21, 25);
		btnOutputFile.setText("...");
		btnOutputFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, aliFile,txtOutputFile);
			}
		});

		lblGraphicalOutput = new Label(shell, SWT.NONE);
		lblGraphicalOutput.setBounds(10, 103, 167, 21);
		lblGraphicalOutput.setText("Graphical Output");
		lblGraphicalOutput.setFont(tfont);

		btnMultiplealignments = new Button(shell, SWT.RADIO);
		btnMultiplealignments.setBounds(10, 136, 167, 21);
		btnMultiplealignments.setText("Multiple alignments");
		
		Button btnUniqueAlignments = new Button(shell, SWT.RADIO);
		btnUniqueAlignments.setBounds(180, 136, 167, 21);
		btnUniqueAlignments.setText("Unique alignments");
		btnUniqueAlignments.setSelection(true);

		btnCoverageStatistics = new Button(shell, SWT.NONE);
		btnCoverageStatistics.setBounds(205, 177, 110, 25);
		btnCoverageStatistics.setText("Plot");
		btnCoverageStatistics.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();
			}
		});

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(336, 177, 110, 25);
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
	 * takes the information from the Coverage.stats file and stores it in ArrayLists,
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
		if (txtOutputFile.getText() == null || txtOutputFile.getText().equals("")) {
			errors.add(FieldValidator.buildMessage(lblOutputFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutputFile.setBackground(oc);
		}
		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shell, "Plot Quality Statistics");
			return;
		}
		
		// obtain the information for plotting
		int col = (btnMultiplealignments.getSelection()) ? 1 : 2 ;
		String alignments = (btnMultiplealignments.getSelection()) ? "Multiple Alignments" : "Unique Alignments" ;

		ArrayList<Double> coverage = new ArrayList<Double>();
		ArrayList<Integer> xdata = new ArrayList<Integer>();
		try {
			File file = new File(txtFile.getText());
			if (file.exists()) {
				FileReader fread = new FileReader(file);
				BufferedReader bfr = new BufferedReader(fread);
				String line; String arrayLine[];
				while ((line = bfr.readLine()) != null) {
					arrayLine = line.split("\t");
					if (!arrayLine[0].equals("More")) {
						coverage.add(Double.parseDouble(arrayLine[col]));
					}
				}	
				bfr.close(); fread.close();
			}
		} catch (IOException e) {
			MessageDialog.openError(shell, "Plot Coverage Statistics Error", e.getMessage());
			e.printStackTrace();
			return;
		}
		int lastPeak = PlotUtils.getLastPeak(coverage);
		coverage.subList(lastPeak*2, coverage.size()).clear();
		for (int i = 0; i < coverage.size(); i++)  xdata.add(i);
		
		// plot
		try{
			Chart chart = PlotUtils.createBarChart("Coverage Statistics", "Coverage", "Number of reference calls");
			PlotUtils.addSample(alignments, chart, xdata, coverage);
			PlotUtils.manageLegend(chart, 1);
			PlotUtils.saveChartPNG(chart, txtOutputFile.getText());
			MessageDialog.openInformation(shell,"Plot Coverage Statistics is running",LoggingHelper.MESSAGE_PROGRESS_NOBAR);
			shell.dispose();
		} catch (Exception e) {
			MessageDialog.openError(shell, "Plot Coverage Statistics Error", e.getMessage());
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
