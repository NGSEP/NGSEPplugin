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
 * @author Jorge Duitama
 *
 */
public class MainImputeGenotype implements SingleFileInputWindow {
	
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

	private Label lblVcfFile;
	private Text txtVcfFile;
	private Button btnVcfFile;

	private Label lblOutputPrefix;
	private Text txtOutputPrefix;
	private Button btnOutputPrefix;

	private Label lblClusters;
	private Text txtClusters;

	private Label lblAverageCMPerKbp;
	private Text txtAverageCMPerKbp;
	
	private Label lblWindowSize;
	private Text txtWindowSize;
	
	private Label lblOverlap;
	private Text txtOverlap;

	private Button btnFixedTransit;
	
	private Button btnInbredParents;
	
	private Button btnInbredSamples;

	private Table tblParentsIds;

	private Button btnStart;
	private Button btnCancel;

	/**
	 * Open the window.
	 * @throws IOException 
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void open() {
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
	protected boolean createContents() {
		MouseListenerNgsep mouse = new MouseListenerNgsep();
		
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setLocation(10, 10);
		shell.setSize(800, 600);
		shell.setText("Genotype imputation");

		lblVcfFile = new Label(shell, SWT.NONE);
		lblVcfFile.setBounds(10, 40, 150, 22);
		lblVcfFile.setText("(*)VCF File:");

		txtVcfFile = new Text(shell, SWT.BORDER);
		txtVcfFile.setBounds(180, 40, 550, 22);
		txtVcfFile.addMouseListener(mouse);
		txtVcfFile.setText(selectedFile);

		btnVcfFile = new Button(shell, SWT.NONE);
		btnVcfFile.setBounds(750, 40, 25, 22);
		btnVcfFile.setText("...");
		btnVcfFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtVcfFile);
			}
		});

		lblOutputPrefix = new Label(shell, SWT.NONE);
		lblOutputPrefix.setBounds(10, 80, 150, 22);
		lblOutputPrefix.setText("(*)Output Prefix:");

		txtOutputPrefix = new Text(shell, SWT.BORDER);
		txtOutputPrefix.setBounds(180, 80, 550, 22);
		txtOutputPrefix.addMouseListener(mouse);
		String outputFile = selectedFile.substring(0,selectedFile.lastIndexOf("."));
		txtOutputPrefix.setText(outputFile);

		btnOutputPrefix = new Button(shell, SWT.NONE);
		btnOutputPrefix.setBounds(750, 80, 25, 22);
		btnOutputPrefix.setText("...");
		btnOutputPrefix.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, selectedFile, txtOutputPrefix);
			}
		});

		lblClusters = new Label(shell, SWT.NONE);
		lblClusters.setBounds(10, 150, 260, 22);
		lblClusters.setText("Number of clusters:");

		txtClusters = new Text(shell, SWT.BORDER);
		txtClusters.setBounds(300, 150, 100, 22);
		txtClusters.addMouseListener(mouse);

		lblAverageCMPerKbp = new Label(shell, SWT.NONE);
		lblAverageCMPerKbp.setBounds(10, 200, 260, 22);
		lblAverageCMPerKbp.setText("Average centiMorgans per Kbp:");

		txtAverageCMPerKbp = new Text(shell, SWT.BORDER);
		txtAverageCMPerKbp.setBounds(300, 200, 100, 22);
		txtAverageCMPerKbp.addMouseListener(mouse);
		
		lblWindowSize = new Label(shell, SWT.NONE);
		lblWindowSize.setBounds(10, 250, 260, 22);
		lblWindowSize.setText("Window size:");

		txtWindowSize = new Text(shell, SWT.BORDER);
		txtWindowSize.setBounds(300, 250, 100, 22);
		txtWindowSize.addMouseListener(mouse);
		
		lblOverlap = new Label(shell, SWT.NONE);
		lblOverlap.setBounds(10, 300, 260, 22);
		lblOverlap.setText("Overlap:");

		txtOverlap = new Text(shell, SWT.BORDER);
		txtOverlap.setBounds(300, 300, 100, 22);
		txtOverlap.addMouseListener(mouse);

		btnFixedTransit = new Button(shell, SWT.CHECK);
		btnFixedTransit.setBounds(10, 350, 260, 22);
		btnFixedTransit.setText("Fixed Transitions");
		
		btnInbredParents = new Button(shell, SWT.CHECK);
		btnInbredParents.setBounds(10, 400, 260, 22);
		btnInbredParents.setText("Inbred parents");
		
		btnInbredSamples = new Button(shell, SWT.CHECK);
		btnInbredSamples.setBounds(10, 450, 260, 22);
		btnInbredSamples.setText("Inbred samples");

		List<String> sampleIds=null;
		try (VCFFileReader reader = new VCFFileReader(selectedFile)){ 
			sampleIds=reader.getSampleIds();
		} catch (IOException e1) {
			MessageDialog.openError(shell, "Error loading sample ids from VCF file",e1.getMessage());
			e1.printStackTrace();
			return false;
		}
		tblParentsIds =new Table(shell, SWT.MULTI | SWT.BORDER| SWT.V_SCROLL| SWT.CHECK);
		tblParentsIds.setBounds(420, 150, 300, 350);
		tblParentsIds.setHeaderVisible(true);
		TableColumn columSelect = new TableColumn(tblParentsIds, SWT.CENTER);
		columSelect.setText("Parents Id");
		for (int i = 0; i < sampleIds.size(); i++) {
			TableItem items=new TableItem(tblParentsIds,SWT.NONE);
			items.setText(0, sampleIds.get(i));
		}
		tblParentsIds.getColumn(0).setWidth(100);

		btnStart = new Button(shell, SWT.NONE);
		btnStart.setBounds(230, 520, 110, 25);
		btnStart.setText("Impute");
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();
			}
		});

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(370, 520, 110, 25);
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
		GenotypeImputer instance=new GenotypeImputer();
		SyncImputeGenotype job=new SyncImputeGenotype("Impute Genotype");
		job.setInstance(instance);

		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		ArrayList<String> listErrors = new ArrayList<String>();
		if (txtVcfFile.getText() == null || txtVcfFile.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblVcfFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtVcfFile.setBackground(oc);
		} else {
			job.setVcfFile(txtVcfFile.getText());
		}
		if (txtOutputPrefix.getText() == null|| txtOutputPrefix.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblOutputPrefix.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutputPrefix.setBackground(oc);
		} else {
			job.setOutputPrefix(txtOutputPrefix.getText());
		}
		if (txtClusters.getText() != null && txtClusters.getText().length()!=0) {
			if (!FieldValidator.isNumeric(txtClusters.getText(), new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblClusters.getText(), FieldValidator.ERROR_NUMERIC));
				txtClusters.setBackground(oc);
			} else {
				instance.setK(Integer.parseInt(txtClusters.getText()));
			}
		}
		if (txtAverageCMPerKbp.getText()!=null && txtAverageCMPerKbp.getText().length()!=0) {
			if (!FieldValidator.isNumeric(txtAverageCMPerKbp.getText(), new Double(0))) {
				listErrors.add(FieldValidator.buildMessage(lblAverageCMPerKbp.getText(), FieldValidator.ERROR_NUMERIC));
				txtAverageCMPerKbp.setBackground(oc);
			} else {
				instance.setAvgCMPerKbp(Double.parseDouble(txtAverageCMPerKbp.getText()));
			}
		}
		if (txtWindowSize.getText()!=null && txtWindowSize.getText().length()!=0) {
			if (!FieldValidator.isNumeric(txtWindowSize.getText(), new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblWindowSize.getText(), FieldValidator.ERROR_INTEGER));
				txtWindowSize.setBackground(oc);
			} else {
				instance.setWindowSize(Integer.parseInt(txtWindowSize.getText()));
			}
		}
		if (txtOverlap.getText()!=null && txtOverlap.getText().length()!=0) {
			if (!FieldValidator.isNumeric(txtOverlap.getText(), new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblOverlap.getText(), FieldValidator.ERROR_INTEGER));
				txtOverlap.setBackground(oc);
			} else {
				instance.setOverlap(Integer.parseInt(txtOverlap.getText()));
			}
		}
		if (listErrors.size() > 0) {
			FieldValidator.paintErrors(listErrors, shell, "Impute genotype");
			return;
		}

		instance.setSkipTransitionsTraining(btnFixedTransit.getSelection());
		instance.setInbredParents(btnInbredParents.getSelection());
		instance.setInbredSamples(btnInbredSamples.getSelection());
		
		List<String>parentsId=new ArrayList<String>();
		for (int i = 0; i < tblParentsIds.getItems().length; i++) {
			if(tblParentsIds.getItem(i).getChecked()){
				parentsId.add(tblParentsIds.getItem(i).getText(0));
			}
		}
		instance.setParentIds(parentsId);
		
		job.setNameProgressBar(new File(txtOutputPrefix.getText()).getName());
		String logFilename = txtOutputPrefix.getText()+"IG.log";
		job.setLogName(logFilename);
		try {
			job.schedule();	
			MessageDialog.openInformation(shell, "Population Impute Genotype",LoggingHelper.MESSAGE_PROGRESS_BAR);
			shell.dispose();
		} catch (Exception e) {
			MessageDialog.openError(shell, "Population Impute Genotype",e.getMessage());
			e.printStackTrace();
			return;
		}

	}
}
