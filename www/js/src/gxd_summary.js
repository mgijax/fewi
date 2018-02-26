//The YUI components required for setting up the data table and
//source to do the AJAX call
var gxdDataTable;
var gxdDataSource;
var defaultSort = "";

//default page size for each summary
var GENES_PAGE_SIZE = 100;
var ASSAYS_PAGE_SIZE = 100;
var RESULTS_PAGE_SIZE = 100;
var IMAGES_PAGE_SIZE = 25;

var LOADING_IMG_SRC = "/fewi/mgi/assets/images/loading.gif";
var LOADING_IMG = "<img src=\""+LOADING_IMG_SRC+"\" height=\"24\" width=\"24\">";

//set up XHRDataSource objects to use POST submissions
var xhrConfig = { connMethodPost :  true,
	maxCacheEntries : 3,
	connXhrMode : "cancelStaleRequests"
	};

//Shortcut variable for the YUI history manager
var History = YAHOO.util.History;

//HTML/YUI page widgets
YAHOO.namespace("gxd.container");

//------ tab definitions + functions ------------
var mgiTab = new MGITabSummary({
	"tabViewId":"resultSummary",
	"tabIds":["genestab","assaystab","resultstab","imagestab","stagegridtab","genegridtab"],
	"pageSizes":[GENES_PAGE_SIZE,ASSAYS_PAGE_SIZE,RESULTS_PAGE_SIZE,IMAGES_PAGE_SIZE,0,0], // mirrors "tabIds"
	"historyId":"gxd"
});

var stageDayMap = {
		1:"E0-2.5",
		2:"E1-2.5",
		3:"E1-3.5",
		4:"E2-4",
		5:"E3-5.5",
		6:"E4-5.5",
		7:"E4.5-6",
		8:"E5-6.5",
		9:"E6.25-7.25",
		10:"E6.5-7.75",
		11:"E7.25-8",
		12:"E7.5-8.75",
		13:"E8-9.25",
		14:"E8.5-9.75",
		15:"E9-10.25",
		16:"E9.5-10.75",
		17:"E10-11.25",
		18:"E10.5-11.25",
		19:"E11-12.25",
		20:"E11.5-13",
		21:"E12.5-14",
		22:"E13.5-15",
		23:"E15",
		24:"E16",
		25:"E17",
		26:"E18",
		27:"P0-3",
		28:"P4-Adult"
};

//TODO: refactor these to use the mgiTab.summaryTabs object instead of resultsTabs
var resultsTabs = mgiTab.summaryTabs;
var getCurrentTab = mgiTab.getCurrentTab;

/*
 * Some helper functions for dealing with YUI history manager
 * and building AJAX requests
 */

//TODO: refactor these functions to use the shared lib names (i.e. mgiParseRequest() instead of parseRequest())
var parseRequest=mgiParseRequest;
//need to add grid filters to the generateRequest function via extraParams
mgiTab._rp.getExtraParams = function()
{
	var params = [];
	var currentTab = mgiTab.getCurrentTab();
	if(currentTab) params.push("tab="+currentTab);

	var facets = getFilterCriteria();
	if(facets) params.push(facets);

	return params.join("&");
}
var generateRequest = mgiTab._rp.generateRequest;

// This method gets the marker ids on the genes tab based on
// the query string and selected filters, to be unsed in forwarding
// to mousemine
var getMarkerIds = function() {
	
	//var url = fewiurl + "gxd/markers/idList?" + getQueryStringWithFilters();
	var url = fewiurl + "gxd/markers/idList";
	
	var callback = {
		success : function(oResponse) {
			$("#mousemineids").val(oResponse.responseText);
		},
	};
	// YAHOO.util.Connect.asyncRequest('GET', url, callback);
	YAHOO.util.Connect.asyncRequest('POST', url, callback, getQueryStringWithFilters());
};

//a global variable to helpthe tab change handler know when to fire off a new query
var newQueryState = false;

// do a form submission based on one of the Export buttons
var buildAndSubmit = function(formID, formAction) {
	// find the specified form
	var gxdForm = $("#" + formID);
	if (gxdForm === null) {
		alert("Can't find element (" + formID + "); action cancelled.");
		return;
	}

	// set its action attribute
	gxdForm.attr('action', formAction);

	// remove any existing hidden parameters in the form
	var oldFields = $("#" + formID + " input[type='hidden']").remove();

	// add the parameters as hidden fields to the form
	var querystringWithFilters = getQueryStringWithFilters();
	var pairs = querystringWithFilters.split("&");

	for (var j in pairs) {
		var pair = pairs[j];
		var tokens = pair.split('=');
		var el = jQuery('<input name="' + tokens[0] + '" value="' + tokens[1] + '" type="hidden">');
		gxdForm.append(el);
	}

	// submit the form
	gxdForm.submit();
};

// parses request parameters and resets and values found with their matching form input element
// returns false if no parameters were found
// responsible for repopulating the form during history manager changes
function reverseEngineerFormInput(request)
{
	var params = parseRequest(request);
	var formID = "#gxdQueryForm";
	var foundDifStruct=false;
	var foundDifStage=false;
	var foundBatch=false;
	var filters = {};	// filters[filter name] = [ values ]

	for(var key in params)
	{
		if(key == "detected")
		{
			// HACK for the radio buttons
			params["detected1"] = params[key];
			params["detected2"] = params[key];
		}
		else if(key == "difStructure") foundDifStruct=true;
		else if(key == "difTheilerStage" || key=="difAge") foundDifStage=true;
		else if(key == 'idType') foundBatch=true;
	}
	// make sure correct form is visible
	// this code allows for flexibility to add third ribbon
	if(foundDifStruct || foundDifStage)
	{
		formID = "#gxdDifferentialQueryForm3";
		showDifferentialForm();
	} else if (foundBatch)
	{
		formID = "#gxdBatchQueryForm1";
		showBatchSearchForm();
	}

	var foundParams = false;
	if (typeof resetQF == 'function') { resetQF(); }
	for(var key in params)
	{
		// need special handling for idFile field (do not set this to
		// an empty string!)
		if (key == 'idFile') {
			// no op - skip it
			// $(formID+" [name='idFile']").value = null;
		}
		else if(key!=undefined && key!="" && key!="detected" && params[key].length>0)
		{
			//var input = YAHOO.util.Dom.get(key);
			// jQuery is better suited to resolving form name parameters
			var input = $(formID+" [name='"+key+"']");
			if(input.length < 1) input = $(formID+" #"+key);
			if(input!=undefined && input!=null && input.length > 0)
			{

				input = input[0];
				if(input.tagName=="TEXTAREA")
				{
					input.value = decodeURIComponent(params[key]);
					if (input.id == 'ids') {
						input.value = input.value.replace(/ /g, '\n');
					}
				}
				else if(input.tagName=="INPUT")
				{
					foundParams = true;
					// do radio boxes
					if(input.type == "radio")
					{
						if(key=="isWildType")
						{
							YAHOO.util.Dom.get("isWildType").checked = true;
						}
						else if(input.value == params[key])
						{
							input.checked=true;
						}
					}
					// do check boxes
					else if(input.type=="checkbox")
					{
						var options = [];
						var rawParams = [].concat(params[key]);
						for(var i=0;i<rawParams.length;i++)
						{
							options.push(decodeURIComponent(rawParams[i]));
						}
						// The YUI.get() only returns one checkbox, but we want the whole set.
						// The class should also be set to the same name.
						var boxes = YAHOO.util.Selector.query("."+key);
						for(var i=0;i<boxes.length;i++)
						{
							var box = boxes[i];
							var checked = false;
							for(var j=0;j<options.length;j++)
							{
								if(options[j] == box.value)
								{
									checked = true;
									box.checked = true;
									break;
								}
							}
							if(!checked)
							{
								box.checked = false;
							}
						}
					}
					else
					{
						if (key == "mutatedIn")
						{
							YAHOO.util.Dom.get("mutatedSpecimen").checked = true;
						}
						input.value = decodeURIComponent(params[key]);
					}
				}
				else if(input.tagName=="SELECT")
				{
					if (input.name == "age") {
						// open the age tab
						//if(foundDifStage) selectDifAge();
						//else selectAge();
						selectAge();
					}
					foundParams = true;
					var options = [];
					// decode all the options first
					var rawParams = [].concat(params[key]);
					for(var i=0;i<rawParams.length;i++)
					{
						options.push(decodeURIComponent(rawParams[i]));
					}
					// find which options need to be selected, and select them.
					for(var key in input.children)
					{
						if(input[key]!=undefined)
						{
							var selected = false;
							for(var j=0;j<options.length;j++)
							{
								if(options[j] == input[key].value)
								{
									selected = true;
									input[key].selected = true;
									break;
								}
							}
							if(!selected)
							{
								input[key].selected = false;
							}
						}
					}
				}
			} else if (typeof isFilterable != 'undefined' &&
					isFilterable(key)) {
			    // deal with filters (no form fields for them)
				// TODO: This needs to move to a different function. Filters should not be a part of this method
			    filters[key] = [].concat(params[key]);
			}
		}
	}

	if(typeof resetFacets != 'undefined')
	{
		resetFacets(filters);
		prepFilters(request);	// need to reset the URLs for the filters
	}
	if (typeof assayTypesCheck == 'function') { assayTypesCheck(); }
	return foundParams;
}

//a globabl variable to help the summary know when to generate a new datatable
var previousQueryString = "none";
var previousFilterString = "none";
var previousGAState = "";
//Called by Browser History Manager to trigger a new state
handleNavigation = function (request, calledLocally) {

	// ensure any popups get hidden
	stagePopupPanel.hide();
	genePopupPanel.hide();
	geneMatrixLegendPopupPanel.hide();
	structMatrixLegendPopupPanel.hide();

	if (calledLocally==undefined)
		calledLocally = false;

	var values = parseRequest(request);

	// collect any filters and ensure that we use them
	var filters = {};
	for (k in values) {
		if (isFilterable(k)) { filters[k] = [].concat(values[k]); }
	}
	if (filters) { resetFacets(filters); }

	var foundParams = true;
	// test if there is a form that needs to be populated
	if (typeof reverseEngineerFormInput == 'function')
		log("request: " + request);
		foundParams = reverseEngineerFormInput(request);

	//Set the global querystring parameter for later navigation
	// if there is no getQueryString function, we assume that window.querystring is already set
	if (typeof getQueryString == 'function')
		window.querystring = getQueryString().replace('&idFile=&', '&');
		//window.querystring = getQueryString();

	// we need the tab state of the request
	var currentTab = getCurrentTab();
	var tabState = values['tab'];
	// Handle proper behavior for back and forward navigation
	// if we have no tab state in the request, then we won't try to switch tabs.
	if(tabState && (currentTab != tabState)) {
		resultsTabs.selectTab(mgiTab.tabs[tabState]);
		return;
	}

	var doNewQuery = querystring != previousQueryString;
	previousQueryString = querystring;

	if((!foundParams) && (typeof resetQF == 'function'))
	{
		log("found empty request");
		// this is how we handle an empty request.
		if(typeof closeSummaryControl == 'function')
			closeSummaryControl();

		refreshTabCounts();
	}
	else
	{
		// Update the "you searched for" text
		if (typeof updateQuerySummary == 'function')
			updateQuerySummary();

		if (typeof openSummaryControl == 'function')
			openSummaryControl();

		// build the summary inside the tab
		buildSummary(request,tabState);

		// update the report buttons
		var querystringWithFilters = getQueryStringWithFilters();
		if (querystringWithFilters != previousFilterString)
		{
			previousFilterString = querystringWithFilters;

			if(currentStageGrid) currentStageGrid.cancelDataSource();
			if(currentGeneGrid) currentGeneGrid.cancelDataSource();

			// Wire up report and batch buttons
			var resultsTextReportButton = YAHOO.util.Dom.get('resultsTextDownload');
			if (!YAHOO.lang.isNull(resultsTextReportButton)) {
				resultsTextReportButton.setAttribute('onClick', "buildAndSubmit('resultsExportForm', '" + fewiurl + "gxd/report.txt')");
			}
			var resultsExcelReportButton = YAHOO.util.Dom.get('resultsExcelDownload');
			if (!YAHOO.lang.isNull(resultsExcelReportButton)) {
				resultsExcelReportButton.setAttribute('onClick', "buildAndSubmit('resultsExportForm', '" + fewiurl + "gxd/report.xlsx')");
			}
			var markersTextReportButton = YAHOO.util.Dom.get('markersTextDownload');
			if (!YAHOO.lang.isNull(markersTextReportButton)) {
				markersTextReportButton.setAttribute('onClick', "buildAndSubmit('markerExportForm', '" + fewiurl + "gxd/marker/report.txt')");
			}
			var markersExcelReportButton = YAHOO.util.Dom.get('markersExcelDownload');
			if (!YAHOO.lang.isNull(markersExcelReportButton)) {
				markersExcelReportButton.setAttribute('onClick', "buildAndSubmit('markerExportForm', '" + fewiurl + "gxd/marker/report.xlsx')");
			}
			var markersBatchForward = YAHOO.util.Dom.get('markersBatchForward');
			if (!YAHOO.lang.isNull(markersBatchForward)) {
				markersBatchForward.setAttribute('onClick', "buildAndSubmit('markerExportForm', '" + fewiurl + "gxd/batch')");
			}
		}

		refreshTabCounts();
		refreshGxdLitLink();

		// Shh, do not tell anyone about this. We are sneaking in secret Google Analytics calls, even though there is no approved User Story for it.
		var GAState = "/gxd/summary/"+tabState+"?"+querystring;
		if(GAState != previousGAState)
		{
			try {
				gaA_pageTracker._trackPageview(GAState);
			} catch (e) {};
			previousGAState = GAState;
		}
	}
};

function buildSummary(request,tabState)
{
	var doStageGrid=false;
	var doGeneGrid=false;

	// determine which type of summary to load.
	var dataTableInitFunction;
	// init
	if (tabState == "genestab") {
		dataTableInitFunction = window.gxdGenesTable;
	} else if(tabState == "assaystab"){
		dataTableInitFunction = window.gxdAssaysTable;
	}
	else if(tabState == "imagestab"){
		dataTableInitFunction = window.gxdImagesTable;
	}
	else if(tabState == "stagegridtab")
	{
		doStageGrid=true;
	}
	else if(tabState == "genegridtab")
	{
		doGeneGrid=true;
	}
	else {
		dataTableInitFunction = window.gxdResultsTable;
	}

	// Load the appropriate summary
	if(doStageGrid)
	{
		structureStageGrid();
		showNowhereElseMessage(request, 'Tissue x Stage Matrix');
	}
	else if(doGeneGrid)
	{
		structureGeneGrid();
		showNowhereElseMessage(request, 'Tissue x Gene Matrix');
	}
	else
	{
		loadDatatable(dataTableInitFunction,request);
		$('#nowhereElseMessage').hide();
	}
}

// Works with clickNotDetectedFilter() to allow easy, programmatic removal of the auto-added Not Detected filter.
function removeNotDetectedFilter() {
	$('#detectedFilter\\:No')[0].click();
	showNowhereElseMessage();
}

// Works with addNotDetectedFilter() to wait for the filter box to pop up and then click it.
// It takes time to retrieve the data to pop up the filter box, so allow the given number
// of 'retries' to wait for it to be available before giving up.
function clickNotDetectedFilter(retries) {
	if ($('[name=detectedFilter][value=No]').length > 0) {
		$('[name=detectedFilter][value=No]').click();
		$('#yui-gen0-button').click(); 

	} else if (retries > 0) {
		// wait 100ms and try again
		setTimeout(function() {
			clickNotDetectedFilter(retries - 1);
		}, 100);
	}
}

// programmatically select the 'Detected' filter, choose the 'No' option, and apply it
function addNotDetectedFilter(secondCall) {
	$('#nowhereElseMessage').css('display', 'none');
	$('#detectedFilter')[0].click();
	clickNotDetectedFilter(30);
}

// if the 'request' includes the 'anywhereElse' checkbox, show the corresponding message div
function showNowhereElseMessage(request, matrixType) {
	var params = parseRequest(request);
	var resultCount = YAHOO.util.Dom.get("totalResultsCount").innerHTML;

	if (('anywhereElse' in params) && ( (!resultCount || (parseInt(resultCount) > 0)) ) ) {
		var message = "View the <a class='autofilter' onClick='addNotDetectedFilter(); return false;'>Not Detected data</a> for this gene set";
		if ('detectedFilter' in params) {
			message = "View the <a class='autofilter' onClick='removeNotDetectedFilter(); return false;'>Detected data</a> for this gene set";
		} 
		$('#nowhereElseMessage').html(message);
		$('#nowhereElseMessage').css('display', 'block');
	}
}

function loadDatatable(dataTableInitFunction,request)
{
	// show page controls
	$(".yui-pg-container").show()

	// returns object of {"datatable": ..., "datasource":...}
	var o = dataTableInitFunction();

	// load datatable
	o.datatable.showTableMessage(o.datatable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);

	// Sends a new request to the DataSource
	o.datasource.sendRequest(request,{
		success : o.datatable.onDataReturnSetRows,
		failure : o.datatable.onDataReturnSetRows,
		scope : o.datatable,
		argument : {} // Pass in container for population at runtime via doBeforeLoadData
	});
}


function getQueryStringWithFilters() {
	var filterString = getFilterCriteria();
	var querystringWithFilters = querystring;
	if (filterString) {
		querystringWithFilters = querystringWithFilters + "&" + filterString;
	}
	return querystringWithFilters.replace("&idFile=&", "&");
}

//refresh all four counts in each tab via AJAX
//store the request objects to verify the correct IDs;
var resultsRq,assaysRs,genesRq,imagesRq;
window.previousTabQuery="";

// also use the totalGenesCount to update the You Searched For section, but
// only on the first call (filtering should not affect that count)

var updatedYSF = false;
var ysfGeneCount = '';	// count of genes matching base query (before filters)

function resetYSF() {
	updatedYSF = false;
	ysfGeneCount = '';
}

function getYsfGeneCount() {
	return ysfGeneCount;
}

function refreshTabCounts()
{
	var querystringWithFilters = getQueryStringWithFilters();
	if(querystringWithFilters==window.previousTabQuery)
	{
		// don't refresh counts if query is the same.
		return;
	}
	window.previousTabQuery=querystringWithFilters;

	//get the tab counts via ajax
	var handleCountRequest = function(o)
	{
		if(o.responseText == "-1") o.responseText = "0"; // set count to zero if errors
		// resolve the request ID to its appropriate handler
		if(o.tId==resultsRq.tId) {
			YAHOO.util.Dom.get("totalResultsCount").innerHTML = o.responseText;
			// no results = no message
			if (parseInt(o.responseText) == 0) {
				$('#nowhereElseMessage').css('display', 'none');
			}
		}
		else if(o.tId==assaysRq.tId) YAHOO.util.Dom.get("totalAssaysCount").innerHTML = o.responseText;
		else if(o.tId==genesRq.tId) {
			log('handling gene count');
			YAHOO.util.Dom.get("totalGenesCount").innerHTML = o.responseText;
			log('updatedYSF: ' + updatedYSF);
			if (!updatedYSF) {
				updatedYSF = true;
				ysfGeneCount = o.responseText;
				$('.countHere').text(o.responseText + $('.countHere').text());
				log('updated .countHere');
			} // end - updated YSF
			log('updatedYSF: ' + updatedYSF);
		}
		else if(o.tId==imagesRq.tId) YAHOO.util.Dom.get("totalImagesCount").innerHTML = o.responseText;
	}

	// clear these until the data comes back
	YAHOO.util.Dom.get("totalResultsCount").innerHTML = "";
	YAHOO.util.Dom.get("totalAssaysCount").innerHTML = "";
	YAHOO.util.Dom.get("totalGenesCount").innerHTML = "";
	YAHOO.util.Dom.get("totalImagesCount").innerHTML = "";

	/*
	resultsRq = YAHOO.util.Connect.asyncRequest('GET', fewiurl+"gxd/results/totalCount?"+querystringWithFilters,
			{	success:handleCountRequest,
		failure:function(o){}
			},null);
	assaysRq = YAHOO.util.Connect.asyncRequest('GET', fewiurl+"gxd/assays/totalCount?"+querystringWithFilters,
			{	success:handleCountRequest,
		failure:function(o){}
			},null);
	genesRq = YAHOO.util.Connect.asyncRequest('GET', fewiurl+"gxd/markers/totalCount?"+querystringWithFilters,
			{	success:handleCountRequest,
		failure:function(o){}
			},null);
	imagesRq = YAHOO.util.Connect.asyncRequest('GET', fewiurl+"gxd/images/totalCount?"+querystringWithFilters,
			{	success:handleCountRequest,
		failure:function(o){}
			},null);
	*/
	resultsRq = YAHOO.util.Connect.asyncRequest('POST', fewiurl+"gxd/results/totalCount",
			{	success:handleCountRequest,
		failure:function(o){}
			}, querystringWithFilters);
	assaysRq = YAHOO.util.Connect.asyncRequest('POST', fewiurl+"gxd/assays/totalCount",
			{	success:handleCountRequest,
		failure:function(o){}
			}, querystringWithFilters);
	genesRq = YAHOO.util.Connect.asyncRequest('POST', fewiurl+"gxd/markers/totalCount",
			{	success:handleCountRequest,
		failure:function(o){}
			}, querystringWithFilters);
	imagesRq = YAHOO.util.Connect.asyncRequest('POST', fewiurl+"gxd/images/totalCount",
			{	success:handleCountRequest,
		failure:function(o){}
			}, querystringWithFilters);
}

window.previousGxdLitQuery=""
	function refreshGxdLitLink()
{
	if(querystring==window.previousGxdLitQuery)
	{
		// don't refresh gxd lit count if query is the same.
		return;
	}
	window.previousGxdLitQuery=querystring;

	// Fire off an AJAX call to generate the link
	var handleGxdLitCount = function(o)
	{
		// "-1" is our flag that the query parameters selected do not apply to GXD Lit.
		if(o.responseText != "-1")
		{
			var gxdLitUrl = fewiurl+"gxd/gxdLitForward?"+querystring;
			YAHOO.util.Dom.get("gxdLitInfo").innerHTML = "<a href=\""+gxdLitUrl+"\">"+o.responseText+"</a>"+
			" Gene Expression Literature Records match your unfiltered query.";
		}
	};

	YAHOO.util.Dom.get("gxdLitInfo").innerHTML = "";
	/* YAHOO.util.Connect.asyncRequest('GET', fewiurl+"gxd/gxdLitCount?"+querystring,
			{	success:handleGxdLitCount,
		failure:function(o){}
			},null);
	*/
	YAHOO.util.Connect.asyncRequest('POST', fewiurl+"gxd/gxdLitCount",
			{	success:handleGxdLitCount,
		failure:function(o){}
			}, querystring);
}


/*
 * Definitions of all the datatables
 */


//Gene results table population function

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
	                     {key:"location", label:"Genome Location - " + assemblyBuild, sortable:false, minWidth:300},
	                     {key:"cm", label:"cM", sortable:false, width:50},
	                     {key:"strand", label:"Strand", sortable:false},
	                     {key:"score", label:"score", sortable:false, hidden:true}
	                     ];

	// DataSource instance
	gxdDataSource = new YAHOO.util.XHRDataSource(fewiurl + "gxd/markers/json", xhrConfig);
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

//	gxdDataSource.maxCacheEntries = 3;
//	gxdDataSource.connXhrMode = "cancelStaleRequests";

	var paginator = mgiTab.createPaginator(
			[50,100,250,500], // rows per page options
			GENES_PAGE_SIZE // rows per page
	);

	// DataTable configurations
	var myConfigs = {
			paginator : paginator,
			dynamicData : true,
			initialLoad : false,
			MSG_LOADING:  LOADING_IMG+' Searching...',
			MSG_EMPTY:    'No genes with expression data found.'
	};

	// DataTable instance
	gxdDataTable = new YAHOO.widget.DataTable("genesdata", myColumnDefs, gxdDataSource, myConfigs);

	// Show loading message while page is being rendered
	gxdDataTable.showTableMessage(gxdDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);

	mgiTab.initPaginator(gxdDataTable,paginator);

	// Define a custom function to route sorting through the Browser History Manager
	var handleSorting = function (oColumn) {
		// The next state will reflect the new sort values
		// while preserving existing pagination rows-per-page
		// As a best practice, a new sort will reset to page 0
		var sortedBy = {dir: this.getColumnSortDir(oColumn), key: oColumn.key};

		// Pass the state along to the Browser History Manager
		History.navigate("gxd", generateRequest(sortedBy, 0, paginator.getRowsPerPage()));
	};
	gxdDataTable.sortColumn = handleSorting;

	gxdDataTable.doBeforeLoadData = function(oRequest, oResponse, oPayload) {
		var pRequest = parseRequest(oRequest);

		extractFilters(pRequest);

		var meta = oResponse.meta;
		oPayload.totalRecords = meta.totalRecords || oPayload.totalRecords;

		var filterCount = YAHOO.util.Dom.get('filterCount');
		if (!YAHOO.lang.isNull(filterCount)){
			setText(filterCount, YAHOO.util.Number.format(oPayload.totalRecords, numConfig));
		}

		oPayload.sortedBy = {
				key: pRequest['sort'] || "symbol",
				dir: pRequest['dir'] ? "yui-dt-" + pRequest['dir'] : "yui-dt-desc" // Convert from server value to DataTable format
		};

		oPayload.pagination = {
				rowsPerPage: Number(pRequest['results']) || GENES_PAGE_SIZE,
				recordOffset: Number(pRequest['startIndex']) || 0
		};

		return true;
	};

	getMarkerIds();

	return {"datatable": gxdDataTable, "datasource": gxdDataSource};
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

//Assays table population function

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
	gxdDataSource = new YAHOO.util.XHRDataSource(fewiurl + "gxd/assays/json", xhrConfig);
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

//	gxdDataSource.maxCacheEntries = 3;
//	gxdDataSource.connXhrMode = "cancelStaleRequests";

	var paginator = mgiTab.createPaginator(
			[50,100,250,500], // rows per page options
			ASSAYS_PAGE_SIZE // rows per page
	);

	// DataTable configurations
	var myConfigs = {
			paginator: paginator,
			dynamicData: true,
			initialLoad: false,
			MSG_LOADING: LOADING_IMG+' Searching...',
			MSG_EMPTY: 'No assays with expression data found.'
	};

	// DataTable instance
	gxdDataTable = new YAHOO.widget.DataTable("assaysdata", myColumnDefs, gxdDataSource, myConfigs);

	// Show loading message while page is being rendered
	gxdDataTable.showTableMessage(gxdDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);

	mgiTab.initPaginator(gxdDataTable,paginator);

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
		History.navigate("gxd", generateRequest(sortedBy, 0, paginator.getRowsPerPage()));
	};
	gxdDataTable.sortColumn = handleSorting;

	gxdDataTable.doBeforeLoadData = function(oRequest, oResponse, oPayload) {
		var pRequest = parseRequest(oRequest);
		extractFilters(pRequest);
		var meta = oResponse.meta;
		oPayload.totalRecords = meta.totalRecords || oPayload.totalRecords;
		//   updateCount(oPayload.totalRecords);
		var filterCount = YAHOO.util.Dom.get('filterCount');
		if (!YAHOO.lang.isNull(filterCount)) {
			setText(filterCount, YAHOO.util.Number.format(oPayload.totalRecords, numConfig));
		}

		oPayload.sortedBy = {
				key: pRequest['sort'] || "gene",
				dir: pRequest['dir'] ? "yui-dt-" + pRequest['dir'] : "yui-dt-desc"
					// Convert from server value to DataTable format
		};

		oPayload.pagination = {
				rowsPerPage: Number(pRequest['results']) || paginator.getRowsPerPage(),
				recordOffset: Number(pRequest['startIndex']) || 0
		};

		return true;
	};

	// Add tooltips to the camera icons
	gxdDataTable.subscribe('postRenderEvent', addCameraIconTooltips);

	return {"datatable": gxdDataTable, "datasource": gxdDataSource};
};



//Assay results table population function

var gxdResultsTable = function() {

	var numConfig = {thousandsSeparator: ','};

	// Column definitions
	var myColumnDefs = [
	                    // sortable:true enables sorting
	                    {key: "gene", label: "Gene", sortable: true },
	                    {key: "assayID", label: "Result Details", sortable: false },
	                    {key: "assayType", label: "Assay Type", sortable: true },
	                    {key: "age", label: "Age", sortable: true },
	                    {key: "structure", label: "Structure",sortable: true},
	                    {key: "detectionLevel",label: "Detected?",sortable: true},
	                    {key: "figures", label: "Images",sortable: false},
	                    {key: "genotype",label: "Mutant Allele(s)",sortable: false},
	                    {key: "reference",label: "Reference",sortable: true},
	                    {key: "score",label: "score",sortable: false,hidden: true}
	                    ];

	// DataSource instance
	gxdDataSource = new YAHOO.util.XHRDataSource(fewiurl + "gxd/results/json", xhrConfig);
	gxdDataSource.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
	gxdDataSource.responseSchema = {
			resultsList: "summaryRows",
			fields: [
			         {key: "gene"},
			         {key: "assayID"},
			         {key: "assayType"},
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

//	gxdDataSource.maxCacheEntries = 3;
//	gxdDataSource.connXhrMode = "cancelStaleRequests";


	var paginator = mgiTab.createPaginator(
			[50,100,250,500], // rows per page options
			RESULTS_PAGE_SIZE // rows per page
	);

	// DataTable configurations
	var myConfigs = {
			paginator: paginator,
			dynamicData: true,
			initialLoad: false,
			MSG_LOADING: LOADING_IMG+' Searching...',
			MSG_EMPTY: 'No results with expression data found.'
	};

	// DataTable instance
	gxdDataTable = new YAHOO.widget.DataTable("resultsdata", myColumnDefs, gxdDataSource, myConfigs);

	// Show loading message while page is being rendered
	gxdDataTable.showTableMessage(gxdDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);

	mgiTab.initPaginator(gxdDataTable,paginator);

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
		History.navigate("gxd", generateRequest(sortedBy, 0, paginator.getRowsPerPage()));
	};
	gxdDataTable.sortColumn = handleSorting;

	gxdDataTable.doBeforeLoadData = function(oRequest, oResponse, oPayload) {
		var pRequest = parseRequest(oRequest);
		extractFilters(pRequest);
		var meta = oResponse.meta;
		oPayload.totalRecords = meta.totalRecords || oPayload.totalRecords;
		//   updateCount(oPayload.totalRecords);
		var filterCount = YAHOO.util.Dom.get('filterCount');
		if (!YAHOO.lang.isNull(filterCount)) {
			setText(filterCount, YAHOO.util.Number.format(oPayload.totalRecords, numConfig));
		}

		oPayload.sortedBy = {
				key: pRequest['sort'] || "gene",
				dir: pRequest['dir'] ? "yui-dt-" + pRequest['dir'] : "yui-dt-desc"
					// Convert from server value to DataTable format
		};

		var rowCount = paginator.getRowsPerPage();
		if ('results' in pRequest) {
			rowCount = Number(pRequest['results']);
		}

		var offset = 0;
		if ('startIndex' in pRequest) {
			offset = Number(pRequest['startIndex']);
		}

		oPayload.pagination = {
				rowsPerPage: rowCount,
				recordOffset: offset
		};

		return true;
	};

	return {"datatable": gxdDataTable, "datasource": gxdDataSource};
};


//Images table population function

var gxdImagesTable = function() {

	var numConfig = {thousandsSeparator: ','};

	// Column definitions
	var myColumnDefs = [
	                    // sortable:true enables sorting
	                    {key: "image", label: "", sortable: false},
	                    {key: "gene", label: "Gene", sortable: true},
	                    {key: "assayType", label: "Assay Type", sortable: true},
	                    {key: "hybridization", label: "Specimen Type", sortable: true},
	                    {key: "specimenLabel", label: "Result Details", sortable: false}
	                    ];

	// DataSource instance
	gxdDataSource = new YAHOO.util.XHRDataSource(fewiurl + "gxd/images/json", xhrConfig);
	gxdDataSource.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
	gxdDataSource.responseSchema = {
			resultsList: "summaryRows",
			fields: [
			         {key: "image"},
			         {key: "gene"},
			         {key: "assayType"},
			         {key: "hybridization"},
			         {key: "specimenLabel"}
			         ],
			         metaFields: {
			        	 totalRecords: "totalCount",
			        	 paginationRecordOffset: "startIndex",
			        	 paginationRowsPerPage: "pageSize",
			        	 sortKey: "sort",
			        	 sortDir: "dir"
			         }
	};

//	gxdDataSource.maxCacheEntries = 3;
//	gxdDataSource.connXhrMode = "cancelStaleRequests";

	var paginator = mgiTab.createPaginator(
			[25,50,100,250], // rows per page options
			IMAGES_PAGE_SIZE // rows per page
	);

	// DataTable configurations
	var myConfigs = {
			paginator: paginator,
			dynamicData: true,
			initialLoad: false,
			MSG_LOADING: LOADING_IMG+' Searching...',
			MSG_EMPTY: 'No expression images found.'
	};

	// DataTable instance
	gxdDataTable = new YAHOO.widget.DataTable("imagesdata", myColumnDefs, gxdDataSource, myConfigs);

	// Show loading message while page is being rendered
	gxdDataTable.showTableMessage(gxdDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);

	mgiTab.initPaginator(gxdDataTable,paginator);

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
		History.navigate("gxd", generateRequest(sortedBy, 0, paginator.getRowsPerPage()));
	};
	gxdDataTable.sortColumn = handleSorting;

	gxdDataTable.doBeforeLoadData = function(oRequest, oResponse, oPayload) {
		var pRequest = parseRequest(oRequest);
		extractFilters(pRequest);
		var meta = oResponse.meta;
		oPayload.totalRecords = meta.totalRecords || oPayload.totalRecords;
		//   updateCount(oPayload.totalRecords);
		var filterCount = YAHOO.util.Dom.get('filterCount');
		if (!YAHOO.lang.isNull(filterCount)) {
			setText(filterCount, YAHOO.util.Number.format(oPayload.totalRecords, numConfig));
		}
		
		oPayload.sortedBy = {
				key: pRequest['sort'] || "assayType",
				dir: pRequest['dir'] ? "yui-dt-" + pRequest['dir'] : "yui-dt-desc"
					// Convert from server value to DataTable format
		};
		
		oPayload.pagination = {
				rowsPerPage: Number(pRequest['results']) || paginator.getRowsPerPage(),
				recordOffset: Number(pRequest['startIndex']) || 0
		};

		return true;
	};


	// Add tooltips to the camera icons
	gxdDataTable.subscribe('postRenderEvent', addCameraIconTooltips);

	return {"datatable": gxdDataTable, "datasource": gxdDataSource};
};


//Initialize the data table to the results table and
//Register the module with the browser history manager

window.gxdDataTable = gxdResultsTable().datatable;
History.register("gxd", History.getBookmarkedState("gxd") || "", handleNavigation);


/**
 * Configure the structure by stage matrix
 *
 * Rendering and logic details are in gxd_summary_matrix.js
 */
var currentStageGrid;
window.prevStageGridQuery="";

var structureStageGrid = function()
{
	// hide page controls
	$(".yui-pg-container").hide()

	var querystringWithFilters = getQueryStringWithFilters();
	if(querystringWithFilters==window.prevStageGridQuery)
	{
		// don't refresh grid if query is the same.
		return;
	}
	window.prevStageGridQuery=querystringWithFilters;

	var buildGrid = function()
	{
		if (typeof getQueryString == 'function') window.querystring = getQueryString().replace('&idFile=&', '&');

		currentStageGrid = GxdTissueMatrix({
			target : "sgTarget",
			// the datasource allows supergrid to make ajax calls for the initial data,
			// 	as well as subsequent calls for expanding rows
			dataSource: {
				url: fewiurl + "gxd/stagegrid/json?" + querystringWithFilters,
				batchSize: 50000,
				offsetField: "startIndex",
				limitField: "results",
				MSG_LOADING: LOADING_IMG+' Searching for data (may take a couple minutes for large datasets)...',
				MSG_EMPTY: 'No assay results with expression data found.'
			},
			cellSize: 28,
			cellRenderer: GxdRender.StructureStageCellRenderer,
			columnRenderer: GxdRender.TSColumnRenderer,
			verticalColumnLabels: true,
	        columnSort: function(a,b){
	        	var aint = parseInt(a.cid);
	        	var bint = parseInt(b.cid);
	        	if(aint>bint) return 1;
	        	else if(aint<bint) return -1;
	        	return 0;
	        },
	        openCloseStateKey: "sg_"+querystring,
	        legendClickHandler: function(e){ structMatrixLegendPopupPanel.show(); },
	        filterSubmitHandler: function(rows,cols)
	        {
	        	var newFacets = window.facets;
	        	var rowIds = [];
	        	rows.forEach(function(row){
	        		rowIds[rowIds.length] = row.rowId;
	        	});

				var colIds = [];
				cols.forEach(function(col){
					colIds[colIds.length] = col.cid;
				});

	        	if (rowIds.length > 0)
	        	{
	        		newFacets["structureIDFilter"] = rowIds;
	        	}
	        	if (colIds.length > 0)
	        	{
	        		newFacets["theilerStageFilter"] = colIds;
	        	}
	        	submitFacets(newFacets);
	        },
	        renderCompletedFunction: function()
	        {
	        	//When matrix is drawn/redrawn we resize it with margins, to fit the browser window
	        	makeMatrixResizable("sgTarget",40,60);
	        	if (SHOW_MATRIX_LEGENDS) {
	        		structMatrixLegendPopupPanel.show();
	        	}
	        }
	    });
	}


	if(currentStageGrid)
	{
		currentStageGrid.cancelDataSource();
	}
	buildGrid();
}

/**
 * Configure the structure by gene matrix
 *
 * Rendering and logic details are in gxd_summary_matrix.js
 */
var currentGeneGrid;
window.prevGeneGridQuery="";

var structureGeneGrid = function()
{
	// hide page controls
	$(".yui-pg-container").hide()

	var querystringWithFilters = getQueryStringWithFilters();
	if(querystringWithFilters==window.prevGeneGridQuery)
	{
		// don't refresh grid if query is the same.
		return;
	}
	window.prevGeneGridQuery=querystringWithFilters;

	var buildGrid = function()
	{
		if (typeof getQueryString == 'function') window.querystring = getQueryString().replace('&idFile=&','&');

		currentGeneGrid = GxdTissueMatrix({
			target : "ggTarget",
			// the datasource allows supergrid to make ajax calls for the initial data,
			// 	as well as subsequent calls for expanding rows
			dataSource: {
				url: fewiurl + "gxd/genegrid/json?" + querystringWithFilters,
				batchSize: 50000,
				offsetField: "startIndex",
				limitField: "results",
				MSG_LOADING: LOADING_IMG+' Searching for data (may take a couple minutes for large datasets)... ' +
				'<br/>If the dataset contains thousands of genes, the Tissue x Gene Matrix may not load. ' +
				'<br/>Consider filtering the data set.',
				MSG_EMPTY: 'No assay results with expression data found.'
			},
			cellSize: 24,
			cellRenderer: GxdRender.StructureGeneCellRenderer,
			columnSort: function(a,b){ return FewiUtil.SortSmartAlpha(a.cid,b.cid);},
			verticalColumnLabels: true,
	        openCloseStateKey: "gg_"+querystring,
	        legendClickHandler: function(e){ geneMatrixLegendPopupPanel.show(); },
	        filterSubmitHandler: function(rows,cols)
	        {
	        	var newFacets = window.facets;
	        	var rowIds = [];
	        	rows.forEach(function(row){
	        		rowIds[rowIds.length] = row.rowId;
	        	});
	        	var colIds = [];
	        	cols.forEach(function(col){
	        		colIds[colIds.length] = col.cid;
	        	});

	        	if (rowIds.length > 0)
	        	{
	        		newFacets["structureIDFilter"] = rowIds;
	        	}
	        	if (colIds.length > 0)
	        	{
	        		newFacets["markerSymbolFilter"] = colIds;
	        	}
	        	submitFacets(newFacets);
	        },
	        renderCompletedFunction: function()
	        {
	        	//When matrix is drawn/redrawn we resize it with margins, to fit the browser window
	        	makeMatrixResizable("ggTarget",40,40);
	        	if (SHOW_MATRIX_LEGENDS) {
	        		geneMatrixLegendPopupPanel.show();
	        	}
	        }
	    });
	}



	if(currentGeneGrid)
	{
		currentGeneGrid.cancelDataSource();
	}
	buildGrid();
}

//popup for gene matrix legend
window.geneMatrixLegendPopupPanel = new YAHOO.widget.Panel("geneLegendPopupPanel",
		{ width:"260px", visible:false, constraintoviewport:true,
			context:['tabSummaryContent', 'tl', 'tr',['beforeShow','windowResize']]
});
window.geneMatrixLegendPopupPanel.render();

//popup for structure matrix legend
window.structMatrixLegendPopupPanel = new YAHOO.widget.Panel("structLegendPopupPanel",
		{ width:"260px", visible:false, constraintoviewport:true,
			context:['tabSummaryContent', 'tl', 'tr',['beforeShow','windowResize']]
});
window.structMatrixLegendPopupPanel.render();


// Handle legend default behavior
window.SHOW_MATRIX_LEGENDS = true;

var closeEl = YAHOO.util.Dom.getElementsByClassName("container-close", null, 
		structMatrixLegendPopupPanel.element)[0];
	
YAHOO.util.Event.on(closeEl, "click", function(){
	SHOW_MATRIX_LEGENDS = false;
});

var closeEl = YAHOO.util.Dom.getElementsByClassName("container-close", null, 
		geneMatrixLegendPopupPanel.element)[0];
	
YAHOO.util.Event.on(closeEl, "click", function(){
	SHOW_MATRIX_LEGENDS = false;
});


//Handle the initial state of the page through history manager
function historyInit()
{
	// try to see if tab was specified in querystring
	// and reset querystring if it was
	var queryTabParam = mgiTab.resolveTabParam();

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
			window.querystring = getQueryString().replace('&idFile=&', '&');

		handleNavigation(currentState);
	}
	else
	{
		// switch to querystring specified tab
		if (queryTabParam && queryTabParam in mgiTab.tabs) resultsTabs.set("activeIndex",mgiTab.tabs[queryTabParam]);
		handleNavigation(generateRequest(null, 0, RESULTS_PAGE_SIZE));
	}
}
History.onReady(historyInit);


//Initialize the YUI browser history manager control

History.initialize("yui-history-field", "yui-history-iframe");

window.addEventListener("pageshow", function(evt){
    if(evt.persisted){
    setTimeout(function(){
        window.location.reload();
    },10);
}
}, false);
