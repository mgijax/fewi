
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
#resultSummary
{
	display:table;
}
#gxdLitInfo
{
	font-weight:bold;
}
#gxd_image_meta
{
	border: none;
	font-size:120%;
}
#gxd_image_meta th
{
	font-weight: bold;
	padding: 4px;
	border-left:none;border-top:none;border-right:none;
	border-bottom: solid 1px #aaa;
	background-color:transparent;
	background:transparent
}
#gxd_image_meta td
{
	border: none;
	padding: 4px;
}
#imagesdata th .yui-dt-liner
{
	display:none;
}
#imagesdata table
{
border-collapse: collapse;
}
#imagesdata tr.yui-dt-odd
{
	border-top:solid 1px #ccc;
	border-bottom:solid 1px #ccc;
}
#imagesdata td.yui-dt-col-image
{
	width: 240px;
	max-width: 240px;
}
#imagesdata td.yui-dt-col-image .yui-dt-liner
{ display: inline; }
#imagesdata td.yui-dt-col-metaData .yui-dt-liner
{
	width: 460px;
}
.nowrap{ whitespace: nowrap; }
.copySymbol
{
	font-weight:bold;
	padding-left:4px;
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
        <li><a id="imagestab" href="#images"><em>Images (<span id="totalImagesCount"></span>)</em></a></li>
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
        <div>
        	<div id="toolbar" class="bluebar">
                    <span style="text-align:right;" class="label">Expression images in MGI are copyrighted; click on an image for details about their use.</span>
                </div>
	        <div id="imagesdata"></div>
        </div>
    </div>
</div>
	<div id="paginationBottom" style="float:right;">&nbsp;</div>
    <div class="gxdLitRow">
    	<br/>
    	<span id="gxdLitInfo"></span>
    </div>
