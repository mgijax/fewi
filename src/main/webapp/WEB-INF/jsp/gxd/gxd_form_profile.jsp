<form:form commandName="gxdProfileQueryForm" id="gxdProfileQueryForm" class="gxdQf">
<table class="pad5 borderedTable" width="100%">
	<tr>
		<td colspan="3" align="left">
			<input class="buttonLabel" value="Search" type="submit"  id="submit3">
			&nbsp;&nbsp;
			<input type="reset" id="reset3">
			&nbsp;&nbsp;
			Search for genes by expression profile.
			<span class="noModeSelected"><b>Select an Assay Type to activate the form.</b></span>
		</td>
	</tr>
	<tr class="stripe1">
		<td class="cat1Gxd"></td>
		<td>
		<div class="floatLeft">

		<table id="profileFormModeTable" style="border:none;" >
		<tr>
		<td colspan="5" style="padding-bottom: 20px; border:none;">
		    <span id="profileModeHandIcon" class="noModeSelected">&#x261E;</span>
		    <input type="radio" id="profileModeC" name="profileMode" value="classical" onchange="profileSetMode()" />
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
		</table>



		<table id="profileStructureTable" style="border:none;" class="hideStages disabled" >

		<!-- ---------------------------------------- -->
		<tbody id="topSection">
		<tr>
		<td colspan="2">
		    Find genes where expression is:
		    </td>
		    <td colspan="3">
		    <input type="checkbox" id="profileShowStagesCheckbox" name="profileShowStagesCheckbox" 
		        value="true" onchange="profileShowStagesChanged()" /> 
			    <span>Show Theiler Stage selectors
				<img id="gxdProfileStageHelpImage" src="${configBean.WEBSHARE_URL}images/help_icon.png" />
                                <div id="gxdProfileStageHelp">
                                        <div class="hd">Show Theiler Stage selectors ...</div>
                                        <div class="bd">
					    <p> You can restrict your query to specific Theiler Stages for each structure specified.
					    You can also query for stages without specifying a structure.
					    NOTE: that when you toggle this button, previous stage specifications (if any) are discarded.
					    </p>
                                        </div>
                                 </div>
			    </span>
		</td>
		</tr>
		<tr>
		    <td>
		        <span>Detected</span>
		    </td>
		    <td>
		        <span id="profileNotDetectedText">Not<br/>detected
				<img id="gxdProfileNdHelpImage" src="${configBean.WEBSHARE_URL}images/help_icon.png" />
                                <div id="gxdProfileNdHelp">
                                        <div class="hd">
					    <span class="classicalOnly">NOT detected or analyzed in...</span>
					    <span class="rnaseqOnly">NOT detected in...</span>
					</div>
                                        <div class="bd">
					    <p class="classicalOnly"> The search will return genes whose expression was absent (not detected),
					    as well as genes whose expression has not been analyzed or recorded in the
					    database for the specified structure.
					    </p>
					    <p class="rnaseqOnly">
					    The search will return genes whose expression is below the TPM cutoff value (&lt;0.5 TPM) for the specified structure.
					    </p>
                                        </div>
                                </div>
			</span>
		    </td>
		    <td>
		        <span>Anatomical Structure</span>
		    </td>
		    <td>
		        <span class="stagesLabel" >and/or</span>
		    </td>
		    <td>
		        <span class="stagesLabel" >Theiler Stage(s)</span>
		    </td>
		    <td>
		        <span></span>
		    </td>
		</tr>
		</tbody>

		<!-- ---------------------------------------- -->
		<tbody id="detectedSection">
		    <!-- contents injected by gxd_query.js -->
		</tbody>

		<!-- ---------------------------------------- -->
		<tbody id="afterDetectedSection">
		    <tr>
		    <td colspan="5">

                    <button type="button" id="addDetected" onClick="addProfileRow()" 
		        style="margin-left:20px;">Add structure</button>
		    </td>
		    </tr>
                    <tr class="classicalOnly">
		    <td></td>
                    <td colspan="4" style="text-align:left;padding-left:50px;">
                    <label id="" style="">
                                <input type="checkbox" id="profileNowhereElseCheckbox" name="profileNowhereElseCheckbox" value="true" 
                                       onchange="profileNowhereElseChecked()">
                                <span id="nowhereElseText" class=""> Not detected or analyzed anywhere else </span>
                        </label>
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
			&nbsp;&nbsp;
			<span>Expression Profile Search limited to expression data from wild-type specimens.</span>
		</td>
	</tr>
</table>
</form:form>
