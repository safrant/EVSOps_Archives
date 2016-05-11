package gov.nih.nci.databdapackage.utils;

import gov.nih.nci.common.AppLogger;
import gov.nih.nci.common.LBConfigConstants;
import gov.nih.nci.common.PackageConstants;
import gov.nih.nci.xml.CodingScheme;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
 * Windows Zip util class
 * 
 * @author garciawa2
 */
public class ZipUtil {

	/**
	 * Constructor
	 */
	private ZipUtil() {
		// Prevent class from being explicitly instantiated
	}

	/**
	 * Compress Lucene index files for deployment
	 * 
	 * @return
	 */
	public static void compressFiles(List<CodingScheme> csList) {

		try {
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					PackageConstants.getPublishDirDeploy() + File.separator
							+ PackageConstants.ZIPFILE_INDEXES));
			AppLogger.println("Creating : " + PackageConstants.ZIPFILE_INDEXES);

			// Add Lucene files for metadata
			addDir(new File(LBConfigConstants.getLuceneDir() + File.separator
					+ "MetaDataIndex"), out);

			// Add Lucene files for vocabs
			Iterator<CodingScheme> it = csList.iterator();
			for (int x = 0; x < csList.size(); x++) {
				CodingScheme value = (CodingScheme) it.next();
				if (value.isSelected()) {
					addDir(new File(LBConfigConstants.getLuceneDir()
							+ File.separator + value.prefix), out);
				}
			}
			// Complete the ZIP file
			out.close();
			AppLogger.println("Zip completed.");

		} catch (FileNotFoundException e) {
			AppLogger.println("Error: Index folder not found!");
			AppLogger.println(e.getMessage());
			return;
		} catch (IOException e) {
			AppLogger.println(e.getMessage());
			return;
		} catch (OutOfMemoryError e) {
			AppLogger.println(PackageConstants.OUT_OF_MEMORY);
			return;
		}
		AppLogger.println("Lucene indexes packed for deployment.");
	}

	/**
	 * Add a folder to the zip file
	 * 
	 * @param dirObj
	 * @param out
	 * @throws IOException
	 */
	private static void addDir(File dirObj, ZipOutputStream out)
			throws IOException {
		File[] files = dirObj.listFiles();
		byte[] tmpBuf = new byte[PackageConstants.BUF_LEN];

		// Add folders to zip
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				addDir(files[i], out);
				continue;
			}

			FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
			AppLogger.println(" Adding: " + files[i].getAbsolutePath());

			out.putNextEntry(new ZipEntry(files[i].getAbsolutePath()));

			// Transfer from the file to the ZIP file
			int len;
			while ((len = in.read(tmpBuf)) > 0) {
				out.write(tmpBuf, 0, len);
			}

			// Complete the entry
			out.closeEntry();
			in.close();
		}

		tmpBuf = null;

	}

}
