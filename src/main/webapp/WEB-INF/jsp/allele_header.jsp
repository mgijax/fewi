<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>

<c:set var="ahCat1" value="summaryHeaderCat1${isGxd}"/>
<c:set var="ahData1" value="summaryHeaderData1"/>

<c:if test="${not empty showGenotypes}">
  <c:set var="ahCat1" value="detailCat1"/>
  <c:set var="ahData1" value="detailData1"/>
  <c:set var="idPrefix" value="Allele "/>
</c:if>

<style>
.allBorders { border: 1px solid black }
.left { text-align: left }
.white { background-color: white }
</style>

<!-- header table -->
<table class="summaryHeader">
<tr >
  <td class="${ahCat1}">
       <span class="label">Allele Symbol</span><br/>
       <span class="label">Allele Name</span><br/>
       <span class="label">${idPrefix}ID</span>
  </td>
  <td class="${ahData1}">
  	<% Allele myAllele = (Allele)request.getAttribute("allele"); %>
    <a href="${configBean.FEWI_URL}allele/${allele.primaryID}" class="symbolLink">
      <%=FormatHelper.superscript(myAllele.getSymbol())%></a><br/>
    <span>${allele.name}</span><br/>
    <span>${allele.primaryID}</span>
  </td>
</tr>

<% // All Genotypes page needs to report on the included genotypes %>
<c:if test="${not empty showGenotypes}">
<tr>
  <td class="detailCat2">
    <span class="label">Summary</span>
  </td> 
  <td class="detailData2">
    ${fn:length(genotypeAssociations)} genotype<c:if test="${fn:length(genotypeAssociations) > 1}">s</c:if><br/>
    <div style="max-height: 125px; overflow-y: scroll; overflow-x: hidden; margin-top: 5px; margin-left: 15px; max-width: 95%" id="modelSummaryDiv">
    <table id="modelSummaryTable" style="width: 100%">
      <tr>
	<td class="allBorders label left white">Jump to</td>
	<td class="allBorders label left white">Allelic Composition</td>
	<td class="allBorders label left white">Genetic Background</td>
	<td class="allBorders label left white">Genotype ID</td>
      </tr>
      <c:forEach var="gtAssociation" items="${allele.phenoTableGenotypeAssociations}">
        <c:set var="genotype" value="${gtAssociation.genotype}" scope="request"/>
	<c:set var="counter" value="${gtAssociation.genotypeSeq}" scope="request"/>

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
	  	<c:if test="${not empty genotype.strainID}">
	  		<a href="${configBean.FEWI_URL}strain/${genotype.strainID}" style="text-decoration:none" target="_blank"><fewi:super value="${genotype.backgroundStrain}"/></a>
  		</c:if>
	  	<c:if test="${empty genotype.strainID}">
	  		<fewi:super value="${genotype.backgroundStrain}"/>
  		</c:if>
	  </td>
	  <td class="allBorders white">${genotype.primaryID}</td>
	</tr>
      </c:forEach>
    </table>
    </div>
  </td>
</tr>
</c:if>
</table>
