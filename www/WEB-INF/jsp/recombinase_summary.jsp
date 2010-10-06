<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

${templateBean.templateHeadHtml}

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>Recombinase Summary</title>

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
<div>
	<div id="querySummary">
		<span class="enhance">You searched for:</span><br/>
		<c:if test="${not empty recombinaseQueryForm.system}"><span class="label">System:</span> 
			${fn:replace(recombinaseQueryForm.system,";", ",") }<br/></c:if>
		<c:if test="${not empty recombinaseQueryForm.driver}"><span class="label">Driver:</span> 
			${fn:replace(recombinaseQueryForm.driver,";", ",") }<br/></c:if>
	</div>
	<div id="paginationTop"  style="float:right;"></div>
</div>

<script type="text/javascript">
function flipColumn (checkboxID, column) {
	var thisCheckBox = document.getElementById(checkboxID);
	var myDataTable = YAHOO.mgiData.myDataTable;
	if (YAHOO.util.Dom.hasClass(thisCheckBox, "checkboxSelected")) {
		alert ("about to delete column: " + column);
		myDataTable.removeColumn (column);
		YAHOO.util.Dom.removeClass(thisCheckBox, "checkboxSelected");
		alert ("deleted column: " + column);
	} else {
		alert ("about to insert column: " + column);
		myDataTable.insertColumn (column);
		YAHOO.util.Dom.addClass(thisCheckBox, "checkboxSelected");
		alert ("inserted column: " + column);
	}
}
</script>

<div id="checkboxes">
    <input type="checkbox" id="refsCountCheckbox" checked="checked"
	class="checkboxSelected"
        onClick="flipColumn('refsCountCheckbox', 'countOfReferences');"></input>
</div>
<div id="dynamicdata"></div>

<script type="text/javascript">
(function () {	
	this.symbolFormatter = function(elLiner, oRecord, oColumn, oData) {
		elLiner.innerHTML= '<b>' + oRecord.getData("symbol") + '</b>';
    };
    
    // Adds the formatters above to the to the symbol col
    YAHOO.widget.DataTable.Formatter.fSymbol = this.symbolFormatter;

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

    // Adds the formatters above to the to the data table, so we can reference
    // them by name for individual columns
    YAHOO.widget.DataTable.Formatter.nomen = this.nomenFormatter;

    // Column definitions -- sortable:true enables sorting
    // These are our actual columns, in the default ordering.
    var myColumnDefs = [
        {key:"driver", label:"Driver", sortable:true},
        {key:"nomenFormatter", label:"Allele Symbol<br/>Gene; Allele Name",
			sortable:false, formatter:"nomen"},
        {key:"symbolFormatter", label:"Allele Symbol<br/>Gene; Allele Name", sortable:true, formatter:"fSymbol"},
        {key:"symbol", label:"Symbol", sortable:true},
        {key:"name", label:"Name", sortable:true},
        {key:"geneName", label:"Gene Name", sortable:true},
//        {key:"primaryID", label:"Primary ID", sortable:true},
        {key:"alleleType", label:"Allele Type", sortable:true},
        {key:"inducibleNote", label:"Inducible", sortable:true},
        {key:"countOfReferences", label:"Refs", sortable:true},
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
            {key:"primaryID"},
            {key:"inducibleNote"},
            {key:"alleleType"},
            {key:"countOfReferences"},
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
    var myDataTable = new YAHOO.widget.DataTable("dynamicdata", myColumnDefs, myDataSource, myConfigs);
    YAHOO.namespace ('mgiData');
    YAHOO.mgiData.myDataTable = myDataTable;


    // Show loading message while page is being rendered
    myDataTable.showTableMessage(myDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);    
    
    // Integrate with Browser History Manager
    var History = YAHOO.util.History;

    // Define a custom function to route sorting through the Browser History Manager
    var handleSorting = function (oColumn) {
        // Calculate next sort direction for given Column
        var sDir = this.getColumnSortDir(oColumn);
        
        // The next state will reflect the new sort values
        // while preserving existing pagination rows-per-page
        // As a best practice, a new sort will reset to page 0
        var newState = generateRequest(0, oColumn.key, sDir, this.get("paginator").getRowsPerPage());

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
