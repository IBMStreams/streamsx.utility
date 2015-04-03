package com.ibm.streamsx.ant.adl;
/*
** Licensed Materials - Property of IBM
** Copyright IBM Corp. 2013
** US Government Users Restricted Rights - Use, duplication or
** disclosure restricted by GSA ADP Schedule Contract with
** IBM Corp.
*/

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ADL {
	
	private final Document adl;
	
	public ADL(File location) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(location);
		doc.getDocumentElement().normalize(); 
		this.adl = doc;
	}
	
	public void hostPools(Map<String,String> mapping) {
		
		NodeList hostPools = adl.getElementsByTagName("hostpool");
		for (int i = 0 ;i < hostPools.getLength(); i++) {
			
			Element hostPool = (Element) hostPools.item(i);
						
			NodeList tags = hostPool.getElementsByTagName("tag");
			for (int t = 0; t < tags.getLength(); t++) {
				Element tag = (Element) tags.item(t);
				final String tagName = tag.getAttribute("name");
				if (mapping.containsKey(tagName)) {
					tag.setAttribute("name", mapping.get(tagName));
				}
			}
		}
	}
	
	public void write(File location) throws TransformerFactoryConfigurationError, TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        
        StreamResult result = new StreamResult(location);
        transformer.transform(new DOMSource(adl), result);
	}
}
