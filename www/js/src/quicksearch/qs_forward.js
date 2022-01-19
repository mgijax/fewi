/* Name: qs_forward.js
 * Purpose: data retrieval and processing logic for the quicksearch's forwarding page
 * Notes: Functions here will be prefixed by "qsf".
 */

// Globals
var qsfCacheName = 'forwardingCache';
var qsfPageSize = 5000;
var qsfIDs = [];

// Functions

// Look up the count of items we expect to receive, then go fetch the data items to be forwarded.
var qsfGetData = function(url) {
	var failed = false;
	$.get(url + "&startIndex=0&results=0", function(data) {
		try {
			var expectedCount = data.totalCount;
			console.log('Identified ' + expectedCount + ' IDs to be retrieved');
			qsfGetIDs(url, expectedCount);
		} catch (e) {
			console.log('E1: Failed to identify IDs to forward: ' + e);
			failed = true;
		}
	}).fail(function() {
		console.log('E2: Failed to identify IDs to forward.');
		failed = true;
	});
	if (failed) {
		$('#statusDiv').html('Error in identifying IDs to be forwarded -- failed');
	}
}

// Fetch the data items to be forwarded, then do the forward.
var qsfGetIDs = function(url, expectedCount) {
	var failed = false;
	$.get(url + "&startIndex=0&results=" + expectedCount, function(data) {
		try {
			for (var i = 0; i < data.summaryRows.length; i++) {
				qsfIDs.push(data.summaryRows[i].item);
			}
			console.log('Got ' + qsfIDs.length + ' IDs');

			$('#statusDiv').html('Finished...  Forwarding...');
			$('#ids')[0].value = qsfIDs.join(delimiter);
			$('#forwardForm')[0].submit();
		} catch (e) {
			console.log('E3: Failed to get IDs to forward.');
			failed = true;
		}
	}).fail(function() {
		console.log('E4: Failed to get IDs to forward.');
		failed = true;
	});
	if (failed) {
		$('#statusDiv').html('Error in retrieving IDs to be forwarded -- failed');
	}
};