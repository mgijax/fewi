<style>
.genoID { font-weight: normal; font-size: 90% }
.imsrIndent { padding-left: 5px }
</style>
<a name="${genotype.primaryID}"></a>
<c:set var="imsrAlleles" value="${genotype.imsrAlleles}"/>
<div class="container detailStructureTable">
	<div class="row" >
		<div class="header <%=leftTdStyles.getNext() %>">
			Genotype<br/>
			<font class="genoID">${genotype.primaryID}</font><br/>
			<c:if test="${not empty counter && counter > 0}">
				<div class="${genotype.genotypeType}Geno genotypeType" style="float: right;text-align: center; padding: 2px; margin-left: 4px; line-height: 1.5em; font-weight: normal; padding-bottom: 8px;">${genotype.genotypeType}${counter}</div>
			</c:if>
		</div>
		<div class="detail <%=rightTdStyles.getNext() %>">
			<table>
				<tr>
					<td class="top">
						<table border="none">
							<tr>
								<td class="rightBorderThinGray label padded top right"><font class="label">Allelic<br/>Composition</font></td>
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
								<td class="rightBorderThinGray label padded top right"><font class="label">Genetic<br/>Background</font></td>
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
						</table>
					</td>
					<td class="top">
						<table border="none">
							<tr>
								<td class="rightBorderThinGray label padded top right"><font class="label">Find Mice</font></td>
								<td class="padded" style="padding-right: 30px; line-height: 150%">
								Using the International Mouse Strain Resource (<a href="http://www.findmice.org/" target="_blank">IMSR</a>)<br/>
							<c:if test="${empty imsrAlleles}">
							No mouse lines available in IMSR.<br/>
							See publication links below for author information.
							</c:if>
							<c:if test="${not empty imsrAlleles}">
							Mouse lines carrying:<br/>

							<c:set var="imsrUrl" value="${configBean.IMSRURL}summary?states=embryo&states=live&states=ovaries&states=sperm"/>
							<c:forEach var="allele" items="${imsrAlleles}">
								<c:set var="isTransgene" value="false"/>
								<c:set var="divider" value=";"/>
								<c:if test="${(allele.alleleType == 'Transgenic') and (allele.marker.markerType == 'Transgene')}">
									<c:set var="isTransgene" value="true"/>
									<c:set var="divider" value=""/>
								</c:if>

								<span class="imsrIndent"><fewi:super value="${allele.symbol}"/></span> mutation
								<c:if test="${allele.imsrStrainCount == 0}">
								(0 available)${divider}
								</c:if>
								<c:if test="${allele.imsrStrainCount > 0}">
								(<a href="${imsrUrl}&gaccid=${allele.primaryID}" target="_blank">${allele.imsrStrainCount} available</a>)${divider}
								</c:if>

								<c:if test="${not isTransgene}">
								any <fewi:super value="${allele.marker.symbol}"/> mutation
								<c:if test="${allele.imsrCountForMarker == 0}">
								(0 available)
								</c:if>
								<c:if test="${allele.imsrCountForMarker > 0}">
								(<a href="${imsrUrl}&states=ES+Cell&gaccid=${allele.marker.primaryID}" target="_blank">${allele.imsrCountForMarker} available</a>)
								</c:if>
								</c:if>
								<br/>
							</c:forEach>
							</c:if>
								</td>
							</tr>
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
