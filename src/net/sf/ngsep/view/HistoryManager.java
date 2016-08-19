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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 
 * @author Juan Camilo Quintero
 *
 */
public class HistoryManager {

	
	public static String createPathRecordGeneral(String nameRoute) {
		return nameRoute + File.separator + "References.projectNGSEP";
	}

	// This method receives as a parameter the path to the project and added
	// References a file with extension. INI. This file will contain the history
	// of the last reference used
	public static String createPathRecordGff3(String route) {
		return route + File.separator + "ReferencesGff3.projectNGSEP";
	}

	// This method receives as a parameter the path to the project and added
	// ReferencesMAp a file with extension. INI. This file will contain the
	// history of the last reference used
	public static String createPathRecordMap(String route) {
		return route + File.separator + "ReferencesMap.projectNGSEP";
	}

	public static File createPathRecordVCF(String directoryProject) {
		return new File(directoryProject + File.separator + "HistoryFileVCF.ini");
	}

	// this method as a parameter I get a file and verify that it exists and if
	// there reads each line of the
	// file and save the result set me on a string, which serve to have the
	// reference file path.
	public static String getPathRecordReference(String strFile) throws IOException {
		File file = new File(strFile);
		if (!file.exists())
			return null;
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			return br.readLine();
		} finally {
			if (fr != null)
				fr.close();
		}
	}
	
	// this method as a parameter I get the project path and the path to the
	// reference file and saved to a text file
	public static void createPathRecordFiles(String strFile, String ref) throws FileNotFoundException {
		// routesrt = routeForHistory(strFile, pathFile);
		PrintWriter outFileReferencesHistory = new PrintWriter(new FileOutputStream(strFile));
		outFileReferencesHistory.print(ref);
		outFileReferencesHistory.flush();
		outFileReferencesHistory.close();
	}

}
