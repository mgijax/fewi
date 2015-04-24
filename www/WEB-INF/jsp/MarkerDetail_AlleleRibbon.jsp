	<c:if test="${(marker.countOfAlleles > 0) or (not empty marker.markerClip) or (not empty marker.incidentalMutations) or (marker.countOfHumanDiseases > 0) or (marker.countOfAllelesWithHumanDiseases > 0) or (marker.countOfPhenotypeImages > 0)}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Mutations,<br/>alleles, and<br/>phenotypes
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				<c:if test="${marker.countOfAlleles > 0}">
					<c:set var="alleleUrl" value="${configBean.FEWI_URL}allele/summary?markerId=${marker.primaryID}"/>
					All mutations/alleles(<a href="${alleleUrl}">${marker.countOfAlleles}</a>) :
					<c:forEach var="item" items="${marker.alleleCountsByType}">
						${item.countType}(<a href="${alleleUrl}&alleleType=${item.countType}">${item.count}</a>)
					</c:forEach>
					<br/>
				</c:if>
				<c:if test="${marker.countOfMutationInvolves > 0}">
					Genomic Mutations involving ${marker.symbol} (<a href="${alleleUrl}&mutationInvolves=1">${marker.countOfMutationInvolves}</a>)<br/>
				</c:if>
				<c:if test="${not empty marker.incidentalMutations}">
					Incidental mutations (data from
					<c:forEach var="incidentalMutation" items="${marker.incidentalMutations}" varStatus="imStatus">
						<c:if test="${imStatus.index>0}">, </c:if><a href="${configBean.FTP_BASE_URL}datasets/incidental_muts/${incidentalMutation.filename}">${incidentalMutation.filenameNoExtension}</a>
					</c:forEach>
					)<br/>
				</c:if>
				<c:if test="${not empty marker.markerClip}">
					&nbsp;<br/>
					<blockquote>${marker.markerClip}</blockquote>
					&nbsp;<br/>
				</c:if>
				<c:if test="${marker.countOfHumanDiseases > 0}">
					Human Diseases Modeled in Mice Using ${marker.symbol} (<a href="" onclick="return overlib( '<span style=\'font-size:12px;\'>Diseases ' +
					'listed here are those where a mutant allele of this gene is involved in a mouse genotype used as ' +
					'a model. This does not mean that mutations in this gene contribute to or are causative of the disease.</span>' +
					'<table name=\'results\' border=\'0\' cellpadding=\'3\' cellspacing=\'0\' width=\'100%\'>' +
					'<tr ><th align=\'left\'>Human Disease</th><th width=\'4\'></th>' +
					'<th width=\'65\'>OMIM ID</th></tr>' +
					<c:set var="mMessage" value="&nbsp;" />
					<c:forEach var="annotation" items="${marker.OMIMAnnotations}" varStatus="status">
					<c:set var="rColor" value="" />
					<c:if test="${status.count % 2 == 0}">
					<c:set var="rColor" value="style=\\'background-color:#F8F8F8;\\'" />
					</c:if>
					'<tr ${rColor} align=\'left\' valign=\'top\'>' +
					'<td><a href=\'${configBean.FEWI_URL}disease/${annotation.termID}\'>${annotation.term}</a></td>' +
					'<td width=\'4\'>' +
					<c:forEach var="star" items="${humanOrtholog.OMIMHumanAnnotations}">
					<c:if test="${annotation.termID eq star.termID}">
					<c:set var="mMessage" value="* Disease is associated with mutations in human ${humanOrtholog.symbol}." />
					'*' +
					</c:if>
					</c:forEach>
					'</td>' +
					'<td><a href=\'${fn:replace(urls.OMIM, '@@@@', annotation.termID)}\' target=\'_blank\'>${annotation.termID}</a></td></tr>' +
					</c:forEach>
					'<tr align=\'left\' valign=\'top\'><td	colspan=\'3\'>${mMessage}</td></tr></table>', STICKY, CAPTION, 'Human Disease Models Associated with Alleles of Mouse ${marker.symbol}', RIGHT, BELOW, WIDTH, 500, DELAY, 250, CLOSECLICK, CLOSETEXT, 'Close X');" onmouseout="nd();">${marker.countOfHumanDiseases}</a>)&nbsp;&nbsp;&nbsp;
				</c:if>
				<c:if test="${marker.countOfAllelesWithHumanDiseases > 0}">
					Mutations Annotated to Human Diseases (<a href="${configBean.FEWI_URL}allele/summary?markerId=${marker.primaryID}&hasOMIM=1">${marker.countOfAllelesWithHumanDiseases}</a>)&nbsp;&nbsp;&nbsp;
				</c:if>
				<c:if test="${marker.countOfPhenotypeImages > 0}">
					Phenotype Images(<a href="${configBean.FEWI_URL}image/phenoSummary/marker/${marker.primaryID}">${marker.countOfPhenotypeImages}</a>)
				</c:if>
			</div>
		</div>
	</c:if>


