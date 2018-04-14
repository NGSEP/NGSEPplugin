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
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.ngsep.control.SampleData;
import net.sf.ngsep.control.SyncDetector;
import net.sf.ngsep.control.SyncThreadJobsPool;
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
 * @author Daniel Cruz, Jorge Duitama, Juan CAmilo Quintero
 *
 */
public class MainVariantsDetector {

	public static final char SOURCE_SINGLE = 'O';
	public static final char SOURCE_MULTI = 'M';
	public static final char SOURCE_WIZARD = 'W';
	protected Shell shell;
	private boolean isMulti=false;
	private List<SampleData> uniqueData=new ArrayList<SampleData>();
	private String outputDirectory;
	private Display display;
	private Button btnCancel;
	private Button btnProceed;
	private File initialAlignmentsFile;
	private char source;


	private TabVDMainArgs t_VDMainArgs;
	private TabVDSNVArgs t_VDSNVArgs;
	private TabVDSVArgs t_VDSVArgs;


	public MainVariantsDetector(String outputDirectory, List<SampleData> uniqueParametersForSample) {
		//multiVariants detection
		this.isMulti=true;
		uniqueData=uniqueParametersForSample;
		this.outputDirectory = outputDirectory;
		source = SOURCE_MULTI;
	}

	public MainVariantsDetector(String alignmentsFile) throws IOException {
		//single variants detection
		this.isMulti=false;
		if(!FieldValidator.isFileExistenceWithData(alignmentsFile)) throw new IOException("File "+alignmentsFile+" could not be opened or is empty");
		initialAlignmentsFile = new File(alignmentsFile);
		this.outputDirectory = initialAlignmentsFile.getParentFile().getAbsolutePath();
		source = SOURCE_SINGLE;
	}

	/**
	 * Open the window.
	 */
	public void open() {

		display = Display.getDefault();
		//creating screen
		shell = new Shell(display);
		if(isMulti){
			shell.setText("Multi Variants Detector");
		} else{
			shell.setText("Variants Detector");
		}
		shell.setLayout(new GridLayout());
		shell.setLocation(150, 200);
		createTabs(shell);
		t_VDMainArgs.setOutputDirectory(outputDirectory);
		if(!isMulti){
			t_VDMainArgs.setInitialAlignmentsFile(initialAlignmentsFile);
		}
		t_VDMainArgs.paint();
		t_VDSNVArgs.paint();
		t_VDSVArgs.paint();

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
		t_VDMainArgs = new TabVDMainArgs(tabFolder, SWT.NONE, source);
		tabVDMainArguments.setControl(t_VDMainArgs);

		//VD SNV
		CTabItem tabVDSNVArguments = new CTabItem( tabFolder, SWT.NONE );
		tabVDSNVArguments.setText("VD SNV options");
		t_VDSNVArgs = new TabVDSNVArgs(tabFolder, SWT.NONE, source);
		tabVDSNVArguments.setControl(t_VDSNVArgs);

		//VD SV
		CTabItem tabVDSVArguments = new CTabItem( tabFolder, SWT.NONE );
		tabVDSVArguments.setText("VD SV options");
		t_VDSVArgs = new TabVDSVArgs(tabFolder, SWT.NONE, source);
		tabVDSVArguments.setControl(t_VDSVArgs);

	}

	public void process() {
		
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
		
		//Load reference
		try {
			String refGenomeFile = (String) commandsVDMain.get("ReferenceFile");
			ReferenceGenome genome = ReferenceGenomesFactory.getInstance().getGenome(refGenomeFile, shell);
			commandsVDMain.put("Genome", genome);
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.openError(shell, " Error loading reference genome",e.getMessage());
			return;
		}
		try {
			if(source==SOURCE_SINGLE){
				VariantsDetector vd;
				
				vd = new VariantsDetector();

				if(commandsVDMain.get("aliFile")!=null){
					vd.setAlignmentsFile((String) commandsVDMain.get("aliFile"));
					commandsVDMain.remove("aliFile");
										
				}

				if(commandsVDMain.get("sampleId")!=null){
					vd.setSampleId((String) commandsVDMain.get("sampleId"));
					commandsVDMain.remove("sampleId");
				}

				
				String vcfFileName = null;
				if (vd.isFindSNVs()) {
					if(commandsVDMain.get("destFile")!=null){
						vcfFileName = (String)commandsVDMain.get("destFile")+ ".vcf";
						
						System.out.println(vcfFileName);
						vd.setOutVars(new PrintStream(vcfFileName));
					}
					
					
				}
				
				if(commandsVDMain.get("RunRDAnalysis")!=null || commandsVDMain.get("FindRepeats")!=null || commandsVDMain.get("RunRPAnalysis")!=null){
					vd.setOutStructural(new PrintStream((String)commandsVDMain.get("destFile")+ "_SV.gff"));
				}
				
				SyncDetector job = new SyncDetector(vd.getSampleId());
				SampleData sd = new SampleData();
				
				sd.setVdLogFile((String)commandsVDMain.get("destFile") + "_VD.log");
				
				commandsVDMain.remove("destFile");


				
				
				copyCommonParams(commandsVDMain,vd);
				
				
				sd.setReferenceFile(vd.getReferenceFile());
				sd.setSampleId(vd.getSampleId());
				sd.setSortedBamFile(vd.getAlignmentsFile());
				if(vcfFileName!=null)sd.setVcfFile(vcfFileName);

				if(commandsVDMain.get("KnownVariantsFile")!=null){
					sd.setVariantsFile((String)(commandsVDMain.get("KnownVariantsFile")));
				}
				
				job.setSampleData(sd);
				job.setVd(vd);
				job.schedule();

			}
			
			if(source==SOURCE_MULTI){
				List<Job> jobs = new ArrayList<Job>();
				
				int numProc = 1;
				
				numProc = (Integer)commandsVDMain.get("numProc");
				
				commandsVDMain.remove("numProc");
				
				
				for (SampleData sd:uniqueData) {
					VariantsDetector vdMulti = new VariantsDetector();
					//Set sample parameters
					vdMulti.setSampleId(sd.getSampleId());
					vdMulti.setAlignmentsFile(sd.getSortedBamFile());
					// Output files: vfc, covergae.stats, cnv, gff
					String vcfFile = sd.getVcfFile();

					if (commandsVDMain.get("FindSNVs")==null) {
						vdMulti.setOutVars(new PrintStream(vcfFile));
					} else {
						sd.setVcfFile(null);
					}
					if (commandsVDMain.get("RunRDAnalysis")!=null || commandsVDMain.get("FindRepeats")!=null || commandsVDMain.get("RunRPAnalysis")!=null) {
						vdMulti.setOutStructural(new PrintStream(sd.getSvFile()));
					}
					if(commandsVDMain.get("KnownVariantsFile")!=null) {
						sd.setVariantsFile((String)commandsVDMain.get("KnownVariantsFile"));
					}
					
					copyCommonParams(commandsVDMain,vdMulti);	
					sd.setReferenceFile(vdMulti.getReferenceFile());
					SyncDetector job = new SyncDetector(sd.getSampleId());
					job.setSampleData(sd);
					job.setVd(vdMulti);
					jobs.add(job);
				}
				if(jobs.size()>0) {
					SyncThreadJobsPool threadsVD = new SyncThreadJobsPool(numProc,jobs);
					Thread syncVD = new Thread(threadsVD);
					syncVD.start();
				}
				
			}
			
			MessageDialog.openInformation(shell, "Variant Detection is running",LoggingHelper.MESSAGE_PROGRESS_BAR + " and check if the file 'HistoryFileVCF.ini' was created in the project path");
			shell.dispose();
			

		} catch (Exception e1) {
			MessageDialog.openError(shell, " Variants Detector Error",e1.getMessage());
			e1.printStackTrace();
		}	

	}
	
	public static void copyCommonParams(Map<String, Object> commonUserParameters,VariantsDetector vd) {
		for(String key:commonUserParameters.keySet()) {
			Object o = commonUserParameters.get(key);
			try {
				Method m = vd.getClass().getMethod("set"+key, o.getClass());
				m.invoke(vd, o);
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
