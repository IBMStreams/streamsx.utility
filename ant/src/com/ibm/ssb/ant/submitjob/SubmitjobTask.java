package com.ibm.ssb.ant.submitjob;
/*
** Licensed Materials - Property of IBM
** Copyright IBM Corp. 2013
** US Government Users Restricted Rights - Use, duplication or
** disclosure restricted by GSA ADP Schedule Contract with
** IBM Corp.
*/

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Parameter;

import com.ibm.ssb.ant.AbstractStreamtoolTask;

public class SubmitjobTask extends AbstractStreamtoolTask {
    public SubmitjobTask() {
        super("submitjob");
    }
    
    /*
     * State defined for this task.
     */
    private File tmpFile;
    
    /*
     * Setters for all the attributes, automatically called by ant.
     */
    private File outfile;
    private Applications applications;
    
    public void addApplications(Applications applications) {
    	if (this.applications != null) {
    		throw new BuildException("Only a single applications nested element is supported");
    	}
    	this.applications = applications;

    }

    public void setOutfile(File outfile) {
		this.outfile = outfile;
	}

	public File getOutfile() {
		return outfile;
	}

	protected void validateExecuteSetup() {
    }

    protected boolean checkExecute() {

        return true;
    }

	@Override
	protected void setStreamtoolArguments(ExecTask exec) {
		
		if (getOutfile() != null) {
			exec.createArg().setValue("--outfile");
			exec.createArg().setValue(getOutfile().getAbsolutePath());
		}
			
		
		List<File> adlFiles = new ArrayList<File>();
		for (FileSet  adlSet : applications.getAdls()) {
			String[] files = adlSet.getDirectoryScanner(getProject()).getIncludedFiles();
			
			for (String file : files) {			
				adlFiles.add(new File(adlSet.getDir(), file));
			}
		}
		if (adlFiles.size() == 1)
			singleAdl(exec, adlFiles.get(0));
		else
			multiAdl(exec, adlFiles);
	}
	
	private void singleAdl(ExecTask exec, File adl) {
		for (Parameter parameter : applications.getParameters()) {
			exec.createArg().setValue("-P");
			exec.createArg().setValue(parameter.getName() + "=" + parameter.getValue());			
		}
		for (Parameter parameter : applications.getConfigs()) {
			exec.createArg().setValue("--config");
			exec.createArg().setValue(parameter.getName() + "=" + parameter.getValue());			
		}
		
		exec.createArg().setFile(adl);
	}
	private void multiAdl(ExecTask exec, List<File> adls) {
		final String params = getParametersString();
		final String configs = getConfigsString();
		
		try {
			tmpFile = File.createTempFile("submit", ".jobs");
			//tmpFile.deleteOnExit();
			FileWriter fw = new FileWriter(tmpFile);
			for (File adl : adls) {
				fw.append(params);
				fw.append(configs);
				fw.append(adl.getAbsolutePath());
				fw.append("\n");
			}
			fw.flush();
			fw.close();
			exec.createArg().setValue("--file");
			exec.createArg().setValue(tmpFile.getPath());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getParametersString() {
		return getGenericParametersString("-P", applications.getParameters());
	}
	private String getConfigsString() {
		return getGenericParametersString("--config", applications.getConfigs());
	}
	
	private String getGenericParametersString(final String lead, final List<Parameter> parameters) {
		StringBuilder sb = new StringBuilder();
		for (Parameter parameter : parameters) {
			sb.append(lead);
			sb.append(" ");
			sb.append(parameter.getName());
			sb.append("=");
			sb.append(parameter.getValue());
			sb.append(" ");
		}
		return sb.toString();
	}
	
	@Override
	public void execute() {
		super.execute();
//		if (tmpFile != null)
//			tmpFile.delete();
	}
}
