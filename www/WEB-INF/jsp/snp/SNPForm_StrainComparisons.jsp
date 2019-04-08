	<tr class="stripe1">
		<td class="cat1">
			Strains and<br>
			Strain Comparisons
		</td>
		<td>
			<div class="left" style="float: left;margin-bottom: 15px;">
				<div id="controlWrapper">
					<div id="controlWrapperLeft" style="float:left;">
						<span class="label">Compare to one or more Reference strains?</span>
						<label><input type="radio" name="referenceMode" value="no"> No</labeL>
						<label><input type="radio" name="referenceMode" value="yes"> Yes</labeL>
						<br/><br/>
						<style>
							.checkbox {
								width: 210px;
								float: left;
							}
						</style>
						<div id="legendWrapper">
							<div id="rcTableWrapper">
								<table id="rcLegend">
								<tr class="refToggle"><td><span class="refColor">Reference</span> strains</td>
									<td>
									<input type="button" id="refDeselectButton" value="Clear All"/>
									</td>
								</tr>
								<tr><td class="refToggle"><span class="cmpColor">Comparison</span> strains</td>
									<td>
									<input type="button" id="doccSelectButton" value="Select DO/CC Founders"/>
									<input type="button" id="mgpSelectButton" value="Select Sanger MGP Strains"/>
									<input type="button" id="selectButton" value="Select All"/>
									<input type="button" id="deselectButton" value="Clear All"/>
									</td>
								</tr>
								</table>
							</div>
						</div>
					</div>
					<div class="refToggle rightColumn" style="float:left;">
						<div id="sameDiffWrapper" class="left">
							You can select one or more Reference Strains.  If you select multiple Reference Strains,
							the SNPs returned will have the same allele in all Reference Strains.<br/><br/>
							<div id="sameDiffRadioButtons">
							<fewi:radio name="searchBySameDiff" divider="<br/>" idPrefix="searchBySameDiffOptionsList" items="${searchBySameDiffOptions}" value="${snpQueryForm.searchBySameDiff}" />
							(only applies if a Reference is selected)<br/>
							</div>
						</div>
					</div>
				</div>
				<div style="clear:both;">
					<c:if test="${empty snpQueryForm.selectedStrains}">
						<fewi:checkboxStrainsGrid name="selectedStrains" refName="referenceStrains" width="4" items="${selectableStrains}" values="${selectedStrains}" />
					</c:if>
					<c:if test="${not empty snpQueryForm.selectedStrains}">
						<fewi:checkboxStrainsGrid name="selectedStrains" refName="referenceStrains" width="4" items="${selectableStrains}" values="${snpQueryForm.selectedStrains}" refValues="${snpQueryForm.referenceStrains}"/>
					</c:if>
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
		snpqry.refDeselectAll();
	}
});

// wire up the checkboxes so that - for any given strain - only one checkbox can be checked (both forms at once)
// and so that checking a box on one form also checks the same box on the other form
$('input[name=selectedStrains]').click(function (e) {
	var strainClicked = e.target.value;
	var boxes = $('input[name=referenceStrains][value="' + strainClicked + '"]');
	for (var i in boxes) {
		boxes[i].checked = false;
	}
	boxes = $('input[name=selectedStrains][value="' + strainClicked + '"]');
	for (var i in boxes) {
		boxes[i].checked = e.target.checked;
	}
});
$('input[name=referenceStrains]').click(function (e) {
	var strainClicked = e.target.value;
	var boxes = $('input[name=selectedStrains][value="' + strainClicked + '"]');
	for (var i in boxes) {
		boxes[i].checked = false;
	}
	boxes = $('input[name=referenceStrains][value="' + strainClicked + '"]');
	for (var i in boxes) {
		boxes[i].checked = e.target.checked;
	}
});
</script>