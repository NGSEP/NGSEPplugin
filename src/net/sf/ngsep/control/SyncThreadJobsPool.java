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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

/**
 * 
 * @author Jorge Duitama, Daniel Cruz, Juan Camilo Quintero
 *
 */
public class SyncThreadJobsPool implements Runnable {

	private int numProcessors;
	private List<Job> allJobs;
	private Queue<Job> pendingJobs = new LinkedList<Job>();
	private boolean stopFail;
	private Map<Integer, String> jobErrors = new TreeMap<Integer, String>();
	
	
	

	public SyncThreadJobsPool(int numberProcessor,List<Job> allJobs) {
		this.allJobs = new ArrayList<Job>(allJobs);
		this.numProcessors = numberProcessor;
		this.pendingJobs.addAll(allJobs);
	}

	@Override
	public void run() {
		
		String failMessage;
		String failSample;
		
		
		if(pendingJobs.size()==0) return;
		Job [] runningJobs =  new Job [numProcessors];
		for(int i=0;i<numProcessors && pendingJobs.size()>0;i++) {
			Job job = pendingJobs.poll();
			
			runningJobs [i] = job;
			job.schedule();
		}
		
		while(true) {
			try {
				Thread.sleep(60000);
//				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
				//TODO: Do something better
			}
			int numNull = 0;
			for(int i=0;i<runningJobs.length;i++) {
				Job job = runningJobs[i];
				if(job == null) {
					numNull++;
				} else {
					if(job.getResult()!=null) {
						
						if(job.getResult().getSeverity() == IStatus.ERROR || job.getResult().getSeverity() == IStatus.CANCEL){
							failMessage = job.getResult().getMessage();
							failSample = job.getResult().getPlugin();
							System.out.println(failMessage+". Sample id: "+failSample);
							int id = getId(job);
							if(id!=-1) jobErrors.put(id, failMessage);
							else System.err.println("Internal error. Job with sample id "+failSample+" not found in the list");
							if(stopFail) return;
						}	
						Job nextJob = pendingJobs.poll();
						runningJobs[i] = nextJob;
						if(nextJob !=null) nextJob.schedule();
						else numNull++;
					}
				}
			}
			if(numNull == numProcessors) return;
		}
	}

	/**
	 * @param job
	 * @return
	 */
	private int getId(Job job) {
		for(int i=0;i<allJobs.size();i++) {
			if(job == allJobs.get(i)) return i;
		}
		return -1;
	}

	public int getNumProcessors() {
		return numProcessors;
	}

	public void setNumProcessors(int numProcessors) {
		this.numProcessors = numProcessors;
	}

	public Queue<Job> getPendingJobs() {
		return pendingJobs;
	}
	
	public int getNumPendingJobs() {
		return pendingJobs.size();
	}

	public boolean isStopFail() {
		return stopFail;
	}

	public void setStopFail(boolean stopFail) {
		this.stopFail = stopFail;
	}

	

	public Map<Integer, String> getJobErrors() {
		return jobErrors;
	}

}
