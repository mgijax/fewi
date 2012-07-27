
<style>
.yui-skin-sam .yui-dt th{
  background:url(${configBean.WEBSHARE_URL}images/cre/SpriteYuiOverRide.png)
  repeat-x 0 -1300px;
}
.yui-skin-sam th.yui-dt-asc,.yui-skin-sam th.yui-dt-desc{
  background:url(${configBean.WEBSHARE_URL}images/cre/SpriteYuiOverRide.png)
  repeat-x 0 -1400px;
}

.yui-skin-sam th.yui-dt-sortable .yui-dt-liner{
  background:url(${configBean.WEBSHARE_URL}images/cre/creSortableArrow.png)
  no-repeat right;
}
.yui-skin-sam th.yui-dt-asc .yui-dt-liner{
  background:url(${configBean.WEBSHARE_URL}images/cre/creDownArrow.png)
  no-repeat right;
}
.yui-skin-sam th.yui-dt-desc .yui-dt-liner{
  background:url(${configBean.WEBSHARE_URL}images/cre/creUpArrow.png)
  no-repeat right;
}
#gxdLitInfo
{
	font-weight:bold;
}
</style>
<div id="summary">

    <div id="paginationTop" style="float:right;">&nbsp;</div>

    <div id="querySummary" style="float:none;margin-left:0;">
        <div class="innertube" style="width:500px">
            <div id="searchSummary"> <!-- filled via js --> </div>
        </div>
    </div>
    <br clear="all" />
    </div>
<div id="resultSummary" class="yui-navset">
    <ul class="yui-nav">
        <li><a id="genestab" href="#genes"><em>Genes (<span id="totalGenesCount"></span>)</em></a></li>
        <li><a id="assaystab" href="#assays"><em>Assays (<span id="totalAssaysCount"></span>)</em></a></li>
        <li class="selected"><a id="resultstab" href="#results"><em>Assay results(<span id="totalResultsCount"></span>)</em></a></li>
    </ul>
    <div class="yui-content">
        <div>
        	<div id="toolbar" class="bluebar">
	        	<div id="downloadDiv">
                    <span class="label">Export:</span>
                    <a id="markersTextDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a> 
                    <a id="markersExcelDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/excel.jpg" width="10" height="10" /> Excel File</a> 
                    <a id="markersBatchForward" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/arrow_right.gif" width="10" height="10" /> Batch Query</a> 
                </div>
             </div>
	        <div id="genesdata"></div>
        </div>
        <div>
	        <div id="assaysdata"></div>
        </div>
        <div>
            <div id="toolbar" class="bluebar">
                <div id="downloadDiv">
                    <span class="label">Export:</span>
                    <a id="resultsTextDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a> 
                    <a id="resultsExcelDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/excel.jpg" width="10" height="10" /> Excel File</a> 
                </div>
            </div>
            <div id="resultsdata"></div>
        </div>
    </div>
</div>
	<div id="paginationBottom" style="float:right;">&nbsp;</div>
    <div class="gxdLitRow">
    	<br/>
    	<span id="gxdLitInfo"></span>
    </div>
