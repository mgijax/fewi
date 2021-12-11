<div id="filterButtonsS">
   <b>Filter Strains and Stocks results by: </b>
   <a id="phenotypeFilterS" class="filterButton">Phenotype <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="diseaseFilterS" class="filterButton">Disease <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
</div>
<div id="breadboxS" class="hidden removeFilterDiv">
  <div id="filterSummaryS">
  	<b>Filtered by: </b>
    <span id="filterListS"></span>
  </div>
</div>
<!-- Note: b4 appears out of order (before b3) for historical reasons. -->
<div id="b4Header" class="qsHeader">Strains and Stocks
  <span id="b4Counts" class="resultCount"></span>
  <span class="helpCursor" onmouseover="return overlib('<div class=detailRowType>This list includes mouse strains or stocks that matched the name, synonym, or accession ID matched some or all of your search text.</div><div class=\'detailRowType\'>See <a href=\'${configBean.USERHELP_URL}QUICK_SEARCH_help.shtml\'>Using the Quick Search Tool</a> for more information and examples.</div>', STICKY, CAPTION, 'Strains', HAUTO, BELOW, WIDTH, 375, DELAY, 600, CLOSECLICK, CLOSETEXT, 'Close X')" onmouseout="nd();">
    <img src="${configBean.WEBSHARE_URL}images/blue_info_icon.gif" border="0">
  </span>
  <div id="strainPaginator" class="strainPaginator"></div>
  <div id="strainDownloads" class="strainDownloads">
    <div class="export">Export:</div>
    <a id="sExcelDownload" href="${configBean.FEWI_URL}/quicksearch/strains/report.xlsx?queryType=${queryType}&query=${query}" title="Excel File"><span class="material-icons">table_view</span></a>
    <a id="sTextDownload" href="${configBean.FEWI_URL}/quicksearch/strains/report.txt?queryType=${queryType}&query=${query}" title="Text File"><span class="material-icons">text_snippet</span></a>
  </div>
</div>
<div id="b4Results"></div>
