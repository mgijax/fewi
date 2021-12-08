<div id="filterButtonsA">
   <b>Filter Alleles results by: </b>
   <a id="featureTypeFilterA" class="filterButton">Feature Type <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="phenotypeFilterA" class="filterButton">Phenotype <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="diseaseFilterA" class="filterButton">Disease <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
</div>
<div id="breadboxA" class="hidden removeFilterDiv">
  <div id="filterSummaryA">
  	<b>Filtered by: </b>
    <span id="filterListA"></span>
  </div>
</div>
<!-- Note: b5 appears out of order (before b2) for historical reasons. -->
<div id="b5Header" class="qsHeader">Alleles
  <span id="b5Counts" class="resultCount"></span>
  <span class="helpCursor" onmouseover="return overlib('<div class=detailRowType>This list includes alleles whose name, symbol, synonym, or accession ID matched some or all of your search text.<br/><br/>This list also includes alleles associated with vocabulary terms matching your search text. <br/><br/></div><div class=\'detailRowType\'>See <a href=\'${configBean.USERHELP_URL}QUICK_SEARCH_help.shtml\'>Using the Quick Search Tool</a> for more information and examples.</div>', STICKY, CAPTION, 'Alleles', HAUTO, BELOW, WIDTH, 375, DELAY, 600, CLOSECLICK, CLOSETEXT, 'Close X')" onmouseout="nd();">
       <img src="${configBean.WEBSHARE_URL}images/blue_info_icon.gif" border="0">
  </span>
  <div id="allelePaginator" class="allelePaginator"></div>
  <div id="alleleDownloads" class="alleleDownloads">
    <div class="export">Export:</div>
    <a id="aExcelDownload" href="${configBean.FEWI_URL}/quicksearch/alleles/report.xlsx?queryType=${queryType}&query=${query}" title="Excel File"><span class="material-icons">table_view</span></a>
    <a id="aTextDownload" href="${configBean.FEWI_URL}/quicksearch/alleles/report.txt?queryType=${queryType}&query=${query}" title="Text File"><span class="material-icons">text_snippet</span></a>
  </div>
</div>
<div id="b5Results"></div>
