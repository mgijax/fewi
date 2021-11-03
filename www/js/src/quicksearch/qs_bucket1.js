/* Name: qs_bucket1.js
 * Purpose: data retrieval and processing logic for the quicksearch's first data bucket (markers)
 * Notes: Functions here will be prefixed by "b1".
 */

// Globals

var b1Failed = false;		// did retrieval for this bucket fail?
var b1PageSize = 100;
var b1CacheName = 'featureCache';

// Having received 'data' from the server, show it on the page.
function b1Show(data) {
	var tbl = '';
	var toShow = b1PageSize;
	if (data.rows.length > 0) {
		tbl = '<TABLE ID="b1Table">';
		tbl = tbl + '<TR><TH>Score</TH><TH>Type</TH><TH>Symbol</TH><TH>Name</TH><TH>Chr</TH><TH>Location (Genome Build)</TH><TH>Str</TH><TH>Best Match</TH></TR>';

		toShow = Math.min(100, data.rows.length);
		for (var i = 0; i < toShow; i++) {
			var item = data.rows[i];
			tbl = tbl + '<TR><TD>' + item.stars.replace(/[*]/g, "&#9733;") + '</TD>';
			tbl = tbl + '<TD class="small">' + item.featureType + '</TD>';

			var symbol = qsSuperscript(item.symbol);
			var name = qsSuperscript(item.name);
			var bestMatchText = qsSuperscript(item.bestMatchText);
			
			if (item.detailUrl === null) {
				tbl = tbl + '<TD>' + symbol + '</TD>';
			} else {
				tbl = tbl + '<TD><a target="_blank" href="' + item.detailUri + '">' + symbol + '</a></TD>';
			}
			tbl = tbl + '<TD class="nameCol">' + name + '</TD>';

			if (item.chromosome === null) {
				tbl = tbl + '<TD class="small">&nbsp;</TD>';
			} else {
				tbl = tbl + '<TD class="small">' + item.chromosome + '</TD>';
			}

			if (item.location === null) {
				tbl = tbl + '<TD>&nbsp;</TD>';
			} else if (item.location.indexOf('-') < 0){
				tbl = tbl + '<TD class="nowrap small">' + item.location + '</TD>';
			} else {
				tbl = tbl + '<TD class="nowrap small">' + item.location + ' (' + genomeBuild + ')</TD>';
			}

			if (item.strand === null) {
				tbl = tbl + '<TD>&nbsp;</TD>';
			} else {
				tbl = tbl + '<TD class="small">' + item.strand + '</TD>';
			}
			if (item.bestMatchType === null) {
				tbl = tbl + '<TD>&nbsp;</TD></TR>';
			} else {
				tbl = tbl + '<TD><span class="termType">' + item.bestMatchType + '</span><span class="small">: ' + bestMatchText + '</span></TD></TR>';
			}
		}
	} else {
		console.log("No b1Results");
	}

	var firstTime = false;							// first time through for the current search?
	if ($('#fCount').html().indexOf('(') < 0) {	// if no count on the Features tab yet, then yes.
		firstTime = true;
	}
	$('#fCount').html("(" + commaDelimit(data.totalCount) + ")");
	var header = qsResultHeader(data.start, data.end, data.totalCount);
	$('#b1Counts').html(header);
	$('#b1Results').html(tbl);
	pgUpdatePaginator(b1CacheName, 'featurePaginator', data.totalCount, b1PageSize, dcGetPage);
	if (firstTime) {
		qsStyleTabText(data.totalCount, 1);
	}
	console.log("Populated " + data.rows.length + " b1Results");
};

// Fetch the data items for bucket 1 (matches by marker or allele)
var b1Fetch = function() {
		qsShowSpinner('#b1Results');
		var url = fewiurl + '/quicksearch/featureBucket?' + getQuerystring();
		dcStartCache(b1CacheName, url, b1Show, b1PageSize, '#b1Results');
		dcGetPage(b1CacheName, 1);
};
