package com.ibm.ssb.ant.spldoc;
/*
** Licensed Materials - Property of IBM
** Copyright IBM Corp. 2013
** US Government Users Restricted Rights - Use, duplication or
** disclosure restricted by GSA ADP Schedule Contract with
** IBM Corp.
*/

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.ssb.ant.ExecBasedTask;

public class MakeDocTask extends ExecBasedTask {
	
	private static final String ns = "http://www.ibm.com/xmlns/prod/streams/spl/toolkitInfo";
    public MakeDocTask() {
        super("spl-make-doc");
    }
    
    /*
     * State defined for this task.
     */
    
    private File location;
    private boolean clean;
    
    private String author;
    private String title;
    
    private File outputDirectory;
    private boolean compositeGraphs;
    private boolean sourceCode;
    
    private List<Toolkit> toolkits;    

    protected void validateExecuteSetup() {
    }

    protected boolean checkExecute() {

        return true;
    }
    
    public void execute() throws BuildException {
    	super.execute();
    	if (toolkits != null) {
    		Delete delete = (Delete) getProject().createTask("delete");
    		delete.setDir(getToolkitLocation());
    		delete.setQuiet(true);
    		delete.execute();
    	}
    }
    
    @Override
    protected void executeAdditionalTasks() throws BuildException {
    	if (toolkits == null)
    		return;
    	
    	StringBuilder mergedToolkitIntro = new StringBuilder();
    	mergedToolkitIntro.append("**Contained Toolkits**\n\n");
    	
    	Copy copy = (Copy) getProject().createTask("copy");
    	
    	DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    	docBuilderFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			throw new BuildException(e1);
		}
        
     	
    	for (Toolkit toolkit : toolkits) {
    		File location =  toolkit.getLocation();
    		if (!location.isDirectory()) {
    			log("Toolkit ignored - no such directory: " + location.getAbsolutePath());
    			continue;
    		}
    		File info = new File(location, "info.xml");
    		if (!info.exists()) {
    			File[] subdirs = location.listFiles(new FileFilter() {
					
					@Override
					public boolean accept(File pathname) {
						return pathname.isDirectory() && new File(pathname, "info.xml").exists();
					}
				});
    			if (subdirs.length == 0)
       		        log("Toolkit ignored - no info.xml	: " + location.getAbsolutePath());
    			else {
    				for (File tk : subdirs) {
    					copy.add(addToolkitToCopy(mergedToolkitIntro, docBuilder, tk));
    				}   					
    			}
    			continue;   			
    		}
    		
    		FileSet set = addToolkitToCopy(mergedToolkitIntro, docBuilder,
					location);
   		    copy.add(set);
   		    
    	}
    		
    	File tmpDir;
		try {
			tmpDir = File.createTempFile("spldoc", "tks", getProject().getBaseDir());
		} catch (IOException e) {
			throw new BuildException(e);
		}
    	FileUtils.getFileUtils().tryHardToDelete(tmpDir);
    	tmpDir.mkdir();
    	copy.setTodir(tmpDir);
    	copy.setIncludeEmptyDirs(false);
    	copy.execute();
    	
    	this.setLocation(tmpDir);
    	
    	/*
    	 *     <info:name>com.ibm.ssb.inet</info:name>
    <info:description>Connectivity toolkit</info:description>
    <info:version>1.0.11</info:version>
    <info:requiredProductVersion>2.0.0</info:requiredProductVersion>
  </info:identity>

  <info:dependencies/>
    	 */
    	
    	try {
			File mergedInfoFile = new File(tmpDir, "info.xml");
			Document mergedInfo = docBuilder.newDocument();
					
			
			
			
			Element root = mergedInfo.createElement("info:toolkitInfoModel");
			mergedInfo.createElementNS(ns, "info:toolkitInfoModel");
			root.setAttribute("xmlns:info", ns);
			mergedInfo.appendChild(root);
			Element identity = mergedInfo.createElement("info:identity");
			root.appendChild(identity);
			Element tkName = mergedInfo.createElement("info:name");
			identity.appendChild(tkName);
			tkName.setTextContent("Merged Documentation Set");
			Element description = mergedInfo.createElement("info:description");
			description.setTextContent(mergedToolkitIntro.toString());
			identity.appendChild(description);
			Element version = mergedInfo.createElement("info:version");
			version.setTextContent("9.9.9");
			identity.appendChild(version);
			Element productVersion = mergedInfo.createElement("info:requiredProductVersion");
			productVersion.setTextContent("9.9.9");
			identity.appendChild(productVersion);
			Element dependencies = mergedInfo.createElement("info:dependencies");
			root.appendChild(dependencies);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(mergedInfo);
			StreamResult result = new StreamResult(mergedInfoFile);
 
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
 
			transformer.transform(source, result);
		} catch (Exception e) {
			throw new BuildException(e);
		}
    }

	private FileSet addToolkitToCopy(StringBuilder mergedToolkitIntro,
			DocumentBuilder docBuilder, File location) {
		
		File info = new File(location, "info.xml");
		try {
			Document doc = docBuilder.parse (info);
			String tkName = ((Element) doc.getElementsByTagNameNS(ns, "name").item(0)).getTextContent();
			String tkDesc = ((Element) doc.getElementsByTagNameNS(ns, "description").item(0)).getTextContent();
			mergedToolkitIntro.append("**");
			mergedToolkitIntro.append(tkName);
			mergedToolkitIntro.append("** ");
			mergedToolkitIntro.append(tkDesc);
			mergedToolkitIntro.append("\n\n");
		} catch (Exception e) {
			throw new BuildException(e);
		}
		
		FileSet set = new FileSet();
		set.setDir(location);
		set.setExcludes("info.xml,toolkit.xml,output");
		return set;
	}

	@Override
	protected void setArguments(ExecTask exec) {
		
		exec.createArg().setValue("--directory");
		exec.createArg().setValue(getToolkitLocation().getAbsolutePath());
		
		if (isClean()) {
			exec.createArg().setValue("--clean");
		}	
		if (isSourceCode()) {
			exec.createArg().setValue("--include-source");
		}
		if (isCompositeGraphs()) {
			exec.createArg().setValue("--include-composite-operator-diagram");
		}

		if (getAuthor() != null) {
			exec.createArg().setValue("--author");
			exec.createArg().setValue(getAuthor());
		}
		if (getTitle() != null) {
			exec.createArg().setValue("--doc-title");
			exec.createArg().setValue(getTitle());
		}
		
        if (outputDirectory != null) {
            exec.createArg().setValue("--output-directory");
            exec.createArg().setFile(outputDirectory);
        }			
	}
	
    /*
     * Setters for all the attributes,
     * automatically called by ant.
     */
    
    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
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

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthor() {
		return author;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setCompositeGraphs(boolean compositeGraphs) {
		this.compositeGraphs = compositeGraphs;
	}

	public boolean isCompositeGraphs() {
		return compositeGraphs;
	}

	public void setSourceCode(boolean sourceCode) {
		this.sourceCode = sourceCode;
	}

	public boolean isSourceCode() {
		return sourceCode;
	}
	
    public void addToolkit(Toolkit toolkit) {
    	if (this.toolkits == null) {
    		toolkits = new ArrayList<Toolkit>();
    	}
    	this.toolkits.add(toolkit);

    }
}
