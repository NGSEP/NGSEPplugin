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
import java.util.Map;
import java.util.TreeMap;

import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.MouseListenerNgsep;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;

/**
 * @author Daniel Cruz
 *
 */
public class TabVDSNVArgs extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	
	
	private Label lblQuerySeq;
	private Text txtQuerySeq;
		
	
	private Label lblHete;
	private Text txtHete;
	
	private Label lblMinQuality;
	private Text txtMinQuality;
	
	private Label lblWizMinGQ;
	private Combo cmbWizMinGQ;
		
	
	private Label lblMaxBaseQS;
	private Text txtMaxBaseQS;
		
	private Label lblAltCov;
	private Label lblAltMax;
	private Label lblAltMin;
	private Text txtAltMax;
	private Text txtAltMin;
		
	private Label lblMaximunAlignmentPer;
	private Text txtMaximunAlignmentStartPosition;
	
	private Button btnPrintSamplePloidy;
	
	private Label lblPloidy;
	private Text txtPloidy;
	
	private Button btnkeepLowerCaseRefChk;
	private Button btnIncludeAllPrimaryAlignments;
	private Button btnIncludeSecondaryAlignments;
	private Button btnGenotypeAllCovered;
	private Button btnEmbeddedSNVs;
	
	private Label lblHapAvgCov;
	private Text txtHapAvgCov;
	
	private MouseListenerNgsep mouse = new MouseListenerNgsep();
	private ArrayList<String> errorsOne = new ArrayList<String>();
	
	private final static int DEF_MIN_QUAL = 40;
	private final static int DEF_MAX_BASE_QS = 30;
	private final static int DEF_PLOIDY = 2;
	private final static int DEF_ALIGN_PER_STARTPOS = 2;
	private final static double DEF_HETEROZYGOSITY = 0.001;
	
	private char source;
	
	
	
	
	public TabVDSNVArgs(Composite parent, int style, char source) {
		super(parent, style);
		this.source = source;
	}
	
	public void paint() {
		
		
		lblQuerySeq = new Label(this, SWT.NONE);
		lblQuerySeq.setBounds(10, 20, 139, 20);
		lblQuerySeq.setText("Genomic Location:");

		txtQuerySeq = new Text(this, SWT.BORDER);
		txtQuerySeq.setToolTipText("Position coordinate ranges.\nExample: 'chr21:33,031,597-33,041,570' ");
		txtQuerySeq.setBounds(155, 20, 180, 21);
		txtQuerySeq.addMouseListener(mouse);
		
		lblHete = new Label(this, SWT.NONE);
		lblHete.setBounds(10, 60, 139, 20);
		lblHete.setText("Heterozygosity Rate:");

		txtHete = new Text(this, SWT.BORDER);
		txtHete.setBounds(155, 60, 180, 21);
		txtHete.setText(String.valueOf(DEF_HETEROZYGOSITY));
		txtHete.addMouseListener(mouse);
		
		lblMinQuality = new Label(this, SWT.NONE);
		lblMinQuality.setText("Minimun Genotype Quality Score:");
		lblMinQuality.setBounds(10, 113, 223, 20);

		txtMinQuality = new Text(this, SWT.BORDER);
		txtMinQuality.setText(String.valueOf(DEF_MIN_QUAL));
		txtMinQuality.setBounds(239, 113, 57, 21);
		txtMinQuality.addMouseListener(mouse);
		
		lblMaxBaseQS = new Label(this, SWT.NONE);
		lblMaxBaseQS.setText("Maximun Base Quality Score:");
		lblMaxBaseQS.setBounds(10, 144, 223, 20);

		txtMaxBaseQS = new Text(this, SWT.BORDER);
		txtMaxBaseQS.setText(String.valueOf(DEF_MAX_BASE_QS));
		txtMaxBaseQS.setBounds(239, 144, 57, 21);
		txtMaxBaseQS.addMouseListener(mouse);
		
		lblAltCov = new Label(this, SWT.NONE);
		lblAltCov.setBounds(10, 180, 193, 20);
		lblAltCov.setText("Alternative  Allele Coverage:");
		
		lblAltMin = new Label(this, SWT.NONE);
		lblAltMin.setText("Min:");
		lblAltMin.setBounds(218, 180, 35, 20);

		lblAltMax = new Label(this, SWT.NONE);
		lblAltMax.setBounds(300, 180, 35, 20);
		lblAltMax.setText("Max:");

		txtAltMin = new Text(this, SWT.BORDER);
		txtAltMin.setBounds(259, 180, 35, 21);
		txtAltMin.addMouseListener(mouse);
		
		txtAltMax = new Text(this, SWT.BORDER);
		txtAltMax.setBounds(344, 180, 35, 21);
		txtAltMax.addMouseListener(mouse);
		
		lblMaximunAlignmentPer = new Label(this, SWT.NONE);
		lblMaximunAlignmentPer.setText("Maximun Alignment Per Start Position:");
		lblMaximunAlignmentPer.setBounds(10, 220, 284, 20);

		txtMaximunAlignmentStartPosition = new Text(this, SWT.BORDER);
		txtMaximunAlignmentStartPosition.setText(String.valueOf(DEF_ALIGN_PER_STARTPOS));
		txtMaximunAlignmentStartPosition.setBounds(300, 220, 70, 21);
		txtMaximunAlignmentStartPosition.addMouseListener(mouse);
		
		btnPrintSamplePloidy = new Button(this, SWT.CHECK);
		btnPrintSamplePloidy.setBounds(500, 20, 325, 23);
		btnPrintSamplePloidy.setText("Print Sample Ploidy");
		
		lblPloidy = new Label(this, SWT.NONE);
		lblPloidy.setText("Ploidy:");
		lblPloidy.setBounds(500, 60, 50, 20);

		txtPloidy = new Text(this, SWT.BORDER);
		txtPloidy.setBounds(600, 60, 57, 21);
		txtPloidy.setText(String.valueOf(DEF_PLOIDY));
		txtPloidy.addFocusListener(new FocusAdapter() {
			// This method captures the field entered the ploidy and can fit
			// your input if one is painted on the box of heterozygosity Rate:
			// 0.000001 and if two heterozygosity Rate: 0.001.
			@Override
			public void focusLost(FocusEvent e) {
				double het = DEF_HETEROZYGOSITY;
				if ("1".equals(txtPloidy.getText())) {
					het = 0.000001;
				}
				txtHete.setText(String.valueOf(het));
				txtHete.update();
			}
		});
		txtPloidy.addMouseListener(mouse);
		
		btnkeepLowerCaseRefChk = new Button(this, SWT.CHECK);
		btnkeepLowerCaseRefChk.setBounds(500, 100, 325, 23);
		btnkeepLowerCaseRefChk.setText("Ignore Lower Case Reference");
		
		btnIncludeAllPrimaryAlignments = new Button(this, SWT.CHECK);
		btnIncludeAllPrimaryAlignments.setText("Include Non Unique Primary Alignments");
		btnIncludeAllPrimaryAlignments.setBounds(500, 140, 325, 23);
		
		btnIncludeSecondaryAlignments = new Button(this, SWT.CHECK);
		btnIncludeSecondaryAlignments.setText("Include Secondary Alignments");
		btnIncludeSecondaryAlignments.setBounds(500, 180, 325, 23);
				
		btnGenotypeAllCovered = new Button(this, SWT.CHECK);
		btnGenotypeAllCovered.setText("Genotype All Covered Sites");
		btnGenotypeAllCovered.setBounds(500, 220, 325, 23);

		btnEmbeddedSNVs = new Button(this, SWT.CHECK);
		btnEmbeddedSNVs.setText("Call SNVs within STRs");
		btnEmbeddedSNVs.setBounds(500, 260, 350, 20);
		
		lblHapAvgCov = new Label(this, SWT.NONE);
		lblHapAvgCov.setEnabled(false);
		lblHapAvgCov.setVisible(false);
		lblHapAvgCov.setText("Haploid Average Coverage:");
		lblHapAvgCov.setBounds(500, 240, 160, 20);
		
		txtHapAvgCov = new Text(this, SWT.BORDER);
		txtHapAvgCov.setEnabled(false);
		txtHapAvgCov.setVisible(false);
		txtHapAvgCov.setBounds(680, 240, 57, 21);
		
		
		
		txtHapAvgCov.addMouseListener(mouse);
		
		if(source == 'W'){
		
			lblWizMinGQ = new Label(this, SWT.NONE);
			lblWizMinGQ.setText("Use minimun Genotype Quality Score in:");
			lblWizMinGQ.setBounds(10, 252, 249, 20);
			
			
			
			cmbWizMinGQ = new Combo(this, SWT.READ_ONLY);
			cmbWizMinGQ.setItems(new String[] {"Only for discovery", "Discovery and genotyping"});
			cmbWizMinGQ.setBounds(272, 252, 168, 23);
			cmbWizMinGQ.select(0);
			
			
		}
		
		
	}
	
	public Map<String,Object> getParams(){
		
		// Common Fields
		// This map stores parameters set by the user that are common for all samples
		errorsOne.clear();
		Map<String,Object> commonUserParameters = new TreeMap<String, Object>();
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		
		
		// SNPs fields
		// This validation is to verify that it entered the box Genomic
		// Location: either in the format provided by example: chr01 :58-98,
		// also validates that the entered after chr01 eg: non-numeric
		// characters and then validates I entered after such chr01: 58 is
		// numeric, otherwise get a message with the appropriate error.
		if (txtQuerySeq.getText() != null && !txtQuerySeq.getText().equals("")) {
			String str = txtQuerySeq.getText();
			int indexStart = str.lastIndexOf(":");
			int indexIntermediate = str.lastIndexOf("-");
			if (indexStart > 1 && indexIntermediate > 1 && indexStart+1<indexIntermediate) {
				String seqName = str.substring(0,indexStart);
				String firstStr = str.substring(indexStart+1,indexIntermediate);
				String lastStr = str.substring(indexIntermediate+1);
				boolean error = false;
				if (!FieldValidator.isAlphaNumeric(seqName)) {
					errorsOne.add(lblQuerySeq.getText()+ " :" + " Invalid characters for sequence name\n");
					txtQuerySeq.setBackground(oc);
					error = true;
				} 
				if (!FieldValidator.isNumeric(firstStr, new Integer(0))){
					errorsOne.add(lblQuerySeq.getText() + " :" + " First position should be integer\n");
					txtQuerySeq.setBackground(oc);
					error = true;
				}
				if (!FieldValidator.isNumeric(lastStr, new Integer(0))) {
					errorsOne.add(lblQuerySeq.getText()+ " :" + " Last position should be integer \n");
					txtQuerySeq.setBackground(oc);
					error = true;
				}
				int first = Integer.parseInt(firstStr);
				int last = Integer.parseInt(lastStr);
				if(first > last) {
					errorsOne.add(lblQuerySeq.getText()+ " :" + " Last position should be greater or equal than first position \n");
					txtQuerySeq.setBackground(oc);
					error = true;
				}
				if(!error) {
					commonUserParameters.put("QuerySeq", seqName);
					commonUserParameters.put("QueryFirst", first);
					commonUserParameters.put("QueryLast", last);
				}
			} else {
				errorsOne.add(lblQuerySeq.getText()+ " :" + " Invalid format");
				txtQuerySeq.setBackground(oc);
			}
		}
		
		
		if (txtHete.getText() != null && !txtHete.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtHete.getText(), new Double(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblHete.getText(), FieldValidator.ERROR_NUMERIC));
				txtHete.setBackground(oc);
			} else {
				commonUserParameters.put("HeterozygosityRate", Double.parseDouble(txtHete.getText()));
			}
		}
		
		if (txtMinQuality.getText() != null && !txtMinQuality.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtMinQuality.getText(),new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblMinQuality.getText(), FieldValidator.ERROR_INTEGER));
				txtMinQuality.setBackground(oc);
			} else {
				commonUserParameters.put("MinQuality", Short.parseShort(txtMinQuality.getText()));					
			}
		}
		
		if (txtMaxBaseQS.getText() != null && !txtMaxBaseQS.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtMaxBaseQS.getText(), new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblMaxBaseQS.getText(), FieldValidator.ERROR_INTEGER));
				txtMaxBaseQS.setBackground(oc);
			}else{
				commonUserParameters.put("MaxBaseQS", Short.parseShort(txtMaxBaseQS.getText()));
			}
		}
		
		if (txtAltMax.getText() != null && !txtAltMax.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtAltMax.getText(), new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblAltMax.getText(), FieldValidator.ERROR_INTEGER));
				txtAltMax.setBackground(oc);
			}else{
				commonUserParameters.put("MaxAltCoverage", Double.parseDouble(txtAltMax.getText()));
			}
		}

		if (txtAltMin.getText() != null && !txtAltMin.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtAltMin.getText(), new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblAltMin.getText(), FieldValidator.ERROR_INTEGER));
				txtAltMin.setBackground(oc);
			}else{
				commonUserParameters.put("MinAltCoverage", Double.parseDouble(txtAltMin.getText()));
			}
		}
		
		if (txtMaximunAlignmentStartPosition.getText() != null && !txtMaximunAlignmentStartPosition.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtMaximunAlignmentStartPosition.getText(),new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblMaximunAlignmentPer.getText(), FieldValidator.ERROR_INTEGER));
				txtMaximunAlignmentStartPosition.setBackground(oc);
			}else{
				commonUserParameters.put("MaxAlnsPerStartPos", Integer.parseInt(txtMaximunAlignmentStartPosition.getText()));
			}
		}
		
		if (txtPloidy.getText() != null && !txtPloidy.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtPloidy.getText(), new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblPloidy.getText(), FieldValidator.ERROR_INTEGER));
				txtPloidy.setBackground(oc);
			} else {
				commonUserParameters.put("NormalPloidy", Byte.parseByte(txtPloidy.getText()));
			}
		}
		
		if (btnkeepLowerCaseRefChk.getSelection()) {
			commonUserParameters.put("IgnoreLowerCaseRef", true);
		}
		
		if (btnIncludeAllPrimaryAlignments.getSelection()) {
			commonUserParameters.put("ProcessNonUniquePrimaryAlignments", true);
		}
		
		if (btnIncludeSecondaryAlignments.getSelection()) {
			commonUserParameters.put("ProcessSecondaryAlignments", true);
		}
		
		if (btnGenotypeAllCovered.getSelection()) {
			commonUserParameters.put("GenotypeAll", true);
		}
		
		if (btnEmbeddedSNVs.getSelection()) {
			commonUserParameters.put("CallEmbeddedSNVs", true);
		}
		
		if (txtHapAvgCov.getText() != null && !txtHapAvgCov.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtHapAvgCov.getText(), new Double(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblHapAvgCov.getText(), FieldValidator.ERROR_NUMERIC));
				txtHapAvgCov.setBackground(oc);
			} else {
				commonUserParameters.put("HaploidAverageCoverage", Double.parseDouble(txtHapAvgCov.getText()));
			}
		}
		
		if (btnPrintSamplePloidy.getSelection()) {
			commonUserParameters.put("PrintSamplePloidy", true);
		}
		
		if(source == 'W'){
			if(cmbWizMinGQ.getSelectionIndex()==0){
				commonUserParameters.put("UseGQboth",false);
			}else{ 
				commonUserParameters.put("UseGQboth",true);
			}
		}
		
		if(!errorsOne.isEmpty())
			return null;
		
		return commonUserParameters;
	}
	
	public ArrayList<String> getErrors() {
		return errorsOne;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
