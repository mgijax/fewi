
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/gxd/gxd_summary.css" />

<style>
table.experimentWrapper { border: 1px solid gray; border-collapse: collapse; margin-bottom: 8px; width: 100%; }
table.experimentWrapper tr td { border: 1px solid gray; padding: 2px; }
table tr.titleRow td { background-color: #E2AC00; text-align: left; font-weight: bold; vertical-align: top; }
td.title { margin-left: 89px; }
td.titleLabel { width: 85px; }
table tr.detailLabelRow td { background-color: #F0F0F0; font-weight: bold; vertical-align: top; text-align: left; border-bottom: none; }
table tr.detailLabelRow td.detailTitle { width: 85px; background-color: #EBCA6D; }
table tr.detailDataRow td { background-color: white; text-align: left; vertical-align: top; border-top: none; }
table tr.descriptionRow td { background-color: #F0F0F0; }
table tr.descriptionRow td.descriptionTitle { width: 85px; background-color: #E2AC00; font-weight: bold; vertical-align: top; }
table tr.noteRow td { background-color: white; }
table tr.noteRow td.noteTitle { width: 85px; background-color: #EBCA6D; font-weight: bold; }

ul.variables { padding-top: 0px; }
ul.variables li { padding-top: 0px; margin-left: 0px; list-style-type: none; }
#paginationBottom { float: right; margin-right: 20px; text-align: right; }
#querySummary { width: 500px; }
a { text-decoration: none; }
.blue { color: blue; }
.yellow { background-color: yellow; }
#contentcolumn { margin: 0 425px 0 425px; }
.facetFilter .yui-panel .bd { width: 284px; }

table.id-table * {
    border: none !important;
}
</style>

<div id="summary">
	<div id="breadbox">
		<div id="contentcolumn">
			<div class="innertube">
				<div id="filterDiv" style="width: 360px;">
					<span id="filterLabel" class="label">Filter experiments by:</span>
					<a id="variableFilter" class="filterButton">Variable&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
					<a id="studyTypeFilter" class="filterButton">Study Type&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
				</div><br/>
				<div id="filterSummary" class="filters" style="clear:both;">
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
    	<div id="facetDialog">
			<div class="hd">Filter</div>
			<div class="bd">
				<form:form method="GET"
					action="${configBean.FEWI_URL}gxd/htexp_index/summary?${queryString}">
					<img src="/fewi/mgi/assets/images/loading.gif">
				</form:form>
			</div>
		</div>
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

	<div id="resultSummary" style="width:100%"></div>

	<div id="paginationBottom">
		<div id="pageControlsBottom" class="paginator"></div>
		<div id="pageLengthBottom" class="pageLength"></div>
		<div id="pageReportBottom" class="pageReport"></div>
	</div>
