	<c:if test="${(marker.countOfAlleles > 0) or (not empty marker.markerClip) or (not empty marker.incidentalMutations) or (marker.countOfHumanDiseases > 0) or (marker.countOfAllelesWithHumanDiseases > 0) or (marker.countOfPhenotypeImages > 0) or (marker.countOfPhenotypeReferences > 0) or (marker.countOfOtherPhenotypeAnnotations > 0)}">
		<div class="row phenoRibbon" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Mutations,<br/>Alleles, and<br/>Phenotypes
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">

			<div id="toggleAlleleRibbon" title="Show More" class="toggleImage hdCollapse"></div>

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
										<a href="${configBean.FEWI_URL}marker/phenotypes/${marker.primaryID}">${marker.countOfAnnotationsMP}</a> phenotype<c:if test="${marker.countOfAnnotationsMP > 1}">s</c:if>
										from ${marker.countOfAllelesMP} allele<c:if test="${marker.countOfAllelesMP > 1}">s</c:if>
										<c:if test="${marker.countOfBackgroundStrains > 0}">in ${marker.countOfBackgroundStrains} genetic background<c:if test="${marker.countOfBackgroundStrains > 1}">s</c:if></c:if>
										<br/>
									</c:if>
									<c:if test="${marker.countOfOtherPhenotypeAnnotations > 0}">
										<a href="${configBean.FEWI_URL}marker/phenotypes/${marker.primaryID}?multigenic=1">${marker.countOfOtherPhenotypeAnnotations}</a> phenotype<c:if test="${marker.countOfOtherPhenotypeAnnotations > 1}">s</c:if> from multigenic genotypes
										<br/>
									</c:if>

									<c:if test="${marker.countOfPhenotypeImages > 0}">
										<a href="${configBean.FEWI_URL}image/phenoSummary/marker/${marker.primaryID}">${marker.countOfPhenotypeImages}</a> images
										<br/>
									</c:if>
									<c:if test="${marker.countOfPhenotypeReferences > 0}">
										<a href="${configBean.FEWI_URL}reference/phenotype/marker/${marker.primaryID}?typeFilter=Literature">${marker.countOfPhenotypeReferences}</a> phenotype reference<c:if test="${marker.countOfPhenotypeReferences > 1}">s</c:if>
										<br/>
									</c:if>
								</div>
							</li>
						</ul>
					</c:if>

					<c:if test="${not empty marker.slimgridCellsMP}">
						<div id="mpSlimgridWrapper" class="sgWrapper">
							<div class="label" style="width: 100%; text-align:center;">Phenotype Overview<img id="sgPhenoHelpImage" src="${configBean.FEWI_URL}assets/images/help_icon_16.png" style="margin-bottom: -3px; margin-left: 3px; cursor: pointer;"/></div><br />
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
									<a href="${alleleUrl}">${marker.countOfAlleles}</a>
								</div>
							</li>

							<c:forEach var="item" items="${marker.alleleCountsByType}">
								<li>
									<div class="label unbold">${item.countType}</div>
									<div class="value">
										<a href="${alleleUrl}&alleleType=${item.countType}">${item.count}</a>
									</div>
								</li>
							</c:forEach>
						</c:if>

						<c:if test="${marker.countOfMutationInvolves > 0}">
							<li>
								<div class="label">Genomic Mutations</div>
								<div class="value">
									<a href="${alleleUrl}&mutationInvolves=1">${marker.countOfMutationInvolves}</a> involving ${marker.symbol}
								</div>
							</li>
						</c:if>

						<c:if test="${not empty marker.incidentalMutations}">
							<li>
								<div class="label">Incidental Mutations</div>
								<div class="value">
									<c:forEach var="incidentalMutation" items="${marker.incidentalMutations}" varStatus="imStatus">
										<c:if test="${imStatus.index>0}">, </c:if><a href="${configBean.FTP_BASE_URL}datasets/incidental_muts/${incidentalMutation.filename}">${incidentalMutation.filenameNoExtension}</a>
									</c:forEach>
								</div>
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

