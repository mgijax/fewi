<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

${templateBean.templateHeadHtml}

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<!--begin custom header content for this example-->
<script type="text/javascript">
	document.documentElement.className = "yui-pe";
</script>

<script src="${configBean.FEWI_URL}assets/js/rowexpansion.js"></script>

<title>References</title>

${templateBean.templateBodyStartHtml}

<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">

<!-- begin header bar -->
<div id="titleBarWrapper" userdoc="reference_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">References</span>
</div>
<!-- end header bar -->


<div id="outer">
	<div id="inner">
		
		<div class="buttons">	
			<a id="toggleQF" class="filterButton qfExpand">Modify Query</a>
		</div>
		<div class="qfTitle">Query Form</div>
	</div>
	<div id="qwrap" style="display:none;">
		<form:form method="GET" commandName="referenceQueryForm" action="${configBean.FEWI_URL}reference/summary">		
		<!-- query form table -->
		<TABLE WIDTH="100%" class="qf pad5 borderedTable">
			<TR>
				<TD COLSPAN="2" align="left">
					<INPUT CLASS="buttonLabel" VALUE="Search" TYPE="submit">
					&nbsp;&nbsp;
					<INPUT TYPE="reset">
				</td>
			</tr>
		
			<TR CLASS="stripe1">
				<TD CLASS="cat1">Author</TD>
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
				    		<form:radiobutton id="authorScope3" path="authorScope" tvalue="last"/> Last Author
						</div>
		    		</div>
				</TD>
			</TR>
		
			<TR CLASS="stripe2">
				<TD CLASS="cat2">Journal</TD>
				<TD>
					<div>
						<div>
							<div id="journalAutoComplete">
								<form:input id="journal" path="journal"></form:input>
								<div id="journalContainer"></div>
							</div>
						</div>
		    		</div>
				</TD>
			</TR>
			<tr  CLASS="stripe1">
				<td CLASS="cat1">Year</td>
				<td>
					<div style="height:4em;">
						<div style="float:left;width:300px;text-align:left;">
							<form:input id="year" path="year" class="formWidth"></form:input>
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
					<div style="height:3em;">
						<div style="float:left; width:300px;text-align:left;">
							<form:input id="id" path="id" class="formWidth" value="" maxlength="256"></form:input>
						</div>
						<div style="height:4em;" class="example">
							<div style="float:left;text-align:left;line-height:4em;vertical-align:middle;width:7em;" class="example">
								Examples:
							</div>
							<div style="text-align:left;" class="example">
							20339075 (PubMed)<br/>
							J:159210 (MGI reference ID)<br/>
							18989690; 18192873; J:159210 (List)
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
	</div>

</div>

<div id="summDiv">
	<div id="querySummary">
		<span class="title">You searched for:</span><br/>
		<span id="totalCount" class="count"></span><br/>
		<c:if test="${not empty referenceQueryForm.author}">
			<span class="label">Author:</span> 
			${referenceQueryForm.author}<br/></c:if>
		<c:if test="${not empty referenceQueryForm.journal}">
			<span class="label">Journal:</span>
			${referenceQueryForm.journal}<br/></c:if>
		<c:if test="${not empty referenceQueryForm.year}">
			<span class="label">Year:</span> 
			${referenceQueryForm.year}<br/></c:if>
		<c:if test="${not empty referenceQueryForm.text}">
			<span class="label">Text:</span> 
			${referenceQueryForm.text}<br/></c:if>
		<c:if test="${not empty referenceQueryForm.id}">
			<span class="label">ID:</span> 
			${referenceQueryForm.id}<br/></c:if>
	</div>
	<div id="filterSummary" style="display:none;" class="filters">
		<span class="label">Filters:</span>
		<span id="fsList"></span>
	</div>	
	<div class="paginator">
		<div id="paginationTop">&nbsp;</div>
	</div>
</div>	
<div>
<div id="filterDiv" class="filters">
	<a id="authorFilter" class="filterButton">Author Filter <img src="${configBean.FEWI_URL}images/filter.png" width="8" height="8" /></a> 
	<a id="journalFilter" class="filterButton">Journal Filter <img src="${configBean.FEWI_URL}images/filter.png" width="8" height="8" /></a> 
	<a id="yearFilter" class="filterButton">Year Filter <img src="${configBean.FEWI_URL}images/filter.png" width="8" height="8" /></a> 
	<a id="curatedDataFilter" class="filterButton">Data Filter <img src="${configBean.FEWI_URL}images/filter.png" width="8" height="8" /></a>
	<a id="toggleAbstract" class="filterButton">Show All Abstracts</a>
</div>
</div>


<div id="dynamicdata"></div>

<div class="facetFilter">
	<div id="facetDialog">
		<div class="hd">Filter</div>	
		<div class="bd">
			<form:form method="GET" action="${configBean.FEWI_URL}reference/summary">
			<img src="/fewi/mgi/assets/images/loading.gif">	
			</form:form>
		</div>
	</div>
</div>

<script type="text/javascript">
	<%@ include file="/js/reference_summary.js" %>
</script>

<script type="text/javascript">
var authorAutocomplete = function() {
    // Use an XHRDataSource
    var oDS = new YAHOO.util.XHRDataSource("${configBean.FEWI_URL}reference/autocomplete/author");
    // Set the responseType
    oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    // Define the schema of the JSON results
    oDS.responseSchema = {resultsList : "resultStrings"};
    oDS.maxCacheEntries = 10;
    oDS.connXhrMode = "cancelStaleRequests";

    // Instantiate the AutoComplete
    var oAC = new YAHOO.widget.AutoComplete("author", "authorContainer", oDS);
    // Throttle requests sent
    oAC.queryDelay = .3;
    oAC.minQueryLength = 2;
    oAC.maxResultsDisplayed = 5000;
    oAC.forceSelection = true;
    oAC.delimChar = ";";

    return {
        oDS: oDS,
        oAC: oAC
    };
}();
</script>

<script type="text/javascript">
var journalAutocomplete = function() {
    // Use an XHRDataSource
    var oDS = new YAHOO.util.XHRDataSource("${configBean.FEWI_URL}reference/autocomplete/journal");
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

<script type="text/javascript">
	var qDisplay = true;
	var toggleQF = function() {

        var qf = YAHOO.util.Dom.get('qwrap');
        var toggleLink = YAHOO.util.Dom.get('toggleQF');

        var attributes = { height: { to: 375 }};

        if (!qDisplay){
        	attributes = { height: { to: 0 }};
        	YAHOO.util.Dom.removeClass(toggleLink, 'qfCollapse');
        	YAHOO.util.Dom.addClass(toggleLink, 'qfExpand');
        	qDisplay = true;
        } else {
        	YAHOO.util.Dom.setStyle(qf, 'height', '0px');
        	YAHOO.util.Dom.setStyle(qf, 'display', 'none');
        	YAHOO.util.Dom.removeClass(toggleLink, 'qfExpand');
        	YAHOO.util.Dom.addClass(toggleLink, 'qfCollapse');
        	qDisplay = false;
        	changeVisibility('qwrap');
        }
		var myAnim = new YAHOO.util.Anim('qwrap', attributes);
		myAnim.duration = 0.5;
		myAnim.animate();

	};

	YAHOO.util.Event.addListener("toggleQF", "click", toggleQF);
</script>

${templateBean.templateBodyStopHtml}
