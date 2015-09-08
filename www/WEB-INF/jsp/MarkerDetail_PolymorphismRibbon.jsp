	<c:if test="${not empty marker.polymorphismCountsByType}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Polymorphisms
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				<table>
					<tr>
						<td class="top">
							<span id="togglePolymorphismRibbon" title="Show More" class="toggleImage hdCollapse" onclick="toggleRibbon('PolymorphismRibbon'); return false;"></span>
						</td>
						<td>
							<div id="closedPolymorphismRibbon" style="display:none;">
							</div>
							<div id="openedPolymorphismRibbon" style="display:block;">
								<c:forEach var="item" items="${marker.polymorphismCountsByType}" varStatus="status">
									<c:set var="countText" value="${item.countType}"/>
									<c:set var="polyUrl" value="${configBean.WI_URL}searches/polymorphism_report.cgi?_Marker_key=${marker.markerKey}"/>
									<c:set var="polyExtra" value=""/>
									<c:if test="${(item.countType == 'PCR') or (item.countType == 'RFLP')}">
										<c:set var="polyUrl" value="${polyUrl}&search=${item.countType}"/>
									</c:if>
									<c:set var="isSnp" value=""/>
									<c:set var="pad" value=""/>
									<c:if test="${fn:startsWith(item.countType, 'SNP')}">
										<c:choose>
											<c:when test="${configBean.BUILDS_IN_SYNC == '0'}">
												<c:set var="countText" value="SNPs"/>
											</c:when>
											<c:otherwise>
												<c:set var="countText" value="${item.countType}"/>
											</c:otherwise>
										</c:choose>

										<c:set var="isSnp" value="1"/>
										<c:set var="polyUrl" value="${configBean.WI_URL}searches/snp_report.cgi?_Marker_key=${marker.markerKey}"/>
										<c:if test="${not empty configBean.SNP_BUILD}">
											<c:set var="polyExtra" value=" from ${configBean.SNP_BUILD}"/>
										</c:if>
										<c:if test="${fn:contains(item.countType, 'multiple')}">
											<c:set var="polyUrl" value="${polyUrl}&includeMultiples=1"/>
											<c:set var="polyExtra" value=""/>
											<c:set var="pad" value="&nbsp;&nbsp;&nbsp;"/>
										</c:if>
									</c:if>
									${pad}${countText}(<a href="${polyUrl}">${item.count}</a>${polyExtra})
									<c:if test="${status.first and (empty isSnp)}">: </c:if>
								</c:forEach>
							</div>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</c:if>


