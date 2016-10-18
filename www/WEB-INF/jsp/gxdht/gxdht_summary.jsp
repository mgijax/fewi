
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/gxd/gxd_summary.css" />

<style>
div.headerShade1 { background-color: #EBCA6D; }
div.dataShade1 { background-color: white; }
div.headerShade2 { background-color: #E2AC00; }
div.dataShade2 { background-color: #F0F0F0; }
div.experimentWrapper { border: 1px solid gray; border-collapse: collapse; margin-bottom: 5px; }
div.idWrapper { border: 1px solid gray; width: 100%; overflow: auto; }
div.idLabels { width: 100px; text-align: right; float: left; font-weight: bold; }
div.ids { width: 125px; padding-left: 4px; float: left; text-align: left; }
div.title { margin-left: 235px; text-align: left; font-weight: bold; }
div.header { text-align: center; width: 100%; border: 1px solid gray; }
div.detailWrapper { border: 1px solid gray; width:100%; overflow: auto; }
div.detailCell { float: left; text-align: left; border-right: 1px solid gray; }
div.detailCellLast { text-align: left; padding: 0px 0px 0px 0px; }
div.detailHeading { text-align: left; padding: 0px 4px 0px 4px; }
div.samples { text-align: left; padding: 4px 4px 0px 4px; }
div.variables { text-align: left; padding: 4px 4px 0px 4px; }
ul.variables { padding-top: 0px; padding: 4px 0px 0px 4px; }
ul.variables li { padding-top: 0px; margin-left: 18px; }
div.type { text-align: left; padding: 4px 4px 0px 4px; }
div.method { text-align: left; padding: 4px 4px 0px 4px; }
div.spacer { width: 4px; float: left; }
div.descriptionWrapper { width: 100%; border: 1px solid gray; overflow: auto; }
div.descriptionTitle { width: 125px; padding-top: 10px; padding-left: 4px; float: left; text-align: left; }
div.description { margin-left: 130px; text-align: left; padding-right: 4px; }
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
	    	<div id="pageControls" class="paginator"></div>
	    	<div id="pageLength"></div>
	    	<div id="pageReport"></div>
	    </div>
		</div>
    </div>

	    <br clear="all" />
    </div>

	<div id="resultSummary"></div>

	<div id="paginationBottom" class="paginator">&nbsp;</div>
