<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "java.util.List" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Microarray Probeset Summary - MGI</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<meta name="description" content="Microarray probesets associated with genome feature {gene symbol}">
<meta name="keywords" content="MGI, mouse, microarray, probeset, MGI gene"> 
<meta name="robots" content="NOODP"/>
<meta name="robots" content="NOYDIR"/> 

<style type="text/css">
td.box { border-top:thin solid grey;border-left:thin solid grey;border-right:thin solid grey;border-bottom:thin solid grey; padding: 2px; }
th.box { border-top:thin solid grey;border-left:thin solid grey;border-right:thin solid grey;border-bottom:thin solid grey; padding: 2px; }
</style>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var querystring = "${queryString}";
</script>

<% Marker marker = (Marker)request.getAttribute("marker");
   StyleAlternator leftTdStyles 
      = new StyleAlternator("detailCat1","detailCat2");
   StyleAlternator rightTdStyles 
      = new StyleAlternator("detailData1","detailData2");
%>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapper" userdoc="EXPRESSION_affy_probe_help.shtml">	
  <div class="yourInputButton">
    <form name="YourInputForm">
      <input class="searchToolButton" value="Your Input Welcome" name="yourInputButton" onclick='window.open("${configBean.MGIHOME_URL}feedback/feedback_form.cgi?accID=${marker.primaryID}&amp;dataDate=<fmt:formatDate type='date' value='${databaseDate}' dateStyle='short'/>")' onmouseover="return overlib('We welcome your corrections and new data. Click here to contact us.', LEFT, WIDTH, 200, TIMEOUT, 2000);" onmouseout="nd();" type="button">
    </form>
  </div>

  <div name="centeredTitle">
  <span class="titleBarMainTitle">Microarray Probeset Summary</span>
  </div>
</div>

<table class="detailStructureTable">
  <!-- row 1 : marker symbol, name, ID -->
  <tr>
    <td class="<%= leftTdStyles.getNext() %>">
       Symbol<br/>
       Name<br/>
       ID
    </td>
    <td class="<%= rightTdStyles.getNext() %>">
      <a href="${configBean.FEWI_URL}marker/${marker.primaryID}" class="symbolLink">${marker.symbol}</a> 
      	<c:if test="${marker.status == 'interim'}">(Interim)</c:if><br/>
      <span>${marker.name}</span><br/>
      <span>${marker.primaryID}</span>
    </td>
  </tr>

  <!-- row 2 : microarray probesets -->
  <tr>
    <td class="<%= leftTdStyles.getNext() %>">
      Probesets
    </td>
    <td class="<%= rightTdStyles.getNext() %>">
      ${marker.countOfMicroarrayProbesets} associated probeset id<c:if test="${marker.countOfMicroarrayProbesets > 1}">s</c:if>
      <div id="myMarkedUpContainer">
	<table id="myTable">
	  <thead>
	    <tr><th>Probeset ID</th><th>Microarray Platform</th></tr>
	  </thead>
	  <tbody>
	    <c:forEach var="item" items="${marker.probesets}">
	      <tr><td>${item.probesetID}</td><td>${item.platform}</td></tr>
	    </c:forEach>
	  </tbody>
	</table>
      </div>
    </td>
  </tr>

  <!-- row 3 : FTP access to microarray data as reports -->
  <tr>
    <td class="<%= leftTdStyles.getNext() %>">
      FTP
    </td>
    <td class="<%= rightTdStyles.getNext() %>">
      <table>
	      <tr><th class="box">MGI microarray annotation files</th></tr>

	<c:forEach var="report" items="${reportsOrdered}">
	<tr><td class="box"><a href="${configBean.PUB_REPORTS_URL}${reports[report]}">${report}</a></td></tr>
	</c:forEach>
      </table>
    </td>
  </tr>
</table> 

<script type="text/javascript"> 
var myDataSource = new YAHOO.util.DataSource(YAHOO.util.Dom.get("myTable")); 
myDataSource.responseType = YAHOO.util.DataSource.TYPE_HTMLTABLE; 
myDataSource.responseSchema = {
    fields: [{key:"Probeset ID", parser:"String"},
            {key:"Microarray Platform"}
    ]
};

var myColumnDefs = [
    {key:"Probeset ID", sortable:true},
    {key:"Microarray Platform", sortable:true}
]; 
	 
var myDataTable = new YAHOO.widget.DataTable("myMarkedUpContainer", myColumnDefs, myDataSource,  {sortedBy:{key:"Probeset ID", dir:YAHOO.widget.DataTable.CLASS_ASC}}); 
</script> 

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

