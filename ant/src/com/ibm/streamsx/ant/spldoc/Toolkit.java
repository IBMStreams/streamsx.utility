package com.ibm.streamsx.ant.spldoc;
/*
** Licensed Materials - Property of IBM
** Copyright IBM Corp. 2013
** US Government Users Restricted Rights - Use, duplication or
** disclosure restricted by GSA ADP Schedule Contract with
** IBM Corp.
*/
import java.io.File;

public class Toolkit {
	
    private File toolkit;

    public void setLocation(File dir) {
    	toolkit = dir;
    }
    File getLocation() {
    	return toolkit;
    }
}
