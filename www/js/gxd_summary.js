// The YUI components required for setting up the data table and 
// source to do the AJAX call
var gxdDataTable;
var gxdDataSource;
var defaultSort = "";

// Shortcut variable for the YUI history manager
var History = YAHOO.util.History;

// HTML/YUI page widgets
YAHOO.namespace("gxd.container"); 
 
// GXD table tab control
YAHOO.widget.Tab.prototype.ACTIVE_TITLE = '';
var resultsTabs = new YAHOO.widget.TabView("resultSummary");

// Global defintion of summary tab mappings of index and history manager parameter
var tabs = {};
tabs["genestab"] = 0;
tabs["assaystab"] = 1;
tabs["resultstab"] = 2;
tabs[0] = "genestab";
tabs[1] = "assaystab";
tabs[2] = "resultstab";

// return the current tab from the history manager
var getCurrentTab = function() {
	var activeIndex = resultsTabs.get("activeIndex");
	return tabs[activeIndex];
};

//default page size for each summary
var GENES_PAGE_SIZE = 100;
var ASSAYS_PAGE_SIZE = 100;
var RESULTS_PAGE_SIZE = 100;
// a global variable to help the tab change handler know when to fire off a new query
var newQueryState = false;

// tab change handler
function handleTabViewActiveTabChange (e) {

	var currentTabState = getCurrentTab();

	var PAGE_SIZE = RESULTS_PAGE_SIZE;
	if(currentTabState == "genestab") PAGE_SIZE = GENES_PAGE_SIZE;
	if(currentTabState == "assaystab") PAGE_SIZE = ASSAYS_PAGE_SIZE;

	
	var currentRequest = History.getCurrentState("gxd");
	var currentValues = parseRequest(currentRequest);
	if(currentValues["tab"])
	{
		if(currentValues["tab"] == currentTabState)
		{
			// no tab change is happening
			// do we have a new query?
			if(newQueryState)
			{
				// this is the only time we don't want to remember the pagination.
				//newQueryState = false;
				newRequest = generateRequest(null, 0, PAGE_SIZE);
			}
			else
			{
				// no new query, save the pagination.
				var sortBy = {key:currentValues["sort"][0] || defaultSort,dir:currentValues["dir"][0] || "asc"};
				newRequest = generateRequest(sortBy,currentValues["startIndex"][0],currentValues["results"][0]);
			}
		}
		else
		{
			//newQueryState = false;
			// tab change
			newRequest = generateRequest(null, 0, PAGE_SIZE);
		}
	}
	else
	{
		//newQueryState = false;
		// no previous tabs
		newRequest = generateRequest(null, 0, PAGE_SIZE);
	}
	newQueryState = false;
	if(newRequest == currentRequest) {
		handleNavigation(newRequest, true);
	} else {
		History.navigate("gxd", newRequest);
	}
}
resultsTabs.addListener("activeTabChange", handleTabViewActiveTabChange);

// refresh all three counts in each tab via AJAX
function refreshTabCounts()
{
	 //get the tab counts via ajax
	var handleResultsCount = function(o)
	{
		if(o.responseText == "-1") o.responseText = "0";
		YAHOO.util.Dom.get("totalResultsCount").innerHTML = o.responseText;
	};
	var handleAssaysCount = function(o)
	{
		if(o.responseText == "-1") o.responseText = "0";
		YAHOO.util.Dom.get("totalAssaysCount").innerHTML = o.responseText;
	};
	var handleGenesCount = function(o)
	{
		if(o.responseText == "-1") o.responseText = "0";
		YAHOO.util.Dom.get("totalGenesCount").innerHTML = o.responseText;
	};
	// Turned off until we can figure out how to get better performance on large group queries
//    var handleTotalCounts = function(o)
//    {
//    	var response = YAHOO.lang.JSON.parse(o.responseText);
//
//    	//set genes count
//    	if(response["genesCount"]==-1) response["genesCount"] = 0;
//    	YAHOO.util.Dom.get("totalGenesCount").innerHTML = response["genesCount"];
//    	
//    	//set assays count
//    	
//    	//set results count
//    	if(response["resultsCount"]==-1) response["resultsCount"] = 0;
//    	YAHOO.util.Dom.get("totalResultsCount").innerHTML = response["resultsCount"];
//    };
	// clear these until the data comes back
	YAHOO.util.Dom.get("totalResultsCount").innerHTML = "";
	YAHOO.util.Dom.get("totalAssaysCount").innerHTML = "";
	YAHOO.util.Dom.get("totalGenesCount").innerHTML = "";
    YAHOO.util.Connect.asyncRequest('GET', fewiurl+"gxd/results/totalCount?"+querystring,
    {	success:handleResultsCount,
    	failure:function(o){}
    },null);
    YAHOO.util.Connect.asyncRequest('GET', fewiurl+"gxd/assays/totalCount?"+querystring,
    	    {	success:handleAssaysCount,
    	    	failure:function(o){}
    	    },null);
    YAHOO.util.Connect.asyncRequest('GET', fewiurl+"gxd/markers/totalCount?"+querystring,
    	    {	success:handleGenesCount,
    	    	failure:function(o){}
    	    },null);
}

function refreshGxdLitLink()
{
	// Fire off an AJAX call to generate the link
	var handleGxdLitCount = function(o)
	{
		// "-1" is our flag that the query parameters selected do not apply to GXD Lit.
		if(o.responseText != "-1")
		{
			var gxdLitUrl = fewiurl+"gxd/gxdLitForward?"+querystring;
			YAHOO.util.Dom.get("gxdLitInfo").innerHTML = "<a href=\""+gxdLitUrl+"\">"+o.responseText+"</a>"+
				" Gene Expression Literature Records match your query.";
		}
	};
	
	YAHOO.util.Dom.get("gxdLitInfo").innerHTML = "";
	YAHOO.util.Connect.asyncRequest('GET', fewiurl+"gxd/gxdLitCount?"+querystring,
		    {	success:handleGxdLitCount,
		    	failure:function(o){}
		    },null);
}


/*
 * Some helper functions for dealing with YUI history manager
 * and building AJAX requests
 */

//
//Parse the URL parameters into key value pairs and return the 
//array
//
function parseRequest(request){
	var reply = {};
	var kvPairs = request.split('&');
	for (pair in kvPairs) {
		var kv = kvPairs[pair].split('=');
		if(!reply[kv[0]]){
			reply[kv[0]] = [];
		}
		reply[kv[0]].push(kv[1]);
	}
	return reply;
};


//Returns a request string for consumption by the DataSource
var generateRequest = function(sortedBy, startIndex, results, facets) {
	var sort = defaultSort;
	var dir = 'asc';

	if (!YAHOO.lang.isUndefined(sortedBy) && !YAHOO.lang.isNull(sortedBy)){
		sort = sortedBy['key'];
		dir = sortedBy['dir'];
	}
	// Converts from DataTable format "yui-dt-[dir]" to server value "[dir]"
	if (dir.length > 7){
		dir = dir.substring(7);
	}
	results   = results || RESULTS_PAGE_SIZE;

	var tabParams = "tab="+getCurrentTab();
	var stateParams = "results="+results+"&startIndex="+startIndex+"&sort="+sort+"&dir="+dir;
	var facetParams = [];
	for (key in facets){
		list = facets[key];
		for(var i=0; i < list.length; i++){
			facetParams.push( key + '=' + list[i].replace('*', ',') );
		}
	}
	var facets = facetParams.join("&");
	var query = [];
	if(querystring) query.push(querystring);
	if(stateParams) query.push(stateParams);
	if(facets) query.push(facets);
	if(tabParams) query.push(tabParams);
	return query.join("&");
};

// a globabl variable to help the summary know when to generate a new datatable
var previousQueryString = "";
var previousGAState = "";
// Called by Browser History Manager to trigger a new state
handleNavigation = function (request, calledLocally) {
	
	if (calledLocally==undefined)
		calledLocally = false;
	
	// we need the tab state of the request
	var values = parseRequest(request);
	var tabState = values['tab'];
	
	var currentTab = getCurrentTab();
	
	var foundParams = true;
	// test if there is a form that needs to be populated
	if (typeof reverseEngineerFormInput == 'function')
		 foundParams = reverseEngineerFormInput(request);
	
	//Set the global querystring parameter for later navigation
	// if there is no getQueryString function, we assume that window.querystring is already set
	if (typeof getQueryString == 'function')
		window.querystring = getQueryString(YAHOO.util.Dom.get("gxdQueryForm")); 

	// Handle proper behavior for back and forward navigation 
	// if we have no tab state in the request, then we won't try to switch tabs.
	if(tabState && (currentTab != tabState)) {
		resultsTabs.selectTab(tabs[tabState]);
		return;
	}

	var doNewQuery = querystring != previousQueryString;
	previousQueryString = querystring;
	
	var PAGE_SIZE = GENES_PAGE_SIZE;

	if (tabState == "genestab") {
		gxdGenesTable();
	} else if(tabState == "assaystab"){
		gxdAssaysTable();
		PAGE_SIZE = ASSAYS_PAGE_SIZE;
	} else {
		gxdResultsTable();
		PAGE_SIZE = RESULTS_PAGE_SIZE;
	}

	if(!foundParams)
	{
		// this is how we handle an empty request.
		if(typeof closeSummaryControl == 'function')
			closeSummaryControl();
	}
	else
	{
		// Update the "you searched for" text
		updateQuerySummary();

		if (typeof openSummaryControl == 'function')
			openSummaryControl();
		gxdDataTable.showTableMessage(gxdDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);

		// Sends a new request to the DataSource
		gxdDataSource.sendRequest(request,{
			success : gxdDataTable.onDataReturnSetRows,
			failure : gxdDataTable.onDataReturnSetRows,
			scope : gxdDataTable,
			argument : {} // Pass in container for population at runtime via doBeforeLoadData
		});
		
		if (doNewQuery)
		{
			refreshTabCounts();
			
			refreshGxdLitLink();

			// Wire up report and batch buttons
			var resultsTextReportButton = YAHOO.util.Dom.get('resultsTextDownload');
			if (!YAHOO.lang.isNull(resultsTextReportButton)) {
				resultsTextReportButton.setAttribute('href', fewiurl + 'gxd/report.txt?' + querystring);
			}
			var resultsExcelReportButton = YAHOO.util.Dom.get('resultsExcelDownload');
			if (!YAHOO.lang.isNull(resultsExcelReportButton)) {
				resultsExcelReportButton.setAttribute('href', fewiurl + 'gxd/report.xlsx?' + querystring);
			}
			var markersTextReportButton = YAHOO.util.Dom.get('markersTextDownload');
			if (!YAHOO.lang.isNull(markersTextReportButton)) {
				markersTextReportButton.setAttribute('href', fewiurl + 'gxd/marker/report.txt?' + querystring);
			}
			var markersExcelReportButton = YAHOO.util.Dom.get('markersExcelDownload');
			if (!YAHOO.lang.isNull(markersExcelReportButton)) {
				markersExcelReportButton.setAttribute('href', fewiurl + 'gxd/marker/report.xlsx?' + querystring);
			}
			var markersBatchForward = YAHOO.util.Dom.get('markersBatchForward');
			if (!YAHOO.lang.isNull(markersBatchForward)) {
				markersBatchForward.setAttribute('href', fewiurl + 'gxd/batch?' + querystring);
			}
		}
		
		// Shh, do not tell anyone about this. We are sneaking in secret Google Analytics calls, even though there is no approved User Story for it.
		var GAState = "/gxd/summary/"+tabState+"?"+querystring;
		if(GAState != previousGAState)
		{
			gaA_pageTracker._trackPageview(GAState);
			previousGAState = GAState;
		}
	}
};

/*
 * Definitions of all the datatables
 */

//
//Gene results table population function
//
var gxdGenesTable = function (oCallback) {	

	//
	// Global variable definitions
	//
	var facets = {};
	var numConfig = {thousandsSeparator: ','};

	// Column definitions
	var myColumnDefs = [ // sortable:true enables sorting       
		{key:"primaryID", label:"MGI ID", sortable:false, width:75},
		{key:"symbol", label:"Gene", sortable:true, width:100},
		{key:"name", label:"Gene Name", sortable:false, minWidth:400},
		{key:"type", label:"Type", sortable:false, minWidth:200},
		{key:"chr", label:"Chr", sortable:true, width:40},
		{key:"location", label:"Genome Location-NCBI Build 37", sortable:false, minWidth:300},     
		{key:"cm", label:"cM", sortable:false, width:50},
		{key:"strand", label:"Strand", sortable:false},
		{key:"score", label:"score", sortable:false, hidden:true}
	];

	// DataSource instance
	gxdDataSource = new YAHOO.util.XHRDataSource(fewiurl + "gxd/markers/json?");
	gxdDataSource.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
	gxdDataSource.responseSchema = {
		resultsList: "summaryRows",
		fields: [
			{key:"primaryID"},
			{key:"symbol"},
			{key:"name"},
			{key:"type"},
			{key:"chr"},
			{key:"location"},
			{key:"cm"},
			{key:"strand"},
			{key:"score"}
		],
		metaFields: {
			totalRecords: "totalCount",
			paginationRecordOffset : "startIndex",
			paginationRowsPerPage : "pageSize",
			sortKey: "sort",
			sortDir: "dir"
		}
	};

	gxdDataSource.maxCacheEntries = 3;
	gxdDataSource.connXhrMode = "cancelStaleRequests";

	// Create the Paginator
	var myPaginator = new YAHOO.widget.Paginator({
		template : "{FirstPageLink} {PreviousPageLink}<strong>{PageLinks}</strong> {NextPageLink} {LastPageLink} <span style=align:right;>{RowsPerPageDropdown}</span><br/>{CurrentPageReport}",
		pageReportTemplate : "Showing marker(s) {startRecord} - {endRecord} of {totalRecords}",
		rowsPerPageOptions : [50,100,250,500],
		containers   : ["paginationTop", "paginationBottom"],
		rowsPerPage : GENES_PAGE_SIZE,
		pageLinks: 3,
		recordOffset: 1
	});

	// DataTable configurations
	var myConfigs = {
		paginator : myPaginator,
		dynamicData : true,
		initialLoad : false,
		MSG_LOADING:  '<img src="/fewi/mgi/assets/images/loading.gif" height="24" width="24"> Searching...',
		MSG_EMPTY:    'No genes with expression data found.'
	};
 
	// DataTable instance
	gxdDataTable = new YAHOO.widget.DataTable("genesdata", myColumnDefs, gxdDataSource, myConfigs);

	// Show loading message while page is being rendered
	gxdDataTable.showTableMessage(gxdDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);

	// Define a custom function to route sorting through the Browser History Manager
	var handleSorting = function (oColumn) {
		// The next state will reflect the new sort values
		// while preserving existing pagination rows-per-page
		// As a best practice, a new sort will reset to page 0
		var sortedBy = {dir: this.getColumnSortDir(oColumn), key: oColumn.key};

		// Pass the state along to the Browser History Manager
		History.navigate("gxd", generateRequest(sortedBy, 0, myPaginator.getRowsPerPage()));
	};
	gxdDataTable.sortColumn = handleSorting;

	// Define a custom function to route pagination through the Browser History Manager
	var handlePagination = function(state) {
		// The next state will reflect the new pagination values
		// while preserving existing sort values
		var newState = generateRequest(this.get("sortedBy"), state.recordOffset, state.rowsPerPage);

		// Pass the state along to the Browser History Manager
		History.navigate("gxd", newState);
	};

	// First unhook the built-in mechanism...
	myPaginator.unsubscribe("changeRequest", gxdDataTable.onPaginatorChangeRequest);
	// ...then hook up our custom function
	myPaginator.subscribe("changeRequest", handlePagination, gxdDataTable, true);
 
	gxdDataTable.doBeforeLoadData = function(oRequest, oResponse, oPayload) {
		var pRequest = parseRequest(oRequest);
		var meta = oResponse.meta;
		oPayload.totalRecords = meta.totalRecords || oPayload.totalRecords;

		var filterCount = YAHOO.util.Dom.get('filterCount');
		if (!YAHOO.lang.isNull(filterCount)){
			setText(filterCount, YAHOO.util.Number.format(oPayload.totalRecords, numConfig));
		}

		oPayload.sortedBy = {
			key: pRequest['sort'][0] || "symbol",
			dir: pRequest['dir'][0] ? "yui-dt-" + pRequest['dir'][0] : "yui-dt-desc" // Convert from server value to DataTable format
		};

		oPayload.pagination = {
			rowsPerPage: Number(pRequest['results'][0]) || GENES_PAGE_SIZE,
			recordOffset: Number(pRequest['startIndex'][0]) || 0
		};

		return true;
	};
};


/*
 * These tooltips display only in the assays table
 */
var addCameraIconTooltips = function() {
	var icons = YAHOO.util.Dom.getElementsByClassName('cameraIcon');
	for(var i=0; i<icons.length; i++) {
		var ttText = "Image(s) available. Follow <b>data</b> link.";
		new YAHOO.widget.Tooltip("tsCamera"+i,{context:icons[i], text:ttText, showdelay:1000});
	}
};
//
//Assays table population function
//
var gxdAssaysTable = function() {

	var numConfig = {thousandsSeparator: ','};

	// Column definitions
	var myColumnDefs = [
		// sortable:true enables sorting
		{key: "gene", label: "Gene", sortable: true },
		{key: "assayID", label: "Assay Details", sortable: false },
		{key: "assayType", label: "Assay Type", sortable: true },
		{key: "reference",label: "Reference",sortable: true},
		{key: "score",label: "score",sortable: false,hidden: true}
	];
	
	// DataSource instance
	gxdDataSource = new YAHOO.util.XHRDataSource(fewiurl + "gxd/assays/json?");
	gxdDataSource.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
	gxdDataSource.responseSchema = {
		resultsList: "summaryRows",
		fields: [
			{key: "assayType"},
			{key: "assayID"},
			{key: "gene"},
			{key: "reference"},
			{key: "score"}
		],
		metaFields: {
			totalRecords: "totalCount",
			paginationRecordOffset: "startIndex",
			paginationRowsPerPage: "pageSize",
			sortKey: "sort",
			sortDir: "dir"
		}
	};

	gxdDataSource.maxCacheEntries = 3;
	gxdDataSource.connXhrMode = "cancelStaleRequests";

	// Create the Paginator
	var myPaginator = new YAHOO.widget.Paginator({
	   template: "{FirstPageLink} {PreviousPageLink}<strong>{PageLinks}</strong> {NextPageLink} {LastPageLink} <span style=align:right;>{RowsPerPageDropdown}</span><br/>{CurrentPageReport}",
	   pageReportTemplate: "Showing results(s) {startRecord} - {endRecord} of {totalRecords}",
	   rowsPerPageOptions: [50,100,250,500],
	   containers: ["paginationTop", "paginationBottom"],
	   rowsPerPage: RESULTS_PAGE_SIZE,
	   pageLinks: 3,
	   recordOffset: 1
	});
	
	// DataTable configurations
	var myConfigs = {
	   paginator: myPaginator,
	   dynamicData: true,
	   initialLoad: false,
	   MSG_LOADING: '<img src="/fewi/mgi/assets/images/loading.gif" height="24" width="24"> Searching...',
	   MSG_EMPTY: 'No genes with expression data found.'
	};
	
	// DataTable instance
	gxdDataTable = new YAHOO.widget.DataTable("assaysdata", myColumnDefs, gxdDataSource, myConfigs);
	
	// Show loading message while page is being rendered
	gxdDataTable.showTableMessage(gxdDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);
	
	// Define a custom function to route sorting through the Browser History Manager
	var handleSorting = function(oColumn) {
	   // The next state will reflect the new sort values
	   // while preserving existing pagination rows-per-page
	   // As a best practice, a new sort will reset to page 0
	   var sortedBy = {
	       dir: this.getColumnSortDir(oColumn),
	       key: oColumn.key
	   };
	   // Pass the state along to the Browser History Manager
	   History.navigate("gxd", generateRequest(sortedBy, 0, myPaginator.getRowsPerPage()));
	};
	gxdDataTable.sortColumn = handleSorting;

	// Define a custom function to route pagination through the Browser History Manager
	var handlePagination = function(state) {
	   // The next state will reflect the new pagination values
	   // while preserving existing sort values
	   var newState = generateRequest(this.get("sortedBy"), state.recordOffset, state.rowsPerPage);
	   //myPaginator.setState(newState);
	   // Pass the state along to the Browser History Manager
	   History.navigate("gxd", newState);
	};
	// First we must unhook the built-in mechanism...
	myPaginator.unsubscribe("changeRequest", gxdDataTable.onPaginatorChangeRequest);
	// ...then we hook up our custom function
	myPaginator.subscribe("changeRequest", handlePagination, gxdDataTable, true);
		
	gxdDataTable.doBeforeLoadData = function(oRequest, oResponse, oPayload) {
		var pRequest = parseRequest(oRequest);
		var meta = oResponse.meta;
		oPayload.totalRecords = meta.totalRecords || oPayload.totalRecords;
		//   updateCount(oPayload.totalRecords);
		var filterCount = YAHOO.util.Dom.get('filterCount');
		if (!YAHOO.lang.isNull(filterCount)) {
			setText(filterCount, YAHOO.util.Number.format(oPayload.totalRecords, numConfig));
		}

		oPayload.sortedBy = {
			key: pRequest['sort'][0] || "gene",
			dir: pRequest['dir'][0] ? "yui-dt-" + pRequest['dir'][0] : "yui-dt-desc"
				// Convert from server value to DataTable format
		};

		oPayload.pagination = {
			rowsPerPage: Number(pRequest['results'][0]) || myPaginator.getRowsPerPage(),
			recordOffset: Number(pRequest['startIndex'][0]) || 0
		};

		return true;
	};

	// Add tooltips to the camera icons
	gxdDataTable.subscribe('postRenderEvent', addCameraIconTooltips);

};


//
//Assay results table population function
//
var gxdResultsTable = function() {

	var numConfig = {thousandsSeparator: ','};

	// Column definitions
	var myColumnDefs = [
	// sortable:true enables sorting
		{key: "gene", label: "Gene", sortable: true },
		{key: "assayID", label: "Result Details", sortable: false },
		{key: "assayType", label: "Assay Type", sortable: true },
		{key: "anatomicalSystem", label: "Anatomical System", sortable: true },
		{key: "age", label: "Age", sortable: true },
		{key: "structure", label: "Structure",sortable: true},
		{key: "detectionLevel",label: "Detected?",sortable: true},
		{key: "figures", label: "Figure(s)",sortable: false},
		{key: "genotype",label: "Mutant Allele(s)",sortable: false},
		{key: "reference",label: "Reference",sortable: true},
		{key: "score",label: "score",sortable: false,hidden: true}
	];
	
	// DataSource instance
	gxdDataSource = new YAHOO.util.XHRDataSource(fewiurl + "gxd/results/json?");
	gxdDataSource.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
	gxdDataSource.responseSchema = {
		resultsList: "summaryRows",
		fields: [
			{key: "gene"},
			{key: "assayID"},
			{key: "assayType"},
			{key: "anatomicalSystem"},
			{key: "age"},
			{key: "structure"},
			{key: "detectionLevel"},
			{key: "figures"},
			{key: "genotype"},
			{key: "reference"},
			{key: "score"}
		],
		metaFields: {
			totalRecords: "totalCount",
			paginationRecordOffset: "startIndex",
			paginationRowsPerPage: "pageSize",
			sortKey: "sort",
			sortDir: "dir"
		}
	};

	gxdDataSource.maxCacheEntries = 3;
	gxdDataSource.connXhrMode = "cancelStaleRequests";

	// Create the Paginator
	var myPaginator = new YAHOO.widget.Paginator({
	   template: "{FirstPageLink} {PreviousPageLink}<strong>{PageLinks}</strong> {NextPageLink} {LastPageLink} <span style=align:right;>{RowsPerPageDropdown}</span><br/>{CurrentPageReport}",
	   pageReportTemplate: "Showing results(s) {startRecord} - {endRecord} of {totalRecords}",
	   rowsPerPageOptions: [50,100,250,500],
	   containers: ["paginationTop", "paginationBottom"],
	   rowsPerPage: RESULTS_PAGE_SIZE,
	   pageLinks: 3,
	   recordOffset: 1
	});
	
	// DataTable configurations
	var myConfigs = {
	   paginator: myPaginator,
	   dynamicData: true,
	   initialLoad: false,
	   MSG_LOADING: '<img src="/fewi/mgi/assets/images/loading.gif" height="24" width="24"> Searching...',
	   MSG_EMPTY: 'No genes with expression data found.'
	};
	
	// DataTable instance
	gxdDataTable = new YAHOO.widget.DataTable("resultsdata", myColumnDefs, gxdDataSource, myConfigs);
	
	// Show loading message while page is being rendered
	gxdDataTable.showTableMessage(gxdDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);
	
	// Define a custom function to route sorting through the Browser History Manager
	var handleSorting = function(oColumn) {
	   // The next state will reflect the new sort values
	   // while preserving existing pagination rows-per-page
	   // As a best practice, a new sort will reset to page 0
	   var sortedBy = {
	       dir: this.getColumnSortDir(oColumn),
	       key: oColumn.key
	   };
	   // Pass the state along to the Browser History Manager
	   History.navigate("gxd", generateRequest(sortedBy, 0, myPaginator.getRowsPerPage()));
	};
	gxdDataTable.sortColumn = handleSorting;

	// Define a custom function to route pagination through the Browser History Manager
	var handlePagination = function(state) {
	   // The next state will reflect the new pagination values
	   // while preserving existing sort values
	   var newState = generateRequest(this.get("sortedBy"), state.recordOffset, state.rowsPerPage);
	   //myPaginator.setState(newState);
	   // Pass the state along to the Browser History Manager
	   History.navigate("gxd", newState);
	};
	// First we must unhook the built-in mechanism...
	myPaginator.unsubscribe("changeRequest", gxdDataTable.onPaginatorChangeRequest);
	// ...then we hook up our custom function
	myPaginator.subscribe("changeRequest", handlePagination, gxdDataTable, true);

	gxdDataTable.doBeforeLoadData = function(oRequest, oResponse, oPayload) {
		var pRequest = parseRequest(oRequest);
		var meta = oResponse.meta;
		oPayload.totalRecords = meta.totalRecords || oPayload.totalRecords;
		//   updateCount(oPayload.totalRecords);
		var filterCount = YAHOO.util.Dom.get('filterCount');
		if (!YAHOO.lang.isNull(filterCount)) {
			setText(filterCount, YAHOO.util.Number.format(oPayload.totalRecords, numConfig));
		}

		oPayload.sortedBy = {
			key: pRequest['sort'][0] || "gene",
			dir: pRequest['dir'][0] ? "yui-dt-" + pRequest['dir'][0] : "yui-dt-desc"
				// Convert from server value to DataTable format
		};

		oPayload.pagination = {
			rowsPerPage: Number(pRequest['results'][0]) || myPaginator.getRowsPerPage(),
			recordOffset: Number(pRequest['startIndex'][0]) || 0
		};

		return true;
	};
};



//
// Initialize the data table to the results table and 
// Register the module with the browser history manager
//
gxdResultsTable();
History.register("gxd", History.getBookmarkedState("gxd") || "", handleNavigation);


//Handle the initial state of the page through history manager
function historyInit()
{
	// get the bookmarked state
	var currentState = History.getBookmarkedState("gxd");
	if(currentState)
	{
		// reset form values
		if (typeof reverseEngineerFormInput == 'function')
			reverseEngineerFormInput(currentState);

		// rebuild the global querystring
		// if there is no getQueryString function, we assume that window.querystring is already set
		if (typeof getQueryString == 'function')
			window.querystring = getQueryString(YAHOO.util.Dom.get("gxdQueryForm"));

		var currentTab = History.getBookmarkedState("gxd");

		if(currentTab=="genestab") 
		{
			resultsTabs.set("activeIndex", 0);
			gxdGenesTable();
		}
		else if(currentTab=="assaystab") 
		{
			resultsTabs.set("activeIndex", 1);
			gxdAssaysTable();
		}

		handleNavigation(currentState); 
	}
	else {
		handleNavigation(generateRequest(null, 0, RESULTS_PAGE_SIZE)); 
	}
}
History.onReady(historyInit); 


//
// Initialize the YUI browser history manager control
//
History.initialize("yui-history-field", "yui-history-iframe");