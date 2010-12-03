
var myDataSource;
var myDataTable;
var generateRequest;
var totalCount = 0;
var facets = new Array();
var numConfig = {thousandsSeparator: ','};

// Integrate with Browser History Manager
var History = YAHOO.util.History;

var populateFilterSummary = function () {
    // add filters to summary
	var fsList = new YAHOO.util.Element('fsList');
    var el;
    
    var brItems = YAHOO.util.Dom.getChildren('fsList');
    for (b in brItems){
    	fsList.removeChild(brItems[b]);
    }

    var vis = false;
    for (k in facets) {
    	var inner = facets[k];
		for(v in inner) {
    		vis = true;
            el = document.createElement("a");
            el.setAttribute('class', 'fsItem');
            el.setAttribute('id', k + ':' + inner[v]);
            var val = k.charAt(0).toUpperCase() + k.slice(1);
            el.textContent = val.replace('Filter', '') + ': ' + inner[v];
            YAHOO.util.Event.addListener(el, "click", clearFilter);

            fsList.appendChild(el);
		}
		el = document.createElement("br");
		YAHOO.util.Dom.insertAfter(el, YAHOO.util.Dom.getLastChild(fsList));    		
    }

	var fSum = YAHOO.util.Dom.get('filterSummary');
	if (vis){
		YAHOO.util.Dom.setStyle(fSum, 'display', 'block');
	} else {

		YAHOO.util.Dom.setStyle(fSum, 'display', 'none');
	}
};

var clearFilter = function () {
	kv = this.id.split(":");		
	var items = facets[kv[0]];		
	var idx = items.indexOf(kv[1]); // Find the index
	if(idx!=-1) {
		items.splice(idx, 1);
	}
	populateFilterSummary();
	var state = myDataTable.getState();
	var newState = generateRequest(0, 
			state.sortedBy.key, 
			state.sortedBy.dir, 
			myDataTable.get("paginator").getRowsPerPage()
	);
	
	History.navigate("myDataTable", newState);
};


(function () {	
	// this function formats the id cell
    this.idFormatter = function(elLiner, oRecord, oColumn, oData) {
        var url = '${externalUrls.PubMed}';
        var link = '';
        var id = oRecord.getData("pubMedID");
        if (id){        
            url = url.replace('@@@@', id);
            link = '<a href="' + url + '" target="new" class="extUrl">' + id + '</a><br/>';
        }       
		elLiner.innerHTML= link + oRecord.getData("jnumID");
    };

    this.titleForm = function(elLiner, oRecord, oColumn, oData) {      
        YAHOO.util.Dom.addClass( elLiner.parentNode, "yui-dt-expandablerow-trigger" );
        YAHOO.util.Dom.setStyle( elLiner, "margin-top", "1.5em" );
        YAHOO.util.Dom.setStyle( elLiner, "margin-bottom", "1.5em" );
        elLiner.innerHTML = oRecord.getData("title");
    };
    
    // Adds the formatters above to the to the symbol col
    YAHOO.widget.DataTable.Formatter.id = this.idFormatter;
    YAHOO.widget.DataTable.Formatter.tForm = this.titleForm;
    
    // Column definitions
    var myColumnDefs = [ // sortable:true enables sorting       
        {key:"idFormatter", label:"PubMed ID<br/>MGI Ref. ID", sortable:false, 
            formatter:"id", width:75},
        {key:"authors", label:"Author(s)", sortable:true, width:200},
        {key:"titleForm", label:"Title", sortable:false, formatter:"tForm"},  
        {key:"journal", label:"Journal", sortable:true, width:100},
        {key:"year", label:"Year", sortable:true, width:30},     
        {key:"curatedData", label:"Curated Data", sortable:false, width:225},
        {key:"vol", label:"Vol(Iss)Pg", sortable:false, width:85},
        {key:"score", label:"Rank", sortable:true, width:75}
    ];

    // DataSource instance
    myDataSource = new YAHOO.util.XHRDataSource("${configBean.FEWI_URL}reference/json?${queryString}&");
    myDataSource.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    myDataSource.responseSchema = {
        resultsList: "resultObjects",
        fields: [
            {key:"score"},
			{key:"jnumID"},
			{key:"pubMedID"},
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
    myDataSource.maxCacheEntries = 10;
    myDataSource.connXhrMode = "cancelStaleRequests";

    // Create the Paginator
    var myPaginator = new YAHOO.widget.Paginator({
        template : "{PreviousPageLink} <strong>{PageLinks}</strong> {NextPageLink} <span style=align:right;>{RowsPerPageDropdown}</span><br/>{CurrentPageReport}",
        pageReportTemplate : "Showing items {startRecord} - {endRecord} of {totalRecords}",
        rowsPerPageOptions : [10,25,50,100],
        containers   : ["paginationTop"],
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
    myDataTable.subscribe( 'cellClickEvent', myDataTable.onEventToggleRowExpansion );


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

        oPayload.totalRecords = meta.totalRecords || oPayload.totalRecords;
        
        updateCount(oPayload.totalRecords);

        oPayload.pagination = {
            rowsPerPage: Number(pRequest['results']) || 25,
            recordOffset: Number(pRequest['startIndex']) || 0
        };
        oPayload.sortedBy = {
            key: pRequest['sort'] || "year",
            dir: pRequest['dir'] ? "yui-dt-" + pRequest['dir'] : "yui-dt-desc" // Convert from server value to DataTable format
        };
        
        var abstractToggle = YAHOO.util.Dom.get('abstractToggle');
        var txt = 'Show All Abstracts';
        abstractToggle.innerText ? abstractToggle.innerText=txt : abstractToggle.textContent=txt;
        populateFilterSummary();
        return true;
    };
    
	updateCount = function (newCount) {
		var countEl = YAHOO.util.Dom.get("totalCount");
		if (!YAHOO.lang.isNull(countEl)){
	    	if(parseInt(totalCount) < parseInt(newCount)){
	    		totalCount = YAHOO.util.Number.format(newCount, numConfig);   		
	    		countEl.textContent = totalCount + " items match your query.";
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
                       generateRequest(0, "${defaultSort}", "yui-dt-desc", 25); // Get default values

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
	var reply = [];
	var kvPairs = request.split('&');
	for (pair in kvPairs) {
		var kv = kvPairs[pair].split('=');
		reply[kv[0]] = kv[1];
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
		var newState = generateRequest(0, 
				state.sortedBy.key, 
				state.sortedBy.dir, 
				myDataTable.get("paginator").getRowsPerPage()
		);
		
		History.navigate("myDataTable", newState);
		this.form.innerHTML = '<img src="/fewi/mgi/assets/images/loading.gif">';	
		this.submit();
	};

	var handleSuccess = function(o) {
		var response = o.responseText;
		response = response.split("<!")[0];
		document.getElementById("resp").innerHTML = response;
	};
	
	var handleFailure = function(o) {
		alert("Submission failed: " + o.status);
	};
	
	var handleSubmitEvent = function(o) {
		alert('submitEvent');
	};
    
	// Instantiate the Dialog
	facetDialog = new YAHOO.widget.Dialog("facetDialog", 
		{ visible : false, 
		  context:["filterDiv","tl","bl", ["beforeShow"]],
		  constraintoviewport : true,
		  buttons : [{ text:"Go", handler:handleSubmit, isDefault:true } ]
		}
	);

	// Wire up the success and failure handlers
	facetDialog.callback = { success: handleSuccess,
						     failure: handleFailure};

	// Render the Dialog
	facetDialog.render();
    
	// facet DataSource instances
	var facetAuthorDS = new YAHOO.util.DataSource("${configBean.FEWI_URL}reference/facet/author?${queryString}");
	facetAuthorDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetAuthorDS.responseSchema = {resultsList: "resultFacets"};
	facetAuthorDS.maxCacheEntries = 10;

	var facetJournalDS = new YAHOO.util.DataSource("${configBean.FEWI_URL}reference/facet/journal?${queryString}");
	facetJournalDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetJournalDS.responseSchema = {resultsList: "resultFacets"};
	facetJournalDS.maxCacheEntries = 10;

	var facetYearDS = new YAHOO.util.DataSource("${configBean.FEWI_URL}reference/facet/year?${queryString}");
	facetYearDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetYearDS.responseSchema = {resultsList: "resultFacets"};
	facetYearDS.maxCacheEntries = 10;

	var facetDataDS = new YAHOO.util.DataSource("${configBean.FEWI_URL}reference/facet/data?${queryString}");
	facetDataDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetDataDS.responseSchema = {resultsList: "resultFacets"};
	facetDataDS.maxCacheEntries = 10;
	
	var populateFacet = function (oRequest, oResponse, oPayload) {
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
		facetDialog.setHeader('Filter by ' + oPayload.title);
		facetDialog.form.innerHTML = options.join('<br/>');
	};

	var authorCallback = {success:populateFacet,
		failure:populateFacet,
		scope:this,
		argument:{name:'authorFilter', title:'Author'}
	};

	var journalCallback = {success:populateFacet,
			failure:populateFacet,
			scope:this,
			argument:{name:'journalFilter', title:'Journal'}
		};

	var yearCallback = {success:populateFacet,
			failure:populateFacet,
			scope:this,
			argument:{name:'yearFilter', title:'Year'}
		};

	var dataCallback = {success:populateFacet,
			failure:populateFacet,
			scope:this,
			argument:{name:'curatedDataFilter', title:'Curated Data'}
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
		facetDataDS.sendRequest(genFacetQuery("curatedDataFilter"), dataCallback);		
		facetDialog.show();
	};
	
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

	YAHOO.util.Event.addListener("authorFilter", "click", populateAuthorDialog, true);
	YAHOO.util.Event.addListener("journalFilter", "click", populateJournalDialog, true);
	YAHOO.util.Event.addListener("yearFilter", "click", populateYearDialog, true);
	YAHOO.util.Event.addListener("curatedDataFilter", "click", populateDataDialog, true);
	
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

	YAHOO.util.Event.addListener("abstractToggle", "click", toggleAbstract);

});
