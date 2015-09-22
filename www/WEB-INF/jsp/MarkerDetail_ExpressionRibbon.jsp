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

	<c:set var="gxdHasMouseLinks" value="false"/>
	<c:set var="gxdHasVertebrateLinks" value="false"/>

	<c:if test="${not (empty marker.allenBrainAtlasID.accID and empty marker.gensatID.accID and empty marker.geoID.accID and empty marker.arrayExpressID.accID)}">
		<c:set var="gxdHasMouseLinks" value="true"/>
	</c:if>
	<c:if test="${not (empty marker.expressedInZfinLinks and empty marker.expressedInChickenLinks and empty marker.expressedInXenBaseLinks)}">
		<c:set var="gxdHasVertebrateLinks" value="true"/>
	</c:if>

	<c:if test="${not empty marker.slimgridCellsAnatomy or marker.countOfGxdLiterature > 0 or marker.countOfGxdResults > 0 or marker.countOfCdnaSources > 0 or marker.countOfGxdImages > 0 or marker.countOfGxdResults > 0 or gxdHasMouseLinks or gxdHasVertebrateLinks}">
		<div class="row gxdRibbon" >
			<div class="header <%=leftTdStyles.getNext() %>">
				<div id="gxdHeading">
					Expression
				</div>
				<div id="gxdLogo" style="margin-top: auto; text-align: center;">
					<a href="${configBean.HOMEPAGES_URL}expression.shtml" style="background-color: transparent"> <img id="gxdLogoImage" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" style='width: 90%;'> </a>
				</div>
			</div>
			<div id="gxd" class="detail <%=rightTdStyles.getNext() %>">

				<div id="toggleExpressionRibbon" title="Show More" class="toggleImage hdCollapse"></div>

				<section class="summarySec1 extra">
					<c:if test="${not empty marker.slimgridCellsAnatomy}">
						<div id="anatomySlimgridWrapper" style="display: inline-block;">
							<div class="label" style="width: 566px; text-align:center;">Expression Overview<img id="sgAnatomyHelpImage" src="${configBean.FEWI_URL}assets/images/help_icon_16.png" style="margin-bottom: -3px; margin-left: 3px; cursor: pointer;"/></div><br />
							<div id="sgAnatomyHelp" style="visibility: hidden; height:0px;">
								<div class="hd">Expression Overview</div>
								<div class="bd" style="text-align: left">
									GXD's primary emphasis is on endogenous gene expression during development.<br/>Click on grid cells to view annotations.<br/><ul><li>Blue cells = expressed in wild-type.</li><li>Gray triangles = other expression annotations only<br/>(e.g. absence of expression or data from mutants).</li></ul>
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
				</section>

				<section class="summarySec2 extra">
					<ul>
						<c:if test="${not (empty marker.allenBrainAtlasID.accID and empty marker.gensatID.accID and empty marker.geoID.accID and empty marker.arrayExpressID.accID)}">
							<li>
								<div class="label">Other Mouse Links</div>
								<c:if test="${not empty marker.allenBrainAtlasID.accID}">
									<div class="value"><a href="${fn:replace (externalUrls.Allen_Brain_Atlas, '@@@@', marker.allenBrainAtlasID.accID)}" target="_new">Allen Institute</a></div>
								</c:if>
								<c:if test="${not empty marker.gensatID.accID}">
									 <div class="value"><a href="${fn:replace (externalUrls.GENSAT, '@@@@', marker.gensatID.accID)}" target="_new">GENSAT</a></div>
								</c:if>
								<c:if test="${not empty marker.geoID.accID}">
									 <div class="value"><a href="${fn:replace (externalUrls.GEO, '@@@@', marker.geoID.accID)}" target="_new">GEO</a></div>
								</c:if>
								<c:if test="${not empty marker.arrayExpressID.accID}">
									 <div class="value"><a href="${fn:replace (externalUrls.ArrayExpress, '@@@@', marker.arrayExpressID.accID)}" target="_new">Expression Atlas</a></div>
								</c:if>
							</li>
						</c:if>

						<c:if test="${not (empty marker.expressedInZfinLinks and empty marker.expressedInChickenLinks and empty marker.expressedInXenBaseLinks)}">
							<li>
								<div class="label">Other Vertebrate Links</div>

								<c:if test="${not empty marker.expressedInChickenLinks}">
									<div class="value">GEISHA 
										<c:forEach var="zf" items="${marker.expressedInChickenLinks}" varStatus="status">
											<a href="${zf.url}" target="_new">${zf.displayText}</a><c:if test="${!status.last}">, </c:if>
										</c:forEach>
									</div>
								</c:if>

								<c:if test="${not empty marker.expressedInXenBaseLinks}">
									<div class="value">Xenbase
										<c:forEach var="zf" items="${marker.expressedInXenBaseLinks}" varStatus="status">
											<a href="${zf.url}" target="_new">${zf.displayText}</a><c:if test="${!status.last}">, </c:if>
										</c:forEach>
									</div>
								</c:if>
		
								<c:if test="${not empty marker.expressedInZfinLinks}">
									<div class="value">ZFIN
										<c:forEach var="zf" items="${marker.expressedInZfinLinks}" varStatus="status">
											<a href="${zf.url}" target="_new">${zf.displayText}</a><c:if test="${!status.last}">, </c:if>
										</c:forEach>
									</div>
								</c:if>

							</li>
						</c:if>

					</ul>
				</section>
						

				<section class="summarySec1 wide extra open">
					<c:if test="${marker.countOfGxdResults > 0 or marker.countOfGxdTissues > 0 or marker.countOfCdnaSources > 0 or marker.countOfGxdLiterature > 0}">
						<div class="item">
							<ul>
								<c:if test="${marker.countOfGxdResults > 0}">
									<li>
										<div class="label">All Results</div>
										<div class="value"><a href="${configBean.FEWI_URL}gxd/marker/${marker.primaryID}">${marker.countOfGxdResults}</a></div>
									</li>
								</c:if>

								<c:if test="${marker.countOfGxdTissues > 0}">
									<li>
										<div class="label">Tissues</div>
										<div class="value"><a href="${configBean.FEWI_URL}tissue/marker/${marker.primaryID}">${marker.countOfGxdTissues}</a></div>
									</li>
								</c:if>

								<c:if test="${marker.countOfCdnaSources > 0}">
									<li>
										<div class="label">cDNA Data</div>
										<div class="value"><a href="${configBean.WI_URL}searches/estclone_report.cgi?_Marker_key=${marker.markerKey}&sort=Tissue">${marker.countOfCdnaSources}</a></div>
									</li>
								</c:if>

								<c:if test="${marker.countOfGxdLiterature > 0}">
									<li>
										<div class="label">Literature Summary</div>
										<div class="value"><a href="${configBean.FEWI_URL}gxdlit/marker/${marker.primaryID}">${marker.countOfGxdLiterature}</a></div>
									</li>
								</c:if>
							</ul>
						</div>
					</c:if>

					<c:if test="${(marker.countOfGxdImages > 0) && not empty gxdImage}">
						<div class="item">
							<div id="gxdImageDiv" style="display:inline-block; vertical-align: top; text-align: center; width: 150px; padding-top: 14px;">
								<a href="${configBean.FEWI_URL}gxd/marker/${marker.primaryID}?tab=imagestab" title="View ${marker.countOfGxdImages} expression image<c:if test='${marker.countOfGxdImages > 1}'>s</c:if>">
									<span style="padding-bottom: 3px">Images<br/></span>
									${gxdImage}
								</a>
							</div>
						</div>
					</c:if>

					<c:if test="${marker.countOfGxdResults > 0}">
						<div class="item">
							<div id="gxdMatrixDiv" style="display:inline-block; vertical-align: top; text-align: center; width: 150px; padding-top: 14px;">
								<a href="${configBean.FEWI_URL}gxd/marker/${marker.primaryID}?tab=stagegridtab">Tissue x Stage Matrix<br/><img id="gxdMatrixIcon" style="padding-top: 3px" src="${configBean.FEWI_URL}assets/images/gxd_matrix_icon.png"></a>
							</div>
						</div>
					</c:if>
				</section>


						

			</div>
		</div>
	</c:if>
