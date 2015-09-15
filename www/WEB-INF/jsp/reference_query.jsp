<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>References</title>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- begin header bar -->
<div id="titleBarWrapper" userdoc="reference_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">References</span>
</div>
<!-- end header bar -->

<div id="outer"  class="bluebar">
	<div id="toggleQF"><span id="toggleLink" class="filterButton">Click to hide search</span></div>
	<div id="qwrap">
		<%@ include file="/WEB-INF/jsp/reference_form.jsp" %>
	</div>
</div>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var qDisplay = false;
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/reference_query.js"></script>


<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
