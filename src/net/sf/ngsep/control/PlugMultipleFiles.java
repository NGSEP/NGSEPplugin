package net.sf.ngsep.control;

import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import net.sf.ngsep.utilities.LoggingHelper;
import net.sf.ngsep.view.MultipleFilesInputWindow;


public class PlugMultipleFiles extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		IStructuredSelection selection = (IStructuredSelection) sel;
		Iterator<?> it = selection.iterator();
		Set<String> files = new TreeSet<>();
		while(it.hasNext()) {
			Object selectedItem = it.next();
			if (selectedItem instanceof IFolder) {
				IFolder ifolder = (IFolder) selectedItem;
				IPath path = ifolder.getLocation();
				if(path==null) {
					MessageDialog.openError(shell, "Error", "Error loading item "+ifolder.getName());
					continue;
				}
				File folderFile = new File(path.toString());
				if (!folderFile.exists()) continue;
				File [] enclosedFiles = folderFile.listFiles();
				for(int i=0;i<enclosedFiles.length;i++) {
					files.add(enclosedFiles[i].getAbsolutePath());
				}
			} else if (selectedItem instanceof IFile) {
				IFile iFile = (IFile) selectedItem;
				IPath path = iFile.getLocation();
				if(path==null) {
					MessageDialog.openError(shell, "Error", "Error loading item "+iFile.getName());
					continue;
				}
				File file = new File(path.toString());
				if (!file.exists()) continue;
				files.add(file.getAbsolutePath());
			}
		}
		if(files.size()==0) {
			MessageDialog.openError(shell, "Error", "Please select one or more folders or files");
			return null;
		}
		String windowClassName = event.getCommand().getId();
		MultipleFilesInputWindow window;
		try {
			window = (MultipleFilesInputWindow) Class.forName(windowClassName).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			MessageDialog.openError(shell, "Error loading window class", e.getMessage());
			throw new ExecutionException(e.getMessage(),e);
		}
		window.setSelectedFiles(files);
		try {
			window.open();
		} catch (Exception e) {
			String message = LoggingHelper.serializeException(e);
			MessageDialog.openError(shell, "Error loading window", message);
		}
		return null;
	}

	

}
