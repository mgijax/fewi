/* Name: qs_forward.js
 * Purpose: data retrieval and processing logic for the quicksearch's forwarding page
 * Notes: Functions here will be prefixed by "qsf".
 */

// Globals
var qsfCacheName = 'forwardingCache';
var qsfPageSize = 5000;
var qsfIDs = [];

// Functions

// Fetch the data items to be forwarded.
var qsfGetData = function(url) {
		dcStartCache(qsfCacheName, url, qsfShow, qsfPageSize, '#errorDiv');
		dcGetPage(qsfCacheName, 1);
};

// Having received 'data' from the server, update the page.
function qsfShow(data) {
	
	if (data.rows.length == 0) {
		$('#errorDiv').html("Error: No results found.");
		return;
	}
	
	for (var i = 0; i < data.rows.length; i++) {
		var uriParts = data.rows[i].detailUri.split('/');
		qsfIDs.push(uriParts[uriParts.length - 1]);
	}
	
	if ((data != null) && (data.rows != null) && (data.rows.length >= qsfPageSize)) {
		dcGetPage(qsfCacheName, 1 + Math.floor(qsfIDs.length / qsfPageSize));
	} else {
		$('#statusDiv').html('Finished...  Forwarding...');
		$('#ids')[0].value = qsfIDs.join(' ');
		$('#forwardForm')[0].submit();
	}

};