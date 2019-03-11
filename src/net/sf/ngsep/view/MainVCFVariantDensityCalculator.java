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

import net.sf.ngsep.control.SyncVCFVariantDensityCalculator;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.genome.ReferenceGenome;
import ngsep.vcf.VCFVariantDensityCalculator;

/**
 * 
 * @author Jorge Duitama
 *
 */
public class MainVCFVariantDensityCalculator implements SingleFileInputWindow {
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
	private Label lblReferenceFile;
	private Text txtReferenceFile;
	private Button btnReferenceFile;
	private Label lblOutputFile;
	private Text txtOutputFile;
	private Button btnOutputFile;
	private Label lblWindowLength;
	private Text txtWindowLength;
	
	
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
		shell.setSize(800, 350);
		shell.setText("VCF variant density calculator");
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
		
		lblOutputFile = new Label(shell, SWT.NONE);
		lblOutputFile.setBounds(10, 110, 180, 22);
		lblOutputFile.setText("(*) Output File:");
		
		txtOutputFile = new Text(shell, SWT.BORDER);
		txtOutputFile.setBounds(200, 110, 550, 22);
		txtOutputFile.addMouseListener(mouse);
		String suggestedOutPrefix = SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile);
		txtOutputFile.setText(suggestedOutPrefix+"_densityStats.txt");
		
		btnOutputFile = new Button(shell, SWT.NONE);
		btnOutputFile.setBounds(760, 110, 25, 22);
		btnOutputFile.setText("...");
		btnOutputFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.SAVE, selectedFile, txtOutputFile);
			}
		});
		
		lblWindowLength = new Label(shell, SWT.NONE);
		lblWindowLength.setText("Window length:");
		lblWindowLength.setBounds(10, 150, 180, 22);
		
		txtWindowLength = new Text(shell, SWT.BORDER);
		txtWindowLength.setBounds(200, 150, 100, 22);
		txtWindowLength.setText(String.valueOf(VCFVariantDensityCalculator.DEF_WINDOW_LENGTH));
		txtWindowLength.addMouseListener(mouse);
		
		btnSubmit = new Button(shell, SWT.NONE);
		btnSubmit.setBounds(240, 250, 200, 25);
		btnSubmit.setText("Density Statistics");
		btnSubmit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();
			}
		});
		

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(460, 250, 200, 25);
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
		SyncVCFVariantDensityCalculator job = new SyncVCFVariantDensityCalculator("VCF variants density calculator");
		VCFVariantDensityCalculator instance = new VCFVariantDensityCalculator ();
		job.setInstance(instance);
		
		if (txtFile.getText() == null || txtFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtFile.setBackground(oc);
		} else {
			job.setInputFile(txtFile.getText());
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

		if (txtOutputFile.getText() == null|| txtOutputFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblOutputFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutputFile.setBackground(oc);
		} else {
			job.setOutputFile(txtOutputFile.getText());
		}
		
		if (txtWindowLength.getText() != null && txtWindowLength.getText().length()!=0) {
			if (!FieldValidator.isNumeric(txtWindowLength.getText(),new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblWindowLength.getText(), FieldValidator.ERROR_INTEGER));
				txtWindowLength.setBackground(oc);
			} else {
				instance.setWindowLength(Integer.parseInt(txtWindowLength.getText()));				
			}
		}
		
		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shell, "VCF variants density calculator");
			return;
		}
		
		
		
		String outputFile = txtOutputFile.getText();
		String logFilename = LoggingHelper.getLoggerFilename(outputFile,"VVDC");
		job.setLogName(logFilename);
		job.setNameProgressBar(new File(outputFile).getName());
		try {
			job.schedule();
		} catch (Exception e) {
			MessageDialog.openError(shell," VCF variants density calculator Error", e.getMessage());
			e.printStackTrace();
		}
		HistoryManager.saveInHistory(HistoryManager.KEY_REFERENCE_FILE, txtReferenceFile.getText());
		MessageDialog.openInformation(shell,"VCF variants density calculator is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
		shell.dispose();
	}
}
