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
			<div class="left refToggle" style="float:left;">
				Return SNPs with alleles in the selected strains:<br/><br/>
				<fewi:radio name="searchBySameDiff" divider="<br/>" idPrefix="searchBySameDiffOptionsList" items="${searchBySameDiffOptions}" value="${snpQueryForm.searchBySameDiff}" />
				(only applies if a Reference is selected)<br/>
			</div>
		</td>
	</tr>

<script>
// handler for the radio buttons, determining whether we are in Reference mode or just Comparison mode
$('input[name=referenceMode]').click(function() {
	if ($('input[name=referenceMode]:checked').length == 1) {
		if ($('input[name=referenceMode]:checked')[0].value == 'yes') {
			$('.refToggle').removeClass('refHide');
		} else {
			$('.refToggle').addClass('refHide');
		}
	}
});
<c:if test="${not empty snpQueryForm.referenceStrainList}">
	// show Reference strain controls
	$('input[name=referenceMode]')[1].click();
</c:if>
<c:if test="${empty snpQueryForm.referenceStrainList}">
	// show only Comparison strain controls
	$('input[name=referenceMode]')[0].click();
</c:if>

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