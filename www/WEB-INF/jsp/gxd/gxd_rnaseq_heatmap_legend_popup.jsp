<%--GXD matrix legend popup --%>
<div class="hd">
<div style="float:right">Morpheus <img src="${configBean.FEWI_URL}assets/images/static/morpheus_icon.png" height="40" /></div>
<div style="float:left"><img src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="40" /></div>
</div>

<style>
.tpmColorCell { height: 25px; width: 40px;
	border: 1px solid black;
}
.tpmCell {
	height: 25px; width: 40px; text-align: left; font-size: 80%; vertial-align: top;
	border-left: none; border-right: none; border-bottom: none;
}
.iconCell { text-align: center; }
.iconText { text-align: left; }
.icon { height: 20px; }
</style>
<div class="bd">
	<div class='legendSection'>
		<div style="clear:both; padding-top: 3px;">
			Data file provided by GXD<br/>
			Heat map visualization provided by <a href="${externalUrls.MORPHEUS}" target="_blank" tabindex="-1" style="color:blue">Morpheus</a>
		</div>
		
		<div>
			Each column is a set of combined bioreplicates
			<ul>
			<li> &#x2605; at end of column label indicates mutant</li>
			<li> metadata fields displayed in rows behind label</li>
			<li> field displayed when it contains &gt;1 value</li>
			<li> to sort sets by values in field, click field label</li>
			</ul>

			<p>Hovering over field displays value.</p>
			
			<p>Color key for TPM range</p>
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
					<td class="tpmCell">5000</td>
				</tr>
			</table>
		
			<table>
				<tr>
					<td class="tpmColorCell" style="background-color: #FFFFFF"></td>
					<td style="padding-left: 3px;" rowspan="2"> no TPM to display, either because that gene was not analyzed or because of expression value filtering done at GXD</td>
				</tr>
				<tr><td></td></tr>
			</table>
			
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
			
			Clustering tools can be found in the Tools dropdown.<br/>
			Save Session and Save Dataset can be found in the File dropdown.<p/>
			
			<a href="${externalUrls.MORPHEUS}/documentation.html" target="_blank" tabIndex="-1" style="color:blue">Documentation</a>
			and
			<a href="${externalUrls.MORPHEUS}/tutorial.html" target="_blank" tabIndex="-1" style="color:blue">tutorial</a>
			can be found at Morpheus.
		</div>
	</div><!-- legendSection -->
</div><!-- div bd -->
