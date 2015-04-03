package com.ibm.ssb.ant.adl;
/*
** Licensed Materials - Property of IBM
** Copyright IBM Corp. 2013
** US Government Users Restricted Rights - Use, duplication or
** disclosure restricted by GSA ADP Schedule Contract with
** IBM Corp.
*/

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class MapHostPoolTagTask extends Task {
	
	private File source;
	private File target;
	private final List<MapTag> tags = new ArrayList<MapTag>();
	
	public void setSource(File source) {
		this.source = source;
	}
	public void setTarget(File target) {
		this.target = target;
	}
	
    public MapTag createTag() {
    	MapTag tag = new MapTag();
        tags.add(tag);
        return tag;
    }
	
	@Override
	public void execute() throws BuildException {
		
		try {
			ADL adl = new ADL(source);
			Map<String,String> mapping = new HashMap<String,String>();
			for (MapTag tag : tags)
				mapping.put(tag.old, tag._new);
			
			adl.hostPools(mapping);
			
			adl.write(target);
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}
	
	public static class MapTag {
		private String _new;
		private String old;
		
		public void setNew(String _new) {
			this._new = _new;
		}
		public void  setOld(String old) {
			this.old = old;
		}
	}

}