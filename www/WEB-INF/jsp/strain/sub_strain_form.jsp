<%--
	Sub template for rendering the Strain query form
--%>

<form method="GET" action="${configBean.FEWI_URL}strain/summary" id="strainForm" name="strainQueryForm">

	<%-- Wrapper for positioning "Search" goButton --%>
	<div>
		<label class="searchLabel">Name</label>

		<div style='display: inline-block'>
		<input type="text" size="40" name="strainName" style="margin-bottom:8px" id="strainNameAC"
			placeholder="name, synonym, or ID"
			value="<c:out value="${strainQueryForm.strainName}"/>" />
		</div>
		<div style='float: right; padding-right: 20px'>
			<button id="clearButton" class="clearButton">Clear</button>
			<button id="searchButton" class="goButton">Search</button>
		</div>

		<label class="searchLabel">Attributes</label>
		<div>
	        <select name="attributes" id="attributeDropList" multiple="" size="7">
			<fewi:selectOptions items="${attributeChoices}" values="${strainQueryForm.attributes}" />
			</select>
		</div>
	</div>
</form>
<script>
	// clear button should clear both strain box and attribute selection list
	$('#clearButton').click(function () {
		$('#attributeDropList').val([]);
		$('[name=strainName]').val('');
		return false;
	});
	// pressing Enter in the strain box should submit the form
	$('#strainForm').keypress(function (e) {
		if (e.which == 13) {
			$('#searchButton').focus().click();
			return false;	// stop processing the keypress
		}
	});
</script>