
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/gxd/gxd_summary.css" />

<style>
#paginationBottom { float: right; margin-right: 20px; text-align: right; padding-top: 4px; }
#querySummary { width: 500px; }
a { text-decoration: none; }
.blue { color: blue; }
</style>

<div id="summary">
	<div id="breadbox">
		<div id="contentcolumn">
			<div class="innertube">
				<div id="filterSummary" class="filters">
					<span class="label">Filtered by:</span>
					&nbsp;<span id="defaultText">No filters selected.</span>
					<span id="filterList"></span><br/>
					<span id="fCount" ><span id="filterCount">0</span> result(s) match after applying filter(s)</span>
				</div>
			</div>
		</div>
	</div>

    <div id="querySummary">
        <div class="innertube">
            <div id="searchSummary"> <!-- filled via js --> </div>
        </div>
    </div>

    <div class="facetFilter">
    </div>

    <div id="rightcolumn">
        <div class="innertube">
	      <div id="paginationTop">
	    	<div id="pageControlsTop" class="paginator"></div>
	    	<div id="pageLengthTop" class="pageLength"></div>
	    	<div id="pageReportTop" class="pageReport"></div>
	      </div>
		</div>
    </div>

	    <br clear="all" />
    </div>

	<div id="resultSummary"></div>

	<div id="paginationBottom">
		<div id="pageControlsBottom" class="paginator"></div>
		<div id="pageLengthBottom" class="pageLength"></div>
		<div id="pageReportBottom" class="pageReport"></div>
	</div>