function main() {
    // Column definitions -- sortable:true enables sorting
    // These are our actual columns, in the default ordering.

    var myColumnDefs = [
        {key:"accId", 
            label:"<b>Accession ID</b>",
            width:220, 
            sortable:false},
        {key:"logicalDb", 
            label:"<b>Database</b>",
            width:150, 
            sortable:false},
        {key:"mgiLink", 
            label:"<b>MGI Links</b>",
            width:150, 
            sortable:false},
        {key:"displayType", 
            label:"<b>Type</b>",
            width:100, 
            sortable:false},
        {key:"description", 
            label:"Description",
            width:350, 
            sortable:false}
    ];

    // DataSource instance
    var myDataSource = new YAHOO.util.DataSource("${configBean.FEWI_URL}accession/json?${queryString}&");

    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
    myDataSource.responseSchema = {
        resultsList: "summaryRows",
        fields: [
            {key:"accId"},
            {key:"logicalDb"},
            {key:"mgiLink"},
            {key:"displayType"},
            {key:"description"}
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

    // Returns a request string for consumption by the DataSource
    var generateRequest = function(startIndex,sortKey,dir,results) {
    	startIndex = startIndex || 0;
        sortKey   = sortKey || "field1";
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

