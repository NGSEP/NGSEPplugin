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

import net.sf.ngsep.control.SyncIndexBowtie;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;

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
 * @author Juan Camilo Quintero
 * @author Jorge Duitama
 */
public class MainIndexBowtie implements SingleFileInputWindow {

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
	
	private Label lblReferenceFile;
	private Text txtReferenceFile;
	private Button btnReferenceFile;
	
	private Label lblIndexBowtie;
	private Text txtIndexBowtie;
	private Button btnIndexBowtie;
	
	private Button btnCreateIndex;
	private Button btnCancel;

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
	 * Create contents of the shell.
	 */
	protected void createContents() {
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setSize(800, 250);
		shell.setText("Create index Bowtie2");
		shell.setLocation(150, 200);
		MouseListenerNgsep mouse = new MouseListenerNgsep();
		
		lblReferenceFile = new Label(shell, SWT.NONE);
		lblReferenceFile.setText("(*)Reference:");
		lblReferenceFile.setBounds(10, 30, 180, 25);

		txtReferenceFile = new Text(shell, SWT.BORDER);
		txtReferenceFile.setBounds(200, 30, 550, 25);
		txtReferenceFile.addMouseListener(mouse);
		txtReferenceFile.setText(selectedFile);
		
		btnReferenceFile = new Button(shell, SWT.NONE);
		btnReferenceFile.setText("...");
		btnReferenceFile.setBounds(760, 30, 25, 25);
		btnReferenceFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtReferenceFile);
			}
		});
		
		lblIndexBowtie = new Label(shell, SWT.NONE);
		lblIndexBowtie.setText("(*)Bowtie2 Index Prefix:");
		lblIndexBowtie.setBounds(10, 70, 180, 25);
		
		txtIndexBowtie = new Text(shell, SWT.BORDER);
		txtIndexBowtie.setBounds(200, 70, 550, 25);
		txtIndexBowtie.addMouseListener(mouse);
		txtIndexBowtie.setText(selectedFile);

		btnIndexBowtie = new Button(shell, SWT.NONE);
		btnIndexBowtie.setText("...");
		btnIndexBowtie.setBounds(760, 70, 25, 25);
		btnIndexBowtie.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, selectedFile,txtIndexBowtie);
			}
		});

		btnCreateIndex = new Button(shell, SWT.NONE);
		btnCreateIndex.setBounds(150, 150, 200, 50);
		btnCreateIndex.setText("Create Index");
		btnCreateIndex.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();
			}
		});

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setText("Cancel");
		btnCancel.setBounds(450, 150, 200, 50);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});

	}

	public void proceed() {
		
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		
		//String strReference=null;
		//String strIndexBowtie=null;
		String strCommand = "bowtie2-build";
		if (SpecialFieldsHelper.isWindows()) {
			strCommand = "bowtie2-build.exe";
		}
		List<String> commandArray = new ArrayList<String>();
		commandArray.add(strCommand);
		ArrayList<String> listErrors = new ArrayList<String>();
		if (txtReferenceFile.getText() == null|| txtReferenceFile.getText().equals("")) {
			listErrors.add(FieldValidator.buildMessage(lblReferenceFile.getText(),FieldValidator.ERROR_MANDATORY));
			txtReferenceFile.setBackground(oc);
		} else {
			commandArray.add(SpecialFieldsHelper.maskWhiteSpaces(txtReferenceFile.getText()));
		}

		if (txtIndexBowtie.getText() == null|| txtIndexBowtie.getText().equals("")) {
			listErrors.add(FieldValidator.buildMessage(lblIndexBowtie.getText(),FieldValidator.ERROR_MANDATORY));
			txtIndexBowtie.setBackground(oc);
		} else {
			commandArray.add(SpecialFieldsHelper.maskWhiteSpaces(txtIndexBowtie.getText()));
		}

		if (listErrors.size() > 0) {
			FieldValidator.paintErrors(listErrors, shell, "Create index Bowtie2");
			return;
		}
		String outputFile=txtIndexBowtie.getText();
		String logFilename = LoggingHelper.getLoggerFilename(outputFile,"CS");
		SyncIndexBowtie indexBowtie = new SyncIndexBowtie("Create Index Bowtie");
		indexBowtie.setCommandArray(commandArray);
		indexBowtie.setLogName(logFilename);
		indexBowtie.setNameProgressBar(new File(outputFile).getName());
		try {
			indexBowtie.schedule();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		HistoryManager.saveInHistory(HistoryManager.KEY_REFERENCE_FILE, txtReferenceFile.getText());
		HistoryManager.saveInHistory(HistoryManager.KEY_BOWTIE2_INDEX, txtIndexBowtie.getText());
		MessageDialog.openInformation(shell, "Create Index Bowtie is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
		shell.dispose();
	}
}
