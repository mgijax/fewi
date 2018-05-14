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
			<th>Official Strain Name</th>
			<th>Synonyms</th>
			<th>Attributes</th>
			<th>IDs / Links</th>
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
				<td><%
					/* dropping into scriptlet for more flexibility than JSTL in this section...
					 * (needed to be able to look up URLs from externaUrls based on the data,
					 * rather than a static string)
					 */
					Properties externalUrlsProperties = (Properties) request.getAttribute("externalUrls");
					Properties configBean = (Properties) request.getAttribute("configBean");
					SimpleStrain strain = (SimpleStrain) pageContext.getAttribute("strain");
					
					for (org.jax.mgi.shr.jsonmodel.AccessionID accID : strain.getAccessionIDs()) {
						String ldb = ((String) accID.getLogicalDB()).replaceAll(" ", "_").replaceAll("-", "_");
						if ("MGI".equals(accID.getLogicalDB())) {
							%><a href="<%= configBean.get("FEWI_URL") %>strain/<%= accID.getAccID() %>" target='_blank'><%= accID.getAccID() %></a><br/><%
						} else if (externalUrlsProperties.get(ldb) != null) {
							String withPrefix = accID.getAccID();
							try {
								// If we can convert the ID to an integer, we need to add a prefix.
								int integerPortion = Integer.parseInt(withPrefix);
								if ("JAX Registry".equals(accID.getLogicalDB())) {
									withPrefix = "JAX:" + withPrefix;
								} else {
									withPrefix = accID.getLogicalDB() + ":" + withPrefix;
								}
							} catch (Exception e) {}
							%><a href="<%= ((String) externalUrlsProperties.get(ldb)).replace("@@@@", accID.getAccID()) %>" target='_blank'><%= withPrefix %></a><br/><%
						} else {
							%><%= accID.getAccID() %> (<%= ldb %>)<br/><%
						}
					}
					%>
				</td>
				<td><c:choose>
						<c:when test="${strain.referenceCount > 0}">
							<a href="${configBean.FEWI_URL}reference/strain/${strain.primaryID}?typeFilter=Literature" target="_blank">${strain.referenceCount}</a>
						</c:when>
						<c:when test="${strain.referenceCount == 0}">${strain.referenceCount}</c:when>
					</c:choose>
				</td>
			</tr>
		</c:forEach>
	</table>
</div>