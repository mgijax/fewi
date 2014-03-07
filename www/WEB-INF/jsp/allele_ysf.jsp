<%-- Allele you searched for component. expects an ${alleleQueryForm} variable --%>
<style>
.smallGrey
{
	font-size: 75%;
	color: #999999;
}
</style>
<c:set var="notFirst" value="${false}"/>
<c:if test="${not empty alleleQueryForm.phenotype}">
	<br><c:if test="${notFirst}">AND</c:if>
	Phenotypes/Diseases: including text <b><fewi:verbatim value="${alleleQueryForm.phenotype}"/></b> 
		<span class="smallGrey">searching MP terms, synonyms, IDs, and notes, disease terms, synonyms and IDs</span>
<c:set var="notFirst" value="${true}"/></c:if>
<c:if test="${not empty alleleQueryForm.nomen}">
	<br><c:if test="${notFirst}">AND</c:if>
	Nomenclature: including text <b><fewi:verbatim value="${alleleQueryForm.nomen}"/></b>
		<span class="smallGrey">searching current symbols/names and synonyms</span>
<c:set var="notFirst" value="${true}"/></c:if>
<c:if test="${alleleQueryForm.hasChromosome}">
	<br><c:if test="${notFirst}">AND</c:if>
	on Chromosome: any of <b>${alleleQueryForm.chromosome}</b>
<c:set var="notFirst" value="${true}"/></c:if>
<c:if test="${not empty alleleQueryForm.coordinate}">
	<br><c:if test="${notFirst}">AND</c:if>
	Genome Coordinates: <b>${alleleQueryForm.coordinate}</b> <c:out value="${alleleQueryForm.coordUnit=='Mbp' ? 'Mbp' : 'bp'}"/>
<c:set var="notFirst" value="${true}"/></c:if>
<c:if test="${not empty alleleQueryForm.cm}">
	<br><c:if test="${notFirst}">AND</c:if>
	cM Position: <b>${alleleQueryForm.cm}</b>
<c:set var="notFirst" value="${true}"/></c:if>
<c:if test="${not empty alleleQueryForm.alleleType}">
	<br><c:if test="${notFirst}">AND</c:if>
	Generation Method: any of <b>${alleleQueryForm.alleleType}</b>
<c:set var="notFirst" value="${true}"/></c:if>
<c:if test="${not empty alleleQueryForm.alleleSubType}">
	<br><c:if test="${notFirst}">AND</c:if>
	Allele Attributes: any of <b>${alleleQueryForm.alleleSubType}</b>
<c:set var="notFirst" value="${true}"/></c:if>
<c:if test="${not empty alleleQueryForm.collection}">
	<br><c:if test="${notFirst}">AND</c:if>
	Collection: any of <b>${alleleQueryForm.collection}</b>
<c:set var="notFirst" value="${true}"/></c:if>
<c:if test="${'0'==alleleQueryForm.isCellLine}">
	<br><c:if test="${notFirst}">AND</c:if>
	<b>Excluding Alleles Existing Only as Cell Lines</b>
<c:set var="notFirst" value="${true}"/></c:if>