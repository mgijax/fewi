<%--
	Sub template for rendering the Recombinase query form
--%>

<form method="GET" action="${configBean.FEWI_URL}recombinase/summary" id="creForm" name="recombinaseQueryForm">

	<label class="searchLabel">Recombinase activity profile</label>
        
        <input type="hidden" name="structures" value="" />

	<%-- Wrapper for positioning "Search" goButton --%>
	<div class="wrapper">
		<table class="structure-table">
                <thead>
		        <tr> <th></th> <th>Detected</th> <th class="nd-header">Not detected</th> </tr>
                </thead>
                <tbody><!-- rows added by www/js/src/recombinase/recombinase_form.js --></tbody>
		</table>


		<label class="nowhereElseLabel" for="nowhereElse">
			<input type="checkbox" name="nowhereElse" id="nowhereElse" value="true" /> and nowhere else
		</label>

		<span id="creAndDivider">AND</span>

		<label class="searchLabel">Recombinase driven by</label>
		<input type="text" size="40" name="driver" id="creDriverAC"
			placeholder="any driver or promoter"
			value="" />
	</div>
        <button type="button" class="addButton">Add structure</button>
	<button type="submit" class="goButton">Search</button>

</form>
