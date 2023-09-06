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

<c:if test="${hasCoords}">
	<fmt:formatNumber value="${marker.preferredCoordinates.startCoordinate}" pattern="#0" var="startCoord"/>
	<fmt:formatNumber value="${marker.preferredCoordinates.endCoordinate}" pattern="#0" var="endCoord"/>
	<c:set var="chromosome" value="${marker.preferredCoordinates.chromosome}"/>
</c:if>

<c:set var="hasMgvLink" value="false"/>
<c:if test="${not (empty chromosome or empty startCoord or empty endCoord)}">
	<fmt:formatNumber value="${marker.preferredCoordinates.startCoordinate - 50000}" pattern="#0" var="startCoordWithFlank"/>
	<fmt:formatNumber value="${marker.preferredCoordinates.endCoordinate + 50000}" pattern="#0" var="endCoordWithFlank"/>
	<c:set var="hasMgvLink" value="true"/>

	<!-- default to canonical marker coordinates for Sanger link with 2kb flank -->
	<fmt:formatNumber value="${marker.preferredCoordinates.startCoordinate - 2000}" pattern="#0" var="sangerStartCoord"/>
	<fmt:formatNumber value="${marker.preferredCoordinates.endCoordinate + 2000}" pattern="#0" var="sangerEndCoord"/>
</c:if>

<c:if test="${snpsfound or hasStrainMarkers or polymorphismsfound or (not empty strainSpecificNote)}">
	<div class="row locationRibbon" id="strainRibbon">
		<div class="header <%=leftTdStyles.getNext() %>">
			Strain<br/>Comparison<br/>
			<div style="float:right;">
				<img src="${configBean.FEWI_URL}assets/images/mice_transparent.png" style="height:50px"/>
			</div>
			<div style="float:right; margin-right: 5px; margin-top: 13px;">
				<img src="${configBean.WEBSHARE_URL}images/new_icon.png"/>
			</div>
		</div>
		<div class="detail <%=rightTdStyles.getNext() %>">
			<div id="scToggle" title="Show More" class="toggleImage hdExpand">more</div>

			<c:set var="mgvUrl" value="${configBean.MGV_URL}#flank=2x&genomes=${externalUrls.MGV_Strains}&landmark=${marker.primaryID}&highlight=${marker.primaryID}&lock=on&paralogs=off&style=${externalUrls.MGV_Style}" />
			<script>
			// used in marker_detail.jsp
			var mgvUrl = "${mgvUrl}";
			</script>
			
			<section class="summarySec1">
				<ul>
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
				
					<c:if test="${hasStrainMarkers}">
						<li class="${extrastate}">
							<div class="label">
								Strain Annotations
							</div>
							<div class="value">
								<span id="annotatedStrainMarkerCount">${marker.annotatedStrainMarkerCount}</span>
							</div>
						</li>
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

			<c:if test="${not empty marker.strainMarkers}">
				<div class="extra closed">
				<form id="strainMarkerForm" name="strainMarkerForm" method="GET" action="" target="_blank">
				<div id="strainGenesWrapperDiv">
				<div id="sgLeftWrapper">
					<div id="strainGenesTableControls">
						For selected strains:
						<select id="strainOp" name="strainOp">
							<option value="mgv">Send to Multiple Genome Viewer (MGV)</option>
							<option value="fasta">Get FASTA</option>
<!--							<option value="snps">Send to Sanger SNP Query (+/- 2kb)</option> -->
						</select>
					</div>
					<div id="strainGenesTableDiv">
						<table class="padded" id="table_strainMarkers">
							<tr class="headerStripe">
					  		<th>Strain</th>
					  		<th>Gene Model ID</th>
					  		<th>Feature Type</th>
					  		<th>Coordinates</th>
					  		<th>Select Strains</th>
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
							<td class="nowrap">${sm.featureType}</td>
							<td class="nowrap">${sm.location}</td>
							<td class="sgCenter">
							  <c:if test="${not empty sgID}">
								<input type="checkbox" name="seqs" value="straingene!${sgID}!!!!!" class="sgCheckbox" />
							  </c:if>
							</td>
						</tr>
						<c:if test="${sm.strainName == 'C57BL/6J'}">
							<!-- prefer C57BL/6J coords for Sanger SNP link -->
							<fmt:formatNumber value="${sm.startCoordinate - 2000}" pattern="#0" var="sangerStartCoord"/>
							<fmt:formatNumber value="${sm.endCoordinate + 2000}" pattern="#0" var="sangerEndCoord"/>
						</c:if>
					</c:forEach> 
				</table>
				</div>
				</div>
				<div id="sgRightWrapper">
					<div id="strainGenesButtonsDiv">
						<input id="sgGoButton" type="button" class="sgButton" value="Go" onClick="strainRibbonGoButtonClick()" /><br/>
						<input id="sgAllButton" type="button" class="sgButton" value="Select All" onClick="clickAllStrainGenes()"/><br/>
						<input id="sgFounderButton" type="button" class="sgButton" value="Select DO/CC Founders" onClick="clickParentalStrainGenes()"/><br/>
						<input id="sgNoneButton" type="button" class="sgButton" value="Deselect All" onClick="clearStrainGeneCheckboxes()"/>
					</div>
				</div>
				</div>
				</form>
				</div>

			</c:if>

		</div>
	</div>
</c:if>

<style>
#strainGenesWrapperDiv {
	vertical-align: top;
	display: flex;
}
#strainGenesTableDiv {
	display: inline-block;
	vertical-align: top;
}
#strainGenesButtonsDiv {
	display: inline-block;
	vertical-align: top;
}
#strainGenesTableControls {
	display: inline-block;
	text-align: right;
	padding-bottom: 9px;
}
.sgButton {
	min-width: 50px;
	padding: 3px;
	margin-left: 3px;
	margin-right: 3px;
	margin-bottom: 6px;
}
.leftpad15 {
	margin-left: 15px;
}
#sgLeftWrapper {
}
#sgRightWrapper {
}
.nowrap {
	white-space: nowrap;
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