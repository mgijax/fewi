	<c:if test="${(marker.countOfAlleles > 0) or (not empty marker.markerClip) or (not empty marker.incidentalMutations) or (marker.countOfHumanDiseases > 0) or (marker.countOfAllelesWithHumanDiseases > 0) or (marker.countOfPhenotypeImages > 0) or (marker.countOfPhenotypeReferences > 0) or (marker.countOfOtherPhenotypeAnnotations > 0)}">
		<div class="row phenoRibbon" id="phenotypeRibbon">
			<div class="header <%=leftTdStyles.getNext() %>">
				Mutations,<br/>Alleles, and<br/>Phenotypes
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">

			<div id="muToggle" title="Show Less" class="toggleImage hdCollapse">less</div>

			<c:if test="${marker.countOfAllelesMP > 0 or marker.countOfOtherPhenotypeAnnotations > 0 or marker.countOfPhenotypeImages > 0 or marker.countOfPhenotypeReferences > 0 or not empty marker.slimgridCellsMP or not empty marker.markerClip}">
				<section class="summarySec1 extra open">
					<c:if test="${marker.countOfAllelesMP > 0 or marker.countOfOtherPhenotypeAnnotations > 0 or marker.countOfPhenotypeImages > 0 or marker.countOfPhenotypeReferences > 0}">
						<ul>
							<li>
								<div class="label">
									Phenotype Summary
								</div>
								<div class="value">
									<c:if test="${marker.countOfAllelesMP > 0}">
										<a href="${configBean.FEWI_URL}marker/phenotypes/${marker.primaryID}" id="phenoAnnotationLink">${marker.countOfAnnotationsMP}</a> phenotype<c:if test="${marker.countOfAnnotationsMP > 1}">s</c:if>
										from ${marker.countOfAllelesMP} allele<c:if test="${marker.countOfAllelesMP > 1}">s</c:if>
										<c:if test="${marker.countOfBackgroundStrains > 0}">in ${marker.countOfBackgroundStrains} genetic background<c:if test="${marker.countOfBackgroundStrains > 1}">s</c:if></c:if>
										<br/>
									</c:if>
									<c:if test="${marker.countOfOtherPhenotypeAnnotations > 0}">
										<a href="${configBean.FEWI_URL}marker/phenotypes/${marker.primaryID}?multigenic=1" id="phenoMultigenicLink">${marker.countOfOtherPhenotypeAnnotations}</a> phenotype<c:if test="${marker.countOfOtherPhenotypeAnnotations > 1}">s</c:if> from multigenic genotypes
										<br/>
									</c:if>

									<c:if test="${marker.countOfPhenotypeImages > 0}">
										<a href="${configBean.FEWI_URL}image/phenoSummary/marker/${marker.primaryID}" id="phenoImageLink">${marker.countOfPhenotypeImages}</a> images
										<br/>
									</c:if>
									<c:if test="${marker.countOfPhenotypeReferences > 0}">
										<a href="${configBean.FEWI_URL}reference/phenotype/marker/${marker.primaryID}?typeFilter=Literature" id="phenoRefLink">${marker.countOfPhenotypeReferences}</a> phenotype reference<c:if test="${marker.countOfPhenotypeReferences > 1}">s</c:if>
										<br/>
									</c:if>
								</div>
							</li>
						</ul>
					</c:if>

					<c:if test="${not empty marker.slimgridCellsMP}">
						<div id="mpSlimgridWrapper" class="sgWrapper">
							<div class="label" style="width: 100%; text-align:center;">Phenotype Overview<img id="sgPhenoHelpImage" src="${configBean.FEWI_URL}assets/images/help_icon_16.png" style="margin-bottom: -3px; margin-left: 3px; cursor: pointer;"/></div><br />
							<div id="sgPhenoHelp" style="visibility: hidden;">
								<div class="hd">Phenotype Overview</div>
								<div class="bd" style="text-align: center">
									Blue squares indicate phenotypes directly attributed to mutations/alleles of this gene.
								</div>
							</div>
							<c:set var="sgID" value="mpSlimgrid"/>
							<c:set var="sgCells" value="${marker.slimgridCellsMP}"/>
							<c:set var="sgShowAbbrev" value="true"/>
							<c:set var="sgTooltipTemplate" value="<count> phenotype(s)"/>
							<c:set var="sgUrl" value="${configBean.FEWI_URL}diseasePortal/popup?isPhenotype=true&markerID=<markerID>&header=<abbrev>"/>

							<%@ include file="../shared_slimgrid.jsp" %>
							<br/>
							<span style="font-size: 90%">Click cells to view annotations.</span>
						</div>
					</c:if>

				</section>
			</c:if>

				<section class="summarySec2 extra open">
					<ul>

					<c:if test="${(marker.countOfAlleles > 0) or (not empty marker.incidentalMutations) or (marker.countOfMutationInvolves > 0)}">

						<c:if test="${marker.countOfAlleles > 0}">
							<c:set var="alleleUrl" value="${configBean.FEWI_URL}allele/summary?markerId=${marker.primaryID}"/>
							<li>
								<div class="label">All Mutations and Alleles</div>
								<div class="value">
									<a href="${alleleUrl}" id="phenoMutationLink">${marker.countOfAlleles}</a>
								</div>
							</li>

							<c:forEach var="item" items="${marker.alleleCountsByType}">
								<c:set var="myID" value="${fn:replace(fn:replace(fn:replace(fn:replace(item.countType, '-', ''), '(', ''), ')', ''), ' ', '')}Link"/>
								<li>
									<div class="label unbold">${item.countType}</div>
									<div class="value">
										<a href="${alleleUrl}&alleleType=${item.countType}" id="${myID}">${item.count}</a>
									</div>
								</li>
							</c:forEach>
						</c:if>

						<c:if test="${marker.countOfMutationInvolves > 0}">
							<li>
								<div class="label">Genomic Mutations</div>
								<div class="value">
									<a href="${alleleUrl}&mutationInvolves=1" id="GenomicMutationsLink">${marker.countOfMutationInvolves}</a> involving ${marker.symbol}
								</div>
							</li>
						</c:if>

						<c:if test="${not empty marker.incidentalMutations}">
							<li>
								<div class="label">Incidental Mutations</div>
								<div class="value">
									<c:forEach var="incidentalMutation" items="${marker.incidentalMutations}" varStatus="imStatus">
										<c:if test="${imStatus.index>0}">, </c:if>
										<c:if test="${incidentalMutation.provider != 'CvDC'}">
											<a href="${fn:replace(externalUrls.IncidentalMutations, '@@@@', marker.primaryID)}" target="_blank">${incidentalMutation.provider}</a>
										</c:if>
										<c:if test="${incidentalMutation.provider == 'CvDC'}">
											<a href="${configBean.FTP_URL}datasets/incidental_muts/${incidentalMutation.provider}.xlsx" target="_blank">${incidentalMutation.provider}</a>
										</c:if>
									</c:forEach>
								</div>
							</li>
						</c:if>

						<li>
							<div class="label">Find Mice (IMSR)</div>
							<div class="value">
								<a href="${configBean.IMSRURL}summary?gaccid=${marker.primaryID}" id="imsrLink">${marker.countForImsr} strains or lines available</a> 
							</div>
						</li>

						<c:if test="${marker.hasPhenotypesRelatedToAnatomy and marker.hasWildTypeExpressionData}">
							<li>
								<div class="label">Comparison Matrix</div>
								<div class="value"><a href="${configBean.FEWI_URL}gxd/phenogrid/${marker.primaryID}" target="_new">Gene Expression + Phenotype</a></div>
							</li>
						</c:if>

						<c:if test="${marker.hasWildTypeExpressionData and (marker.countOfRecombinaseAllelesWithActivityData > 0)}">
							<li>
								<div class="label">Recombinase Activity</div>
								<div class="value" title="${marker.symbol} is the driver for ${marker.countOfRecombinaseAllelesWithActivityData} recombinase alleles with activity data."><a href="${configBean.FEWI_URL}gxd/recombinasegrid/${marker.primaryID}" target="_new">Comparison Matrix  Gene Expression + Recombinase Activity</a></div>
							</li>
						</c:if>
					</c:if>

				</section>

				<section class="wide summarySec1 extra open">

					<c:if test="${not empty marker.markerClip}">
						<div id="mpMarkerClip">
							${marker.markerClip}
						</div>
					</c:if>

				</section>

			</div>
		</div>
	</c:if>

