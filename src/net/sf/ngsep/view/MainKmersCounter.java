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

import net.sf.ngsep.control.SyncKmersCounter;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.sequences.KmersCounter;

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
 * Main window for the KmersCounter menu
 * @author Cristian Loaiza
 *
 */
public class MainKmersCounter implements SingleFileInputWindow {
	//General attributes
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
	private Label lblKmerSize;
	private Text txtKmerSize;
	private Button btnBothStrands;
	private Button btnIsFasta;
	
	
	
	
	//---------------------------------------
	
	/**
	 * Open the window
	 */
	public void open(){
		display = Display.getDefault();
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("Kmers Counter");
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
		lblFile.setText("(*)Input Fastq/Fasta File:");
		
		txtFile = new Text (shell, SWT.BORDER);
		txtFile.setBounds(200, 40, 600, 25);
		txtFile.addMouseListener(mouse);
		if (selectedFile != null && !selectedFile.equals("")) {
			txtFile.setText(selectedFile);
		}
				
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
		lblOutput.setBounds(20, 140, 170, 30);
		lblOutput.setText("(*)Output file:");
		
		txtOutput = new Text (shell, SWT.BORDER);
		txtOutput.setBounds(200, 140, 600, 22);
		txtOutput.addMouseListener(mouse);
		//Suggest name for the output file
		txtOutput.setText(SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile)+"_kmers.txt");
		
		btnOutput = new Button (shell, SWT.NONE);
		btnOutput.setBounds(830, 140, 25, 25);
		btnOutput.setText("...");
		btnOutput.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, selectedFile, txtOutput);
			}
		});
		
		//Mer size
		lblKmerSize = new Label (shell, SWT.NONE);
		lblKmerSize.setBounds(20, 200, 170, 30);
		lblKmerSize.setText("(*)Mer size:");
		
		txtKmerSize = new Text (shell, SWT.BORDER);
		txtKmerSize.setBounds(200, 200, 50, 22);
		txtKmerSize.addMouseListener(mouse);
		txtKmerSize.setText(""+KmersCounter.DEFAULT_KMER_SIZE);
		
		//Calculate both strands
		
		btnBothStrands = new Button (shell, SWT.CHECK);
		btnBothStrands.setBounds(20, 250, 220, 22);
		btnBothStrands.setText("Calculate both strands");
		
		//Is fasta
		
		btnIsFasta = new Button (shell, SWT.CHECK);
		btnIsFasta.setBounds(240, 250, 170, 22);
		btnIsFasta.setText("Fasta");
		
		//buttons on the bottom
		btnSubmit = new Button(shell, SWT.NONE);
		btnSubmit.setBounds(250, 400, 170, 25);
		btnSubmit.setText("K-mers Counter");
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
		KmersCounter instance = new KmersCounter();
		
		//Validate fields and record errors in the list
		ArrayList<String> listErrors = new ArrayList<String>();
		
		if (txtFile.getText() == null || txtFile.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtFile.setBackground(oc);
		}
		
		if (txtKmerSize.getText() == null || txtKmerSize.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblKmerSize.getText(), FieldValidator.ERROR_MANDATORY));
			txtKmerSize.setBackground(oc);
		} else if (!FieldValidator.isNumeric(txtKmerSize.getText(), new Integer(0))) {
			listErrors.add(FieldValidator.buildMessage(lblKmerSize.getText(), FieldValidator.ERROR_INTEGER));
			txtKmerSize.setBackground(oc);
		} else {
			instance.setKmerSize(Integer.parseInt(txtKmerSize.getText()));
		}
		
		if (txtOutput.getText() == null || txtOutput.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblOutput.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutput.setBackground(oc);
		}
		if (listErrors.size() > 0) {
			FieldValidator.paintErrors(listErrors, shell, "K-mers count calculator");
			return;
		}
		
		if (btnBothStrands.getSelection()) instance.setBothStrands(true);
		if (btnIsFasta.getSelection()) instance.setFasta(true);
		
		String outFile = txtOutput.getText();
		
		//Create the job and give the instance of the model with the parameters set
		SyncKmersCounter job = new SyncKmersCounter("K-mers count analysis");
		job.setInputFile(txtFile.getText());
		job.setOutputFile(outFile);
		job.setInstance(instance);
		
		String logFilename = LoggingHelper.getLoggerFilename(outFile,"KMR");
		job.setLogName(logFilename);
		job.setNameProgressBar(new File(outFile).getName());
		try {
			job.schedule();
			MessageDialog.openInformation(shell, "K-mers counter is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
			shell.dispose();
		} catch (Exception e) {
			MessageDialog.openError(shell, "K-mers counter error",e.getMessage());
			e.printStackTrace();
			return;
		}
	}
}