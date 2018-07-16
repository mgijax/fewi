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
</c:if>

<c:if test="${snpsfound or hasStrainMarkers or polymorphismsfound or hasCoords or (not empty strainSpecificNote)}">
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
			<div title="Show More" class="toggleImage hdExpand">more</div>

			<c:set var="mgvUrl" value="${externalUrls.MGV}#ref=C57BL/6J&genomes=${externalUrls.MGV_Strains}&chr=${chromosome}&start=${startCoordWithFlank}&end=${endCoordWithFlank}&highlight=${marker.primaryID}" />
			<script>
			var mgvUrl = "${mgvUrl}";
			</script>
			
			<section class="summarySec1">
				<ul>
					<c:if test="${hasStrainMarkers}">
						<li class="${extrastate}">
							<div class="label">
								Strain Annotations
							</div>
							<div class="value">
								${marker.annotatedStrainMarkerCount}
								<c:if test="${hasMgvLink}">
									<span id="mgvSpan" class="leftpad15">
										<a href="${mgvUrl}" target="_blank" id="mgvLink">
										Multiple Genome Viewer (MGV)
										</a>
									</span>
								</c:if>
							</div>
						</li>
					</c:if>
					<c:if test="${(not hasStrainMarkers) and (hasMgvLink)}">
						<li class="${extrastate}">
							<div class="label">
							</div>
							<div class="value">
								<span id="mgvSpan">
									<a href="${externalUrls.MGV}#ref=C57BL/6J&genomes=${externalUrls.MGV_Strains}&chr=${chromosome}&start=${startCoordWithFlank}&end=${endCoordWithFlank}&highlight=${marker.primaryID}" target="_blank" id="mgvLink">
									Multiple Genome Viewer (MGV)
									</a>
								</span>
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

			<c:if test="${not empty marker.strainMarkers}">
				<div class="extra closed">
				<form id="strainMarkerForm" name="strainMarkerForm" method="GET" action="" target="_blank">
				<div id="strainGenesTableControls">
					For selected strains:
					<select id="strainOp" name="strainOp">
						<option value="fasta">Get FASTA</option>
						<option value="mgv">Send to Multi Genome Viewer (MGV)</option>
						<option value="muscle">Send to MUSCLE (Multiple Sequence Alignment Tool)</option>
						<option value="snps">Send to Sanger SNP Query (+/- 2kb)</option>
					</select>
					<input type="button" class="sgButton" value="Go" onClick="strainRibbonGoButtonClick()" />
				</div>
				<div id="strainGenesWrapperDiv">
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
				<div id="strainGenesButtonsDiv">
					<input type="button" class="sgButton" value="Select All" onClick="clickAllStrainGenes()"/><br/>
					<input type="button" class="sgButton" value="Select DO/CC Founders" onClick="clickParentalStrainGenes()"/><br/>
					<input id="sgResetButton" type="reset" class="sgButton" value="Deselect All" />
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
	min-width: 838px;
	margin-left: 35px;
}
.sgButton {
	min-width: 50px;
	padding: 3px;
	margin: 3px;
}
.leftpad15 {
	margin-left: 15px;
}
</style>
<script>
	function clickAllStrainGenes() {
		// for all checkboxes on the strainMarkerForm that aren't checked, click them
		$('[name=strainMarkerForm] [type=checkbox]:not(:checked)').click();
	}
	
	function realWidth(obj){
	    var clone = obj.clone();
	    clone.css("visibility","hidden");
	    $('body').append(clone);
	    var width = clone.outerWidth();
	    clone.remove();
	    return width;
	}

	// add centering of download checkboxes
	$('.sgCenter').css({'text-align' : 'center'});
	$('#strainGenesTableControls').width(realWidth($('#strainGenesTableDiv')));
</script>