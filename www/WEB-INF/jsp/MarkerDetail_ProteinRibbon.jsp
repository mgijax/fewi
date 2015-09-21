<c:if test="${(marker.countOfUniProtSequences > 0) or (not empty marker.interProAnnotations) or (not empty marker.proteinOntologyAnnotations) or (not empty marker.ecIDs) or (not empty marker.pdbIDs)}">
    <div class="row proteinRibbon" >
	<div class="header <%=leftTdStyles.getNext() %>">
	    Protein<br/>Information
	</div>
	<div id="proteinInfo" class="detail <%=rightTdStyles.getNext() %>">
	    <!-- order of display, when available:  UniProt, Protein Ontology,
	      -- PDB, EC, InterPro Domains
	      -->

	    <section class="summarySec1">
		<div id="toggleProteinRibbon" class="toggleImage hdCollapse" title="Show More"></div>
		<ul>
		    <c:if test='${marker.countOfUniProtSequences > 0}'>
			<li>
		            <div class="label">UniProt</div>
		            <div class="value"><a href='${configBean.FEWI_URL}sequence/marker/${marker.primaryID}?provider=UniProt'>${marker.countOfUniProtSequences}</a> Sequences</div>
			</li>
		    </c:if>
		    <c:if test='${not empty marker.proteinOntologyAnnotations}'>
			<li>
		            <div class="label">Protein Ontology</div>
			    <c:forEach var="item" items="${marker.proteinOntologyAnnotations}">
			        <div class="value"><a href="${fn:replace(urls.Protein_Ontology, '@@@@', item.termID)}">${item.termID}</a> ${item.term}</div>
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
			        <div class="value"><a href="${fn:replace(urls.InterPro, '@@@@', item.termID)}">${item.termID}</a> ${item.term}</div>
			    </c:forEach>
			</li>
		    </c:if>
	    	</ul>
	    </section>
	</div><!-- proteinInfo -->
    </div><!-- row proteinRibbon -->
</c:if>
