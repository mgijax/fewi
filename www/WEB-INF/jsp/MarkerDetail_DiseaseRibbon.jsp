	<c:if test="${fn:length(MouseOMIMAnnotations) > 0 or fn:length(HumanOMIMAnnotations) > 0 or (not empty diseaseRefCount && diseaseRefCount > 0) or (marker.countOfAllelesWithHumanDiseases > 0)}">
		<div class="row diseaseRibbon" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Human Diseases
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">

				<div id="toggleDiseaseRibbon" title="Show More" class="toggleImage hdExpand"></div>
				<section class="summarySec1 wide">
					<ul>

						<c:choose>
							<c:when test="${(fn:length(MouseOMIMAnnotations) > 0) or (fn:length(HumanOMIMAnnotations) > 0)}">
								<li>
									<div class="label">
										Diseases
									</div>
									<div class="value">
										<c:if test="${fn:length(MouseOMIMAnnotations) > 0}">
											${fn:length(MouseOMIMAnnotations)} with ${marker.symbol} mouse models<c:if test="${fn:length(HumanOMIMAnnotations) > 0}">;</c:if>
										</c:if>
										<c:if test="${fn:length(HumanOMIMAnnotations) > 0}">
											${fn:length(HumanOMIMAnnotations)} with human ${AllHumanSymbols} associations
										</c:if>
									</div>
								</li>

							</c:when>
							<c:otherwise>
								<c:if test="${(not empty diseaseRefCount && diseaseRefCount > 0) or (marker.countOfAllelesWithHumanDiseases > 0)}">
									<div class="value">
										<table border="none">
											<tr>
												<c:if test="${marker.countOfAllelesWithHumanDiseases > 0}">
													<td class="rightBorderThinGray label padded top right"><font class="label">Mutations/Alleles</font></td>
													<td class="padded" style="padding-right: 30px;"><a href="${configBean.FEWI_URL}allele/summary?markerId=${marker.primaryID}&hasOMIM=1">${marker.countOfAllelesWithHumanDiseases}</a> with disease annotations</td>
												</c:if>
												<c:if test="${not empty diseaseRefCount && diseaseRefCount > 0}">
													<td class="rightBorderThinGray label padded top right"><font class="label">References</font></td>
													<td class="padded"><a href="${configBean.FEWI_URL}reference/diseaseRelevantMarker/${marker.primaryID}">${diseaseRefCount}</a> with disease annotations</td>
												</c:if>
											</tr>
										</table>
									</div>
								</c:if>
							</c:otherwise>
						</c:choose>

					</ul>

					<div class="extra closed second">

						<c:if test="${fn:length(MouseOMIMAnnotations) > 0 or fn:length(HumanOMIMAnnotations) > 0}">
							<br/>
							<table>
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
											<td class="td_disease_tbl">
												<a href="${configBean.FEWI_URL}disease/${diseaseRow.get('diseaseId')}">${diseaseRow.get('diseaseTerm')}</a>&nbsp;&nbsp;&nbsp;<span style="font-size: smaller;">OMIM: <a class="MP" href='http://www.omim.org/entry/${diseaseRow.get('diseaseId')}' target="_blank">${diseaseRow.get('diseaseId')}</a></span>
											</td>
											<td class="td_disease_tbl">
												<c:set var="nonNotCount" value="${fn:length(MouseModels.get(diseaseRow.get('diseaseId')))}" />
												<c:set var="notCount" value="${fn:length(NotMouseModels.get(diseaseRow.get('diseaseId')))}" />
					
												<c:if test="${nonNotCount > 0}">
													<span id="show${diseaseRow.get('diseaseId')}" class="link">View ${fn:length(MouseModels.get(diseaseRow.get('diseaseId')))}</span> model<c:if test="${fn:length(MouseModels.get(diseaseRow.get('diseaseId'))) > 1}">s</c:if>
													<%@ include file="MarkerDetail_disease_popup.jsp" %>
												</c:if>
												<c:if test="${nonNotCount == 0 and notCount > 0}">
													<span id="show${diseaseRow.get('diseaseId')}" class="link">View ${fn:length(NotMouseModels.get(diseaseRow.get('diseaseId')))}</span> "NOT" model<c:if test="${fn:length(NotMouseModels.get(diseaseRow.get('diseaseId'))) > 1}">s</c:if>
													<%@ include file="MarkerDetail_disease_popup.jsp" %>
												</c:if>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							<div style="padding-top: 6px">
								<font style="font-size: 90%">Click on a disease name to see all genes associated with that disease.</font>
							</div>
						</c:if>

						<c:if test="${(not empty diseaseRefCount && diseaseRefCount > 0) or (marker.countOfAllelesWithHumanDiseases > 0)}">
							<c:if test="${(fn:length(MouseOMIMAnnotations) > 0) or (fn:length(HumanOMIMAnnotations) > 0)}">
								<br/>
							</c:if>
							<table border="none">
								<tr>
									<c:if test="${marker.countOfAllelesWithHumanDiseases > 0}">
										<td class="rightBorderThinGray label padded top right"><font class="label">Mutations/Alleles</font></td>
										<td class="padded" style="padding-right: 30px;"><a href="${configBean.FEWI_URL}allele/summary?markerId=${marker.primaryID}&hasOMIM=1">${marker.countOfAllelesWithHumanDiseases}</a> with disease annotations</td>
									</c:if>
									<c:if test="${not empty diseaseRefCount && diseaseRefCount > 0}">
										<td class="rightBorderThinGray label padded top right"><font class="label">References</font></td>
										<td class="padded"><a href="${configBean.FEWI_URL}reference/diseaseRelevantMarker/${marker.primaryID}?typeFilter=Literature">${diseaseRefCount}</a> with disease annotations</td>
									</c:if>
								</tr>
							</table>
						</c:if>

					</div>
				</section>

			</div>
		</div>
	</c:if>
