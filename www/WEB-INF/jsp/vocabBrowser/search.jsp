<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.link.ProviderLinker" %>
<%@ page import = "java.util.List" %>
<%@ page import = "org.jax.mgi.fewi.summary.VocabBrowserSearchResult" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<div style="padding-bottom: 8px;">
<form name="vocabBrowserSearchForm" onSubmit="refreshSearchPane(); return false;">
	<div id="searchWrapper">
		<div id="searchBoxDiv" style="float: left; padding-left: 10px;">
			<input type="text" size="35" id="searchTerm" name="term" value="${searchTerm}" style="width: auto; position: relative;">
		</div>
		<div id="clearButtonDiv" style="padding-top: 4px">
    		<input type="button" id="clearButton" value="Clear" name="Clear" onClick="resetSearch()">
    	</div>
    </div>
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

<div id="searchResults" style="text-align: left; padding-left: 2px; padding-right: 2px; min-height: 300px;">
<c:forEach var="result" items="${results}">
<div style="padding-bottom: 8px">
<a href="${browserUrl}${result.accID}" onClick="searchResultClick('${result.accID}'); return false;">${result.highlightedTerm}</a>
<c:if test="${not result.matchedTerm}">(${result.highlightedSynonym})</c:if>
</div>
</c:forEach>
</div>

<style>
.easy-autocomplete-container {
	overflow-y: auto;
}
.easy-autocomplete-container ul {
	text-align: left;
	margin-left: -40px;
	width: 325px;
	max-height: 290px;
}

.easy-autocomplete-container ul li {
	font-size: 90%;
	line-height: 0.9em;
}
</style>

<script>
var selectedID = null;
var options = {
	url: function(phrase) {
			return "${autocompleteUrl}" + phrase;
		},
	getValue: "term",
	template: {
		type: 'custom',
		method: function(value, item) {
			return item.autocompleteDisplay;
			}
		},
		list: {
			onSelectItemEvent: function() {
				selectedID = $('#searchTerm').getSelectedItemData().accID;
			},
			onChooseEvent: function() {
				refreshSearchPane();
				if (selectedID != null) {
					searchResultClick(selectedID);
				} else {
					selectedID = $('#searchTerm').getItemData(0).accID;
					searchResultClick(selectedID);
					$('#searchTerm').val($('#searchTerm').getItemData(0).term);
				}
			},
			maxNumberOfElements: 200
		},
		requestDelay: 50,
		listLocation: 'resultObjects'
};
$('#searchTerm').easyAutocomplete(options);
</script>