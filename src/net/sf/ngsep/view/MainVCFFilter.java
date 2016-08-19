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
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.FileHandler;

import net.sf.ngsep.control.SyncVCFFilter;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.genome.ReferenceGenome;
import ngsep.genome.io.SimpleGenomicRegionFileHandler;
import ngsep.transcriptome.Transcriptome;
import ngsep.vcf.VCFFilter;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Juan Camilo Quintero
 *
 */
public class MainVCFFilter {

	//General variables 
	protected Shell shell;
	private Display display;

	//File selected initially by the user
	private String selectedFile;

	//Action buttons
	private Button btnVariantsFilter;
	private Button btnCancel;
	//-----------------------	

	//Main arguments
	private Text txtFile;
	private Label lblOutputFile;
	private Text txtOutputFile;
	private Label lblMinDistance;
	private Text txtMinDistance;
	private Label lblMinQuality;
	private Text txtMinQuality;
	private Label lblMinCoverage;
	private Text txtMinCoverage;
	private Label lblMaf;
	private Label lblMinMaf;
	private Text txtMinMAF;
	private Label lblMaxMaf;
	private Text txtMaxMAF;
	private Label lblOH;
	private Label lblMinOH;
	private Text txtMinOH;
	private Label lblMaxOH;
	private Text txtMaxOH;
	private Label lblMinIndividualGenotyped;
	private Text txtMinIndividualGenotyped;
	private Label lblFilterRegionsFrom;
	private Text txtFilterRegions;
	private Button btnFilterRegions;
	
	private Label lblMaximunNumberOf;
	private Text txtMaxSamplesCnvs;
	private Text txtSelectRegionsFile;
	private Label lblSelectRegionsFile;
	private Button btnSelectRegionsFile;
	private Button btnKeepOnlyBi;
	private Button btnFilterInvariantSites;
	private Button btnFilterInvariantAlternative;
	private Button btnFilterInvariantReference;
	private Button btnFile;
	private Button btnOutputFile;
	private Label lblFile;
	//----------------------------------------

	//GCContent filter
	private Label lblReference;
	private Text txtReferenceFile;
	private Label lblMinGccontent;
	private Text txtMinGCContet;
	private Label lblMaxGccontent;
	private Text txtMaxGCContent;
	private Label lblGccontent;
	private Button btnReferenceFile;
	//----------------------------------------

	//functional filter
	private Label lblGeneName;
	private Text txtGeneName;
	private Label lblFunctionalRole;
	//private Combo cmbFctRole;
	private Table tabFunctionalRoles;
	
	//----------------------------------------

	//Sample Select
	private Text txtFilterSamples;
	private Button btnSamplesFile;
	private Combo cmbFilterSelect;

	//----------------------------------------
	
	// Default Parameters
	private static final int DEF_MIN_DISTANCE = 0;
	private static final double DEF_MIN_MAF = 0.0;
	private static final double DEF_MAX_MAF = 0.5;
	private static final double DEF_MIN_OH = 0.0;
	private static final double DEF_MAX_OH = 1.0;
	private static final int DEF_MIN_INDIVIDUALS = 1;
	private static final int DEF_MIN_QUALITY = 40;
	private static final int DEF_MIN_COVERAGE = 1;
	private static final double DEF_MIN_CG_CONTENT = 40.0;
	private static final double DEF_MAX_CG_CONTENT = 65.0;
	
	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
		shell = new Shell(display);
		shell.setText("Variants Filter VCF");
		shell.setLayout(new GridLayout());
		shell.setLocation(150, 200);
		createTabs(shell);

		// Create a top part
		Composite topComposite = new Composite( shell, SWT.NONE );
		topComposite.setLayout( new GridLayout(3, true));
		GridData gd_topComposite = new GridData( SWT.CENTER, SWT.TOP, true, false );
		gd_topComposite.widthHint = 359;
		topComposite.setLayoutData( gd_topComposite);
		btnVariantsFilter = new Button(topComposite, SWT.NONE);
		GridData gd_btnStart = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnStart.widthHint = 105;
		btnVariantsFilter.setLayoutData(gd_btnStart);
		btnVariantsFilter.setText("Filter");
		btnVariantsFilter.setBounds(370, 294, 110, 25);
		btnVariantsFilter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();
			}
		});	
		new Label(topComposite, SWT.NONE);
		btnCancel = new Button(topComposite, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.widthHint = 108;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setBounds(340, 294, 110, 25);	
		btnCancel.setText("Cancel");
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
	 * @param parent is the objets composite 
	 */
	public void createTabs( Composite parent ) {
		final CTabFolder tabFolder = new CTabFolder(parent, SWT.BORDER );
		GridData gd_tabFolder = new GridData( GridData.FILL_BOTH );
		gd_tabFolder.heightHint = 649;
		tabFolder.setLayoutData( gd_tabFolder);
		tabFolder.setSimple( false );
		CTabItem tabMainArguments = new CTabItem( tabFolder, SWT.NONE );
		tabMainArguments.setText("Main arguments");
		tabMainArguments.setControl(createContents(tabFolder, true, false,false,false));
		CTabItem tabInputOptions = new CTabItem( tabFolder, SWT.NONE );
		tabInputOptions.setText("GC Content filter");
		tabInputOptions.setControl(createContents(tabFolder,false,true,false,false));
		CTabItem tabSortingParameters= new CTabItem( tabFolder, SWT.NONE );
		tabSortingParameters.setText("Functional filter");
		tabSortingParameters.setControl(createContents(tabFolder,false,false,true,false));	
		//my Stuff
		CTabItem tabSelectingOptions= new CTabItem( tabFolder, SWT.NONE );
		tabSelectingOptions.setText("Sample Selection");
		tabSelectingOptions.setControl(createContents(tabFolder,false,false,false,true));
	}


	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	protected Composite createContents(Composite parent,boolean mainArguments,boolean gccontentFilter,boolean funtionalFilter, boolean sampleSelect) {
		MouseListenerNgsep mouse = new MouseListenerNgsep();
		Composite c = new Composite(parent, SWT.NONE );
		c.setLayout( new GridLayout(0, false ));
		if(mainArguments){
			//paramters the main arguments
			lblFile = new Label(c, SWT.NONE);
			lblFile.setBounds(10, 21, 115, 20);
			lblFile.setText("(*) File:");

			txtFile = new Text(c, SWT.BORDER);
			txtFile.setBounds(199, 21, 528, 21);
			txtFile.addMouseListener(mouse);

			btnFile = new Button(c, SWT.NONE);
			btnFile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile,txtFile);
				}
			});
			btnFile.setBounds(739, 21, 29, 23);
			btnFile.setText("...");

			lblOutputFile = new Label(c, SWT.NONE);
			lblOutputFile.setText("(*) Output File:");
			lblOutputFile.setBounds(10, 71, 115, 20);

			txtOutputFile = new Text(c, SWT.BORDER);
			txtOutputFile.setBounds(199, 71, 528, 21);
			txtOutputFile.addMouseListener(mouse);
			String suggestedOutPrefix = SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile);
			txtOutputFile.setText(suggestedOutPrefix+"_filter.vcf");

			btnOutputFile = new Button(c, SWT.NONE);
			btnOutputFile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, selectedFile,txtOutputFile);
				}
			});
			btnOutputFile.setText("...");
			btnOutputFile.setBounds(739, 71, 29, 23);

			lblFilterRegionsFrom = new Label(c, SWT.NONE);
			lblFilterRegionsFrom.setText("Filter Regions From File:");
			lblFilterRegionsFrom.setBounds(10, 121, 170, 20);

			txtFilterRegions = new Text(c, SWT.BORDER);
			txtFilterRegions.setBounds(199, 121, 528, 21);
			txtFilterRegions.addMouseListener(mouse);

			btnFilterRegions = new Button(c, SWT.NONE);
			btnFilterRegions.setText("...");
			btnFilterRegions.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtFilterRegions);
				}
			});
			btnFilterRegions.setBounds(739, 121, 29, 23);

			lblSelectRegionsFile = new Label(c, SWT.NONE);
			lblSelectRegionsFile.setText("Select Regions From File:");
			lblSelectRegionsFile.setBounds(10, 171, 170, 20);

			txtSelectRegionsFile = new Text(c, SWT.BORDER);
			txtSelectRegionsFile.setBounds(199, 171, 528, 21);

			btnSelectRegionsFile = new Button(c, SWT.NONE);
			btnSelectRegionsFile.setText("...");
			btnSelectRegionsFile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtSelectRegionsFile);
				}
			});
			btnSelectRegionsFile.setBounds(739, 171, 29, 23);

			lblMinDistance = new Label(c, SWT.NONE);
			lblMinDistance.setBounds(10, 221, 223, 32);
			lblMinDistance.setText("Min distance between variants:");

			txtMinDistance = new Text(c, SWT.BORDER);
			txtMinDistance.setBounds(239, 221, 40, 26);
			txtMinDistance.addMouseListener(mouse);
			txtMinDistance.setText(String.valueOf(DEF_MIN_DISTANCE));

			lblMaf = new Label(c, SWT.NONE);
			lblMaf.setText("MAF:");
			lblMaf.setBounds(10, 270, 84, 25);

			lblMinMaf = new Label(c, SWT.NONE);
			lblMinMaf.setText("Min:");
			lblMinMaf.setBounds(230, 270, 40, 25);

			txtMinMAF = new Text(c, SWT.BORDER);
			txtMinMAF.setBounds(280, 270, 50, 25);
			txtMinMAF.setText(String.valueOf(DEF_MIN_MAF));
			txtMinMAF.addMouseListener(mouse);

			lblMaxMaf = new Label(c, SWT.NONE);
			lblMaxMaf.setText("Max:");
			lblMaxMaf.setBounds(350, 270, 40, 25);

			txtMaxMAF = new Text(c, SWT.BORDER);
			txtMaxMAF.setBounds(400, 270, 50, 25);
			txtMaxMAF.setText(String.valueOf(DEF_MAX_MAF));
			txtMaxMAF.addMouseListener(mouse);
			
			lblOH = new Label(c, SWT.NONE);
			lblOH.setText("Observed Heterozygosity:");
			lblOH.setBounds(10, 320, 200, 25);
			
			lblMinOH = new Label(c, SWT.NONE);
			lblMinOH.setText("Min:");
			lblMinOH.setBounds(230, 320, 40, 25);
			
			txtMinOH = new Text(c, SWT.BORDER);
			txtMinOH.setBounds(280, 320, 50, 25);
			txtMinOH.setText(String.valueOf(DEF_MIN_OH));
			txtMinOH.addMouseListener(mouse);

			lblMaxOH = new Label(c, SWT.NONE);
			lblMaxOH.setText("Max:");
			lblMaxOH.setBounds(350, 320, 40, 25);

			txtMaxOH = new Text(c, SWT.BORDER);
			txtMaxOH.setBounds(400, 320, 50, 25);
			txtMaxOH.setText(String.valueOf(DEF_MAX_OH));
			txtMaxOH.addMouseListener(mouse);

			lblMinIndividualGenotyped = new Label(c, SWT.NONE);
			lblMinIndividualGenotyped.setText("Min number of samples genotyped: ");
			lblMinIndividualGenotyped.setBounds(10, 371, 280, 32);

			txtMinIndividualGenotyped = new Text(c, SWT.BORDER);
			txtMinIndividualGenotyped.setBounds(320, 371, 40, 26);
			txtMinIndividualGenotyped.setText(String.valueOf(DEF_MIN_INDIVIDUALS));
			txtMinIndividualGenotyped.addMouseListener(mouse);


			lblMaximunNumberOf = new Label(c, SWT.NONE);
			lblMaximunNumberOf.setText("Maximun number of samples with CNVS:");
			lblMaximunNumberOf.setBounds(10, 421, 283, 32);

			txtMaxSamplesCnvs = new Text(c, SWT.BORDER);
			txtMaxSamplesCnvs.setBounds(320, 421, 40, 26);
			txtMaxSamplesCnvs.addMouseListener(mouse);

			lblMinQuality = new Label(c, SWT.NONE);
			lblMinQuality.setText("Min Quality:");
			lblMinQuality.setBounds(500, 221, 149, 32);

			txtMinQuality = new Text(c, SWT.BORDER);
			txtMinQuality.setBounds(710, 221, 40, 26);
			txtMinQuality.setText(String.valueOf(DEF_MIN_QUALITY));
			txtMinQuality.addMouseListener(mouse);

			lblMinCoverage = new Label(c, SWT.NONE);
			lblMinCoverage.setText("Min Coverage:");
			lblMinCoverage.setBounds(500, 271, 149, 32);

			txtMinCoverage = new Text(c, SWT.BORDER);
			txtMinCoverage.setBounds(710, 271, 40, 26);
			txtMinCoverage.setText(String.valueOf(DEF_MIN_COVERAGE));
			txtMinCoverage.addMouseListener(mouse);

			btnKeepOnlyBi = new Button(c, SWT.CHECK);
			btnKeepOnlyBi.setText("Keep only bi allelic SNVs");
			btnKeepOnlyBi.setBounds(500, 300, 238, 32);

			btnFilterInvariantSites = new Button(c, SWT.CHECK);
			btnFilterInvariantSites.setText("Filter Invariant Sites");
			btnFilterInvariantSites.setBounds(500, 330, 238, 32);

			btnFilterInvariantAlternative = new Button(c, SWT.CHECK);
			btnFilterInvariantAlternative.setText("Filter Invariant Alternative");
			btnFilterInvariantAlternative.setBounds(500, 360, 238, 32);

			btnFilterInvariantReference = new Button(c, SWT.CHECK);
			btnFilterInvariantReference.setText("Filter Invariant Reference");
			btnFilterInvariantReference.setBounds(500, 390, 238, 32);

			if (selectedFile != null && !selectedFile.equals("")) {
				txtFile.setText(selectedFile);
			}
		}
		//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

		if(gccontentFilter){
			//paramters the gccontent filter
			lblReference = new Label(c, SWT.NONE);
			lblReference.setText("Reference File:");
			lblReference.setBounds(10, 21, 115, 27);

			txtReferenceFile = new Text(c, SWT.BORDER);
			txtReferenceFile.setBounds(199, 21, 528, 26);
			txtReferenceFile.addMouseListener(mouse);

			btnReferenceFile = new Button(c, SWT.NONE);
			btnReferenceFile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtReferenceFile);
				}
			});
			btnReferenceFile.setText("...");
			btnReferenceFile.setBounds(739, 21, 29, 23);

			lblGccontent = new Label(c, SWT.NONE);
			lblGccontent.setText("GC Content:");
			lblGccontent.setBounds(10, 71, 84, 27);

			lblMinGccontent = new Label(c, SWT.NONE);
			lblMinGccontent.setText("Min:");
			lblMinGccontent.setBounds(119, 71, 37, 26);

			txtMinGCContet = new Text(c, SWT.BORDER);
			txtMinGCContet.setBounds(174, 71, 42, 26);
			txtMinGCContet.setText(String.valueOf(DEF_MIN_CG_CONTENT));
			txtMinGCContet.addMouseListener(mouse);

			lblMaxGccontent = new Label(c, SWT.NONE);
			lblMaxGccontent.setText("Max:");
			lblMaxGccontent.setBounds(242, 71, 37, 29);

			txtMaxGCContent = new Text(c, SWT.BORDER);
			txtMaxGCContent.setBounds(292, 71, 42, 26);
			txtMaxGCContent.setText(String.valueOf(DEF_MAX_CG_CONTENT));
			txtMaxGCContent.addMouseListener(mouse);
		}
		//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

		if(funtionalFilter){
			//paramters the functional filter
			lblGeneName = new Label(c, SWT.NONE);
			lblGeneName.setText("Gene name:");
			lblGeneName.setBounds(10, 21, 115, 29);

			txtGeneName = new Text(c, SWT.BORDER);
			txtGeneName.setBounds(155, 21, 160, 26);
			txtGeneName.addMouseListener(mouse);

			lblFunctionalRole = new Label(c, SWT.NONE);
			lblFunctionalRole.setText("Functional Role:");
			lblFunctionalRole.setBounds(10, 71, 115, 29);

			String itemsRole[] = {Transcriptome.ANNOTATION_INTRON,Transcriptome.ANNOTATION_INTERGENIC,Transcriptome.ANNOTATION_5P_UTR,Transcriptome.ANNOTATION_3P_UTR,Transcriptome.ANNOTATION_UPSTREAM, Transcriptome.ANNOTATION_DOWNSTREAM,Transcriptome.ANNOTATION_NONCODINGRNA, Transcriptome.ANNOTATION_SYNONYMOUS, Transcriptome.ANNOTATION_MISSENSE,Transcriptome.ANNOTATION_NONSENSE,Transcriptome.ANNOTATION_FRAMESHIFT,Transcriptome.ANNOTATION_JUNCTION};
			
			tabFunctionalRoles = new Table(c, SWT.BORDER | SWT.SCROLL_PAGE  | SWT.CHECK);
			tabFunctionalRoles.setHeaderVisible(true);
			tabFunctionalRoles.setBounds(151, 71, 300, 300);
			TableColumn columnAnn = new TableColumn(tabFunctionalRoles, SWT.NONE);
			columnAnn.setWidth(300);
			columnAnn.setText("Annotation");
			columnAnn.setResizable(false);
			columnAnn.setMoveable(false);
			
			for(int i=0;i<itemsRole.length;i++) {
				TableItem item=new TableItem(tabFunctionalRoles,SWT.NONE);
				item.setText(0, itemsRole[i]);
			}
			tabFunctionalRoles.update();
			tabFunctionalRoles.redraw();
			
		}
		//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

		if(sampleSelect){
			//Sample Select Parameters

			cmbFilterSelect = new Combo(c, SWT.READ_ONLY);
			cmbFilterSelect.setBounds(10, 41, 200, 30);
			cmbFilterSelect.addMouseListener(mouse);
			
			
			String filterSelect[] = {"File of samples to filter", "File of samples to select" };
			cmbFilterSelect.setItems(filterSelect);
			cmbFilterSelect.setVisible(true);
			cmbFilterSelect.select(0);
			
			txtFilterSamples = new Text(c, SWT.BORDER);
			txtFilterSamples.setBounds(220, 41, 500, 26);
			txtFilterSamples.addMouseListener(mouse);
			
			btnSamplesFile = new Button(c, SWT.NONE);
			btnSamplesFile.setText("...");
			btnSamplesFile.setBounds(740, 41, 29, 23);
		
			btnSamplesFile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtFilterSamples);
				}
			});
			
			
		}
		//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

		return c;

	}

	public void proceed() {

		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		VCFFilter populationVCF = new VCFFilter();
		//Validate fields and record errors in the list

		ArrayList<String> listErrors = new ArrayList<String>();
		if (txtFile.getText() == null || txtFile.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtFile.setBackground(oc);
		}

		if (txtOutputFile.getText() == null|| txtOutputFile.getText().length()==0) {
			listErrors.add(FieldValidator.buildMessage(lblOutputFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutputFile.setBackground(oc);
		}

		if (txtMinQuality.getText() != null && txtMinQuality.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMinQuality.getText(),new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblMinQuality.getText(), FieldValidator.ERROR_INTEGER));
				txtMinQuality.setBackground(oc);
			} else {
				populationVCF.setMinGenotypeQuality(Integer.parseInt(txtMinQuality.getText()));
			}
		}

		if (txtMinCoverage.getText() != null && txtMinCoverage.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMinCoverage.getText(), new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblMinCoverage.getText(), FieldValidator.ERROR_INTEGER));
				txtMinCoverage.setBackground(oc);
			} else {
				populationVCF.setMinCoverage(Integer.parseInt(txtMinCoverage.getText()));
			}
		}

		if (txtMinDistance.getText() != null && txtMinDistance.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMinDistance.getText(), new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblMinDistance.getText(), FieldValidator.ERROR_INTEGER));
				txtMinDistance.setBackground(oc);
			} else {
				populationVCF.setMinDistance(Integer.parseInt(txtMinDistance.getText()));
			}
		}

		if (txtMinIndividualGenotyped.getText() != null && txtMinIndividualGenotyped.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMinIndividualGenotyped.getText(),new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblMinIndividualGenotyped.getText(), FieldValidator.ERROR_INTEGER));
				txtMinIndividualGenotyped.setBackground(oc);
			} else {
				populationVCF.setMinIndividualsGenotyped(Integer.parseInt(txtMinIndividualGenotyped.getText()));
			}
		}

		if (txtMinMAF.getText() != null && txtMinMAF.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMinMAF.getText(), new Double(0))) {
				listErrors.add(FieldValidator.buildMessage(lblMinMaf.getText(), FieldValidator.ERROR_NUMERIC));
				txtMinMAF.setBackground(oc);
			} else {
				populationVCF.setMinMAF(Double.parseDouble(txtMinMAF.getText()));
			}
		}

		if (txtMaxMAF.getText() != null && txtMaxMAF.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMaxMAF.getText(), new Double(0))) {
				listErrors.add(FieldValidator.buildMessage(lblMaxMaf.getText(), FieldValidator.ERROR_NUMERIC));
				txtMaxMAF.setBackground(oc);
			} else {
				populationVCF.setMaxMAF(Double.parseDouble(txtMaxMAF.getText()));
			}
		}
		
		if (txtMinOH.getText() != null && txtMinOH.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMinOH.getText(), new Double(0))) {
				listErrors.add(FieldValidator.buildMessage(lblMinOH.getText(), FieldValidator.ERROR_NUMERIC));
				txtMinOH.setBackground(oc);
			} else {
				populationVCF.setMinOH(Double.parseDouble(txtMinOH.getText()));
			}
		}

		if (txtMaxOH.getText() != null && txtMaxOH.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMaxOH.getText(), new Double(0))) {
				listErrors.add(FieldValidator.buildMessage(lblMaxOH.getText(), FieldValidator.ERROR_NUMERIC));
				txtMaxOH.setBackground(oc);
			} else {
				populationVCF.setMaxOH(Double.parseDouble(txtMaxOH.getText()));
			}
		}

		if (txtReferenceFile.getText() != null && txtReferenceFile.getText().length()>0) {
			try {
				ReferenceGenome genome = ReferenceGenomesFactory.getInstance().getGenome(txtReferenceFile.getText(), shell);
				populationVCF.setGenome(genome);
			} catch (IOException e) {
				e.printStackTrace();
				listErrors.add(FieldValidator.buildMessage(lblReference.getText(), "error loading file: "+e.getMessage()));
				txtReferenceFile.setBackground(oc);
			}
			if (txtMinGCContet.getText() != null && !txtMinGCContet.getText().equals("")) {
				if (!FieldValidator.isNumeric(txtMinGCContet.getText(),new Double(0))) {
					listErrors.add(FieldValidator.buildMessage(lblMinGccontent.getText(), FieldValidator.ERROR_NUMERIC));
					txtMinGCContet.setBackground(oc);
				} else {
					populationVCF.setMinGCContent(Double.parseDouble(txtMinGCContet.getText()));
				}
			}
			if(txtMaxGCContent.getText() != null && !txtMaxGCContent.getText().equals("")) {
				if (!FieldValidator.isNumeric(txtMaxGCContent.getText(),new Double(0))) {
					listErrors.add(FieldValidator.buildMessage(lblMaxGccontent.getText(), FieldValidator.ERROR_NUMERIC));
					txtMaxGCContent.setBackground(oc);
				} else {
					populationVCF.setMaxGCContent(Double.parseDouble(txtMaxGCContent.getText()));
				}
			}
		}

		if (txtMaxSamplesCnvs.getText() != null && txtMaxSamplesCnvs.getText().length()>0) {
			if (!FieldValidator.isNumeric(txtMaxSamplesCnvs.getText(),new Integer(0))) {
				listErrors.add(FieldValidator.buildMessage(lblMaximunNumberOf.getText(), FieldValidator.ERROR_NUMERIC));
				txtMaxSamplesCnvs.setBackground(oc);
			} else {
				populationVCF.setMaxCNVs(Integer.parseInt(txtMaxSamplesCnvs.getText()));
			}
		}

		Set<String>annotations = new TreeSet<String>();
		for(int i=0;i<tabFunctionalRoles.getItemCount();i++) {
			TableItem item = tabFunctionalRoles.getItem(i);
			if(item.getChecked()) {
				annotations.add(item.getText(0));
			}
		}
		
		if(annotations.size()>0)populationVCF.setAnnotations(annotations);

		if (txtGeneName.getText() != null && txtGeneName.getText().length()>0) {
			if (FieldValidator.isNumeric(txtGeneName.getText(), new Integer(0)) || FieldValidator.isNumeric(txtGeneName.getText(),new Double(0))) {
				listErrors.add(FieldValidator.buildMessage(lblGeneName.getText(), FieldValidator.ERROR_NUMERIC));
				txtGeneName.setBackground(oc);
			} else {
				populationVCF.setGeneId(txtGeneName.getText());
			}
		}
		
		SimpleGenomicRegionFileHandler regionFileHandler = new SimpleGenomicRegionFileHandler();
		if (txtFilterRegions.getText() != null && txtFilterRegions.getText().length()>0) {
			try {
				populationVCF.setRegionsToFilter(regionFileHandler.loadRegions(txtFilterRegions.getText()));
			} catch (IOException e) {
				e.printStackTrace();
				listErrors.add(FieldValidator.buildMessage(lblFilterRegionsFrom.getText(), "error loading file: "+e.getMessage()));
				txtFilterRegions.setBackground(oc);
			}
		}


		if (txtSelectRegionsFile.getText() != null && txtSelectRegionsFile.getText().length()>0) {
			try {
				populationVCF.setRegionsToSelect(regionFileHandler.loadRegions(txtSelectRegionsFile.getText()));
			} catch (IOException e) {
				e.printStackTrace();
				listErrors.add(FieldValidator.buildMessage(lblSelectRegionsFile.getText(), "error loading file: "+e.getMessage()));
				txtSelectRegionsFile.setBackground(oc);
			}
		}

		
		if (txtFilterSamples.getText() != null && txtFilterSamples.getText().length()>0) {
			try {
				populationVCF.setSampleIds(VCFFilter.loadSampleIds(txtFilterSamples.getText()));		
			} catch (IOException e) {
				e.printStackTrace();
				listErrors.add(FieldValidator.buildMessage("File of samples", "error loading file: "+e.getMessage()));
				txtFilterSamples.setBackground(oc);
			}
		}

		if (listErrors.size() > 0) {
			FieldValidator.paintErrors(listErrors, shell, "Variants Filter VCF");
			return;
		}

		//Change Combo box
		if (btnKeepOnlyBi.getSelection()) {
			populationVCF.setKeepOnlySNVs(true);
		}

		if (btnFilterInvariantSites.getSelection()) {
			populationVCF.setFilterInvariant(true);
		}


		if (btnFilterInvariantReference.getSelection()) {
			populationVCF.setFilterInvariantReference(true);
		}

		if (btnFilterInvariantAlternative.getSelection()) {
			populationVCF.setFilterInvariantAlternative(true);
		}

		
		
		int select = cmbFilterSelect.getSelectionIndex();
		if (select==0) {
			populationVCF.setFilterSamples(true);
		}
		
		String outputFile = txtOutputFile.getText();
		SyncVCFFilter syncPopulationVCF = new SyncVCFFilter("VCF Filter");
		syncPopulationVCF.setInputFile(txtFile.getText());
		syncPopulationVCF.setOutputFile(outputFile);
		syncPopulationVCF.setPopulationVCF(populationVCF);
		String logFilename = LoggingHelper.getLoggerFilename(outputFile,"FV");
		syncPopulationVCF.setLogName(logFilename);
		syncPopulationVCF.setNameProgressBar(new File(outputFile).getName());
		try {
			FileHandler logFile = new FileHandler(logFilename, false);
			syncPopulationVCF.setLogFile(logFile);
			syncPopulationVCF.schedule();
			MessageDialog.openInformation(shell, "Population VCF Filter",LoggingHelper.MESSAGE_PROGRESS_BAR);
			shell.dispose();
		} catch (Exception e) {
			MessageDialog.openError(shell, "Population VCF Filter",e.getMessage());
			e.printStackTrace();
			return;

		}
	}


	public String getSelectedFile() {
		return selectedFile;
	}


	public void setSelectedFile(String selectedFile) {
		this.selectedFile = selectedFile;
	}

}
