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
import java.io.IOException;
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

import net.sf.ngsep.control.SyncAlleleSharingStatistics;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.transcriptome.Transcriptome;
import ngsep.vcf.AlleleSharingStatsCalculator;

/**
 * 
 * @author Jorge Duitama
 *
 */
public class MainAlleleSharingStatistics {
	//General attributes
	protected Shell shell;
	private Display display;
	
	//File selected initially by the user
	private String selectedFile;
	
	//Action buttons
	private Button btnSubmit;
	private Button btnCancel;
	//---------------------------------------

	//Main arguments
	private Label lblVCFFile;
	private Text txtVCFFile;
	private Button btnBrowseVCFFile;
	private Label lblPopFile;
	private Text txtPopFile;
	private Button btnBrowsePopFile;
	private Label lblTranscriptomeFile;
	private Text txtTranscriptomeFile;
	private Button btnBrowseTranscriptomeFile;
	private Label lblOutput;
	private Text txtOutput;
	private Button btnBrowseOutput;
	
	
	private Label lblGroup1;
	private Text txtGroup1;
	private Label lblGroup2;
	private Text txtGroup2;
	
	private Label lblWinSize;
	private Text txtWinSize;
	private Label lblStepSize;
	private Text txtStepSize;
	
	private Button btnIncludeIntrons;
	
	public String getSelectedFile() {
		return selectedFile;
	}
	public void setSelectedFile(String selectedFile) {
		this.selectedFile = selectedFile;
	}
	
	/**
	 * Open the window
	 */
	public void open(){
		display = Display.getDefault();
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("Allele Sharing Statistics Calculator");
		shell.setLocation(150, 200);
		shell.setSize(900, 500);
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

		//VCF file
		lblVCFFile = new Label (shell, SWT.NONE);
		lblVCFFile.setBounds(20, 40, 170, 30);
		lblVCFFile.setText("(*)Input VCF File:");
		
		txtVCFFile = new Text (shell, SWT.BORDER);
		txtVCFFile.setBounds(200, 40, 600, 22);
		txtVCFFile.addMouseListener(mouse);
		if (selectedFile != null && !selectedFile.equals("")) {
			txtVCFFile.setText(selectedFile);
		}
				
		btnBrowseVCFFile = new Button (shell, SWT.NONE);
		btnBrowseVCFFile.setBounds(830, 40, 25, 25);
		btnBrowseVCFFile.setText("...");
		btnBrowseVCFFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtVCFFile);
			}
		});
		
		//Population file
		lblPopFile = new Label (shell, SWT.NONE);
		lblPopFile.setBounds(20, 90, 170, 30);
		lblPopFile.setText("(*)Population File:");
		
		txtPopFile = new Text (shell, SWT.BORDER);
		txtPopFile.setBounds(200, 90, 600, 22);
		txtPopFile.addMouseListener(mouse);
				
		btnBrowsePopFile = new Button (shell, SWT.NONE);
		btnBrowsePopFile.setBounds(830, 90, 25, 25);
		btnBrowsePopFile.setText("...");
		btnBrowsePopFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtPopFile);
			}
		});
		
		//Transcriptome file
		lblTranscriptomeFile = new Label (shell, SWT.NONE);
		lblTranscriptomeFile.setBounds(20, 140, 170, 30);
		lblTranscriptomeFile.setText("Transcriptome File (.gff):");
		
		txtTranscriptomeFile = new Text (shell, SWT.BORDER);
		txtTranscriptomeFile.setBounds(200, 140, 600, 22);
		txtTranscriptomeFile.addMouseListener(mouse);
				
		btnBrowseTranscriptomeFile = new Button (shell, SWT.NONE);
		btnBrowseTranscriptomeFile.setBounds(830, 140, 25, 25);
		btnBrowseTranscriptomeFile.setText("...");
		btnBrowseTranscriptomeFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtTranscriptomeFile);
			}
		});
		
		lblOutput = new Label (shell, SWT.NONE);
		lblOutput.setBounds(20, 190, 170, 30);
		lblOutput.setText("(*)Output File:");
		
		txtOutput = new Text (shell, SWT.BORDER);
		txtOutput.setBounds(200, 190, 600, 22);
		txtOutput.addMouseListener(mouse);
		//Suggest name for the output file
		if (txtVCFFile.getText() != null && !txtVCFFile.getText().equals("")) {
			String srtOutPutFileOne = selectedFile;
			if (srtOutPutFileOne.contains(".")) {
				srtOutPutFileOne = srtOutPutFileOne.substring(0,srtOutPutFileOne.lastIndexOf("."));
			} else {
				srtOutPutFileOne = srtOutPutFileOne.substring(0,srtOutPutFileOne.length());
			}
			txtOutput.setText(srtOutPutFileOne + "_AlleleSharingStats.txt");
		}
		btnBrowseOutput = new Button (shell, SWT.NONE);
		btnBrowseOutput.setBounds(830, 190, 25, 25);
		btnBrowseOutput.setText("...");
		btnBrowseOutput.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, selectedFile, txtOutput);
			}
		});
		
		lblGroup1= new Label(shell, SWT.NONE);
		lblGroup1.setText("(*)Group 1:");
		lblGroup1.setBounds(20, 240, 110, 25);
		
		txtGroup1 = new Text(shell, SWT.BORDER);
		txtGroup1.setBounds(140, 240, 150, 25);
		txtGroup1.addMouseListener(mouse);
		
		lblGroup2= new Label(shell, SWT.NONE);
		lblGroup2.setText("(*)Group 2:");
		lblGroup2.setBounds(300, 240, 110, 25);
		
		txtGroup2 = new Text(shell, SWT.BORDER);
		txtGroup2.setBounds(420, 240, 150, 25);
		txtGroup2.addMouseListener(mouse);
		
		lblWinSize = new Label(shell, SWT.NONE);
		lblWinSize.setText("Window size:");
		lblWinSize.setBounds(20, 290, 110, 25);
		
		txtWinSize = new Text(shell, SWT.BORDER);
		txtWinSize.setBounds(140, 290, 150, 25);
		txtWinSize.addMouseListener(mouse);
		
		lblStepSize = new Label(shell, SWT.NONE);
		lblStepSize.setText("Step size:");
		lblStepSize.setBounds(300, 290, 110, 25);
		
		txtStepSize = new Text(shell, SWT.BORDER);
		txtStepSize.setBounds(420, 290, 150, 25);
		txtStepSize.addMouseListener(mouse);
		
		btnIncludeIntrons = new Button(shell, SWT.CHECK);
		btnIncludeIntrons.setBounds(20, 340, 150, 25);
		btnIncludeIntrons.setText("Include introns");
		
		//buttons on the bottom
		btnSubmit = new Button(shell, SWT.NONE);
		btnSubmit.setBounds(250, 400, 200, 25);
		btnSubmit.setText("Allele sharing statistics");
		btnSubmit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();
			}
		});	
		
		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(500, 400, 150, 25);
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
		
		//Validate fields and record errors in the list
		ArrayList<String> listErrors = new ArrayList<String>();
		AlleleSharingStatsCalculator calculator = new AlleleSharingStatsCalculator();
		if (txtVCFFile.getText() == null || txtVCFFile.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblVCFFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtVCFFile.setBackground(oc);
		}
		if (txtPopFile.getText() == null || txtPopFile.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblPopFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtPopFile.setBackground(oc);
		}
		if (txtOutput.getText() == null || txtOutput.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblOutput.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutput.setBackground(oc);
		}
		if (txtGroup1.getText() == null || txtGroup1.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblGroup1.getText(), FieldValidator.ERROR_MANDATORY));
			txtGroup1.setBackground(oc);
		}
		if (txtGroup2.getText() == null || txtGroup2.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblGroup2.getText(), FieldValidator.ERROR_MANDATORY));
			txtGroup2.setBackground(oc);
		}
		
		if (txtWinSize.getText() != null && txtWinSize.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtWinSize.getText(),new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblWinSize.getText(), FieldValidator.ERROR_INTEGER));
				txtWinSize.setBackground(oc);
			} else {
				calculator.setWindowSize(Integer.parseInt(txtWinSize.getText()));
			}
		}
		if (txtStepSize.getText() != null && txtStepSize.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtStepSize.getText(),new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblStepSize.getText(), FieldValidator.ERROR_INTEGER));
				txtStepSize.setBackground(oc);
			} else {
				calculator.setStepSize(Integer.parseInt(txtStepSize.getText()));
			}
		}
		if (txtTranscriptomeFile.getText() != null && txtTranscriptomeFile.getText().length()>0) {
			try {
				Transcriptome t = ReferenceGenomesFactory.getInstance().getTranscriptome(txtTranscriptomeFile.getText(), shell);
				calculator.setTranscriptome(t);
			} catch (IOException e) {
				e.printStackTrace();
				listErrors.add(FieldValidator.buildMessage(lblTranscriptomeFile.getText(),"Error loading transcriptome: "+e.getMessage()));
				txtTranscriptomeFile.setBackground(oc);
			}
		}
		calculator.setIncludeIntrons(btnIncludeIntrons.getSelection());
		
		
		
		if (listErrors.size() > 0) {
			FieldValidator.paintErrors(listErrors, shell, "Allele Sharing Statistics Calculator");
			return;
		}
		//Create the job and give the instance of the model with the parameters set
		SyncAlleleSharingStatistics job = new SyncAlleleSharingStatistics("AlleleSharingStatistics");
		job.setCalculator(calculator);
		job.setVcfFile(txtVCFFile.getText());
		job.setPopulationFile(txtPopFile.getText());
		String outputFile = txtOutput.getText(); 
		job.setOutFile(outputFile);
		job.setGroups1(txtGroup1.getText().split(","));
		job.setGroups2(txtGroup2.getText().split(","));

		
		//Manage the logger and the progress bar
		String logFilename = LoggingHelper.getLoggerFilename(outputFile,"AS");
		job.setLogName(logFilename);
		job.setNameProgressBar(new File(outputFile).getName());
		try {
			job.schedule();
			MessageDialog.openInformation(shell, "Allele Sharing Statistics Calculator is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
			shell.dispose();
		} catch (Exception e) {
			MessageDialog.openError(shell, "Allele Sharing Statistics Calculator Error",e.getMessage());
			e.printStackTrace();
			return;
		}
	}
}
