package gov.nih.nci.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Vector;

import gov.nih.nci.common.AppLogger;
import gov.nih.nci.bdalite.api.system.FileUtil;

/**
 * Application constants class
 * @author garciawa2
 */
public class DeployConstants {

    // Application version
    static public final int MAJOR_VER = 1;
    static public final int MINOR_VER = 0;
    static public final String DEFAULT_CONFIG_FILE = "deploy.properties";    

    // Standard error messages
    static public final String MISSING_PARM = "Missing parameter";
    static public final String MISSING_VALUE = "Missing parameter value";
    static public final String BAD_VALUE = "Bad parameter value";
    static public final String ABORT_DEPLOY = "Deployment aborted.";
    static public final String DB_ERROR = "A database error has occurred. Unable to continue processing.";
    static public final String LOCK_FILE = "dpy.loc";
    
    // Class internal constants
    static private ResourceBundle rs;

    // Application constants
    static public final String NA = "N/A";
    static public final String TRUE = "true";
    static public final String FALSE = "false";
    static public final String EMPTY = "";
    static public final String TARFILE_INDEXES = "indexes.tar";
    static public final String TARFILE_SQL = "sql.tar";

    // Application server constants
    static public final int JBOSS_WAIT = 5;
    static public final int JBOSS_TRIES = 40;
    static public final int JBOSS_MAX = 10;
    
    // JBoss server information
    private static Vector<JBossServer> jbossList = null; 
    
    // Internal variables
    private static String propertyFile = null;

    /**
     * Constructor
     */
    private DeployConstants() {
        // Prevent class from being explicitly instantiated
    }

    /**
     * Load resource bundle using default property file name
     * @return
     */
    public static boolean loadPropertyFile() {
    	propertyFile = DEFAULT_CONFIG_FILE;
    	return loadPropertyFile(DEFAULT_CONFIG_FILE);
    }    
    
    /** 
     * Load resource bundle using a given property file name
     * @return
     */
    public static boolean loadPropertyFile(String propFile) {
        boolean retc = false;
        propertyFile = propFile;
        
        if (rs == null) {
            try {
            	// Read in property file
                rs = new PropertyResourceBundle(new FileInputStream(new File(
                		propFile)));
                AppLogger.println("Loading configuration file (" + propFile
                        + ").");
                
                // Read list of JBoss servers
                String target_server = null;
                JBossServer server = null;
                int count = 0;
                jbossList = new Vector<JBossServer>();
                DeployConstants cons = new DeployConstants();
                AppLogger.println("Loading list of JBoss servers...");
                for (int x=0; x<JBOSS_MAX; x++) {
                	target_server = _getString("target_server_"+x);
                	if (target_server != null) {
                		count++;                		
                		server = cons.new JBossServer();
                		server.target_server = target_server;
                		server.target_user = _getString("target_user_"+x);
                		server.target_start_jboss_path = _getString("target_start_jboss_path_"+x);
                		server.target_stop_jboss_path = _getString("target_stop_jboss_path_"+x);                		
                		server.target_url = _getString("target_url_"+x);
                		jbossList.add(server);
                	}              		
                }               
                AppLogger.println(count + " JBoss servers identified for restart."); 
                retc = true;
            } catch (java.util.MissingResourceException e) {
                // No resource file.
            	AppLogger.println("Missing resource file '" + propFile + "'.");
                retc = false;
            } catch (FileNotFoundException e) {
            	AppLogger.println("Configuration file '" + propFile
                        + "' not found.");
                retc = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retc;
    }

    /**
     * Return name of property file currently being used
     * @return
     */
    public static String getPropertyFileName() {
    	return propertyFile;
    }
    
    /**
     * Return application resource bundle
     *
     * @return
     */
    public static ResourceBundle getRs() {
        return rs;
    }

    /**
     * Retrieve a string from the resource file
     * @param name
     * @return
     */
    private static String _getString(String name) {
        try {
            String s = rs.getString(name);
            return s;
        } catch (MissingResourceException e) {
            return null;
        }
    }

    /**
     * Returns target lbconfig properties file path
     * @return
     */
    public static String getlBconfigFile() {
        return _getString("target_lbconfig");
    }     
        
    /**
     * Return location of Lexevs directory
     * @return Location of lexevs directory (2 up from LB config path)
     */
	public static String getLBDir() {
		String lbdir = FileUtil.getPath(getlBconfigFile()) + File.separator
				+ ".." + File.separator + "..";
		return FileUtil.getCanonicalPath(lbdir);
	}
    
    /**
     * Returns ssh key file location
     * @return
     */
    public static String getSshKeyFile() {
        return _getString("ssh.key.file");
    }

    /**
     * Returns source folder
     * @return
     */
    public static String getSourcePublishDir() {
        return _getString("publish_dir");
    }

    /**
     * Returns folder to publish from
     * @return
     */
    public static String getPublishDirDeploy() {
        return _getString("publish_dir") + File.separator + "deploy";
    }    

    /**
     * Returns SQL folder to publish from
     * @return
     */
    public static String getPublishDirSQL() {
        return _getString("publish_dir") + File.separator + "deploy/sql";
    }     

    /**
     * Returns Lucene folder to publish from
     * @return
     */
    public static String getPublishDirLucene() {
        return _getString("publish_dir") + File.separator + "deploy/lucene";
    }    

    /**
     * Returns Backup directory to use
     * @return
     */
    public static String getBackupDir() {
        return _getString("backup_dir");
    }        
    
    /**
     * Returns target server
     * @return
     */
    public static String getTargetServer(int index) {
    	if (jbossList == null) return null;    	
        return jbossList.get(index).target_server;
    }

    /**
     * Returns target user
     * @return
     */
    public static String getTargetUser(int index) {
    	if (jbossList == null) return null;
        return jbossList.get(index).target_user;
    }

    /**
     * Returns Jboss target startup script path
     * @return
     */
    public static String getTargetJbossStart(int index) {
    	if (jbossList == null) return null;
        return jbossList.get(index).target_start_jboss_path;
    }

    /**
     * Returns Jboss target stop script path
     * @return
     */
    public static String getTargetJbossStop(int index) {
    	if (jbossList == null) return null;
    	return jbossList.get(index).target_stop_jboss_path;
    }

    /**
     * Returns Lexevsapi url for target server
     * @return
     */
    public static String getTargetURL(int index) {
    	if (jbossList == null) return null;
    	return jbossList.get(index).target_url;
    }    
        
    /**
     * Class to hold JBoss server to stop / start information
     * @author garciawa2
     */
    class JBossServer {
    	public String target_server = null;
    	public String target_user = null;
    	public String target_start_jboss_path = null;  
    	public String target_stop_jboss_path = null;
    	public String target_url = null;
    }

    /**
     * Return number of servers to startup and shutdown
     * @return
     */
    public static int serverCount() {
    	if (jbossList == null) return 0;
    	return jbossList.size();
    }
    
} // End Class Constants
