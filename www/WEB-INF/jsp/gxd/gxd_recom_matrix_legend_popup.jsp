<%--GXD matrix legend popup --%>
<div class="hd">Matrix Legend</div>
  <div class="bd">

	<div class='legendWrapper'>
	
	<div class='legendSection'>
	
		<h4>Number of results annotated as</h4>
		
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
	    <h4>Note</h4>
		<div>
                Cre activity comparison is to endogenous gene expression in mouse structures. Not all recombinase-carrying alleles are displayed in this matrix. Alleles and transgenes whose tissue activity data is not yet recorded are omitted.
                </div>
	</div>
	
 	</div>
</div>
