package net.sf.ngsep.view;

import java.util.Set;

public interface MultipleFilesInputWindow {
	/**
	 * Changes the selected files
	 * @param selectedFiles
	 */
	public void setSelectedFiles(Set<String> selectedFiles);
	/**
	 * Opens the window
	 */
	public void open();
}
