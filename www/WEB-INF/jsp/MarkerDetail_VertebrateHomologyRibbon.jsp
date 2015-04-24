	<c:if test="${(not empty homologyClasses) or (marker.hasOneEnsemblGeneModelID) or (not empty marker.pirsfAnnotation)}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Vertebrate<br/>homology
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				<c:forEach var="homologyClass" items="${homologyClasses}">
					<c:if test="${not empty homologyClass.primaryID}">
						HomoloGene:${homologyClass.primaryID}&nbsp;&nbsp;<a href="${configBean.FEWI_URL}homology/${homologyClass.primaryID}">Vertebrate Homology Class</a><br/>
						<c:forEach var="organismOrthology" items="${homologyClass.orthologs}" varStatus="status">${organismOrthology.markerCount} ${organismOrthology.organism}<c:if test="${!status.last}">;</c:if> </c:forEach><p/>
					</c:if>
				</c:forEach>

				<c:forEach var="organism" items="${hcopLinks}">
					<c:if test="${fn:length(organism.value) > 0}">
						HCOP ${organism.key} homology predictions:
						<c:forEach var="hmarker" items="${organism.value}" varStatus="hcstat">
							<c:if test="${organism.key == 'human'}">
								<a href="${fn:replace(urls.HCOP, '@@@@', hmarker.value.symbol)}" target="_blank">${hmarker.value.symbol}</a><c:if test="${!hcstat.last}">, </c:if>
							</c:if>
						</c:forEach>
						<br>
					</c:if>
				</c:forEach>

				<c:set var="pirsf" value="${marker.pirsfAnnotation}"/>
				<c:if test="${not empty pirsf}">
					Protein SuperFamily: <a href="${configBean.FEWI_URL}vocab/pirsf/${pirsf.termID}">${pirsf.term}</a><br/>
				</c:if>
				<c:if test="${marker.hasOneEnsemblGeneModelID}">
					<c:set var="genetreeUrl" value="${configBean.GENETREE_URL}"/>
					<c:set var="genetreeUrl" value="${fn:replace(genetreeUrl, '<model_id>', marker.ensemblGeneModelID.accID)}"/>
					Gene Tree: <a href="${configBean.GENETREE_URL}${marker.ensemblGeneModelID.accID}">${marker.symbol}</a><br/>
				</c:if>
			</div>
		</div>
	</c:if>
