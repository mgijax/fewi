/* Name: qs_bucket3.js
 * Purpose: data retrieval and processing logic for the quicksearch's third data bucket (results by other ID)
 * Notes: Functions here will be prefixed by "b3".
 */

// Globals

var b3Failed = false;		// did retrieval for the ID bucket fail?
var b3PageSize = 100;
var b3CacheName = 'otherIdCache';

// Having received 'data' from the server, show it on the page.
var b3Show = function(data) {
	var tbl = '';
	if (data.rows.length > 0) {
		tbl = tbl + '<TABLE ID="b3Table">';
		tbl = tbl + '<TR><TH>Type</TH><TH>Name/Description</TH><TH>Why did this match?</TH></TR>';

		for (var i = 0; i < data.rows.length; i++) {
			var item = data.rows[i];
			
			if (item.objectSubtype === null) {
				tbl = tbl + '<TD style="padding-right: 30px"><a href="' + item.detailUri + '">' + item.objectType + '</a></TD>';
			} else {
				tbl = tbl + '<TD style="padding-right: 30px"><a href="' + item.detailUri + '">' + item.objectType + '</a>, ' + item.objectSubtype + '</TD>';
			}
			tbl = tbl + '<TD style="padding-right: 30px">' + item.name + '</TD>';
			tbl = tbl + '<TD><SPAN CLASS="termType">' + item.bestMatchType + '</SPAN>: <SPAN CLASS="small">' + item.bestMatchText + '</SPAN></TD></TR>';
		}
		$('#b3Results').html(tbl);
		console.log("Populated " + data.rows.length + " b3Results");
	} else {
		console.log("No b3Results");
	}

	var header = qsResultHeader(data.start, data.end, data.totalCount);
	var firstTime = false;							// first time through for the current search?
	if ($('#idCount').html().indexOf('(') < 0) {	// if no count on the Other IDs tab yet, then yes.
		firstTime = true;
	}
	$('#idCount').html("(" + commaDelimit(data.totalCount) + ")");
	$('#b3Counts').html(header);
	$('#b3Results').html(tbl);
	pgUpdatePaginator(b3CacheName, 'otherIdPaginator', data.totalCount, b3PageSize, dcGetPage)
	if (firstTime) {
		qsStyleTabText(data.totalCount, 5);
	}
};

// Fetch the data items for bucket 3 (matches by ID).
var b3Fetch = function() {
		qsShowSpinner('#b3Results');
		var url = fewiurl + '/quicksearch/otherBucket?' + getQuerystring();
		dcStartCache(b3CacheName, url, b3Show, b3PageSize, '#b3Results');
		dcGetPage(b3CacheName, 1);
};
