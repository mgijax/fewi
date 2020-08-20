/* Name: rnaseq_heatmap.js
 * Purpose: supports data retrieval, processing, and display on the RNA-Seq heat map popup
 * Assumes: queryString is available from the JSP as a Javascript variable.
 */

var logging = true;	// is logging to the console enabled? (true/false)

// log a message to the browser console, if logging is enabled
function log(msg) {
    if (logging) {
	    try {
    		console.log(msg);
    	} catch (c) {
    	    setTimeout(function() { throw new Error(msg); }, 0);
    	}
   	}
}

// Definitions for cell coloring based on average quantile-normalized TPM value.
// Note that colorMap should not contain definitions less than zero.  (Below zero numbers make
// all the cells show up as white.)
var colorMap = [
	{ value: 0,
		color: '#E0E0E0'
		},
	{ value: 0.0000998,
		color: '#E0E0E0'
		},
	{ value: 0.0001000,
		color: '#98CDF4'
		},
	{ value: 0.0011500,
		color: '#45AFFD'
		},
	{ value: 0.0022000,
		color: '#3292E4'
		},
	{ value: 0.1012000,
		color: '#1E74CA'
		},
	{ value: 0.2002000,
		color: '#105FAD'
		},
	{ value: 0.4001000,
		color: '#024990'
		},
	{ value: 0.6000000,
		color: '#000066'
		},
	{ value: 1,
		color: '#000000'
		}
];

// Show the legend popup.
function showPopup() {
	$('#tipsPopup').dialog( {
		title : 'Heat Map Legend',
		width : '400px',
		position : { my: 'right center', at: 'right center', within: window }
	} );
}

// update the #loadingMessage div to show the given message in 's', appending a user support link if specified.
function updateLoadingMessage(s, supportLink = true) {
	if (supportLink) {
		s = s + ' Please write User Support (<a href="${configBean.MGIHOME_URL}"/support/mgi_inbox.shtml" target="_blank">mgi-help@jax.org</a>) with your search parameters.';
	}
	$('#loadingMessage').empty();
	$('#loadingMessage').html(s);
}

// cellTPM[marker ID][sample key] = average quantile-normalized TPM value
var cellTPM = {};

// number of cells to request in a single batch
var chunkSize = 250000;

// number of data cells to retrieve
var totalCount = 0;

// list of chunks of cells to retrieve, each a sublist as [ start index (inclusive), end index (exclusive) ]
var chunks = [];

// map of sample keys
var sampleKeys = {};

// Slice and dice the data to produce the data for Morpheus.
function buildDataForMorpheus() {
	
}

// Get data for the samples we found.
function retrieveSamples(){
	
}

// Get data for the markers we found.
function retrieveMarkers() {
	
}

// Go ahead and retrieve the different chunks of data, then process them.
function retrieveNextChunk() {
	if (chunks.length > 0) {
		[ start, end ] = chunks.pop()
		
		var url = fewiurl + '/gxd/rnaSeqHeatMap/recordSlice?' + queryString + '&start=' + start + '&end=' + end;
		$.get(url, function(cellList) {
			try {
				log('Got ' + cellList.length + ' cells between ' + start + ' and ' + end);
				var cell;
				for (cell of cellList) {

					// For each (marker, sample) pair keep its TPM value.
					if (!(cell.markerID in cellTPM)) {
						cellTPM[cell.markerID] = {}
					}
					cellTPM[cell.markerID][cell.csmKey] = cell.avgQnTpm;
					
					// Remember which sample keys we've seen.
					if (!(cell.csmKey in sampleKeys)) {
						sampleKeys[cell.csmKey] = 1;
					}
				}
				retrieveNextChunk();		// If more chunks to gather, do the next one.

			} catch (e) {
				updateLoadingMessage('Failed to retrieve cells from ' + start + ' to ' + end + ": " + e);
			} })
			.fail(function() {
				updateLoadingMessage('Failed to retrieve cells (communication error).');
			});
	} else {
		// All chunks have been retrieved, so process them.  We should have data in cellTPM and sampleKeys.
		// Identify the unique marker IDs, building them and the sample keys into lists.
		var markerIDs = [];
		for (markerID in cellTPM) {
			markerIDs.push(markerID);
		}
		
		var sampleKeyList = [];
		for (sampleKey in sampleKeys) {
			sampleKeyList.push(sampleKey);
		}
		log('Got ' + markerIDs.length + ' marker IDs');
		log('Got ' + sampleKeyList.length + ' sample keys');
	}
}

// Populate the global 'chunks' to request 'totalCount' data cells, then retrieve them.
function identifyChunks(totalCount) {
	chunks = [];
	cellTPM = {};
	sampleKeys = {};

	var start = 0;
	while (start < totalCount) {
		var end = start + chunkSize;
		chunks.push( [ start, end ] );
		log('Identified chunk from ' + start + ' to ' + end);
		start = end;
	}
	retrieveNextChunk();
}

// Having identified the 'totalCount' of cells to retrieve, go ahead and gather them.
function getCells(totalCount) {
	cellTPM = {};		// reset mapping
	identifyChunks(totalCount);
}

// Start processing data retrieval for the RNA-Seq heat map, initially getting the count of data cells.
function main() {
	updateLoadingMessage('<img src="/fewi/mgi/assets/images/loading.gif" height="24" width="24"> Loading data from GXD...');

	// get total count of records to be processed
	
	var totalCount = -1;
	var url = fewiurl + '/gxd/rnaSeqHeatMap/totalCount?' + queryString;
	$.get(url, function(data) {
		try {
			// Once we get the count, start fetching cells.
			getCells(queryString, parseInt(data));
		} catch (e) {
			updateLoadingMessage('Non-integer count of records.');
		} })
		.fail(function() {
			updateLoadingMessage('Failed to retrieve total count of records.');
		});
}