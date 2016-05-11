package gov.nih.nci.xml;

import gov.nih.nci.common.AppLogger;
import gov.nih.nci.common.LBConfigConstants;
import gov.nih.nci.common.PackageConstants;
import gov.nih.nci.utils.XMLUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2008,2009 NGIT. This software was developed in conjunction 
 * with the National Cancer Institute, and so to the extent government 
 * employees are co-authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 *   1. Redistributions of source code must retain the above copyright 
 *      notice, this list of conditions and the disclaimer of Article 3, 
 *      below. Redistributions in binary form must reproduce the above 
 *      copyright notice, this list of conditions and the following 
 *      disclaimer in the documentation and/or other materials provided 
 *      with the distribution.
 *   2. The end-user documentation included with the redistribution, 
 *      if any, must include the following acknowledgment:
 *      "This product includes software developed by NGIT and the National 
 *      Cancer Institute."   If no such end-user documentation is to be
 *      included, this acknowledgment shall appear in the software itself,
 *      wherever such third-party acknowledgments normally appear.
 *   3. The names "The National Cancer Institute", "NCI" and "NGIT" must 
 *      not be used to endorse or promote products derived from this software.
 *   4. This license does not authorize the incorporation of this software
 *      into any third party proprietary programs. This license does not 
 *      authorize the recipient to use any trademarks owned by either NCI 
 *      or NGIT 
 *   5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED 
 *      WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 *      OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE 
 *      DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
 *      NGIT, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, 
 *      INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 *      BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 *      LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 *      CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 *      LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 *      ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 *      POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * @author garciawa2 Holds the registry file
 */
public class RegistryXMLFile {

	private static Document doc = null;
	private static HashMap<String, History> histList = null;
	private List<CodingScheme> csList = null;
	private List<CodingScheme> rcsList = null;

	/**
	 * Constructor using file name
	 * @param xmlfile
	 */
	public RegistryXMLFile(String xmlfilename) {
		doc = openXMLFile(xmlfilename);
	}

	/**
	 * Create a new XML Document object
	 * @param xmlfilename
	 * @return
	 */
	private Document openXMLFile(String xmlfilename) {

		if (xmlfilename == null) return null;
		Document nDoc = null;
		File file = new File(xmlfilename);
		if (!file.exists()) {
			AppLogger.println("File (" + xmlfilename + ") does not exist!");
			return nDoc;
		}
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			nDoc = dBuilder.parse(file);
			nDoc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return nDoc;
	}	
	
	/**
	 * Retrieve list of current coding schemes
	 * @return
	 */
	public List<CodingScheme> getCodingSchemes() {

		if (doc == null) return null;
		if (!doc.getDocumentElement().getNodeName().equals(
				LBConfigConstants.REG_HEADER_TAG)) {
			AppLogger.println(PackageConstants.BAD_REG_FILE);
			System.exit(-1);
		}
		if (csList != null)	return csList;

		NodeList nList = doc
				.getElementsByTagName(LBConfigConstants.CODINGSCHEME_TAG);
		csList = new ArrayList<CodingScheme>();

		loadHistories(); // Load history records

		for (int x = 0; x < nList.getLength(); x++) {
			Node nNode = nList.item(x);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				CodingScheme cs = new CodingScheme();
				cs.urn = eElement.getAttribute("urn");
				cs.dbURL = eElement.getAttribute("dbURL");
				cs.dbName = eElement.getAttribute("dbName");
				cs.prefix = eElement.getAttribute("prefix");
				cs.status = eElement.getAttribute("status");
				cs.tag = eElement.getAttribute("tag");
				cs.version = eElement.getAttribute("version");
				cs.deactivateDate = eElement.getAttribute("deactivateDate");
				cs.lastUpdateDate = eElement.getAttribute("lastUpdateDate");
				cs.unselect();
				if (histList.containsKey(cs.urn))
					cs.hasHistory = true;
				else
					cs.hasHistory = false;
				csList.add(cs);
			}
		}

		return csList;
	}

	/**
	 * Retrieve list of removed coding schemes
	 * @return
	 */
	public List<CodingScheme> getRemovedCodingSchemes() {

		if (doc == null) return null;
		if (!doc.getDocumentElement().getNodeName().equals(
				LBConfigConstants.REG_HEADER_TAG)) {
			AppLogger.println(PackageConstants.BAD_REG_FILE);
			System.exit(-1);
		}

		if (rcsList != null)	return rcsList;

		NodeList nList = doc
				.getElementsByTagName(LBConfigConstants.REMOVED_CODINGSCHEME_TAG);
		rcsList = new ArrayList<CodingScheme>();

		loadHistories(); // Load histrory records

		for (int x = 0; x < nList.getLength(); x++) {
			Node nNode = nList.item(x);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				CodingScheme cs = new CodingScheme();
				cs.urn = eElement.getAttribute("urn");
				cs.dbURL = eElement.getAttribute("dbURL");
				cs.dbName = eElement.getAttribute("dbName");
				cs.prefix = eElement.getAttribute("prefix");
				cs.status = eElement.getAttribute("status");
				cs.tag = eElement.getAttribute("tag");
				cs.version = eElement.getAttribute("version");
				cs.deactivateDate = eElement.getAttribute("deactivateDate");
				cs.lastUpdateDate = eElement.getAttribute("lastUpdateDate");
				cs.unselect();
				if (histList.containsKey(cs.urn))
					cs.hasHistory = true;
				else
					cs.hasHistory = false;
				rcsList.add(cs);
			}
		}

		return rcsList;
	}	
	
	/**
	 * Returns list of history entries
	 * @return
	 */
	private void loadHistories() {

		if (!doc.getDocumentElement().getNodeName().equals(
				LBConfigConstants.REG_HEADER_TAG)) {
			AppLogger.println(PackageConstants.BAD_REG_FILE);
			System.exit(-1);
		}

		if (histList != null)
			return; // Don't load if already loaded

		NodeList nList = doc.getElementsByTagName(LBConfigConstants.HISTORY_TAG);
		histList = new HashMap<String, History>();

		for (int x = 0; x < nList.getLength(); x++) {
			Node nNode = nList.item(x);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				History hist = new History();
				hist.urn = eElement.getAttribute("urn");
				hist.dbURL = eElement.getAttribute("dbURL");
				hist.dbName = eElement.getAttribute("dbName");
				hist.prefix = eElement.getAttribute("prefix");
				hist.lastUpdateDate = eElement.getAttribute("lastUpdateDate");
				histList.put(hist.urn, hist);
			}
		}

	}

	/**
	 * Return hostory map
	 * @return
	 */
	public HashMap<String, History> getHistoryMap() {
		if (histList == null)
			loadHistories();
		return histList;
	}

	/**
	 * Write new metadata file
	 * @return
	 */
	public void writeRegistryFile(List<CodingScheme> csList, String fname) {

		Document registry = XMLUtil.copy(doc); // Copy original document
		if (!registry.getDocumentElement().getNodeName().equals(
				LBConfigConstants.REG_HEADER_TAG)) {
			AppLogger.println(PackageConstants.BAD_REG_FILE);
			System.exit(-1);
		}

		// Build new coding schemes, removed coding schemes and history nodes
		Node newSchemeNode = registry
			.createElement(LBConfigConstants.CODINGSCHEMES_TAG);
		Node removedSchemeNode = registry
			.createElement(LBConfigConstants.REMOVED_CODINGSCHEMES_TAG);
		Node newHistNode = registry
			.createElement(LBConfigConstants.HISTORIES_TAG);		

		// Setup structure to eliminate duplicate history table entries
		// since different terminologies can pint to the same history file.
		Set<String> checkDupHist = new HashSet<String>();

		Iterator<CodingScheme> it = csList.iterator();
		for (int cs = 0; cs < csList.size(); cs++) {
			CodingScheme value = (CodingScheme) it.next();
			if (value.isSelected()) {
				// Update or add a coding scheme
				Element sNode = registry
						.createElement(LBConfigConstants.CODINGSCHEME_TAG);
				sNode.setAttribute("dbURL", PackageConstants.EMPTY);
				sNode.setAttribute("urn", value.urn);
				sNode.setAttribute("dbName", value.dbName);
				sNode.setAttribute("prefix", value.prefix);
				sNode.setAttribute("status", value.status);
				sNode.setAttribute("tag", value.tag);
				sNode.setAttribute("version", value.version);
				sNode.setAttribute("deactivateDate", value.deactivateDate);
				sNode.setAttribute("lastUpdateDate", value.lastUpdateDate);
				newSchemeNode.appendChild(sNode);
				if (value.hasHistory) {
					if (!checkDupHist.contains(value.urn)) {
						Element hNode = registry
								.createElement(LBConfigConstants.HISTORY_TAG);
						History hist = histList.get(value.urn);
						hNode.setAttribute("dbURL", PackageConstants.EMPTY);
						hNode.setAttribute("urn", hist.urn);
						hNode.setAttribute("prefix", hist.prefix);
						hNode.setAttribute("dbName", hist.dbName);
						hNode.setAttribute("lastUpdateDate",
								hist.lastUpdateDate);
						newHistNode.appendChild(hNode);
						checkDupHist.add(value.urn);
					}
				}
			} else if (value.isRemoved()) {
				// Remove coding scheme
				Element rNode = registry
					.createElement(LBConfigConstants.REMOVED_CODINGSCHEME_TAG);			
				rNode.setAttribute("urn", value.urn);
				rNode.setAttribute("prefix", value.prefix);
				rNode.setAttribute("version", value.version);
				removedSchemeNode.appendChild(rNode);
			}			
		}

		// Replace <codingScheme> and <history> tags with the ones selected by
		// the user
		Element root = registry.getDocumentElement();
		NodeList toplevel = root.getChildNodes();
		for (int ht = 0; ht < toplevel.getLength(); ht++) {
			Node nNode = toplevel.item(ht);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				if (nNode.getNodeName().equalsIgnoreCase(
						LBConfigConstants.CODINGSCHEMES_TAG)) {
					root.replaceChild(newSchemeNode, nNode);
				}
				if (nNode.getNodeName().equalsIgnoreCase(
						LBConfigConstants.HISTORIES_TAG)) {
					root.replaceChild(newHistNode, nNode);
				}
			}
		}
		// Add removed schemes to template
		root.appendChild(removedSchemeNode);		

		// Write output file
		writeFile(registry, fname);
		AppLogger.println("Write of " + fname + " complete.");
		checkDupHist = null;
	}

	/**
	 * Write out a new registry.xml file
	 * @param registry
	 */
	private void writeFile(Document registry, String fname) {
		try {
			registry.normalizeDocument();
			Writer output = new BufferedWriter(new FileWriter(fname));
			OutputFormat format = new OutputFormat(registry);
			format.setIndenting(true);
			format.setIndent(PackageConstants.INDENT);
			format.setLineWidth(PackageConstants.LINE_WIDTH);
			XMLSerializer serializer = new XMLSerializer(output, format);
			serializer.serialize(registry);
			output.close();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clean up code befor exit
	 */
	public void close() {
		// Cleaup and close
	}

	/**
	 * Merge a registry file in to this one
	 * @param fname
	 */
	public void mergeFile(String sourceFile, String outFile ) {

		// Copy current document (use as a template)
		Document newRegistry = XMLUtil.copy(doc); 

		// Test if copy is a valid Lexevs reg document
		if (!newRegistry.getDocumentElement().getNodeName().equals(
				LBConfigConstants.REG_HEADER_TAG)) {
			AppLogger.println(PackageConstants.BAD_REG_FILE);
			System.exit(-1);
		}		
		
		// Remove deployment tags from the doc copy (IE; removedCodingSchemes)
		Element oRoot = newRegistry.getDocumentElement();
		removeTopLevelTag(oRoot, LBConfigConstants.REMOVED_CODINGSCHEMES_TAG);
		
		// Add 'codingScheme' tags from source file if not found in the remove section
		//  and not already present
		Document sourceRegistry = openXMLFile(sourceFile);
		Element sRoot = sourceRegistry.getDocumentElement();
		NodeList sToplevel = sRoot.getElementsByTagName(LBConfigConstants.CODINGSCHEME_TAG);
		for (int ht = 0; ht < sToplevel.getLength(); ht++) {
			Node node = sToplevel.item(ht);
			if (node.getNodeType() == Node.ELEMENT_NODE) {				
				Element eElement = (Element) node;
				if (!containsUrnToRemove(doc.getDocumentElement(), eElement
					.getAttribute("urn")) &&
					!containsUrn(doc.getDocumentElement(), eElement
					.getAttribute("urn")) ) {
					Node nNode = (Element)newRegistry.importNode(node, false); 
					appendNode(oRoot, LBConfigConstants.CODINGSCHEMES_TAG, nNode);
				}
			}
		}		
		
		// Add 'histories' tags from source file if not found in the remove section
		sToplevel = sRoot.getElementsByTagName(LBConfigConstants.HISTORY_TAG);
		for (int ht = 0; ht < sToplevel.getLength(); ht++) {
			Node node = sToplevel.item(ht);
			if (node.getNodeType() == Node.ELEMENT_NODE) {				
				Element eElement = (Element) node;
				if (!containsUrnToRemove(doc.getDocumentElement(), eElement
					.getAttribute("urn")) &&
					!containsHistoryUrn(doc.getDocumentElement(), eElement
					.getAttribute("urn"))) {
					Node nNode = (Element)newRegistry.importNode(node, false); 
					appendNode(oRoot, LBConfigConstants.HISTORIES_TAG, nNode);
				}
			}
		}		
		
		// Get DB information
		String dbURL = "jdbc:mysql://" + LBConfigConstants.getDatabaseServer()
			+ ":" + LBConfigConstants.getDatabasePort()
			+ "/" + LBConfigConstants.getDatabaseName();
		
		// Update scheme tags with new DB information
		sToplevel = oRoot.getElementsByTagName(LBConfigConstants.CODINGSCHEME_TAG);
		for (int ht = 0; ht < sToplevel.getLength(); ht++) {
			Node node = sToplevel.item(ht);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node;
				eElement.setAttribute("dbURL", dbURL);
			}		
		}
		
		// Update history tags with new DB information
		sToplevel = oRoot.getElementsByTagName(LBConfigConstants.HISTORY_TAG);
		for (int ht = 0; ht < sToplevel.getLength(); ht++) {
			Node node = sToplevel.item(ht);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node;
				eElement.setAttribute("dbURL", dbURL);
			}		
		}		

		// Write output file
		writeFile(newRegistry, outFile);
		AppLogger.println("Write of " + outFile + " complete.");		
		
	}	
	
	// ***
	// *** Local XML help methods
	// ***
	
	/**
	 * Remove a tag from an element
	 * @param root
	 * @param tag
	 */
	private void removeTopLevelTag(Element root, String tag) {
		if (tag == null) return;
		NodeList toplevel = root.getChildNodes();
		for (int ht = 0; ht < toplevel.getLength(); ht++) {
			Node nNode = toplevel.item(ht);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {				
				if (nNode.getNodeName().equalsIgnoreCase(tag)) {
					root.removeChild(nNode);
				}
			}
		} // End loop	
	}
	
	/**
	 * Check if a prefix is in the remove scheme section
	 * @param root
	 * @param prefix
	 * @return
	 */
	private boolean containsUrnToRemove(Element root, String urn) {
		if (root == null || urn == null) return false;
		NodeList toplevel = root.getElementsByTagName(LBConfigConstants.REMOVED_CODINGSCHEME_TAG);
		for (int ht = 0; ht < toplevel.getLength(); ht++) {
			Node nNode = toplevel.item(ht);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {				
				Element eElement = (Element) nNode;
				if (eElement.getAttribute("urn").equals(urn))
					return true;
			}
		} // End loop		
		return false;
	}

	/**
	 * Check if a prefix is in the scheme section
	 * @param root
	 * @param prefix
	 * @return
	 */
	private boolean containsUrn(Element root, String urn) {
		if (root == null || urn == null) return false;
		NodeList toplevel = root.getElementsByTagName(LBConfigConstants.CODINGSCHEME_TAG);
		for (int ht = 0; ht < toplevel.getLength(); ht++) {
			Node nNode = toplevel.item(ht);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {				
				Element eElement = (Element) nNode;
				if (eElement.getAttribute("urn").equals(urn))
					return true;
			}
		} // End loop		
		return false;
	}	

	/**
	 * Check if a prefix is in the scheme section
	 * @param root
	 * @param prefix
	 * @return
	 */
	private boolean containsHistoryUrn(Element root, String urn) {
		if (root == null || urn == null) return false;
		NodeList toplevel = root.getElementsByTagName(LBConfigConstants.HISTORY_TAG);
		for (int ht = 0; ht < toplevel.getLength(); ht++) {
			Node nNode = toplevel.item(ht);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {				
				Element eElement = (Element) nNode;
				if (eElement.getAttribute("urn").equals(urn))
					return true;
			}
		} // End loop		
		return false;
	}	
	
	/**
	 * Append a new node to a level tag
	 * @param root
	 * @param level
	 * @param newChild
	 */
	private void appendNode(Element root, String level, Node newChild) {
		if (root == null || level == null || newChild == null) return;
		NodeList toplevel = root.getElementsByTagName(level);
		for (int ht = 0; ht < toplevel.getLength(); ht++) {
			Node nNode = toplevel.item(ht);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {				
				nNode.appendChild(newChild);
				return;
			}
		} // End loop		
	}
	
} // End class RegistryXMLFile
