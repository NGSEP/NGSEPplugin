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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.ngsep.control.SampleData;
import net.sf.ngsep.control.SyncDetector;
import net.sf.ngsep.control.SyncMultisampleVariantsDetector;
import net.sf.ngsep.control.SyncThreadJobsPool;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import ngsep.discovery.MultisampleVariantsDetector;
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
 * @author Daniel Cruz, Jorge Duitama, Juan Camilo Quintero
 *
 */
public class MainVariantsDetector {

	public static final char BEHAVIOR_SINGLE = 'O';
	public static final char BEHAVIOR_MULTI_INDIVIDUAL = 'M';
	public static final char BEHAVIOR_MULTI_COMBINED = 'C';
	public static final char BEHAVIOR_WIZARD = 'W';
	protected Shell shell;
	private Display display;
	
	private String initialAlignmentsFile;
	private String outputPath;
	
	private List<SampleData> uniqueData=new ArrayList<SampleData>();
	
	
	private Button btnCancel;
	private Button btnProceed;
	
	private char behavior;


	private TabVDMainArgs t_VDMainArgs;
	private TabVDSNVArgs t_VDSNVArgs;
	private TabVDSVArgs t_VDSVArgs;


	public MainVariantsDetector(String outputPath, List<SampleData> uniqueParametersForSample, boolean individualAnalysis) {
		//multiVariants detection
		uniqueData=uniqueParametersForSample;
		this.outputPath = outputPath;
		if(individualAnalysis) {
			behavior = BEHAVIOR_MULTI_INDIVIDUAL;
		} else {
			behavior = BEHAVIOR_MULTI_COMBINED;
		}
	}

	public MainVariantsDetector(String alignmentsFile) throws IOException {
		//single variants detection
		if(!FieldValidator.isFileExistenceWithData(alignmentsFile)) throw new IOException("File "+alignmentsFile+" could not be opened or is empty");
		initialAlignmentsFile = alignmentsFile;
		behavior = BEHAVIOR_SINGLE;
	}

	/**
	 * Open the window.
	 */
	public void open() {

		display = Display.getDefault();
		//creating screen
		shell = new Shell(display);
		if(behavior==BEHAVIOR_MULTI_INDIVIDUAL) {
			shell.setText("Multi Variants Detector. Individual analysis per file");
		} else if(behavior == BEHAVIOR_MULTI_COMBINED) {
			shell.setText("Multi Variants Detector. Combined analysis");
		} else {
			shell.setText("Variants Detector. Single sample analysis");
		}
		shell.setLayout(new GridLayout());
		shell.setLocation(150, 200);
		createTabs(shell);
		
		if (behavior == BEHAVIOR_SINGLE) {
			t_VDMainArgs.setInitialAlignmentsFile(initialAlignmentsFile);
		} else {
			t_VDMainArgs.setSuggestedOutputPath(outputPath);
		}
		t_VDMainArgs.paint();
		t_VDSNVArgs.paint();
		if (behavior!= BEHAVIOR_MULTI_COMBINED) {
			t_VDSVArgs.paint();
		}

		// Create a top part
		Composite topComposite = new Composite( shell, SWT.NONE );
		topComposite.setLayout( new GridLayout(3, true));
		GridData gd_topComposite = new GridData( SWT.CENTER, SWT.TOP, true, false );
		gd_topComposite.widthHint = 359;
		topComposite.setLayoutData( gd_topComposite);
		btnProceed = new Button(topComposite, SWT.NONE);
		GridData gd_btnStart = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnStart.widthHint = 105;
		btnProceed.setLayoutData(gd_btnStart);
		btnProceed.setText("Find Variants");
		btnProceed.setBounds(370, 294, 110, 25);
		btnProceed.addSelectionListener(new SelectionAdapter() {
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
		shell.setSize(800, 600);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	protected void createTabs(Composite parent) {
		final CTabFolder tabFolder = new CTabFolder(parent, SWT.BORDER );
		GridData gd_tabFolder = new GridData( GridData.FILL_BOTH );
		gd_tabFolder.heightHint = 649;
		tabFolder.setLayoutData( gd_tabFolder);
		tabFolder.setSimple( false );


		//VD main
		CTabItem tabVDMainArguments = new CTabItem( tabFolder, SWT.NONE );
		tabVDMainArguments.setText("VD Main arguments");
		t_VDMainArgs = new TabVDMainArgs(tabFolder, SWT.NONE, behavior);
		tabVDMainArguments.setControl(t_VDMainArgs);

		//VD SNV
		CTabItem tabVDSNVArguments = new CTabItem( tabFolder, SWT.NONE );
		tabVDSNVArguments.setText("VD SNV options");
		t_VDSNVArgs = new TabVDSNVArgs(tabFolder, SWT.NONE, behavior);
		tabVDSNVArguments.setControl(t_VDSNVArgs);

		//VD SV
		if(behavior!= BEHAVIOR_MULTI_COMBINED) {
			CTabItem tabVDSVArguments = new CTabItem( tabFolder, SWT.NONE );
			tabVDSVArguments.setText("VD SV options");
			t_VDSVArgs = new TabVDSVArgs(tabFolder, SWT.NONE);
			tabVDSVArguments.setControl(t_VDSVArgs);
		}
	}

	public void process() {
		
		Map<String,Object> commandsVDMain = t_VDMainArgs.getParams();

		if (commandsVDMain==null) {
			FieldValidator.paintErrors(t_VDMainArgs.getErrors(), shell, "VD Main Args");
			return;
		}

		Map<String,Object> commandsVDSNV = t_VDSNVArgs.getParams();

		if (commandsVDSNV==null) {
			FieldValidator.paintErrors(t_VDSNVArgs.getErrors(), shell, "VD SNV Args");
			return;
		}
		
		commandsVDMain.putAll(commandsVDSNV);

		if(behavior!= BEHAVIOR_MULTI_COMBINED) {
			Map<String,Object> commandsVDSV = null;
			commandsVDSV = t_VDSVArgs.getParams();

			if (commandsVDSV==null) {
				FieldValidator.paintErrors(t_VDSVArgs.getErrors(), shell, "VD SV Args");
				return;
			}
			commandsVDMain.putAll(commandsVDSV);
		}
		
		//Load reference
		try {
			String refGenomeFile = (String) commandsVDMain.remove("ReferenceFile");
			ReferenceGenome genome = ReferenceGenomesFactory.getInstance().getGenome(refGenomeFile, shell);
			commandsVDMain.put("Genome", genome);
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.openError(shell, " Error loading reference genome",e.getMessage());
			return;
		}
		if (behavior==BEHAVIOR_SINGLE) {
			VariantsDetector vd = new VariantsDetector();

			if(commandsVDMain.get("aliFile")!=null){
				vd.setAlignmentsFile((String) commandsVDMain.remove("aliFile"));
			}
			if(commandsVDMain.get("sampleId")!=null){
				vd.setSampleId((String) commandsVDMain.remove("sampleId"));
			}

			String outputFilePrefix = (String) commandsVDMain.remove("destFile");
			if (commandsVDMain.get("FindSNVs")!=null) {
				if(outputFilePrefix!=null){
					String vcfFileName = outputFilePrefix+ ".vcf";
					vd.setOutVarsFilename(vcfFileName);
				}
			}
			
			if(commandsVDMain.get("RunRDAnalysis")!=null || commandsVDMain.get("FindRepeats")!=null || commandsVDMain.get("RunRPAnalysis")!=null){
				vd.setOutSVFilename(outputFilePrefix+ "_SV.gff");
			}
			copyCommonParams(commandsVDMain,vd);
			SyncDetector job = new SyncDetector(vd.getSampleId());
			job.setLogName(outputFilePrefix + "_VD.log");
			job.setVd(vd);
			try {
				job.schedule();
			} catch (Exception e1) {
				MessageDialog.openError(shell, " Variants Detector Error",e1.getMessage());
				e1.printStackTrace();
			}
		}
		if (behavior==BEHAVIOR_MULTI_COMBINED) {
			MultisampleVariantsDetector multiVD = new MultisampleVariantsDetector();
			String outFilename = (String) commandsVDMain.remove("destFile");
			try {
				copyCommonParams(commandsVDMain,multiVD);
			} catch (Exception e) {
				MessageDialog.openError(shell, "Variants Detector Error",e.getMessage());
				e.printStackTrace();
				return;
			}
			multiVD.setOutFilename(outFilename);
			List<String> alignmentFiles = new ArrayList<>();
			for (SampleData sd:uniqueData) {
				alignmentFiles.add(sd.getSortedBamFile());
			}
			multiVD.setAlignmentFiles(alignmentFiles);
			SyncMultisampleVariantsDetector job = new SyncMultisampleVariantsDetector("Multisample variants detector");
			job.setInstance(multiVD);
			String logFilename = LoggingHelper.getLoggerFilename(outFilename, "MCVD");
			job.setLogName(logFilename);
			job.setNameProgressBar(new File(outFilename).getName());
			try {
				job.schedule();
			} catch (Exception e1) {
				MessageDialog.openError(shell, "Variants Detector Error",e1.getMessage());
				e1.printStackTrace();
				return;
			}
		}
		if(behavior==BEHAVIOR_MULTI_INDIVIDUAL){
			List<Job> jobs = new ArrayList<Job>();
			int numProc = (int)commandsVDMain.get("numProc");
			commandsVDMain.remove("numProc");
				
				
			for (SampleData sd:uniqueData) {
				VariantsDetector vdMulti = new VariantsDetector();
				//Set sample parameters
				vdMulti.setSampleId(sd.getSampleId());
				vdMulti.setAlignmentsFile(sd.getSortedBamFile());
				// Output files: vfc, covergae.stats, cnv, gff

				if (commandsVDMain.get("FindSNVs")!=null) {
					vdMulti.setOutVarsFilename(sd.getVcfFile());
				} else {
					sd.setVcfFile(null);
				}
				if (commandsVDMain.get("RunRDAnalysis")!=null || commandsVDMain.get("FindRepeats")!=null || commandsVDMain.get("RunRPAnalysis")!=null) {
					vdMulti.setOutSVFilename(sd.getSvFile());
				}
				if(commandsVDMain.get("KnownVariantsFile")!=null) {
					sd.setVariantsFile((String)commandsVDMain.get("KnownVariantsFile"));
				}
				
				copyCommonParams(commandsVDMain,vdMulti);	
				sd.setReferenceFile(vdMulti.getReferenceFile());
				SyncDetector job = new SyncDetector(sd.getSampleId());
				job.setVd(vdMulti);
				jobs.add(job);
			}
			if(jobs.size()>0) {
				SyncThreadJobsPool threadsVD = new SyncThreadJobsPool(numProc,jobs);
				Thread syncVD = new Thread(threadsVD);
				syncVD.start();
			}
		}
		MessageDialog.openInformation(shell, "Variant Detection is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
		shell.dispose();
	}
	
	public static void copyCommonParams(Map<String, Object> commonUserParameters,Object program) {
		for(String key:commonUserParameters.keySet()) {
			Object o = commonUserParameters.get(key);
			try {
				Method m = program.getClass().getMethod("set"+key, o.getClass());
				m.invoke(program, o);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static String extractPrefix(String samFile){
		int index = samFile.lastIndexOf(".");
		if(index <0) return samFile;
		String nameBam= samFile.substring(0,index);
		if (nameBam.contains("Sorted")) {
			nameBam = nameBam.substring(0,samFile.lastIndexOf("Sorted") - 1);
			return nameBam;
		} else if (nameBam.contains("sorted")) {
			nameBam = nameBam.substring(0,samFile.lastIndexOf("sorted") - 1);
			return nameBam;
		} else {
			return nameBam;
		}
	}


}
