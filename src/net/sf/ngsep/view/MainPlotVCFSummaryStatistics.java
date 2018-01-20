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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.sf.ngsep.control.PlotVCFsummStatistics;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;

/**
 * @author Juan Fernando De la Hoz
 */
public class MainPlotVCFSummaryStatistics implements SingleFileInputWindow {
	protected Shell shell;
	private Display display;
	
	//File selected initially by the user
	private String selectedFile;
	public String getSelectedFile() {
		return selectedFile;
	}
	public void setSelectedFile(String selectedFile) {
		this.selectedFile = selectedFile;
	}
	
	private static int SNP_NUM = 1;
	private static int INDEL_NUM = 2;
	private static int OTHER_NUM = 3;
	private static String SNP_TXT = "SNPs";
	private static String INDEL_TXT = "Indels";
	private static String OTHER_TXT = "OtherVariants";
	
	private Label lblStatsFile;
	private Text txtStatsFile;
	private Button btnStatsFile;
	private Label lblPlotPrfx;
	private Text txtPlotPrfx;
	private Button btnPlotPrfx;
	private Label lblInstruction;
	
	private Button btnGenotypeCallStats;
	private Button btnSNPsPerSampleStats;
	private Button btnIndelsPerSampleStats;
	private Button btnOtherPerSampleStats;
	private Button btnAlleleFreqDistribution;
	
	private Button btnGenotypedSNPs;
	private Button btnGenotypedIndels;
	private Button btnGenotypedOther;
	
	private Button btnPlot;
	private Button btnCancel;
	
	public void open() {
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while(!shell.isDisposed()){
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public void createContents(){
		MouseListenerNgsep mouse = new MouseListenerNgsep();
		Font font = new Font(Display.getCurrent(), "Arial", 10, SWT.BOLD);
		
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setSize(800, 500);
		shell.setText("Plot VCF Summary Statistics");
		shell.setLocation(150, 200);
		
		lblStatsFile = new Label(shell, SWT.NONE);
		lblStatsFile.setBounds(10, 20, 200, 23);
		lblStatsFile.setText("(*) VCF Summary Statistics File:");
		
		txtStatsFile = new Text(shell, SWT.BORDER);
		txtStatsFile.setBounds(220, 20, 510, 23);
		txtStatsFile.setText(selectedFile);
		txtStatsFile.addMouseListener(mouse);
		
		btnStatsFile = new Button(shell, SWT.NONE);
		btnStatsFile.setBounds(750, 20, 23, 23);
		btnStatsFile.setText("...");
		btnStatsFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtStatsFile);
			}
		});
		
		lblPlotPrfx = new Label(shell, SWT.NONE);
		lblPlotPrfx.setBounds(10, 60, 200, 23);
		lblPlotPrfx.setText("(*) Plot Name Prefix:");
		
		txtPlotPrfx = new Text(shell, SWT.BORDER);
		txtPlotPrfx.setBounds(220, 60, 510, 23);
		txtPlotPrfx.setText(selectedFile.substring(0, selectedFile.lastIndexOf(".")));
		txtPlotPrfx.addMouseListener(mouse);
		
		btnPlotPrfx = new Button(shell, SWT.NONE);
		btnPlotPrfx.setBounds(750, 60, 23, 23);
		btnPlotPrfx.setText("...");
		btnPlotPrfx.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtStatsFile);
			}
		});
		
		lblInstruction = new Label(shell, SWT.NONE);
		lblInstruction.setBounds(50, 105, 400, 25);
		lblInstruction.setFont(font);
		lblInstruction.setText("Select the options for plotting: ");
		
		btnGenotypeCallStats = new Button(shell, SWT.CHECK);
		btnGenotypeCallStats.setBounds(10, 140, 260, 23);
		btnGenotypeCallStats.setText("Variants Accumulation:");
		btnGenotypeCallStats.addMouseListener(MouseClickOnGenotCalls);
		
		btnGenotypedSNPs = new Button(shell, SWT.CHECK);
		btnGenotypedSNPs.setBounds(300, 140, 150, 23);
		btnGenotypedSNPs.setText("Biallelic SNPs");
		btnGenotypedSNPs.setSelection(true);
		btnGenotypedSNPs.setVisible(false);
		
		btnGenotypedIndels = new Button(shell, SWT.CHECK);
		btnGenotypedIndels.setBounds(460, 140, 150, 23);
		btnGenotypedIndels.setText("Biallelic Indels");
		btnGenotypedIndels.setVisible(false);
		
		btnGenotypedOther = new Button(shell, SWT.CHECK);
		btnGenotypedOther.setBounds(630, 140, 150, 23);
		btnGenotypedOther.setText("Other");
		btnGenotypedOther.setVisible(false);
		
		btnSNPsPerSampleStats = new Button(shell, SWT.CHECK);
		btnSNPsPerSampleStats.setBounds(10, 200, 250, 23);
		btnSNPsPerSampleStats.setText("SNP Calls per Sample");

		btnIndelsPerSampleStats = new Button(shell, SWT.CHECK);
		btnIndelsPerSampleStats.setBounds(270, 200, 250, 23);
		btnIndelsPerSampleStats.setText("Indel Calls per Sample");
		btnIndelsPerSampleStats.setVisible(false);										// temporal

		btnOtherPerSampleStats = new Button(shell, SWT.CHECK);
		btnOtherPerSampleStats.setBounds(530, 200, 250, 23);
		btnOtherPerSampleStats.setText("Other Variants per Sample");
		btnOtherPerSampleStats.setVisible(false);										// temporal
		
		btnAlleleFreqDistribution = new Button(shell, SWT.CHECK);
		btnAlleleFreqDistribution.setBounds(10, 260, 260, 23);
		btnAlleleFreqDistribution.setText("Allele Frequency Distribution");
		
		btnPlot = new Button(shell, SWT.NONE);
		btnPlot.setBounds(300, 420, 80, 25);
		btnPlot.setText("Plot");
		btnPlot.addSelectionListener( new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				proceed();
			}
		});
		
		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(420, 420, 80, 25);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				shell.close();
			}
		});
	}

	public void proceed() {
		// Check for empty fields
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		ArrayList<String> errors = new ArrayList<String>();
		String inputFile = null;
		String outputFile = null;
		if (txtStatsFile.getText() == null || txtStatsFile.getText().equals("")) {
			errors.add(FieldValidator.buildMessage(lblStatsFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtStatsFile.setBackground(oc);
		} else {
			inputFile = txtStatsFile.getText();
		}
		if (txtPlotPrfx.getText() == null || txtPlotPrfx.getText().equals("")) {
			errors.add(FieldValidator.buildMessage(lblPlotPrfx.getText(), FieldValidator.ERROR_MANDATORY));
			txtPlotPrfx.setBackground(oc);
		} else {
			outputFile = txtPlotPrfx.getText();
		}
		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shell,"Missing Fields in Plot VCF Statistics");
			return;
		}
		
		// Plot
		try{
			PlotVCFsummStatistics plotter = new PlotVCFsummStatistics(inputFile, outputFile);
			
			if (btnGenotypeCallStats.getSelection()){
				if (btnGenotypedSNPs.getSelection()){
					plotter.plotGenotypeCallStats(SNP_NUM, SNP_TXT);
				} if (btnGenotypedIndels.getSelection()){
					plotter.plotGenotypeCallStats(INDEL_NUM, INDEL_TXT);
				} if (btnGenotypedOther.getSelection()){
					plotter.plotGenotypeCallStats(OTHER_NUM, OTHER_TXT);
				}
			}
			
			if (btnSNPsPerSampleStats.getSelection()){
				plotter.plotSNPsPerSampleStats();
			} 
			
			if (btnIndelsPerSampleStats.getSelection()){
				plotter.plotIndelsPerSampleStats();
			} 
			
			if (btnOtherPerSampleStats.getSelection()){
				plotter.plotOtherVarPerSampleStats();
			}
			
			if (btnAlleleFreqDistribution.getSelection()){
				plotter.plotAlleleFreqDistribution();
			}
			
			shell.dispose();
		} catch (Exception e) {
			MessageDialog.openError(shell, "VCF Summary Statistics Plotting Error",e.getMessage());
			e.printStackTrace();
			return;
		}
	}
	
	private final MouseListener MouseClickOnGenotCalls = new MouseListener() {
		@Override
		public void mouseDoubleClick(MouseEvent e) {
		}
		@Override
		public void mouseDown(MouseEvent e) {
		}
		@Override
		public void mouseUp(MouseEvent e) {
			if (btnGenotypeCallStats.getSelection()) {
				btnGenotypedSNPs.setVisible(true);
				btnGenotypedIndels.setVisible(true);
				btnGenotypedOther.setVisible(true);
			} else {
				btnGenotypedSNPs.setVisible(false);
				btnGenotypedIndels.setVisible(false);
				btnGenotypedOther.setVisible(false);
			}
		}
	};
	
}
