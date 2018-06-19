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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import net.sf.ngsep.control.SampleData;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.SpecialFieldsHelper;
import ngsep.alignments.io.ReadAlignmentFileReader;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * @author Juan Camilo Quintero
 * @author Jorge Duitama
 */
public class MainMultiVariantsDetector implements MultipleFilesInputWindow {
	protected Shell shell;
	private Display display;
	
	//Files selected initially by the user
	private Set<String> selectedFiles;
	
	@Override
	public void setSelectedFiles(Set<String> selectedFiles) {
		this.selectedFiles = selectedFiles;
		
	}
			
	private Table table;
	private TableColumn tbcCheck;
	private TableColumn tbcBamFile;
	private TableColumn tbcSampleId;
	private TableColumn tbcOutputFile;
	private TableColumn tbcFullPath;
	private Label lblListFiles;
	private Font tfont;
	private Button btnNext;
	private Button btnCancel;
	private Button btnSelectAll;
	private Button btnIndependentAnalysis;
	private Label lblOutput;
	private Text txtOutput;
	private Button btnOutput;
	
	private String suggestedOutFolder = null;
	
	
	

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
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

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("Multi Variants Detector");
		shell.setLocation(150, 200);
		shell.setSize(800, 750);
		tfont = new Font(Display.getCurrent(), "Arial", 12, SWT.BOLD);
		
		lblListFiles = new Label(shell, SWT.NONE);
		lblListFiles.setText("List of files to discover variants");
		lblListFiles.setFont(tfont);
		lblListFiles.setBounds(50, 10, 300, 25);
		
		btnSelectAll = new Button(shell, SWT.PUSH);
		btnSelectAll.setBounds(10, 55, 30, 30);
		btnSelectAll.setText(">");
		btnSelectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (int i = 0; i < table.getItemCount(); i++) {
					if (table.getItems()[i].getChecked()) {
						table.getItems()[i].setChecked(false);
					} else {
						table.getItems()[i].setChecked(true);
					}
				}
			}
		});

		table = new Table(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL| SWT.CHECK | SWT.H_SCROLL | SWT.HIDE_SELECTION | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setBounds(50, 50, 700, 500);

		tbcCheck = new TableColumn(table, SWT.NONE);
		tbcCheck.setText("Select");
		tbcCheck.setResizable(true);
		tbcCheck.setMoveable(false);
		tbcCheck.setWidth(50);

		tbcBamFile = new TableColumn(table, SWT.NONE);
		tbcBamFile.setText("Input BAM File");
		tbcBamFile.setResizable(true);
		tbcBamFile.setMoveable(false);
		tbcBamFile.setWidth(200);
		
		tbcSampleId = new TableColumn(table, SWT.NONE);
		tbcSampleId.setText("Sample Id");
		tbcSampleId.setResizable(true);
		tbcSampleId.setWidth(150);

		tbcOutputFile = new TableColumn(table, SWT.NONE);
		tbcOutputFile.setText("Output Prefix");
		tbcOutputFile.setResizable(true);
		tbcOutputFile.setWidth(200);
		
		tbcFullPath = new TableColumn(table, SWT.NONE);
		//tbcFullPath.setWidth(0);
		tbcFullPath.setResizable(true);

		
		for (String filePath:selectedFiles) {
			String fnLowerCase = filePath.toLowerCase();
			if (!fnLowerCase.endsWith(".bam")) {
				continue;
			}
			File file = new File(filePath);
			if(!file.exists()) {
				continue;
			}
			String filename = file.getName();
			String sampleId="";
			Set<String> sampleIds;
			try {
				sampleIds = extractSampleIds(shell, filePath);
				if(sampleIds.size()==1) {
					sampleId = sampleIds.iterator().next();
				}
			} catch (IOException e) {
				MessageDialog.openError(shell, " Variants Detector Error","Error loading sample ids from the alignments file "+ filename+": "+ e.getMessage()+". Skipping file");
				continue;
			}
			
			if(suggestedOutFolder==null) {
				suggestedOutFolder = file.getParentFile().getAbsolutePath();
			}
			TableItem item = new TableItem(table, SWT.CHECK);
			item.setText(1, filename);
			if(sampleId!=null) item.setText(2, sampleId);
			String nameVCF=filename.substring(0, filename.lastIndexOf("."));
			item.setText(3, nameVCF);
			item.setText(4,filePath);
		}
		
		
		Button btnJointAnalysis = new Button(shell, SWT.RADIO);
		btnJointAnalysis.setBounds(10, 600, 370, 25);
		btnJointAnalysis.setText("Joint analysis");
		btnJointAnalysis.setSelection(true);
		btnJointAnalysis.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lblOutput.setText("(*)Output File:");
				lblOutput.setVisible(true);
				lblOutput.setVisible(true);
				txtOutput.setText(suggestedOutFolder+File.separator+"AllSamples.vcf");
			}
		});
		
		btnIndependentAnalysis = new Button(shell, SWT.RADIO);
		btnIndependentAnalysis.setBounds(410, 600, 370, 25);
		btnIndependentAnalysis.setText("Independent analysis per file");
		btnIndependentAnalysis.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lblOutput.setText("(*)Output Directory:");
				lblOutput.setVisible(true);
				lblOutput.setVisible(true);
				txtOutput.setText(suggestedOutFolder);
			}
		});


		lblOutput = new Label(shell, SWT.NONE);
		lblOutput.setText("(*)Output File:");
		lblOutput.setBounds(10, 640, 180, 25);

		txtOutput = new Text(shell, SWT.BORDER);
		txtOutput.setText(suggestedOutFolder+File.separator+"AllSamples.vcf");
		txtOutput.setBounds(200, 640, 550, 25);

		btnOutput = new Button(shell, SWT.NONE);
		btnOutput.setBounds(760, 640, 24, 25);
		btnOutput.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnIndependentAnalysis.getSelection()) {
					SpecialFieldsHelper.updateDirectoryTextBox(shell, SWT.SAVE, selectedFiles.iterator().next(), txtOutput);
				} else {
					SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, selectedFiles.iterator().next(), txtOutput);
				}
				
			}
		});
		btnOutput.setText("...");
		
		btnNext = new Button(shell, SWT.NONE);
		btnNext.setText("Next");
		btnNext.setBounds(200, 680, 150, 30);
			
		btnNext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				process();
			}
		});

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setText("Cancel");
		btnCancel.setBounds(450, 680, 150, 30);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// Determine where the mouse was clicked
				Point pt = new Point(e.x, e.y);
				// Determine which row was selected
				TableItem item = table.getItem(pt);
				if (item == null) return;
				// Determine which column was selected
				int column = -1;
				for (int i = 0, n = table.getColumnCount(); i < n; i++) {
					Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						// This is the selected column
						column = i;
						break;
					}
				}
				if (column <= 0) return;
				String toModify = item.getText(column);
				if(column == 1) {
					FileDialog dialog = new FileDialog(shell);
					dialog.setFileName(toModify);
					String absolutePath = item.getText(4);
					dialog.setFilterPath(absolutePath);
					String newFile = dialog.open();
					if(newFile!=null){
						File f = new File (newFile);
						if(!f.exists()) {
							MessageDialog.openError(shell, " Variants Detector Error","The selected file does not exist");
							return;
						}
						String filename = f.getName();
						if(!filename.toLowerCase().endsWith(".bam")) {
							MessageDialog.openError(shell, " Variants Detector Error","The selected file must be a bam file");
							return;
						}
						Set<String> sampleIds;
						try {
							sampleIds = extractSampleIds(shell, f.getAbsolutePath());
						} catch (IOException e1) {
							MessageDialog.openError(shell, " Variants Detector Error","Error loading sample ids from the alignments file "+ filename+": "+ e1.getMessage()+". Skipping file");
							return;
						}
						item.setText(column,filename);
						item.setText(4,f.getAbsolutePath());
						
						if(sampleIds.size()==1) {
							item.setText(2,sampleIds.iterator().next());
							String nameVCF=filename.substring(0, filename.lastIndexOf("."));
							item.setText(3, nameVCF);
						}
					}		
				}
			
				if(column == 2 || column == 3) {
					String newMessaje=JOptionPane.showInputDialog("New Name", toModify);
					if (newMessaje!=null) {
						item.setText(column,newMessaje);
					}
				}				
			}
		});
	}
	
	public static boolean isSampleIdUnique(List<String> readGroups, String sampleId){
		for (int i = 0; i < readGroups.size(); i++) {
			if (!readGroups.get(i).equals(sampleId))return false;
		}
		return true;
	}

	public static Set<String> extractSampleIds(Shell shell, String alignmentsFile) throws IOException {
		Set<String> sampleIds = new HashSet<>();
		try (ReadAlignmentFileReader reader =  new ReadAlignmentFileReader(alignmentsFile)) {
			sampleIds.addAll(reader.getSampleIdsByReadGroup().values());
		}
		return sampleIds;
	}
	public void process(){
		
		if (txtOutput.getText()==null || txtOutput.getText().length()==0) {
			MessageDialog.openError(shell, "Error", FieldValidator.buildMessage(lblOutput.getText(), FieldValidator.ERROR_MANDATORY));
			return;
		}
		String outputPath = txtOutput.getText();
		
		List<SampleData> uniqueDataForSample=new ArrayList<SampleData>();
		int itemNoChecked=0;
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			if (item.getChecked()) {
				SampleData sampleData = new SampleData();
				String nameSample=item.getText(1);
				String sampleId=item.getText(2);
				String namePrefix=item.getText(3);
				if (nameSample != null && !nameSample.equals("")) {
					sampleData.setSortedBamFile(item.getText(4));
				} else {
					MessageDialog.openError(shell, "Error", "Input file in line: "+i+" can not be empty");
					return;
				} 
				if (sampleId != null && !sampleId.equals("")) {
					sampleData.setSampleId(sampleId);
				} else {
					MessageDialog.openError(shell, "Error", "SampleId in line: "+i+" can not be empty");
					return;
				}
				if (namePrefix != null && !namePrefix.equals("")) {
					sampleData.setVcfFile(outputPath + File.separator + namePrefix+".vcf");
					sampleData.setSvFile(outputPath + File.separator + namePrefix+"_SV.gff");
					sampleData.setVdLogFile(outputPath + File.separator + namePrefix+"_VD.log");	
				} else {
					MessageDialog.openError(shell, "Error", "Prefix for output files in line: "+i+" can not be empty");
					return;
				}
				uniqueDataForSample.add(sampleData);
			} else{
				itemNoChecked++;
			}
		}
		if (itemNoChecked==table.getItemCount()) {
			MessageDialog.openError(shell, "Multi Variants Detector Error", "Please select at least one sample");
			return;	
		}
		MainVariantsDetector shellVariantsDetector=new MainVariantsDetector(outputPath, uniqueDataForSample,btnIndependentAnalysis.getSelection());
		//MessageDialog.openInformation(shell, "Multi Variants Detector", "Created next window object");
		try {
			shellVariantsDetector.open();
		} catch (Exception e) {
			String message = LoggingHelper.serializeException(e);
			MessageDialog.openError(shell, "Multi Variants Detector Error", "Error loading next frame: "+message);
		}
		shell.dispose();
		
	}

}
