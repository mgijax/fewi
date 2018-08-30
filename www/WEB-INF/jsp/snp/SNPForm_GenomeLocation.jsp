	<tr class="stripe1">
		<td class="cat1">Genome Region</td>
		<td>
			<div>
				<div style="float: left; height: 95px; margin-right: 40px;">
					<span class="label"><a onclick="javascript:openUserhelpWindow('${userhelppage}#chromosome'); return false;" href="${userhelpurl}#chromosome">Chromosome:</a></span><br/>
					<fewi:select name="selectedChromosome" id="chromosomeDropList" items="${chromosomes}" value="${snpQueryForm.selectedChromosome}" />
				</div>

				<div style="float: left; height: 95px; margin-right: 40px;">
					<span class="label"><a onclick="javascript:openUserhelpWindow('${userhelppage}#coordinates'); return false;" href="${userhelpurl}#coordinates">Genome Coordinates:</a></span> <i>(from ${assemblyVersion})</i><br/>
					<input name="coordinate" value="${snpQueryForm.coordinate}">
					<fewi:select name="coordinateUnit" id="coordinateUnitDropList" items="${coordinateUnits}" value="${snpQueryForm.coordinateUnit}" /><br>
					<span class="example">
						Examples:<br>
						125.618-125.622 Mbp<br>
						1-125618205 bp<br>
						125618205 - 999999999 bp<br>
					</span>
				</div>
				
				<div style="clear:both;">
					<span class="label">
					<a onclick="javascript:openUserhelpWindow('${userhelppage}#marker_range'); return false;" href="${helpPage}#marker_range">Marker Range</a></span>:
					<span class="example">use current symbols</span><br/>
					between
					<input name="startMarker" size="12" value="${snpQueryForm.startMarker}">
					and <input name="endMarker" size="12" value="${snpQueryForm.endMarker}">
					<br/>
	      			<span class="example" style="margin-left:50px;">Example: between D19Mit32 and Tbx10</span>
				</div>

				<div style="height: 30px; float: left; position: relative;">
					<div style="position: absolute; bottom: 0; width: 385px;">
						You can filter SNPs by function class in the search results.
					</div>
				</div>
			</div>
		</td>
	</tr>
