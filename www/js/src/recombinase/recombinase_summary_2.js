// third section of Javascript to be filled in the recombinase_summary.jsp
// (There are 3 sections because HTML markup goes in between them.)
// This file contains functions to reset the checkboxes at the top of the
// page, to define and display the DataTable, and to create its Tooltips.

var recombinaseDataTable = null;
var recombinaseDataSource = null;

function getRecombinaseDataTable() {
    return recombinaseDataTable;
}
function getRecombinaseDataSource() {
	return recombinaseDataSource;
}

// Returns a request string for consumption by the DataSource
var generateRequest = function(startIndex,sortKey,dir,results) {
	startIndex = startIndex || 0;
	sortKey   = sortKey || "driver";
	dir   = (dir) ? dir.substring(7) : "asc"; // Converts from DataTable format "yui-dt-[dir]" to server value "[dir]"
	results   = results || 25;

	var filterString = "";
	try {
		filterString = filters.getUrlFragment();
		if (filterString) {
			filterString = '&' + filterString;
		}
	} catch (e) {}
	return "results="+results+"&startIndex="+startIndex+"&sort="+sortKey+"&dir="+dir+filterString;
};

var handleNavigationRaw = function (request) {
	recombinaseDataTable.showTableMessage(recombinaseDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);

	// Sends a new request to the DataSource
	recombinaseDataSource.sendRequest(request,{
		success : recombinaseDataTable.onDataReturnSetRows,
		failure : recombinaseDataTable.onDataReturnSetRows,
		scope : recombinaseDataTable,
		argument : {} // Pass in container for population at runtime via doBeforeLoadData
	});
};

function main() {
    // Column definitions -- sortable:true enables sorting
    // These are our actual columns, in the default ordering.
    var myColumnDefs = [
        {key:"driver",
            label:"Driver",
            sortable:true},
        {key:"gridLink",
            label:"Matrix<br/>View",
			sortable:false,
			width:35},
        {key:"nomenclature",
            label:"Allele Symbol<br/>Gene; Allele Name",
			sortable:true,
			width:245},
		{key:"detectedSystems",
			label:"Recombinase Activity<br/>Detected",
			sortable:true,
//			sortOptions: { defaultDir: YAHOO.widget.DataTable.CLASS_DESC },
			width:220},
		{key:"notDetectedSystems",
			label:"Recombinase Activity<br/>Not Detected",
			sortable:true,
//			sortOptions: { defaultDir: YAHOO.widget.DataTable.CLASS_DESC },
			width:220},
        {key:"inducibleNote",
            label:"Induced By",
            width:88,
            sortable:true},
        {key:"imsrCount",
            label:"Find Mice<br/>(IMSR)",
            width:60,
            sortable:true},
        {key:"countOfReferences",
            label:"Refs",
            width:36,
            sortable:true},
        {key:"synonyms",
            label:"Allele Synonym",
            width:170,
            sortable:false}

    ];

    // DataSource instance
    var myDataSource = new YAHOO.util.DataSource(fewiurl + "recombinase/json?" + querystring + "&");

    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
    myDataSource.responseSchema = {
        resultsList: "summaryRows",
        fields: [
			{key:"driver"},
			{key:"gridLink"},
			{key:"nomenclature"},
            {key:"detectedSystems"},
            {key:"notDetectedSystems"},
            {key:"inducibleNote"},
            {key:"imsrCount"},
            {key:"countOfReferences"},
            {key:"synonyms"}
        ],
        metaFields: {
            totalRecords: "totalCount",
            paginationRecordOffset : "startIndex",
            paginationRowsPerPage : "pageSize",
            sortKey: "sort",
            sortDir: "dir"
        }
    };

    // save in a global variable
	recombinaseDataSource = myDataSource;

    // Create the Paginator
    var myPaginator = new YAHOO.widget.Paginator({
        template : "{FirstPageLink} {PreviousPageLink}<strong>{PageLinks}</strong> {NextPageLink} {LastPageLink} <span style=align:right;>{RowsPerPageDropdown}</span><br/>{CurrentPageReport}",
        pageReportTemplate : "Showing items {startRecord} - {endRecord} of {totalRecords}",
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

    // DataTable instance
    var myDataTable = new YAHOO.widget.DataTable("dynamicdata", myColumnDefs,
    	    myDataSource, myConfigs);

    // save in a global variable
    recombinaseDataTable = myDataTable;


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
            key: pRequest['sort'] || "driver",
            dir: pRequest['dir'] ? "yui-dt-" + pRequest['dir'] : "yui-dt-asc" // Convert from server value to DataTable format
        };
        return true;
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

//showColumn(YAHOO.mgiData.selectedSystem);

// activate tooltips in span tags in the table (all can share a common
// Tooltip object and pick up their contents from the Spans' title attributes
YAHOO.mgiData.myTip = new YAHOO.widget.Tooltip ("mgiTip", {
		context : YAHOO.util.Selector.query("span", YAHOO.mgiData.myDataTable) });




(function(){

	// toggle facetHelp for certain filters
	$(".filterButton").click(function(e){

		var $filterEl = $(this);
		var filterId = $filterEl.attr("id");

		// only toggle facetHelp for the two allele systems filters

		if (filterId == "systemNotDetectedFilter"
			|| filterId == "systemDetectedFilter") {
			$(".facetHelp").show();
		}
		else {
			$(".facetHelp").hide();
		}
	});

})();
