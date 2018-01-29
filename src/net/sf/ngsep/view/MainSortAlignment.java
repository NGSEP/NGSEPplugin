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
public class MainSortAlignment implements SingleFileInputWindow {

	//General attributes
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
	
	private Label lblInputFile;
	private Text txtInputFile;
	private Button btnInputFile;
	
	private Label lblOutputFile;
	private Text txtOutputFile;
	private Button btnOutputFile;
	
	
	private Button btnSortAligments;
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

	protected void createContents() {
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setSize(800, 250);
		shell.setText("Sort Alignment");
		shell.setLocation(10, 10);

		MouseListenerNgsep mouse = new MouseListenerNgsep();

		lblInputFile = new Label(shell, SWT.NONE);
		lblInputFile.setBounds(10, 40, 140, 22);
		lblInputFile.setText("(*)File:");

		txtInputFile = new Text(shell, SWT.BORDER);
		txtInputFile.setBounds(160, 40, 580, 22);
		txtInputFile.addMouseListener(mouse);
		txtInputFile.setText(selectedFile);
		
		btnInputFile = new Button(shell, SWT.NONE);
		btnInputFile.setBounds(750, 40, 25, 22);
		btnInputFile.setText("...");
		btnInputFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile,txtInputFile);
			}
		});
		
		
		lblOutputFile = new Label(shell, SWT.NONE);
		lblOutputFile.setBounds(10, 80, 140, 22);
		lblOutputFile.setText("(*)Output File:");
		
		txtOutputFile = new Text(shell, SWT.BORDER);
		txtOutputFile.setBounds(160, 80, 580, 22);
		txtOutputFile.addMouseListener(mouse);
		String name = SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile);
		if (name.contains("_sorted")) {
			name = name.substring(0,name.lastIndexOf("_sorted"));
			name = name + "_sorted_1.bam";
		} else if (name.contains("sorted")) {
			name = name.substring(0,name.lastIndexOf("sorted"));
			name = name + "_sorted_1.bam";
		} else {
			name = name + "_sorted.bam";
		}
		txtOutputFile.setText(name);
			
		btnOutputFile = new Button(shell, SWT.NONE);
		btnOutputFile.setBounds(750, 80, 25, 22);
		btnOutputFile.setText("...");
		btnOutputFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, selectedFile,txtOutputFile);
				}
			});
		

		

		btnSortAligments = new Button(shell, SWT.NONE);
		btnSortAligments.setBounds(200, 150, 180, 25);
		btnSortAligments.setText("Sort Aligments");
		btnSortAligments.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();

			}
		});
		
		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(450, 150, 180, 25);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});

	}

	// This method captures the separator system to verify the path to create
	// the temporary folder keep generating temporary files for the command line
	// that is passed to the library of samSort.jar, the result is generated in
	// the file src the same file name but a reference to the text addiction
	// _Sorter
	public void proceed() {
		SyncSortAlignment job = new SyncSortAlignment("Sort alignments");
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		
		ArrayList<String> errors = new ArrayList<String>();
		if (txtInputFile.getText() == null || txtInputFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblInputFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtInputFile.setBackground(oc);
		} else {
			job.setInputFile(txtInputFile.getText());
		}

		if (txtOutputFile.getText() == null|| txtOutputFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblOutputFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutputFile.setBackground(oc);
		} else {
			job.setOutputFile(txtOutputFile.getText());
		}
		
		if(txtInputFile.getText().equals(txtOutputFile.getText())){
			errors.add(FieldValidator.buildMessage(lblOutputFile.getText(), FieldValidator.ERROR_SAME_NAME));
			txtOutputFile.setBackground(oc);
		}
		
		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shell, "Statistics");
			return;
		}
		String logFilename = LoggingHelper.getLoggerFilename(txtOutputFile.getText(),"SORT");
		job.setLogName(logFilename);
		job.setNameProgressBar(new File(txtOutputFile.getText()).getName());			
			
		try {
			job.schedule();
			MessageDialog.openInformation(shell, "Sort Alignment is running",LoggingHelper.MESSAGE_PROGRESS_NOBAR);
			shell.dispose();

		} catch (Exception e) {
			MessageDialog.openError(shell, " Sort Alignment Error",e.getMessage());
			e.printStackTrace();
			return;
		}
	}
}
