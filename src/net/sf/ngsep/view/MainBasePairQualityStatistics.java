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
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.alignments.BasePairQualityStatisticsCalculator;
import ngsep.alignments.ReadAlignment;
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
 * @author Daniel Cruz
 * @author Juan Camilo Quintero
 * @author Juan Fernando de la Hoz
 * @author Jorge Duitama
 *
 */
public class MainBasePairQualityStatistics implements SingleFileInputWindow {

	protected Shell shell;
	private Display display;
	
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
	
	private Label lblReferenceFile;
	private Text txtReferenceFile;
	private Button btnReferenceFile;
	
	private Label lblOutputPrefix;
	private Text txtOutputPrefix;
	private Button btnOutputPrefix;
	
	private Label lblMinMQ;
	private Text txtMinMQ;
	
	private Label lblGraphicalOutput;
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
		shell.setSize(800, 350);
		shell.setText("Calculate Quality Statistics");
		shell.setLocation(150, 200);

		MouseListenerNgsep mouse = new MouseListenerNgsep();

		lblInputFile = new Label(shell, SWT.NONE);
		lblInputFile.setBounds(10, 20, 140, 22);
		lblInputFile.setText("(*)File:");

		txtInputFile = new Text(shell, SWT.BORDER);
		txtInputFile.setBounds(160, 20, 580, 22);
		txtInputFile.addMouseListener(mouse);
		txtInputFile.setText(selectedFile);

		btnInputFile = new Button(shell, SWT.NONE);
		btnInputFile.setBounds(760, 20, 25, 22);
		btnInputFile.setText("...");
		btnInputFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtInputFile);
			}
		});
		
		lblReferenceFile = new Label(shell, SWT.NONE);
		lblReferenceFile.setBounds(10, 60, 140, 22);
		lblReferenceFile.setText("(*)Reference File:");

		txtReferenceFile = new Text(shell, SWT.BORDER);
		txtReferenceFile.setBounds(160, 60, 580, 22);
		txtReferenceFile.addMouseListener(mouse);
		// Suggest the latest stored genome
		String historyReference = HistoryManager.getHistory(selectedFile, HistoryManager.KEY_REFERENCE_FILE);
		if (historyReference!=null) txtReferenceFile.setText(historyReference);
		
		btnReferenceFile = new Button(shell, SWT.NONE);
		btnReferenceFile.setBounds(760, 60, 25, 22);
		btnReferenceFile.setText("...");
		btnReferenceFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtReferenceFile);
			}
		});
		
		lblOutputPrefix = new Label(shell, SWT.NONE);
		lblOutputPrefix.setBounds(10, 100, 140, 22);
		lblOutputPrefix.setText("(*)Output files prefix:");

		txtOutputPrefix = new Text(shell, SWT.BORDER);
		txtOutputPrefix.setBounds(160, 100, 580, 22);
		txtOutputPrefix.addMouseListener(mouse);
		txtOutputPrefix.setText(SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile) + "ReadPos");
		
		btnOutputPrefix = new Button(shell, SWT.NONE);
		btnOutputPrefix.setBounds(760, 100, 25, 22);
		btnOutputPrefix.setText("...");
		btnOutputPrefix.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, selectedFile, txtOutputPrefix);
			}
		});

		lblMinMQ = new Label(shell, SWT.NONE);
		lblMinMQ.setBounds(10, 140, 330, 22);
		lblMinMQ.setText("Minimum mapping quality unique alignments:");
		
		txtMinMQ = new Text(shell, SWT.BORDER);
		txtMinMQ.setBounds(350, 140, 200, 22);
		txtMinMQ.addMouseListener(mouse);
		txtMinMQ.setText(""+ReadAlignment.DEF_MIN_MQ_UNIQUE_ALIGNMENT);
		
		lblGraphicalOutput = new Label(shell, SWT.NONE);
		lblGraphicalOutput.setBounds(10, 200, 200, 22);
		lblGraphicalOutput.setText("Graphical output");
		lblGraphicalOutput.setFont(new Font(Display.getCurrent(), "Arial", 10, SWT.BOLD));

		Button btnMultipleAlignments = new Button(shell, SWT.RADIO);
		btnMultipleAlignments.setBounds(10, 240, 200, 22);
		btnMultipleAlignments.setText("All alignments");
		
		btnUniqueAlignments = new Button(shell, SWT.RADIO);
		btnUniqueAlignments.setBounds(220, 240, 200, 22);
		btnUniqueAlignments.setText("Unique alignments");
		btnUniqueAlignments.setSelection(true);

		btnProcess = new Button(shell, SWT.NONE);
		btnProcess.setBounds(220, 300, 130, 22);
		btnProcess.setText("Statistics");
		btnProcess.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				process();
			}
		});

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(410, 300, 130, 22);
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
		BasePairQualityStatisticsCalculator instance = new BasePairQualityStatisticsCalculator();
		SyncBasePairQualityStatistics job = new SyncBasePairQualityStatistics("Basepair Quality Statistics");
		job.setInstance(instance);
		
		if (txtInputFile.getText() == null || txtInputFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblInputFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtInputFile.setBackground(oc);
		} else {
			job.setAlignmentsFile(txtInputFile.getText());
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
		if (txtOutputPrefix.getText() == null || txtOutputPrefix.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblOutputPrefix.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutputPrefix.setBackground(oc);
		} else {
			job.setStatsOutputFile(txtOutputPrefix.getText()+".stats");
			job.setPlotFile(txtOutputPrefix.getText());
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
			FieldValidator.paintErrors(errors, shell, "Statistics");
			return;
		}
		job.setPlotUniqueAlignments(btnUniqueAlignments.getSelection());	
		
		String logFilename = txtOutputPrefix.getText()+".log";
		job.setLogName(logFilename);
		job.setNameProgressBar(new File(txtOutputPrefix.getText()).getName());
		
		try {
			job.schedule();
		} catch (Exception e) {
			MessageDialog.openError(shell, "Quality Statistics Calculator Error",e.getMessage());
			e.printStackTrace();
			return;
		}
		HistoryManager.saveInHistory(HistoryManager.KEY_REFERENCE_FILE, txtReferenceFile.getText());
		MessageDialog.openInformation(shell,"Calculate Quality Statistics is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
		shell.dispose();
	}
}
