package net.sf.ngsep.control;

import java.io.PrintStream;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import net.sf.ngsep.utilities.DefaultProgressNotifier;
import net.sf.ngsep.utilities.LoggingHelper;
import ngsep.discovery.MultisampleVariantsDetector;

public class SyncMultisampleVariantsDetector extends Job {

	//Instance of the model class with the optional parameters already set
	private MultisampleVariantsDetector instance;

	//Attributes to set the logger
	private String logName;
	
	//Name for the progress bar
	private String nameProgressBar;
	
	/**
	 * Creates a Single Individual Simulator Job with the given name
	 * @param name of the job
	 */
	public SyncMultisampleVariantsDetector(String name) {
		super(name);
	}
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		FileHandler logFile = null;
		PrintStream out = null;
		Logger log = null;
		
		try {
			//Create log
			logFile = new FileHandler(logName, false);
			log = LoggingHelper.createLogger(logName, logFile);
			instance.setLog(log);
			
			//Start progress bar
			monitor.beginTask(nameProgressBar, 50000);
			
			//Create progress notifier and set it to listen the model class 
			instance.setProgressNotifier(new DefaultProgressNotifier(monitor));
			instance.findVariants();
			log.info("Process finished");
			monitor.done();
		} catch (Exception e) {
			log.info("Error running Single Individual Simulator: ");
			String message = LoggingHelper.serializeException(e);
			log.severe(message);
		} finally {
			LoggingHelper.closeLogger(log);
			if (out != null) {
				out.flush();
				out.close();
			}
		}
		return Status.OK_STATUS;
	}
	/**
	 * @return the instance
	 */
	public MultisampleVariantsDetector getInstance() {
		return instance;
	}
	/**
	 * @param instance the instance to set
	 */
	public void setInstance(MultisampleVariantsDetector instance) {
		this.instance = instance;
	}
	/**
	 * @return the logName
	 */
	public String getLogName() {
		return logName;
	}
	/**
	 * @param logName the logName to set
	 */
	public void setLogName(String logName) {
		this.logName = logName;
	}
	/**
	 * @return the nameProgressBar
	 */
	public String getNameProgressBar() {
		return nameProgressBar;
	}
	/**
	 * @param nameProgressBar the nameProgressBar to set
	 */
	public void setNameProgressBar(String nameProgressBar) {
		this.nameProgressBar = nameProgressBar;
	}
	
	

}
