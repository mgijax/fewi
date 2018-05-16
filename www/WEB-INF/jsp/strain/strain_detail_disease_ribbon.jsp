	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>" id="diseaseRibbonLabel">
			Associated<br/>Diseases
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">
			<section class="summarySec1">
				<span class="indented">
		    	${fn:length(strain.diseases)} associated disease<c:if test="${fn:length(strain.diseases) > 1}">s</c:if></span>
		    	<br/>
			    <div id="diseaseSummaryDiv" style="margin-top: 5px; margin-left: 15px; max-width: 95%" id="diseaseDiv">
				    <table id="diseaseSummaryTable">
				    <tr>
				    	<th>Human Diseases</th>
					    <c:forEach var="genotype" items="${strain.diseaseGenotypes}" varStatus="gStatus">
					    	<th><a href="${configBean.FEWI_URL}allele/genoview/${genotype.genotypeID}">model ${gStatus.count}</a></th>
					    </c:forEach>
				    </tr>
				    <c:forEach var="row" items="${strain.diseases}">
				    	<tr>
				    		<td><c:if test="${not empty row.diseaseID}">
				    			<a href="${configBean.FEWI_URL}disease/${row.diseaseID}" target="_blank" title="${row.diseaseID}"><fewi:super value="${row.disease}"/></a>
				    			</c:if></td>
				    		<c:forEach var="genotype" items="${strain.diseaseGenotypes}">
					    		<td class='center'>
					    			<c:choose>
					    			<c:when test="${row.getCellFlag(genotype.genotypeID) == 1}">&#8730;</c:when>
					    			<c:when test="${row.getCellFlag(genotype.genotypeID) == -1}"><img src="${configBean.WEBSHARE_URL}images/notSymbol.gif" border="0" valign="bottom"/></c:when>
					    			<c:otherwise></c:otherwise>
					    			</c:choose>
					    		</td>
				    		</c:forEach>
				    	</tr>
				    </c:forEach> 
					</table>
			    </div>
			</section>
		</div>
	</div>
	<script>
	// fix width of DIV containing table and adjust the header color
	$('#diseaseSummaryTable th').css('background-color', $('#diseaseRibbonLabel').css('background-color'));
	</script>