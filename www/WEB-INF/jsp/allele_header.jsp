<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>

<c:set var="ahCat1" value="summaryHeaderCat1${isGxd}"/>
<c:set var="ahData1" value="summaryHeaderData1"/>

<c:if test="${not empty showGenotypes}">
  <c:set var="ahCat1" value="detailCat1"/>
  <c:set var="ahData1" value="detailData1"/>
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
       <span class="label">ID</span>
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
    ${fn:length(genotypeAssociations)} model<c:if test="${fn:length(genotypeAssociations) > 1}">s</c:if><br/>
    <div style="max-height: 125px; overflow-y: scroll; overflow-x: hidden; margin-top: 5px; margin-left: 15px;" id="modelSummaryDiv">
    <table id="modelSummaryTable">
      <tr>
	<td class="allBorders label left white">Jump to</td>
	<td class="allBorders label left white">Genotype</td>
	<td class="allBorders label left white">Genetic Background</td>
	<td class="allBorders label left white">MGI ID</td>
      </tr>
      <c:forEach var="gtAssociation" items="${allele.phenoTableGenotypeAssociations}">
        <c:set var="genotype" value="${gtAssociation.genotype}" scope="request"/>
	<c:set var="counter" value="${gtAssociation.genotypeSeq}" scope="request"/>

        <tr>
	  <td class="allBorders white">
	    <a href="#${genotype.primaryID}" style="text-decoration: none">
	      <c:if test="${not empty counter and (counter > 0)}">
	        <div class="${genotype.genotypeType}Geno genotypeType" style="margin-left: 7px; text-align: center; padding-bottom: 8px;">${genotype.genotypeType}${counter}</div>
	      </c:if>
	    </a>
	  </td>
	  <td class="allBorders white"><span class="genotypeCombo"><fewi:genotype value="${genotype}" newWindow="${true}"/></span></td>
	  <td class="allBorders white"><fewi:super value="${genotype.backgroundStrain}"/></td>
	  <td class="allBorders white">${genotype.primaryID}</td>
	</tr>
      </c:forEach>
    </table>
    </div>
  </td>
</tr>
</c:if>
</table>
