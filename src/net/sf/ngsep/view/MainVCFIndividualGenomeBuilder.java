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

import net.sf.ngsep.control.SyncVCFIndividualGenomeBuilder;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.genome.ReferenceGenome;
import ngsep.vcf.VCFIndividualGenomeBuilder;

public class MainVCFIndividualGenomeBuilder implements SingleFileInputWindow {

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
	private Label lblFile;
	private Text txtFile;
	private Button btnFile;
	private Label lblReferenceFile;
	private Text txtReferenceFile;
	private Button btnReferenceFile;
	private Label lblOutputFile;
	private Text txtOutputFile;
	private Button btnOutputFile;

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
		shell.setSize(800, 400);
		shell.setText("Individual Genome Builder");
		shell.setLocation(10, 10);

		MouseListenerNgsep mouse = new MouseListenerNgsep();
		lblFile = new Label(shell, SWT.NONE);
		lblFile.setBounds(10, 30, 180, 22);
		lblFile.setText("(*VCF) Variants File:");
		
		txtFile = new Text(shell, SWT.BORDER);
		txtFile.setBounds(200, 30, 550, 22);
		if (selectedFile != null && selectedFile.length()>0) {
			txtFile.setText(selectedFile);
		}
		txtFile.addMouseListener(mouse);
		
		btnFile = new Button(shell, SWT.NONE);
		btnFile.setBounds(760, 30, 25, 22);
		btnFile.setText("...");
		btnFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, selectedFile, txtFile);
			}
		});
		
		lblReferenceFile = new Label(shell, SWT.NONE);
		lblReferenceFile.setBounds(10, 70, 180, 22);
		lblReferenceFile.setText("(*FASTA) Reference Genome:");
		

		txtReferenceFile = new Text(shell, SWT.BORDER);
		txtReferenceFile.setBounds(200, 70, 550, 22);
		txtReferenceFile.addMouseListener(mouse);
		// Suggest the latest stored genome
		String historyReference = HistoryManager.getHistory(selectedFile, HistoryManager.KEY_REFERENCE_FILE);
		if (historyReference!=null) txtReferenceFile.setText(historyReference);
		
		btnReferenceFile = new Button(shell, SWT.NONE);
		btnReferenceFile.setBounds(760, 70, 25, 22);
		btnReferenceFile.setText("...");
		btnReferenceFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, selectedFile, txtReferenceFile);
			}
		});
		
		lblOutputFile = new Label(shell, SWT.NONE);
		lblOutputFile.setBounds(10, 110, 180, 22);
		lblOutputFile.setText("(*FA) Output File:");
		
		txtOutputFile = new Text(shell, SWT.BORDER);
		txtOutputFile.setBounds(200, 110, 550, 22);
		txtOutputFile.addMouseListener(mouse);
		String suggestedOutPrefix = SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile);
		txtOutputFile.setText(suggestedOutPrefix+"_genome.fa");
		
		
		
		btnOutputFile = new Button(shell, SWT.NONE);
		btnOutputFile.setBounds(760, 110, 25, 22);
		btnOutputFile.setText("...");
		btnOutputFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.SAVE, selectedFile, txtOutputFile);
			}
		});
		
		btnSubmit = new Button(shell, SWT.NONE);
		btnSubmit.setBounds(240, 330, 200, 25);
		btnSubmit.setText("Build genome");
		btnSubmit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();
			}
		});
		

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(460, 330, 200, 25);
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
		SyncVCFIndividualGenomeBuilder job = new SyncVCFIndividualGenomeBuilder("Individual Genome Builder");
		VCFIndividualGenomeBuilder instance = new VCFIndividualGenomeBuilder();
		job.setInstance(instance);
		
		if (txtFile.getText() == null || txtFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtFile.setBackground(oc);
		} else {
			job.setInputFile(txtFile.getText());
		}
		
		if (txtReferenceFile.getText() == null || txtReferenceFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblReferenceFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtReferenceFile.setBackground(oc);
		} else {
			try {
				ReferenceGenome genome = ReferenceGenomesFactory.getInstance().getGenome(txtReferenceFile.getText(), shell);
				instance.setGenome(genome);
			} catch (IOException e) {
				e.printStackTrace();
				errors.add(FieldValidator.buildMessage(lblReferenceFile.getText(), "error loading file: "+e.getMessage()));
				txtReferenceFile.setBackground(oc);
			}
		}
		if (txtOutputFile.getText() == null|| txtOutputFile.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblOutputFile.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutputFile.setBackground(oc);
		} else {
			job.setOutputFile(txtOutputFile.getText());
		}
		
		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shell, "Individual Genome Builder");
			return;
		}
		
		String outputFile = txtOutputFile.getText();
		String logFilename = LoggingHelper.getLoggerFilename(outputFile,"VIGB");
		job.setLogName(logFilename);
		job.setNameProgressBar(new File(outputFile).getName());
		try {
			job.schedule();
		} catch (Exception e) {
			MessageDialog.openError(shell," Individual Genome Builder Error", e.getMessage());
			e.printStackTrace();
		}
		HistoryManager.saveInHistory(HistoryManager.KEY_REFERENCE_FILE, txtReferenceFile.getText());
		MessageDialog.openInformation(shell,"Individual Genome Builder is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
		shell.dispose();
		
	}
}
