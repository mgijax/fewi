	<c:if test="${(marker.countOfGOTerms > 0) or (marker.isInReferenceGenome > 0)}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Gene&nbsp;Ontology<br/>(GO)<br/>classifications
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				All GO classifications: (<a href="${configBean.FEWI_URL}go/marker/${marker.primaryID}">${marker.countOfGOTerms}</a> annotations)<br/>
				<table>
					<c:if test="${not empty processAnnot1}">
						<tr><td>Process</td>
							<td style="padding-left:6em;"><a href="${configBean.WI_URL}searches/GO.cgi?id=${processAnnot1.termID}">${processAnnot1.term}</a><c:if test="${not empty processAnnot2}">, </c:if>
							<a href="${configBean.WI_URL}searches/GO.cgi?id=${processAnnot2.termID}">${processAnnot2.term}</a><c:if test="${not empty processAnnot3}">, ...</c:if>
						</td></tr>
					</c:if>
					<c:if test="${not empty componentAnnot1}">
						<tr><td>Component</td>
							<td style="padding-left:6em;"><a href="${configBean.WI_URL}searches/GO.cgi?id=${componentAnnot1.termID}">${componentAnnot1.term}</a><c:if test="${not empty componentAnnot2}">, </c:if>
							<a href="${configBean.WI_URL}searches/GO.cgi?id=${componentAnnot2.termID}">${componentAnnot2.term}</a><c:if test="${not empty componentAnnot3}">, ...</c:if>
						</td></tr>
					</c:if>
					<c:if test="${not empty functionAnnot1}">
						<tr><td>Function</td>
							<td style="padding-left:6em;"><a href="${configBean.WI_URL}searches/GO.cgi?id=${functionAnnot1.termID}">${functionAnnot1.term}</a><c:if test="${not empty functionAnnot2}">, </c:if>
							<a href="${configBean.WI_URL}searches/GO.cgi?id=${functionAnnot2.termID}">${functionAnnot2.term}</a><c:if test="${not empty functionAnnot3}">, ...</c:if>
						</td></tr>
					</c:if>
				</table>
				<c:if test="${marker.isInReferenceGenome > 0}">
					This is a <a href="${referenceGenomeURL}">GO Consortium Reference Genome Project</a> gene.<br>
				</c:if>
				<c:set var="funcbaseID" value="${marker.funcBaseID}"/>
				<c:if test="${not empty funcbaseID}">
					External Resources:
					<a href="${fn:replace(urls.FuncBase, '@@@@', funcbaseID.accID)}">FuncBase</a><br/>
				</c:if>
			</div>
		</div>
	</c:if>


