<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<fewi:simpleseo
	title="Search Mouse SNPs from dbSNP"
	description="${seoDescription}"
	keywords="${seoKeywords}"
/>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- begin header bar -->
<div id="titleBarWrapper" userdoc="SNP_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">Search Mouse SNPs from dbSNP</span>
</div>
<!-- end header bar -->

<div id="outer">
	<span id="toggleImg" class="qfCollapse"></span>
	<c:if test="${not empty querystring}">
	<div id="toggleQF"><span id="toggleLink" class="filterButton">Click to hide search</span></div>
	<br>
	</c:if>
	<div id="qwrap">
		<%@ include file="/WEB-INF/jsp/snp/form.jsp" %>
	</div>
</div>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/snp_query.js"></script>

<script type="text/javascript">
	snpqry.setQueryFormDisplay(true);
	snpqry.setQueryFormHeight();
</script>


<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
