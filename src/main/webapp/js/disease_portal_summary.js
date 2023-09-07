// --------- some global configurable variables ----------

// The YUI components required for setting up the data table and
// source to do the AJAX call
var defaultSort = "";

//default page size for each summary
var GRID_PAGE_SIZE = 250;
var GENES_PAGE_SIZE = 250;
var DISEASES_PAGE_SIZE = 250;
var DEFAULT_PAGE_SIZE = GENES_PAGE_SIZE;

// number of rows to be displayed for the currently selected tab
var CURRENT_PAGE_SIZE = GRID_PAGE_SIZE;

// Shortcut variable for the YUI history manager
var History = YAHOO.util.History;

// HTML/YUI page widgets
YAHOO.namespace("hdp.container");

YAHOO.hdp.container.panel1 = new YAHOO.widget.Panel(
	"showMgiHumanGenesHelpDivDialog", {
		width : "420px",
		visible : false,
		constraintoviewport : true,
		context : [ 'showMgiHumanGenesHelpDiv', 'tr', 'bl' ]
	});
YAHOO.hdp.container.panel1.render();

// Instantiate a Panel from markup
YAHOO.hdp.container.panel2 = new YAHOO.widget.Panel(
	"showMgiHumanDiseaseHelpDivDialog", {
		width : "420px",
		visible : false,
		constraintoviewport : true,
		context : [ 'showMgiHumanDiseaseHelpDiv', 'tr', 'bl' ]
	});
YAHOO.hdp.container.panel2.render();

YAHOO.util.Event.addListener("showMgiHumanGenesHelpDiv", "click", showPanel1);
YAHOO.util.Event.addListener("showMgiHumanDiseaseHelpDiv", "click", showPanel2);

// ------ tab definitions + functions ------------
var mgiTab = new MGITabSummary({
	"tabViewId":"resultSummary",
	"tabIds":["gridtab","genestab","diseasestab"],
	"pageSizes":[GRID_PAGE_SIZE,GENES_PAGE_SIZE,DISEASES_PAGE_SIZE], // mirrors "tabIds"
	"historyId":"hdp"
});

// TODO: refactor these to use the mgiTab.summaryTabs object instead of resultsTabs
var resultsTabs = mgiTab.summaryTabs;
var getCurrentTab = mgiTab.getCurrentTab;

/*
 * Some helper functions for dealing with YUI history manager
 * and building AJAX requests
 */

// TODO: refactor these functions to use the shared lib names (i.e. mgiParseRequest() instead of parseRequest())
var parseRequest=mgiParseRequest;
// need to add grid filters to the generateRequest function via extraParams
mgiTab._rp.getExtraParams = function()
{
	var params = [];
	var currentTab = mgiTab.getCurrentTab();
	if(currentTab) params.push("tab="+currentTab);

//	if(_GFilters && _GFActive)
//	{
//		if(_GFilters.fGene) params.push("fGene="+(_GFilters.fGene.join("|")));
//		if(_GFilters.fHeader) params.push("fHeader="+(_GFilters.fHeader.join("|")));
//	}

	return params.join("&");
}
var generateRequest = mgiTab._rp.generateRequest;


// --------- History Navigation functions ------------------
// a global variable to help the summary know when to generate a new datatable
var previousQueryString = "";
var previousGAState = "";
var previousFilterState = "";

// Called by Browser History Manager to trigger a new state
handleNavigation = function (request, calledLocally, fromInit)
{
	if (calledLocally==undefined) calledLocally = false;
	if (fromInit==undefined) fromInit = false;

	var filterState = hmdcFilters.getUrlFragment();

	// we need the tab state of the request
	var values = mgiParseRequest(request);
	var tabState = values['tab'];
	if($.isArray(tabState)) tabState = tabState[0];

	hmdcFilters.callbacksOff();

	if (!calledLocally) {
	    hmdcFilters.setAllFilters(values);
	}

	var currentTab = getCurrentTab();

	var foundParams = true;
	// test if there is a form that needs to be populated
	if (typeof reverseEngineerFormInput == 'function')
		 foundParams = reverseEngineerFormInput(request);

	//Set the global querystring parameter for later navigation
	// if there is no getQueryString function, we assume that window.querystring is already set
	if (typeof getQueryString == 'function')
		window.querystring = getQueryString();

	hmdcFilters.callbacksOn();

//	// Handle proper behavior for back and forward navigation
//	// if we have no tab state in the request, then we won't try to switch tabs.
	if(tabState && (currentTab != tabState)) {
		resultsTabs.selectTab(mgiTab.tabs[tabState]);
		return;
	}

	var doGrid = false;
	if (tabState == "gridtab") {
		doGrid = true;
	}
	else if (tabState == "genestab") {
		genesResultsTable();
	} else if(tabState == "diseasestab"){
		diseasesResultsTable();
	}
	else {
		// default catch all
		//genesResultsTable();
		doGrid = true;
	}

	if(!foundParams && fromInit)
	{
		// this is how we handle an empty request.
		if(typeof closeSummaryControl == 'function')
			closeSummaryControl();
	}
	else
	{
		var doNewQuery = (querystring != previousQueryString) || _GF.submitActive || (previousFilterState != filterState);
		_GF.submitActive=false;

		if(doNewQuery)
		{
			// reset the max disease col field (default is 100)
			$("#numDCol").val("100");
		}

		// only reset the previousQueryString if there is a query to do
		previousQueryString = querystring;
		previousFilterState = filterState;

		if (typeof openSummaryControl == 'function')
			openSummaryControl();

		if(doGrid)
		{
			generateGrid(request);
		}
		else
		{
			hdpDataTable.showTableMessage(hdpDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);

			// Sends a new request to the DataSource
			hdpDataSource.sendRequest(request,{
				success : hdpDataTable.onDataReturnSetRows,
				failure : hdpDataTable.onDataReturnSetRows,
				scope : hdpDataTable,
				argument : {} // Pass in container for population at runtime via doBeforeLoadData
			});
		}
		if (doNewQuery)
		{
			// set the gridState for filters to postSubmit
			_GF.setState(_GF.gridState.postSubmit);

			// Update the "you searched for" text
			if (typeof updateQuerySummary == 'function')
				updateQuerySummary();

			// refresh tab counts
			refreshTabCounts();

			// wire up any download buttons
			var markersTextReportButton = YAHOO.util.Dom.get('markersTextDownload');
			if (!YAHOO.lang.isNull(markersTextReportButton)) {
				markersTextReportButton.setAttribute('href', fewiurl + 'diseasePortal/marker/report.txt?' + querystring + filterState);
			}
			var diseaseTextReportButton = YAHOO.util.Dom.get('diseaseTextDownload');
			if (!YAHOO.lang.isNull(diseaseTextReportButton)) {
				diseaseTextReportButton.setAttribute('href', fewiurl + 'diseasePortal/disease/report.txt?' + querystring + filterState);
			}
		}

		// Shh, do not tell anyone about this. We are sneaking in secret Google Analytics calls, even though there is no approved User Story for it.
		var GAState = "/diseasePortal/summary/"+tabState+"?"+querystring + filterState;
		if(GAState != previousGAState)
		{
			ga_logPageview(GAState);
			previousGAState = GAState;
		}
	}
};

var lastGridRequest;
var generateGrid = function(request)
{
	lastGridRequest = request;
	$("#griddata").html("");
	$("#griddata_loading").show();
	// make request and populate the grid content
	var hdpGridAjaxUrl = fewiurl + "diseasePortal/grid";
	MGIAjax.postLoadContent(hdpGridAjaxUrl, "griddata",request);


	/*
	 * We need to fire a query to get the total count for the grid.
	 * 	Then, we can use that count to initialize the paginator
	 */
	var handleGridCountRequest = function(o)
	{
		if(o.responseText == "-1") o.responseText = "0"; // set count to zero if errors

		if(o.tId==gridRq.tId)
		{
			// grid count
			var totalCount = Number(o.responseText);

			// set up the paginator controls for the grid
			var pRequest = parseRequest(lastGridRequest);
			var paginator = mgiTab.createPaginator(
						[250,500,1000], // rows per page options
						Number(pRequest['results']) || GRID_PAGE_SIZE, // rows per page
						totalCount, // totalCount
						Number(pRequest['startIndex']) || 0 // recordOffset
			);

			CURRENT_PAGE_SIZE = Number(pRequest['results']) || GRID_PAGE_SIZE;

			var handlePagination = function(state)
			{
				// The next state will reflect the new pagination values
				var pRequest = parseRequest(lastGridRequest);
				// preserve any sorting that may be in the last request
				var sortedBy = null;
				if(pRequest["sort"])
				{
					sortedBy = {
						dir: pRequest["dir"],
						key: pRequest["sort"]
					};
				}
				var newState = mgiTab._rp.generateRequest(sortedBy, state.recordOffset, state.rowsPerPage, hmdcFilters.getFacets());
				// Pass the state along to the Browser History Manager
				History.navigate(mgiTab.historyMgrId, newState);
			};
			paginator.subscribe('changeRequest',handlePagination);
			paginator.render();
		}
	}
	gridRq = YAHOO.util.Connect.asyncRequest('POST', fewiurl+"diseasePortal/grid/totalCount",
		    {	success:handleGridCountRequest,
		    	failure:function(o){}
		    },querystring + hmdcFilters.getUrlFragment());
}


function StateHandler(states)
{
	this.states=states;
	for(var i=0; i<states.length; i++)
	{
		var state = states[i];
		this[state] = state;
		// set the current state to be the first one
		if(i==0) this.state = state;
	}
}
//------ functions for handling the grid filters ------------
// ----- class for handling filter state -----
function GridFilter()
{
	// constants
	this.filterIndicatorId = "filterReset";
	this.geneFilterId = "fGene";
	this.headerFilterId = "fHeader";
	this.featureTypeId = "featureTypeFilter";
	this.fields = [this.geneFilterId,this.headerFilterId];
//	this.fields = [this.geneFilterId,this.headerFilterId,this.featureTypeId];
	this.filterDelim = "|";
	this.highlightCssClass = "gridHl";
	this.checkBoxClass = "gridCheck";
	this.reverseId = "reverseF";

	// state variables
	this.filtersActive = false;
	this.submitActive = false;
	this.prevFilterQuery = null;
	this.reverse = false;
	
	/*
	 * States are as follows::
	 * 	initial - only set on page load
	 * 	working - set when user starts clicking checkboxes
	 * 	preSubmit - user hits submit, but query has yet to fire
	 * 	postSubmit - query has fired, but user has not clicked any more checkboes
	 */
	this.gridState = new StateHandler(["initial","working","preSubmit","postSubmit"]);
	this.fieldStates = {};
	for(var i=0; i<this.fields.length; i++)
	{
		var field = this.fields[i];
		this.fieldStates[field] = new StateHandler(["postSubmit","working"]);
	}

	this.setState = function(state)
	{
		this.gridState.state = state;
		// if postSubmit, then set the individual fieldStates as well.
		if(state == this.gridState.postSubmit)
		{
			for(var i=0; i<this.fields.length; i++)
			{
				var field = this.fields[i];
				this.setFieldState(field,state);
			}
		}
	}
	this.isState = function(state) { return this.gridState.state == state; }
	this.setFieldState = function(field,state)
	{
		if(field in this.fieldStates) this.fieldStates[field].state = state;
	}
	this.isFieldState = function(field,state)
	{
		if(field in this.fieldStates) return this.fieldStates[field].state == state;
	}

	// define closure for functions
	var _self = this;

	// getter functions for the input fields
	// 	REQUIRES: hidden input fields with the above names (fGene and fHeader)
	this.getField = function(fieldName)
	{
		var fJq = $("[name="+fieldName+"]");
		if(fJq.length>0)
		{
			return $(fJq[0]);
		}
		return null;
	}

	this.setReverse = function(trueOrFalse)
	{
		_self.reverse = trueOrFalse;
		if(_self.reverse) $("#"+_self.reverseId).val("true");
		else  $("#"+_self.reverseId).val("");
	}
	
	// returns an object representing the hidden fields
	// RETURN FORMAT: {fGene : [filter1,filter2,...], fHeader : [filter1,filter2,...]}
	this.getFiltersObject = function()
	{
		var filtersObject = {};
		for(var i=0; i<_self.fields.length; i++)
		{
			var fieldName = _self.fields[i];
			var field = _self.getField(fieldName);
			if(field)
			{
				var value = field.attr("value");
				if(value && value!="")
				{
					var filters = value.split(_self.filterDelim);
					filtersObject[fieldName] = filters;
				}
				else filtersObject[fieldName] = [];
			}
		}
		return filtersObject;
	}

//	this.isFilterActive = function(fieldName,filterValue)
//	{
//		var filtersObj = _self.getFiltersObject();
//		if(fieldName in filtersObj)
//		{
//			var filters = filtersObj[fieldName];
//			return $.inArray(filterValue,filters)>=0;
//		}
//	}
	/*
	 * Goes through all active filters, then reselects any that are on the visible grid
	 */
	this.reselectActiveBoxes = function()
	{
		if(_self.isState(_self.gridState.working))
		{
			var filtersObj = _self.getFiltersObject();
			for(fieldName in filtersObj)
			{
				if($.inArray(fieldName,_self.fields)>=0)
				{
					var filters = filtersObj[fieldName];
					for(var i=0; i<filters.length; i++)
					{
						var filterValue = filters[i];
						if(filterValue)
						{
							var boxes = $("[filter="+fieldName+"]").filter("[value='"+filterValue+"']");
							if(boxes.length>0)
							{
								var box = $(boxes[0]);
								// not sure how to avoid the double checked=true
								// because of the order of event handlers, the checked flag needs to be set first,
								// 	however, calling click() somehow also toggles the check symbol.
								// hence, we have double checked=true calls.
								box.prop("checked",true);
								box.click();
								box.prop("checked",true);
							}
						}
					}
				}
			}
		}
	}

	this.toggleFiltersIndicator = function(show)
	{
//		var filterIndicatorDiv = $("#"+_self.filterIndicatorId);

		/* If we are to show this row/col filter button, then we know
		 * we also need to show its containing DIV.
		 * If we are to hide it, then we only hide the containing DIV
		 * if there are no other filters.
		 */
		if(show==true) {
		    filters.setHmdcButtonVisible(true);
//		    filterIndicatorDiv.show();
//		    $("#filterSummary").show();
		} else if (hmdcFilters.getUrlFragment() == '') {
//		    filterIndicatorDiv.hide();
		    filters.setHmdcButtonVisible(false);
		}
		filters.populateFilterSummary();
	}

	// save the modified filters object to the hidden input fields
	// FORMAT (same object as above): {fGene : [filter1,filter2,...], fHeader : [filter1,filter2,...]}
	this.saveFiltersObject = function(filtersObject)
	{
		if(filtersObject)
		{
			for(var i=0; i<_self.fields.length; i++)
			{
				var fieldName = _self.fields[i];
				var field = _self.getField(fieldName);
				if(field && fieldName in filtersObject && filtersObject[fieldName])
				{
					var value = filtersObject[fieldName].join(_self.filterDelim);
					field.attr("value",value);
				}
			}
		}
	}

	// reset/clear the hidden input fields
	this.resetFields = function()
	{
		for(var i=0; i<_self.fields.length; i++)
		{
			var field = _self.getField(_self.fields[i]);
			if(field)
			{
				field.attr("value","");
			}
		}
		 $("#"+_self.reverseId).val("");
	}

	// check the hidden inputs to see if fields have been set yet
	this.isFieldsSet = function()
	{
		//window.querystring=getQueryString();

		for(var i=0; i<_self.fields.length; i++)
		{
			var fieldName = _self.fields[i];
			var field = _self.getField(fieldName);
			if(field)
			{
				var value = field.attr("value");
				if(value && value!="")
				{
					return true;
				}
			}
		}
		return false;
	}

	this.inactivate = function()
	{
		_self.filtersActive = false;
	}

	this.init = function()
	{
		_self.resetFields();
		_self.inactivate();
	}

	// submit the current filters (if there are any)
	this.submit = function(doReset)
	{
		if(_self.isFieldsSet() || doReset)
		{
			if(doReset)
			{
				_self.init();
				window.querystring = getQueryString();
			}

			mgiTab.newQueryState = true;

			if(typeof resultsTabs != 'undefined')
			{
				_self.setState(_self.gridState.preSubmit);
				_self.filtersActive = true;
				_self.submitActive=true;
				window.querystring=getQueryString();

				// selecting the grid tab initiates the proper sequence of events for submitting a new query
				resultsTabs.selectTab(0);
			}
		}
		else
		{
			_self.inactivate();
		}
	}
	this.resetFilters = function()
	{
		_self.submit(true);
		_self.init();
		_self.filtersActive=false;
	}

	this.highlightField = function(checkBox,fieldName)
	{
		if(checkBox)
		{
			if(fieldName == "fGene")
			{
				// toggle row class
				// get tr
				checkBox.parent().parent().toggleClass(_self.highlightCssClass);
			}
			else if(fieldName == "fHeader")
			{
				// toggle column class
				var colCss = checkBox.attr("colid");
				if(colCss)
				{
					$("."+colCss).toggleClass(_self.highlightCssClass);
				}
			}
		}
	}

	// event handler for clicking a filter checkbox
	this.gridCheckClick = function(e)
	{
		var checkBox = $(this);
		var fieldName = checkBox.attr("filter");

		_self.highlightField(checkBox,fieldName);

		var filtersObject = _self.getFiltersObject();
		if(filtersObject[fieldName])
		{
			var filters = filtersObject[fieldName];
			var value = checkBox.attr("value");
			if(_self.isFieldState(fieldName,_self.gridState.postSubmit))
			{
				// if this is post submit, clear this field, but not the other.
				filters = [];
				_self.setFieldState(fieldName,_self.gridState.working);
				_self.setState(_self.gridState.working);
			}

			// either add or remove this filters depending on the "checked" attribute
			var inArrayIndex = $.inArray(value, filters);
			if(inArrayIndex >= 0) filters.splice( inArrayIndex, 1 );

			if(checkBox.is(":checked")) filters.push(value);

			filtersObject[fieldName] = filters;
		}
		_self.saveFiltersObject(filtersObject);
	}
}
_GF = new GridFilter();
_GF.init();

function isGFActive()
{
	return _GF.filtersActive;
}
function isGFiltersSet()
{
	return _GF.isFieldsSet();
}
// click handler for submit filters button
function gridFilterSubmitClick(e)
{
	_GF.submit();
}
//click handler for submit reverse filters button
function gridRevereseFilterSubmitClick(e)
{
	_GF.setReverse(true);
	_GF.submit();
}
// click handler for reset button
function resetFiltersClick(e)
{
	_GF.resetFilters();
}
// click handler for the gridCheck filters
var gridCheckClick = _GF.gridCheckClick;

// function for resetting the disease column limit
function gridMoreDiseasesClick(e)
{
	$("#numDCol").val("2000");
	window.querystring=getQueryString();

	// selecting the grid tab initiates the proper sequence of events for submitting a new query
	resultsTabs.selectTab(0);
}


// ------- Function for handling the tab counts --------
var refreshTabCounts = function()
{
	 //get the tab counts via ajax
	var handleCountRequest = function(o)
	{
		if(o.responseText == "-1") o.responseText = "0"; // set count to zero if errors

		// resolve the request ID to its appropriate handler
		if(o.tId==genesRq.tId) YAHOO.util.Dom.get("totalGenesCount").innerHTML = o.responseText;
		else if(o.tId==diseasesRq.tId) YAHOO.util.Dom.get("totalDiseasesCount").innerHTML = o.responseText;

	}

	// clear these until the data comes back
	YAHOO.util.Dom.get("totalGenesCount").innerHTML = "";
	YAHOO.util.Dom.get("totalDiseasesCount").innerHTML = "";

	genesRq = YAHOO.util.Connect.asyncRequest('POST', fewiurl+"diseasePortal/markers/totalCount",
    {	success:handleCountRequest,
    	failure:function(o){}
    },querystring + hmdcFilters.getUrlFragment());

	diseasesRq = YAHOO.util.Connect.asyncRequest('POST', fewiurl+"diseasePortal/diseases/totalCount",
    {	success:handleCountRequest,
    	failure:function(o){}
    },querystring + hmdcFilters.getUrlFragment());
}

//----------- functions for configuring the datatables -------------------
//
//genes results table population function
//
var hdpDataTable;
var hdpDataSource;
var genesResultsTable = function() {

	var numConfig = {thousandsSeparator: ','};

	// Column definitions
	var columnDefs = [
	    //{key: "highlightedFields", label: "Why Matched", sortable: false },
		{key: "organism", label: "Organism", sortable: true },
		{key: "symbol", label: "Gene Symbol", sortable: true },
		{key: "location", label: "Genetic Location", sortable: false },
		{key: "coordinate", label: "Genome Coordinates", sortable: true },
		{key: "disease", label: "Associated Human Diseases (<span id=\"showMgiHumanGenesHelpDiv\" style=\"color: #06F;cursor: pointer;\">source</span>)", sortable: false, width:240 },
		{key: "system", label: "Abnormal Mouse Phenotypes<br/> Reported in these Systems", sortable: false },
		{key: "allRefCount", label: "References in MGI", sortable: false },
		{key: "imsrCount", label: "Mice With Mutations<br/> In this Gene (IMSR)", sortable: false },
		{key: "score",label: "score",sortable: false,hidden: true}
	];

	// DataSource instance
	hdpDataSource = new YAHOO.util.XHRDataSource(fewiurl + "diseasePortal/markers/json?");
	hdpDataSource.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
	hdpDataSource.connMethodPost = true;
	hdpDataSource.responseSchema = {
		resultsList: "summaryRows",
		fields: [
		    //{key: "highlightedFields"},
			{key: "organism"},
			{key: "symbol"},
			{key: "location"},
			{key: "coordinate"},
			{key: "disease"},
			{key: "system"},
			{key: "allRefCount"},
			{key: "imsrCount"},
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

	hdpDataSource.maxCacheEntries = 3;
	hdpDataSource.connXhrMode = "cancelStaleRequests";


	var paginator = mgiTab.createPaginator(
			[250,500,1000], // rows per page options
			GENES_PAGE_SIZE // rows per page
	);

	// DataTable configurations
	var configs = {
	   paginator: paginator,
	   dynamicData: true,
	   initialLoad: false,
	   MSG_LOADING: '<img src="/fewi/mgi/assets/images/loading.gif" height="24" width="24"> Searching...',
	   MSG_EMPTY: 'No genes found.'
	};

	// DataTable instance
	hdpDataTable = new YAHOO.widget.DataTable("genesdata", columnDefs, hdpDataSource, configs);

	// Show loading message while page is being rendered
	hdpDataTable.showTableMessage(hdpDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);

	mgiTab.initPaginator(hdpDataTable,paginator);

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
	   History.navigate("hdp", generateRequest(sortedBy, 0, paginator.getRowsPerPage(), hmdcFilters.getFacets()));
	};
	hdpDataTable.sortColumn = handleSorting;


	hdpDataTable.doBeforeLoadData = function(oRequest, oResponse, oPayload) {
		var pRequest = parseRequest(oRequest);
		var meta = oResponse.meta;
		oPayload.totalRecords = meta.totalRecords || oPayload.totalRecords;
		//   updateCount(oPayload.totalRecords);
		var filterCount = YAHOO.util.Dom.get('filterCount');
		if (!YAHOO.lang.isNull(filterCount)) {
			setText(filterCount, YAHOO.util.Number.format(oPayload.totalRecords, numConfig));
		}

		oPayload.sortedBy = {
			key: (pRequest['sort']) || "symbol",
			dir: (pRequest['dir']) ? "yui-dt-" + pRequest['dir'] : "yui-dt-desc"
				// Convert from server value to DataTable format
		};

		oPayload.pagination = {
			rowsPerPage: Number(pRequest['results']) || paginator.getRowsPerPage(),
			recordOffset: Number(pRequest['startIndex']) || 0
		};

		CURRENT_PAGE_SIZE = Number(pRequest['results']) || paginator.getRowsPerPage();

		// add jQuery UI tooltips for genes
		refreshJQTooltips();
//		hdpDataTable.subscribe("postRenderEvent",function(o){
//			$(".jquiTT").tooltip({
//			  content: function (callback) {
//			     callback($(this).prop('title'));
//			  },
//			  tooltipClass: "tooltip",
//			  hide: {duration:0},
//			  show: {duration:0}
//			});
//		});
		// Ignore this for now. Just me playing around with jquery "teaser" text for systems
		// -Kstone
		// use jquery UI to make nice HTML enabled tooltips
//		hdpDataTable.subscribe("postRenderEvent",function(o){
//			var systemCells = $("td.yui-dt-col-system div");
//			systemCells.each(function(index,el){
//				// save the original full HTML
//				var max = 80;
//				var original = $(el).html();
//				if(original.length>max)
//				{
//					$(el).css("cursor","pointer");
//					$.data(el,"fullHtml",original);
//					$.data(el,"shortened",true);
//					var short = original.slice(0,max)+"...";
//					$.data(el,"shortHtml",short);
//					// shorten it
//					$(el).html(short);
//					$(el).click(function(e){
//						// toggle between short and original
//						var isShortened = $.data(this,"shortened");
//						if(isShortened)
//						{
//							$(this).html($.data(this,"fullHtml"));
//							$.data(this,"shortened",false);
//						}
//						else
//						{
//							$(this).html($.data(this,"shortHtml"));
//							$.data(this,"shortened",true);
//						}
//
//					});
//				}
//			});
//		});

		return true;
	};

	YAHOO.util.Event.addListener("showMgiHumanGenesHelpDiv", "click", showPanel1);
	YAHOO.util.Event.addListener("showMgiHumanDiseaseHelpDiv", "click", showPanel2);
};
var diseasesResultsTable = function() {

	var numConfig = {thousandsSeparator: ','};

	// Column definitions
	var columnDefs = [
	// sortable:true enables sorting
	    //{key: "highlightedFields", label: "Why Matched", sortable: false },
		{key: "disease", label: "Disease", sortable: true },
		{key: "diseaseId", label: "DO ID", sortable: true },
		{key: "diseaseModels", label: "Mouse Models", sortable: false },
		{key: "mouseMarkers", label: "Associated Genes from Mouse Models", sortable: false },
		{key: "humanMarkers", label: "Associated Human Genes (<span id=\"showMgiHumanDiseaseHelpDiv\" style=\"color: #06F;cursor: pointer;\">source</span>)", sortable: false },
		{key: "refCount", label: "References using <br/>Mouse Models", sortable: false },
		{key: "score",label: "score",sortable: false,hidden: true}
	];

	// DataSource instance
	hdpDataSource = new YAHOO.util.XHRDataSource(fewiurl + "diseasePortal/diseases/json?");
	hdpDataSource.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
	hdpDataSource.connMethodPost = true;
	hdpDataSource.responseSchema = {
		resultsList: "summaryRows",
		fields: [
		    //{key: "highlightedFields"},
			{key: "disease"},
			{key: "diseaseId"},
			{key: "diseaseModels"},
			{key: "mouseMarkers"},
			{key: "humanMarkers"},
			{key: "refCount"},
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

	hdpDataSource.maxCacheEntries = 3;
	hdpDataSource.connXhrMode = "cancelStaleRequests";

	var paginator = mgiTab.createPaginator(
			[250,500,1000], // rows per page options
			GENES_PAGE_SIZE // rows per page
	);

	// DataTable configurations
	var configs = {
	   paginator: paginator,
	   dynamicData: true,
	   initialLoad: false,
	   MSG_LOADING: '<img src="/fewi/mgi/assets/images/loading.gif" height="24" width="24"> Searching...',
	   MSG_EMPTY: 'No diseases found.'
	};

	// DataTable instance
	hdpDataTable = new YAHOO.widget.DataTable("diseasesdata", columnDefs, hdpDataSource, configs);

	// Show loading message while page is being rendered
	hdpDataTable.showTableMessage(hdpDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);

	// we need to tie some functions of the paginator with the instantiated data table object
	mgiTab.initPaginator(hdpDataTable,paginator);

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
	   History.navigate("hdp", generateRequest(sortedBy, 0, paginator.getRowsPerPage(), hmdcFilters.getFacets()));
	};
	hdpDataTable.sortColumn = handleSorting;

	hdpDataTable.doBeforeLoadData = function(oRequest, oResponse, oPayload) {
		var pRequest = parseRequest(oRequest);
		var meta = oResponse.meta;
		oPayload.totalRecords = meta.totalRecords || oPayload.totalRecords;
		//   updateCount(oPayload.totalRecords);
		var filterCount = YAHOO.util.Dom.get('filterCount');
		if (!YAHOO.lang.isNull(filterCount)) {
			setText(filterCount, YAHOO.util.Number.format(oPayload.totalRecords, numConfig));
		}

		oPayload.sortedBy = {
			key: (pRequest['sort']) || "disease",
			dir: (pRequest['dir']) ? "yui-dt-" + pRequest['dir'] : "yui-dt-desc"
				// Convert from server value to DataTable format
		};

		oPayload.pagination = {
			rowsPerPage: Number(pRequest['results']) || paginator.getRowsPerPage(),
			recordOffset: Number(pRequest['startIndex']) || 0
		};

		CURRENT_PAGE_SIZE = Number(pRequest['results']) || paginator.getRowsPerPage();
		return true;
	};

	YAHOO.util.Event.addListener("showMgiHumanGenesHelpDiv", "click", showPanel1);
	YAHOO.util.Event.addListener("showMgiHumanDiseaseHelpDiv", "click", showPanel2);
};

genesResultsTable();
History.register("hdp", History.getBookmarkedState("hdp") || "", handleNavigation);


// ------ functions for handling alternative entries to the summary page (e.g. from a bookmark, or to a specific tab ------

//Handle the initial state of the page through history manager
function historyInit()
{
	// try to see if tab was specified in querystring
	// and reset querystring if it was
	var queryTabParam = mgiTab.resolveTabParam();

	// get the bookmarked state
	var currentState = History.getBookmarkedState("hdp");
	if(currentState)
	{
		// reset form values
		if (typeof reverseEngineerFormInput == 'function')
			reverseEngineerFormInput(currentState);

		// rebuild the global querystring
		// if there is no getQueryString function, we assume that window.querystring is already set
		if (typeof getQueryString == 'function')
			window.querystring = getQueryString();

		handleNavigation(currentState,true,true);
	}
	else
	{
		// switch to querystring specified tab
		if (queryTabParam && queryTabParam in mgiTab.tabs) resultsTabs.set("activeIndex",mgiTab.tabs[queryTabParam]);
		handleNavigation(generateRequest(null, 0, DEFAULT_PAGE_SIZE, hmdcFilters.getFacets()),true,true);
	}
};
History.onReady(historyInit);


//
// Initialize the YUI browser history manager control
//
History.initialize("yui-history-field", "yui-history-iframe");


function refreshJQTooltips()
{
	
	// use jquery UI to make nice HTML enabled tooltips
	setTimeout(function(){
			$(".jquiTT").parent().tooltip({
		  content: function (callback) {
		     callback($(this).prop('title'));
		  	},
		  tooltipClass: "tooltip",
		  show: null,
		  open: function(event, ui){
				if(typeof(event.originalEvent) === 'undefined')
				{
					return false;
				}
				var $id = $(ui.tooltip).attr('id');
				
				// close any lingering tooltips
				$('div.ui-tooltip').not('#' + $id).remove();
		  	}
		});
	},400);

};

function showPanel1() {
	YAHOO.hdp.container.panel1.cfg.setProperty("context",['showMgiHumanGenesHelpDiv','tr','bl']);
	YAHOO.hdp.container.panel1.show();
	if (ev) { 
		YAHOO.util.Event.stopEvent(ev); 
	} 
}

function showPanel2() {
	YAHOO.hdp.container.panel2.cfg.setProperty("context",['showMgiHumanDiseaseHelpDiv','tr','bl']);
	YAHOO.hdp.container.panel2.show();
	if (ev) { 
		YAHOO.util.Event.stopEvent(ev); 
	} 
}

$(function(){
	// put anything here that you definitally want to happen only after page is rendered.
	$("#filterReset").click(resetFiltersClick);
	filters.registerHmdcButton('filterReset', 'Remove row/column filters',
	    'click to remove row/column filters', false, resetFiltersClick);
});
