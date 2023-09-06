<c:if test="${not empty hasMarkers}">
<!-- Associated markers -->
<div class="row">
	<div class="header <%=leftTdStyles.getNext() %>">
		Gene/Marker<br/>Associations<br/>and<br/>Function<br/>Classes
	</div>
	<div class="detail <%=rightTdStyles.getNext() %>" style="padding-left: 10px">
	<span class="small"><i>MGI genes/markers associated with the SNP</i></span><br/>
	<section class="summarySec1 wide" style="overflow:auto;">
	<c:set var="tableCt" value="0"/>
	<c:forEach var="cc" items="${snp.consensusCoordinates}">
		<c:set var="tableCt" value="${tableCt + 1}"/>
		<table id="locationTable${tableCt}" style="margin-bottom: 3px">
			<tr>
				<td class="blueBG snpMarkerHeader box" colspan="9">
					<span class="bold">Location:</span>
					Chr${cc.chromosome}:${cc.startCoordinate} (${snpAssemblyVersion})
					<span class="bold">rs orientation:</span>
					${cc.strandFormatted}
				</td>
			</tr>
			<tr>
				<th class="blueBG snpMarkerHeader">Symbol</th>
				<th class="blueBG snpMarkerHeader">Name</th>
				<th class="blueBG snpMarkerHeader">Transcript</th>
				<th class="blueBG snpMarkerHeader">Protein</th>
				<th class="blueBG snpMarkerHeader">Function<br/>Class</th>
				<th class="blueBG snpMarkerHeader">Allele</th>
				<th class="blueBG snpMarkerHeader">Residue</th>
				<th class="blueBG snpMarkerHeader">Codon<br/>Position</th>
				<th class="blueBG snpMarkerHeader">AA<br/>Position</th>
			</tr>
			<c:set var="mrkPrev" value=""/>
			<c:forEach var="mrk" items="${cc.markers}">
			    <c:if test="${mrkPrev != mrk.symbol}">
				<c:set var="mrkCount" value="0"/>
				<c:forEach var="mrk2" items="${cc.markers}">
				    <c:if test="${mrk2.accid == mrk.accid}">
					<c:set var="mrkCount" value="${mrkCount + 1}"/>  
				    </c:if>
				</c:forEach>
				<c:set var="rowspan" value=""/>
				<c:if test="${mrkCount > 1}">
				    <c:set var="rowspan" value=" rowspan='${mrkCount}'"/>
				</c:if>
				<%@ include file="SNPDetail_Markers_TableRow.jsp" %>
			    </c:if>
			    <c:if test="${mrkPrev == mrk.symbol}">
				<%@ include file="SNPDetail_Markers_TableSubRow.jsp" %>
			    </c:if>
			    <c:set var="mrkPrev" value="${mrk.symbol}"/>
			</c:forEach>
		</table>
	</c:forEach>
	</section>
	</div>
</div>
</c:if>
