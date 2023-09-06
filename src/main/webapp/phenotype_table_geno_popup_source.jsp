<c:set var="markerIDs" value=""/>
<c:set var="markerSymbols" value=""/>
<c:forEach var="aa" items="${genotype.alleleAssociations}">
	<c:set var="markerID" value="${aa.allele.marker.primaryID}"/>
	<c:set var="markerSymbol" value="${aa.allele.marker.symbol}"/>
	<c:if test="${not fn:contains(markerIDs, markerID)}">
		<c:set var="markerIDs" value="${markerID},${markerIDs}"/>
		<c:set var="markerSymbols" value="${markerSymbol},${markerSymbols}"/>
	</c:if>
</c:forEach>
<c:if test="${fn:length(fn:split(markerIDs,',')) == 1}">
	<c:set var="singleMarker" value="${markerID}"/>
	<c:set var="singleMarkerSymbol" value="${markerSymbol}"/>
</c:if>

<c:set var="showDataSources" value=""/>
<c:set var="hasImpcData" value=""/>

<c:forEach var="mpSystem" items="${mpSystems}">
	<c:forEach var="term" items="${mpSystem.terms}">
		<c:forEach var="annot" items="${term.annots}">
			<c:forEach var="ref" items="${annot.references}">
		   	<c:if test="${ref.hasNonMgiSource}">
					<c:set var="showDataSources" value="1"/>
				</c:if>
				<c:if test="${(ref.phenotypingCenter.abbreviation == 'IMPC') || (ref.interpretationCenter.abbreviation == 'IMPC')}">
					<c:set var="hasImpcData" value="1"/>
				</c:if>
			</c:forEach>
		</c:forEach>
	</c:forEach>
</c:forEach>

<c:if test="${not empty showDataSources}">
	<div style="text-align:center;" onmouseout="nd();" onmouseover="return overlib('Mouse over source labels to view Data Interpretation and Phenotyping Centers for high-throughput phenotype annotations.<br><br><b>Data Interpretation Center:</b> The source of Mammalian Phenotype calls made from primary phenotyping data.<br><br><b>Phenotyping Center:</b> The source of primary phenotyping data (where phenotyping tests were performed for annotations shown).', LEFT, WIDTH, 400);">
		Data Sources
		<br>
		<img width="16" height="15" src="${configBean.WEBSHARE_URL}images/help_small_transp.gif">
	</div>
</c:if>
<c:if test="${(not empty hasImpcData) && (not empty singleMarker)}">
	<div style="text-align: center">
		<a href="http://www.mousephenotype.org/data/genes/${singleMarker}" target="_blank">IMPC Data for ${singleMarkerSymbol}</a>
	</div>
</c:if>
