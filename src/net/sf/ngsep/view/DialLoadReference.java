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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import ngsep.genome.ReferenceGenome;

/**
 * 
 * @author Jorge Duitama
 *
 */
public class DialLoadReference extends Dialog {
	private String file;
	private ReferenceGenome genome;
	private IOException exception;
	private boolean threadDone = false;
	
    private Composite progressBarComposite;//
    private Label message;//
    private ProgressBar progressBar = null; //
    private Shell shell; //
    
    protected volatile boolean isClosed = false;//closed state
    
    protected String processMessage = "Please wait......";
    protected String shellTitle = "Loading reference genome ...";
    protected int processBarStyle = SWT.SMOOTH; //process bar style
	
	public DialLoadReference(Shell parent) {
		super(parent);
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public ReferenceGenome getGenome() {
		return genome;
	}
	
	

	public IOException getException() {
		return exception;
	}
	public Object open() {
        createContents(); //create window
        shell.open();
        shell.layout();
        
        //start work
        Thread t = new ProcessThread();
        t.start();

        Display display = getParent().getDisplay();
        int i=0;
        while (!threadDone) {
        	if(i%100==0)progressBar.setSelection(i/100+1);
            if (!display.readAndDispatch()) {
                display.sleep();
            }
            i++;
        }
        shell.dispose();
        return genome;
    }
	protected void createContents() {
        shell = new Shell(getParent(), SWT.TITLE | SWT.PRIMARY_MODAL);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 10;

        shell.setLayout(gridLayout);
        shell.setSize(483, 181);
        shell.setText(shellTitle);

        final Composite composite = new Composite(shell, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
        composite.setLayout(new GridLayout());

        message = new Label(composite, SWT.NONE);
        message.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
        message.setText(processMessage);

        progressBarComposite = new Composite(shell, SWT.NONE);
        progressBarComposite.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        progressBarComposite.setLayout(new FillLayout());

        progressBar = new ProgressBar(progressBarComposite, processBarStyle);
        progressBar.setMaximum(100);

    }
	class ProcessThread extends Thread {
        public void run() {
        	threadDone = false;
            try {
				genome = new ReferenceGenome(file);
			} catch (IOException e) {
				genome = null;
				exception = e;
			}
            threadDone = true;
        }
    }
	
}
