/**
 * 
 */
package gov.nih.nci.evs.pdq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.exolab.castor.xml.dtd.ContentParticle;
import org.exolab.castor.xml.dtd.DTDdocument;
import org.exolab.castor.xml.dtd.Element;
import org.exolab.castor.xml.dtd.parser.DTDInitialParser;
import org.exolab.castor.xml.dtd.parser.InputCharStream;

/**
 * @author safrant
 * 
 */
public class PDQtoOWL {
	private static URI saveURI = null;
	private static String inputPath = "";
	private static String consoPath = "";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PDQtoOWL p2o = null;
		if (args.length > 0) {
			inputPath = (args[0]);
		}
		if (args.length > 1) {
			saveURI = toURI(args[1]);
		}
		if (args.length > 2) {
			consoPath = args[2];
		}

		if (inputPath != null && saveURI != null) {
			p2o = new PDQtoOWL(inputPath, saveURI);
		} else {
			System.out.println("Problem creating ontology");
			System.exit(0);
		}
	}

	private static URI toURI(String path) {
		try {
			URI uri = new URI(path);
			return uri;
		} catch (URISyntaxException e) {
			System.out.println("Unable to resolve save URI.  Not valid syntax");
			System.exit(1);
		}
		return null;
	}

	public PDQtoOWL() {
		// Need to be given a folder where the PDQ files are located
		// Need to read all the xml files in the folder
		// Need to gather the relevant elements from the files while reading
		// Need to determine hierarchy using SemanticType and parent properties
		// Need to determine root nodes
		// Need to write it all out to OWL
	}

	@SuppressWarnings("unused")
	public PDQtoOWL(String path, URI saveURI) {
		try {
			PDQOntology onto = null;
			if (consoPath.length() > 0) {
				// extract PDQ Conso
				String filteredConso = consoPath + "PDQ";
				filterConso(filteredConso);

				onto = new PDQOntology(path, filteredConso);
			} else {
				onto = new PDQOntology(path);
			}
			PDQOWLWriter writer = new PDQOWLWriter(onto, saveURI);
		} catch (Exception e) {

		}
	}

	private void filterConso(String filteredConso) {
		String re = ".*PDQ.*";
		String myencoding = "latin5";
		try {
			File consoFile = new File(consoPath);
			File filteredFile = new File(filteredConso);
			FileOutputStream filteredStream = new FileOutputStream(filteredFile);
			FileReader fr = new FileReader(consoFile);
			BufferedReader in = new BufferedReader(fr);
			String line;
			Collection<String> lines = new Vector<String>();
			while ((line = in.readLine()) != null) {

				if (line.matches(re)) {

					// IOUtils.write(line, filteredStream, myencoding);
					lines.add(line);
					// System.out.println(line);
				}

			}
			IOUtils.writeLines(lines, "\n", filteredStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class Filter implements FileFilter {
		// Only return the CDR XML files from the directory
		public boolean accept(File file) {
			boolean isXML = file.getName().endsWith("xml");
			return isXML;
		}
	}

	private void readDTDexolab(String path) {
		FileInputStream inputStream;
		InputStreamReader reader;
		InputCharStream charStream;
		DTDInitialParser initialParser;
		String intermedResult;
		StringReader strReader;
		org.exolab.castor.xml.dtd.parser.DTDParser parser;
		DTDdocument dtd;
		try {
			// instantiate input byte stream, associated with the input file
			inputStream = new FileInputStream(path);

			// instantiate character reader from the input file byte stream
			reader = new InputStreamReader(inputStream, "US-ASCII");

			// instantiate char stream for initial parser from the input reader
			charStream = new InputCharStream(reader);

			// instantiate initial parser
			initialParser = new DTDInitialParser(charStream);

			// get result of initial parsing - DTD text document with parameter
			// entity references expanded
			intermedResult = initialParser.Input();

			// construct StringReader from the intermediate parsing result
			strReader = new StringReader(intermedResult);

			// instantiate char stream for the main parser
			charStream = new InputCharStream(strReader);

			// instantiate main parser
			parser = new org.exolab.castor.xml.dtd.parser.DTDParser(charStream);

			// parse intermediate parsing result with the main parser
			// and get corresponding DTD document oblect
			dtd = parser.Input();

			Enumeration<Element> elements = dtd.getElements();
			while (elements.hasMoreElements()) {
				Element element = elements.nextElement();
				if (element.getName().equals("Term")) {
					processTermexolab(element);
				}
			}

		} catch (Exception e) {

		}
	}

	private void processTermexolab(Element element) {
		// Search for know elements. If the dtd has changed, alert the user.
		// They will need to update the application for the new element
		try {
			ContentParticle cp = element.getContent();
			Enumeration<Element> children = cp.getChildren();
			processTermChildren(children);
		} catch (Exception e) {

		}
	}

	private void processTermChildren(Enumeration<Element> elements) {
		try {

		} catch (Exception e) {

		}
	}

	public static String underscoredString(String input) {

		return input.trim().replace(" ", "_").replace("(", "_")
		        .replace(")", "_");
	}
}
