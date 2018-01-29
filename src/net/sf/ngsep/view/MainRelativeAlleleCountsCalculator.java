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

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.sf.ngsep.control.SyncRelativeAlleleCountsCalculator;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.discovery.BAMRelativeAlleleCountsCalculator;

/**
 * Main window for the RelativeAlleleCountsCalculator menu
 * @author Jorge Duitama
 *
 */
public class MainRelativeAlleleCountsCalculator implements SingleFileInputWindow  {
	
	//General attributes
	private Shell shell;
	private Display display;
	
	//File selected initially by the user
	private String selectedFile;
	public String getSelectedFile() {
		return selectedFile;
	}
	public void setSelectedFile(String selectedFile) {
		this.selectedFile = selectedFile;
	}

	//Action buttons
	private Button btnSubmit;
	private Button btnCancel;
	//---------------------------------------

	//Main arguments
	private Label lblFile;
	private Text txtFile;
	private Button btnFile;
	private Label lblOutput;
	private Text txtOutput;
	private Button btnOutput;
	
	private Label lblFilterRegions;
	private Text txtFilterRegions;
	private Button btnFilterRegions;
	
	private Label lblSelectRegions;
	private Text txtSelectRegions;
	private Button btnSelectRegions;
	private Label lblRD;
	private Label lblMinRD;
	private Text txtMinRD;
	private Label lblMaxRD;
	private Text txtMaxRD;
	private Label lblMinBQ;
	private Text txtMinBQ;
	
	private Button btnSecondaryAlns;
	//----------------------------------------
	
	// Default Parameters
	
	private static final int DEF_MIN_RD = 10;
	private static final int DEF_MAX_RD = 1000;
	private static final int DEF_MIN_BQ = 20;
	
	
	//---------------------------------------
	
	/**
	 * Open the window
	 */
	public void open(){
		display = Display.getDefault();
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("BAM Relative Allele Counts Calculator");
		shell.setLocation(150, 200);
		shell.setSize(900, 600);
		createContents();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents (){
		MouseListenerNgsep mouse = new MouseListenerNgsep();

		//Input & Output
		lblFile = new Label (shell, SWT.NONE);
		lblFile.setBounds(20, 40, 170, 30);
		lblFile.setText("(*)Input File:");
		
		txtFile = new Text (shell, SWT.BORDER);
		txtFile.setBounds(200, 40, 600, 25);
		txtFile.addMouseListener(mouse);
		txtFile.setText(selectedFile);
				
		btnFile = new Button (shell, SWT.NONE);
		btnFile.setBounds(830, 40, 25, 25);
		btnFile.setText("...");
		btnFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtFile);
			}
		});
		
		lblOutput = new Label (shell, SWT.NONE);
		lblOutput.setBounds(20, 90, 170, 30);
		lblOutput.setText("(*)Output file:");
		
		txtOutput = new Text (shell, SWT.BORDER);
		txtOutput.setBounds(200, 90, 600, 25);
		txtOutput.addMouseListener(mouse);
		//Suggest name for the output file
		txtOutput.setText(SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile)+"_relAlleleCounts.txt");
		
		btnOutput = new Button (shell, SWT.NONE);
		btnOutput.setBounds(830, 90, 25, 25);
		btnOutput.setText("...");
		btnOutput.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, selectedFile, txtOutput);
			}
		});
		
		lblFilterRegions = new Label(shell, SWT.NONE);
		lblFilterRegions.setText("Filter Regions From File:");
		lblFilterRegions.setBounds(20, 140, 170, 30);

		txtFilterRegions = new Text(shell, SWT.BORDER);
		txtFilterRegions.setBounds(200, 140, 600, 25);
		txtFilterRegions.addMouseListener(mouse);

		btnFilterRegions = new Button(shell, SWT.NONE);
		btnFilterRegions.setBounds(830, 140, 25, 25);
		btnFilterRegions.setText("...");
		btnFilterRegions.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtFilterRegions);
			}
		});
		

		lblSelectRegions = new Label(shell, SWT.NONE);
		lblSelectRegions.setText("Select Regions From File:");
		lblSelectRegions.setBounds(20, 190, 170, 30);

		txtSelectRegions = new Text(shell, SWT.BORDER);
		txtSelectRegions.setBounds(200, 190, 600, 25);

		btnSelectRegions = new Button(shell, SWT.NONE);
		btnSelectRegions.setBounds(830, 190, 25, 25);
		btnSelectRegions.setText("...");
		btnSelectRegions.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtSelectRegions);
			}
		});
		
		lblRD = new Label(shell, SWT.NONE);
		lblRD.setText("Read depth.");
		lblRD.setBounds(20, 240, 200, 30);
		
		lblMinRD = new Label(shell, SWT.NONE);
		lblMinRD.setText("Min:");
		lblMinRD.setBounds(230, 240, 40, 30);
		
		txtMinRD = new Text(shell, SWT.BORDER);
		txtMinRD.setBounds(280, 240, 50, 25);
		txtMinRD.setText(String.valueOf(DEF_MIN_RD));
		txtMinRD.addMouseListener(mouse);

		lblMaxRD = new Label(shell, SWT.NONE);
		lblMaxRD.setText("Max:");
		lblMaxRD.setBounds(350, 240, 40, 30);

		txtMaxRD = new Text(shell, SWT.BORDER);
		txtMaxRD.setBounds(400, 240, 50, 25);
		txtMaxRD.setText(String.valueOf(DEF_MAX_RD));
		txtMaxRD.addMouseListener(mouse);
		
		lblMinBQ = new Label(shell, SWT.NONE);
		lblMinBQ.setText("Min Base Quality:");
		lblMinBQ.setBounds(500, 240, 200, 30);

		txtMinBQ = new Text(shell, SWT.BORDER);
		txtMinBQ.setBounds(730, 240, 50, 25);
		txtMinBQ.setText(String.valueOf(DEF_MIN_BQ));
		txtMinBQ.addMouseListener(mouse);
		
		btnSecondaryAlns = new Button(shell, SWT.CHECK);
		btnSecondaryAlns.setText("Process Secondary Alignments");
		btnSecondaryAlns.setBounds(20, 290, 400, 30);

		
		//buttons on the bottom
		btnSubmit = new Button(shell, SWT.NONE);
		btnSubmit.setBounds(250, 400, 170, 25);
		btnSubmit.setText("Calculate counts");
		btnSubmit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();
			}
		});	
		
		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(500, 400, 130, 25);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});	
	}
	public void proceed(){
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		BAMRelativeAlleleCountsCalculator instance = new BAMRelativeAlleleCountsCalculator();
		SyncRelativeAlleleCountsCalculator job = new SyncRelativeAlleleCountsCalculator("Relative allele counts");
		job.setInstance(instance);
		
		//Validate fields and record errors in the list
		ArrayList<String> listErrors = new ArrayList<String>();
		
		if (txtFile.getText() == null || txtFile.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtFile.setBackground(oc);
		} else {
			job.setInputFile(txtFile.getText());
		}
		if (txtOutput.getText() == null || txtOutput.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblOutput.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutput.setBackground(oc);
		} else {
			job.setOutputFile(txtOutput.getText());
		}
		if (txtMinRD.getText() != null && txtMinRD.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMinRD.getText(), new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblMinRD.getText(), FieldValidator.ERROR_INTEGER));
				txtMinRD.setBackground(oc);
			} else {
				instance.setMinRD(Integer.parseInt(txtMinRD.getText()));
			}
		}

		if (txtMaxRD.getText() != null && txtMaxRD.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMaxRD.getText(), new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblMaxRD.getText(), FieldValidator.ERROR_INTEGER));
				txtMaxRD.setBackground(oc);
			} else {
				instance.setMaxRD(Integer.parseInt(txtMaxRD.getText()));
			}
		}
		
		if (txtMinBQ.getText() != null && txtMinBQ.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMinBQ.getText(),new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblMinBQ.getText(), FieldValidator.ERROR_NUMERIC));
				txtMinBQ.setBackground(oc);
			} else {
				instance.setMinBaseQualityScore(Integer.parseInt(txtMinBQ.getText()));
			}
		}
		
		if (listErrors.size() > 0) {
			FieldValidator.paintErrors(listErrors, shell, "Relative allele counts calculator");
			return;
		}
		
		if (txtFilterRegions.getText() != null && txtFilterRegions.getText().length()>0) {
			instance.setRepeatsFile(txtFilterRegions.getText());
		}
		if (txtSelectRegions.getText() != null && txtSelectRegions.getText().length()>0) {
			instance.setSelectedRegionsFile(txtSelectRegions.getText());
		}
		if (btnSecondaryAlns.getSelection()) {
			instance.setSecondaryAlns(true);
		}
		
		String outputFile = txtOutput.getText();
		String logFilename = LoggingHelper.getLoggerFilename(outputFile,"BRAC");
		job.setLogName(logFilename);
		job.setNameProgressBar(new File(outputFile).getName());
		try {
			job.schedule();
			MessageDialog.openInformation(shell, "Relative allele counts calculator is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
			shell.dispose();
		} catch (Exception e) {
			MessageDialog.openError(shell, "Relative allele counts calculator error",e.getMessage());
			e.printStackTrace();
			return;
		}
	}
	
}
