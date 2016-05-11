package gov.nih.nci.databdapackage;

import gov.nih.nci.common.AppLogger;
import gov.nih.nci.bdalite.api.system.FileUtil;
import gov.nih.nci.common.LBConfigConstants;
import gov.nih.nci.common.PackageConstants;
import gov.nih.nci.bdalite.api.util.StopWatch;
import gov.nih.nci.bdalite.api.system.ExecUtil;
import gov.nih.nci.xml.CodingScheme;

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
 * Compress Lucene directories utility class
 *
 * @author garciawa2
 */
public class PackageLuceneDirs {

    /**
     * Constructor
     */
    private PackageLuceneDirs() {
        // Prevent class from being explicitly instantiated
    }
    
    /**
     * Copy Lucene index files for deployment
     *
     * @return
     */
    public static int copyFiles(List<CodingScheme> csList) {

        String luceneSrcDir = null, luceneTrgDir = null;

        // Start a timer
        StopWatch timer = new StopWatch();
        timer.start();

        if (ExecUtil.getOsName().equals(ExecUtil.UNIX)) {
            luceneTrgDir = PackageConstants.getPublishDirLucene();
            luceneSrcDir = LBConfigConstants.getLuceneDir();            
        } else {
            // If MS Windows, convert to a cygwin path
        	luceneTrgDir = ExecUtil.cygwinPath(PackageConstants.getPublishDirLucene());
        	luceneSrcDir = ExecUtil.cygwinPath(LBConfigConstants.getLuceneDir());
        }

        // Remove old lucene directories from store location        
        FileUtil.clearDirectory(luceneTrgDir);
        
        // Add Lucene directories to the new tar file
        Iterator<CodingScheme> it = csList.iterator();
        int count = 1;
        for (int x = 0; x < csList.size(); x++) {
            CodingScheme value = (CodingScheme) it.next();
            if (value.isSelected()) {
            	AppLogger.println((count++) + ". Storing index ("
                        + value.prefix + ")...");
                String cmd = "cp -r " + luceneSrcDir + "/" + value.prefix + " " 
                        + luceneTrgDir + "/" + value.prefix;
                if (ExecUtil.execCmd(cmd) != 0) return(-1);                
            }
        }

        // Add Lucene files for metadata
        AppLogger.println((count++) + ". Storing index (MetaDataIndex)...");
        String cmd = "cp -r " + luceneSrcDir + "/MetaDataIndex" + " "
                + luceneTrgDir + "/MetaDataIndex";
        if (ExecUtil.execCmd(cmd) != 0) {
            return(-1);
        }
        
        // Print timing results
        timer.stop();
        StopWatch.ElapsedTime elapsedTime = timer.getElapsedTimeWithQualifier();
        System.out.format("Lucene indexes copied in %.2f %s.\n",
                elapsedTime.time, elapsedTime.qualifier);
     
        return 0;
    }   
    
}
