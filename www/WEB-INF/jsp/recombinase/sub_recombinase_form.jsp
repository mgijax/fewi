<%--
	Sub template for rendering the Recombinase query form
--%>

<form method="GET" action="${configBean.FEWI_URL}recombinase/summary" id="creForm" name="recombinaseQueryForm">

	<label class="searchLabel">Recombinase activity assayed <!-- and
	   <label class="detectedLabel" for="creDetected">
	     <input type="checkbox" name="detected" id="creDetected" value="true"
	   	   <c:if test="${recombinaseQueryForm.detected}">checked</c:if> />observed
	   </label>
	   <label class="detectedLabel" for="creNotDetected">
	     /<input type="checkbox" name="notDetected" id="creNotDetected" value="true"
	   	   <c:if test="${recombinaseQueryForm.notDetected}">checked</c:if> />absent
	   </label>
	   -->
	 in</label>

	<%-- Wrapper for positioning "Search" goButton --%>
	<div class="wrapper">
		<input type="text" size="40" name="structure" id="creStructureAC"
			placeholder="any anatomical structure"
			value="<c:out value="${recombinaseQueryForm.structure}"/>" />

		<span id="creAndDivider">AND</span>

		<label class="searchLabel">Recombinase driven by</label>
		<input type="text" size="40" name="driver" id="creDriverAC"
			placeholder="any driver or promoter"
			value="<c:out value="${recombinaseQueryForm.driver}"/>" />
	</div>
	<button class="goButton">Search</button>

</form>