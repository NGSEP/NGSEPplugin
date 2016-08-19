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
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import ngsep.vcf.VCFFileHeader;
import ngsep.vcf.VCFFileReader;
import ngsep.vcf.VCFFileWriter;
import ngsep.vcf.VCFRecord;


public class MainVCFView extends ViewPart{
	private String aliFile;
	private Label label;

	public void openVcf() throws IOException{
		String vcfFile = getAliFile();
		if (vcfFile != null) {
			if (new File(vcfFile).exists()) {
				VCFFileWriter writer = new VCFFileWriter();
				VCFFileReader reader = new VCFFileReader(vcfFile);
				VCFFileHeader header = reader.getHeader();
				Iterator<VCFRecord> it = reader.iterator();
				PrintStream out = new PrintStream(System.out);
				writer.printHeader(header, out);
				int countLines = 0;
				while (it.hasNext()) {
					VCFRecord record = it.next();
					countLines++;
					if (countLines < 25) {
						writer.printVCFRecord(record, out);
					} else {
						break;
					}
				}
			     reader.close();
			     out.close();
			}
		}

	}
	
	
	public String getAliFile() {
		return aliFile;
	}

	public void setAliFile(String aliFile) {
		this.aliFile = aliFile;
	}

	 public void createPartControl(Composite parent) {
         label = new Label(parent, 0);
         try {
			openVcf();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }

	 public void setFocus() {
         label.setFocus();
	 }
	 

}
