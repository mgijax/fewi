<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "java.util.Properties" %>
<%@ page import = "org.jax.mgi.shr.jsonmodel.SimpleStrain" %>
<%@ page import = "org.jax.mgi.shr.jsonmodel.AccessionID" %>
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
			<th>Strain/Stock Name</th>
			<th>Synonyms</th>
			<th>Attributes</th>
			<th>IDs</th>
			<th>References</th>
		</tr>
		<c:forEach var="strain" items="${strains}" varStatus="status">
			<tr>
				<td><a href="${configBean.FEWI_URL}strain/${strain.primaryID}" target="_blank"><fewi:super value="${strain.name}"/></a>
				</td>
				<td><c:forEach var="synonym" items="${strain.synonyms}" varStatus="cStatus">
						<fewi:super value="${synonym}"/><br/>
					</c:forEach>
				</td>
				<td><c:forEach var="attribute" items="${strain.attributes}" varStatus="cStatus">
						${attribute}<br/>
					</c:forEach>
				</td>
				<td>
					<c:forEach var="id" items="${strain.accessionIDs}">
						<c:set var="displayID" value="${id.accID}"/>
						<c:catch var="err">
							<fmt:parseNumber value="${displayID}" type="number" integerOnly="true" var="numericID" />
							<c:choose>
								<c:when test="${id.logicalDB == 'JAX Registry'}">
									<c:set var="displayID" value="JAX:${displayID}"/>
								</c:when>
								<c:otherwise>
									<c:set var="displayID" value="${id.logicalDB}:${displayID}"/>
								</c:otherwise>
							</c:choose>
						</c:catch>
						${displayID}<br/>
					</c:forEach>
				</td>
				<td><c:choose>
						<c:when test="${strain.referenceCount > 0}">
							<a href="${configBean.FEWI_URL}reference/strain/${strain.primaryID}?typeFilter=Literature" class="referenceLink" target="_blank">${strain.referenceCount}</a>
						</c:when>
						<c:when test="${strain.referenceCount == 0}">${strain.referenceCount}</c:when>
					</c:choose>
				</td>
			</tr>
		</c:forEach>
	</table>
</div>