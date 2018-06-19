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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.ngsep.control.SampleData;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Daniel Cruz
 *
 */
public class TabMapMainArgs extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */


//	Basic Fields For all cases including Wizard and Multi

	private Label lblIndexFile;
	private Text txtIndexFile;
	private Button btnIndexFile;


	private Label lblInsertSize;
	private Label lblMinimunInsert;
	private Text txtMinimunInsIze;
	private Label lblMaximunInsert;
	private Text txtMaximunInsIze;

	private Label lblPlatform;
	private Combo cmbPlatform;

	private Button btnCheckAlignment;
	private Text txtKalignment;
	private Button btnReportAllAlignment;

	private String outputDirectory;
	private String prefix = new String();

	
	
	private ArrayList<String> errors = new ArrayList<String>();

	private MouseListenerNgsep mouse = new MouseListenerNgsep();
	
//	Only Multi
	
	private Label lblNumberOfProcessors;
	private Text txtNumberOfProcessors;
		
// Fields for one sample
	private File fastqFile1;
	private File fastqFile2;

	
	private Label lblFileOne;
	private Label lblFileTwo;
	private Text txtFileOne;
	private Text txtFileTwo;
	private Button btnChange;
	
	private Label lbloutputFile;
	private Text txtOutPutFile;
	private Button btnOutPutFile;
	
	private Label lblReadGroupId;
	private Label lblSampleId;
	
	private Text txtReadGroupId;
	private Text txtSampeId;
	
	private Composite parent;
	private char source;
	
	
	public TabMapMainArgs(Composite parent, int style, char source) {
		super(parent, style);
		this.parent = parent;
		this.source = source;
	}


	public void paint() {
		

		lblIndexFile = new Label(this, SWT.NONE);
		lblIndexFile.setText("(*)Bowtie2 Index:");
		lblIndexFile.setBounds(17, 141, 124, 21);

		txtIndexFile = new Text(this, SWT.BORDER);
		txtIndexFile.setBounds(177, 138, 600, 21);
		txtIndexFile.addMouseListener(mouse);
		
		// Suggest the latest stored index
		String historyReference = HistoryManager.getHistory(outputDirectory, HistoryManager.KEY_BOWTIE2_INDEX);
		if (historyReference!=null) txtIndexFile.setText(historyReference);

		btnIndexFile = new Button(this, SWT.NONE);
		btnIndexFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(parent.getShell(), SWT.OPEN, outputDirectory,txtIndexFile);
			}
		});
		btnIndexFile.setBounds(790, 135, 24, 25);
		btnIndexFile.setText("...");

		//Insert size items
		lblInsertSize=new Label(this, SWT.NONE);
		lblInsertSize.setText("Insert size:");
		lblInsertSize.setBounds(17, 180, 80, 21);

		lblMinimunInsert = new Label(this, SWT.NONE);
		lblMinimunInsert.setText("Min:");
		lblMinimunInsert.setBounds(110, 180, 50, 21);

		txtMinimunInsIze = new Text(this, SWT.BORDER);
		txtMinimunInsIze.setBounds(177, 180, 42, 21);

		lblMaximunInsert = new Label(this, SWT.NONE);
		lblMaximunInsert.setText("Max:");
		lblMaximunInsert.setBounds(242, 180, 50, 21);

		txtMaximunInsIze = new Text(this, SWT.BORDER);
		txtMaximunInsIze.setBounds(312, 180, 42, 21);

		//PlatForm items
		lblPlatform = new Label(this, SWT.NONE);
		lblPlatform.setText("Platform:");
		
		if(source=='W')//Other sources will move
			lblPlatform.setBounds(17, 220, 124, 21);

		cmbPlatform = new Combo(this, SWT.READ_ONLY);
		
		if(source=='W')//Other sources will move
			cmbPlatform.setBounds(177, 300, 158, 28);
		
		String itemsPlataform[] = { "ILLUMINA", "CAPILLARY", "LS454", "SOLID","HELICOS", "IONTORRENT", "PACBIO" };
		cmbPlatform.setItems(itemsPlataform);
		cmbPlatform.setVisible(true);
		cmbPlatform.select(0);
		cmbPlatform.setBounds(177, 220, 158, 28);

		//Report Alignment items
		btnCheckAlignment = new Button(this, SWT.CHECK);
		btnCheckAlignment.setBounds(500, 180, 255, 21);
		btnCheckAlignment.setText("Number of alignments to report");
		btnCheckAlignment.addMouseListener(onMouseClickButtonK);

		txtKalignment = new Text(this, SWT.BORDER);
		txtKalignment.setBounds(780, 180, 33, 21);
		txtKalignment.setVisible(false);
		txtKalignment.getText();
		txtKalignment.addMouseListener(mouse);

		btnReportAllAlignment = new Button(this, SWT.CHECK);
		btnReportAllAlignment.setText("Report all alignments ");
		btnReportAllAlignment.setBounds(500, 220, 202, 21);
		btnReportAllAlignment.addMouseListener(onMouseClickButtonReport);

		if(source == 'M'){ //Multimapping paint
			lblPlatform.setBounds(17, 220, 124, 21);
			cmbPlatform.setBounds(177, 220, 158, 28);
			
			lblNumberOfProcessors = new Label(this, SWT.NONE);
			lblNumberOfProcessors.setText("Number Of Processors:");
			lblNumberOfProcessors.setBounds(500, 263, 190, 21);

			txtNumberOfProcessors = new Text(this, SWT.BORDER);
			txtNumberOfProcessors.setBounds(700, 260, 42, 21);
			//by default it suggests the user to use all possible processor minus 1, to be able to use his computer.
			int processors = Runtime.getRuntime().availableProcessors()-1;
			txtNumberOfProcessors.setText(String.valueOf(processors));	
			
		}
		
		if(source == 'O'){ //One sample paint 
			lblFileOne = new Label(this, SWT.NONE);
			lblFileOne.setBounds(17, 21, 95, 21);
			lblFileOne.setText("File # 1:");

			lblFileTwo = new Label(this, SWT.NONE);
			lblFileTwo.setText("File # 2:");
			lblFileTwo.setBounds(17, 61, 95, 21);

			txtFileOne = new Text(this, SWT.BORDER);
			txtFileOne.setEnabled(true);
			txtFileOne.setBounds(177, 18, 600, 21);
			txtFileOne.addMouseListener(mouse);

			txtFileTwo = new Text(this, SWT.BORDER);
			txtFileTwo.setEnabled(true);
			txtFileTwo.setBounds(177, 58, 600, 21);
			txtFileTwo.addMouseListener(mouse);

			btnChange = new Button(this, SWT.NONE);
			btnChange.setText("\u2191\u2193");
			btnChange.setBounds(790, 32, 33, 25);
			btnChange.addSelectionListener(new SelectionAdapter() {
				// This method allows you to change a file from a text box to
				// another event by clicking a button
				@Override
				public void widgetSelected(SelectionEvent e) {
					String FileOne = txtFileOne.getText();
					String FileTwo = txtFileTwo.getText();
					if (FileOne != null && FileOne.length()>0 || FileTwo != null && FileTwo.length()>0) {
						txtFileOne.setText(FileTwo);
						txtFileTwo.setText(FileOne);
					}
				}
			});

			lbloutputFile = new Label(this, SWT.NONE);
			lbloutputFile.setText("(*)Output File Prefix:");
			lbloutputFile.setBounds(17, 101, 146, 21);

			txtOutPutFile = new Text(this, SWT.BORDER);
			txtOutPutFile.setBounds(177, 98, 600, 24);
			txtOutPutFile.addMouseListener(mouse);
//			txtOutPutFile.setText(outputDirectory+File.separator+prefix);
			
			btnOutPutFile = new Button(this, SWT.NONE);
			btnOutPutFile.setText("...");
			btnOutPutFile.setBounds(790, 95, 24, 25);
			btnOutPutFile.addSelectionListener(new SelectionAdapter() {
				// the method Utilities.RouteForWinoLinux returns me the path where
				// the eclipse runtime files
				// according to separator that has the operating system for windows
				// \ Linux / and I suggested the route for the event of selecting a
				// route
				@Override
				public void widgetSelected(SelectionEvent e) {
					SpecialFieldsHelper.updateFileTextBox(parent.getShell(),  SWT.SAVE, outputDirectory,txtOutPutFile);
					outputDirectory = new File(txtOutPutFile.getText()).getParentFile().getAbsolutePath();
				}
			});
			
			lblReadGroupId = new Label(this, SWT.NONE);
			lblReadGroupId.setText("Read group Id:");
			lblReadGroupId.setBounds(17, 220, 124, 21);

			lblSampleId = new Label(this, SWT.NONE);
			lblSampleId.setText("Sample Id:");
			lblSampleId.setBounds(17, 260, 124, 21);

			txtReadGroupId = new Text(this, SWT.BORDER);
			txtReadGroupId.setBounds(177, 220, 177, 21);

			txtSampeId = new Text(this, SWT.BORDER);
			txtSampeId.setBounds(177, 260, 177, 21);

			txtReadGroupId.addMouseListener(mouse);
			txtSampeId.addMouseListener(mouse);

					
			lblPlatform.setBounds(17, 300, 124, 21);
			cmbPlatform.setBounds(177, 300, 158, 28);
			refreshFields();
		}
	}

	private final MouseListener onMouseClickButtonReport = new MouseListener() {

		@Override
		public void mouseDoubleClick(MouseEvent e) {

		}

		@Override
		public void mouseDown(MouseEvent e) {

		}

		@Override
		public void mouseUp(MouseEvent e) {
			if (btnReportAllAlignment.getSelection()) {
				btnCheckAlignment.setVisible(false);
			} else if (!btnReportAllAlignment.getSelection()) {
				btnCheckAlignment.setVisible(true);
			}
		}
	};

	private final MouseListener onMouseClickButtonK = new MouseListener() {

		@Override
		public void mouseDoubleClick(MouseEvent e) {

		}

		@Override
		public void mouseDown(MouseEvent e) {

		}

		@Override
		public void mouseUp(MouseEvent e) {
			if (btnCheckAlignment.getSelection()) {
				txtKalignment.setVisible(true);
				txtKalignment.setText("2");
				btnReportAllAlignment.setVisible(false);
			} else if (!btnCheckAlignment.getSelection()) {
				txtKalignment.setVisible(false);
				btnReportAllAlignment.setVisible(true);
				txtKalignment.redraw();
				txtKalignment.update();
				txtKalignment.pack();
			}
			txtKalignment.update();
		}
	};

	public Map<String, Object> getParams(){

		errors.clear();

		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		Map<String,Object> userParams = new TreeMap<String, Object>();
		
		//For single
		if (source=='O'){
			SampleData sample = new SampleData();
			boolean bFileOne = txtFileOne.getText().length()>0 && txtFileOne.getText() != null;
			boolean bFileTwo = txtFileTwo.getText().length()>0 && txtFileTwo.getText() != null;

			if (!bFileOne ) {
				errors.add(FieldValidator.buildMessage(lblFileOne.getText(),FieldValidator.ERROR_MANDATORY));
				txtFileOne.setBackground(oc);
				txtFileTwo.setBackground(oc);
			} else {
				sample.setFastq1(txtFileOne.getText());
			}
			if(bFileTwo) {
				sample.setFastq2(txtFileTwo.getText());
			}



			if (txtOutPutFile.getText() == null|| txtOutPutFile.getText().equals("")) {
				errors.add(FieldValidator.buildMessage(lbloutputFile.getText(), FieldValidator.ERROR_MANDATORY));
				txtOutPutFile.setBackground(oc);
			} else {
				sample.setSamFile(txtOutPutFile.getText()+".sam");
				sample.setSortedBamFile(txtOutPutFile.getText()+"_sorted.bam");
				sample.setMapLogFile(txtOutPutFile.getText()+"Map.log");
			}
			if (txtSampeId.getText() == null || txtSampeId.getText().length()==0) {
				errors.add(FieldValidator.buildMessage(lblSampleId.getText(),FieldValidator.ERROR_MANDATORY));
				txtSampeId.setBackground(oc);
			} else {
				sample.setSampleId(txtSampeId.getText());
			}
			if (txtReadGroupId.getText() == null || txtReadGroupId.getText().length()==0) {
				errors.add(FieldValidator.buildMessage(lblReadGroupId.getText(),FieldValidator.ERROR_MANDATORY));
				txtReadGroupId.setBackground(oc);
			} else {
				sample.setReadGroupId(txtReadGroupId.getText());

			}
			userParams.put("singleSample", sample);
		}
		
		if (source=='M'){
			int numProcesses=2;
			int processors = Runtime.getRuntime().availableProcessors();
			if (txtNumberOfProcessors.getText() != null && txtNumberOfProcessors.getText().length()>0) {
				if (!FieldValidator.isNumeric(txtNumberOfProcessors.getText(),new Integer(0))) {
					errors.add(FieldValidator.buildMessage(lblNumberOfProcessors.getText(), FieldValidator.ERROR_INTEGER));
					txtNumberOfProcessors.setBackground(oc);
				} else if(Integer.parseInt(txtNumberOfProcessors.getText())<=processors){
					numProcesses = Integer.parseInt(txtNumberOfProcessors.getText());
					userParams.put("numProc", numProcesses);
				} else{
					errors.add(lblNumberOfProcessors.getText() + " the number entered in this box must be less than " + processors);
					txtNumberOfProcessors.setBackground(oc);
				}
			} else{
				FieldValidator.buildMessage(lblNumberOfProcessors.getText(),FieldValidator.ERROR_MANDATORY);
			}
			
			
			
		}
		
		String bowtieCommand = "bowtie2";
		if (SpecialFieldsHelper.isWindows()) {
			bowtieCommand = "bowtie2-align.exe";
		}
		
		userParams.put("mainCMD", bowtieCommand);
		
//		commandArray.add(bowtieCommand);

		if (txtIndexFile.getText() == null|| txtIndexFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblIndexFile.getText(),FieldValidator.ERROR_MANDATORY));
			txtIndexFile.setBackground(oc);
		} else {
			userParams.put("indexCMD","-x");
			userParams.put("indexArg", SpecialFieldsHelper.maskWhiteSpaces(txtIndexFile.getText()));
//			commandArray.add("-x");
//			commandArray.add(SpecialFieldsHelper.maskWhiteSpaces(txtReferenceFile.getText()));
		}

		if (txtMinimunInsIze.getText() != null && txtMinimunInsIze.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMinimunInsIze.getText(),new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblMinimunInsert.getText() , FieldValidator.ERROR_INTEGER));
				txtMinimunInsIze.setBackground(oc);
			} else {
				userParams.put("minimunInsIzeCMD","-I");
				userParams.put("minimunInsIzeArg",txtMinimunInsIze.getText());
			
//				commandArray.add("-I");
//				commandArray.add(txtMinimunInsIze.getText());
			}
		}

		if (txtMaximunInsIze.getText() != null && txtMaximunInsIze.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMaximunInsIze.getText(),new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblMaximunInsert.getText() , FieldValidator.ERROR_INTEGER));
				txtMaximunInsIze.setBackground(oc);
			} else {
				userParams.put("maximunInsIzeCMD","-X");
				userParams.put("maximunInsIzeArg",txtMaximunInsIze.getText());
				
//				commandArray.add("-X");
//				commandArray.add(txtMaximunInsIze.getText());
			}
		}

		if (btnCheckAlignment.getSelection()) {
			if (txtKalignment.getText() == null|| txtKalignment.getText().length()==0) {
				errors.add(FieldValidator.buildMessage(btnCheckAlignment.getText(), FieldValidator.ERROR_MANDATORY));
				txtKalignment.setBackground(oc);
			} else if (!FieldValidator.isNumeric(txtKalignment.getText(),new Integer(0))) {
				errors.add(FieldValidator.buildMessage(btnCheckAlignment.getText() , FieldValidator.ERROR_INTEGER));
				txtKalignment.setBackground(oc);
			} else {
				userParams.put("kalignmentCMD","-k");
				userParams.put("kalignmentArg",txtKalignment.getText());
//				commandArray.add("-k");
//				commandArray.add(txtKalignment.getText());	
			}
		}

		userParams.put("addTextCMD","--rg");
		
		userParams.put("platformCMD","PL:");
		
		userParams.put("platformArg",cmbPlatform.getItem(cmbPlatform.getSelectionIndex()));
		
//		commandArray.add("--rg");
//		commandArray.add("PL:"+cmbPlatform.getItem(cmbPlatform.getSelectionIndex()));

		if (btnReportAllAlignment.getSelection()) {
			userParams.put("reportAllAlignmentCMD","-a");
//			commandArray.add("-a");
		}
		

		if(!errors.isEmpty()) return null;

		HistoryManager.saveInHistory(HistoryManager.KEY_BOWTIE2_INDEX, txtIndexFile.getText());
		return userParams;
	}


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public  static List<Integer> calculateDifferences(String file1, String file2) {
		int i = 0;
		List<Integer> differences = new ArrayList<Integer>();
		while (i < file1.length() && i < file2.length()) {
			char letterFirstElement = file1.charAt(i);
			char letterSecondElement = file2.charAt(i);
			if (letterFirstElement != letterSecondElement) {
				differences.add(i);
			}
			i++;
		}
		return differences;
	}
	
	public void refreshFields(){
		String inputFilePath = fastqFile1.getAbsolutePath();
		txtFileOne.setText(inputFilePath);
		if (fastqFile2 != null) {
			txtFileTwo.setText(fastqFile2.getAbsolutePath());
		}
		
		boolean bFileOne = txtFileOne.getText().length()>0 && txtFileOne.getText() != null;
		boolean bFileTwo = txtFileTwo.getText().length()>0 && txtFileTwo.getText() != null;

		if (bFileOne ) {
			fastqFile1=new File(txtFileOne.getText());
		}

		if(bFileTwo) {
			fastqFile2=new File(txtFileTwo.getText());
		}

		prefix=fastqFile1.getName();
		if (fastqFile2!=null) {
			int indexDifference = calculateDifferences(fastqFile1.getName(),fastqFile2.getName()).get(0);
			prefix = fastqFile1.getName().substring(0, indexDifference);	
		} else {
			int index = prefix.indexOf(".");
			if(index>0) prefix = prefix.substring(0, index);					
		}
		txtSampeId.setText(prefix);
		txtReadGroupId.setText(prefix);
		txtOutPutFile.setText(outputDirectory+File.separator+prefix);	
	}

	public ArrayList<String> getErrors() {
		return errors;
	}


	public File getFastqFile1() {
		return fastqFile1;
	}


	public void setFastqFile1(File fastqFile1) {
		this.fastqFile1 = fastqFile1;
	}


	public File getFastqFile2() {
		return fastqFile2;
	}


	public void setFastqFile2(File fastqFile2) {
		this.fastqFile2 = fastqFile2;
	}


	public String getOutputDirectory() {
		return outputDirectory;
	}


	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}



}
