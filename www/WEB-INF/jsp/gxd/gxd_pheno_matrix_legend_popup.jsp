<%--GXD matrix legend popup --%>
<div class="hd">Matrix Legend</div>

<div class="bd" style="overflow: hidden;">

<div style="width: 49%; float:left; padding-right: 4px; border-right: 2px solid black;">
	
	<div class='legendSection'>
	
		<h4>Gene Expression</h4>
		<h6>Number of expression results annotated as</h6>
		</br>
		
		<div>present in structure and/or substructures</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell blue3'></div>
			<span class='legendExampleRange'> > 50 </span>
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
			<div class='legendExampleCell red3'></div>
			<span class='legendExampleRange'> > 20 </span>
		</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell red2'></div>
			<span class='legendExampleRange'> 5-20 </span>
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


</div>

<div style="width: 48%; float:right;">

  <div class='legendSection'>

	<h4>Phenotype</h4>
	<h6>Number of annotations mapped to</h6>
	<h6>structure and/or substructure</h6>
	</br>
		
	<div class='legendSelectionRow'>
		<div class='legendExampleCell phenoBlue4'></div>
		<span class='legendExampleRange'> > 100 </span>
	</div>
	<div class='legendSelectionRow'>
		<div class='legendExampleCell phenoBlue3'></div>
		<span class='legendExampleRange'> 6-99 </span>
	</div>
	<div class='legendSelectionRow'>
		<div class='legendExampleCell phenoBlue2'></div>
		<span class='legendExampleRange'> 2-5 </span>
	</div>
	<div class='legendSelectionRow'>
		<div class='legendExampleCell phenoBlue1'></div>
		<span class='legendExampleRange'> 1 </span>
	</div>
  </div>

  <br/>
  <div class='legendSection'>
  <h4>Other Symbols</h4>
  </div>

  <div class='legendSection'>
	<div class='legendExampleCell '>
	<div class='validStageWrapper'>
	<span class='noResultsIndicator' >N</span>
	</div>
	</div>
	<span class='legendExampleRange'> no abnormalities detected, </span> <br/>
	<span class='legendExampleRange'> contrary to expectations</span>
  </div>

  <div class='legendSection'>
	<div class='legendExampleCell '>
	<div class='validStageWrapper'>
	<span class='backgroundSensativeIndicator' >!</span>
	</div>
	</div>
	<span class='legendExampleRange'> phenotype varies with </span> <br/>
	<span class='legendExampleRange'> strain background</span>
  </div>

	<div class='legendSection geneOnly' >
		<div class='legendExampleCell'>
		  <div class='inSubStructIndicatorWrapper'>
		  </div>
		</div>
		<span class='legendExampleRange'> no annotations mapped to this </span>
		<br/>
		<span class='legendExampleRange'> structure or substructure </span>
	</div>



</div>

</div>












