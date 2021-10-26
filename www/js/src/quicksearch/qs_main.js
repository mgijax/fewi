/* Name: qs_main.js
 * Purpose: main logic for quicksearch JSP page and data retrieval
 * Notes: Functions here will be prefixed by "qs".
 */

var qsWaitFor = {};		// numbers of tabs for which we're waiting for results

// If the user clicks through to one of the linked objects, remember what tab was displayed (so we can come
// back to it).
$(window).on('unload beforeunload', function() {
	sessionStorage.setItem(sessionID, parseInt($('.ui-tabs-active a').attr('id').replace('ui-id-', '')));
});

// main logic for quick search
var qsMain = function() {
	// Only go ahead with queries if an even number of double-quotes in the search string.
	var ct = 0;
	var pos = query.indexOf('"');
	qsWaitFor = {};
	
	while (pos >= 0) {
		ct++;
		pos = query.indexOf('"', pos + 1);
	}
	
	if (ct % 2 == 0) {
		b1Fetch();		// bucket 1 : markers bucket
		b2Fetch();		// bucket 2 : vocab terms + strains bucket
		b3Fetch();		// bucket 3 : ID bucket
		b4Fetch();		// bucket 4 : strains bucket
		b5Fetch();		// bucket 5 : alleles bucket
		for (var i = 1; i < 5; i++) {
			qsWaitFor[i] = true;
		}
	} else {
		$('#errorDiv').html('Error: Your search includes an odd number of quotation marks.  ' +
			'Please edit your search to use quotation marks only in pairs.');
		$('#errorDiv').removeClass('hidden');
	}
};

// find a string beginning with the given string 'c' that doesn't appear in string 's'
var findTag = function(c, s) {
  	if ((s === null) || (s.indexOf(c) < 0)) { return c; }
  	return findTag(c + c[0], s);
};
  
// convert MGI superscript notation <...> to HTML superscript tags
var qsSuperscript = function(s) {
	if (s === null) { return s; }
    var openTag = findTag('{', s);
  	return s.split('<').join(openTag).split('>').join('</sup>').split(openTag).join('<sup>');
};
  
// return a string listing counts for search results
var qsResultHeader = function(start, end, total) {
	if (total == 0) {
		return "no results";
	} else if (start == end) {
		return "sorted by best match, showing " + commaDelimit(start) + " of " + commaDelimit(total);
	}
	return "sorted by best match, showing " + commaDelimit(start) + "-" + commaDelimit(end) + " of " + commaDelimit(total);
};

// update the request & data due to a change of state in one of the filters (is a callback for the filters.js library)
var qsProcessFilters = function() {
	filters.populateFilterSummary();		// fills all the filter summary DIVs with remove filter buttons
	
	// Now need to identify which tab(s) have changed filters, based on comparing parameter values to the last time
	// this method was called.  For each, we will need to update the paginator, data, formatting of the tab itself,
	// and the hidden/shown status of the DIV with the remove filter buttons.

	pgClearPaginator('featurePaginator');
	pgClearPaginator('vocabPaginator');
	pgClearPaginator('otherIdPaginator');
	pgClearPaginator('strainPaginator');
	pgClearPaginator('allelePaginator');
	b1Fetch();
	b2Fetch();
	b3Fetch();
	b4Fetch();
	b5Fetch();

	// If no active filters, hide the remove filter buttons.
	if (queryString == getQuerystring()) {
		$('#breadboxGF').addClass('hidden');
		$('#breadboxA').addClass('hidden');
		$('#breadboxV').addClass('hidden');
		$('#breadboxO').addClass('hidden');
		$('#breadboxS').addClass('hidden');
	} else {
		$('#breadboxGF').removeClass('hidden');
		$('#breadboxA').removeClass('hidden');
		$('#breadboxV').removeClass('hidden');
		$('#breadboxO').removeClass('hidden');
		$('#breadboxS').removeClass('hidden');
	}
}

// callback function: should be wired into the filters.js module as a callback.  This will track which filters are
// applied and log a GA event when a new one has been added.
var previousFilters = {};
var qsLogFilters = function() {
	var newFilters = filters.urlToHash(filters.getUrlFragment().substring(1));
	
	for (var field in newFilters) {
		if (!(field in previousFilters)) {
			ga_logEvent("QuickSearch: added filter", field);
		}
	}
	previousFilters = newFilters; 
}

// show the "waiting" spinner in the given 'divName' (including the pound sign)
var qsShowSpinner = function(divName) {
	$(divName).html("<img src='/fewi/mgi/assets/images/loading.gif' height='21' width='21'> Waiting for results...");
}

// used to set tab text color and font weight based on number of results
var qsStyleTabText = function(resultCount, tabNumber) {
	if (resultCount > 0) {
		$('#ui-id-' + tabNumber).removeClass('noResults');
		$('#ui-id-' + tabNumber).addClass('hasResults');
		$('#ui-id-' + tabNumber).css({ 'color' : '#002255', 'font-weight' : 'bold'});	// standard button text color
	} else {
		$('#ui-id-' + tabNumber).removeClass('hasResults');
		$('#ui-id-' + tabNumber).addClass('noResults');
		$('#ui-id-' + tabNumber).css({ 'color' : 'black', 'font-weight' : 'normal'});	// lighter button text
	}
	if (tabNumber in qsWaitFor) {
		delete qsWaitFor[tabNumber];
	}
	
	// If all the tabs have come back with data, choose which one to display.
	// 1. If we remember a previously selected tab (because the user clicked to see a linked object and then
	//	came Back to this page, show that tab.
	// 2. Or, if there is no remembered tab, then just show the first one that has results.
	if (Object.keys(qsWaitFor).length == 0) {
		var tabID = sessionStorage.getItem(sessionID);
		if (tabID == null) {
			$('.hasResults').first().click();
		} else {
			$('#ui-id-' + tabID).first().click();
		}
	}
}
