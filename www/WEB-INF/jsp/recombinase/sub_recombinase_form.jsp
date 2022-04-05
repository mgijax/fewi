<%--
	Sub template for rendering the Recombinase query form
--%>

<form method="GET" target="_blank" action="${configBean.FEWI_URL}recombinase/summary" id="creForm" name="recombinaseQueryForm">

	<label class="searchLabel">Recombinase activity profile</label>
        
        <button type="button" class="addButton" style="margin-left:40px;">Add structure</button>
        <%-- <button type="button" class="removeButton">Remove structure</button> --%>
        <%-- multiple structres with detected/not-detected encoded into this field for 
             easier  processing on the server side. E.g., "heart and not liver" would be
             encoded as "heart|-liver" (see recombinase_form.js) --%>
        <input type="hidden" name="structures" value="" />

	<%-- Wrapper for positioning "Search" goButton --%>
        <table class="wrapper-table">
        <tbody>

        <tr>
        <td>
            <div class="wrapper">
		<table class="structure-table">
                <thead>
		        <tr> <th></th> <th>Detected</th> <th class="nd-header">Not detected</th> </tr>
                </thead>
                <tbody>
                    <!-- rows added by www/js/src/recombinase/recombinase_form.js --></tbody>
		</table>


		<label class="nowhereElseLabel" for="nowhereElse">
			<input type="checkbox" name="nowhereElse" id="nowhereElse" value="true" />
                        and nowhere else
		</label>

                <span id="creAndDivider">AND</span>

		<label class="searchLabel">Recombinase driven by</label>
		<input type="text" size="40" name="driver" id="creDriverAC"
			placeholder="any driver or promoter"
			value="" />
            </div>
        </td>
        <td class="search-cell">
            <button type="submit" class="goButton">Search</button>
        </td>
        </tr>

        </tbody>
        </table>

</form>
