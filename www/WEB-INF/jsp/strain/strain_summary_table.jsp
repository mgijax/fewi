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
	<table id="strainSummaryTable">
		<tr>
			<th>Official Strain Name</th>
			<th>Synonyms</th>
			<th>Attributes</th>
			<th>IDs / Links</th>
			<th>References</th>
		</tr>
		<c:forEach var="strain" items="${strains}" varStatus="status">
			<tr>
				<td><a href="${configBean.FEWI_URL}strain/${strain.primaryID}" target="_blank"><fewi:super value="${strain.name}"/></a>
					<span class="smaller">(${strain.primaryID})</span>
				</td>
				<td><c:forEach var="synonym" items="${strain.synonyms}" varStatus="cStatus">
						<fewi:super value="${synonym}"/><br/>
					</c:forEach>
				</td>
				<td><c:forEach var="attribute" items="${strain.attributes}" varStatus="cStatus">
						${attribute}<br/>
					</c:forEach>
				</td>
				<td><c:forEach var="accID" items="${strain.accessionIDs}" varStatus="mStatus">
				    	${accID.accID} (${accID.logicalDB})<br/>
					</c:forEach>
				</td>
				<td>${strain.referenceCount}
				</td>
			</tr>
		</c:forEach>
	</table>
</div>