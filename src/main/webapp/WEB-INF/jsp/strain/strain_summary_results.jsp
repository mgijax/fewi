
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/gxd/gxd_summary.css" />

<style>
#paginationBottom { float: right; margin-right: 20px; text-align: right; padding-top: 4px; }
#paginationTopDiv { float: right; text-align: right; width: 350px; height: 53px; }
#paginationTop { float: right; margin-right: 20px; text-align: right; padding-top: 4px; }
#querySummary { width: 500px; }
a { text-decoration: none; }
.blue { color: blue; }
</style>

<div class="facetFilter">
	<div id="facetDialog">
		<div class="hd">Filter</div>
		<div class="bd" style="width: 285px">
			<form:form method="GET" action="${configBean.FEWI_URL}strain/summary?${queryString}">
				<img src="${configBean.FEWI_URL}assets/images/loading.gif">
			</form:form>
		</div>
	</div>
</div>

<div id="summary">
	<div id="breadbox">
	    <div id="paginationTopDiv">
    	    <div class="innertube">
	    	  <div id="paginationTop">
	    		<div id="pageControlsTop" class="paginator"></div>
		    	<div id="pageLengthTop" class="pageLength"></div>
		    	<div id="pageReportTop" class="pageReport"></div>
	    	  </div>
			</div>
    	</div>
	  	<div id="ysf">${ysf}</div>
		<div id="contentcolumn">
			<div class="innertube">
				<div id="filterDiv" style="width: 260px;">
					<span id="filterLabel" class="label">Filter strains by:</span>
					<a id="attributeFilter" class="filterButton">Attribute&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
				</div><br/>
				<div id="filterSummaryWrapper" style="width: 500px; display: inline-block;">
					<div id="filterSummary" class="filters" style="display: none">
						<span class="label">Filtered by:</span>&nbsp; <span id="defaultText" style="display: none;">No filters selected.</span> <span id="filterList"></span><br/>
					</div>
				</div>
			</div>
		</div>
	</div>
    <br clear="all" />
</div>

<div id="toolbar" class="bluebar" style="">
	<div id="downloadDiv">
		<span class="label">Export:</span>
		<a id="textDownload"  class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a>
		<a id="excelDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/excel.jpg" width="10" height="10" /> Excel File</a>
	</div>
</div>
	<div id="resultSummary"></div>

	<div id="paginationBottom">
		<div id="pageControlsBottom" class="paginator"></div>
		<div id="pageLengthBottom" class="pageLength"></div>
		<div id="pageReportBottom" class="pageReport"></div>
	</div>