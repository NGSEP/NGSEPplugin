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

import net.sf.ngsep.control.SyncMutatedPeptidesExtractor;
import net.sf.ngsep.utilities.EclipseProjectHelper;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.genome.ReferenceGenome;
import ngsep.transcriptome.MutatedPeptidesExtractor;

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
 * @author Jorge Duitama
 */
public class MainMutatedPeptidesExtractor implements SingleFileInputWindow {
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
	
	//Main arguments
	private Label lblFile;
	private Text txtFile;
	private Button btnFile;
	private Label lblTranscriptomeGFF3;
	private Text txtTranscriptomeGFF3;
	private Button btnTranscriptomeGFF3;
	private Label lblReferenceFile;
	private Text txtReferenceFile;
	private Button btnReferenceFile;
	private Label lblOutputFile;
	private Text txtOutputFile;
	private Button btnOutputFile;
	
	private Label lblMutatedSampleId;
	private Text txtMutatedSampleId;

	private Label lblControlSampleId;
	private Text txtControlSampleId;
	
	private Label lblMaxLength;
	private Text txtMaxLength;
	
	
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

	private void createContents() {
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setSize(800, 400);
		shell.setText("Mutated Peptides Extractor");
		shell.setLocation(10, 10);

		MouseListenerNgsep mouse = new MouseListenerNgsep();
		lblFile = new Label(shell, SWT.NONE);
		lblFile.setBounds(10, 30, 180, 22);
		lblFile.setText("(*VCF) Variants File:");
		
		txtFile = new Text(shell, SWT.BORDER);
		txtFile.setBounds(200, 30, 550, 22);
		if (selectedFile != null && selectedFile.length()>0) {
			txtFile.setText(selectedFile);
		}
		txtFile.addMouseListener(mouse);
		
		btnFile = new Button(shell, SWT.NONE);
		btnFile.setBounds(760, 30, 25, 22);
		btnFile.setText("...");
		btnFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, selectedFile, txtFile);
			}
		});
		

		lblTranscriptomeGFF3 = new Label(shell, SWT.NONE);
		lblTranscriptomeGFF3.setBounds(10, 70, 180, 22);
		lblTranscriptomeGFF3.setText("(*GFF) Gene Annotation File:");
		
		
		txtTranscriptomeGFF3 = new Text(shell, SWT.BORDER);
		txtTranscriptomeGFF3.setBounds(200, 70, 550, 22);
		txtTranscriptomeGFF3.addMouseListener(mouse);
		
		// Suggest the latest stored transcriptome
		try {
			String directoryProject = EclipseProjectHelper.findProjectDirectory(selectedFile);
			String historyFile = HistoryManager.createPathRecordGff3(directoryProject);
			String historyReference = HistoryManager.getPathRecordReference(historyFile);
			if (historyReference!=null) {
				txtTranscriptomeGFF3.setText(historyReference);
			}
		} catch (Exception e) {
			e.getMessage();
			MessageDialog.openError(shell, "Mutated Peptides Extractor error","error trying to load the transcriptome path history most recently used"+ e.getMessage());
		}
		
		btnTranscriptomeGFF3 = new Button(shell, SWT.NONE);
		btnTranscriptomeGFF3.setBounds(760, 70, 25, 22);
		btnTranscriptomeGFF3.setText("...");
		btnTranscriptomeGFF3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, selectedFile, txtTranscriptomeGFF3);
			}
		});
		

		lblReferenceFile = new Label(shell, SWT.NONE);
		lblReferenceFile.setBounds(10, 110, 180, 22);
		lblReferenceFile.setText("(*FASTA) Reference Genome:");
		

		txtReferenceFile = new Text(shell, SWT.BORDER);
		txtReferenceFile.setBounds(200, 110, 550, 22);
		txtReferenceFile.addMouseListener(mouse);
		// Suggest the latest stored genome
		try {
			String directoryProject = EclipseProjectHelper.findProjectDirectory(selectedFile);
			String historyFile = HistoryManager.createPathRecordGeneral(directoryProject);
			String historyReference = HistoryManager.getPathRecordReference(historyFile);
			if (historyReference!=null) {
				txtReferenceFile.setText(historyReference);
			}
		} catch (Exception e) {
			e.getMessage();
			MessageDialog.openError(shell, "Mutated Peptides Extractor error","error trying to load the reference path history most recently used"+ e.getMessage());
		}
		
		btnReferenceFile = new Button(shell, SWT.NONE);
		btnReferenceFile.setBounds(760, 110, 25, 22);
		btnReferenceFile.setText("...");
		btnReferenceFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, selectedFile, txtReferenceFile);
			}
		});
		
		lblOutputFile = new Label(shell, SWT.NONE);
		lblOutputFile.setBounds(10, 150, 180, 22);
		lblOutputFile.setText("(*fa) Output File:");
		
		txtOutputFile = new Text(shell, SWT.BORDER);
		txtOutputFile.setBounds(200, 150, 550, 22);
		txtOutputFile.addMouseListener(mouse);
		String suggestedOutPrefix = SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile);
		txtOutputFile.setText(suggestedOutPrefix+"_mutatedPeptides.fa");
		
		
		
		btnOutputFile = new Button(shell, SWT.NONE);
		btnOutputFile.setBounds(760, 150, 25, 22);
		btnOutputFile.setText("...");
		btnOutputFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.SAVE, selectedFile, txtOutputFile);
			}
		});
		
		lblMutatedSampleId = new Label(shell, SWT.NONE);
		lblMutatedSampleId.setText("Mutated sample :");
		lblMutatedSampleId.setBounds(10, 190, 180, 22);
		
		txtMutatedSampleId = new Text(shell, SWT.BORDER);
		txtMutatedSampleId.setBounds(200, 190, 180, 22);
		txtMutatedSampleId.addMouseListener(mouse);
		
		lblControlSampleId = new Label(shell, SWT.NONE);
		lblControlSampleId.setText("Control sample :");
		lblControlSampleId.setBounds(410, 190, 180, 22);
		
		txtControlSampleId = new Text(shell, SWT.BORDER);
		txtControlSampleId.setBounds(600, 190, 180, 22);
		txtControlSampleId.addMouseListener(mouse);
		
		lblMaxLength= new Label(shell, SWT.NONE);
		lblMaxLength.setText("Max peptide length:");
		lblMaxLength.setBounds(10, 230, 180, 22);
		
		txtMaxLength = new Text(shell, SWT.BORDER);
		txtMaxLength.setBounds(200, 230, 180, 22);
		txtMaxLength.setText(String.valueOf(MutatedPeptidesExtractor.DEF_MAX_LENGTH));
		txtMaxLength.addMouseListener(mouse);
		
		btnSubmit = new Button(shell, SWT.NONE);
		btnSubmit.setBounds(240, 330, 200, 25);
		btnSubmit.setText("Find peptides");
		btnSubmit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();
			}
		});
		

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(460, 330, 200, 25);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
	}

	public void proceed() {
		
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		ArrayList<String> errors = new ArrayList<String>();
		SyncMutatedPeptidesExtractor job = new SyncMutatedPeptidesExtractor("Mutated peptides extractor");
		MutatedPeptidesExtractor instance = new MutatedPeptidesExtractor ();
		job.setInstance(instance);
		
		if (txtFile.getText() == null || txtFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtFile.setBackground(oc);
		} else {
			job.setVariantsFile(txtFile.getText());
		}

		if (txtTranscriptomeGFF3.getText() == null || txtTranscriptomeGFF3.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblTranscriptomeGFF3.getText(), FieldValidator.ERROR_MANDATORY));
			txtTranscriptomeGFF3.setBackground(oc);
		} else {
			job.setTranscriptomeMap(txtTranscriptomeGFF3.getText());
		}

		if (txtReferenceFile.getText() == null || txtReferenceFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblReferenceFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtReferenceFile.setBackground(oc);
		} else {
			try {
				ReferenceGenome genome = ReferenceGenomesFactory.getInstance().getGenome(txtReferenceFile.getText(), shell);
				job.setGenome(genome);
			} catch (IOException e) {
				e.printStackTrace();
				errors.add(FieldValidator.buildMessage(lblReferenceFile.getText(), "error loading file: "+e.getMessage()));
				txtReferenceFile.setBackground(oc);
			}
		}

		if (txtOutputFile.getText() == null|| txtOutputFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblOutputFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutputFile.setBackground(oc);
		} else {
			job.setOutputFile(txtOutputFile.getText());
		}
		
		if (txtMutatedSampleId.getText() != null && txtMutatedSampleId.getText().length()!=0) {
			instance.setMutatedSampleId(txtMutatedSampleId.getText());					
		}
		
		if (txtControlSampleId.getText() != null && txtControlSampleId.getText().length()!=0) {
			instance.setControlSampleId(txtControlSampleId.getText());					
		}
		
		if (txtMaxLength.getText() != null && txtMaxLength.getText().length()!=0) {
			if (!FieldValidator.isNumeric(txtMaxLength.getText(),new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblMaxLength.getText(), FieldValidator.ERROR_INTEGER));
				txtMaxLength.setBackground(oc);
			} else {
				instance.setMaxLength(Integer.parseInt(txtMaxLength.getText()));					
			}
		}
			
		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shell, "Mutated peptides extractor");
			return;
		}
		
		try {
			String directoryProject = EclipseProjectHelper.findProjectDirectory(selectedFile);
			String historyOne = HistoryManager.createPathRecordGeneral(directoryProject);
			HistoryManager.createPathRecordFiles(historyOne, txtReferenceFile.getText().toString());
			String history = HistoryManager.createPathRecordGff3(directoryProject);
			HistoryManager.createPathRecordFiles(history,txtTranscriptomeGFF3.getText());
		} catch (Exception e) {
			MessageDialog.openError(shell, "Mutated Peptides Extractor Error","Error trying to place the reference path history most recently used"+ e.getMessage());
			return;
		}
		
		String outputFile = txtOutputFile.getText();
		String logFilename = LoggingHelper.getLoggerFilename(outputFile,"VFA");
		job.setLogName(logFilename);
		job.setNameProgressBar(new File(outputFile).getName());
		try {
			job.schedule();
		} catch (Exception e) {
			MessageDialog.openError(shell,"Mutated Peptides Extractor Error", e.getMessage());
			e.printStackTrace();
		}
		MessageDialog.openInformation(shell,"Mutated Peptides Extractor is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
		shell.dispose();

	}

}
