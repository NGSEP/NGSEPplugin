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

import net.sf.ngsep.control.SyncVCFComparator;
import net.sf.ngsep.utilities.EclipseProjectHelper;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.genome.ReferenceGenome;
import ngsep.vcf.VCFComparator;

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
 * @author Claudia Perea
 * @author Jorge Duitama
 *
 */
public class MainVCFComparator implements SingleFileInputWindow {
	
		//Parameters
	
		//General variables 
		private Shell shell;
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
		private Button btnCompare;
		private Button btnCancel;
		
		//-----------------------	

		//Main arguments
		private Label lblInputVcf1;
		private Text txtInputVcf1;
		private Button btnInputVcf1;
		private Label lblInputVcf2;
		private Text txtInputVcf2;
		private Button btnInputVcf2;
		private Label lblOutputFile;
		private Text txtOutputFile;
		private Button btnOutputFile;
		private Label lblReferenceFile;
		private Text txtReferenceFile;
		private Button btnReferenceFile;

		
		private Label lblGenotype;
		private Text txtGenotyped;
		private Label lblDifferences;
		private Text txtDifferences;
		
		//----------------------------------------

		/**
		 * Open the window.
		 */
		
		public void open() {
			display = Display.getDefault();
			shell = new Shell(display, SWT.SHELL_TRIM);
			shell.setText("Compare VCFs");
			shell.setLocation(150, 200);
			shell.setSize(855, 417);
			createContents();
			shell.open();

			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}
		
		
		/**
		 * Create contents of  the window.
		 */
		protected void createContents() {
			MouseListenerNgsep mouse = new MouseListenerNgsep();
			
			lblInputVcf1=new Label(shell, SWT.NONE);
			lblInputVcf1.setBounds(10, 24, 186, 21);
			lblInputVcf1.setText("(*) VCF file 1:");
			
			txtInputVcf1=new Text(shell, SWT.BORDER);
			txtInputVcf1.setBounds(219, 21, 545, 25);
			txtInputVcf1.addMouseListener(mouse);
			if (selectedFile != null && selectedFile.length()>0) {
				txtInputVcf1.setText(selectedFile);
			}
			
			btnInputVcf1=new Button(shell, SWT.NONE);
			btnInputVcf1.setBounds(770, 21, 24, 25);
			btnInputVcf1.setText("...");
			btnInputVcf1.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SpecialFieldsHelper.updateFileTextBox(shell,  SWT.OPEN, selectedFile, txtInputVcf1);
				}			
			});
			
			lblInputVcf2=new Label(shell, SWT.NONE);
			lblInputVcf2.setBounds(10, 64, 186, 21);
			lblInputVcf2.setText("(*) VCF file 2 :");
			
			txtInputVcf2=new Text(shell, SWT.BORDER);
			txtInputVcf2.setBounds(219, 61, 545, 25);
			txtInputVcf2.addMouseListener(mouse);
			
			btnInputVcf2=new Button(shell, SWT.NONE);
			btnInputVcf2.setBounds(770, 61, 24, 25);
			btnInputVcf2.setText("...");
			btnInputVcf2.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SpecialFieldsHelper.updateFileTextBox(shell,  SWT.OPEN, selectedFile,txtInputVcf2);
				}			
			});

			
			lblReferenceFile=new Label(shell, SWT.NONE);
			lblReferenceFile.setBounds(10, 105, 186, 21);
			lblReferenceFile.setText("(*FASTA) Reference Genome:");
			
			txtReferenceFile=new Text(shell, SWT.BORDER);
			txtReferenceFile.setBounds(219, 105, 545, 25);
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
			
			
			
			btnReferenceFile=new Button(shell, SWT.NONE);
			btnReferenceFile.setBounds(770, 105, 24, 25);
			btnReferenceFile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SpecialFieldsHelper.updateFileTextBox(shell,  SWT.OPEN, selectedFile, txtReferenceFile);
				}
			});
			
			btnReferenceFile.setText("...");
			
			lblOutputFile = new Label(shell, SWT.NONE);
			lblOutputFile.setBounds(10, 152, 186, 21);
			lblOutputFile.setText("(*) Output File:");
			
			txtOutputFile = new Text(shell, SWT.BORDER);
			txtOutputFile.setBounds(219, 152, 545, 25);
			txtOutputFile.addMouseListener(mouse);
			String suggestedOutPrefix = SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile);
			txtOutputFile.setText(suggestedOutPrefix+"_comparison.txt");
			
			
			
			btnOutputFile = new Button(shell, SWT.NONE);
			btnOutputFile.setBounds(770, 152, 24, 25);
			btnOutputFile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SpecialFieldsHelper.updateFileTextBox(shell,  SWT.SAVE, selectedFile, txtOutputFile);
				}		
			});
			btnOutputFile.setText("...");
			
			lblGenotype = new Label(shell, SWT.NONE);
			lblGenotype.setText("Min % of Genotyped SNPs");
			lblGenotype.setBounds(10, 216, 225, 21);
			
			txtGenotyped = new Text(shell, SWT.BORDER);
			txtGenotyped.setBounds(246, 213, 58, 25);
			txtGenotyped.setText("50");
			txtGenotyped.addMouseListener(mouse);
			
			lblDifferences = new Label(shell, SWT.NONE);
			lblDifferences.setText("Max % of Differences");
			lblDifferences.setBounds(10, 262, 238, 21);
			
			txtDifferences = new Text(shell, SWT.BORDER);
			txtDifferences.setBounds(246, 259, 58, 25);
			txtDifferences.setText("1");
			txtDifferences.addMouseListener(mouse);
			
			btnCompare = new Button(shell, SWT.NONE);
			btnCompare.setBounds(218, 320, 122, 30);
			btnCompare.setText("Compare");
			btnCompare.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					process();
				}
			});
			
			btnCancel = new Button(shell, SWT.NONE);
			btnCancel.setText("Cancel");
			btnCancel.setBounds(380, 320, 122, 30);
			btnCancel.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					shell.close();
				}
			});
		}
		
		public void process() {
			Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
			ArrayList<String> errorsShell = new ArrayList<String>();
			VCFComparator instance = new VCFComparator();
			SyncVCFComparator job = new SyncVCFComparator("VCFComparator");
			job.setInstance(instance);
			
			if (txtInputVcf1.getText()==null|| txtInputVcf1.getText().length()==0) {
				errorsShell.add(FieldValidator.buildMessage(lblInputVcf1.getText(), FieldValidator.ERROR_MANDATORY));
				txtInputVcf1.setBackground(oc);
			} else {
				job.setInputFile1(txtInputVcf1.getText());
			}
			
			if (txtInputVcf2.getText()==null|| txtInputVcf2.getText().length()==0) {
				errorsShell.add(FieldValidator.buildMessage(lblInputVcf2.getText(), FieldValidator.ERROR_MANDATORY));
				txtInputVcf2.setBackground(oc);
			} else {
				job.setInputFile2(txtInputVcf2.getText());
			}
			
			if (txtOutputFile.getText()==null|| txtOutputFile.getText().length()==0) {
				errorsShell.add(FieldValidator.buildMessage(lblOutputFile.getText(), FieldValidator.ERROR_MANDATORY));
				txtOutputFile.setBackground(oc);
			} else {
				job.setOutputFile(txtOutputFile.getText());
			}
			
			if (txtReferenceFile.getText()==null|| txtReferenceFile.getText().length()==0) {
				errorsShell.add(FieldValidator.buildMessage(lblReferenceFile.getText(), FieldValidator.ERROR_MANDATORY));
				txtReferenceFile.setBackground(oc);
			} else {
				try {
					ReferenceGenome genome = ReferenceGenomesFactory.getInstance().getGenome(txtReferenceFile.getText(), shell);
					instance.setGenome(genome);
				} catch (IOException e) {
					e.printStackTrace();
					errorsShell.add(FieldValidator.buildMessage(lblReferenceFile.getText(), "error loading file: "+e.getMessage()));
					txtReferenceFile.setBackground(oc);
				}
			}
					
			if (txtGenotyped.getText() != null && !txtGenotyped.getText().equals("")) {
				if (!FieldValidator.isNumeric(txtGenotyped.getText(), new Integer(0))) {
					errorsShell.add(FieldValidator.buildMessage(lblGenotype.getText(), FieldValidator.ERROR_INTEGER));
					txtGenotyped.setBackground(oc);
				}else{
					double genotypedPercentage=Double.parseDouble((txtGenotyped.getText()));
					instance.setMinPCTGenotyped(genotypedPercentage);
				}
			}
			
			if (txtDifferences.getText() != null && !txtDifferences.getText().equals("")) {
				if (!FieldValidator.isNumeric(txtDifferences.getText(), new Integer(0))) {
					errorsShell.add(FieldValidator.buildMessage(lblDifferences.getText(), FieldValidator.ERROR_INTEGER));
					txtDifferences.setBackground(oc);
				}else{
					double differencesPercentage=Double.parseDouble((txtDifferences.getText()));
					instance.setMaxPCTDiffs(differencesPercentage);
				}
			}
			
			if (errorsShell.size() > 0) {
				FieldValidator.paintErrors(errorsShell, shell, "Compare VCFs");
				return;
			}
			
			try {
				String directoryProject = EclipseProjectHelper.findProjectDirectory(selectedFile);
				String historyOne = HistoryManager.createPathRecordGeneral(directoryProject);
				HistoryManager.createPathRecordFiles(historyOne, txtReferenceFile.getText().toString());
			} catch (Exception e) {
				MessageDialog.openError(shell, " Variants Functional Annotator Error","error while trying to place the reference path history most recently used"+ e.getMessage());
				return;
			}
			
			String logFilename = LoggingHelper.getLoggerFilename(txtOutputFile.getText(),"VCFC");
			job.setLogName(logFilename);
			job.setNameProgressBar(new File(txtOutputFile.getText()).getName());
			try {
				job.schedule();
				MessageDialog.openInformation(shell, "Compare VCF",LoggingHelper.MESSAGE_PROGRESS_NOBAR);
				shell.dispose();	
			} catch (Exception e) {
				MessageDialog.openError(shell, "Compare VCF", e.getMessage());
				e.printStackTrace();
				return;
			}
		}
}
