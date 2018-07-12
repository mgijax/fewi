<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "mgi.frontend.datamodel.Genotype" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<fewi:simpleseo
   title="Phenotypes associated with ${plainPairs}"
   description="View ${plainPairs} either: ${strains}: phenotypes, images, diseases, and references."
   keywords="${plainPairs}, either ${strains}, mouse, mice, murine, Mus"
/>

<%@ include file="/WEB-INF/jsp/google_analytics_pageview.jsp" %>

<%@ include file="/WEB-INF/jsp/phenotype_table_geno_popup_imports.jsp" %>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<div id="titleBarWrapper" style="max-width: none;">
   <div name="centeredTitle">
      <span class="titleBarMainTitle">Phenotypes associated with ${superscriptPairs}</span>
   </div>
</div>
	
<style>
.allBorders { border: 1px solid black }
.left { text-align: left }
.white { background-color: white }
.highlight { background-color: yellow }
</style>

<!-- summary table at page top -->
<table class="summaryHeader">
<tr>
  <td class="detailCat2">
    <span class="label">Summary</span>
  </td> 
  <td class="detailData2">
    ${fn:length(genoCluster.genotypes)} genotype<c:if test="${fn:length(genoCluster.genotypes) > 1}">s</c:if><br/>
    <div style="max-height: 125px; overflow-y: scroll; overflow-x: hidden; margin-top: 5px; margin-left: 15px; max-width: 95%" id="modelSummaryDiv">
    <table id="modelSummaryTable" style="width: 100%">
      <tr>
		<td class="allBorders label left white">Jump to</td>
		<td class="allBorders label left white">Allelic Composition</td>
		<td class="allBorders label left white">Genetic Background</td>
		<td class="allBorders label left white">Genotype ID</td>
      </tr>
      <c:set var="counter" value="0"/>
      <c:forEach var="genotype" items="${genoCluster.genotypes}">
		<c:set var="counter" value="${counter + 1}"/>
        <tr>
		  <td class="allBorders white">
	    	<a href="#${genotype.primaryID}" style="text-decoration: none">
	      	<c:if test="${not empty counter and (counter > 0)}">
	      	<div class="${genotype.genotypeType}Geno genotypeType" style="margin-left: 7px; text-align: center; padding-bottom: 8px; <c:if test='${counter > 99}'>padding-right: 3px;</c:if>">${genotype.genotypeType}${counter}</div>
	      	</c:if>
	   		 </a>
	  	  </td>
	 	  <td class="allBorders white"><span class="genotypeCombo"><fewi:genotype value="${genotype}" newWindow="${true}"/></span></td>
	  	  <td class="allBorders white">
	  	  	<c:choose>
	  	  	<c:when test="${not empty genotype.strainID}">
	  	  		<a href="${configBean.FEWI_URL}strain/${genotype.strainID}" class="MP" target="_blank"><fewi:super value="${genotype.backgroundStrain}"/></a>
	  	  	</c:when>
	  	  	<c:otherwise>
	  	  		<fewi:super value="${genotype.backgroundStrain}"/>
	  	  	</c:otherwise>
	  	  	</c:choose>
	  	  </td>
	  	  <td class="allBorders white">${genotype.primaryID}</td>
		</tr>
      </c:forEach>
    </table>
    </div>
    <c:if test="${not empty genoCluster.nonTransgeneMarker}">
    	<br/>
    	<span class='label'>Comparison Matrix</span> 
    	<a href="${configBean.FEWI_URL}gxd/phenogrid/${genoCluster.nonTransgeneMarker.primaryID}?genoclusterKey=${genoCluster.genoClusterKey}" class="MP">Gene Expression + Phenotype</a>
    </c:if>
    <c:if test="${not empty structureTerm}">
    	<br/>
		<span class='highlight'>Highlighted</span> phenotype terms are associated with the anatomy term
			<span class="label"><a href="${configBean.FEWI_URL}vocab/gxd/anatomy/${structureID}" class="MP" target="_blank">${structureTerm}</a></span>
			and/or its substructures.
    </c:if>
  </td>
</tr>
</table>
<br/><br/>

<c:set var="counter" value="0"/>
<c:forEach var="genotype" items="${genoCluster.genotypes}" varStatus="genoStatus">

<div style="overflow:hidden;">
	<!-- Set all the values that the genoview jsp expects -->
	<c:set var="genotype" value="${genotype}" scope="request"/>
	<c:set var="counter" value="${counter + 1}"/>
	<c:set var="mpSystems" value="${genotype.MPSystems }"  scope="request"/>
	<c:set var="hasImage" value="${genotype.hasPrimaryImage }"  scope="request"/>
	<c:set var="hasDiseaseModels" value="${not empty genotype.diseases }"  scope="request"/>
	<% Genotype genotype = (Genotype)request.getAttribute("genotype"); 
	  NotesTagConverter ntc = new NotesTagConverter();
	  StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
	  StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2");
	%>
	<%@ include file="../phenotype_table_geno_popup_header.jsp" %>

	<!-- Header -->
	<%@ include file="/WEB-INF/jsp/phenotype_table_geno_popup_content.jsp" %>
	<div style="clear: both;"></div>
	</div>
    <br/>
</c:forEach>
<c:if test="${not empty structureID}">
	<script>
	// if we were given a structureID parameter, we need to find the MP IDs to highlight and do so
	$.get('${configBean.FEWI_URL}gxd/phenogrid/annotated_pheno_ids',
		'genoclusterKey=${genoClusterKey}&structureID=${structureID}',
		function(data) {
			var mpIDs = data.replace(/:/g,'').split(',');
			mpIDs.forEach(function(s) {
				$('.' + s).addClass('highlight');
			});
		});
	</script>
</c:if>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
