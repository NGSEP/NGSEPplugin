package net.sf.ngsep.control;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import net.sf.ngsep.view.SingleFileInputWindow;

public class SingleFilePlug extends AbstractHandler {
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		IStructuredSelection selection = (IStructuredSelection) sel;
		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof IFile) {
			IFile inputFile = (IFile) firstElement;
			String selectedFile = inputFile.getLocation().toString();
			String windowClassName = event.getCommand().getId();
			SingleFileInputWindow window;
			try {
				window = (SingleFileInputWindow) Class.forName(windowClassName).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				MessageDialog.openInformation(shell, "Error loading window class", e.getMessage());
				throw new ExecutionException(e.getMessage(),e);
			}
			window.setSelectedFile(selectedFile);
			try {	
				window.open();
			} catch (Exception e) {
				MessageDialog.openInformation(shell, "Error opening window", e.getMessage());
				throw new ExecutionException(e.getMessage(),e); 
			}
		} else {
			MessageDialog.openInformation(shell, "Info", "Please select a single file");
		}
		return null;
	}
}
