package gov.nih.nci.evs.hugo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

public class HugoToOwl {
	private static Properties delimiters = new Properties();
	private static Properties columns = new Properties();
	private static Properties specialistDatabases = new Properties();
	private static Properties mainConfig = new Properties();
	private static URI saveURI = null;
	private File file = null;
	private static Properties sysProp = System.getProperties();

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String configFileName = null;
		if (args.length > 0) {
			configFileName = args[0];
		}

		if (configFileName == null) {
			final String filename = sysProp.getProperty("hugotoowl.properties");
			configFileName = filename;
		}

		// If config file name still null
		if (configFileName == null) {
			System.out.println("Must provide location of config file");
			System.exit(0);
		}

		HugoToOwl hto = new HugoToOwl();

		boolean propsValid = hto.configure(configFileName);
		if (propsValid) {
			hto.processHugo();
		}
	}

	private HugoToOwl() {

	}

	private boolean configure(String mainPropertyFile) {

		try {
			boolean propertiesValid = true;
			mainConfig = readProperties(mainPropertyFile);
			String inputFile = mainConfig.getProperty("source");
			file = readCsvFile(inputFile);

			String outputFile = mainConfig.getProperty("target");
			saveURI = new URI(outputFile);

			String columnsFileName = mainConfig.getProperty("columns");
			String columnsPath = new URI(columnsFileName).getPath();
			columns = readProperties(columnsPath);

			String delimitersFileName = mainConfig.getProperty("delimiters");
			String delimitersPath = new URI(delimitersFileName).getPath();
			delimiters = readProperties(delimitersPath);

			String specialistFileName = mainConfig.getProperty("specialist");
			String specialistPath = new URI(specialistFileName).getPath();
			specialistDatabases = readProperties(specialistPath);
			return propertiesValid;
		} catch (Exception e) {
			System.out.println(e.toString());
			return false;
		}
	}

	// private HugoToOwl(String fileLocation) {
	// file = readCsvFile(fileLocation);
	// try {
	// String savePath = processToURI(System.getProperty("user.dir")
	// + "\\Hugo.owl");
	// saveURI = new URI(savePath);
	// // HugoCvsParser cvsParser = new HugoCvsParser(file);
	// // HugoOntology hugoOntology = new HugoOntology(cvsParser);
	// // HugoOwlWriter owlWriter = new HugoOwlWriter(hugoOntology,
	// // saveURI);
	// } catch (Exception e) {
	// System.out.println("Error reading in CSV file.  Program ending");
	// e.printStackTrace();
	// System.exit(0);
	// }
	//
	// }

	// private HugoToOwl(String fileLocation, String savePath) {
	// file = readCsvFile(fileLocation);
	// try {
	// saveURI = new URI(savePath);
	// // HugoCvsParser cvsParser = new HugoCvsParser(file);
	// // HugoOntology hugoOntology = new HugoOntology(cvsParser);
	// // HugoOwlWriter owlWriter = new HugoOwlWriter(hugoOntology,
	// // saveURI);
	// } catch (Exception e) {
	// System.out.println("Error reading in CSV file.  Program ending");
	// e.printStackTrace();
	// System.exit(0);
	// }
	// }

	public void processHugo() {
		try {
			HugoCsvParser cvsParser = new HugoCsvParser(file);
			HugoOntology hugoOntology = new HugoOntology(cvsParser);
			HugoOwlWriter owlWriter = new HugoOwlWriter(hugoOntology, saveURI);
		} catch (Exception e) {
			System.out.println("Error reading in CSV file.  Program ending");
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static File readCsvFile(String fileLocation) {
		File file = checkValidURI(fileLocation);
		if (file == null) {
			file = checkValidPath(fileLocation);
		}
		if (file == null) {
			System.out.println("Not a valid file location");
			System.exit(0);
		}
		return file;
	}

	public static File checkValidURI(String fileLoc) {
		try {
			URI uri = new URI(fileLoc);
			return new File(uri);
		} catch (Exception e) {
			return null;
		}
	}

	public static File checkValidPath(String fileLoc) {
		try {
			File file = new File(fileLoc);
			return file;
		} catch (Exception e) {
			return null;
		}
	}

	public static Properties readProperties(String configFile) {
		Properties propFile = new Properties();
		try {
			FileInputStream instream = new FileInputStream(configFile);
			propFile.load(instream);
			instream.close();
		} catch (FileNotFoundException e) {
			System.out.println("No " + configFile + " found");
		} catch (IOException e) {
			System.out.println("Problem reading " + configFile);
		} catch (Exception e) {
			System.out.println("Unexpected error reading " + configFile);
		}
		return propFile;
	}

	public static Properties getDelimitedColumns() {
		return columns;
	}

	public static Properties getDelimiters() {
		return delimiters;
	}

	public static Properties getSpecialistDatabases() {
		return specialistDatabases;
	}

	public void setColumns(Properties in_Columns) {
		columns = in_Columns;
	}

	public void setDelimiters(Properties in_Delimiters) {
		delimiters = in_Delimiters;
	}

	public void setSpecialistDatabases(Properties in_Specialist) {
		specialistDatabases = in_Specialist;
	}

	public static String underscoredString(String input) {

		return input.trim().replace(" ", "_").replace("(", "_").replace(")",
		        "_");
	}

	public static String processToURI(String path) {
		String step1 = path.trim().replace("\\", "/").replace(" ", "%20");
		String step2 = "file:///" + step1;
		return step2;
	}
}