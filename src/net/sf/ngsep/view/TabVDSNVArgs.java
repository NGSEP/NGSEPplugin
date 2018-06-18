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
 * @author Jorge Duitama
 */
public class TabVDSNVArgs extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	
	
	private Label lblQuerySeq;
	private Text txtQuerySeq;
	private Text txtQueryFirst;
	private Text txtQueryLast;
		
	
	private Label lblHete;
	private Text txtHete;
	
	private Label lblMinQuality;
	private Text txtMinQuality;
	
	private Label lblWizMinGQ;
	private Combo cmbWizMinGQ;
		
	
	private Label lblMaxBaseQS;
	private Text txtMaxBaseQS;
		
	private Label lblMaximunAlignmentPer;
	private Text txtMaximunAlignmentStartPosition;
	
	private Button btnPrintSamplePloidy;
	
	private Label lblPloidy;
	private Text txtPloidy;
	
	private Button btnIgnoreLowerCaseRefChk;
	private Button btnIncludeAllPrimaryAlignments;
	private Button btnIncludeSecondaryAlignments;
	private Button btnEmbeddedSNVs;
	
	private MouseListenerNgsep mouse = new MouseListenerNgsep();
	private ArrayList<String> errorsOne = new ArrayList<String>();
	
	private final static int DEF_MIN_QUAL = 40;
	private final static int DEF_MAX_BASE_QS = 30;
	private final static int DEF_PLOIDY = 2;
	private final static int DEF_ALIGN_PER_STARTPOS = 2;
	private final static double DEF_HETEROZYGOSITY = 0.001;
	
	private char behavior;
	
	
	
	
	public TabVDSNVArgs(Composite parent, int style, char behavior) {
		super(parent, style);
		this.behavior = behavior;
	}
	
	public void paint() {
		
		
		lblQuerySeq = new Label(this, SWT.NONE);
		lblQuerySeq.setBounds(10, 20, 60, 22);
		lblQuerySeq.setText("Region:");

		txtQuerySeq = new Text(this, SWT.BORDER);
		txtQuerySeq.setBounds(80, 20, 110, 22);
		txtQuerySeq.addMouseListener(mouse);
		
		Label lbl1 = new Label(this,  SWT.NONE);
		lbl1.setBounds(195, 20, 15, 22);
		lbl1.setText(":");
		
		txtQueryFirst = new Text(this, SWT.BORDER);
		txtQueryFirst.setBounds(210, 20, 110, 22);
		txtQueryFirst.addMouseListener(mouse);
		
		Label lbl2 = new Label(this,  SWT.NONE);
		lbl2.setBounds(325, 20, 15, 22);
		lbl2.setText("-");
		
		txtQueryLast = new Text(this, SWT.BORDER);
		txtQueryLast.setBounds(340, 20, 110, 22);
		txtQueryLast.addMouseListener(mouse);
		
		lblPloidy = new Label(this, SWT.NONE);
		lblPloidy.setText("Ploidy:");
		lblPloidy.setBounds(10, 60, 300, 22);

		txtPloidy = new Text(this, SWT.BORDER);
		txtPloidy.setBounds(340, 60, 80, 22);
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
				if (!"2".equals(txtPloidy.getText())) {
					btnPrintSamplePloidy.setSelection(true);
				}
			}
		});
		txtPloidy.addMouseListener(mouse);
		
		lblHete = new Label(this, SWT.NONE);
		lblHete.setBounds(10, 100, 300, 22);
		lblHete.setText("Heterozygosity Rate:");

		txtHete = new Text(this, SWT.BORDER);
		txtHete.setBounds(340, 100, 120, 22);
		txtHete.setText(String.valueOf(DEF_HETEROZYGOSITY));
		txtHete.addMouseListener(mouse);
		
		lblMinQuality = new Label(this, SWT.NONE);
		lblMinQuality.setText("Minimum Genotype Quality Score:");
		lblMinQuality.setBounds(10, 140, 300, 22);

		txtMinQuality = new Text(this, SWT.BORDER);
		txtMinQuality.setText(String.valueOf(DEF_MIN_QUAL));
		txtMinQuality.setBounds(340, 140, 80, 22);
		txtMinQuality.addMouseListener(mouse);
		
		lblMaxBaseQS = new Label(this, SWT.NONE);
		lblMaxBaseQS.setText("Maximum Base Quality Score:");
		lblMaxBaseQS.setBounds(10, 180, 300, 22);

		txtMaxBaseQS = new Text(this, SWT.BORDER);
		txtMaxBaseQS.setText(String.valueOf(DEF_MAX_BASE_QS));
		txtMaxBaseQS.setBounds(340, 180, 80, 21);
		txtMaxBaseQS.addMouseListener(mouse);
		
		lblMaximunAlignmentPer = new Label(this, SWT.NONE);
		lblMaximunAlignmentPer.setText("Maximum Alignments Per Start Position:");
		lblMaximunAlignmentPer.setBounds(10, 220, 300, 22);

		txtMaximunAlignmentStartPosition = new Text(this, SWT.BORDER);
		txtMaximunAlignmentStartPosition.setText(String.valueOf(DEF_ALIGN_PER_STARTPOS));
		txtMaximunAlignmentStartPosition.setBounds(340, 220, 80, 22);
		txtMaximunAlignmentStartPosition.addMouseListener(mouse);
		
		if(behavior == MainVariantsDetector.BEHAVIOR_WIZARD){
			
			lblWizMinGQ = new Label(this, SWT.NONE);
			lblWizMinGQ.setText("Use minimun Genotype Quality Score in:");
			lblWizMinGQ.setBounds(10, 300, 300, 22);
			
			cmbWizMinGQ = new Combo(this, SWT.READ_ONLY);
			cmbWizMinGQ.setItems(new String[] {"Only for discovery", "Discovery and genotyping"});
			cmbWizMinGQ.setBounds(320, 300, 160, 22);
			cmbWizMinGQ.select(0);
		}
		
		btnPrintSamplePloidy = new Button(this, SWT.CHECK);
		btnPrintSamplePloidy.setBounds(500, 20, 290, 22);
		btnPrintSamplePloidy.setText("Print Sample Ploidy");
		
		btnIgnoreLowerCaseRefChk = new Button(this, SWT.CHECK);
		btnIgnoreLowerCaseRefChk.setBounds(500, 60, 290, 22);
		btnIgnoreLowerCaseRefChk.setText("Ignore Lower Case Reference");
		
		btnIncludeAllPrimaryAlignments = new Button(this, SWT.CHECK);
		btnIncludeAllPrimaryAlignments.setText("Process All Primary Alignments");
		btnIncludeAllPrimaryAlignments.setBounds(500, 100, 290, 22);
		
		btnIncludeSecondaryAlignments = new Button(this, SWT.CHECK);
		btnIncludeSecondaryAlignments.setText("Process Secondary Alignments");
		btnIncludeSecondaryAlignments.setBounds(500, 140, 290, 22);
				
		btnEmbeddedSNVs = new Button(this, SWT.CHECK);
		btnEmbeddedSNVs.setText("Call SNVs within STRs");
		btnEmbeddedSNVs.setBounds(500, 180, 290, 22);
	}
	
	public Map<String,Object> getParams(){
		
		// This map stores parameters set by the user that are common for all samples
		errorsOne.clear();
		Map<String,Object> commonUserParameters = new TreeMap<>();
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		
		if (txtQuerySeq.getText() != null && !txtQuerySeq.getText().equals("")) {
			
			if (!FieldValidator.isAlphaNumeric(txtQuerySeq.getText())) {
				errorsOne.add(lblQuerySeq.getText()+ " :" + " Invalid characters for sequence name");
				txtQuerySeq.setBackground(oc);
			} else {
				commonUserParameters.put("QuerySeq", txtQuerySeq.getText());
			}
		}
		int first = -1;
		int last = -1;
		if (txtQueryFirst.getText() != null && !txtQueryFirst.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtQueryFirst.getText(), new Integer(0))){
				errorsOne.add(lblQuerySeq.getText() + " :" + " First position should be integer");
				txtQueryFirst.setBackground(oc);
			} else {
				first = Integer.parseInt(txtQueryFirst.getText());
				//commonUserParameters.put("QueryFirst", first);
			}
		}
		if (txtQueryLast.getText() != null && !txtQueryLast.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtQueryLast.getText(), new Integer(0))){
				errorsOne.add(lblQuerySeq.getText() + " :" + " Last position should be integer");
				txtQueryLast.setBackground(oc);
			} else {
				last = Integer.parseInt(txtQueryLast.getText());
				//commonUserParameters.put("QueryLast", last);
			}
		}
			
		if((first == -1 && last!=-1) || (first != -1 && last==-1) ) {
			errorsOne.add(lblQuerySeq.getText()+ " :" + " Both first and last position should be provided \n");
			txtQueryFirst.setBackground(oc);
			txtQueryLast.setBackground(oc);
		} else if(first > last) {
			errorsOne.add(lblQuerySeq.getText()+ " :" + " Last position should be greater or equal than first position \n");
			txtQueryFirst.setBackground(oc);
			txtQueryLast.setBackground(oc);
		} else if (first !=-1 && last!=-1){
			commonUserParameters.put("QueryFirst", txtQueryFirst.getText());
			commonUserParameters.put("QueryLast", txtQueryLast.getText());
		}
		if (txtHete.getText() != null && !txtHete.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtHete.getText(), new Double(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblHete.getText(), FieldValidator.ERROR_NUMERIC));
				txtHete.setBackground(oc);
			} else {
				commonUserParameters.put("HeterozygosityRate", txtHete.getText());
			}
		}
		
		if (txtMinQuality.getText() != null && !txtMinQuality.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtMinQuality.getText(),new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblMinQuality.getText(), FieldValidator.ERROR_INTEGER));
				txtMinQuality.setBackground(oc);
			} else {
				commonUserParameters.put("MinQuality", txtMinQuality.getText());					
			}
		}
		
		if (txtMaxBaseQS.getText() != null && !txtMaxBaseQS.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtMaxBaseQS.getText(), new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblMaxBaseQS.getText(), FieldValidator.ERROR_INTEGER));
				txtMaxBaseQS.setBackground(oc);
			} else{
				commonUserParameters.put("MaxBaseQS", txtMaxBaseQS.getText());
			}
		}
		
		if (txtMaximunAlignmentStartPosition.getText() != null && !txtMaximunAlignmentStartPosition.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtMaximunAlignmentStartPosition.getText(),new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblMaximunAlignmentPer.getText(), FieldValidator.ERROR_INTEGER));
				txtMaximunAlignmentStartPosition.setBackground(oc);
			}else{
				commonUserParameters.put("MaxAlnsPerStartPos", txtMaximunAlignmentStartPosition.getText());
			}
		}
		
		if (txtPloidy.getText() != null && !txtPloidy.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtPloidy.getText(), new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblPloidy.getText(), FieldValidator.ERROR_INTEGER));
				txtPloidy.setBackground(oc);
			} else {
				commonUserParameters.put("NormalPloidy", txtPloidy.getText());
			}
		}
		
		if (btnIgnoreLowerCaseRefChk.getSelection()) {
			commonUserParameters.put("IgnoreLowerCaseRef", true);
		}
		
		if (btnIncludeAllPrimaryAlignments.getSelection()) {
			commonUserParameters.put("ProcessNonUniquePrimaryAlignments", true);
		}
		
		if (btnIncludeSecondaryAlignments.getSelection()) {
			commonUserParameters.put("ProcessSecondaryAlignments", true);
		}
		
		if (btnEmbeddedSNVs.getSelection()) {
			commonUserParameters.put("CallEmbeddedSNVs", true);
		}
		
		if (btnPrintSamplePloidy.getSelection()) {
			commonUserParameters.put("PrintSamplePloidy", true);
		}
		
		if(behavior == MainVariantsDetector.BEHAVIOR_WIZARD) {
			if(cmbWizMinGQ.getSelectionIndex()==0){
				commonUserParameters.put("UseGQboth",false);
			}else{ 
				commonUserParameters.put("UseGQboth",true);
			}
		}
		
		return errorsOne.isEmpty()?commonUserParameters:null;
	}
	
	public ArrayList<String> getErrors() {
		return errorsOne;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
