/* Name: rnaseq_heatmap.js
 * Purpose: supports data retrieval, processing, and display on the RNA-Seq heat map popup
 * Assumes: queryString is available from the JSP as a Javascript variable.
 */

var logging = true;		// is logging to the console enabled? (true/false)
var lastTime = new Date().getTime();	// time in ms when last message was logged
var spinner = "<img src='/fewi/mgi/assets/images/loading.gif' height='24' width='24'>";
var startTime = new Date().getTime();	// time in ms when script loaded

// log a message to the browser console, if logging is enabled
function log(msg) {
    if (logging) {
	    try {
	    	var elapsed = new Date().getTime() - lastTime;
    		console.log(elapsed + 'ms : ' + msg);
	    	lastTime = new Date().getTime();
    	} catch (c) {}
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

// If countDone is non-null, show a progress meter showing the progress from 'countDone' to global 'totalCount'.
function updateStatus(countDone = null) {
	if (countDone != null) {
		try {
			var s = progressMeter(countDone, totalCount);
			$('#statusUpdates').empty();
			$('#statusUpdates').html(s);
			var margin = ($(window).width() - $('#progressMeter').width()) / 2;
			$('#progressMeter').css('margin-left', margin + 'px');
		} catch (e) {
			console.log('Failed to bulid progressMeter: ' + e);
		}
	}
}

// update the #loadingMessage div to show the given message in 's', appending a user support link if specified.
// If supportLink is true, give a message about contacting user support.
function updateLoadingMessage(s, supportLink = true) {
	if (supportLink) {
		s = s + '<br/> Please write User Support (<a href="${configBean.MGIHOME_URL}"/support/mgi_inbox.shtml" target="_blank">mgi-help@jax.org</a>) with your search parameters.';
	}
	$('#loadingMessage').empty();
	$('#loadingMessage').html(s);
}

// cellTPM[marker ID][sample key] = average quantile-normalized TPM value
var cellTPM = {};

// number of cells to request in a single batch (sized to have progress meter updates every 11-12 seconds)
var chunkSize = 150000;

// number of data cells already retrieved
var countDone = 0;

// number of data cells to retrieve
var totalCount = 0;

// list of chunks of cells to retrieve, each a sublist as [ start index (inclusive), end index (exclusive) ]
var chunks = [];

// number of chunks where we've sent a request and are currently waiting for results
var chunksInProgress = 0;

// max number of chunks to request simultaneously
var maxRequests = 3;

// Have we failed yet?  If so, we should ignore pending data requests and not overwrite error messages.
var failed = false;

// map of sample keys
var sampleKeys = {};

// data structure for Morpheus (complex)
var hmData = {};

// Reset the hmData structure (for Morpheus) and fill in the simple fields.
function initializeHmData(sampleList, markerList) {
	hmData = {};		// reset structure
	hmData['rows'] = markerList.length;
	hmData['columns'] = sampleList.length;
	hmData['seriesDataTypes'] = [ 'Float32' ];
	hmData['seriesNames'] = [ 'Mouse RNA-Seq Heat Map of GXD search results' ];
}

// Populate the sample IDs into hmData.
function fillInSampleIDs(sampleList) {
	hmData['sampleIDs'] = [];
	var sample;
	for (var s = 0; s < sampleList.length; s++) {
		sample = sampleList[s];
		hmData['sampleIDs'].push(sample['bioreplicateSetID']);
	}
	log('Collected ' + hmData['sampleIDs'].length + ' sample IDs');
}

// Populate sample data into hmData.
function fillInSamples(sampleList) {
	hmData['columnMetadataModel'] = {
		'vectors' : [
			{
				'name' : 'label',
				'array' : []
			},
			{
				'name' : 'structure',
				'array' : []
			},
			{
				'name' : 'age',
				'array' : []
			},
			{
				'name' : 'stage',
				'array' : []
			},
			{
				'name' : 'alleles',
				'array' : []
			},
			{
				'name' : 'strain',
				'array' : []
			},
			{
				'name' : 'sex',
				'array' : []
			},
			{
				'name' : 'expID',
				'array' : []
			},
			{
				'name' : 'bioreplicateCount',
				'array' : []
			},
			{
				'name' : 'MGI_BioReplicateSet_ID',
				'array' : []
			}
		]
	};

	var sample;
	for (var s = 0; s < sampleList.length; s++) {
		sample = sampleList[s];
		hmData['columnMetadataModel']['vectors'][0]['array'].push(sample['label']);
		hmData['columnMetadataModel']['vectors'][1]['array'].push(sample['structure']);
		hmData['columnMetadataModel']['vectors'][2]['array'].push(sample['age']);
		hmData['columnMetadataModel']['vectors'][3]['array'].push(String(sample['stage']));
		hmData['columnMetadataModel']['vectors'][4]['array'].push(sample['alleles']);
		hmData['columnMetadataModel']['vectors'][5]['array'].push(sample['strain']);
		hmData['columnMetadataModel']['vectors'][6]['array'].push(sample['sex']);
		hmData['columnMetadataModel']['vectors'][7]['array'].push(sample['expID']);
		hmData['columnMetadataModel']['vectors'][8]['array'].push(String(sample['bioreplicateCount']));
		hmData['columnMetadataModel']['vectors'][9]['array'].push(sample['bioreplicateSetID']);
	}

	log('Collected ' + hmData['columnMetadataModel']['vectors'].length + ' sample vectors');
}

// Populate marker data into hmData.
function fillInMarkers(markerList) {
	hmData['rowMetadataModel'] = {
		'vectors' : [
			{
				'name' : 'Gene Symbol',
				'array' : []
			},
			{
				'name' : 'MGI ID',
				'array' : []
			},
			{
				'name' : 'Ensembl ID',
				'array' : []
			}
		]
	};

	var marker;
	for (var m = 0; m < markerList.length; m++) {
		marker = markerList[m];
		hmData['rowMetadataModel']['vectors'][0]['array'].push(marker['symbol']);
		hmData['rowMetadataModel']['vectors'][1]['array'].push(marker['markerID']);
		hmData['rowMetadataModel']['vectors'][2]['array'].push(marker['ensemblGMID']);
	}

	log('Collected ' + hmData['rowMetadataModel']['vectors'].length + ' marker vectors');
}

// Actually build the structure of TPM values into hmData.
function fillInCells(sampleList, markerList) {
	var notStudied = null;		// flag for cells that have not been studied

	// Data are in a list of rows, with each row being a list of column values.
	// Initially all will be notStudied.
	
	hmData['seriesArrays'] = [[]];					// only 1 series, but allow for multiples
	for (var r = 0; r < hmData['rows']; r++) {
		hmData['seriesArrays'][0].push([]);				// add new empty row
		for (var c = 0; c < hmData['columns']; c++) {
			hmData['seriesArrays'][0][r].push(notStudied);	// add another column to that row
		}
	}
	
	log('Created data array (' + hmData['rows'] + ' x ' + hmData['columns'] + ')');
	
	// Now populate the cells with real data from cellTPM.
	
	var marker;
	var markerID;
	var sample;
	var sampleID;
	var cells = 0;
	for (var m = 0; m < markerList.length; m++) {
		marker = markerList[m];
		markerID = marker['markerID'];
		for (var s = 0; s < sampleList.length; s++) {
			sample = sampleList[s];
			sampleID = parseInt(sample['bioreplicateSetID']);
			if ((markerID in cellTPM) && (sampleID in cellTPM[markerID])) {
				hmData['seriesArrays'][0][m][s] = parseFloat(cellTPM[markerID][sampleID]);
				cells++;
			}
		}
	}
	log('Filled data array with ' + cells + ' cells');
}

// Slice and dice the data to produce the data for Morpheus.  Then hand off to Morpheus to render the heat map.
function buildDataForMorpheus(sampleList, markerList) {
	updateLoadingMessage(spinner + ' Collating cells, genes, and samples...', false);
	initializeHmData(sampleList, markerList);
	fillInSampleIDs(sampleList);
	fillInSamples(sampleList);
	fillInMarkers(markerList);
	fillInCells(sampleList, markerList);
	log('Finished building data for Morpheus');

	$('#heatmapWrapper').empty();
	
	new morpheus.HeatMap({
	    el: $('#heatmapWrapper'),
	    dataset: hmData,
	    colorScheme: {
	      type: 'fractions',
	      scalingMode: 1,
	      stepped: false,
	      min: 0,
	      max: 5000,
	      missingColor: '#FFFFFF',
	      map: colorMap
  		  }
	  }); 

	// Hide the tab title at the top of the heat map, as it has odd characters that I can't get
	// to disappear (and it's not overly useful anyway, for our purposes).
	$('li.morpheus-sortable[role=presentation]').css('display', 'none');
  
	// Show the Tips popup after a brief delay, so we give the browser's scrollbars time to get
	// into place.
	setTimeout(function() { showPopup() }, 500);
	var elapsed = new Date().getTime() - startTime;
	log('Heatmap displayed (' + elapsed + ' ms)');
}

// Get data for the samples we found.  Then continue onward and build the data structure for Morpheus.
function retrieveSamples(sampleKeys, markerList) {
	if (sampleKeys.length == 0) {
		updateLoadingMessage('Found no samples.', false);
	}
	var url = fewiurl + '/gxd/rnaSeqHeatMap/samples';
	var parameters = queryString + '&hmSampleKeys=' + String(sampleKeys);
	updateLoadingMessage(spinner + ' Getting data for ' + Number(sampleKeys.length).toLocaleString() + ' samples...', false);
	
	$.post(url, parameters, function(sampleList) {
		try {
			log('Got ' + sampleList.length + ' samples');
			buildDataForMorpheus(sampleList, markerList);
		} catch (e) {
			updateLoadingMessage('Failed to retrieve samples for ' + sampleKeys.length + ' keys: ' + e);
		} })
		.fail(function() {
			updateLoadingMessage('Failed to retrieve samples (communication error).');
		});
}

// Get data for the markers we found.  Then continue onward and get data for samples next.
function retrieveMarkers(markerIDs, sampleKeys) {
	if (markerIDs.length == 0) {
		updateLoadingMessage('Found no genes.', false);
	}
	var url = fewiurl + '/gxd/rnaSeqHeatMap/markers';
	var parameters = queryString + '&hmMarkerIDs=' + String(markerIDs);
	updateLoadingMessage(spinner + ' Getting data for ' + Number(markerIDs.length).toLocaleString() + ' genes...', false);
	
	$.post(url, parameters, function(markerList) {
		try {
			log('Got ' + markerList.length + ' markers');
			retrieveSamples(sampleKeys, markerList);
		} catch (e) {
			updateLoadingMessage('Failed to retrieve genes for ' + markerIDs.length + ' IDs');
		} })
		.fail(function() {
			updateLoadingMessage('Failed to retrieve genes (communication error).');
		});
}

// Process the given chunk of data (cells from the server)
function processChunk(cellList, start, end) {
	if (failed) {
		return;		// Just bail out with no further updates, in case of another retrieval failing.
	}
	chunksInProgress = chunksInProgress - 1;

	var cell, cellString;
	var markerID, csmKey, tpm;

	// Transferring cell data as a comma-delimited string rather than a dictionary reduces data transit
	// by roughly 50%.
				
	for (cellString of cellList) {
		cell = cellString.split(',');		// marker ID, sample key, TPM
		markerID = cell[0];
		csmKey = cell[1];
		tpm = cell[2];
					
		// For each (marker, sample) pair keep its TPM value.
		if (!(markerID in cellTPM)) {
			cellTPM[markerID] = {}
		}
		cellTPM[markerID][csmKey] = tpm;
					
		// Remember which sample keys we've seen.
		if (!(csmKey in sampleKeys)) {
			sampleKeys[csmKey] = 1;
		}
	}

	countDone = countDone + cellList.length;
	if (countDone < totalCount) {
		updateStatus(countDone);
	}
	retrieveChunk();		// If more chunks to gather, do the next one.
}

// Go ahead and retrieve the different chunks of data, then process them.
function retrieveChunk() {
	// More chunks to do?  Grab one and go.
	if (chunks.length > 0) {
		[ start, end ] = chunks.pop();
		chunksInProgress = chunksInProgress + 1;
		
                var queryStringChunk = queryString + '&start=' + start + '&end=' + end
		var url = fewiurl + '/gxd/rnaSeqHeatMap/recordSlice'
                $.post(url, queryStringChunk, function(cellList) {
			try {
				log('Got ' + cellList.length + ' cells from ' + start + ' to ' + end);
				processChunk(cellList, start, end);
			} catch (e) {
				updateLoadingMessage('Failed to retrieve cells from ' + start + ' to ' + end + ": " + e);
				failed = true;
			}
		}).fail(function() {
				updateLoadingMessage('Failed to retrieve cells (communication error).');
				failed = true;
		});
	} else if (chunksInProgress == 0) {
		// All chunks have been retrieved, so process them.  We should have data in cellTPM and sampleKeys.
		// Identify the unique marker IDs, building them and the sample keys into lists.
		var markerIDs = [];
		for (markerID in cellTPM) {
			markerIDs.push(markerID);
		}
		log('Got ' + markerIDs.length + ' marker IDs');
		
		var sampleKeyList = [];
		for (sampleKey in sampleKeys) {
			sampleKeyList.push(sampleKey);
		}
		log('Got ' + sampleKeyList.length + ' sample keys');
		$('#statusUpdates').empty();
		
		retrieveMarkers(markerIDs, sampleKeyList);
	} else {
		log('Waiting for other chunks to finish');
		// Other chunks are still being processed, so just drop this line of processing and wait for them.
	}
}

// Populate the global 'chunks' to request 'totalCount' data cells, then retrieve them.
function identifyChunks() {
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
	log('Created ' + chunks.length + ' chunks');
	updateLoadingMessage(spinner + ' Getting TPM values from GXD...', false);
	
	// start up multiple simultaneous requests of the server
	for (var i = 0; i < maxRequests; i++) {
		retrieveChunk();
	}
}

// Having identified the 'totalCount' of cells to retrieve, go ahead and gather them.
function getCells() {
	cellTPM = {};		// reset mapping
	try {
		identifyChunks();
	} catch (e) {
		console.log('Error: ' + e);
	}
}

// Start processing data retrieval for the RNA-Seq heat map, initially getting the count of data cells.
function main() {
	updateLoadingMessage(spinner + ' Preparing...', false);

	// get total count of records to be processed
	
	var url = fewiurl + '/gxd/rnaSeqHeatMap/totalCount'
	$.post(url, queryString, function(data) {
		try {
			// Once we get the count, start fetching cells.
			totalCount = parseInt(data);
			log('Got totalCount of ' + data);
			getCells(queryString);
		} catch (e) {
			updateLoadingMessage('Non-integer count of records.');
		} })
		.fail(function() {
			updateLoadingMessage('Failed to retrieve total count of records.');
		});
}
