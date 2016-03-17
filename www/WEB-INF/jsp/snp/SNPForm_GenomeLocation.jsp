	<tr class="stripe1">
		<td class="cat1">Genome Region</td>
		<td>
			<div>
				<div style="float: left; height: 120px; margin-right: 40px;">
					<span class="label"><a onclick="javascript:openUserhelpWindow('${userhelppage}#chromosome'); return false;" href="${userhelpurl}#chromosome">Chromosome:</a></span><br/>
					<fewi:select name="selectedChromosome" id="chromosomeDropList" items="${chromosomes}" value="${snpQueryForm.selectedChromosome}" />
				</div>

				<div style="float: left; height: 105px; margin-right: 40px;">
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

				<div style="height: 105px; float: left; position: relative;">
					<div style="position: absolute; bottom: 0; width: 385px;">
						You can filter SNPs by function class in the search results.
					</div>
				</div>
			</div>
		</td>
	</tr>
