<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% Reference reference = (Reference)request.getAttribute("reference"); %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

${templateBean.templateHeadHtml}

  <title>Mouse Sequence Summary Report</title>
  <%@ include file="/WEB-INF/jsp/includes.jsp" %>

${templateBean.templateBodyStartHtml}



<!-- header bar -->
<div id="titleBarWrapper" style="max-width:1200px" userdoc="sequence_detail.shtml">    
    <!--myTitle -->
    <span class="titleBarMainTitle">Mouse Sequences Summary Report</span>
</div>


<!-- structural table -->
<table class="detailStructureTable">

<!-- ID/Version -->
<tr >
  <td class="detailCat1">
       <b>Reference</b>
  </td>
  <td class="detailData1">
    <a style="font-size:x-large;  font-weight: bold;" 
      href="${configBean.FEWI_URL}reference/${reference.jnumID}">${reference.jnumID}</a>
    <br/>
    ${reference.shortCitation}
  </td>
</tr>
</table>



<hr>



<div id="dynamicdata"></div>

<script type="text/javascript">
function main() {
    // Column definitions -- sortable:true enables sorting
    // These are our actual columns, in the default ordering.
    var myColumnDefs = [
        {key:"seqForward", 
            label:"<b>Select</b>",
            width:90, 
            sortable:false},
        {key:"seqInfo", 
            label:"<b>Sequence</b>",
            width:180, 
            sortable:true},
        {key:"seqType", 
            label:"<b>Type</b>",
            sortable:true,
            width:80}, 
        {key:"length", 
            label:"<b>Length</b>", 
            sortable:true, 
            width:60},
        {key:"strainSpecies", 
            label:"<b>Strain/Species</b>", 
            sortable:true, 
            width:90},
        {key:"description", 
            label:"<b>Description From<br/>Sequence Provider</b>", 
            sortable:true, 
            width:300},
        {key:"cloneCollection", 
            label:"<b>Clone<br/>Collection</b>", 
            sortable:true, 
            width:100},
        {key:"markerSymbol", 
            label:"<b>Marker<br/>Symbol</b>", 
            sortable:true, 
            width:100}
    ];

    // DataSource instance
    var myDataSource = new YAHOO.util.DataSource("${configBean.FEWI_URL}sequence/json?refKey=${reference.referenceKey}&");

    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
    myDataSource.responseSchema = {
        resultsList: "summaryRows",
        fields: [
            {key:"seqForward"},
            {key:"seqInfo"},
            {key:"seqType"},
            {key:"length"},
            {key:"strainSpecies"},
            {key:"description"},
            {key:"cloneCollection"},
            {key:"markerSymbol"}
        ],
        metaFields: {
            totalRecords: "totalCount"
        }
    };

    // DataTable configurations
    var myConfigs = {
        dynamicData : true,
        draggableColumns : true,
        initialLoad : true
    };  
    
    // DataTable instance
    var myDataTable = new YAHOO.widget.DataTable("dynamicdata", myColumnDefs, 
            myDataSource, myConfigs);
    
    // Show loading message while page is being rendered
    myDataTable.showTableMessage(myDataTable.get("MSG_LOADING"), 
            YAHOO.widget.DataTable.CLASS_LOADING);    
    

};

// CALLING MAIN FUNCTION
main();

</script>




${templateBean.templateBodyStopHtml}

