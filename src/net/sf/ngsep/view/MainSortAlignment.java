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

import java.util.ArrayList;

import net.sf.ngsep.control.SyncSortAlignment;
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
public class MainSortAlignment {

	protected Shell shell;
	private Text txtFileSam;
	private String aliFile;
	private Button btnCancel;
	private Label lblFileSam;
	private Button btnSortAligment;
	private Text txtOutPutFile;
	private Button btnOutPutFile;
	private Label lblOutputFile;
	private Display display;
	private Button btnFile;
	
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

	protected void createContents() {
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setSize(752, 213);
		shell.setText("Sort Alignment");
		shell.setLocation(150, 200);

		MouseListenerNgsep mouse = new MouseListenerNgsep();

		lblFileSam = new Label(shell, SWT.NONE);
		lblFileSam.setBounds(23, 40, 95, 20);
		lblFileSam.setText("(*)File (.bam):");

		txtFileSam = new Text(shell, SWT.BORDER);
		String filePathSystem = getAliFile();
		if (filePathSystem != null && !filePathSystem.equals("")) {
			txtFileSam.setText(filePathSystem);
		}
		txtFileSam.setBounds(124, 37, 545, 21);
		txtFileSam.addMouseListener(mouse);

		btnSortAligment = new Button(shell, SWT.NONE);
		btnSortAligment.setBounds(124, 111, 110, 25);
		btnSortAligment.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();

			}
		});
		btnSortAligment.setText("Sort Aligment");

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		btnCancel.setBounds(257, 111, 110, 25);

		lblOutputFile = new Label(shell, SWT.NONE);
		lblOutputFile.setBounds(23, 75, 95, 20);
		lblOutputFile.setText("(*)Output File:");

		txtOutPutFile = new Text(shell, SWT.BORDER);
		txtOutPutFile.setBounds(124, 74, 545, 21);
		txtOutPutFile.addMouseListener(mouse);
		String runtimeOne = txtFileSam.getText();
		// Here is validated if the referenced file exists and if so then I
		// capture file name and you add_Sorted.bam default and is placed
		// in the box of the output file
		if (txtFileSam.getText() != null && !txtFileSam.equals("")) {
			String nameStr = runtimeOne.substring(0,runtimeOne.lastIndexOf("."));
			if (nameStr.contains(".")) {
				String outputFileAuxiliary = nameStr.substring(0,nameStr.lastIndexOf("."));
				if (outputFileAuxiliary != null && !outputFileAuxiliary.equals(""))
					outputFileAuxiliary = outputFileAuxiliary + "_sorted.bam";
					txtOutPutFile.setText(outputFileAuxiliary);
			} else {
				if (nameStr != null && !nameStr.equals("")) {
					if (nameStr.contains("_sorted")) {
						nameStr = runtimeOne.substring(0,runtimeOne.lastIndexOf("_sorted"));
						nameStr = nameStr + "_sorted_1.bam";
						txtOutPutFile.setText(nameStr);
					} else if (nameStr.contains("sorted")) {
						nameStr = runtimeOne.substring(0,runtimeOne.lastIndexOf("sorted"));
						nameStr = nameStr + "_sorted_1.bam";
						txtOutPutFile.setText(nameStr);
					} else {
						nameStr = nameStr + "_sorted.bam";
						txtOutPutFile.setText(nameStr);
					}
				}
			}
		}
		btnOutPutFile = new Button(shell, SWT.NONE);
		btnOutPutFile.setBounds(672, 70, 21, 25);
		btnOutPutFile.addSelectionListener(new SelectionAdapter() {
			// this method returns me the path where the eclipse runtime
			// files according to separator that has the operating system
			// for windows \ Linux / to trigger the button event to
			// add a file to the screen, I suggest a name for the
			// file output with possible extensions
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, aliFile,txtOutPutFile);
				}
			});
		btnOutPutFile.setText("...");

		btnFile = new Button(shell, SWT.NONE);
		btnFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, aliFile,txtFileSam);
			}
		});
		btnFile.setText("...");
		btnFile.setBounds(673, 37, 21, 25);

	}

	// This method captures the separator system to verify the path to create
	// the temporary folder keep generating temporary files for the command line
	// that is passed to the library of samSort.jar, the result is generated in
	// the file src the same file name but a reference to the text addiction
	// _Sorter
	public void proceed() {
		try {
			Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
			ArrayList<String> errors = new ArrayList<String>();
			if (txtFileSam.getText() == null || txtFileSam.getText().length()==0) {
				errors.add(FieldValidator.buildMessage(lblFileSam.getText(), FieldValidator.ERROR_MANDATORY));
				txtFileSam.setBackground(oc);
			}

			if (txtOutPutFile.getText() == null|| txtOutPutFile.getText().length()==0) {
				errors.add(FieldValidator.buildMessage(lblOutputFile.getText(), FieldValidator.ERROR_MANDATORY));
				txtOutPutFile.setBackground(oc);
			}
			
			if(txtFileSam.getText().equals(txtOutPutFile.getText())){
				errors.add(FieldValidator.buildMessage(lblOutputFile.getText(), FieldValidator.ERROR_SAME_NAME));
				txtOutPutFile.setBackground(oc);
			}
			
			if (errors.size() > 0) {
				FieldValidator.paintErrors(errors, shell, "Statistics");
				return;
			}
					
			SyncSortAlignment syncSortAligment = new SyncSortAlignment();
			syncSortAligment.setInputFile(txtFileSam.getText());
			syncSortAligment.setOutputFile(txtOutPutFile.getText());
			syncSortAligment.runJob();
			MessageDialog.openInformation(shell, "Sort Alignment is running",LoggingHelper.MESSAGE_PROGRESS_NOBAR);
			shell.dispose();

		} catch (Exception e) {
			MessageDialog.openError(shell, " Sort Alignment Error",e.getMessage());
			e.printStackTrace();
			return;
		}
	}

	public String getAliFile() {
		return aliFile;
	}

	public void setAliFile(String aliFile) {
		this.aliFile = aliFile;
	}
}
