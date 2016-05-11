import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.transform.TransformerConfigurationException;

import gnu.getopt.LongOpt;
import gnu.getopt.Getopt;
import gov.nih.nci.bdalite.api.system.FileUtil;
import gov.nih.nci.common.LBConfigConstants;
import gov.nih.nci.common.DeployConstants;
import gov.nih.nci.common.AppLogger;
import gov.nih.nci.databdadeploy.utils.DeployDatabaseUtil;
import gov.nih.nci.bdalite.api.system.ConsoleInput;
import gov.nih.nci.bdalite.api.system.ConsoleOutput;
import gov.nih.nci.bdalite.api.system.ExecUtil;
import gov.nih.nci.bdalite.api.system.HttpUtil;
import gov.nih.nci.bdalite.api.system.LockFIle;
import gov.nih.nci.bdalite.api.system.RemoteUtil;
import gov.nih.nci.bdalite.api.util.StringUtil;
import gov.nih.nci.bdalite.api.util.StopWatch;
import gov.nih.nci.xml.CodingScheme;
import gov.nih.nci.xml.MetadataXMLFile;
import gov.nih.nci.xml.RegistryXMLFile;
import static gov.nih.nci.common.DeployConstants.*;

/**
 * Application: Deploy LexEVS 5.1 files
 * 
 * @author garciawa2
 */
public class DeployApp {

	//
	// Define constants.
	// -----------------
	private final static String[] USAGE = { "Usage: deploy(.sh) -hterd -f prop -s action\n",
			"       h             -->  Help\n",
			"       f property    -->  File <property file>\n",
			"       s action      -->  Server (stop|start)\n",
			"       t             -->  Test SSH\n",
			"       e             -->  Exit after deployment\n",
			"       r             -->  Disable JBoss server restart\n",
			"       d             -->  Deploy without user interaction"};

	private final static String[] LOGO = { "\n",
			"NCI LexEVS Deploy Utility v" + MAJOR_VER + "." + MINOR_VER + "\n",
			"----------------------------------" };

	private static int retcode = 0;	
	private static boolean restart = true;
	private static boolean exitApp = false;
	
	/**
	 * Main application
	 * 
	 * @param argv
	 * @throws TransformerConfigurationException
	 */
	public static void main(String[] argv)
			throws TransformerConfigurationException {

		logo(); // Display application Logo
		AppLogger.rollOver(); // Roll log file if it exists
		AppLogger.info("Starting deploy application.");
		String serverCmd = null;	
		String propertyFile = null;
		boolean serverBounce = false;
		boolean autoDeploy = false;

		// Application parameters
		LongOpt longopts[] = {
			new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
			new LongOpt("server", LongOpt.REQUIRED_ARGUMENT, null, 's'),
			new LongOpt("file", LongOpt.REQUIRED_ARGUMENT, null, 'f'),
			new LongOpt("test", LongOpt.NO_ARGUMENT, null, 't'),
			new LongOpt("restart", LongOpt.NO_ARGUMENT, null, 'r'),
			new LongOpt("exit", LongOpt.NO_ARGUMENT, null, 'e'),
			new LongOpt("auto", LongOpt.NO_ARGUMENT, null, 'd')
		};

		Getopt g = new Getopt("export", argv, ":hs:tref:d", longopts);
		g.setOpterr(true); // Let the command line framework do some checking
		int c;

		// Process the parameters passed in by user

		if ((c = g.getopt()) != -1) {

			do {
				switch (c) {
				case 'h':
					usage();
					System.exit(0);		
				case 'f':
					propertyFile = g.getOptarg().trim();
					break;
				case 's':						
					serverCmd = g.getOptarg().trim();	
					if (serverCmd.equalsIgnoreCase("start") || serverCmd.equalsIgnoreCase("stop"))
						serverBounce = true;
					else {
						AppLogger.println("Unknown server command '" + serverCmd + "'.");
						System.exit(0);
					}					
					break;
				case 't':
					if (!loadPropFile(propertyFile))
						System.exit(0);						
					testSSH();
					System.exit(0);	
				case 'r':
					restart = false;
					break;
				case 'e':
					exitApp = true;
					break;
				case 'd':
					autoDeploy = true;
					break;					
				case '?':
					System.out.println();
					AppLogger.println("Error in command line!");
					System.exit(0);
				case ':':
					System.out.println();
					AppLogger.println("Missing parameters in command line!");
					System.exit(0);
				default:
					System.out.println();
					AppLogger.println("Unknown option '" + (char) c + "'.");
					usage();
					System.exit(0);
				}
			} while ((c = g.getopt()) != -1);

		}

		// Load property file	
		if (!loadPropFile(propertyFile))
			System.exit(0);				

        // Process lbconfig file
        if (!LBConfigConstants.loadPropertyFile(DeployConstants.getlBconfigFile())) {
        	AppLogger.println("Unable to proceed. Bad or missing LB property file.");
            System.exit(-1);
        }				
		
        // *** Run any command line actions
        
        if (serverBounce == true) {
			for (int x=0; x<DeployConstants.serverCount(); x++) {
				RemoteUtil remote = new RemoteUtil(DeployConstants
						.getTargetServer(x),
						DeployConstants.getTargetUser(x), DeployConstants
								.getSshKeyFile());				
				if (serverCmd.equalsIgnoreCase("start"))
					startJBoss(remote,x);
				else if (serverCmd.equalsIgnoreCase("stop"))	
					stopJBoss(remote,x);
			}  
			System.exit(0);
        }       
                
        if (autoDeploy == true) {
        	deployTerminologies_data();		// Deploy database portion
        	deployTerminologies_files();	// Deploy file portion
        	System.exit(0);
        }	
        
		// *** Check and Show parameters passed ***
		
		// Get user input
		Commands.displayCommands();
		boolean runFlag = true;
		String command = null;
		while (runFlag) {
			command = ConsoleInput.getString("\nEnter command: ");
			switch (Commands.toCommand(command)) {
			case help:
			case h:
				Commands.displayCommands();
				break;
			case info:
			case i:
				Info();
				break;
			case deployfiles:
			case df:
				deployTerminologies_files();
				if (exitApp) runFlag = false; 
				break;
			case deploydata:
			case dd:
				deployTerminologies_data();
				if (exitApp) runFlag = false;
				break;				
			case list:
			case l:	
				displayTerminologyList();
				break;
			case c:
			case clear:
				ConsoleOutput.clearScreen();
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

		// Cleanup and exit
		System.out.println();
		AppLogger.println("Program exiting(" + retcode + ").");
		System.exit(retcode);
	}

	/**
	 * Display program usage information.
	 * 
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
	 * @author garciawa2 Class to hold command list
	 */
	private enum Commands {
		// Command list
		help, h, deployfiles, df, deploydata, dd, info, i, clear, c, list, l, quit, q, NOVALUE;

		private final static String[] COMMANDS = {
				"Commands: (H)elp, (DF) DeployFiles, (DD) DeployDatabase, (L)ist, (I)nfo\n",
				"          (C)lear, (Q)uit\n",
				"\n  " + LBConfigConstants.HELP_MSG + ":\n",
				"  " + LBConfigConstants.HELP_URL
			};

		/**
		 * Get enum value of command from a string
		 * 
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
	 * Display company LOGO.
	 * 
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
		System.out.println("Current configuration");
		System.out.println("---------------------");
        System.out.println("         LexEVS version = "
                + LBConfigConstants.LEXEVSVER + " (Compatibility  with)");		
		System.out.println("           SSH key path = "
				+ DeployConstants.getSshKeyFile());
		System.out.println("            Publish dir = "
				+ DeployConstants.getSourcePublishDir());
		System.out.println("             Backup dir = "
				+ DeployConstants.getBackupDir());		
		System.out.println("        Target database = "
				+ LBConfigConstants.getDatabaseServer() + ":"
				+ LBConfigConstants.getDatabasePort());		
		System.out.println("   Target database name = "
				+ LBConfigConstants.getDatabaseName());
		System.out.println("   Target database user = "
				+ LBConfigConstants.getDatabaseUser());		
		System.out.println("   Target Registry File = "
				+ LBConfigConstants.getRegistryFile());		
		System.out.println("   Target Metadata File = "
				+ LBConfigConstants.getMetadataFile());	
		for (int x=0; x<DeployConstants.serverCount(); x++) {
			System.out.println("          Target server = " + (x+1) + " : "
					+ DeployConstants.getTargetURL(x));
			System.out.println("            Target user = " + (x+1) + " : "
					+ DeployConstants.getTargetUser(x));
		}
	}

	/**
	 * List terminologies from registry.xml fie
	 */
	private static void displayTerminologyList() {
		
		// Read Registry file		
		RegistryXMLFile regTemplate = null;
		regTemplate = new RegistryXMLFile(DeployConstants.getPublishDirDeploy()
			+ "/" + LBConfigConstants.REG_FILE_NAME + LBConfigConstants.TEMPLATE_EXT);		

		// Read Metadata file		
		MetadataXMLFile metaTemplate = null;
		metaTemplate = new MetadataXMLFile(DeployConstants.getPublishDirDeploy()
			+ "/" + LBConfigConstants.METADATA_FILE_NAME + LBConfigConstants.TEMPLATE_EXT);
		metaTemplate.readMetadata();
		
		// List coding schemes to add		
		
		List<CodingScheme> csList = regTemplate.getCodingSchemes();
		if (csList == null)	return;
		Iterator<CodingScheme> it = csList.iterator();
		System.out.println("\nTerminologies to be deployed:");
		if (csList.size() > 0) {
			for (int x = 0; x < csList.size(); x++) {
				CodingScheme value = (CodingScheme) it.next();
				// Print output
				System.out.println(String.format("  %02d : %s (%s)",
					(x + 1), metaTemplate
						.getCodingScheme(value.prefix), value.version));		
			}	
		}
		else
			System.out.println("  None.");
					
		// List coding schemes to be removed

		csList = regTemplate.getRemovedCodingSchemes();
		if (csList == null)	return;
		it = csList.iterator();		
		System.out.println("\nTerminologies to be removed:");
		if (csList == null || csList.size() > 0) {
			for (int x = 0; x < csList.size(); x++) {
				CodingScheme value = (CodingScheme) it.next();
				// Print output
				System.out.println(String.format("  %02d : %s (%s)",
					(x + 1), value.urn, value.version));		
			}		
		} else
			System.out.println("  None.");
	}

	/**
	 * Deploy the packaged terminology - DATABASE
	 * 
	 * @return
	 */
	private static int deployTerminologies_data() {
	
		if (ConsoleInput.getYesNo("\nDeploy the database (Y/N): ") == false)
			return 0;
		
		// Set lock file		
        if (!LockFIle.lock(DeployConstants.LOCK_FILE)) {
       	 System.out.println("\nError: Deploy application currently being run by another user!");
       	 return -1;
        }		
		    
		// Start a timer
		StopWatch timer = new StopWatch();
		timer.start();		
		
		// ***
		// *** Verify that external applications are avialable before starting ***
		// ***
		
		// Test for cygwin if running on a Windows machine
		if (ExecUtil.isWindows()) {
			if (!ExecUtil.existsBASH()) {
				AppLogger.println("Error: bash shell not found or wrong version found!");
				AppLogger.println("       Please install Cygwin.");
				AppLogger.println(DeployConstants.ABORT_DEPLOY);
				LockFIle.release();
				return (-1);
			} else
				AppLogger.println("Cygwin Bash found.");
		}

		// Read Registry file		
		RegistryXMLFile regTemplate = null;
		regTemplate = new RegistryXMLFile(DeployConstants.getPublishDirDeploy()
			+ "/" + LBConfigConstants.REG_FILE_NAME + LBConfigConstants.TEMPLATE_EXT);		
		
		List<CodingScheme> csList = regTemplate.getRemovedCodingSchemes();
		
		AppLogger.println("Removing database tables for...");
		if (DeployDatabaseUtil.removeTables(csList) != 0) {
			AppLogger.println(DeployConstants.ABORT_DEPLOY);
			LockFIle.release();
			return (-1);			
		}
	
		AppLogger.println("Deploying database tables...");
		if (DeployDatabaseUtil.loadTables() != 0) {
			AppLogger.println(DeployConstants.ABORT_DEPLOY);
			LockFIle.release();
			return (-1);			
		}

		// Print timing results
		timer.stop();
		StopWatch.ElapsedTime elapsedTime = timer.getElapsedTimeWithQualifier();
		System.out.format("Terminology data deployed in %.2f %s.\n",
				elapsedTime.time, elapsedTime.qualifier);

		LockFIle.release();
		return 0;
	}	
	
	/**
	 * Deploy the packaged terminology - FILES
	 * 
	 * @return
	 */
	private static int deployTerminologies_files() {	

		if (ConsoleInput.getYesNo("\nDeploy application files (Y/N): ") == false)
			return 0;		
		
		// Check for write access to Lucene index folder
		if (!FileUtil.isWritable(LBConfigConstants.getLuceneDir())) {
			System.out.println("Application requires write access to '"
					+ LBConfigConstants.getLuceneDir() + "'.");
			System.out.println("Unable to continue processing.");
			return (-1);
		}
    	
		// Set lock file		
        if (!LockFIle.lock(DeployConstants.LOCK_FILE)) {
       	 System.out.println("\nError: Deploy application currently being run by another user!");
       	 return -1;
        }			
       
		// Start a timer
		StopWatch timer = new StopWatch();
		timer.start();		

		// ***
		// *** Verify that external applications are avialable before starting ***
		// ***
		
		// Test for cygwin if running on a Windows machine
		if (ExecUtil.isWindows()) {
			if (!ExecUtil.existsBASH()) {
				AppLogger.println("Error: bash shell not found or wrong version found!");
				AppLogger.println("       Please install Cygwin.");
				AppLogger.println(DeployConstants.ABORT_DEPLOY);
				LockFIle.release();
				return (-1);
			} else
				AppLogger.println("Cygwin Bash found.");
		}		
		
		// Test for ssh
		if (!ExecUtil.existsSSH()) {
			AppLogger.println("Error: ssh utility not found!");
			LockFIle.release();
			return (-1);
		} else
			AppLogger.println("Utility ssh found.");		

		// ***
		// *** Prompt user for backup of index files ***
		// ***		
		
		if (ConsoleInput.getYesNo("\nBackup Lucene index files (Y/N): ") == true) {
	        if (!ExecUtil.existsTAR()) {
	        	AppLogger.println("Error: tar utility not found!");
	        	LockFIle.release();
	            return (-1);
	        } else
	        	AppLogger.println("Utility tar found.");
			
	        // Perform the backup
	        FileUtil.mkdir(DeployConstants.getBackupDir());
	        AppLogger.println("Using '" + DeployConstants.getBackupDir() + "' for backups.");	
			String bkCommand = "tar -C " + DeployConstants.getLBDir() +
				" -cvpzf " +  DeployConstants.getBackupDir() + File.separator +
			    "lexbig" + LBConfigConstants.LEXEVSVER	+ "_" + 
				StringUtil.getTimeStamp("MMddyyyy")	+ ".tgz resources";	        
	        AppLogger.println("Backing up '" + DeployConstants.getLBDir() + "'...");	        
	        AppLogger.println(bkCommand);
	        
	        if (ExecUtil.execCmd(bkCommand) != 0) {
	        	AppLogger.println("Unable to perform the backup!");
	        	return -1;	        	
	        }
		}
		
		// ***
		// *** Generate new registry.xml and metadata.xml files ***
		// ***
		
		// Backup current registry.xml
		String timeStamp = StringUtil.getTimeStamp();

		if (FileUtil.copyFile(LBConfigConstants.getRegistryFile(),
				LBConfigConstants.getRegistryFile() + "." + timeStamp) != 0) {
			AppLogger.println(DeployConstants.ABORT_DEPLOY);
			LockFIle.release();
			return (-1);
		}		
			
		// Read Registry template file
		
		RegistryXMLFile regTemplate = null;
		regTemplate = new RegistryXMLFile(DeployConstants.getPublishDirDeploy()
			+ "/" + LBConfigConstants.REG_FILE_NAME + LBConfigConstants.TEMPLATE_EXT);
		
		// Write new Registry file by merging with the template
		
		regTemplate.mergeFile(LBConfigConstants.getRegistryFile() + "." + timeStamp,
			LBConfigConstants.getRegistryFile());
		
		// Backup current metadata file
		
		if (FileUtil.copyFile(LBConfigConstants.getMetadataFile(),
				LBConfigConstants.getMetadataFile() + "." + timeStamp) != 0) {
			AppLogger.println(DeployConstants.ABORT_DEPLOY);
			LockFIle.release();
			return (-1);
		}		
		
		// Read metadata template file
		
		MetadataXMLFile metaTemplate = null;
		metaTemplate = new MetadataXMLFile(DeployConstants.getPublishDirDeploy()
			+ "/" + LBConfigConstants.METADATA_FILE_NAME + LBConfigConstants.TEMPLATE_EXT);	

		// Write new Metadata file by merging with the template
		
		metaTemplate.mergeFile(LBConfigConstants.getMetadataFile() + "." + timeStamp,
			LBConfigConstants.getMetadataFile());		
		
		// Shutdown Jboss server(s)
		if (restart) {
			for (int x=0; x< DeployConstants.serverCount(); x++) {
				// Setup a remote command object RemoteUtil remote = new
				RemoteUtil remote = new RemoteUtil(DeployConstants.getTargetServer(x),
						DeployConstants.getTargetUser(x), DeployConstants
								.getSshKeyFile());				
				if (stopJBoss(remote,x) != 0) {
					LockFIle.release();
					return(-1);
				}
			}
		}	

		// Clear Lucene index files related to deleted Terminologies
		 
		AppLogger.println("Clearing Lucene index folder of deleted Terminologies...");		
		
		List<CodingScheme> csList = regTemplate.getRemovedCodingSchemes();
		csList = regTemplate.getRemovedCodingSchemes();
		if (csList != null && csList.size() > 0)	{
			Iterator<CodingScheme> it = csList.iterator();
			it = csList.iterator();
			String path = null;
			for (int x = 0; x < csList.size(); x++) {
				CodingScheme value = (CodingScheme) it.next();				
				if (value.prefix != null && value.prefix.length() > 0) {
					path = LBConfigConstants.getLuceneDir() + "/" + value.prefix;
					AppLogger.println("  Removing '" + path + "'... ");
					if (ExecUtil.execCmd("rm -rf " + path) != 0) {
						AppLogger.println(DeployConstants.ABORT_DEPLOY);
						LockFIle.release();
						return(-1);
					}		
				}			
			}		
		} else {
			AppLogger.println("  Nothing to remove.");
		}
	  
		// Copy new Lucene index files
		
		AppLogger.println("Copying new Lucene index files..."); 
		if (!FileUtil.copyDirectory(DeployConstants.getPublishDirLucene(),LBConfigConstants.getLuceneDir())) {
			AppLogger.println(DeployConstants.ABORT_DEPLOY);
			LockFIle.release();
			return(-1);
		}		
	  
		// Start Jboss server(s)	
		if (restart) {
			for (int x=0; x<DeployConstants.serverCount(); x++) {
				// Setup a remote command object RemoteUtil remote = new
				RemoteUtil remote = new RemoteUtil(DeployConstants.getTargetServer(x),
						DeployConstants.getTargetUser(x), DeployConstants
								.getSshKeyFile());			
				if (startJBoss(remote,x) != 0) {
					LockFIle.release();
					return (-1);
				}
			}
		}	
		
		// Print timing results
		timer.stop();
		StopWatch.ElapsedTime elapsedTime = timer.getElapsedTimeWithQualifier();
		System.out.format("Terminologies deployed in %.2f %s.\n",
				elapsedTime.time, elapsedTime.qualifier);

		LockFIle.release();
		return 0;		
		
	}

	/**
	 * Start Lexevsapi JBoss
	 * @return
	 */
	private static int startJBoss(RemoteUtil remote, int index) {

		if (DeployConstants.getTargetURL(index) == null
				|| DeployConstants.getTargetURL(index)
						.equalsIgnoreCase("null"))
			return (0);
		
		AppLogger.println("Starting JBoss #" +  index +"...");
		
		// Check if JBoss already started
		
		if (HttpUtil.checkURLUp(DeployConstants.getTargetURL(index),true) == 0) {
			AppLogger.println("JBoss already started.");
			return (0);
		} 
		
		// If not, start JBoss		
		
		if (remote.execCmdNoWait(DeployConstants.getTargetJbossStart(index)) != 0) {
			AppLogger.println(DeployConstants.ABORT_DEPLOY);
			return (-1);
		} else {
			if (HttpUtil.checkURLUp(DeployConstants.getTargetURL(index),
					JBOSS_TRIES, JBOSS_WAIT, false) != 0) {
				System.out.println();
				AppLogger.println("Error! Could not start JBoss.");
				return (-1);
			} else
				AppLogger.println("JBoss has been started.");
		}			
		
		return 0;
	}

	/**
	 * Stop Lexevsapi JBoss
	 * @return
	 */
	private static int stopJBoss(RemoteUtil remote, int index) {
	
		if (DeployConstants.getTargetURL(index) == null
				|| DeployConstants.getTargetURL(index)
						.equalsIgnoreCase("null"))
			return (0);		
		
		AppLogger.println("Shutting down JBoss #" + index + "..."); 
		
		// Check if JBoss is already shutdown
		
		if (HttpUtil.checkURLUp(DeployConstants.getTargetURL(index),true) != 0) {
			AppLogger.println("JBoss already down.");
			return (0);
		}		
		
		// If running, stop JBoss
		
		if (remote.execCmdNoWait(DeployConstants.getTargetJbossStop(index)) != 0) {
			AppLogger.println(DeployConstants.ABORT_DEPLOY);
			return(-1);
		} else {
			if (HttpUtil.checkURLDown(DeployConstants.getTargetURL(index),
					JBOSS_TRIES, JBOSS_WAIT) != 0) {
				System.out.println();
				AppLogger.println("Error! Could not shutdown JBoss.");
				return (-1);
			} else {
				System.out.println();
				AppLogger.println("JBoss has been shutdown.");
			}
		}
				
		return 0;
	}	

	/**
	 * Stop Lexevsapi JBoss
	 * @return
	 */
	private static int testSSH() {			
		System.out.println("Testing SSH. (" +  DeployConstants.serverCount()
				+ ") server count.");	
		for (int x=0; x<DeployConstants.serverCount(); x++) {
			RemoteUtil remote = new RemoteUtil(DeployConstants
					.getTargetServer(x),
					DeployConstants.getTargetUser(x), DeployConstants
							.getSshKeyFile(), true);
			if (remote.execCmd("ls -ail") != 0) {
				System.out.println(DeployConstants.ABORT_DEPLOY);
				return(-1);
			}
		}
		return 0;
	}		
    
	private static boolean loadPropFile(String propertyFile) {
	
		// Load property file		
		if (propertyFile != null) {
			if (!FileUtil.fileExists(propertyFile)) {
				AppLogger.println("Property file does not exist!");
				return false;
			}			
			if (!DeployConstants.loadPropertyFile(propertyFile)) {
				AppLogger.println("Configuration error. Exiting.");
				return false;
			}			
		} else {
			if (!DeployConstants.loadPropertyFile()) {
				AppLogger.println("Configuration error. Exiting.");
				return false;
			}
		}		
		return true;
		
	}	
	
} // End Class MainApp
