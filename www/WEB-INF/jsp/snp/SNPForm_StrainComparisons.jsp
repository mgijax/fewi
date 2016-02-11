	<tr class="stripe1">
		<td class="cat1">
			Strains and<br>
			Strain Comparisons
		</td>
		<td>
			<div class="left" style="float: left;margin-bottom: 15px;">
				<span class="label"><a onclick="javascript:openUserhelpWindow('${userhelppage}#sasc'); return false;" href="${userhelpurl}#sasc">Find SNPs Involving these Strains</a></span><br /><br />
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
						<fewi:checkboxStrainsGrid name="selectedStrains" width="4" items="${selectableStrains}" values="${selectedStrains}" />
					</c:if>
					<c:if test="${not empty snpQueryForm.selectedStrains}">
						<fewi:checkboxStrainsGrid name="selectedStrains" width="4" items="${selectableStrains}" values="${snpQueryForm.selectedStrains}" />
					</c:if>
				</div>
			</div>
			<div class="left" style="float:left;">
				<span class="label"><a onclick="javascript:openUserhelpWindow('${userhelppage}#sasc'); return false;" href="${userhelpurl}#sasc">Reference Strain for Comparison</a></span><br /><br />
				<fewi:select name="referenceStrain" size="1" items="${referenceStrains}" value="${snpQueryForm.referenceStrain}" />
				<br/><br/>

				Return SNPs with alleles in the selected strains:<br/><br/>
				<fewi:radio name="searchBySameDiff" divider="<br/>" idPrefix="searchBySameDiffOptionsList" items="${searchBySameDiffOptions}" value="${snpQueryForm.searchBySameDiff}" />
				(only applies if the Reference is selected)<br/>
			</div>
		</td>
	</tr>
