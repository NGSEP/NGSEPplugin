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

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.widgets.Shell;

import ngsep.genome.ReferenceGenome;
import ngsep.transcriptome.Transcriptome;
import ngsep.transcriptome.io.GFF3TranscriptomeHandler;

/**
 * 
 * @author Jorge Duitama
 *
 */
public class ReferenceGenomesFactory {
	private Map<String, ReferenceGenome> genomes = new TreeMap<String, ReferenceGenome>();
	private Map<String, Transcriptome> transcriptomes = new TreeMap<String, Transcriptome>();
	private static ReferenceGenomesFactory instance = new ReferenceGenomesFactory();
	private ReferenceGenomesFactory () {
		
	}
	public static ReferenceGenomesFactory getInstance() {
		return instance;
	}
	public ReferenceGenome getGenome(String filename, Shell shell) throws IOException {
		ReferenceGenome genome = genomes.get(filename);
		if(genome == null) {
			genome = loadGenomeFromDialog(filename,shell);
			genomes.put(filename, genome);
		}
		return genome;
	}
	private ReferenceGenome loadGenomeFromDialog(String filename, Shell shell) throws IOException {
		
		DialLoadReference dialReference = new DialLoadReference(shell);
		dialReference.setFile(filename);
		ReferenceGenome genome = (ReferenceGenome) dialReference.open();
		if(genome == null) throw dialReference.getException();
		return genome;
	}
	public Transcriptome getTranscriptome(String filename, Shell shell) throws IOException {
		Transcriptome t = transcriptomes.get(filename);
		if(t == null) {
			GFF3TranscriptomeHandler handler = new GFF3TranscriptomeHandler();
			t = handler.loadMap(filename);
			transcriptomes.put(filename, t);
		}
		return t;
	}
}
