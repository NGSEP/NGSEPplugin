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

import java.io.InputStream;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

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
public class SimpleSAMDocumentProvider extends FileDocumentProvider {
	@Override
	protected boolean setDocumentContent(IDocument document, IEditorInput editorInput, String encoding) throws CoreException {
		if (editorInput instanceof IStorageEditorInput) {
			IStorage storage= ((IStorageEditorInput) editorInput).getStorage();
			InputStream stream= storage.getContents();
			SAMFileReader reader= null;
			try {
				reader = new SAMFileReader(stream);
				SAMRecordIterator it = reader.iterator();
				StringBuffer buffer= new StringBuffer(DEFAULT_FILE_SIZE);
				for (int i = 0; i < SimpleVCFDocumentProvider.LINES_DISPLAYED && it.hasNext(); i++) {
					SAMRecord alnRecord = it.next();
					buffer.append(alnRecord.getSAMString());
				}
				document.set(buffer.toString());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				//TODO: Allow loading more lines
				try {
					if(reader!=null) reader.close();
				} catch (Exception e) {
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
