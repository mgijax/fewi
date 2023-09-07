function main() {
    // Column definitions -- sortable:true enables sorting
    // These are our actual columns, in the default ordering.

    var myColumnDefs = [
        {key:"marker",
            label:"Symbol, Name",
            width:260,
            sortable:false},
        {key:"chromosome",
            label:"Chr",
            width:25,
            sortable:false},
        {key:"term",
            label:"Annotated Term",
            width:200,
            sortable:false},
        {key:"annotationExtensions",
            label:"Context",
            width:230,
            sortable:false},
        {key:"isoforms",
            label:"Proteoform",
            width:130,
            sortable:false},
        {key:"evidence",
            label:"Evidence",
            width:50,
            sortable:false},
        {key:"inferred",
            label:"Inferred From",
            width:130,
            sortable:false},
		{key:"references",
			label:"Reference(s)",
			width:180,
			sortable:false}
    ];

    // DataSource instance
    var myDataSource = new YAHOO.util.DataSource(fewiurl + "go/json?" + querystring + "&");

    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
    myDataSource.responseSchema = {
        resultsList: "summaryRows",
        fields: [
            {key:"marker"},
            {key:"isoforms"},
            {key:"chromosome"},
            {key:"term"},
			{key:"annotationExtensions"},
			{key:"evidence"},
            {key:"inferred"},
            {key:"references"}
        ],
        metaFields: {
            totalRecords: "totalCount"
        }
    };

    // Create the Paginator
    var myPaginator = new YAHOO.widget.Paginator({
        template : "{FirstPageLink} {PreviousPageLink}<strong>{PageLinks}</strong> {NextPageLink} {LastPageLink} <span style=align:right;>{RowsPerPageDropdown}</span><br/>{CurrentPageReport}",
        pageReportTemplate : "Showing items {startRecord} - {endRecord} of {totalRecords}",
        rowsPerPageOptions : [100, 500, 1000],
        rowsPerPage : 100,
        containers   : ["paginationTop", "paginationBottom"],
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

	if (oResponse != null) {
            oPayload.totalRecords = oResponse.meta.totalRecords || oPayload.totalRecords;
	}

        oPayload.pagination = {
            rowsPerPage: Number(pRequest['results']) || 100,
            recordOffset: Number(pRequest['startIndex']) || 0
        };
        var reportButton = YAHOO.util.Dom.get('textDownload');
        if (!YAHOO.lang.isNull(reportButton)){
        	facetQuery = generateRequest(0, 'term', 'asc', oPayload.totalRecords);
	        reportButton.setAttribute('href', fewiurl + 'go/report.txt?' + querystring + '&' + facetQuery);
        }
        reportButton = YAHOO.util.Dom.get('excelDownload');
        if (!YAHOO.lang.isNull(reportButton)){
        	facetQuery = generateRequest(0, 'term', 'asc', oPayload.totalRecords);
	        reportButton.setAttribute('href', fewiurl + 'go/report.xlsx?' + querystring + '&' + facetQuery);
        }

        return true;
    };

    // Returns a request string for consumption by the DataSource
    var generateRequest = function(startIndex,sortKey,dir,results) {
    	startIndex = startIndex || 0;
        sortKey   = sortKey || "term";
        dir   = (dir) ? dir.substring(7) : "asc"; // Converts from DataTable format "yui-dt-[dir]" to server value "[dir]"
        results   = results || 100;
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

