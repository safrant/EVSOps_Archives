<%@ taglib uri='/WEB-INF/taglib.tld' prefix='security'%>
<!-- fang need to change when deploy-->
<security:EnforceLoginTag loginPage='/evslogin.jsp' errorPage='/error.jsp'/>


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
<a href="/download/evsportal.jsp" class="PathLink">EVS Downloads</a> &gt;
<font color="#444444">EVS NCI Thesaurus Downloads</font>

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
        <DIV CLASS="SubPageHeader">EVS NCI Thesaurus Downloads</b></DIV>

                                     <hr>


								   <table cellspacing="5" cellpadding="5" border="0" class="tabletext">
                                      <tbody>
                                        <tr>
                                          <td>The NCI Thesaurus version 10.04f in flat file format</td>
                                          <td><a href="ftp://ftp1.nci.nih.gov/pub/cacore/EVS/NCI_Thesaurus/Thesaurus_10.04f.FLAT.zip">
                                            <u>Thesaurus_10.04f.FLAT.zip</u></a>
                                          </td>
                                          <td></td>
                                        </tr>
                                        
                                        <tr>
                                          <td>The NCI Thesaurus version 10.04f in LexGrid XML</td>
                                          <td>
                                            <a href="ftp://ftp1.nci.nih.gov/pub/cacore/EVS/NCI_Thesaurus/Thesaurus_10.04f.LexGrid.zip">
                                            <u>Thesaurus_10.04f.LexGrid.zip</u></a>
                                          </td>
                                          <td></td>
                                        </tr>

                                        <tr>
                                          <td>The NCI Thesaurus version 10.04f in Asserted OWL</td>
                                          <td>
                                            <a href="ftp://ftp1.nci.nih.gov/pub/cacore/EVS/NCI_Thesaurus/Thesaurus_10.04f.OWL.zip">
                                            <u>Thesaurus_10.04f.OWL.zip</u></a>
                                          </td>
                                          <td></td>
                                        </tr>

                                        <tr>
                                          <td>The NCI Thesaurus version 10.04f in Inferred OWL</td>
                                          <td>
                                            <a href="ftp://ftp1.nci.nih.gov/pub/cacore/EVS/NCI_Thesaurus/ThesaurusInf_10.04f.OWL.zip">
                                            <u>ThesaurusInf_10.04f.OWL.zip</u></a>
                                          </td>
                                          <td></td>
                                        </tr>


                                      </tbody>
                                    </table>

                                    <hr>

                                    <p>For previous releases of the NCI Thesaurus please browse our publication
                                    <a href="ftp://ftp1.nci.nih.gov/pub/cacore/EVS/NCI_Thesaurus/archive/">Archives</a>.

                                    <p>For additional information, please see the
                                    <a href="https://wiki.nci.nih.gov/display/EVS/LexEVS+5.1+Release+Notes">Release Notes</a> of LexEVS 5.1.
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

