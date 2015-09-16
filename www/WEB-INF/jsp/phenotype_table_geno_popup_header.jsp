<a name="${genotype.primaryID}"></a>
<c:set var="imsrAlleles" value="${genotype.imsrAlleles}"/>
<div class="container detailStructureTable">
	<div class="row" >
		<div class="header <%=leftTdStyles.getNext() %>">
			Model
			<c:if test="${not empty counter && counter > 0}">
				<div class="${genotype.genotypeType}Geno genotypeType" style="float: right;text-align: center; padding: 2px; margin-left: 4px;">${genotype.genotypeType}${counter}</div>
			</c:if>
		</div>
		<div class="detail <%=rightTdStyles.getNext() %>">
			<table>
				<tr>
					<td class="top">
						<table border="none">
							<tr>
								<td class="rightBorderThinGray label padded top right"><font class="label">Genotype</font></td>
								<td class="padded">
									<span class="genotypeCombo">
										<fewi:genotype value="${genotype}" newWindow="${true}" />
									</span>
								</td>
								<td class="padded top" style="padding-right: 30px;">
									<!-- Geno Box probally will go back here -->
								</td>
							</tr>
							<tr>
								<td class="rightBorderThinGray label padded top right"><font class="label">Model ID</font></td>
								<td class="padded" style="padding-right: 30px;">${genotype.primaryID}</td>
								<td>&nbsp;</td>
							</tr>
						</table>
					</td>
					<td class="top">
						<table border="none">
							<tr>
								<td class="rightBorderThinGray label padded top right"><font class="label">Genetic Background</font></td>
								<td class="padded" style="padding-right: 30px;">
									<fewi:super value="${genotype.backgroundStrain}"/>
								</td>
							</tr>
							<c:if test="${not empty genotype.cellLines}">
								<tr>
									<td class="rightBorderThinGray label padded top right"><font class="label">Cell Lines</font></td>
									<td class="padded" style="padding-right: 30px;">
										<fewi:super value="${genotype.cellLines}"/>
									</td>
								</tr>
							</c:if>  
							<c:if test="${not empty imsrAlleles}">
							<c:set var="imsrUrl" value="${configBean.IMSRURL}summary?states=embryo&states=live&states=ovaries&states=sperm"/>
								<tr>
									<td class="rightBorderThinGray label padded top right"><font class="label">Find Mice</font></td>
									<td classs="padded" style="padding-right: 30px">
									<c:forEach var="allele" items="${imsrAlleles}">
									<c:if test="${allele.imsrStrainCount > 0}">
									<span style="line-height: 1.5em">IMSR strains carrying <fewi:super value="${allele.symbol}"/> (<a href="${imsrUrl}&gaccid=${allele.primaryID}" target="_blank">${allele.imsrStrainCount} available</a>)<br/></span>
									</c:if>
									<c:if test="${(allele.imsrStrainCount == 0) and (allele.imsrCountForMarker > 0)}">
									<span style="line-height: 1.5em">IMSR strains or lines carrying other mutations in <fewi:super value="${allele.marker.symbol}"/> (<a href="${imsrUrl}&states=ES+Cell&gaccid=${allele.marker.primaryID}" target="_blank">${allele.imsrCountForMarker} available</a>)<br/></span>
									</c:if>

									</c:forEach>
									</td>
								</tr>
							</c:if>
						</table>
					</td>
					<td>
						<%@ include file="phenotype_table_geno_popup_source.jsp" %>
					</td>
				</tr>
			</table>
		</div>
	</div>
</div>

<div style="float: right;">
	<%@ include file="phenotype_table_geno_popup_legend.jsp" %>
</div>
