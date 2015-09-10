<style>
.sgWrapper { display: inline-block; vertical-align: bottom }
.sgWrapperHeight { height: 188px }
.sgWrapperTitle { text-align: center; width: *; padding-bottom: 28px }
.sgSpacer { display: inline-block; width: 25px }
</style>
	<c:if test="${(marker.countOfGOTerms > 0) or (not empty marker.funcBaseID)}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Gene&nbsp;Ontology<br/>(GO)<br/>Classifications
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				<table>
					<tr>
						<td class="top">
							<span id="toggleGORibbon" title="Show More" class="toggleImage hdCollapse" onclick="toggleRibbon('GORibbon'); return false;"></span>
						</td>
						<td>
							<div id="closedGORibbon" style="display:none;">
							</div>
							<div id="openedGORibbon" style="display:block;">
								<div id="goTopWrapper" style="padding-right: 10px; padding-bottom: 10px">
									<c:if test="${marker.countOfGOTerms > 0}">
										<div id="goLeft" style="display: inline-block">
											<table border="none">
												<tr>
													<td class="rightBorderThinGray label padded top right"><font class="label">All GO Annotations</font></td>
													<td class="padded"><a href="${configBean.FEWI_URL}go/marker/${marker.primaryID}">${marker.countOfGOTerms}</a></td>
												</tr>
												<c:if test="${(not empty marker.countOfGOReferences) and (marker.countOfGOReferences > 0)}">
													<tr>
														<td class="rightBorderThinGray label padded top right"><font class="label">GO References</font></td>
														<td class="padded"><a href="${configBean.FEWI_URL}reference/go/marker/${marker.primaryID}?typeFilter=Literature">${marker.countOfGOReferences}</a></td>
													</tr>
												</c:if>
											</table>
										</div>
									</c:if>
									<c:if test="${not empty marker.funcBaseID}">
										<div id="goRight" style="display: inline-block; vertical-align: top">
											<table border="none">
												<tr>
													<td class="rightBorderThinGray label padded top right"><font class="label">External Resources</font></td>
													<td class="padded"><a href="${fn:replace(urls.FuncBase, '@@@@', marker.funcBaseID.accID)}" target="_blank">FuncBase</a></td>
												</tr>
											</table>
										</div>
									</c:if>
								</div>
					
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
				</table>
			</div>
		</div>
	</c:if>

