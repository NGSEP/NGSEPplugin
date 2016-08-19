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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Daniel Cruz
 *
 */
public class TabMapSortArgs extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private Button btnSkipSorting;
	private Button btnKeepUnsorted;	
	
	
	private ArrayList<String> errors = new ArrayList<String>();
	
	private char source;
	
	
	public TabMapSortArgs(Composite parent, int style, char source) {
		super(parent, style);
		this.source = source;
	}
	
	public void paint() {
		

		
		if(source!='W'){
			btnSkipSorting = new Button(this, SWT.CHECK);
			btnSkipSorting.setText("Skip sorting");
			btnSkipSorting.setBounds(17, 21, 124, 21);
			btnSkipSorting.addMouseListener(onMouseClickButtonSkipSorting);
		}

		btnKeepUnsorted = new Button(this, SWT.CHECK);
		btnKeepUnsorted.setText("Keep unsorted sam file");
		btnKeepUnsorted.setBounds(17, 61, 202, 21);
		
	}
	
	public Map<String, Object> getParams(){

		errors.clear();
		Map<String,Object> userParams = new TreeMap<String, Object>();
		
		if(btnSkipSorting!=null)
			userParams.put("skipSortCMD", btnSkipSorting.getSelection());
		
		userParams.put("keepUnsortedCMD", btnKeepUnsorted.getSelection());
			
			
		if(!errors.isEmpty())
			return null;

		return userParams;
		
	}
	
	private final MouseListener onMouseClickButtonSkipSorting = new MouseListener() {

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
			if (btnSkipSorting.getSelection()) {
				btnKeepUnsorted.setSelection(true);
				btnKeepUnsorted.setEnabled(false);
			} else {
				btnKeepUnsorted.setSelection(false);
				btnKeepUnsorted.setEnabled(true);
			}
		}
	};

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public ArrayList<String> getErrors() {
		return errors;
	}

}
