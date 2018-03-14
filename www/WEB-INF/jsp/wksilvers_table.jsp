<%-- 
	Renders table of data for markers cited in W.K. Silvers' book and the associated MGI markers.
	Assumes 'markers' is a List<WKSilversMarker>.
--%>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<style>
.rosetta {
	border-collapse: collapse;
	width: 90%;
}
.rosetta tr th {
	background-color: #D0E0F0;
	font-family: Arial, Helvetica;
	font-size: medium;
	font-weight: normal; 
	border: 1px solid gray;
	padding: 3px;
}
.rosetta tr td { 
	border: 1px solid gray;
	padding: 3px;
}
tbody tr:nth-child(odd) {
	background-color: #dddddd;
}
</style>
<table class="rosetta">
	<tr>
		<th>Gene Symbols used in<br/><i>The Coat Colors of Mice</i></th> 
		<th colspan="2">Official Gene Nomenclature<br/>from Mouse Genome Informatics</th>
	</tr>
	<tr>
		<td>Symbol in W.K. Silvers</td>
		<td>Current Gene Symbol</td>
		<td>Phenotypic Alleles</td>
	</tr>
	<c:forEach var="marker" items="${markers}">
	  <tr>
	  	<td><a href="${configBean.WKSILVERS_URL}${marker.silversUrlFragment}"><fewi:super value="${marker.silversSymbol}"/></a></td>
	  	<td><a href="${configBean.FEWI_URL}marker/${marker.markerID}" target="_blank"><fewi:super value="${marker.markerSymbol}"/></a></td>
	  	<td><a href="${configBean.FEWI_URL}allele/summary?markerId=${marker.markerID}" target="_blank"><fewi:super value="${marker.markerSymbol}"/> allele(s)</a></td>
	  </tr>
	</c:forEach>
</table>