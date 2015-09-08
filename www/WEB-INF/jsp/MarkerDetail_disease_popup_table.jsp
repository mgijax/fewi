<table style="filter:alpha(opacity=100);">
	<tr>
		<td class="headerStripe allBorders" style="max-width='80%'"><font class="label">Allelic Composition</font></td>
		<td class="headerStripe allBorders"><font class="label">Genetic Background</font></td>
		<td class="headerStripe allBorders"><font class="label">Reference</font></td>
		<td class="headerStripe allBorders"><font class="label">Phenotypes</font></td>
	</tr>

	<c:forEach var="dm" items="${models}">
		<tr>
			<td class="allBorders leftAlign">${ntc.convertToHTMLNewWindowWithoutPopup(dm.allelePairs)}</td>
			<td class="allBorders leftAlign">${tf.superscriptHTML(dm.backgroundStrain)}</td>
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
