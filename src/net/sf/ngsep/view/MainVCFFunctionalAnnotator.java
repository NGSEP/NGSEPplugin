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
import ngsep.transcriptome.VariantAnnotationParameters;
import ngsep.vcf.VCFFunctionalAnnotator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Juan Camilo Quintero
 * @author Jorge Duitama
 */
public class MainVCFFunctionalAnnotator implements SingleFileInputWindow {
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
	private Label lblSpliceDonorOffset;
	private Text txtSpliceDonorOffset;
	private Label lblSpliceAcceptorOffset;
	private Text txtSpliceAcceptorOffset;
	private Label lblSpliceRegionIntronOffset;
	private Text txtSpliceRegionIntronOffset;
	private Label lblSpliceRegionExonOffset;
	private Text txtSpliceRegionExonOffset;
	
	
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
			MessageDialog.openError(shell, " Functional Annotator error","error while trying to place the reference path history most recently used"+ e.getMessage());
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
			MessageDialog.openError(shell, " Functional Annotator error","error while trying to place the reference path history most recently used"+ e.getMessage());
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
		lblOutputFile.setText("(*VCF) Output File:");
		
		txtOutputFile = new Text(shell, SWT.BORDER);
		txtOutputFile.setBounds(200, 150, 550, 22);
		txtOutputFile.addMouseListener(mouse);
		String suggestedOutPrefix = SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile);
		txtOutputFile.setText(suggestedOutPrefix+"_Annotated.vcf");
		
		
		
		btnOutputFile = new Button(shell, SWT.NONE);
		btnOutputFile.setBounds(760, 150, 25, 22);
		btnOutputFile.setText("...");
		btnOutputFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.SAVE, selectedFile, txtOutputFile);
			}
		});
		
		Label lblSpliceOffsets = new Label(shell, SWT.NONE);
		lblSpliceOffsets.setBounds(10, 190, 250, 22);
		lblSpliceOffsets.setText("Splice offsets:");
		lblSpliceOffsets.setFont(new Font(Display.getCurrent(), "Arial", 10, SWT.BOLD));
		
		lblSpliceDonorOffset = new Label(shell, SWT.NONE);
		lblSpliceDonorOffset.setText("Donor :");
		lblSpliceDonorOffset.setBounds(10, 230, 120, 22);
		
		txtSpliceDonorOffset = new Text(shell, SWT.BORDER);
		txtSpliceDonorOffset.setBounds(140, 230, 50, 22);
		txtSpliceDonorOffset.setText(String.valueOf(VariantAnnotationParameters.DEF_SPLICE_DONOR));
		txtSpliceDonorOffset.addMouseListener(mouse);
		
		lblSpliceAcceptorOffset = new Label(shell, SWT.NONE);
		lblSpliceAcceptorOffset.setText("Acceptor :");
		lblSpliceAcceptorOffset.setBounds(200, 230, 120, 22);
		
		txtSpliceAcceptorOffset = new Text(shell, SWT.BORDER);
		txtSpliceAcceptorOffset.setBounds(330, 230, 50, 22);
		txtSpliceAcceptorOffset.setText(String.valueOf(VariantAnnotationParameters.DEF_SPLICE_ACCEPTOR));
		txtSpliceAcceptorOffset.addMouseListener(mouse);
		
		lblBpUpstream= new Label(shell, SWT.NONE);
		lblBpUpstream.setText("Bp Upstream :");
		lblBpUpstream.setBounds(400, 230, 120, 22);
		
		txtBpUpstream = new Text(shell, SWT.BORDER);
		txtBpUpstream.setBounds(530, 230, 50, 22);
		txtBpUpstream.setText(String.valueOf(VariantAnnotationParameters.DEF_UPSTREAM));
		txtBpUpstream.addMouseListener(mouse);
		
		lblBpDownstream = new Label(shell, SWT.NONE);
		lblBpDownstream.setText("Bp Downstream :");
		lblBpDownstream.setBounds(600, 230, 120, 22);
		
		txtBpDownstream = new Text(shell, SWT.BORDER);
		txtBpDownstream.setBounds(730, 230, 50, 22);
		txtBpDownstream.setText(String.valueOf(VariantAnnotationParameters.DEF_DOWNSTREAM));
		txtBpDownstream.addMouseListener(mouse);

		lblSpliceRegionIntronOffset = new Label(shell, SWT.NONE);
		lblSpliceRegionIntronOffset.setText("Region intron :");
		lblSpliceRegionIntronOffset.setBounds(10, 270, 120, 22);
		
		txtSpliceRegionIntronOffset = new Text(shell, SWT.BORDER);
		txtSpliceRegionIntronOffset.setBounds(140, 270, 50, 22);
		txtSpliceRegionIntronOffset.setText(String.valueOf(VariantAnnotationParameters.DEF_SPLICE_REGION_INTRON));
		txtSpliceRegionIntronOffset.addMouseListener(mouse);
		
		lblSpliceRegionExonOffset = new Label(shell, SWT.NONE);
		lblSpliceRegionExonOffset.setText("Region exon :");
		lblSpliceRegionExonOffset.setBounds(200, 270, 120, 22);
		
		txtSpliceRegionExonOffset = new Text(shell, SWT.BORDER);
		txtSpliceRegionExonOffset.setBounds(330, 270, 50, 22);
		txtSpliceRegionExonOffset.setText(String.valueOf(VariantAnnotationParameters.DEF_SPLICE_REGION_EXON));
		txtSpliceRegionExonOffset.addMouseListener(mouse);
		
		btnSubmit = new Button(shell, SWT.NONE);
		btnSubmit.setBounds(240, 330, 200, 25);
		btnSubmit.setText("Functional Annotation");
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
		SyncVCFFunctionalAnnotator job = new SyncVCFFunctionalAnnotator("Variants Functional Annotator");
		VCFFunctionalAnnotator instance = new VCFFunctionalAnnotator ();
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
		
		if (txtBpUpstream.getText() != null && txtBpUpstream.getText().length()!=0) {
			if (!FieldValidator.isNumeric(txtBpUpstream.getText(),new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblBpUpstream.getText(), FieldValidator.ERROR_INTEGER));
				txtBpUpstream.setBackground(oc);
			} else {
				instance.setOffsetUpstream(Integer.parseInt(txtBpUpstream.getText()));				
			}
		}
		
		if (txtBpDownstream.getText() != null && txtBpDownstream.getText().length()!=0) {
			if (!FieldValidator.isNumeric(txtBpDownstream.getText(),new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblBpDownstream.getText(), FieldValidator.ERROR_INTEGER));
				txtBpDownstream.setBackground(oc);
			} else {
				instance.setOffsetDownstream(Integer.parseInt(txtBpDownstream.getText()));					
			}
		}
		
		if (txtSpliceDonorOffset.getText() != null && txtSpliceDonorOffset.getText().length()!=0) {
			if (!FieldValidator.isNumeric(txtSpliceDonorOffset.getText(),new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblSpliceDonorOffset.getText(), FieldValidator.ERROR_INTEGER));
				txtSpliceDonorOffset.setBackground(oc);
			} else {
				instance.setSpliceDonorOffset(Integer.parseInt(txtSpliceDonorOffset.getText()));					
			}
		}
		
		if (txtSpliceAcceptorOffset.getText() != null && txtSpliceAcceptorOffset.getText().length()!=0) {
			if (!FieldValidator.isNumeric(txtSpliceAcceptorOffset.getText(),new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblSpliceAcceptorOffset.getText(), FieldValidator.ERROR_INTEGER));
				txtSpliceAcceptorOffset.setBackground(oc);
			} else {
				instance.setSpliceAcceptorOffset(Integer.parseInt(txtSpliceAcceptorOffset.getText()));					
			}
		}
		
		if (txtSpliceRegionIntronOffset.getText() != null && txtSpliceRegionIntronOffset.getText().length()!=0) {
			if (!FieldValidator.isNumeric(txtSpliceRegionIntronOffset.getText(),new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblSpliceRegionIntronOffset.getText(), FieldValidator.ERROR_INTEGER));
				txtSpliceRegionIntronOffset.setBackground(oc);
			} else {
				instance.setSpliceRegionIntronOffset(Integer.parseInt(txtSpliceRegionIntronOffset.getText()));					
			}
		}
		
		if (txtSpliceRegionExonOffset.getText() != null && txtSpliceRegionExonOffset.getText().length()!=0) {
			if (!FieldValidator.isNumeric(txtSpliceRegionExonOffset.getText(),new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblSpliceRegionExonOffset.getText(), FieldValidator.ERROR_INTEGER));
				txtSpliceRegionExonOffset.setBackground(oc);
			} else {
				instance.setSpliceRegionExonOffset(Integer.parseInt(txtSpliceRegionExonOffset.getText()));					
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
		job.setLogName(logFilename);
		job.setNameProgressBar(new File(outputFile).getName());
		try {
			job.schedule();
		} catch (Exception e) {
			MessageDialog.openError(shell," Variants Functional Annotator Error", e.getMessage());
			e.printStackTrace();
		}
		MessageDialog.openInformation(shell,"Variants Functional Annotator is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
		shell.dispose();

	}

}
