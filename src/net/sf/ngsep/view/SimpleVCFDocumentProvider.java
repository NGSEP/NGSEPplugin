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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;

/**
 * 
 * @author Jorge Duitama
 *
 */
public class SimpleVCFDocumentProvider extends FileDocumentProvider {
	public static final int LINES_DISPLAYED = 100;
	@Override
	protected boolean setDocumentContent(IDocument document, IEditorInput editorInput, String encoding) throws CoreException {
		if (editorInput instanceof IStorageEditorInput) {
			IStorage storage= ((IStorageEditorInput) editorInput).getStorage();
			InputStream stream= storage.getContents();
			if (encoding == null) encoding= getDefaultEncoding();
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(stream,encoding),DEFAULT_FILE_SIZE);
				StringBuffer buffer= new StringBuffer(DEFAULT_FILE_SIZE);
				String separator = System.getProperty("line.separator"); 
				String line = in.readLine();
				for (int i = 0; i < LINES_DISPLAYED && line != null; i++) {
					buffer.append(line);
					buffer.append(separator);
					line = in.readLine();
				}
				
				
				document.set(buffer.toString());
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				//TODO: Allow loading more lines
				try {
					if(in!=null) in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return true;
		}
		return false;
	}
	@Override
	public boolean isModifiable(Object element) {
		return false;
	}
	
	
	
	
	

}
