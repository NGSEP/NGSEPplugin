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
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Daniel Cruz
 *
 */
public class TabMapAliArgs extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */

	private Button btnInput;
	private Combo cmbInput;
	private Button btnPhred;
	private Text txtTrim5;
	private Text txtTrim3;
	private Label lblTrim3;
	private Label lblTrim5;
	private Text txtD;
	private Text txtR;
	private Label lblD;
	private Label lblR;

	private static final String INPUT_TYPE_FASTA = "FASTA";
	private static final String INPUT_TYPE_FASTQ = "FASTQ";
	private static final String INPUT_TYPE_QSEQ = "Illumina's qseq format";
	private static final String INPUT_TYPE_RAW = "raw one-sequence-per-line";

	//Alignment options
	private Label lblN;
	private Text txtN;
	private Label lblL;
	private Text txtL;
	private Label lblI; 
	private Text txtI;
	private Label lblNceil;
	private Text txtNCeil;
	private Label lblDpad;
	private Text txtDpad;
	private Label lblGbar;
	private Text txtGbar;

	private Button btnIgnoreEquals;
	private Button btnNofw;
	private Button btnNorc;
	//-------------------------------

	private ArrayList<String> errors = new ArrayList<String>();

	
	
	public TabMapAliArgs(Composite parent, int style, char source) {
		super(parent, style);
	}


	public void paint() {
		

		btnInput = new Button(this, SWT.CHECK);
		btnInput.setText("Input:");
		btnInput.addMouseListener(onMouseClickButton);
		btnInput.setBounds(17, 21, 124, 21);

		cmbInput = new Combo(this, SWT.READ_ONLY);
		cmbInput.setBounds(160, 21, 183, 23);
		String items[] = { "----select one----", INPUT_TYPE_FASTQ,INPUT_TYPE_QSEQ, INPUT_TYPE_FASTA, INPUT_TYPE_RAW };
		cmbInput.setItems(items);
		cmbInput.setVisible(false);

		btnPhred = new Button(this, SWT.CHECK);
		btnPhred.setBounds(17, 61, 124, 21);
		btnPhred.setText("Phred 64");
		btnPhred.getSelection();

		lblTrim5 = new Label(this, SWT.NONE);
		lblTrim5.setText("Trim:	5':");
		lblTrim5.setBounds(17, 100, 110, 21);

		txtTrim5 = new Text(this, SWT.BORDER);
		txtTrim5.setBounds(130, 100, 42, 21);

		lblTrim3 = new Label(this, SWT.NONE);
		lblTrim3.setText("3':");
		lblTrim3.setBounds(200, 100, 30, 21);

		txtTrim3 = new Text(this, SWT.BORDER);
		txtTrim3.setBounds(250, 100, 42, 21);

		lblD = new Label(this, SWT.NONE);
		lblD.setText("Give up extending after:");
		lblD.setBounds(17, 140, 214, 21);

		txtD = new Text(this, SWT.BORDER);
		txtD.setBounds(250, 140, 42, 21);

		lblR = new Label(this, SWT.NONE);
		lblR.setText("Maximum number of times will 're-seed':");
		lblR.setBounds(17, 180, 280, 21);

		txtR = new Text(this, SWT.BORDER);
		txtR.setBounds(300, 180, 42, 21);

		lblL = new Label(this, SWT.NONE);
		lblL.setText("Length of seed 'word':");
		lblL.setBounds(380, 21, 300, 21);

		txtL = new Text(this, SWT.BORDER);
		txtL.setBounds(750, 21, 42, 21);

		lblI = new Label(this, SWT.NONE);
		lblI.setText("Interval between seed 'words':");
		lblI.setBounds(380, 61, 300, 21);

		txtI = new Text(this, SWT.BORDER);
		txtI.setBounds(750, 61, 76, 21);

		lblGbar = new Label(this, SWT.NONE);
		lblGbar.setText("Disallow gaps within the first/last:");
		lblGbar.setBounds(380, 101, 300, 21);

		txtGbar = new Text(this, SWT.BORDER);
		txtGbar.setBounds(750, 101, 42, 21);

		lblDpad = new Label(this, SWT.NONE);
		lblDpad.setText("Include <int> extra ref chars:");
		lblDpad.setBounds(380, 141, 300, 21);

		txtDpad = new Text(this, SWT.BORDER);
		txtDpad.setBounds(750, 141, 42, 21);

		lblNceil = new Label(this, SWT.NONE);
		lblNceil.setText("Max number of ambiguous characters, f(read length):");
		lblNceil.setBounds(380, 181, 350, 21);

		txtNCeil = new Text(this, SWT.BORDER);
		txtNCeil.setBounds(750, 181, 76, 21);

		lblN = new Label(this, SWT.NONE);
		lblN.setText("Allowed mismatches in seed alignment:");
		lblN.setBounds(380, 221, 300, 21);

		txtN = new Text(this, SWT.BORDER);
		txtN.setBounds(750, 221, 42, 21);

		btnIgnoreEquals = new Button(this, SWT.CHECK);
		btnIgnoreEquals.setBounds(380, 261, 250, 21);
		btnIgnoreEquals.setText("Ignore Base Qualities");

		btnNofw = new Button(this, SWT.CHECK);
		btnNofw.setText("Map only to reverse strand");
		btnNofw.setBounds(380, 301, 250, 21);

		btnNorc = new Button(this, SWT.CHECK);
		btnNorc.setText("Map only to forward strand");
		btnNorc.setBounds(380, 341, 250, 21);		


	}

	private final MouseListener onMouseClickButton = new MouseListener() {

		@Override
		public void mouseDoubleClick(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseDown(MouseEvent e) {
			// TODO Auto-generated method stub

		}
		@Override
		public void mouseUp(MouseEvent e) {
			if (btnInput.getSelection()) {
				cmbInput.setVisible(true);
				cmbInput.select(0);
			} else if (!btnInput.getSelection()) {
				cmbInput.setVisible(false);
				cmbInput.redraw();
				cmbInput.update();
				cmbInput.pack();
			}
			cmbInput.update();
		}
	};


	public Map<String, Object> getParams(){

		errors.clear();
		Map<String,Object> userParams = new TreeMap<String, Object>();
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;

		if (btnInput.getSelection()) {
			int index = cmbInput.getSelectionIndex();
			String selection = "";
			if(index>0) selection = cmbInput.getItem(index);
			if (INPUT_TYPE_FASTQ.equals(selection)) {
				//commandArray.add("-q");
				userParams.put("inputCMD", "-q");
			} else if (INPUT_TYPE_QSEQ.equals(selection)) {
				//commandArray.add("--qseq");
				userParams.put("inputCMD", "--qseq");
			} else if (INPUT_TYPE_FASTA.equals(selection)) {
				//commandArray.add("-f");
				userParams.put("inputCMD", "-f");
			} else if (INPUT_TYPE_RAW.equals(selection)) {
				//commandArray.add("-r");
				userParams.put("inputCMD", "-r");
			}
		}

		if (btnPhred.getSelection() == true) {
			//				commandArray.add("--phred64");
			userParams.put("phredCMD", "--phred64");
		}


		if (txtTrim5.getText() != null && txtTrim5.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtTrim5.getText(), new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblTrim5.getText(), FieldValidator.ERROR_INTEGER));
				txtTrim5.setBackground(oc);
			} else {
				//					commandArray.add("--trim5");
				//					commandArray.add(txtTrim5.getText());
				userParams.put("trim5CMD", "--trim5");
				userParams.put("trim5Arg", txtTrim5.getText());
			}
		}

		if (txtTrim3.getText() != null && txtTrim3.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtTrim3.getText(), new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblTrim3.getText(), FieldValidator.ERROR_INTEGER));
				txtTrim3.setBackground(oc);
			} else {
				//					commandArray.add("--trim3");
				//					commandArray.add(txtTrim3.getText());
				userParams.put("trim3CMD", "--trim3");
				userParams.put("trim3Arg", txtTrim3.getText());
			}
		}


		if (txtN.getText() != null && txtN.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtN.getText(), new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblN.getText() , FieldValidator.ERROR_INTEGER));
				txtN.setBackground(oc);
			} else {
				//					commandArray.add("-N");
				//					commandArray.add(txtN.getText());
				userParams.put("nCMD", "-N");
				userParams.put("nArg", txtN.getText());
			}
		}

		if (txtL.getText() != null && txtL.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtL.getText(), new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblL.getText() , FieldValidator.ERROR_INTEGER));
				txtL.setBackground(oc);
			} else {
				//					commandArray.add("-L");
				//					commandArray.add(txtL.getText());
				userParams.put("lCMD", "-L");
				userParams.put("lArg", txtL.getText());
			}
		}

		if (txtGbar.getText() != null && txtGbar.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtGbar.getText(), new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblGbar.getText() , FieldValidator.ERROR_INTEGER));
				txtGbar.setBackground(oc);
			} else {
				//					commandArray.add("--gbar");
				//					commandArray.add(txtGbar.getText());
				userParams.put("gbarCMD", "--gbar");
				userParams.put("gbarArg", txtGbar.getText());
			}
		}


		if (txtDpad.getText() != null && txtDpad.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtDpad.getText(), new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblDpad.getText() , FieldValidator.ERROR_INTEGER));
				txtDpad.setBackground(oc);
			} else {
				//					commandArray.add("--dpad");
				//					commandArray.add(txtDpad.getText());
				userParams.put("dpadCMD", "--dpad");
				userParams.put("dpadArg", txtDpad.getText());
			}
		}

		if (txtD.getText() != null && txtD.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtD.getText(), new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblD.getText() , FieldValidator.ERROR_INTEGER));
				txtD.setBackground(oc);
			} else {
				//					commandArray.add("-D");
				//					commandArray.add(txtD.getText());
				userParams.put("dCMD", "-D");
				userParams.put("dArg", txtD.getText());
			}
		}

		if (txtR.getText() != null && txtR.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtR.getText(), new Integer(0))) {
				errors.add(FieldValidator.buildMessage(lblR.getText() , FieldValidator.ERROR_INTEGER));
				txtR.setBackground(oc);
			} else {
				//					commandArray.add("-R");
				//					commandArray.add(txtR.getText());
				userParams.put("rCMD", "-R");
				userParams.put("rArg", txtR.getText());
			}
		}

		if (txtI.getText() != null && txtI.getText().length()>0) {
			//				commandArray.add("-i");
			//				commandArray.add(txtI.getText().toUpperCase());
			userParams.put("iCMD", "-I");
			userParams.put("iArg", txtI.getText().toUpperCase());
		}

		if (txtNCeil.getText() != null && txtNCeil.getText().length()>0) {
			//				commandArray.add("--n-ceil");
			//				commandArray.add(txtNCeil.getText().toUpperCase());
			userParams.put("nceilCMD", "--n-ceil");
			userParams.put("nceilArg", txtNCeil.getText().toUpperCase());
		}


		if (btnIgnoreEquals.getSelection() == true) {
			//				commandArray.add("--ignore-quals");
			userParams.put("ignoreCMD", "--ignore-quals");
		}

		if (btnNofw.getSelection() == true) {
			//				commandArray.add("--nofw");
			userParams.put("nofwCMD", "--nofw");
		}

		if (btnNorc.getSelection() == true) {
			//				commandArray.add("--norc");
			userParams.put("norcCMD", "--norc");
		}

		if(!errors.isEmpty())
			return null;

		return userParams;

	}	





	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}



	public ArrayList<String> getErrors() {
		return errors;
	}

}
