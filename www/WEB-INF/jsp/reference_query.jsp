<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

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

<form:form method="GET" commandName="referenceQueryForm" action="${configBean.FEWI_URL}reference/summary">

<!-- query form table -->
<TABLE WIDTH="100%" class="pad5 borderedTable">
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
						<input id="author" name="author" type="text" value="" maxlength="256"/>
						<div id="authorContainer"></div>
					</div>
				</div>
				<div style="float:left; text-align:left;">
					<input id="authorScope1" name="authorScope" type="radio" value="any" checked="checked"/> Any Author(s)<br/>
		    		<input id="authorScope2" name="authorScope" type="radio" value="first"/> First Author<br/>
		    		<input id="authorScope3" name="authorScope" type="radio" value="last"/> Last Author
				</div>
    		</div>
		</TD>
	</TR>

    <!-- map position section -->
	<TR CLASS="stripe2">
		<TD CLASS="cat2">Journal</TD>
		<TD>
			<div style="position:relative;">
				<div style="float:left; width:300px;text-align:left;">
					<div id="authorAutoComplete">
						<input id="journal" name="journal" type="text" value="" maxlength="256"/>
						<div id="journalContainer"></div>
					</div>
				</div>
    		</div>
		</TD>
	</TR>
	<tr  CLASS="stripe1">
		<td CLASS="cat1">Year</td>
		<td>
			<div style="position:relative;height:4em;">
				<div style="float:left;width:300px;text-align:left;">
					<input id="year" name="year" cols="40" class="formWidth" type="text" value=""/>
				</div>
				<div style="height:4em;" class="example">
					<div style="float:left;text-align:left;line-height:4em;vertical-align:middle;width:7em;" class="example">
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
	<tr  CLASS="stripe2">
		<td CLASS="cat2">Text</td>
		<td>
			<div style="position:relative;height:4em;">
				<div style="float:left;width:300px;text-align:left;">
					<textarea id="text" name="text" class="formWidth"></textarea><br/>
					<input id="inTitle1" name="inTitle" type="checkbox" value="true" checked="checked"/><input type="hidden" name="_inTitle" value="on"/> In Title
					<input id="inAbstract1" name="inAbstract" type="checkbox" value="true" checked="checked"/><input type="hidden" name="_inAbstract" value="on"/> In Abstract
				</div>
				<div style="height:4em;" class="example">
					<div style="float:left;text-align:left;line-height:4em;vertical-align:middle;width:7em;" class="example">
						Examples:
					</div>
					<div style="text-align:left;" class="example">
						oocyte, spermatocyte<br/>
						"telomeres in meiocytes"<br/>
						5-bromo-2'-deoxyuridine-positive cells<br/>
					spastic paraplegia spinocerabellar ataxia cerebellum
					</div>
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
			<div style="position:relative;height:3em;">
				<div style="float:left; width:300px;text-align:left;">
					<input id="id" name="id" class="formWidth" type="text" value="" maxlength="256"/>
				</div>
				<div style="height:4em;" class="example">
					<div style="float:left;text-align:left;line-height:4em;vertical-align:middle;width:7em;" class="example">
						Examples:
					</div>
					<div style="text-align:left;" class="example">
					20339075 (PubMed)<br/>
					J:159210 (MGI reference ID)<br/>
					18989690, 18192873, 18467500
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
YAHOO.example.AuthorAutocomplete = function() {
    // Use an XHRDataSource
    var oDS = new YAHOO.util.XHRDataSource("${configBean.FEWI_URL}reference/autocomplete/author");
    // Set the responseType
    oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    // Define the schema of the JSON results
    oDS.responseSchema = {resultsList : "resultStrings"};

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
    var oDS = new YAHOO.util.XHRDataSource("${configBean.FEWI_URL}reference/autocomplete/journal");
    // Set the responseType
    oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    // Define the schema of the JSON results
    oDS.responseSchema = {resultsList : "resultStrings"};

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