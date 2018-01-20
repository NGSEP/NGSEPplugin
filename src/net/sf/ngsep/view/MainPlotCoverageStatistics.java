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

import java.io.IOException;
import java.util.ArrayList;

import net.sf.ngsep.control.SyncCoverageStatisticsCalculator;
import net.sf.ngsep.utilities.FieldValidator;
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
 * @author Juan Fernando de la Hoz
 * @author Jorge Duitama
 *
 */
public class MainPlotCoverageStatistics implements SingleFileInputWindow {
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
	private Label lblOutputFile;
	private Text txtOutputFile;
	private Button btnOutputFile;
	
	private Button btnUniqueAlignments;
	private Button btnCoverageStatistics;
	private Button btnCancel;
	

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

	/**
	 * Draws all the contents on the window, suggests a name for the output image
	 */
	private void createContents() {
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setSize(800, 200);
		shell.setText("Plot Coverage  Statistics");
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

		lblOutputFile = new Label(shell, SWT.NONE);
		lblOutputFile.setBounds(10, 60, 140, 22);
		lblOutputFile.setText("(*)Output File:");
		
		txtOutputFile = new Text(shell, SWT.BORDER);
		txtOutputFile.setBounds(160, 60, 580, 22);
		txtOutputFile.addMouseListener(mouse);
		txtOutputFile.setText(SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile));
		
		btnOutputFile = new Button(shell, SWT.NONE);
		btnOutputFile.setBounds(760, 60, 25, 22);
		btnOutputFile.setText("...");
		btnOutputFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, selectedFile, txtOutputFile);
			}
		});

		Button btnAllAlignments = new Button(shell, SWT.RADIO);
		btnAllAlignments.setBounds(10, 100, 200, 22);
		btnAllAlignments.setText("All alignments");
		btnAllAlignments.setSelection(true);
		
		btnUniqueAlignments = new Button(shell, SWT.RADIO);
		btnUniqueAlignments.setBounds(220, 100, 200, 22);
		btnUniqueAlignments.setText("Unique alignments");

		btnCoverageStatistics = new Button(shell, SWT.NONE);
		btnCoverageStatistics.setBounds(240, 150, 130, 22);
		btnCoverageStatistics.setText("Plot");
		btnCoverageStatistics.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();
			}
		});

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(410, 150, 130, 22);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
	}
	
	/**
	 * Main execution of the Class, it checks if the user introduced correctly the files,
	 * takes the information from the Coverage.stats file and stores it in ArrayLists,
	 * and finally creates and saves histograms  
	 */
	public void proceed() {
		// Check for empty fields
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		ArrayList<String> errors = new ArrayList<String>();
		if (txtInputFile.getText() == null || txtInputFile.getText().equals("")) {
			errors.add(FieldValidator.buildMessage(lblInputFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtInputFile.setBackground(oc);
		}
		if (txtOutputFile.getText() == null || txtOutputFile.getText().equals("")) {
			errors.add(FieldValidator.buildMessage(lblOutputFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutputFile.setBackground(oc);
		}
		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shell, "Plot Coverage Statistics");
			return;
		}
		
		// obtain the information for plotting
		try {
			SyncCoverageStatisticsCalculator.plotStatistics(txtInputFile.getText(),txtOutputFile.getText(),btnUniqueAlignments.getSelection());
		} catch (IOException e) {
			MessageDialog.openError(shell, "Plot Coverage Statistics Error", e.getMessage());
			e.printStackTrace();
			return;
		}
		shell.dispose();
	}

}
