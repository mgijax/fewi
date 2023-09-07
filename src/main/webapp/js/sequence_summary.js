var myDataTable = null;
var myDataSource = null;
var handleHistoryNavigation = null;
var generateRequest = null;

function main() {
    // Column definitions -- sortable:true enables sorting
    // These are our actual columns, in the default ordering.

    var myColumnDefs = [
        {key:"seqForward", 
            label:"Select",
            width:90, 
            sortable:false},
        {key:"seqInfo", 
            label:"Sequence",
            width:180, 
            sortable:false},
        {key:"seqType", 
            label:"Type",
            sortable:false,
            width:80}, 
        {key:"length", 
            label:"Length", 
            sortable:false, 
            width:60},
        {key:"strainSpecies", 
            label:"Strain/Species", 
            sortable:false, 
            width:90},
        {key:"description", 
            label:"Description From<br/>Sequence Provider", 
            sortable:false, 
            width:300},
        {key:"cloneCollection", 
            label:"Clone<br/>Collection", 
            sortable:false, 
            width:120},
        {key:"markerSymbol", 
            label:"Marker<br/>Symbol", 
            sortable:false, 
            width:100}
    ];

    // DataSource instance (global)
    myDataSource = new YAHOO.util.XHRDataSource(fewiurl + "sequence/json?" + querystring + "&");

    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
    myDataSource.responseSchema = {
        resultsList: "summaryRows",
        fields: [
            {key:"seqForward"},
            {key:"seqInfo"},
            {key:"seqType"},
            {key:"length"},
            {key:"strainSpecies"},
            {key:"description"},
            {key:"cloneCollection"},
            {key:"markerSymbol"}
        ],
        metaFields: {
            totalRecords: "totalCount"
        }
    };

    // Create the Paginator
    var myPaginator = new YAHOO.widget.Paginator({
        template : "{FirstPageLink} {PreviousPageLink}<strong>{PageLinks}</strong> {NextPageLink} {LastPageLink} <span style=align:right;>{RowsPerPageDropdown}</span><br/>{CurrentPageReport}",
        pageReportTemplate : "Showing sequence(s) {startRecord} - {endRecord} of {totalRecords}",
        rowsPerPageOptions : [10,25,50,100],
        containers   : ["paginationTop", "paginationBottom"],
        rowsPerPage : 25,
        pageLinks: 3,
        recordOffset: 1
    });

    // DataTable configurations
    var myConfigs = {
        paginator : myPaginator,
        dynamicData : true,
        draggableColumns : true,
        initialLoad : false
    };  
    
    // DataTable instance (global)
    myDataTable = new YAHOO.widget.DataTable("dynamicdata", myColumnDefs, myDataSource, myConfigs);
    
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
        var newState = generateRequest(state.recordOffset, 0, 0, state.rowsPerPage);

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

        return true;
    };

    // Returns a request string for consumption by the DataSource (global)
    generateRequest = function(startIndex,sortKey,dir,results) {
    	startIndex = startIndex || 0;
        sortKey   = sortKey || "seqType";
        dir   = (dir) ? dir.substring(7) : "asc"; // Converts from DataTable format "yui-dt-[dir]" to server value "[dir]"
        results   = results || 25;
        return "results="+results+"&startIndex="+startIndex+"&sort="+sortKey+"&dir="+dir + filters.getUrlFragment();
    };

    // Called by Browser History Manager to trigger a new state (global)
    handleHistoryNavigation = function (request) {
    	myDataTable.showTableMessage(myDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);
    	
    	// Sends a new request to the DataSource
        myDataSource.sendRequest(request,{
            success : myDataTable.onDataReturnSetRows,
            failure : myDataTable.onDataReturnSetRows,
            scope : myDataTable,
            argument : {} // Pass in container for population at runtime via doBeforeLoadData
        });
	    filters.populateFilterSummary();
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

/* not sure why filtering causes the screen to scoll down toward the bottom of the results table, but
 * we use this function to bring it back up
 */
function scrollUp() {
	setTimeout(function() { window.scrollTo(0, $('#titleBarWrapper').offset().top); }, 250);
}
