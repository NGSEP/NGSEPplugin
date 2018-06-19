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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.sf.ngsep.control.SyncDetermineVariants;
import net.sf.ngsep.control.SyncMergeVCF;
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
 * @author Juan Camilo Quintero
 * @author Jorge Duitama
 */
public class MainMergeVCF implements MultipleFilesInputWindow {
	
	protected Shell shell;
	private Display display;
	
	//Files selected initially by the user
	private Set<String> selectedFiles;
	
	@Override
	public void setSelectedFiles(Set<String> selectedFiles) {
		this.selectedFiles = selectedFiles;
		
	}
	
	private Table table;
	private Button btnSelectAll;
	
	private Label lblReferenceFile;
	private Text txtReferenceFile;
	private Button btnReferenceFile;
	
	private Label lblOutputFile;
	private Text txtOutputFile;
	private Button btnOutputFile;
	
	private Button btnMixList;
	private Button btnMergeVcfFiles;
	private Button btnCancel;
	
	

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		display = Display.getDefault();
		if(createContents()) {
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} else {
			shell.dispose();
		}
	}

	/**
	 * Create contents of the shell.
	 */
	protected boolean createContents() {
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setSize(800, 600);
		shell.setText("Merge VCF");
		shell.setLocation(150, 200);

		MouseListenerNgsep mouse = new MouseListenerNgsep();
		Font tfont = new Font(Display.getCurrent(), "Arial", 10, SWT.BOLD);
		Label lblListMixForVCF = new Label(shell, SWT.NONE);
		lblListMixForVCF.setText("List of files to merge");
		lblListMixForVCF.setFont(tfont);
		lblListMixForVCF.setBounds(450, 30, 200, 25);

		// Method to invert selection
		btnSelectAll = new Button(shell, SWT.PUSH);
		btnSelectAll.setBounds(10, 70, 30, 30);
		btnSelectAll.setText(">");
		btnSelectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (int i = 0; i < table.getItemCount(); i++) {
					if (table.getItems()[i].getChecked()) {
						table.getItems()[i].setChecked(false);
					} else {
						table.getItems()[i].setChecked(true);
					}
				}
			}
		});
		

		table = new Table(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL| SWT.CHECK | SWT.H_SCROLL | SWT.HIDE_SELECTION | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setBounds(50, 70, 730, 300);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnCheck = new TableColumn(table, SWT.NONE);
		tblclmnCheck.setText("Check");
		tblclmnCheck.setResizable(true);
		tblclmnCheck.setWidth(50);

		TableColumn tblclmnVcfFile = new TableColumn(table, SWT.NONE);
		tblclmnVcfFile.setResizable(true);
		tblclmnVcfFile.setWidth(680);
		tblclmnVcfFile.setText("VCF file");

		

		
		List<String> filesList = new ArrayList<>();
		for(String file:selectedFiles) {
			String filelc=file.toLowerCase();
			if(filelc.endsWith(".vcf") || filelc.endsWith(".vcf.gz")) filesList.add(file);
		}
		if(filesList.size()<2) {
			MessageDialog.openError(shell, "Merge VCF Error","Not enough variant files were found. Please select at least two valid files");
			return false;
		}
		Collections.sort(filesList);
		selectedFiles.clear();
		selectedFiles.addAll(filesList);
		for (String vcfFile:filesList) {
			TableItem item = new TableItem(table, SWT.CHECK);
			item.setText(1, vcfFile);
		}

		TableEditor tableEditor = new TableEditor(table);
		tableEditor.horizontalAlignment = SWT.LEFT;
		tableEditor.grabHorizontal = true;
		tableEditor.minimumWidth = 50;

		
		
		String firstSelectedFile = filesList.get(0);
		
		lblReferenceFile = new Label(shell, SWT.NONE);
		lblReferenceFile.setBounds(10, 400, 180, 25);
		lblReferenceFile.setText("(*FASTA) Reference Genome:");
		

		txtReferenceFile = new Text(shell, SWT.BORDER);
		txtReferenceFile.setBounds(200, 400, 550, 25);
		txtReferenceFile.addMouseListener(mouse);
		// Suggest the latest stored genome
		String historyReference = HistoryManager.getHistory(firstSelectedFile, HistoryManager.KEY_REFERENCE_FILE);
		if (historyReference!=null) txtReferenceFile.setText(historyReference);
		
		btnReferenceFile = new Button(shell, SWT.NONE);
		btnReferenceFile.setBounds(760, 400, 25, 25);
		btnReferenceFile.setText("...");
		btnReferenceFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, selectedFiles.iterator().next(), txtReferenceFile);
			}
		});

		lblOutputFile = new Label(shell, SWT.NONE);
		lblOutputFile.setText("(*)Output File:");
		lblOutputFile.setFont(tfont);
		lblOutputFile.setBounds(10, 450, 180, 25);

		txtOutputFile = new Text(shell, SWT.BORDER);
		txtOutputFile.setBounds(200, 450, 550, 25);
		txtOutputFile.addMouseListener(mouse);
		
	
		File outputFile=new File(selectedFiles.iterator().next());
		String dirAbsolutePath = outputFile.getParentFile().getAbsolutePath();
		String suggestedOutFile = dirAbsolutePath+File.separator+"variantsfile.vcf";
		txtOutputFile.setText(suggestedOutFile);
		
		btnOutputFile = new Button(shell, SWT.NONE);
		btnOutputFile.setBounds(760, 450, 25, 25);
		btnOutputFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,  SWT.SAVE, selectedFiles.iterator().next(), txtOutputFile);
			}
		});
		btnOutputFile.setText("...");
		

		btnMixList = new Button(shell, SWT.NONE);
		btnMixList.setText("Determine list of variants");
		btnMixList.setBounds(50, 500, 200, 30);
		btnMixList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				determineVariants();
			}
		});
		

		btnMergeVcfFiles = new Button(shell, SWT.NONE);
		btnMergeVcfFiles.setText("Merge vcf files");
		btnMergeVcfFiles.setBounds(300, 500, 200, 30);
		btnMergeVcfFiles.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mergeVcf();
			}
		});
		
		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setText("Cancel");
		btnCancel.setBounds(550, 500, 200, 30);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		return true;
	}

	public void determineVariants() {
		
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		String nameFileDeterminateVariants=null;
		ArrayList<String> errors = new ArrayList<String>();
		if (txtReferenceFile.getText() == null || txtReferenceFile.getText().equals("")) {
			errors.add(FieldValidator.buildMessage(lblReferenceFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtReferenceFile.setBackground(oc);
		}
		if (txtOutputFile.getText() == null || txtOutputFile.getText().equals("")) {
			errors.add(FieldValidator.buildMessage(lblOutputFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutputFile.setBackground(oc);
		}
		
		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shell, "Determine variants");
			return;
		}
		
		
		
		List<String> vcfFiles = getSelectedFiles();
		
		
		if(vcfFiles.size()<2){
			MessageDialog.openError(shell, " Determine variants error"," Select at least two files ");
			return;
		}
		ReferenceGenome genome;
		try {
			genome = ReferenceGenomesFactory.getInstance().getGenome(txtReferenceFile.getText(), shell);
		} catch (IOException e) {
			MessageDialog.openError(shell, " Error loading reference genome",e.getMessage());
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
			MessageDialog.openError(shell, " Determine variants Error",e.getMessage());
			return;
		}
		HistoryManager.saveInHistory(HistoryManager.KEY_REFERENCE_FILE, txtReferenceFile.getText());
		MessageDialog.openInformation(shell,"Determine Variants is running",LoggingHelper.MESSAGE_PROGRESS_BAR + " and check if the file " + nameFileDeterminateVariants + " was created in the outputFile path");
		shell.dispose();
		
	}

	public void mergeVcf() {
		
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		ArrayList<String> errors = new ArrayList<String>();
		if (txtReferenceFile.getText() == null || txtReferenceFile.getText().equals("")) {
			errors.add(FieldValidator.buildMessage(lblReferenceFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtReferenceFile.setBackground(oc);
		}
		if (txtOutputFile.getText() == null || txtOutputFile.getText().equals("")) {
			errors.add(FieldValidator.buildMessage(lblOutputFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutputFile.setBackground(oc);
		}

		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shell, "Merge VCF files");
			return;
		}

		SyncMergeVCF job = new SyncMergeVCF("MergeVCF");
		
		List<String> vcfFiles = getSelectedFiles();
		
		if(vcfFiles.size()<2){
			MessageDialog.openError(shell, " Merge VCF Error"," Select at least two files ");
			return;
		}
		ReferenceGenome genome;
		try {
			genome = ReferenceGenomesFactory.getInstance().getGenome(txtReferenceFile.getText(), shell);
		} catch (IOException e) {
			MessageDialog.openError(shell, " Error loading reference genome",e.getMessage());
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
			MessageDialog.openError(shell, " Merge VCF Error",e.getMessage());
			return;
		}
		HistoryManager.saveInHistory(HistoryManager.KEY_REFERENCE_FILE, txtReferenceFile.getText());
		MessageDialog.openInformation(shell, "Merge VCF is running",LoggingHelper.MESSAGE_PROGRESS_BAR + "and check if the file " + filename + " was created in the outputFile path");
		shell.dispose();

	}

	private List<String> getSelectedFiles ( ) {
		List<String> selectedFiles = new ArrayList<>();
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			if (item.getChecked()) {
				selectedFiles.add(item.getText(1));
			}
		}
		return selectedFiles;
	}
	
}