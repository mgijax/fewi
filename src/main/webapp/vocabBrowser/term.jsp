<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.link.ProviderLinker" %>
<%@ page import = "java.util.List" %>
<%@ page import = "org.jax.mgi.shr.jsonmodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%  
    StyleAlternator leftTdStyles = new StyleAlternator("detailListCat1","detailCat2");
    StyleAlternator rightTdStyles = new StyleAlternator("detailListBg1","detailListBg2");
    BrowserTerm term = (BrowserTerm) request.getAttribute("term");
%>
      <input type="hidden" id="pageTitle" value="${title}"/>
      <table style="width:95%" id="termPaneDetails">
        <tr style="width:100%">
         <th class="rightBorderThinGray padded label top">Term: </th>
	     <td class="padded top" style="width:*"><span class="highlight"><b>${term.term}</b></span></td>
	    </tr>
		<c:if test="${not empty term.synonyms}">
	        <tr><th class="rightBorderThinGray padded label top">Synonyms: </th>
			  <td class="padded top">
			    <c:forEach var="synonym" items="${term.distinctSynonyms}" varStatus="status">
			      ${synonym}<c:if test="${!status.last}"> | </c:if>
			    </c:forEach>
			  </td>
			</tr>
	    </c:if>
	    <c:if test="${not empty term.definition}">
          <tr><th class="rightBorderThinGray padded label top">Definition: </th>
	          <td class="padded top">${term.definition}</td>
	      </tr>
	    </c:if>
		<c:if test="${not empty term.allParents}">
	        <tr><th class="rightBorderThinGray padded label top">Parent Terms: </th>
			  <td class="padded top">
			    <c:forEach var="parent" items="${term.allParents}" varStatus="status">
			      <c:if test="${not empty parent.edgeType}"><I>${parent.edgeType}</I></c:if>
			      <a href="${browserUrl}${parent.primaryID}" onClick="parentClick('${parent.primaryID}'); return false;">${parent.term}</a><c:if test="${!status.last}"><br/></c:if> 
			    </c:forEach>
			  </td>
			</tr>
	    </c:if>
		<c:if test="${not empty term.comment}">
          <tr><th class="rightBorderThinGray padded label top">Comment: </th>
             <c:set var="termComment" value='${term.comment}'/>
             <c:if test="${fn:startsWith(term.primaryID.accID, 'MP:')}">
               <c:set var="termComment" value="<%= FormatHelper.cleanComment(term.getComment()) %>" />
             </c:if>
	          <td class="padded top">${termComment}</td>
	    </c:if>
		<c:if test="${not empty term.dagName}">
	        <tr><th class="rightBorderThinGray padded label top">Category: </th>
		      <td class="padded top">${term.dagName}</td>
	    	</tr>
	    </c:if>
	    <tr><th class="rightBorderThinGray padded label top">ID: </th>
	      <td class="padded top">${term.primaryID.accID}
	      <c:if test="${fn:startsWith(term.primaryID.accID, 'DOID:')}">
	        <span id="doLink"><a href="${configBean.FEWI_URL}disease/${term.primaryID.accID}">(DO detail page)</a></span>
	      </c:if>
	      </td>
	    </tr>
		<c:if test="${term.relatedToTissues}">
	        <tr><th class="rightBorderThinGray padded label top">Anatomy: </th>
			  <td class="padded top">
			  	<a href="${configBean.FEWI_URL}vocab/gxd/anatomy/by_phenotype/${term.primaryID.accID}">tissues</a> associated with this phenotype (with links to expression data)
			  </td>
			</tr>
		</c:if>
		<c:if test="${not empty term.secondaryIDs}">
	        <tr><th class="rightBorderThinGray padded label top">Other IDs: </th>
			  <td class="padded top">
			    <c:forEach var="id" items="${term.secondaryIDs}" varStatus="status">
			      ${id.accID}<c:if test="${!status.last}">, </c:if>
			    </c:forEach>
			  </td>
			</tr>
		</c:if>
      </table>

<script>
	setBrowserTitle("${title}");
</script>