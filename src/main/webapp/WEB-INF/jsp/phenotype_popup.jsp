<%@ page import = "org.jax.mgi.fe.datamodel.QueryFormOption" %>

<HTML>
<HEAD>
  <TITLE>Phenotypes Selection List</TITLE>
  <SCRIPT LANGUAGE="JavaScript">
    function populateParent()
    {
        if (window.opener && !window.opener.closed)
        {
            var selObj = window.document.PhenoPopup.valueList;
            var s = "";
            var joiner = " AND ";
            var i;

            // compute the string to be added to the Phenotypes box

            for (i = 0; i < selObj.options.length; i++)
            {
                if (selObj.options[i].selected)
                {
                    if (s == "")
                    {
                        s = selObj.options[i].value;
                    }
                    else
                    {
                        s = s + joiner + selObj.options[i].value;
                    }
                }
            }

            // add the new query string to the Phenotypes box, if user
            // picked anything

            if (s != "")
            {
                if (window.opener.document.queryForm.phenotypes.value != "")
                {
                    window.opener.document.queryForm.phenotypes.value =
                        window.opener.document.queryForm.phenotypes.value +
                        joiner + s;
                }
                else
                {
                    window.opener.document.queryForm.phenotypes.value = s;
                }
            }
        }
        window.close();
    }
  </SCRIPT>
  <LINK REL="stylesheet" HREF="${configBean.WEBSHARE_URL}css/mgi.css" TYPE="text/css"/>
</HEAD>

<BODY BGCOLOR="#ffffff">
  <FORM NAME="PhenoPopup">
    <TABLE BORDER="0" CELLPADDING="2" CELLSPACING="0" WIDTH="100%">
      <TR>
        <TD>Select <STRONG>Anatomical Systems Affected by Phenotypes</STRONG>
          to add to the query.&nbsp;<BR>
          <SPAN CLASS="small">(Multiple selections allowed.)</SPAN>
        </TD>
	<TD ALIGN="right" VALIGN="top"><a href="javascript:childWindow=window.open('${configBean.USERHELP_URL}ALLELE_phenotype_highlevel_help.shtml', 'helpWindow', 'width=1000,height=600,resizable=yes,scrollbars=yes,alwaysRaised=yes'); childWindow.focus();"><IMG SRC="${configBean.WEBSHARE_URL}images/help_small.jpg"/></A>
        </TD>
      </TR>
    </TABLE>
    <BR>

    <TABLE BORDER="0" CELLPADDING="2" CELLSPACING="0" WIDTH="100%">
      <TR>
        <TD>
          <SELECT NAME="valueList" MULTIPLE SIZE=20>
	    ${mpHeaders}
          </SELECT>
        </TD>
        <TD>
          <INPUT TYPE="button" VALUE="Add To Query"
            onClick="populateParent()">
          <P>&nbsp;<P>
          <INPUT TYPE="button" VALUE="Cancel" onClick="window.close()">
        </TD>
      </TR>
    </TABLE>
    <P>
  </FORM>
<HR>
</BODY>
</HTML>

