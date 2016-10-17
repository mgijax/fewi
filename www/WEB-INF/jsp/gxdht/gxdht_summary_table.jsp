<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.searchUtil.entities.GxdHtExperiment" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "java.util.List" %>

<%@ page trimDirectiveWhitespaces="true" %>
<fewi:count count="${count}" />
<div id="injectedResults">
  <c:forEach var="exp" items="${experiments}" varStatus="status">
    <div id="row${status.index}" style="border-bottom: thin solid black">
	  <div id="row${status.index}ids">
		ArrayExpress: ${exp.arrayExpressID}<br/>
		Geo: ${exp.geoID}<br/>
		Title: ${exp.title}
	  </div>
	  <div id="row${status.index}header">
	  	Experiment Information
	  </div>
	  <div id="row${status.index}samples">
	  	${exp.sampleCount} samples<br/>
	  	${exp.matchingSampleCount} match the search criteria
	  </div>
	  <div id="row${status.index}variables">
	  	<ul>
	  	<c:forEach var="ev" items="${exp.experimentalVariables}">
	  	  <li>${ev}</li>
	  	</c:forEach>
	  	</ul>
	  </div>
	  <div id="row${status.index}type">
	  	Study Type: ${exp.studyType}
	  </div>
	  <div id="row${status.index}method">
	  	Method: ${exp.method}
	  </div>
	  <div id="row${status.index}description">
	  	Description: ${exp.description}
	  </div>
    </div> 
  </c:forEach>
</div>