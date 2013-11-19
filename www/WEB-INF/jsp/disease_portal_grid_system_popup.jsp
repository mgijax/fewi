<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/WEB-INF/jsp/google_analytics_pageview.jsp" %>

<style type="text/css">
  #hdpSystemPopupHeader{ 
    min-height:32px; 
    min-width:500px;
    background-color:#DFEFFF; 
    margin-bottom:12px;
    padding:3px;
    border:thin solid #002255;
    font-family: Verdana,Arial,Helvetica; 
    font-size: 20px;
  }
  #hdpSystemPopupLegend
  {
  	display: table-cell;
  	border-spacing:0px; 
  	border-collapse:collapse; 
  	font-family: Verdana,Arial,Helvetica;
  	font-size: 12px;
  	background-color: #DFEFFF;
  	margin-bottom: 8px;
  }
  #hdpSystemPopupLegend td
  {
  	border: solid 1px black;
  	padding: 0px 4px;
  }
  .bsn
  {
  	color: #FF8300;
  	font-weight: bold;
  	font-size: 120%;
  	font-family: serif;
  	top: -2px;
  	right: -1px;
  	position: absolute;
  }
  .bsn_legend
  {
 	color: #FF8300;
  	font-weight: bold;
  	font-size: 120%;
  	font-family: serif;
  }
</style>

<script>
	// change window title on page load
	// 00D7 is unicode for &times;
    document.title = '${gridClusterString} \u00D7 ${termHeader} Grid Drill Down';
</script>

<!-- Table and Wrapping div -->

<div id="hdpSystemPopupHeader">Data for ${gridClusterString} and ${termHeader} <c:if test="${ termHeader!='normal phenotype'}"> abnormalities </c:if>   </div>

<table id="hdpSystemPopupLegend">
<tr>
	<td>*</td><td>Aspects of the system are reported to show a normal phenotype</td></tr><tr>
	<td class="bsn_legend">!</td><td>Indicates phenotype varies with strain background</td>
</tr>
</table>

<%@ include file="/WEB-INF/jsp/disease_portal_grid_popup_grid.jsp" %>
