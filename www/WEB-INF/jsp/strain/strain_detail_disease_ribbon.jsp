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
				    	<th>ID</th>
				    	<th>Disease</th>
				    </tr>
				    <c:forEach var="row" items="${strain.diseases}">
				    	<tr>
				    		<td><c:if test="${not empty row.diseaseID}">
				    			<a href="${configBean.FEWI_URL}disease/${row.diseaseID}" target="_blank"><fewi:super value="${row.diseaseID}"/></a>
				    			</c:if></td>
				    		<td><c:if test="${not empty row.disease}">
				    			<fewi:super value="${row.disease}"/>
				    			</c:if></td>
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