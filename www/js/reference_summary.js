
var myDataSource;
var myDataTable;
var generateRequest;
var totalCount = 0;
var facets = {};
var numConfig = {thousandsSeparator: ','};

// Integrate with Browser History Manager
var History = YAHOO.util.History;

// this function populates the 'breadbox' with current filters
var populateFilterSummary = function () {
	var fSum = YAHOO.util.Dom.get('filterSummary');
	
	// clear state
	if (!YAHOO.lang.isNull(fSum)){
		var filterList = new YAHOO.util.Element('filterList');
		while (filterList.hasChildNodes()) {
			filterList.removeChild(filterList.get('firstChild'));
		}
	    clear = document.createElement("a");
	    clear.setAttribute('class', 'filterItem');
	    clear.setAttribute('id', 'clearFilter');
	    setText(clear, 'Remove All Filters');
	    filterList.appendChild(clear);
	    YAHOO.util.Event.addListener(clear, "click", clearFilter);
	
	    var vis = false;
	    
	    for (k in facets) {
	    	var inner = facets[k];
	    	var brTag = false;
			for(v in inner) {
	    		vis = true;
	    		brTag = true;
	            el = document.createElement("a");
	            el.setAttribute('class', 'filterItem');
	            el.setAttribute('id', k + ':' + inner[v]);
	            var val = k.charAt(0).toUpperCase() + k.slice(1);
	            val = val.replace('Filter', '') + ': ' + inner[v];
	            setText(el, val);
	            filterList.appendChild(el);
	            YAHOO.util.Event.addListener(el, "click", clearFilter);
	            
	            filterList.appendChild(document.createTextNode(' '));
			}
			
			if (brTag){
				filterList.appendChild(document.createElement("br"));
			}
	    }
			
		if (vis){
			YAHOO.util.Dom.setStyle(fSum, 'display', 'block');
		} else {
			YAHOO.util.Dom.setStyle(fSum, 'display', 'none');
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
		var idx = items.indexOf(val); // Find the index
		if(idx != -1) {
			items.splice(idx, 1);
		}
	}
	var state = myDataTable.getState();
	
	var sort = 'score';
	var dir = 'desc';
	if (!YAHOO.lang.isNull(state.sortedBy)){
		alert('hi');
		sort = state.sortedBy['sort'];
		dir = state.sortedBy['dir'];
	}
	var newState = generateRequest(0, 
			sort, dir, myDataTable.get("paginator").getRowsPerPage()
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
        {key:"titleForm", label:"Title", sortable:false, formatter:"tForm"},  
        {key:"journal", label:"Journal", sortable:true, width:100},
        {key:"year", label:"Year", sortable:true, width:30},     
        {key:"curatedData", label:"Curated Data", sortable:false, width:225},
        {key:"vol", label:"Vol(Iss)Pg", sortable:false, width:85}
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
            {key:"curatedData"}
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
        template : "{PreviousPageLink} <strong>{PageLinks}</strong> {NextPageLink} <span style=align:right;>{RowsPerPageDropdown}</span><br/>{CurrentPageReport}",
        pageReportTemplate : "Showing items {startRecord} - {endRecord} of {totalRecords}",
        rowsPerPageOptions : [10,25,50,100],
        containers   : ["paginationTop", "paginationBottom"],
        rowsPerPage : 25,
        pageLinks: 5,
        recordOffset: 1
    });

    // DataTable configurations
    var myConfigs = {
        paginator : myPaginator,
        rowExpansionTemplate : '<div class="refAbstract">{abstract}</div>',
        dynamicData : true,
        initialLoad : false
    };  
    
    // DataTable instance
    myDataTable = new YAHOO.widget.RowExpansionDataTable("dynamicdata", myColumnDefs, myDataSource, myConfigs);

    //Subscribe to a click event to bind to
    myDataTable.subscribe( 'cellClickEvent', myDataTable.onEventToggleRowExpansion )

    // Show loading message while page is being rendered
    myDataTable.showTableMessage(myDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);    
    
    // Define a custom function to route sorting through the Browser History Manager
    var handleSorting = function (oColumn) {
        // Calculate next sort direction for given Column
        var sDir = this.getColumnSortDir(oColumn);
        
        // The next state will reflect the new sort values
        // while preserving existing pagination rows-per-page
        // As a best practice, a new sort will reset to page 0
        var newState = generateRequest(0, oColumn.key, sDir, this.get("paginator").getRowsPerPage());
        
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
        
        oPayload.pagination = {
            rowsPerPage: Number(pRequest['results'][0]) || 25,
            recordOffset: Number(pRequest['startIndex'][0]) || 0
        };
        
        if (pRequest['sort'][0] != 'score'){       	
	        oPayload.sortedBy = {
	            key: pRequest['sort'][0] || "year",
	            dir: pRequest['dir'][0] ? "yui-dt-" + pRequest['dir'][0] : "yui-dt-desc" // Convert from server value to DataTable format
	        };
        }

        
        var reportButton = YAHOO.util.Dom.get('textDownload');
        if (!YAHOO.lang.isNull(reportButton)){
        	var sort = 'score';
        	var dir = 'desc';
        	if (!YAHOO.lang.isUndefined(oPayload.sortedBy)){
        		sort = oPayload.sortedBy['sort'];
        		dir = oPayload.sortedBy['dir'];
        	}
	        facetQuery = generateRequest(0, sort, dir, totalCount);
	        reportButton.setAttribute('href', fewiurl + 'reference/report.txt?' + querystring + '&' + facetQuery);
        }
        
        var txt = 'Show All Abstracts';
        var toggle = YAHOO.util.Dom.get('toggleAbstract');
        if (!YAHOO.lang.isNull(toggle)){
        	setText(toggle, txt);
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
	    	if(totalCount == 0 || totalCount < parseInt(newCount)){
	    		totalCount = newCount;
	    		setText(countEl, YAHOO.util.Number.format(newCount, numConfig));    		
	    	}
		}
	};
	
    // Returns a request string for consumption by the DataSource
    generateRequest = function(startIndex,sortKey,dir,results) {
        startIndex = startIndex || 0;
        sortKey   = sortKey || "year";
        
        dir   = (dir) ? dir.substring(7) : "desc"; // Converts from DataTable format "yui-dt-[dir]" to server value "[dir]"
        results   = results || 25;
        
        var stateParams = "results="+results+"&startIndex="+startIndex+"&sort="+sortKey+"&dir="+dir;
        var facetParams = '';
        for (key in facets){
			for (item in facets[key]){
				facetParams = facetParams + '&' + key + '=' + facets[key][item];
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
                       generateRequest(0, defaultSort, "yui-dt-desc", 25); // Get default values

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
		for (i in selections){
			facets[i] = selections[i];
		}

		var state = myDataTable.getState();

    	var sort = 'score';
    	var dir = 'desc';
    	if (!YAHOO.lang.isNull(state.sortedBy)){

    		sort = state.sortedBy['sort'];
    		dir = state.sortedBy['dir'];
    	}

		var newState = generateRequest(0, 
				sort, dir, 
				myDataTable.get("paginator").getRowsPerPage()
		);

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
		  buttons : [{ text:"Go", handler:handleSubmit, isDefault:true } ]
		}
	);
	
	facetDialog.hideEvent.subscribe(function(){ 
		this.form.innerHTML = '<img src="/fewi/mgi/assets/images/loading.gif">'; });


	// Wire up the success and failure handlers
	facetDialog.callback = { success: handleSuccess,
						     failure: handleFailure};

	// Render the Dialog
	facetDialog.render();

	// facet DataSource instances
	var facetAuthorDS = new YAHOO.util.DataSource(fewiurl + "reference/facet/author?" + querystring);
	facetAuthorDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetAuthorDS.responseSchema = {resultsList: "resultFacets"};
	facetAuthorDS.maxCacheEntries = 3;

	var facetJournalDS = new YAHOO.util.DataSource(fewiurl + "reference/facet/journal?" + querystring);
	facetJournalDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetJournalDS.responseSchema = {resultsList: "resultFacets"};
	facetJournalDS.maxCacheEntries = 3;

	var facetYearDS = new YAHOO.util.DataSource(fewiurl + "reference/facet/year?" + querystring);
	facetYearDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetYearDS.responseSchema = {resultsList: "resultFacets"};
	facetYearDS.maxCacheEntries = 3;

	var facetDataDS = new YAHOO.util.DataSource(fewiurl + "reference/facet/data?" + querystring);
	facetDataDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetDataDS.responseSchema = {resultsList: "resultFacets"};
	facetDataDS.maxCacheEntries = 3;

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
			options[x] = '<input type="checkbox" name="' + oPayload.name + '" value="' + res[x].replace(/,/g, '*') + '"' + checked + '> ' + res[x];
		}
		populateFacetDialog(oPayload.title, options.join('<br/>'));
	};

	var populateFacetDialog = function (title, body) {
		facetDialog.setHeader('Filter by ' + title);
		facetDialog.form.innerHTML = body;
	}

	var handleError = function (oRequest, oResponse, oPayload) {
		populateFacetDialog(oPayload.title, 'Too many ' + oPayload.title + 's in the matching references to display');
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
        	if (key != exclude){
				for (item in facets[key]){
					facetParams = facetParams + '&' + key + '=' + facets[key][item];
				}
        	}
        }
        return facetParams;
	};
	
	// add listeners to the filter buttons
	YAHOO.util.Event.addListener("authorFilter", "click", populateAuthorDialog, true);
	YAHOO.util.Event.addListener("journalFilter", "click", populateJournalDialog, true);
	YAHOO.util.Event.addListener("yearFilter", "click", populateYearDialog, true);
	YAHOO.util.Event.addListener("curatedDataFilter", "click", populateDataDialog, true);
	
	// this function toggles the display of the abstract cell & updates button text
	var toggleAbstract = function() {	
        var txt;
        this.innerText ? txt=this.innerText : txt=this.textContent;
        
        var expand = true;
        
        if (txt == 'Hide All Abstracts'){
        	expand = false;
        	myDataTable.collapseAllRows();
        	txt = 'Show All Abstracts';
        } else {
        	txt = 'Hide All Abstracts';
        }
        
        this.innerText ? this.innerText=txt : this.textContent=txt;
        var state = myDataTable.getState();
        var pagination = state.pagination;

        if (expand){
        	var i = pagination.recordOffset;
        	while(i < i + pagination.rowsPerPage){
        		myDataTable.expandRow(i);
        		i = i+1;
        	}
        }
	};

	// add toggleAbstract functio to the UI button
	YAHOO.util.Event.addListener("toggleAbstract", "click", toggleAbstract);
	
	(function() {
	    var Dom = YAHOO.util.Dom,
	        Event = YAHOO.util.Event;
	    
	    var resize = new YAHOO.util.Resize('qfWrap', {handles: ['b'],
	    	maxHeight:'400', minHeight:'50'});
	})();

});

var resetQF = function () {
	var form = YAHOO.util.Dom.get("referenceQueryForm");
	form.author.value = 'foo';
	form.journal.value = 'foo';
	form.year.value = 'foo';
	form.text.value = 'foo';
	form.id.value = 'foo';
};

YAHOO.util.Event.addListener("referenceQueryForm", "reset", resetQF);

YAHOO.util.Event.addListener("toggleQF", "click", toggleQF);
YAHOO.util.Event.addListener("toggleImg", "click", toggleQF);
