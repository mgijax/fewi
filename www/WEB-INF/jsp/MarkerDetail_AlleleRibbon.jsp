<style>
td.limitWidth { max-width: 500px }
</style>
<c:if test="${(marker.countOfAlleles > 0) or (not empty marker.markerClip) or (not empty marker.incidentalMutations) or (marker.countOfHumanDiseases > 0) or (marker.countOfAllelesWithHumanDiseases > 0) or (marker.countOfPhenotypeImages > 0) or (marker.countOfPhenotypeReferences > 0) or (marker.countOfOtherPhenotypeAnnotations > 0)}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Mutations,<br/>Alleles, and<br/>Phenotypes
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				<table>
					<tr>
						<td class="top">
							<span id="toggleAlleleRibbon" title="Show More" class="toggleImage hdCollapse" onclick="toggleRibbon('AlleleRibbon'); return false;"></span>
						</td>
						<td>
							<div id="openedAlleleRibbon" style="display:block;">
								<c:set var="mpHasSummaryDiv" value="false"/>
								<c:set var="mpHasSlimgrid" value="false"/>
								<c:set var="mpHasAlleleDiv" value="false"/>
								<c:if test="${(marker.countOfAllelesMP > 0) or (marker.countOfPhenotypeImages > 0) or (marker.countOfPhenotypeReferences > 0) or (marker.countOfOtherPhenotypeAnnotations > 0)}">
									<c:set var="mpHasSummaryDiv" value="true"/>
								</c:if>
								<c:if test="${(marker.countOfAlleles > 0) or (not empty marker.incidentalMutations) or (marker.countOfMutationInvolves > 0)}">
									<c:set var="mpHasAlleleDiv" value="true"/>
								</c:if>
								<c:if test="${not empty marker.slimgridCellsMP}">
									<c:set var="mpHasSlimgrid" value="true"/>
								</c:if>

								<c:if test="${mpHasSummaryDiv or mpHasSlimgrid}">
									<div id="mpLeftDiv" style="display: inline-block; vertical-align: top; padding-bottom: 6px;">
								</c:if>

									<c:if test="${mpHasSummaryDiv}">
										<div style="padding-bottom: 6px">
											<table border="none">
												<c:set var="psHeader" value="<font class='label'>Phenotype Summary</font>"/>
												<c:if test="${(marker.countOfAllelesMP > 0) or (marker.countOfOtherPhenotypeAnnotations > 0)}">
													<tr>
														<td class="rightBorderThinGray label padded top right">${psHeader}</td>
														<td class="padded limitWidth">
															<c:if test="${marker.countOfAllelesMP > 0}">
															<a href="${configBean.FEWI_URL}marker/phenotypes/${marker.primaryID}">${marker.countOfAnnotationsMP}</a> phenotype<c:if test="${marker.countOfAnnotationsMP > 1}">s</c:if>
															from ${marker.countOfAllelesMP} allele<c:if test="${marker.countOfAllelesMP > 1}">s</c:if>
															<c:if test="${marker.countOfBackgroundStrains > 0}">in ${marker.countOfBackgroundStrains} genetic background<c:if test="${marker.countOfBackgroundStrains > 1}">s</c:if></c:if>
															<c:if test="${marker.countOfOtherPhenotypeAnnotations > 0}"><br/></c:if>
															</c:if>
															<c:if test="${marker.countOfOtherPhenotypeAnnotations > 0}"><a href="${configBean.FEWI_URL}marker/phenotypes/${marker.primaryID}?multigenic=1">${marker.countOfOtherPhenotypeAnnotations}</a> phenotype<c:if test="${marker.countOfOtherPhenotypeAnnotations > 1}">s</c:if> from multigenic genotypes
															</c:if>
														</td>
													</tr>
													<c:set var="psHeader" value=""/>
												</c:if>
												<c:if test="${marker.countOfPhenotypeImages > 0}">
													<tr>
														<td class="rightBorderThinGray label padded top right">${psHeader}</td>
														<td class="padded"><a href="${configBean.FEWI_URL}image/phenoSummary/marker/${marker.primaryID}">${marker.countOfPhenotypeImages}</a> images</td>
													</tr>
													<c:set var="psHeader" value=""/>
												</c:if>
												<c:if test="${marker.countOfPhenotypeReferences > 0}">
													<tr>
														<td class="rightBorderThinGray label padded top right">${psHeader}</td>
														<td class="padded"><a href="${configBean.FEWI_URL}reference/phenotype/marker/${marker.primaryID}?typeFilter=Literature">${marker.countOfPhenotypeReferences}</a> phenotype reference<c:if test="${marker.countOfPhenotypeReferences > 1}">s</c:if></td>
													</tr>
												</c:if>
											</table>
										</div>
									</c:if>

									<c:if test="${mpHasSlimgrid}">
										<div id="mpSlimgridWrapper" class="sgWrapper">
											<div class="label" style="width: 566px; text-align:center; padding-bottom: 6px;">Phenotype Overview<img id="sgPhenoHelpImage" src="${configBean.WEBSHARE_URL}images/help_icon.png"/></div>
											<div id="sgPhenoHelp" style="visibility: hidden; height:0px;">
												<div class="hd">Phenotype Overview</div>
												<div class="bd" style="text-align: center">
													Blue squares indicate phenotypes directly attributed to mutations/alleles of this gene.
												</div>
											</div>
											<c:set var="sgID" value="mpSlimgrid"/>
											<c:set var="sgCells" value="${marker.slimgridCellsMP}"/>
											<c:set var="sgShowAbbrev" value="true"/>
											<c:set var="sgTooltipTemplate" value="<count> phenotype(s)"/>
											<c:set var="sgUrl" value="${configBean.FEWI_URL}diseasePortal/sgCell?genes=<markerID>&phenotypes=<termID>&termHeader=<abbrev>"/>

											<%@ include file="MarkerDetail_slimgrid.jsp" %>
											<br/>
											<span style="font-size: 90%">Click cells to view annotations.</span>
										</div>
									</c:if>

								<c:if test="${mpHasSummaryDiv or mpHasSlimgrid}">
									</div><!-- mpLeftDiv -->
								</c:if>

								<c:if test="${mpHasAlleleDiv}">
									<div id="alleleCountDiv" style="display: inline-block; vertical-align: top; padding-bottom: 6px;">
										<table border="none">
											<c:if test="${marker.countOfAlleles > 0}">
												<c:set var="alleleUrl" value="${configBean.FEWI_URL}allele/summary?markerId=${marker.primaryID}"/>
												<tr>
													<td class="rightBorderThinGray label padded top right"><font class="label">All Mutations and Alleles</font></td>
													<td class="padded"><a href="${alleleUrl}">${marker.countOfAlleles}</a></td>
												</tr>
												<c:forEach var="item" items="${marker.alleleCountsByType}">
													<tr>
														<td class="rightBorderThinGray label padded top right"><font class="label">${item.countType}</font></td>
														<td class="padded"><a href="${alleleUrl}&alleleType=${item.countType}">${item.count}</a></td>
													</tr>
												</c:forEach>
												<tr style="height: 5px"><td></td><td></td></tr>
											</c:if>
									<c:if test="${empty marker.slimgridCellsMP}">
										</table>
									</div>
									<div style="display: inline-block">
										<table border="none">
									</c:if>

										<c:if test="${marker.countOfMutationInvolves > 0}">
											<tr>
												<td class="rightBorderThinGray label padded top right"><font class="label">Genomic Mutations<br/>Involving ${marker.symbol}</font></td>
												<td class="padded"><a href="${alleleUrl}&mutationInvolves=1">${marker.countOfMutationInvolves}</a></td>
											</tr>
											<tr style="height: 5px"><td></td><td></td></tr>
										</c:if>
										<c:if test="${not empty marker.incidentalMutations}">
											<tr>
												<td class="rightBorderThinGray label padded top right"><font class="label">Incidental Mutations</font></td>
												<td class="padded">
													<c:forEach var="incidentalMutation" items="${marker.incidentalMutations}" varStatus="imStatus">
														<c:if test="${imStatus.index>0}">, </c:if><a href="${configBean.FTP_BASE_URL}datasets/incidental_muts/${incidentalMutation.filename}">${incidentalMutation.filenameNoExtension}</a>
													</c:forEach>
												</td>
											</tr>
										</c:if>
										</table>
									</div>
								</c:if>

								<c:if test="${not empty marker.markerClip}">
									<div id="mpMarkerClip">
										${marker.markerClip}
									</div>
								</c:if>

							</div>
							<div id="closedAlleleRibbon" style="display:none;">
							</div>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</c:if>

