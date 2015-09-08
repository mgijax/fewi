							<c:choose>
								<c:when test="${(fn:length(MouseOMIMAnnotations) > 0) or (fn:length(HumanOMIMAnnotations) > 0)}">
									<div id="diseaseLeftDiv" style="display: inline-block">
										<table border="none">
											<tr>
												<td class="rightBorderThinGray label padded top right"><font class="label">Diseases</font></td>
												<td class="padded">
													<c:if test="${fn:length(MouseOMIMAnnotations) > 0}">
														${fn:length(MouseOMIMAnnotations)} with ${marker.symbol} mouse models<c:if test="${fn:length(HumanOMIMAnnotations) > 0}">;</c:if>
													</c:if>
													<c:if test="${fn:length(HumanOMIMAnnotations) > 0}">
														${fn:length(HumanOMIMAnnotations)} with human ${AllHumanSymbols} associations
													</c:if>
												</td>
											</tr>
										</table>
									</div>
								</c:when>
								<c:otherwise>
									<c:if test="${(not empty diseaseRefCount && diseaseRefCount > 0) or (marker.countOfAllelesWithHumanDiseases > 0)}">
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
									</c:if>
								</c:otherwise>
							</c:choose>
