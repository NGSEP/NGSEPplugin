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


import net.sf.ngsep.control.SyncVCFDiversityCalculator;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.vcf.VCFDiversityCalculator;

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
 * 
 * @author Daniel Cruz
 * @author Juan Camilo Quintero
 * @author Jorge Duitama
 */
public class MainVCFDiversityCalculator implements SingleFileInputWindow {
	
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
	
	private Text txtVcfFile;
	private Text txtOutputFile;
	private Text txtPopfile;
	private Label lblVcfFile;
	private Label lblPopfile;
	private Label lblOutPutFile;
	private Button btnOutputFile;
	private Button btnVcfFile;
	private Button btnStart;
	private Button btnCancel;
	private Button btnPopfile;
	private Label lblSubpop;

	/**
	 * Open the window.
	 * 
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

	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN);
		shell.setSize(814, 280);
		shell.setText("VCF Diversity Calculator");
		shell.setLocation(150, 200);
		Font tfont = new Font(Display.getCurrent(), "Arial", 10, SWT.BOLD);
		MouseListenerNgsep mouse = new MouseListenerNgsep();

		lblVcfFile = new Label(shell, SWT.NONE);
		lblVcfFile.setText("(*)VCF File:");
		lblVcfFile.setBounds(10, 22, 167, 21);

		txtVcfFile = new Text(shell, SWT.BORDER);
		txtVcfFile.setBounds(205, 22, 545, 21);
		txtVcfFile.addMouseListener(mouse);
		txtVcfFile.setText(selectedFile);

		txtOutputFile = new Text(shell, SWT.BORDER);
		txtOutputFile.setBounds(205, 63, 545, 21);
		txtOutputFile.addMouseListener(mouse);
		String suggestedOutPrefix = SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile);
		txtOutputFile.setText(suggestedOutPrefix+"_Diversity.txt");

		lblOutPutFile = new Label(shell, SWT.NONE);
		lblOutPutFile.setText("(*)Output File:");
		lblOutPutFile.setBounds(10, 63, 167, 21);

		btnOutputFile = new Button(shell, SWT.NONE);
		btnOutputFile.setText("...");
		btnOutputFile.setBounds(761, 63, 21, 25);
		btnOutputFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, selectedFile, txtOutputFile);
			}
		});


		btnVcfFile = new Button(shell, SWT.NONE);
		btnVcfFile.setText("...");
		btnVcfFile.setBounds(761, 22, 21, 25);
		btnVcfFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtVcfFile);
			}
		}); 

		lblSubpop = new Label(shell, SWT.NONE);
		lblSubpop.setText("Define Subpopulations");
		lblSubpop.setFont(tfont);
		lblSubpop.setBounds(20, 107, 167, 21);

		lblPopfile = new Label(shell, SWT.NONE);
		lblPopfile.setText("Population File:");
		lblPopfile.setBounds(10, 150, 167, 21);

		txtPopfile = new Text(shell, SWT.BORDER);
		txtPopfile.setBounds(205, 150, 545, 21);
		txtPopfile.addMouseListener(mouse);

		btnPopfile = new Button(shell, SWT.NONE);
		btnPopfile.setText("...");
		btnPopfile.setBounds(761, 155, 21, 25);
		btnPopfile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtPopfile);
			}
		});
		
		btnStart = new Button(shell, SWT.NONE);
		btnStart.setText("Calculate");
		btnStart.setBounds(205, 196, 110, 25);
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				proceed();
			}
		});

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		btnCancel.setBounds(336, 196, 110, 25);
	}


	public void proceed() {	
		ArrayList<String> errors = new ArrayList<String>();
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		
		VCFDiversityCalculator instance = new VCFDiversityCalculator();
		SyncVCFDiversityCalculator job = new SyncVCFDiversityCalculator("VCF Diversity Calculator");
		job.setInstance(instance);

		if (txtVcfFile.getText() == null || txtVcfFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblVcfFile.getText(),FieldValidator.ERROR_MANDATORY));
			txtVcfFile.setBackground(oc);
		} else {
			job.setVcfFile(txtVcfFile.getText());
		}
		
		if (txtOutputFile.getText() == null || txtOutputFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblOutPutFile.getText(),FieldValidator.ERROR_MANDATORY));
			txtOutputFile.setBackground(oc);
		} else {
			job.setOutputFile(txtOutputFile.getText());
		}

		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shell,"VCF Diversity Calculator");
			return;
		}

		if (txtPopfile.getText() != null && txtPopfile.getText().length()>0) {
			job.setSamplesFile(txtPopfile.getText());
		}
		
		String outputFile = txtOutputFile.getText();
		String logFilename = LoggingHelper.getLoggerFilename(outputFile,"DC");
		job.setLogName(logFilename);
		job.setNameProgressBar(new File(outputFile).getName());

		try {
			job.schedule();
			MessageDialog.openInformation(shell, "VCF Diversity Calculator",LoggingHelper.MESSAGE_PROGRESS_BAR);
			shell.dispose();
		} catch (Exception e) {
			MessageDialog.openError(shell, "VCF Diversity Calculator",e.getMessage());
			e.printStackTrace();
			return;
		}
	}
}