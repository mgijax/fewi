<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "java.util.List" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% List<Marker> markers = (List<Marker>)request.getAttribute("markers"); %>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>Interaction Explorer</title>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>
<div style="padding: 4px;">

<!-- iframe for history managers use -->
<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<style>
td.blue { background-color: #D0E0F0; }
td.padded { padding: 2px; }
td.rightPad { padding-right: 10px; }
td.left { text-align: left; }
td.bold { font-weight: bold; }
td.box { border: 1px solid black; }
td.nowrap { white-space:nowrap; }

path.link {
   fill: none;
   /*stroke: #9CC6F0;*/
   stroke: rgb(230,159,0);
   stroke-width: 0.7px;
}

path.link.predicted {
   /*stroke: #F09CC6;*/
   stroke: rgb(204,121,167);
}

path.link.predicted_self {
   /*stroke: #F09CC6;*/
   stroke: rgb(204,121,167);
}

path.link.validated {
   /*stroke: #F09CC6;*/
   stroke: rgb(86,180,233);
}

path.link.validated_self {
   /*stroke: #F09CC6;*/
   stroke: rgb(86,180,233);
}

circle {
	cursor: pointer;
	stroke: #3182bd;
	stroke-width: 1.5px;
}

.nodestyle {
	font: 12px sans-serif;
	pointer-events: none;
}

.overlay {
	fill: none;
	pointer-events: all;
}

.warningclass {
	fill: rgb(255, 0, 0);
	font: 12px sans-serif;
}

.loadingclass {
	fill: rgb(0, 0, 0);
	font: 18px sans-serif;
}

</style>

<!-- header bar -->
<div id="titleBarWrapper" userdoc="INTERACTION_explorer_help.shtml">
	<span class="titleBarMainTitle">Interaction Explorer</span>
</div>

<div id="headerTable" style="width: 1245px;">
	<div id="mgiHelpDiv" style="margin-top: -30px; margin-right: 30px; float: right">
		<span class="filterButton" id="showMgiHelpDiv" style="text-align: right;">About MGI Interaction Data</span><br />
	</div>
	<div style="max-width: 450px; float: left;">
		<span style="font-family: Arial,Helvetica; font-weight: bold;">Interactions involving the following genome features:</span><br />
		<table>
			<tr>
				<td class="blue padded left bold box nowrap rightPad">Symbol</td>
				<td class="blue padded left bold box nowrap">Name</td>
				<td class="blue padded left bold box nowrap">Feature Type</td>
			</tr>
			<script type="text/javascript">
				var markerIDs = [];
			</script>
			<c:forEach var="marker" items="${markers}">
				<tr>
					<td class="box left padded nowrap rightPad"><a href="${configBean.FEWI_URL}marker/${marker.primaryID}" target="_blank">${marker.symbol}</a></td>
					<td class="box left padded nowrap rightPad" style="white-space: normal;">${marker.name}</td>
					<td class="box left padded nowrap rightPad">${marker.markerSubtype}</td>
					<script type="text/javascript">
						markerIDs.push("${marker.primaryID}");
					</script>
				</tr>
			</c:forEach>
		</table>
	</div>
	<div style="float: left; margin-left: 30px;">
		<div id="filterDiv" style="width: 460px">
			<span id="filterLabel" class="label">Filter interactions by:</span>
			<a id="termFilter" class="filterButton">Interaction&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
			<a id="validationFilter" class="filterButton">Validation&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
			<a id="dataSourceFilter" class="filterButton">Data Source&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
			<a id="scoreFilter" class="filterButton">Score&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
		</div><br />
		<div id="breadbox" style="width: 765px; margin-top: 10px;">
			<div id="filterSummary" class="filters" style="display: none">
				<span class="label">Filtered by:</span>&nbsp; <span id="defaultText" style="display: none;">No filters selected.</span> <span id="filterList"></span><br />
			</div>
		</div>
	</div>
</div>
<form action="${configBean.FEWI_URL}batch/summary" method="post" target="_blank" enctype="multipart/form-data" name="batchWeb" style="display:inline;" id="batchForm">
	<input type="hidden" value="MGI" name="idType">
	<input type="hidden" value="Nomenclature" name="attributes">
	<input type="hidden" value="Location" name="attributes">
	<input type="hidden" value="" name="ids" id="ids">
</form>
<form action="${configBean.MOUSEMINE_URL}mousemine/portal.do" method="post" target="_blank" name="mouseMineWeb" style="display:inline;" id="mouseMineForm">
	<input type="hidden" value="SequenceFeature" name="class">
	<input type="hidden" value="" name="externalids" id="mousemineids">
</form>
<br style="clear: both;" />
<div style="float: left; width: 1245px;">
	<div style="margin-top: 30px; float: left;">
		<span class="filterButton" id="showGraphHelpDiv" style="margin-right: 10px;">Graph Help</span>
		<span class="filterButton" style="margin-right: 10px;"><a target="_blank" href="${configBean.FTP_URL}feature_relationships_downloads" title="Download the full data set from the MGI ftp site">Download Data</a></span>
		<a id="toBatchQuery" target="_blank" class="filterButton" onClick="javascript: batchWeb.submit();" style="display:none" title="Forward the genome features from the table to the Batch Query form"><img src="${configBean.WEBSHARE_URL}images/arrow_right.gif" width="10" height="10" /> Batch Query</a>
		<span style="margin-right: 10px"></span>
		<a id="toMouseMine" target="_blank" class="filterButton" onClick="javascript: mouseMineWeb.submit();" style="display:none" title="Forward the genome features from the table to MouseMine"><img src="${configBean.WEBSHARE_URL}images/arrow_right.gif" width="10" height="10" /> MouseMine</a>
		<div style="display: none"><span class="label">Export:</span>
		<a id="relationshipTextDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a>
		<a id="relationshipExcelDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/excel.jpg" width="10" height="10" /> Excel File</a></div>
	</div>

	<div id="paginationTop" style="text-align: right; float: right;">&nbsp;</div>
	
</div>

	<script type="text/javascript" src="${configBean.WEBSHARE_URL}js/slider-min.js"></script>
	<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/filters.js"></script>
	<script type="text/javascript">
		var fewiurl = "${configBean.FEWI_URL}";
		var querystring = "${e:forJavaScript(queryString)}";
		var plusimage = "${configBean.FEWI_URL}assets/images/zoom-plus-mini.png";
		var minusimage = "${configBean.FEWI_URL}assets/images/zoom-minus-mini.png";

		function getQuerystring() {
			return querystring + filters.getUrlFragment();
			//	    return "markerIDs=" + markerIDs.join(",");
		}
	</script>
	<script type="text/javascript" charset="utf-8" src="https://d3js.org/d3.v3.js"></script>
	<!--  <script type="text/javascript" charset="utf-8" src="${configBean.FEWI_URL}assets/js/d3.v3.js"></script> -->

	<div id="panes" style="clear: both; white-space: nowrap;">
		<!-- removed resize:both from the styles for these two divs -->
		<div id="graph" style="width: 600px; height: 550px; display: inline-block; border: 1px solid black; vertical-align: top;"></div>
		<div id="dynamicdata" style="display: inline-block; height: 550px; width: 630px; overflow: auto; border: 1px solid black; vertical-align: top;"></div>
	</div>

	<style>
		.facetFilter .yui-panel .bd {
			width: 285px
		}
		
		.yui-skin-sam .yui-h-slider {
			background:
				url("${configBean.FEWI_URL}assets/images/slider_background_h.png")
				no-repeat scroll 5px 0 rgba(0, 0, 0, 0);
			width: 263px;
		}
	</style>

	<div class="facetFilter">
		<div id="facetDialog">
			<div class="hd">Filter</div>
			<div class="bd">
				<form:form method="GET"
					action="${configBean.FEWI_URL}interaction/explorer">
					<img src="/fewi/mgi/assets/images/loading.gif">
				</form:form>
			</div>
		</div>
	</div>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/interaction_summary.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/interaction_graph_summary.js"></script>
<script type="text/javascript">
    filters.setQueryStringFunction(getQuerystring);
    filters.setFewiUrl("${configBean.FEWI_URL}");
    filters.setHistoryManagement("myDataTable", handleNavigationRaw);
    filters.setDataTable(is_getDataTable());
    filters.setGeneratePageRequestFunction(is_generateRequest);
    filters.addFilter('termFilter', 'Interaction', 'termFilter', 'relationshipTermFilter', fewiurl + 'interaction/facet/interaction');
    filters.addFilter('validationFilter', 'Validation', 'validationFilter', 'validationFilter', fewiurl + 'interaction/facet/validation');
    filters.addFilter('dataSourceFilter', 'Data Source', 'dataSourceFilter', 'dataSourceFilter', fewiurl + 'interaction/facet/dataSource');
    filters.addFilter('scoreFilter', 'Score', 'scoreFilter', 'scoreFilter', fewiurl + 'interaction/facet/score',
		filters.sliderFormatter, filters.sliderParser, 'Filter Predicted Interactions by Score');
    filters.setSummaryNames('filterSummary', 'filterList');
</script>

<div id="graphHelpDivDialog" class="facetFilter">
   <div class="hd">Graph Help</div>
   <div class="bd">
	   <p>The graph shows the selected marker at the center, surrounded by the markers for which there is evidence of an interaction. Blue lines connect validated interactions and red lines connect predicted interactions.</p>
	   <p>The first 500 markers are shown based on how the table is sorted. The default sort is by validation type and then score.</p>
	   <p>Apply filters to reduce the number of results.</p>
	   <p>Click the plus sign to zoom in (2X) on the graph and the minus sign to zoom out (50%).</p>
	   <p>Click and hold to drag and reposition the graph.</p>
   </div>
</div>

<div id="mgiHelpDivDialog" class="facetFilter">
	<div class="hd">About MGI Interaction Data</div>
	<div class="bd">

		MGI obtains interaction data associated with mouse genes (or genome
		features) from expert resources and published literature, and displays
		these data as interactions between the associated genes (or features).
		The types of interactions and their sources currently represented are:
		<p>

		<style type="text/css">
			table.sourceTable td, table.sourceTable th{
				border:1px solid #002255;
				text-align: center;
				padding:1px;
			}
		</style>
		<table class="sourceTable">
			<tr><td><b>Interaction Type</b></td><td><b>Data Source</b></td><td><b>Validation</b></td></tr>
			<tr><td rowspan=4>microRNA targeting</td><td>mirTarBase</td><td>Validated</td></tr>
			<tr><td>microT-CDS</td><td>Predicted</td></tr>
			<tr><td>miRDB</td><td>Predicted</td></tr>
			<tr><td>Pictar</td><td>Predicted</td></tr>
		</table>
		<p>

			<b>Interaction Types</b><br> Additional interaction types (e.g.,
			protein-protein, genetic, co-expression) are currently not
			represented but will be implemented over time. <br> <br> <b>Gene
				Product Information</b><br> Information on the specific gene
			products involved for different interaction types is available by
			downloading the interaction data using the "Download Data" button.
			This leads to an ftp directory where interaction data files can be selected for download.  More information on the download data is available in a README file in the ftp directory.
			<br>
			<br> <b>Validation</b><br> MGI supports experimentally
			validated and predicted interaction data, and distinguishes these in
			the Interaction Explorer by edge color in the graph view
			(Validated=blue, Predicted=red), and with a column in the table view.
			You can filter interaction results by Validation using the filter
			provided, and sort the table view by Validation, by clicking on the
			column header. Sorting by Validation applies a secondary sort by
			Score to predicted interactions.<br> <br> <b>Score</b><br>
			Predicted interactions are represented with a prediction score and
			the algorithm used by the data source to make the prediction. Scores
			from different sources vary in scale and range. To allow sorting and
			filtering different data sets by Score, MGI scales Score values to a
			common range (between 0 and 1.0). Users are advised to consult
			algorithm details before comparing interaction score rank between
			different algorithms. Algorithms used for predicted interactions are
			not shown in the table view, but can be obtained by downloading the
			interaction data, and information about the data sources and
			algorithms can be found in the associated references. You can filter
			interaction results by a selected Minimum Score threshold value using
			the "Score" filter provided, and sort by clicking on the Score column
			header in the table view. The threshold Score filter only applies to
			interactions with a Score value. Sorting by Score weights any Score
			value higher than no Score value (Validated and Inferred interactions
			often have no Score value). <br>
	</div>
</div>

<script type="text/javascript">
	YAHOO.namespace("example.container");

	YAHOO.util.Event.onDOMReady(function() {
		// Instantiate a Panel from markup
		YAHOO.example.container.panel1 = new YAHOO.widget.Panel(
				"graphHelpDivDialog", {
					width : "420px",
					visible : false,
					constraintoviewport : true,
					context : [ 'showGraphHelpDiv', 'tr', 'bl' ]
				});
		YAHOO.example.container.panel1.render();
		YAHOO.util.Event.addListener("showGraphHelpDiv", "click",
				YAHOO.example.container.panel1.show,
				YAHOO.example.container.panel1, true);

		YAHOO.example.container.panel2 = new YAHOO.widget.Panel(
				"mgiHelpDivDialog", {
					width : "700px",
					visible : false,
					constraintoviewport : true,
					context : [ 'showMgiHelpDiv', 'tr', 'bl' ]
				});
		YAHOO.example.container.panel2.render();
		YAHOO.util.Event.addListener("showMgiHelpDiv", "click",
				YAHOO.example.container.panel2.show,
				YAHOO.example.container.panel2, true);
	});

	var relationshipTextReportButton = YAHOO.util.Dom.get('relationshipTextDownload');
	if (!YAHOO.lang.isNull(relationshipTextReportButton)) {
		relationshipTextReportButton.setAttribute('href', fewiurl + 'interaction/report.txt?markerIDs=' + markerIDs);
	}

	var relationshipExcelReportButton = YAHOO.util.Dom.get('relationshipExcelDownload');
	if (!YAHOO.lang.isNull(relationshipExcelReportButton)) {
		relationshipExcelReportButton.setAttribute('href', fewiurl + 'interaction/report.xlsx?markerIDs=' + markerIDs);
	}

    is_updateBatchQueryLink();
    filters.registerCallback("BatchCallback", is_updateBatchQueryLink);

//    var batchButton = YAHOO.util.Dom.get('toBatchQuery');
//    var batchForm = YAHOO.util.Dom.get('batchForm');
//    batchButton.href = 'javascript:batchForm.submit()';
</script>


</div>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
