package gov.nih.nci.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * LexBIG constants class
 *
 * @author garciawa2
 */
public class LBConfigConstants {

	// Class public constants
	static public final String LEXEVSVER = "51";
	
    // Class internal constants
    static private ResourceBundle rs;	
    static private String lb_base_path = null;
    static private String propfile = null;
    static private String db_url = null;
    static private String db_server = null;
    static private String db_port = null;    
    static private String db_name = null;
        
    // XML file constants
    static public final String METADATA_FILE_NAME = "metadata.xml";
    static public final String REG_FILE_NAME = "registry.xml";
    static public final String TEMPLATE_EXT = ".template";
    
    // XML file constants
    static public final String REG_HEADER_TAG = "LexBIG_Registry";
    static public final String META_HEADER_TAG = "IndexerServiceMetaData";
    static public final String CODINGSCHEMES_TAG = "codingSchemes";
    static public final String CODINGSCHEME_TAG = "codingScheme";    
    static public final String REMOVED_CODINGSCHEMES_TAG = "removedCodingSchemes";
    static public final String REMOVED_CODINGSCHEME_TAG = "removedCodingScheme";
    static public final String HISTORIES_TAG = "histories";
    static public final String HISTORY_TAG = "history";
    static public final String NOTE_TAG = "note";
    static public final String INDEX_TAG = "index";   
	
    // Help constants    
    static public final String HELP_MSG = "For detailed usage information please visit the following URL";
	static public final String HELP_URL = "https://wiki.nci.nih.gov/display/EVS/NCI+LexEVS+Data+Packaging+and+Deployment+Utility";
   
    /**
     * Constructor
     */
    private LBConfigConstants() {
        // Prevent class from being explicitly instantiated
    }    
    
    /**
     * Load resource bundle
     */
    public static boolean loadPropertyFile(String propfile) {
    	if (propfile == null) return false;    
        boolean retc = false;
        if (rs == null) {
            try {
                rs = new PropertyResourceBundle(new FileInputStream(new File(
                		propfile)));
                AppLogger.println("Loading properties file (" + propfile
                        + ").");
                retc = true;
            } catch (java.util.MissingResourceException e) {
                // No resource file.
            	AppLogger.println("Missing resource file '" + propfile + "'.");
                retc = false;
            } catch (FileNotFoundException e) {
            	AppLogger.println("Properties file '" + propfile
                        + "' not found.");
                retc = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LBConfigConstants.propfile = propfile;
        
        // Breakup connection URL for use in command line queries
        _parseDatabaseURL();
        
        return retc;
    }    
    
    /**
     * Return application resource bundle
     *
     * @return
     */
    public static ResourceBundle getRs() {
    	if (rs == null) loadPropertyFile(propfile);
        return rs;
    }    
    
    /**
     * Retrieve a string from the resource file
     * @param name
     * @return
     */
    private static String _getString(String name) {
    	if (rs == null) return null;
        try {
            String s = rs.getString(name);
            return s;
        } catch (MissingResourceException e) {
            return null;
        }
    }    

    /**
     * Parse database connection URL
     * @return
     */
    private static void _parseDatabaseURL() {
    	db_url = _getString("DB_URL");
    	if (db_url == null) {        	
    		AppLogger.println("Error: Property 'DB_URL' not found!");        	
        	return;        	
        }
        Pattern p = Pattern.compile("^(.*)://([^/]+):(.*)/(.*)");
        Matcher m = p.matcher(db_url);

        if (m.matches()) {
        	db_server = m.group(2);
        	db_port = m.group(3);
        	db_name = m.group(4);
        }
    }    
    
    /**
     * Returns LB base file path
     * @return
     */
    private static String _getLBBasePath() {
    	if (lb_base_path != null) return lb_base_path;    	
    	// Compute LB base path
    	if (_getString("LG_BASE_PATH") != null && _getString("LG_BASE_PATH").length() > 0) { 
    		lb_base_path = _getString("LG_BASE_PATH") + File.separator;
    		return lb_base_path;    		
    	}    	
    	if (propfile == null) return ".";
    	File path = new File(propfile);
    	return path.getParent();
    }     
    
    /**
     * Returns master metadata file path
     * @return
     */
    public static String getMetadataFile() {
    	File path = new File(_getLBBasePath() + File.separator + _getString("INDEX_LOCATION"));
        try {
			return path.getCanonicalPath() + File.separator + METADATA_FILE_NAME;
		} catch (IOException e) {
			AppLogger.println("Metadata file '" + METADATA_FILE_NAME
                    + "' not found.");			
		}
		return null;
    }    
    
    /**
     * Returns master registry file path
     * @return
     */
    public static String getRegistryFile() {
    	File path = new File(_getLBBasePath());
        try {
			return path.getCanonicalPath() + File.separator + _getString("REGISTRY_FILE");
		} catch (IOException e) {
			AppLogger.println("Registry file '" + _getString("REGISTRY_FILE")
					+ "' not found.");	
		}
		return null;        
      }    
    
    /**
     * Returns folder to Lucene index files
     * @return
     */
    public static String getLuceneDir() {
    	File path = new File(_getLBBasePath() + File.separator + _getString("INDEX_LOCATION"));
        try {
			return path.getCanonicalPath();
		} catch (IOException e) {
			AppLogger.println("Index directory '" + _getString("INDEX_LOCATION")
                    + "' not found.");			
		}
		return null;
    }    
    
    /**
     * Returns database java URL
     * @return
     */
    public static String getDatabaseURL() {
    	return db_url;    	
    }    
    
    /**
     * Returns database server name
     * @return
     */
    public static String getDatabaseServer() {
        return db_server;
    }    
    
    /**
     * Returns database port
     * @return
     */
    public static String getDatabasePort() {
        return db_port;
    }    
    
    /**
     * Returns database name
     * @return
     */
    public static String getDatabaseName() {
        return db_name;
    }    
    
    /**
     * Returns database user name
     * @return
     */
    public static String getDatabaseUser() {
        return _getString("DB_USER");
    }

    /**
     * Returns database user password
     * @return
     */
    public static String getDatabasePassword() {
        return _getString("DB_PASSWORD");
    }    
    
}
