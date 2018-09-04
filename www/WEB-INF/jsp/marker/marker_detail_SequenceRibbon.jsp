<style>
.left {text-align: left}
.inheritColor {background-color: inherit}
</style>

<c:if test="${(marker.countOfSequences > 0) or (not empty marker.ccdsIDs) or (not empty marker.uniGeneIDs) or (not empty otherIDs['Entrez Gene']) or (not empty otherIDs['Ensembl Gene Model'])}">
	<div class="row sequenceRibbon" id="sequenceRibbon">
		<div class="header <%=leftTdStyles.getNext() %>">
			Sequences &amp;<br/>
			Gene Models
		</div>
		<div id="sequence" class="detail <%=rightTdStyles.getNext() %>">
			<div id="seqToggle" class="toggleImage hdCollapse" title="Show Less">less</div>
			<c:if test="${(marker.countOfSequences > 0) or (not empty marker.ccdsIDs) or (not empty marker.uniGeneIDs)}">
				<section class="summarySec1 extra">
					<ul>
					<c:if test="${marker.countOfSequences > 0}">
					<li>
					<div class="label">All Sequences</div>
					<div class="value"><a href="${configBean.FEWI_URL}sequence/marker/${marker.primaryID}" id="allSeqLink">${marker.countOfSequences}</a></div>
					</li>
					</c:if>
					<c:if test="${marker.countOfRefSeqSequences > 0}">
					<li>
					<div class="label" style="font-weight: normal">RefSeq</div>
					<div class="value"><a href="${configBean.FEWI_URL}sequence/marker/${marker.primaryID}?provider=RefSeq" id="refseqLink">${marker.countOfRefSeqSequences}</a></div>
					</li>
					</c:if>
					<c:if test="${marker.countOfUniProtSequences > 0}">
					<li>
					<div class="label" style="font-weight: normal">UniProt</div>
					<div class="value"><a href="${configBean.FEWI_URL}sequence/marker/${marker.primaryID}?provider=UniProt" id="uniprotSeqLink">${marker.countOfUniProtSequences}</a></div>
					</li>
					</c:if>
					<c:if test="${not empty marker.ccdsIDs}">
					<li>
					<div class="label">CCDS</div>
					<div class="value">${otherIDs['Consensus CDS Project']}</div>
					</li>
					</c:if>
					<c:if test="${not empty marker.uniGeneIDs}">
					<li>
					<div class="label">UniGene</div>
					<div class="value">${otherIDs['UniGene']}</div>
					</li>
					</c:if>
					</ul>
				</section>
			</c:if>
</c:if>

<c:if test="${(not empty otherIDs['Ensembl Gene Model']) or (not empty otherIDs['Entrez Gene'])}">
				<section class="summarySec2 extra" style="width: 49%">
					<ul>
					<c:if test="${not empty otherIDs['Ensembl Gene Model']}">
					<li>
					<div class="label">Ensembl</div>
					<div class="value">${otherIDs['Ensembl Gene Model']}</div>
					</li>
					</c:if>
					<c:if test="${not empty otherIDs['Entrez Gene']}">
					<li>
					<div class="label">NCBI Gene</div>
					<div class="value">${otherIDs['Entrez Gene']}</div>
					</li>
					</c:if>
					</ul>
				</section>
</c:if>

<c:if test="${(not empty marker.representativeGenomicSequence) or (not empty marker.representativeTranscriptSequence) or (not empty marker.representativePolypeptideSequence)}">
				<section class="summarySec1 extra wide" style="margin-left: 91px;">
					<form name="sequenceForm" method="GET">
						<table class="padded">
							<tr class="headerStripe"><th class="left td_disease_tbl_hdr" colspan="4">Representative Sequences</th><th class="left td_disease_tbl_hdr">Length</th><th class="left td_disease_tbl_hdr">Strain/Species</th><th class="left td_disease_tbl_hdr">Flank</th></tr>
							<c:if test="${not empty marker.representativeGenomicSequence}">
								<c:set var="seq" value="${marker.representativeGenomicSequence}" scope="request"/>
								<% Sequence seqDna = (Sequence) request.getAttribute("seq"); %>
								<tr>
								<td class="td_disease_tbl padded"><input type="radio" name="seq1" value="<%= FormatHelper.getSeqForwardValue(seqDna) %>"></td>
								<td class="td_disease_tbl padded">genomic</td>
								<td class="td_disease_tbl padded">${marker.representativeGenomicSequence.primaryID}</td>
								<td class="td_disease_tbl padded">${fn:replace(genomicLink, "href", "target='blank' href")} | <a href="${configBean.FEWI_URL}sequence/${marker.representativeGenomicSequence.primaryID}">MGI Sequence Detail</a></td>
								<td class="td_disease_tbl padded">${marker.representativeGenomicSequence.length}</td>
								<td class="td_disease_tbl padded">${genomicSource}</td>
								<td class="td_disease_tbl padded">&#177; <input type="text" size="3" name="flank1" value="0">&nbsp;kb</td></tr>
							</c:if>
							<c:if test="${not empty marker.representativeTranscriptSequence}">
								<c:set var="seq" value="${marker.representativeTranscriptSequence}" scope="request"/>
								<% Sequence seqRna = (Sequence) request.getAttribute("seq"); %>
								<tr>
								<td class="td_disease_tbl padded"><input type="radio" name="seq1" value="<%= FormatHelper.getSeqForwardValue(seqRna) %>"></td>
								<td class="td_disease_tbl padded">transcript</td>
								<td class="td_disease_tbl padded">${marker.representativeTranscriptSequence.primaryID}</td>
								<td class="td_disease_tbl padded">${fn:replace(transcriptLink, "href", "target='_blank' href")} | <a href="${configBean.FEWI_URL}sequence/${marker.representativeTranscriptSequence.primaryID}">MGI Sequence Detail</a></td>
								<td class="td_disease_tbl padded">${marker.representativeTranscriptSequence.length}</td>
								<td class="td_disease_tbl padded">${transcriptSource}</td>
								<td class="td_disease_tbl padded">&nbsp;</td></tr>
							</c:if>
							<c:if test="${not empty marker.representativePolypeptideSequence}">
								<c:set var="seq" value="${marker.representativePolypeptideSequence}" scope="request"/>
								<% Sequence seqPoly = (Sequence) request.getAttribute("seq"); %>
								<tr>
								<td class="td_disease_tbl padded"><input type="radio" name="seq1" value="<%= FormatHelper.getSeqForwardValue(seqPoly) %>"></td>
								<td class="td_disease_tbl padded">polypeptide</td>
								<td class="td_disease_tbl padded">${marker.representativePolypeptideSequence.primaryID}</td>
								<td class="td_disease_tbl padded">${fn:replace(polypeptideLink, "href", "target='_blank' href")} | <a href="${configBean.FEWI_URL}sequence/${marker.representativePolypeptideSequence.primaryID}">MGI Sequence Detail</a></td>
								<td class="td_disease_tbl padded">${marker.representativePolypeptideSequence.length}</td>
								<td class="td_disease_tbl padded">${polypeptideSource}</td>
								<td class="td_disease_tbl padded">&nbsp;</td></tr>
							</c:if>
						</table>
					</form>
					<form name="sequenceFormPullDown" style="margin-top: 5px; margin-bottom: 5px">
					<span style="margin-left: 5px">
					<I>For the selected sequence</I>
					</span>
						<select name="seqPullDown">
						<option value="${configBean.SEQFETCH_URL}" selected> download in FASTA format</option>
						<option value="${configBean.FEWI_URL}sequence/blast"> forward to NCBI BLAST</option>
						<input type="button" value="Go" onClick="formatForwardArgs()">
						</select>
					</form>
				</section>
</c:if>
<c:if test="${(marker.countOfSequences > 0) or (not empty marker.ccdsIDs) or (not empty marker.uniGeneIDs) or (not empty otherIDs['Entrez Gene']) or (not empty otherIDs['Ensembl Gene Model'])}">
		</div> <!-- sequence details -->
	</div><!-- sequence ribbon -->
</c:if>
