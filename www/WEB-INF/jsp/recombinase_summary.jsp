<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

${templateBean.templateHeadHtml}

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>Recombinase Summary</title>

<style>
body {z-index=-2;}

.yui-skin-sam .yui-dt th{
  background:url(http://cardolan.informatics.jax.org/webshare/images/cre/SpriteYuiOverRide.png)
  repeat-x 0 -1300px;
}
.yui-skin-sam th.yui-dt-asc,.yui-skin-sam th.yui-dt-desc{
  background:url(http://cardolan.informatics.jax.org/webshare/images/cre/SpriteYuiOverRide.png)
  repeat-x 0 -1400px;
}

.yui-skin-sam th.yui-dt-sortable .yui-dt-liner{
  background:url(http://cardolan.informatics.jax.org/webshare/images/cre/creSortableArrow.png)
  no-repeat right;
}
.yui-skin-sam th.yui-dt-asc .yui-dt-liner{
  background:url(http://cardolan.informatics.jax.org/webshare/images/cre/creDownArrow.png)
  no-repeat right;
}
.yui-skin-sam th.yui-dt-desc .yui-dt-liner{
  background:url(http://cardolan.informatics.jax.org/webshare/images/cre/creUpArrow.png)
  no-repeat right;
}
.yui-dt a {
  text-decoration: none;
}
.yui-dt img {
  border: none;
}

.pageAdvice {
  font-size: 11px;
  font-style: italic;
  color: #002255;
  padding:2px;
}
.selectText {
  font-size:10px;
}
.smallerCellText{
  font-size:10px;
}
.colSelectContainer{
  width:700px;
  position: relative;
  height:200px;
  border: 1px #AAA solid;
}
.colSelectSubContainer{
  /*border: 1px #999999 solid;*/
}
#summaryResetButton {
font-size: 12px;
font-family: Verdana,Arial,Helvetica;
color: #002255;
font-weight: bolder;
background-color: #eeeeee;
border: 1px #7D95B9 solid;
padding: 2px;
cursor: pointer;
}

table.checkBoxSelectTable{
    border-collapse:collapse;
    border:1px solid #AAA;
    border-spacing:0px;
    padding:0px;
    white-space:nowrap;
    width:auto;
    line-height:1.1;
    line-height:110%;
}
table.checkBoxSelectTable td{
    font-size:11px;
    white-space:nowrap;
}
</style>
<script src="/fewi/js/rowexpansion.js"></script>

<!-- Browser History Manager source file -->
<script src="http://yui.yahooapis.com/2.8.1/build/history/history-min.js"></script>

${templateBean.templateBodyStartHtml}

<iframe id="yui-history-iframe" src="/fewi/js/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- begin header bar -->
<div id="titleBarWrapper" style="max-width:1200px" userdoc="marker_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">Recombinase Summary</span>
</div>
<!-- end header bar -->

<script type="text/javascript">
function flipColumn (checkboxID, column) {
	var thisCheckBox = document.getElementById(checkboxID);
	var myDataTable = YAHOO.mgiData.myDataTable;
	if (YAHOO.util.Dom.hasClass(thisCheckBox, "checkboxSelected")) {
		myDataTable.hideColumn (column);
		YAHOO.util.Dom.removeClass(thisCheckBox, "checkboxSelected");
	} else {
		myDataTable.showColumn (column);
		YAHOO.util.Dom.addClass(thisCheckBox, "checkboxSelected");
	}
}
</script>

<div id="checkboxes">
  <table class="checkBoxSelectTable">
    <tr><td colspan="4" class="pageAdvice">You can control the data displayed below.</td>
      <td colspan="2">&nbsp;</td></tr>
    <tr><td colspan="4" class="pageAdvice">Check the boxes to show Anatomical System
      columns containing links to data and images.</td>
      <td colspan="2" class="pageAdvice">Hide or show other columns.</td></tr>
	<tr>
	  <td><input type="checkbox" id="adiposeTissueCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('adiposeTissueCheckbox', 'inAdiposeTissue');">Adipose&nbsp;Tissue</input>
	  </td>
	  <td><input type="checkbox" id="headCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('headCheckbox', 'inHead');">Head</input>
	  </td>
	  <td><input type="checkbox" id="muscleCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('muscleCheckbox', 'inMuscle');">Muscle</input>
	  </td>
	  <td><input type="checkbox" id="skeletalSystemCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('skeletalSystemCheckbox', 'inSkeletalSystem');">Skeletal&nbsp;System</input>
	  </td>
	  <td colspan="2"><input type="checkbox" id="synonymsCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('synonymsCheckbox', 'synonyms');">Allele&nbsp;Synonyms</input>
	  </td>
	</tr>
	<tr>
	  <td><input type="checkbox" id="alimentarySystemCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('alimentarySystemCheckbox', 'inAlimentarySystem');">Alimentary&nbsp;System</input>
	  </td>
	  <td><input type="checkbox" id="hemolymphoidSystemCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('hemolymphoidSystemCheckbox', 'inHemolymphoidSystem');">Hemolymphoid&nbsp;System</input>
	  </td>
	  <td><input type="checkbox" id="nervousSystemCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('nervousSystemCheckbox', 'inNervousSystem');">Nervous&nbsp;System</input>
	  </td>
	  <td><input type="checkbox" id="tailCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('tailCheckbox', 'inTail');">Tail</input>
	  </td>
	  <td><input type="checkbox" id="alleleTypeCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('alleleTypeCheckbox', 'alleleType');">Allele&nbsp;Type</input>
	  </td>
	  <td><input type="checkbox" id="imsrCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('imsrCheckbox', 'imsrCount');">IMSR</input>
	  </td>
	</tr>
	<tr>
	  <td><input type="checkbox" id="branchialArchesCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('branchialArchesCheckbox', 'inBranchialArches');">Branchial&nbsp;Arches</input>
	  </td>
	  <td><input type="checkbox" id="integumentalSystemCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('integumentalSystemCheckbox', 'inIntegumentalSystem');">Integumental&nbsp;System</input>
	  </td>
	  <td><input type="checkbox" id="renalAndUrinarySystemCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('renalAndUrinarySystemCheckbox', 'inRenalAndUrinarySystem');">Renal&nbsp;and&nbsp;Urinary&nbsp;System</input>
	  </td>
	  <td><input type="checkbox" id="earlyEmbryoCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('earlyEmbryoCheckbox', 'inEarlyEmbryo');">Early&nbsp;Embryo,&nbsp;All&nbsp;Tissues</input>
	  </td>
	  <td><input type="checkbox" id="inducibleCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('inducibleCheckbox', 'inducible');">Inducible</input>
	  </td>
	  <td><input type="checkbox" id="referenceCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('referenceCheckbox', 'countOfReferences');">References</input>
	  </td>
	</tr>
	<tr>
	  <td><input type="checkbox" id="cardiovascularCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('cardiovascularCheckbox', 'inCardiovascularSystem');">Cardiovascular&nbsp;System</input>
	  </td>
	  <td><input type="checkbox" id="limbsCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('limbsCheckbox', 'inLimbs');">Limbs</input>
	  </td>
	  <td><input type="checkbox" id="reproductiveSystemCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('reproductiveSystemCheckbox', 'inReproductiveSystem');">Reproductive&nbsp;System</input>
	  </td>
	  <td><input type="checkbox" id="extraEmbryonicCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('extraEmbryonicCheckbox', 'inExtraembryonicComponent');">Extraembryonic&nbsp;Component</input>
	  </td>
	  <td colspan="2">&nbsp;</td>
	</tr>
	<tr>
	  <td><input type="checkbox" id="cavitiesAndLiningsCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('cavitiesAndLiningsCheckbox', 'inCavitiesAndLinings');">Cavities&nbsp;And&nbsp;Linings</input>
	  </td>
	  <td><input type="checkbox" id="liverAndBiliarySystemCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('liverAndBiliarySystemCheckbox', 'inLiverAndBiliarySystem');">Liver&nbsp;and&nbsp;Biliary&nbsp;System</input>
	  </td>
	  <td><input type="checkbox" id="respiratorySystemCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('respiratorySystemCheckbox', 'inRespiratorySystem');">Respiratory&nbsp;System</input>
	  </td>
	  <td colspan="3"><input type="checkbox" id="embryoOtherCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('embryoOtherCheckbox', 'inEmbryoOther');">Embryo-other&nbsp;(Embryonic&nbsp;structures&nbsp;not&nbsp;listed&nbsp;above)</input>
	  </td>
	</tr>
	<tr>
	  <td><input type="checkbox" id="endocrineSystemCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('endocrineSystemCheckbox', 'inEndocrineSystem');">Endocrine&nbsp;System</input>
	  </td>
	  <td><input type="checkbox" id="mesenchymeCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('mesenchymeCheckbox', 'inMesenchyme');">Mesenchyme</input>
	  </td>
	  <td><input type="checkbox" id="sensoryOrgansCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('sensoryOrgansCheckbox', 'inSensoryOrgans');">Sensory&nbsp;Organs</input>
	  </td>
	  <td colspan="2"><input type="checkbox" id="postnatalOtherCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('postnatalOtherCheckbox', 'inPostnatalOther');">Postnatal-other&nbsp;(Postnatal&nbsp;structures&nbsp;not&nbsp;listed&nbsp;above)</input>
	  </td>
	  <td>Reset Page</td>
	</tr>
  </table>
</div>
<div>
	<div id="querySummary">
		<span class="enhance">You searched for:</span><br/>
		<c:if test="${not empty recombinaseQueryForm.system}"><span class="label">System:</span> 
			${fn:replace(recombinaseQueryForm.system,";", ",") }<br/></c:if>
		<c:if test="${not empty recombinaseQueryForm.driver}"><span class="label">Driver:</span> 
			${fn:replace(recombinaseQueryForm.driver,";", ",") }<br/></c:if>
    <span class="pageAdvice" style="height: 20px;">
	    Click column headings to sort table data.  Drag headings to rearrange columns.
    </span>
	</div>
</div>
<div id="paginationTop"  style="float:right;"></div>
<div id="dynamicdata"></div>

<script type="text/javascript">
(function () {	
	// this function formats the allele/gene nomenclature column
    this.nomenFormatter = function(elLiner, oRecord, oColumn, oData) {
		// if gene name and allele name match, only show one
		if (oRecord.getData("name") == oRecord.getData("geneName")) {
			elLiner.innerHTML= '<b>' + oRecord.getData("symbol") + '</b><br/>' + 
			oRecord.getData("name");
		} else {
			// the gene name and allele name differ, so show both
			elLiner.innerHTML= '<b>' + oRecord.getData("symbol") + '</b><br/>' + 
				oRecord.getData("geneName") + '; ' + oRecord.getData("name");
		}
	};

    YAHOO.namespace ('mgiData');
	
	this.detectedInFormatter = function(elLiner, oRecord, oColumn, oData) {
		if (oRecord.getData("detectedCount") > 0) {
			elLiner.innerHTML="Detected In: " + oRecord.getData("detectedCount");
		} else {
			elLiner.innerHTML="";
		}
		if (oRecord.getData("notDetectedCount") > 0) {
			elLiner.innerHTML = elLiner.innerHTML + "<br/>Not Detected In: " + oRecord.getData("notDetectedCount");
		}
	};
	
    // Adds the formatters above to the to the data table, so we can reference
    // them by name for individual columns
    YAHOO.widget.DataTable.Formatter.nomen = this.nomenFormatter;
	YAHOO.widget.DataTable.Formatter.detectedIn = this.detectedInFormatter;
    
    // Column definitions -- sortable:true enables sorting
    // These are our actual columns, in the default ordering.
    var myColumnDefs = [
        {key:"driver", 
            label:"<B>Driver</B>",
            width:90, 
            sortable:true},
        {key:"symbol", 
            label:"<B>Allele Symbol<br/>Gene; Allele Name</B>",
			sortable:true,
			width:245, 
			formatter:"nomen"},
		{key:"detectedIn", 
			label:"<B>Recombinase<br/>Data</B>", 
			sortable:true, 
			width:220,
			formatter:"detectedIn"},
	    {key:"inAdiposeTissue", 
			label:"Adipose<br/>Tissue", 
			sortable:true,
			width:54},
		{key:"inAlimentarySystem",
			label:"Alimentary<br/>System",
			sortable:true,
			width:60},
		{key:"inBranchialArches",
			label:"Branchial<br/>Arches",
			sortable:true,
			width:60},
		{key:"inCardiovascularSystem",
			label:"Cardiovascular<br/>System",
			sortable:true,
			width:88},
		{key:"inCavitiesAndLinings",
			label:"Cavities &amp;<br/>their Linings",
			sortable:true,
			width:72},
		{key:"inEndocrineSystem",
			label:"Endocrine<br/>System",
			sortable:true,
			width:64},
		{key:"inHead",
			label:"Head",
			sortable:true,
			width:54},
		{key:"inHemolymphoidSystem",
			label:"Hemolymphoid<br/>System",
			sortable:true,
			width:88},
		{key:"inIntegumentalSystem",
			label:"Integumental<br/>System",
			sortable:true,
			width:82},
		{key:"inLimbs",
			label:"Limbs",
			sortable:true,
			width:54},
		{key:"inLiverAndBiliarySystem",
			label:"Liver &amp;<br/>Biliary System",
			sortable:true,
			width:84},
		{key:"inMesenchyme",
			label:"Mesenchyme",
			sortable:true,
			width:82},
		{key:"inMuscle",
			label:"Muscle",
			sortable:true,
			width:54},
		{key:"inNervousSystem",
			label:"Nervous<br/>System",
			sortable:true,
			width:60},
		{key:"inRenalAndUrinarySystem",
			label:"Renal &amp;<br/>Urinary System",
			sortable:true,
			width:90},
		{key:"inReproductiveSystem",
			label:"Reproductive<br/>System",
			sortable:true,
			width:80},
		{key:"inRespiratorySystem",
			label:"Respiratory<br/>System",
			sortable:true,
			width:72},
		{key:"inSensoryOrgans",
			label:"Sensory<br/>Organs",
			sortable:true,
			width:54},
		{key:"inSkeletalSystem",
			label:"Skeletal<br/>System",
			sortable:true,
			width:60},
		{key:"inTail",
			label:"Tail",
			sortable:true,
			width:54},
		{key:"inEarlyEmbryo",
			label:"Early<br/>Embryo",
			sortable:true,
			width:60},
		{key:"inExtraembryonicComponent",
			label:"Extraembryonic<br/>Component",
			sortable:true,
			width:90},
		{key:"inEmbryoOther",
			label:"Embryo<br/>Other",
			sortable:true,
			width:60},
		{key:"inPostnatalOther",
			label:"Postnatal<br/>Other",
			sortable:true,
			width:60},
        {key:"synonyms",
            label:"<B>Allele Synonym</B>",
            width:170,
            sortable:false},
   		{key:"alleleType", 
            label:"<B>Allele<br/>Type</B>",
            width:60, 
            sortable:true},
        {key:"inducibleNote", 
            label:"<B>Inducible</B>",
            width:58, 
            sortable:true},
        {key:"imsrCount", 
            label:"<B>Find Mice<br/>(IMSR)</B>",
            width:60, 
            sortable:true},
        {key:"countOfReferences", 
            label:"<B>Refs</B>",
            width:36, 
            sortable:true},
    ];

    // DataSource instance
    var myDataSource = new YAHOO.util.DataSource("json?${queryString}&");

    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
    myDataSource.responseSchema = {
        resultsList: "resultObjects",
        fields: [
			{key:"driver"},
			{key:"symbol"},
			{key:"name"},
            {key:"geneName"},
            {key:"inAdiposeTissue"},
            {key:"inAlimentarySystem"},
            {key:"inBranchialArches"},
            {key:"inCardiovascularSystem"},
            {key:"inCavitiesAndLinings"},
            {key:"inEndocrineSystem"},
            {key:"inHead"},
            {key:"inHemolymphoidSystem"},
            {key:"inIntegumentalSystem"},
            {key:"inLimbs"},
            {key:"inLiverAndBiliarySystem"},
            {key:"inMesenchyme"},
            {key:"inMuscle"},
            {key:"inNervousSystem"},
            {key:"inRenalAndUrinarySystem"},
            {key:"inReproductiveSystem"},
            {key:"inRespiratorySystem"},
            {key:"inSensoryOrgans"},
            {key:"inSkeletalSystem"},
            {key:"inTail"},
            {key:"inEarlyEmbryo"},
            {key:"inExtraembryonicComponent"},
            {key:"inEmbryoOther"},
            {key:"inPostnatalOther"},
            {key:"inducibleNote"},
            {key:"alleleType"},
            {key:"countOfReferences"},
            {key:"imsrCount"},
            {key:"detectedCount"},
            {key:"notDetectedCount"},
            {key:"synonyms"},
        ],
        metaFields: {
            totalRecords: "totalCount",
            paginationRecordOffset : "startIndex",
            paginationRowsPerPage : "pageSize",
            sortKey: "sort",
            sortDir: "dir"
        }
    };

    // Create the Paginator
    var myPaginator = new YAHOO.widget.Paginator({
        template : "{PreviousPageLink} <strong>{PageLinks}</strong> {NextPageLink} <span style=align:right;>{RowsPerPageDropdown}</span><br/>{CurrentPageReport}",
        pageReportTemplate : "Showing items {startRecord} - {endRecord} of {totalRecords}",
        rowsPerPageOptions : [10,25,50,100],
        rowsPerPage : 25,
        pageLinks: 5,
        recordOffset: 1
    });

    // DataTable configurations
    var myConfigs = {
        paginator : myPaginator,
        dynamicData : true,
        draggableColumns : true,
        initialLoad : false
    };  
    
    // DataTable instance
    var myDataTable = new YAHOO.widget.DataTable("dynamicdata", myColumnDefs, 
    	    myDataSource, myConfigs);
    YAHOO.mgiData.myDataTable = myDataTable;


    // Show loading message while page is being rendered
    myDataTable.showTableMessage(myDataTable.get("MSG_LOADING"), 
    	    YAHOO.widget.DataTable.CLASS_LOADING);    
    
    // Integrate with Browser History Manager
    var History = YAHOO.util.History;

    // Define a custom function to route sorting through the Browser History Manager
    var handleSorting = function (oColumn) {
        // Calculate next sort direction for given Column
        var sDir = this.getColumnSortDir(oColumn);
        
        // The next state will reflect the new sort values
        // while preserving existing pagination rows-per-page
        // As a best practice, a new sort will reset to page 0
        var newState = generateRequest(0, oColumn.key, sDir, 
                this.get("paginator").getRowsPerPage());

        // Pass the state along to the Browser History Manager
        History.navigate("myDataTable", newState);
    };
    myDataTable.sortColumn = handleSorting;

    // Define a custom function to route pagination through the Browser History Manager
    var handlePagination = function(state) {
        // The next state will reflect the new pagination values
        // while preserving existing sort values
        // Note that the sort direction needs to be converted from DataTable format to server value
        var sortedBy  = this.get("sortedBy"),
            newState = generateRequest(
            state.recordOffset, sortedBy.key, sortedBy.dir, state.rowsPerPage
        );
        myPaginator.setState(state);
        // Pass the state along to the Browser History Manager
        History.navigate("myDataTable", newState);
    };
    // First we must unhook the built-in mechanism...
    myPaginator.unsubscribe("changeRequest", myDataTable.onPaginatorChangeRequest);
    // ...then we hook up our custom function
    myPaginator.subscribe("changeRequest", handlePagination, myDataTable, true);

    // Update payload data on the fly for tight integration with latest values from server 
    myDataTable.doBeforeLoadData = function(oRequest, oResponse, oPayload) {
		var pRequest = parseRequest(oRequest);
        var meta = oResponse.meta;

        oPayload.totalRecords = meta.totalRecords || oPayload.totalRecords;
        oPayload.pagination = {
            rowsPerPage: Number(pRequest['results']) || 25,
            recordOffset: Number(pRequest['startIndex']) || 0
        };
        oPayload.sortedBy = {
            key: pRequest['sort'] || "symbol",
            dir: pRequest['dir'] ? "yui-dt-" + pRequest['dir'] : "yui-dt-asc" // Convert from server value to DataTable format
        };
        return true;
    };

	// TODO -- check out these methods, as they may be useful for showing a Loading message
	//	during loads of new data
	// myDataTable.doBeforePaginatorChange()
	// myDataTable.doBeforeSortColumn()
	// myDataTable.showTableMessage()
    
    // TODO -- other useful methods
    // myDataTable.hideColumn()
    // myDataTable.showColumn()
    
    // Returns a request string for consumption by the DataSource
    var generateRequest = function(startIndex,sortKey,dir,results) {
        startIndex = startIndex || 0;
        sortKey   = sortKey || "symbol";
        dir   = (dir) ? dir.substring(7) : "asc"; // Converts from DataTable format "yui-dt-[dir]" to server value "[dir]"
        results   = results || 25;
        return "results="+results+"&startIndex="+startIndex+"&sort="+sortKey+"&dir="+dir;
    };

    // Called by Browser History Manager to trigger a new state
    var handleHistoryNavigation = function (request) {
        // Sends a new request to the DataSource
        myDataSource.sendRequest(request,{
            success : myDataTable.onDataReturnSetRows,
            failure : myDataTable.onDataReturnSetRows,
            scope : myDataTable,
            argument : {} // Pass in container for population at runtime via doBeforeLoadData
        });
    };

    // Calculate the first request
    var initialRequest = History.getBookmarkedState("myDataTable") || // Passed in via URL
                       generateRequest(); // Get default values

    // Register the module
    History.register("myDataTable", initialRequest, handleHistoryNavigation);

    // Render the first view
    History.onReady(function() {
        // Current state after BHM is initialized is the source of truth for what state to render
        var currentState = History.getCurrentState("myDataTable");
        handleHistoryNavigation(currentState);
    });

    // Initialize the Browser History Manager.
    YAHOO.util.History.initialize("yui-history-field", "yui-history-iframe");
})();

function parseRequest(request){
	var reply = [];
	var kvPairs = request.split('&');
	for (pair in kvPairs) {
		var kv = kvPairs[pair].split('=');
		reply[kv[0]] = kv[1];
	}
	return reply;
}

</script>

${templateBean.templateBodyStopHtml}
