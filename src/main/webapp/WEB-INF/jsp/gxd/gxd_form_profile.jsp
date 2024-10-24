<form:form commandName="gxdProfileQueryForm" id="gxdProfileQueryForm" class="gxdQf classical">
<table class="pad5 borderedTable" width="100%">
	<tr>
		<td colspan="3" align="left">
			<input class="buttonLabel" value="Search" type="submit"  id="submit3">
			&nbsp;&nbsp;
			<input type="reset" id="reset3">
			&nbsp;&nbsp;
			Search for genes by expression profile.
			<br />
			<br />

			<input type="radio" id="profileModeC" name="profileMode" value="classical" checked="true" onchange="profileSetMode()" />
			<label for="profileModeC"> 
			    Classical Expression <span>Assays
				<img id="gxdProfileClassicalHelpImage" src="${configBean.WEBSHARE_URL}images/help_icon.png" />
                                <div id="gxdProfileClassicalHelp">
                                        <div class="hd">Classical expression assays ...</div>
                                        <div class="bd">
					<p>
					Include:
					<ul>
					<li> In situ assays
					    <ul>
					    <li> Immunohistochemistry</li>
					    <li> In situ reporter (knock in)</li>
					    <li> RNA in situ hybridization</li>
					    </ul>
					</li>
					<li> Blot assays
					    <ul>
					    <li> Northern blot</li>
					    <li> Nuclease S1</li>
					    <li> RNase protection</li>
					    <li> RT-PCR</li>
					    <li> Western blot</li>
					    </ul>
					</li>
					</ul>
					</p>
                                        </div>
                                 </div>
			</label>
			<input type="radio" id="profileModeR" name="profileMode" value="rnaseq" onchange="profileSetMode()" />
			<label for="profileModeR">RNA-Seq</label>
		</td>
	</tr>
	<tr class="stripe1">
		<td class="cat1Gxd"></td>
		<td>
		<div class="floatLeft">

		<table id="profileStructureTable" style="border:none;" class="hideStages" >

		<!-- ---------------------------------------- -->
		<tbody id="topSection">
		<tr>
		<td > Find genes where expression is <span class="bold">Detected</span> </td>
		<td colspan="2">
		    <input type="checkbox" id="profileShowStagesCheckbox" name="profileShowStagesCheckbox" 
		        value="true" onchange="profileToggleShowStages()" /> 
			    <span>Show stage selectors
				<img id="gxdProfileStageHelpImage" src="${configBean.WEBSHARE_URL}images/help_icon.png" />
                                <div id="gxdProfileStageHelp">
                                        <div class="hd">Show stage selectors ...</div>
                                        <div class="bd">
					    <p> You can restrict your query to specific stages for each structure specified.
					    You can also query for stages without specifying a structure.
					    NOTE: that when you toggle this button, previous stage specifications (if any) are discarded.
					    </p>
                                        </div>
                                 </div>
			    </span>
		</tr>
		<tr>
		<td colspan="2">In Structure(s)</td>
		<td><span class="stagesLabel" title="You can limit Theiler Stages separately for each structure.">Stages</span></td>
		</tr>
		</tbody>

		<!-- ---------------------------------------- -->
		<tbody id="detectedSection">
		    <!-- contents injected by gxd_query.js -->
		</tbody>

		<!-- ---------------------------------------- -->
		<tbody id="afterDetectedSection">
		    <tr>
		    <td colspan="3">

		    <span class="bold">Add Structure:</span>
                    <button type="button" id="addDetected" onClick="addProfileRow(true)" 
		        style="margin-left:20px;">detected in</button>
		    </td>
		    </tr>
		</tbody>

		<!-- ---------------------------------------- -->
		<tbody id="beforeNotDetectedSection">
		    <tr>
		    <td colspan="3">
		    And <span class="bold red">NOT</span> Detected 
		              <span class="classicalOnly">or Analyzed
				<img id="gxdProfileHelpImage" src="${configBean.WEBSHARE_URL}images/help_icon.png" />
                                <div id="gxdProfileHelp">
                                        <div class="hd"><b>NOT</b> detected or analyzed in...</div>
                                        <div class="bd">
                                                <p>The search will return genes whose expression was absent (not detected),
                                                as well as genes whose expression has not been analyzed or recorded in the database for the specified structure.</p>
                                        </div>
                                 </div>
			      </span>

		    </td>

		    </tr>
		    <tr class="classicalOnly">
		    <td colspan="3">
		    <label id="" style="">
				<input type="checkbox" id="profileNowhereElseCheckbox" name="profileNowhereElseCheckbox" value="true" 
				       onchange="profileSetNotMode()">
				<span id="nowhereElseText" class=""> Anywhere else </span>
			</label>
		    </td>
		    </tr>
		    <tr class="classicalOnly">
		    <td colspan="2" >
			<label id="" style="">
				<input type="checkbox" id="profileNotInStructuresCheckbox" name="profileNotInStructuresCheckbox" value="true"
				       onchange="profileSetNotMode()">
				<span id="profileNotInStructuresText" class=""> In structure(s) </span>
			</label>
		    </td>
		    <td><span class="stagesLabel" title="You can limit Theiler Stages separately for each structure.">Stages</span></td>
		    </tr>
		    <tr class="rnaseqOnly">
		    <td colspan="2">In Structure(s)</td>
		    <td><span class="stagesLabel" title="You can limit Theiler Stages separately for each structure.">Stages</span></td>
		    </tr>
		</tbody>

		<!-- ---------------------------------------- -->
		<tbody id="notDetectedSection">
		    <!-- contents injected by gxd_query.js -->
		</tbody>

		<!-- ---------------------------------------- -->
		<tbody id="afterNotDetectedSection">
		<tr>
		<td colspan="3">


		    <span class="bold">Add Structure:</span>
                    <button type="button" id="addNotDetected" onClick="addProfileRow(false)" style="margin-left:20px;"><span>NOT</span> detected in</button>

		</td>
		</tr>
		</tbody>

		</table>
	</td>
	</tr>

	<tr>
		<td colspan="3" align="left">
			<input class="buttonLabel" value="Search" type="submit"  id="submit4">
			&nbsp;&nbsp;
			<input type="reset" id="reset4">
		</td>
	</tr>
</table>
</form:form>
