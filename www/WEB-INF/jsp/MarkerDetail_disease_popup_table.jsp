<table style="filter:alpha(opacity=100);">
	<tr>
		<th class="headerStripe allBorders" style="max-width='80%'">Allelic Composition</th>
		<th class="headerStripe allBorders">Genetic Background</th>
		<th class="headerStripe allBorders">Reference</th>
		<th class="headerStripe allBorders">Phenotypes</th>
	</tr>

	<c:forEach var="dm" items="${models}">
		<tr>
			<td class="allBorders leftAlign">${ntc.convertToHTMLNewWindowWithoutPopup(dm.allelePairs)}</td>
			<td class="allBorders leftAlign">
				<c:choose>
					<c:when test="${not empty dm.genotype.strainID}">
						<a href="${configBean.FEWI_URL}strain/${dm.genotype.strainID}" target="_blank">${tf.superscriptHTML(dm.backgroundStrain)}</a>
					</c:when>
					<c:otherwise>
						${tf.superscriptHTML(dm.backgroundStrain)}
					</c:otherwise>
				</c:choose>
			</td>
			<td class="allBorders leftAlign">
				<!-- loop on the references -->
				<c:forEach var="ref" items="${dm.references}">
					<a href="${configBean.FEWI_URL}reference/${ref.jnumID}" target="_blank">${ref.jnumID}</a>
				</c:forEach>
			</td>
			<td class="allBorders leftAlign"><a href="${configBean.FEWI_URL}allele/genoview/${dm.genotypeID}" target="_blank">View</a></td>
		</tr>
	</c:forEach>

</table>
