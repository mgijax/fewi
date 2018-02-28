<%--
	Sub template for rendering the Recombinase query form
--%>

<form method="GET" action="${configBean.FEWI_URL}recombinase/summary" id="creForm" name="recombinaseQueryForm">

	<label class="searchLabel">Recombinase activity assayed in</label>

	<%-- Wrapper for positioning "Search" goButton --%>
	<div class="wrapper">
		<input type="text" size="40" name="structure" style="margin-bottom:8px" id="creStructureAC"
			placeholder="any anatomical structure"
			value="<c:out value="${recombinaseQueryForm.structure}"/>" />
		<label class="nowhereElseLabel" for="nowhereElse">
			<input type="checkbox" name="nowhereElse" id="nowhereElse" value="true" <c:if
				test="${recombinaseQueryForm.nowhereElse}">checked</c:if> /> and nowhere else
		</label>

		<span id="creAndDivider">AND</span>

		<label class="searchLabel">Recombinase driven by</label>
		<input type="text" size="40" name="driver" id="creDriverAC"
			placeholder="any driver or promoter"
			value="<c:out value="${recombinaseQueryForm.driver}"/>" />
	</div>
	<button class="goButton">Search</button>

</form>