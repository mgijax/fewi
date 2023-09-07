<div id="filterButtonsO">
   <b>Filter Other IDs results by: </b>
   <a id="featureTypeFilterO" class="filterButton">Feature Type <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
</div>
<div id="breadboxO" class="hidden removeFilterDiv">
  <div id="filterSummaryO">
  	<b>Filtered by: </b>
    <span id="filterListO"></span>
  </div>
</div>
<div id="b3Header" class="qsHeader">Other Results by ID
  <span id="b3Counts" class="resultCount"></span>
  <span class="helpCursor" onmouseover="return overlib('<div class=detailRowType>This section includes links to sequences, orthology relationships, SNPs and other results whose accession ID matched an item in your search text.</div><div class=\'detailRowType\'>See <a href=\'${configBean.USERHELP_URL}QUICK_SEARCH_help.shtml\'>Using the Quick Search Tool</a> for more information and examples.</div>', STICKY, CAPTION, 'Other Results By ID', HAUTO, BELOW, WIDTH, 375, DELAY, 600, CLOSECLICK, CLOSETEXT, 'Close X')" onmouseout="nd();">
    <img src="${configBean.WEBSHARE_URL}images/blue_info_icon.gif" border="0">
  </span>
  <div id="otherIdPaginator" class="otherIdPaginator"></div>
  <div id="otherIDDownloads" class="otherIDDownloads">
    <div class="export">Download as:</div>
    <select id="oDownloads" class="selectList">
    	<option id="oTextDownload" value="text" url="${configBean.FEWI_URL}/quicksearch/otherIDs/report.txt?queryType=${queryType}&query=${query}">Text</option>
    	<option id="oExcelDownload" value="excel" url="${configBean.FEWI_URL}/quicksearch/otherIDs/report.xlsx?queryType=${queryType}&query=${query}">Excel</option>
    </select>
    <button id="oExportGo" onClick="b3Download()">Go</button>
  </div>
</div>
<div id="b3Results"></div>
