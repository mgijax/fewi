<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.searchUtil.entities.GxdHtExperiment" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%
	StyleAlternator headingClass = new StyleAlternator("headerShade2", "headerShade1");
	StyleAlternator dataClass = new StyleAlternator("dataShade1", "dataShade1");		// all white for now
	request.setAttribute("headingClass", headingClass);
	request.setAttribute("dataClass", dataClass);
%>
<%@ page import = "java.util.List" %>

<%@ page trimDirectiveWhitespaces="true" %>
<fewi:count count="${count}" /> <fewi:count count="${totalCount}" />
<div id="injectedResults">
  <c:forEach var="exp" items="${experiments}" varStatus="status">
  	<c:set var="aeLink" value="${fn:replace(externalUrls.ArrayExpressExperiment, '@@@@', exp.arrayExpressID)}" />
  	<c:set var="geoLink" value="" />
  	<c:if test="${not empty exp.geoID}">
	  	<c:set var="geoLink" value="${fn:replace(externalUrls.GEOSeries, '@@@@', exp.geoID)}" />
  	</c:if>

    <div id="row${status.index}" class="experimentWrapper ${dataClass.next}">
	  <div id="row${status.index}idWrapper" class="idWrapper ${headingClass.next}">
		<div id="row${status.index}idLabels" class="idLabels">ArrayExpress:<c:if test="${not empty geoLink}"><br/>GEO:</c:if></div>
		<div id="row${status.index}ids" class="ids"><a href="${aeLink}" target="_blank">${exp.arrayExpressID}</a>
			<c:if test="${not empty geoLink}"><br/><a href="${geoLink}" target="_blank">${exp.geoID}</a></c:if></div>
		<div id="row${status.index}title" class="title">${exp.title}</div>
	  </div>
	  <div id="row${status.index}detailWrapper" class="detailWrapper">
	    <div id="row${status.index}samplesWrapper" class="detailCell">
	      <div id="row${status.index}samplesHeader" class="detailHeading ${headingClass.next}">Samples</div>
	      <div id="row${status.index}samples" class="samples">
	  	  	<a id="row${status.index}sampleCount" onClick="gs_samplePopup('${exp.arrayExpressID}')">${exp.sampleCount}</a> samples&nbsp;&nbsp;
	  	  	<a id="row${status.index}button" class="filterButton" onClick="gs_samplePopup('${exp.arrayExpressID}')">View</a>
	  	  	<br/>
	  	  	${exp.matchingSampleCount} match the search criteria
	  	  </div>
	    </div>
	    <div id="row${status.index}variablesWrapper" class="detailCell">
	      <div id="row${status.index}variablesHeader" class="detailHeading ${headingClass.current}">Experimental variables</div>
	      <div id="row${status.index}variables" class="variables">
	  	  	<ul class="variables">
	  	  	<c:forEach var="ev" items="${exp.experimentalVariables}">
	  	  	  <li>${ev}</li>
	  	  	</c:forEach>
	  	  	</ul>
	  	  </div>
	    </div>
	    <div id="row${status.index}typeWrapper" class="detailCell">
	      <div id="row${status.index}typeHeader" class="detailHeading ${headingClass.current}">Study type</div>
	      <div id="row${status.index}type" class="type">${exp.studyType}</div>
	    </div>
	    <div id="row${status.index}spacer" class="spacer">&nbsp;</div>
	    <div id="row${status.index}methodWrapper" class="detailCellLast">
	      <div id="row${status.index}methodHeader" class="detailHeading ${headingClass.current}">Method</div>
	      <div id="row${status.index}method" class="method">${exp.method}
	      	<c:if test="${not empty exp.note}"><p/>Note: ${exp.note}</c:if>
	      </div>
	    </div>
	  </div>
	  <c:if test="${not empty exp.description}">
	  <div id="row${status.index}descriptionWrapper" class="descriptionWrapper ${headingClass.current}">
	      <div id="row${status.index}descriptionTitle" class="descriptionTitle">Description</div>
	      <div id="row${status.index}description" class="description">${exp.description}</div>
	  </div>
	  </c:if>
    </div> 
  </c:forEach>
</div>