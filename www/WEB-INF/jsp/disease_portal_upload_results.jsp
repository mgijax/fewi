<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%-- This is merely a page to communicate whether a file upload (via POST) was successful, or if not why. --%>
<c:if test="${not empty success}"><div id="success">${success}</div></c:if>
<c:if test="${not empty error}"><div id="error">${error}</div></c:if>