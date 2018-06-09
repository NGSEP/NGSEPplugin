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

import net.sf.ngsep.control.SyncSingleIndividualSimulator;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.simulation.SingleIndividualSimulator;

/**
 * @author Jorge Duitama
 */
public class MainSingleIndividualSimulator implements SingleFileInputWindow {
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

	//Action buttons
	private Button btnSimulate;
	private Button btnCancel;
	
	//Main arguments
	private Label lblInputFile;
	private Text txtInputFile;
	private Button btnInputFile;
	private Label lblOutputPrefix;
	private Text txtOutputPrefix;
	private Button btnOutputPrefix;
	private Label lblSTRsFile;
	private Text txtSTRsFile;
	private Button btnSTRsFile;
	private Label lblSnvRate;
	private Text txtSnvRate;
	private Label lblIndelRate;
	private Text txtIndelRate;
	private Label lblMutatedSTRFraction;
	private Text txtMutatedSTRFraction;
	
	private Label lblSampleId;
	private Text txtSampleId;
	private Label lblPloidy;
	private Text txtPloidy;
	private Label lblStrUnitIndex;
	private Text txtStrUnitIndex;

	
	@Override
	public void open() {
		display = Display.getDefault();
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("Compare VCFs");
		shell.setLocation(150, 200);
		shell.setSize(800, 350);
		createContents();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

	}
	
	/**
	 * Create contents of  the window.
	 */
	protected void createContents() {
		MouseListenerNgsep mouse = new MouseListenerNgsep();
		
		lblInputFile = new Label(shell, SWT.NONE);
		lblInputFile.setBounds(10, 20, 190, 22);
		lblInputFile.setText("(*FASTA) Reference Genome:");
		
		txtInputFile=new Text(shell, SWT.BORDER);
		txtInputFile.setBounds(220, 20, 530, 22);
		txtInputFile.addMouseListener(mouse);
		if (selectedFile != null && selectedFile.length()>0) {
			txtInputFile.setText(selectedFile);
		}
		
		btnInputFile=new Button(shell, SWT.NONE);
		btnInputFile.setBounds(760, 20, 25, 22);
		btnInputFile.setText("...");
		btnInputFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,  SWT.OPEN, selectedFile, txtInputFile);
			}			
		});
		
		lblOutputPrefix = new Label(shell, SWT.NONE);
		lblOutputPrefix.setBounds(10, 60, 190, 22);
		lblOutputPrefix.setText("(*) Output Prefix:");
		
		txtOutputPrefix = new Text(shell, SWT.BORDER);
		txtOutputPrefix.setBounds(220, 60, 530, 22);
		txtOutputPrefix.addMouseListener(mouse);
		String suggestedOutPrefix = SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile)+"_sim";
		txtOutputPrefix.setText(suggestedOutPrefix);
		
		btnOutputPrefix = new Button(shell, SWT.NONE);
		btnOutputPrefix.setBounds(760, 60, 25, 22);
		btnOutputPrefix.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,  SWT.SAVE, selectedFile, txtOutputPrefix);
			}		
		});
		btnOutputPrefix.setText("...");
		
		lblSTRsFile = new Label(shell, SWT.NONE);
		lblSTRsFile.setBounds(10, 100, 190, 22);
		lblSTRsFile.setText("STRs file:");
		
		txtSTRsFile = new Text(shell, SWT.BORDER);
		txtSTRsFile.setBounds(220, 100, 530, 22);
		txtSTRsFile.addMouseListener(mouse);
		
		btnSTRsFile = new Button(shell, SWT.NONE);
		btnSTRsFile.setBounds(760, 100, 25, 22);
		btnSTRsFile.setText("...");
		btnSTRsFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,  SWT.OPEN, selectedFile,txtSTRsFile);
			}			
		});		
		
		lblSnvRate = new Label(shell, SWT.NONE);
		lblSnvRate.setText("SNV rate across the genome");
		lblSnvRate.setBounds(10, 140, 250, 22);
		
		txtSnvRate = new Text(shell, SWT.BORDER);
		txtSnvRate.setBounds(280, 140, 100, 22);
		txtSnvRate.setText(""+SingleIndividualSimulator.DEF_SNV_RATE);
		txtSnvRate.addMouseListener(mouse);
		
		lblIndelRate = new Label(shell, SWT.NONE);
		lblIndelRate.setText("Indel rate across the genome");
		lblIndelRate.setBounds(10, 180, 250, 22);
		
		txtIndelRate = new Text(shell, SWT.BORDER);
		txtIndelRate.setBounds(280, 180, 100, 22);
		txtIndelRate.setText(""+SingleIndividualSimulator.DEF_INDEL_RATE);
		txtIndelRate.addMouseListener(mouse);
		
		lblMutatedSTRFraction = new Label(shell, SWT.NONE);
		lblMutatedSTRFraction.setText("Fraction of STRs mutated");
		lblMutatedSTRFraction.setBounds(10, 220, 250, 22);
		
		txtMutatedSTRFraction = new Text(shell, SWT.BORDER);
		txtMutatedSTRFraction.setBounds(280, 220, 100, 22);
		txtMutatedSTRFraction.setText(""+SingleIndividualSimulator.DEF_MUTATED_STR_FRACTION);
		txtMutatedSTRFraction.addMouseListener(mouse);
		
		lblSampleId = new Label(shell, SWT.NONE);
		lblSampleId.setText("Sample id");
		lblSampleId.setBounds(410, 140, 200, 22);
		
		txtSampleId = new Text(shell, SWT.BORDER);
		txtSampleId.setBounds(630, 140, 150, 22);
		txtSampleId.setText(""+SingleIndividualSimulator.DEF_SAMPLE_ID);
		txtSampleId.addMouseListener(mouse);
		
		lblPloidy = new Label(shell, SWT.NONE);
		lblPloidy.setText("Ploidy");
		lblPloidy.setBounds(410, 180, 200, 22);
		
		txtPloidy = new Text(shell, SWT.BORDER);
		txtPloidy.setBounds(630, 180, 150, 22);
		txtPloidy.setText(""+SingleIndividualSimulator.DEF_PLOIDY);
		txtPloidy.addMouseListener(mouse);
		
		lblStrUnitIndex = new Label(shell, SWT.NONE);
		lblStrUnitIndex.setText("Column index STR unit");
		lblStrUnitIndex.setBounds(410, 220, 200, 22);
		
		txtStrUnitIndex = new Text(shell, SWT.BORDER);
		txtStrUnitIndex.setBounds(630, 220, 150, 22);
		txtStrUnitIndex.setText(""+SingleIndividualSimulator.DEF_STR_UNIT_INDEX);
		txtStrUnitIndex.addMouseListener(mouse);
		
		btnSimulate = new Button(shell, SWT.NONE);
		btnSimulate.setBounds(240, 280, 130, 22);
		btnSimulate.setText("Simulate");
		btnSimulate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();
			}
		});
		
		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setText("Cancel");
		btnCancel.setBounds(410, 280, 130, 22);
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
		SingleIndividualSimulator instance = new SingleIndividualSimulator();
		SyncSingleIndividualSimulator job = new SyncSingleIndividualSimulator("Single Individual Simulator");
		job.setInstance(instance);
		
		ArrayList<String> errors = new ArrayList<String>();
		if (txtInputFile.getText() == null || txtInputFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblInputFile.getText(),FieldValidator.ERROR_MANDATORY));
			txtInputFile.setBackground(oc);
		} else{
			job.setInputFile(txtInputFile.getText());
		}
		if (txtOutputPrefix.getText() == null || txtOutputPrefix.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblOutputPrefix.getText(),FieldValidator.ERROR_MANDATORY));
			txtOutputPrefix.setBackground(oc);
		} else { 
			job.setOutputPrefix(txtOutputPrefix.getText());
		}
		
		if (txtSTRsFile.getText() != null && txtSTRsFile.getText().length()>0) {
			instance.setStrsFile(txtSTRsFile.getText());
		}
		if (txtSnvRate.getText() != null && txtSnvRate.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtSnvRate.getText(),new Double(0))) {
				errors.add(FieldValidator.buildMessage(lblSnvRate.getText(), FieldValidator.ERROR_NUMERIC));
				txtSnvRate.setBackground(oc);
			} else {
				instance.setSnvRate(Double.parseDouble(txtSnvRate.getText()));
			}
		}
		if (txtIndelRate.getText() != null && txtIndelRate.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtIndelRate.getText(),new Double(0))) {
				errors.add(FieldValidator.buildMessage(lblIndelRate.getText(), FieldValidator.ERROR_NUMERIC));
				txtIndelRate.setBackground(oc);
			} else {
				instance.setIndelRate(Double.parseDouble(txtIndelRate.getText()));
			}
		}
		if (txtMutatedSTRFraction.getText() != null && txtMutatedSTRFraction.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMutatedSTRFraction.getText(),new Double(0))) {
				errors.add(FieldValidator.buildMessage(lblMutatedSTRFraction.getText(), FieldValidator.ERROR_NUMERIC));
				txtMutatedSTRFraction.setBackground(oc);
			} else {
				instance.setMutatedSTRFraction(Double.parseDouble(txtMutatedSTRFraction.getText()));
			}
		}
		
		if (txtSampleId.getText() != null && txtSampleId.getText().length()>0) {
			instance.setSampleId(txtSampleId.getText());
		}
		if (txtPloidy.getText() != null && txtPloidy.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtPloidy.getText(),new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblPloidy.getText(), FieldValidator.ERROR_INTEGER));
				txtPloidy.setBackground(oc);
			} else {
				instance.setPloidy(Byte.parseByte(txtPloidy.getText()));
			}
		}
		if (txtStrUnitIndex.getText() != null && txtStrUnitIndex.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtStrUnitIndex.getText(),new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblStrUnitIndex.getText(), FieldValidator.ERROR_INTEGER));
				txtStrUnitIndex.setBackground(oc);
			} else {
				instance.setStrUnitIndex(Integer.parseInt(txtStrUnitIndex.getText()));
			}
		}
		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shell,"Single Individual Simulator");
			return;
		}
		
		String logFilename = txtOutputPrefix.getText()+".log";
		job.setLogName(logFilename);
		job.setNameProgressBar(new File(txtOutputPrefix.getText()).getName());
		try {
			job.schedule();
			MessageDialog.openInformation(shell,"Single Individual Simulator is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
			shell.dispose();

		} catch (Exception e) {
			MessageDialog.openError(shell,"Single Individual Simulator Error", e.getMessage());
			e.printStackTrace();
			return;
		}
	}

}
