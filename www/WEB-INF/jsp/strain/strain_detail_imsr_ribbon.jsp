	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>" id="imsrRibbonLabel">
			Find Mice (IMSR)
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">
				<span class="indented">
		    	${fn:length(strain.imsrData)} associated strain record<c:if test="${fn:length(strain.imsrData) > 1}">s</c:if> at IMSR</span>
		    	<br/>
			    <div id="imsrSummaryDiv" style="margin-top: 5px; margin-left: 15px; max-width: 95%" id="imsrDiv">
				    <table id="imsrSummaryTable">
				    <tr>
				    	<th>IMSR</th>
				    	<th>Repository</th>
				    	<th>IMSR Strain</th>
				    	<th>Why Matched</th>
				    </tr>
				    <c:forEach var="row" items="${strain.imsrData}">
				    	<tr>
				    		<td><c:if test="${not empty row.imsrID}">
				    			<a href="http://www.findmice.org/summary?strainid=${row.imsrID}" target="_blank"><fewi:super value="${row.imsrID}"/></a>
				    			</c:if></td>
				    		<td><c:if test="${not empty row.repository}">
				    			${row.repository}
				    			</c:if></td>
				    		<td><c:if test="${not empty row.imsrStrain}">
				    			<fewi:super value="${row.imsrStrain}"/>
				    			</c:if></td>
				    		<td><c:if test="${not empty row.matchType}">
				    			${row.matchType}
				    			</c:if></td>
				    	</tr>
				    </c:forEach> 
					</table>
			    </div>
		</div>
	</div>
	<script>
	// fix width of DIV containing table and adjust the header color
	$('#imsrSummaryTable th').css('background-color', $('#imsrRibbonLabel').css('background-color'));
	</script>