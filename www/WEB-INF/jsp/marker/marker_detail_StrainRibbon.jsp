	<div class="row locationRibbon" id="strainRibbon">
		<div class="header <%=leftTdStyles.getNext() %>">
			Strain<br/>Comparison
		</div>
		<div class="detail <%=rightTdStyles.getNext() %>">

			<c:set var="showJBrowser" value="${not empty marker.preferredCoordinates or not empty jbrowseUrl}" />
			<c:set var="showDownloadSequence" value="${not empty marker.preferredCoordinates}" />
			<c:set var="showGenomeBrowserLinks" value="${not (empty ensemblGenomeBrowserUrl and empty ucscGenomeBrowserUrl and empty ncbiMapViewerUrl)}" />

			<c:set var="showGeneticMap" value="${(not empty marker.preferredCentimorgans) or (not empty marker.preferredCytoband) or (marker.countOfMappingExperiments > 0) or (not empty qtlIDs) or (not empty marker.aliases)}" />
			
			<c:set var="geneticMapExtra" value="${not empty qtlIDs or marker.countOfMappingExperiments > 0 or not empty marker.aliases or hasMiniMap}" />
			<c:set var="showLocationNote" value="${not empty marker.locationNote}" />

			<c:set var="arrowstate" value="hdExpand" />
			<c:set var="extrastate" value="" />
			<c:set var="arrowtext" value="more" />
			<c:set var="titletext" value="Show More" />
			<c:if test="${not (showLocationNote or showJBrowser or showDownloadSequence or showGenomeBrowserLinks or geneticMapExtra)}">
				<c:set var="arrowstate" value="hdCollapse" />
				<c:set var="extrastate" value="extra" />
				<c:set var="arrowtext" value="less" />
				<c:set var="titletext" value="Show Less" />
			</c:if>

			<div title="${titletext}" class="toggleImage ${arrowstate}">${arrowtext}</div>

			<section class="summarySec1">
				<ul>
					<li class="${extrastate}">
						<div class="label">
							Strain Annotations
						</div>
						<div class="value">
							${marker.annotatedStrainMarkerCount}
						</div>
					</li>

					<c:set var="snpsfound" value="false"/>
					<c:forEach var="item" items="${marker.polymorphismCountsByType}" varStatus="status">
						<c:if test="${(fn:startsWith(item.countType, 'SNP')) and (item.count > 0)}">
							<c:set var="snpsfound" value="true"/>
						</c:if>
					</c:forEach>

					<c:if test="${snpsfound}">
						<c:forEach var="item" items="${marker.polymorphismCountsByType}" varStatus="status">
							<c:if test="${(fn:startsWith(item.countType, 'SNP')) and (item.count > 0)}">
								<li class="${extrastate}">
								<c:set var="polyUrl" value="${configBean.FEWI_URL}snp/marker/${marker.primaryID}"/>
									<div class="label" style="white-space: normal;">${item.countType}</div>
									<div class="value"><a href="${polyUrl}" id="snpLink">${item.count}</a>
										<c:if test="${not empty snpBuildNumber}"><span style="font-size: smaller; font-weight: normal;">from ${snpBuildNumber}</span></c:if>
									</div>
								</li>
							</c:if>
						</c:forEach>
					</c:if>
				
					<c:forEach var="item" items="${marker.polymorphismCountsByType}" varStatus="status">
						<c:if test="${(item.countType == 'PCR') or (item.countType == 'RFLP')}">
							<li class="${extrastate}">
								<c:set var="polyUrl" value="${configBean.FEWI_URL}marker/polymorphisms/${fn:toLowerCase(item.countType)}/${marker.primaryID}"/>
								<div class="label" style="white-space: normal;">${item.countType}</div>
								<div class="value"><a href="${polyUrl}" id="${item.countType}Link">${item.count}</a></div>
							</li>
						</c:if>
					</c:forEach>
				</ul>
			</section>
			<section class="summarySec1">
				<ul>
					<li class="${extrastate}">
						<c:if test="${not empty strainSpecificNote}">
							<div class="value">
							<nobr><a onClick="return overlib('${strainSpecificNote}', STICKY, CAPTION, 'Strain-Specific Marker', ANCHOR, 'mice', ANCHORALIGN, 'BL', 'BR', WIDTH, 400, CLOSECLICK, CLOSETEXT, 'Close X');" href="#"><img style="position: relative; top: 7px;" src="${configBean.WEBSHARE_URL}images/mice.jpg" height="25" width="25" id="mice" border="0"></a>
							<a onClick="return overlib('${strainSpecificNote}', STICKY, CAPTION, 'Strain-Specific Marker', ANCHOR, 'mice', ANCHORALIGN, 'BL', 'BR', WIDTH, 400, CLOSECLICK, CLOSETEXT, 'Close X');" href="#" class="markerNoteButton" style='display:inline;'>Strain-Specific Marker</a></nobr>
							</div>
						</c:if>
					</li>
				</ul>
			</section>

			<c:if test="${not (empty marker.preferredCoordinates and empty ensemblGenomeBrowserUrl and empty ucscGenomeBrowserUrl and empty gbrowseUrl and empty jbrowseUrl)}">
					<fmt:formatNumber value="${marker.preferredCoordinates.startCoordinate}" pattern="#0" var="startCoord"/>
					<fmt:formatNumber value="${marker.preferredCoordinates.endCoordinate}" pattern="#0" var="endCoord"/>
					<c:set var="chromosome" value="${marker.preferredCoordinates.chromosome}"/>
			</c:if>

					<c:if test="${not (empty chromosome or empty startCoord or empty endCoord)}">
						<fmt:formatNumber value="${marker.preferredCoordinates.startCoordinate - 50000}" pattern="#0" var="startCoordWithFlank"/>
						<fmt:formatNumber value="${marker.preferredCoordinates.endCoordinate + 50000}" pattern="#0" var="endCoordWithFlank"/>
						<li class="extra closed">
							<div class="value" style="font-size: smaller; margin-left: 16.5em;">
								<div style="float:left; margin-right: 5px">
									<img src="${configBean.WEBSHARE_URL}images/new_icon.png"/>
								</div>
								<div style="padding-top:5px; font-size: 1.2em;">
									<a href="${externalUrls.MGV}#ref=C57BL/6J&genomes=${externalUrls.MGV_Strains}&chr=${chromosome}&start=${startCoordWithFlank}&end=${endCoordWithFlank}&highlight=${marker.primaryID}" target="_blank" id="mgvLink">
									Multiple Genome Viewer (MGV)
									</a>
								</div>
							</div>
						</li>
					</c:if>

			<c:if test="${not empty marker.strainMarkers}">

				<div class="extra closed">
				<form name="strainMarkerForm" method="GET" action="${configBean.SEQFETCH_URL}" target="_blank">
				<div id="strainGenesTableButtons">
					<input type="submit" class="sgButton" value="Get FASTA" />
					<input type="button" class="sgButton" value="Check All" onClick="clickAllStrainGenes()"/>
					<input type="reset" class="sgButton" value="Uncheck All" />
				</div>
				<div id="strainGenesTableDiv">
				<table class="padded" id="table_strainMarkers">
					
					<tr class="headerStripe">
					  <th>Strain</th>
					  <th>Gene Model ID</th>
					  <th>Feature Type</th>
					  <th>Coordinates</th>
					  <th>Downloads</th>
					</tr>
					
					<c:forEach var="sm" items="${marker.strainMarkers}">
						<c:set var="sgID" value=""/>
						<tr>
							<td>
							  <a href="${configBean.FEWI_URL}strain/${sm.strainID}">${sm.strainName}</a>
							</td>
							<td>
								<c:if test="${sm.noAnnotation}">
									no annotation
								</c:if>
								<c:if test="${not sm.noAnnotation}">
									<c:forEach var="gm" items="${sm.preferredGeneModels}">
									  <a href="${configBean.FEWI_URL}sequence/${gm.geneModelID}" target="_blank">${gm.geneModelID}</a></br>
									  <c:if test="${empty sgID}">
									  	<c:set var="sgID" value="${gm.geneModelID}"/>
								  	  </c:if>
									</c:forEach>
								</c:if>
							</td>
							<td>${sm.featureType}</td>
							<td>${sm.location}</td>
							<td class="sgCenter">
							  <c:if test="${not empty sgID}">
								<input type="checkbox" name="seqs" value="straingene!${sgID}!!!!!" class="sgCheckbox" />
							  </c:if>
							</td>
						</tr>
					</c:forEach> 

				</table>
				</div>
				</form>
				</div>

			</c:if>

		</div>
	</div>

<style>
#strainGenesTableDiv {
}
#strainGenesTableButtons {
	text-align: center;
	margin-left: 15px;
	width: 779px;
}
.sgButton {
	width: 90px;
	padding: 3px;
	margin: 3px;
}
</style>
<script>
	function clickAllStrainGenes() {
		// for all checkboxes on the strainMarkerForm that aren't checked, click them
		$('[name=strainMarkerForm] [type=checkbox]:not(:checked)').click();
	}
	// add centering of download checkboxes
	$('.sgCenter').css({'text-align' : 'center'});
</script>