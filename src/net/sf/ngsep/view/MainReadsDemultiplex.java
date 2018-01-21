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

import net.sf.ngsep.control.SyncReadsDemultiplex;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.sequences.ReadsDemultiplex;

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
 * Main window of the sample demultiplexing class
 * @author Juan Fernando De la Hoz
 * @author Jorge Duitama
 */
public class MainReadsDemultiplex implements SingleFileInputWindow {
	
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

	private Label lblIndex;
	private Text txtIndex;
	private Button btnIndex;
	private Label lblOutDir;
	private Text txtOutDir;
	private Button btnOutDir;
	private Label lblLaneFilesDescriptor;
	private Text txtLaneFilesDescriptor;
	private Button btnLaneFilesDescriptor;
	private Label lblFastqFile1;
	private Text txtFastqFile1;
	private Button btnFastqFile1;
	private Label lblFastqFile2;
	private Text txtFastqFile2;
	private Button btnFastqFile2;
	private Label lblFlowCell;
	private Text txtFlowCell;
	private Label lblLane;
	private Text txtLane;
	private Label lblTrimSeq;
	private Text txtTrimSeq;
	private Button btnUncompressOutput;
	private Button btnDualBarcoding;
	
	private Button btnDemultiplex;
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
		txtIndex.setText(selectedFile);
		
		btnIndex = new Button(shell, SWT.NONE);
		btnIndex.setBounds(765, 20, 25, 22);
		btnIndex.setText("...");
		btnIndex.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected( SelectionEvent e ){
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtIndex);
			}
		});
		
		lblOutDir = new Label(shell, SWT.NONE);
		lblOutDir.setBounds(10, 60, 140, 22);
		lblOutDir.setText("(*)Output Directory:");
		
		txtOutDir = new Text(shell, SWT.BORDER);
		txtOutDir.setBounds(160, 60, 580, 22);
		txtOutDir.addMouseListener(mouse);
		
		if (selectedFile != null){
			File f = new File(selectedFile);
			if(f.getParentFile()!=null) txtOutDir.setText(f.getParentFile().getAbsolutePath());
		}
		
		btnOutDir = new Button(shell, SWT.NONE);
		btnOutDir.setBounds(765, 60, 25, 22);
		btnOutDir.setText("...");
		btnOutDir.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected( SelectionEvent e ) {
				SpecialFieldsHelper.updateDirectoryTextBox(shell, SWT.SAVE, selectedFile, txtOutDir);
			}
		});
		
		
		
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
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtLaneFilesDescriptor);
			}
		});
		
		lblFastqFile1 = new Label(shell, SWT.NONE);
		lblFastqFile1.setBounds(50, 150, 180, 22);
		lblFastqFile1.setText("FASTQ File:");
		
		txtFastqFile1 = new Text(shell, SWT.BORDER);
		txtFastqFile1.setBounds(230, 150, 440, 22);
		txtFastqFile1.addMouseListener(mouse);
		
		btnFastqFile1 = new Button(shell, SWT.NONE);
		btnFastqFile1.setBounds(700, 150, 25, 22);
		btnFastqFile1.setText("...");
		btnFastqFile1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent e ) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtFastqFile1);
			}
		});
		
		lblFastqFile2 = new Label(shell, SWT.NONE);
		lblFastqFile2.setBounds(50, 190, 180, 22);
		lblFastqFile2.setText("FASTQ File (pair-end):");
		
		txtFastqFile2 = new Text(shell, SWT.BORDER);
		txtFastqFile2.setBounds(230, 190, 440, 22);
		txtFastqFile2.addMouseListener(mouse);
		
		btnFastqFile2 = new Button(shell, SWT.NONE);
		btnFastqFile2.setBounds(700, 190, 25, 22);
		btnFastqFile2.setText("...");
		btnFastqFile2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent e ) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtFastqFile2);
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
		
		btnDualBarcoding = new Button(shell, SWT.CHECK);
		btnDualBarcoding.setBounds(50, 340, 300, 22);
		btnDualBarcoding.setText("Dual barcoding");
		
		btnDemultiplex = new Button(shell, SWT.NONE);
		btnDemultiplex.setBounds(240, 400, 130, 22);
		btnDemultiplex.setText("Demultiplex");
		btnDemultiplex.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected( SelectionEvent e ){
				proceed();
			}
		});
		
		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(410, 400, 130, 22);
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
		
		ReadsDemultiplex instance = new ReadsDemultiplex();
		SyncReadsDemultiplex job = new SyncReadsDemultiplex("Reads demultiplex");
		job.setInstance(instance);
		
		// Check for errors and if absent, assign variables in instance
		ArrayList<String> listErrors = new ArrayList<String>();
		
		if (txtIndex.getText() == null || txtIndex.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblIndex.getText(), FieldValidator.ERROR_MANDATORY));
			txtIndex.setBackground(oc);
		} else {
			job.setIndexFile(txtIndex.getText());
		}
		if (txtOutDir.getText() == null || txtOutDir.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblOutDir.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutDir.setBackground(oc);
		} else {
			instance.setOutDirectory(txtOutDir.getText());
		}
		if (txtLaneFilesDescriptor.getText() == null || txtLaneFilesDescriptor.getText().length()==0) {
			if (txtFlowCell.getText() == null || txtFlowCell.getText().length()==0 || 
					txtLane.getText() == null || txtLane.getText().length()==0 ||
					txtFastqFile1.getText() == null || txtFastqFile1.getText().length()==0) {
				listErrors.add(FieldValidator.buildMessage(lblFastqFile1.getText(), FieldValidator.ERROR_MANDATORY));
				listErrors.add(FieldValidator.buildMessage(lblFlowCell.getText(), FieldValidator.ERROR_MANDATORY));
				listErrors.add(FieldValidator.buildMessage(lblLane.getText(), FieldValidator.ERROR_MANDATORY));
				txtFastqFile1.setBackground(oc);
				txtFlowCell.setBackground(oc);
				txtLane.setBackground(oc);
			} else {
				job.setFastqFile1(txtFastqFile1.getText());
				instance.setFlowcell(txtFlowCell.getText());
				instance.setLane(txtLane.getText());
				
				if (txtFastqFile2.getText() != null && txtFastqFile2.getText().length()!=0) job.setFastqFile2(txtFastqFile2.getText());
			}
		} else {
			instance.setLaneFilesDescriptor(txtLaneFilesDescriptor.getText());
		}
		if (txtTrimSeq.getText() != null && txtTrimSeq.getText().length() != 0){
			instance.setTrimSequence(txtTrimSeq.getText());
		}
		if (listErrors.size() > 0) {
			FieldValidator.paintErrors(listErrors, shell, "Sample Deconvolution");
			return;
		}
		instance.setUncompressedOutput(btnUncompressOutput.getSelection());
		instance.setDualBarcode(btnDualBarcoding.getSelection());
		
		//Manage the logger and the progress bar
		
		String logAndBar = txtIndex.getText();
		String logFilename = LoggingHelper.getLoggerFilename(logAndBar,"SD"+Long.toString(System.currentTimeMillis()));
		job.setLogName(logFilename);
		job.setNameProgressBar(new File(logAndBar).getName());
		try {
			job.schedule();	
			MessageDialog.openInformation(shell, "Sample demultiplexing is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
			shell.dispose();
		} catch (Exception e) {
			MessageDialog.openError(shell, "Sample demultiplexing",e.getMessage());
			e.printStackTrace();
			return;
		}
	}
	
}
