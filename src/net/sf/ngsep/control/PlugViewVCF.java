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

import net.sf.ngsep.view.MainVCFView;

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
 * @author Jorge Duitama
 *
 */
public class PlugViewVCF extends AbstractHandler {

	// This class performs the task of allowing select a project file in which
	// right-click menu and options appear NGSEP
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		IStructuredSelection selection = (IStructuredSelection) sel;
		IFile inputFile;
		Object firstElement = selection.getFirstElement();
		// Address VCF File
		String resAli;
		MainVCFView window;
		if (firstElement instanceof IFile) {
			try {
				inputFile = (IFile) firstElement;
				resAli = inputFile.getLocation().toString();
				window = new MainVCFView();
				window.setAliFile(resAli);
				window.openVcf();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			MessageDialog.openInformation(shell, "Info",
					"Please select a VCF file");
		}

		return null;
	}

}
