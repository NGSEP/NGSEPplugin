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
package net.sf.ngsep.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;

import net.sf.ngsep.utilities.EclipseProjectHelper;

/**
 * 
 * @author Jorge Duitama
 *
 */
public class SamplesDatabase {
	// Databases indexed by the absolute path of the project directory
	private static Map<String, SamplesDatabase> databases = new TreeMap<String, SamplesDatabase>();
	public static final String DEFAULT_DATABASE_FILENAME = "HistoryFileVCF.ini";
	private Map<String, SampleData> samples;
	private String databaseFilename;

	
	public static void updateSample(SampleData sampleData) throws IOException {
		//TODO: Move this validation to variants detector and multivariantsDetector
		String pDBAM = EclipseProjectHelper.findProjectDirectory(sampleData.getSortedBamFile());
		SamplesDatabase db = findDatabase(pDBAM);
		db.updateSampleDB(sampleData);
	}

	private static SamplesDatabase findDatabase(String projectDirectory) throws IOException {
		SamplesDatabase db = databases.get(projectDirectory);
		if (db == null) {
			db = new SamplesDatabase(projectDirectory + File.separator+ DEFAULT_DATABASE_FILENAME);
			databases.put(projectDirectory, db);
		}
		return db;
	}
	
	public static SamplesDatabase getDatabase (String projectDirectory) throws IOException {
		return findDatabase(projectDirectory);
	}

	public SamplesDatabase (String databaseFilename) throws IOException {
		this.databaseFilename = databaseFilename;
		loadData();
	}
	private synchronized void updateSampleDB(SampleData data) throws IOException {
		SampleData dbData = samples.get(data.getSampleId());
		if (dbData == null) {
			samples.put(data.getSampleId(), data);	
		} else {
			if(data.getVcfFile()!=null) dbData.setVcfFile(data.getVcfFile());
			if(data.getSortedBamFile()!=null) dbData.setSortedBamFile(data.getSortedBamFile());
			if(data.getReferenceFile()!=null) dbData.setReferenceFile(data.getReferenceFile());
			if(data.getVariantsFile()!=null) dbData.setVariantsFile(data.getVariantsFile());
		}
		saveData();
	}
	
	public Map<String,SampleData> getSamplesWithValidData() {
		Map<String,SampleData> answer = new TreeMap<String, SampleData>();
		for(Map.Entry<String, SampleData> entry:samples.entrySet()) {
			SampleData sample = entry.getValue();
			if (sample.getReferenceFile().length()>0 && sample.getSampleId().length()>0 && sample.getSortedBamFile().length()>0 && sample.getVcfFile().length()>0) {
				answer.put(entry.getKey(), entry.getValue());
			}
		}
		return answer;
	}

	private void loadData() throws IOException {
		samples = new TreeMap<String, SampleData>();
		File f = new File(databaseFilename);
		if (!f.exists()) {
			f.createNewFile();
			return;
		}
		FileInputStream fis = null;
		BufferedReader in = null;
		try {
			fis = new FileInputStream(f);
			in = new BufferedReader(new InputStreamReader(fis));
			String line = in.readLine();
			while (line != null) {
				String[] items = line.split("\t");
				SampleData sample = new SampleData();
				try {
					sample.setSampleId(items[0]);
					if(!"-".equals(items[1])) sample.setVcfFile(items[1]);
					if(!"-".equals(items[2])) sample.setSortedBamFile(items[2]);
					if(!"-".equals(items[3])) sample.setReferenceFile(items[3]);
				} catch (ArrayIndexOutOfBoundsException e) {
					throw new IOException("Malformed database file "+databaseFilename,e);
				}
				// For backwards compatibility with older versions
				if (items.length > 4 && !"-".equals(items[4])) sample.setVariantsFile(items[4]);
				samples.put(items[0], sample);
				line = in.readLine();
			}
		} finally {
			if(in!=null)in.close();
			if(fis!=null)fis.close();
		}
		
	}
	private void saveData () throws IOException {
		PrintStream out = new PrintStream(databaseFilename);
		for(SampleData sd:samples.values()) {
			out.print(sd.getSampleId());
			out.print("\t"+((sd.getVcfFile()!=null)?sd.getVcfFile():"-"));
			out.print("\t"+((sd.getSortedBamFile()!=null)?sd.getSortedBamFile():"-"));
			out.print("\t"+((sd.getReferenceFile()!=null)?sd.getReferenceFile():"-"));
			out.println("\t"+((sd.getVariantsFile()!=null)?sd.getVariantsFile():"-"));
		}
		out.flush();
		out.close();
	}

}