// functions in this module are named with an "is_" prefix, as an abbreviation
// for "interaction_summary"

var is_sortValue = "validation";
var is_sortDir = "yui-dt-asc";
var is_dataTable = null;
var is_dataSource = null;

// Integrate with Browser History Manager (shortcut here for convenience)
var History = YAHOO.util.History;

function is_log(msg) {
    // log a message to the browser console
    setTimeout(function() { throw new Error(msg); }, 0);
}

function is_getDataTable() {
    // return a reference to the YUI DataTable
    return is_dataTable;
}

function is_getSort() {
    // return the global sort value
    return is_sortValue;
}

function is_getSortDir() {
    // return the global sort direction
    return is_sortDir;
}

function is_setSort(sort, dir) {
    // remember the given sort value globally
    is_sortValue = sort;
    is_sortDir = dir;
}

// Returns a request string for consumption by the DataSource
var is_generateRequest = function(startIndex,sortKey,dir,results) {
    startIndex = startIndex || 0;
    sortKey   = sortKey || "validation";
    dir   = (dir) ? dir.substring(7) : "asc"; // Converts from DataTable format "yui-dt-[dir]" to server value "[dir]"
    results   = results || 25;

    var filterString = "";

    try {
	filterString = filters.getUrlFragment();
	if (filterString) {
	    filterString = '&' + filterString;
	}
    } catch (e) {}

    return "results="+results+"&startIndex="+startIndex+"&sort="+sortKey+"&dir="+dir + filterString;
};

// Called by Browser History Manager to trigger a new state
//var handleHistoryNavigation = function (request) {
var handleNavigationRaw = function (request) {
    	
    	is_dataTable.showTableMessage(is_dataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);
    	
    	// Sends a new request to the DataSource
        is_dataSource.sendRequest(request,{
            success : is_dataTable.onDataReturnSetRows,
            failure : is_dataTable.onDataReturnSetRows,
            scope : is_dataTable,
            argument : {} // Pass in container for population at runtime via doBeforeLoadData
        });

    };

function is_main() {
    // Column definitions -- sortable:true enables sorting
    // These are our actual columns, in the default ordering.

    var myColumnDefs = [
        {key:"linkedOrganizer", 
            label:"Feature 1",
            width:75, 
	    resizeable:true,
            sortable:true},
        {key:"relationshipTerm", 
            label:"Interaction",
            width:130, 
	    resizeable:true,
            sortable:true},
        {key:"linkedParticipant", 
            label:"Feature 2",
            width:75, 
	    resizeable:true,
            sortable:true},
        {key:"validation", 
            label:"Validation", 
            width:85,
	    resizeable:true,
            sortable:true}, 
        {key:"dataSource", 
            label:"Data Source", 
            width:80,
	    resizeable:true,
            sortable:true}, 
        {key:"score", 
            label:"Score", 
            width:50,
	    resizeable:true,
            sortable:true}, 
        {key:"reference", 
            label:"Reference", 
            width:70,
	    resizeable:true,
            sortable:true}
    ];

    // DataSource instance
    // assumes getQuerystring() is defined in the JSP itself, so we can get the
    // current list of expanded marker IDs
    var myDataSource = new YAHOO.util.XHRDataSource(fewiurl + "interaction/json?" + getQuerystring() + "&");

    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
    myDataSource.responseSchema = {
        resultsList: "summaryRows",
        fields: [
            {key:"linkedOrganizer"},
            {key:"relationshipTerm"},
            {key:"linkedParticipant"},
            {key:"validation"},
            {key:"reference"},
            {key:"dataSource"},
            {key:"score"}
        ],
        metaFields: {
            totalRecords: "totalCount"
        }
    };

    // store data source in a global
    is_dataSource = myDataSource;

    // Create the Paginator
    var myPaginator = new YAHOO.widget.Paginator({
        template : "{FirstPageLink} {PreviousPageLink}<strong>{PageLinks}</strong> {NextPageLink} {LastPageLink} <span style=align:right;>{RowsPerPageDropdown}</span><br/>{CurrentPageReport}",
        pageReportTemplate : "Showing interaction(s) {startRecord} - {endRecord} of {totalRecords}",
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
        draggableColumns : false,
        initialLoad : false
    };  
    
    // DataTable instance
    var myDataTable = new YAHOO.widget.DataTable("dynamicdata", myColumnDefs, 
    	    is_dataSource, myConfigs);
    
    // save in a global variable
    is_dataTable = myDataTable;

    // Show loading message while page is being rendered
    myDataTable.showTableMessage(myDataTable.get("MSG_LOADING"), 
    	    YAHOO.widget.DataTable.CLASS_LOADING);    
    	    
    // Define a custom function to route sorting through the Browser History Manager
    var handleSorting = function (oColumn) {
        // Calculate next sort direction for given Column
        var sDir = this.getColumnSortDir(oColumn);
        
        // The next state will reflect the new sort values
        // while preserving existing pagination rows-per-page
        // As a best practice, a new sort will reset to page 0
        var newState = is_generateRequest(0, oColumn.key, sDir, 
                this.get("paginator").getRowsPerPage());

	is_setSort(oColumn.key, sDir);
	updateGraphBasedOnSorting();
        is_updateBatchQueryLink();

        // Pass the state along to the Browser History Manager
        //History.navigate("myDataTable", newState);
	handleNavigationRaw(newState);
    };
    myDataTable.sortColumn = handleSorting;


    // Define a custom function to route pagination through the Browser History Manager
    var handlePagination = function(state) {
        // The next state will reflect the new pagination values
        // while preserving existing sort values
        // Note that the sort direction needs to be converted from DataTable format to server value
	 
	var tableState = this.getState();

	var sDir = tableState.sortedBy.dir;
	var sCol = tableState.sortedBy.key;

        var newState = is_generateRequest(state.recordOffset, sCol, sDir, state.rowsPerPage);

        myPaginator.setState(newState);
        // Pass the state along to the Browser History Manager
        //History.navigate("myDataTable", newState);
	handleNavigationRaw(newState);
    };
    // First we must unhook the built-in mechanism...
    myPaginator.unsubscribe("changeRequest", myDataTable.onPaginatorChangeRequest);
    // ...then we hook up our custom function
    myPaginator.subscribe("changeRequest", handlePagination, myDataTable, true);

    // Update payload data on the fly for tight integration with latest values from server 
    myDataTable.doBeforeLoadData = function(oRequest, oResponse, oPayload) {

        var pRequest = is_parseRequest(oRequest);
        var meta = oResponse.meta;

	if (meta) {
            oPayload.totalRecords = meta.totalRecords || oPayload.totalRecords;
	} else {
            oPayload.totalRecords = oPayload.totalRecords;
	}
        oPayload.pagination = {
            rowsPerPage: Number(pRequest['results']) || 25,
            recordOffset: Number(pRequest['startIndex']) || 0
        };

	var sortKey = pRequest['sort'] || 'validation';
	if (typeof(sortKey) !== 'string') {
	    sortKey = is_sortValue;
	}

        oPayload.sortedBy = {
            key: sortKey,
            dir: pRequest['dir'] ? "yui-dt-" + pRequest['dir'] : "yui-dt-asc" // Convert from server value to DataTable format
        };

        return true;
    };

    // Calculate the first request
    var initialRequest = is_generateRequest();
//    var initialRequest = History.getBookmarkedState("myDataTable") || // Passed in via URL
//                       is_generateRequest(); // Get default values

    // Register the module
//    History.register("myDataTable", initialRequest, handleHistoryNavigation);

    // Render the first view
//    History.onReady(function() {
        // Current state after BHM is initialized is the source of truth for what state to render
//        var currentState = History.getCurrentState("myDataTable");
//        handleHistoryNavigation(currentState);
//    });
    handleNavigationRaw(initialRequest);

    // Initialize the Browser History Manager.
//    History.initialize("yui-history-field", "yui-history-iframe");
};
is_main();

function is_parseRequest(request){
	var reply = [];
	var kvPairs = request.split('&');
	for (pair in kvPairs) {
		var kv = kvPairs[pair].split('=');
		reply[kv[0]] = kv[1];
	}
	return reply;
}

/* update the data table according to the parameters encoded in the given
 * 'request'
 */
var is_handleNavigation = function(request) {
    is_log("entered is_handleNavigation");
    
    if (!request) {
	request = getQuerystring();
    }

    is_log("received request : " + request);

    var pRequest = is_parseRequest(request);
    filters.setAllFilters(pRequest);

    is_log("set filters");

    var dataTable = is_getDataTable();
    dataTable.showTableMessage(dataTable.get("MSG_LOADING"),
	YAHOO.widget.DataTable.CLASS_LOADING);

    is_log("updated table message");

    is_dataSource.sendRequest (request, {
        success : dataTable.onDataReturnSetRows,
        failure : dataTable.onDataReturnSetRows,
        scope : dataTable,
        argument : {} // Pass in container for population at runtime via doBeforeLoadData
    });
    is_log("sent request");

    filters.populateFilterSummary();
    is_log("exiting is_handleNavigation(" + request + ")");
};

var is_fetchAndCall = function(url, callbackFn) {
    // get the contents at 'url' via ajax, then pass the contents to the given
    // callback function.  The callback function should handle a null value
    // for cases where the fetch fails.
 
    var callback = {
	success : function(oResponse) {
		var oResults = null;

		// Try to evaluate it as a JSON string and convert it to 
		// Javascript objects.  If that fails, just pass along the
		// string itself.
		try {
		    oResults = eval("(" + oResponse.responseText + ")");
		} catch (err) {
		    oResults = oResponse.responseText;
		}
		oResponse.argument.callbackFn(oResults);
		  },
	failure : function(oResponse) {
		oResponse.argument.callbackFn(null);
		  },
	argument: {
		'url' : url,
		'callbackFn' : callbackFn
		  },
	timeout: 10000
    };
    YAHOO.util.Connect.asyncRequest('GET', url, callback);
};

var is_updateBatchQueryLink = function() {
    var url = fewiurl + 'interaction/idList?' + getQuerystring() + '&sort='
	+ is_getSort() + '&dir=' + is_getSortDir();
    is_fetchAndCall(url, is_setButtonSymbolListWeb);
};

var is_setButtonTooltip = function(s) {
    var el = document.getElementById('ids');
    if (el) {
	var tooltip = 'Forward genome features to Batch Query';

	try {
	    var rowCount = is_dataTable.configs.paginator.getTotalRecords();
	    var rowCountStr = rowCount + ' interactions';
	    if (rowCount == 1) {
		rowCountStr = '1 interaction';
	    }

	    var idCount = s.split(',').length - 1;	// trailing comma
	    if (idCount > 0) {
		if (idCount > 1) {
		    tooltip = 'Forward ' + idCount
			+ ' distinct genome features (from '
			+ rowCountStr + ') to Batch Query';
		} else {
		    tooltip = 'Forward 1 genome feature (from '
			+ rowCountStr + ') to Batch Query';
		}
	    }
	} catch (e) {
	    // fallback position in case of error (no counts)
	    tooltip = 'Forward genomes features (from interactions) '
		    + 'to Batch Query';
	}

	var el2 = document.getElementById('toBatchQuery');
	if (el2) {
	    el2.title = tooltip;
	}

	var el3 = document.getElementById('toMouseMine');
	if (el3) {
	    el3.title = tooltip.replace('Batch Query', 'MouseMine');
	}
    }
};

var is_setButtonSymbolListWeb = function(s) {
    if ((s === null) || (s == '')) {
	// if can't get the full list, fall back on the main marker(s) for the
	// page
	s = markerIDs.join(', ');
    }

    var el = document.getElementById('ids');
    if (el) {
	el.value = s;
	var el2 = document.getElementById('toBatchQuery');
	if (el2) {
	    el2.style.display = 'inline';
	}
    }

    var el3 = document.getElementById('mousemineids');
    if (el3) {
	el3.value = s;
	var el4 = document.getElementById('toMouseMine');
	if (el4) {
	    el4.style.display = 'inline';
	}
    }

    // use a 100ms delay before updating the tooltip, to give the paginator
    // time to work
    setTimeout(function() { is_setButtonTooltip(s); }, 100);
};
