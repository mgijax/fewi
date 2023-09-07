	<div class="row" >
		<div class="header <%=leftTdStyles.getNext() %>">
			Summary
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">

			<section class="summarySec1 open">
				<ul>
					<li>
						<div class="label">ID</div>
						<div class="value">${snp.accid}</div>
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
<c:if test="${configBean.snpsOutOfSync != 'true'}">
					<li>
						<div class="label">Additional Resources</div>
						<div class="value">
							<c:if test="${!snp.multiCoord}">
								<a href="${snp.getJBrowserLink(JBrowserLinkTemplate)}" target="_blank"><nobr>JBrowse Genome Browser</nobr></a> |
							</c:if>
							<a href="http://www.ensembl.org/Mus_musculus/snpview?snp=${snp.accid}" target="_blank"><nobr>Ensembl SNPView</nobr></a> |
							<a href="http://genome.ucsc.edu/cgi-bin/hgTracks?clade=vertebrate&amp;org=Mouse&amp;db=mm39&amp;position=${snp.accid}" target="_blank"><nobr>UCSC Browser</nobr></a> 
							<br>
							<c:if test="${snp.multiCoord}">
								<div style="color:red; padding-top:3px;">
									* SNP maps to multiple genome locations. JBrowse Genome Browser link is not available.
								</div>
							</c:if>
						</div>
					</li>
</c:if>
				</ul>
			</section>
		</div>
	</div>


