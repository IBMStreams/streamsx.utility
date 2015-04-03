package com.ibm.streamsx.ant.submitjob;
/*
** Licensed Materials - Property of IBM
** Copyright IBM Corp. 2013
** US Government Users Restricted Rights - Use, duplication or
** disclosure restricted by GSA ADP Schedule Contract with
** IBM Corp.
*/
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Parameter;

public class Applications {
	
    private List<FileSet> adls = new ArrayList<FileSet>();
    private List<Parameter> parameters = new ArrayList<Parameter>();
    private List<Parameter> configs = new ArrayList<Parameter>();
        
    public void addFileset(FileSet fileset) {
    	adls.add(fileset);
    }
    public void addParameter(Parameter parameter) {
    	parameters.add(parameter);
    }
    public void addConfig(Parameter parameter) {
    	configs.add(parameter);
    }
    List<FileSet> getAdls() {
    	return adls;
    }
    List<Parameter> getParameters() {
    	return parameters;
    }
    List<Parameter> getConfigs() {
    	return configs;
    }
}
