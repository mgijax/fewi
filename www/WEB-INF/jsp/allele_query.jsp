<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

${templateBean.templateHeadHtml}

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>Phenotypes, Alleles & Disease Models Query Form</title>

${templateBean.templateBodyStartHtml}

<!-- header bar -->
<div id="titleBarWrapper" userdoc="ALLELE_help.shtml">
  <span class="titleBarMainTitle">Phenotypes, Alleles & Disease Models Query Form</span>
</div>
<c:set var="helpPage" value="${configBean.USERHELP_URL}ALLELE_help.shtml"/>

<div id="outer" >
	<div id="toggleQF"><span id="toggleLink" class="filterButton">Click to hide search</span></div>
	<div id="qwrap">
		<%@ include file="/WEB-INF/jsp/allele_form.jsp" %>
	</div>
</div>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var qDisplay = false;
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/allele_query.js"></script>


${templateBean.templateBodyStopHtml}