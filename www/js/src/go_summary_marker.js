var pageSortKey = "isoforms";
var pageSortDir = "yui-dt-desc";
var pageDataTable = null;
var pageDataSource = null;

var History = YAHOO.util.History;

function getPageDataTable() {
    // return a reference to the YUI DataTable
    return pageDataTable;
};

function getPageDataSource() {
	return pageDataSource;
}

function getPageSortKey() {
	return pageSortKey;
};

function getPageSortDir() {
	return pageSortDir;
};

function setPageSort(sort, dir) {
	pageSortKey = sort;
	pageSortDir = dir;
};

var generatePageRequest = function(startIndex,sortKey,sortDir,results) {
	startIndex = startIndex || 0;
	sortKey = sortKey || pageSortKey;
	sortDir = (sortDir) ? sortDir.substring(7) : "desc"; // Converts from DataTable format "yui-dt-[dir]" to server value "[dir]"
	results = results || 100;

	var filterString = "";
	try {
		filterString = filters.getUrlFragment();
		if (filterString) {
			filterString = '&' + filterString;
		}
	} catch (e) {}

	return "results=" + results + "&startIndex=" + startIndex + "&sort=" + sortKey + "&dir=" + sortDir + filterString;
};

var handleNavigationRaw = function (request) {

	pageDataTable.showTableMessage(pageDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);

	// Sends a new request to the DataSource
	pageDataSource.sendRequest(request,{
		success : pageDataTable.onDataReturnSetRows,
		failure : pageDataTable.onDataReturnSetRows,
		scope : pageDataTable,
		argument : {} // Pass in container for population at runtime via doBeforeLoadData
	});
};

var scrollToTableTop = function(count) {
    if (count == null) { count = 0; }

    var x = document.getElementById('tableTop');
    if (x != null) {
	if (Math.abs(x.getBoundingClientRect().bottom) > 1.0) {
	    x.scrollIntoView();
	}
    }

    if (count < 40) {
	setTimeout( function() {
	    scrollToTableTop(count + 1);
	}, 25);
    }
};

var handleNavigation = function(request) {
	if (!request) {
		request = getQuerystring();
	}

	var pRequest = parsePageRequest(request);
	filters.setAllFilters(pRequest);

	var dataTable = getPageDataTable();
	dataTable.showTableMessage(dataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);

	pageDataSource.sendRequest (request, {
		success : dataTable.onDataReturnSetRows,
		failure : dataTable.onDataReturnSetRows,
		scope : dataTable,
		argument : {} // Pass in container for population at runtime via doBeforeLoadData
	});

	filters.populateFilterSummary();
};


function parsePageRequest(request) {
	var reply = [];
	var kvPairs = request.split('&');
	for (pair in kvPairs) {
		var kv = kvPairs[pair].split('=');
		reply[kv[0]] = kv[1];
	}
	return reply;
};

function main() {
	// Column definitions -- sortable:true enables sorting
	// These are our actual columns, in the default ordering.

	var myColumnDefs = [
		{
			key:"category",
			label:"Aspect",
			width:130,
			sortable:true
		}, {
			key:"headers",
			label:"Category",
			width:150,
			sortable:true
		}, {
			key:"term",
			label:"Classification Term",
			width:240,
			sortable:true
		}, {
			key:"annotationExtensions",
			label:"Context",
			width:200,
			sortable:false
		}, {
			key:"isoforms",
			label:"Proteoform",
			width:130,
			sortable:true
		}, {
			key:"evidence",
			label:"Evidence",
			width:55,
			sortable:true
		}, {
			key:"inferred",
			label:"Inferred From",
			width:200,
			sortable:false
		}, {
			key:"references",
			label:"Reference(s)",
			width:200,
			sortable:true
		}
	];

	// DataSource instance
	var myDataSource = new YAHOO.util.DataSource(fewiurl + "go/json?" + getQuerystring() + "&");

	myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
	myDataSource.responseSchema = {
		resultsList: "summaryRows",
		fields: [
		    {key:"isoforms"},
			{key:"category"},
			{key:"headers"},
			{key:"term"},
			{key:"evidence"},
			{key:"annotationExtensions"},
			{key:"inferred"},
			{key:"references"}
		],
		metaFields: {
			totalRecords: "totalCount"
		}
	};

	pageDataSource = myDataSource;


	// Create the Paginator
	var myPaginator = new YAHOO.widget.Paginator({
		template : "{FirstPageLink} {PreviousPageLink}<strong>{PageLinks}</strong> {NextPageLink} {LastPageLink} <span style=align:right;>{RowsPerPageDropdown}</span><br/>{CurrentPageReport}",
		pageReportTemplate : "Showing items {startRecord} - {endRecord} of {totalRecords}",
		rowsPerPageOptions : [100, 500, 1000],
		rowsPerPage : 100,
		containers	: ["paginationTop", "paginationBottom"],
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
	var myDataTable = new YAHOO.widget.DataTable("dynamicdata", myColumnDefs, myDataSource, myConfigs);

	pageDataTable = myDataTable;

	// Show loading message while page is being rendered
	myDataTable.showTableMessage(myDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);

	// Define a custom function to route sorting through the Browser History Manager
	var handleSorting = function (oColumn) {
		// Calculate next sort direction for given Column
		var sDir = this.getColumnSortDir(oColumn);

		// The next state will reflect the new sort values
		// while preserving existing pagination rows-per-page
		// As a best practice, a new sort will reset to page 0
		var newState = generatePageRequest(0, oColumn.key, sDir, this.get("paginator").getRowsPerPage());

		setPageSort(oColumn.key, sDir);

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

		var newState = generatePageRequest(state.recordOffset, sCol, sDir, state.rowsPerPage);

		myPaginator.setState(newState);

		handleNavigationRaw(newState);
	};
	// First we must unhook the built-in mechanism...
	myPaginator.unsubscribe("changeRequest", myDataTable.onPaginatorChangeRequest);
	// ...then we hook up our custom function
	myPaginator.subscribe("changeRequest", handlePagination, myDataTable, true);

	// Update payload data on the fly for tight integration with latest values from server
	myDataTable.doBeforeLoadData = function(oRequest, oResponse, oPayload) {

		var pRequest = parsePageRequest(oRequest);
		var meta = oResponse.meta;

		if (meta) {
			oPayload.totalRecords = meta.totalRecords || oPayload.totalRecords;
		} else {
			oPayload.totalRecords = oPayload.totalRecords;
		}
		oPayload.pagination = {
			rowsPerPage: Number(pRequest['results']) || 100,
			recordOffset: Number(pRequest['startIndex']) || 0
		};

		var reportButton = YAHOO.util.Dom.get('textDownload');
		if (!YAHOO.lang.isNull(reportButton)){
			facetQuery = generatePageRequest(0, 'term', 'asc', oPayload.totalRecords);
			reportButton.setAttribute('href', fewiurl + 'go/report.txt?' + querystring + '&' + facetQuery);
		}
		reportButton = YAHOO.util.Dom.get('excelDownload');
		if (!YAHOO.lang.isNull(reportButton)){
			facetQuery = generatePageRequest(0, 'term', 'asc', oPayload.totalRecords);
			reportButton.setAttribute('href', fewiurl + 'go/report.xlsx?' + querystring + '&' + facetQuery);
		}

		oPayload.sortedBy = {
			key: pRequest['sort'] || pageSortKey,
			dir: pRequest['dir'] ? "yui-dt-" + pRequest['dir'] : pageSortDir // Convert from server value to DataTable format
		};

		return true;
	};

//	// Called by Browser History Manager to trigger a new state
//	var handleHistoryNavigation = function (request) {
//		// Sends a new request to the DataSource
//		if (!request) {
//			request = getQuerystring();
//		}
//
//		var pRequest = parseRequest(request);
//		filters.setAllFilters(pRequest);
//
//		var dataTable = getPageDataTable();
//		dataTable.showTableMessage(dataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);
//
//		myDataSource.sendRequest(request,{
//			success : dataTable.onDataReturnSetRows,
//			failure : dataTable.onDataReturnSetRows,
//			scope : dataTable,
//			argument : {} // Pass in container for population at runtime via doBeforeLoadData
//		});
//
//		filters.populateFilterSummary();
//	};
//	handleNavigationGlobal = handleHistoryNavigation;

	var initialRequest = generatePageRequest();

	handleNavigationRaw(initialRequest);


};
main();

