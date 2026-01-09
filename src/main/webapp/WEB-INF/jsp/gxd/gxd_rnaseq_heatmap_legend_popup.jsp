<%--GXD matrix legend popup --%>
<div class="hd">
	<div style="float:right">Morpheus <img src="${configBean.FEWI_URL}assets/images/static/morpheus_icon.png" height="40" /></div>
	<div style="float:left"><img src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="40" /></div>
</div>

<style>
.legendFont { font-size: 80%; }
.colorKey {}
.tpmColorCell { height: 35px; width: 35px; border: 1px solid black; padding-bottom: 0px; }
.tpmCell {
	height: 25px; width: 35px; text-align: left; vertial-align: top;
	border-left: none; border-right: none; border-bottom: none; font-size: 75%;
	padding-top: 2px;
}
.tpmLegendBracketCell {
	height: 5px; 
	border-left: 1px solid black; border-right: 1px solid black; border-bottom: 1px solid black; border-top: none;
	padding-top: 2px;
}
.tpmLegendCell {
	height: 25px; text-align: center; vertial-align: top;
	border-left: none; border-right: none; border-bottom: none; font-size: 75%;
	padding-top: 2px; padding-bottom: 8px;
}
.iconCell { text-align: center; }
.iconText { text-align: left; font-family: unset; }
.icon { height: 20px; }
.legendSection {}
.legendCredits {}
.legendBioreplicates {}
.legendBullets { padding-top: 0px; }
.legendHover {}
.legendColorKey { margin-top: 5px; }
.legendNan { margin-top: 5px; }
.legendButtons { margin-top: 5px; }
.legendTools { margin-top: 5px; }
.legendDocs { margin-top: 5px; }
</style>

<div class="bd">
	<div class='legendSection legendFont'>
		<div id='legendCredits' style="clear:both; padding-top: 3px; padding-bottom: 10px;">
			Data file provided by GXD<br/>
			Heat map visualization and analysis tools rendered by <a href="/morpheus" target="_blank" tabindex="-1" style="color:blue">Morpheus</a>
		</div>
		
		<div id="legendBioreplicates">
			Each column is a set of combined bioreplicates
			<ul id="legendBullets">
			<li> &#x2605; at end of column label indicates mutant</li>
			<li> metadata fields displayed in rows below label</li>
			<li> field displayed when it contains &gt;1 value</li>
			<li> to sort sets by values in field, click checkered box beside field label</li>
			<li> to set multiple sort levels, hold shift key down when clicking boxes</li>
			</ul>
		</div>

		<div id="legendHover">Hovering over cell displays value.</div>
			
		<div id="legendColorKey">
			Color key for TPM range
			<table id="colorKey">
				<tr>
					<td class="tpmColorCell" style="background-color: #E0E0E0;"></td>
					<td class="tpmColorCell" style="background-color: #98CEF4;"></td>
					<td class="tpmColorCell" style="background-color: #45AFFD;"></td>
					<td class="tpmColorCell" style="background-color: #3292E4;"></td>
					<td class="tpmColorCell" style="background-color: #1E74CA;"></td>
					<td class="tpmColorCell" style="background-color: #105FAD;"></td>
					<td class="tpmColorCell" style="background-color: #024990;"></td>
					<td class="tpmColorCell" style="background-color: #000066;"></td>
					<td class="tpmColorCell" style="background-color: #000000;"></td>
				</tr>
				<tr>
					<td class="tpmCell">0</td>
					<td class="tpmCell">0.5</td>
					<td class="tpmCell">5.75</td>
					<td class="tpmCell">11</td>
					<td class="tpmCell">506</td>
					<td class="tpmCell">1001</td>
					<td class="tpmCell">2001</td>
					<td class="tpmCell">3000</td>
					<td class="tpmCell">5000+</td>
				</tr>
				<tr>
					<td class="tpmLegendBracketCell"></td>
					<td class="tpmLegendBracketCell" colspan="2"></td>
					<td class="tpmLegendBracketCell" colspan="2"></td>
					<td class="tpmLegendBracketCell" colspan="4"></td>
				</tr>
				<tr>
					<td class="tpmLegendCell">Below Cutoff</td>
					<td class="tpmLegendCell" colspan="2">Low</td>
					<td class="tpmLegendCell" colspan="2">Medium</td>
					<td class="tpmLegendCell" colspan="4">High</td>
				</tr>
			</table>
		</div>
		
		<div id="legendNan">
			<table>
				<tr>
					<td style="vertical-align:top;">
					    <div class="tpmColorCell" style="background-color: #FFFFFF"></div>
					</td>
					<td class="legendFont" style="padding-left: 3px;"> 
					no TPM value available (NaN) occurs either because application of a GXD expression value filter excluded 
					the gene from the results for that bioreplicate set or the bioreplicate set was returned because its 
					structure is included in the list of detected structures for an Expression Profile Search but that gene 
					is not detected (below cutoff) in that bioreplicate set.
					</td>
				</tr>
			</table>
		</div>
			
		<div id="legendButtons" style="margin-top: 5px">
			Heat map is customizable
			<table>
				<tr>
					<td class="iconCell"><img class="icon" src="${configBean.FEWI_URL}assets/images/static/heatmap_zoom.png"/></td>
					<td class="iconText">zoom/resizing options</td>
				</tr>
				<tr>
					<td class="iconCell"><img class="icon" src="${configBean.FEWI_URL}assets/images/static/heatmap_options.png"/></td>
					<td class="iconText">display options</td>
				</tr>
				<tr>
					<td class="iconCell"><img class="icon" src="${configBean.FEWI_URL}assets/images/static/heatmap_save.png"/></td>
					<td class="iconText">save image of map</td>
				</tr>
				<tr>
					<td class="iconCell"><img class="icon" src="${configBean.FEWI_URL}assets/images/static/heatmap_filter.png"/></td>
					<td class="iconText">column/row filtering by field</td>
				</tr>
				<tr>
					<td class="iconCell"><img class="icon" src="${configBean.FEWI_URL}assets/images/static/heatmap_key.png"/></td>
					<td class="iconText">TPM color key</td>
				</tr>
			</table>
		</div>
			
		<div id="legendTools">
			Clustering tools can be found in the Tools dropdown.<br/>
			Save Session and Save Dataset can be found in the File dropdown.
		</div>
		
		<div id="legendDocs">	
			<a href="/morpheus/documentation.html" target="_blank" tabIndex="-1" style="color:blue">Documentation</a>
			and
			<a href="/morpheus/tutorial.html" target="_blank" tabIndex="-1" style="color:blue">tutorial</a>
			can be found at Morpheus.
		</div>
	</div><!-- legendSection -->
</div><!-- div bd -->
