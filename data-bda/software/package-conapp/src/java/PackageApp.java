import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.transform.TransformerConfigurationException;

import gnu.getopt.LongOpt;
import gnu.getopt.Getopt;
import gov.nih.nci.common.AppLogger;
import gov.nih.nci.bdalite.api.system.FileUtil;
import gov.nih.nci.common.LBConfigConstants;
import gov.nih.nci.common.PackageConstants;
import gov.nih.nci.bdalite.api.system.LockFIle;
import gov.nih.nci.bdalite.api.util.StopWatch;

import static gov.nih.nci.common.PackageConstants.*;

import gov.nih.nci.xml.CodingScheme;
import gov.nih.nci.xml.MetadataXMLFile;
import gov.nih.nci.xml.RegistryXMLFile;
import gov.nih.nci.bdalite.api.system.ConsoleInput;
import gov.nih.nci.bdalite.api.system.ExecUtil;
import gov.nih.nci.databdapackage.PackageLuceneDirs;
import gov.nih.nci.databdapackage.utils.PackageDatabaseUtil;

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
 * Application: Package LexEVS 5.1 files for deployment
 * @author garciawa2
 */
public class PackageApp {

    //
    // Define constants.
    // -----------------
    private final static String[] USAGE = { "Usage: package(.sh) -hb -f prop\n",
            "       h             -->  Help",
            "       f property    -->  File <property file>\n",
    		"       b             -->  Deploy Binaries"};
    private final static String[] LOGO = {
            "\n",
            "NCI LexEVS Package Utility v" + MAJOR_VER + "." + MINOR_VER + "\n",
            "----------------------------------" };
    
    private static int retcode = 0;
    private static MetadataXMLFile meta = null;
    private static RegistryXMLFile reg = null;
    private static final char SELECT_MARK = 'S';
    private static final char REMOVE_MARK = 'R';
    private static final char EMPTY_MARK = ' ';

    /**
     * Main application
     * @param argv
     * @throws TransformerConfigurationException
     */
    public static void main(String[] argv)
            throws TransformerConfigurationException {
    	
    	String propertyFile = null;
        logo(); // Display application Logo
        AppLogger.rollOver(); // Roll log file if it exists
        AppLogger.info("Starting package application.");

        // Application parameters
        LongOpt longopts[] = {
        	new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
        	new LongOpt("file", LongOpt.REQUIRED_ARGUMENT, null, 'f'),
        	new LongOpt("Binaries", LongOpt.NO_ARGUMENT, null, 'b')
        };

        Getopt g = new Getopt("export", argv, ":hbf:",
                longopts);
        g.setOpterr(true); // Let the command line framework do some checking
        int c;

        // Process the parameters passed in by user

        if ((c = g.getopt()) != -1) {

            do {
                switch (c) {
                case 'h':
                    usage();
                    System.exit(0);
                    break;
				case 'f':
					propertyFile = g.getOptarg().trim();
					break;                    
                case 'b':
                	deployBinariesForPack();
                    System.exit(0);
                    break;                    
                case '?':
                	System.out.println();
                	AppLogger.println("\nError in command line!");
                    System.exit(0);
                case ':':
                	System.out.println();
                	AppLogger.println("\nMissing parameters in command line!");
                    System.exit(0);
                default:
                	System.out.println();
                	AppLogger.println("\nUnknown option '" + (char) c + "'.");
                    usage();
                    System.exit(0);
                }
            } while ((c = g.getopt()) != -1);

        }

        // Load property file
        
        if (propertyFile != null) {
			if (!FileUtil.fileExists(propertyFile)) {
				AppLogger.println("Property file does not exist!");
				System.exit(0);				
			}        	
	        if (!PackageConstants.loadPropertyFile()) {
	        	AppLogger.println("Configuration error. Exiting.");
	            System.exit(0);
	        }       
        } else {
	        if (!PackageConstants.loadPropertyFile()) {
	        	AppLogger.println("Configuration error. Exiting.");
	            System.exit(0);
	        }         	
        }
        
        // Set max backup files
        AppLogger.setMaxBackupIndex(PackageConstants.getMaxBackupCount());
        
        // *** Check and Show parameters passed ***

        // Process lbconfig file
        if (!LBConfigConstants.loadPropertyFile(PackageConstants.getlBconfigFile())) {
        	AppLogger.println("Unable to proceed. Bad or missing LB property file.");
            System.exit(-1);
        }

        // Read in Metadata file
        AppLogger.println("Reading file ("
                + LBConfigConstants.getMetadataFile() + ")...");
        meta = new MetadataXMLFile(LBConfigConstants.getMetadataFile());
        if (!meta.readMetadata()) {
        	AppLogger.println("Unable to proceed. Bad or missing metadata file.");
            System.exit(-1);
        }

        // Read in Registry file
        AppLogger.println("Reading file ("
                + LBConfigConstants.getRegistryFile() + ")...");
        reg = new RegistryXMLFile(LBConfigConstants.getRegistryFile());
        if (reg == null) {
        	AppLogger.println("Unable to proceed. Bad or missing registry file.");
            System.exit(-1);
        }

        List<CodingScheme> csList = reg.getCodingSchemes();

        // Get user input
        Commands.displayCommands();
        boolean runFlag = true;
        String command = null;
        while (runFlag) {
            command = ConsoleInput.getString("\nEnter command: ");
            switch (Commands.toCommand(command)) {
            case list:
            case l:
                displayTerminologyList(csList);
                break;
            case help:
            case h:
                Commands.displayCommands();
                break;
            case select:
            case s:
                selectTerminology(command, csList);
                if (countSelectedTerminolgies(csList) > 0)
                    displayTerminologyList(csList);
                break;
            case unselect:
            case u:
                unselectTerminology(command, csList);
                displayTerminologyList(csList);
                break;
            case r:
                removeTerminology(command, csList);
                displayTerminologyList(csList);
                break;
            case pack:
            case p:
                packageTerminologyList(csList);
                break;
            case info:
            case i:
                Info();
                break;
            case bin:    
            case b:
            	deployBinariesForPack();
            	break;
            case clear:
            case c:
                clearConsole();
                break;
            case quit:
            case q:
                runFlag = false;
                break;
            default:
                if (command.length() > 0) {
                    System.out.println("Unknown command '" + command + "'.");
                    Commands.displayCommands();
                }
            }
        }

        reg.close();

        // Cleanup and exit
        System.out.println();
        AppLogger.println("Program exiting (" + retcode + ").");
        System.exit(retcode);
    }

    /**
     * Display program usage information.
     * @return void
     */
    private static void usage() {
        System.out.println();
        for (int x = 0; x < USAGE.length; x++) {
            System.out.print(USAGE[x]);
        }
        System.out.println("\n");
    }

    /**
     * Display company LOGO.
     * @return void
     */
    private static void logo() {
        for (int x = 0; x < LOGO.length; x++) {
            System.out.print(LOGO[x]);
        }
        System.out.println();
    }

    /**
     * Display current configuration
     */
    private static void Info() {
        System.out.println();
        System.out.println("Current Configuration");
        System.out.println("---------------------");
        System.out.println("   LexEVS version = "
                + LBConfigConstants.LEXEVSVER + " (Compatibility  with)");
        System.out.println("    Registry file = "
                + LBConfigConstants.getRegistryFile());
        System.out.println("    Metadata file = "
                + LBConfigConstants.getMetadataFile());
        System.out.println("      Publish dir = "
                + PackageConstants.getPublishDirDeploy());
        System.out.println(" Lucene directory = "
                + LBConfigConstants.getLuceneDir());
        System.out.println("     Database URL = "
                + LBConfigConstants.getDatabaseURL());
        System.out.println("    Database User = "
                + LBConfigConstants.getDatabaseUser());
        System.out.println("      Max backups = "
                + AppLogger.getMaxBackupIndex());        
    }

    /**
     * List terminologies
     * @param csList
     */
    private static void displayTerminologyList(List<CodingScheme> csList) {
        int countSel = countSelectedTerminolgies(csList);
        if (csList == null) return;
        Iterator<CodingScheme> it = csList.iterator();
        System.out.println("\nFound (" + csList.size() + ") terminologies. "
                + countSel + " selected:\n");
        char histMark;
        char selectedMark = EMPTY_MARK;

        for (int x = 0; x < csList.size(); x++) {
            CodingScheme value = (CodingScheme) it.next();
            // Check for history
            if (value.hasHistory)
                histMark = 'H';
            else
                histMark = ' ';
            // Test for operation type
            if (value.isSelected())
                selectedMark = SELECT_MARK;
            else if (value.isRemoved())
                selectedMark = REMOVE_MARK;
            else
                selectedMark = EMPTY_MARK;
            // Print output
            System.out.println(String.format("  %c %c %02d : %s (%s)",
                    histMark, selectedMark, (x + 1), meta
                            .getCodingScheme(value.prefix), value.version));
        }
    }

    /**
     * Return number of selected terminologies
     * @param csList
     * @return
     */
    private static int countSelectedTerminolgies(List<CodingScheme> csList) {
        int count = 0;
        if (csList == null)
            return 0;
        Iterator<CodingScheme> it = csList.iterator();
        for (int x = 0; x < csList.size(); x++) {
            CodingScheme value = (CodingScheme) it.next();
            if (value.isSelected() || value.isRemoved())
                count++;
        }
        return count;
    }

    /**
     * Sselect a Terminology
     * @param command
     * @param csList
     */
    private static void selectTerminology(String command,
            List<CodingScheme> csList) {
        if (command == null || csList == null)
            return;
        StringTokenizer st = new StringTokenizer(command, " \n\r\t,");
        st.nextToken(); // lose first command (s)
        String sel = null;
        int iSel = -1;
        while (st.hasMoreTokens()) {
            sel = st.nextToken();
            iSel = -1;
            try {
                if (sel.equalsIgnoreCase("a") || sel.equalsIgnoreCase("all")) {
                    for (int x = 0; x < csList.size(); x++)
                        ((CodingScheme) csList.get(x)).select();
                } else {
                    iSel = Integer.parseInt(sel);
                    if (iSel > 0 && iSel < csList.size() + 1)
                        ((CodingScheme) csList.get(iSel - 1)).select();
                    else
                        System.out.println("Input ignored.");
                }
            } catch (java.lang.NumberFormatException e) {
                // Ignore non-numeric input
                continue;
            }
        }
    }

    /**
     * Unselect a Terminology
     * @param command
     * @param csList
     */
    private static void unselectTerminology(String command,
            List<CodingScheme> csList) {
        if (command == null || csList == null)
            return;
        StringTokenizer st = new StringTokenizer(command, " \n\r\t,");
        st.nextToken(); // lose first command (s)
        String sel = null;
        while (st.hasMoreTokens()) {
            sel = st.nextToken();
            int iSel = -1;
            try {
                if (sel.equalsIgnoreCase("a") || sel.equalsIgnoreCase("all")) {
                    for (int x = 0; x < csList.size(); x++)
                        ((CodingScheme) csList.get(x)).unselect();
                } else {
                    iSel = Integer.parseInt(sel);
                    if (iSel > 0 && iSel < csList.size() + 1)
                        ((CodingScheme) csList.get(iSel - 1)).unselect();
                    else
                        System.out.println("Input ignored.");
                }
            } catch (java.lang.NumberFormatException e) {
                // Ignore non-numeric input
                continue;
            }
        }
    }

    /**
     * Select a Terminology for removal
     * @param command
     * @param csList
     */
    private static void removeTerminology(String command,
            List<CodingScheme> csList) {
        if (command == null || csList == null)
            return;
        StringTokenizer st = new StringTokenizer(command, " \n\r\t,");
        st.nextToken(); // lose first command (r)
        String sel = null;
        while (st.hasMoreTokens()) {
            sel = st.nextToken();
            int iSel = -1;
            try {
                if (sel.equalsIgnoreCase("a") || sel.equalsIgnoreCase("all")) {
                    for (int x = 0; x < csList.size(); x++)
                        ((CodingScheme) csList.get(x)).remove();
                } else {
                    iSel = Integer.parseInt(sel);
                    if (iSel > 0 && iSel < csList.size() + 1) {
                        ((CodingScheme) csList.get(iSel - 1)).remove();
                    } else
                        System.out.println("Input ignored.");
                }
            } catch (java.lang.NumberFormatException e) {
                // Ignore non-numeric input
                continue;
            }
        }
    }

    /**
     * @author garciawa2 Class to hold command list
     */
    private enum Commands {
        // Command list
        help, h, list, l, select, s, unselect, u, remove, r, pack, p, i, info, bin, b, clear, c, quit, q, NOVALUE;

        private final static String[] COMMANDS = {
                "Commands: (H)elp, (L)ist, (S)elect #, (U)nselect #, (R)emove #, (P)ack, (I)nfo\n",
                "          (B)inaries, (C)lear, (Q)uit\n\n",
                "Column identifiers:\n",
                "          S - Terminology selected for packaging.\n",
                "          R - Terminology selected for removal.\n",
                "          H - Terminology includes history tables.\n",
				"\n  " + LBConfigConstants.HELP_MSG + ":\n",
				"  " + LBConfigConstants.HELP_URL                
            };

        /**
         * Get enum value of command from a string
         * @param str
         * @return
         */
        public static Commands toCommand(String str) {
            if (str == null)
                return NOVALUE;
            try {
                // Get first command, ignore all others on this line
                StringTokenizer st = new StringTokenizer(str, " \n\r\t,");
                if (st.countTokens() > 0)
                    str = st.nextToken();
                return valueOf(str.trim().toLowerCase());
            } catch (Exception ex) {
                return NOVALUE;
            }
        }

        /**
         * Display list of commands
         *
         * @return void
         */
        private static void displayCommands() {
            System.out.println();
            for (int x = 0; x < COMMANDS.length; x++) {
                System.out.print(COMMANDS[x]);
            }
            System.out.println();
        }

    }

    /**
     * Clear console
     */
    private static void clearConsole() {
        final int ROWS = 25;
        for (int x = 0; x < ROWS; x++)
            System.out.println();
    }

    /**
     * Publish the selected terminology list
     * @param csList
     * @return
     */
    private static int packageTerminologyList(List<CodingScheme> csList) {
        int termCount = 0;
      
        if ((termCount = countSelectedTerminolgies(csList)) < 1) {
            System.out.println("\nNothing to pack.");
            return 0;
        }
        
		if (ConsoleInput.getYesNo("\nBegin packaging of terminologies (Y/N): ") == false)
			return 0;        

		// Set lock file		
        if (!LockFIle.lock(PackageConstants.LOCK_FILE)) {
       	 System.out.println("\nError: Packing application currently being run by another user!");
       	 return -1;
        }	

		System.out.println();
		AppLogger.println("\nPackaging started.");
        System.out.println("-------------------");
        AppLogger.println("Processing (" + termCount + ") terminologies.");

        // Start a timer
        StopWatch timer = new StopWatch();
        timer.start();
      
        // Verify that external applications are available before starting

        // Test for cygwin if running on a Windows machine
        if (ExecUtil.isWindows()) {
            if (!ExecUtil.existsBASH()) {
            	AppLogger.println("Error: bash shell not found or wrong version found!");
            	AppLogger.println("       Please install Cygwin.");
            	AppLogger.println(PackageConstants.ABORT_PUB);
            	LockFIle.release();
                return (-1);
            } else
            	AppLogger.println("Cygwin Bash found.");
        }
    
        // Test for mysql and mysqldump
        if (!ExecUtil.existsMySQL()) {
        	AppLogger.println("Error: mysqldump utility not found!");
        	LockFIle.release();
            return (-1);
        } else
        	AppLogger.println("Utility mysqldump found.");
        if (!PackageDatabaseUtil.pingMySQL()) {
        	AppLogger.println("Error: Unable to connect to master database!");
        	LockFIle.release();
            return (-1);
        } else
        	AppLogger.println("MySQL connection is good.");
        
        // Create packaging folders. If Windows pub folder will be in
        // Cygwin:/tmp

        AppLogger.println("Creating packaging folder...");
        if (ExecUtil.execCmd("mkdir -p " + PackageConstants.getPublishDirDeploy()) != 0) {
        	AppLogger.println("Error creating direcory '"
                    + PackageConstants.getPublishDirDeploy() + "'!");
        	LockFIle.release();
            return (-1);
        }
       
        // Write registry template file
        reg.writeRegistryFile(csList, PackageConstants.getPublishDirDeploy()
            + File.separator + LBConfigConstants.REG_FILE_NAME
            + LBConfigConstants.TEMPLATE_EXT);
        
        // Write metadata template file
        meta.writeMetadataFile(csList, PackageConstants.getPublishDirDeploy()
            + File.separator +LBConfigConstants.METADATA_FILE_NAME
            + LBConfigConstants.TEMPLATE_EXT);        
    
        AppLogger.println("Creating Lucene folder... " + PackageConstants.getPublishDirLucene());
        if (ExecUtil.execCmd("mkdir -p " + PackageConstants.getPublishDirLucene()) != 0) {
        	AppLogger.println("Error creating direcory '"
                    + PackageConstants.getPublishDirLucene() + "'!");
        	LockFIle.release();
            return(-1);
        }         
        
        // Store Lucene indexes
        if (PackageLuceneDirs.copyFiles(csList) != 0) {
        	AppLogger.println("Error storing Lucene indexes!");
        	LockFIle.release();
            return (-1);
        }

        AppLogger.println("Creating SQL folder... " + PackageConstants.getPublishDirSQL());
        if (ExecUtil.execCmdNW("mkdir -p " + PackageConstants.getPublishDirSQL()) != 0) {
        	AppLogger.println("Error creating direcory '"
                    + PackageConstants.getPublishDirSQL() + "'!");
        	LockFIle.release();
            return(-1);
        }        
         
        // Dump associated data tables
        PackageDatabaseUtil.dumpTables(csList, reg.getHistoryMap());
         
        // Print timing results
        timer.stop();
        StopWatch.ElapsedTime elapsedTime = timer.getElapsedTimeWithQualifier();
        System.out.format("Packaging completed in %.2f %s.\n",
                elapsedTime.time, elapsedTime.qualifier);

        LockFIle.release();
        
        return 0;
    }

    /**
     * Copy binaries in to publishing and deployment directories
     * (Used in software development)
     * @return
     */
	private static int deployBinariesForPack() {
		
        if (ExecUtil.execCmd("mkdir -p " + PackageConstants.getPublishDirPack()) != 0) {
            System.out.println("Error creating direcory '"
                    + PackageConstants.getPublishDirPack() + "'!");
            return (-1);
        }		
		
        if (ExecUtil.execCmd("mkdir -p " + PackageConstants.getPublishDirDeploy()) != 0) {
            System.out.println("Error creating direcory '"
                    + PackageConstants.getPublishDirDeploy() + "'!");
            return (-1);
        }
        
        // Package files
        
        System.out.println("\nPacking binaries...");
		FileUtil.copyFile("package.jar", PackageConstants.getPublishDirPack());
		FileUtil.copyFile("package.properties", PackageConstants.getPublishDirPack());
		FileUtil.copyFile("package.sh", PackageConstants.getPublishDirPack());
		ExecUtil.execCmd("chmod +x " + PackageConstants.getPublishDirPack()
				+ File.separator + "package.sh");		
		FileUtil.copyFile("mysql-connector-java-*.jar", PackageConstants.getPublishDirPack());
		FileUtil.copyFile("xercesImpl.jar", PackageConstants.getPublishDirPack());
		FileUtil.copyFile("log4j.jar", PackageConstants.getPublishDirPack());
		FileUtil.copyFile("bda-lite-*.jar", PackageConstants.getPublishDirPack());
		FileUtil.copyFile("ant*.jar", PackageConstants.getPublishDirPack());
		
		// Deploy files
		System.out.println("\nDeployment binaries...");
		FileUtil.copyFile("deploy.jar", PackageConstants.getPublishDirDeploy());
		FileUtil.copyFile("deploy.properties", PackageConstants.getPublishDirDeploy());
		if (FileUtil.fileExists(PackageConstants.getPublishDirDeploy()
				+ File.separator + "jboss.properties")) {
			FileUtil.appendFile(PackageConstants.getPublishDirDeploy()
				+ File.separator + "jboss.properties", PackageConstants.getPublishDirDeploy()
				+ File.separator + "deploy.properties");			
		}		
		FileUtil.copyFile("deploy.sh", PackageConstants.getPublishDirDeploy());
		ExecUtil.execCmd("chmod +x " + PackageConstants.getPublishDirDeploy()
				+ File.separator + "deploy.sh");		
		FileUtil.copyFile("mysql-connector-java-*.jar", PackageConstants.getPublishDirDeploy());
		FileUtil.copyFile("xercesImpl.jar", PackageConstants.getPublishDirDeploy());
		FileUtil.copyFile("log4j.jar", PackageConstants.getPublishDirDeploy());
		FileUtil.copyFile("bda-lite-*.jar", PackageConstants.getPublishDirDeploy());
		FileUtil.copyFile("ant*.jar", PackageConstants.getPublishDirDeploy());
			
		System.out.println();
		return 0;
	}	
	
} // End Class PackageApp
