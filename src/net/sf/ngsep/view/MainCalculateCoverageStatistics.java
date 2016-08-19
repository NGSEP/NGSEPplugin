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
import java.util.logging.FileHandler;

import net.sf.ngsep.control.SyncCalculateCoverageStatistics;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;

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
 * @author Daniel Cruz, Juan Camilo Quintero, Juan Fernando de la Hoz
 *
 */
public class MainCalculateCoverageStatistics {
	
	private String aliFile;
	
	protected Shell shell;
	private Display display;
	private Label lblAlifile;
	private Label lblOutPutFile;
	private Label lblGraphical;
	private Text txtAlifile;
	private Text txtOutputFile;
	private Button btnAlifile;
	private Button btnOutputFile;
	private Button btnMultipleAlignments;
	private Button btnStart;
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
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN);
		shell.setSize(814, 280);
		shell.setText("Calculate Coverage Statistics");
		shell.setLocation(150, 200);

		MouseListenerNgsep mouse = new MouseListenerNgsep();
		Font tfont = new Font(Display.getCurrent(), "Arial", 10, SWT.BOLD);
		
		lblAlifile = new Label(shell, SWT.NONE);
		lblAlifile.setBounds(10, 22, 167, 21);
		lblAlifile.setText("(*)File:");
		
		txtAlifile = new Text(shell, SWT.BORDER);
		txtAlifile.setBounds(205, 22, 545, 21);
		txtAlifile.addMouseListener(mouse);
		
		btnAlifile = new Button(shell, SWT.NONE);
		btnAlifile.setBounds(761, 22, 21, 25);
		btnAlifile.setText("...");
		btnAlifile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, aliFile,txtAlifile);
				}
		});

		lblOutPutFile = new Label(shell, SWT.NONE);
		lblOutPutFile.setBounds(10, 63, 167, 21);
		lblOutPutFile.setText("(*)Output File:");

		txtOutputFile = new Text(shell, SWT.BORDER);
		txtOutputFile.setBounds(205, 63, 545, 21);
		txtOutputFile.addMouseListener(mouse);
		if (aliFile!= null && aliFile.length()>0) {
			txtAlifile.setText(aliFile);
			String nameStr = aliFile.substring(0,aliFile.lastIndexOf("."));
			txtOutputFile.setText(nameStr + "Coverage");
		}
		
		btnOutputFile = new Button(shell, SWT.NONE);
		btnOutputFile.setBounds(761, 63, 21, 25);
		btnOutputFile.setText("...");
		btnOutputFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, aliFile,txtOutputFile);
			}
		});

		lblGraphical = new Label(shell, SWT.NONE);
		lblGraphical.setBounds(10, 120, 167, 21);
		lblGraphical.setText("Graphical Output");
		lblGraphical.setFont(tfont);

		btnMultipleAlignments = new Button(shell, SWT.RADIO);
		btnMultipleAlignments.setBounds(10, 150, 167, 21);
		btnMultipleAlignments.setText("Multiple alignments");
		
		Button btnUniqueAlignments = new Button(shell, SWT.RADIO);
		btnUniqueAlignments.setBounds(180, 150, 167, 21);
		btnUniqueAlignments.setText("Unique alignments");
		btnUniqueAlignments.setSelection(true);
		
		btnStart = new Button(shell, SWT.NONE);
		btnStart.setBounds(205, 200, 110, 25);
		btnStart.setText("Statistics");
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				process();
			}
		});

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(336, 200, 110, 25);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
	}

	// This method validates input parameters MainPositionStatistics screen and
	// calls the method of executing a syncStaticts class which runs the process
	// of generating statistics.
	public void process() {
		try {
			SyncCalculateCoverageStatistics syncCoverageCalculator = new SyncCalculateCoverageStatistics();
			
			Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
			ArrayList<String> errors = new ArrayList<String>();
			if (txtAlifile.getText() == null || txtAlifile.getText().length()==0) {
				errors.add(FieldValidator.buildMessage(lblAlifile.getText(),FieldValidator.ERROR_MANDATORY));
				txtAlifile.setBackground(oc);
			}else{
				syncCoverageCalculator.setAlifile(txtAlifile.getText());
			}
			if (txtOutputFile.getText() == null || txtOutputFile.getText().length()==0) {
				errors.add(FieldValidator.buildMessage(lblOutPutFile.getText(),FieldValidator.ERROR_MANDATORY));
				txtOutputFile.setBackground(oc);
			}else{
				syncCoverageCalculator.setOutputFile(txtOutputFile.getText());
				syncCoverageCalculator.setNameProgressBar(new File(txtOutputFile.getText()).getName());
				String logFilename = LoggingHelper.getLoggerFilename(txtOutputFile.getText(),"CS");
				FileHandler logFile = new FileHandler(logFilename, false);
				syncCoverageCalculator.setLogName(logFilename);
				syncCoverageCalculator.setLogFile(logFile);
			}
			if (errors.size() > 0) {
				FieldValidator.paintErrors(errors, shell,"Calculate Coverage Statistics");
				return;
			}
			
			syncCoverageCalculator.setMultipleAlignments(btnMultipleAlignments.getSelection());
			
			syncCoverageCalculator.runJob();
			MessageDialog.openInformation(shell,"Calculate Coverage Statistics is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
			shell.dispose();

		} catch (Exception e) {
			MessageDialog.openError(shell,"Coverage Statistics Calculator Error", e.getMessage());
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
