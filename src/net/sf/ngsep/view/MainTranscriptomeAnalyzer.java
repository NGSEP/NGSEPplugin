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

import net.sf.ngsep.control.SyncTranscriptomeAnalyzer;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.genome.ReferenceGenome;
import ngsep.transcriptome.TranscriptomeAnalyzer;
import ngsep.transcriptome.VariantAnnotationParameters;

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
public class MainTranscriptomeAnalyzer implements SingleFileInputWindow {
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
	private Label lblTranscriptomeGFF3;
	private Text txtTranscriptomeGFF3;
	private Button btnTranscriptomeGFF3;
	private Label lblReferenceFile;
	private Text txtReferenceFile;
	private Button btnReferenceFile;
	private Label lblOutputPrefix;
	private Text txtOutputPrefix;
	private Button btnOutputPrefix;
	
	private Button btnCompleteTranscripts;
	private Label lblMinProteinLength;
	private Text txtMinProteinLength;
	
	
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
		shell.setText("Variants Functional Annotator");
		shell.setLocation(10, 10);

		MouseListenerNgsep mouse = new MouseListenerNgsep();
		

		lblTranscriptomeGFF3 = new Label(shell, SWT.NONE);
		lblTranscriptomeGFF3.setBounds(10, 30, 180, 22);
		lblTranscriptomeGFF3.setText("(*GFF) Gene Annotation File:");
		
		
		txtTranscriptomeGFF3 = new Text(shell, SWT.BORDER);
		txtTranscriptomeGFF3.setBounds(200, 30, 550, 22);
		txtTranscriptomeGFF3.addMouseListener(mouse);
		
		if (selectedFile != null && selectedFile.length()>0) {
			txtTranscriptomeGFF3.setText(selectedFile);
		}
		
		btnTranscriptomeGFF3 = new Button(shell, SWT.NONE);
		btnTranscriptomeGFF3.setBounds(760, 30, 25, 22);
		btnTranscriptomeGFF3.setText("...");
		btnTranscriptomeGFF3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, selectedFile, txtTranscriptomeGFF3);
			}
		});
		
		lblReferenceFile = new Label(shell, SWT.NONE);
		lblReferenceFile.setBounds(10, 70, 180, 22);
		lblReferenceFile.setText("(*FASTA) Reference Genome:");
		
		txtReferenceFile = new Text(shell, SWT.BORDER);
		txtReferenceFile.setBounds(200, 70, 550, 22);
		txtReferenceFile.addMouseListener(mouse);
		// Suggest the latest stored genome
		String historyReference = HistoryManager.getHistory(selectedFile, HistoryManager.KEY_REFERENCE_FILE);
		if (historyReference!=null) txtReferenceFile.setText(historyReference);
		
		btnReferenceFile = new Button(shell, SWT.NONE);
		btnReferenceFile.setBounds(760, 70, 25, 22);
		btnReferenceFile.setText("...");
		btnReferenceFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, selectedFile, txtReferenceFile);
			}
		});
		
		lblOutputPrefix = new Label(shell, SWT.NONE);
		lblOutputPrefix.setBounds(10, 110, 180, 22);
		lblOutputPrefix.setText("Output Prefix:");
		
		txtOutputPrefix = new Text(shell, SWT.BORDER);
		txtOutputPrefix.setBounds(200, 110, 550, 22);
		txtOutputPrefix.addMouseListener(mouse);
		String suggestedOutPrefix = SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile);
		txtOutputPrefix.setText(suggestedOutPrefix);
		
		
		
		btnOutputPrefix = new Button(shell, SWT.NONE);
		btnOutputPrefix.setBounds(760, 110, 25, 22);
		btnOutputPrefix.setText("...");
		btnOutputPrefix.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.SAVE, selectedFile, txtOutputPrefix);
			}
		});
		
		lblMinProteinLength = new Label(shell, SWT.NONE);
		lblMinProteinLength.setText("Minimum protein length :");
		lblMinProteinLength.setBounds(10, 150, 180, 22);
		
		txtMinProteinLength = new Text(shell, SWT.BORDER);
		txtMinProteinLength.setBounds(200, 150, 100, 22);
		txtMinProteinLength.setText(String.valueOf(VariantAnnotationParameters.DEF_SPLICE_REGION_INTRON));
		txtMinProteinLength.addMouseListener(mouse);
		
		btnCompleteTranscripts = new Button(shell, SWT.CHECK);
		btnCompleteTranscripts.setBounds(10, 190, 250, 22);
		btnCompleteTranscripts.setText("Output only complete transcripts");
		
		btnSubmit = new Button(shell, SWT.NONE);
		btnSubmit.setBounds(240, 330, 200, 25);
		btnSubmit.setText("Analyze");
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
		SyncTranscriptomeAnalyzer job = new SyncTranscriptomeAnalyzer("TranscriptomeAnalyzer");
		TranscriptomeAnalyzer instance = new TranscriptomeAnalyzer ();
		job.setInstance(instance);

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
				instance.setGenome(genome);
			} catch (IOException e) {
				e.printStackTrace();
				errors.add(FieldValidator.buildMessage(lblReferenceFile.getText(), "error loading file: "+e.getMessage()));
				txtReferenceFile.setBackground(oc);
			}
		}

		if (txtOutputPrefix.getText() == null|| txtOutputPrefix.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblOutputPrefix.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutputPrefix.setBackground(oc);
		} else {
			job.setOutputPrefix(txtOutputPrefix.getText());
		}
		
		if (txtMinProteinLength.getText() != null && txtMinProteinLength.getText().length()!=0) {
			if (!FieldValidator.isNumeric(txtMinProteinLength.getText(),new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblMinProteinLength.getText(), FieldValidator.ERROR_INTEGER));
				txtMinProteinLength.setBackground(oc);
			} else {
				instance.setMinProteinLength(Integer.parseInt(txtMinProteinLength.getText()));				
			}
		}
		
			
		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shell, "Variants Functional Annotator");
			return;
		}
		if(btnCompleteTranscripts.getSelection()) {
			instance.setSelectCompleteProteins(true);
		}
		
		
		String outputPrefix = txtOutputPrefix.getText();
		String logFilename = LoggingHelper.getLoggerFilename(outputPrefix,"TRA");
		job.setLogName(logFilename);
		job.setNameProgressBar(new File(outputPrefix).getName());
		try {
			job.schedule();
		} catch (Exception e) {
			MessageDialog.openError(shell," Transcriptome Analyzer Error", e.getMessage());
			e.printStackTrace();
		}
		HistoryManager.saveInHistory(HistoryManager.KEY_REFERENCE_FILE, txtReferenceFile.getText());
		MessageDialog.openInformation(shell,"Transcriptome Analyzer is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
		shell.dispose();

	}

}
