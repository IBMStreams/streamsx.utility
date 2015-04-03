package com.ibm.streamsx.ant;
/*
** Licensed Materials - Property of IBM
** Copyright IBM Corp. 2013
** US Government Users Restricted Rights - Use, duplication or
** disclosure restricted by GSA ADP Schedule Contract with
** IBM Corp.
*/

import org.apache.tools.ant.taskdefs.ExecTask;


public abstract class AbstractStreamtoolTask extends ExecBasedTask {
	
	private final String stcmd;
	private String instance;

    /*
     * State defined for this task.
     */

    public AbstractStreamtoolTask(String stcmd) {
        super("streamtool");
        this.stcmd = stcmd;
    }
    
    /*
     * Setters for all the attributes, automatically called by ant.
     */

    public void setInstance(String instance) {
        this.instance = instance;
    }
    
    public String getInstance() {
    	if (instance != null)
    		return instance;
        return getProject().getProperty("streams.instance");
    }
    
    protected boolean checkExecute() {
        return true;
    }
    
    @Override
    protected final void setArguments(ExecTask exec) {
    	exec.createArg().setValue(stcmd);
    	String instance = getInstance();
    	if (instance != null) {
    		exec.createArg().setValue("--instance-id");
    		exec.createArg().setValue(instance);
    	}
    	setStreamtoolArguments(exec);
    }
    
    protected abstract void setStreamtoolArguments(ExecTask exec);
}
