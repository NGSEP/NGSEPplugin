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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.ngsep.control.SampleData;
import net.sf.ngsep.control.SyncDetector;
import net.sf.ngsep.control.SyncMapRead;
import net.sf.ngsep.control.SyncThreadAllWizard;
import net.sf.ngsep.control.SyncWizardMapVD;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import ngsep.discovery.VariantsDetector;
import ngsep.genome.ReferenceGenome;

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
 * 
 * @author Daniel Cruz
 *
 */
public class MainWizard {

	//General variables 
	private Shell shell;
	private Display display;
	private List<SampleData> listSamples=new ArrayList<SampleData>();

	private Button btnStart;
	private Button btnCancel;

	private String currentFolder;
	private String mappingFolder;
	private String variantsFolder;

	private TabMapMainArgs t_MapMainArgs;
	private TabMapAliArgs t_MapAliArgs;
	private TabMapSortArgs t_MapSortArgs;

	private TabVDMainArgs t_VDMainArgs;
	private TabVDSNVArgs t_VDSNVArgs;
	private TabVDSVArgs t_VDSVArgs;

	private int numProcAssig;


	private String varPrefix;
	
	private boolean stopFail;


	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */

	public MainWizard(String currentFolder, String mappingFolder, String variantsFolder ) {
		this.currentFolder = currentFolder;
		this.mappingFolder = mappingFolder;
		this.variantsFolder = variantsFolder;

	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		display = Display.getDefault();
		shell = new Shell(display);

		shell.setText("NGSEP Wizard");

		shell.setLayout(new GridLayout());
		shell.setLocation(150, 200);

		createTabs(shell);
		t_MapMainArgs.setOutputDirectory(mappingFolder);
		t_MapMainArgs.paint();
		t_MapAliArgs.paint();
		t_MapSortArgs.paint();

		t_VDMainArgs.setOutputDirectory(variantsFolder);
		t_VDMainArgs.paint();
		t_VDSNVArgs.paint();
		t_VDSVArgs.paint();


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
		btnStart.setText("Go Wizard");
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
	 * Creates the tabs.
	 * @param parent is the objects composite 
	 * @wbp.parser.entryPoint
	 */
	public void createTabs( Composite parent ) {
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
		t_MapMainArgs = new TabMapMainArgs(tabFolder, SWT.NONE, 'W');
		tabMapMainArguments.setControl(t_MapMainArgs);

		//Mapping Ali
		CTabItem tabMapAliArguments = new CTabItem( tabFolder, SWT.NONE );
		tabMapAliArguments.setText("Map Alignment options");
		t_MapAliArgs = new TabMapAliArgs(tabFolder, SWT.NONE, 'W');
		tabMapAliArguments.setControl(t_MapAliArgs);

		//Mapping Sort
		CTabItem tabMapSortArguments = new CTabItem( tabFolder, SWT.NONE );
		tabMapSortArguments.setText("Map Sorting options");
		t_MapSortArgs = new TabMapSortArgs(tabFolder, SWT.NONE, 'W');
		tabMapSortArguments.setControl(t_MapSortArgs);

		//VD main
		CTabItem tabVDMainArguments = new CTabItem( tabFolder, SWT.NONE );
		tabVDMainArguments.setText("VD Main arguments");
		t_VDMainArgs = new TabVDMainArgs(tabFolder, SWT.NONE, 'W');
		tabVDMainArguments.setControl(t_VDMainArgs);

		//VD SNV
		CTabItem tabVDSNVArguments = new CTabItem( tabFolder, SWT.NONE );
		tabVDSNVArguments.setText("VD SNV options");
		t_VDSNVArgs = new TabVDSNVArgs(tabFolder, SWT.NONE, 'W');
		tabVDSNVArguments.setControl(t_VDSNVArgs);

		//VD SV
		CTabItem tabVDSVArguments = new CTabItem( tabFolder, SWT.NONE );
		tabVDSVArguments.setText("VD SV options");
		t_VDSVArgs = new TabVDSVArgs(tabFolder, SWT.NONE, 'W');
		tabVDSVArguments.setControl(t_VDSVArgs);




	}


	/**
	 * @wbp.parser.entryPoint
	 */
	public void process() {
		
			
		

		List<Job> jobsWizard = new ArrayList<Job>();

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


		//VD		


		Map<String,Object> commandsVDMain = null;
		commandsVDMain = t_VDMainArgs.getParams();

		if (commandsVDMain==null) {
			FieldValidator.paintErrors(t_VDMainArgs.getErrors(), shell, "VD Main Args");
			return;
		}

		Map<String,Object> commandsVDSNV = null;
		commandsVDSNV = t_VDSNVArgs.getParams();

		if (commandsVDSNV==null) {
			FieldValidator.paintErrors(t_VDSNVArgs.getErrors(), shell, "VD SNV Args");
			return;
		}

		Map<String,Object> commandsVDSV = null;
		commandsVDSV = t_VDSVArgs.getParams();

		if (commandsVDSV==null) {
			FieldValidator.paintErrors(t_VDSVArgs.getErrors(), shell, "VD SV Args");
			return;
		}

		commandsVDMain.putAll(commandsVDSNV);
		commandsVDMain.putAll(commandsVDSV);
			
		try {
			//Load reference
			String refGenomeFile = (String) commandsVDMain.get("ReferenceFile");
			ReferenceGenome genome = ReferenceGenomesFactory.getInstance().getGenome(refGenomeFile, shell);
			commandsVDMain.put("Genome", genome);
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.openError(shell, "Error loading reference file",e.getMessage());
			return;
		}

			//  Use Case Execution
		try{	
			//validate if the minimun quality genotyping applies for discovery and genotyping
			Boolean bothGQ = (Boolean)commandsVDMain.get("UseGQboth");
			if(bothGQ!=null){
				commandsVDMain.remove("UseGQboth");
			}

			for (SampleData sampleTmp:listSamples) {

				//Mapping				
				SyncMapRead mapJobSample = new SyncMapRead("Read mapping sample " + sampleTmp.getSampleId());
				mapJobSample.setMapCommandArray(commandsMapMain);
				mapJobSample.setSampleData(sampleTmp);
				mapJobSample.setOutputDirectory(mappingFolder);

				//Mapping Sort	
				if(commandsMapMain.get("skipSortCMD")!=null&&(Boolean)commandsMapMain.get("skipSortCMD")==true){
					mapJobSample.setSkipSorting(true);
				}

				if(commandsMapMain.get("keepUnsortedCMD")!=null&&(Boolean)commandsMapMain.get("keepUnsortedCMD")==true){
					mapJobSample.setKeepUnSorted(true);
				}

				//VD
				VariantsDetector vdSample = new VariantsDetector();
				vdSample.setSampleId(sampleTmp.getSampleId());
				vdSample.setAlignmentsFile(sampleTmp.getSortedBamFile());
				// Output files: vfc, gff
				String vcfFile = sampleTmp.getVcfFile();

				if(commandsVDMain.get("FindSNVs")==null){
					vdSample.setOutVars(new PrintStream(vcfFile));
				} else {
					sampleTmp.setVcfFile(null);
					sampleTmp.setVcfFileGT(null);
				}

				if(commandsVDMain.get("RunRDAnalysis")==null||commandsVDMain.get("FindRepeats")==null||commandsVDMain.get("RunRPAnalysis")==null){
					vdSample.setOutStructural(new PrintStream(sampleTmp.getSvFile()));
				}else{
					sampleTmp.setSvFile(null);
				}

				MainVariantsDetector.copyCommonParams(commandsVDMain,vdSample);
				sampleTmp.setReferenceFile(vdSample.getReferenceFile());
				SyncDetector vdJobSample = new SyncDetector(sampleTmp.getSampleId());
				vdJobSample.setSampleData(sampleTmp);
				vdJobSample.setVd(vdSample);

				SyncWizardMapVD wiMapVD = new SyncWizardMapVD("Wizard Step 1: "+sampleTmp.getSampleId());
				wiMapVD.setSampleData(sampleTmp);
				wiMapVD.setMappingJob(mapJobSample);
				wiMapVD.setDetectingJob(vdJobSample);
				wiMapVD.setOutputDirectory(currentFolder);
				jobsWizard.add(wiMapVD);
			}

			if(jobsWizard.size()>0) {
				SyncThreadAllWizard allWizard = new SyncThreadAllWizard(currentFolder, variantsFolder);
				allWizard.setNumProcs(numProcAssig);
				allWizard.setJobsWizard(jobsWizard);

				allWizard.setVariantsPrefix(varPrefix);
				allWizard.setCommandsVD(commandsVDMain);
				allWizard.setStopFail(stopFail);
				
				if(bothGQ!=null){
					allWizard.setBothGQ(bothGQ);
				}
				
				Thread threadAllWizard = new Thread(allWizard);
				threadAllWizard.start();
			}
		} catch (Exception e1) {
			MessageDialog.openError(shell, "Wizard Error",e1.getMessage());
			e1.printStackTrace();
			return;
		}
		MessageDialog.openInformation(shell, "NGSEP Wizard is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
		shell.setEnabled(false);
		shell.dispose();


	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public String getCurrentFolder() {
		return currentFolder;
	}

	public void setCurrentFolder(String currentFolder) {
		this.currentFolder = currentFolder;
	}

	public List<SampleData> getListSamples() {
		return listSamples;
	}

	public void setListSamples(List<SampleData> listSamples) {
		this.listSamples = listSamples;
	}

	public int getNumProcAssig() {
		return numProcAssig;
	}

	public void setNumProcAssig(int numProcAssig) {
		this.numProcAssig = numProcAssig;
	}

	public String getVarPrefix() {
		return varPrefix;
	}

	public void setVarPrefix(String varPrefix) {
		this.varPrefix = varPrefix;
	}

	public boolean isStopFail() {
		return stopFail;
	}

	public void setStopFail(boolean stopFail) {
		this.stopFail = stopFail;
	}	

}
