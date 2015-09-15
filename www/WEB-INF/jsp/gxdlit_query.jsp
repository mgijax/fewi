<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>Gene Expression Literature Search</title>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- begin header bar -->
<div id="titleBarWrapperGxd" userdoc="EXPRESSION_literature_help.shtml">	
	<a href="${configBean.HOMEPAGES_URL}expression.shtml"><img class="gxdLogo" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="75"></a>
	<span class="titleBarMainTitleGxd" style='display:inline-block; margin-top: 20px;'>Gene Expression Literature Search</span>
</div>
<!-- end header bar -->

<form:form method="GET" commandName="gxdLitQueryForm" action="${configBean.FEWI_URL}gxdlit/summary">

<!-- query form table -->
<TABLE WIDTH="100%" class="pad5 borderedTable">
	<TR>
		<TD COLSPAN="2" align="left">
			<INPUT CLASS="buttonLabel" VALUE="Search" TYPE="submit">
			&nbsp;&nbsp;
			<INPUT TYPE="reset">
		</td>
	</tr>

	<TR CLASS="stripe1">
		<TD CLASS="cat1Gxd">Gene Symbol/Name</TD>
		<TD>
			<div>
				<div id="nomenDic" style="position:relative; z-index:100; float:left; width:300px;text-align:left;">
					<form:input id="nomen" path="nomen" class="formWidth"></form:input>
				</div>
				<div style="height:4em;" class="example">
					<div style="float:left;text-align:left;line-height:4em;vertical-align:middle;width:7em;" class="example">
						Examples:
					</div>
					<div style="text-align:left;" class="example">
						one gene&nbsp;&nbsp;<em class="paleText small">e.g., Shh</em><br/>
						a set of genes with similar nomenclature &nbsp;&nbsp;<em class="paleText small">e.g., Hoxa*</em><br/>
					</div>						
				</div>
    	    </div>
		</TD>
	</TR>
	
	<TR CLASS="stripe2">
		<TD CLASS="cat2Gxd">Assay type</TD>
		<TD>
			<div>
				<div style="float:left; width:300px;text-align:left;">
                	<form:select multiple="true" path="assayType" size="5" items="${gxdLitQueryForm.assayTypes}">
                       	<form:options items="${assayTypes}" />
                    </form:select>
				</div>
    			</div>
		</TD>
	</TR>
	
	<TR CLASS="stripe1">
		<TD CLASS="cat1Gxd">Age</TD>
		<TD>
			<div>
				<div style="float:left; width:60px;text-align:left;">
                	<form:select multiple="true" path="age" size="5" items="${gxdLitQueryForm.ages}">
                       	<form:options items="${ages}" />
                    </form:select> 
				</div> (days post conception)
    		</div>
		</TD>
	</TR>	

	<TR CLASS="stripe2">
		<TD CLASS="cat2Gxd">Author</TD>
		<TD>
			<div>
				<div style="float:left; width:300px;text-align:left;">
					<div id="authorAutoComplete" style="position:relative; z-index:100;">
						<form:input id="author" path="author"></form:input>
						<div id="authorContainer"></div>
					</div>
				</div>
				<div style="float:left; text-align:left;">
					<form:radiobutton id="authorScope1" path="authorScope" value="any" checked="checked"/> Any Author(s)<br/>
		    		<form:radiobutton id="authorScope2" path="authorScope" value="first"/> First Author<br/>
		    		<form:radiobutton id="authorScope3" path="authorScope" value="last"/> Last Author
				</div>
    		</div>
		</TD>
	</TR>

	<TR CLASS="stripe1">
		<TD CLASS="cat1Gxd">Journal</TD>
		<TD>
			<div style="float:left;width:300px;text-align:left;">
				<div>
					<div id="journalAutoComplete">
						<form:input id="journal" path="journal"></form:input>
						<div id="journalContainer"></div>
					</div>
				</div>
    		</div>
   			<div style="height:2em;" class="example">
				<div style="float:left;text-align:left;vertical-align:middle;width:7em;" class="example">
					Examples:<br/>
					(See <a href="ftp://ftp.ncbi.nih.gov/pubmed/J_Medline.txt">NLM</a>.)
				</div>
				<div style="text-align:left;" class="example">
					Proc Natl Acad Sci USA<br/>
					J Cell Mol Med 
				</div>
			</div>
		</TD>
	</TR>
	<tr  CLASS="stripe2">
		<td CLASS="cat2Gxd">Year</td>
		<td>
			<div style="height:5.5em;">
				<div style="float:left;width:300px;text-align:left;">
					<form:input id="year" path="year" class="formWidth"></form:input>
				</div>
				<div style="height:5.5em;" class="example">
					<div style="height:5.5em;float:left;text-align:left;line-height:4em;vertical-align:middle;width:7em;" class="example">
						Examples:
					</div>
					<div style="text-align:left;" class="example">
						2008<br/>
						1990-2004<br/>
						-2007 (from the earliest reference through 2007)<br/>
						2009- (from 2009 through the present)
					</div>
				</div>
    		</div>
		</td>
	</tr>
	<tr  CLASS="stripe1">
		<td CLASS="cat1Gxd">Text</td>
		<td>
			<div style="height:5em;">
				<div style="float:left;width:300px;text-align:left;">
					<form:textarea id="text" path="text" class="formWidth"></form:textarea><br/>
					<form:checkbox id="inTitle1" path="inTitle" /> In Title
					<form:checkbox id="inAbstract1" path="inAbstract" /> In Abstract
				</div>
				<div style="height:4em;" class="example">
					<div style="float:left;text-align:left;line-height:4em;vertical-align:middle;width:7em;" class="example">
						Examples:
					</div>
					<div style="text-align:left;" class="example">
						gastrulation, morphogenesis<br>
						"pattern formation"<br>
					</div>
				</div>
    		</div>
		</td>
	</tr>
    <TR>
		<TD COLSPAN="3" align="left">
			<INPUT CLASS="buttonLabel" VALUE="Search" TYPE="submit">
			&nbsp;&nbsp;
			<INPUT TYPE="reset">
		</TD>
    </TR>
</TABLE>
</form:form>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var qDisplay = false;
</script>


<script type="text/javascript">
(function() {
    // Use an XHRDataSource
    var oDS = new YAHOO.util.XHRDataSource(fewiurl + "autocomplete/author/gxd");
    // Set the responseType
    oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    // Define the schema of the JSON results
    oDS.responseSchema = {resultsList: "summaryRows", fields:["author", "isGenerated"]};
    //oDS.maxCacheEntries = 10;
    oDS.connXhrMode = "cancelStaleRequests";

    // Instantiate the AutoComplete
    var oAC = new YAHOO.widget.AutoComplete("author", "authorContainer", oDS);
    // Throttle requests sent
    oAC.queryDelay = .3;
    oAC.minQueryLength = 2;
    oAC.maxResultsDisplayed = 5000;
    oAC.forceSelection = true;
    oAC.delimChar = ";";
    
    oAC.formatResult = function(oResultData, sQuery, sResultMatch) {
    	   var sKey = sResultMatch;
    	 
    	   // some other piece of data defined by schema
    	   var isGenerated = oResultData[1];
    	   if (isGenerated){
    		   sKey = sKey + " <span class='autocompleteHighlight'>(all)</span>";
    	   }

    	  return (sKey);
    	}; 
    	
    var toggleVis = function(){
        if (YAHOO.util.Dom.getStyle('authHelp', 'display') == 'none'){
            YAHOO.util.Dom.setStyle('authHelp', 'display', 'block');
        }
    };
    
    oAC.itemSelectEvent.subscribe(toggleVis); 

    return {
        oDS: oDS,
        oAC: oAC
    };
})();

</script>

<script type="text/javascript">
var journalAutocomplete = function() {
    // Use an XHRDataSource
    var oDS = new YAHOO.util.XHRDataSource("${configBean.FEWI_URL}autocomplete/journal/gxd");
    // Set the responseType
    oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    // Define the schema of the JSON results
    oDS.responseSchema = {resultsList : "resultStrings"};
    oDS.maxCacheEntries = 10;
    oDS.connXhrMode = "cancelStaleRequests";

    // Instantiate the AutoComplete
    var oAC = new YAHOO.widget.AutoComplete("journal", "journalContainer", oDS);
    // Throttle requests sent
    oAC.queryDelay = .3;
    oAC.minQueryLength = 2;
    oAC.maxResultsDisplayed = 5000;
    oAC.forceSelection = false;
    oAC.delimChar = ";";

    return {
        oDS: oDS,
        oAC: oAC
    };
}();

</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
