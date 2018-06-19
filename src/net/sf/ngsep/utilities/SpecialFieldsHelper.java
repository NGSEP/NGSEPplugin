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
package net.sf.ngsep.utilities;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Daniel Cruz, Juan Camilo Quintero
 *
 */
public class SpecialFieldsHelper {
	
	private static final String OS = System.getProperty("os.name").toLowerCase();
	

	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);

	}
	public static void updateFileTextBox (Shell shell, int method, String suggestedFile, Text dest) {
		FileDialog fileDialog = new FileDialog(shell, method);
		suggest(fileDialog, method, suggestedFile, dest);
		String out = fileDialog.open();
		if(out!=null) dest.setText(out);
	}
	
	public static void updateDirectoryTextBox (Shell shell, int method, String suggestedFile, Text dest) {
		DirectoryDialog dirDialog = new DirectoryDialog(shell, method);
		suggest(dirDialog, method, suggestedFile, dest);
		String out = dirDialog.open();
		if(out!=null) dest.setText(out);
	}
	
	private static void suggest(Object dialog, int method, String suggestedFile, Text dest) {
		File suggestedDirectory = null;
		String suggestedName = "";
		//Try first with the current selected file
		if(dest.getText()!=null && dest.getText().length()>0) {
			File file = new File(dest.getText());
			if (file.exists() && file.isDirectory()) {
				suggestedDirectory = file;
			} else {
				suggestedDirectory = file.getParentFile();
				if(method == SWT.SAVE) suggestedName = file.getName();
			}
		}
		if(suggestedDirectory == null ) {
			File file = new File(suggestedFile);
			if (file.exists() && file.isDirectory()) {
				suggestedDirectory = file;
			} else {
				suggestedDirectory = file.getParentFile();
				if(method == SWT.SAVE) suggestedName = file.getName();
			}
		}
		if(dialog instanceof FileDialog) {
			FileDialog fileDialog = (FileDialog)dialog;
			if(suggestedDirectory!=null && suggestedDirectory.exists()) {
				fileDialog.setFilterPath(suggestedDirectory.getAbsolutePath());
			}
			if(suggestedName.length()>0) fileDialog.setFileName(suggestedName);
		} else if (dialog instanceof DirectoryDialog) {
			DirectoryDialog dirDialog = (DirectoryDialog)dialog;
			if(suggestedDirectory!=null && suggestedDirectory.exists()) {
				dirDialog.setFilterPath(suggestedDirectory.getAbsolutePath());
			}
		}
	}
	
	public static String getPathFile(String routeFile){
		File outputFile=new File(routeFile);
		return outputFile.getAbsolutePath();
	}
	
	public static String getFileName(String routeFile){
		return routeFile.substring(routeFile.lastIndexOf(File.separator)+1,routeFile.length());
	}
	
	public static String buildSuggestedOutputPrefix(String file) {
		if(file == null) return "";
		String srtOutputPrefix = file;
		if (srtOutputPrefix.contains(".")) {
			srtOutputPrefix = srtOutputPrefix.substring(0,srtOutputPrefix.lastIndexOf("."));
			if(file.endsWith(".gz")) srtOutputPrefix = srtOutputPrefix.substring(0,srtOutputPrefix.lastIndexOf("."));
		}
		return srtOutputPrefix;
	}
	
	public static String maskWhiteSpaces(String filename) {
		String [] tokens = filename.split(" ");
		if(tokens.length==1) return filename;
		if(isWindows()) {
			return "\""+filename+"\"";
		}
		StringBuilder b = new StringBuilder();
		for(int i=0;i<tokens.length;i++) {
			if(i>0) b.append("\\ ");
			b.append(tokens[i]);
		}
		return b.toString();
	}
}
