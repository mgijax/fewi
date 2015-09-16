
	<c:if test="${(marker.countOfGOTerms > 0) or (not empty marker.funcBaseID)}">
		<div class="row goRibbon" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Gene&nbsp;Ontology<br/>(GO)<br/>Classifications
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">

				<section class="summarySec1 wide">

					<ul>

						<c:if test="${(not empty marker.countOfGOReferences) and (marker.countOfGOReferences > 0)}">
							<li style="padding-right: 4em;">
								<div class="label">
									<div id="toggleGORibbon" title="Show More" class="toggleImage hdCollapse"></div>
									GO References
								</div>
								<div class="value"><a href="${configBean.FEWI_URL}reference/go/marker/${marker.primaryID}?typeFilter=Literature">${marker.countOfGOReferences}</a></div>
							</li>
						</c:if>

						<c:if test="${marker.countOfGOTerms > 0}">
							<li>
								<div class="label">
									All GO Annotations
								</div>
								<div class="value"><a href="${configBean.FEWI_URL}go/marker/${marker.primaryID}">${marker.countOfGOTerms}</a></div>
							</li>
						</c:if>
						<c:if test="${not empty marker.funcBaseID}">
							<li>
								<div class="label">
									External Resources
								</div>
								<div class="value"><a href="${fn:replace(urls.FuncBase, '@@@@', marker.funcBaseID.accID)}" target="_blank">FuncBase</a></div>
							</li>
						</c:if>
					</ul>
				</section>

				<div class="extra open goGrids">
					<section class="summarySec1 wide">
						<table>
							<tbody>
								<tr>
									<td class="top"></td>
									<td>
										<div id="openedGORibbon">
											<div id="goTopWrapper"></div>
											<style>
												.sgWrapper { display: inline-block; vertical-align: bottom }
												.sgWrapperHeight { height: 188px }
												.sgWrapperTitle { text-align: center; width: *; padding-bottom: 28px }
												.sgSpacer { display: inline-block; width: 25px }
											</style>

											<c:if test="${not (empty marker.slimgridCellsFunction and empty marker.slimgridCellsProcess and empty marker.slimgridCellsComponent)}">
												<div id="goSlimgridWrapper">
													<div id="mfSlimgridWrapper" class="sgWrapper sgWrapperHeight">
														<div class="label sgWrapperTitle">Molecular Function</div><br/>
														<c:set var="sgID" value="mfSlimgrid"/>
														<c:set var="sgCells" value="${marker.slimgridCellsFunction}"/>
														<c:set var="sgShowAbbrev" value="true"/>
														<c:set var="sgTooltipTemplate" value="<count> annotation(s)"/>
														<c:set var="sgUrl" value="${configBean.FEWI_URL}go/marker/<markerID>?header=<term>"/>
														<%@ include file="MarkerDetail_slimgrid.jsp" %>
													</div>
													<div class="sgSpacer"></div>
													<div id="bpSlimgridWrapper" class="sgWrapper sgWrapperHeight">
														<div class="label sgWrapperTitle">Biological Process</div><br/>
														<c:set var="sgID" value="bpSlimgrid"/>
														<c:set var="sgCells" value="${marker.slimgridCellsProcess}"/>
														<c:set var="sgShowAbbrev" value="true"/>
														<c:set var="sgTooltipTemplate" value="<count> annotation(s)"/>
														<%@ include file="MarkerDetail_slimgrid.jsp" %>
													</div>
													<div class="sgSpacer"></div>
													<div id="ccSlimgridWrapper" class="sgWrapper sgWrapperHeight">
														<div class="label sgWrapperTitle">Cellular Component</div><br/>
														<c:set var="sgID" value="ccSlimgrid"/>
														<c:set var="sgCells" value="${marker.slimgridCellsComponent}"/>
														<c:set var="sgShowAbbrev" value="true"/>
														<c:set var="sgTooltipTemplate" value="<count> annotation(s)"/>
														<%@ include file="MarkerDetail_slimgrid.jsp" %>
													</div>
													<div style="font-size: 90%">Click cells to view annotations.</div>
												</div>
											</c:if>
										</div>
									</td>
								</tr>
							</tbody>
						</table>
					</section>
				</div>
			</div>
		</div>
	</c:if>

