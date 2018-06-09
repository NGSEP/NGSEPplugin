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

import net.sf.ngsep.view.MainMapReads;

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
 * 
 * @author Juan Camilo Quintero
 *
 */
public class PlugMapRead extends AbstractHandler {
	@SuppressWarnings("unchecked")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		IStructuredSelection selection = (IStructuredSelection) sel;
		
		String fastq1 = null;
		String fastq2 = null;
		Iterator<Object> it = selection.iterator();
		try {
			
			if (it.hasNext()) {
				IFile inputFile = (IFile) it.next();
				fastq1 = inputFile.getLocation().toString();
			}
			if (it.hasNext() ) {
				IFile inputFileTwo = (IFile) it.next();
				fastq2 = inputFileTwo.getLocation().toString();
			}
			if(fastq1!=null) {
				MainMapReads shellMapRead = new MainMapReads(fastq1, fastq2);
				shellMapRead.open();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog
					.openInformation(shell, "Error",
							"You must select one or two files to be able to Map Reads");
		}
		return null;
	}
}
