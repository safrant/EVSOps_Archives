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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
 * @author garciawa2 Holds the metadata file
 */
public class MetadataXMLFile {

	private File xmlfile = null;
	private Document doc = null;
	private HashMap<String, Metadata> metaMap = null;
	private HashMap<String, Metadata> removedMetaMap = null;
	private DocumentBuilder dBuilder = null;

	/**
	 * Constructor using file name
	 * @param xmlfile
	 */
	public MetadataXMLFile(String xmlfilename) {
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
			this.xmlfile = new File(xmlfilename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
			nDoc = dBuilder.parse(this.xmlfile);
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
	 * Read in the list of current coding schemes from the XML file
	 * @return
	 */
	public boolean readMetadata() {

		NodeList nList = null;
		Element nElement = null;
		Node nNode = null;

		if (doc == null) return false;

		if (!doc.getDocumentElement().getNodeName().equals(
				"IndexerServiceMetaData")) {
			AppLogger.println(PackageConstants.BAD_META_FILE);
			System.exit(-1);
		}

		Node root = doc.getFirstChild();
		metaMap = new HashMap<String, Metadata>();
		removedMetaMap = new HashMap<String, Metadata>();

		// Read in note tags first
		nList = root.getChildNodes();
		for (int nt = 0; nt < nList.getLength(); nt++) {
			nNode = nList.item(nt);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				nElement = (Element) nNode;
				Metadata md = new Metadata();
				if (nNode.getNodeName().equalsIgnoreCase("note")) {
					md.key = nElement.getAttribute("key");
					md.value = nElement.getAttribute("value");
					md.notes = null;
					md.codingScheme = null;
					metaMap.put(md.value, md);
				}
			}
		}

		// Read in nodes to be removed
		// * This is only populated when a metadata.xml.template file
		// * is read
		nList = doc.getElementsByTagName(LBConfigConstants.REMOVED_CODINGSCHEME_TAG);
		for (int nt = 0; nt < nList.getLength(); nt++) {
			nNode = nList.item(nt);
			Metadata md = new Metadata();
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				md.key = nElement.getAttribute("key");
				md.value = nElement.getAttribute("value");
				md.notes = null;
				md.codingScheme = null;
				removedMetaMap.put(md.value, md);			
			}			
		}

		// Read in 'index' tags while adding the corresponding 'note' tags.
		nList = root.getChildNodes();
		for (int it = 0; it < nList.getLength(); it++) {
			nNode = nList.item(it);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				if (nNode.getNodeName().equalsIgnoreCase("index")) {

					if (nNode.hasChildNodes()) {
						nElement = (Element) nNode;
						// If no matching 'index' tag is found the metadata XML
						// file is bad
						if (metaMap.containsKey(nElement.getAttribute("name"))) {
							Metadata target = metaMap.get(nElement
									.getAttribute("name"));
							NodeList cList = nNode.getChildNodes();
							target.notes = new HashMap<String, String>();
							for (int y = 0; y < cList.getLength(); y++) {
								Node cNode = cList.item(y);
								if (cNode.getNodeType() == Node.ELEMENT_NODE) {
									Element cElement = (Element) cNode;
									// Update 'note' object
									if (cElement
											.getAttribute("key")
											.equalsIgnoreCase(
												LBConfigConstants.CODINGSCHEME_TAG)) {
										target.codingScheme = cElement
											.getAttribute("value");
									}
									// Add the new entry
									target.notes.put(cElement
											.getAttribute("key"), cElement
											.getAttribute("value"));
								}
							}
						} else {
							AppLogger.println("\n **** "
									+ PackageConstants.WARN_BAD_REG_FILE
									+ " ****");
							AppLogger.println("'note' tag for '"
									+ nElement.getAttribute("name")
									+ "' was not found!\n");
						}
					}

				}
			}
		}

		return true;
	}

	/**
	 * Write new metadata file
	 * @return
	 */
	public void writeMetadataFile(List<CodingScheme> csList, String fname) {

		Document meta = XMLUtil.copy(doc); // Copy origanal document

		if (!meta.getDocumentElement().getNodeName().equals(
				LBConfigConstants.META_HEADER_TAG)) {
			AppLogger.println(PackageConstants.BAD_META_FILE);
			System.exit(-1);
		}

		// Replace <note> and <index> tags with the ones selected by
		// the user
		Element root = meta.getDocumentElement();
		NodeList toplevel = root.getChildNodes();
		// First remove all childern since they will be recreated
		for (int tl = 0; tl < toplevel.getLength(); tl++) {
			Node nNode = toplevel.item(tl);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				root.removeChild(nNode);
			}
		}
		
		// Build new removed coding schemes node
		Node removedSchemeNode = meta
			.createElement(LBConfigConstants.REMOVED_CODINGSCHEMES_TAG);		

		// Next add selected entries
		Iterator<CodingScheme> it = csList.iterator();
		for (int cs = 0; cs < csList.size(); cs++) {
			CodingScheme value = (CodingScheme) it.next();
			if (value.isSelected()) {
				// Get meta entry
				Metadata mEntry = metaMap.get(value.prefix);
				// Add 'note' tag
				Element nNode = meta.createElement(LBConfigConstants.NOTE_TAG);
				nNode.setAttribute("key", mEntry.key);
				nNode.setAttribute("value", value.prefix);
				root.appendChild(nNode);
				// Add 'index' tag
				Element iNode = meta.createElement(LBConfigConstants.INDEX_TAG);
				iNode.setAttribute("name", value.prefix);
				// Add sub notes
				Iterator<Entry<String, String>> nit = mEntry.notes.entrySet()
						.iterator();
				while (nit.hasNext()) {
					Entry<String, String> e = nit.next();
					Element newChild = meta
							.createElement(LBConfigConstants.NOTE_TAG);
					newChild.setAttribute("key", e.getKey().toString());
					newChild.setAttribute("value", e.getValue().toString());
					iNode.appendChild(newChild);
				}
				root.appendChild(iNode);
			} else if (value.isRemoved()) {
				// Remove coding scheme
				Element rNode = meta
					.createElement(LBConfigConstants.REMOVED_CODINGSCHEME_TAG);			
				rNode.setAttribute("urn", value.urn);
				rNode.setAttribute("prefix", value.prefix);
				rNode.setAttribute("version", value.version);
				removedSchemeNode.appendChild(rNode);
			}
		}

		// Add removed schemes to template
		root.appendChild(removedSchemeNode);		
		
		// Write output file
		writeFile(meta, fname);
		AppLogger.println("Write of " + fname + " complete.");
		meta = null;
	}

	/**
	 * Write out a new metadata.xml file
	 * @param meta
	 */
	private void writeFile(Document meta, String fname) {
		try {
			meta.normalizeDocument();
			Writer output = new BufferedWriter(new FileWriter(fname));
			OutputFormat format = new OutputFormat(meta);
			format.setIndenting(true);
			format.setIndent(PackageConstants.INDENT);
			format.setLineWidth(PackageConstants.LINE_WIDTH);
			XMLSerializer serializer = new XMLSerializer(output, format);
			serializer.serialize(meta);
			output.close();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Merge a metadata file in to this one
	 * @param fname
	 */
	public void mergeFile(String sourceFile, String outFile ) {

		// Copy current document (use as a template)
		Document newMetadata = XMLUtil.copy(doc); 
		
		// Test if copy is a valid Lexevs metadata document		
		if (!doc.getDocumentElement().getNodeName().equals(
				LBConfigConstants.META_HEADER_TAG)) {
			AppLogger.println(PackageConstants.BAD_META_FILE);
			System.exit(-1);
		}		
		
		// Remove deployment tags from the doc copy (IE; removedCodingSchemes)
		Element oRoot = newMetadata.getDocumentElement();
		removeTopLevelTag(oRoot, LBConfigConstants.REMOVED_CODINGSCHEMES_TAG);		

		Document sourceMetadata = openXMLFile(sourceFile);
		Element sRoot = sourceMetadata.getDocumentElement();
		NodeList sToplevel = null;
		
		// Add 'index' tags from source file if not found in the remove section
		//  and not already present		
		sToplevel = sRoot.getElementsByTagName(LBConfigConstants.INDEX_TAG);
		for (int ht = 0; ht < sToplevel.getLength(); ht++) {
			Node node = sToplevel.item(ht);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node;
				if (!containsPrefixToRemove(doc.getDocumentElement(), eElement
					.getAttribute("name")) &&
					!containsPrefix(doc.getDocumentElement(), eElement
					.getAttribute("name")) ) {
					
					// Add Note tag
					Node noteNode = getNoteNode(sRoot, oRoot, eElement
						.getAttribute("name"));
					if (noteNode != null) {
						Node newNode = (Element)newMetadata.importNode(noteNode, true);						
						oRoot.appendChild(newNode);
					}
					// Add index tag
					Node nNode = (Element)newMetadata.importNode(node, true); 
					oRoot.appendChild(nNode);
				}
			}
		}		

		// Write output file
		writeFile(newMetadata, outFile);
		AppLogger.println("Write of " + outFile + " complete.");		
	}
	
	/**
	 * Locate a note tag with a given prefix
	 * 
	 * @param sRoot
	 * @param oRoot
	 * @param prefix
	 * @return
	 */
	private Node getNoteNode(Element sRoot, Element oRoot, String prefix) {
		NodeList sToplevel = null;		
	
		sToplevel = sRoot.getElementsByTagName(LBConfigConstants.NOTE_TAG);		
		for (int ht = 0; ht < sToplevel.getLength(); ht++) {
			Node node = sToplevel.item(ht);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node;
				if (eElement.getAttribute("value").contains(prefix)) {					
					return node;
				}
			}
		}		
		return null;
	}
	
	/**
	 * Retrieve list of current coding schemes
	 * @return
	 */
	public Map<String, Metadata> getMetadata() {
		if (metaMap == null)
			readMetadata();
		return metaMap;
	}

	/**
	 * Retrieve list of removed coding schemes
	 * @return
	 */
	public Map<String, Metadata> getRemovedMetadata() {
		if (removedMetaMap == null)
			readMetadata();
		return removedMetaMap;
	}	
	
	/**
	 * Return CodingScheme name for a prefix
	 * @param prefix
	 * @return
	 */
	public String getCodingScheme(String prefix) {
		if (metaMap.containsKey(prefix)) {
			Metadata target = metaMap.get(prefix);
			return target.codingScheme;
		}
		return PackageConstants.NOT_FOUND;
	}

	/**
	 * Return RemovedCodingScheme name for a prefix
	 * @param prefix
	 * @return
	 */
	public String getRemovedCodingScheme(String prefix) {
		if (removedMetaMap.containsKey(prefix)) {
			Metadata target = removedMetaMap.get(prefix);
			return target.codingScheme;
		}
		return PackageConstants.NOT_FOUND;
	}	
	
	/**
	 * Dump the metadata for debug purposes
	 * @return
	 */
	public void dump() {
		Iterator<Entry<String, Metadata>> it = metaMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Metadata> entry = it.next();
			Metadata md = entry.getValue();
			System.out.println();
			AppLogger.println(" " + md.key + " => " + md.value);
		}
	}

	/**
	 * Dump the metadata for debug purposes
	 * @param meta
	 */
	public void dump(Document meta) {
		Element root = meta.getDocumentElement();
		NodeList toplevel = root.getChildNodes();
		System.out.println("-------------------------------");
		for (int x = 0; x < toplevel.getLength(); x++) {
			Node nNode = toplevel.item(x);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				System.out.println(nNode.getNodeName());
			}
		}
		System.out.println("-------------------------------");
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
	private boolean containsPrefixToRemove(Element root, String prefix) {
		if (root == null || prefix == null) return false;
		NodeList toplevel = root.getElementsByTagName(LBConfigConstants.REMOVED_CODINGSCHEME_TAG);
		for (int ht = 0; ht < toplevel.getLength(); ht++) {
			Node nNode = toplevel.item(ht);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {				
				Element eElement = (Element) nNode;
				if (eElement.getAttribute("prefix").equals(prefix))
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
	private boolean containsPrefix(Element root, String prefix) {
		if (root == null || prefix == null) return false;
		NodeList toplevel = root.getElementsByTagName(LBConfigConstants.INDEX_TAG);
		for (int ht = 0; ht < toplevel.getLength(); ht++) {
			Node nNode = toplevel.item(ht);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {				
				Element eElement = (Element) nNode;
				if (eElement.getAttribute("name").equals(prefix))
					return true;
			}
		} // End loop		
		return false;
	}		
	
}
