	<c:if test="${marker.countOfSequences > 0}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Sequences
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				<div id="toggleSequenceRibbon" title="Show More" class="toggleImage hdCollapse"></div>

				<div class="extra" style="padding-left: 35px;">
					<form name="sequenceForm" method="GET">
						<table class="padded">
							<tr><td class="padded" colspan="4">Representative Sequences</td><td class="padded">Length</td><td class="padded">Strain/Species</td><td class="padded">Flank</td></tr>
							<c:if test="${not empty marker.representativeGenomicSequence}">
								<c:set var="seq" value="${marker.representativeGenomicSequence}" scope="request"/>
								<% Sequence seqDna = (Sequence) request.getAttribute("seq"); %>
								<tr><td class="padded"><input type="radio" name="seq1" value="<%= FormatHelper.getSeqForwardValue(seqDna) %>"></td><td>genomic</td>
								<td class="padded">${marker.representativeGenomicSequence.primaryID}</td>
								<td class="padded">${fn:replace(genomicLink, "VEGA", "VEGA Gene Model")} | <a href="${configBean.FEWI_URL}sequence/${marker.representativeGenomicSequence.primaryID}">MGI Sequence Detail</a></td>
								<td class="padded">${marker.representativeGenomicSequence.length}</td>
								<td class="padded">${genomicSource}</td>
								<td class="padded">&#177; <input type="text" size="3" name="flank1" value="0">&nbsp;kb</td></tr>
							</c:if>
							<c:if test="${not empty marker.representativeTranscriptSequence}">
								<c:set var="seq" value="${marker.representativeTranscriptSequence}" scope="request"/>
								<% Sequence seqRna = (Sequence) request.getAttribute("seq"); %>
								<tr><td class="padded"><input type="radio" name="seq1" value="<%= FormatHelper.getSeqForwardValue(seqRna) %>"></td><td>transcript</td>
								<td class="padded">${marker.representativeTranscriptSequence.primaryID}</td>
								<td class="padded">${transcriptLink} | <a href="${configBean.FEWI_URL}sequence/${marker.representativeTranscriptSequence.primaryID}">MGI Sequence Detail</a></td>
								<td class="padded">${marker.representativeTranscriptSequence.length}</td>
								<td class="padded">${transcriptSource}</td><td>&nbsp;</td></tr>
							</c:if>
							<c:if test="${not empty marker.representativePolypeptideSequence}">
								<c:set var="seq" value="${marker.representativePolypeptideSequence}" scope="request"/>
								<% Sequence seqPoly = (Sequence) request.getAttribute("seq"); %>
								<tr><td class="padded"><input type="radio" name="seq1" value="<%= FormatHelper.getSeqForwardValue(seqPoly) %>"></td><td>polypeptide</td>
								<td class="padded">${marker.representativePolypeptideSequence.primaryID}</td>
								<td class="padded">${polypeptideLink} | <a href="${configBean.FEWI_URL}sequence/${marker.representativePolypeptideSequence.primaryID}">MGI Sequence Detail</a></td>
								<td class="padded">${marker.representativePolypeptideSequence.length}</td>
								<td class="padded">${polypeptideSource}</td><td>&nbsp;</td></tr>
							</c:if>
						</table>
					</form>
					<p>
					<form name="sequenceFormPullDown">
						<I>For the selected sequences</I>
						<select name="seqPullDown">
						<option value="${configBean.SEQFETCH_URL}" selected> download in FASTA format</option>
						<option value="${configBean.FEWI_URL}sequence/blast"> forward to NCBI BLAST</option>
						<input type="button" value="Go" onClick="formatForwardArgs()">
						</select>
					</form>
					<c:set var="seqUrl" value="${configBean.FEWI_URL}sequence/marker/${marker.primaryID}"/>
					<c:if test="${marker.countOfSequences > 0}">
						All sequences(<a href="${seqUrl}">${marker.countOfSequences}</a>)
					</c:if>
					<c:if test="${marker.countOfRefSeqSequences > 0}">
						RefSeq(<a href="${seqUrl}?provider=RefSeq">${marker.countOfRefSeqSequences}</a>)
					</c:if>
					<c:if test="${marker.countOfUniProtSequences > 0}">
						UniProt(<a href="${seqUrl}?provider=UniProt">${marker.countOfUniProtSequences}</a>)
					</c:if>
				</div>
			</div>
		</div>
	</c:if>
