/* Name: qs_bucket2.js
 * Purpose: data retrieval and processing logic for the quicksearch's second data bucket (vocab terms + strains)
 * Notes: Functions here will be prefixed by "b2".
 */

// Globals

var b2Failed = false;		// did retrieval for this bucket fail?

// Fetch the data items for bucket 2 (matches by strain or vocab term)
var b2Fetch = function() {
		var url = fewiurl + '/quicksearch/vocabBucket?' + queryString;
		$.get(url, function(data) {
			try {
				b2Show(data);
			} catch (e) {
				console.log("Failed to display data for the vocab/strain bucket: " + e);
				failed = true;
			}
		}).fail(function() {
				console.log("Failed to retrieve data for the vocab/strain bucket: " + e);
				failed = true;
		});
};

// Having received 'data' from the server, show it on the page.
var b2Show = function(data) {
	if (data.summaryRows.length > 0) {
		var tbl = '<TABLE ID="b2Table">';
		tbl = tbl + '<TR><TH>Score</TH><TH>Term</TH><TH>Associated Data</TH><TH>Best Mactch</TH></TR>';

		for (var i = 0; i < Math.min(100, data.summaryRows.length); i++) {
			var item = data.summaryRows[i];
			tbl = tbl + '<TR><TD>TBD</TD>';
			tbl = tbl + '<TD>' + item.vocabName + ': ' + item.term + '</TD>';
			if (item.annotationCount > 0) {
				tbl = tbl + '<TD>' + item.annotationText + '</TD>';
			} else {
				tbl = tbl + '<TD>&nbsp;</TD>';
			}
			tbl = tbl + '<TD>TBD</TD></TR>';
		}
		$('#b2Results').html(tbl);
		console.log("Populated " + data.summaryRows.length + " b2Results");
	} else {
		console.log("No b2Results");
	}
};