<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

${templateBean.templateHeadHtml}

<title>Reference Summary</title>

<style type="text/css">
/*margin and padding on body element
  can introduce errors in determining
  element position and are not recommended;
  we turn them off as a foundation for YUI
  CSS treatments. */
body {
	margin:0;
	padding:0;
}
#yui-history-iframe {
  position: absolute;
  top: 0;
  left: 0;
  width: 1px;
  height: 1px;
  visibility: hidden;
}

</style>

<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.1/build/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.1/build/paginator/assets/skins/sam/paginator.css" />
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.1/build/datatable/assets/skins/sam/datatable.css" />
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/yahoo-dom-event/yahoo-dom-event.js"></script>

<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/connection/connection-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/json/json-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/element/element-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/paginator/paginator-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/datasource/datasource-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/datatable/datatable-min.js"></script>

<script src="/fewi/js/blank.html"></script>

<!-- Browser History Manager source file -->
<script src="http://yui.yahooapis.com/2.8.1/build/history/history-min.js"></script>

${templateBean.templateBodyStartHtml}

<iframe id="yui-history-iframe" src="/fewi/js/rowexpansion.js"></iframe>
<input id="yui-history-field" type="hidden">


<!-- begin header bar -->
<div id="titleBarWrapper" style="max-width:1200px" userdoc="marker_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">References Summary</span>
</div>
<!-- end header bar -->

<div id="dynamicdata"></div>

<script type="text/javascript">
(function () {	
	// this function formats the marker detail link
    this.volFormatter = function(elLiner, oRecord, oColumn, oData) {
		elLiner.innerHTML= '<b>' + oRecord.getData("vol") + '</b>(' + oRecord.getData("issue") + ')' + oRecord.getData("pages");
    };
    
    // Adds the formatter above to the to the symbol col
    YAHOO.widget.DataTable.Formatter.vol = this.volFormatter;

    // Column definitions
    var myColumnDefs = [ // sortable:true enables sorting
        {key:"jnumID", label:"jnumID", sortable:false},
        {key:"authors", label:"Authors", sortable:true},

        {key:"title", label:"Title", sortable:false},
        {key:"journal", label:"Journal", sortable:true},
        {key:"year", label:"Year", sortable:true},
        {key:"volFomatter", label:"Vol(Iss)Pg", sortable:false, formatter:"vol"},
    ];

    // DataSource instance
    var myDataSource = new YAHOO.util.DataSource("json?${queryString}&");
    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
    myDataSource.responseSchema = {
        resultsList: "resultObjects",
        fields: [
			{key:"jnumID"},
            {key:"journal"},
            {key:"title"},
            {key:"authors"},
            {key:"abstract"},
            {key:"year"},
            {key:"vol"},
            {key:"issue"},
            {key:"pages"},
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
        template : "{PreviousPageLink} <strong>{PageLinks}</strong> {NextPageLink} <span style=align:right;>{RowsPerPageDropdown}</span>",
        pageReportTemplate : "Showing items {startIndex} - {endIndex} of {totalRecords}",
        rowsPerPageOptions : [10,25,50,100],
        rowsPerPage : 25,
        pageLinks: 5
    });


    // DataTable configurations
    var myConfigs = {
        paginator : myPaginator,
        dynamicData : true,
        initialLoad : false
    };  
    
    // DataTable instance
    var myDataTable = new YAHOO.widget.DataTable("dynamicdata", myColumnDefs, myDataSource, myConfigs);

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
            key: pRequest['sort'] || "journal",
            dir: pRequest['dir'] ? "yui-dt-" + pRequest['dir'] : "yui-dt-asc" // Convert from server value to DataTable format
        };
        return true;
    };
    
    // Returns a request string for consumption by the DataSource
    var generateRequest = function(startIndex,sortKey,dir,results) {
        startIndex = startIndex || 0;
        sortKey   = sortKey || "journal";
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
