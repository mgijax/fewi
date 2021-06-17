/* Name: qs_bucket3.js
 * Purpose: data retrieval and processing logic for the quicksearch's third data bucket (results by other ID)
 * Notes: Functions here will be prefixed by "b3".
 */

// Globals

var b3Failed = false;		// did retrieval for the ID bucket fail?

// Fetch the data items for bucket 3 (matches by ID).
var b3Fetch = function() {
		var url = fewiurl + '/quicksearch/otherBucket?' + queryString;
		$.get(url, function(data) {
			try {
				b3Show(data);
			} catch (e) {
				console.log("Failed to display data for the Other ID bucket: " + e);
				failed = true;
			}
		}).fail(function() {
				console.log("Failed to retrieve data for the Other ID bucket: " + e); 
				failed = true;
		});
};

// Having received 'data' from the server, show it on the page.
var b3Show = function(data) {
	var tbl = '';
	if (data.summaryRows.length > 0) {
		tbl = tbl + '<TABLE ID="b3Table">';
		tbl = tbl + '<TR><TH>Type</TH><TH>Name/Description</TH><TH>Why did this match?</TH></TR>';

		for (var i = 0; i < data.summaryRows.length; i++) {
			var item = data.summaryRows[i];
			
			if (item.objectSubtype === null) {
				tbl = tbl + '<TD style="padding-right: 30px"><a href="' + item.detailUri + '">' + item.objectType + '</a></TD>';
			} else {
				tbl = tbl + '<TD style="padding-right: 30px"><a href="' + item.detailUri + '">' + item.objectType + '</a>, ' + item.objectSubtype + '</TD>';
			}
			tbl = tbl + '<TD style="padding-right: 30px">' + item.name + '</TD>';
			tbl = tbl + '<TD><SPAN CLASS="termType">' + item.bestMatchType + '</SPAN>: <SPAN CLASS="small">' + item.bestMatchText + '</SPAN></TD></TR>';
		}
		$('#b3Results').html(tbl);
		console.log("Populated " + data.summaryRows.length + " b3Results");
	} else {
		console.log("No b3Results");
	}

	var header = qsResultHeader(1, data.totalCount, data.totalCount);
	$('#idCount').html("(" + commaDelimit(data.totalCount) + ")");
	$('#b3Counts').html(header);
	$('#b3Results').html(tbl);
	qsStyleTabText(data.totalCount, 5);
};