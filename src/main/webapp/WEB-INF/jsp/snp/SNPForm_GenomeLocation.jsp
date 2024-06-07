	<tr class="stripe1">
		<td class="cat1">Genome Region</td>
		<td>
			<div>
				<div style="float: left; height: 95px; margin-right: 40px;">
					<span class="label"><a onclick="javascript:openUserhelpWindow('${userhelppage}#chromosome'); return false;" href="${userhelpurl}#chromosome">Chromosome:</a></span><br/>
					<fewi:select name="selectedChromosome" id="chromosomeDropList" items="${chromosomes}" value="${e:forHtml(snpQueryForm.selectedChromosome)}" />
				</div>

				<div style="float: left; height: 95px; margin-right: 40px;">
					<span class="label"><a onclick="javascript:openUserhelpWindow('${userhelppage}#coordinates'); return false;" href="${userhelpurl}#coordinates">Genome Coordinates:</a></span> <i>(from ${assemblyVersion})</i><br/>
					<input name="coordinate" value="${e:forHtml(snpQueryForm.coordinate)}">
					<fewi:select name="coordinateUnit" id="coordinateUnitDropList" items="${coordinateUnits}" value="${e:forHtml(snpQueryForm.coordinateUnit)}" /><br>
					<span class="example">
						Examples:<br>
						125.618-125.622 Mbp<br>
						1-125618205 bp<br>
						125618205 - 999999999 bp<br>
					</span>
				</div>
				
				<div id="outOfSyncLocationMessage" style="display:none; color: red;">
					--This section is temporarily disabled due to a discrepancy between the genome coordinates of RefSNPs and MGI genes.--
				</div>

				<div style="clear:both;">
					<span class="label">
					<a onclick="javascript:openUserhelpWindow('${userhelppage}#marker_range'); return false;" href="${helpPage}#marker_range">Marker Range</a></span>:
					<span class="example">use current symbols</span><br/>
					between
					<input name="startMarker" size="12" value="${e:forHtml(snpQueryForm.startMarker)}">
					and <input name="endMarker" size="12" value="${e:forHtml(snpQueryForm.endMarker)}">
					<br/>
	      			<span class="example" style="margin-left:50px;">Example: between D19Mit32 and Tbx10</span>
				</div>

			</div>
		</td>
	</tr>
