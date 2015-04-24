	<c:if test="${not empty humanHomologs}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">Human<br/>homologs</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				<!-- details for each human marker -->
				<table id="humanHomologDetails">

					<c:forEach var="humanHomolog" items="${humanHomologs}" varStatus="humanHomologStatus">
						<div>
							<c:set var="humanCoords" value="${humanHomolog.preferredCoordinates}"/>
							<fmt:formatNumber value="${humanCoords.startCoordinate}" pattern="#0" var="humanStartCoord"/>
							<fmt:formatNumber value="${humanCoords.endCoordinate}" pattern="#0" var="humanEndCoord"/>
		
							<div style="float: left;">${humanHomolog.symbol}, ${humanHomolog.name}</div>
							<div style="float: left; margin-left: 20px;">Orthology source: 
								<c:forEach var="homologyCluster" items="${marker.getHomologyClusterSources(humanHomolog)}" varStatus="hstat">
									${homologyCluster.source}<c:if test="${!hstat.last}">, </c:if>
								</c:forEach>	
							</div>
							<br />

							<div style="float: left;">IDs:</div>
							<c:if test="${not empty humanHomolog.hgncID}">
								<div style="float: left; margin-left: 20px;"><a href="${fn:replace(urls.HGNC, '@@@@', humanHomolog.hgncID.accID)}" target="_blank">${humanHomolog.hgncID.accID}</a></div>
							</c:if>
	
							<div style="float: left; margin-left: 20px;">NCBI Gene ID: <a href="${fn:replace(urls.Entrez_Gene, '@@@@', humanHomolog.entrezGeneID.accID)}" target="_blank">${humanHomolog.entrezGeneID.accID}</a></div>
			
							<c:if test="${not empty humanHomolog.neXtProtIDs}">
								<div style="float: left; margin-left: 20px;">neXtProt AC:
									<c:forEach var="neXtProt" items="${humanHomolog.neXtProtIDs}" varStatus="neXtProtStatus">
										<a href="${fn:replace(urls.neXtProt, '@@@@', neXtProt.accID)}" target="_blank">${neXtProt.accID}</a><c:if test="${!neXtProtStatus.last}">, </c:if>
									</c:forEach>
								</div>
							</c:if>
							<br/>
		
							<c:if test="${not empty humanHomolog.synonyms}">
								<div style="float: left;">Human Synonyms: 
									<c:forEach var="synonym" items="${humanHomolog.synonyms}" varStatus="synonymStatus">
										${synonym.synonym}<c:if test="${!synonymStatus.last}">, </c:if>
									</c:forEach>
								</div>
								<br />
							</c:if>

							<div style="float: left;">Human Chr (Location):
								<c:set var="humanCytoband" value="${humanHomolog.preferredCytoband}"/>
								<c:if test="${not empty humanCytoband}">${humanCytoband.chromosome}${humanCytoband.cytogeneticOffset}<c:if test="${not empty humanCoords}">; </c:if></c:if>
								<c:if test="${not empty humanCoords}">
									chr${humanCoords.chromosome}:${humanStartCoord}-${humanEndCoord}
									<c:if test="${not empty humanCoords.strand}">(${humanCoords.strand})</c:if>&nbsp;&nbsp;<I>${humanCoords.buildIdentifier}</I>
								</c:if>
							</div>
							<br/>

							<c:if test="${not empty humanHomolog.OMIMHumanAnnotations}">
								<div style="float: left;">Disease Associations: 
									(<a href="" onclick="return overlib( '<table name=\'results\' border=\'0\' cellpadding=\'3\' cellspacing=\'0\' width=\'100%\'><tr ><th align=\'left\'>Human Disease</th><th width=\'4\'></th>' +
										'<th width=\'65\'>OMIM ID</th></tr>' +
										<c:set var="hMessage" value="&nbsp;" />
										<c:forEach var="annotation" items="${humanHomolog.OMIMHumanAnnotations}" varStatus="status">
										<c:set var="rColor" value="" />
										<c:if test="${status.count % 2 == 0}">
										<c:set var="rColor" value="style=\\'background-color:#F8F8F8;\\'" />
										</c:if>
										'<tr ${rColor} align=\'left\' valign=\'top\'>' +
										'<td><a href=\'${configBean.FEWI_URL}disease/${annotation.termID}\'>${annotation.term}</a></td>' +
										'<td width=\'4\'>'	+
										<c:forEach var="star" items="${marker.OMIMAnnotations}">
										<c:if test="${annotation.termID eq star.termID}">
											<c:set var="hMessage" value="* Disease is modeled in mouse using ${marker.symbol}." />
										'*' +
										</c:if>
										</c:forEach>
										'</td>' +
										'<td><a href=\'${fn:replace(urls.OMIM, '@@@@', annotation.termID)}\' target=\'_blank\'>${annotation.termID}</a></td></tr>' +
										</c:forEach>
										'<tr align=\'left\' valign=\'top\'><td	colspan=\'3\'>${hMessage}</td></tr></table>', STICKY, CAPTION, 'Human Disease Models Associated with Alleles of Human ${humanHomolog.symbol}', RIGHT, BELOW, WIDTH, 500, DELAY, 250, CLOSECLICK, CLOSETEXT, 'Close X');" onmouseout="nd();">${humanHomolog.countOfHumanDiseases}</a>)
									Disease<c:if test="${humanHomolog.countOfHumanDiseases > 0}">s</c:if> Associated with Human ${humanHomolog.symbol}
								</div>
								<br/>
							</c:if>
						</div>


						<c:if test="${humanHomologStatus.first and fn:length(humanHomologs) > 1}">
							<!-- Start Hidden Div -->
							<div id="homologShowMore">
								<div style='float:left; cursor:pointer; position:relative; top:2px;' id='rightArrowHomologTag' onClick='toggleHomologTags()'><img src='${configBean.WEBSHARE_URL}images/rightArrow.gif'></div>
								<div style='float:left; cursor:pointer; display:none; position:relative; top:2px;' id='downArrowHomologTag' onClick='toggleHomologTags()'><img src='${configBean.WEBSHARE_URL}images/downArrow.gif'></div>
								&nbsp;&nbsp;${fn:length(humanHomologs) - 1} More Homolog(s):
							</div>
							<div id="moreHomologs" style="display:none">
						</c:if>

						<c:if test="${!humanHomologStatus.last}"><hr></c:if>

					</c:forEach>
							<!-- End Hidden Div -->
							</div>
				</table>
			</div>
		</div>
	</c:if>

