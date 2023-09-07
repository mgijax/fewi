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
	<table id="probeSummaryTable">
		<tr>
			<th>Probe/Clone</th>
			<th>Clone Collection</th>
			<th>Sequence Type</th>
			<th>Genes</th>
			<th>Chromosome</th>
		</tr>
		<c:forEach var="probe" items="${probes}" varStatus="status">
			<tr>
				<td><a href="${configBean.FEWI_URL}probe/${probe.primaryID}" target="_blank"><fewi:super value="${probe.name}"/></a>
					<span class="smaller">(${probe.primaryID})</span>
				</td>
				<td><c:forEach var="collection" items="${probe.collections}" varStatus="cStatus">
						${collection}<br/>
					</c:forEach>
				</td>
				<td>${probe.segmentType}</td>
				<td><c:forEach var="marker" items="${probe.markers}" varStatus="mStatus">
					<a href="${configBean.FEWI_URL}marker/${marker.primaryID}" target="_blank">${marker.symbol}</a><c:if
						test="${marker.isPutative}"> (PUTATIVE)</c:if><c:if test="${!mStatus.last}"><br/></c:if>
					</c:forEach>
				</td>
				<td><c:forEach var="marker" items="${probe.markers}" varStatus="mStatus">${marker.location}<br/>
					</c:forEach>
				</td>
			</tr>
		</c:forEach>
	</table>
</div>