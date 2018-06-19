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

import net.sf.ngsep.control.SyncCNVcompare;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.discovery.rd.CNVseqAlgorithm;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Main window for the CNVseqAlgorithm menu, an implementation of the CNV-seq algorithm (Xie and Tammi, 2009)
 * @author Juan Fernando De la Hoz, Jorge Duitama
 *
 */
public class MainCNVcompare {
	//General attributes
	protected Shell shell;
	private Display display;
	//---------------------------------------
	
	//Files selected initially by the user
	private String selectedFileX;
	public String getSelectedFileX() {
		return selectedFileX;
	}
	public void setSelectedFileX(String selectedFile) {
		this.selectedFileX = selectedFile;
	}
	private String selectedFileY;
	public String getSelectedFileY() {
		return selectedFileY;
	}
	public void setSelectedFileY(String selectedFile) {
		this.selectedFileY = selectedFile;
	}
	private String outputFileY;
	public void setOutputFileY(String outputFileY){
		this.outputFileY = outputFileY;
	}
	//---------------------------------------
	
	//Action buttons
	private Button btnCNVcompare;
	private Button btnCancel;
	//---------------------------------------

	//Main arguments
	private Label lblFileX;
	private Text txtFileX;
	private Button btnBrowseFileX;
	private Label lblFileY;
	private Text txtFileY;
	private Button btnBrowseFileY;
	private Label lblOutput;
	private Text txtOutput;
	private Button btnBrowseOutput;
	private Label lblRefGenome;
	private Text txtRefGenome;
	private Button btnBrowseRefGenome;
	private Label lblWindowSize;
	private Text txtWindowSize;
	private Label lblPvalue;
	private Text txtPvalue;
	private Button btnCGcorrection;
	private Button btnMultiTestCorrection;
	private Button btnWholeGenome;	
	//private Label lblMinCNVratio;
	//private Text txtMinCNVratio;
	//---------------------------------------
	
	public void open(){
		display = Display.getDefault();
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("Read Depth Comparator");
		shell.setLocation(150, 200);
		shell.setSize(900, 475);
		createContents();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public void createContents(){
		MouseListenerNgsep mouse = new MouseListenerNgsep();

		//Input & Output
		lblFileX = new Label (shell, SWT.NONE);
		lblFileX.setBounds(20, 30, 170, 22);
		lblFileX.setText("(*)Sample 1 (.bam):");
		
		txtFileX = new Text (shell, SWT.BORDER);
		txtFileX.setBounds(200, 30, 600, 22);
		txtFileX.addMouseListener(mouse);
		if (selectedFileX != null && !selectedFileX.equals("")) {
			txtFileX.setText(selectedFileX);
		}
		
		btnBrowseFileX = new Button (shell, SWT.NONE);
		btnBrowseFileX.setBounds(830, 30, 25, 22);
		btnBrowseFileX.setText("...");
		btnBrowseFileX.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFileX, txtFileX);
			}
		});
		
		lblFileY = new Label (shell, SWT.NONE);
		lblFileY.setBounds(20, 80, 170, 22);
		lblFileY.setText("(*)Sample 2 (.bam):");
		
		txtFileY = new Text (shell, SWT.BORDER);
		txtFileY.setBounds(200, 80, 600, 22);
		txtFileY.addMouseListener(mouse);
		if (selectedFileY != null && !selectedFileY.equals("")) {
			txtFileY.setText(selectedFileY);
		}
		
		btnBrowseFileY = new Button (shell, SWT.NONE);
		btnBrowseFileY.setBounds(830, 80, 25, 22);
		btnBrowseFileY.setText("...");
		btnBrowseFileY.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFileX, txtFileY);
			}
		});
		
		lblRefGenome = new Label(shell, SWT.NONE);
		lblRefGenome.setText("(*)Reference genome:");
		lblRefGenome.setBounds(20, 130, 170, 22);

		txtRefGenome = new Text(shell, SWT.BORDER);
		txtRefGenome.setBounds(200, 130, 600, 22);
		txtRefGenome.addMouseListener(mouse);

		btnBrowseRefGenome = new Button(shell, SWT.NONE);
		btnBrowseRefGenome.setBounds(830, 130, 25, 22);
		btnBrowseRefGenome.setText("...");
		btnBrowseRefGenome.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFileX, txtRefGenome);
			}
		});
		// Suggest the latest stored genome
		String historyReference = HistoryManager.getHistory(selectedFileX, HistoryManager.KEY_REFERENCE_FILE);
		if (historyReference!=null) txtRefGenome.setText(historyReference);
		
		lblOutput = new Label (shell, SWT.NONE);
		lblOutput.setBounds(20, 180, 170, 22);
		lblOutput.setText("(*)Output File:");

		txtOutput = new Text (shell, SWT.BORDER);
		txtOutput.setBounds(200, 180, 600, 22);
		txtOutput.addMouseListener(mouse);
		//Suggest name for the output file
		if (txtFileX.getText() != null && !txtFileX.getText().equals("") && txtFileY.getText() != null && !txtFileY.getText().equals("")) {
			String srtOutPutFileX = selectedFileX;
			if (srtOutPutFileX.contains(".")) {
				srtOutPutFileX = srtOutPutFileX.substring(0,srtOutPutFileX.lastIndexOf("."));
			}
			String srtOutPutFileY = outputFileY;
			if (srtOutPutFileY.contains(".")) {
				srtOutPutFileY = srtOutPutFileY.substring(0, srtOutPutFileY.lastIndexOf("."));
			}
					
			txtOutput.setText(srtOutPutFileX +"_-_"+ srtOutPutFileY + "_CNV");
		}
		
		btnBrowseOutput = new Button (shell, SWT.NONE);
		btnBrowseOutput.setBounds(830, 180, 25, 22);
		btnBrowseOutput.setText("...");
		btnBrowseOutput.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, selectedFileX, txtOutput);
			}
		});
		
		//Desired Window size
		lblWindowSize = new Label (shell, SWT.NONE);
		lblWindowSize.setBounds(180, 230, 150, 22);
		lblWindowSize.setText("Window size:");
		
		txtWindowSize = new Text (shell, SWT.BORDER);
		txtWindowSize.setBounds(330, 230, 50, 22);
		txtWindowSize.addMouseListener(mouse);
		txtWindowSize.setText("100");
		
		//Maximum p-value to report
		lblPvalue = new Label (shell, SWT.NONE);
		lblPvalue.setBounds(440, 230, 250, 22);
		lblPvalue.setText("Max p-value to report:");
		
		txtPvalue = new Text (shell, SWT.BORDER);
		txtPvalue.setBounds(630, 230, 100, 22);
		txtPvalue.addMouseListener(mouse);
		txtPvalue.setText("0.001");
		
		//checkbutton for GC correction
		btnCGcorrection = new Button(shell, SWT.CHECK);
		btnCGcorrection.setText("Perform GC correction");
		btnCGcorrection.setBounds(180, 280, 255, 22);
		
		//checkbutton for multiple testing -Bonferroni- correction
		btnMultiTestCorrection = new Button(shell, SWT.CHECK);
		btnMultiTestCorrection.setText("Multiple testing (Bonferroni) correction");
		btnMultiTestCorrection.setBounds(180, 330, 500, 22);

		//checkbutton for whole genome output
		btnWholeGenome = new Button(shell, SWT.CHECK);
		btnWholeGenome.setText("Print whole genome output");
		btnWholeGenome.setBounds(440, 280, 255, 22);
		btnWholeGenome.addMouseListener(onMouseClickButtonPvalue);
		
		//buttons on the bottom
		btnCNVcompare = new Button(shell, SWT.NONE);
		btnCNVcompare.setBounds(270, 390, 130, 25);
		btnCNVcompare.setText("Compare RD");
		btnCNVcompare.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();
			}
		});	
		
		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(500, 390, 130, 25);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});	

	}
	
	private final MouseListener onMouseClickButtonPvalue = new MouseListener() {
		@Override
		public void mouseDoubleClick(MouseEvent e) {
			// TODO Auto-generated method stub
		}
		@Override
		public void mouseDown(MouseEvent e) {
			// TODO Auto-generated method stub
		}
		@Override
		public void mouseUp(MouseEvent e) {
			if (btnWholeGenome.getSelection()) {
				txtPvalue.setEnabled(false);
				txtPvalue.setText("0.5");
				lblPvalue.setEnabled(false);
				btnMultiTestCorrection.setEnabled(false);
				btnMultiTestCorrection.setSelection(false);
			} else if (!btnWholeGenome.getSelection()) {
				txtPvalue.setEnabled(true);
				txtPvalue.setText("0.001");
				lblPvalue.setEnabled(true);
				btnMultiTestCorrection.setEnabled(true);
			}
			txtPvalue.update();
		}
	};
	
	public void proceed(){
		CNVseqAlgorithm cnvSeqObj = new CNVseqAlgorithm();

		//Validate fields and record errors in the list
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		ArrayList<String> listErrors = new ArrayList<String>();
		if (txtFileX.getText() == null || txtFileX.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblFileX.getText(), FieldValidator.ERROR_MANDATORY));
			txtFileX.setBackground(oc);
		}
		if (txtFileY.getText() == null || txtFileY.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblFileY.getText(), FieldValidator.ERROR_MANDATORY));
			txtFileY.setBackground(oc);
		}
		if (txtOutput.getText() == null|| txtOutput.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblOutput.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutput.setBackground(oc);
		}
		if (txtRefGenome.getText() == null|| txtRefGenome.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblRefGenome.getText(), FieldValidator.ERROR_MANDATORY));
			txtRefGenome.setBackground(oc);
		}
		if (txtWindowSize.getText() == null || txtWindowSize.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblWindowSize.getText(), FieldValidator.ERROR_MANDATORY));
			txtRefGenome.setBackground(oc);
		} else {
			if (!txtWindowSize.getText().equals("100") && !FieldValidator.isNumeric(txtWindowSize.getText(),new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblWindowSize.getText(), FieldValidator.ERROR_INTEGER));
				txtWindowSize.setBackground(oc);
			} else {
				cnvSeqObj.setBinSize(Integer.parseInt(txtWindowSize.getText()));
			}
		}
		if (txtPvalue.getText() != null && txtPvalue.getText().length()>0 && !txtPvalue.getText().equals("0.001")) {
			if (!FieldValidator.isNumeric(txtPvalue.getText(),new Double(0))) {
				listErrors.add(FieldValidator.buildMessage(lblPvalue.getText(), FieldValidator.ERROR_NUMERIC));
				txtPvalue.setBackground(oc);
			} else {
				cnvSeqObj.setpValue(Double.parseDouble(txtPvalue.getText()));
			}
		}
		if (listErrors.size() > 0) {
			FieldValidator.paintErrors(listErrors, shell, "Read Depth Comparator");
			return;
		}
		
		//Create the job and give it the instance of the model with the parameters set
		SyncCNVcompare syncCNVcompare = new SyncCNVcompare("Read Depth Comparator");
		cnvSeqObj.setBamXfile(txtFileX.getText());
		cnvSeqObj.setBamYfile(txtFileY.getText());
		cnvSeqObj.setOutFile(txtOutput.getText()+".cnvSeq");
		//TODO: Set genome
		cnvSeqObj.setReference(txtRefGenome.getText());
		cnvSeqObj.setGcCorrection(btnCGcorrection.getSelection());
		cnvSeqObj.setWholeGenomePrnt(btnWholeGenome.getSelection());
		cnvSeqObj.setBonferroni(btnMultiTestCorrection.getSelection());
		syncCNVcompare.setCNVcompare(cnvSeqObj);
		
		//Manage the logger and the progress bar
		String logFilename = LoggingHelper.getLoggerFilename(txtOutput.getText(),"RDcompar");
		syncCNVcompare.setLogName(logFilename);
		syncCNVcompare.setNameProgressBar(new File(txtOutput.getText()).getName());
		try {
			syncCNVcompare.setLogFile(new FileHandler(logFilename, false));
			syncCNVcompare.schedule();
		} catch (Exception e) {
			MessageDialog.openError(shell, "Read Depth Comparator Error",e.getMessage());
			e.printStackTrace();
			return;
		}
		HistoryManager.saveInHistory(HistoryManager.KEY_REFERENCE_FILE, txtRefGenome.getText());
		MessageDialog.openInformation(shell, "Read Depth Comparator is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
		shell.dispose();
	}
}
