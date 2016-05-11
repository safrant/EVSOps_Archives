package gov.nih.nci.common;

import gov.nih.nci.bdalite.api.system.FileUtil;
import gov.nih.nci.bdalite.common.BDAException;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 * Build / deployment runtime logger
 *
 * @author garciawa2
 * @since 1.0
 */
public class AppLogger {

    private static Logger logger = null;
    private static final String CONVERSION_PATTERN = "%d{ABSOLUTE} %5p - %m%n";
    private static final String LOG_FILE = "runtime.log";
    private static final String NAME = "RUNLOG";
    public static final int MAX_BACKUPS_DEFAULT = 3;
    private static int max_backups = MAX_BACKUPS_DEFAULT;

    /**
     * Constructor
     */
    private AppLogger() {
        // Prevent class from being explicitly instantiated
    }

    /**
     * Initialize the logger class
     */
    public static void init() {
    	if (logger != null) return; // Logger already started
    	
        logger = Logger.getLogger(AppLogger.class.getName());
        PatternLayout evsLayout = new PatternLayout();
        evsLayout.setConversionPattern(CONVERSION_PATTERN);

        RollingFileAppender evsAppender = null;
        try {
            evsAppender = new RollingFileAppender(evsLayout,LOG_FILE);
            evsAppender.setName(NAME);
            evsAppender.setMaxBackupIndex(max_backups);
        } catch (IOException e) {
        	throw new BDAException("Error: '" + e.getMessage() + "'.");
        }
        logger.addAppender(evsAppender);
        logger.setLevel((Level) Level.DEBUG);
    }

    /**
     * Set max backup files
     * @param count
     */
    public static void setMaxBackupIndex(int count) {
    	if (logger == null) init();
    	if (count < 1) count = 1;
    	((RollingFileAppender)logger.getAppender(NAME)).setMaxBackupIndex(count);
    	max_backups = count;    	
    }    
    
    /**
     * Return actual max backup count
     * @return Maximum backup index count
     */
    public static int getMaxBackupIndex() {
    	if (logger == null) init();
    	return ((RollingFileAppender)logger.getAppender(NAME)).getMaxBackupIndex();
    }
    
    /**
     * Roll the log over
     */
    public static void rollOver() {
    	if (FileUtil.fileExists(LOG_FILE)) {
    		if (logger == null) init();
    		((RollingFileAppender)logger.getAppender(NAME)).rollOver();
    		return;
    	}
    }
    
    /**
     * Write a info message
     * @param text
     */
    public static void info(String text) {
        if (logger == null) init();
        if (text == null || text.length() < 1) return;
        logger.info(text);
    }

    /**
     * Write to both the console and the runtime log file
     * @param text
     */
    public static void println(String text) {
        if (logger == null) init();
        if (text == null || text.length() < 1) return;
        System.out.println(text);
        logger.info(text);
    }

} // End BDALogger