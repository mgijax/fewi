<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.searchUtil.entities.GxdHtExperiment" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.link.IDLinker" %>
<%
	StyleAlternator headingClass = new StyleAlternator("headerShade2", "headerShade1");
	request.setAttribute("headingClass", headingClass);
    IDLinker idLinker = (IDLinker) request.getAttribute("idLinker");
    String pmID = null;
%>
<%@ page import = "java.util.List" %>

<%@ page trimDirectiveWhitespaces="true" %>
<fewi:count count="${count}" /> <fewi:count count="${totalCount}" />
<div id="injectedResults">
  <c:forEach var="exp" items="${experiments}" varStatus="status">
        <c:set var="expID" value="${exp.arrayExpressID}" />
        <c:if test="${empty expID}">
            <c:set var="expID" value="${exp.geoID}" />
        </c:if>
        <c:set var="aeLink" value="" />
        <c:if test="${not empty exp.arrayExpressID}">
            <c:set var="aeLink" value="${fn:replace(externalUrls.ArrayExpressExperiment, '@@@@', exp.arrayExpressID)}" />
        </c:if>
        <c:set var="geoLink" value="" />
        <c:if test="${not empty exp.geoID}">
                <c:set var="geoLink" value="${fn:replace(externalUrls.GEOSeries, '@@@@', exp.geoID)}" />
        </c:if>
  	<c:set var="atlasLink" value="" />
  	<c:if test="${exp.isInAtlas == 1}">
	  	<c:set var="atlasLink" value="${fn:replace(externalUrls.ExpressionAtlas, '@@@@', exp.arrayExpressID)}" />
  	</c:if>
	<c:set var="gxdLink" value="" />
	<c:if test="${exp.isLoaded == 1}">
		<c:set var="gxdLink" value="${configBean.FEWI_URL}gxd/experiment/${exp.arrayExpressID}" />
	</c:if>

	<table id="experimentTable${status.index}" class="experimentWrapper">
		<tr id="titleRow${status.index}" class="titleRow">
			<td id="titleLabel${status.index}" class="titleLabel">Title</td>
			<td id="title${status.index}" class="title" colspan="6">
		      	<c:if test="${highlightTitle}"><fewi:highlight value='${exp.title}' searchString="${textSearch}" highlightClass="yellow"/></c:if>
		      	<c:if test="${empty highlightTitle}">${exp.title}</c:if>
			</td>
		</tr>

		<tr id="detailLabelRow${status.index}" class="detailLabelRow">
			<td id="detailLabel${status.index}" rowspan="2" class="detailTitle">Details</td>
			<td id="sampleLabel${status.index}">Samples</td>
			<td id="variableLabel${status.index}">Experimental variables</td>
			<td id="studyTypeLabel${status.index}">Study type</td>
			<td id="methodLabel${status.index}">Method</td>
			<td id="pubMedLabel${status.index}">PubMed ID</td>
			<td id="viewLabel${status.index}">View experiment at</td>
		</tr>

		<tr id="detailDataRow${status.index}" class="detailDataRow">
			<td id="sampleData${status.index}">
		  	  	<a id="row${status.index}sampleCount" onClick="gs_samplePopup('${expID}')">${exp.sampleCount}</a> samples&nbsp;&nbsp;
		  	  	<a id="row${status.index}button" class="filterButton" onClick="gs_samplePopup('${expID}')">View</a>
		  	  	<c:if test="${highlightSamples}"><br/>
		  	  		${exp.matchingSampleCount} match the search criteria
		  	  	</c:if>
	  	  	</td>
			<td id="variableData${status.index}">
		  	  	<ul class="variables">
	  		  	<c:forEach var="ev" items="${exp.filteredExperimentalVariables}">
	  	  		  <li>${ev}</li>
	  	  		</c:forEach>
	  	  		</ul>
	  	  	</td>
			<td id="studyTypeData${status.index}">${exp.studyType}</td>
			<td id="methodData${status.index}">
			    <c:forEach var="rst" items="${exp.methods}">
				${rst}<br/>
			    </c:forEach>
			</td>
			<td id="pubMedData${status.index}">
			  	<c:if test="${not empty exp.pubmedIDs}">
			  		<c:forEach var="pmID" items="${exp.pubmedIDs}" varStatus="pmStatus">
						<% pmID = (String) pageContext.getAttribute("pmID"); %>
		  				<%= idLinker.getLink("PubMed", pmID, pmID, "extUrl") %><c:if test="${!pmStatus.last}">, </c:if>
		  			</c:forEach>
		  		</c:if>
		  	</td>
			<td id="viewData${status.index}">
                            <table class="id-table">
                            <tbody>
				<c:if test="${not empty gxdLink}"><tr><td>GXD:</td><td><a href="${gxdLink}">${exp.arrayExpressID}</a></td></tr></c:if>
				<c:if test="${not empty atlasLink}"><tr><td>Expression Atlas:</td><td><a href="${atlasLink}" target="_blank" class="extUrl">${exp.arrayExpressID}</a> </td></tr></c:if>
				<c:if test="${not empty aeLink}"><tr><td>ArrayExpress:</td><td><a href="${aeLink}" target="_blank" class="extUrl">${exp.arrayExpressID}</a></td></tr> </c:if>
				<c:if test="${not empty geoLink}"><tr><td>GEO:</td><td><a href="${geoLink}" target="_blank" class="extUrl">${exp.geoID}</a></td></tr> </c:if>
                            </tbody>
                            </table>
			</td>
		</tr>

	  <c:if test="${not empty exp.description}">
		<tr id="descriptionRow${status.index}" class="descriptionRow">
			<td id="descriptionLabel${status.index}" class="descriptionTitle">Description</td>
			<td id="description${status.index}" class="description" colspan="6">
		    	<c:if test="${highlightDescription}"><fewi:highlight value='${exp.linkedDescription}' searchString="${textSearch}" highlightClass="yellow"/></c:if>
		    	<c:if test="${empty highlightDescription}">${exp.linkedDescription}</c:if>
			</td>
		</tr>
	  </c:if>

	  <c:if test="${not empty exp.note}">
		<tr id="noteRow${status.index}" class="noteRow">
			<td id="noteLabel${status.index}" class="noteTitle">Note</td>
			<td id="note${status.index}" class="note" colspan="6">
				${exp.note}
			</td>
		</tr>
	  </c:if>
	</table>
  </c:forEach>
</div>
