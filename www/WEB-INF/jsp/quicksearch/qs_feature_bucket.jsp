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
    <div class="export">Export:</div>
    <a id="fExcelDownload" href="${configBean.FEWI_URL}/quicksearch/features/report.xlsx?queryType=${queryType}&query=${query}" title="Excel File"><span class="material-icons">table_view</span></a>
    <a id="fTextDownload" href="${configBean.FEWI_URL}/quicksearch/features/report.txt?queryType=${queryType}&query=${query}" title="Text File"><span class="material-icons">text_snippet</span></a>
    <a id="fBatchQuery" target="_blank" style="font-size: 14px; outline: 1px solid black; border-radius: 5px; padding: 1px; background-color: #000001; color: #dfefff;" href="${configBean.FEWI_URL}/quicksearch/forward?forwardTo=mgibq&tab=feature&queryType=${queryType}&query=${query}" title="MGI Batch Query">BQ</a>
  </div>
</div>
<div id="b1Results"></div>
