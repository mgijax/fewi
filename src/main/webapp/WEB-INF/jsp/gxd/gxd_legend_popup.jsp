<%--GXD matrix legend popup --%>
<div class="hd">Matrix Legend</div>
  <div class="bd">

	<div class='legendWrapper'>
	
	<div class='legendSection'>
	
		<h4>Number of expression results annotated as</h4>
		
		<div>present in structure and/or substructures</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell blue5'></div>
			<span class='legendExampleRange'> > 10,000 </span>
		</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell blue4'></div>
			<span class='legendExampleRange'> 1,001 - 10,000 </span>
		</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell blue3'></div>
			<span class='legendExampleRange'> 51 - 1,000 </span>
		</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell blue2'></div>
			<span class='legendExampleRange'> 5-50 </span>
		</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell blue1'></div>
			<span class='legendExampleRange'> 1-4 </span>
		</div>
	</div>

	<div class='legendSection'>
		<div>absent in structure</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell red5'></div>
			<span class='legendExampleRange'> > 10,000 </span>
		</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell red4'></div>
			<span class='legendExampleRange'> 1,001 - 10,000 </span>
		</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell red3'></div>
			<span class='legendExampleRange'> 51 - 1,000 </span>
		</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell red2'></div>
			<span class='legendExampleRange'> 5-50 </span>
		</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell red1'></div>
			<span class='legendExampleRange'> 1-4 </span>
		</div>
	</div>


	<div class='legendSection'>
		
		<h4>Other Symbols</h4>
		<div class='legendExampleCell posNegCell'>
		  <div class='hiddenNegIndicatorWrapper'>
		  	<div class='hiddenNegIndicator'></div>
		  </div>
		</div>
		<span class='legendExampleRange'> structure has both present</span> <br/>
		<span class='legendExampleRange'> and absent results </span>
	</div>

	<div class='legendSection'>
		<div class='legendExampleCell sashCell'>
			<img style="height: inherit; width: inherit;" src="${configBean.FEWI_URL}assets/images/sash.png" />
		</div>
		<span class='legendExampleRange'> ambiguous in structure </span>
	</div>

	<div class='legendSection'>
		<div class='legendExampleCell subStructCell'>
		  <div class='inSubStructIndicatorWrapper'>
		  	<div class='inSubStructIndicator'></div>
		  </div>
		</div>
		<span class='legendExampleRange'> only absent or ambiguous results </span> <br/>
		<span class='legendExampleRange'> in substructures </span>
	</div>

	<div class='legendSection theilerStageOnly' >
		<div class='legendExampleCell '>
		<div class='validStageWrapper'>
		<span class='validStageIndicator' >o</span>
		</div>
		</div>
		<span class='legendExampleRange'> tissue exists at
		  <a id='tsGlossaryLink'
		    href="${configBean.FEWI_URL}glossary/theiler" target="_blank">Theiler stage</a> (but </span> <br/>
		<span class='legendExampleRange'>has no annotations)</span>
	</div>

	<div class='legendSection theilerStageOnly' >
		<div class='legendExampleCell'>
		  <div class='inSubStructIndicatorWrapper'>
		  </div>
		</div>
		<span class='legendExampleRange'> tissue does not exist at this stage </span>
	</div>
	
	<div class='legendSection geneOnly' >
		<div class='legendExampleCell'>
		  <div class='inSubStructIndicatorWrapper'>
		  </div>
		</div>
		<span class='legendExampleRange'> no annotations for the gene in this </span>
		<br/>
		<span class='legendExampleRange'> tissue </span>
	</div>

        <div class="legendSection" style="text-align: center;font-style: italic;">
            Click on a grid cell to access annotation details and images.
       </div>

	<div class='legendSection legendFilterSection'>
	    <h4>Filter</h4>
		<div>Use gray checkboxes to select rows/columns for filtering</div>
	</div>
	
	<div class='legendSection legendBottom'>
		<div>
			<a href="${configBean.USERHELP_URL}EXPRESSION_help.shtml#matrixcolors"
				onclick="javascript:openUserhelpWindow('EXPRESSION_help.shtml#matrixcolors'); return false;"
			>more details</a>
		</div>
	</div>
	
 	</div>
</div>
