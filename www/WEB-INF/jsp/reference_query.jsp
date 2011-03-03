<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

${templateBean.templateHeadHtml}

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>References</title>

${templateBean.templateBodyStartHtml}

<!-- begin header bar -->
<div id="titleBarWrapper" userdoc="marker_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">References</span>
</div>
<!-- end header bar -->

<%@ include file="/WEB-INF/jsp/reference_form.jsp" %>

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

${templateBean.templateBodyStopHtml}