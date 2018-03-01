<%--
	Sub template for rendering the Recombinase query form
--%>

<form method="GET" action="${configBean.FEWI_URL}recombinase/summary" id="creForm" name="recombinaseQueryForm">

	<label class="searchLabel">Recombinase activity <select name="structureOperator" id="structureOperator"
		onChange="structureOperatorChanged()">
		<option value="assayed" selected="true">assayed</option>
		<option value="detected">detected</option>
		</select>
		in</label>

	<%-- Wrapper for positioning "Search" goButton --%>
	<div class="wrapper">
		<input type="text" size="40" name="structure" style="margin-bottom:8px" id="creStructureAC"
			placeholder="any anatomical structure"
			value="<c:out value="${recombinaseQueryForm.structure}"/>" />
		<label class="nowhereElseLabel" for="nowhereElse">
			<input type="checkbox" name="nowhereElse" id="nowhereElse" value="true" 
				onClick="checkboxChanged()"
				<c:if test="${recombinaseQueryForm.nowhereElse}">checked</c:if> /> and nowhere else
		</label>

		<span id="creAndDivider">AND</span>

		<label class="searchLabel">Recombinase driven by</label>
		<input type="text" size="40" name="driver" id="creDriverAC"
			placeholder="any driver or promoter"
			value="<c:out value="${recombinaseQueryForm.driver}"/>" />
	</div>
	<button class="goButton">Search</button>

</form>
<script>
/***--- form automation ---***/

// If the user selected 'assayed', then we need to clear the 'nowhere else' checkbox.
function structureOperatorChanged() {
	if ($('#structureOperator')[0].selectedIndex == 0) {
		$('#nowhereElse')[0].checked = false;
	}
}

// If the user checked the 'nowhere else' checkbox, then we need to ensure the structure
// operator is 'detected'.
function checkboxChanged() {
	if ($('#nowhereElse')[0].checked) {
		$('#structureOperator')[0].selectedIndex = 1;
	}
}
</script>