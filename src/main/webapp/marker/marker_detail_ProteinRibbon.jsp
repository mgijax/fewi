<c:if test="${(marker.countOfUniProtSequences > 0) or (not empty marker.interProAnnotations) or (not empty marker.proteinOntologyAnnotations) or (not empty marker.ecIDs) or (not empty marker.pdbIDs)}">
	<div class="row proteinRibbon" id="proteinInfoRibbon">
		<div class="header <%=leftTdStyles.getNext() %>">
			Protein<br/>Information
		</div>
		<div id="protein" class="detail <%=rightTdStyles.getNext() %>">
			<!-- order of display, when available:  UniProt, Protein Ontology,
			  -- PDB, EC, InterPro Domains
			  -->

			<div id="piToggle" class="toggleImage hdCollapse" title="Show Less">less</div>

			<section class="summarySec1 extra wide">
				<ul>
					<c:if test='${marker.countOfUniProtSequences > 0}'>
						<li>
							<div class="label">UniProt</div>
							<div class="value"><a href='${configBean.FEWI_URL}sequence/marker/${marker.primaryID}?provider=UniProt' id='uniprotLink'>${marker.countOfUniProtSequences}</a> Sequence<c:if test="${marker.countOfUniProtSequences > 1}">s</c:if></div>
						</li>
					</c:if>
					<c:if test='${not empty marker.proteinOntologyAnnotations}'>
						<li>
							<div class="label">Protein Ontology</div>
							<c:forEach var="item" items="${marker.proteinOntologyAnnotations}">
								<div class="value"><a href="${fn:replace(urls.Protein_Ontology, '@@@@', fn:replace(item.termID, ':', '_'))}" target="_blank">${item.termID}</a> ${item.term}</div>
								<div class="value"><a href="${fn:replace(urls.PRO_Browser, '@@@@', item.termID)}" target="_blank">(term hierarchy)</a></div>
							</c:forEach>
						</li>
					</c:if>
					<c:if test='${not empty marker.pdbIDs}'>
						<li>
							<div class="label">PDB</div>
							<div class="value">${otherIDs["PDB"]}</div>
						</li>
					</c:if>
					<c:if test='${not empty marker.ecIDs}'>
						<li>
							<div class="label">EC</div>
							<div class="value">${otherIDs["EC"]}</div>
						</li>
					</c:if>
					<c:if test='${not empty marker.interProAnnotations}'>
						<li>
							<div class="label">InterPro Domains</div>
							<c:forEach var="item" items="${marker.interProAnnotations}">
								<div class="value"><a href="${fn:replace(urls.InterPro, '@@@@', item.termID)}" target="_blank">${item.termID}</a> ${item.term}</div>
							</c:forEach>
						</li>
					</c:if>
					<c:if test='${not empty marker.glyGenAnnotations}'>
						<li>
							<div class="label">GlyGen</div>
							<c:forEach var="item" items="${marker.glyGenAnnotations}">
								<div class="value"><a href="${fn:replace(urls.GlyGen, '@@@@', item.termID)}" target="_blank">${item.termID}</a> ${item.term}</div>
							</c:forEach>
						</li>
					</c:if>
				</ul>
			</section>
		</div><!-- proteinInfo -->
	</div><!-- row proteinRibbon -->
</c:if>
