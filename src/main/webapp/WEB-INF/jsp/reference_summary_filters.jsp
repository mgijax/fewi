	<div id="breadbox">
		<div id="contentcolumn">
			<div id="filterDiv" style="clear: both">
			<span class="label">Filter references by:</span> 
			<a id="authorFilter" class="filterButton">Author <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
			<a id="journalFilter" class="filterButton">Journal <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
			<a id="yearFilter" class="filterButton">Year <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
			<a id="curatedDataFilter" class="filterButton">Data <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
			<a id="typeFilter" class="filterButton">Reference Type <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
			</div>
			<div id="fCount" style="display:none;" >
				<span id="filterCount">0</span> reference(s) match after applying filter(s)
			</div>
			<div id="filterSummary" class="filters">
				<span class="label">Filters:</span>
				&nbsp;<span id="defaultText"  style="display:none;">No filters selected. Filter these references above.</span>
				<span id="filterList"></span><br/>
			</div>
			<!-- <div class="innertube">
			</div> -->
		</div>
	</div>
