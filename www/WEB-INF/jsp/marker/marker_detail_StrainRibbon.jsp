<c:set var="snpsfound" value="false"/>
<c:set var="polymorphismsfound" value="false"/>
<c:forEach var="item" items="${marker.polymorphismCountsByType}" varStatus="status">
	<c:if test="${(fn:startsWith(item.countType, 'SNP')) and (item.count > 0)}">
		<c:set var="snpsfound" value="true"/>
	</c:if>
	<c:if test="${((item.countType == 'PCR') or (item.countType == 'RFLP')) and (item.count > 0)}">
		<c:set var="polymorphismsfound" value="true"/>
	</c:if>
</c:forEach>

<c:set var="hasStrainMarkers" value="${marker.annotatedStrainMarkerCount > 0}"/>
<c:set var="hasCoords" value="${not empty marker.preferredCoordinates}"/>

<c:if test="${snpsfound or hasStrainMarkers or polymorphismsfound or hasCoords or (not empty strainSpecificNote)}">
	<div class="row locationRibbon" id="strainRibbon">
		<div class="header <%=leftTdStyles.getNext() %>">
			Strain<br/>Comparison<br/>
			<div style="float:right;">
				<img src="${configBean.FEWI_URL}assets/images/mice_transparent.png" style="height:28px"/>
			</div>
			<div style="float:right; margin-right: 5px">
				<img src="${configBean.WEBSHARE_URL}images/new_icon.png"/>
			</div>
		</div>
		<div class="detail <%=rightTdStyles.getNext() %>">
			<div title="Show More" class="toggleImage hdExpand">more</div>

			<section class="summarySec1">
				<ul>
					<c:if test="${hasStrainMarkers}">
						<li class="${extrastate}">
							<div class="label">
								Strain Annotations
							</div>
							<div class="value">
								${marker.annotatedStrainMarkerCount}
							</div>
						</li>
					</c:if>

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
				
					<c:if test="${polymorphismsfound}">
						<c:forEach var="item" items="${marker.polymorphismCountsByType}" varStatus="status">
							<c:if test="${(item.countType == 'PCR') or (item.countType == 'RFLP')}">
								<li class="extra closed">
									<c:set var="polyUrl" value="${configBean.FEWI_URL}marker/polymorphisms/${fn:toLowerCase(item.countType)}/${marker.primaryID}"/>
									<div class="label" style="white-space: normal;">${item.countType}</div>
									<div class="value"><a href="${polyUrl}" id="${item.countType}Link">${item.count}</a></div>
								</li>
							</c:if>
						</c:forEach>
					</c:if>
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

			<c:if test="${hasCoords}">
				<fmt:formatNumber value="${marker.preferredCoordinates.startCoordinate}" pattern="#0" var="startCoord"/>
				<fmt:formatNumber value="${marker.preferredCoordinates.endCoordinate}" pattern="#0" var="endCoord"/>
				<c:set var="chromosome" value="${marker.preferredCoordinates.chromosome}"/>
			</c:if>

			<c:if test="${not (empty chromosome or empty startCoord or empty endCoord)}">
				<fmt:formatNumber value="${marker.preferredCoordinates.startCoordinate - 50000}" pattern="#0" var="startCoordWithFlank"/>
				<fmt:formatNumber value="${marker.preferredCoordinates.endCoordinate + 50000}" pattern="#0" var="endCoordWithFlank"/>
				<li class="extra closed">
					<div class="value" style="margin-left: 16.5em;">
						<div id="mgvDiv">
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
</c:if>

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