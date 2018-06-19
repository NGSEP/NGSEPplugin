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

/**
 * 
 * @author Jorge Duitama
 *
 */
public class EclipseProjectHelper {
	private static boolean containsEclipseProjectFile(File f) {
		if (!f.exists() || !f.isDirectory()) return false;
		for (File f2 : f.listFiles()) {
			if (".project".equals(f2.getName())) {
				return true;
			}
		}
		return false;
	}

	public static String findProjectDirectory(String filename) {
		for (File f = new File(filename);f != null;f = f.getParentFile()) {
			if(containsEclipseProjectFile(f)) return f.getAbsolutePath();
			
		}
		return null;
	}
}
