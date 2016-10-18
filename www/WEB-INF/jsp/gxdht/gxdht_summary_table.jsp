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
    <div id="row${status.index}" class="experimentWrapper ${dataClass.next}">
	  <div id="row${status.index}ids" class="idWrapper ${headingClass.next}">
		<div id="idLabels" class="idLabels">ArrayExpress:<br/>Geo:</div>
		<div id="ids" class="ids">${exp.arrayExpressID}<br/>${exp.geoID}</div>
		<div id="title" class="title">${exp.title}</div>
	  </div>
	  <div id="row${status.index}header" class="header">
	  	Experiment Information
	  </div>
	  <div id="row${status.index}detailWrapper" class="detailWrapper">
	    <div id="row${status.index}samplesWrapper" class="detailCell">
	      <div id="row${status.index}samplesHeader" class="detailHeading ${headingClass.next}">Samples</div>
	      <div id="row${status.index}samples" class="samples">
	  	  	${exp.sampleCount} samples&nbsp;&nbsp;
	  	  	<a id="row${status.index}button" class="filterButton" href="javascript:alert('Coming in US95...')">View</a>
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
	      <div id="row${status.index}method" class="method">${exp.method}</div>
	    </div>
	  </div>
	  <div id="row${status.index}descriptionWrapper" class="descriptionWrapper ${headingClass.current}">
	      <div id="row${status.index}descriptionTitle" class="descriptionTitle">Description</div>
	      <div id="row${status.index}description" class="description">${exp.description}</div>
	  </div>
    </div> 
  </c:forEach>
</div>