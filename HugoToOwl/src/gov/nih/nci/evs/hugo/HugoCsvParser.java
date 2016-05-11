package gov.nih.nci.evs.hugo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Pattern;

public class HugoCsvParser {

	// private static Properties columns = new Properties();
	// private static Properties delimiters = new Properties();
	// private static Properties specialistDatabases = new Properties();

	public HugoCsvParser(File file) throws Exception {
		readFile(file);

	}

	private Vector<String> header = null;
	private final Vector<Vector<String>> lineByLineData = new Vector<Vector<String>>();

	private void readFile(File file) throws Exception {
		Scanner reader = new Scanner(file);
		Pattern p = Pattern.compile("\t");
		String line = reader.nextLine(); // first line is a header
		header = tokenizeString(line, p);
		Vector<String> data = new Vector<String>();
		while (reader.hasNextLine()) {
			line = reader.nextLine();
			data.add(line);
			lineByLineData.add(tokenizeString(line, p));
		}
		if (lineByLineData.size() < 1) {
			throw new FileNotFoundException();
		}
		boolean isValidData = validateAgainstHeader();
		if (!isValidData) {
			throw new Exception();
		}
	}

	public static Vector<String> tokenizeString(String inputString,
	        Pattern delimiter) {
		Vector<String> tokens = new Vector<String>();
		Scanner lineReader = new Scanner(inputString);
		lineReader.useDelimiter(delimiter);
		while (lineReader.hasNext()) {
			String value = lineReader.next();
			tokens.add(value.trim());
		}
		lineReader.close();
		return tokens;
	}

	public Vector<String> getHeader() {
		return header;
	}

	public Vector<Vector<String>> getData() {
		return lineByLineData;
	}

	/**
	 * Checks each line to make sure it has the same number of tokens as the
	 * header If not, then the tokenizing has gone wrong.
	 * 
	 * @return
	 */
	private boolean validateAgainstHeader() {
		for (Vector<String> line : lineByLineData) {
			if (line.size() > header.size()) {
				System.out.println("Invalid data at " + line.elementAt(0)
				        + ". More data than there are columns");
				return false;
			}
			if (line.size() < 1) {
				System.out.println("Invalid data.  Empty line");
				return false;
			}
			if (line.size() < (header.size() / 2)) {
				System.out.println("Too few data fields at "
				        + line.elementAt(0) + ".  Data is " + line.size());
				return false;
			}
			if (line.size() != header.size()) {
				System.out.println("Warning: Data is " + line.size()
				        + " fields long.  Header is " + header.size()
				        + " long at " + line.elementAt(0));
			}
			System.out.println("Read " + line.elementAt(0));
		}
		return true;
	}

}
