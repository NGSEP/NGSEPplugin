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


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Label;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;

import net.sf.ngsep.control.SyncImputeGenotype;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.variants.imputation.GenotypeImputer;
import ngsep.vcf.VCFFileReader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Juan Fernando de la Hoz
 *
 */
public class MainImputeGenotype {

	//General attributes
	protected Shell shell;
	private Display display;

	//Input file 
	private String vcfFile;

	private Label lblVcfFile;
	private Text txtVcfFile;
	private Button btnVcfFile;

	private Label lblOutputFile;
	private Text txtOutputFile;
	private Button btnOutputFile;

/*	private Label lblFUTURE;
	private Text txtFUTURE;
*/
	private Label lblClusters;
	private Text txtClusters;

	private Label lblAverageCMPerKbp;
	private Text txtAverageCMPerKbp;

	private Button btnFixedTransit;

	private Table tblParentsIds;

	private Button btnStart;
	private Button btnCancel;

	/**
	 * Open the window.
	 * @throws IOException 
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void open() throws IOException {
		display = Display.getDefault();
		boolean created = createContents();
		if(created) {
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}
	}

	/**
	 * Create contents of the shell.
	 * @throws IOException 
	 */
	protected boolean createContents() throws IOException {
		MouseListenerNgsep mouse = new MouseListenerNgsep();
		
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setLocation(150, 200);
		shell.setSize(750, 450);
		shell.setText("Impute Genotype   ( Beta version )");

		lblVcfFile = new Label(shell, SWT.NONE);
		lblVcfFile.setBounds(10, 50, 130, 21);
		lblVcfFile.setText("(*)VCF File:");

		txtVcfFile = new Text(shell, SWT.BORDER);
		txtVcfFile.setBounds(150, 50, 530, 21);
		txtVcfFile.addMouseListener(mouse);

		btnVcfFile = new Button(shell, SWT.NONE);
		btnVcfFile.setBounds(700, 50, 25, 21);
		btnVcfFile.setText("...");
		btnVcfFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, vcfFile, txtVcfFile);
			}
		});

		lblOutputFile = new Label(shell, SWT.NONE);
		lblOutputFile.setBounds(10, 100, 130, 21);
		lblOutputFile.setText("(*)Output File:");

		txtOutputFile = new Text(shell, SWT.BORDER);
		txtOutputFile.setBounds(150, 100, 530, 21);
		txtOutputFile.addMouseListener(mouse);

		btnOutputFile = new Button(shell, SWT.NONE);
		btnOutputFile.setBounds(700, 100, 25, 21);
		btnOutputFile.setText("...");
		btnOutputFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, vcfFile,txtOutputFile);
			}
		});

		List<String> sampleIds=null;
		if (vcfFile != null && vcfFile.length()>0) {
			txtVcfFile.setText(vcfFile);
			String outputFile = vcfFile.substring(0,vcfFile.lastIndexOf("."));
			txtOutputFile.setText(outputFile);
			VCFFileReader reader = new VCFFileReader(vcfFile);
			sampleIds=reader.getSampleIds();
			reader.close();
		}

/*		lblFUTURE = new Label(shell, SWT.NONE);
		lblFUTURE.setBounds(10, 170, 260, 21);
		lblFUTURE.setText("(*)FUTURE OPTIONS:");

		txtFUTURE = new Text(shell, SWT.BORDER);
		txtFUTURE.setBounds(280, 170, 100, 21);
		txtFUTURE.addMouseListener(mouse); 					*/

		lblClusters = new Label(shell, SWT.NONE);
		lblClusters.setBounds(10, 220, 260, 21);
		lblClusters.setText("Number of clusters:");

		txtClusters = new Text(shell, SWT.BORDER);
		txtClusters.setBounds(280, 220, 100, 21);
		txtClusters.addMouseListener(mouse);

		lblAverageCMPerKbp = new Label(shell, SWT.NONE);
		lblAverageCMPerKbp.setBounds(10, 270, 260, 21);
		lblAverageCMPerKbp.setText("Average centiMorgans per Kbp:");

		txtAverageCMPerKbp = new Text(shell, SWT.BORDER);
		txtAverageCMPerKbp.setBounds(280, 270, 100, 21);
		txtAverageCMPerKbp.addMouseListener(mouse);

		btnFixedTransit = new Button(shell, SWT.CHECK);
		btnFixedTransit.setBounds(10, 320, 260, 21);
		btnFixedTransit.setText("Fixed Transitions");

		tblParentsIds =new Table(shell, SWT.MULTI | SWT.BORDER| SWT.V_SCROLL| SWT.CHECK);
		tblParentsIds.setBounds(450, 150, 200, 200);
		tblParentsIds.setHeaderVisible(true);
		TableColumn columSelect = new TableColumn(tblParentsIds, SWT.CENTER);
		columSelect.setText("Parents Id");
		for (int i = 0; i < sampleIds.size(); i++) {
			TableItem items=new TableItem(tblParentsIds,SWT.NONE);
			items.setText(0, sampleIds.get(i));
		}
		tblParentsIds.getColumn(0).setWidth(100);

		btnStart = new Button(shell, SWT.NONE);
		btnStart.setBounds(230, 380, 110, 25);
		btnStart.setText("Impute");
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();
			}
		});

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(370, 380, 110, 25);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		
		return true;
	}

	public void proceed(){
		GenotypeImputer popGenotypeImpute=new GenotypeImputer();
		SyncImputeGenotype imputeGenotype=new SyncImputeGenotype("Impute Genotype");

		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		ArrayList<String> listErrors = new ArrayList<String>();
		if (txtVcfFile.getText() == null || txtVcfFile.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblVcfFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtVcfFile.setBackground(oc);
		} else {
			imputeGenotype.setVcfFile(txtVcfFile.getText());
		}
		if (txtOutputFile.getText() == null|| txtOutputFile.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblOutputFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutputFile.setBackground(oc);
		}
/*		if (txtFUTURE.getText() == null || txtFUTURE.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblFUTURE.getText(), FieldValidator.ERROR_MANDATORY));
			txtFUTURE.setBackground(oc);
		} else {
			if (!FieldValidator.isNumeric(txtFUTURE.getText(), new Double(0))) {
				listErrors.add(FieldValidator.buildMessage(lblFUTURE.getText(), FieldValidator.ERROR_NUMERIC));
				txtFUTURE.setBackground(oc);
			} else {
				popGenotypeImpute.setPctFUTURE(Double.parseDouble(txtFUTURE.getText()));
			}
		}			*/
		if (txtClusters.getText() != null && txtClusters.getText().length()!=0) {
			if (!FieldValidator.isNumeric(txtClusters.getText(), new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblClusters.getText(), FieldValidator.ERROR_NUMERIC));
				txtClusters.setBackground(oc);
			} else {
				popGenotypeImpute.setK(Integer.parseInt(txtClusters.getText()));
			}
		}
		if (txtAverageCMPerKbp.getText()!=null && txtAverageCMPerKbp.getText().length()!=0) {
			if (!FieldValidator.isNumeric(txtAverageCMPerKbp.getText(), new Double(0))) {
				listErrors.add(FieldValidator.buildMessage(lblAverageCMPerKbp.getText(), FieldValidator.ERROR_NUMERIC));
				txtAverageCMPerKbp.setBackground(oc);
			} else {
				popGenotypeImpute.setAvgCMPerKbp(Double.parseDouble(txtAverageCMPerKbp.getText()));
			}
		}
		if (listErrors.size() > 0) {
			FieldValidator.paintErrors(listErrors, shell, "Impute genotype");
			return;
		}

		popGenotypeImpute.setFixedTransitions(btnFixedTransit.getSelection());
		
		List<String>parentsId=new ArrayList<String>();
		for (int i = 0; i < tblParentsIds.getItems().length; i++) {
			if(tblParentsIds.getItem(i).getChecked()){
				parentsId.add(tblParentsIds.getItem(i).getText(0));
			}
		}
		popGenotypeImpute.setParentIds(parentsId);
		
		String outputFile = txtOutputFile.getText();
		imputeGenotype.setOutputFile(outputFile);
		imputeGenotype.setNameProgressBar(new File(outputFile).getName());
		String logFilename = LoggingHelper.getLoggerFilename(outputFile,"IG");
		imputeGenotype.setLogName(logFilename);
		imputeGenotype.setPopulationGenotypeImpute(popGenotypeImpute);
		try {
			FileHandler logFile = new FileHandler(logFilename, false);
			imputeGenotype.setLogFile(logFile);
			imputeGenotype.schedule();	
			MessageDialog.openInformation(shell, "Population Impute Genotype",LoggingHelper.MESSAGE_PROGRESS_BAR);
			shell.dispose();
		} catch (Exception e) {
			MessageDialog.openError(shell, "Population Impute Genotype",e.getMessage());
			e.printStackTrace();
			return;
		}

	}

	public String getVcfFile() {
		return vcfFile;
	}

	public void setVcfFile(String vcfFile) {
		this.vcfFile = vcfFile;
	}
}
