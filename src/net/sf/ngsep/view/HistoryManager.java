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
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import java.util.Set;

import net.sf.ngsep.utilities.EclipseProjectHelper;

/**
 * @author Juan Camilo Quintero
 * @author Jorge Duitama
 *
 */
public class HistoryManager {
	
	private static final String HISTORY_FILENAME = "NGSEPFilesHistory.txt";
	
	public static final String KEY_BOWTIE2_INDEX = "Bowtie2Index";
	public static final String KEY_REFERENCE_FILE = "Reference";
	public static final String KEY_TRANSCRIPTOME_FILE = "Transcriptome";
	public static final String KEY_STRS_FILE = "STRS";
	
	/**
	 * Retrieves from the history the path with the given key
	 * @param searchFile File within the project to use as pivot to search for history
	 * @param key to search within the history
	 * @return String path associated with the given key. Null if the path can not be found
	 */
	public static String getHistory(String searchFile, String key) {
		String directoryProject = EclipseProjectHelper.findProjectDirectory(searchFile);
		if(directoryProject==null) return null;
		String historyFile = directoryProject+ File.separator + HISTORY_FILENAME;
		Properties p = new Properties();
		try (FileReader reader = new FileReader(historyFile)) {
			p.load(reader);
		} catch (IOException e) {
			return null;
		}
		return p.getProperty(key);
	}
	/**
	 * Saves the given path with the given property
	 * @param key to store the given path 
	 * @param path to save
	 */
	public static void saveInHistory(String key, String path) {
		if(path==null || path.length()==0) return;
		String directoryProject = EclipseProjectHelper.findProjectDirectory(path);
		if(directoryProject==null) return;
		String historyFile = directoryProject+ File.separator + HISTORY_FILENAME;
		Properties p = new Properties();
		if((new File(historyFile)).exists()) {
			try (FileReader reader = new FileReader(historyFile)) {
				p.load(reader);
			} catch (IOException e) {
				return;
			}
		}
		if(path.equals(p.getProperty(key))) return;
		p.setProperty(key, path);
		try (PrintStream out=new PrintStream(historyFile)) {
			Set<?> keys = p.keySet();
			for(Object key2:keys) {
				out.println(key2.toString()+"="+p.getProperty(key2.toString()));
			}
		} catch (IOException e) {
			return;
		}
	}
}
