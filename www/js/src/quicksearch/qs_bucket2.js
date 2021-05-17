/* Name: qs_bucket2.js
 * Purpose: data retrieval and processing logic for the quicksearch's second data bucket (vocab terms + strains)
 * Notes: Functions here will be prefixed by "b2".
 */

// Globals

var b2Failed = false;		// did retrieval for this bucket fail?
var b2PageSize = 100;
var b2CacheName = 'vocabCache';

// Having received 'data' from the server, show it on the page.
function b2Show(data) {
	var toShow = b2PageSize;
	var tbl = '';
	if (data.rows.length > 0) {
		tbl = '<TABLE ID="b2Table">';
		tbl = tbl + '<TR><TH>Score</TH><TH class="termCol">Term</TH><TH class="dataCol">Associated Data</TH><TH class="bestMatchCol">Best Match</TH></TR>';

		toShow = Math.min(100, data.rows.length);
		for (var i = 0; i < toShow; i++) {
			var item = data.rows[i];
			tbl = tbl + '<TR>';
			if (item.stars === null) {
				tbl = tbl + '<TD>TBD</TD>';
			} else {
				tbl = tbl + '<TD>' + item.stars.replace(/[*]/g, "&#9733;") + '</TD>';
			}
			if (item.detailUri === null) {
				tbl = tbl + '<TD><span class="termType">' + item.vocabName + '</span>: ' + item.term + '</TD>';
			} else {
				tbl = tbl + '<TD><span class="termType">' + item.vocabName + '</span>: <a href="' + item.detailUri + '">' + item.term + '</a></TD>';
			}
			if (item.annotationCount > 0) {
				if (item.annotationUri === null) {
					tbl = tbl + '<TD><span class="small">' + item.annotationText + '</span></TD>';
				} else {
					tbl = tbl + '<TD class="nowrap"><a class="small" href="' + item.annotationUri + '">'+ item.annotationText + '</a></TD>';
				}
			} else {
				tbl = tbl + '<TD>&nbsp;</TD>';
			}

			if (item.bestMatchType === null) {
				tbl = tbl + '<TD>&nbsp;</TD></TR>';
			} else if (item.bestMatchText.startsWith(item.bestMatchType)) {
				tbl = tbl + '<TD><span class="small">ID: ' + item.bestMatchText + '</span></TD></TR>'; 
			} else {
				tbl = tbl + '<TD><span class="termType">' + item.bestMatchType + '</span><span class="small">: ' + item.bestMatchText + '</span></TD></TR>';
			}
		}
	} else {
		console.log("No b2Results");
	}
	var header = qsResultHeader(data.start, data.end, data.totalCount);
	$('#b2Counts').html(header);
	$('#b2Results').html(tbl);
	pgUpdatePaginator(b2CacheName, 'vocabPaginator', data.totalCount, b2PageSize, dcGetPage)
	console.log("Populated " + data.rows.length + " b2Results");
};

// Fetch the data items for bucket 2 (matches by vocab term)
var b2Fetch = function() {
		qsShowSpinner('#b2Results');
		var url = fewiurl + '/quicksearch/vocabBucket?' + getQuerystring();
		dcStartCache(b2CacheName, url, b2Show, b2PageSize, '#b2Results');
		dcGetPage(b2CacheName, 1);
};