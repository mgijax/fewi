<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.link.ProviderLinker" %>
<%@ page import = "java.util.List" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%  // Pull detail object into servlet scope
    // EXAMPLE - Marker foo = (Marker)request.getAttribute("foo");

    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailListCat1","detailCat2");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailListBg1","detailListBg2");
    VocabTerm term = (VocabTerm) request.getAttribute("term");
%>
      <input type="hidden" id="pageTitle" value="${title}"/>
      <table style="width:75%" id="termPaneDetails">
        <tr style="width:100%">
         <th class="rightBorderThinGray padded label top">Term: </th>
	     <td class="padded top" style="width:*"><span class="highlight"><b>${term.term}</b></span></td>
	     <td rowspan="2" class="padded top" style="width=30%; vertical-align: bottom">
	       <div style="float:right">
	         <span style="font-size: 80%; font-style: italic;">${dropdownMsg}<br></span>
	         ${dropdown}
	       </div>
	     </td>
	   </tr>
       <tr><th class="rightBorderThinGray padded label top">Present at: </th>
	     <td class="padded top">
		    <c:if test="${term.isEmapa}">
		    Theiler Stage<c:if test="${term.emapInfo.startStage != term.emapInfo.endStage}">s ${term.emapInfo.startStage}-${term.emapInfo.endStage}</c:if>
		    <c:if test="${term.emapInfo.startStage == term.emapInfo.endStage}">${term.emapInfo.startStage}</c:if>
		    </c:if>
		    <c:if test="${term.isEmaps}">Theiler Stage ${term.emapInfo.stage}</c:if>
		  </td>
		</tr>
        <tr><th class="rightBorderThinGray padded label top">ID: </th>
	      <td class="padded top">${term.primaryId}</td>
	    </tr>
		<c:if test="${not empty term.synonyms}">
	        <tr><th class="rightBorderThinGray padded label top">Synonyms: </th>
			  <td class="padded top">
			    <c:forEach var="synonym" items="${term.synonyms}" varStatus="status">
			      ${synonym.synonym}<c:if test="${!status.last}">, </c:if>
			    </c:forEach>
			  </td>
			</tr>
	    </c:if>
	  <c:if test="${not empty term.definition}">
        <tr><th class="rightBorderThinGray padded label top">Definition: </th>
	        <td class="padded top">${term.definition}</td>
	    </tr>
	  </c:if>
	  <c:if test="${not empty term.emapParentEdges}">
        <tr><th class="rightBorderThinGray padded label top">Parent Terms: </th>
		  <td class="padded top">
		    <c:forEach var="parentEdge" items="${term.emapParentEdges}" varStatus="status">
		    <i>${fn:replace(parentEdge.edgeLabel, "_", "-")}</i>
		    <a href="${configBean.FEWI_URL}vocab/gxd/anatomy/${parentEdge.parent.primaryId}" onClick="resetPanes('${parentEdge.parent.primaryId}', true); return false">${parentEdge.parent.term}</a><br/>
		    </c:forEach>
		  </td>
		</tr>
	  </c:if>
	  <c:if test="${term.isEmapa and (not empty hasPhenotypeAssociations)}">
	    <tr><th class="padded label top"></th>
	    	<td class="padded top"></td>
	   	</tr>
        <tr><th class="rightBorderThinGray padded label top">Phenotype: </th>
	        <td class="padded top" colspan="2"><a href="${configBean.FEWI_URL}vocab/mp_ontology/by_anatomy/${term.primaryId}">phenotype terms</a> associated with this structure (with links to phenotype data)
	        </td>
	    </tr>
	  </c:if>
      </table>
