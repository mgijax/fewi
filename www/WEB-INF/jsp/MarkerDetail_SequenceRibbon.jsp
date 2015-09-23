<c:if test="${(marker.countOfSequences > 0) or (not empty marker.ccdsIDs) or (not empty marker.uniGeneIDs)}">
	<div class="row sequenceRibbon" >
		<div class="header <%=leftTdStyles.getNext() %>">
			Sequences &amp;<br/>
			Gene Models
		</div>
		<div id="sequence" class="detail <%=rightTdStyles.getNext() %>">
			<div id="toggleSequenceRibbon" class="toggleImage hdCollapse" title="Show More"></div>
			<c:if test="${(marker.countOfSequences > 0) or (not empty marker.ccdsIDs) or (not empty marker.uniGeneIDs)}">
				<section class="summarySec1 extra">
				<div class="label" style="font-size: 104%; width: 60%; text-align: center;">Sequences</div><br/>
					<ul>
					<c:if test="${marker.countOfSequences > 0}">
					<li>
					<div class="label">All</div>
					<div class="value"><a href="${configBean.FEWI_URL}sequence/marker/${marker.primaryID}">${marker.countOfSequences}</a></div>
					</li>
					</c:if>
					<c:if test="${marker.countOfRefSeqSequences > 0}">
					<li>
					<div class="label">RefSeq</div>
					<div class="value"><a href="${configBean.FEWI_URL}sequence/marker/${marker.primaryID}?provider=RefSeq">${marker.countOfRefSeqSequences}</a></div>
					</li>
					</c:if>
					<c:if test="${marker.countOfUniProtSequences > 0}">
					<li>
					<div class="label">UniProt</div>
					<div class="value"><a href="${configBean.FEWI_URL}sequence/marker/${marker.primaryID}?provider=UniProt">${marker.countOfUniProtSequences}</a></div>
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

<c:if test="${(not empty otherIDs['VEGA Gene Model']) or (not empty otherIDs['Ensembl Gene Model']) or (not empty otherIDs['Entrez Gene'])}">
				<section class="summarySec2 extra">
				<div class="label" style="font-size: 104%; width: 100%; text-align: center;">Gene Models</div><br/>
					<ul>
					<c:if test="${not empty otherIDs['VEGA Gene Model']}">
					<li>
					<div class="label">VEGA</div>
					<div class="value">${otherIDs['VEGA Gene Model']}</div>
					</li>
					</c:if>
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

<style>
tr.underlined { border-bottom: solid thin black }
</style>

<c:if test="${(not empty marker.representativeGenomicSequence) or (not empty marker.representativeTranscriptSequence) or (not empty marker.representativePolypeptideSequence)}">
				<section class="summarySec1 extra wide" style="margin-left: 91px;">
					<form name="sequenceForm" method="GET">
						<table class="padded">
							<tr class="underlined"><td class="padded" colspan="4"><div class="label" style="font-size: 104%">Representative Sequences</div></td><td class="padded">Length</td><td class="padded">Strain/Species</td><td class="padded">Flank</td></tr>
							<c:if test="${not empty marker.representativeGenomicSequence}">
								<c:set var="seq" value="${marker.representativeGenomicSequence}" scope="request"/>
								<% Sequence seqDna = (Sequence) request.getAttribute("seq"); %>
								<tr class="underlined"><td class="padded"><input type="radio" name="seq1" value="<%= FormatHelper.getSeqForwardValue(seqDna) %>"></td><td>genomic</td>
								<td class="padded">${marker.representativeGenomicSequence.primaryID}</td>
								<td class="padded">${fn:replace(genomicLink, "VEGA", "VEGA Gene Model")} | <a href="${configBean.FEWI_URL}sequence/${marker.representativeGenomicSequence.primaryID}">MGI Sequence Detail</a></td>
								<td class="padded">${marker.representativeGenomicSequence.length}</td>
								<td class="padded">${genomicSource}</td>
								<td class="padded">&#177; <input type="text" size="3" name="flank1" value="0">&nbsp;kb</td></tr>
							</c:if>
							<c:if test="${not empty marker.representativeTranscriptSequence}">
								<c:set var="seq" value="${marker.representativeTranscriptSequence}" scope="request"/>
								<% Sequence seqRna = (Sequence) request.getAttribute("seq"); %>
								<tr class="underlined"><td class="padded"><input type="radio" name="seq1" value="<%= FormatHelper.getSeqForwardValue(seqRna) %>"></td><td>transcript</td>
								<td class="padded">${marker.representativeTranscriptSequence.primaryID}</td>
								<td class="padded">${transcriptLink} | <a href="${configBean.FEWI_URL}sequence/${marker.representativeTranscriptSequence.primaryID}">MGI Sequence Detail</a></td>
								<td class="padded">${marker.representativeTranscriptSequence.length}</td>
								<td class="padded">${transcriptSource}</td><td>&nbsp;</td></tr>
							</c:if>
							<c:if test="${not empty marker.representativePolypeptideSequence}">
								<c:set var="seq" value="${marker.representativePolypeptideSequence}" scope="request"/>
								<% Sequence seqPoly = (Sequence) request.getAttribute("seq"); %>
								<tr class="underlined"><td class="padded"><input type="radio" name="seq1" value="<%= FormatHelper.getSeqForwardValue(seqPoly) %>"></td><td>polypeptide</td>
								<td class="padded">${marker.representativePolypeptideSequence.primaryID}</td>
								<td class="padded">${polypeptideLink} | <a href="${configBean.FEWI_URL}sequence/${marker.representativePolypeptideSequence.primaryID}">MGI Sequence Detail</a></td>
								<td class="padded">${marker.representativePolypeptideSequence.length}</td>
								<td class="padded">${polypeptideSource}</td><td>&nbsp;</td></tr>
							</c:if>
						</table>
					</form>
					<form name="sequenceFormPullDown" style="margin-top: 5px; margin-bottom: 5px">
					<span style="margin-left: 5px">
					<I>For the selected sequences</I>
					</span>
						<select name="seqPullDown">
						<option value="${configBean.SEQFETCH_URL}" selected> download in FASTA format</option>
						<option value="${configBean.FEWI_URL}sequence/blast"> forward to NCBI BLAST</option>
						<input type="button" value="Go" onClick="formatForwardArgs()">
						</select>
					</form>
				</section>

		</div> <!-- sequence details -->
	</div><!-- sequence ribbon -->
</c:if>
