<div id="filterButtons">
   <b>Filter results by: </b>
   <a id="featureTypeFilterF" class="filterButton">Feature Type <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="functionFilterF" class="filterButton">Molecular Function <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="processFilterF" class="filterButton">Biological Process <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="componentFilterF" class="filterButton">Cellular Component <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="expressionFilterF" class="filterButton">Expression <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="phenotypeFilterF" class="filterButton">Phenotype <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="diseaseFilterF" class="filterButton">Disease <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
</div>
<div id="filterNote" class="example" style="padding-top: 6px;">
  Note: Filters will be applied to data on all tabs.
</div>
<div id="breadbox" class="hidden">
  <div id="filterSummary">
  	<b>Filtered by: </b>
    <span id="filterList"></span>
  </div>
</div>

<!-- for filter popup (re-used by all filter buttons) -->
<div class="facetFilter">
	<div id="facetDialog">
		<div class="hd">Filter</div>
		<div class="bd">
			<!-- Form action is intended to be just a no-op here. -->
			<form:form method="GET"
				action="${configBean.WEBSHARE_URL}images/filter.png">
				<img src="/fewi/mgi/assets/images/loading.gif">
			</form:form>
		</div>
	</div>
</div>
	