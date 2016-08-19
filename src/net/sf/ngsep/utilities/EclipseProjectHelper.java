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
import java.io.IOException;

/**
 * 
 * @author Jorge Duitama
 *
 */
public class EclipseProjectHelper {
	public static boolean containsEclipseProjectFile(File directory) {
		if (!directory.isDirectory())
			return false;
		if (directory.exists()) {
			File[] content = directory.listFiles();
			for (File f : content) {
				if (".project".equals(f.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	public static String findProjectDirectory(String filename)
			throws IOException {
		File f = new File(filename);
		while (f != null && !containsEclipseProjectFile(f)) {
			f = f.getParentFile();
		}
		if (f != null)
			return f.getAbsolutePath();
		throw new IOException(
				"Can not find eclipse project for the chosen output directory. Please use as output a directory located within an eclipse project.");
	}
}
