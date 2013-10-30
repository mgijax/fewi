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
</style>

<script>
	// change window title on page load
	// 00D7 is unicode for &times;
    document.title = '${gridClusterString} \u00D7 ${termHeader} Grid Drill Down';
</script>

<!-- Table and Wrapping div -->

<div id="hdpSystemPopupHeader">Data for ${gridClusterString} and ${termHeader}</div>

<%@ include file="/WEB-INF/jsp/disease_portal_grid_popup_grid.jsp" %>
