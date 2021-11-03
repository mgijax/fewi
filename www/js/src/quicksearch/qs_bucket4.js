/* Name: qs_bucket4.js
 * Purpose: data retrieval and processing logic for the quicksearch's fourth data bucket (strains)
 * Notes: Functions here will be prefixed by "b4".
 */

// Globals

var b4Failed = false;		// did retrieval for this bucket fail?
var b4PageSize = 100;
var b4CacheName = 'strainCache';

// Having received 'data' from the server, show it on the page.
function b4Show(data) {
	var toShow = b4PageSize;
	var tbl = '';
	if (data.rows.length > 0) {
		tbl = '<TABLE ID="b4Table">';
		tbl = tbl + '<TR><TH>Score</TH><TH class="termCol">Name</TH><TH class="dataCol">References</TH><TH class="bestMatchCol">Best Match</TH></TR>';

		toShow = Math.min(100, data.rows.length);
		for (var i = 0; i < toShow; i++) {
			var item = data.rows[i];
			tbl = tbl + '<TR>';
			if (item.stars === null) {
				tbl = tbl + '<TD>TBD</TD>';
			} else {
				tbl = tbl + '<TD>' + item.stars.replace(/[*]/g, "&#9733;") + '</TD>';
			}

			var name = qsSuperscript(item.name);
			var bestMatchText = qsSuperscript(item.bestMatchText);

			if (item.detailUri === null) {
				tbl = tbl + '<TD>' + name + '</TD>';
			} else {
				tbl = tbl + '<TD><a target="_blank" href="' + item.detailUri + '">' + name + '</a></TD>';
			}
			if (item.referenceCount > 0) {
				var label = ' references';
				if (item.referenceCount == 1) { label = ' reference'; }

				if (item.referenceUri === null) {
					tbl = tbl + '<TD><span class="small">' + item.referenceCount + label + '</span></TD>';
				} else {
					tbl = tbl + '<TD class="nowrap"><a class="small" target="_blank" href="' + item.referenceUri + '">'+ item.referenceCount + label + '</a></TD>';
				}
			} else {
				tbl = tbl + '<TD>&nbsp;</TD>';
			}

			if (item.bestMatchType === null) {
				tbl = tbl + '<TD>&nbsp;</TD></TR>';
			} else {
				tbl = tbl + '<TD><span class="termType">' + item.bestMatchType + '</span><span class="small">: ' + bestMatchText + '</span></TD></TR>';
			}
		}
	} else {
		console.log("No b4Results");
	}
	var header = qsResultHeader(data.start, data.end, data.totalCount);
	var firstTime = false;							// first time through for the current search?
	if ($('#sCount').html().indexOf('(') < 0) {	// if no count on the Strains tab yet, then yes.
		firstTime = true;
	}
	$('#sCount').html("(" + commaDelimit(data.totalCount) + ")");
	$('#b4Counts').html(header);
	$('#b4Results').html(tbl);
	pgUpdatePaginator(b4CacheName, 'strainPaginator', data.totalCount, b4PageSize, dcGetPage)
	if (firstTime) {
		qsStyleTabText(data.totalCount, 4);
	}
	console.log("Populated " + data.rows.length + " b4Results");
};

// Fetch the data items for bucket 4 (matches by strain)
var b4Fetch = function() {
		qsShowSpinner('#b4Results');
		var url = fewiurl + '/quicksearch/strainBucket?' + getQuerystring();
		dcStartCache(b4CacheName, url, b4Show, b4PageSize, '#b4Results');
		dcGetPage(b4CacheName, 1);
};