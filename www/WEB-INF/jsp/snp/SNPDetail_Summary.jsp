	<div class="row" >
		<div class="header <%=leftTdStyles.getNext() %>">
			Summary
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">

			<section class="summarySec1 open">
				<ul>
					<li>
						<div class="label">ID</div>
						<div class="value">${snp.accid} <a href="${fn:replace(externalUrls.RefSNP, '@@@@', snp.accid)}" target="_blank">dbSNP</a></div>
					</li>
					<li>
						<div class="label">Variation Type</div>
						<div class="value">${snp.variationClass}</div>
					</li>
					<li>
						<div class="label">Alleles</div>
						<div class="value">${snp.alleleSummary}</div>
					</li>
				</ul>
			</section>
			<section class="summarySec2 open">
				<ul>
					<li>
						<div class="label">Created in dbSNP Build</div>
						<div class="value">
							${snp.buildCreated}
						</div>
					</li>
					<li>
						<div class="label">Last Updated in dbSNP Build</div>
						<div class="value">
							${snp.buildUpdated}
						</div>
					</li>
					<li>
						<div class="label">Additional Resources</div>
						<div class="value">
							<a href="http://phenome.jax.org/db/q?rtn=snp/ret1&amp;gohint=1&amp;ureg=${snp.accid}" target="_blank">MPD</a> |
							<c:if test="${!snp.multiCoord}">
								<a href="${snp.getJBrowserLink(JBrowserLinkTemplate)}" target="_blank"><nobr>Mouse Genome Browser</nobr></a> |
							</c:if>
							<a href="http://www.ensembl.org/Mus_musculus/snpview?snp=${snp.accid}" target="_blank"><nobr>Ensembl SNPView</nobr></a> |
							<a href="http://genome.ucsc.edu/cgi-bin/hgTracks?clade=vertebrate&amp;org=Mouse&amp;db=mm9&amp;position=${snp.accid}" target="_blank"><nobr>UCSC Browser</nobr></a> |
							<a href="http://www.ncbi.nlm.nih.gov/mapview/map_search.cgi?taxid=10090&amp;advsrch=off&amp;query=${snp.accid}" target="_blank"><nobr>NCBI MapViewer</nobr></a><br>
							<c:if test="${snp.multiCoord}">
								<div style="color:red; padding-top:3px;">
									* SNP maps to multiple genome locations. Mouse Genome Browser link is not available.
								</div>
							</c:if>
						</div>
					</li>

				</ul>
			</section>
		</div>
	</div>

