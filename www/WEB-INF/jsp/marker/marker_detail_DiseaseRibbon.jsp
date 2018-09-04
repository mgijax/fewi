	<c:if test="${fn:length(MouseDOAnnotations) > 0 or fn:length(HumanDOAnnotations) > 0 or (not empty diseaseRefCount && diseaseRefCount > 0) or (marker.countOfAllelesWithHumanDiseases > 0)}">
		<div class="row diseaseRibbon" id="diseaseRibbon">
			<div class="header <%=leftTdStyles.getNext() %>">
				Human Diseases
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">

				<c:set var="arrowstate" value="hdCollapse" />
				<c:set var="arrowtext" value="less" />
				<c:set var="titletext" value="Show Less" />

				<c:if test="${(fn:length(MouseDOAnnotations) > 0) or (fn:length(HumanDOAnnotations) > 0)}">
					<c:set var="arrowstate" value="hdExpand" />
					<c:set var="arrowtext" value="more" />
					<c:set var="titletext" value="Show More" />
				</c:if>

				<div id="hdToggle" title="${titletext}" class="toggleImage ${arrowstate}">${arrowtext}</div>

				<c:set var="sectionstate" value="open" />

				<c:if test="${(fn:length(MouseDOAnnotations) > 0) or (fn:length(HumanDOAnnotations) > 0)}">
					<section class="summarySec1 wide">
						<ul>
							<li>
								<div class="label">
									Diseases
								</div>
								<div class="value">
									<c:if test="${fn:length(MouseDOAnnotations) > 0}">
										${fn:length(MouseDOAnnotations)} with ${marker.symbol} mouse models<c:if test="${fn:length(HumanDOAnnotations) > 0}">;</c:if>
									</c:if>
									<c:if test="${fn:length(HumanDOAnnotations) > 0}">
										${fn:length(HumanDOAnnotations)} with human ${AllHumanSymbols} associations
									</c:if>
								</div>
							</li>
						</ul>

						<div class="extra closed second">

							<br/>
							<table id="humanDiseaseTable">
								<tbody>
									<tr>
										<th></th>
										<th></th>
										<th></th>
										<th></th>
										<th class="headerStripe td_disease_tbl_hdr" style="text-align: left">Human Disease</th>
										<th class="headerStripe td_disease_tbl_hdr" style="text-align: left">Mouse Models</th>
									</tr>
									<c:forEach var="diseaseRow" items="${DiseaseRows}">
										<c:set var="diseaseTooltip" value="Human and Mouse genes associated with disease"/>
										<c:if test="${diseaseRow.get('type') == 'mouse'}">
											<c:set var="diseaseTooltip" value="Mouse gene associated with disease"/>
										</c:if>
										<c:if test="${diseaseRow.get('type') == 'human'}">
											<c:set var="diseaseTooltip" value="Human gene associated with disease"/>
										</c:if>
										<tr>
											<c:if test="${not empty diseaseRow.get('headerRow')}">
												<td rowspan="${diseaseRow.get('headerRow')}" class='centerMiddle'>
													<nobr>
														<c:if test="${diseaseRow.get('type') == 'human' or diseaseRow.get('type') == 'both'}">
															<img src="${configBean.WEBSHARE_URL}images/man_icon.gif" title="${diseaseTooltip}">
														</c:if>
														<c:if test="${diseaseRow.get('type') == 'mouse' or diseaseRow.get('type') == 'both'}">
															<img src="${configBean.WEBSHARE_URL}images/black_mouse_small.gif" title="${diseaseTooltip}">
														</c:if>
													</nobr>
												</td>
												<td rowspan="${diseaseRow.get('headerRow')}">&nbsp;</td>
												<td rowspan="${diseaseRow.get('headerRow')}" class='topBorder bottomBorder leftBorder'>&nbsp;&nbsp;</td>
												<td rowspan="${diseaseRow.get('headerRow')}">&nbsp;</td>
											</c:if>
											<c:set var="disease_safe_name" value="${fn:replace(diseaseRow.get('diseaseId'), ':', '_')}" />
											<td class="td_disease_tbl">
												<div style="float: left; margin-right: 10px;"><a href="${configBean.FEWI_URL}disease/${diseaseRow.get('diseaseId')}">${diseaseRow.get('diseaseTerm')}</a></div>
												<div style="float: right;"><span id="show_${disease_safe_name}_dialog" class="link">IDs</span></div>
												<%@ include file="marker_detail_disease_popup2.jsp" %>
											</td>
											<td class="td_disease_tbl">
												<c:set var="nonNotCount" value="${fn:length(MouseModels.get(diseaseRow.get('diseaseId')))}" />
												<c:set var="notCount" value="${fn:length(NotMouseModels.get(diseaseRow.get('diseaseId')))}" />
					
												<c:if test="${nonNotCount > 0}">
													<span id="show${disease_safe_name}" class="link">View ${fn:length(MouseModels.get(diseaseRow.get('diseaseId')))}</span> model<c:if test="${fn:length(MouseModels.get(diseaseRow.get('diseaseId'))) > 1}">s</c:if>
													<%@ include file="marker_detail_disease_popup.jsp" %>
												</c:if>
												<c:if test="${nonNotCount == 0 and notCount > 0}">
													<span id="show${disease_safe_name}" class="link">View ${fn:length(NotMouseModels.get(diseaseRow.get('diseaseId')))}</span> "NOT" model<c:if test="${fn:length(NotMouseModels.get(diseaseRow.get('diseaseId'))) > 1}">s</c:if>
													<%@ include file="marker_detail_disease_popup.jsp" %>
												</c:if>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							<div style="padding-top: 6px">
								<font style="font-size: 90%">Click on a disease name to see all genes associated with that disease.</font>
							</div>
							<br/>
						</div>
					</section>
					
					<c:set var="sectionstate" value="closed" />
				</c:if>


				<c:if test="${(not empty diseaseRefCount && diseaseRefCount > 0) or (marker.countOfAllelesWithHumanDiseases > 0)}">
					<c:if test="${marker.countOfAllelesWithHumanDiseases > 0}">
						<section class="summarySec1 extra ${sectionstate}">
							<ul>
								<li>
									<div class="label">Mutations/Alleles</div>
									<div class="value"><a href="${configBean.FEWI_URL}allele/summary?markerId=${marker.primaryID}&hasDO=1" id="diseaseMutationLink">${marker.countOfAllelesWithHumanDiseases}</a> with disease annotations</div>
								</li>
							</ul>
						</section>
					</c:if>
					<c:if test="${not empty diseaseRefCount && diseaseRefCount > 0}">
						<section class="summarySec2 extra ${sectionstate}">
							<ul>
								<li>
									<div class="label">References</div>
									<div class="value"><a href="${configBean.FEWI_URL}reference/diseaseRelevantMarker/${marker.primaryID}" id="diseaseRefLink">${diseaseRefCount}</a> with disease annotations</div>
								</li>
							</ul>
						</section>
					</c:if>
				</c:if>

			</div>
		</div>
	</c:if>
