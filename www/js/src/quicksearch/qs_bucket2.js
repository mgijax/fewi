/* Name: qs_bucket2.js
 * Purpose: data retrieval and processing logic for the quicksearch's second data bucket (vocab terms + strains)
 * Notes: Functions here will be prefixed by "b2".
 */

// Globals

var b2Failed = false;		// did retrieval for this bucket fail?

// Fetch the data items for bucket 2 (matches by strain or vocab term)
var b2Fetch = function() {
		var url = fewiurl + '/quicksearch/vocabBucket?' + getQuerystring();
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
	var toShow = 100;
	var tbl = '';
	if (data.summaryRows.length > 0) {
		tbl = '<TABLE ID="b2Table">';
		tbl = tbl + '<TR><TH>Score</TH><TH>Term</TH><TH>Associated Data</TH><TH>Best Match</TH></TR>';

		toShow = Math.min(100, data.summaryRows.length);
		for (var i = 0; i < toShow; i++) {
			var item = data.summaryRows[i];
			tbl = tbl + '<TR>';
			if (item.stars === null) {
				tbl = tbl + '<TD>TBD</TD>';
			} else {
				tbl = tbl + '<TD>' + item.stars.replace(/[*]/g, "&#9733;") + '</TD>';
			}
			if (item.detailUri === null) {
				tbl = tbl + '<TD>' + item.vocabName + ': ' + item.term + '</TD>';
			} else {
				tbl = tbl + '<TD>' + item.vocabName + ': <a href="' + item.detailUri + '">' + item.term + '</a></TD>';
			}
			if (item.annotationCount > 0) {
				if (item.annotationUri === null) {
					tbl = tbl + '<TD>' + item.annotationText + '</TD>';
				} else {
					tbl = tbl + '<TD><a href="' + item.annotationUri + '">'+ item.annotationText + '</a></TD>';
				}
			} else {
				tbl = tbl + '<TD>&nbsp;</TD>';
			}

			if (item.bestMatchType === null) {
				tbl = tbl + '<TD>&nbsp;</TD></TR>';
			} else {
				tbl = tbl + '<TD>' + item.bestMatchType + ': ' + item.bestMatchText + '</TD></TR>';
			}
		}
	} else {
		console.log("No b2Results");
	}
	var header = qsResultHeader(1, toShow, data.totalCount);
	$('#b2Counts').html(header);
	$('#b2Results').html(tbl);
	console.log("Populated " + data.summaryRows.length + " b2Results");
};