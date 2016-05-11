package gov.nih.nci.databdapackage.utils;

import gov.nih.nci.common.AppLogger;
import gov.nih.nci.bdalite.api.system.FileUtil;
import gov.nih.nci.common.LBConfigConstants;
import gov.nih.nci.common.PackageConstants;
import gov.nih.nci.bdalite.api.system.ExecUtil;
import gov.nih.nci.bdalite.api.util.StopWatch;
import gov.nih.nci.xml.CodingScheme;
import gov.nih.nci.xml.History;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
 * Utility class to hold database helper methods
 * @author garciawa2
 */
public class PackageDatabaseUtil {

	static public final String LIST_TABLES = "SELECT table_name FROM information_schema.tables WHERE table_name like ?;";
	private static Connection conn = null;

	/**
	 * Constructor
	 */
	private PackageDatabaseUtil() {
		// Prevent class from being explicitly instantiated
	}
	
	/**
	 * Dump data tables
	 * 
	 * @param csList
	 * @param histList
	 */
	public static void dumpTables(List<CodingScheme> csList,
			HashMap<String, History> histList) {

		// Start a timer
		StopWatch timer = new StopWatch();
		timer.start();

		// Create SQL directory if it doesn't exists
		if (ExecUtil.execCmd("mkdir -p " + PackageConstants.getPublishDirSQL()) != 0) {
			AppLogger.println("Error creating direcory '"
					+ PackageConstants.getPublishDirSQL() + "'!");
			return;
		}
		// Clear SQL directory if it already exists
		FileUtil.clearDirectory(PackageConstants.getPublishDirSQL());

		// Export tables selected by user

		conn = dbConnect();

		Iterator<CodingScheme> it = csList.iterator();
		for (int x = 0; x < csList.size(); x++) {
			CodingScheme value = (CodingScheme) it.next();
			if (value.isSelected()) {
				// Get data tables
				try {
					PreparedStatement stmt = (PreparedStatement) conn
							.prepareStatement(LIST_TABLES);
					ResultSet rs = null;

					stmt.setString(1, value.prefix + "%");
					if (stmt.execute()) {
						rs = stmt.getResultSet();
						while (rs.next()) {
							String table = rs.getString("table_name");
							AppLogger.println("Exporting '" + table + "'...");
							if (dumpSQLTable(table) != 0)
								return;
						} // End while
						rs.close();
					} else {
						AppLogger.println(PackageConstants.DB_ERROR);
						return;
					}

					// Get history tables

					if (value.hasHistory) {
						if (histList.containsKey(value.urn)) {
							History hist = histList.get(value.urn);
							stmt = (PreparedStatement) conn
									.prepareStatement(LIST_TABLES);
							rs = null;
							stmt.setString(1, hist.prefix + "%");
							if (stmt.execute()) {
								rs = stmt.getResultSet();
								while (rs.next()) {
									String table = rs.getString("table_name");
									AppLogger.println("Exporting history '"
											+ table + "'...");
									if (dumpSQLTable(table) != 0)
										return;
								} // End while
							} else {
								AppLogger.println(PackageConstants.DB_ERROR);
								return;
							}
						}
					} // End Hist
					
				} catch (SQLException e) {
					AppLogger.println(PackageConstants.DB_ERROR);
					e.printStackTrace();
					return;
				}
			}
		}

		dbClose();

		// Print timing results
		timer.stop();
		StopWatch.ElapsedTime elapsedTime = timer.getElapsedTimeWithQualifier();
		System.out.format("Database SQL files exported in %.2f %s.\n",
				elapsedTime.time, elapsedTime.qualifier);

	}

	/**
	 * Dump a SQL table
	 * @param tablename
	 * @return
	 */
	private static int dumpSQLTable(String tablename) {
		String cmd = "mysqldump"
			+ " --host="
			+ LBConfigConstants
					.getDatabaseServer()
			+ " --port="
			+ LBConfigConstants
					.getDatabasePort()
			+ " -u "
			+ LBConfigConstants
					.getDatabaseUser()
			+ " --password="
			+ LBConfigConstants
					.getDatabasePassword()
			+ " "
			+ LBConfigConstants
					.getDatabaseName() + " "
			+ tablename + " > "
			+ PackageConstants.getPublishDirSQL() + File.separator + tablename + ".sql";	
		return (ExecUtil.execCmd(cmd));
	}
	
	/**
	 * Check if the database is available
	 * 
	 * @return
	 */
	public static boolean pingMySQL() {
		Connection dbConn = dbConnect();
		if (dbConn == null)
			return false;
		try {
			dbConn.close();
		} catch (SQLException e) {
			AppLogger.println(PackageConstants.DB_ERROR);
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
					+ LBConfigConstants.getDatabaseName(), LBConfigConstants
					.getDatabaseUser(), LBConfigConstants.getDatabasePassword());
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
			AppLogger.println(PackageConstants.DB_ERROR);
			AppLogger.println("Please check the database credentials in file "
					+ PackageConstants.getPropertyFile() + ".");
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
				AppLogger.println(PackageConstants.DB_ERROR);
			}
	}

}
