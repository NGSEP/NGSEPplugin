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

import net.sf.ngsep.control.SyncVCFSummaryStatistics;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.vcf.VCFSummaryStatisticsCalculator;

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

/**
 * Main window for the VCFSummaryStatisticsCalculator menu
 * @author Juan Fernando De la Hoz, Jorge Duitama
 *
 */
public class MainVCFSummaryStatistics implements SingleFileInputWindow {
	protected Shell shell;
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
	private Button btnSummaryStatistics;
	private Button btnCancel;
	//---------------------------------------

	//Main arguments
	private Label lblFile;
	private Text txtFile;
	private Button btnBrowseFile;
	private Label lblOutput;
	private Text txtOutput;
	private Button btnBrowseOutput;
	private Label lblMinSamplesGenotyped;
	private Text txtMinSamplesGenotyped;
	//---------------------------------------
	
	/**
	 * Open the window
	 */
	public void open(){
		display = Display.getDefault();
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("VCF Summary Statistics Calculator");
		shell.setLocation(150, 200);
		shell.setSize(900, 300);
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
		lblFile.setText("(*)Input VCF File:");
		
		txtFile = new Text (shell, SWT.BORDER);
		txtFile.setBounds(200, 40, 600, 22);
		txtFile.addMouseListener(mouse);
		if (selectedFile != null && !selectedFile.equals("")) {
			txtFile.setText(selectedFile);
		}
				
		btnBrowseFile = new Button (shell, SWT.NONE);
		btnBrowseFile.setBounds(830, 40, 25, 25);
		btnBrowseFile.setText("...");
		btnBrowseFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtFile);
			}
		});
		
		lblOutput = new Label (shell, SWT.NONE);
		lblOutput.setBounds(20, 90, 170, 30);
		lblOutput.setText("(*)Output File:");
		
		txtOutput = new Text (shell, SWT.BORDER);
		txtOutput.setBounds(200, 90, 600, 22);
		txtOutput.addMouseListener(mouse);
		String suggestedOutprefix = SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile);
		//Suggest name for the output file
		txtOutput.setText(suggestedOutprefix + "_SummaryStats.txt");
		
		btnBrowseOutput = new Button (shell, SWT.NONE);
		btnBrowseOutput.setBounds(830, 90, 25, 25);
		btnBrowseOutput.setText("...");
		btnBrowseOutput.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, selectedFile, txtOutput);
			}
		});
		
		//Samples Genotyped
		lblMinSamplesGenotyped = new Label (shell, SWT.NONE);
		lblMinSamplesGenotyped.setBounds(180, 150, 250, 30);
		lblMinSamplesGenotyped.setText("Min number of samples genotyped:");
		
		txtMinSamplesGenotyped = new Text (shell, SWT.BORDER);
		txtMinSamplesGenotyped.setBounds(450, 150, 50, 22);
		txtMinSamplesGenotyped.addMouseListener(mouse);
		txtMinSamplesGenotyped.setText("20");
		
		//buttons on the bottom
		btnSummaryStatistics = new Button(shell, SWT.NONE);
		btnSummaryStatistics.setBounds(270, 200, 170, 25);
		btnSummaryStatistics.setText("Summary Statistics");
		btnSummaryStatistics.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();
			}
		});	
		
		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(500, 200, 130, 25);
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
		VCFSummaryStatisticsCalculator instance = new VCFSummaryStatisticsCalculator();
		
		//Validate fields and record errors in the list
		ArrayList<String> listErrors = new ArrayList<String>();
		
		if (txtFile.getText() == null || txtFile.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtFile.setBackground(oc);
		}
		if (txtOutput.getText() == null|| txtOutput.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblOutput.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutput.setBackground(oc);
		}
		if (txtMinSamplesGenotyped.getText() != null && txtMinSamplesGenotyped.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMinSamplesGenotyped.getText(),new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblMinSamplesGenotyped.getText(), FieldValidator.ERROR_INTEGER));
				txtMinSamplesGenotyped.setBackground(oc);
			} else {
				instance.setMinSamplesGenotyped(Integer.parseInt(txtMinSamplesGenotyped.getText()));
			}
		}
		if (listErrors.size() > 0) {
			FieldValidator.paintErrors(listErrors, shell, "VCF Summary Statistics Calculator");
			return;
		}

		//Create the job and give the instance of the model with the parameters set
		SyncVCFSummaryStatistics syncVCFStata = new SyncVCFSummaryStatistics("VCF Summary Statistics Calculator");
		String outputFile = txtOutput.getText();
		syncVCFStata.setInputFile(txtFile.getText());
		syncVCFStata.setOutputFile(outputFile);
		syncVCFStata.setInstance(instance);
		
		//Manage the logger and the progress bar
		String logFilename = LoggingHelper.getLoggerFilename(outputFile,"ST");
		syncVCFStata.setLogName(logFilename);
		syncVCFStata.setNameProgressBar(new File(outputFile).getName());
		try {
			syncVCFStata.schedule();
			MessageDialog.openInformation(shell, "VCF Summary Statistics Calculator is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
			shell.dispose();
		} catch (Exception e) {
			MessageDialog.openError(shell, "VCF Summary Statistics Calculator Error",e.getMessage());
			e.printStackTrace();
			return;
		}
	}
}