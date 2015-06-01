package com.ibm.streamsx.ant.toolkit;
/*
** Licensed Materials - Property of IBM
** Copyright IBM Corp. 2013
** US Government Users Restricted Rights - Use, duplication or
** disclosure restricted by GSA ADP Schedule Contract with
** IBM Corp.
*/

import java.io.File;

import org.apache.tools.ant.taskdefs.ExecTask;

import com.ibm.streamsx.ant.ExecBasedTask;

public class IndexToolkitTask extends ExecBasedTask {
    public IndexToolkitTask() {
        super("spl-make-toolkit");
    }
    
    /*
     * State defined for this task.
     */
    
    private File location;
    private boolean clean;
    
    /*
     * Setters for all the attributes, automatically called by ant.
     */

    protected void validateExecuteSetup() {
    }

    protected boolean checkExecute() {

        return true;
    }

	@Override
	protected void setArguments(ExecTask exec) {
		
		exec.createArg().setValue("--directory");
		exec.createArg().setValue(getToolkitLocation().getAbsolutePath());
		
		if (isClean()) {
			exec.createArg().setValue("--clean");
		}		
		exec.createArg().setValue("--no-mixed-mode-preprocessing");
	}

	public void setLocation(File location) {
		this.location = location;
	}

	public File getToolkitLocation() {
		return location;
	}

	public void setClean(boolean clean) {
		this.clean = clean;
	}

	public boolean isClean() {
		return clean;
	}
}
