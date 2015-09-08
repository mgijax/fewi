	<c:set var="proteinAnnotations" value="${marker.proteinAnnotations}"/>
	<c:if test="${not empty proteinAnnotations}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Protein-related<br/>Information
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">

				<table>
					<tr>
						<td class="top">
							<span id="toggleProteinRibbon" title="Show More" class="toggleImage hdCollapse" onclick="toggleRibbon('ProteinRibbon'); return false;"></span>
						</td>
						<td>
							<div id="closedProteinRibbon" style="display:none;">
							</div>
							<div id="openedProteinRibbon" style="display:block;">
								<table>
									<tr><td class="rightPadded">Resource</td><td class="rightPadded">ID</td><td>Description</td></tr>
									<c:forEach var="item" items="${proteinAnnotations}">
										<c:set var="url" value=""/>
										<c:if test="${item.vocabName == 'InterPro Domains'}">
											<c:set var="url" value="${urls.InterPro}"/>
										</c:if>
										<c:if test="${item.vocabName == 'Protein Ontology'}">
											<c:set var="url" value="${urls.Protein_Ontology}"/>
										</c:if>
										<tr><td class="rightPadded">${fn:replace (item.vocabName, " Domains", "")}</td>
										<c:if test="${url != ''}">
											<td class="rightPadded"><a href="${fn:replace(url, '@@@@', item.termID)}">${item.termID}</a></td>
										</c:if>
										<c:if test="${url == ''}">
											<td>${item.termID}</td>
										</c:if>
										<td>${item.term}</td></tr>
									</c:forEach>
								</table>
								<c:if test="${not empty marker.representativePolypeptideSequence}">
									<c:set var="seq" value="${marker.representativePolypeptideSequence}" scope="request"/>
									<c:if test="${seq.provider == 'SWISS-PROT'}">
										<c:set var="url" value="${urls.InterPro_ISpy}"/>
										<a href="${fn:replace(url, '@@@@', seq.primaryID)}" target="_new">Graphical View of Protein Domain Structure</a>
									</c:if>
								</c:if>
							</div>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</c:if>
