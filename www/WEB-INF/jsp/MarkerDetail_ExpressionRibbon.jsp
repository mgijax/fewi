<style>
#sgAnatomyHelp {
font-family: Verdana,Arial,Helvetica;
font-size: 12px;
font-weight: normal;
}
body.yui-skin-sam .yui-panel .hd, body.yui-skin-sam .yui-ac-hd {
	background: #025 none repeat scroll 0 0;
	color: #fff;
	font-weight: bold;
}
body.yui-skin-sam .yui-panel .bd {
	padding: 10px;
	border-bottom: thin solid black;
}
td.width140 { width : 140px }
</style>
	<!-- expression row -->
	<c:set var="allenID" value="${marker.allenBrainAtlasID.accID}"/>
	<c:set var="gensatID" value="${marker.gensatID.accID}"/>
	<c:set var="geoID" value="${marker.geoID.accID}"/>
	<c:set var="arrayExpressID" value="${marker.arrayExpressID.accID}"/>
	<c:set var="zfinLinks" value="${marker.expressedInZfinLinks}"/>
	<c:set var="geishaLinks" value="${marker.expressedInChickenLinks}"/>
	<c:set var="xenbaseLinks" value="${marker.expressedInXenBaseLinks}"/>

	<c:set var="gxdHasSlimgrid" value="false"/>
	<c:set var="gxdHasMgiData" value="false"/>
	<c:set var="gxdHasImages" value="false"/>
	<c:set var="gxdHasMatrix" value="false"/>
	<c:set var="gxdHasMouseLinks" value="false"/>
	<c:set var="gxdHasVertebrateLinks" value="false"/>

	<c:if test="${not empty marker.slimgridCellsAnatomy}">
		<c:set var="gxdHasSlimgrid" value="true"/>
	</c:if>
	<c:if test="${(marker.countOfGxdLiterature > 0) or (marker.countOfGxdResults > 0) or (marker.countOfCdnaSources > 0)}">
		<c:set var="gxdHasMgiData" value="true"/>
		<c:if test="${marker.countOfGxdResults > 0}">
			<c:set var="gxdHasMatrix" value="true"/>
		</c:if>
	</c:if>
	<c:if test="${marker.countOfGxdImages > 0}">
		<c:set var="gxdHasImages" value="true"/>
	</c:if>
	<c:if test="${not (empty allenID and empty gensatID and empty geoID and empty arrayExpressID)}">
		<c:set var="gxdHasMouseLinks" value="true"/>
	</c:if>
	<c:if test="${not (empty zfinLinks and empty geishaLinks and empty xenbaseLinks)}">
		<c:set var="gxdHasVertebrateLinks" value="true"/>
	</c:if>

	<c:if test="${gxdHasSlimgrid or gxdHasMgiData or gxdHasImages or gxdHasMatrix or gxdHasMouseLinks or gxdHasVertebrateLinks}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				<div id="gxdHeading">
					Expression
				</div>
				<div id="gxdLogo" style="margin-top: auto; text-align: center;">
					<a href="${configBean.HOMEPAGES_URL}expression.shtml" style="background-color: transparent"> <img id="gxdLogoImage" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" style='width: 90%;'> </a>
				</div>
			</div>
			<div id="gxd" class="detail <%=rightTdStyles.getNext() %>">
				<table>
					<tr>
						<td class="top">
							<span id="toggleExpressionRibbon" title="Show More" class="toggleImage hdCollapse" onclick="toggleRibbon('ExpressionRibbon'); showHideById('gxdLogo'); return false;"></span>
						</td>
						<td>
							<div id="closedExpressionRibbon" style="display:none;">
							</div>
							<div id="openedExpressionRibbon" style="display:block;">

								<c:if test="${gxdHasSlimgrid or gxdHasMgiData or gxdHasImages or gxdHasMatrix}">
									<div id="gxdLeftDiv" style="display:inline-block; vertical-align: top">
								</c:if>

									<c:if test="${gxdHasSlimgrid}">
										<div id="anatomySlimgridWrapper">
											<div class="label" style="width: 566px; text-align:center; padding-bottom: 15px">Expression Overview<img id="sgAnatomyHelpImage" src="${configBean.WEBSHARE_URL}images/help_icon.png"/></div>
											<div id="sgAnatomyHelp" style="visibility: hidden; height:0px;">
												<div class="hd">Expression Overview</div>
												<div class="bd" style="text-align: left">
													GXD’s primary emphasis is on endogenous gene expression during development.<br/>Click on grid cells to view annotations.<br/><ul><li>Blue cells = expressed in wild-type.</li><li>Gray triangles = other expression annotations only<br/>(e.g. absence of expression or data from mutants).</li></ul>
												</div>
											</div>
											<c:set var="sgID" value="anatomySlimgrid"/>
											<c:set var="sgCells" value="${marker.slimgridCellsAnatomy}"/>
											<c:set var="sgShowAbbrev" value="true"/>
											<c:set var="sgTooltipTemplate" value="<count> annotation(s)"/>
											<c:set var="sgUrl" value="${configBean.FEWI_URL}gxd/summary?markerMgiId=<markerID>&structureID=<termID>&structure=<term>&tab=stagegridtab"/>

											<%@ include file="MarkerDetail_slimgrid.jsp" %>
											<div style="font-size: 90%">Click cells to view annotations.</div>
										</div>
									</c:if>

									<c:if test="${gxdHasMgiData}">
										<c:set var="gxdDataPad" value=""/>
										<c:if test="${marker.countOfGxdResults > 0}">
											<c:set var="gxdDataPad" value="padding-top: 10px; "/>
										</c:if>
										<div id="gxdMgiData" style="${gxdDataPad}display: inline-block">
											<table border="none">
												<c:if test="${marker.countOfGxdResults > 0}">
													<tr>
														<td class="rightBorderThinGray label padded top right"><font class="label">All Results</font></td>
														<td class="padded"><a href="${configBean.FEWI_URL}gxd/marker/${marker.primaryID}">${marker.countOfGxdResults}</a></td>
													</tr>
												</c:if>

												<c:if test="${marker.countOfGxdTissues > 0}">
													<tr>
														<td class="rightBorderThinGray label padded top right"><font class="label">Tissues</font></td>
														<td class="padded"><a href="${configBean.FEWI_URL}tissue/marker/${marker.primaryID}">${marker.countOfGxdTissues}</a></td>
													</tr>
												</c:if>

												<c:if test="${marker.countOfCdnaSources > 0}">
													<tr>
														<td class="rightBorderThinGray label padded top right"><font class="label">cDNA Data</font></td>
														<td class="padded"><a href="${configBean.WI_URL}searches/estclone_report.cgi?_Marker_key=${marker.markerKey}&sort=Tissue">${marker.countOfCdnaSources}</a></td>
													</tr>
												</c:if>

												<c:if test="${marker.countOfGxdLiterature > 0}">
													<tr>
														<td class="rightBorderThinGray label padded top right"><font class="label">Literature Summary</font></td>
														<td class="padded"><a href="${configBean.FEWI_URL}gxdlit/marker/${marker.primaryID}">${marker.countOfGxdLiterature}</a></td>
													</tr>
												</c:if>
											</table>
										</div><!-- gxdMgiData -->
									</c:if>

									<c:if test="${(marker.countOfGxdImages > 0) && not empty gxdImage}">
										<div id="gxdImageDiv" style="display:inline-block; vertical-align: top; text-align: center; width: 150px; padding-top: 14px;">
											<a href="${configBean.FEWI_URL}gxd/marker/${marker.primaryID}?tab=imagestab" title="View ${marker.countOfGxdImages} expression image<c:if test='${marker.countOfGxdImages > 1}'>s</c:if>">
												<span style="padding-bottom: 3px">Images<br/></span>
												${gxdImage}
											</a>
										</div>
									</c:if>

									<c:if test="${marker.countOfGxdResults > 0}">
										<div id="gxdMatrixDiv" style="display:inline-block; vertical-align: top; text-align: center; width: 150px; padding-top: 14px;">
											<a href="${configBean.FEWI_URL}gxd/marker/${marker.primaryID}?tab=stagegridtab">Tissue x Stage Matrix<br/><img id="gxdMatrixIcon" style="padding-top: 3px" src="${configBean.FEWI_URL}assets/images/gxd_matrix_icon.png"></a>
										</div>
									</c:if>

								<c:if test="${gxdHasSlimgrid or gxdHasMgiData or gxdHasImages or gxdHasMatrix}">
									</div><!-- gxdLeftDiv -->
								</c:if>

								<c:if test="${gxdHasMouseLinks or gxdHasVertebrateLinks}">
									<c:set var="gxdRightDivPad" value=""/>
									<c:if test="${gxdHasSlimgrid}">
										<c:set var="gxdRightDivPad" value="padding-top: 60px; "/>
									</c:if>
									<div id="gxdRightDiv" style="${gxdRightDivPad}display: inline-block; vertical-align: top">
								</c:if>

									<!-- check to see whether the links divs should be horizontal -->
									<c:set var="gxdWideFlag" value=""/>
									<c:if test="${not (gxdHasSlimgrid or gxdHasMatrix or gxdHasImages)}">
										<c:set var="gxdWideFlag" value="display: inline-block; "/>
									</c:if>

									<c:set var="fixedWidth" value=""/>
									<c:if test="${gxdHasMouseLinks and gxdHasVertebrateLinks}">
										<c:set var="fixedWidth" value=" width140"/>
									</c:if>

									<c:if test="${gxdHasMouseLinks}">
										<c:set var="gxdMouseLabel" value="Other Mouse Links"/>
										<div id="gxdMouseLinks" style="${gxdWideFlag}padding-bottom: 6px">
											<table border="none">

												<c:if test="${not empty allenID}">
													<tr>
														<td class="rightBorderThinGray label padded top right${fixedWidth}"><font class="label">${gxdMouseLabel}</font></td>
														<td class="padded"><a href="${fn:replace (externalUrls.Allen_Brain_Atlas, '@@@@', allenID)}" target="_new">Allen Institute</a></td>
													</tr>
													<c:set var="gxdMouseLabel" value=""/>
												</c:if>

												<c:if test="${not empty gensatID}">
													<tr>
														<td class="rightBorderThinGray label padded top right${fixedWidth}"><font class="label">${gxdMouseLabel}</font></td>
														<td class="padded"><a href="${fn:replace (externalUrls.GENSAT, '@@@@', gensatID)}" target="_new">GENSAT</a></td>
													</tr>
													<c:set var="gxdMouseLabel" value=""/>
												</c:if>

												<c:if test="${not empty geoID}">
													<tr>
														<td class="rightBorderThinGray label padded top right${fixedWidth}"><font class="label">${gxdMouseLabel}</font></td>
														<td class="padded"><a href="${fn:replace (externalUrls.GEO, '@@@@', geoID)}" target="_new">GEO</a></td>
													</tr>
													<c:set var="gxdMouseLabel" value=""/>
												</c:if>

												<c:if test="${not empty arrayExpressID}">
													<tr>
														<td class="rightBorderThinGray label padded top right${fixedWidth}"><font class="label">${gxdMouseLabel}</font></td>
														<td class="padded"><a href="${fn:replace (externalUrls.ArrayExpress, '@@@@', arrayExpressID)}" target="_new">Expression Atlas</a></td>
													</tr>
												</c:if>
											</table>
										</div>
									</c:if>

									<c:if test="${gxdHasVertebrateLinks}">
										<c:set var="gxdVertebrateLabel" value="Other Vertebrate Links"/>
										<div id="gxdVertebrateLinks" style="${gxdWideFlag}vertical-align: top">
											<table border="none">

												<c:if test="${not empty geishaLinks}">
													<tr>
														<td class="rightBorderThinGray label padded top right${fixedWidth}"><font class="label">${gxdVertebrateLabel}</font></td>
														<td class="padded">GEISHA 
															<c:forEach var="zf" items="${geishaLinks}" varStatus="status">
																<a href="${zf.url}" target="_new">${zf.displayText}</a><c:if test="${!status.last}">, </c:if>
															</c:forEach>
															<c:set var="gxdVertebrateLabel" value=""/>
														</td>
													</tr>
												</c:if>

												<c:if test="${not empty xenbaseLinks}">
													<tr>
														<td class="rightBorderThinGray label padded top right${fixedWidth}"><font class="label">${gxdVertebrateLabel}</font></td>
														<td class="padded">Xenbase
															<c:forEach var="zf" items="${xenbaseLinks}" varStatus="status">
																<a href="${zf.url}" target="_new">${zf.displayText}</a><c:if test="${!status.last}">, </c:if>
															</c:forEach>
															<c:set var="gxdVertebrateLabel" value=""/>
														</td>
													</tr>
												</c:if>
						
												<c:if test="${not empty zfinLinks}">
														<tr>
															<td class="rightBorderThinGray label padded top right${fixedWidth}"><font class="label">${gxdVertebrateLabel}</font></td>
															<td class="padded">ZFIN
																<c:forEach var="zf" items="${zfinLinks}" varStatus="status">
																	<a href="${zf.url}" target="_new">${zf.displayText}</a><c:if test="${!status.last}">, </c:if>
																</c:forEach>
															</td>
														</tr>
												</c:if>
											</table>
										</div>
									</c:if>

								<c:if test="${gxdHasMouseLinks or gxdHasVertebrateLinks}">
									</div><!-- gxdRightDiv -->
								</c:if>
							</div>
						</td>
					</tr>
				</table>
			</div><!-- gxd -->
		</div><!-- row -->
	</c:if>
	
