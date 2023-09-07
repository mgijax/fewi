
var myDataSource;
var myDataTable;
var generateRequest;
var totalCount = 0;
var facets = {};
var numConfig = {thousandsSeparator: ','};

// Integrate with Browser History Manager
var History = YAHOO.util.History;

// returns the current state of facets as a string
var facetString = function() {
	var s = '{';
	for (k in facets) {
		if (s.length > 1) { s = s + ', '; }
		s = s + k + ':' + facets[k];
	}
	var s = s + '}';
	return s;
};

// interrogates the URL parameters and uses them to pre-fill a certain subset
// of the filters; pass in the desired querystring

var parseParameters = function(qs, updateSummaryNow) {
	var req = parseRequest(qs);
	var foundOne = 0;

	if (req.typeFilter != undefined) {
		facets['typeFilter'] = req.typeFilter;
		foundOne++;
	}
	if ((updateSummaryNow == true) && (foundOne > 0)) {
		populateFilterSummary();
	}
}

// Safari doesn't yet have startsWith for strings, so we include it here;
// returns true if 'x' starts with 'y', false if not.
var startsWith= function (x, y) {
	return (x.lastIndexOf(y, 0) === 0);
}

// this function populates the 'breadbox' with current filters
var populateFilterSummary = function () {
	var fSum = YAHOO.util.Dom.get('filterSummary');
	// clear state

	var addClearAllButton = false;		// need to add a Clear All btn?

	if (!YAHOO.lang.isUndefined(fSum)){
		var filterList = new YAHOO.util.Element('filterList');
		var fCount = YAHOO.util.Dom.get('fCount');
		var defaultText = YAHOO.util.Dom.get('defaultText');
		if(!YAHOO.lang.isNull(YAHOO.util.Dom.get('filterList'))){
			while (filterList.hasChildNodes()) {
				filterList.removeChild(filterList.get('firstChild'));
			}
		    addClearAllButton = true;
		}
	    var vis = false;
	    for (k in facets) {
	    	var inner = facets[k];
	    	var brTag = false;
        	list = facets[k];
			for(v=0; v < inner.length; v++) {
				YAHOO.util.Dom.setStyle(fSum, 'display', 'block');
	    		vis = true;
	    		//brTag = true;
	            var el = document.createElement("a");
	            el.setAttribute('class', 'filterItem');
	            el.setAttribute('id', k + ':' + inner[v]);
	            var val = k.charAt(0).toUpperCase() + k.slice(1);
	            val = val.replace('Filter', '') + ': ' + inner[v].replace(/\*/g, ',');

		    if (startsWith(val, "Type")) {
			    val = val.replace("Type", "Reference Type");
		    }
	            setText(el, val);

	            filterList.appendChild(el);
	            YAHOO.util.Event.addListener(el, "click", clearFilter);
	            
	            filterList.appendChild(document.createTextNode(' '));
			}
			if (brTag){
				filterList.appendChild(document.createElement("br"));
			}
	    }

	    // Clear All button is at the end now

	    if (addClearAllButton) {
		    clear = document.createElement("a");
		    clear.setAttribute('class', 'filterItem');
		    clear.setAttribute('id', 'clearFilter');
		    setText(clear, 'Remove All Filters');
		    filterList.appendChild(clear);
		    YAHOO.util.Event.addListener(clear, "click", clearFilter);	
	    }

		if (vis){
			if(!YAHOO.lang.isNull(fCount)){
				YAHOO.util.Dom.setStyle(fCount, 'display', 'block');
				YAHOO.util.Dom.setStyle(clear, 'display', 'inline');
			}
			if(!YAHOO.lang.isNull(defaultText)){
				YAHOO.util.Dom.setStyle(defaultText, 'display', 'none');
			}
			
		} else {
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

// removes a filter
var clearFilter = function () {
	kv = this.id.split(":");		
	var items = facets[kv[0]];	
	var val = this.id.slice(this.id.indexOf(":")+1);
	if (val == 'clearFilter'){
		facets = {};
	} else {
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

	History.navigate("myDataTable", newState);
};


(function () {	
    this.titleForm = function(elLiner, oRecord, oColumn, oData) {      
        YAHOO.util.Dom.addClass( elLiner.parentNode, "yui-dt-expandablerow-trigger" );
        YAHOO.util.Dom.setStyle( elLiner, "margin-top", "1.5em" );
        YAHOO.util.Dom.setStyle( elLiner, "margin-bottom", "1.5em" );
        elLiner.innerHTML = oRecord.getData("title");
    };
    
    // Add formatter above to the title col
    YAHOO.widget.DataTable.Formatter.tForm = this.titleForm;
    
    // Column definitions
    var myColumnDefs = [ // sortable:true enables sorting       
        {key:"id", label:"PubMed ID<br/>MGI Ref. ID", sortable:false, width:75},
        {key:"authors", label:"Author(s)", sortable:true, width:200},
        {key:"titleForm", label:"Title", sortable:false, formatter:"tForm", minWidth:300},
        {key:"curatedData", label:"Curated Data", sortable:false, width:225},
        {key:"journal", label:"Journal", sortable:true, width:100},
        {key:"year", label:"Year", sortable:true, width:30},     
        {key:"vol", label:"Vol(Iss)Pg", sortable:false, width:85},
        {key:"score", label:"Rank", sortable:true, hidden:true}
    ];

    // DataSource instance
    myDataSource = new YAHOO.util.XHRDataSource(fewiurl + "reference/json?" + querystring + "&");
    myDataSource.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    myDataSource.responseSchema = {
        resultsList: "summaryRows",
        fields: [
			{key:"id"},
            {key:"journal"},
            {key:"title"},
            {key:"authors"},
            {key:"abstract"},
            {key:"year"},
            {key:"vol"},
            {key:"curatedData"},
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
    myDataSource.maxCacheEntries = 3;
    myDataSource.connXhrMode = "cancelStaleRequests";

    // Create the Paginator
    var myPaginator = new YAHOO.widget.Paginator({
        template : "{FirstPageLink} {PreviousPageLink}<strong>{PageLinks}</strong> {NextPageLink} {LastPageLink} <span style=align:right;>{RowsPerPageDropdown}</span><br/>{CurrentPageReport}",
        pageReportTemplate : "Showing reference(s) {startRecord} - {endRecord} of {totalRecords}",
        rowsPerPageOptions : [10,25,50,100],
        containers   : ["paginationTop", "paginationBottom"],
        rowsPerPage : 25,
        pageLinks: 3,
        recordOffset: 1
    });

    // DataTable configurations
    var myConfigs = {
        paginator : myPaginator,
        rowExpansionTemplate : '<div class="refAbstract">{abstract}</div>',
        dynamicData : true,
        initialLoad : false,
        MSG_LOADING:  '<img src="/fewi/mgi/assets/images/loading.gif" height="24" width="24"> Searching...',
        MSG_EMPTY:    'No references found.'
    };  
    
    // DataTable instance
    myDataTable = new YAHOO.widget.RowExpansionDataTable("dynamicdata", myColumnDefs, myDataSource, myConfigs);

    //Subscribe to a click event to bind to
    myDataTable.subscribe( 'cellClickEvent', myDataTable.onEventToggleRowExpansion );

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
    // Update payload data on the fly for tight integration with latest values from server 
    
    myDataTable.doBeforeLoadData = function(oRequest, oResponse, oPayload) {
		var pRequest = parseRequest(oRequest);
        var meta = oResponse.meta;
        facets = {};
        for (k in pRequest){
        	if(k.indexOf("Filter") != -1){
        		facets[k] = pRequest[k];
        	}
        }
        oPayload.totalRecords = meta.totalRecords || oPayload.totalRecords;

        updateCount(oPayload.totalRecords);
        var filterCount = YAHOO.util.Dom.get('filterCount');
        if (!YAHOO.lang.isNull(filterCount)){
        	setText(filterCount, YAHOO.util.Number.format(oPayload.totalRecords, numConfig));
        }
        
        oPayload.sortedBy = {
                key: pRequest['sort'][0] || "year",
                dir: pRequest['dir'][0] ? "yui-dt-" + pRequest['dir'][0] : "yui-dt-desc" // Convert from server value to DataTable format
        };
        
        oPayload.pagination = {
            rowsPerPage: Number(pRequest['results'][0]) || 25,
            recordOffset: Number(pRequest['startIndex'][0]) || 0
        };

        var reportButton = YAHOO.util.Dom.get('textDownload');
        if (!YAHOO.lang.isNull(reportButton)){      	
	        facetQuery = generateRequest(oPayload.sortedBy, 0, totalCount);
	        reportButton.setAttribute('href', fewiurl + 'reference/report.txt?' + querystring + '&' + facetQuery);
        }
        
        var txt = 'Show All Abstracts';
        var toggleAbs = YAHOO.util.Dom.get('toggleAbstract');
        if (!YAHOO.lang.isNull(toggleAbs)){
        	setText(toggleAbs, txt);
        }

        populateFilterSummary();
        return true;
    };
    
    myDataTable.subscribe("postRenderEvent", function() {
    	var threshold = 1;
    	if(totalCount <= threshold){
    		i = 0;
            var toggle = YAHOO.util.Dom.get('toggleAbstract');
            if (!YAHOO.lang.isNull(toggle)){
            	setText(toggle, 'Hide All Abstracts');
            }
    		while (i <= threshold){   			
    			myDataTable.expandRow(i);
    			i = i + 1;
    		}
    	}
    });

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
	
    // Returns a request string for consumption by the DataSource
    generateRequest = function(sortedBy, startIndex, results) {
    	var sort = defaultSort;
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

var facetDialog;

YAHOO.util.Event.onDOMReady(function () {

	var oCallback = {
		    success : myDataTable.onDataReturnInitializeTable,
		    failure : myDataTable.onDataReturnInitializeTable,
		    scope: this
	};
	

	// Define various event handlers for Dialog
	var handleSubmit = function() {	
		var selections = this.getData();
		var list = [];
		for (i in selections){
			facets[i] = selections[i];
		}
		var state = myDataTable.getState();
		var newState = generateRequest(state.sortedBy, 0, myDataTable.get("paginator").getRowsPerPage());
		YAHOO.util.History.navigate("myDataTable", newState);	
		this.submit();
	};

	var handleSuccess = function(o) {
		var response = o.responseText;
		response = response.split("<!")[0];
		document.getElementById("resp").innerHTML = response;
	};
	
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
	
	facetDialog.hideEvent.subscribe(function(){ 
		this.form.innerHTML = '<img src="/fewi/mgi/assets/images/loading.gif">'; 
	});


	// Wire up the success and failure handlers
	facetDialog.callback = { success: handleSuccess,
						     failure: handleFailure};

	// Render the Dialog
	facetDialog.render();

	// facet DataSource instances
	var facetAuthorDS = new YAHOO.util.DataSource(fewiurl + "reference/facet/author?" + querystring);
	facetAuthorDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetAuthorDS.responseSchema = {resultsList: "resultFacets",  
		metaFields: {
			 message: "message"}
	};
	facetAuthorDS.maxCacheEntries = 3;
	
	facetAuthorDS.doBeforeParseData = function(oRequest , oFullResponse , oCallback){
		oCallback.argument.error = oFullResponse.error;
		return oFullResponse;
	};


	var facetTypeDS = new YAHOO.util.DataSource(fewiurl + "reference/facet/type?" + querystring);
	facetTypeDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetTypeDS.responseSchema = {resultsList: "resultFacets",   
			metaFields: {
				 message: "message"}
	};
	facetTypeDS.maxCacheEntries = 3;
	
	facetTypeDS.doBeforeParseData = function(oRequest , oFullResponse , oCallback){
		oCallback.argument.error = oFullResponse.error;
		return oFullResponse;
	};

	var facetJournalDS = new YAHOO.util.DataSource(fewiurl + "reference/facet/journal?" + querystring);
	facetJournalDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetJournalDS.responseSchema = {resultsList: "resultFacets",   
			metaFields: {
				 message: "message"}
	};
	facetJournalDS.maxCacheEntries = 3;
	
	facetJournalDS.doBeforeParseData = function(oRequest , oFullResponse , oCallback){
		oCallback.argument.error = oFullResponse.error;
		return oFullResponse;
	};

	var facetYearDS = new YAHOO.util.DataSource(fewiurl + "reference/facet/year?" + querystring);
	facetYearDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetYearDS.responseSchema = {resultsList: "resultFacets",         
			metaFields: {
		message: "message"}
	};
	facetYearDS.maxCacheEntries = 3;
	
	facetYearDS.doBeforeParseData = function(oRequest , oFullResponse , oCallback){
		oCallback.argument.error = oFullResponse.error;
		return oFullResponse;
	};

	var facetDataDS = new YAHOO.util.DataSource(fewiurl + "reference/facet/data?" + querystring);
	facetDataDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetDataDS.responseSchema = {resultsList: "resultFacets",         
			metaFields: {
        message: "message"}
	};
	facetDataDS.maxCacheEntries = 3;
	
	facetDataDS.doBeforeParseData = function(oRequest , oFullResponse , oCallback){
		oCallback.argument.error = oFullResponse.error;
		return oFullResponse;
	};

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
			if (oPayload.name == 'typeFilter') {
			    options[x] = '<label><input type="checkbox" name="' + oPayload.name + '" value="' + res[x] + '"' + checked + '> ' + res[x] + '</label>';
			} else {
			    options[x] = '<label><input type="checkbox" name="' + oPayload.name + '" value="' + res[x].replace(/,/g, '*') + '"' + checked + '> ' + res[x] + '</label>';
			}
		}
		populateFacetDialog(oPayload.title, options.join('<br/>'), false);
	};

	var populateFacetDialog = function (title, body, error) {
		facetDialog.setHeader('Filter by ' + title);
		facetDialog.form.innerHTML = body;
		buttons = facetDialog.getButtons();

		for (k in buttons){
			//alert('button: ' + error);
			buttons[k].set('disabled', error);
		}
	};

	var handleError = function (oRequest, oResponse, oPayload) {
		buttons = facetDialog.getButtons();
		for (k in buttons){
			buttons[k].set('disabled', true);
		}
		populateFacetDialog(oPayload.title, oPayload.error, true);
	};

	var authorCallback = {success:parseFacetResponse,
		failure:handleError,
		scope:this,
		argument:{name:'authorFilter', title:'Author'}
	};

	var journalCallback = {success:parseFacetResponse,
			failure:handleError,
			scope:this,
			argument:{name:'journalFilter', title:'Journal'}
		};

	var typeCallback = {success:parseFacetResponse,
			failure:handleError,
			scope:this,
			argument:{name:'typeFilter', title:'Reference Type'}
		};

	var yearCallback = {success:parseFacetResponse,
			failure:handleError,
			scope:this,
			argument:{name:'yearFilter', title:'Year'}
		};

	var dataCallback = {success:parseFacetResponse,
			failure:handleError,
			scope:this,
			argument:{name:'dataFilter', title:'Data'}
		};

	var populateAuthorDialog = function () {
		facetAuthorDS.sendRequest(genFacetQuery("authorFilter"), authorCallback);		
		facetDialog.show();
	};

	var populateJournalDialog = function () {
		facetJournalDS.sendRequest(genFacetQuery("journalFilter"), journalCallback);		
		facetDialog.show();
	};

	var populateTypeDialog = function () {
		facetTypeDS.sendRequest(genFacetQuery("typeFilter"), typeCallback);		
		facetDialog.show();
	};

	var populateYearDialog = function () {
		facetYearDS.sendRequest(genFacetQuery("yearFilter"), yearCallback);		
		facetDialog.show();
	};

	var populateDataDialog = function () {
		facetDataDS.sendRequest(genFacetQuery("dataFilter"), dataCallback);		
		facetDialog.show();
	};
	
	// function to generate the querystring for the facet parameters
	var genFacetQuery = function(exclude) {
        var facetParams = '';
        for (key in facets){
        	list = facets[key];
			for (i=0; i < list.length; i++){
				facetParams = facetParams + '&' + key + '=' + list[i];
			}
        }
        return facetParams;
	};
	
	// add listeners to the filter buttons
	YAHOO.util.Event.addListener("authorFilter", "click", populateAuthorDialog, true);
	YAHOO.util.Event.addListener("journalFilter", "click", populateJournalDialog, true);
	YAHOO.util.Event.addListener("typeFilter", "click", populateTypeDialog, true);
	YAHOO.util.Event.addListener("yearFilter", "click", populateYearDialog, true);
	YAHOO.util.Event.addListener("curatedDataFilter", "click", populateDataDialog, true);
	
	// this function toggles the display of the abstract cell & updates button text
	var toggleAbstract = function() {
		var txt = "";
        this.innerText ? txt=this.innerText: txt=this.textContent;
        var expand = true;
           
        if (txt == 'Hide All Abstracts'){       	
        	expand = false;
        	myDataTable.collapseAllRows();
        	txt = 'Show All Abstracts';
        } else {
        	txt = 'Hide All Abstracts';
        }
        
        setText(this, txt);
        
        var state = myDataTable.getState();
        var pagination = state.pagination;

        if (expand){
        	var i = pagination.recordOffset ;
        	var rows = pagination.rowsPerPage + i;
        	while(i < rows){
        		myDataTable.expandRow(i++);
        	}
        }
	};

	// add toggleAbstract function to the UI button
	YAHOO.util.Event.addListener("toggleAbstract", "click", toggleAbstract);
	
	(function() {
	    var Dom = YAHOO.util.Dom,
	        Event = YAHOO.util.Event;
	    
	    var resize = new YAHOO.util.Resize('qfWrap', {handles: ['b'],
	    	maxHeight:'400', minHeight:'50'});
	})();

});
