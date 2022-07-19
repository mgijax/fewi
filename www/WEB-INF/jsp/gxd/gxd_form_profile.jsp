<style>
#profileStructureTable td {
	border:none;
}
</style>
<form:form commandName="gxdProfileQueryForm" id="gxdProfileQueryForm" class="gxdQf">
<table class="pad5 borderedTable" width="100%">
	<tr>
		<td colspan="3" align="left">
			<input class="buttonLabel" value="Search" type="submit"  id="submit3">
			&nbsp;&nbsp;
			<input type="reset" id="reset3">
			&nbsp;&nbsp;
			Search for genes by expression profile.
<!--			<div id="profileError" class="small error" style="display:none; white-space:nowrap;padding-bottom:4px;padding-top:4px;"></div>
-->
		</td>
	</tr>
	<tr class="stripe1">
		<td class="cat1Gxd"></td>
		<td>
		<div class="floatLeft">
		Find genes where expression is <br/>

		<table id="profileStructureTable" style="border:none;">
		<thead>
			<tr>
				<th class="" style="border:none;"></th>
				<th class="" style="border:none;">Detected</th>
				<th class="" style="border:none;">Not </br>Detected </th>
			</tr>
		</thead>
		<tbody id="profileStructureTableBody">
			<tr>
				<td style="border:none;">
					<input style="width: 320px; position: relative;" id="profileStructure1" name="structure" placeholder="anatomical structure"></input>
					<input type="hidden" id="profileStructure1ID" name="structureID" value=""/>
					<div id="profileStructureContainer1"></div>
				</td>
				<td style="border:none;"><input type="radio" name="detected_1" value="true" checked onChange="structureRadioChange()"/></td>
				<td style="border:none;"><input type="radio" name="detected_1" value="false" class="notDetected" onChange="structureRadioChange()"/></td>
			</tr>
			<tr>
				<td style="border:none;">
					<input style="width: 320px; position: relative;" id="profileStructure2" name="structure" placeholder="anatomical structure"></input>
					<input type="hidden" id="profileStructure2ID" name="structureID" value=""/>
					<div id="profileStructureContainer2"></div>				</td>
				<td style="border:none;"><input type="radio" name="detected_2" value="true" checked onChange="structureRadioChange()"/></td>
				<td style="border:none;"><input type="radio" name="detected_2" value="false" class="notDetected" onChange="structureRadioChange()"/></td>
			</tr>
			<tr>
				<td style="border:none;">
					<input style="width: 320px; position: relative;" id="profileStructure3" name="structure" placeholder="anatomical structure"></input>
					<input type="hidden" id="profileStructure3ID" name="structureID" value=""/>
					<div id="profileStructureContainer3"></div>				</td>
				<td style="border:none;"><input type="radio" name="detected_3" value="true" checked onChange="structureRadioChange()" /></td>
				<td style="border:none;"><input type="radio" name="detected_3" value="false" class="notDetected" onChange="structureRadioChange()"/></td>
			</tr>
			<!-- additional rows added via js -->
		</tbody>
		</table>
		<div>
			<button type="button" class="addButton" onClick="handleAddStructure()" style="margin-left:220px;">Add structure</button>
		</div>
			<label id="allInSituLabel" style="margin-left:400px;">
				<input type="checkbox" id="nowhereElseCheckbox" name="" onChange="handleNowhereElse()" class="" />
						<span id="nowhereElseText" class=""> Expression not detected anywhere else </span>
			</label>

		</div>
		</td>
	</tr>

	<tr>
		<td colspan="3" align="left">
			<input class="buttonLabel" value="Search" type="submit"  id="submit4">
			&nbsp;&nbsp;
			<input type="reset" id="reset4">
			&nbsp;&nbsp;
			<span style='font-weight: bold;'>RNA-Seq data not yet included in this search. </span>			
		</td>
	</tr>
</table>
</form:form>