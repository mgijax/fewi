<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.link.ProviderLinker" %>
<%@ page import = "java.util.List" %>
<%@ page import = "org.jax.mgi.fewi.searchUtil.entities.SolrAnatomyTerm" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" 
    pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<!-- We want to hide the search term for links coming in via MP ID or (allele) MGI ID. -->
<c:set var="searchFieldValue" value="${searchTerm}" />
<c:set var="bestMatch" value=", sorted by best match" />
<c:if test="${fn:startsWith(searchTerm, 'MP:') or fn:startsWith(searchTerm, 'MGI:')}">
	<c:set var="searchFieldValue" value="" />
	<c:set var="bestMatch" value="" />
</c:if>

<div style="padding-bottom: 8px;">
<form id="vocabBrowserSearchForm" name="vocabBrowserSearchForm" onSubmit="refreshSearchPane(); return false;">
	<input type="text" size="35" id="searchTerm" name="searchTerm" value="${e:forHtml(searchFieldValue)}" style="width: auto; position: relative;">
    <div id="structureContainer" style="width: 250px; text-align: left; display: inline;"></div>
    <input type="button" value="Clear" name="Clear" onClick="resetSearch()">
</form>
</div>

<div style="padding-bottom: 8px;">
<c:if test="${(empty results) and (not empty searchTerm)}">
no matching terms
</c:if>
<c:if test="${not empty results}">
${resultCount} term<c:if test="${fn:length(results) > 1}">s</c:if>${bestMatch}
</c:if>
</div>

<div id="searchResults" style="text-align: left; padding-left: 2px; padding-right: 2px">
<c:forEach var="result" items="${results}">
<div style="padding-bottom: 8px">
<a href="${configBean.FEWI_URL}vocab/gxd/anatomy/${result.accID}" onClick="resetPanes('${result.accID}', true); return false;">${result.highlightedStructure}</a>
<c:if test="${(searchFieldValue != '') and (not result.matchedStructure) and (result.highlightedSynonym != '')}">(${result.highlightedSynonym})</c:if>
<span class="small">${result.stageRange}</span>
</div>
</c:forEach>
</div>
