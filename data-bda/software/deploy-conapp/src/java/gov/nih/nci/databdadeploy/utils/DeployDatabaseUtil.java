package gov.nih.nci.databdadeploy.utils;

import gov.nih.nci.common.DeployConstants;
import gov.nih.nci.common.AppLogger;
import gov.nih.nci.common.LBConfigConstants;
import gov.nih.nci.bdalite.api.system.ExecUtil;
import gov.nih.nci.xml.CodingScheme;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * <!-- LICENSE_TEXT_START --> Copyright 2008,2009 NGIT. This software was
 * developed in conjunction with the National Cancer Institute, and so to the
 * extent government employees are co-authors, any rights in such works shall be
 * subject to Title 17 of the United States Code, section 105. Redistribution
 * and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met: 1. Redistributions
 * of source code must retain the above copyright notice, this list of
 * conditions and the disclaimer of Article 3, below. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 2. The end-user documentation included with the
 * redistribution, if any, must include the following acknowledgment: "This
 * product includes software developed by NGIT and the National Cancer
 * Institute." If no such end-user documentation is to be included, this
 * acknowledgment shall appear in the software itself, wherever such third-party
 * acknowledgments normally appear. 3. The names
 * "The National Cancer Institute", "NCI" and "NGIT" must not be used to endorse
 * or promote products derived from this software. 4. This license does not
 * authorize the incorporation of this software into any third party proprietary
 * programs. This license does not authorize the recipient to use any trademarks
 * owned by either NCI or NGIT 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 * DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE, NGIT, OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. <!-- LICENSE_TEXT_END -->
 */

public class DeployDatabaseUtil {

	static public final String LIST_TABLES = "SELECT table_name FROM information_schema.tables where table_name like ?;";
	static public final String DROP_TABLE = "DROP TABLE IF EXISTS ";
	private static Connection conn = null;

	/**
	 * Constructor
	 */
	private DeployDatabaseUtil() {
		// Prevent class from being explicitly instantiated
	}

	/**
	 * Check if the database is available
	 * 
	 * @return
	 */
	public static boolean pingMySQL() {
		AppLogger.println("Testing connection to "
				+ LBConfigConstants.getDatabaseServer()
				+ ":" + LBConfigConstants.getDatabasePort()
				+ "...");
		Connection dbConn = dbConnect();
		if (dbConn == null)
			return false;
		try {
			dbConn.close();
		} catch (SQLException e) {
			AppLogger.println(DeployConstants.DB_ERROR);
		}
		return true;
	}

	/**
	 * Connect to the master database
	 * 
	 * @return
	 */
	private static Connection dbConnect() {

		if (conn != null)
			return conn;
		Connection dbConn = null;

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			dbConn = DriverManager.getConnection("jdbc:mysql://"
					+ LBConfigConstants.getDatabaseServer() + ":"
					+ LBConfigConstants.getDatabasePort() + "/"
					+ LBConfigConstants.getDatabaseName(),LBConfigConstants
					.getDatabaseUser(), LBConfigConstants
					.getDatabasePassword());
		} catch (InstantiationException e) {
			AppLogger.println(ExecUtil.SYS_ERROR);
			e.printStackTrace();
			return (null);
		} catch (IllegalAccessException e) {
			AppLogger.println(ExecUtil.SYS_ERROR);
			e.printStackTrace();
			return (null);
		} catch (ClassNotFoundException e) {
			AppLogger.println(ExecUtil.SYS_ERROR);
			e.printStackTrace();
			return (null);
		} catch (SQLException e) {
			AppLogger.println(DeployConstants.DB_ERROR);
			AppLogger.println("Please check the database credentials in property file '"
					+ DeployConstants.getPropertyFileName() + "'.");
		}

		return dbConn;
	}

	/**
	 * Close the connect to the master database
	 */
	private static void dbClose() {
		if (conn != null)
			try {
				conn.close();
				conn = null;
			} catch (SQLException e) {
				AppLogger.println(DeployConstants.DB_ERROR);
			}
	}	
	
	/**
	 * Load data tables
	 */
	public static int loadTables() {	
		
		// Modify path for cygwin
		String sourceDir = null;
		if (ExecUtil.getOsName().equals(ExecUtil.UNIX)) {
			sourceDir = DeployConstants.getPublishDirSQL();
		} else {
			// If MS Windows, convert to a cygwin path
			sourceDir = ExecUtil.cygwinPath(DeployConstants.getPublishDirSQL());
		}		
	
		// Run MySQL import
		File fd = new File(sourceDir);
		
		String[] child = fd.list();
		for (int i=0; i<child.length; i++) {
			AppLogger.println("Loading file '" + child[i] + "'...");
			String cmd = "mysql" + " --host="
					+ LBConfigConstants.getDatabaseServer() + " --port="
					+ LBConfigConstants.getDatabasePort() + " --user="
					+ LBConfigConstants.getDatabaseUser() + " --password="
					+ LBConfigConstants.getDatabasePassword() + " "
					+ LBConfigConstants.getDatabaseName() + " < "
					+ sourceDir + File.separator + child[i];
			if (ExecUtil.execCmd(cmd) != 0) {
				return (-1);
			}
		}
	
		return (0);
	}
	
	/**
	 * Load data tables
	 */
	public static int removeTables(List<CodingScheme> csList) {
		
		if (csList == null || csList.size() < 1) {
			AppLogger.println("No database tables to remove.");
			return 0;
		}
		
		conn = dbConnect();
		
		Iterator<CodingScheme> it = csList.iterator();		
		for (int x = 0; x < csList.size(); x++) {
			CodingScheme value = (CodingScheme) it.next();

			AppLogger.println(String.format("  %02d : %s (%s)",
				(x + 1), value.urn, value.version));

			try {			
				Statement execStmt = conn.createStatement();
				execStmt.execute("SET foreign_key_checks = 0");
				
				PreparedStatement stmt = (PreparedStatement) conn
						.prepareStatement(LIST_TABLES);
				stmt.setString(1, value.prefix + "%");
				ResultSet rs = null;
				if (stmt.execute()) {
					rs = stmt.getResultSet();
					while (rs.next()) {
						String table = rs.getString("table_name");
						Statement dropStmt = conn.createStatement ();
						AppLogger.println("     Dropping table '" + table + "'...");
						dropStmt.execute(DROP_TABLE + table);
						dropStmt.close();
					}
				}
				execStmt.execute("SET foreign_key_checks = 1");
			} catch (SQLException e) {
				AppLogger.println(DeployConstants.DB_ERROR);
				AppLogger.info(e.getMessage());
				return (-1);
			}			
			
		}
	
		dbClose();
		return (0);
	}
	
} // End of class DatabaseUtil
