package net.sf.ngsep.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.sf.ngsep.control.SyncVCFGoldStandardComparator;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.benchmark.VCFGoldStandardComparator;
import ngsep.genome.ReferenceGenome;

public class MainVCFGoldStandardComparator implements SingleFileInputWindow {
	protected Shell shell;
	private Display display;
	
	//File selected initially by the user
	private String selectedFile;
	public String getSelectedFile() {
		return selectedFile;
	}
	public void setSelectedFile(String selectedFile) {
		this.selectedFile = selectedFile;
	}
	
	//Action buttons
	private Button btnSubmit;
	private Button btnCancel;
	
	//Main arguments
	private Label lblGenomeFile;
	private Text txtGenomeFile;
	private Button btnGenomeFile;
	
	private Label lblGoldStandardFile;
	private Text txtGoldStandardFile;
	private Button btnGoldStandardFile;
	
	private Label lblTestFile;
	private Text txtTestFile;
	private Button btnTestFile;
	
	private Label lblOutputFile;
	private Text txtOutputFile;
	private Button btnOutputFile;
	
	private Label lblConfidenceRegionsFile;
	private Text txtConfidenceRegionsFile;
	private Button btnConfidenceRegionsFile;
	
	private Label lblComplexRegionsFile;
	private Text txtComplexRegionsFile;
	private Button btnComplexRegionsFile;
	
	private Button btnGenomicVCF;
	
	@Override
	public void open() {
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	private void createContents() {
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setSize(800, 450);
		shell.setText("VCF gold standard comparator");
		shell.setLocation(10, 10);

		MouseListenerNgsep mouse = new MouseListenerNgsep();
		
		lblGenomeFile = new Label(shell, SWT.NONE);
		lblGenomeFile.setBounds(10, 30, 180, 22);
		lblGenomeFile.setText("(*FA) Reference Genome:");
		
		txtGenomeFile = new Text(shell, SWT.BORDER);
		txtGenomeFile.setBounds(200, 30, 550, 22);
		
		txtGenomeFile.addMouseListener(mouse);
		
		// Suggest the latest stored genome
		String historyReference = HistoryManager.getHistory(selectedFile, HistoryManager.KEY_REFERENCE_FILE);
		if (historyReference!=null) txtGenomeFile.setText(historyReference);
		
		btnGenomeFile = new Button(shell, SWT.NONE);
		btnGenomeFile.setBounds(760, 30, 25, 22);
		btnGenomeFile.setText("...");
		btnGenomeFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, selectedFile, txtGenomeFile);
			}
		});
		

		lblGoldStandardFile = new Label(shell, SWT.NONE);
		lblGoldStandardFile.setBounds(10, 70, 180, 22);
		lblGoldStandardFile.setText("(*VCF) Gold standard:");
		
		txtGoldStandardFile = new Text(shell, SWT.BORDER);
		txtGoldStandardFile.setBounds(200, 70, 550, 22);
		txtGoldStandardFile.addMouseListener(mouse);
		
		btnGoldStandardFile = new Button(shell, SWT.NONE);
		btnGoldStandardFile.setBounds(760, 70, 25, 22);
		btnGoldStandardFile.setText("...");
		btnGoldStandardFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, selectedFile, txtGoldStandardFile);
			}
		});
		
		lblTestFile = new Label(shell, SWT.NONE);
		lblTestFile.setBounds(10, 110, 180, 22);
		lblTestFile.setText("(*VCF) Test file:");
		
		txtTestFile = new Text(shell, SWT.BORDER);
		txtTestFile.setBounds(200, 110, 550, 22);
		txtTestFile.addMouseListener(mouse);
		txtTestFile.setText(selectedFile);
		
		btnTestFile = new Button(shell, SWT.NONE);
		btnTestFile.setBounds(760, 110, 25, 22);
		btnTestFile.setText("...");
		btnTestFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, selectedFile, txtTestFile);
			}
		});
		
		lblOutputFile = new Label(shell, SWT.NONE);
		lblOutputFile.setBounds(10, 150, 180, 22);
		lblOutputFile.setText("Output file:");
		
		txtOutputFile = new Text(shell, SWT.BORDER);
		txtOutputFile.setBounds(200, 150, 550, 22);
		txtOutputFile.addMouseListener(mouse);
		String suggestedOutFile = SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile)+"_benchmark.txt";
		txtOutputFile.setText(suggestedOutFile);
		
		btnOutputFile = new Button(shell, SWT.NONE);
		btnOutputFile.setBounds(760, 150, 25, 22);
		btnOutputFile.setText("...");
		btnOutputFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.SAVE, selectedFile, txtOutputFile);
			}
		});
		
		lblConfidenceRegionsFile = new Label(shell, SWT.NONE);
		lblConfidenceRegionsFile.setBounds(10, 190, 180, 22);
		lblConfidenceRegionsFile.setText("Confidence regions file:");
		
		txtConfidenceRegionsFile = new Text(shell, SWT.BORDER);
		txtConfidenceRegionsFile.setBounds(200, 190, 550, 22);
		txtConfidenceRegionsFile.addMouseListener(mouse);
		
		btnConfidenceRegionsFile = new Button(shell, SWT.NONE);
		btnConfidenceRegionsFile.setBounds(760, 190, 25, 22);
		btnConfidenceRegionsFile.setText("...");
		btnConfidenceRegionsFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, selectedFile, txtConfidenceRegionsFile);
			}
		});
		
		lblComplexRegionsFile = new Label(shell, SWT.NONE);
		lblComplexRegionsFile.setBounds(10, 230, 180, 22);
		lblComplexRegionsFile.setText("Complex regions file:");
		
		txtComplexRegionsFile = new Text(shell, SWT.BORDER);
		txtComplexRegionsFile.setBounds(200, 230, 550, 22);
		txtComplexRegionsFile.addMouseListener(mouse);
		
		btnComplexRegionsFile = new Button(shell, SWT.NONE);
		btnComplexRegionsFile.setBounds(760, 230, 25, 22);
		btnComplexRegionsFile.setText("...");
		btnComplexRegionsFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, selectedFile, txtComplexRegionsFile);
			}
		});
		
		btnGenomicVCF = new Button(shell, SWT.CHECK);
		btnGenomicVCF.setBounds(10, 270, 390, 22);
		btnGenomicVCF.setText("Gold standard contains confidence regions");
		
		btnSubmit = new Button(shell, SWT.NONE);
		btnSubmit.setBounds(240, 370, 200, 25);
		btnSubmit.setText("Submit");
		btnSubmit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();
			}
		});
		
		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(460, 370, 200, 25);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
	}
	
	public void proceed() {
		Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
		ArrayList<String> errors = new ArrayList<String>();
		
		VCFGoldStandardComparator instance = new VCFGoldStandardComparator();
		
		SyncVCFGoldStandardComparator job = new SyncVCFGoldStandardComparator("Gold standard comparator");
		job.setInstance(instance);
		
		if (txtGenomeFile.getText() == null || txtGenomeFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblGenomeFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtGenomeFile.setBackground(oc);
		} else {
			try {
				ReferenceGenome genome = ReferenceGenomesFactory.getInstance().getGenome(txtGenomeFile.getText(), shell);
				instance.setGenome(genome);
			} catch (IOException e) {
				e.printStackTrace();
				errors.add(FieldValidator.buildMessage(lblGenomeFile.getText(), "error loading file: "+e.getMessage()));
				txtGenomeFile.setBackground(oc);
			}
		}

		if (txtGoldStandardFile.getText() == null || txtGoldStandardFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblGoldStandardFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtGoldStandardFile.setBackground(oc);
		} else {
			job.setGoldStandardFile(txtGoldStandardFile.getText());
		}
		
		if (txtTestFile.getText() == null || txtTestFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblTestFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtTestFile.setBackground(oc);
		} else {
			job.setTestFile(txtTestFile.getText());
		}
		
		if (txtOutputFile.getText() == null|| txtOutputFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblOutputFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutputFile.setBackground(oc);
		} else {
			job.setOutputFile(txtOutputFile.getText());
		}

		if (txtConfidenceRegionsFile.getText() != null && txtConfidenceRegionsFile.getText().length()>0) {
			try {
				instance.setConfidenceRegions(txtConfidenceRegionsFile.getText());
			} catch (IOException e) {
				e.printStackTrace();
				errors.add(FieldValidator.buildMessage(lblConfidenceRegionsFile.getText(), "Error loading file: "+e.getMessage()));
				txtConfidenceRegionsFile.setBackground(oc);
			}
			
		}
		
		if (txtComplexRegionsFile.getText() != null && txtComplexRegionsFile.getText().length()>0) {
			try {
				instance.setComplexRegions(txtComplexRegionsFile.getText());
			} catch (IOException e) {
				e.printStackTrace();
				errors.add(FieldValidator.buildMessage(lblComplexRegionsFile.getText(), "Error loading file: "+e.getMessage()));
				txtComplexRegionsFile.setBackground(oc);
			}
		}
		
		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shell, "Genomes Aligner");
			return;
		}
		
		instance.setGenomicVCF(btnGenomicVCF.getSelection());
		
		String outputFile = txtOutputFile.getText(); 
		String logFilename = LoggingHelper.getLoggerFilename(outputFile, "VGSV");
		job.setLogName(logFilename);
		job.setNameProgressBar(new File(outputFile).getName());
		try {
			job.schedule();
		} catch (Exception e) {
			MessageDialog.openError(shell," VCF gold standard comparator Error", e.getMessage());
			e.printStackTrace();
		}
		MessageDialog.openInformation(shell,"VCF gold standard comparator is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
		shell.dispose();
	}

}
