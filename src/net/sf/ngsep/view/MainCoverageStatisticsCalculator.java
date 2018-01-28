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

import net.sf.ngsep.control.SyncCoverageStatisticsCalculator;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.alignments.ReadAlignment;
import ngsep.discovery.CoverageStatisticsCalculator;

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
 * Main window for the coverage statistics calculator
 * @author Daniel Cruz
 * @author Juan Camilo Quintero
 * @author Juan Fernando de la Hoz
 * @author Jorge Duitama
 *
 */
public class MainCoverageStatisticsCalculator implements SingleFileInputWindow {

	protected Shell shell;
	private Display display;
	
	private String selectedFile;
	
	public String getSelectedFile() {
		return selectedFile;
	}

	public void setSelectedFile(String selectedFile) {
		this.selectedFile = selectedFile;
	}
	
	private Label lblAlifile;
	private Text txtAlifile;
	private Button btnAlifile;
	
	private Label lblOutputFilePrefix;
	private Text txtOutputFilePrefix;
	private Button btnOutputFilePrefix;
	
	private Label lblMinMQ;
	private Text txtMinMQ;
	
	private Label lblGraphical;
	private Button btnUniqueAlignments;
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
		shell.setSize(814, 300);
		shell.setText("Calculate Coverage Statistics");
		shell.setLocation(150, 200);

		MouseListenerNgsep mouse = new MouseListenerNgsep();
		
		lblAlifile = new Label(shell, SWT.NONE);
		lblAlifile.setBounds(10, 20, 140, 22);
		lblAlifile.setText("(*)File:");
		
		txtAlifile = new Text(shell, SWT.BORDER);
		txtAlifile.setBounds(160, 20, 580, 22);
		txtAlifile.addMouseListener(mouse);
		txtAlifile.setText(selectedFile);
		
		btnAlifile = new Button(shell, SWT.NONE);
		btnAlifile.setBounds(760, 20, 25, 22);
		btnAlifile.setText("...");
		btnAlifile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtAlifile);
				}
		});

		lblOutputFilePrefix = new Label(shell, SWT.NONE);
		lblOutputFilePrefix.setBounds(10, 60, 140, 22);
		lblOutputFilePrefix.setText("(*)Output files prefix:");

		txtOutputFilePrefix = new Text(shell, SWT.BORDER);
		txtOutputFilePrefix.setBounds(160, 60, 580, 22);
		txtOutputFilePrefix.addMouseListener(mouse);
		txtOutputFilePrefix.setText(SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile) + "Coverage");
		
		btnOutputFilePrefix = new Button(shell, SWT.NONE);
		btnOutputFilePrefix.setBounds(760, 60, 25, 22);
		btnOutputFilePrefix.setText("...");
		btnOutputFilePrefix.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, selectedFile, txtOutputFilePrefix);
			}
		});
		
		lblMinMQ = new Label(shell, SWT.NONE);
		lblMinMQ.setBounds(10, 100, 330, 22);
		lblMinMQ.setText("Minimum mapping quality unique alignments:");
		
		txtMinMQ = new Text(shell, SWT.BORDER);
		txtMinMQ.setBounds(350, 100, 200, 22);
		txtMinMQ.addMouseListener(mouse);
		txtMinMQ.setText(""+ReadAlignment.DEF_MIN_MQ_UNIQUE_ALIGNMENT);

		lblGraphical = new Label(shell, SWT.NONE);
		lblGraphical.setBounds(10, 150, 200, 22);
		lblGraphical.setText("Graphical output");
		lblGraphical.setFont(new Font(Display.getCurrent(), "Arial", 10, SWT.BOLD));

		Button btnAllAlignments = new Button(shell, SWT.RADIO);
		btnAllAlignments.setBounds(10, 190, 200, 22);
		btnAllAlignments.setText("All alignments");
		btnAllAlignments.setSelection(true);
		
		btnUniqueAlignments = new Button(shell, SWT.RADIO);
		btnUniqueAlignments.setBounds(220, 190, 200, 22);
		btnUniqueAlignments.setText("Unique alignments");
		
		btnStart = new Button(shell, SWT.NONE);
		btnStart.setBounds(240, 240, 130, 22);
		btnStart.setText("Statistics");
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();
			}
		});

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(410, 240, 130, 22);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
	}

	/**
	 * Runs the Coverage statistics process
	 */
	public void proceed() {
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		CoverageStatisticsCalculator instance = new CoverageStatisticsCalculator();
		SyncCoverageStatisticsCalculator job = new SyncCoverageStatisticsCalculator("Coverage statistics");
		job.setInstance(instance);
		
		ArrayList<String> errors = new ArrayList<String>();
		if (txtAlifile.getText() == null || txtAlifile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblAlifile.getText(),FieldValidator.ERROR_MANDATORY));
			txtAlifile.setBackground(oc);
		} else{
			job.setAlignmentsFile(txtAlifile.getText());
		}
		if (txtOutputFilePrefix.getText() == null || txtOutputFilePrefix.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblOutputFilePrefix.getText(),FieldValidator.ERROR_MANDATORY));
			txtOutputFilePrefix.setBackground(oc);
		} else { 
			instance.setOutFilename(txtOutputFilePrefix.getText()+".stats");
			job.setPlotFile(txtOutputFilePrefix.getText());
		}
		if (txtMinMQ.getText() != null && txtMinMQ.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMinMQ.getText(),new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblMinMQ.getText(), FieldValidator.ERROR_INTEGER));
				txtMinMQ.setBackground(oc);
			} else {
				instance.setMinMQ(Integer.parseInt(txtMinMQ.getText()));
			}
		}
		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shell,"Calculate Coverage Statistics");
			return;
		}
		job.setPlotUniqueAlignments(btnUniqueAlignments.getSelection());
		
		String logFilename = txtOutputFilePrefix.getText()+".log";
		job.setLogName(logFilename);
		job.setNameProgressBar(new File(txtOutputFilePrefix.getText()).getName());
		try {
			job.schedule();
			MessageDialog.openInformation(shell,"Calculate Coverage Statistics is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
			shell.dispose();

		} catch (Exception e) {
			MessageDialog.openError(shell,"Coverage Statistics Calculator Error", e.getMessage());
			e.printStackTrace();
			return;
		}
	}
}
