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
				<th class="" style=""><span id="notDetectedHeaderText" style="">Not </br>Detected</span> 
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
		<tbody>
			<!-- content inserted by gxd_query.js -->
		</tbody>
		</table>
		<div>
			<button type="button" class="addButton" onClick="addProfileRow(true)" style="margin-left:220px;">Add structure</button>
		</div>
			<label id="" style="margin-left:400px;"> 
				<input type="checkbox" id="profileNowhereElseCheckbox" name="profileNowhereElseCheckbox" 
					onChange="profileNweChange()" class="" value="true" />
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
