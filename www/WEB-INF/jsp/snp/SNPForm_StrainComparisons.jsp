	<tr class="stripe1">
		<td class="cat1">
			Strains and<br>
			Strain Comparisons
		</td>
		<td>
			<div class="left" style="float: left;margin-bottom: 15px;">
				<span class="label">Compare to one or more Reference strains?</span>
				<label><input type="radio" name="referenceMode" value="no"> No</labeL>
				<label><input type="radio" name="referenceMode" value="yes"> Yes</labeL>
				<br/><br/>
				<input type="button" id="selectButton" value="Select All"/>
				<input type="button" id="deselectButton" value="Clear All"/><br/><br/>
				<style>
					.checkbox {
						width: 210px;
						float: left;
					}
				</style>
				<div>
					<c:if test="${empty snpQueryForm.selectedStrains}">
						<fewi:checkboxStrainsGrid name="selectedStrains" refName="referenceStrain" width="4" items="${selectableStrains}" values="${selectedStrains}" />
					</c:if>
					<c:if test="${not empty snpQueryForm.selectedStrains}">
						<fewi:checkboxStrainsGrid name="selectedStrains" refName="referenceStrain" width="4" items="${selectableStrains}" values="${snpQueryForm.selectedStrains}" refValues="${snpQueryForm.referenceStrainList}"/>
					</c:if>
				</div>
			</div>
			<div class="refToggle rightColumn" style="float:left;">
				<div id="legendWrapper">
				<span id="rcLegendTitle">Legend</span><br/>
					<div id="rcTableWrapper">
						<table id="rcLegend">
						<tr><td class="refColor">R</td><td><span class="refColor">Reference</span> strains</td></tr>
						<tr><td class="cmpColor">C</td><td><span class="cmpColor">Comparison</span> strains</td></tr>
						</table>
					</div>
				</div>
				<div id="sameDiffWrapper" class="left">
				Return SNPs with alleles in the selected strains:<br/><br/>
				<fewi:radio name="searchBySameDiff" divider="<br/>" idPrefix="searchBySameDiffOptionsList" items="${searchBySameDiffOptions}" value="${snpQueryForm.searchBySameDiff}" />
				(only applies if a Reference is selected)<br/>
				</div>
			</div>
		</td>
	</tr>

<script>
// handler for the radio buttons, determining whether we are in Reference mode or just Comparison mode
$('input[name=referenceMode]').on('click', function(e) {
    var formID = 'form' + $(".ui-tabs-active").attr('aria-controls').split('-')[1];
	if (this.value == 'yes') {
		$('.refToggle').removeClass('refHide');
		// also keep the other form's selection in sync
		if (formID == 'form1') {
			$('#form2 input[name=referenceMode][value=yes]')[0].checked = true;		
		} else {
			$('#form1 input[name=referenceMode][value=yes]')[0].checked = true;		
		}
	} else {
		$('.refToggle').addClass('refHide');
		// also keep the other form's selection in sync
		if (formID == 'form1') {
			$('#form2 input[name=referenceMode][value=no]')[0].checked = true;		
		} else {
			$('#form1 input[name=referenceMode][value=no]')[0].checked = true;		
		}
	}
});

// wire up the checkboxes so that - for any given strain - only one checkbox can be checked (both forms at once)
// and so that checking a box on one form also checks the same box on the other form
$('input[name=selectedStrains]').click(function (e) {
	var strainClicked = e.target.value;
	var boxes = $('input[name=referenceStrain][value="' + strainClicked + '"]');
	for (var i in boxes) {
		boxes[i].checked = false;
	}
	boxes = $('input[name=selectedStrains][value="' + strainClicked + '"]');
	for (var i in boxes) {
		boxes[i].checked = e.target.checked;
	}
});
$('input[name=referenceStrain]').click(function (e) {
	var strainClicked = e.target.value;
	var boxes = $('input[name=selectedStrains][value="' + strainClicked + '"]');
	for (var i in boxes) {
		boxes[i].checked = false;
	}
	boxes = $('input[name=referenceStrain][value="' + strainClicked + '"]');
	for (var i in boxes) {
		boxes[i].checked = e.target.checked;
	}
});
</script>