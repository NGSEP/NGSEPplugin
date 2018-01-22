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
import java.util.List;
import java.util.Map;
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

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */

	//	Basic Fields For all cases including Wizard and Multi
	private String outputDirectory;


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
	private Button btnSkipRDChk;
	private Button btnSkipRepeatsChk;
	private Button btnSkipSNVSDetection;
	private Button btnSkipRPChk;
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

	private File initialAlignmentsFile;


	//	Only Multi
	private Label lblNumberOfProcessors;
	private Text txtNumberOfProcessors;



	private MouseListenerNgsep mouse = new MouseListenerNgsep();
	private ArrayList<String> errorsOne = new ArrayList<String>();

	private Composite parent;
	private char source;
	
	
	
	//....
	private Label lblKnownSTRs;
	private Text txtKnownSTRs;
	private Button btnKnownSTRs;


	public TabVDMainArgs(final Composite parent, int style, char source) {
		super(parent, style);
		this.parent = parent;
		this.source = source;
	}

	public void paint() {
		try {


			lblReferenceFile = new Label(this, SWT.NONE);
			lblReferenceFile.setBounds(10, 74, 190, 20);
			lblReferenceFile.setText("(*)Reference File:");

			txtReferenceFile = new Text(this, SWT.BORDER);
			txtReferenceFile.setBounds(210, 74, 575, 21);
			txtReferenceFile.addMouseListener(mouse);

			String directoryProject = EclipseProjectHelper.findProjectDirectory(outputDirectory);
			String historyFile = HistoryManager.createPathRecordGeneral(directoryProject);
			String historyReference = HistoryManager.getPathRecordReference(historyFile);
			if (historyReference!=null) {
				txtReferenceFile.setText(historyReference);
			}		

			btnRef = new Button(this, SWT.NONE);
			btnRef.setBounds(800, 72, 21, 23);
			btnRef.setText("...");
			btnRef.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SpecialFieldsHelper.updateFileTextBox(parent.getShell(), SWT.OPEN, outputDirectory, txtReferenceFile);
				}
			});

			lblReferenceFile.setBounds(10, 80, 190, 20);
			txtReferenceFile.setBounds(210, 80, 575, 21);
			btnRef.setBounds(800, 80, 21, 23);


			lblKnownSVs = new Label(this, SWT.NONE);
			lblKnownSVs.setText("Known SVs (.gff) File:");
			lblKnownSVs.setBounds(10, 115, 190, 20);

			txtKnownSVs = new Text(this, SWT.BORDER);
			txtKnownSVs.setBounds(210, 115, 575, 21);
			txtKnownSVs.addMouseListener(mouse);

			btnKnownSVs = new Button(this, SWT.NONE);
			btnKnownSVs.setText("...");
			btnKnownSVs.setBounds(800, 115, 21, 25);
			btnKnownSVs.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SpecialFieldsHelper.updateFileTextBox(parent.getShell(), SWT.OPEN, outputDirectory, txtKnownSVs);
				}
			});
			lblKnownSTRs = new Label(this, SWT.NONE);
			lblKnownSTRs.setText("known short tandem repeats File:");
			lblKnownSTRs.setBounds(10, 150, 190, 20);
			
			txtKnownSTRs = new Text(this, SWT.BORDER);
			txtKnownSTRs.setBounds(210, 150, 575, 21);
			
			btnKnownSTRs = new Button(this, SWT.NONE);
			btnKnownSTRs.setText("...");
			btnKnownSTRs.setBounds(800, 150, 21, 25);
			btnKnownSTRs.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SpecialFieldsHelper.updateFileTextBox(parent.getShell(), SWT.OPEN, outputDirectory, txtKnownSTRs);
				}
			});

			btnSkipRepeatsChk = new Button(this, SWT.CHECK);
			btnSkipRepeatsChk.setText("Skip detection of repetitive regions");
			btnSkipRepeatsChk.setBounds(10, 220, 300, 20);

			btnSkipRDChk = new Button(this, SWT.CHECK);	
			btnSkipRDChk.setText("Skip read depth analysis");
			btnSkipRDChk.setBounds(10, 253, 300, 20);
			btnSkipRDChk.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if(btnSkipRDChk.getSelection()) {
						btnSkipNewCNVChk.setSelection(true);
						btnSkipNewCNVChk.setEnabled(false);
						/*					tblCNVAlgorithms.setEnabled(false);
					for ( int i = 0 ; i<tblCNVAlgorithms.getItems().length ; i++){
						tblCNVAlgorithms.getItem(i).setChecked(false);
					}*/
					} else {
						btnSkipNewCNVChk.setSelection(false);
						btnSkipNewCNVChk.setEnabled(true);
						/*					tblCNVAlgorithms.getItem(0).setChecked(true);
					tblCNVAlgorithms.setEnabled(true);*/
					}
				}
			});

			btnSkipNewCNVChk = new Button(this, SWT.CHECK);	
			btnSkipNewCNVChk.setText("Skip new CNV detection with RD");
			btnSkipNewCNVChk.setBounds(10, 283, 300, 20);

			// does it worth to keep?? 
			/*		btnSkipNewCNVChk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnSkipNewCNVChk.getSelection()) {

				} else {

				}
			}
		});*/

			btnSkipRPChk = new Button(this, SWT.CHECK);
			btnSkipRPChk.setBounds(10, 313, 300, 20);
			btnSkipRPChk.setText("Skip read pair analysis");

			btnSkipSNVSDetection = new Button(this, SWT.CHECK);
			btnSkipSNVSDetection.setText("Skip detection of SNVs and small indels");
			btnSkipSNVSDetection.setBounds(10, 343, 300, 20);
			
			lblMinMQ = new Label(this, SWT.NONE);
			lblMinMQ.setBounds(400, 220, 330, 20);
			lblMinMQ.setText("Minimum mapping quality unique alignments:");
			
			txtMinMQ = new Text(this, SWT.BORDER);
			txtMinMQ.setBounds(750, 140, 50, 20);
			txtMinMQ.addMouseListener(mouse);
			txtMinMQ.setText(""+ReadAlignment.DEF_MIN_MQ_UNIQUE_ALIGNMENT);

			lblIgnoreBase=new Label(this, SWT.NONE);
			lblIgnoreBase.setText("Ignore Basepairs :");
			lblIgnoreBase.setBounds(400, 260, 140, 20);

			lblIgnoreBases5 = new Label(this, SWT.NONE);
			lblIgnoreBases5.setText("5':");
			lblIgnoreBases5.setBounds(550, 260, 30, 20);

			txtIgnoreBases5 = new Text(this, SWT.BORDER);
			txtIgnoreBases5.setText("0");
			txtIgnoreBases5.setBounds(590,260,40,21);
			txtIgnoreBases5.addMouseListener(mouse);

			lblIgnoreBases3 = new Label(this, SWT.NONE);
			lblIgnoreBases3.setText("3':");
			lblIgnoreBases3.setBounds(650, 260, 30, 20);

			txtIgnoreBases3 = new Text(this, SWT.BORDER);
			txtIgnoreBases3.setText("0");
			txtIgnoreBases3.setBounds(690, 260, 40, 21);
			txtIgnoreBases3.addMouseListener(mouse);
			


			if(source != MainVariantsDetector.SOURCE_WIZARD){
				lblKnownVariantsFile = new Label(this, SWT.NONE);
				lblKnownVariantsFile.setText("Known Variants (.vcf) File:");
				lblKnownVariantsFile.setBounds(10, 185, 190, 20);

				txtKnownVariantsFile = new Text(this, SWT.BORDER);
				txtKnownVariantsFile.setBounds(210, 185, 575, 21);
				txtKnownVariantsFile.addMouseListener(mouse);

				btnKnownVariantsFile = new Button(this, SWT.NONE);
				btnKnownVariantsFile.setText("...");
				btnKnownVariantsFile.setBounds(800, 185, 21, 25);
				btnKnownVariantsFile.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						SpecialFieldsHelper.updateFileTextBox(parent.getShell(), SWT.OPEN, outputDirectory, txtKnownVariantsFile);
					}
				});
			}

			if(source == MainVariantsDetector.SOURCE_MULTI){ 					//Multi paint
				lblReferenceFile.setBounds(10, 80, 190, 20);
				txtReferenceFile.setBounds(210, 80, 575, 21);
				btnRef.setBounds(800, 80, 21, 23);

				lblNumberOfProcessors = new Label(this, SWT.NONE);
				lblNumberOfProcessors.setText("Number Of Processors:");
				lblNumberOfProcessors.setBounds(400, 340, 240, 21);	
				txtNumberOfProcessors = new Text(this, SWT.BORDER);
				txtNumberOfProcessors.setBounds(650, 340, 100, 21);
				//by default it suggests the user to use all possible processor minus 1, to be able to use his computer.
				int processors = Runtime.getRuntime().availableProcessors()-1;
				txtNumberOfProcessors.setText(String.valueOf(processors));		

			}

			if(source == MainVariantsDetector.SOURCE_SINGLE){ 					//One sample paint 
				lblFile = new Label(this, SWT.NONE);
				lblFile.setText("(*)File:");
				lblFile.setBounds(10, 10, 190, 20);

				txtFile = new Text(this, SWT.BORDER);
				txtFile.setBounds(210, 10, 575, 21);
				txtFile.addMouseListener(mouse);
				txtFile.setText(initialAlignmentsFile.getAbsolutePath());

				btnFile = new Button(this, SWT.NONE);
				btnFile.setText("...");
				btnFile.setBounds(800, 10, 21, 23);
				btnFile.addSelectionListener(new SelectionAdapter() {
					// this method returns me the path where the eclipse runtime files
					// according to separator that has the operating system for windows
					// \ Linux / and I suggested the route for the event of selecting a
					// route
					@Override
					public void widgetSelected(SelectionEvent e) {
						SpecialFieldsHelper.updateFileTextBox(parent.getShell(), SWT.OPEN, outputDirectory, txtFile);
					}
				});

				lblDestFile = new Label(this, SWT.NONE);
				lblDestFile.setText("(*)Output File Prefix:");
				lblDestFile.setBounds(10, 45, 190, 20);

				txtDestFile = new Text(this, SWT.BORDER);
				txtDestFile.setBounds(210, 45, 575, 21);
				txtDestFile.setToolTipText("Please enter your output file, you can use the button next to this field to browse it");
				txtDestFile.addMouseListener(mouse);
				txtDestFile.setText(extractPrefix(initialAlignmentsFile.getAbsolutePath()));

				// in this cycle is validated that this initial selected file to suggest
				// it as the output file without extension and without sorted if he were
				// to take it into the text box Output File


				btnDest = new Button(this, SWT.NONE);
				btnDest.setBounds(800, 45, 21, 23);
				btnDest.setText("...");
				btnDest.addSelectionListener(new SelectionAdapter() {
					// this method returns me the path where the eclipse runtime files
					// according to separator that has the operating system for windows
					// \ Linux / and I suggested the route for the event of selecting a
					// route
					@Override
					public void widgetSelected(SelectionEvent e) {
						SpecialFieldsHelper.updateFileTextBox(parent.getShell(), SWT.SAVE, outputDirectory, txtDestFile);
						outputDirectory = new File(txtDestFile.getText()).getParentFile().getAbsolutePath();
					}
				});

				lblSampleId = new Label(this, SWT.NONE);
				lblSampleId.setText("(*)Sample Id:");
				lblSampleId.setBounds(400, 300, 150, 20);

				txtSampleId = new Text(this, SWT.BORDER);
				txtSampleId.setBounds(560, 300, 240, 21);
				txtSampleId.addMouseListener(mouse);
				try {
					List<String> readGroups = MainMultiVariantsDetector.extractReadGroups(initialAlignmentsFile.getAbsolutePath());
					if(readGroups.size()>0) {
						String sampleId=readGroups.get(0);	
						if (MainMultiVariantsDetector.isSampleIdUnique(readGroups, sampleId)) txtSampleId.setText(sampleId);
					}
				} catch (IOException e) {
					MessageDialog.openError(parent.getShell(), " Variants Detector Error","error while trying to obtain read groups from the alignments file"+ e.getMessage());
				}
			}
				
	

		} catch (Exception e) {
			e.getMessage();
			MessageDialog.openError(parent.getShell(), " Variants Detector Error",""+ e.getMessage());
		}




	}

	public Map<String,Object> getParams(){

		// Common Fields
		// This map stores parameters set by the user that are common for all samples
		errorsOne.clear();
		Map<String,Object> commonUserParameters = new TreeMap<String, Object>();
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;


		String directoryProject = null;

		//For single
		if (source==MainVariantsDetector.SOURCE_SINGLE){
			//Validation of fields that are only present in the screen to find variants for a single sample
			if (txtDestFile.getText() == null || txtDestFile.getText().equals("")) {
				errorsOne.add(FieldValidator.buildMessage(lblDestFile.getText(), FieldValidator.ERROR_MANDATORY));
				txtDestFile.setBackground(oc);
			}else{
				commonUserParameters.put("destFile", txtDestFile.getText());
			}

			String finalAlignmentsFile = txtFile.getText(); 
			if (finalAlignmentsFile == null || finalAlignmentsFile.length()==0) {
				errorsOne.add(FieldValidator.buildMessage(finalAlignmentsFile, FieldValidator.ERROR_MANDATORY));
				txtFile.setBackground(oc);
			} else if (FieldValidator.isFileExistenceWithData(finalAlignmentsFile)) {
				//				vd.setAlignmentsFile(finalAlignmentsFile);
				commonUserParameters.put("aliFile", finalAlignmentsFile);
			} else {
				errorsOne.add(FieldValidator.buildMessage(lblFile.getText(), FieldValidator.ERROR_FILE_EMPTY));
				txtFile.setBackground(oc);
			}
			if ( txtSampleId.getText() == null || txtSampleId.getText().equals("")) {
				errorsOne.add(FieldValidator.buildMessage(lblSampleId.getText(), FieldValidator.ERROR_MANDATORY));
				txtSampleId.setBackground(oc);
			}else{
				commonUserParameters.put("sampleId", txtSampleId.getText());
				//				vd.setSampleId(txtSampleId.getText());
			}
			
			try {
				String pD1 = EclipseProjectHelper.findProjectDirectory(outputDirectory);
				String pD2 = EclipseProjectHelper.findProjectDirectory(txtDestFile.getText());
				if (!pD1.equals(pD2)) {
					errorsOne.add(FieldValidator.buildMessage(lblDestFile.getText(), "Please use an output directory within the same project where the bam file is located"));

				}
			} catch (IOException e) {
				MessageDialog.openError(parent.getShell(), "Error trying to find project directory. ", e.getMessage());
			}
		}

		if(source == MainVariantsDetector.SOURCE_MULTI){ 
			int numProcesses=2;
			int processors = Runtime.getRuntime().availableProcessors();
			if (txtNumberOfProcessors.getText() != null && !txtNumberOfProcessors.getText().equals("")) {
				if (!FieldValidator.isNumeric(txtNumberOfProcessors.getText(),new Integer(0))) {
					errorsOne.add(FieldValidator.buildMessage(lblNumberOfProcessors.getText(), FieldValidator.ERROR_NUMPROCESSORS));
					txtNumberOfProcessors.setBackground(oc);
				}else if(Integer.parseInt(txtNumberOfProcessors.getText())<=processors){
					numProcesses = Integer.parseInt(txtNumberOfProcessors.getText());
					commonUserParameters.put("numProc", numProcesses);
				}else{
					errorsOne.add(lblNumberOfProcessors.getText() + " the number entered in this box must be less than " + processors);
					txtNumberOfProcessors.setBackground(oc);
				}
			}else{
				FieldValidator.buildMessage(lblNumberOfProcessors.getText(), FieldValidator.ERROR_MANDATORY);
			}

		}

		if(source != MainVariantsDetector.SOURCE_WIZARD){
			if (txtKnownVariantsFile.getText() != null && !txtKnownVariantsFile.getText().equals("")) {
				if (FieldValidator.isFileExistenceWithData(txtKnownVariantsFile.getText())) {
					commonUserParameters.put("KnownVariantsFile", txtKnownVariantsFile.getText());
				} else {
					errorsOne.add(FieldValidator.buildMessage(lblKnownVariantsFile.getText(), FieldValidator.ERROR_FILE_EMPTY));
					txtKnownVariantsFile.setBackground(oc);
				}
			}
		}


		if (txtReferenceFile.getText() == null || txtReferenceFile.getText().length()==0) {
			errorsOne.add(FieldValidator.buildMessage(lblReferenceFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtReferenceFile.setBackground(oc);
		}
		try {
			directoryProject = EclipseProjectHelper.findProjectDirectory(outputDirectory);
			String routeRef = HistoryManager.createPathRecordGeneral(directoryProject);
			HistoryManager.createPathRecordFiles(routeRef, txtReferenceFile.getText().toString());
		} catch (Exception e) {

			errorsOne.add(FieldValidator.buildMessage(" Error while trying to locate the reference path history most recently used",FieldValidator.ERROR_FILE_EMPTY));

		}
		
		// Validation for reference file
		String refFile = txtReferenceFile.getText();
		if (refFile != null && refFile.length()>0) {
			if (FieldValidator.isFileExistenceWithData(refFile)) {
				commonUserParameters.put("ReferenceFile", refFile);
			} else {
				errorsOne.add(FieldValidator.buildMessage(lblReferenceFile.getText(), FieldValidator.ERROR_FILE_EMPTY));
				txtReferenceFile.setBackground(oc);
			}
		}


		if (txtIgnoreBases3.getText() != null && !txtIgnoreBases3.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtIgnoreBases3.getText(),new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblIgnoreBases3.getText(), FieldValidator.ERROR_INTEGER));
				txtIgnoreBases3.setBackground(oc);
			}else{
				commonUserParameters.put("BasesToIgnore3P", Byte.parseByte(txtIgnoreBases3.getText()));
			}
		}

		if (txtIgnoreBases5.getText() != null && !txtIgnoreBases5.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtIgnoreBases5.getText(), new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblIgnoreBases5.getText(), FieldValidator.ERROR_INTEGER));
				txtIgnoreBases5.setBackground(oc);
			}else{
				commonUserParameters.put("BasesToIgnore5P", Byte.parseByte(txtIgnoreBases5.getText()));
			}
		}
		
		if (txtMinMQ.getText() != null && txtMinMQ.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMinMQ.getText(),new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblMinMQ.getText(), FieldValidator.ERROR_INTEGER));
				txtMinMQ.setBackground(oc);
			} else {
				commonUserParameters.put("minMQ", Integer.parseInt(txtMinMQ.getText()));
			}
		}

		if (txtKnownSVs.getText() != null && !txtKnownSVs.getText().equals("")) {
			if (FieldValidator.isFileExistenceWithData(txtKnownSVs.getText())) {
				commonUserParameters.put("KnownSVsFile", txtKnownSVs.getText());
			} else {
				errorsOne.add(FieldValidator.buildMessage(lblKnownSVs.getText(), FieldValidator.ERROR_FILE_EMPTY));
				txtKnownSVs.setBackground(oc);
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

		if (btnSkipRepeatsChk.getSelection()) {
			commonUserParameters.put("FindRepeats", false);
		}

		if (btnSkipRDChk.getSelection()) {
			commonUserParameters.put("RunRDAnalysis", false);
		}

		if (btnSkipNewCNVChk.getSelection()) {
			commonUserParameters.put("FindNewCNVs", false);
		}

		if (btnSkipSNVSDetection.getSelection()) {
			commonUserParameters.put("FindSNVs", false);
		}

		if (btnSkipRPChk.getSelection()) {
			commonUserParameters.put("RunRPAnalysis", false);
		}



		if(!errorsOne.isEmpty())
			return null;

		return commonUserParameters;


	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public ArrayList<String> getErrors() {
		return errorsOne;
	}

	public File getInitialAlignmentsFile() {
		return initialAlignmentsFile;
	}

	public void setInitialAlignmentsFile(File initialAlignmentsFile) {
		this.initialAlignmentsFile = initialAlignmentsFile;
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

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

}
