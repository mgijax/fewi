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
  	<c:set var="aeLink" value="${fn:replace(externalUrls.ArrayExpressExperiment, '@@@@', exp.arrayExpressID)}" />
  	<c:set var="geoLink" value="" />
  	<c:if test="${not empty exp.geoID}">
	  	<c:set var="geoLink" value="${fn:replace(externalUrls.GEOSeries, '@@@@', exp.geoID)}" />
  	</c:if>

    <div id="row${status.index}" class="experimentWrapper dataShade1}">
	  <div id="row${status.index}titleWrapper" class="idWrapper headerShade2">
	  	<div id="row${status.index}titleLabel" class="titleLabel">Title</div>
		<div id="row${status.index}title" class="title">
	      	<c:if test="${highlightTitle}"><fewi:highlight value='${exp.title}' searchString="${textSearch}" highlightClass="yellow"/></c:if>
	      	<c:if test="${empty highlightTitle}">${exp.title}</c:if>
		</div>
	  </div>
	  
	  <div id="row${status.index}detailWrapper" class="detailWrapper">
	    <div id="row${status.index}detailLabel" class="detailTitle headerShade3">Details</div>
	    <div id="row${status.index}samplesWrapper" class="detailCell">
	      <div id="row${status.index}samplesHeader" class="detailHeading headerShade1">Samples</div>
	      <div id="row${status.index}samples" class="samples">
	  	  	<a id="row${status.index}sampleCount" onClick="gs_samplePopup('${exp.arrayExpressID}')">${exp.sampleCount}</a> samples&nbsp;&nbsp;
	  	  	<a id="row${status.index}button" class="filterButton" onClick="gs_samplePopup('${exp.arrayExpressID}')">View</a>
	  	  	<c:if test="${highlightSamples}"><br/>
	  	  		${exp.matchingSampleCount} match the search criteria
	  	  	</c:if>
	  	  </div>
	    </div>
	    <div id="row${status.index}variablesWrapper" class="detailCell">
	      <div id="row${status.index}variablesHeader" class="detailHeading headerShade1">Experimental variables</div>
	      <div id="row${status.index}variables" class="variables">
	  	  	<ul class="variables">
	  	  	<c:forEach var="ev" items="${exp.experimentalVariables}">
	  	  	  <li>${ev}</li>
	  	  	</c:forEach>
	  	  	</ul>
	  	  </div>
	    </div>
	    <div id="row${status.index}typeWrapper" class="detailCell">
	      <div id="row${status.index}typeHeader" class="detailHeading headerShade1">Study type</div>
	      <div id="row${status.index}type" class="type">${exp.studyType}</div>
	    </div>
	    <div id="row${status.index}methodWrapper" class="detailCell">
	      <div id="row${status.index}methodHeader" class="detailHeading headerShade1">Method</div>
	      <div id="row${status.index}method" class="method">${exp.method}
	      </div>
	    </div>
	    <div id="row${status.index}pmWrapper" class="detailCell">
	      <div id="row${status.index}pmHeader" class="detailHeading headerShade1">Reference</div>
		  <div id="row${status.index}pmIDs" class="pmIDs">
		  	<c:if test="${not empty exp.pubmedIDs}">
		  		PubMed:
		  		<c:forEach var="pmID" items="${exp.pubmedIDs}" varStatus="pmStatus">
					<% pmID = (String) pageContext.getAttribute("pmID"); %>
		  			<%= idLinker.getLink("PubMed", pmID) %><c:if test="${!pmStatus.last}">, </c:if>
		  		</c:forEach>
		  	</c:if>
		  </div>
	    </div>
	    <div id="row${status.index}spacer" class="spacer">&nbsp;</div>
	    <div id="row${status.index}linkWrapper" class="detailCellLast">
	      <div id="row${status.index}linkHeader" class="detailHeading headerShade1">View experiment at</div>
		  <div id="row${status.index}ids" class="ids">ArrayExpress: <a href="${aeLink}" target="_blank" class="extUrl">${exp.arrayExpressID}</a> 
			<c:if test="${not empty geoLink}"><br/>GEO: <a href="${geoLink}" target="_blank" class="extUrl">${exp.geoID}</a> </c:if>
		  </div>
	    </div>
	  </div>
	  <c:if test="${not empty exp.description}">
	  <div id="row${status.index}descriptionWrapper" class="descriptionWrapper headerShade1">
	    <div id="row${status.index}descriptionTitle" class="descriptionTitle headerShade2">Description</div>
	    <div id="row${status.index}description" class="description">
	    	<c:if test="${highlightDescription}"><fewi:highlight value='${exp.linkedDescription}' searchString="${textSearch}" highlightClass="yellow"/></c:if>
	    	<c:if test="${empty highlightDescription}">${exp.linkedDescription}</c:if>
	    </div>
	  </div>
	  </c:if>
	  <c:if test="${not empty exp.note}">
	  <div id="row${status.index}noteWrapper" class="noteWrapper detailShade1">
	    <div id="row${status.index}noteTitle" class="noteTitle headerShade3">Note</div>
	    <div id="row${status.index}note" class="note">${exp.note}</div>
	  </div>
	  </c:if>
    </div> 
  </c:forEach>
</div>