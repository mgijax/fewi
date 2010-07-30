<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>


    <meta http-equiv="content-type" content="text/html; charset=utf-8">
<title>Marker Summary</title>

<style type="text/css">
/*margin and padding on body element
  can introduce errors in determining
  element position and are not recommended;
  we turn them off as a foundation for YUI
  CSS treatments. */
body {
	margin:0;
	padding:0;
}
</style>

<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.1/build/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.1/build/paginator/assets/skins/sam/paginator.css" />
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.1/build/datatable/assets/skins/sam/datatable.css" />
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/yahoo-dom-event/yahoo-dom-event.js"></script>

<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/connection/connection-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/json/json-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/element/element-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/paginator/paginator-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/datasource/datasource-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/datatable/datatable-min.js"></script>

<!--there is no custom header content for this example-->

</head>

<body class="yui-skin-sam">


<h1>Marker Summary Json</h1>

<div class="exampleIntro">
	<p>This example enables server-side sorting and pagination for data that is
	dynamic in nature.</p>			
</div>

<!--BEGIN SOURCE CODE FOR EXAMPLE =============================== -->

<div id="dynamicdata"></div>

<script type="text/javascript">
YAHOO.example.DynamicData = function() {
	
	// this function formats the marker detail link
    this.symbolLinkFormatter = function(elLiner, oRecord, oColumn, oData) {
		elLiner.innerHTML = ' <a href="/fewi/mgi/marker/' + oRecord.getData("markerKey") + '">' + oRecord.getData("symbol") + '</a>';
    };
    
    // Adds the formatter above to the to the symbol col
    YAHOO.widget.DataTable.Formatter.symbolLink = this.symbolLinkFormatter;

    // Column definitions
    var myColumnDefs = [ // sortable:true enables sorting
        {key:"symbol", label:"Symbol", sortable:true, formatter:"symbolLink"},
        {key:"name", label:"Name", sortable:true},
        {key:"type", label:"Type", sortable:true}
    ];
    
    // DataSource instance
    var myDataSource = new YAHOO.util.DataSource("json?${queryString}&");
    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
    myDataSource.responseSchema = {
        resultsList: "markerList",
        fields: [
			{key:"markerKey"},
            {key:"symbol"},
            {key:"name"},
            {key:"type"}
        ],
        metaFields: {
            totalRecords: "maxCount" // Access to value in the server response
        }
    };
    
    // DataTable configuration
    var myConfigs = {
        dynamicData: true, // Enables dynamic server-driven data
        sortedBy : {key:"symbol", dir:YAHOO.widget.DataTable.CLASS_ASC}, // Sets UI initial sort arrow
        paginator: new YAHOO.widget.Paginator({ rowsPerPage: 25,
            template: "{PreviousPageLink} <strong>{PageLinks}</strong> {NextPageLink} <span style=align:right;>{RowsPerPageDropdown}</span>",
            rowsPerPageOptions: [10,25,50,100],
            pageLinks: 5 }) // Enables pagination 
    };
    
    // DataTable instance
    var myDataTable = new YAHOO.widget.DataTable("dynamicdata", myColumnDefs, myDataSource, myConfigs);
    // Update totalRecords on the fly with value from server
    myDataTable.handleDataReturnPayload = function(oRequest, oResponse, oPayload) {
        oPayload.totalRecords = oResponse.meta.totalRecords;
        return oPayload;
    }
    
    return {
        ds: myDataSource,
        dt: myDataTable
    };
        
}();
</script>


