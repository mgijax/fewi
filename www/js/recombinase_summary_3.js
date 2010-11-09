// third section of Javascript to be filled in the recombinase_summary.jsp
// (There are 3 sections because HTML markup goes in between them.)
// This file contains functions to reset the checkboxes at the top of the
// page, to define and display the DataTable, and to create its Tooltips.

function resetCheckboxes() {
	var hidden = [ "Adipose Tissue", "Alimentary System", "Branchial Arches",
	    "Cardiovascular System", "Cavities and their Linings", "Endocrine System",
	    "Head", "Hemolymphoid System", "Integumental System", "Limbs",
	    "Liver and Biliary System", "Mesenchyme", "Muscle", "Nervous System",
	    "Renal and Urinary System", "Reproductive System", "Respiratory System",
	    "Sensory Organs", "Skeletal System", "Tail", "Early Embryo",
	    "Extraembryonic Component", "Embryo Other", "Postnatal Other", "Allele Type",
	    "Inducible" ];

	var visible = [ "Allele Synonyms", "IMSR", "References" ];

	for (i = 0; i < hidden.length; i++) {
		hideColumn(hidden[i]);
	}
	for (j = 0; j < visible.length; j++) {
		showColumn(visible[i]);
	}
	showColumn(YAHOO.mgiData.selectedSystem);
}

function main() {
    // Column definitions -- sortable:true enables sorting
    // These are our actual columns, in the default ordering.
    var myColumnDefs = [
        {key:"driver", 
            label:"<B>Driver</B>",
            width:90, 
            sortable:true},
        {key:"nomenclature", 
            label:"<B>Allele Symbol<br/>Gene; Allele Name</B>",
			sortable:true,
			width:245}, 
		{key:"detectedCount", 
			label:"<B>Recombinase<br/>Data</B>", 
			sortable:true, 
			width:220},
	    {key:"inAdiposeTissue", 
			label:"Adipose<br/>Tissue", 
			sortable:true,
			hidden:true,
			width:54},
		{key:"inAlimentarySystem",
			label:"Alimentary<br/>System",
			sortable:true,
			hidden:true,
			width:60},
		{key:"inBranchialArches",
			label:"Branchial<br/>Arches",
			sortable:true,
			hidden:true,
			width:60},
		{key:"inCardiovascularSystem",
			label:"Cardiovascular<br/>System",
			sortable:true,
			hidden:true,
			width:88},
		{key:"inCavitiesAndLinings",
			label:"Cavities &amp;<br/>their Linings",
			sortable:true,
			hidden:true,
			width:72},
		{key:"inEndocrineSystem",
			label:"Endocrine<br/>System",
			sortable:true,
			hidden:true,
			width:64},
		{key:"inHead",
			label:"Head",
			sortable:true,
			hidden:true,
			width:54},
		{key:"inHemolymphoidSystem",
			label:"Hemolymphoid<br/>System",
			sortable:true,
			hidden:true,
			width:88},
		{key:"inIntegumentalSystem",
			label:"Integumental<br/>System",
			sortable:true,
			hidden:true,
			width:82},
		{key:"inLimbs",
			label:"Limbs",
			sortable:true,
			hidden:true,
			width:54},
		{key:"inLiverAndBiliarySystem",
			label:"Liver &amp;<br/>Biliary System",
			sortable:true,
			hidden:true,
			width:84},
		{key:"inMesenchyme",
			label:"Mesenchyme",
			sortable:true,
			hidden:true,
			width:82},
		{key:"inMuscle",
			label:"Muscle",
			sortable:true,
			hidden:true,
			width:54},
		{key:"inNervousSystem",
			label:"Nervous<br/>System",
			sortable:true,
			hidden:true,
			width:60},
		{key:"inRenalAndUrinarySystem",
			label:"Renal &amp;<br/>Urinary System",
			sortable:true,
			hidden:true,
			width:90},
		{key:"inReproductiveSystem",
			label:"Reproductive<br/>System",
			sortable:true,
			hidden:true,
			width:80},
		{key:"inRespiratorySystem",
			label:"Respiratory<br/>System",
			sortable:true,
			hidden:true,
			width:72},
		{key:"inSensoryOrgans",
			label:"Sensory<br/>Organs",
			sortable:true,
			hidden:true,
			width:54},
		{key:"inSkeletalSystem",
			label:"Skeletal<br/>System",
			sortable:true,
			hidden:true,
			width:60},
		{key:"inTail",
			label:"Tail",
			sortable:true,
			hidden:true,
			width:54},
		{key:"inEarlyEmbryo",
			label:"Early<br/>Embryo",
			sortable:true,
			hidden:true,
			width:60},
		{key:"inExtraembryonicComponent",
			label:"Extraembryonic<br/>Component",
			sortable:true,
			hidden:true,
			width:90},
		{key:"inEmbryoOther",
			label:"Embryo<br/>Other",
			sortable:true,
			hidden:true,
			width:60},
		{key:"inPostnatalOther",
			label:"Postnatal<br/>Other",
			sortable:true,
			hidden:true,
			width:60},
        {key:"synonyms",
            label:"<B>Allele Synonym</B>",
            width:170,
            sortable:false},
   		{key:"alleleType", 
            label:"<B>Allele<br/>Type</B>",
            width:60, 
			hidden:true,
            sortable:true},
        {key:"inducibleNote", 
            label:"<B>Inducible</B>",
            width:58, 
			hidden:true,
            sortable:true},
        {key:"imsrCount", 
            label:"<B>Find Mice<br/>(IMSR)</B>",
            width:60, 
            sortable:true},
        {key:"countOfReferences", 
            label:"<B>Refs</B>",
            width:36, 
            sortable:true}
    ];

    // DataSource instance
    var myDataSource = new YAHOO.util.DataSource("json?${queryString}&");

    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
    myDataSource.responseSchema = {
        resultsList: "resultObjects",
        fields: [
			{key:"driver"},
			{key:"nomenclature"},
            {key:"detectedCount"},
            {key:"inAdiposeTissue"},
            {key:"inAlimentarySystem"},
            {key:"inBranchialArches"},
            {key:"inCardiovascularSystem"},
            {key:"inCavitiesAndLinings"},
            {key:"inEndocrineSystem"},
            {key:"inHead"},
            {key:"inHemolymphoidSystem"},
            {key:"inIntegumentalSystem"},
            {key:"inLimbs"},
            {key:"inLiverAndBiliarySystem"},
            {key:"inMesenchyme"},
            {key:"inMuscle"},
            {key:"inNervousSystem"},
            {key:"inRenalAndUrinarySystem"},
            {key:"inReproductiveSystem"},
            {key:"inRespiratorySystem"},
            {key:"inSensoryOrgans"},
            {key:"inSkeletalSystem"},
            {key:"inTail"},
            {key:"inEarlyEmbryo"},
            {key:"inExtraembryonicComponent"},
            {key:"inEmbryoOther"},
            {key:"inPostnatalOther"},
            {key:"synonyms"},
            {key:"alleleType"},
            {key:"inducibleNote"},
            {key:"imsrCount"},
            {key:"countOfReferences"}
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
        draggableColumns : true,
        initialLoad : false
    };  
    
    // DataTable instance
    var myDataTable = new YAHOO.widget.DataTable("dynamicdata", myColumnDefs, 
    	    myDataSource, myConfigs);
    
    YAHOO.mgiData.myDataTable = myDataTable;

    // Show loading message while page is being rendered
    myDataTable.showTableMessage(myDataTable.get("MSG_LOADING"), 
    	    YAHOO.widget.DataTable.CLASS_LOADING);    
    
    // Integrate with Browser History Manager
    var History = YAHOO.util.History;

    // Define a custom function to route sorting through the Browser History Manager
    var handleSorting = function (oColumn) {
        // Calculate next sort direction for given Column
        var sDir = this.getColumnSortDir(oColumn);
        
        // The next state will reflect the new sort values
        // while preserving existing pagination rows-per-page
        // As a best practice, a new sort will reset to page 0
        var newState = generateRequest(0, oColumn.key, sDir, 
                this.get("paginator").getRowsPerPage());

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
            key: pRequest['sort'] || "driver",
            dir: pRequest['dir'] ? "yui-dt-" + pRequest['dir'] : "yui-dt-asc" // Convert from server value to DataTable format
        };
        return true;
    };

    // Returns a request string for consumption by the DataSource
    var generateRequest = function(startIndex,sortKey,dir,results) {
    	startIndex = startIndex || 0;
        sortKey   = sortKey || "driver";
        dir   = (dir) ? dir.substring(7) : "asc"; // Converts from DataTable format "yui-dt-[dir]" to server value "[dir]"
        results   = results || 25;
        return "results="+results+"&startIndex="+startIndex+"&sort="+sortKey+"&dir="+dir;
    };

    // Called by Browser History Manager to trigger a new state
    var handleHistoryNavigation = function (request) {
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
};
main();

function parseRequest(request){
	var reply = [];
	var kvPairs = request.split('&');
	for (pair in kvPairs) {
		var kv = kvPairs[pair].split('=');
		reply[kv[0]] = kv[1];
	}
	return reply;
}

showColumn(YAHOO.mgiData.selectedSystem);

// activate tooltips in span tags in the table (all can share a common
// Tooltip object and pick up their contents from the Spans' title attributes
YAHOO.mgiData.myTip = new YAHOO.widget.Tooltip ("mgiTip", {
		context : YAHOO.util.Selector.query("span", YAHOO.mgiData.myDataTable) });
