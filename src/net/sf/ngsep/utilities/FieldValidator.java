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
import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * @author Jorge Duitama, Daniel Cruz, Juan Camilo Quintero
 *
 */
public class FieldValidator {
	
	public static final String ERROR_MANDATORY = "can not be empty";
	public static final String ERROR_NUMERIC = "should have a number";
	public static final String ERROR_INTEGER = "should have an integer number";
	public static final String ERROR_ALPHANUMERIC = "should not include special characters";
	public static final String ERROR_FILE_EMPTY = "can not be opened or is empty";
	public static final String ERROR_NUMPROCESSORS = "can not be larger than the number of processors available in the computer";
	public static final String ERROR_SAME_NAME = "the output file may not have the same name as the input";
	
	
	
	public static boolean isAlphaNumeric(String string) {
		if (string.trim().matches("-?[a-zA-Z0-9_ ]+")) {
			// is AlphaNumeric
			return true;
		} else {
			// no is AlphaNumeric
			return false;
		}
	}
	
	public static boolean isNumeric(String number, Object objclass) {
		try {
			if (objclass.getClass() == Integer.class)
				new Integer(number);

			else if (objclass.getClass() == Long.class)
				new Long(number);

			else if (objclass.getClass() == Double.class)
				new Double(number);

			else if (objclass.getClass() == Float.class)
				new Float(number);

			else
				return false;

			return true;
		}

		catch (Exception e) {
			return false;
		}
	}
	
	public static boolean isFileExistenceWithData(String strFile) {
		File archivo = new File(strFile);
		if (archivo.exists()) {
			if (archivo.length() > 1024)
				return true;
		}
		return false;
	}
	
	public static String buildMessage(String fieldName, String errorText ) {
		return fieldName + " " + errorText;
	}
	
	
	public static void paintErrors(ArrayList<String> strErros, Shell shell,String name) {
		String strErrorS = "";
		for (int i = 0; i < strErros.size(); i++) {
			strErrorS = strErrorS + "- " + strErros.get(i) + "\n";
		}
		if (!strErros.isEmpty()) {
			MessageDialog.openError(shell, name, strErrorS);
		}

	}
}
