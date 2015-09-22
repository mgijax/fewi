<c:if test="${(marker.countOfUniProtSequences > 0) or (not empty marker.interProAnnotations) or (not empty marker.proteinOntologyAnnotations) or (not empty marker.ecIDs) or (not empty marker.pdbIDs)}">
	<div class="row proteinRibbon" >
		<div class="header <%=leftTdStyles.getNext() %>">
			Protein<br/>Information
		</div>
		<div id="protein" class="detail <%=rightTdStyles.getNext() %>">
			<!-- order of display, when available:  UniProt, Protein Ontology,
			  -- PDB, EC, InterPro Domains
			  -->

			<c:set var="proteinUnindent" value=" style='width: calc(13.219em - 26px);'" />
			<section class="summarySec1 wide">
				<div id="toggleProteinRibbon" class="toggleImage hdCollapse" title="Show More"></div>
				<ul>
					<c:if test='${marker.countOfUniProtSequences > 0}'>
						<li class="extra open">
							<div class="label"${proteinUnindent}>UniProt</div>
							<div class="value"><a href='${configBean.FEWI_URL}sequence/marker/${marker.primaryID}?provider=UniProt'>${marker.countOfUniProtSequences}</a> Sequences</div>
						</li>
						<c:set var="proteinUnindent" value=""/>
					</c:if>
					<c:if test='${not empty marker.proteinOntologyAnnotations}'>
						<li class="extra open">
							<div class="label"${proteinUnindent}>Protein Ontology</div>
							<c:forEach var="item" items="${marker.proteinOntologyAnnotations}">
								<div class="value"><a href="${fn:replace(urls.Protein_Ontology, '@@@@', item.termID)}">${item.termID}</a> ${item.term}</div>
							</c:forEach>
						</li>
						<c:set var="proteinUnindent" value=""/>
					</c:if>
					<c:if test='${not empty marker.pdbIDs}'>
						<li class="extra open">
							<div class="label"${proteinUnindent}>PDB</div>
							<div class="value">${otherIDs["PDB"]}</div>
						</li>
						<c:set var="proteinUnindent" value=""/>
					</c:if>
					<c:if test='${not empty marker.ecIDs}'>
						<li class="extra open">
							<div class="label"${proteinUnindent}>EC</div>
							<div class="value">${otherIDs["EC"]}</div>
						</li>
						<c:set var="proteinUnindent" value=""/>
					</c:if>
					<c:if test='${not empty marker.interProAnnotations}'>
						<li class="extra open">
							<div class="label"${proteinUnindent}>InterPro Domains</div>
							<c:forEach var="item" items="${marker.interProAnnotations}">
								<div class="value"><a href="${fn:replace(urls.InterPro, '@@@@', item.termID)}">${item.termID}</a> ${item.term}</div>
							</c:forEach>
						</li>
						<c:set var="proteinUnindent" value=""/>
					</c:if>
				</ul>
			</section>
		</div><!-- proteinInfo -->
	</div><!-- row proteinRibbon -->
</c:if>
