<%-- Static home page loader. Wraps a header/footer around pageUrl --%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/home/homepages.css">

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<c:catch var="exception">
	<jsp:include page="/WEB-INF/jsp/static/home/${pageUrl}" flush="true" />
</c:catch>
<c:if test="${exception != null}">
	<p class="error">File not found: ${pageUrl}</p>
</c:if>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
