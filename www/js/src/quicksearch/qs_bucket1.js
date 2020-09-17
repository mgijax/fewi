/* Name: qs_bucket1.js
 * Purpose: data retrieval and processing logic for the quicksearch's first data bucket (markers + alleles)
 * Notes: Functions here will be prefixed by "b1".
 */

// Globals

var b1Failed = false;		// did retrieval for this bucket fail?

// Fetch the data items for bucket 1 (matches by marker or allele)
var b1Fetch = function() {
		var url = fewiurl + '/quicksearch/featureBucket?' + queryString;
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
	if (data.summaryRows.length > 0) {
		var tbl = '<TABLE ID="b1Table">';
		tbl = tbl + '<TR><TH>Score</TH><TH>Type</TH><TH>Symbol</TH><TH>Name</TH><TH>Chr</TH><TH>Location</TH><TH>Str</TH><TH>Best Match</TH></TR>';

		for (var i = 0; i < Math.min(100, data.summaryRows.length); i++) {
			var item = data.summaryRows[i];
			tbl = tbl + '<TR><TD>TBD</TD>';
			tbl = tbl + '<TD>' + item.featureType + '</TD>';

			if (item.detailUrl === null) {
				tbl = tbl + '<TD>' + item.symbol + '</TD>';
			} else {
				tbl = tbl + '<TD><a href="' + item.detailUri + '">' + item.symbol + '</a></TD>';
			}
			tbl = tbl + '<TD>' + item.name + '</TD>';
			tbl = tbl + '<TD>TBD</TD>';
			tbl = tbl + '<TD>TBD</TD>';
			tbl = tbl + '<TD>TBD</TD>';
			tbl = tbl + '<TD>TBD</TD></TR>';
		}
		$('#b1Results').html(tbl);
		console.log("Populated " + data.summaryRows.length + " b1Results");
	} else {
		console.log("No b1Results");
	}
};