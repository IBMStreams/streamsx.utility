package com.ibm.streamsx.ant;
/*
** Licensed Materials - Property of IBM
** Copyright IBM Corp. 2013
** US Government Users Restricted Rights - Use, duplication or
** disclosure restricted by GSA ADP Schedule Contract with
** IBM Corp.
*/

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.Commandline;

/**
 * Class to execute utilities from ${streams.install}/bin
 */
public abstract class ExecBasedTask extends Task {
	
	private String streamsHome;
	private String executablePath;
	private final String executable;
	private ExecTask exec;
	private File dir;
	
	protected ExecBasedTask(String executable) {
		this.executable = executable;
	}
	
	public File getDir() {
		return dir;
	}

	public void setDir(File dir) {
		this.dir = dir;
	}

	@Override
	public void init() {
		
		streamsHome = getProject().getProperty("streams.install");
		File sh = new File(streamsHome);
		if (!sh.exists() || !sh.isDirectory())
			throw new BuildException("streams.install does not point to a valid directory: " +
					sh.getPath());
		
		File bin = new File(sh, "bin");
		File ep = new File(bin, executable);
		
		if (!ep.exists() || !ep.isFile())
			throw new BuildException("streams.install does not contain a valid command: " +
					ep.getPath());
		
		executablePath = ep.getAbsolutePath();
		
		exec = (ExecTask) getProject().createTask("exec");
	}
	
	@Override
	public void execute() throws BuildException {
		
		validateExecuteSetup();
		
		if (!checkExecute())
			return;
		
		executeAdditionalTasks();
		
		if (getDir() != null)
		    exec.setDir(getDir());
		exec.setTaskType(getTaskType());
		exec.setTaskName(getTaskName());
		
		exec.setExecutable(executablePath);
		exec.setFailonerror(true);
		
		setArguments(exec);
		
		exec.execute();
	}
	
	public Commandline.Argument createArg() {
		return exec.createArg();
	}
		
	protected abstract void validateExecuteSetup();
	
	protected abstract boolean checkExecute();
	
	protected abstract void setArguments(ExecTask exec);
	
	protected void executeAdditionalTasks() throws BuildException{}
}
