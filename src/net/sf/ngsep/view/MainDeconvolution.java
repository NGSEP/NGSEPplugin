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
import java.util.logging.FileHandler;

import net.sf.ngsep.control.SyncDeconvolution;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;

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
 * Main window of the GBS Cornell Deconvolution class
 * @author Juan Fernando De la Hoz
 */
public class MainDeconvolution {

	private Shell shell;
	private Display display;
	
	private String indexFile;

	private Label lblIndex;
	private Text txtIndex;
	private Button btnIndex;
	private Label lblOutDir;
	private Text txtOutDir;
	private Button btnOutDir;
	private Label lblLaneFilesDescriptor;
	private Text txtLaneFilesDescriptor;
	private Button btnLaneFilesDescriptor;
	private Label lblFile;
	private Text txtFile;
	private Button btnFile;
	private Label lblFile2;
	private Text txtFile2;
	private Button btnFile2;
	private Label lblFlowCell;
	private Text txtFlowCell;
	private Label lblLane;
	private Text txtLane;
	private Label lblTrimSeq;
	private Text txtTrimSeq;
	private Button btnUncompressOutput;
	
	private Button btnDeconvolute;
	private Button btnCancel;
	
	/**
	 * opens the window
	 */
	public void open(){
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
	 * Creates the content of the window
	 */
	public void createContents(){
		MouseListenerNgsep mouse = new MouseListenerNgsep();

		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setSize(800, 440);
		shell.setLocation(150, 200);
		shell.setText("Sample Deconvolution");
		
		lblIndex = new Label(shell, SWT.NONE);
		lblIndex.setBounds(10, 20, 140, 22);
		lblIndex.setText("(*)Index File:");

		txtIndex = new Text(shell, SWT.BORDER);
		txtIndex.setBounds(160, 20, 580, 22);
		txtIndex.addMouseListener(mouse);
		txtIndex.setText(indexFile);
		
		btnIndex = new Button(shell, SWT.NONE);
		btnIndex.setBounds(765, 20, 25, 22);
		btnIndex.setText("...");
		btnIndex.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected( SelectionEvent e ){
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, indexFile, txtIndex);
			}
		});
		
		lblOutDir = new Label(shell, SWT.NONE);
		lblOutDir.setBounds(10, 60, 140, 22);
		lblOutDir.setText("(*)Output Directory:");
		
		txtOutDir = new Text(shell, SWT.BORDER);
		txtOutDir.setBounds(160, 60, 580, 22);
		txtOutDir.addMouseListener(mouse);
		
		btnOutDir = new Button(shell, SWT.NONE);
		btnOutDir.setBounds(765, 60, 25, 22);
		btnOutDir.setText("...");
		btnOutDir.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected( SelectionEvent e ) {
				SpecialFieldsHelper.updateDirectoryTextBox(shell, SWT.SAVE, indexFile, txtOutDir);
			}
		});
		
		if (indexFile != null){
				File f = new File(indexFile);
				if(f.getParentFile()!=null) 
					txtOutDir.setText(f.getParentFile().getAbsolutePath());
		}
		
		lblLaneFilesDescriptor = new Label(shell, SWT.NONE);
		lblLaneFilesDescriptor.setBounds(10, 100, 140, 22);
		lblLaneFilesDescriptor.setText("Lane-FASTQ Info:");

		txtLaneFilesDescriptor = new Text(shell, SWT.BORDER);
		txtLaneFilesDescriptor.setBounds(160, 100, 580, 22);
		txtLaneFilesDescriptor.addMouseListener(mouse);
		
		btnLaneFilesDescriptor = new Button(shell, SWT.NONE);
		btnLaneFilesDescriptor.setBounds(765, 100, 25, 22);
		btnLaneFilesDescriptor.setText("...");
		btnLaneFilesDescriptor.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected( SelectionEvent e ){
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, indexFile, txtLaneFilesDescriptor);
			}
		});
		
		lblFile = new Label(shell, SWT.NONE);
		lblFile.setBounds(50, 150, 180, 22);
		lblFile.setText("FASTQ File:");
		
		txtFile = new Text(shell, SWT.BORDER);
		txtFile.setBounds(230, 150, 440, 22);
		txtFile.addMouseListener(mouse);
		
		btnFile = new Button(shell, SWT.NONE);
		btnFile.setBounds(700, 150, 25, 22);
		btnFile.setText("...");
		btnFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent e ) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, indexFile, txtFile);
			}
		});
		
		lblFile2 = new Label(shell, SWT.NONE);
		lblFile2.setBounds(50, 190, 180, 22);
		lblFile2.setText("FASTQ File (pair-end):");
		
		txtFile2 = new Text(shell, SWT.BORDER);
		txtFile2.setBounds(230, 190, 440, 22);
		txtFile2.addMouseListener(mouse);
		
		btnFile2 = new Button(shell, SWT.NONE);
		btnFile2.setBounds(700, 190, 25, 22);
		btnFile2.setText("...");
		btnFile2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent e ) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, indexFile, txtFile2);
			}
		});
		
		lblFlowCell = new Label(shell, SWT.NONE);
		lblFlowCell.setBounds(50, 240, 90, 22);
		lblFlowCell.setText("Flow Cell:");
		
		txtFlowCell = new Text(shell, SWT.BORDER);
		txtFlowCell.setBounds(150, 240, 150, 22);
		txtFlowCell.addMouseListener(mouse);
		
		lblLane = new Label(shell, SWT.NONE);
		lblLane.setBounds(350, 240, 90, 22);
		lblLane.setText("Lane:");
		
		txtLane = new Text(shell, SWT.BORDER);
		txtLane.setBounds(450, 240, 100, 22);
		txtLane.addMouseListener(mouse);
		
		lblTrimSeq = new Label(shell, SWT.NONE);
		lblTrimSeq.setBounds(50, 290, 150, 22);
		lblTrimSeq.setText("Trim sequence:");
		
		txtTrimSeq = new Text(shell, SWT.BORDER);
		txtTrimSeq.setBounds(200, 290, 250, 22);
		txtTrimSeq.addMouseListener(mouse);
		
		btnUncompressOutput = new Button(shell, SWT.CHECK);
		btnUncompressOutput.setBounds(480, 290, 300, 22);
		btnUncompressOutput.setText("Output uncompressed files");
		if(SpecialFieldsHelper.isWindows())	btnUncompressOutput.setSelection(true);
		else btnUncompressOutput.setSelection(false);
		
		btnDeconvolute = new Button(shell, SWT.NONE);
		btnDeconvolute.setBounds(240, 350, 130, 22);
		btnDeconvolute.setText("Deconvolute");
		btnDeconvolute.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected( SelectionEvent e ){
				proceed();
			}
		});
		
		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(410, 350, 130, 22);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected( SelectionEvent e ){
				shell.close();
			}
		});
	}
	
	/**
	 * Checks for empty fields, instantiates and passes the variables to the Sync class
	 */
	public void proceed(){
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		SyncDeconvolution deconvolutionJob = new SyncDeconvolution();
		
		// Check for errors and if absent, assign variables in Deconvolution Job 
		ArrayList<String> listErrors = new ArrayList<String>();
		if (txtIndex.getText() == null || txtIndex.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblIndex.getText(), FieldValidator.ERROR_MANDATORY));
			txtIndex.setBackground(oc);
		} else {
			deconvolutionJob.setIndexFile(txtIndex.getText());
		}
		if (txtOutDir.getText() == null || txtOutDir.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblOutDir.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutDir.setBackground(oc);
		} else {
			deconvolutionJob.setOutputDir(txtOutDir.getText());
		}
		if (txtLaneFilesDescriptor.getText() == null || txtLaneFilesDescriptor.getText().length()==0) {
			if (txtFlowCell.getText() == null || txtFlowCell.getText().length()==0 || 
					txtLane.getText() == null || txtLane.getText().length()==0 ||
					txtFile.getText() == null || txtFile.getText().length()==0){
				listErrors.add(FieldValidator.buildMessage(lblFile.getText(), FieldValidator.ERROR_MANDATORY));
				listErrors.add(FieldValidator.buildMessage(lblFlowCell.getText(), FieldValidator.ERROR_MANDATORY));
				listErrors.add(FieldValidator.buildMessage(lblLane.getText(), FieldValidator.ERROR_MANDATORY));
				txtFile.setBackground(oc);
				txtFlowCell.setBackground(oc);
				txtLane.setBackground(oc);
			} else {
				if (!FieldValidator.isNumeric(txtLane.getText(),new Integer(0))) {
					listErrors.add(FieldValidator.buildMessage(lblLane.getText(), FieldValidator.ERROR_INTEGER));
					txtLane.setBackground(oc);
				} else {
					deconvolutionJob.setFastqFile(txtFile.getText());
					deconvolutionJob.setLane(txtLane.getText());
					deconvolutionJob.setFlowCell(txtFlowCell.getText());
					if (txtFile2.getText() != null && txtFile2.getText().length()!=0) deconvolutionJob.setFastqFile2(txtFile2.getText());
				}
			}
		} else {
			deconvolutionJob.setLaneDescriptorFile(txtLaneFilesDescriptor.getText());
		}
		if (txtTrimSeq.getText() != null && txtTrimSeq.getText().length() != 0){
			deconvolutionJob.setTrimSequence(txtTrimSeq.getText());
		}
		if (listErrors.size() > 0) {
			FieldValidator.paintErrors(listErrors, shell, "Sample Deconvolution");
			return;
		}
		deconvolutionJob.setUncompressOutput(btnUncompressOutput.getSelection());
		
		//Manage the logger and the progress bar
		try {
			String logAndBar = null;
			logAndBar = txtIndex.getText();
			String logFilename = LoggingHelper.getLoggerFilename(logAndBar,Long.toString(System.currentTimeMillis()));
			logFilename = LoggingHelper.getLoggerFilename(logFilename,"SD");
			FileHandler logFile = new FileHandler(logFilename, false);
			deconvolutionJob.setLogName(logFilename);
			deconvolutionJob.setLogFile(logFile);
			deconvolutionJob.setNameProgressBar(new File(logAndBar).getName());
			deconvolutionJob.runJob();	
			MessageDialog.openInformation(shell, "Sample Deconvolution is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
			shell.dispose();
		} catch (Exception e) {
			MessageDialog.openError(shell, "Sample Deconvolution",e.getMessage());
			e.printStackTrace();
			return;
		}
	}

	public String getIndexFile() {
		return indexFile;
	}

	public void setFastqFile(String fastqFile) {
		this.indexFile = fastqFile;
	}
	
}
