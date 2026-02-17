<!-- SNP Assays and SNP Consensus -->
<c:set var="strainCount" value="0"/>
<c:if test="${not empty strains}">
	<c:set var="strainCount" value="${fn:length(strains)}"/>
</c:if>
<c:set var="columnCount" value="${strainCount + 6}"/>

<div class="row" >
	<div class="header <%=leftTdStyles.getNext() %>">
		SNP Assays<br/>and<br/>SNP Consensus
	</div>
	<div class="detail <%=rightTdStyles.getNext() %>" style="padding-left: 10px">
	<% /* Current data has no examples of consensus SNPs without subSNPs,
		so we'll work with the assumption that there will always be
		at least one subSNP for every consensus SNP. */
	%>
	<span class="small"><i>Strain alleles of submitted assays, and consensus values for this SNP</i></span><br/>
	<a onClick="javascript:openUserhelpWindow('SNP_legend_help.shtml'); return false;" href="${configBean.USERHELP_URL}SNP_legend_help.shtml" class="bold" style="line-height: 200%">Legend</a><br/>
		<section class="summarySec1 wide" style="overflow:auto;">
			<table id="snpAssays">
			    <c:if test="${not empty subSnpsWithStrains}">
				<!-- SubSNP header row -->
				<tr>
				<th class="blueBG snpStrainHeader">Assay ID</th>
				<th class="blueBG snpStrainHeader">Submitter<br/>SNP ID</th>
				<th class="blueBG snpStrainHeader">Submitter<br/>Handle</th>
				<th class="blueBG snpStrainHeader">Population</th>
				<td class="blueBG snpStrainVerticalHeader"><div class="snpStrainDiv">ss orientation</div></td>
				<th class="blueBG snpStrainHeader">Variation Type</th>
				<c:forEach var="strain" items="${strains}">
				<td class="blueBG snpStrainVerticalHeader"><div class="snpStrainDiv" <fewi:tooltip text="${strain}" superscript="true"/>>${strain}</div></td>
				</c:forEach>
				</tr>

				<!-- Individual SubSNP rows -->
				<c:forEach var="ss" items="${subSnpsWithStrains}">
				<%@ include file="SNPDetail_Assays_SubSNP.jsp" %>
				</c:forEach>
			    </c:if><!-- not empty subSnpsWithStrains -->

				<!-- Consensus SNP title row -->
				<tr>
				<th class="yellowBar" colspan="${columnCount}">SNP Consensus Information</th>
				</tr>

				<!-- Consensus SNP header row -->
				<tr>
				<th class="blueBG snpStrainHeader" colspan="5">SNP</th>
				<th class="blueBG snpStrainHeader">Consensus Type</th>
				<c:forEach var="strain" items="${strains}">
				<td class="blueBG snpStrainVerticalHeader"><div class="snpStrainDiv" <fewi:tooltip text="${strain}" superscript="true"/>>${strain}</div></td>
				</c:forEach>
				</tr>

				<!-- Consensus SNP row -->
				<tr>
					<td colspan="5">${snp.accid}</td>
					<td>${snp.variationClass}</td>
					<c:set var="alleles" value="${snp.consensusAlleles}"/>
					<%@ include file="SNPDetail_Assays_Alleles.jsp" %>
				</tr>
			</table>
			<c:if test="${not empty subSnpsWithoutStrains}">
				<div class="small" style="margin-top: 10px; margin-bottom: 10px;"><i>Additional submitted assays for this SNP, where the strain of origin for observed alleles is not specified by the submitter</i></div>
				<table id="snpAssaysWithoutStrains">
					<tr>
						<th class="blueBG snpStrainHeader">Assay ID</th>
						<th class="blueBG snpStrainHeader">Submitter<br/>SNP ID</th>
						<th class="blueBG snpStrainHeader">Submitter<br/>Handle</th>
						<th class="blueBG snpStrainHeader">Population</th>
						<td class="blueBG snpStrainVerticalHeader"><div class="snpStrainDiv">ss orientation</div></td>
						<th class="blueBG snpStrainHeader">Variation<br/>Type</th>
						<th class="blueBG snpStrainHeader">Alleles</th>
					</tr>
				<c:forEach var="ss" items="${subSnpsWithoutStrains}">
					<c:forEach var="pop" items="${ss.populations}">
					  <c:if test="${fn:length(pop.alleles) == 0}">
					    <tr>
					        <td<c:if test="${ss.exemplar}"> class="exemplar"</c:if>>${ss.accid}</td>
						<td>${ss.submitterId}</td>
						<td>${pop.subHandleName}</td>
						<td>${pop.populationName}</td>
						<td>${ss.orientation}</td>
						<td>${ss.variationClass}</td>
						<td>${ss.alleleSummary}</td>
					    </tr>
					  </c:if>
					</c:forEach>
				</c:forEach>
				</table>
			</c:if> <!-- not empty subSnpsWithStrains -->
		</section>
	</div>
</div>
