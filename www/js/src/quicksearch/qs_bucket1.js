/* Name: qs_bucket1.js
 * Purpose: data retrieval and processing logic for the quicksearch's first data bucket (markers + alleles)
 * Notes: Functions here will be prefixed by "b1".
 */

// Globals

var b1Failed = false;		// did retrieval for this bucket fail?

// Fetch the data items for bucket 1 (matches by marker or allele)
var b1Fetch = function() {
		var url = fewiurl + '/quicksearch/featureBucket?' + getQuerystring();
		$.get(url, function(data) {
			try {
				b1Show(data);
			} catch (e) {
				console.log("Failed to display data for the feature bucket: " + e);
				failed = true;
			}
		}).fail(function() {
				console.log("Failed to retrieve data for the feature bucket: " + e);
				failed = true;
		});
};

// Having received 'data' from the server, show it on the page.
var b1Show = function(data) {
	var tbl = '';
	var toShow = 100;
	if (data.summaryRows.length > 0) {
		tbl = '<TABLE ID="b1Table">';
		tbl = tbl + '<TR><TH>Score</TH><TH>Type</TH><TH>Symbol</TH><TH>Name</TH><TH>Chr</TH><TH>Location</TH><TH>Str</TH><TH>Best Match</TH></TR>';

		toShow = Math.min(100, data.summaryRows.length);
		for (var i = 0; i < toShow; i++) {
			var item = data.summaryRows[i];
			tbl = tbl + '<TR><TD>' + item.stars.replace(/[*]/g, "&#9733;") + '</TD>';
			tbl = tbl + '<TD>' + item.featureType + '</TD>';

			var symbol = qsSuperscript(item.symbol);
			var name = qsSuperscript(item.name);
			var bestMatchText = qsSuperscript(item.bestMatchText);
			
			if (item.detailUrl === null) {
				tbl = tbl + '<TD>' + symbol + '</TD>';
			} else {
				tbl = tbl + '<TD><a href="' + item.detailUri + '">' + symbol + '</a></TD>';
			}
			tbl = tbl + '<TD>' + name + '</TD>';
			tbl = tbl + '<TD>' + item.chromosome + '</TD>';

			if ((item.startCoord === null) || (item.endCoord === null)) {
				tbl = tbl + '<TD>&nbsp;</TD>';
			} else {
				tbl = tbl + '<TD class="nowrap">' + item.startCoord + '-' + item.endCoord + '</TD>';
			}

			if (item.strand === null) {
				tbl = tbl + '<TD>&nbsp;</TD>';
			} else {
				tbl = tbl + '<TD>' + item.strand + '</TD>';
			}
			if (item.bestMatchType === null) {
				tbl = tbl + '<TD>&nbsp;</TD></TR>';
			} else {
				tbl = tbl + '<TD>' + item.bestMatchType + ': ' + bestMatchText + '</TD></TR>';
			}
		}
	} else {
		console.log("No b1Results");
	}
	var header = qsResultHeader(1, toShow, data.totalCount);
	$('#b1Counts').html(header);
	$('#b1Results').html(tbl);
	console.log("Populated " + data.summaryRows.length + " b1Results");
};