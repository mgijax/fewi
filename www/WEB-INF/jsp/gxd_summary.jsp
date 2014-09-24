
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

#matrixPopupButtonWrapper
{
	width:100%;
	height: 40px;
	font-size: 90%;
	font-weight: bold;
	border-top-style: solid;
	background-color:#EEEEEE;
	border-color: #111111;
	border-width: 2px;

}
#matrixPopupResultsButton
{
	padding: 3px;
	margin-top: 8px;
	margin-left: 12px;
	background-color:#EBCA6D;
	float:left;
}
#matrixPopupImagesButton
{
	padding: 3px;
	margin-top: 8px;
	margin-right: 12px;
	background-color:#EBCA6D;
	float:right;
}

#structStagePopupContents
{
	width:100%;
	background-color: #FFFFFF;
}
#structGenePopupContents
{
	width:100%;
	background-color: #FFFFFF;
}

#stagePopupTable
{
	border-collapse: separate;
	border-spacing: 2px;
	width:100%;
	background-color: #FFFFFF;
	margin: 0 auto;
	border:1px;
}

#stagePopupTable th
{
	background-color: #EBCA6D;
	padding:10px;
	text-align: center;
	border:3px;
}
#stagePopupTable td
{
	padding:10px;
	text-align: center;
	border:1px;
}
#structStagePopup_h
{
	cursor: move;
	border-width:0px;
	line-height:2.5;
	background:	#EEE;
}
#structGenePopup_h
{
	cursor: move;
	border-width:0px;
	line-height:2.5;
	background:	#EEE;
}
#legendWrapper {
	width:300px;
	overflow: auto;
}
.legendSection{
	width: 100%;
	margin-bottom:22px;
	font-size:11px;
}
.legendSelectionRow{
	height:21px;
}
.legendExampleCell {
	height:20px;
	width:20px;
	float:left;
}
.legendExampleRange {
	margin-left:5px;
	vertical-align:text-top;
}
#legendPopupPanel .hd {
	color: black;
	background-color: #aaa;
}
.hiddenNegIndicatorWrapper {
	width: 100%;
	height: 100%;
	border-style: solid;
	border-width: 1px;
	border-color: #CCC;
	position:relative;
	overflow:hidden;
	background:#6699FF;
}
.hiddenNegIndicator {
	width: 0;
	height: 0;
	border-style: solid;
	border-width: 0 20px 20px 0;
	border-color: transparent #FF6767 transparent transparent;
	position: absolute;
	top: -6px;
	left: 0;
}
.inSubStructIndicatorWrapper {
	width: 100%;
	height: 100%;
	border-style: solid;
	border-width: 1px;
	border-color: #CCC;
	position:relative;
	overflow:hidden;
	background:#FFF;
}
.inSubStructIndicator {
	width: 0;
	height: 0;
	border-style: solid;
	border-width: 0 20px 20px 0;
	border-color: transparent #FFDB67 transparent transparent;
	position: absolute;
	top: -6px;
	left: 0;
}

.validStageWrapper {
	width: 100%;
	height: 100%;
	border-style: solid;
	border-width: 1px;
	border-color: #CCC;
	position:relative;
	overflow:hidden;
	background:#FFF;
}
.validStageIndicator {
	color:#CCCCCC;
	font-family:"Courier New";
	padding-left: 5px;
	padding-bottom: 1px;
	font-size:160%
}
</style>

<style type="text/css">
	#sgTarget line, #ggTarget line {
	    stroke: #aaa;
	    stroke-width: 1px;
	}
	#sgTarget .blue1, #ggTarget .blue1 {
		fill: rgb(215,228,255);
	}
	#sgTarget .blue2, #ggTarget .blue2 {
		fill: rgb(102,153,255);
	}
	#sgTarget .blue3, #ggTarget .blue3 {
		fill: rgb(0,64,192);
	}
	#sgTarget .red1, #ggTarget .red1 {
		fill: rgb(255,215,215);
	}
	#sgTarget .red2, #ggTarget .red2 {
		fill: rgb(255,103,103);
	}
	#sgTarget .red3, #ggTarget .red3 {
		fill: rgb(192,0,0);
	}
	#sgTarget .gray, #ggTarget .gray {
		fill: rgb(191,191,191);
		/*fill: rgb(191,191,191);*/
	}
	#sgTarget .gold, #ggTarget .gold {
		fill: rgb(255,219,103);
		/*fill: rgb(243,223,157);*/
	}

	#sgTarget .matrixButtonText, #ggTarget .matrixButtonText {
		font-weight: bold;
	}

	#sgTarget .matrixFilterButton, #ggTarget .matrixFilterButton {
			padding-bottom: 20px;
			margin-bottom: 20px;
	}

	#sgTarget .fh, #ggTarget .fh{
		fill: rgb(230,230,230);
		/*
	    fill:white;
	    stroke:#aaa;
	    stroke-width: 2px; */
	}
	.matrixContainer {
		height:100%;
		width:100%;
		overflow: scroll;
		border: solid 1px black;
	}
	.matrixContainer svg {
		border: solid 1px black;
		border-top: none;
		border-left: none;
	}
</style>
<div id="summary">
	<div id="breadbox">
		<div id="contentcolumn">
			<div class="innertube">
		            <jsp:include page="gxd_summary_filters.jsp"></jsp:include><br>
				<div id="filterSummary" class="filters" style="display:none">
					<span class="label">Filtered by:</span>
					&nbsp;<span id="defaultText"  style="display:none;">No filters selected.</span>
					<span id="filterList"></span><br/>
					<span id="fCount" style="display:none;" ><span id="filterCount">0</span> result(s) match after applying filter(s)</span>
				</div>
			</div>
		</div>
	</div>

<!--    <div id="querySummary" style="float:none;margin-left:0;">
        <div class="innertube" style="width:500px">
-->
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

    <div id="rightcolumn">
        <div class="innertube">
	    <div id="paginationTop">&nbsp;</div>
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
   		<li><a id="stagegridtab" href="#stageGrid"><em>Tissue x Stage Matrix</em></a></li>
   		<li><a id="genegridtab" href="#geneGrid"><em>Tissue x Gene Matrix</em></a></li>
    </ul>
    <div class="yui-content" id="tabSummaryContent">
        <div id="goldbarDiv">
        	<div id="toolbar" class="goldbar">
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
            <div id="toolbar" class="goldbar">
                <div id="downloadDiv">
                    <span class="label">Export:</span>
                    <a id="resultsTextDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a>
                    <a id="resultsExcelDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/excel.jpg" width="10" height="10" /> Excel File</a>
                </div>
            </div>
            <div id="resultsdata"></div>
        </div>
        <div>
        	<div id="toolbar" class="goldbar">
                    <span style="text-align:right;" class="label">Expression images in MGI are copyrighted; click on an image for details about their use.</span>
                </div>
	        <div id="imagesdata"></div>
        </div>
       	<div>
	        <div id="stagegriddata">
	        	<div style="background-color:white;">
	        		<div style="text-align:center;font-weight:bold;font-size:120%;">Theiler Stage</div>
					<div id="sgTarget" class="matrixContainer">
					</div>
				</div>
	        </div>
        </div>
        <div>
	        <div id="genegriddata">
	        	<div style="background-color:white;">
					<div id="ggTarget" class="matrixContainer">
					</div>
				</div>
	        </div>
        </div>
    </div>
</div>
	<div id="paginationBottom" style="float:right;">&nbsp;</div>
    <div class="gxdLitRow">
    	<br/>
    	<span id="gxdLitInfo"></span>
    </div>

<div id="structStagePopup" style="visibility: hidden;" class="facetFilter structPopup">
  <div id="structStagePopupContents"> </div>
</div>

<div id="structGenePopup" style="visibility: hidden;" class="facetFilter structPopup">
  <div id="structGenePopupContents"> </div>
</div>

<div id="structLegendPopupPanel" style="visibility: hidden;" class="facetFilter">
  <div class="hd">Matrix Legend</div>
  <div class="bd">

	<div id='legendWrapper'>
	<div class='legendSection' style="margin-bottom:10px;">
		<div style='padding-bottom:0px; text-align:center; font-weight:bold;'>Number of expression results</div>
	</div>
	<div class='legendSection' >
		<div style='padding-bottom:0px;'>present in structure and/or substructures</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell' style='background:#0040C0; margin-left:10px;'></div>
			<span class='legendExampleRange'> > 50 </span>
		</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell' style='background:#6699FF; margin-left:30px;'></div>
			<span class='legendExampleRange'> 5-50 </span>
		</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell' style='background:#D7E4FF; margin-left:50px;'></div>
			<span class='legendExampleRange'> 1-4 </span>
		</div>
	</div>

	<div class='legendSection' >
		<div style='padding-bottom:0px;'>absent in structure</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell' style='background:#C00000; margin-left:10px;'></div>
			<span class='legendExampleRange'> > 20 </span>
		</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell' style='background:#FF6767 ; margin-left:30px;'></div>
			<span class='legendExampleRange'> 5-20 </span>
		</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell' style='background:#FFD7D7; margin-left:50px;'></div>
			<span class='legendExampleRange'> 1-4 </span>
		</div>
	</div>

	<div class='legendSection' >
		<div class='legendExampleCell' style='margin-top:3px;'>
		  <div class='hiddenNegIndicatorWrapper'>
		  	<div class='hiddenNegIndicator'></div>
		  </div>
		</div>
		<span class='legendExampleRange'> structure contains both present</span> <br/>
		<span class='legendExampleRange'> and absent results </span>
	</div>

	<div class='legendSection' >
		<div class='legendExampleCell' style='background:#BFBFBF ;'></div>
		<span class='legendExampleRange'> ambiguous in structure </span>
	</div>

	<div class='legendSection' >
		<div class='legendExampleCell' style='margin-top:3px;'>
		  <div class='inSubStructIndicatorWrapper'>
		  	<div class='inSubStructIndicator'></div>
		  </div>
		</div>
		<span class='legendExampleRange'> results in substructures - either</span> <br/>
		<span class='legendExampleRange'> ambiguous or absent </span>
	</div>

	<div class='legendSection' >
		<div class='legendExampleCell'>
		<div class='validStageWrapper'>
		<span class='validStageIndicator' >o</span>
		</div>
		</div>
		<span class='legendExampleRange'> tissue exists at
		  <a href="${configBean.FEWI_URL}glossary/theiler" target="_blank">Theiler stage</a> </span>
	</div>

	<div class='legendSection' >
		<div class='legendExampleCell' style='background:#FFFFFF;'>
		  <div class='inSubStructIndicatorWrapper'>
		  </div>
		</div>
		<span class='legendExampleRange'> tissue does not exist at this stage </span>
	</div>

  </div>
</div>
</div>

<div id="geneLegendPopupPanel" style="visibility: hidden;" class="facetFilter">
  <div class="hd">Matrix Legend</div>
  <div class="bd">

	<div id='legendWrapper'>
	<div class='legendSection' style="margin-bottom:10px;">
		<div style='padding-bottom:0px; text-align:center; font-weight:bold;'>Number of expression results</div>
	</div>
	<div class='legendSection' >
		<div style='padding-bottom:0px;'>present in structure and/or substructures</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell' style='background:#0040C0; margin-left:10px;'></div>
			<span class='legendExampleRange'> > 50 </span>
		</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell' style='background:#6699FF; margin-left:30px;'></div>
			<span class='legendExampleRange'> 5-50 </span>
		</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell' style='background:#D7E4FF; margin-left:50px;'></div>
			<span class='legendExampleRange'> 1-4 </span>
		</div>
	</div>

	<div class='legendSection' >
		<div style='padding-bottom:0px;'>absent in structure</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell' style='background:#C00000; margin-left:10px;'></div>
			<span class='legendExampleRange'> > 20 </span>
		</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell' style='background:#FF6767 ; margin-left:30px;'></div>
			<span class='legendExampleRange'> 5-20 </span>
		</div>
		<div class='legendSelectionRow'>
			<div class='legendExampleCell' style='background:#FFD7D7; margin-left:50px;'></div>
			<span class='legendExampleRange'> 1-4 </span>
		</div>
	</div>

	<div class='legendSection' >
		<div class='legendExampleCell' style='margin-top:3px;'>
		  <div class='hiddenNegIndicatorWrapper'>
		  	<div class='hiddenNegIndicator'></div>
		  </div>
		</div>
		<span class='legendExampleRange'> structure contains both present</span> <br/>
		<span class='legendExampleRange'> and absent results </span>
	</div>

	<div class='legendSection' >
		<div class='legendExampleCell' style='background:#BFBFBF ;'></div>
		<span class='legendExampleRange'> ambiguous in structure </span>
	</div>

	<div class='legendSection' >
		<div class='legendExampleCell' style='margin-top:3px;'>
		  <div class='inSubStructIndicatorWrapper'>
		  	<div class='inSubStructIndicator'></div>
		  </div>
		</div>
		<span class='legendExampleRange'> results in substructures - either</span> <br/>
		<span class='legendExampleRange'> ambiguous or absent </span>
	</div>

	<div class='legendSection' >
		<div class='legendExampleCell' style='background:#FFFFFF;'>
		  <div class='inSubStructIndicatorWrapper'>
		  </div>
		</div>
		<span class='legendExampleRange'> no annotations for the gene in this tissue </span>
	</div>

  </div>
</div>
</div>


