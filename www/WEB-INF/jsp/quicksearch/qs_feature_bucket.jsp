<div id="filterButtons">
   <b>Filter Genome Features results by: </b>
   <a id="featureTypeFilterF" class="filterButton">Feature Type <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="functionFilterF" class="filterButton">Molecular Function <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="processFilterF" class="filterButton">Biological Process <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="componentFilterF" class="filterButton">Cellular Component <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="expressionFilterF" class="filterButton">Expression <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="phenotypeFilterF" class="filterButton">Phenotype <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="diseaseFilterF" class="filterButton">Disease <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
</div>
<div id="breadboxF" class="hidden removeFilterDiv">
  <div id="filterSummaryF">
  	<b>Filtered by: </b>
    <span id="filterListF"></span>
  </div>
</div>
<div id="b1Header" class="qsHeader">Genome Features
  <span id="b1Counts" class="resultCount"></span>
  <span class="helpCursor" onmouseover="return overlib('<div class=detailRowType>This list includes genes, QTL, cytogenetic markers, and other genome features whose name, symbol, synonym, or accession ID matched some or all of your search text.<br/><br/>This list also includes genome features associated with vocabulary terms matching your search text. <br/><br/></div><div class=\'detailRowType\'>See <a href=\'${configBean.USERHELP_URL}QUICK_SEARCH_help.shtml\'>Using the Quick Search Tool</a> for more information and examples.</div>', STICKY, CAPTION, 'Genome Features', HAUTO, BELOW, WIDTH, 375, DELAY, 600, CLOSECLICK, CLOSETEXT, 'Close X')" onmouseout="nd();">
       <img src="${configBean.WEBSHARE_URL}images/blue_info_icon.gif" border="0">
  </span>
  <div id="featurePaginator" class="featurePaginator"></div>
  <div id="featureDownloads" class="featureDownloads">
    <div class="export">Download as:</div>
    <select id="fDownloads" class="selectList">
    	<option id="fTextDownload" value="text" url="${configBean.FEWI_URL}/quicksearch/features/report.txt?queryType=${queryType}&query=${query}">Text</option>
    	<option id="fExcelDownload" value="excel" url="${configBean.FEWI_URL}/quicksearch/features/report.xlsx?queryType=${queryType}&query=${query}">Excel</option>
    </select>
    <button id="fExportGo" onClick="b1Download()">Go</button>

    <div class="export" style="padding-left: 25px">Forward to:</div>
    <select id="fForwards" class="selectList">
    	<option id="fBatchForward" value="batch" url="${configBean.FEWI_URL}/quicksearch/forward?forwardTo=mgibq&tab=feature&queryType=${queryType}&query=${query}">MGI batch query</option>
    	<option id="fGxdBatchForward" value="gxdBatch" url="${configBean.FEWI_URL}/gxd/batchForward?${queryString}">Gene Expression Data</option>
    	<option id="fMouseMineForward" value="mouseMine" url="${configBean.FEWI_URL}/quicksearch/forward?forwardTo=mouseMine&tab=feature&${queryString}">MouseMine</option>
    </select>
    <button id="fForwardGo" onClick="b1Forward()">Go</button>
  </div>
</div>
<div id="b1Results"></div>
