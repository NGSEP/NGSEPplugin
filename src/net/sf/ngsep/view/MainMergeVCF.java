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

import net.sf.ngsep.control.SampleData;
import net.sf.ngsep.control.SamplesDatabase;
import net.sf.ngsep.control.SyncDetermineVariants;
import net.sf.ngsep.control.SyncMergeVCF;
import net.sf.ngsep.utilities.EclipseProjectHelper;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.genome.ReferenceGenome;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Juan Camilo Quintero, Jorge Duitama
 *
 */
public class MainMergeVCF {
	
	private String selectedFile;
	
	protected Shell shlMergeVcf;
	private Font tfont;
	private Text txtOutputFile;
	private Label lblListMixForVCF;
	private Label lbloutputFile;
	private Button btnMixList;
	private Button btnCancel;
	private Button btnOutputFile;
	
	private Table table;
	private TableColumn tableColumn;
	private TableColumn tableColumn_2;
	private TableColumn tableColumn_1;
	private TableColumn tblclmnVcfFile;
	private TableColumn tblclmnCheck;
	Map<String,SampleData> samplesDB;
	private TableItem item;
	private Button btnSelectAll;
	private Text text;
	private TableEditor tableEditor;
	private Button btnMergeVcfFiles;
	private Button btnDeselectAllFiles;
	private Display display;

	public String getSelectedFile() {
		return selectedFile;
	}

	public void setSelectedFile(String selectedFile) {
		this.selectedFile = selectedFile;
	}

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		display = Display.getDefault();
		boolean created = createContents();
		if(created) {
			shlMergeVcf.open();
			shlMergeVcf.layout();
			while (!shlMergeVcf.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}

	}

	/**
	 * Create contents of the shell.
	 */
	protected boolean createContents() {
		shlMergeVcf = new Shell(display, SWT.SHELL_TRIM);
		shlMergeVcf.setSize(1078, 600);
		shlMergeVcf.setText("Merge VCF");
		shlMergeVcf.setLocation(150, 200);

		MouseListenerNgsep mouse = new MouseListenerNgsep();
		tfont = new Font(Display.getCurrent(), "Arial", 10, SWT.BOLD);
		lblListMixForVCF = new Label(shlMergeVcf, SWT.NONE);
		lblListMixForVCF.setText("List Merge For VCF");
		lblListMixForVCF.setFont(tfont);
		lblListMixForVCF.setBounds(468, 30, 169, 21);

		

		table = new Table(shlMergeVcf, SWT.BORDER | SWT.CHECK | SWT.MULTI| SWT.V_SCROLL);
		table.setBounds(211, 71, 831, 298);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tblclmnCheck = new TableColumn(table, SWT.NONE);
		tblclmnCheck.setText("Check");
		tblclmnCheck.setResizable(true);
		tblclmnCheck.setWidth(62);

		tblclmnVcfFile = new TableColumn(table, SWT.NONE);
		tblclmnVcfFile.setResizable(true);
		tblclmnVcfFile.setWidth(141);
		tblclmnVcfFile.setText("Sample ID");

		tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setResizable(true);
		tableColumn.setWidth(185);
		tableColumn.setText(" VCF File");

		tableColumn_2 = new TableColumn(table, SWT.NONE);
		tableColumn_2.setResizable(true);
		tableColumn_2.setWidth(201);
		tableColumn_2.setText("Name BAM");

		tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setResizable(true);
		tableColumn_1.setWidth(250);
		tableColumn_1.setText("Name Reference");

		String directoryProject = null;
		try {
			directoryProject = EclipseProjectHelper.findProjectDirectory(selectedFile);
		} catch (Exception e) {
			MessageDialog.openError(shlMergeVcf, " Map reads Error","error while trying to place the reference path history most recently used"+ e.getMessage());
			return false;
		}

		File historyVD = HistoryManager.createPathRecordVCF(directoryProject);
		if (historyVD.getAbsoluteFile() != null) {
			SamplesDatabase samplesDatabase;
			try {
				samplesDatabase=new SamplesDatabase(historyVD.getAbsoluteFile().getAbsolutePath());
			} catch (IOException e1) {
				MessageDialog.openError(shlMergeVcf," Merge VCF Error",e1.getMessage());
				return false;
			}
			samplesDB=samplesDatabase.getSamplesWithValidData();
			//MessageDialog.openInformation(shlMergeVcf, "Merge VCF","Number of samples: "+samplesDB.size());
			// through the list and added to each column of the table for the
			// corresponding item
			if(samplesDB.size()==0){
				MessageDialog.openError(Display.getDefault().getActiveShell(),"Merge VCF Error","The selected file is not a samples database file or is empty.");
				return false;
			}
			for (String sampleId:samplesDB.keySet()) {
				SampleData sample = samplesDB.get(sampleId);
				item = new TableItem(table, SWT.CHECK);
				String nameVCF = sample.getVcfFile();
				String srtNameVCF = nameVCF.substring(0, nameVCF.lastIndexOf("."));
				String nameBAM = sample.getSortedBamFile();
				String srtNameBam = nameBAM.substring(0, nameBAM.lastIndexOf("."));
				String nameReference = sample.getReferenceFile();
				String srtNameReference = nameReference.substring(0, nameReference.lastIndexOf("."));
				item.setText(1, sampleId);
				item.setText(2, srtNameVCF);
				item.setText(3, srtNameBam);
				item.setText(4, srtNameReference);
				String variantsFile=sample.getVariantsFile();
				if(variantsFile!=null){
					String srtvariantsFile = variantsFile.substring(0, variantsFile.lastIndexOf("."));
					item.setText(5, srtvariantsFile);
				}
			}
			

		}

		tableEditor = new TableEditor(table);
		tableEditor.horizontalAlignment = SWT.LEFT;
		tableEditor.grabHorizontal = true;
		tableEditor.minimumWidth = 50;

		// Method to select all and select combo box
		btnSelectAll = new Button(shlMergeVcf, SWT.PUSH);
		btnSelectAll.setBounds(10, 90, 150, 25);
		btnSelectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean checkBoxFlag = false;
				for (int i = 0; i < table.getItemCount(); i++) {
					if (table.getItems()[i].getChecked()) {
						checkBoxFlag = true;
					}
					if (!checkBoxFlag) {
						for (int m = 0; m < table.getItemCount(); m++) {
							table.getItems()[m].setChecked(true);
							table.selectAll();
							checkBoxFlag = true;
						}
					}

				}
			}
		});
		btnSelectAll.setText("Select all files ");

		btnDeselectAllFiles = new Button(shlMergeVcf, SWT.PUSH);
		btnDeselectAllFiles.setText("Deselect all files ");
		btnDeselectAllFiles.setBounds(10, 125, 150, 25);
		btnDeselectAllFiles.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean checkBoxFlag = false;
				for (int i = 0; i < table.getItemCount(); i++) {
					if (table.getItems()[i].getChecked()) {
						checkBoxFlag = true;
					}
				}

				if (checkBoxFlag) {
					for (int m = 0; m < table.getItemCount(); m++) {
						table.getItems()[m].setChecked(false);
						table.deselectAll();
						checkBoxFlag = false;
					}
				}
			}
		});

		lbloutputFile = new Label(shlMergeVcf, SWT.NONE);
		lbloutputFile.setText("(*)Output File:");
		lbloutputFile.setFont(tfont);
		lbloutputFile.setBounds(50, 400, 125, 25);

		txtOutputFile = new Text(shlMergeVcf, SWT.BORDER);
		txtOutputFile.setBounds(200, 400, 680, 25);
		txtOutputFile.addMouseListener(mouse);
		
	
		File outputFile=new File(selectedFile);
		String dirAbsolutePath = outputFile.getParentFile().getAbsolutePath();
		String suggestedOutFile = dirAbsolutePath+File.separator+"variantsfile.vcf";
		txtOutputFile.setText(suggestedOutFile);
		
		btnOutputFile = new Button(shlMergeVcf, SWT.NONE);
		btnOutputFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shlMergeVcf,  SWT.SAVE, selectedFile, txtOutputFile);
			}
		});
		btnOutputFile.setText("...");
		btnOutputFile.setBounds(900, 400, 25, 25);
		

		text = new Text(shlMergeVcf, SWT.BORDER);
		text.setBounds(661, 444, 57, 21);
		text.setVisible(false);

		btnMixList = new Button(shlMergeVcf, SWT.NONE);
		btnMixList.setText("Determine list of variants");
		btnMixList.setBounds(200, 500, 200, 30);
		btnMixList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				determineVariants();
			}
		});
		

		btnMergeVcfFiles = new Button(shlMergeVcf, SWT.NONE);
		btnMergeVcfFiles.setText("Merge vcf files");
		btnMergeVcfFiles.setBounds(450, 500, 200, 30);
		btnMergeVcfFiles.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mergeVcf();
			}
		});
		
		btnCancel = new Button(shlMergeVcf, SWT.NONE);
		btnCancel.setText("Cancel");
		btnCancel.setBounds(700, 500, 110, 30);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlMergeVcf.close();
			}
		});
		
		return true;
	}

	public void determineVariants() {
		
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		String nameFileDeterminateVariants=null;
		ArrayList<String> errors = new ArrayList<String>();
		if (txtOutputFile.getText() == null || txtOutputFile.getText().equals("")) {
			errors.add(FieldValidator.buildMessage(lbloutputFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutputFile.setBackground(oc);
		}
		
		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shlMergeVcf, "Determine variants");
			return;
		}
		
		
		
		List<String> vcfFiles = new ArrayList<String>();
		String referenceFile = fillVCFFilesList(vcfFiles);
		if(referenceFile == null) return;
		
		
		if(vcfFiles.size()<2){
			MessageDialog.openError(shlMergeVcf, " Determine variants error"," Select at least two samples ");
			return;
		}
		ReferenceGenome genome;
		try {
			genome = ReferenceGenomesFactory.getInstance().getGenome(referenceFile, shlMergeVcf);
		} catch (IOException e) {
			MessageDialog.openError(shlMergeVcf, " Error loading reference genome",e.getMessage());
			return;
		}
		String outFile = txtOutputFile.getText();
		String logFilename = LoggingHelper.getLoggerFilename(outFile,"DV");
		SyncDetermineVariants syncDetermineVariants = new SyncDetermineVariants("DetermineVariants");
		syncDetermineVariants.setLogName(logFilename);
		syncDetermineVariants.setSequenceNames(genome.getSequencesMetadata());
		syncDetermineVariants.setListFiles(vcfFiles);
		syncDetermineVariants.setOutputFile(outFile);
		try {
			syncDetermineVariants.schedule();
		} catch (Exception e) {
			MessageDialog.openError(shlMergeVcf, " Determine variants Error",e.getMessage());
			return;
		}
		MessageDialog.openInformation(shlMergeVcf,"Determine Variants is running",LoggingHelper.MESSAGE_PROGRESS_BAR + " and check if the file " + nameFileDeterminateVariants + " was created in the outputFile path");
		shlMergeVcf.dispose();
		
	}

	public void mergeVcf() {
		
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		ArrayList<String> errors = new ArrayList<String>();
		if (txtOutputFile.getText() == null || txtOutputFile.getText().equals("")) {
			errors.add(FieldValidator.buildMessage(lbloutputFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutputFile.setBackground(oc);
		}

		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shlMergeVcf, "Merge VCF files");
			return;
		}

		SyncMergeVCF job = new SyncMergeVCF("MergeVCF");
		
		List<String> vcfFiles = new ArrayList<String>();
		String referenceFile = fillVCFFilesList(vcfFiles);
		if(referenceFile == null) return;
		
		if(vcfFiles.size()<2){
			MessageDialog.openError(shlMergeVcf, " Merge VCF Error"," Select at least two samples ");
			return;
		}
		ReferenceGenome genome;
		try {
			genome = ReferenceGenomesFactory.getInstance().getGenome(referenceFile, shlMergeVcf);
		} catch (IOException e) {
			MessageDialog.openError(shlMergeVcf, " Error loading reference genome",e.getMessage());
			return;
		}
		String logFilename = LoggingHelper.getLoggerFilename(txtOutputFile.getText(),"MVCF");
		job.setLogName(logFilename);
		job.setListFiles(vcfFiles);
		job.setSequenceNames(genome.getSequencesMetadata());
		job.setOutputFile(txtOutputFile.getText());
		String filename = new File(txtOutputFile.getText()).getName();
		job.setNameProgressBar(filename);
		try {
			job.schedule();
		} catch (Exception e) {
			MessageDialog.openError(shlMergeVcf, " Merge VCF Error",e.getMessage());
			return;
		}
		MessageDialog.openInformation(shlMergeVcf, "Merge VCF is running",LoggingHelper.MESSAGE_PROGRESS_BAR + "and check if the file " + filename + " was created in the outputFile path");
		shlMergeVcf.dispose();

	}

	private String fillVCFFilesList (List<String> vcfFiles) {
		String referenceFile = null;
		for (int i = 0; i < table.getItemCount(); i++) {
			item = table.getItem(i);
			if (item.getChecked()) {
				SampleData sample = samplesDB.get(item.getText(1));
				vcfFiles.add(sample.getVcfFile());
				String sampleReference = sample.getReferenceFile();
				if(referenceFile == null) referenceFile = sampleReference;
				else if (!referenceFile.equals(sampleReference)) {
					vcfFiles.clear();
					MessageDialog.openError(shlMergeVcf, " Merge VCF Error","the samples to merge should have the same reference");
					return null;
				}
			}
		}
		return referenceFile;
	}
	
}