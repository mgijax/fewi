/* Name: DataCache.js
 * Purpose: To aid performance (from the user's perspective), this module aids in pre-fetching data
 *   for display in a paginated interface.  This was built for the new Quick Search, where we want
 *   to be able to have separate paginators for the different buckets (feature, vocab terms, strains).
 * Notes: Members of this class that are intended for public use will begin with a dc* prefix.  Ones
 *   intended for private use will begin with a pdc* prefix.  Accessing private data elements or
 *   functions with the private prefix (pdc) may have unintended consequences.
 */

/*** private data members ***/

// turn on/off logging messages from this module to the console
var pdcLogging = true;

// number of pages we desire to have in cache ahead of where we are currently
var pdcLookAhead = 4;

// maps from cache name to a list of its cached data elements
var pdcCache = {};

// maps from a cache name to its current page size
var pdcPageSize = {};

// maps from a cache name to its URL (except for pagination parameters) for fetching data
var pdcUrl = {};

// maps from a cache name to its callback function (which is called and passed a list of data,
// once they are available)
var pdcCallback = {};

// maps from a cache name to its total count of matches
var pdcTotalCount = {}

// maps from a cache name to the div where a spinner should be displayed
var pdcSpinnerDiv = {}

/*** public functions ***/

/* Instantiate a new cache. (Or clear and redefine an existing one, if that 'cacheName' is
 * already in use.)  Use the given 'url' to request the next several pages of data.
 */
var dcStartCache = function(cacheName, url, callback, pageSize, spinnerDiv) {
	pdcLog("Initializing cache " + cacheName);
	pdcClearCache(cacheName);
	dcSetCallback(cacheName, callback);
	dcSetUrl(cacheName, url);
	dcSetPageSize(cacheName, pageSize);
	pdcSpinnerDiv[cacheName] = spinnerDiv;
}

/* Return the requested page of data from the cache, and request more from the server if needed to
 * be ready for future requested pages.  Page numbers begin at 1, rather than 0, for user convenience.
 */
var dcGetPage = function(cacheName, pageNumber) {
	pdcLog("Fetching page " + pageNumber + " from cache " + cacheName);
	var startIndex = (pageNumber - 1) * pdcPageSize[cacheName];
	var endIndex = startIndex + pdcPageSize[cacheName];
	var lastRetrieved = 0;
	var targetIndex = endIndex + (pdcPageSize[cacheName] * pdcLookAhead);
	if (cacheName in pdcCache) {
		lastRetrieved = pdcCache[cacheName].length; 
	}
	if (pdcTotalCount[cacheName] >= 0) {
		targetIndex = Math.min(targetIndex, pdcTotalCount[cacheName]);
	}

	if (cacheName in pdcSpinnerDiv) {
		if (pdcSpinnerDiv[cacheName] != null) {
			qsShowSpinner(pdcSpinnerDiv[cacheName]);
		}
	}

	// Do we already have enough data in-hand?
	if (lastRetrieved >= endIndex) {
		// We have enough for this request, but do we need to fetch more for future requests?
		if (lastRetrieved < targetIndex) {
			pdcFetchData(cacheName, lastRetrieved, targetIndex);
		}

		// Return results from what we've already cached.
		var toReturn = {}
		toReturn.rows = pdcCache[cacheName].slice(startIndex, endIndex);
		toReturn.totalCount = pdcTotalCount[cacheName];
		toReturn.start = startIndex + 1;
		toReturn.end = endIndex;
		pdcCallback[cacheName](toReturn);
		
	} else {
		// If we don't already have enough results in-hand, we need to get more and then return some.
		pdcFetchData(cacheName, lastRetrieved, targetIndex, startIndex, endIndex);
	}
}

/* Change the page size for an existing cache.  Resets to page 1 for next data returned, but keeps
 * any cached data because the URL hasn't changed.  Defaults to 100 if pageSize not specified.
 */
var dcSetPageSize = function(cacheName, pageSize) {
	pdcLog("Changing page size for cache " + cacheName + " to " + pageSize);

	if (pageSize === null) { pageSize = 100; }
	pdcPageSize[cacheName] = pageSize;
}

/* Change the callback function for the given cache name.  (This function will be called and passed a
 * list of data, once they are available.)
 */
var dcSetCallback = function(cacheName, callback) {
	pdcLog("Changed callback for cache " + cacheName);
	pdcCallback[cacheName] = callback;
}

/* Change the URL for an existing cache.  Resets to page 1 for next data returned, clears the
 * cache (because different data are now requested), and loads the next batch of data from the server.
 */
var dcSetUrl = function(cacheName, url) {
	pdcLog("Changing URL for cache " + cacheName + " to " + url);
	pdcUrl[cacheName] = url;
	pdcClearCache(cacheName);
}

/*** private functions ***/

/* Log entry to console, if logging is turned on
 */
var pdcLog = function(entry) {
	if (pdcLogging) {
		console.log(entry);
	}
}

/* Request results from 'start' to 'end' from the server, bringing them into the given cache, so
 * they will be ready in case the user wants them.  And if we've received passBackStart and
 * passBackEnd, then the user is actively waiting for some of these data, so pass them to the
 * callback once we have them.
 */
var pdcFetchData = function(cacheName, start, end, passBackStart, passBackEnd) {
	pdcLog("Fetching data from " + start + " to " + end + " for cache " + cacheName);
	var results = end - start;		// number of results to fetch
	var url = pdcUrl[cacheName] + "&startIndex=" + start + "&results=" + results;

	$.get(url, function(data) {
		try {
			pdcCache[cacheName] = pdcCache[cacheName].concat(data.summaryRows);
			pdcTotalCount[cacheName] = data.totalCount;

			// If the user is waiting for some of this data, pass them back for display.
			if ((passBackStart != null) && (passBackEnd != null)) {
				pdcLog('passBackStart: ' + passBackStart);
				pdcLog('passBackEnd: ' + passBackEnd);
				pdcLog('pdcCache["' + cacheName + '"].length = ' + pdcCache[cacheName].length);
				var cbFunction = pdcCallback[cacheName];
				pdcLog('Got callback function');
				var rows = pdcCache[cacheName].slice(passBackStart, Math.min(passBackEnd, pdcCache[cacheName].length));
				pdcLog('Got slice of rows: ' + rows.length);
				var toReturn = {}
				toReturn.rows = rows;
				toReturn.totalCount = pdcTotalCount[cacheName];
				toReturn.start = passBackStart + 1;
				toReturn.end = Math.min(passBackEnd, passBackStart + rows.length);
				cbFunction(toReturn);
				pdcLog('Called callback function');
			}
		} catch (e) {
			console.log("Failed to process data for cache " + cacheName + ": " + e);
			failed = true;
		}
	}).fail(function() {
			console.log("Failed to retrieve data for cache " + cacheName);
			failed = true;
	});
}

/* Clear any cached data for the given cacheName.
 */
var pdcClearCache = function(cacheName) {
	pdcLog("Clearing cache " + cacheName);
	pdcCache[cacheName] = [];
	pdcTotalCount[cacheName] = -1;
}