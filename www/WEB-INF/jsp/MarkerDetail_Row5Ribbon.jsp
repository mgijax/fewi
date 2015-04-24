	<c:if test="${not (empty marker.preferredCoordinates and empty vegaGenomeBrowserUrl and empty ensemblGenomeBrowserUrl and empty ucscGenomeBrowserUrl and empty gbrowseUrl and empty jbrowseUrl)}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Sequence&nbsp;Map
			</div>
			<fmt:formatNumber value="${marker.preferredCoordinates.startCoordinate}" pattern="#0" var="startCoord"/>
			<fmt:formatNumber value="${marker.preferredCoordinates.endCoordinate}" pattern="#0" var="endCoord"/>
			<c:set var="chromosome" value="${marker.preferredCoordinates.chromosome}"/>
			<c:set var="coord1" value="${fn:replace('Chr<chr>:<start>-<end>', '<chr>', chromosome)}"/>
			<c:set var="coord2" value="${fn:replace(coord1, '<start>', startCoord)}"/>
			<c:set var="coords" value="${fn:replace(coord2, '<end>', endCoord)}"/>

			<div class="detail <%=rightTdStyles.getNext() %>">
				<table style="width: 100%;">
					<tr><td>
						<c:if test="${not empty marker.preferredCoordinates}">
							Chr${chromosome}:${startCoord}-${endCoord}
							${marker.preferredCoordinates.mapUnits}<c:if test="${not empty marker.preferredCoordinates.strand}">, ${marker.preferredCoordinates.strand} strand</c:if><br/>
							<c:if test="${empty marker.preferredCoordinates}">
								${marker.preferredCoordinates.mapUnits}<br/>
							</c:if>
						</c:if>
						<c:if test="${not empty marker.qtlNote}">
							${marker.qtlNote}<br/>
						</c:if>
						<c:if test="${not empty marker.preferredCoordinates}">
							From ${marker.preferredCoordinates.provider} annotation of ${marker.preferredCoordinates.buildIdentifier}<br/>
							<p/>
						</c:if>

						<c:if test="${not empty marker.preferredCoordinates}">
							<form name="markerCoordForm" method="GET" action="${configBean.SEQFETCH_URL}">
								<c:set var="length" value="${marker.preferredCoordinates.endCoordinate - marker.preferredCoordinates.startCoordinate + 1}"/>
								<c:set var="seqfetchParms" value="mousegenome!!${marker.preferredCoordinates.chromosome}!${startCoord}!${endCoord}!!"/>

								<!-- handle end < start, which is very atypical -->
								<c:if test="${length < 0}">
									<c:set var="length" value="${marker.preferredCoordinates.startCoordinate - marker.preferredCoordinates.endCoordinate + 1}"/>
									<c:set var="seqfetchParms" value="mousegenome!!${marker.preferredCoordinates.chromosome}!${endCoord}!${startCoord}!!"/>
								</c:if>

								<fmt:formatNumber value="${length}" pattern="#0" var="lengthStr"/>

								<input type="hidden" name="seq1" value="${seqfetchParms}">
								<input type="button" value="Get FASTA" onClick="formatFastaArgs()">
								&nbsp;&nbsp;${lengthStr} bp
								&nbsp;&nbsp;&#177; <input type="text" size="3" name="flank1" value="0">&nbsp;kb flank
							</form>
							<p/>
						</c:if>

						<c:set var="vegaID" value="${marker.vegaGeneModelID.accID}"/>
						<c:set var="ensemblID" value="${marker.ensemblGeneModelID.accID}"/>
						<c:set var="ncbiID" value="${marker.ncbiGeneModelID.accID}"/>
						<c:set var="foundOne" value="0"/>
						<c:if test="${not empty vegaGenomeBrowserUrl}">
							<a href="${vegaGenomeBrowserUrl}" target="_new">VEGA Genome Browser</a>
							<c:set var="foundOne" value="1"/>
						</c:if>
						<c:if test="${not empty ensemblGenomeBrowserUrl}">
							<c:if test="${foundOne > 0}"> | </c:if>
							<a href="${ensemblGenomeBrowserUrl}" target="_new">Ensembl Genome Browser</a>
							<c:set var="foundOne" value="1"/>
						</c:if>
						<c:if test="${not empty ucscGenomeBrowserUrl}">
							<c:if test="${foundOne > 0}"> | </c:if>
							<a href="${ucscGenomeBrowserUrl}" target="_new">UCSC Browser</a>
							<c:set var="foundOne" value="1"/>
						</c:if>
						<c:if test="${not empty ncbiMapViewerUrl}">
							<c:if test="${foundOne > 0}"> | </c:if>
							<a href="${ncbiMapViewerUrl}" target="_new">NCBI Map Viewer</a>
						</c:if>
						</td><td align="right" width="*">
						<c:if test="${not empty jbrowseUrl}">
							<table><tr><td align="center">
								<c:if test="${not empty gbrowseThumbnailUrl}">
									<a href="${jbrowseUrl}"><img border="0" src="${gbrowseThumbnailUrl}"/></a> <br/>
								</c:if>
								<a href="${jbrowseUrl}">Mouse Genome Browser</a>
							</td></tr></table>
						</c:if>
					</td></tr>
				</table>
			</div>
		</div>
	</c:if>


