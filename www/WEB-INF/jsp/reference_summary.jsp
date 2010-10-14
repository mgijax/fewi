<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

${templateBean.templateHeadHtml}

<!--begin custom header content for this example-->
<script type="text/javascript">
	document.documentElement.className = "yui-pe";
</script>

<script src="${configBean.fewiUrl}js/rowexpansion.js"></script>

<style type="text/css">
#example {
    height:30em;
}

label { 
    display:block;
    float:left;
    width:45%;
    clear:left;
}

.clear {
    clear:both;
}

#resp {
    margin:10px;
    padding:5px;
    border:1px solid #ccc;
    background:#fff;
}

#resp li {
    font-family:monospace
}

.yui-pe .yui-pe-content {
    display:none;
}

</style>

<title>Reference Summary</title>

${templateBean.templateBodyStartHtml}

<iframe id="yui-history-iframe" src="${configBean.fewiUrl}js/blank.html"></iframe>
<input id="yui-history-field" type="hidden">

<!-- begin header bar -->
<div id="titleBarWrapper" style="max-width:1200px" userdoc="marker_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">References Summary</span>
</div>
<!-- end header bar -->
<div>
	<div id="querySummary">
		<span class="enhance">You searched for:</span><br/>
		<c:if test="${not empty referenceQueryForm.author}"><span class="label">Author:</span> 
			${fn:replace(referenceQueryForm.author,";", ",") }<br/></c:if>
		<c:if test="${not empty referenceQueryForm.journal}"><span class="label">Journal:</span>
			${fn:replace(referenceQueryForm.journal,";", ",") }<br/></c:if>
		<c:if test="${not empty referenceQueryForm.year}"><span class="label">Year:</span> ${referenceQueryForm.year}<br/></c:if>
		<c:if test="${not empty referenceQueryForm.text}"><span class="label">Text:</span> ${referenceQueryForm.text}<br/></c:if>
		<c:if test="${not empty referenceQueryForm.id}"><span class="label">ID:</span> ${referenceQueryForm.id}<br/></c:if>
	</div>
	<div id="paginationTop"  style="float:right;"></div>
</div>

<div>
	<a id="authorFilter">Author Filter <img src="${configBean.fewiUrl}images/filter.png" width="12" height="12" /></a> 
</div>
<div>
	<a id="journalFilter">Journal Filter <img src="${configBean.fewiUrl}images/filter.png" width="12" height="12" /></a> 
</div>
<div>
	<a id="yearFilter">Year Filter <img src="${configBean.fewiUrl}images/filter.png" width="12" height="12" /></a> 
</div>
<div>
	<a id="curatedDataFilter">Data Filter <img src="${configBean.fewiUrl}images/filter.png" width="12" height="12" /></a> 
</div>

<div id="dynamicdata"></div>

<div class="facetFilter">
<div id="facetDialog">
	<div class="hd">Filter</div>
	
	<div class="bd">
		<form:form method="GET" action="${configBean.fewiUrl}reference/summary">
		
		</form:form>
	</div>
</div>
</div>

<script type="text/javascript">
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
		elLiner.innerHTML= oRecord.getData("pubMedID") + '<br/>' + oRecord.getData("jnumID");
    };
    
    this.dataFormatter = function(elLiner, oRecord, oColumn, oData) {
        var text = '<ul class=\"curatedData\">';
        var expTotal = oRecord.getData("countOfGXDAssays") + oRecord.getData("countOfGXDResults") + oRecord.getData("countOfGXDStructures");
        if (expTotal > 0){
	        text = text + "<li>Expression assays: " + oRecord.getData("countOfGXDAssays");
	        text = text + " results: " + oRecord.getData("countOfGXDResults");
	        text = text + " tissues: " + oRecord.getData("countOfGXDStructures") + '</li>';
        }
        if (oRecord.getData("countOfGXDIndex") > 0){           
        	text = text + "<li>Gene expression literature content records: " + oRecord.getData("countOfGXDIndex") + '</li>';
        }
        if(oRecord.getData("countOfMarkers") > 0){
	        text = text + "<li>Genome features: " + oRecord.getData("countOfMarkers") + '</li>';
        }
        if(oRecord.getData("countOfAlleles") > 0){
	        text = text + "<li>Phenotypic alleles: " + oRecord.getData("countOfAlleles") + '</li>';
        }
        if(oRecord.getData("countOfOrthologs") > 0){
	        text = text + "<li>Mamallian orthologs: " + oRecord.getData("countOfOrthologs") + '</li>';
        }
        if(oRecord.getData("countOfMappingResults") > 0){
	        text = text + "<li>Mapping data: " + oRecord.getData("countOfMappingResults") + '</li>';
        }
        if(oRecord.getData("countOfProbes") > 0){
	        text = text + "<li>Molecular probes and clones: " + oRecord.getData("countOfProbes") + '</li>';
        }
        if(oRecord.getData("countOfSequenceResults") >0){
	        text = text + "<li>Sequences: " + oRecord.getData("countOfSequenceResults") + '</li>';  
        }
		elLiner.innerHTML = text + '</ul>';      
    };
    // Adds the formatters above to the to the symbol col
    YAHOO.widget.DataTable.Formatter.vol = this.volFormatter;
    YAHOO.widget.DataTable.Formatter.id = this.idFormatter;
    YAHOO.widget.DataTable.Formatter.data = this.dataFormatter;

    // Column definitions
    var myColumnDefs = [ // sortable:true enables sorting
        {key:"idFormatter", label:"PubMed ID<br/>MGI Reference ID", sortable:false, 
            formatter:"id", width:75},
        {key:"authors", label:"Author(s)", sortable:true, width:250},
        {key:"title", label:"Title", sortable:false},
        {key:"journal", label:"Journal", sortable:true, width:150},
        {key:"year", label:"Year", sortable:true, width:40},
        {key:"volFomatter", label:"Vol(Iss)Pg", sortable:false, formatter:"vol", width:100},
        {key:"dataFomatter", label:"Curated Data", sortable:false, formatter:"data", width:300}
    ];

    // DataSource instance
    myDataSource = new YAHOO.util.DataSource("${configBean.fewiUrl}reference/json?${queryString}&");
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
        rowsPerPage : 25,
        pageLinks: 5,
        recordOffset: 1
    });

    // DataTable configurations
    var myConfigs = {
        paginator : myPaginator,
        dynamicData : true,
        initialLoad : false
    };  
    
    // DataTable instance
    myDataTable = new YAHOO.widget.DataTable("dynamicdata", myColumnDefs, myDataSource, myConfigs);

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
			facets[i] = selections[i];
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
							  context:["yui-col1","tr","tr", ["beforeShow"]],
							  constraintoviewport : true,
							  buttons : [{ text:"Go", handler:handleSubmit, isDefault:true } ]
							});

	// Wire up the success and failure handlers
	facetDialog.callback = { success: handleSuccess,
						     failure: handleFailure };

	// Render the Dialog
	facetDialog.render();
    
	// DataSource instance
	var facetAuthorDS = new YAHOO.util.DataSource("${configBean.fewiUrl}reference/facet/author?${queryString}");
	facetAuthorDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetAuthorDS.responseSchema = {resultsList: "resultFacets"};

	var facetJournalDS = new YAHOO.util.DataSource("${configBean.fewiUrl}reference/facet/journal?${queryString}");
	facetJournalDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetJournalDS.responseSchema = {resultsList: "resultFacets"};

	var facetYearDS = new YAHOO.util.DataSource("${configBean.fewiUrl}reference/facet/year?${queryString}");
	facetYearDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetYearDS.responseSchema = {resultsList: "resultFacets"};

	var facetDataDS = new YAHOO.util.DataSource("${configBean.fewiUrl}reference/facet/data?${queryString}");
	facetDataDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
	facetDataDS.responseSchema = {resultsList: "resultFacets"};

	var populateFacet = function (oRequest, oResponse, oPayload) {
		var res = oResponse.results;
		var options = [];

		for(x in res){
			options[x] = '<input type="checkbox" name="' + oPayload.name + '" value="' + res[x] + '"> ' + res[x];
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
</script>

${templateBean.templateBodyStopHtml}
