<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Reference Summary</title>

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

</head>
<body class="yui-skin-sam">


foo


<div id="dynamicdata"></div>

<script type="text/javascript">
YAHOO.example.DynamicData = function() {
	
	// this function formats the marker detail link
    this.symbolLinkFormatter = function(elLiner, oRecord, oColumn, oData) {
		elLiner.innerHTML = ' <a href="/fewi/mgi/reference/' + oRecord.getData("refernceId") + '">' + oRecord.getData("jnum") + '</a>';
    };
    
    // Adds the formatter above to the to the symbol col
    YAHOO.widget.DataTable.Formatter.symbolLink = this.symbolLinkFormatter;

    // Column definitions
    var myColumnDefs = [ // sortable:true enables sorting
        {key:"jnumID", label:"jnumID", sortable:true},
        {key:"journal", label:"journal", sortable:false},
        {key:"title", label:"title", sortable:false},
        {key:"authors", label:"authors", sortable:false}
    ];
    
    // DataSource instance
    var myDataSource = new YAHOO.util.DataSource("json?${queryString}&");
    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
    myDataSource.responseSchema = {
        resultsList: "referenceList",
        fields: [
			{key:"jnumID"},
            {key:"journal"},
            {key:"title"},
            {key:"authors"}
        ],
        metaFields: {
            totalRecords: "totalCount" // Access to value in the server response
        }
    };
    
    // DataTable configuration
    var myConfigs = {
        dynamicData: true, // Enables dynamic server-driven data
        sortedBy : {key:"jnumID", dir:YAHOO.widget.DataTable.CLASS_ASC}, // Sets UI initial sort arrow
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
</body>
</html>