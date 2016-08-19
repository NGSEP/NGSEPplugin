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
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import net.sf.ngsep.control.SampleData;
import net.sf.ngsep.utilities.EclipseProjectHelper;
import net.sf.ngsep.utilities.FieldValidator;
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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
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
public class MainMultiVariantsDetector {
	protected Shell shell;
	private Display display;
	private Table table;
	private TableColumn tbcCheck;
	private TableColumn tbcFileOne;
	private TableColumn tbcSampleId;
	private TableColumn tbcOutputFile;
	private TableColumn tbcFullPath;
	private Label lblListFiles;
	private Font tfont;
	private Button btnNext;
	private Button btnCancel;
	private Button btnSelectAll;
	private Label lblOutput;
	private Text txtOutput;
	private Button btnOutput;
	private String folder;

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
		tfont = new Font(Display.getCurrent(), "Arial", 10, SWT.BOLD);

		File file = null;
		File[] files = null;
		if (folder != null && !folder.equals("")) {
			file = new File(folder);
			if (file.exists()) {
				files = file.listFiles();
			}
		}

		table = new Table(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL| SWT.CHECK | SWT.H_SCROLL | SWT.HIDE_SELECTION | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tbcCheck = new TableColumn(table, SWT.NONE);
		tbcCheck.setText("Check File");
		tbcCheck.setResizable(true);
		tbcCheck.setMoveable(false);

		tbcFileOne = new TableColumn(table, SWT.NONE);
		tbcFileOne.setText("Input BAM File");
		tbcFileOne.setResizable(true);
		tbcFileOne.setMoveable(false);
		
		tbcSampleId = new TableColumn(table, SWT.NONE);
		tbcSampleId.setText("Sample Id");
		tbcSampleId.setResizable(true);

		tbcOutputFile = new TableColumn(table, SWT.NONE);
		tbcOutputFile.setText("Output Prefix");
		tbcOutputFile.setResizable(true);
		
		tbcFullPath = new TableColumn(table, SWT.NONE);
		//tbcFullPath.setWidth(0);
		tbcFullPath.setResizable(true);

		lblListFiles = new Label(shell, SWT.NONE);
		lblListFiles.setText("List of files to discover variants");
		lblListFiles.setFont(tfont);
		lblListFiles.setBounds(52, 7, 260, 21);

		lblOutput = new Label(shell, SWT.NONE);
		lblOutput.setText("(*)Output Directory:");

		txtOutput = new Text(shell, SWT.BORDER);
		txtOutput.setText(folder);

		btnOutput = new Button(shell, SWT.NONE);
		btnOutput.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog=new DirectoryDialog(shell);
				File file = new File(folder);
				dialog.setFilterPath(file.getAbsolutePath());
				dialog.open();
				String out=dialog.getFilterPath();
				if (out!=null){
					txtOutput.setText(out);
				}
			}
		});
		btnOutput.setText("...");

		btnNext = new Button(shell, SWT.NONE);
		btnNext.setText("Next");
		btnNext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				process();
			}
		});

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});

		List<File> listFiles = Arrays.asList(files);
		int countAccepted = 0;
		for (int i = 0; i < listFiles.size(); i++) {
			String filename = listFiles.get(i).getName();
			String fnLowerCase = filename.toLowerCase();
			String fileabsolutePath = listFiles.get(i).getAbsolutePath();
			if (!fnLowerCase.endsWith(".bam") && !fnLowerCase.endsWith(".sam")) {
				continue;
			}
			String sampleId=null;
			try {
				List<String> readGroups = extractReadGroups(fileabsolutePath);
				if(readGroups.size()>0) {
					String sampleIdTmp = readGroups.get(0);
					if (isSampleIdUnique(readGroups, sampleIdTmp)) sampleId = sampleIdTmp;
				}
			} catch (IOException e1) {
				MessageDialog.openError(shell, "Variants Detector error", "Could not extract read group from file "+fileabsolutePath+". Skipping file");
				continue;
			}
			TableItem item = new TableItem(table, SWT.CHECK);
			item.setText(1, filename);
			if(sampleId!=null) item.setText(2, sampleId);
			String nameVCF=filename.substring(0, filename.lastIndexOf("."));
			item.setText(3, nameVCF);
			item.setText(4,listFiles.get(i).getAbsolutePath());
			countAccepted++;
		}	

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
					dialog.setFilterPath(folder);
					String newFile = dialog.open();
					if(newFile!=null){
						File f = new File (newFile);
						item.setText(column,f.getName());
						item.setText(4,f.getAbsolutePath());
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

		List<TableColumn> columns=new ArrayList<TableColumn>();
		columns.add(0, tbcCheck);
		columns.add(1, tbcFileOne);
		columns.add(2, tbcSampleId);
		columns.add(3, tbcOutputFile);

		List<Button> buttons=new ArrayList<Button>();
		buttons.add(0, btnOutput);
		buttons.add(1, btnNext);
		buttons.add(2, btnCancel);
		//TODO: Design this method better
		MainMultiMapping.paint(countAccepted, shell, table, columns, lblOutput, txtOutput, buttons,false);

	}
	
	public static boolean isSampleIdUnique(List<String> readGroups, String sampleId){
		for (int i = 0; i < readGroups.size(); i++) {
			if (!readGroups.get(i).equals(sampleId))return false;
		}
		return true;
	}

	public static List<String> extractReadGroups(String alignmentsFile) throws IOException {
		ReadAlignmentFileReader reader = null;
		try {
			reader = new ReadAlignmentFileReader(alignmentsFile);
			return reader.getReadGroups();
		} finally {
			if (reader != null) reader.close();
		}
	}
	public void process(){
		
		if (txtOutput.getText()==null || txtOutput.getText().length()==0) {
			MessageDialog.openError(shell, "Error", FieldValidator.buildMessage(lblOutput.getText(), FieldValidator.ERROR_MANDATORY));
			return;
		}
		String outputDirectory = txtOutput.getText();
		try {
			String pD1 = EclipseProjectHelper.findProjectDirectory(folder);
			String pD2 = EclipseProjectHelper.findProjectDirectory(outputDirectory);
			if (!pD1.equals(pD2)) throw new IOException("Please use an output directory within the same project where the bam file is located");
		} catch (IOException e) {
			MessageDialog.openError(shell, "Error", e.getMessage());
			return;
		}
		
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
					sampleData.setVcfFile(outputDirectory + File.separator + namePrefix+".vcf");
					sampleData.setSvFile(outputDirectory + File.separator + namePrefix+"_SV.gff");
					sampleData.setVdLogFile(outputDirectory + File.separator + namePrefix+"_VD.log");	
				} else {
					MessageDialog.openError(shell, "Error", "Prefix for output files in line: "+i+" can not be empty");
					return;
				}
				uniqueDataForSample.add(sampleData);
			}else{
				itemNoChecked++;
			}
		}
		if (itemNoChecked==table.getItemCount()) {
			MessageDialog.openError(shell, "Multi Variants Detector Error", "Please select at least one sample");
			return;	
		}else{
			MainVariantsDetector shellVariantsDetector=new MainVariantsDetector(outputDirectory, uniqueDataForSample);
			shellVariantsDetector.open();
			shell.dispose();
			

		}
		
	}


	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

}
