<link rel="stylesheet" type="text/css" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/gxd/gxd_summary.css" />

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
.yui-skin-sam .yui-pg-last { display: none; }
#tooManyResultsWrapper {
  padding-bottom: 10px;
}
#tooManyResults {
  border: 1px solid red;
  color: red;
  font-weight: bold;
  display: none;
  padding: 4px;
  margin-left: 5px;
}
#tooManyGenes {
  border: 1px solid black;
  color: black;
  font-weight: bold;
  display: none;
  padding: 4px;
  margin-left: 5px;
}
.canHide {}
.heatMapLinkHidden { display : none; }
#heatMapLimit { font-size: 125%; }
.ui-dialog-titlebar { background-color: #d0e0f0; color:red; }
</style>


<div id="summary">
	<div id="breadbox">
		<div id="contentcolumn">
			<div class="innertube">
		            <jsp:include page="gxd_summary_filters.jsp"></jsp:include><br>
				<div id="filterSummary" class="filters">
					<span class="label">Filtered by:</span>
					&nbsp;<span id="defaultText">No filters selected.</span>
					<span id="filterList"></span><br/>
					<span id="fCount" ><span id="filterCount">0</span> result(s) match after applying filter(s)</span>
				</div>
			</div>
		</div>
	</div>

    <div id="rightcolumn">
        <div class="innertube">
        	<div id="paginationTop">&nbsp;</div>
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
		<form:form method="GET" action="${configBean.FEWI_URL}gxd">
		<img src="/fewi/mgi/assets/images/loading.gif">
		</form:form>
	    </div>
	</div>
    </div>

	    <br clear="all" />
    </div>
    <div id="tooManyResultsWrapper">
    	<div id="tooManyResults">Large search returns make our pages slow to load. Therefore, some functionality has been disabled until you refine your search to bring the number of returned assay results under <span id="maxCount">TBD</span>.
    	</div>
    </div>
<div id="resultSummary" class="yui-navset">
    <ul class="yui-nav">
        <li><a id="genestab" href="#genes"><em>Genes (<span id="totalGenesCount"></span>)</em></a></li>
        <li><a id="assaystab" href="#assays"><em>Assays (<span id="totalAssaysCount"></span>)</em></a></li>
        <li class="selected"><a id="resultstab" href="#results"><em>Assay results(<span id="totalResultsCount"></span>)</em></a></li>
        <li><a id="imagestab" href="#images"><em>Images (<span id="totalImagesCount"></span>)</em></a></li>
	<li><a id="stagegridtab" href="#stageGrid"><em>Tissue x Stage Matrix</em></a></li>
	<li><a id="genegridtab" href="#geneGrid"><em>Tissue x Gene Matrix</em></a></li>
	<li><a id="heatmaptab" href="#heatmap"><em>Heat Map</em></a></li>
    </ul>
    <div class="yui-content" id="tabSummaryContent">
        <div id="goldbarDiv">
        	<div id="toolbar" class="goldbar">
							<form action="${configBean.MOUSEMINE_URL}mousemine/portal.do" method="post" name="mousemine" target="_blank">
								<input id="mousemineids" type="hidden" value="" name="externalids">
								<input type="hidden" value="SequenceFeature" name="class">
							</form>

	        	<div id="downloadDiv">
			<form name="markerExportForm" id="markerExportForm" action="" method="POST">
                   <span class="label canHide">Export:</span>
                   <a id="markersTextDownload" class="canHide filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a>
                   <a id="markersBatchForward" class="canHide filterButton"><img src="${configBean.WEBSHARE_URL}images/arrow_right.gif" width="10" height="10" /> MGI Batch Query</a>
						<a id="mouseMineLink" target="_blank" class="canHide filterButton" onClick="javascript: mousemine.submit();"><img src="${configBean.WEBSHARE_URL}images/arrow_right.gif" width="10" height="10" /> MouseMine</a>
			</form><!-- markerExportForm -->
                </div>
             </div>
	        <div id="genesdata"></div>
        </div>
        <div>
	        <div id="assaysdata"></div>
        </div>
        <div>
            <div id="toolbar" class="goldbar">
                <div id="downloadDiv">
		    <form name="resultsExportForm" id="resultsExportForm" action="" method="POST">
                    <span class="label canHide">Export:</span>
                    <a id="resultsTextDownload" class="canHide filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a>
                    <a id="heatMapLink" class="filterButton heatMapLinkHidden" onClick="popupHeatMap()"> RNA-Seq <img src="${configBean.WEBSHARE_URL}images/arrow_right.gif" width="10" height="10" style="margin-bottom: -1px;"/> Heat Map</a>
		    <span class="label" style="padding-left: 100px;">Show Additional Sample Data <input id="showHide" type="checkbox" onClick="flipOptionalColumns()"></span>
		    </form><!-- resultsExportForm -->
                </div>
            </div>
            <div id="resultsdata"></div>
        </div>
        <div>
        	<div id="toolbar" class="goldbar">
                    <span class="label">Expression images in MGI are copyrighted; click on an image for details about their use.</span>
                </div>
	        <div id="imagesdata"></div>
        </div>
       	<div>
	        <div id="stagegriddata">
	        	<div class="gridWrapper">
	        		<div class="gridTitle">Theiler Stage</div>
					<div id="sgTarget" class="matrixContainer">
					</div>
				</div>
	        </div>
        </div>
        <div>
	        <div id="genegriddata">
	        	<div class="gridWrapper">
					<div id="ggTarget" class="matrixContainer">
					</div>
				</div>
	        </div>
	        <div id="hiddenGeneMatrixPaginator" class="facetFilter" style="display: none;"></div>
        </div>
        <div>
		<div class="goldbar">&nbsp;</div>
		<div id="heatmaptabcontent">
		    <table>
		    <tr>
		    <td>
			<img onclick="popupHeatMap();" src="/fewi/mgi/assets/images/heatmap_screenshot.png" />
		    </td>
		    <td>
			<table>
			<tr>
			    <td>
			    <button onclick="popupHeatMap();">&#x279C; RNA-Seq Heat Map</button>
			    </td>
			</tr>
			<tr>
			    <td>
			    Export RNA-Seq search results to a heat map <br/> for visualization and analysis.
			    </td>
			</tr>

			<tr>
			    <td>
			    Powered by: 
			    <svg id="morpheusLogo" width="32px" height="32px">
			    <g>
				<rect x="0" y="0" width="32" height="14" style="fill:#ca0020;stroke:none"></rect>
				<rect x="0" y="18" width="32" height="14" style="fill:#0571b0;stroke:none"></rect>
			    </g>
			    </svg>
			    Morpheus.
			    </td>
			</tr>
			</table>
		    </td>
		    </tr>
		    </table>
		</div>
        </div>
    </div>
</div>
	<div id="paginationBottom">&nbsp;</div>
    <div class="gxdLitRow">
    	<br/>
    	<span id="gxdLitInfo" class="canHide"></span>
    </div>

<div id="structStagePopup" class="visHidden facetFilter structPopup">
  <div id="structStagePopupContents"> </div>
</div>

<div id="structGenePopup" class="visHidden facetFilter structPopup">
  <div id="structGenePopupContents"> </div>
</div>

<div id="structLegendPopupPanel" class="visHidden facetFilter">
  <jsp:include page="gxd_legend_popup.jsp"></jsp:include>
</div>

<div id="geneLegendPopupPanel" style="visibility: hidden;" class="facetFilter">
  <jsp:include page="gxd_legend_popup.jsp"></jsp:include>
</div>

<div id="heatMapLimit" style="display:none" title="Too many results">
<p>Too many RNA-Seq results for export to heat map. Please refine your search to bring total RNA-Seq results under 10,000,000.</p>
</div>

<!-- Patterns for matrix sash icon -->
<svg height="0" width="0" xmlns="http://www.w3.org/2000/svg" version="1.1">
  <defs>
    <pattern id="sash28" patternUnits="userSpaceOnUse" width="28" height="28">
      <image xlink:href="${configBean.FEWI_URL}assets/images/sash.png"
        x="-3" y="-3" width="33" height="32">
      </image>
    </pattern>    
    <pattern id="sash24" patternUnits="userSpaceOnUse" width="24" height="24">
      <image xlink:href="${configBean.FEWI_URL}assets/images/sash.png"
        x="-3" y="-3" width="29" height="28">
      </image>
    </pattern>
  </defs>
</svg>
