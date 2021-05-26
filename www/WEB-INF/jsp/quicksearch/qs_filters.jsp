<div id="filterButtons">
   <b>Filter results by: </b>
   <a id="functionFilter" class="filterButton">Molecular Function <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="processFilter" class="filterButton">Biological Process <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="componentFilter" class="filterButton">Cellular Component <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
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
			<form:form method="GET"
				action="${configBean.FEWI_URL}quicksearch/summary">
				<img src="/fewi/mgi/assets/images/loading.gif">
			</form:form>
		</div>
	</div>
</div>
	