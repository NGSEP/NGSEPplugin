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
import java.util.logging.FileHandler;

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
 * 
 * @author Juan Camilo Quintero
 *
 */
public class MainIndexBowtie {

	protected Shell shlCreate;
	// address directory
	private String aliFile;
	private Display display;
	private Text txtReference;
	private Text txtIndexBowtie;
	private Label lblreference;
	private Label lblindexPrefixBowtie;
	private Button btnIndexBowtie;
	private Button btnReferenceFile;
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
		shlCreate.open();
		shlCreate.layout();
		while (!shlCreate.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		shlCreate = new Shell(display, SWT.SHELL_TRIM);
		shlCreate.setSize(810, 247);
		shlCreate.setText("Create index Bowtie2");
		shlCreate.setLocation(150, 200);
		MouseListenerNgsep mouse = new MouseListenerNgsep();
		lblreference = new Label(shlCreate, SWT.NONE);
		lblreference.setText("(*)Reference:");
		lblreference.setBounds(10, 38, 161, 21);

		txtIndexBowtie = new Text(shlCreate, SWT.BORDER);
		txtIndexBowtie.setBounds(181, 75, 545, 26);
		txtIndexBowtie.addMouseListener(mouse);
		txtReference = new Text(shlCreate, SWT.BORDER);
		txtReference.setEnabled(true);
		txtReference.setBounds(181, 35, 545, 26);
		txtReference.addMouseListener(mouse);
		String referenceFile = getAliFile();
		if (referenceFile != null && !referenceFile.equals("")) {
			txtReference.setText(referenceFile);
			txtIndexBowtie.setText(referenceFile);
		}
		lblindexPrefixBowtie = new Label(shlCreate, SWT.NONE);
		lblindexPrefixBowtie.setText("(*)Index Bowtie2 Prefix:");
		lblindexPrefixBowtie.setBounds(10, 78, 161, 21);

		btnIndexBowtie = new Button(shlCreate, SWT.NONE);
		btnIndexBowtie.setText("...");
		btnIndexBowtie.setBounds(745, 73, 24, 25);
		btnIndexBowtie.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shlCreate, SWT.SAVE, aliFile,txtIndexBowtie);
			}
		});

		btnReferenceFile = new Button(shlCreate, SWT.NONE);
		btnReferenceFile.setText("...");
		btnReferenceFile.setBounds(745, 34, 24, 25);
		btnReferenceFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shlCreate, SWT.OPEN, aliFile,txtReference);
			}
		});

		btnCreateIndex = new Button(shlCreate, SWT.NONE);
		btnCreateIndex.setBounds(181, 154, 124, 30);
		btnCreateIndex.setText("Create Index");
		btnCreateIndex.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();
			}
		});

		btnCancel = new Button(shlCreate, SWT.NONE);
		btnCancel.setText("Cancel");
		btnCancel.setBounds(364, 154, 124, 30);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlCreate.close();
			}
		});

	}

	public void proceed() {
		try {
			Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
			SyncIndexBowtie indexBowtie = new SyncIndexBowtie();
			//String strReference=null;
			//String strIndexBowtie=null;
			String strCommand = "bowtie2-build";
			if (SpecialFieldsHelper.isWindows()) {
				strCommand = "bowtie2-build.exe";
			}
			List<String> commandArray = new ArrayList<String>();
			commandArray.add(strCommand);
			ArrayList<String> listErrors = new ArrayList<String>();
			if (txtReference.getText() == null|| txtReference.getText().equals("")) {
				listErrors.add(FieldValidator.buildMessage(lblreference.getText(),FieldValidator.ERROR_MANDATORY));
				txtReference.setBackground(oc);
			} else {
				commandArray.add(SpecialFieldsHelper.maskWhiteSpaces(txtReference.getText()));
			}

			if (txtIndexBowtie.getText() == null|| txtIndexBowtie.getText().equals("")) {
				listErrors.add(FieldValidator.buildMessage(lblindexPrefixBowtie.getText(),FieldValidator.ERROR_MANDATORY));
				txtIndexBowtie.setBackground(oc);
			} else {
				commandArray.add(SpecialFieldsHelper.maskWhiteSpaces(txtIndexBowtie.getText()));
			}

			if (listErrors.size() > 0) {
				FieldValidator.paintErrors(listErrors, shlCreate, "Create index Bowtie2");
				return;
			}
			// this piece is stored in the project path in the system and the
			// address entered by the user to the reference file,
			// then stored in a file such routes that will have the long history
			// of the last reference entered.
			String outputFile=txtIndexBowtie.getText();
			String logFilename = LoggingHelper.getLoggerFilename(outputFile,"CS");
			FileHandler logFile = new FileHandler(logFilename, false);
			indexBowtie.setCommandArray(commandArray);
			indexBowtie.setLogName(logFilename);
			indexBowtie.setLogFile(logFile);
			indexBowtie.setNameProgressBar(new File(outputFile).getName());
			indexBowtie.runJob();
			MessageDialog.openInformation(shlCreate, "Create Index Bowtie is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
			shlCreate.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getAliFile() {
		return aliFile;
	}

	public void setAliFile(String aliFile) {
		this.aliFile = aliFile;
	}
}
