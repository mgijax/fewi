<% /* generates <td> cells for allele calls, based on a list of ${strains}
    * and a list of AlleleSNP objects called ${alleles}.
    * Each allele can be of either SubSnpAllele or ConsensusSnpAllele the exception is for the conflict
    * property that only exists on the ConsensusSnpAllele
    */
%>
<c:forEach var="strain" items="${strains}">
	<c:set var="call" value="" />
	<c:set var="conflict" value="" />
	<c:forEach var="allele" items="${alleles}">
		<c:if test="${allele.strain == strain}">
			<c:set var="call" value="${allele.allele}"/>
			<c:catch var="exception">
				<c:if test="${allele.conflict and (allele.allele != '?')}">
					<c:set var="conflict" value=" conflict"/>
				</c:if>
			</c:catch>
		</c:if>
	</c:forEach>
	<td class="allele${fn:replace(fn:replace(call, '-', 'Dash'), '?', 'Q')}${conflict}">${call}</td>
</c:forEach>
