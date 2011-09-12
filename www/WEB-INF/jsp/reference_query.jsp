<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

${templateBean.templateHeadHtml}

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>References</title>

${templateBean.templateBodyStartHtml}

<!-- begin header bar -->
<div id="titleBarWrapper" userdoc="reference_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">References</span>
</div>
<!-- end header bar -->

<div id="outer"  class="bluebar">
	<div id="toggleQF">Search Form</div>
	<div id="qwrap">
		<%@ include file="/WEB-INF/jsp/reference_form.jsp" %>
	</div>
</div>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var qDisplay = false;
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/reference_query.js"></script>


${templateBean.templateBodyStopHtml}