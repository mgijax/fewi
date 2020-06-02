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
	Marker Symbol/Name: including text <b><fewi:verbatim value="${e:forHtml(queryForm.nomen)}"/></b>
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
	InterPro Protein Domain: contains <b>${e:forHtml(queryForm.interpro)}</b>
	<c:set var="notFirst" value="${true}"/></c:if>
<c:if test="${not empty queryForm.phenotype}">
	<br><c:if test="${notFirst}">AND</c:if>
	Phenotypes/Diseases: including text <b><fewi:verbatim value="${queryForm.phenotype}"/></b>
		<span class="smallGrey">searching MP terms, synonyms, IDs, and notes, disease terms, synonyms and IDs</span>
	<c:set var="notFirst" value="${true}"/></c:if>

<%-- sorting help text --%>
<c:choose>
  <c:when test="${not empty queryForm.nomen && (not empty queryForm.go || not empty queryForm.interpro)}">
	<br>For a Marker Symbol/Name and GO or InterPro search, the default sort is by text-matching relevance score emphasizing Marker Symbol/Name matches.
  </c:when>
  <c:when test="${not empty queryForm.nomen}">
	<br>For a Marker Symbol/Name search the default sort is by text-matching relevance score.
  </c:when>
  <c:when test="${not empty queryForm.go || not empty queryForm.interpro}">
	<br>For a GO or InterPro search, the default sort is by text-matching relevance score.
  </c:when>
  <c:otherwise>
	<br>The default sort is alphanumeric by Symbol.
  </c:otherwise>
</c:choose>

