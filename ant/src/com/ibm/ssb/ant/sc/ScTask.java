package com.ibm.ssb.ant.sc;
/*
** Licensed Materials - Property of IBM
** Copyright IBM Corp. 2013
** US Government Users Restricted Rights - Use, duplication or
** disclosure restricted by GSA ADP Schedule Contract with
** IBM Corp.
*/

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.taskdefs.ExecTask;

import com.ibm.ssb.ant.ExecBasedTask;
import com.ibm.ssb.ant.FileLocation;

public class ScTask extends ExecBasedTask {

    /*
     * State defined for this task.
     */
    private String mainComposite;
    private boolean clean;
    private boolean _static;
    private boolean standalone;
    private boolean optimize;
    private boolean verbose;
    private boolean noToolkitIndexing;
	private File outputDirectory;
    private File dataDirectory;
    private File checkpointDirectory;
    private StreamsTransport transport;
    private PartitionFusion fusion;
    private List<FileLocation> toolkits = new ArrayList<FileLocation>();


    public ScTask() {
        super("sc");
    }
    
    /*
     * Setters for all the attributes, automatically called by ant.
     */

    public void setMainComposite(String mainComposite) {
        this.mainComposite = mainComposite;
    }

    public void setClean(boolean clean) {
        this.clean = clean;
    }
    
	public void setNoToolkitIndexing(boolean noToolkitIndexing) {
		this.noToolkitIndexing = noToolkitIndexing;
	}

    public void setStatic(boolean _static) {
        this._static = _static;
    }

    public void setStandalone(boolean standalone) {
        this.standalone = standalone;
    }

    public void setOptimize(boolean optimize) {
        this.optimize = optimize;
    }
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
    
    public void setDataDirectory(File dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public void setCheckpointDirectory(File checkpointDirectory) {
        this.checkpointDirectory = checkpointDirectory;
    }

    public void setTransport(StreamsTransport transport) {
        this.transport = transport;
    }

    public void setFusion(PartitionFusion fusion) {
        this.fusion = fusion;
    }

    /**
     * Convert the state into arguments for the execution
     * of $STREAMS_INSTALL/bin/cs. Always use the full form
     * of the flag to make ant -verbose readable.
     */
    protected void setArguments(ExecTask exec) {
        
        

        exec.createArg().setValue("--main-composite");
        exec.createArg().setValue(mainComposite);

        if (clean)
            exec.createArg().setValue("--clean");

        if (_static)
            exec.createArg().setValue("--static-link");

        if (standalone)
            exec.createArg().setValue("--standalone-application");

        if (optimize)
            exec.createArg().setValue("--optimized-code-generation");
        if (verbose)
            exec.createArg().setValue("--verbose-mode");
        if (noToolkitIndexing)
            exec.createArg().setValue("--no-toolkit-indexing");

        if (outputDirectory != null) {
            exec.createArg().setValue("--output-directory");
            exec.createArg().setFile(outputDirectory);
        }

        if (dataDirectory != null) {
            exec.createArg().setValue("--data-directory");
            exec.createArg().setFile(dataDirectory);
        }

        if (checkpointDirectory != null) {
            exec.createArg().setValue("--checkpoint-directory");
            exec.createArg().setFile(checkpointDirectory);
        }

        if (transport != null) {
            exec.createArg().setValue("--use-transport");
            exec.createArg().setValue(transport.toString());
        }
        
        if (fusion != null) {
            exec.createArg().setValue("--part-mode");
            exec.createArg().setValue(fusion.getOption());
        }

        for (FileLocation tl : toolkits) {
            exec.createArg().setValue("--spl-path");
            exec.createArg().setFile(tl.getLocation());
        }
    }

    protected void validateExecuteSetup() {
    }

    protected boolean checkExecute() {

        return true;
    }

    public FileLocation createToolkit() {
        FileLocation toolkit = new FileLocation();
        toolkits.add(toolkit);
        return toolkit;
    }

}
