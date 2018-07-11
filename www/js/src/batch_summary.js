var myDataSource;
var myDataTable;
var handleIdSet;
var generateRequest;
var totalCount = 0;
var numConfig = {thousandsSeparator: ','};

// Integrate with Browser History Manager
var History = YAHOO.util.History;

var getMarkerIds = function() {
   var url = fewiurl + "batch/idList?"   + querystring + "&";
   var callback = {
      success : function(oResponse) {
         $("#mousemineids").val(oResponse.responseText);
      },
   };
   YAHOO.util.Connect.asyncRequest('GET', url, callback);
};

(function () {		
    // Column definitions -- sortable:true enables sorting
    // These are our actual columns, in the default ordering.
	
	// default columns
    var myColumnDefs = [
        {key:"term", 
            label:"Input",
            sortable:false},
        {key:"type", 
            label:"Input<br/>Type",
            sortable:false},
        {key:"markerId", 
            label:"MGI Gene/<br/>Marker ID",
            sortable:false}
    ];
    
    // optional columns
    if (nomenclature){
    	myColumnDefs.push({label:"<div class='center'>Nomenclature</div>",
            sortable:false,
            children: [{key: "symbol", label: "Symbol"},
                {key: "name", label: "Name"},
                {key: "feature", label: "Feature Type"}]});
    }
    if (loco){
    	myColumnDefs.push({label:"C57BL/6J Genome Location - " + ncbiBuild,
    	    sortable:false,
    	    children: [{key: "chromosome",
    	    		label: "Chr"},
    	        {key: "strand",
    	        	label: "Strand"},
    	        {key: "start",
    	            label: "Start"},
    	        {key: "end",
    	        	label: "End"}]});
    }
    if (ensembl){
    	myColumnDefs.push({key:"ensemblIds", 
    	    label:"Ensembl ID",
    	    sortable:false});
    }
    if (entrez){
    	myColumnDefs.push({key:"entrezIds", 
    	    label:"Entrez Gene ID",
    	    sortable:false});
    }
    if (go){
    	myColumnDefs.push({label:"Gene Ontology (GO)",
    	    sortable:false,
    	    children: [{key: "goIds",
	    		label: "ID"},
	    	{key: "goTerms",
		    	label: "Term"},
    		{key: "goCodes",
	    		label: "Code"}]});
    }
    if (mp){
    	myColumnDefs.push({label:"Mammalian Phenotype (MP) <a onMouseOver='popupHelp(\"mp\");' class='helpLink'>Caveat &amp; Help</a>",
    	    sortable:false,
    	    children: [{key: "mpIds",
    	    	label: "ID"},
	    	{key: "mpTerms",
		    		label: "Term"}]});
    }
    if (doa){
    	myColumnDefs.push({label:"Human Disease (DO) <a onMouseOver='popupHelp(\"do\");' class='helpLink'>Caveat &amp; Help</a>",
    	    sortable:false,
    	    children: [{key: "doIds",
	    		label: "ID"},
	    	{key: "doTerms",
		    	label: "Term"}]});
    }
    if (allele){
    	myColumnDefs.push({label:"MGI Allele",
    	    sortable:false,
    	    children: [{key: "alleleIds",
	    		label: "ID"},
	    	{key: "alleleSymbols",
		    	label: "Symbol"}]});
    }
    if (exp){
    	myColumnDefs.push({label:"Gene Expression <a onMouseOver='popupHelp(\"exp\");' class='helpLink'>Caveat &amp; Help</a>",
    	    sortable:false,
    	    children: [{key: "expressionStructure",
	    		label: "Anatomical Structure"},
	        {key: "expressionResultCount",
	        	label: "Assay Results"},
	        {key: "expressionDetectedCount",
	            label: "Detected"},
	        {key: "expressionNotDetectedCount",
	        	label: "Not Detected"}]});
    }
    if (refsnp){
    	myColumnDefs.push({key:"refsnpIds", 
    	    label:"RefSNP ID",
    	    sortable:false});
    }
    if (refseq){
    	myColumnDefs.push({key:"refseqIds", 
    	    label:"GenBank/RefSeq ID",
    	    sortable:false});
    }
    if (uniprot){
    	myColumnDefs.push({key:"uniprotIds", 
    	    label:"Uniprot ID",
    	    sortable:false});
    }

    // DataSource instance
    var myDataSource =    new YAHOO.util.XHRDataSource(fewiurl + "batch/json?"   + querystring + "&");

    myDataSource.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    myDataSource.responseSchema = {
        resultsList: "summaryRows",
        fields: [
            {key:"term"},
            {key:"type"},
            {key:"markerId"},
            {key:"symbol"},
            {key:"name"},
            {key:"feature"},
            {key:"chromosome"},
            {key:"strand"},
            {key:"start"},
            {key:"end"},
        	{key:"ensemblIds"},
        	{key:"entrezIds"},
        	{key:"goIds"},
        	{key:"goTerms"},
        	{key:"goCodes"},
        	{key:"mpIds"},
        	{key:"mpTerms"},
        	{key:"doIds"},
        	{key:"doTerms"},
        	{key:"alleleIds"},
        	{key:"alleleSymbols"},
        	{key:"expressionStructure"},
        	{key:"expressionResultCount"},
        	{key:"expressionDetectedCount"},
        	{key:"expressionNotDetectedCount"},
        	{key:"refsnpIds"},
        	{key:"refseqIds"},
        	{key:"uniprotIds"}
        ],
        metaFields: {
	        totalRecords: "totalCount",
	        metaObj: "meta",
	        paginationRecordOffset : "startIndex",
	        paginationRowsPerPage : "pageSize"
        }
    };
    
    myDataSource.maxCacheEntries = 3;
    myDataSource.connXhrMode = "cancelStaleRequests";
    myDataSource.connMethodPost = true;
    myDataSource.connTimeout = 0;

    // Create the Paginator
    var myPaginator = new YAHOO.widget.Paginator({
        template : "{FirstPageLink} {PreviousPageLink}<strong>{PageLinks}</strong> {NextPageLink} {LastPageLink} <span style=align:right;>{RowsPerPageDropdown}</span><br/>{CurrentPageReport}",
        pageReportTemplate : "Showing row(s) {startRecord} - {endRecord} of {totalRecords}",
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
        MSG_EMPTY:    'No markers found.'
    };   
    
    // DataTable instance
    var myDataTable = new YAHOO.widget.DataTable("batchdata", myColumnDefs, myDataSource, myConfigs);
    
    // Show loading message while page is being rendered
    myDataTable.showTableMessage(myDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);    

    // Define a custom function to route pagination through the Browser History Manager
    var handlePagination = function(state) {
        // The next state will reflect the new pagination values
        // while preserving existing sort values
        var newState = generateRequest(state.recordOffset, state.rowsPerPage);
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

        oPayload.totalRecords = meta.totalRecords || oPayload.totalRecords;
        updateCount('totalCount', oPayload.totalRecords);
        updateCount('markerCount', meta.metaObj.counts.marker);
        
        var filterCount = YAHOO.util.Dom.get('filterCount');
        if (!YAHOO.lang.isNull(filterCount)){
        	setText(filterCount, YAHOO.util.Number.format(oPayload.totalRecords, numConfig));
        }
        oPayload.pagination = {
            rowsPerPage: Number(pRequest['results'][0]) || 25,
            recordOffset: Number(pRequest['startIndex'][0]) || 0
        };
        var reportButton = YAHOO.util.Dom.get('textDownload');
        var facetQuery = generateRequest(0, oPayload.totalRecords);
        if (!YAHOO.lang.isNull(reportButton)){      	
	        reportButton.setAttribute('href', fewiurl + 'batch/report.txt?' + querystring + '&' + facetQuery);
        }
        reportButton = YAHOO.util.Dom.get('excelDownload');
        if (!YAHOO.lang.isNull(reportButton)){      	
	        reportButton.setAttribute('href', fewiurl + 'batch/report.xlsx?' + querystring + '&' + facetQuery);
        }
        return true;
    };
    
	updateCount = function (elName, newCount) {
		var countEl = YAHOO.util.Dom.get(elName);
		if (!YAHOO.lang.isNull(countEl)){
			setText(countEl, YAHOO.util.Number.format(newCount, numConfig)); 
	    	totalCount = newCount;
		}
	};
    
    // Returns a request string for consumption by the DataSource
    generateRequest = function(startIndex, results) {
        results = results || 25;
        return "results="+results+"&startIndex="+startIndex;
    };

    handleIdSet = function(data, other) {
       console.log(other);
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
        getMarkerIds();
    };

    // Calculate the first request
    var initialRequest = History.getBookmarkedState("myDataTable") || // Passed in via URL
                       generateRequest(0, 25); // Get default values

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

function popupHelp(help_cat){	 
	var do_help = "<b>Human Disease (DO) Annotations</b><ul><li>Human Disease (DO) terms appear by gene.</li>\
	    <li>Each term listed indicates that a mutant allele involving this gene is present in a mouse genotype used as a disease model.</li></ul><b>Caveats</b>\
	    <ul><li>The term does not necessarily imply that mutations in that gene contribute to or cause the disease.</li>\
	    <li>Analyzed mice may have causative mutations in other genes.</li>\
	    <li>Wide variation exists due to homozygotes vs. heterozygotes and different strain backgrounds.</li></ul>\
	    <b>Click on the disease term ...</b><ul><li>to see the mouse genotypes used as disease models.</li></ul>";
	// text for mp help popup
	var mp_help = "<b>Phenotype Annotations</b><ul><li>Mammalian Phenotype (MP) terms appear by gene.</li>\
       <li>Each term describes a mouse phenotype with some mutation in that gene.</li>\
       <li>Mutations may be in the endogenous gene or an insertion of the gene or its homolog.</li></ul><b>Caveats</b>\
	    <ul><li>The term does not necessarily imply that mutations in that gene contribute to or cause the phenotype.</li>\
	    <li>Analyzed mice may have causative mutations in other genes.</li>\
	    <li>Wide phenotypic variation exists due to homozygotes vs. heterozygotes and different strain backgrounds.</li></ul>\
	    <b>Click (details) after any phenotype term ...</b><ul><li>to see genotypes and additional annotation information.</li></ul>";
	// text for expression help popup
	var exp_help = "Please note that some of these results may have been obtained from mutant specimens.  The detected counts include also specimens for which detected = ambiguous or not specified (as well as present). ";  
	 
	var help_text = '';
	if (help_cat == 'mp') {
		help_text = mp_help;
	} else if (help_cat == 'do'){
		help_text = do_help;
	} else {
		help_text = exp_help;
	}

	 // template used to build column popup help
	 return overlib(help_text, STICKY, CAPTION, 'Caveat &amp; Help', LEFT, BELOW, WIDTH, 300, DELAY, 600, CLOSECLICK, CLOSETEXT, 'Close X');
};
