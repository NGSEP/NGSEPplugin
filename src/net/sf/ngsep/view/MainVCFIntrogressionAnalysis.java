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

import net.sf.ngsep.control.SyncVCFIntrogressionAnalysis;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.vcf.VCFWindowIntrogressionAnalysis;

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
 * Main window for the VCFIntrogressionAnalysis menu
 * @author Jorge Duitama
 *
 */
public class MainVCFIntrogressionAnalysis {
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
	private Label lblPopFile;
	private Text txtPopFile;
	private Button btnPopFile;
	private Label lblOutput;
	private Text txtOutput;
	private Button btnOutput;
	private Label lblMinPCTGenotyped;
	private Text txtMinPCTGenotyped;
	private Label lblMinDiffAF;
	private Text txtMinDiffAF;
	private Label lblMaxMAFWithin;
	private Text txtMaxMAFWithin;
	private Label lblWindowSize;
	private Text txtWindowSize;
	private Label lblOverlap;
	private Text txtOverlap;
	private Label lblMatchScore;
	private Text txtMatchScore;
	private Label lblMismatchScore;
	private Text txtMismatchScore;
	private Label lblMinScore;
	private Text txtMinScore;
	private Button btnPrintVCF;
	private Button btnPrintUnassigned;
	
	
	
	
	
	//---------------------------------------
	
	/**
	 * Open the window
	 */
	public void open(){
		display = Display.getDefault();
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("VCF Introgression analysis");
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
		lblFile.setText("(*)Input VCF File:");
		
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
		
		lblPopFile = new Label (shell, SWT.NONE);
		lblPopFile.setBounds(20, 90, 170, 30);
		lblPopFile.setText("(*)Population File:");
		
		txtPopFile = new Text (shell, SWT.BORDER);
		txtPopFile.setBounds(200, 90, 600, 25);
		txtPopFile.addMouseListener(mouse);
				
		btnPopFile = new Button (shell, SWT.NONE);
		btnPopFile.setBounds(830, 90, 25, 25);
		btnPopFile.setText("...");
		btnPopFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtPopFile);
			}
		});
		
		lblOutput = new Label (shell, SWT.NONE);
		lblOutput.setBounds(20, 140, 170, 30);
		lblOutput.setText("(*)Output prefix:");
		
		txtOutput = new Text (shell, SWT.BORDER);
		txtOutput.setBounds(200, 140, 600, 22);
		txtOutput.addMouseListener(mouse);
		//Suggest name for the output file
		txtOutput.setText(SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile));
		
		btnOutput = new Button (shell, SWT.NONE);
		btnOutput.setBounds(830, 140, 25, 25);
		btnOutput.setText("...");
		btnOutput.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, selectedFile, txtOutput);
			}
		});
		
		//Optional Parameters to calculate population haplotypes
		lblMinPCTGenotyped = new Label (shell, SWT.NONE);
		lblMinPCTGenotyped.setBounds(20, 190, 200, 30);
		lblMinPCTGenotyped.setText("Min % genotyped:");
		
		txtMinPCTGenotyped = new Text (shell, SWT.BORDER);
		txtMinPCTGenotyped.setBounds(250, 190, 100, 25);
		txtMinPCTGenotyped.addMouseListener(mouse);
		txtMinPCTGenotyped.setText(String.valueOf(VCFWindowIntrogressionAnalysis.DEF_MIN_PCT_GENOTYPED));
		
		lblMinDiffAF = new Label (shell, SWT.NONE);
		lblMinDiffAF.setBounds(20, 240, 200, 30);
		lblMinDiffAF.setText("Min diff. allele frequency:");
		
		txtMinDiffAF = new Text (shell, SWT.BORDER);
		txtMinDiffAF.setBounds(250, 240, 100, 25);
		txtMinDiffAF.addMouseListener(mouse);
		txtMinDiffAF.setText(String.valueOf(VCFWindowIntrogressionAnalysis.DEF_MIN_DIFF_AF));
		
		lblMaxMAFWithin = new Label (shell, SWT.NONE);
		lblMaxMAFWithin.setBounds(20, 290, 200, 30);
		lblMaxMAFWithin.setText("Max MAF within group:");
		
		txtMaxMAFWithin = new Text (shell, SWT.BORDER);
		txtMaxMAFWithin.setBounds(250, 290, 100, 25);
		txtMaxMAFWithin.addMouseListener(mouse);
		txtMaxMAFWithin.setText(String.valueOf(VCFWindowIntrogressionAnalysis.DEF_MAX_MAF_WITHIN));
		
		btnPrintVCF = new Button(shell, SWT.CHECK);
		btnPrintVCF.setBounds(20, 340, 150, 30);
		btnPrintVCF.setText("Output VCF");
		
		btnPrintUnassigned = new Button(shell, SWT.CHECK);
		btnPrintUnassigned.setBounds(200, 340, 220, 30);
		btnPrintUnassigned.setText("Report unassigned regions");
		
		//Optional parameters for window-based analysis
		
		lblWindowSize = new Label (shell, SWT.NONE);
		lblWindowSize.setBounds(400, 190, 100, 30);
		lblWindowSize.setText("Window size:");
		
		txtWindowSize = new Text (shell, SWT.BORDER);
		txtWindowSize.setBounds(530, 190, 100, 25);
		txtWindowSize.addMouseListener(mouse);
		txtWindowSize.setText(String.valueOf(VCFWindowIntrogressionAnalysis.DEF_WINDOW_SIZE));
		
		lblOverlap = new Label (shell, SWT.NONE);
		lblOverlap.setBounds(650, 190, 100, 30);
		lblOverlap.setText("Overlap:");
		
		txtOverlap = new Text (shell, SWT.BORDER);
		txtOverlap.setBounds(780, 190, 100, 25);
		txtOverlap.addMouseListener(mouse);
		txtOverlap.setText(String.valueOf(VCFWindowIntrogressionAnalysis.DEF_OVERLAP));
		
		lblMatchScore = new Label (shell, SWT.NONE);
		lblMatchScore.setBounds(400, 240, 100, 30);
		lblMatchScore.setText("Scores. Match:");
		
		txtMatchScore = new Text (shell, SWT.BORDER);
		txtMatchScore.setBounds(530, 240, 100, 25);
		txtMatchScore.addMouseListener(mouse);
		txtMatchScore.setText(String.valueOf(VCFWindowIntrogressionAnalysis.DEF_MATCH_SCORE));
		
		lblMismatchScore = new Label (shell, SWT.NONE);
		lblMismatchScore.setBounds(650, 240, 100, 30);
		lblMismatchScore.setText("Mismatch:");
		
		txtMismatchScore = new Text (shell, SWT.BORDER);
		txtMismatchScore.setBounds(780, 240, 100, 25);
		txtMismatchScore.addMouseListener(mouse);
		txtMismatchScore.setText(String.valueOf(VCFWindowIntrogressionAnalysis.DEF_MISMATCH_SCORE));
		
		
		lblMinScore = new Label (shell, SWT.NONE);
		lblMinScore.setBounds(400, 290, 150, 30);
		lblMinScore.setText("Min assignment score:");
		
		txtMinScore = new Text (shell, SWT.BORDER);
		txtMinScore.setBounds(700, 290, 100, 25);
		txtMinScore.addMouseListener(mouse);
		txtMinScore.setText(String.valueOf(VCFWindowIntrogressionAnalysis.DEF_MIN_SCORE));
		
		//buttons on the bottom
		btnSubmit = new Button(shell, SWT.NONE);
		btnSubmit.setBounds(250, 400, 170, 25);
		btnSubmit.setText("Introgression analysis");
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
		VCFWindowIntrogressionAnalysis instance = new VCFWindowIntrogressionAnalysis();
		
		//Validate fields and record errors in the list
		ArrayList<String> listErrors = new ArrayList<String>();
		
		if (txtFile.getText() == null || txtFile.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtFile.setBackground(oc);
		}
		if (txtPopFile.getText() == null || txtPopFile.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblPopFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtPopFile.setBackground(oc);
		} else {
			instance.setPopulationsFile(txtPopFile.getText());
		}
		if (txtOutput.getText() == null || txtOutput.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblOutput.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutput.setBackground(oc);
		}
		if (txtMinPCTGenotyped.getText() != null && txtMinPCTGenotyped.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMinPCTGenotyped.getText(), new Double(0))) {
				listErrors.add(FieldValidator.buildMessage(lblMinPCTGenotyped.getText(), FieldValidator.ERROR_NUMERIC));
				txtMinPCTGenotyped.setBackground(oc);
			} else {
				instance.setMinPctGenotyped(Double.parseDouble(txtMinPCTGenotyped.getText()));
			}
		}
		if (txtMinDiffAF.getText() != null && txtMinDiffAF.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMinDiffAF.getText(), new Double(0))) {
				listErrors.add(FieldValidator.buildMessage(lblMinDiffAF.getText(), FieldValidator.ERROR_NUMERIC));
				txtMinDiffAF.setBackground(oc);
			} else {
				instance.setMinDiffAF(Double.parseDouble(txtMinDiffAF.getText()));
			}
		}
		if (txtMaxMAFWithin.getText() != null && txtMaxMAFWithin.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMaxMAFWithin.getText(), new Double(0))) {
				listErrors.add(FieldValidator.buildMessage(lblMaxMAFWithin.getText(), FieldValidator.ERROR_NUMERIC));
				txtMaxMAFWithin.setBackground(oc);
			} else {
				instance.setMaxMAFWithin(Double.parseDouble(txtMaxMAFWithin.getText()));
			}
		}
		if (txtWindowSize.getText() != null && txtWindowSize.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtWindowSize.getText(), new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblWindowSize.getText(), FieldValidator.ERROR_INTEGER));
				txtWindowSize.setBackground(oc);
			} else {
				instance.setWindowSize(Integer.parseInt(txtWindowSize.getText()));
			}
		}
		if (txtOverlap.getText() != null && txtOverlap.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtOverlap.getText(), new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblOverlap.getText(), FieldValidator.ERROR_INTEGER));
				txtOverlap.setBackground(oc);
			} else {
				instance.setOverlap(Integer.parseInt(txtOverlap.getText()));
			}
		}
		if (txtMatchScore.getText() != null && txtMatchScore.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMatchScore.getText(), new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblMatchScore.getText(), FieldValidator.ERROR_INTEGER));
				txtMatchScore.setBackground(oc);
			} else {
				instance.setMatchScore(Integer.parseInt(txtMatchScore.getText()));
			}
		}
		if (txtMismatchScore.getText() != null && txtMismatchScore.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMismatchScore.getText(), new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblMismatchScore.getText(), FieldValidator.ERROR_INTEGER));
				txtMismatchScore.setBackground(oc);
			} else {
				instance.setMismatchScore(Integer.parseInt(txtMismatchScore.getText()));
			}
		}
		if (txtMinScore.getText() != null && txtMinScore.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMinScore.getText(), new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblMinScore.getText(), FieldValidator.ERROR_INTEGER));
				txtMinScore.setBackground(oc);
			} else {
				instance.setMinScore(Integer.parseInt(txtMinScore.getText()));
			}
		}
		if (listErrors.size() > 0) {
			FieldValidator.paintErrors(listErrors, shell, "VCF introgression Analysis");
			return;
		}
		
		if (btnPrintVCF.getSelection()) instance.setPrintVCF(true);
		if (btnPrintUnassigned.getSelection()) instance.setPrintUnassigned(true);
		
		String outPrefix = txtOutput.getText();
		instance.setOutPrefix(outPrefix);
		
		
		//Create the job and give the instance of the model with the parameters set
		SyncVCFIntrogressionAnalysis job = new SyncVCFIntrogressionAnalysis("VCF introgression analysis");
		job.setInputFile(txtFile.getText());
		job.setInstance(instance);
		
		String logFilename = outPrefix+"_VIA.log";
		job.setLogName(logFilename);
		job.setNameProgressBar(new File(outPrefix).getName());
		try {
			job.schedule();
			MessageDialog.openInformation(shell, "VCF introgression analysis is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
			shell.dispose();
		} catch (Exception e) {
			MessageDialog.openError(shell, "VCF introgression analysis error",e.getMessage());
			e.printStackTrace();
			return;
		}
	}
}