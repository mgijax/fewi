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
	<table id="antibodySummaryTable">
		<tr>
			<th>Antibody</th>
			<th>Gene</th>
			<th>Type</th>
			<th>Host</th>
			<th>References</th>
		</tr>
		<c:forEach var="antibody" items="${antibodies}" varStatus="status">
			<tr>
				<td><a href="${configBean.FEWI_URL}antibody/${antibody.primaryID}" target="_blank"><fewi:super value="${antibody.name}"/></a>
					<span class="smaller">(${antibody.primaryID})</span>
				</td>
				<td><c:forEach var="gene" items="${antibody.markers}" varStatus="cStatus">
						${gene}<br/>
					</c:forEach>
				</td>
				<td>${antibody.type}</td>
				<td>${antibody.species}</td>
				<td>${antibody.referenceCount}</td>
			</tr>
		</c:forEach>
	</table>
</div>
