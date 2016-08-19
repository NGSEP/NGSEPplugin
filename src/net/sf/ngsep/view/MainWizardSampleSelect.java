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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JOptionPane;

import net.sf.ngsep.control.SampleData;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.MouseListenerNgsep;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
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
 * @author Daniel Cruz
 *
 */
public class MainWizardSampleSelect {
	protected Shell shell;
	private String folder;
	private boolean pairedEndReads;
	private Display display;
	private Table table;
	private TableColumn tbcCheck;
	private TableColumn tbcFileOne;
	private TableColumn tbcFileTwo;
	private TableColumn tbcReadGroup;
	private TableColumn tbcSampleId;
	private TableColumn tbcOutputFile;
	private TableColumn tbcFullPathFile1;
	private TableColumn tbcFullPathFile2;
	private Font tfont;

	private static final String [] DETECTED_FILE_EXTS = {"fa","fasta","fastq","fq","gz"};
	private Label lblListFilesTo;
	private Button btnNext;
	private Button btnCancel;
	private Button btnSelectAll;
	
	//For Mapping
	private Label lblMapOutput;
	private Text txtMapOutput;
	private Button btnMapOutput;
	
	//For VD
	private Label lblVDOutput;
	private Text txtVDOutput;
	private Button btnVDOutput;
	
	// Processor
	
	private Label lblNumberOfProcessors;
	private Text txtNumberOfProcessors;
	
	// Variants Prefix
	private Label lblVarPrex;
	private Text txtVarPrex;
	
	//If fails
	private Button btnChkAbortFails;
	

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
		if(pairedEndReads){
			shell.setText("Wizard Paired End Reads");
		} else {
			shell.setText("Wizard Single End Reads");
		}
		shell.setLocation(150, 200);
		tfont = new Font(Display.getCurrent(), "Arial", 10, SWT.BOLD);

		File file = null;
		File[] files = null;
		if (getFolder() != null && !getFolder().equals("")) {
			file = new File(getFolder());
			if (file.exists()) {
				files = file.listFiles();
			}
		}

		table = new Table(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.CHECK | SWT.H_SCROLL | SWT.HIDE_SELECTION | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tbcCheck = new TableColumn(table, SWT.NONE);
		tbcCheck.setText("Check File");
		tbcCheck.setResizable(true);
		tbcCheck.setMoveable(false);

		tbcFileOne = new TableColumn(table, SWT.NONE);
		tbcFileOne.setText("File One");
		tbcFileOne.setResizable(true);
		tbcFileOne.setMoveable(false);

		tbcFileTwo = new TableColumn(table, SWT.NONE);
		tbcFileTwo.setText("File Two");
		tbcFileTwo.setResizable(true);

		tbcReadGroup = new TableColumn(table, SWT.NONE);
		tbcReadGroup.setText("Read Group Id");
		tbcReadGroup.setResizable(true);

		tbcSampleId = new TableColumn(table, SWT.NONE);
		tbcSampleId.setText("Sample Id");
		tbcSampleId.setResizable(true);

		tbcOutputFile = new TableColumn(table, SWT.NONE);
		tbcOutputFile.setText("Output Prefix");
		tbcOutputFile.setResizable(true);
		
		tbcFullPathFile1 = new TableColumn(table, SWT.NONE);
		tbcFullPathFile1.setWidth(0);
		tbcFullPathFile1.setResizable(true);
		
		tbcFullPathFile2 = new TableColumn(table, SWT.NONE);
		tbcFullPathFile2.setWidth(0);
		tbcFullPathFile2.setResizable(true);

		lblListFilesTo = new Label(shell, SWT.NONE);
		lblListFilesTo.setText("List files to be processed");
		lblListFilesTo.setFont(tfont);
		lblListFilesTo.setBounds(52, 7, 186, 21);

		
// for mapping		
		lblMapOutput = new Label(shell, SWT.NONE);
		lblMapOutput.setText("(*)Mapping Output Directory:");
		txtMapOutput = new Text(shell, SWT.BORDER);
		txtMapOutput.setText(folder);
		btnMapOutput = new Button(shell, SWT.NONE);
		btnMapOutput.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog=new DirectoryDialog(shell);
				File file = new File(folder);
				dialog.setFilterPath(file.getAbsolutePath());
				dialog.open();
				String out=dialog.getFilterPath();
				if (out!=null){
					txtMapOutput.setText(out);
				}
			}
		});

		btnMapOutput.setText("...");

// for VD		
		lblVDOutput = new Label(shell, SWT.NONE);
		lblVDOutput.setText("(*)Variants Output Directory:");
		txtVDOutput = new Text(shell, SWT.BORDER);
		txtVDOutput.setText(folder);
		btnVDOutput = new Button(shell, SWT.NONE);
		btnVDOutput.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog=new DirectoryDialog(shell);
				File file = new File(folder);
				dialog.setFilterPath(file.getAbsolutePath());
				dialog.open();
				String out=dialog.getFilterPath();
				if (out!=null){
					txtVDOutput.setText(out);
				}
			}
		});

		btnVDOutput.setText("...");

//		Processor
		lblNumberOfProcessors = new Label(shell, SWT.NONE);
		lblNumberOfProcessors.setText("Number Of Processors:");
		txtNumberOfProcessors = new Text(shell, SWT.BORDER);
//by default it suggests the user to use all possible processor minus 1, to be able to use his computer.
		int processors = Runtime.getRuntime().availableProcessors()-1;
		txtNumberOfProcessors.setText(String.valueOf(processors));
		
//		VarPrefix
		lblVarPrex = new Label(shell, SWT.NONE);
		lblVarPrex.setText("Final vcf Prefix:");
		txtVarPrex = new Text(shell, SWT.BORDER);
		txtVarPrex.setText("WizardVars");
		
//		fails
		
		btnChkAbortFails = new Button(shell, SWT.CHECK);
		btnChkAbortFails.setText("Stop if one sample fails? ");
		
		
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

		List<File> listFiles = new ArrayList<File>(Arrays.asList(files));
		//Temporal list just to paint later. It does not have full paths of the fastq files
		List<SampleData> pairedSamples = new ArrayList<SampleData>();
		Collections.sort(listFiles,new FilesComparator());
		for (int i=0;i<listFiles.size();i++) {
			String filename = listFiles.get(i).getAbsolutePath();
			if(!correctFormat(filename)) {
				continue;
			}
			SampleData sd = new SampleData();
			sd.setFastq1(filename);
			if(pairedEndReads){
				if(i<listFiles.size()-1) {
					String filename2 = listFiles.get(i+1).getAbsolutePath();
					if(areCompatible(filename, filename2)) {
						sd.setFastq2(filename2);
						i++;
					}
				}
			}
			pairedSamples.add(sd);
		}

		for (SampleData sd:pairedSamples) {
			File f1 = new File(sd.getFastq1());
			String prefix = f1.getName();
			
			TableItem item = new TableItem(table, SWT.CHECK);
			item.setText(1, f1.getName());
			item.setText(6,sd.getFastq1());
			if (sd.getFastq2()!=null) {
				File f2 = new File(sd.getFastq2());
				item.setText(2,f2.getName());
				item.setText(7,sd.getFastq2());

				int indexDifference = MainMapReads.calculateDifferences(prefix,f2.getName()).get(0); 				
				prefix = prefix.substring(0, indexDifference);
				char last = prefix.charAt(prefix.length()-1);
				if(last=='_' || last == '-' || last == ' ') {
					prefix = prefix.substring(0,prefix.length()-1);
				}
			} else {
				int index = prefix.indexOf(".");
				if(index>0) prefix = prefix.substring(0, index);
				
			}
			item.setText(3, prefix);
			item.setText(4, prefix);
			item.setText(5, prefix);
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
				if(column == 1 || column == 2) {
					FileDialog dialog = new FileDialog(shell);
					dialog.setFilterPath(folder);
					String newFile = dialog.open();
					if(newFile!=null){
						File f = new File (newFile);
						item.setText(column,f.getName());
						item.setText(column+5,f.getAbsolutePath());
					}		
				}
				
				if(column == 3 || column == 4 || column == 5) {
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
		columns.add(2, tbcFileTwo);
		columns.add(3, tbcSampleId);
		columns.add(4, tbcReadGroup);
		columns.add(5, tbcOutputFile);
		

		List<Button> buttons=new ArrayList<Button>();
		buttons.add(0, btnMapOutput);
		buttons.add(1, btnVDOutput);
		buttons.add(2, btnNext);
		buttons.add(3, btnCancel);
		buttons.add(4, btnChkAbortFails);

		paint(listFiles.size(), shell, table, columns, lblMapOutput, txtMapOutput, lblVDOutput, txtVDOutput, lblNumberOfProcessors, txtNumberOfProcessors, lblVarPrex, txtVarPrex,buttons, true);
	}

	/**
	 * 
	 */
	public void process(){
		try{
			
			Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
					
			int numProcessorsAssign=0;
			
			
			if (txtVarPrex.getText()==null || txtVarPrex.getText().length()==0) {
				MessageDialog.openError(shell, "Error", FieldValidator.buildMessage(lblVarPrex.getText(), FieldValidator.ERROR_MANDATORY));
				return;
			}
			
			String variantsPrefix = txtVarPrex.getText();
			
			if (txtMapOutput.getText()==null || txtMapOutput.getText().length()==0) {
				MessageDialog.openError(shell, "Error", FieldValidator.buildMessage(lblMapOutput.getText(), FieldValidator.ERROR_MANDATORY));
				return;
			}
			
			String outputMapDirectory = txtMapOutput.getText();
			
			if (txtVDOutput.getText()==null || txtVDOutput.getText().length()==0) {
				MessageDialog.openError(shell, "Error", FieldValidator.buildMessage(lblVDOutput.getText(), FieldValidator.ERROR_MANDATORY));
				return;
			}
			
			String outputVDDirectory = txtVDOutput.getText();
			
			int processors = Runtime.getRuntime().availableProcessors();
			if (txtNumberOfProcessors.getText() != null && txtNumberOfProcessors.getText().length()>0) {
				if (!FieldValidator.isNumeric(txtNumberOfProcessors.getText(),new Integer(0))) {
					MessageDialog.openError(shell, "Error",FieldValidator.buildMessage(lblNumberOfProcessors.getText(), FieldValidator.ERROR_INTEGER));
					txtNumberOfProcessors.setBackground(oc);
				} else if(Integer.parseInt(txtNumberOfProcessors.getText())<=processors){
					numProcessorsAssign = Integer.parseInt(txtNumberOfProcessors.getText());
				} else{
					MessageDialog.openError(shell, "Error",FieldValidator.buildMessage(lblNumberOfProcessors.getText(), " the number entered in this box must be less than " + processors));
					txtNumberOfProcessors.setBackground(oc);
				}
			} else{
				FieldValidator.buildMessage(lblNumberOfProcessors.getText(),FieldValidator.ERROR_MANDATORY);
			}
			
			
			
			List<SampleData> samples=new ArrayList<SampleData>();
		
			String fileOne = null;
			String fileTwo = null;
			String readGroupId = null;
			String sampleId = null;
			int itemNoChecked=0;
			for (int i = 0; i < table.getItemCount(); i++) {
				TableItem item = table.getItem(i);
				if (item.getChecked()) {
					SampleData sampleData = new SampleData();
					String outputNamePrefix = item.getText(5);
					fileOne = item.getText(1);
					fileTwo = item.getText(2);
					readGroupId = item.getText(3);
					sampleId = item.getText(4);
					if (fileOne != null && !fileOne.equals("")) {
						sampleData.setFastq1(item.getText(6));

					} 
					if (fileTwo != null && !fileTwo.equals("")) {
						sampleData.setFastq2(item.getText(7));
					}

					if (readGroupId != null && !readGroupId.equals("")) {
						sampleData.setReadGroupId(readGroupId);
					}
					if (sampleId != null && !sampleId.equals("")) {
						sampleData.setSampleId(sampleId);
					}

					if (outputNamePrefix != null && !outputNamePrefix.equals("")) {
						sampleData.setSamFile(outputMapDirectory + File.separator + outputNamePrefix+".sam");
						sampleData.setSortedBamFile(outputMapDirectory + File.separator + outputNamePrefix+"_sorted.bam");
						sampleData.setMapLogFile(outputMapDirectory + File.separator + outputNamePrefix+"_Map.log");
						
						
						sampleData.setVcfFile(outputMapDirectory + File.separator + outputNamePrefix+".vcf");
						sampleData.setVdLogFile(outputMapDirectory + File.separator + outputNamePrefix+"_VD.log");
						
						sampleData.setVcfFileGT(outputVDDirectory + File.separator + outputNamePrefix+"_gt.vcf");
						sampleData.setVdGTLogFile(outputVDDirectory+ File.separator + outputNamePrefix+"_gt.log");
						
						sampleData.setSvFile(outputMapDirectory + File.separator + outputNamePrefix+"_SV.gff");
							
						
					}
					samples.add(sampleData);
				}else{
					itemNoChecked++;
				}

			}
			
			if (itemNoChecked==table.getItemCount()) {
				MessageDialog.openError(shell, "Wizard Error", "Please select at least one sample");
				return;	
			}
			
			boolean stopFail = btnChkAbortFails.getSelection();
			
						
			if ((txtMapOutput.getText()!=null && txtMapOutput.getText().length()>0)&&(txtVDOutput.getText()!=null && txtVDOutput.getText().length()>0)){
				MainWizard shellWiz=new MainWizard(folder,txtMapOutput.getText(), txtVDOutput.getText());
				shellWiz.setListSamples(samples);
				shellWiz.setNumProcAssig(numProcessorsAssign);
				shellWiz.setVarPrefix(variantsPrefix);
				shellWiz.setStopFail(stopFail);
				shellWiz.open();
			}else{
				MessageDialog.openError(shell, "Wizard Error", "Please select the output directories");
			}
			shell.setEnabled(false);
			shell.dispose();
			
			
		} catch (Exception e) {
			MessageDialog.openError(shell, "Wizard Error", e.getMessage());
			e.printStackTrace();
			return;
		}

	}

	public boolean correctFormat(String file) {
		int i = file.lastIndexOf('.');
		if(i<0) return false;
		String ext = file.substring(i+1);
		return Arrays.binarySearch(DETECTED_FILE_EXTS, ext)>=0;
	}
	
	public boolean areCompatible(String file1, String file2) {
		if (file1.length() != file2.length())
			return false;
		int numberDifferentLetters = MainMapReads.calculateDifferences(file1, file2).size();
		if (numberDifferentLetters > 1) {
			return false;
		} else {
			return true;
		}
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public boolean isPairedEndReads() {
		return pairedEndReads;
	}

	public void setPairedEndReads(boolean pairedEndReads) {
		this.pairedEndReads = pairedEndReads;
	}
	
	public static void paint(int numberRows, Shell shell, Table table, List<TableColumn> tableColumns,Label mapLabel,Text mapText,Label vdLabel,Text vdText,Label procLabel,Text procText,Label varLabel,Text varText,List<Button> buttons, boolean isMapping){
		TableColumn columnSample2=null;
		TableColumn columnSampleId=null;
		TableColumn columnReadGroupId=null;
		TableColumn columnNameBam=null;
		TableColumn columnCheck=tableColumns.get(0);
		TableColumn columnSample=tableColumns.get(1);
		if (isMapping) {
			columnSample2=tableColumns.get(2);
			columnSampleId=tableColumns.get(3);
			columnReadGroupId=tableColumns.get(4);
			columnNameBam=tableColumns.get(5);					
		} else {
			columnSampleId=tableColumns.get(2);
			columnNameBam=tableColumns.get(3);
		}
		Button btnMap=buttons.get(0);
		Button btnVd=buttons.get(1);
		Button btnNext=buttons.get(2);
		Button btnCancel=buttons.get(3);
		Button btnCheckFails=buttons.get(4); 
		
		//TODO >20 Design //percentage location could be better?
//		if (numberRows < 20) {
			shell.setSize(750, 580);
			table.setBounds(50, 50, 650, 298);
			columnCheck.setWidth(70);
			columnSample.setWidth(116);
			if (isMapping) {
				columnSample2.setWidth(116);
				columnReadGroupId.setWidth(116);
			}
			columnSampleId.setWidth(116);
			columnNameBam.setWidth(250);
			
			mapLabel.setBounds(30, 370, 160, 21);
			mapText.setBounds(210, 370, 450, 21);
			btnMap.setBounds(681, 370, 24, 25);
			btnNext.setBounds(150, 490, 122, 30);
			btnCancel.setBounds(326, 490, 122, 30);
			
			// Just For wizard
			
			vdLabel.setBounds(30, 400, 160, 21);
			vdText.setBounds(210, 400, 450, 21);
			btnVd.setBounds(681, 400, 24, 25);
			
			procLabel.setBounds(30, 430, 160, 21);
			procText.setBounds(210, 430, 25, 21);
			
			varLabel.setBounds(30, 460, 160, 21);
			varText.setBounds(210, 460, 100, 21);
			
			btnCheckFails.setBounds(350, 460, 200, 21);
			
			
	/*	} else if(numberRows > 20 && numberRows < 60){
			shell.setSize(900, 750);
			table.setBounds(50, 50, 700, 400);
			columnCheck.setWidth(70);
			columnSample.setWidth(126);
			if (isMapping) {
				columnSample2.setWidth(126);
				columnReadGroupId.setWidth(126);
			}
			columnSampleId.setWidth(126);
			columnNameBam.setWidth(126);
			mapLabel.setBounds(30, 460, 150, 21);
			mapText.setBounds(210, 460, 450, 21);
			btnMap.setBounds(681, 460, 24, 25);
			btnNext.setBounds(150, 500, 122, 30);
			btnCancel.setBounds(326, 500, 122, 30);
			
			// For wizard
			
			vdLabel.setBounds(30, 415, 160, 21);
			vdText.setBounds(210, 415, 450, 21);
			btnVd.setBounds(681, 415, 24, 25);
			
			procLabel.setBounds(30, 445, 160, 21);
			procText.setBounds(210, 445, 25, 21);
			
			varLabel.setBounds(30, 485, 160, 21);
			varText.setBounds(210, 485, 100, 21);
			
			btnCheckFails.setBounds(350, 485, 200, 21);
			
			
		} else {
			shell.setSize(900, 740);
			table.setBounds(50, 50, 750, 500);
			columnCheck.setWidth(70);
			columnSample.setWidth(140);
			if (isMapping) {
				columnSample2.setWidth(140);
				columnReadGroupId.setWidth(140);
			}
			columnSampleId.setWidth(140);
			columnNameBam.setWidth(140);
			mapLabel.setBounds(50, 580, 130, 21);
			mapText.setBounds(190, 580, 580, 21);
			btnMap.setBounds(771, 580, 24, 25);
			btnNext.setBounds(150, 620, 122, 30);
			btnCancel.setBounds(326, 620, 122, 30);	
			
			// For wizard
			
			vdLabel.setBounds(30, 430, 160, 21);
			vdText.setBounds(210, 430, 450, 21);
			btnVd.setBounds(681, 430, 24, 25);
			
			procLabel.setBounds(30, 450, 160, 21);
			procText.setBounds(210, 450, 25, 21);
			
			varLabel.setBounds(30, 500, 160, 21);
			varText.setBounds(210, 500, 100, 21);
			
			btnCheckFails.setBounds(350, 500, 200, 21);
		}*/
	}
	
	class FilesComparator implements Comparator<File> {

		@Override
		public int compare(File f1, File f2) {
			return f1.getName().compareTo(f2.getName());
		}
		
	}	

}
