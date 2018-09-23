package net.sf.ngsep.view;

import java.io.File;
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

import net.sf.ngsep.control.SyncGenomesAligner;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.assembly.GenomesAligner;

public class MainGenomesAligner implements SingleFileInputWindow {
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
	private Label lblGenome1File;
	private Text txtGenome1File;
	private Button btnGenome1File;
	private Label lblTranscriptome1File;
	private Text txtTranscriptome1File;
	private Button btnTranscriptome1File;
	
	private Label lblGenome2File;
	private Text txtGenome2File;
	private Button btnGenome2File;
	private Label lblTranscriptome2File;
	private Text txtTranscriptome2File;
	private Button btnTranscriptome2File;
	
	private Label lblOutputPrefix;
	private Text txtOutputPrefix;
	private Button btnOutputPrefix;

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
		shell.setText("Genomes Aligner");
		shell.setLocation(10, 10);

		MouseListenerNgsep mouse = new MouseListenerNgsep();
		
		lblGenome1File = new Label(shell, SWT.NONE);
		lblGenome1File.setBounds(10, 30, 180, 22);
		lblGenome1File.setText("(*FA) Genome 1:");
		
		txtGenome1File = new Text(shell, SWT.BORDER);
		txtGenome1File.setBounds(200, 30, 550, 22);
		if (selectedFile != null && selectedFile.length()>0) {
			txtGenome1File.setText(selectedFile);
		}
		txtGenome1File.addMouseListener(mouse);
		
		btnGenome1File = new Button(shell, SWT.NONE);
		btnGenome1File.setBounds(760, 30, 25, 22);
		btnGenome1File.setText("...");
		btnGenome1File.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, selectedFile, txtGenome1File);
			}
		});
		

		lblTranscriptome1File = new Label(shell, SWT.NONE);
		lblTranscriptome1File.setBounds(10, 70, 180, 22);
		lblTranscriptome1File.setText("(*GFF) Annotation Genome 1:");
		
		txtTranscriptome1File = new Text(shell, SWT.BORDER);
		txtTranscriptome1File.setBounds(200, 70, 550, 22);
		txtTranscriptome1File.addMouseListener(mouse);
		
		btnTranscriptome1File = new Button(shell, SWT.NONE);
		btnTranscriptome1File.setBounds(760, 70, 25, 22);
		btnTranscriptome1File.setText("...");
		btnTranscriptome1File.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, selectedFile, txtTranscriptome1File);
			}
		});
		
		lblGenome2File = new Label(shell, SWT.NONE);
		lblGenome2File.setBounds(10, 110, 180, 22);
		lblGenome2File.setText("(*FA) Genome 2:");
		
		txtGenome2File = new Text(shell, SWT.BORDER);
		txtGenome2File.setBounds(200, 110, 550, 22);
		txtGenome2File.addMouseListener(mouse);
		
		// Suggest the latest stored genome
		String historyReference = HistoryManager.getHistory(selectedFile, HistoryManager.KEY_REFERENCE_FILE);
		if (historyReference!=null) txtGenome2File.setText(historyReference);
		
		btnGenome2File = new Button(shell, SWT.NONE);
		btnGenome2File.setBounds(760, 110, 25, 22);
		btnGenome2File.setText("...");
		btnGenome2File.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, selectedFile, txtGenome2File);
			}
		});
		

		lblTranscriptome2File = new Label(shell, SWT.NONE);
		lblTranscriptome2File.setBounds(10, 150, 180, 22);
		lblTranscriptome2File.setText("(*GFF) Annotation Genome 2:");
		
		txtTranscriptome2File = new Text(shell, SWT.BORDER);
		txtTranscriptome2File.setBounds(200, 150, 550, 22);
		txtTranscriptome2File.addMouseListener(mouse);
		
		// Suggest the latest stored transcriptome
		String historyTranscriptome = HistoryManager.getHistory(selectedFile, HistoryManager.KEY_TRANSCRIPTOME_FILE);
		if (historyTranscriptome!=null) txtTranscriptome2File.setText(historyTranscriptome);
		
		btnTranscriptome2File = new Button(shell, SWT.NONE);
		btnTranscriptome2File.setBounds(760, 150, 25, 22);
		btnTranscriptome2File.setText("...");
		btnTranscriptome2File.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.OPEN, selectedFile, txtTranscriptome2File);
			}
		});
		
		lblOutputPrefix = new Label(shell, SWT.NONE);
		lblOutputPrefix.setBounds(10, 190, 180, 22);
		lblOutputPrefix.setText("Output prefix:");
		
		txtOutputPrefix = new Text(shell, SWT.BORDER);
		txtOutputPrefix.setBounds(200, 190, 550, 22);
		txtOutputPrefix.addMouseListener(mouse);
		String suggestedOutPrefix = SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile);
		txtOutputPrefix.setText(suggestedOutPrefix);
		
		
		
		btnOutputPrefix = new Button(shell, SWT.NONE);
		btnOutputPrefix.setBounds(760, 190, 25, 22);
		btnOutputPrefix.setText("...");
		btnOutputPrefix.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,SWT.SAVE, selectedFile, txtOutputPrefix);
			}
		});
		
		btnSubmit = new Button(shell, SWT.NONE);
		btnSubmit.setBounds(240, 330, 200, 25);
		btnSubmit.setText("Align");
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
		
		GenomesAligner instance = new GenomesAligner();
		
		SyncGenomesAligner job = new SyncGenomesAligner("Genomes Aligner");
		job.setInstance(instance);
		
		if (txtGenome1File.getText() == null || txtGenome1File.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblGenome1File.getText(), FieldValidator.ERROR_MANDATORY));
			txtGenome1File.setBackground(oc);
		} else {
			job.setGenome1File(txtGenome1File.getText());
		}

		if (txtTranscriptome1File.getText() == null || txtTranscriptome1File.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblTranscriptome1File.getText(), FieldValidator.ERROR_MANDATORY));
			txtTranscriptome1File.setBackground(oc);
		} else {
			job.setTranscriptome1File(txtTranscriptome1File.getText());
		}
		
		if (txtGenome2File.getText() == null || txtGenome2File.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblGenome2File.getText(), FieldValidator.ERROR_MANDATORY));
			txtGenome2File.setBackground(oc);
		} else {
			job.setGenome2File(txtGenome2File.getText());
		}

		if (txtTranscriptome2File.getText() == null || txtTranscriptome2File.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblTranscriptome2File.getText(), FieldValidator.ERROR_MANDATORY));
			txtTranscriptome2File.setBackground(oc);
		} else {
			job.setTranscriptome2File(txtTranscriptome2File.getText());
		}
		
		if (txtOutputPrefix.getText() == null || txtOutputPrefix.getText().length()==0) {
			errors.add(FieldValidator.buildMessage(lblOutputPrefix.getText(), FieldValidator.ERROR_MANDATORY));
			txtOutputPrefix.setBackground(oc);
		} else {
			instance.setOutPrefix(txtOutputPrefix.getText());
		}
		
		if (errors.size() > 0) {
			FieldValidator.paintErrors(errors, shell, "Genomes Aligner");
			return;
		}
		
		String logFilename = LoggingHelper.getLoggerFilename(instance.getOutPrefix(),"GNAL");
		job.setLogName(logFilename);
		job.setNameProgressBar(new File(instance.getOutPrefix()).getName());
		try {
			job.schedule();
		} catch (Exception e) {
			MessageDialog.openError(shell," Genomes Aligner Error", e.getMessage());
			e.printStackTrace();
		}
		MessageDialog.openInformation(shell,"Genomes aligner is running",LoggingHelper.MESSAGE_PROGRESS_BAR);
		shell.dispose();
	}

}
