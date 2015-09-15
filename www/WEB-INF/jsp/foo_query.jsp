<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Foo Search</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapper" userdoc="foo_help.shtml">	
	<span class="titleBarMainTitle">Foo Query</span>
</div>

<div id="outer"  class="bluebar">
	<div id="toggleQF"><span id="toggleLink" class="filterButton">Click to hide search</span></div>
	<div id="qwrap">
		<%@ include file="/WEB-INF/jsp/foo_form.jsp" %>
	</div>
</div>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var qDisplay = false;
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/foo_query.js"></script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
