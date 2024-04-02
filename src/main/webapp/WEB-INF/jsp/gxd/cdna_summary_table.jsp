<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.shr.jsonmodel.Clone" %>
<%@ page import = "org.jax.mgi.shr.jsonmodel.CloneMarker" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%
	StyleAlternator headingClass = new StyleAlternator("headerShade2", "headerShade1");
	request.setAttribute("headingClass", headingClass);
%>
<%@ page import = "java.util.List" %>

<%@ page trimDirectiveWhitespaces="true" %>
<fewi:count count="${count}" /> <fewi:count count="${totalCount}" />
<div id="injectedResults">
	<table id="cdnaSummaryTable">
		<tr>
			<th>Clone</th>
			<th>Clone Collection</th>
			<th>Source Tissue</th>
			<th>Source Age</th>
			<th>Source Cell Line</th>
			<th>Gene</th>
		</tr>
		<c:forEach var="clone" items="${clones}" varStatus="status">
			<tr>
				<td><a href="${configBean.FEWI_URL}probe/${clone.primaryID}" target="_blank"><fewi:super value="${clone.name}"/></a>
					<span class="smaller">(${clone.primaryID})</span>
				</td>
				<td><c:forEach var="collection" items="${clone.collections}" varStatus="cStatus">
						${collection}<br/>
					</c:forEach>
				</td>
				<td><fewi:suppressNA value="${clone.tissue}"/></td>
				<td><fewi:suppressNA value="${clone.age}"/></td>
				<td><fewi:suppressNA value="${clone.cellLine}"/></td>
				<td><c:forEach var="marker" items="${clone.markers}" varStatus="mStatus">
					<a href="${configBean.FEWI_URL}marker/${marker.primaryID}" target="_blank">${marker.symbol}</a><c:if
						test="${marker.putative}"> (PUTATIVE)</c:if><c:if test="${!mStatus.last}">, </c:if>
					</c:forEach>
				</td>
			</tr>
		</c:forEach>
	</table>
</div>
