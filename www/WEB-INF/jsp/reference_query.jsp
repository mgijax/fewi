<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<style type="text/css">

</style>


${templateBean.templateHeadHtml}

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>Reference Query</title>

${templateBean.templateBodyStartHtml}

<!-- begin header bar -->
<div id="titleBarWrapper" style="max-width:1200px" userdoc="marker_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">References Query</span>
</div>
<!-- end header bar -->

<form:form method="GET" commandName="referenceQueryForm" action="/fewi/mgi/reference/summary">

<!-- query form table -->
<TABLE WIDTH="100%" class="border">
	<TR>
		<TD COLSPAN="2" align="left">
			<INPUT CLASS="buttonLabel" VALUE="Search" TYPE="submit">
			&nbsp;&nbsp;
			<INPUT TYPE="reset"> 
		</td>
	</tr>

	<!-- gene symbol/name section -->
	<TR CLASS="stripe1">
		<TD CLASS="cat1">Author</TD>
		<TD>
			<div style="position:relative;">
				<div style="float:left; width:300px;text-align:left;">
					<div id="authorAutoComplete">
						<form:input id="author" path="author" maxlength="256"/>
						<div id="authorContainer"></div>
					</div>
				</div>
				<div style="float:left; text-align:left;">
					<form:radiobutton path="authorScope" value="any"/> Any Author(s)<br/>
		    		<form:radiobutton path="authorScope" value="first"/> First Author<br/>
		    		<form:radiobutton path="authorScope" value="last"/> Last Author
				</div>
    		</div>		
		</TD>
	</TR>

    <!-- map position section -->
	<TR CLASS="stripe2">
		<TD CLASS="cat2">Journal</TD>
		<TD>
			<div style="position:relative;">
				<div style="float:left; width:300px;text-align:left;z-index=1000;">
					<div id="authorAutoComplete">
						<form:input id="journal" path="journal" maxlength="256" />
						<div id="journalContainer"></div>
					</div>
				</div>
				<div style="float:left; text-align:left;">
					Example text here.
				</div>
    		</div>
		</TD>
	</TR>
	<tr  CLASS="stripe1">
		<td CLASS="cat1">Year</td>
		<td>
			<div style="position:relative;">
				<div style="float:left; width:300px;text-align:left;">
					<form:input path="year" cols="40" class="formWidth"/>
				</div>
				<div style="float:left; text-align:left;">
					Example text here.
				</div>
    		</div>			
		</td>
	</tr>
	<tr  CLASS="stripe2">
		<td CLASS="cat2">Text</td>
		<td>	
			<div style="position:relative;">
				<div style="float:left; width:300px;text-align:left;">
					<form:textarea path="text"  rows="3" cols="40" class="formWidth"/><br/>
					<form:checkbox path="inTitle" value="inTitle"/> In Title 
					<form:checkbox path="inAbstract" value="inAbstract"/> In Abstract
				</div>
				<div style="float:left; text-align:left;">
					Example text here.
				</div>
    		</div>		
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<div style="width:800px; text-align: center; font-weight: bold;">OR</div>
		</td>
	</tr>
	<tr  CLASS="stripe1">
		<td  CLASS="cat1">
			PubMed ID or<br/>
			MGI Reference ID
		</td>
		<td  CLASS="data1">
			<div style="position:relative;">
				<div style="float:left; width:300px;text-align:left;">
					<form:input path="id" maxlength="256" class="formWidth"/>
				</div>
				<div style="float:left; text-align:left;" class="example">
					Example text here.
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
YAHOO.example.AuthorAutocomplete = function() {
    // Use an XHRDataSource
    var oDS = new YAHOO.util.XHRDataSource("reference/autocomplete/author");
    // Set the responseType
    oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    // Define the schema of the JSON results
    oDS.responseSchema = {resultsList : "resultObjects"};

    // Instantiate the AutoComplete
    var oAC = new YAHOO.widget.AutoComplete("author", "authorContainer", oDS);
    // Throttle requests sent
    oAC.queryDelay = .3;
    oAC.minQueryLength = 2;
    oAC.maxResultsDisplayed = 1000;
    oAC.useIFrame = true;
    oAC.forceSelection = true;
    oAC.allowBrowserAutocomplete = false;
    oAC.delimChar = ";";

    return {
        oDS: oDS,
        oAC: oAC
    };
}();
</script>

<script type="text/javascript">
YAHOO.example.JournalAutocomplete = function() {
    // Use an XHRDataSource
    var oDS = new YAHOO.util.XHRDataSource("reference/autocomplete/journal");
    // Set the responseType
    oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    // Define the schema of the JSON results
    oDS.responseSchema = {resultsList : "resultObjects"};

    // Instantiate the AutoComplete
    var oAC = new YAHOO.widget.AutoComplete("journal", "journalContainer", oDS);
    // Throttle requests sent
    oAC.queryDelay = .3;
    oAC.minQueryLength = 2;
    oAC.maxResultsDisplayed = 1000;
    oAC.useIFrame = false;
    oAC.forceSelection = true;
    oAC.allowBrowserAutocomplete = false;
    oAC.delimChar = ";";

    return {
        oDS: oDS,
        oAC: oAC
    };
}();
</script>



${templateBean.templateBodyStopHtml}