
var myDataSource;
var myDataTable;
var generateRequest;
var facets = [];

// Integrate with Browser History Manager
var History = YAHOO.util.History;


(function () {	
	// this function formats the vol(iss)pg column
    this.volFormatter = function(elLiner, oRecord, oColumn, oData) {
        elLiner.innerHTML= '<b>' + oRecord.getData("vol") + '</b>(' + oRecord.getData("issue") + ')' + oRecord.getData("pages");
    };

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
    
    this.dataFormatter = function(elLiner, oRecord, oColumn, oData) {
        var numConfig = {thousandsSeparator: ','};
        var text = '<ul class=\"curatedData\">';
        var expTotal = oRecord.getData("countOfGXDAssays") + oRecord.getData("countOfGXDResults") + oRecord.getData("countOfGXDStructures");
        if (expTotal > 0){
	        text = text + "<li>Expression assays: <a href=\"${configBean.FEWI_URL}expression/reference/" + oRecord.getData("jnumID") + "\">" + YAHOO.util.Number.format(oRecord.getData("countOfGXDAssays"), numConfig);
	        text = text + "</a>, results: <a href=\"${configBean.FEWI_URL}expression/reference/" + oRecord.getData("jnumID") + "\">" + YAHOO.util.Number.format(oRecord.getData("countOfGXDResults"), numConfig);
	        text = text + "</a>, tissues: <a href=\"${configBean.FEWI_URL}expression/reference/" + oRecord.getData("jnumID") + "\">" + YAHOO.util.Number.format(oRecord.getData("countOfGXDStructures"), numConfig) + '</a></li>';
        }
        if (oRecord.getData("countOfGXDIndex") > 0){           
        	text = text + "<li>Gene expression literature content records: <a href=\"${configBean.FEWI_URL}expression/reference/" + oRecord.getData("jnumID") + "\">" + YAHOO.util.Number.format(oRecord.getData("countOfGXDIndex"), numConfig) + '</a></li>';
        }
        if(oRecord.getData("countOfMarkers") > 0){
	        text = text + "<li>Genome features: <a href=\"${configBean.FEWI_URL}marker/reference/" + oRecord.getData("jnumID") + "\">" + YAHOO.util.Number.format(oRecord.getData("countOfMarkers"), numConfig) + '</a></li>';
        }
        if(oRecord.getData("countOfAlleles") > 0){
	        text = text + "<li>Phenotypic alleles: <a href=\"${configBean.FEWI_URL}allele/reference/" + oRecord.getData("jnumID") + "\">" + YAHOO.util.Number.format(oRecord.getData("countOfAlleles"), numConfig) + '</a></li>';
        }
        if(oRecord.getData("countOfOrthologs") > 0){
	        text = text + "<li>Mamallian orthologs: <a href=\"${configBean.FEWI_URL}ortholog/reference/" + oRecord.getData("jnumID") + "\">" + YAHOO.util.Number.format(oRecord.getData("countOfOrthologs"), numConfig) + '</a></li>';
        }
        if(oRecord.getData("countOfMappingResults") > 0){
	        text = text + "<li>Mapping data: <a href=\"${configBean.FEWI_URL}map/reference/" + oRecord.getData("jnumID") + "\">" + YAHOO.util.Number.format(oRecord.getData("countOfMappingResults"), numConfig) + '</a></li>';
        }
        if(oRecord.getData("countOfProbes") > 0){
	        text = text + "<li>Molecular probes and clones: <a href=\"${configBean.FEWI_URL}probe/reference/" + oRecord.getData("jnumID") + "\">" + YAHOO.util.Number.format(oRecord.getData("countOfProbes"), numConfig) + '</a></li>';
        }
        if(oRecord.getData("countOfSequenceResults") >0){
	        text = text + "<li>Sequences: <a href=\"${configBean.FEWI_URL}sequence/reference/" + oRecord.getData("jnumID") + "\">" + YAHOO.util.Number.format(oRecord.getData("countOfSequenceResults"), numConfig) + '</a></li>';  
        }
		elLiner.innerHTML = text + '</ul>';      
    };

    this.titleForm = function(elLiner, oRecord, oColumn, oData) {
        
        YAHOO.util.Dom.addClass( elLiner.parentNode, "yui-dt-expandablerow-trigger" );
        elLiner.innerHTML = oRecord.getData("title");
    };
    
    // Adds the formatters above to the to the symbol col
    YAHOO.widget.DataTable.Formatter.vol = this.volFormatter;
    YAHOO.widget.DataTable.Formatter.id = this.idFormatter;
    YAHOO.widget.DataTable.Formatter.data = this.dataFormatter;
    YAHOO.widget.DataTable.Formatter.tForm = this.titleForm;

    // Column definitions
    var myColumnDefs = [ // sortable:true enables sorting
        {key:"idFormatter", label:"PubMed ID<br/>MGI Reference ID", sortable:false, 
            formatter:"id", width:75},
        {key:"authors", label:"Author(s)", sortable:true, width:250},
        {key:"titleForm", label:"Title", sortable:false, formatter:"tForm", width:300},        
        {key:"journal", label:"Journal", sortable:true, width:150},
        {key:"year", label:"Year", sortable:true, width:30},
        {key:"volFomatter", label:"Vol(Iss)Pg", sortable:false, formatter:"vol", width:85},
        {key:"dataFomatter", label:"Curated Data", sortable:false, formatter:"data", width:300}
    ];

    // DataSource instance
    myDataSource = new YAHOO.util.DataSource("${configBean.FEWI_URL}reference/json?${queryString}&");
    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
    myDataSource.responseSchema = {
        resultsList: "resultObjects",
        fields: [
			{key:"jnumID"},
			{key:"pubMedID"},
            {key:"journal"},
            {key:"title"},
            {key:"authors"},
            {key:"abstract"},
            {key:"year"},
            {key:"vol"},
            {key:"issue"},
            {key:"pages"},
            {key:"countOfAlleles"},
            {key:"countOfGXDAssays"},
            {key:"countOfGXDIndex"},
            {key:"countOfGXDResults"},
            {key:"countOfGXDStructures"},
            {key:"countOfMappingResults"},
            {key:"countOfOrthalogs"},
            {key:"countOfMarkers"},
            {key:"countOfProbes"},
            {key:"countOfSequenceResults"}
        ],
        metaFields: {
            totalRecords: "totalCount",
            paginationRecordOffset : "startIndex",
            paginationRowsPerPage : "pageSize",
            sortKey: "sort",
            sortDir: "dir"
        }
    };

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
        oPayload.pagination = {
            rowsPerPage: Number(pRequest['results']) || 25,
            recordOffset: Number(pRequest['startIndex']) || 0
        };
        oPayload.sortedBy = {
            key: pRequest['sort'] || "journal",
            dir: pRequest['dir'] ? "yui-dt-" + pRequest['dir'] : "yui-dt-asc" // Convert from server value to DataTable format
        };
        return true;
    };
    
    // Returns a request string for consumption by the DataSource
    generateRequest = function(startIndex,sortKey,dir,results) {
        startIndex = startIndex || 0;
        sortKey   = sortKey || "journal";
        dir   = (dir) ? dir.substring(7) : "asc"; // Converts from DataTable format "yui-dt-[dir]" to server value "[dir]"
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

})();

function parseRequest(request){
	var reply = [];
	var kvPairs = request.split('&');
	for (pair in kvPairs) {
		var kv = kvPairs[pair].split('=');
		reply[kv[0]] = kv[1];
	}
	return reply;
}

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
			var s = selections[i];
			facets[i] = s;
		}

		var state = myDataTable.getState();
		var newState = generateRequest(0, 
				state.sortedBy.key, 
				state.sortedBy.dir, 
				myDataTable.get("paginator").getRowsPerPage()
		);
		
		History.navigate("myDataTable", newState);
		this.submit();
	};
	var handleCancel = function() {
		this.cancel();
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
							});

	// Wire up the success and failure handlers
	facetDialog.callback = { success: handleSuccess,
						     failure: handleFailure };

	// Render the Dialog
	facetDialog.render();
    
	// facet DataSource instances
	var facetAuthorDS = new YAHOO.util.DataSource("${configBean.FEWI_URL}reference/facet/author?${queryString}");
	facetAuthorDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetAuthorDS.responseSchema = {resultsList: "resultFacets"};

	var facetJournalDS = new YAHOO.util.DataSource("${configBean.FEWI_URL}reference/facet/journal?${queryString}");
	facetJournalDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetJournalDS.responseSchema = {resultsList: "resultFacets"};

	var facetYearDS = new YAHOO.util.DataSource("${configBean.FEWI_URL}reference/facet/year?${queryString}");
	facetYearDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetYearDS.responseSchema = {resultsList: "resultFacets"};

	var facetDataDS = new YAHOO.util.DataSource("${configBean.FEWI_URL}reference/facet/data?${queryString}");
	facetDataDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetDataDS.responseSchema = {resultsList: "resultFacets"};

	
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
		facetAuthorDS.sendRequest(null, authorCallback);		
		facetDialog.show();
	};

	var populateJournalDialog = function () {
		facetJournalDS.sendRequest(null, journalCallback);		
		facetDialog.show();
	};

	var populateYearDialog = function () {
		facetYearDS.sendRequest(null, yearCallback);		
		facetDialog.show();
	};

	var populateDataDialog = function () {
		facetDataDS.sendRequest(null, dataCallback);		
		facetDialog.show();
	};

	YAHOO.util.Event.addListener("authorFilter", "click", populateAuthorDialog, true);
	YAHOO.util.Event.addListener("journalFilter", "click", populateJournalDialog, true);
	YAHOO.util.Event.addListener("yearFilter", "click", populateYearDialog, true);
	YAHOO.util.Event.addListener("curatedDataFilter", "click", populateDataDialog, true);

});

