<%--
	Sub template for rendering the Strain query form
--%>

<form method="GET" action="${configBean.FEWI_URL}strain/summary" id="strainForm" name="strainQueryForm">

	<%-- Wrapper for positioning "Search" goButton --%>
	<div class="wrapper">
		<label class="searchLabel">Name</label>

		<div style='display: inline-block'>
		<input type="text" size="40" name="strainName" style="margin-bottom:8px" id="strainNameAC"
			placeholder="name, synonym, on ID"
			value="<c:out value="${strainQueryForm.strainName}"/>" />
		</div>
		<div style='float: right; padding-right: 20px'>
			<button class="goButton">Search</button>
		</div>

		<label class="searchLabel">Type</label>
		<div>
			<div style="display: inline-block">
	      	<fewi:checkboxOptions items="${strainTypeChoices1}" name="strainType" values="${strainQueryForm.strainType}" />
			</div>
			<div style="display: inline-block">
	      	<fewi:checkboxOptions items="${strainTypeChoices2}" name="strainType" values="${strainQueryForm.strainType}" />
			</div>
			<div style="display: inline-block">
	      	<fewi:checkboxOptions items="${strainTypeChoices3}" name="strainType" values="${strainQueryForm.strainType}" />
			</div>
		</div>
	</div>

</form>