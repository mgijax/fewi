
// global variables needed to share objects between functions
var myDataSource;
var myDataTable;
var generateRequest;
var totalCount = 0;
var facets = {};
var numConfig = {thousandsSeparator: ','};

// Integrate with Browser History Manager
var History = YAHOO.util.History;

// this function populates the 'breadbox' div with current filters
// depends on the specific div structure found in the summary div
var populateFilterSummary = function () {
	// get the filterSummary div
	var fSum = YAHOO.util.Dom.get('filterSummary');
	if (!YAHOO.lang.isUndefined(fSum)){
		// get filterList, fCount and defaultText spans
		var filterList = new YAHOO.util.Element('filterList');
		var fCount = YAHOO.util.Dom.get('fCount');
		var defaultText = YAHOO.util.Dom.get('defaultText');
		// remove all elements inside filterList
		if(!YAHOO.lang.isNull(YAHOO.util.Dom.get('filterList'))){
			while (filterList.hasChildNodes()) {
				filterList.removeChild(filterList.get('firstChild'));
			}
			// add the Remove all button
		    clear = document.createElement("a");
		    clear.setAttribute('class', 'filterItem');
		    clear.setAttribute('id', 'clearFilter');
		    setText(clear, 'Remove All Filters');
		    filterList.appendChild(clear);
		    YAHOO.util.Event.addListener(clear, "click", clearFilter);	
		}
	    var vis = false;
	    // add a button for each facet value selected
	    for (k in facets) {
	    	// get a list of facets for a particular filter
	    	var inner = facets[k];
	    	// flag to add br tag between facet types
	    	var brTag = false;
        	list = facets[key];
        	// add a button for each facet value
			for(v=0; v < inner.length; v++) {
				YAHOO.util.Dom.setStyle(fSum, 'display', 'block');
	    		vis = true;
	    		brTag = true;
	            var el = document.createElement("a");
	            el.setAttribute('class', 'filterItem');
	            el.setAttribute('id', k + ':' + inner[v]);
	            var val = k.charAt(0).toUpperCase() + k.slice(1);
	            val = val.replace('Filter', '') + ': ' + inner[v].replace('*', ',');
	            setText(el, val);

	            filterList.appendChild(el);
	            YAHOO.util.Event.addListener(el, "click", clearFilter);
	            
	            filterList.appendChild(document.createTextNode(' '));
			}
			// add br tag an do next facet list
			if (brTag){
				filterList.appendChild(document.createElement("br"));
			}
	    }
	    // hide default text and show filters
		if (vis){
			if(!YAHOO.lang.isNull(fCount)){
				YAHOO.util.Dom.setStyle(fCount, 'display', 'block');
				YAHOO.util.Dom.setStyle(clear, 'display', 'inline');
			}
			if(!YAHOO.lang.isNull(defaultText)){
				YAHOO.util.Dom.setStyle(defaultText, 'display', 'none');
			}		
		} else {
		// hide filters and show default text
			if(!YAHOO.lang.isNull(fCount)){
				YAHOO.util.Dom.setStyle(fCount, 'display', 'none');
				YAHOO.util.Dom.setStyle(clear, 'display', 'none');
			}		
			if(!YAHOO.lang.isNull(defaultText)){
				YAHOO.util.Dom.setStyle(defaultText, 'display', 'inline');
			}
		}
	}
};

// handler for filter button.  clears the filter value and triggers 
// an ajax update of the dataTable
var clearFilter = function () {
	// split text of button to get key of the list and value of filter selection
	kv = this.id.split(":");		
	var items = facets[kv[0]];	
	var val = this.id.slice(this.id.indexOf(":")+1);
	// check for clear all
	if (val == 'clearFilter'){
		facets = {};
	} else {
	// splice the value out of the facet list
		for (var i = 0; i < items.length; i++) {
		  if (items[i] == val) {
		    found = i;
		    break;
		  }
		}
		if(i != -1) {
			items.splice(i, 1);
		}
	}
	var newState = generateRequest(myDataTable.getState().sortedBy, 0, myDataTable.get("paginator").getRowsPerPage()
	);
	// trigger update of datatable
	History.navigate("myDataTable", newState);
};



(function () {	
    // Column definitions
    var myColumnDefs = [
        {key:"field1", 
            label:"Field1",
            width:150, 
            sortable:false},
        {key:"field2", 
            label:"Field2",
            width:240, 
            sortable:false},
        {key:"field3", 
            label:"Field3",
            sortable:false}
    ];

    // DataSource instance
    myDataSource = new YAHOO.util.XHRDataSource(fewiurl + "foo/json?" + querystring + "&");
    myDataSource.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    myDataSource.responseSchema = {
        resultsList: "summaryRows",
        fields: [
            {key:"field1"},
            {key:"field2"},
            {key:"field3"}
        ],
        metaFields: {
            totalRecords: "totalCount",
            paginationRecordOffset : "startIndex",
            paginationRowsPerPage : "pageSize",
            sortKey: "sort",
            sortDir: "dir"
        }
    };
    myDataSource.maxCacheEntries = 3;
    myDataSource.connXhrMode = "cancelStaleRequests";

    // Create the Paginator
    var myPaginator = new YAHOO.widget.Paginator({
        template : "{FirstPageLink} {PreviousPageLink}<strong>{PageLinks}</strong> {NextPageLink} {LastPageLink} <span style=align:right;>{RowsPerPageDropdown}</span><br/>{CurrentPageReport}",
        pageReportTemplate : "Showing items {startRecord} - {endRecord} of {totalRecords}",
        containers   : ["paginationTop", "paginationBottom"],
        rowsPerPageOptions : [10,25,50,100],
        rowsPerPage : 25,
        pageLinks: 3,
        recordOffset: 1
    });

    // DataTable configurations
    var myConfigs = {
        paginator : myPaginator,
        dynamicData : true,
        draggableColumns : true,
        initialLoad : false,
        MSG_LOADING:  '<img src="/fewi/mgi/assets/images/loading.gif" height="24" width="24"> Searching...',
        MSG_EMPTY:    'No foo(s) found.'
    };  
    
    // DataTable instance
    myDataTable = new YAHOO.widget.DataTable("dynamicdata", myColumnDefs, 
    	    myDataSource, myConfigs);

    // Show loading message while page is being rendered
    myDataTable.showTableMessage(myDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);    
    
    // Define a custom function to route sorting through the Browser History Manager
    var handleSorting = function (oColumn) {
        // The next state will reflect the new sort values
        // while preserving existing pagination rows-per-page
        // As a best practice, a new sort will reset to page 0
        var sortedBy = {dir: this.getColumnSortDir(oColumn), key: oColumn.key};
        // Pass the state along to the Browser History Manager
        History.navigate("myDataTable", generateRequest(sortedBy, 0, 25));
    };
    myDataTable.sortColumn = handleSorting;

    // Define a custom function to route pagination through the Browser History Manager
    var handlePagination = function(state) {
        // The next state will reflect the new pagination values
        // while preserving existing sort values
        var newState = generateRequest(this.get("sortedBy"), state.recordOffset, state.rowsPerPage);
        //myPaginator.setState(newState);
        // Pass the state along to the Browser History Manager
        History.navigate("myDataTable", newState);
    };
    // First we must unhook the built-in mechanism...
    myPaginator.unsubscribe("changeRequest", myDataTable.onPaginatorChangeRequest);
    // ...then we hook up our custom function
    myPaginator.subscribe("changeRequest", handlePagination, myDataTable, true);
    
    // catch data return and do some stuff before the table updates; update 
    // filter selection, update total count, update filter count, update 
    // pagination object, set url for report buttons
    myDataTable.doBeforeLoadData = function(oRequest, oResponse, oPayload) {
		var pRequest = parseRequest(oRequest);
        var meta = oResponse.meta;
        // parse which filters are set
        facets = {};
        for (k in pRequest){
        	if(k.indexOf("Filter") != -1){
        		facets[k] = pRequest[k];
        	}
        }
        // parse result count
        oPayload.totalRecords = meta.totalRecords || oPayload.totalRecords;
        // update total count
        updateCount(oPayload.totalRecords);
        // update filter count
        var filterCount = YAHOO.util.Dom.get('filterCount');
        if (!YAHOO.lang.isNull(filterCount)){
        	setText(filterCount, YAHOO.util.Number.format(oPayload.totalRecords, numConfig));
        }
        
        // parse sort values from data
        oPayload.sortedBy = {
                key: pRequest['sort'][0] || "field1",
                dir: pRequest['dir'][0] ? "yui-dt-" + pRequest['dir'][0] : "yui-dt-desc" // Convert from server value to DataTable format
        };
        // parse pagination from data
        oPayload.pagination = {
            rowsPerPage: Number(pRequest['results'][0]) || 25,
            recordOffset: Number(pRequest['startIndex'][0]) || 0
        };

        // set report button urls
        var reportButton = YAHOO.util.Dom.get('textDownload');
        if (!YAHOO.lang.isNull(reportButton)){      	
	        facetQuery = generateRequest(oPayload.sortedBy, 0, totalCount);
	        reportButton.setAttribute('href', fewiurl + 'foo/report.txt?' + querystring + '&' + facetQuery);
        }
        
        // udate filter summary div
        populateFilterSummary();
        return true;
    };

    // update count method handles updating total count.  only updates if
    // current count is 0 or less then new count.  
	updateCount = function (newCount) {
		var countEl = YAHOO.util.Dom.get("totalCount");
		if (!YAHOO.lang.isNull(countEl)){
			var count = parseInt(newCount);
	    	if((totalCount == 0 || totalCount < count) && count > 0){
	    		totalCount = newCount;
	    		setText(countEl, YAHOO.util.Number.format(newCount, numConfig));    		
	    	}
		}
	};
	
    // Returns a querystring for consumption by the DataSource
    generateRequest = function(sortedBy, startIndex, results) {
    	var sort = 'field1';
    	var dir = 'desc';

    	if (!YAHOO.lang.isUndefined(sortedBy) && !YAHOO.lang.isNull(sortedBy)){
    		sort = sortedBy['key'];
    		dir = sortedBy['dir'];
    	}
    	// Converts from DataTable format "yui-dt-[dir]" to server value "[dir]"
        if (dir.length > 7){
        	dir = dir.substring(7);
        }
        results   = results || 25;
        var stateParams = "results="+results+"&startIndex="+startIndex+"&sort="+sort+"&dir="+dir;
        var facetParams = '';
        for (key in facets){
        	list = facets[key];
        	for(i=0; i < list.length; i++){
        		facetParams = facetParams + '&' + key + '=' + list[i].replace('*', ',');
        	}
        }
        return stateParams + facetParams;
    };
    
    // Called by Browser History Manager to trigger a new state
    handleHistoryNavigation = function (request) {
    	myDataTable.showTableMessage(myDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);   
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
                       generateRequest(myDataTable.getState().sortedBy, 0, 25); // Get default values

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

})();

// splits the request into a mapping
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

// global variable for facet dialog
var facetDialog;

// sets up the facet dialog once the dom is ready
YAHOO.util.Event.onDOMReady(function () {
	// handles submit from facet dialog; gets selected options and parses them  
	// to the facets map.   then trigger an update of the datatable
	var handleSubmit = function() {	
		var selections = this.getData();
		for (i in selections){
			facets[i] = selections[i];
		}
		var state = myDataTable.getState();
		var newState = generateRequest(state.sortedBy, 0, myDataTable.get("paginator").getRowsPerPage());
		YAHOO.util.History.navigate("myDataTable", newState);
		this.submit();
	};

	// default success handler
	var handleSuccess = function(o) {
		var response = o.responseText;
		response = response.split("<!")[0];
		document.getElementById("resp").innerHTML = response;
	};
	// default failure handler.  not sure when this is triggered
	var handleFailure = function(o) {
		this.form.innerHTML = '<img src="/fewi/mgi/assets/images/loading.gif">';
		alert("Submission failed: " + o.status);
	};

	// Instantiate the filter Dialog
	facetDialog = new YAHOO.widget.Dialog("facetDialog", 
		{ visible : false, 
		  context:["filterDiv","tl","bl", ["beforeShow"]],
		  constraintoviewport : true,
		  buttons : [{ text:"Filter", handler:handleSubmit, isDefault:true } ]
		}
	);
	
	// prepare facetdialog when hidden: contents replaced by loading gif
	facetDialog.hideEvent.subscribe(function(){ 
		this.form.innerHTML = '<img src="/fewi/mgi/assets/images/loading.gif">'; 
		for (k in buttons){
			buttons[k].set('disabled', false);
		}
	});

	// Wire up the success and failure handlers
	facetDialog.callback = { success: handleSuccess,
						     failure: handleFailure};

	// Render the Dialog
	facetDialog.render();

	// define data source for a filter
	var facetFooDS = new YAHOO.util.DataSource(fewiurl + "foo/facet/foo?" + querystring);
	facetFooDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetFooDS.responseSchema = {resultsList: "resultFacets",         
		metaFields: {
        message: "message"} };
	facetFooDS.maxCacheEntries = 3;
	
	// catch and parse potential datasource error and forward along
	facetFooDS.doBeforeParseData = function(oRequest , oFullResponse , oCallback){
		oCallback.argument.error = oFullResponse.error;
		return oFullResponse;
	};

	// parses the datasource reply and builds option list for facetDialog
	var parseFacetResponse = function (oRequest, oResponse, oPayload) {
		var res = oResponse.results;
		var options = [];

		for(x in res){
			var checked = '';
			var fVal = res[x];
			
			if (oPayload.name in facets){
				var fil = facets[oPayload.name];
				var i = fil.length;
				while (i--) {
					if (fil[i] === fVal) {
						checked = ' CHECKED';
					}
				}
			}
			options[x] = '<label><input type="checkbox" name="' + oPayload.name + '" value="' + res[x].replace(/,/g, '*') + '"' + checked + '> ' + res[x] + '</label>';
		}
		populateFacetDialog(oPayload.title, options.join('<br/>'));
	};

	// setup facetDialog for filter list display
	var populateFacetDialog = function (title, body) {
		facetDialog.setHeader('Filter by ' + title);
		facetDialog.form.innerHTML = body;
		buttons = facetDialog.getButtons();
	};

	// setup facetDialog for error display
	var handleError = function (oRequest, oResponse, oPayload) {
		buttons = facetDialog.getButtons();
		for (k in buttons){
			buttons[k].set('disabled', true);
		}
		populateFacetDialog(oPayload.title, oPayload.error);
	};

	// define setup for foo filter
	var fooCallback = {success:parseFacetResponse,
		failure:handleError,
		scope:this,
		argument:{name:'fooFilter', title:'Foo'}
	};

	// function to attach to actual foo filter button
	var populateFooDialog = function () {
		facetFooDS.sendRequest(genFacetQuery("fooFilter"), fooCallback);		
		facetDialog.show();
	};
	
	// add listeners to the foo filter button
	YAHOO.util.Event.addListener("fooFilter", "click", populateFooDialog, true);

	
	// function to generate the querystring for the facet parameters
	var genFacetQuery = function() {
        var facetParams = '';
        for (key in facets){
        	list = facets[key];
			for (i=0; i < list.length; i++){
				facetParams = facetParams + '&' + key + '=' + list[i];
			}
        }
        return facetParams;
	};

});