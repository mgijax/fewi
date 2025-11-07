var recomDataTable;

function main() {
    // Column definitions -- sortable:true enables sorting
    // These are our actual columns, in the default ordering.

    var myColumnDefs = [
        {key:"structure", 
            label:"<b>Structure</b>",
            width:112, 
            sortable:true},
        {key:"cellType", 
            label:"<b>Cell Type</b>",
            maxAutoWidth:80, 
            sortable:false},
        {key:"assayedAge", 
            label:"<b>Assayed Age</b>",
            width:80, 
            sortable:true},
        {key:"level", 
            label:"<b>Level</b>",
            sortable:true,
            width:53},
        {key:"pattern", 
            label:"<b>Pattern</b>",
            sortable:true,
            width:53},
        {key:"source", 
            label:"<b>Reference, Source</b>",
            sortable:true,
            width:120},
        {key:"assayType", 
            label:"<b>Assay Type</b>",
            sortable:false,
            width:120},
        {key:"reporterGene", 
            label:"<b>Reporter Gene</b>",
            sortable:false,
            width:80},
        {key:"detectionMethod", 
            label:"<b>Detection Method</b>",
            sortable:false,
            width:120},
        {key:"assayNote", 
            label:"<b>Assay Note</b>",
            sortable:false,
            width:160},
        {key:"allelicComp", 
            hidden:true,
            label:"<b>Allelic Composition, Genetic Background</b>",
            sortable:false,
            width:300},
        {key:"sex", 
            hidden:true,
            label:"<b>Sex</b>",
            sortable:false,
            width:50},
        {key:"specimenNote", 
            hidden:true,
            label:"<b>Specimen Notes</b>",
            sortable:false,
            width:150},
        {key:"resultNotes", 
            hidden:true,
            label:"<b>Result Note</b>",
            sortable:false,
            width:540}

    ];

    // DataSource instance
    var myDataSource = new YAHOO.util.DataSource("${configBean.FEWI_URL}recombinase/jsonSpecificity?${queryString}&");

    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
    myDataSource.responseSchema = {
        resultsList: "summaryRows",
        fields: [
            {key:"structure"},
            {key:"cellTypes"},
            {key:"assayedAge"},
            {key:"level"},
            {key:"pattern"},
            {key:"source"},
            {key:"assayType"},
            {key:"reporterGene"},
            {key:"detectionMethod"},
            {key:"assayNote"},
            {key:"allelicComp"},
            {key:"sex"},
            {key:"specimenNote"},
            {key:"resultNotes"}
        ],
        metaFields: {
            totalRecords: "totalCount"
        }
    };

    // Create the Paginator
    var myPaginator = new YAHOO.widget.Paginator({
        template : "{PreviousPageLink} <strong>{PageLinks}</strong> {NextPageLink} <span style=align:right;>{RowsPerPageDropdown}</span><br/>{CurrentPageReport}",
        pageReportTemplate : "Showing items {startRecord} - {endRecord} of {totalRecords}",
        rowsPerPageOptions : [10,25,50,100],
        containers   : ["paginationTop", "paginationBottom"],
        rowsPerPage : 50,
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
    recomDataTable = new YAHOO.widget.DataTable("dynamicdata", myColumnDefs, 
    	    myDataSource, myConfigs);
    
    // Show loading message while page is being rendered
    recomDataTable.showTableMessage(recomDataTable.get("MSG_LOADING"), 
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
        History.navigate("recomDataTable", newState);
    };
    recomDataTable.sortColumn = handleSorting;


    // Define a custom function to route pagination through the Browser History Manager
    var handlePagination = function(state) {
        // The next state will reflect the new pagination values
        // while preserving existing sort values
        // Note that the sort direction needs to be converted from DataTable format to server value
        var newState = generateRequest(state.recordOffset, 0, 0, state.rowsPerPage);

        myPaginator.setState(state);
        // Pass the state along to the Browser History Manager
        History.navigate("recomDataTable", newState);
    };
    // First we must unhook the built-in mechanism...
    myPaginator.unsubscribe("changeRequest", recomDataTable.onPaginatorChangeRequest);
    // ...then we hook up our custom function
    myPaginator.subscribe("changeRequest", handlePagination, recomDataTable, true);

    // Update payload data on the fly for tight integration with latest values from server 
    recomDataTable.doBeforeLoadData = function(oRequest, oResponse, oPayload) {

        var pRequest = parseRequest(oRequest);
        var meta = oResponse.meta;

        oPayload.totalRecords = meta.totalRecords || oPayload.totalRecords;
        oPayload.pagination = {
            rowsPerPage: Number(pRequest['results']) || 50,
            recordOffset: Number(pRequest['startIndex']) || 0
        };

        oPayload.sortedBy = {
            key: pRequest['sort'] || "structure",
            dir: pRequest['dir'] ? "yui-dt-" + pRequest['dir'] : "yui-dt-desc" // Convert from server value to DataTable format
        };

        return true;
    };

    // Returns a request string for consumption by the DataSource
    var generateRequest = function(startIndex,sortKey,dir,results) {
    	startIndex = startIndex || 0;
        sortKey   = sortKey || "structure";
        dir   = (dir) ? dir.substring(7) : "asc"; // Converts from DataTable format "yui-dt-[dir]" to server value "[dir]"
        results   = results || 50;
        return "results="+results+"&startIndex="+startIndex+"&sort="+sortKey+"&dir="+dir;
    };

    // Called by Browser History Manager to trigger a new state
    var handleHistoryNavigation = function (request) {
    	// Sends a new request to the DataSource
        myDataSource.sendRequest(request,{
            success : recomDataTable.onDataReturnSetRows,
            failure : recomDataTable.onDataReturnSetRows,
            scope : recomDataTable,
            argument : {} // Pass in container for population at runtime via doBeforeLoadData
        });

    };

    // Calculate the first request
    var initialRequest = History.getBookmarkedState("recomDataTable") || // Passed in via URL
                       generateRequest(); // Get default values

    // Register the module
    History.register("recomDataTable", initialRequest, handleHistoryNavigation);

    // Render the first view
    History.onReady(function() {
        // Current state after BHM is initialized is the source of truth for what state to render
        var currentState = History.getCurrentState("recomDataTable");
        handleHistoryNavigation(currentState);
    });

    // Initialize the Browser History Manager.
    YAHOO.util.History.initialize("yui-history-field", "yui-history-iframe");

};
main();

function parseRequest(request){
	var reply = [];
	var kvPairs = request.split('&');
	for (pair in kvPairs) {
		var kv = kvPairs[pair].split('=');
		reply[kv[0]] = kv[1];
	}
	return reply;
}

// functions for hiding/showing of columns
var showResultNotes = function(e) {
  recomDataTable.showColumn("resultNotes");
  recomDataTable.hideColumn("allelicComp");
  recomDataTable.hideColumn("background");
  recomDataTable.hideColumn("sex");
  recomDataTable.hideColumn("specimenNote");
  recomDataTable.hideColumn("assayType");
  recomDataTable.hideColumn("reporterGene");
  recomDataTable.hideColumn("detectionMethod");
  recomDataTable.hideColumn("assayNote");
  YAHOO.util.Dom.addClass("showResultNotesButton", "creActiveButton");
  YAHOO.util.Dom.removeClass("showGenoTypeButton", "creActiveButton");
  YAHOO.util.Dom.removeClass("showAssayInfoButton", "creActiveButton");
};
var showGenoTypeInfo = function(e) {
  recomDataTable.hideColumn("resultNotes");
  recomDataTable.showColumn("allelicComp");
  recomDataTable.showColumn("background");
  recomDataTable.showColumn("sex");
  recomDataTable.showColumn("specimenNote");
  recomDataTable.hideColumn("assayType");
  recomDataTable.hideColumn("reporterGene");
  recomDataTable.hideColumn("detectionMethod");
  recomDataTable.hideColumn("assayNote");
  YAHOO.util.Dom.removeClass("showResultNotesButton", "creActiveButton");
  YAHOO.util.Dom.addClass("showGenoTypeButton", "creActiveButton");
  YAHOO.util.Dom.removeClass("showAssayInfoButton", "creActiveButton");

};
var showAssayInfo = function(e) {
  recomDataTable.hideColumn("resultNotes");
  recomDataTable.hideColumn("allelicComp");
  recomDataTable.hideColumn("background");
  recomDataTable.hideColumn("sex");
  recomDataTable.hideColumn("specimenNote");
  recomDataTable.showColumn("assayType");
  recomDataTable.showColumn("reporterGene");
  recomDataTable.showColumn("detectionMethod");
  recomDataTable.showColumn("assayNote");
  YAHOO.util.Dom.removeClass("showResultNotesButton", "creActiveButton");
  YAHOO.util.Dom.removeClass("showGenoTypeButton", "creActiveButton");
  YAHOO.util.Dom.addClass("showAssayInfoButton", "creActiveButton");
};

YAHOO.util.Event.addListener("showResultNotesButton", "click", showResultNotes, this, true);
YAHOO.util.Event.addListener("showGenoTypeButton", "click", showGenoTypeInfo, this, true);
YAHOO.util.Event.addListener("showAssayInfoButton", "click", showAssayInfo, this, true);
