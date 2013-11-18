<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%-- This is merely a page to communicate whether a file upload (via POST) was successful, or if not why. --%>
<c:if test="${not empty success}"><div id="success">${success}</div></c:if>
<c:if test="${not empty error}"><div id="error">${error}<br/>Refer to our <a target="_blank" href="${configBean.USERDOCS_URL}disease_connection_help.shtml">help doc</a> for any questions about file formats.</div></c:if>