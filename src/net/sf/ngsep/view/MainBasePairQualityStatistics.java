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

import net.sf.ngsep.control.SyncBasePairQualityStatistics;
import net.sf.ngsep.utilities.EclipseProjectHelper;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.alignments.BasePairQualityStatisticsCalculator;
import ngsep.genome.ReferenceGenome;

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
public class MainBasePairQualityStatistics {

	private String aliFile;
	
	protected Shell shell;
	private Display display;
	private Label lblFile;
	private Label lblReferenceFile;
	private Label lblOutputFile;
	private Label lblGraphicalOutput;
	private Text txtFile;
	private Text txtReferenceTex;
	private Text txtOutputText;
	private Button btnFile;
	private Button btnReferenceButton;
	private Button btnOutputButton;
	private Button btnUniqueAlignments;
	private Button btnProcess;
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
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setSize(855, 342);
		shell.setText("Calculate Quality Statistics");
		shell.setLocation(150, 200);

		MouseListenerNgsep mouse = new MouseListenerNgsep();
		Font tfont = new Font(Display.getCurrent(), "Arial", 10, SWT.BOLD);

		lblFile = new Label(shell, SWT.NONE);
		lblFile.setBounds(10, 11, 167, 21);
		lblFile.setText("(*)File:");

		txtFile = new Text(shell, SWT.BORDER);
		txtFile.setBounds(205, 12, 545, 21);
		txtFile.addMouseListener(mouse);

		btnFile = new Button(shell, SWT.NONE);
		btnFile.setBounds(761, 11, 21, 25);
		btnFile.setText("...");
		btnFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, aliFile,txtFile);
			}
		});
		
		lblReferenceFile = new Label(shell, SWT.NONE);
		lblReferenceFile.setBounds(10, 51, 167, 21);
		lblReferenceFile.setText("(*)Reference File:");

		txtReferenceTex = new Text(shell, SWT.BORDER);
		txtReferenceTex.setBounds(205, 52, 545, 21);
		txtReferenceTex.addMouseListener(mouse);
		// Here you take the route of the project in the system and suggest the
		// direction for the text box reference file
		try {
			String directoryProject = EclipseProjectHelper.findProjectDirectory(aliFile);
			String historyFile = HistoryManager.createPathRecordGeneral(directoryProject);
			String historyReference = HistoryManager.getPathRecordReference(historyFile);
			if (historyReference!=null) {
				txtReferenceTex.setText(historyReference);
			}
		} catch (Exception e) {
			e.getMessage();
			MessageDialog.openError(shell," Quality Statistics Error","An error occurred while locating the file path of the reference, possibly the system can not recover that route."+ e.getMessage());
		}
		
		btnReferenceButton = new Button(shell, SWT.NONE);
		btnReferenceButton.setBounds(761, 51, 21, 25);
		btnReferenceButton.setText("...");
		btnReferenceButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, aliFile,txtReferenceTex);
			}
		});
		
		lblOutputFile = new Label(shell, SWT.NONE);
		lblOutputFile.setBounds(10, 97, 167, 21);
		lblOutputFile.setText("(*)Output File Prefix:");

		txtOutputText = new Text(shell, SWT.BORDER);
		txtOutputText.setBounds(205, 98, 545, 21);
		txtOutputText.addMouseListener(mouse);
		if (aliFile != null && aliFile.length()>0) {
			txtFile.setText(aliFile);
			String srtOutPutFileOne = aliFile.substring(0,aliFile.lastIndexOf("."));
			txtOutputText.setText(srtOutPutFileOne + "ReadPos");
		}
		
		btnOutputButton = new Button(shell, SWT.NONE);
		btnOutputButton.setBounds(761, 97, 21, 25);
		btnOutputButton.setText("...");
		btnOutputButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, aliFile,txtOutputText);
			}
		});

		lblGraphicalOutput = new Label(shell, SWT.NONE);
		lblGraphicalOutput.setBounds(10, 150, 167, 21);
		lblGraphicalOutput.setText("Graphical output");
		lblGraphicalOutput.setFont(tfont);

		Button btnMultipleAlignments = new Button(shell, SWT.RADIO);
		btnMultipleAlignments.setBounds(10, 180, 167, 21);
		btnMultipleAlignments.setText("Multiple alignments");
		
		btnUniqueAlignments = new Button(shell, SWT.RADIO);
		btnUniqueAlignments.setBounds(180, 180, 167, 21);
		btnUniqueAlignments.setText("Unique alignments");
		btnUniqueAlignments.setSelection(true);

		btnProcess = new Button(shell, SWT.NONE);
		btnProcess.setBounds(205, 245, 110, 25);
		btnProcess.setText("Statistics");
		btnProcess.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				process();
			}
		});

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(336, 245, 110, 25);
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
		
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		ArrayList<String> errors = new ArrayList<String>();
		BasePairQualityStatisticsCalculator calculator = new BasePairQualityStatisticsCalculator();
		
		if (txtFile.getText() == null || txtFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtFile.setBackground(oc);
		}
		if (txtReferenceTex.getText() == null || txtReferenceTex.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblReferenceFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtReferenceTex.setBackground(oc);
		} else {
			try {
				ReferenceGenome genome = ReferenceGenomesFactory.getInstance().getGenome(txtReferenceTex.getText(), shell);
				calculator.setGenome(genome);
			} catch (IOException e) {
				e.printStackTrace();
				errors.add(FieldValidator.buildMessage(lblReferenceFile.getText(), "error loading file: "+e.getMessage()));
				txtReferenceTex.setBackground(oc);
			}
		}
		if (txtOutputText.getText() == null || txtOutputText.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblOutputFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutputText.setBackground(oc);
		}
		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shell, "Statistics");
			return;
		}
		String logFilename = LoggingHelper.getLoggerFilename(txtOutputText.getText(),"PS");
		SyncBasePairQualityStatistics syncStatistics = new SyncBasePairQualityStatistics("BasePairQualityStats");
		syncStatistics.setAliFile(txtFile.getText());
		syncStatistics.setPositStatsCalculator(calculator);
		syncStatistics.setOutputFile(txtOutputText.getText());
		syncStatistics.setNameProgressBar(new File(txtOutputText.getText()).getName());
		syncStatistics.setLogName(logFilename);
		syncStatistics.setUniqueAlignments(btnUniqueAlignments.getSelection());	
			
		// this piece is stored in the project path in the system and the
		// address entered by the user to the reference file,
		// then stored in a file such routes that will have the long history
		// of the last reference entered.
		String directoryProject = null;
		try {
			directoryProject = EclipseProjectHelper.findProjectDirectory(aliFile);
			String routeMap = HistoryManager.createPathRecordGeneral(directoryProject);
			HistoryManager.createPathRecordFiles(routeMap, txtReferenceTex.getText().toString());
		} catch (Exception e) {
			MessageDialog.openError(shell, " Quality Statistics Error","error while trying to place the reference path history most recently used"+ e.getMessage());
			return;
		}
		
		try {
			syncStatistics.schedule();
		} catch (Exception e) {
			MessageDialog.openError(shell, "Quality Statistics Calculator Error",e.getMessage());
			e.printStackTrace();
			return;
		}
		MessageDialog.openInformation(shell,"Calculate Quality Statistics is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
		shell.dispose();
	}

	public String getAliFile() {
		return aliFile;
	}

	public void setAliFile(String aliFile) {
		this.aliFile = aliFile;
	}
}
