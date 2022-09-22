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
				<th class="" style=""></th>
				<th class="" style="">Detected</th>
				<th class="" style="">Not </br>Detected 
					<img id="gxdProfileHelpImage" src="${configBean.WEBSHARE_URL}images/help_icon.png" />
					<div id="gxdProfileHelp">
						<div class="hd"><b>NOT</b> detected or analyzed in...</div>
						<div class="bd">
							<p style="font-weight: normal;">The search will return genes whose expression was absent (not detected),
							as well as genes whose expression has not been analyzed or recorded in the database for the specified structure.</p>
						</div>
					</div>
				</th>
			</tr>
		</thead>
		<tbody id="profileStructureTableBody">
			<tr id="profileStructureRow1" >
				<td style="">
					<button type="button" onClick="removeStructureRow(1)" id="removeStructureRowButton1" class="removeButton" title="Remove this structure.">X</button>
					<input style="width: 320px; position: relative;" id="profileStructure1" name="profileStructure1" placeholder="anatomical structure"></input>
					<input type="hidden" id="profileStructure1ID" name="profileStructureID1" value=""/>
					<div class="anatomyAC" style="width: 400px;" id="profileStructureContainer1"></div>
				</td>
				<td style=""><input type="radio" name="detected_1" value="true"  id="profileDetected1" checked onChange="structureRadioChange()"/></td>
				<td style=""><input type="radio" name="detected_1" value="false" id="profileNotDetected1" class="notDetected" onChange="structureRadioChange()"/></td>
			</tr>
			<tr id="profileStructureRow2" >
				<td style="">
					<button type="button" onClick="removeStructureRow(2)" class="removeButton" title="Remove this structure.">X</button>
					<input style="width: 320px; position: relative;" id="profileStructure2" name="profileStructure2" placeholder="anatomical structure"></input>
					<input type="hidden" id="profileStructure2ID" name="profileStructureID2" value=""/>
					<div class="anatomyAC" style="width: 400px;" id="profileStructureContainer2"></div>				
				</td>
				<td style=""><input type="radio" name="detected_2" value="true"  id="profileDetected2" checked onChange="structureRadioChange()"/></td>
				<td style=""><input type="radio" name="detected_2" value="false" id="profileNotDetected2" class="notDetected" onChange="structureRadioChange()"/></td>
			</tr>
			<tr id="profileStructureRow3" >
				<td style="">
					<button type="button" onClick="removeStructureRow(3)" class="removeButton" title="Remove this structure.">X</button>
					<input style="width: 320px; position: relative;" id="profileStructure3" name="profileStructure3" placeholder="anatomical structure"></input>
					<input type="hidden" id="profileStructure3ID" name="profileStructureID3" value=""/>
					<div class="anatomyAC" style="width: 400px;" id="profileStructureContainer3"></div>				
				</td>
				<td style=""><input type="radio" name="detected_3" value="true"  id="profileDetected3" checked onChange="structureRadioChange()" /></td>
				<td style=""><input type="radio" name="detected_3" value="false" id="profileNotDetected3" class="notDetected" onChange="structureRadioChange()"/></td>
			</tr>
			<tr id="profileStructureRow4" style="display:none;">
				<td style="">
					<button type="button" onClick="removeStructureRow(4)" class="removeButton" title="Remove this structure.">X</button>
					<input style="width: 320px; position: relative;" id="profileStructure4" name="profileStructure4" placeholder="anatomical structure"></input>
					<input type="hidden" id="profileStructure4ID" name="profileStructureID4" value=""/>
					<div class="anatomyAC" style="width: 400px;" id="profileStructureContainer4"></div>				
				</td>
				<td style=""><input type="radio" name="detected_4" value="true"  id="profileDetected4" checked onChange="structureRadioChange()" /></td>
				<td style=""><input type="radio" name="detected_4" value="false" id="profileNotDetected4" class="notDetected" onChange="structureRadioChange()"/></td>
			</tr>
			<tr id="profileStructureRow5" style="display:none;">
				<td style="">
					<button type="button" onClick="removeStructureRow(5)" class="removeButton" title="Remove this structure.">X</button>
					<input style="width: 320px; position: relative;" id="profileStructure5" name="profileStructure5" placeholder="anatomical structure"></input>
					<input type="hidden" id="profileStructure5ID" name="profileStructureID5" value=""/>
					<div class="anatomyAC" style="width: 400px;" id="profileStructureContainer5"></div>				
				</td>
				<td style=""><input type="radio" name="detected_5" value="true"  id="profileDetected5" checked onChange="structureRadioChange()" /></td>
				<td style=""><input type="radio" name="detected_5" value="false" id="profileNotDetected5" class="notDetected" onChange="structureRadioChange()"/></td>
			</tr>
			<tr id="profileStructureRow6" style="display:none;">
				<td style="">
					<button type="button" onClick="removeStructureRow(6)" class="removeButton" title="Remove this structure.">X</button>
					<input style="width: 320px; position: relative;" id="profileStructure6" name="profileStructure6" placeholder="anatomical structure"></input>
					<input type="hidden" id="profileStructure6ID" name="profileStructureID6" value=""/>
					<div class="anatomyAC" style="width: 400px;" id="profileStructureContainer6"></div>				
				</td>
				<td style=""><input type="radio" name="detected_6" value="true"  id="profileDetected6" checked onChange="structureRadioChange()" /></td>
				<td style=""><input type="radio" name="detected_6" value="false" id="profileNotDetected6" class="notDetected" onChange="structureRadioChange()"/></td>
			</tr>
			<tr id="profileStructureRow7" style="display:none;">
				<td style="">
					<button type="button" onClick="removeStructureRow(7)" class="removeButton" title="Remove this structure.">X</button>
					<input style="width: 320px; position: relative;" id="profileStructure7" name="profileStructure7" placeholder="anatomical structure"></input>
					<input type="hidden" id="profileStructure7ID" name="profileStructureID7" value=""/>
					<div class="anatomyAC" style="width: 400px;" id="profileStructureContainer7"></div>				
				</td>
				<td style=""><input type="radio" name="detected_7" value="true"  id="profileDetected7" checked onChange="structureRadioChange()" /></td>
				<td style=""><input type="radio" name="detected_7" value="false" id="profileNotDetected7" class="notDetected" onChange="structureRadioChange()"/></td>
			</tr>
			<tr id="profileStructureRow8" style="display:none;">
				<td style="">
					<button type="button" onClick="removeStructureRow(8)" class="removeButton" title="Remove this structure.">X</button>
					<input style="width: 320px; position: relative;" id="profileStructure8" name="profileStructure8" placeholder="anatomical structure"></input>
					<input type="hidden" id="profileStructure8ID" name="profileStructureID8" value=""/>
					<div class="anatomyAC" style="width: 400px;" id="profileStructureContainer8"></div>				
				</td>
				<td style=""><input type="radio" name="detected_8" value="true"  id="profileDetected8" checked onChange="structureRadioChange()" /></td>
				<td style=""><input type="radio" name="detected_8" value="false" id="profileNotDetected8" class="notDetected" onChange="structureRadioChange()"/></td>
			</tr>
			<tr id="profileStructureRow9" style="display:none;">
				<td style="">
					<button type="button" onClick="removeStructureRow(9)" class="removeButton" title="Remove this structure.">X</button>
					<input style="width: 320px; position: relative;" id="profileStructure9" name="profileStructure9" placeholder="anatomical structure"></input>
					<input type="hidden" id="profileStructure9ID" name="profileStructureID9" value=""/>
					<div class="anatomyAC" style="width: 400px;" id="profileStructureContainer9"></div>				
				</td>
				<td style=""><input type="radio" name="detected_9" value="true"  id="profileDetected9" checked onChange="structureRadioChange()" /></td>
				<td style=""><input type="radio" name="detected_9" value="false" id="profileNotDetected9" class="notDetected" onChange="structureRadioChange()"/></td>
			</tr>
			<tr id="profileStructureRow10" style="display:none;">
				<td style="">
					<button type="button" onClick="removeStructureRow(10)" class="removeButton" title="Remove this structure.">X</button>
					<input style="width: 320px; position: relative;" id="profileStructure10" name="profileStructure10" placeholder="anatomical structure"></input>
					<input type="hidden" id="profileStructure10ID" name="profileStructureID10" value=""/>
					<div class="anatomyAC" style="width: 400px;" id="profileStructureContainer10"></div>				
				</td>
				<td style=""><input type="radio" name="detected_10" value="true"  id="profileDetected10" checked onChange="structureRadioChange()" /></td>
				<td style=""><input type="radio" name="detected_10" value="false" id="profileNotDetected10" class="notDetected" onChange="structureRadioChange()"/></td>
			</tr>
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