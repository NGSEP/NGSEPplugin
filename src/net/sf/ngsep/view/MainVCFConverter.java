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
import java.util.logging.FileHandler;

import net.sf.ngsep.control.SyncVCFConverter;
import net.sf.ngsep.utilities.FieldValidator;
import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.utilities.MouseListenerNgsep;
import net.sf.ngsep.utilities.SpecialFieldsHelper;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Juan Camilo Quintero
 * @author Claudia Perea
 * @author Paulo Izquierdo
 * @author Jorge Duitama
 *
 */
public class MainVCFConverter implements SingleFileInputWindow {
	
	//Parameters

	//General variables 
	private Shell shell;
	private Display display;
	
	//File selected initially by the user
	private String selectedFile;
	public String getSelectedFile() {
		return selectedFile;
	}
	public void setSelectedFile(String selectedFile) {
		this.selectedFile = selectedFile;
	}

	private Text txtFile;
	private Text txtOutputFile;
	private Text txtPopulationFile;
	private Label lblFile;
	private Label lblOutputFile;
	private Label lblPopulationFile;
	private Button btnFile;
	private Button btnOutoputFile;
	private Button btnPopulationFile;
	
	private Button btnVCFConverter;
	private Button btnCancel;
	private Button btnPrintFasta;
	private Button btnPrintMatrix;
	private Button btnPrintHapmap;
	private Button btnPrintSpagedi;
	private Button btnPrintPlink;
	private Button btnPrintHaploView;
	private Button btnPrintEmma;
	private Button btnPrintPowermarker;
	private Button btnPrintDarwin;
	private Button btnPrintEigensoft;
	private Button btnPrintFlapjack;
	private Button btnPrintStructure;
	private Button btnPrintrrBLUP;
	private Button btnPrintTreemix;
	private Button btnPrintJoinMap;
	private Text txtId1JoinMap;
	private Text txtId2JoinMap;
	private Label lblId1JoinMap;
	private Label lblId2JoinMap;
	
	/**
	 * Open the window.
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
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		MouseListenerNgsep mouse = new MouseListenerNgsep();
		
		shell = new Shell(display,SWT.SHELL_TRIM);
		shell.setLocation(150, 200);
		shell.setSize(785, 560);
		shell.setText("VCF Converter");

		lblFile = new Label(shell, SWT.NONE);
		lblFile.setBounds(10, 30, 160, 26);
		lblFile.setText("(*)File:");

		txtFile = new Text(shell, SWT.BORDER);
		txtFile.setBounds(180, 30, 500, 26);
		txtFile.addMouseListener(mouse);
		txtFile.setText(selectedFile);

		btnFile = new Button(shell, SWT.NONE);
		btnFile.setBounds(700, 30, 35, 26);
		btnFile.setText("...");
		btnFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.OPEN, selectedFile, txtFile);
			}
		});

		lblOutputFile = new Label(shell, SWT.NONE);
		lblOutputFile.setBounds(10, 80, 160, 26);
		lblOutputFile.setText("(*)Output File Prefix:");

		txtOutputFile = new Text(shell, SWT.BORDER);
		txtOutputFile.setBounds(180, 80, 500, 26);
		txtOutputFile.addMouseListener(mouse);
		String suggestedOutPrefix = SpecialFieldsHelper.buildSuggestedOutputPrefix(selectedFile);
		txtOutputFile.setText(suggestedOutPrefix);

		btnOutoputFile = new Button(shell, SWT.NONE);
		btnOutoputFile.setBounds(700, 80, 35, 26);
		btnOutoputFile.setText("...");
		btnOutoputFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell, SWT.SAVE, selectedFile, txtOutputFile);
			}
		});

		//------------------------------------------------------------------------------------------
		
		btnPrintStructure = new Button(shell, SWT.CHECK);
		btnPrintStructure.setBounds(10, 150, 160, 20);
		btnPrintStructure.setText("Print Structure");

		btnPrintHapmap = new Button(shell, SWT.CHECK);
		btnPrintHapmap.setBounds(190, 150, 160, 20);
		btnPrintHapmap.setText("Print HapMap");

		btnPrintHaploView = new Button(shell, SWT.CHECK);
		btnPrintHaploView.setBounds(370, 150, 160, 20);
		btnPrintHaploView.setText("Print Haplo View");

		btnPrintEigensoft = new Button(shell, SWT.CHECK);
		btnPrintEigensoft.setBounds(550, 150, 160, 20);
		btnPrintEigensoft.setText("Print Eigensoft");

		btnPrintFasta = new Button(shell, SWT.CHECK);
		btnPrintFasta.setBounds(10, 200, 160, 20);
		btnPrintFasta.setText("Print Fasta");

		btnPrintSpagedi = new Button(shell, SWT.CHECK);
		btnPrintSpagedi.setBounds(190, 200, 160, 20);
		btnPrintSpagedi.setText("Print Spagedi");

		btnPrintEmma = new Button(shell, SWT.CHECK);
		btnPrintEmma.setBounds(370, 200, 160, 20);
		btnPrintEmma.setText("Print Emma");

		btnPrintFlapjack = new Button(shell, SWT.CHECK);
		btnPrintFlapjack.setBounds(550, 200, 160, 20);
		btnPrintFlapjack.setText("Print FlapJack");

		btnPrintMatrix = new Button(shell, SWT.CHECK);
		btnPrintMatrix.setBounds(10, 250, 160, 20);
		btnPrintMatrix.setText("Print Matrix");

		btnPrintPlink = new Button(shell, SWT.CHECK);
		btnPrintPlink.setBounds(190, 250, 160, 20);
		btnPrintPlink.setText("Print Plink");

		btnPrintPowermarker = new Button(shell, SWT.CHECK);
		btnPrintPowermarker.setBounds(370, 250, 170, 20);
		btnPrintPowermarker.setText("Print PowerMarker");
		
		btnPrintDarwin = new Button(shell, SWT.CHECK);
		btnPrintDarwin.setBounds(550, 250, 160, 20);
		btnPrintDarwin.setText("Print Darwin");
		
		btnPrintrrBLUP = new Button(shell, SWT.CHECK);
		btnPrintrrBLUP.setBounds(10, 300, 160, 20);
		btnPrintrrBLUP.setText("Print rrBLUP");
		
		btnPrintTreemix = new Button(shell, SWT.CHECK);
		btnPrintTreemix.setBounds(10, 350, 160, 20);
		btnPrintTreemix.setText("Print Treemix");
		btnPrintTreemix.addMouseListener(onMouseClickPrintTreeMix);
		
		lblPopulationFile = new Label(shell, SWT.NONE);
		lblPopulationFile.setBounds(190, 350, 130, 26);
		lblPopulationFile.setText("Populations File :");
		lblPopulationFile.setVisible(false);
		
		txtPopulationFile = new Text(shell, SWT.BORDER);
		txtPopulationFile.setBounds(330, 350, 360, 26);
		txtPopulationFile.setVisible(false);
		txtPopulationFile.addMouseListener(mouse);

		btnPopulationFile = new Button(shell, SWT.NONE);
		btnPopulationFile.setBounds(700, 350, 35, 26);
		btnPopulationFile.setText("...");
		btnPopulationFile.setVisible(false);
		btnPopulationFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpecialFieldsHelper.updateFileTextBox(shell,  SWT.OPEN, selectedFile, txtPopulationFile);
			}
		});
		
		btnPrintJoinMap = new Button(shell, SWT.CHECK);
		btnPrintJoinMap.setBounds(10, 400, 160, 20);
		btnPrintJoinMap.setText("Print JoinMap");
		btnPrintJoinMap.addMouseListener(onMouseClickPrintJoinMap);
		
		lblId1JoinMap=new Label(shell, SWT.NONE);
		lblId1JoinMap.setBounds(190, 400, 160, 20);
		lblId1JoinMap.setText("Id Sample1 JoinMap:");
		lblId1JoinMap.setVisible(false);
		
		txtId1JoinMap = new Text(shell, SWT.BORDER);
		txtId1JoinMap.setBounds(370, 400, 80, 26);
		txtId1JoinMap.setVisible(false);
		
		lblId2JoinMap=new Label(shell, SWT.NONE);
		lblId2JoinMap.setBounds(470, 400, 160, 20);
		lblId2JoinMap.setText("Id Sample2 JoinMap:");
		lblId2JoinMap.setVisible(false);
		
		txtId2JoinMap = new Text(shell, SWT.BORDER);
		txtId2JoinMap.setBounds(650, 400, 80, 26);
		txtId2JoinMap.setVisible(false);
		
		//------------------------------------------------------------------------------------------
		
		btnVCFConverter = new Button(shell, SWT.NONE);
		btnVCFConverter.setBounds(200, 470, 130, 30);
		btnVCFConverter.setText("VCF Converter");
		btnVCFConverter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				proceed();
			}
		});

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(380, 470, 130, 30);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
	}

	private final MouseListener onMouseClickPrintTreeMix= new MouseListener() {
		@Override
		public void mouseDoubleClick(MouseEvent e) {
			// TODO Auto-generated method stub
		}
		@Override
		public void mouseDown(MouseEvent e) {
			// TODO Auto-generated method stub
		}
		@Override
		public void mouseUp(MouseEvent e) {
			if (btnPrintTreemix.getSelection()) {
				lblPopulationFile.setVisible(true);
				txtPopulationFile.setVisible(true);
				btnPopulationFile.setVisible(true);
			}else{
				lblPopulationFile.setVisible(false);
				txtPopulationFile.setVisible(false);
				btnPopulationFile.setVisible(false);
			}
		}
	};
	
	private final MouseListener onMouseClickPrintJoinMap= new MouseListener() {
		@Override
		public void mouseDoubleClick(MouseEvent e) {
			// TODO Auto-generated method stub
		}
		@Override
		public void mouseDown(MouseEvent e) {
			// TODO Auto-generated method stub
		}
		@Override
		public void mouseUp(MouseEvent e) {
			if (btnPrintJoinMap.getSelection()) {
				lblId1JoinMap.setVisible(true);
				lblId2JoinMap.setVisible(true);
				txtId1JoinMap.setVisible(true);
				txtId2JoinMap.setVisible(true);
			}else{
				lblId1JoinMap.setVisible(false);
				lblId2JoinMap.setVisible(false);
				txtId1JoinMap.setVisible(false);
				txtId2JoinMap.setVisible(false);
			}
		}
	};
	
	public void proceed() {
		try {
			Color oc = MouseListenerNgsep.COLOR_EXCEPCION;
			ArrayList<String> listErrors = new ArrayList<String>();
			SyncVCFConverter vcfConverter = new SyncVCFConverter();
			if (txtFile.getText() == null || txtFile.getText().equals("")) {
				listErrors.add(FieldValidator.buildMessage(lblFile.getText(), FieldValidator.ERROR_MANDATORY));
				txtFile.setBackground(oc);
			} else {
				vcfConverter.setFile(txtFile.getText());
			}
			if (txtOutputFile.getText() == null || txtOutputFile.getText().equals("")) {
				listErrors.add(FieldValidator.buildMessage(lblOutputFile.getText(), FieldValidator.ERROR_MANDATORY));
				txtOutputFile.setBackground(oc);
			} else {
				vcfConverter.setOutputFile(txtOutputFile.getText());
			}
			if (btnPrintStructure.getSelection())
				vcfConverter.setStructure(true);

			if (btnPrintFasta.getSelection())
				vcfConverter.setFasta(true);

			if (btnPrintMatrix.getSelection())
				vcfConverter.setMatrix(true);

			if (btnPrintHapmap.getSelection())
				vcfConverter.setHapMap(true);

			if (btnPrintSpagedi.getSelection())
				vcfConverter.setSpagedi(true);

			if (btnPrintPlink.getSelection())
				vcfConverter.setPlink(true);

			if (btnPrintHaploView.getSelection())
				vcfConverter.setHaploview(true);

			if (btnPrintEmma.getSelection())
				vcfConverter.setEmma(true);

			if (btnPrintPowermarker.getSelection())
				vcfConverter.setPowerMarker(true);

			if (btnPrintEigensoft.getSelection())
				vcfConverter.setEigensoft(true);

			if (btnPrintFlapjack.getSelection())
				vcfConverter.setFlapJack(true);
			
			if (btnPrintDarwin.getSelection())
				vcfConverter.setDarwin(true);
			
			if (btnPrintrrBLUP.getSelection())
				vcfConverter.setrrBLUP(true);
			
			if(btnPrintTreemix.getSelection()){
				if(txtPopulationFile.getText() == null || txtPopulationFile.getText().equals("")){
					listErrors.add(FieldValidator.buildMessage(lblPopulationFile.getText(), "Population File is mandatory for TreeMix"));
					txtPopulationFile.setBackground(oc);
				}else{
					vcfConverter.setTreeMix(true);
					vcfConverter.setPopulationFile(txtPopulationFile.getText());
				}
			}
	
			if (btnPrintJoinMap.getSelection()) {
				if(txtId1JoinMap.getText() == null || txtId1JoinMap.getText().equals("")){
					listErrors.add(FieldValidator.buildMessage(lblId1JoinMap.getText(), FieldValidator.ERROR_MANDATORY));
					txtId1JoinMap.setBackground(oc);
					txtId2JoinMap.setBackground(null);
				} else if(txtId2JoinMap.getText() == null || txtId2JoinMap.getText().equals("")){
					listErrors.add(FieldValidator.buildMessage(lblId2JoinMap.getText(), FieldValidator.ERROR_MANDATORY));
					txtId1JoinMap.setBackground(null);
					txtId2JoinMap.setBackground(oc);
				} else {
					vcfConverter.setJoinMap(true);
					vcfConverter.setIdParent1(txtId1JoinMap.getText());
					vcfConverter.setIdParent2(txtId2JoinMap.getText());
				}
			}
			
			if (listErrors.size() > 0) {
				FieldValidator.paintErrors(listErrors, shell, "VCF Converter");
				return;
			}
				
			String outPrefix = txtOutputFile.getText();
			String logFilename = outPrefix+"_VC.log";
			FileHandler logFile = new FileHandler(logFilename, false);
			vcfConverter.setLogName(logFilename);
			vcfConverter.setLogFile(logFile);
			vcfConverter.setNameProgressBar(new File(outPrefix).getName());
			vcfConverter.runJob();
			MessageDialog.openInformation(shell, "VCF Converter",LoggingHelper.MESSAGE_PROGRESS_BAR);
			shell.dispose();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
