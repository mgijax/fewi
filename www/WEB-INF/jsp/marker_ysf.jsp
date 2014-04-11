<%-- Allele you searched for component. expects an ${alleleQueryForm} variable --%>
<style>
.smallGrey
{
	font-size: 75%;
	color: #999999;
}
</style>
<c:set var="notFirst" value="${false}"/>
<c:if test="${not empty queryForm.nomen}">
	<br><c:if test="${notFirst}">AND</c:if>
	Marker Symbol/Name: including text <b><fewi:verbatim value="${queryForm.nomen}"/></b>
		<span class="smallGrey">searching current symbols/names and synonyms</span>
<c:set var="notFirst" value="${true}"/></c:if>
<c:if test="${not empty queryForm.mcv}">
	<br><c:if test="${notFirst}">AND</c:if>
	Feature Type: any of <b>${queryForm.mcvDisplay}</b>
<c:set var="notFirst" value="${true}"/></c:if>
<c:if test="${queryForm.hasChromosome}">
	<br><c:if test="${notFirst}">AND</c:if>
	on Chromosome: any of <b>${queryForm.chromosome}</b>
<c:set var="notFirst" value="${true}"/></c:if>
<c:if test="${not empty queryForm.coordinate}">
	<br><c:if test="${notFirst}">AND</c:if>
	Genome Coordinates: <b>${queryForm.coordinate}</b> <c:out value="${queryForm.coordUnit=='Mbp' ? 'Mbp' : 'bp'}"/>
<c:set var="notFirst" value="${true}"/></c:if>
<c:if test="${not empty queryForm.cm}">
	<br><c:if test="${notFirst}">AND</c:if>
	cM Position: <b>${queryForm.cm}</b>
<c:set var="notFirst" value="${true}"/></c:if>
<c:if test="${not empty queryForm.startMarker}">
	<br><c:if test="${notFirst}">AND</c:if>
	Marker Range: between <b>${queryForm.startMarker}</b> and <b>${queryForm.endMarker}</b>
<c:set var="notFirst" value="${true}"/></c:if>
<c:if test="${not empty queryForm.go}">
	<br><c:if test="${notFirst}">AND</c:if>
	Gene Ontology Terms(s): contains <b>${queryForm.go}</b>
	<c:if test="${not empty queryForm.goVocab and fn:length(queryForm.goVocab)<3}">
	searching <b>${queryForm.goVocabYSF}</b>
	</c:if>
<c:set var="notFirst" value="${true}"/></c:if>
<c:if test="${not empty queryForm.interpro}">
	<br><c:if test="${notFirst}">AND</c:if>
	InterPro Protein Domain: contains <b>${queryForm.interpro}</b>
<c:set var="notFirst" value="${true}"/></c:if>
<c:if test="${not empty queryForm.phenotype}">
	<br><c:if test="${notFirst}">AND</c:if>
	Phenotypes/Diseases: including text <b><fewi:verbatim value="${queryForm.phenotype}"/></b> 
		<span class="smallGrey">searching MP terms, synonyms, IDs, and notes, disease terms, synonyms and IDs</span>
<c:set var="notFirst" value="${true}"/></c:if>
<%-- Special nomen help text --%>
<c:if test="${not empty queryForm.nomen}">
	The default sort when Marker Symbol/Name is submitted is by a text-matching relevance score.
</c:if>