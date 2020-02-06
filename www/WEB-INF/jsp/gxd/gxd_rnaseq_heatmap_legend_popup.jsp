<%--GXD matrix legend popup --%>
<div class="hd">
<div style="float:right">Morpheus <img src="${configBean.FEWI_URL}assets/images/static/morpheus_icon.png" height="40" /></div>
<div style="float:left"><img src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="40" /></div>
</div>

<div class="bd">
	<div class='legendSection'>
	
		<h4>Gene Expression</h4>
		<div style="clear:both">
		<h6>Number of expression results annotated as</h6>
		</br>
		</div>
		
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
		<span class='legendExampleRange'> either absent or ambiguous results </span> <br/>
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
</div><!-- div bd -->
