package gov.nih.nci.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import gov.nih.nci.common.AppLogger;

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
 * Application constants class
 *
 * @author garciawa2
 */
public class PackageConstants {

    // Application version
    static public final int MAJOR_VER = 1;
    static public final int MINOR_VER = 0;

    // Class internal constants
    static private ResourceBundle rs;

    // Application constants
    static public final String NA = "N/A";
    static public final String TRUE = "true";
    static public final String FALSE = "false";
    static public final String EMPTY = "";
    static public final String NOT_FOUND = "Not found!";
    static public final char ESCAPE = '@';
    static public final String DEFAULT_CONFIG_FILE = "package.properties";    
    static public final String ZIPFILE_INDEXES = "indexes.zip";
    static public final String TARFILE_INDEXES = "indexes.tar";
    static public final String TARFILE_SQL = "sql.tar";
    static public final String LOCK_FILE = "pkg.loc";

    // Standard error messages
    static public final String MISSING_PARM = "Missing parameter";
    static public final String MISSING_VALUE = "Missing parameter value";
    static public final String BAD_VALUE = "Bad parameter value";
    static public final String BAD_REG_FILE = "Error: Invalid LexEVS registry file.";
    static public final String WARN_BAD_REG_FILE = "Warning: Badly formed LexEVS registry file.";
    static public final String BAD_META_FILE = "Error: Invalid LexEVS metadata file.";
    static public final String WARN_BAD_META_FILE = "Warning: Badly formed LexEVS metadata file.";
    static public final String OUT_OF_MEMORY = "Error: Out of memory!";
    static public final String DB_ERROR = "A database error has occurred. Unable to continue processing.";
    static public final String ABORT_PUB = "Publish aborted.";

    // Deployment substitution variables.
    static public final String DB_URL = "dbURL";

    // XML formatiing defaults
    static public final int INDENT = 2;
    static public final int LINE_WIDTH = 500;

    // Zip constants
    static public final int BUF_LEN = 102400000;    // 10MB

    // Internal variables
    private static String propertyFile = null;
    
    /**
     * Constructor
     */
    private PackageConstants() {
        // Prevent class from being explicitly instantiated
    }

    /**
     * Return name of current property file being used
     * @return
     */
    public static String getPropertyFile() {
    	return propertyFile;
    }    
    
    /**
     * Load resource bundle with default property files name
     * @return
     */
    public static boolean loadPropertyFile() {    
    	propertyFile = DEFAULT_CONFIG_FILE;
    	return loadPropertyFile(DEFAULT_CONFIG_FILE);
    }
    
    /**
     * Load resource bundle with a given property files name
     * @param propertyFile
     * @return
     */
    public static boolean loadPropertyFile(String fname) {
    	propertyFile = fname;
    	
        boolean retc = false;
        if (rs == null) {
            try {
                rs = new PropertyResourceBundle(new FileInputStream(new File(
                		fname)));
                AppLogger.println("Loading configuration file (" + fname
                        + ").");
                retc = true;
            } catch (java.util.MissingResourceException e) {
                // No resource file.
            	AppLogger.println("Missing resource file '" + fname + "'.");
                retc = false;
            } catch (FileNotFoundException e) {
            	AppLogger.println("Configuration file '" + fname
                        + "' not found.");
                retc = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retc;
    }

    /**
     * Return application resource bundle
     * @return
     */
    public static ResourceBundle getRs() {
    	if (rs == null) loadPropertyFile();
        return rs;
    }

    /**
     * Retrieve a string from the resource file
     * @param name
     * @return
     */
    private static String _getString(String name) {
    	if (rs == null) loadPropertyFile();
        try {
            String s = rs.getString(name);
            return s;
        } catch (MissingResourceException e) {
            return null;
        }
    }

    /**
     * Returns master lbconfig properties file path
     * @return
     */
    public static String getlBconfigFile() {
        return _getString("master_lbconfig");
    }    
    
    /**
     * Returns folder to publish in
     * @return
     */
    public static String getPublishDirDeploy() {
        return _getString("publish_dir") + File.separator + "deploy";
    }

    /**
     * Returns folder to publish in
     * @return
     */
    public static String getPublishDirPack() {
        return _getString("publish_dir") + File.separator + "pack";
    }    

    /**
     * Returns folder to store lucene index directories in
     * @return
     */
    public static String getPublishDirLucene() {
        return _getString("publish_dir") + File.separator + "deploy/lucene";
    }     

    /**
     * Returns folder to store SQL files in
     * @return
     */
    public static String getPublishDirSQL() {
        return _getString("publish_dir") + File.separator + "deploy/sql";
    }       
    
    /**
     * Return backup count
     * @return
     */
    public static int getMaxBackupCount() {
    	String max_s = _getString("max_backups");
    	if (max_s == null || max_s.length() < 1) {
    		// Get default from logger class
    		return AppLogger.MAX_BACKUPS_DEFAULT;
    	}
    	int i = 0;
        try {
        	i = Integer.parseInt(max_s.trim());
        } catch (NumberFormatException e) {
        	i = 1;
        }
    	return i;
    }
    
} // End Class Constants
