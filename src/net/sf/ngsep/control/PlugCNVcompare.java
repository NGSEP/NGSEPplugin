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
package net.sf.ngsep.control;

import java.util.Iterator;

import net.sf.ngsep.view.MainCNVcompare;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Entry point for the CNV compare menu 
 * @author Juan Fernando De la Hoz, Jorge Duitama
 *
 */
public class PlugCNVcompare extends AbstractHandler{

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//Retrieve active shell
		Shell shell = HandlerUtil.getActiveShell(event);
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		IStructuredSelection selection = (IStructuredSelection) sel;
		
		String bam1 = null;
		String bam2 = null;
		String bam2sufix = null;
		Iterator<Object> it = selection.iterator();
		
		try{
			
			if (it.hasNext()) {
				IFile inputFileOne = (IFile) it.next();
				bam1 = inputFileOne.getLocation().toString();
			}
			if (it.hasNext() ) {
				IFile inputFileTwo = (IFile) it.next();
				bam2 = inputFileTwo.getLocation().toString();
				bam2sufix = inputFileTwo.getLocation().lastSegment();
			}
			if(bam1!=null) {
				MainCNVcompare view = new MainCNVcompare();
				view.setSelectedFileX(bam1);
				view.setSelectedFileY(bam2);
				view.setOutputFileY(bam2sufix);
				view.open();
			}
		} catch (Exception e) {
			MessageDialog.openError(shell," CNV comparator Error","Please select two BAM files");
			e.printStackTrace();
	}
		
		return null;
	}
}
