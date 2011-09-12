<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

${templateBean.templateHeadHtml}

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>MGI Batch Query</title>

${templateBean.templateBodyStartHtml}

<style type="text/css">
    /*:focus { -moz-outline-style: none; }*/
    a { outline:expression(hideFocus='true'); outline:0; }
</style>

<div id="titleBarWrapper" style="max-width:1200px" userdoc="BATCH_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">MGI Batch Query</span>
</div>


<div id="outer" class="bluebar">
	<div id="toggleQF">Search Form</div>
	<div id="qwrap">
		<%@ include file="/WEB-INF/jsp/batch_form.jsp" %>
	</div>
</div>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var qDisplay = false;
</script>
    
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/batch_query.js"></script>

${templateBean.templateBodyStopHtml}