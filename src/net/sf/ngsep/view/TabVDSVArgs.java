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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.MouseListenerNgsep;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;


/**
 * @author Daniel Cruz
 *
 */
public class TabVDSVArgs extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	
	private Label lblGenomicSize;
	private Text txtGenomicSize;
	private Label lblBinSize;
	private Text txtBinSize;
	private Button btnIgnoreProperPairFlagChk;
	private Label lblCNVAlgorithms;
	private Table tblCNVAlgorithms;
	private Label lblMinSVQuality;
	private Text txtMinSVQuality;
	private Label lblMaxLenDeletion;
	private Text txtMaxLenDeletion;
	private Label lblSizeSplitReadSeed;
	private Text txtSizeSplitReadSeed;
	private Label lblMaxPCTOverlapCNVs;
	private Text txtMaxPCTOverlapCNVs;
	
	
	private ArrayList<String> errorsOne = new ArrayList<String>();
	private MouseListenerNgsep mouse = new MouseListenerNgsep();
	
	private final static int DEF_BIN_SIZE = 100;
	private final static short DEF_MIN_SV_QUALITY = 20;
	private final static int DEF_SEED_SIZE_SR = 8;
	private final static int DEF_MAX_DELETION_SIZE = 1000000;
	private final static int DEF_MAX_PCT_OVLAP_CNV = 100; 
	
	
	public TabVDSVArgs(Composite parent, int style, char source) {
		super(parent, style);
	}
	
	public void paint() {
		

		
		lblGenomicSize = new Label(this, SWT.NONE);
		lblGenomicSize.setText("Genome Size:");
		lblGenomicSize.setBounds(10, 20, 139, 20);

		txtGenomicSize = new Text(this, SWT.BORDER);
		txtGenomicSize.setBounds(155, 20, 180, 21);
		txtGenomicSize.addMouseListener(mouse);
		
		lblBinSize = new Label(this, SWT.NONE);
		lblBinSize.setText("Bin Size:");
		lblBinSize.setBounds(10, 60, 139, 20);

		txtBinSize = new Text(this, SWT.BORDER);
		txtBinSize.setBounds(155, 60, 91, 21);
		txtBinSize.setText(String.valueOf(DEF_BIN_SIZE));
		txtBinSize.addMouseListener(mouse);
		
		btnIgnoreProperPairFlagChk = new Button(this, SWT.CHECK);	
		btnIgnoreProperPairFlagChk.setText("Ignore proper pair flag");
		btnIgnoreProperPairFlagChk.setBounds(10, 100, 400, 20);
		
		lblCNVAlgorithms = new Label(this, SWT.NONE);
		lblCNVAlgorithms.setText("(*)Choose Algorithm for CNV detection:");
		lblCNVAlgorithms.setBounds(10, 150, 300, 20);			

		tblCNVAlgorithms =new Table(this, SWT.MULTI | SWT.BORDER| SWT.V_SCROLL| SWT.CHECK);
		tblCNVAlgorithms.setBounds(10, 180, 200, 200);
		tblCNVAlgorithms.setHeaderVisible(true);
		List<String> CNValgorithms = new ArrayList<String>();
		CNValgorithms.add("CNVnator");
		CNValgorithms.add("EWT");
		TableColumn columSelect = new TableColumn(tblCNVAlgorithms, SWT.CENTER);
		columSelect.setText("CNV Algorithms");
		for (int i = 0; i < CNValgorithms.size(); i++) {
			TableItem items = new TableItem(tblCNVAlgorithms, SWT.NONE);
			items.setText(0, CNValgorithms.get(i));
		}
		tblCNVAlgorithms.getItem(0).setChecked(true);
		tblCNVAlgorithms.getColumn(0).setWidth(100);
		
		lblMinSVQuality = new Label(this, SWT.NONE);
		lblMinSVQuality.setText("Min Quality:");
		lblMinSVQuality.setBounds(450, 20, 190, 20);

		txtMinSVQuality = new Text(this, SWT.BORDER);
		txtMinSVQuality.setBounds(650, 20, 100, 21);
		txtMinSVQuality.setText(String.valueOf(DEF_MIN_SV_QUALITY));
		txtMinSVQuality.addMouseListener(mouse);
		
		lblMaxLenDeletion = new Label(this, SWT.NONE);
		lblMaxLenDeletion.setText("Max Deletion Length:");
		lblMaxLenDeletion.setBounds(450, 60, 190, 20);

		txtMaxLenDeletion = new Text(this, SWT.BORDER);
		txtMaxLenDeletion.setBounds(650, 60, 100, 21);
		txtMaxLenDeletion.setText(String.valueOf(DEF_MAX_DELETION_SIZE));
		txtMaxLenDeletion.addMouseListener(mouse);
		
		lblSizeSplitReadSeed = new Label(this, SWT.NONE);
		lblSizeSplitReadSeed.setText("Seed Size (Split Read):");
		lblSizeSplitReadSeed.setBounds(450, 100, 190, 20);

		txtSizeSplitReadSeed = new Text(this, SWT.BORDER);
		txtSizeSplitReadSeed.setBounds(650, 100, 100, 21);
		txtSizeSplitReadSeed.setText(String.valueOf(DEF_SEED_SIZE_SR));
		txtSizeSplitReadSeed.addMouseListener(mouse);
		
		lblMaxPCTOverlapCNVs = new Label(this, SWT.NONE);
		lblMaxPCTOverlapCNVs.setText("Max % Overlap repeats-CNVs:");
		lblMaxPCTOverlapCNVs.setBounds(450, 140, 240, 20);

		txtMaxPCTOverlapCNVs = new Text(this, SWT.BORDER);
		txtMaxPCTOverlapCNVs.setBounds(700, 140, 100, 21);
		txtMaxPCTOverlapCNVs.setText(String.valueOf(DEF_MAX_PCT_OVLAP_CNV));
		txtMaxPCTOverlapCNVs.addMouseListener(mouse);
		
	}
	
	public Map<String,Object> getParams(){
		
		// Common Fields

		errorsOne.clear();
		Map<String,Object> commonUserParameters = new TreeMap<String, Object>();
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		
		
		if (txtGenomicSize.getText() != null && !txtGenomicSize.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtGenomicSize.getText(), new Long(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblGenomicSize.getText(), FieldValidator.ERROR_NUMERIC));
				txtGenomicSize.setBackground(oc);
			}else{
				commonUserParameters.put("GenomeSize", Long.parseLong(txtGenomicSize.getText()));
			}
		}

		if (txtBinSize.getText() != null && !txtBinSize.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtBinSize.getText(), new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblBinSize.getText(), FieldValidator.ERROR_INTEGER));
				txtBinSize.setBackground(oc);
			}else{
				commonUserParameters.put("BinSize", Integer.parseInt(txtBinSize.getText()));
			}
		}
		
		if (btnIgnoreProperPairFlagChk.getSelection()) {
			commonUserParameters.put("IgnoreProperPairFlag", true);
		}
		
		if (tblCNVAlgorithms.isEnabled()){
			String algorithms = "";
			for(int i = 0 ; i < tblCNVAlgorithms.getItems().length ; i++){
				if(tblCNVAlgorithms.getItem(i).getChecked()){
					algorithms += tblCNVAlgorithms.getItem(i).getText(0) + ",";
				}
			}
			if (algorithms.equals("")) {
				errorsOne.add(FieldValidator.buildMessage(tblCNVAlgorithms.getColumn(0).getText(), FieldValidator.ERROR_FILE_EMPTY));
				tblCNVAlgorithms.setBackground(oc);
			} else {
				if(algorithms.lastIndexOf(",") == algorithms.length()-1){
					algorithms = algorithms.substring(0, algorithms.lastIndexOf(","));
				}
				commonUserParameters.put("AlgCNV", algorithms);
			}
		}
		
		if (txtMinSVQuality.getText() != null && !txtMinSVQuality.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtMinSVQuality.getText(), new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblMinSVQuality.getText(), FieldValidator.ERROR_INTEGER));
				txtMinSVQuality.setBackground(oc);
			}else{
				commonUserParameters.put("MinSVQuality", Short.parseShort(txtMinSVQuality.getText()));
			}
		}
		
		if (txtMaxLenDeletion.getText() != null && !txtMaxLenDeletion.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtMaxLenDeletion.getText(), new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblMaxLenDeletion.getText(), FieldValidator.ERROR_INTEGER));
				txtMaxLenDeletion.setBackground(oc);
			}else{
				commonUserParameters.put("MaxLengthDeletion", Integer.parseInt(txtMaxLenDeletion.getText()));
			}
		}
		
		if (txtSizeSplitReadSeed.getText() != null && !txtSizeSplitReadSeed.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtSizeSplitReadSeed.getText(), new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblSizeSplitReadSeed.getText(), FieldValidator.ERROR_INTEGER));
				txtSizeSplitReadSeed.setBackground(oc);
			}else{
				commonUserParameters.put("SplitReadSeed", Integer.parseInt(txtSizeSplitReadSeed.getText()));
			}
		}
		
		if (txtMaxPCTOverlapCNVs.getText() != null && !txtMaxPCTOverlapCNVs.getText().equals("")) {
			if (!FieldValidator.isNumeric(txtMaxPCTOverlapCNVs.getText(), new Integer(0))) {
				errorsOne.add(FieldValidator.buildMessage(lblMaxPCTOverlapCNVs.getText(), FieldValidator.ERROR_INTEGER));
				txtMaxPCTOverlapCNVs.setBackground(oc);
			}else{
				commonUserParameters.put("MaxPCTOverlapCNVs", Integer.parseInt(txtMaxPCTOverlapCNVs.getText()));
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
