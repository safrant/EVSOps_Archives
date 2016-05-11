package gov.nih.nci.evs.pdq;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PDQDOMParser {

	Document dom = null;
	PDQConcept concept = null;
	Vector<String> props = new Vector<String>();
	Vector<String> rels = new Vector<String>();

	public PDQDOMParser() {

	}

	public PDQConcept parseXmlFile(File path) {
		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			concept = new PDQConcept();
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			dom = db.parse(path);
			parseDocument();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return concept;
	}

	private void parseDocument() {
		// get the root element
		Element docEle = dom.getDocumentElement();

		concept.setId(docEle.getAttribute("id"));

		String leg = docEle.getAttribute("LegacyPDQID");
		if (leg.length() > 0) {
			// concept.setLegacyPDQID(leg);
			PDQProperty prop = new PDQProperty("LegacyPDQID", leg);
			concept.addProperty(prop);
		}

		String nciID = docEle.getAttribute("NCIThesaurusConceptID");
		if (nciID != null && nciID.length() > 0) {
			// concept.setNCIThesaurusID(nciID);
			PDQProperty prop = new PDQProperty("NCIThesaurusConceptID", nciID);
			concept.addProperty(prop);
		}

		String termType = getTextValue(docEle, "TermTypeName");
		if (termType != null && termType.length() > 0) {
			PDQProperty prop = new PDQProperty("TermTypeName", termType);
			concept.setTermType(prop);
		}

		String displayName = getTextValue(docEle, "DisplayName");
		if (displayName != null && displayName.length() > 0) {
			PDQProperty prop = new PDQProperty("DisplayName", displayName);
			concept.addProperty(prop);
		}

		String dateFirst = getTextValue(docEle, "DateFirstPublished");
		if (dateFirst != null && dateFirst.length() > 0) {
			PDQProperty prop = new PDQProperty("DateFirstPublished", dateFirst);
			concept.addProperty(prop);
		}

		String dateLast = getTextValue(docEle, "DateLastModified");
		if (dateLast != null && dateLast.length() > 0) {
			PDQProperty prop = new PDQProperty("DateLastModified", dateLast);
			concept.addProperty(prop);
		}

		String preferredName = getTextValue(docEle, "PreferredName");
		if (preferredName != null && preferredName.length() > 0) {
			concept.setPreferredName(preferredName);
		}

		// get a nodelist of elements
		NodeList nl = docEle.getElementsByTagName("OtherName");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				// get the OtherName element
				Element el = (Element) nl.item(i);
				// get the Employee object
				getOtherName(el);
			}
		}

		nl = docEle.getElementsByTagName("Definition");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				// get the OtherName element
				Element el = (Element) nl.item(i);
				// get the Employee object
				getDefinition(el);
			}
		}

		nl = docEle.getElementsByTagName("RelatedTerm");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				// get the OtherName element
				Element el = (Element) nl.item(i);
				// get the Employee object
				getRelatedTerm(el);
			}
		}

		nl = docEle.getElementsByTagName("ParentTerm");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				// get the OtherName element
				Element el = (Element) nl.item(i);
				// get the Employee object
				getParentTerm(el);
			}
		}

		nl = docEle.getElementsByTagName("SemanticType");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				// get the OtherName element
				Element el = (Element) nl.item(i);
				// get the Employee object
				getSemanticType(el);
			}
		}
	}

	private void getSemanticType(Element el) {
		String id = el.getAttribute("ref");
		PDQRelationship rel = new PDQRelationship("SemanticType", id, true);
		concept.addParent(rel);
	}

	private void getParentTerm(Element el) {
		Node node1 = el.getFirstChild();
		NamedNodeMap nMap = node1.getAttributes();
		Node node2 = nMap.getNamedItem("ref");
		String id = node2.getNodeValue();

		String target = getTextValue(el, "ParentTermName");
		String name = getTextValue(el, "ParentType");
		PDQRelationship rel = new PDQRelationship("subClassOf", id, true);
		concept.addParent(rel);
	}

	private void getRelatedTerm(Element el) {
		Node node1 = el.getFirstChild();
		NamedNodeMap nMap = node1.getAttributes();
		Node node2 = nMap.getNamedItem("ref");
		String id = node2.getNodeValue();

		String target = getTextValue(el, "RelatedTermName");
		String name = getTextValue(el, "RelationshipType");
		PDQRelationship rel = new PDQRelationship(name, id);
		concept.addRelationship(rel);

		if (!rels.contains(name)) rels.add(name);
	}

	private void getDefinition(Element el) {

		// Need to treat this special due to hyperlinks within element
		String value = getTextValue(el, "DefinitionText");
		String type = getTextValue(el, "DefinitionType");

		PDQProperty prop = new PDQProperty(type, value);
		concept.setDefinition(prop);
	}

	/**
	 * I take an employee element and read the values in, create an Employee
	 * object and return it
	 */
	private void getOtherName(Element el) {

		String value = getTextValue(el, "OtherTermName");
		String ntype = getTextValue(el, "OtherNameType");

		PDQProperty prop = new PDQProperty(ntype, value);
		concept.addProperty(prop);
		if (!props.contains(ntype)) props.add(ntype);
	}

	/**
	 * I take a xml element and the tag name, look for the tag and get the text
	 * content i.e for <employee><name>John</name></employee> xml snippet if the
	 * Element points to employee node and tagName is 'name' I will return John
	 */
	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if (nl != null && nl.getLength() > 0) {
			Element el = (Element) nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
			// check for external references

		}

		return textVal;
	}
}
