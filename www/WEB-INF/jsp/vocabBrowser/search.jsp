<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.link.ProviderLinker" %>
<%@ page import = "java.util.List" %>
<%@ page import = "org.jax.mgi.fewi.summary.VocabBrowserSearchResult" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<div style="padding-bottom: 8px;">
<form name="vocabBrowserSearchForm" onSubmit="refreshSearchPane(); return false;">
	<input type="text" size="35" id="searchTerm" name="term" value="${searchTerm}" style="width: auto; position: relative;">
    <div id="termContainer" style="width: 250px; text-align: left; display: inline;"></div>
    <input type="button" value="Clear" name="Clear" onClick="resetSearch()">
</form>
</div>

<div style="padding-bottom: 8px;">
<c:if test="${(empty results) and (not empty searchTerm)}">
no matching terms
</c:if>
<c:if test="${not empty results}">
${resultCount} term<c:if test="${fn:length(results) > 1}">s</c:if>, sorted by best match
</c:if>
</div>

<div id="searchResults" style="text-align: left; padding-left: 2px; padding-right: 2px">
<c:forEach var="result" items="${results}">
<div style="padding-bottom: 8px">
<a href="${browserUrl}${result.accID}" onClick="searchResultClick('${result.accID}'); return false;">${result.highlightedTerm}</a>
<c:if test="${not result.matchedTerm}">(${result.highlightedSynonym})</c:if>
</div>
</c:forEach>
</div>
