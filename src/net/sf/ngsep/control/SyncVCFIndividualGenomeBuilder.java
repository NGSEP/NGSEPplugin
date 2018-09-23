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
import ngsep.vcf.VCFIndividualGenomeBuilder;

public class SyncVCFIndividualGenomeBuilder extends Job {

	private VCFIndividualGenomeBuilder instance = new VCFIndividualGenomeBuilder();
	//Parameters to set before the execution of the process
	private String inputFile=null;
	
	private String outputFile = null;
	
	//Attributes to set the logger
	private String logName;
	
	//Name for the progress bar
	private String nameProgressBar;
	
	public SyncVCFIndividualGenomeBuilder(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		//Create log
		FileHandler logFile = null;
		PrintStream out = null;
		Logger log = null;
		try {
			logFile = new FileHandler(logName, false);
			log = LoggingHelper.createLogger(logName, logFile);
			//Start progress bar
			monitor.beginTask(getNameProgressBar(), 1000);
			log.info("Started neighbor joining calculation");
			
			instance.setLog(log);
			instance.setProgressNotifier(new DefaultProgressNotifier(monitor));
			if (inputFile != null) {
				out = new PrintStream(outputFile);
				instance.makeGenomeFromVCF(inputFile, out);
			}
			log.info("Process finished");
			monitor.done();
		} catch (Exception e) {
			log.info("Error Calculating neighbor joining: ");
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
	public VCFIndividualGenomeBuilder getInstance() {
		return instance;
	}

	/**
	 * @param instance the instance to set
	 */
	public void setInstance(VCFIndividualGenomeBuilder instance) {
		this.instance = instance;
	}

	/**
	 * @return the inputFile
	 */
	public String getInputFile() {
		return inputFile;
	}

	/**
	 * @param inputFile the inputFile to set
	 */
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	/**
	 * @return the outputFile
	 */
	public String getOutputFile() {
		return outputFile;
	}

	/**
	 * @param outputFile the outputFile to set
	 */
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
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
