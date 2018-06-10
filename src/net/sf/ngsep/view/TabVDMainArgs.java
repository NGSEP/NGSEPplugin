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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sf.ngsep.utilities.EclipseProjectHelper;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.alignments.ReadAlignment;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Daniel Cruz
 *
 */
public class TabVDMainArgs extends Composite {

	private String initialAlignmentsFile;
	private String suggestedOutputPath;

	

	/**
	 * @param initialAlignmentsFile the initialAlignmentsFile to set
	 */
	public void setInitialAlignmentsFile(String initialAlignmentsFile) {
		this.initialAlignmentsFile = initialAlignmentsFile;
	}

	/**
	 * @param suggestedOutputPath the suggestedOutputPath to set
	 */
	public void setSuggestedOutputPath(String suggestedOutputPath) {
		this.suggestedOutputPath = suggestedOutputPath;
	}

	private Label lblReferenceFile;
	private Text txtReferenceFile;
	private Button btnRef;

	private Label lblKnownSVs;
	private Text txtKnownSVs;
	private Button btnKnownSVs;

	private Label lblIgnoreBase;
	private Label lblIgnoreBases5;
	private Text txtIgnoreBases5;
	private Label lblIgnoreBases3;
	private Text txtIgnoreBases3;
	private Button btnRunRDChk;
	private Button btnRunRepeatsChk;
	private Button btnSkipSNVSDetection;
	private Button btnRunRPChk;
	private Button btnSkipNewCNVChk;


	// Fields for one sample
	private Label lblDestFile;
	private Text txtDestFile;
	private Button btnDest;
	private Label lblFile;
	private Text txtFile;
	private Button btnFile;

	private Label lblKnownVariantsFile;
	private Text txtKnownVariantsFile;
	private Button btnKnownVariantsFile;

	private Label lblSampleId;
	private Text txtSampleId;
	
	private Label lblMinMQ;
	private Text txtMinMQ;

	


	//	Only Multi
	private Label lblNumberOfProcessors;
	private Text txtNumberOfProcessors;



	private MouseListenerNgsep mouse = new MouseListenerNgsep();
	private ArrayList<String> errorsOne = new ArrayList<String>();

	private Composite parent;
	private char behavior;
	
	
	
	//....
	private Label lblKnownSTRs;
	private Text txtKnownSTRs;
	private Button btnKnownSTRs;


	public TabVDMainArgs(final Composite parent, int style, char behavior) {
		super(parent, style);
		this.parent = parent;
		this.behavior = behavior;
	}

	public void paint() {
		if(behavior == MainVariantsDetector.BEHAVIOR_SINGLE) {
			lblFile = new Label(this, SWT.NONE);
			lblFile.setText("(*)File:");
			lblFile.setBounds(10, 20, 190, 22);

			txtFile = new Text(this, SWT.BORDER);
			txtFile.setBounds(210, 20, 540, 22);
			txtFile.addMouseListener(mouse);
			txtFile.setText(initialAlignmentsFile);

			btnFile = new Button(this, SWT.NONE);
			btnFile.setText("...");
			btnFile.setBounds(760, 20, 25, 22);
			btnFile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SpecialFieldsHelper.updateFileTextBox(parent.getShell(), SWT.OPEN, suggestedOutputPath, txtFile);
				}
			});
		}
		
		if(behavior == MainVariantsDetector.BEHAVIOR_SINGLE || behavior == MainVariantsDetector.BEHAVIOR_MULTI_COMBINED) {
			lblDestFile = new Label(this, SWT.NONE);
			String text = "(*)Output File";
			if(behavior == MainVariantsDetector.BEHAVIOR_SINGLE) text+=" Prefix";
			lblDestFile.setText(text +":");
			lblDestFile.setBounds(10, 60, 190, 20);
	
			txtDestFile = new Text(this, SWT.BORDER);
			txtDestFile.setBounds(210, 60, 540, 22);
			txtDestFile.addMouseListener(mouse);
			if(suggestedOutputPath==null) suggestedOutputPath = extractPrefix(initialAlignmentsFile);
			txtDestFile.setText(suggestedOutputPath);
			
			btnDest = new Button(this, SWT.NONE);
			btnDest.setBounds(760, 60, 25, 22);
			btnDest.setText("...");
			btnDest.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SpecialFieldsHelper.updateFileTextBox(parent.getShell(), SWT.SAVE, suggestedOutputPath, txtDestFile);
				}
			});
		}

		

		lblReferenceFile = new Label(this, SWT.NONE);
		lblReferenceFile.setBounds(10, 100, 190, 22);
		lblReferenceFile.setText("(*)Reference File:");

		txtReferenceFile = new Text(this, SWT.BORDER);
		txtReferenceFile.setBounds(210, 100, 540, 22);
		txtReferenceFile.addMouseListener(mouse);

		String historyReference =null;
		try {
			String directoryProject = EclipseProjectHelper.findProjectDirectory(suggestedOutputPath);
			String historyFile = HistoryManager.createPathRecordGeneral(directoryProject);
			historyReference = HistoryManager.getPathRecordReference(historyFile);
		} catch (IOException e) {
			MessageDialog.openError(parent.getShell(), "Variants Detector Error","Error loading the latest reference genome: "+ e.getMessage());
		}
		if (historyReference!=null) {
			txtReferenceFile.setText(historyReference);
		}		

		btnRef = new Button(this, SWT.NONE);
		btnRef.setBounds(760, 100, 25, 22);
		btnRef.setText("...");
		btnRef.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(parent.getShell(), SWT.OPEN, suggestedOutputPath, txtReferenceFile);
			}
		});

		if(behavior != MainVariantsDetector.BEHAVIOR_MULTI_COMBINED) {
			
			lblKnownSVs = new Label(this, SWT.NONE);
			lblKnownSVs.setText("Known SVs (.gff) File:");
			lblKnownSVs.setBounds(10, 140, 190, 22);
	
			txtKnownSVs = new Text(this, SWT.BORDER);
			txtKnownSVs.setBounds(210, 140, 540, 22);
			txtKnownSVs.addMouseListener(mouse);
	
			btnKnownSVs = new Button(this, SWT.NONE);
			btnKnownSVs.setText("...");
			btnKnownSVs.setBounds(760, 140, 25, 22);
			btnKnownSVs.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SpecialFieldsHelper.updateFileTextBox(parent.getShell(), SWT.OPEN, suggestedOutputPath, txtKnownSVs);
				}
			});
		}
		lblKnownSTRs = new Label(this, SWT.NONE);
		lblKnownSTRs.setText("known STRs File:");
		lblKnownSTRs.setBounds(10, 180, 190, 22);
		
		txtKnownSTRs = new Text(this, SWT.BORDER);
		txtKnownSTRs.setBounds(210, 180, 540, 22);
		
		btnKnownSTRs = new Button(this, SWT.NONE);
		btnKnownSTRs.setText("...");
		btnKnownSTRs.setBounds(760, 180, 25, 22);
		btnKnownSTRs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(parent.getShell(), SWT.OPEN, suggestedOutputPath, txtKnownSTRs);
			}
		});
			
		if(behavior != MainVariantsDetector.BEHAVIOR_WIZARD){
			lblKnownVariantsFile = new Label(this, SWT.NONE);
			lblKnownVariantsFile.setText("Known Variants (.vcf) File:");
			lblKnownVariantsFile.setBounds(10, 220, 190, 22);

			txtKnownVariantsFile = new Text(this, SWT.BORDER);
			txtKnownVariantsFile.setBounds(210, 220, 540, 22);
			txtKnownVariantsFile.addMouseListener(mouse);

			btnKnownVariantsFile = new Button(this, SWT.NONE);
			btnKnownVariantsFile.setText("...");
			btnKnownVariantsFile.setBounds(760, 220, 25, 22);
			btnKnownVariantsFile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SpecialFieldsHelper.updateFileTextBox(parent.getShell(), SWT.OPEN, suggestedOutputPath, txtKnownVariantsFile);
				}
			});
		}
		if(behavior != MainVariantsDetector.BEHAVIOR_MULTI_COMBINED) {
			btnRunRepeatsChk = new Button(this, SWT.CHECK);
			btnRunRepeatsChk.setText("Run detection of repetitive regions");
			btnRunRepeatsChk.setBounds(10, 260, 300, 22);

			btnRunRDChk = new Button(this, SWT.CHECK);	
			btnRunRDChk.setText("Run read depth (RD) analysis");
			btnRunRDChk.setBounds(10, 300, 300, 22);
			btnRunRDChk.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if(btnRunRDChk.getSelection()) {
						btnSkipNewCNVChk.setSelection(false);
						btnSkipNewCNVChk.setEnabled(true);
					} else {
						btnSkipNewCNVChk.setSelection(true);
						btnSkipNewCNVChk.setEnabled(false);
					}
				}
			});
			btnSkipNewCNVChk = new Button(this, SWT.CHECK);	
			btnSkipNewCNVChk.setText("Skip new CNV detection with RD");
			btnSkipNewCNVChk.setBounds(10, 340, 300, 22);

			btnRunRPChk = new Button(this, SWT.CHECK);
			btnRunRPChk.setBounds(10, 380, 300, 22);
			btnRunRPChk.setText("Run read pair (RP) analysis");

			btnSkipSNVSDetection = new Button(this, SWT.CHECK);
			btnSkipSNVSDetection.setText("Skip detection of SNVs and small indels");
			btnSkipSNVSDetection.setBounds(10, 420, 300, 20);
		}
		
		
		//Second half
		
		lblMinMQ = new Label(this, SWT.NONE);
		lblMinMQ.setBounds(400, 260, 320, 22);
		lblMinMQ.setText("Minimum mapping quality unique alignments:");
		
		txtMinMQ = new Text(this, SWT.BORDER);
		txtMinMQ.setBounds(720, 260, 70, 22);
		txtMinMQ.addMouseListener(mouse);
		txtMinMQ.setText(""+ReadAlignment.DEF_MIN_MQ_UNIQUE_ALIGNMENT);

		lblIgnoreBase=new Label(this, SWT.NONE);
		lblIgnoreBase.setText("Ignore Basepairs :");
		lblIgnoreBase.setBounds(400, 300, 130, 22);

		lblIgnoreBases5 = new Label(this, SWT.NONE);
		lblIgnoreBases5.setText("5':");
		lblIgnoreBases5.setBounds(540, 300, 30, 22);

		txtIgnoreBases5 = new Text(this, SWT.BORDER);
		txtIgnoreBases5.setText("0");
		txtIgnoreBases5.setBounds(580, 300, 50, 22);
		txtIgnoreBases5.addMouseListener(mouse);

		lblIgnoreBases3 = new Label(this, SWT.NONE);
		lblIgnoreBases3.setText("3':");
		lblIgnoreBases3.setBounds(640, 300, 30, 22);

		txtIgnoreBases3 = new Text(this, SWT.BORDER);
		txtIgnoreBases3.setText("0");
		txtIgnoreBases3.setBounds(680, 300, 50, 22);
		txtIgnoreBases3.addMouseListener(mouse);
		

		if(behavior == MainVariantsDetector.BEHAVIOR_MULTI_INDIVIDUAL) {

			lblNumberOfProcessors = new Label(this, SWT.NONE);
			lblNumberOfProcessors.setText("Number Of Processors:");
			lblNumberOfProcessors.setBounds(400, 340, 240, 22);	
			txtNumberOfProcessors = new Text(this, SWT.BORDER);
			txtNumberOfProcessors.setBounds(650, 340, 100, 22);
			//by default it suggests the user to use all possible processor minus 1, to be able to use his computer.
			int processors = Runtime.getRuntime().availableProcessors()-1;
			txtNumberOfProcessors.setText(String.valueOf(processors));		

		}
		if(behavior == MainVariantsDetector.BEHAVIOR_SINGLE) {
			lblSampleId = new Label(this, SWT.NONE);
			lblSampleId.setText("(*)Sample Id:");
			lblSampleId.setBounds(400, 340, 120, 22);

			txtSampleId = new Text(this, SWT.BORDER);
			txtSampleId.setBounds(530, 340, 250, 22);
			txtSampleId.addMouseListener(mouse);
			try {
				Set<String> sampleIds = MainMultiVariantsDetector.extractSampleIds(parent.getShell(), initialAlignmentsFile);
				if(sampleIds.size()==1) {
					txtSampleId.setText(sampleIds.iterator().next());
				}
			} catch (IOException e) {
				MessageDialog.openError(parent.getShell(), " Variants Detector Error","error while trying to obtain read groups from the alignments file"+ e.getMessage());
			}
		}
	}

	public Map<String,Object> getParams(){

		// Common Fields
		// This map stores parameters set by the user that are common for all samples
		errorsOne.clear();
		Map<String,Object> commonUserParameters = new TreeMap<>();
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;


		String directoryProject = null;

		if (behavior==MainVariantsDetector.BEHAVIOR_SINGLE || behavior == MainVariantsDetector.BEHAVIOR_MULTI_COMBINED){
			//Validation of fields that are only present in the screen to find variants for a single sample
			if (txtDestFile.getText() == null || txtDestFile.getText().equals("")) {
				errorsOne.add(FieldValidator.buildMessage(lblDestFile.getText(), FieldValidator.ERROR_MANDATORY));
				txtDestFile.setBackground(oc);
			}else{
				commonUserParameters.put("destFile", txtDestFile.getText());
			}
		}
		if (behavior==MainVariantsDetector.BEHAVIOR_SINGLE){
		
			String finalAlignmentsFile = txtFile.getText(); 
			if (finalAlignmentsFile == null || finalAlignmentsFile.length()==0) {
				errorsOne.add(FieldValidator.buildMessage(finalAlignmentsFile, FieldValidator.ERROR_MANDATORY));
				txtFile.setBackground(oc);
			} else if (FieldValidator.isFileExistenceWithData(finalAlignmentsFile)) {
				commonUserParameters.put("aliFile", finalAlignmentsFile);
			} else {
				errorsOne.add(FieldValidator.buildMessage(lblFile.getText(), FieldValidator.ERROR_FILE_EMPTY));
				txtFile.setBackground(oc);
			}
			if ( txtSampleId.getText() == null || txtSampleId.getText().equals("")) {
				errorsOne.add(FieldValidator.buildMessage(lblSampleId.getText(), FieldValidator.ERROR_MANDATORY));
				txtSampleId.setBackground(oc);
			} else{
				commonUserParameters.put("sampleId", txtSampleId.getText());
			}
		}

		if(behavior == MainVariantsDetector.BEHAVIOR_MULTI_INDIVIDUAL) {
			if (txtNumberOfProcessors.getText() != null && !txtNumberOfProcessors.getText().equals("")) {
				if (!FieldValidator.isNumeric(txtNumberOfProcessors.getText(),new Integer(0))) {
					errorsOne.add(FieldValidator.buildMessage(lblNumberOfProcessors.getText(), FieldValidator.ERROR_NUMPROCESSORS));
					txtNumberOfProcessors.setBackground(oc);
				} else {
					commonUserParameters.put("numProc", txtNumberOfProcessors.getText());
				}
			} else {
				FieldValidator.buildMessage(lblNumberOfProcessors.getText(), FieldValidator.ERROR_MANDATORY);
			}

		}

		if(behavior != MainVariantsDetector.BEHAVIOR_WIZARD){
			if (txtKnownVariantsFile.getText() != null && !txtKnownVariantsFile.getText().equals("")) {
				if (FieldValidator.isFileExistenceWithData(txtKnownVariantsFile.getText())) {
					commonUserParameters.put("KnownVariantsFile", txtKnownVariantsFile.getText());
				} else {
					errorsOne.add(FieldValidator.buildMessage(lblKnownVariantsFile.getText(), FieldValidator.ERROR_FILE_EMPTY));
					txtKnownVariantsFile.setBackground(oc);
				}
			}
		}

		// Validation for reference file
		String refFile = txtReferenceFile.getText();
		if (refFile == null || refFile.length()==0) {
			errorsOne.add(FieldValidator.buildMessage(lblReferenceFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtReferenceFile.setBackground(oc);
		} else if (FieldValidator.isFileExistenceWithData(refFile)) {
			commonUserParameters.put("ReferenceFile", refFile);
		} else {
			errorsOne.add(FieldValidator.buildMessage(lblReferenceFile.getText(), FieldValidator.ERROR_FILE_EMPTY));
			txtReferenceFile.setBackground(oc);
		}
		try {
			directoryProject = EclipseProjectHelper.findProjectDirectory(suggestedOutputPath);
			String routeRef = HistoryManager.createPathRecordGeneral(directoryProject);
			HistoryManager.createPathRecordFiles(routeRef, txtReferenceFile.getText().toString());
		} catch (Exception e) {
			errorsOne.add(FieldValidator.buildMessage(" Error while trying to locate the reference path history most recently used",FieldValidator.ERROR_FILE_EMPTY));

		}

		if (txtIgnoreBases3.getText() != null && !txtIgnoreBases3.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtIgnoreBases3.getText(),new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblIgnoreBases3.getText(), FieldValidator.ERROR_INTEGER));
				txtIgnoreBases3.setBackground(oc);
			}else{
				commonUserParameters.put("BasesToIgnore3P", txtIgnoreBases3.getText());
			}
		}

		if (txtIgnoreBases5.getText() != null && !txtIgnoreBases5.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtIgnoreBases5.getText(), new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblIgnoreBases5.getText(), FieldValidator.ERROR_INTEGER));
				txtIgnoreBases5.setBackground(oc);
			}else{
				commonUserParameters.put("BasesToIgnore5P", txtIgnoreBases5.getText());
			}
		}
		
		if (txtMinMQ.getText() != null && txtMinMQ.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMinMQ.getText(),new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblMinMQ.getText(), FieldValidator.ERROR_INTEGER));
				txtMinMQ.setBackground(oc);
			} else {
				commonUserParameters.put("MinMQ", txtMinMQ.getText());
			}
		}
		if(behavior != MainVariantsDetector.BEHAVIOR_MULTI_COMBINED) {
			if (txtKnownSVs.getText() != null && !txtKnownSVs.getText().equals("")) {
				if (FieldValidator.isFileExistenceWithData(txtKnownSVs.getText())) {
					commonUserParameters.put("KnownSVsFile", txtKnownSVs.getText());
				} else {
					errorsOne.add(FieldValidator.buildMessage(lblKnownSVs.getText(), FieldValidator.ERROR_FILE_EMPTY));
					txtKnownSVs.setBackground(oc);
				}
			}
		}
		if (txtKnownSTRs.getText() != null && !txtKnownSTRs.getText().equals("")) {
			if (FieldValidator.isFileExistenceWithData(txtKnownSTRs.getText())) {
				commonUserParameters.put("KnownSTRsFile", txtKnownSTRs.getText());
			} else {
				errorsOne.add(FieldValidator.buildMessage(lblKnownSTRs.getText(), FieldValidator.ERROR_FILE_EMPTY));
				txtKnownSTRs.setBackground(oc);
			}
		}
		if(behavior!=MainVariantsDetector.BEHAVIOR_MULTI_COMBINED) {
			if (btnRunRepeatsChk.getSelection()) {
				commonUserParameters.put("FindRepeats", true);
			}

			if (btnRunRDChk.getSelection()) {
				commonUserParameters.put("RunRDAnalysis", true);
			}
			
			if (btnRunRPChk.getSelection()) {
				commonUserParameters.put("RunRPAnalysis", true);
			}

			if (btnSkipNewCNVChk.getSelection()) {
				commonUserParameters.put("FindNewCNVs", false);
			}
			
			if (btnSkipSNVSDetection.getSelection()) {
				commonUserParameters.put("FindSNVs", false);
			}
		}

		return errorsOne.isEmpty()?commonUserParameters:null;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public ArrayList<String> getErrors() {
		return errorsOne;
	}

	public static String extractPrefix(String samFile){
		int index = samFile.lastIndexOf(".");
		if(index <0) return samFile;
		String nameBam= samFile.substring(0,index);
		if (nameBam.contains("Sorted")) {
			nameBam = nameBam.substring(0,samFile.lastIndexOf("Sorted") - 1);
			return nameBam;
		} else if (nameBam.contains("sorted")) {
			nameBam = nameBam.substring(0,samFile.lastIndexOf("sorted") - 1);
			return nameBam;
		} else {
			return nameBam;
		}
	}
}
