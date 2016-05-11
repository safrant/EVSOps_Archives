package gov.nih.nci.evs.pdq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Vector;

import org.apache.commons.io.IOUtils;

public class PDQOntology {

	private final Vector<PDQConcept> concepts = new Vector<PDQConcept>();
	String xmlPath = "";
	File consoFile = null;
	PDQDOMParser domParser = new PDQDOMParser();
	private Vector<String> props = new Vector<String>();
	private Vector<String> rels = new Vector<String>();
	private static Charset charset = Charset.forName("ISO-8859-15");
	private static CharsetDecoder decoder = charset.newDecoder();

	Vector<PDQConcept> getConcepts() {
		return concepts;
	}

	Vector<String> getProperties() {
		return props;
	}

	Vector<String> getRelationships() {
		return rels;
	}

	private void createConcepts() {
		// Read in the XML and create concepts and properties
		// Create a new concept for each XML file
		try {
			createObsoleteConcept();
			createProtocolSelectionConcept();
			File directory = new File(xmlPath);

			File[] files = directory.listFiles(new Filter());

			for (File file : files) {
				// modify file to add CDATA to Definition

				String path = file.getAbsolutePath();
				String newPath = path + "parsable.txt";
				File newFile = new File(newPath);
				encloseDefinition(file, newFile);
				// Read the files into the OWL model
				// System.out.println(file.toString());
				createConcept(newFile);
				newFile.deleteOnExit();
			}

			// Cleanup the parsable.txt files

		} catch (Exception e) {

		}

	}

	private void encloseDefinition(File file, File newFile) {
		String myencoding = "latin5";
		try {
			String content = IOUtils.toString(new FileInputStream(file),
					myencoding);
			content = content.replace("<DefinitionText>",
					"<DefinitionText><![CDATA[");
			content = content.replace("</DefinitionText>",
					"]]></DefinitionText>");
			IOUtils.write(content, new FileOutputStream(newFile), myencoding);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createConcept(File file) {
		// PDQXMLParser parser = new PDQXMLParser();
		PDQConcept concept = new PDQConcept();
		concept = domParser.parseXmlFile(file);
		if (consoFile != null) {
			findMetaCUI2(concept);
		}

		concepts.add(concept);

	}

	private void createObsoleteConcept() {
		PDQConcept concept = new PDQConcept();
		concept.setId("Obsolete_Concepts");
		concept.setPreferredName("Obsolete_Concepts");
		if (!concepts.contains(concept)) {
			concepts.add(concept);
		}
	}
	
	private void createProtocolSelectionConcept() {
		PDQConcept concept = new PDQConcept();
		concept.setId("Protocol_Selection_Criteria");
		concept.setPreferredName("Protocol_Selection_Criteria");
		if (!concepts.contains(concept)) {
			concepts.add(concept);
		}
	}

	private void findMetaCUI2(PDQConcept concept) {
		String code = concept.getId();
		String re = ".*" + code + ".*";
		try {
			FileReader fr = new FileReader(consoFile);
			BufferedReader in = new BufferedReader(fr);
			String line;
			while ((line = in.readLine()) != null) {

				if (line.matches(re)) {
					int endOfCUI = line.indexOf("|");
					String cui = (String) line.subSequence(0, endOfCUI);
					PDQProperty prop = new PDQProperty("NCI_Metathesaurus_CUI",
							cui);
					concept.addMetaCUI(prop);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PDQOntology(String path) {

		xmlPath = path;
		createConcepts();
		props = domParser.props;
		rels = domParser.rels;
	}

	public PDQOntology(String path, String consoPath) {

		xmlPath = path;

		consoFile = new File(consoPath);
		createConcepts();
		props = domParser.props;
		rels = domParser.rels;
	}

	class Filter implements FileFilter {
		// Only return the CDR XML files from the directory
		public boolean accept(File file) {
			boolean isXML = file.getName().endsWith("xml");
			return isXML;
		}
	}

}
