
<html>
<head>
<title>EVS Downloads</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="images/caIMAGE.css" type="text/css">
<link rel="stylesheet" href="images/sidebar_css.css" type="text/css">
<link rel="stylesheet" href="images/mainsubnav_css.css" type="text/css">
<link rel="stylesheet" href="images/sitemap_css.css" type="text/css">
<script language="JavaScript">
function changeMenuStyle(obj, new_style) { 
  obj.className = new_style;
}

function showCursor(){
	document.body.style.cursor='hand'
}

function hideCursor(){
	document.body.style.cursor='default'
}

function confirmDelete(){
  if (confirm('Are you sure you want to delete?')){
    return true;
    }else{
    return false;
  }
}

function setUrl ( url, target ) {
  if ( target == '_self' ) {
    document.location.href = url;
  } else {
    open(url,target);
  }
}
</script>
                                            
<SCRIPT LANGUAGE="JavaScript">
<!--
function gotoFunction() 
{
  if(document.gotoCtl.siteList.options[document.gotoCtl.siteList.selectedIndex].value != "")
  {
    self.location = document.gotoCtl.siteList.options[document.gotoCtl.siteList.selectedIndex].value;
  }
}
// -->
</SCRIPT>


<SCRIPT LANGUAGE="JavaScript">
<!--
function spawn(url) {
  var w = window.open(url, "_blank",
      "screenX=0,screenY=0,status=yes,toolbar=yes,menubar=yes," +
      "location=yes,scrollbars=yes,resizable=1");
}
// -->
</SCRIPT>


                              

<SCRIPT LANGUAGE="JavaScript">
function spawn(url,winw,winh) {
  var w = window.open(url, "_blank",
      "screenX=0,screenY=0,status=yes,toolbar=yes,menubar=yes,location=yes,width=" + winw + ",height=" + winh +
      ",scrollbars=yes,resizable=yes");
} 

function spawnX(url,winw,winh) {
  var w = window.open(url, "_blank",
      "screenX=0,screenY=0,status=no,toolbar=yes,menubar=no,location=no,width=" + winw + ",height=" + winh + 
      ",scrollbars=yes,resizable=yes");
} 

function showWindow(imgscr){
                myWind = window.open(imgscr,"subWindow","HEIGHT=520,WIDTH=520,resizable");
                myWind.focus();
        }

function URLencode(sStr) {
    return escape(sStr).replace(/\+/g, '%2C').replace(/\"/g,'%22').replace(/\'/g, '%27');
  }


</script>

</head>

<body bgcolor="#6E81A6" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
<tr> 
<td height="1%"> 
<!-- start cancer.gov -->
<table width="100%" border="0" cellspacing="0" cellpadding="0" bgcolor="#A90101">
  <tr bgcolor="#A90101">
    <td width="1"><a href="#skipnav"><img alt="skip banner navigation" border="0" src="images/spacer.gif" height="1" width="1"></a></td>
    <td width="1"><a href="#skip_header"><img alt="skip banner" border="0" src="images/spacer.gif" height="1" width="1"></a></td>
    <td width="211" height="24" align="left"><a href="http://www.cancer.gov" target="_blank"><img src="images/brandtype.gif" width="211" height="24" border="0"></a></td>
    <td>&nbsp;</td>
    <td width="307" height="24" align="right"><a href="http://www.cancer.gov" target="_blank"><img src="images/tagline_nologo.gif" width="307" height="24" border="0"></a></td>
  </tr>
</table>
<!-- end cancer.gov -->
<!-- start header -->
<%@ include file="images/header.html" %>
<!-- End of Header Copy -->
</td> 
</tr>
<tr>
<td height="98%" VALIGN="TOP">
<! -- Start of Body Copy  -->


<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td  VALIGN="TOP">
      <table cellspacing="0" border=0 cellpadding="0" bordercolor="#993300" class="tblbdsers" width="100%" height="100%">
        <!-- start sidebar -->
		  <%@ include file="images/sidebar.html" %>
		  <!-- end sidebar -->	
		  		 		  
          <td bgcolor="#FFFFFF" valign="top" colspan="2"  >  
          <table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <TD CLASS="NonSelectedHeaderHome" onmouseover="changeMenuStyle(this,'NonSelectedHeaderHomeOver'),showCursor()" onmouseout="changeMenuStyle(this,'NonSelectedHeaderHome'),hideCursor()" onclick="document.location.href='/'">
<TABLE CELLPADDING=0 CELLSPACING=0><TR><TD WIDTH="1"  ALIGN="LEFT" VALIGN="TOP"><IMG SRC="images/clearSpace.gif" WIDTH="1" HEIGHT="1" ALT="" STYLE="background-color: #CCC" /></TD><TD CLASS="TabHomeCell"><a name="skip_sidebar"></a><a href="#skip_mainnav"><img alt="skip top level navigation" border="0" src="images/spacer.gif" height="1" width="1"></a><NOBR><a href="/" class="NavHeaderLink">HOME</a></NOBR></TD></TR></TABLE></TD>
<TD CLASS="NavSeparator"><IMG SRC="images/mainMenuSeparator.gif" WIDTH="1" HEIGHT="16" ALT="" /></TD>
<TD CLASS="NonSelectedHeader" onmouseover="changeMenuStyle(this,'NonSelectedHeaderOver'),showCursor()" onmouseout="changeMenuStyle(this,'NonSelectedHeader'),hideCursor()" onclick="document.location.href='/about'"><NOBR><a href="/about" class="NavHeaderLink">ABOUT NCICB</a></NOBR></TD>
<TD CLASS="NavSeparator"><IMG SRC="images/mainMenuSeparator.gif" WIDTH="1" HEIGHT="16" ALT="" /></TD>
<TD CLASS="NonSelectedHeader" onmouseover="changeMenuStyle(this,'NonSelectedHeaderOver'),showCursor()" onmouseout="changeMenuStyle(this,'NonSelectedHeader'),hideCursor()" onclick="document.location.href='/infrastructure'"><NOBR><a href="/infrastructure" class="NavHeaderLink">INFRASTRUCTURE</a></NOBR></TD>
<TD CLASS="NavSeparator"><IMG SRC="images/mainMenuSeparator.gif" WIDTH="1" HEIGHT="16" ALT="" /></TD>

      <TD CLASS="NonSelectedHeader" onmouseover="changeMenuStyle(this,'NonSelectedHeaderOver'),showCursor()" onmouseout="changeMenuStyle(this,'NonSelectedHeader'),hideCursor()" onclick="document.location.href='/tools'"><NOBR><a href="/tools" class="NavHeaderLink">TOOLS</a></NOBR></TD>
<TD CLASS="NavSeparator"><IMG SRC="images/mainMenuSeparator.gif" WIDTH="1" HEIGHT="16" ALT="" /></TD>
      <TD CLASS="NonSelectedHeader" onmouseover="changeMenuStyle(this,'NonSelectedHeaderOver'),showCursor()" onmouseout="changeMenuStyle(this,'NonSelectedHeader'),hideCursor()" onclick="document.location.href='/projects'"><NOBR><a href="/projects" class="NavHeaderLink">PROJECTS</a></NOBR></TD>
<TD CLASS="NavSeparator"><IMG SRC="images/mainMenuSeparator.gif" WIDTH="1" HEIGHT="16" ALT="" /></TD>
      <TD CLASS="SelectedHeader" onmouseover="changeMenuStyle(this,'SelectedHeader'),showCursor()" onmouseout="changeMenuStyle(this,'SelectedHeader'),hideCursor()" onclick="document.location.href='/download'"><NOBR><a href="/download" class="NavHeaderLink">DOWNLOADS</a></NOBR></TD>
<TD CLASS="NavSeparator"><IMG SRC="images/mainMenuSeparator.gif" WIDTH="1" HEIGHT="16" ALT="" /></TD>
  
      <TD CLASS="NonSelectedHeader" onmouseover="changeMenuStyle(this,'NonSelectedHeaderOver'),showCursor()" onmouseout="changeMenuStyle(this,'NonSelectedHeader'),hideCursor()" onclick="document.location.href='/training'"><NOBR><a href="/training" class="NavHeaderLink">TRAINING</a></NOBR></TD>
<TD CLASS="NavSeparator"><IMG SRC="images/mainMenuSeparator.gif" WIDTH="1" HEIGHT="16" ALT="" /></TD>
      <TD CLASS="NonSelectedHeader" onmouseover="changeMenuStyle(this,'NonSelectedHeaderOver'),showCursor()" onmouseout="changeMenuStyle(this,'NonSelectedHeader'),hideCursor()" onclick="document.location.href='/support'"><NOBR><a href="/support" class="NavHeaderLink">SUPPORT</a></NOBR></TD>
<TD CLASS="NavSeparator"><IMG SRC="images/mainMenuSeparator.gif" WIDTH="1" HEIGHT="16" ALT="" /></TD>


<td CLASS="EmptySelectedHeader" WIDTH="100%" ALIGN="LEFT">&nbsp;</td>

</tr>
</table>



<table cellpadding="0" cellspacing="0" border="0" width="100%">
<tr><td class="PathHeader">

<a href="/download/" class="PathLink">Downloads</a> &gt;

<a href="/download/evslicenseagreement.jsp" class="PathLink">EVS License Agreement</a> &gt;
<font color="#444444">EVS Downloads</font>

</td></tr></table>

<a name="skipnav"></a>

<table border=0 cellpadding=0 cellspacing=0 width="620" height="100%">
<tr><td style="padding-left: 10px; padding-top: 10px; padding-right: 10px;" valign="top">
      <table cellspacing="0" cellpadding="0" width="100%" height="100%" border=0>
<div>
                    <TR>
            <TD VALIGN="TOP" width="600" class="bodytext">
        <DIV CLASS="PageHeader">EVS Downloads</DIV>
<BR>
<TABLE CELLSPACING=0 CELLPADDING=0 WIDTH="600" CLASS="BodyTable">
<TR>
<TD><IMG SRC="images/clear.gif" WIDTH="12" HEIGHT="1" ALT="" /></TD>
<TD CLASS="bodytext" VALIGN="TOP">

<P>
        <DIV CLASS="SubPageHeader">EVS (Enterprise Vocabulary Services)</b></DIV>

		<p><b>  Editing of NCI Thesaurus 10.04f was completed on April 26, 2010. Version 10.04f was April’s sixth build in our development cycle.      </b>
                                      <p><b>  The NCI Metathesaurus 201002D includes the NCI Thesaurus version 10.02d. This version of the NCI Metathesaurus based on the UMLS build 2009AA.
									</b>
                                      <hr>
                                      <p>The <b>Metathesaurus_201002D.RRF.zip</b> contains the distribution (over 20 files) of the NCI
                                        Metathesaurus in the NLM's Rich Release Format (RRF).  The NCI Metathesaurus is
                                        modeled after the same RRF structure used for the UMLS Metathesaurus.  Full
                                        documentation for the RRF can be obtained from the <a href="http://www.ncbi.nlm.nih.gov/bookshelf/br.fcgi?book=nlmumls&part=ch02">UMLSKS Knowledge Source Server page</a>.
                                        While the table structures are the same, there are differences in the data that is contained
                                        in the NCI Metathesaurus vs the UMLS Metathesaurus.  The <b>NCI_RRF_Addendum.pdf</b> file documents
                                        any term types, source precedence order, sources, and the relationship attributes
                                        in the NCI Metathesaurus that may be different from the UMLS Metathesaurus.
                                      <p>A version of <b>NLM's MetamorphoSys</b> has been configured to work with the NCI Metathesaurus.  This version takes into account the NCI's different list of sources and precedence order.  Users are encouraged to also download the release specific update, which will renew various configuration files within their install to work with the most current version of the NCI Metathesaurus.




										<p><table cellspacing="5" cellpadding="5" border="0" class="tabletext">
                                          <tbody>
                                            <tr>
                                              <td>The NCI Metathesaurus version 201002D in RRF format</td>
                                                <td>
                                                <a href="evsmetalicensecheck.jsp"><u>Metathesaurus_201002D.RRF.zip</u></a>
                                                </td>
                                              <td></td>
                                            </tr>
                                            <tr>
                                              <td>Addendum to the NLM's RRF documentation, NCI-specific changes</td>
                                                <td><a href="ftp://ftp1.nci.nih.gov/pub/cacore/EVS/NCI_Metathesaurus/NCI_RRF_Addendum.pdf">
                                                <u>NCI_RRF_Addendum.pdf</u></a>
                                                </td>
                                              <td></td>
                                            </tr>
                                            <tr>
                                              <td>NCI-specific MetamorphoSys install file</td>
                                                <td><a href="ftp://ftp1.nci.nih.gov/pub/cacore/EVS/NCI_Metathesaurus/mmsys.zip">
                                                <u>mmsys.zip </u></a>
                                                </td>
                                              <td></td>
                                            </tr>
                                            <tr>
                                              <td>NCI-specific MetamorphoSys update file</td>
                                                <td><a href="ftp://ftp1.nci.nih.gov/pub/cacore/EVS/NCI_Metathesaurus/mmsys.201002.zip">
                                                <u>mmsys.201002.zip</u></a>
                                                </td>
                                              <td></td>
                                            </tr>
                                            <tr>
                                              <td>MetamorphoSys installation Readme</td>
                                                <td><a href="ftp://ftp1.nci.nih.gov/pub/cacore/EVS/NCI_Metathesaurus/mmsys_Readme.txt">
                                                <u>mmsys_Readme.txt</u></a>
                                                </td>
                                              <td></td>
                                            </tr>
                                          </tbody>
                                        </table>

                                      <hr>
                                      <p>In the first two formats below, the ontology is in a defined state, i.e.
                                      relations are as stated by the editors, no inferred relations are
                                      specified.  Please review the description of each of these formats and then proceed to the <a href="evslicenseagreement.jsp"><u>NCI Thesaurus download page and archives</u></a> to choose the one you prefer.



                                    <p>The <b>Thesaurus_10.04f.txt flat file</b> is in tab-delimited format.  Included in this format are all
                                    the terms associated with NCI Thesaurus concepts (names and synonyms), a
                                    text definition of the concept (if one is present), and stated parent-child
                                    relations, sufficient to reconstruct the hierarchy.  The fields are:

                                    <center><p><tab><code>code &lt;tab&gt; name &lt;tab&gt; parents &lt;tab&gt; synonyms &lt;tab&gt; definition</code></center>

                                    <p>The "parents" field contains the concept name(s) of the superconcept(s).
                                    If a "parents" or "synonyms" field contains multiple entries, these
                                    are pipe-delimited.  For root concepts without "parents", this field
                                    contains the string "root_node".  The first entry in the "synonyms" field
                                    is the preferred name of the concept.  If no preferred name has been stated
                                    for the concept, this field contains the concept name.  The
                                    "definition" field contains only one definition if more than one
                                    definition is associated with the concept; not all concepts contain
                                    definitions.


                                    <p>The <b>Thesaurus.owl</b> file contains the entire terminology expressed
                                    in the Web Ontology Language (OWL) (
                                    <a href="http://www.w3.org/TR/owl-ref/" target="_blank">http://www.w3.org/TR/owl-ref/</a>
                                    ), with the exception of
                                    the Ontylog namespace declaration, which was deemed unnecessary.  The Ontylog
                                    Roles where converted to restrictions on OWL properties, and most of the
                                    concept annotations in Ontylog properties were converted to OWL
                                    AnnotationProperty; as in the Ontylog xml file, properties of use only to
                                    the EVS (e.g. editor notes) are absent in the OWL file.  Because
                                    Roles in Ontylog are mapped from a domain kind to a range kind, the OWL
                                    version of the Thesaurus has each kind as a root class to facilitate the
                                    conversion of Roles to OWL properties.</p>

                                    <p>The <b>ThesaurusInf_10.04f.OWL.zip</b> file contains the terminology from the NCI Thesaurus but excludes retired concepts and includes inferred relationships. This file is created for import into the UMLS and NCI Metathesaurus. Properties of use only to the EVS (e.g. editor notes) are absent in the released terminology. </p>

                                    <p>The <b>Thesaurus_10.04f.LexGrid.zip</b> file contains the entire terminology expressed in LexGrid XML (<a href="https://cabig-kc.nci.nih.gov/Vocab/KC/index.php/LexGrid_Model_and_Schema" target="_blank">https://cabig-kc.nci.nih.gov/Vocab/KC/index.php/LexGrid_Model_and_Schema</a>).  For more information about LexGrid and the LexGrid XML format, please visit the caBIG Vocabulary Knowledge Center (<a href="https://cabig-kc.nci.nih.gov/Vocab/KC/index.php/LexGrid" target="_blank">https://cabig-kc.nci.nih.gov/Vocab/KC/index.php/LexGrid</a>).


                                    <table cellspacing="5" cellpadding="5" border="0" class="tabletext">
                                      <tbody>
                                        <tr>
                                          <td>The NCI Thesaurus version 10.04f</td>
                                          <td>
                                            <a href="evslicenseagreement.jsp"><u>Go to the NCI Thesaurus download page</u></a>
                                          </td>
                                          <td></td>
                                        </tr>
                                      </tbody>
                                    </table>

                                    <hr>

                                    <p>Here is a series of documents
                                    explaining the semantics and other Ontylog-related entities of the Thesaurus:
                                    <p>
                                    <table cellspacing="5" cellpadding="5" border="0" class="tabletext">
                                      <tbody>
                                        <tr>
                                          <td>Short description of the Thesaurus in its Ontylog representation</td>
                                            <td><a href="ftp://ftp1.nci.nih.gov/pub/cacore/EVS/ThesaurusSemantics/NCI Thesaurus Semantics.pdf">
                                            <u>NCI Thesaurus Semantics.pdf</u></a>
                                            </td>
                                          <td></td>
                                        </tr>
                                        <tr>
                                          <td>Associations defined in the Thesaurus</td>
                                            <td><a href="ftp://ftp1.nci.nih.gov/pub/cacore/EVS/ThesaurusSemantics/Associations.pdf">
                                            <u>Associations.pdf</u></a>
                                            </td>
                                          <td></td>
                                        </tr>
                                        <tr>
                                          <td>Definitions of the various Kinds (domains) in the Thesaurus</td>
                                            <td><a href="ftp://ftp1.nci.nih.gov/pub/cacore/EVS/ThesaurusSemantics/KindDefinitions.pdf">
                                            <u>KindDefinitions.pdf</u></a>
                                            </td>
                                          <td></td>
                                        </tr>
                                        <tr>
                                          <td>The Ontylog roles in Thesaurus, with domains and ranges</td>
                                            <td><a href="ftp://ftp1.nci.nih.gov/pub/cacore/EVS/ThesaurusSemantics/Roles.pdf">
                                            <u>Roles.pdf</u></a>
                                            </td>
                                          <td></td>
                                        </tr>
                                        <tr>
                                          <td>The text properties defined in the Thesaurus</td>
                                          <td><a href="ftp://ftp1.nci.nih.gov/pub/cacore/EVS/ThesaurusSemantics/Properties.pdf">
                                          <u>Properties.pdf</u></a></td>
                                          <td></td>
                                        </tr>
                                        <tr>
                                          <td>Various use cases related to roles</td>
                                            <td><a href="ftp://ftp1.nci.nih.gov/pub/cacore/EVS/ThesaurusSemantics/RoleToUCmapping.pdf">
                                            <u>RoleToUCmapping.pdf</u></a>
                                            </td>
                                          <td></td>
                                        </tr>
                                        <tr>
                                          <td>Visio diagram of the Thesaurus description logic T-Box</td>
                                            <td><a href="ftp://ftp1.nci.nih.gov/pub/cacore/EVS/ThesaurusSemantics/TBox.vsd">
                                            <u>TBox.vsd</u></a>
                                            </td>
                                          <td></td>
                                        </tr>
                                        <tr>
                                          <td>PNG file of the above diagram</td>
                                          <td><a href="ftp://ftp1.nci.nih.gov/pub/cacore/EVS/ThesaurusSemantics/Tbox.png">
                                          <u>Tbox.png</u></a></td>
                                          <td></td>
                                        </tr>
                                      </tbody>
                                    </table>

									<hr>

                                    <p>Web browsing of the NCI Thesaurus is provided through the <a href="http://ncit.nci.nih.gov"><u>NCI Thesaurus Browser</u></a>.
                                    <p>Web browsing of the NCI Thesaurus and other public terminologies is provided through the <a href="http://nciterms.nci.nih.gov"><u>NCI Term Browser</u></a>.

                                    <p><a href="http://ncim.nci.nih.gov"><u>Browsing of the public version of the NCI Metathesaurus</u></a> is provided on a separate Web site.
                                    <p>For additional information, please see the <a href="https://wiki.nci.nih.gov/display/EVS/LexEVS+5.1+Release+Notes">Release Notes</a> of LexEVS 5.1.
                                  </td></tr>
                                </tbody>
                              </table>















		</TD>
</TR>
</TABLE>





<BR>




        <BR>

 
      </TD>
            </div> 
</table>

</td></tr>
</table>
          </td>
        </tr>
      </table> 



</td>
</tr>
</table>
<! -- End of Body Copy -->
</td> 
</tr>
<tr>
<td height="1%">
<! -- Start of Footer Copy -->



<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td CLASS="NavMenuTitleCell2" WIDTH="184"><div align="center"><a href="#skip_footer"><img alt="skip footer navigation" border="0" src="images/spacer.gif" height="1" width="184"></a></td>
   <TD CLASS="NonSelectedHeader" onmouseover="changeMenuStyle(this,'NonSelectedHeaderOver'),showCursor()" onmouseout="changeMenuStyle(this,'NonSelectedHeader'),hideCursor()" onclick="setUrl('/about/contact_us', '_top');"><NOBR>CONTACT US</NOBR></TD>
   <TD CLASS="NavSeparator"><IMG SRC="images/mainMenuSeparator.gif" WIDTH="1" HEIGHT="16" ALT="" /></TD>
   <TD CLASS="NonSelectedHeader" onmouseover="changeMenuStyle(this,'NonSelectedHeaderOver'),showCursor()" onmouseout="changeMenuStyle(this,'NonSelectedHeader'),hideCursor()" onclick="setUrl('http://www.nih.gov/about/privacy.htm', '_blank');"><NOBR>PRIVACY NOTICE</NOBR></TD>
   <TD CLASS="NavSeparator"><IMG SRC="images/mainMenuSeparator.gif" WIDTH="1" HEIGHT="16" ALT="" /></TD>
   <TD CLASS="NonSelectedHeader" onmouseover="changeMenuStyle(this,'NonSelectedHeaderOver'),showCursor()" onmouseout="changeMenuStyle(this,'NonSelectedHeader'),hideCursor()" onclick="setUrl('http://www.nih.gov/about/disclaim.htm', '_blank');"><NOBR>DISCLAIMER</NOBR></TD>
   <TD CLASS="NavSeparator"><IMG SRC="images/mainMenuSeparator.gif" WIDTH="1" HEIGHT="16" ALT="" /></TD>
   <TD CLASS="NonSelectedHeader" onmouseover="changeMenuStyle(this,'NonSelectedHeaderOver'),showCursor()" onmouseout="changeMenuStyle(this,'NonSelectedHeader'),hideCursor()" onclick="setUrl('http://www3.cancer.gov/accessibility/nci508.htm', '_blank');"><NOBR>ACCESSIBILITY</NOBR></TD>
   <TD CLASS="NavSeparator"><IMG SRC="images/mainMenuSeparator.gif" WIDTH="1" HEIGHT="16" ALT="" /></TD>
   <TD CLASS="NonSelectedHeader" onmouseover="changeMenuStyle(this,'NonSelectedHeaderOver'),showCursor()" onmouseout="changeMenuStyle(this,'NonSelectedHeader'),hideCursor()" onclick="setUrl('/support', '_top');"><NOBR>APPLICATION SUPPORT</NOBR></TD>
   <TD CLASS="NavSeparator"><IMG SRC="images/mainMenuSeparator.gif" WIDTH="1" HEIGHT="16" ALT="" /></TD>
   <td CLASS="EmptySelectedHeader2" WIDTH="100%">&nbsp;</td>
  </tr>
</table>

<!-- Start Footer Table -->
<table width="100%" border="0" cellspacing="0" cellpadding="6">
		<tr>
		<td valign="top" bgColor=#FFFFFF><div align="center" >
			<a href="http://www.cancer.gov/" target="_blank"><img src="images/footer_nci.gif" width="63" height="31" alt="National Cancer Institute" border="0"></a>
			<a href="http://www.dhhs.gov/" target="_blank"><img src="images/footer_hhs.gif" width="39" height="31" alt="Department of Health and Human Services" border="0"></a>
			<a href="http://www.nih.gov/" target="_blank"><img src="images/footer_nih.gif" width="46" height="31" alt="National Institutes of Health" border="0"></a>
			<a href="http://www.firstgov.gov/" target="_blank"><img src="images/footer_firstgov.gif" width="91" height="31" alt="FirstGov.gov" border="0"></a></div>
		</td>
	</tr>
</table>
<!-- End Footer table -->

<p></p></body></html>