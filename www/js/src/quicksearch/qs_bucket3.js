/* Name: qs_bucket3.js
 * Purpose: data retrieval and processing logic for the quicksearch's third data bucket (results by ID)
 * Notes: Functions here will be prefixed by "b3".
 */

// Globals

var b3Failed = false;		// did retrieval for the ID bucket fail?

// Fetch the data items for bucket 3 (matches by ID).
var b3Fetch = function() {
		var url = fewiurl + '/quicksearch/idBucket?' + queryString;
		$.get(url, function(data) {
			try {
				b3Show(data);
			} catch (e) {
				console.log("Failed to display data for the ID bucket: " + e);
				failed = true;
			}
		}).fail(function() {
				console.log("Failed to retrieve data for the ID bucket: " + e);
				failed = true;
		});
};

// Having received 'data' from the server, show it on the page.
var b3Show = function(data) {
	if (data.summaryRows.length > 0) {
		var tbl = '<TABLE ID="b3Table">';
		tbl = tbl + '<TR><TH>Type</TH><TH>Name/Description</TH><TH>Why did this match?</TH></TR>';

		for (var i = 0; i < data.summaryRows.length; i++) {
			var item = data.summaryRows[i];
			tbl = tbl + '<TR><TD>' + item.mgiLink + '</TD>';
			tbl = tbl + '<TD>' + item.description + '</TD>';
			tbl = tbl + '<TD>' + item.displayType + ': ' + item.accId + '</TD></TR>';
		}
		$('#b3Results').html(tbl);
		console.log("Populated " + data.summaryRows.length + " b3Results");
	} else {
		console.log("No b3Results");
	}
};