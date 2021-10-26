<div id="filterButtonsV">
   <b>Filter Vocabulary Terms results by: </b>
   <a id="functionFilterV" class="filterButton">Molecular Function <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="processFilterV" class="filterButton">Biological Process <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="componentFilterV" class="filterButton">Cellular Component <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="expressionFilterV" class="filterButton">Expression <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="phenotypeFilterV" class="filterButton">Phenotype <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="diseaseFilterV" class="filterButton">Disease <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
</div>
<div id="breadboxV" class="hidden removeFilterDiv">
  <div id="filterSummaryV">
  	<b>Filtered by: </b>
    <span id="filterListV"></span>
  </div>
</div>
<div id="b2Header" class="qsHeader">Vocabulary Terms
  <span id="b2Counts" class="resultCount"></span>
  <span class="helpCursor" onmouseover="return overlib('<div class=detailRowType>Use the vocabulary terms listed here <ul><li>to learn MGI\'s official terms</li><li>to focus on detailed research topics</li><li>to explore related research areas</li><li>to investigate alternative areas</li></ul></div><div class=\'detailRowType\'>See <a href=\'${configBean.USERHELP_URL}QUICK_SEARCH_help.shtml\'>Using the Quick Search Tool</a> for more information and examples.</div>', STICKY, CAPTION, 'Vocabulary Terms', HAUTO, BELOW, WIDTH, 375, DELAY, 600, CLOSECLICK, CLOSETEXT, 'Close X')" onmouseout="nd();">
    <img src="${configBean.WEBSHARE_URL}images/blue_info_icon.gif" border="0">
  </span>
  <div id="vocabPaginator" class="vocabPaginator"></div>
</div>
<div id="b2Results"></div>
