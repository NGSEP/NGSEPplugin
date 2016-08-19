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

import net.sf.ngsep.control.SyncVCFFunctionalAnnotator;
import net.sf.ngsep.utilities.EclipseProjectHelper;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.genome.ReferenceGenome;

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
 * 
 * @author Juan Camilo Quintero
 *
 */
public class MainVCFFunctionalAnnotator {
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
	private Label lblBpUpstream;
	private Text txtBpUpstream;
	private Label lblBpDownstream;
	private Text txtBpDownstream;
	
	//Default parameters
	public static final int DEF_UPSTREAM=1000;
	public static final int DEF_DOWNSTREAM=300;
	
	
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
		shell.setSize(875, 334);
		shell.setText("Variants Functional Annotator");
		shell.setLocation(10, 10);

		MouseListenerNgsep mouse = new MouseListenerNgsep();
		lblFile = new Label(shell, SWT.NONE);
		lblFile.setBounds(10, 31, 215, 21);
		lblFile.setText("(*VCF) Variants File:");
		
		txtFile = new Text(shell, SWT.BORDER);
		txtFile.setBounds(242, 28, 545, 21);
		if (selectedFile != null && selectedFile.length()>0) {
			txtFile.setText(selectedFile);
		}
		txtFile.addMouseListener(mouse);
		
		btnFile = new Button(shell, SWT.NONE);
		btnFile.setBounds(798, 27, 21, 25);
		btnFile.setText("...");
		btnFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, selectedFile,txtFile);
			}
		});
		

		lblTranscriptomeGFF3 = new Label(shell, SWT.NONE);
		lblTranscriptomeGFF3.setBounds(10, 73, 215, 21);
		lblTranscriptomeGFF3.setText("(*GFF) Gene Annotation File:");
		
		
		txtTranscriptomeGFF3 = new Text(shell, SWT.BORDER);
		txtTranscriptomeGFF3.setBounds(242, 69, 545, 21);
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
			MessageDialog.openError(shell, " Functional Annotator error","error while trying to place the reference path history most recently used"+ e.getMessage());
		}
		
		btnTranscriptomeGFF3 = new Button(shell, SWT.NONE);
		btnTranscriptomeGFF3.setBounds(798, 67, 21, 25);
		btnTranscriptomeGFF3.setText("...");
		btnTranscriptomeGFF3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, txtTranscriptomeGFF3.getText(), txtTranscriptomeGFF3);
			}
		});
		

		lblReferenceFile = new Label(shell, SWT.NONE);
		lblReferenceFile.setBounds(10, 115, 230, 21);
		lblReferenceFile.setText("(*FASTA) Reference Genome:");
		

		txtReferenceFile = new Text(shell, SWT.BORDER);
		txtReferenceFile.setBounds(242, 111, 545, 21);
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
			MessageDialog.openError(shell, " Functional Annotator error","error while trying to place the reference path history most recently used"+ e.getMessage());
		}
		
		btnReferenceFile = new Button(shell, SWT.NONE);
		btnReferenceFile.setBounds(798, 111, 21, 25);
		btnReferenceFile.setText("...");
		btnReferenceFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, selectedFile,txtReferenceFile);
			}
		});
		
		

		txtOutputFile = new Text(shell, SWT.BORDER);
		txtOutputFile.setBounds(242, 153, 545, 21);
		txtOutputFile.addMouseListener(mouse);
		String suggestedOutPrefix = SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile);
		txtOutputFile.setText(suggestedOutPrefix+"_Annotated.vcf");
		
		lblOutputFile = new Label(shell, SWT.NONE);
		lblOutputFile.setBounds(10, 157, 215, 21);
		lblOutputFile.setText("(*VCF) Output File:");
		
		btnOutputFile = new Button(shell, SWT.NONE);
		btnOutputFile.setBounds(798, 153, 21, 25);
		btnOutputFile.setText("...");
		btnOutputFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.SAVE, selectedFile, txtOutputFile);
			}
		});
		
		lblBpUpstream= new Label(shell, SWT.NONE);
		lblBpUpstream.setText("Bp Upstream :");
		lblBpUpstream.setBounds(10, 213, 110, 21);
		
		txtBpUpstream = new Text(shell, SWT.BORDER);
		txtBpUpstream.setBounds(140, 213, 50, 23);
		txtBpUpstream.setText(String.valueOf(DEF_UPSTREAM));
		txtBpUpstream.addMouseListener(mouse);
		
		lblBpDownstream = new Label(shell, SWT.NONE);
		lblBpDownstream.setText("Bp Downstream :");
		lblBpDownstream.setBounds(210, 213, 124, 21);
		
		txtBpDownstream = new Text(shell, SWT.BORDER);
		txtBpDownstream.setBounds(351, 213, 50, 23);
		txtBpDownstream.setText(String.valueOf(DEF_DOWNSTREAM));
		txtBpDownstream.addMouseListener(mouse);

		btnSubmit = new Button(shell, SWT.NONE);
		btnSubmit.setBounds(245, 254, 167, 25);
		btnSubmit.setText("Functional Annotation");
		btnSubmit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();
			}
		});
		

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(433, 254, 167, 25);
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
		SyncVCFFunctionalAnnotator syncVariantsFunctional = new SyncVCFFunctionalAnnotator("Variants Functional Annotator");
		
		if (txtFile.getText() == null || txtFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtFile.setBackground(oc);
		} else {
			syncVariantsFunctional.setVariantsFile(txtFile.getText());
		}

		if (txtTranscriptomeGFF3.getText() == null || txtTranscriptomeGFF3.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblTranscriptomeGFF3.getText(), FieldValidator.ERROR_MANDATORY));
			txtTranscriptomeGFF3.setBackground(oc);
		} else {
			syncVariantsFunctional.setTranscriptomeMap(txtTranscriptomeGFF3.getText());
		}

		if (txtReferenceFile.getText() == null || txtReferenceFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblReferenceFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtReferenceFile.setBackground(oc);
		} else {
			try {
				ReferenceGenome genome = ReferenceGenomesFactory.getInstance().getGenome(txtReferenceFile.getText(), shell);
				syncVariantsFunctional.setGenome(genome);
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
			syncVariantsFunctional.setOutputFile(txtOutputFile.getText());
		}
		
		if (txtBpDownstream.getText() != null && txtBpDownstream.getText().length()!=0) {
			if (!FieldValidator.isNumeric(txtBpDownstream.getText(),new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblBpDownstream.getText(), FieldValidator.ERROR_INTEGER));
				txtBpDownstream.setBackground(oc);
			} else {
				syncVariantsFunctional.setBpDownstream(Integer.parseInt(txtBpDownstream.getText()));					
			}
		}
		
		if (txtBpUpstream.getText() != null && txtBpUpstream.getText().length()!=0) {
			if (!FieldValidator.isNumeric(txtBpUpstream.getText(),new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblBpUpstream.getText(), FieldValidator.ERROR_INTEGER));
				txtBpUpstream.setBackground(oc);
			} else {
				syncVariantsFunctional.setBpUpstream(Integer.parseInt(txtBpUpstream.getText()));				
			}
		}
			
		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shell, "Variants Functional Annotator");
			return;
		}
		
		try {
			String directoryProject = EclipseProjectHelper.findProjectDirectory(selectedFile);
			String historyOne = HistoryManager.createPathRecordGeneral(directoryProject);
			HistoryManager.createPathRecordFiles(historyOne, txtReferenceFile.getText().toString());
			String history = HistoryManager.createPathRecordGff3(directoryProject);
			HistoryManager.createPathRecordFiles(history,txtTranscriptomeGFF3.getText());
		} catch (Exception e) {
			MessageDialog.openError(shell, " Variants Functional Annotator Error","error while trying to place the reference path history most recently used"+ e.getMessage());
			return;
		}
		
		String outputFile = txtOutputFile.getText();
		String logFilename = LoggingHelper.getLoggerFilename(outputFile,"VFA");
		syncVariantsFunctional.setLogName(logFilename);
		syncVariantsFunctional.setNameProgressBar(new File(outputFile).getName());
		try {
			syncVariantsFunctional.schedule();
		} catch (Exception e) {
			MessageDialog.openError(shell," Variants Functional Annotator Error", e.getMessage());
			e.printStackTrace();
		}
		MessageDialog.openInformation(shell,"Variants Functional Annotator is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
		shell.dispose();

	}

}
