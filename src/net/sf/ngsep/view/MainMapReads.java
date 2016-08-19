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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.ngsep.control.SampleData;
import net.sf.ngsep.control.SyncMapRead;
import net.sf.ngsep.control.SyncThreadJobsPool;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Daniel Cruz, Juan Camilo Quintero
 *
 */
public class MainMapReads {

	protected Shell shell;
	private Display display;
	private List<SampleData> listSamples=new ArrayList<SampleData>();
	// Fastq files for non multiple case
	private File fastqFile1;
	private File fastqFile2;
	private boolean isMultiple = false;
	private String outputDirectory;
	private char source;

	private Button btnStart;
	private Button btnCancel;


	private TabMapMainArgs t_MapMainArgs;
	private TabMapAliArgs t_MapAliArgs;
	private TabMapSortArgs t_MapSortArgs;


	/**
	 * Launch the application.
	 * @param args
	 * @throws IOException 
	 */

	public MainMapReads(String fastq1, String fastq2) throws IOException {

		if(!FieldValidator.isFileExistenceWithData(fastq1)) throw new IOException("File: "+fastq1+" could not be opened or is empty");

		fastqFile1 = new File(fastq1);

		this.outputDirectory = fastqFile1.getParentFile().getAbsolutePath();


		if(fastq2!=null){
			fastqFile2=new File(fastq2);
		}

		source = 'O'; //One sample

		// TODO Auto-generated constructor stub
	}

	public MainMapReads (String outputDirectory, List<SampleData> samples) {
		this.outputDirectory = outputDirectory;
		listSamples = samples;
		isMultiple = true;
		source = 'M'; //Multi sample
	}

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();

		shell = new Shell(display);
		if(isMultiple){
			shell.setText("Multi Mapping");
		} else{
			shell.setText("Map Reads");
		}
		shell.setLayout(new GridLayout());
		shell.setLocation(150, 200);
		createTabs(shell);

		t_MapMainArgs.setOutputDirectory(outputDirectory);

		if(!isMultiple){
			t_MapMainArgs.setFastqFile1(fastqFile1);
			t_MapMainArgs.setFastqFile2(fastqFile2);
		}

		t_MapMainArgs.paint();
		t_MapAliArgs.paint();
		t_MapSortArgs.paint();



		// Create a top part
		Composite topComposite = new Composite( shell, SWT.NONE );
		topComposite.setLayout( new GridLayout(3, true));
		GridData gd_topComposite = new GridData( SWT.CENTER, SWT.TOP, true, false );
		gd_topComposite.widthHint = 359;
		topComposite.setLayoutData( gd_topComposite);
		btnStart = new Button(topComposite, SWT.NONE);
		GridData gd_btnStart = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnStart.widthHint = 105;
		btnStart.setLayoutData(gd_btnStart);
		btnStart.setText("Map Reads");
		btnStart.setBounds(370, 294, 110, 25);
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				process();
			}
		});
		new Label(topComposite, SWT.NONE);
		btnCancel = new Button(topComposite, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.widthHint = 108;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");
		btnCancel.setBounds(340, 294, 110, 25);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		shell.setSize(900, 567);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}


	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createTabs(Composite parent) {
		final CTabFolder tabFolder = new CTabFolder(parent, SWT.BORDER );
		GridData gd_tabFolder = new GridData( GridData.FILL_BOTH );
		gd_tabFolder.heightHint = 649;
		tabFolder.setLayoutData( gd_tabFolder);
		tabFolder.setSimple( false );


		/*Source parameter  O = One sample
		 *					M = Multi
		 *					W = Wizard 
		 */

		//Mapping main
		CTabItem tabMapMainArguments = new CTabItem( tabFolder, SWT.NONE );
		tabMapMainArguments.setText("Map Main arguments");
		t_MapMainArgs = new TabMapMainArgs(tabFolder, SWT.NONE, source);
		tabMapMainArguments.setControl(t_MapMainArgs);

		//Mapping Ali
		CTabItem tabMapAliArguments = new CTabItem( tabFolder, SWT.NONE );
		tabMapAliArguments.setText("Map Alignment options");
		t_MapAliArgs = new TabMapAliArgs(tabFolder, SWT.NONE, source);
		tabMapAliArguments.setControl(t_MapAliArgs);

		//Mapping Sort
		CTabItem tabMapSortArguments = new CTabItem( tabFolder, SWT.NONE );
		tabMapSortArguments.setText("Map Sorting options");
		t_MapSortArgs = new TabMapSortArgs(tabFolder, SWT.NONE, source);
		tabMapSortArguments.setControl(t_MapSortArgs);

	}

	/**
	 * 
	 */
	public void process() {
		// TODO Auto-generated method stub
		try {
			//Mapping			

			Map<String,Object> commandsMapMain = null;
			commandsMapMain = t_MapMainArgs.getParams();

			if (commandsMapMain==null) {
				FieldValidator.paintErrors(t_MapMainArgs.getErrors(), shell, "Map Main Args");
				return;
			}

			Map<String,Object> commandsMapAli = null;
			commandsMapAli = t_MapAliArgs.getParams();

			if (commandsMapAli==null) {
				FieldValidator.paintErrors(t_MapAliArgs.getErrors(), shell, "Map Alignment Args");
				return;
			}

			Map<String,Object> commandsMapSort = null;
			commandsMapSort = t_MapSortArgs.getParams();

			if (commandsMapSort==null) {
				FieldValidator.paintErrors(t_MapSortArgs.getErrors(), shell, "Map Alignment Args");
				return;
			}

			commandsMapMain.putAll(commandsMapAli);
			commandsMapMain.putAll(commandsMapSort);

			if(source=='O'){
				SyncMapRead syncMapRead = new SyncMapRead("Map reads process");
				syncMapRead.setMapCommandArray(commandsMapMain);
				//Mapping Sort	
				if(commandsMapMain.get("skipSortCMD")!=null&&(Boolean)commandsMapMain.get("skipSortCMD")==true){
					syncMapRead.setSkipSorting(true);
				}

				if(commandsMapMain.get("keepUnsortedCMD")!=null&&(Boolean)commandsMapMain.get("keepUnsortedCMD")==true){
					syncMapRead.setKeepUnSorted(true);
				}

				syncMapRead.setOutputDirectory(outputDirectory);
				syncMapRead.setSampleData((SampleData)(commandsMapMain.get("singleSample")));
				syncMapRead.schedule();
			}

			if(source=='M'){

				List<Job> jobs = new ArrayList<Job>();
				for (int i = 0; i < listSamples.size(); i++) {
					SyncMapRead process = new SyncMapRead("Read mapping sample " + listSamples.get(i).getSampleId());
					process.setMapCommandArray(commandsMapMain);
					process.setSampleData(listSamples.get(i));
					if(commandsMapMain.get("skipSortCMD")!=null&&(Boolean)commandsMapMain.get("skipSortCMD")==true){
						process.setSkipSorting(true);
					}

					if(commandsMapMain.get("keepUnsortedCMD")!=null&&(Boolean)commandsMapMain.get("keepUnsortedCMD")==true){
						process.setKeepUnSorted(true);
					}

					process.setOutputDirectory(outputDirectory);
					jobs.add(process);
				}
				if(jobs.size()>0) {
					SyncThreadJobsPool threadsMapping = new SyncThreadJobsPool((Integer)commandsMapMain.get("numProc"),jobs);
					Thread syncMapping = new Thread(threadsMapping);
					syncMapping.start();
				}
			}
			MessageDialog.openInformation(shell, "Map Reads is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
			shell.setEnabled(false);
			shell.dispose();
		} catch (Exception e) {
			MessageDialog.openError(shell, "Map Reads Error", e.getMessage());
			e.printStackTrace();
			return;
		}

	}

	public  static List<Integer> calculateDifferences(String file1, String file2) {
		int i = 0;
		List<Integer> differences = new ArrayList<Integer>();
		while (i < file1.length() && i < file2.length()) {
			char letterFirstElement = file1.charAt(i);
			char letterSecondElement = file2.charAt(i);
			if (letterFirstElement != letterSecondElement) {
				differences.add(i);
			}
			i++;
		}
		return differences;
	}


}
