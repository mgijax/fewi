/* Name: qs_main.js
 * Purpose: main logic for quicksearch JSP page and data retrieval
 * Notes: Functions here will be prefixed by "qs".
 */

var qsWaitFor = {};		// numbers of tabs for which we're waiting for results
var qsLastFilters = {};	// map of the filters most recently applied, as:  { filter : [ selected values ] }

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
	// and the hidden/shown status of the DIV with the remove filter buttons.  Also update the links for any
	// download buttons on the affected filter.
	switch (qsTabWithChangedFilters()) {
		case 'F' :
			pgClearPaginator('featurePaginator');
			b1Fetch();
			qsHideShowRemoveFilterButtons('feature');
			$('#fTextDownload')[0].href = fewiurl + 'quicksearch/features/report.txt?' + getQuerystring();
			$('#fExcelDownload')[0].href = fewiurl + 'quicksearch/features/report.xlsx?' + getQuerystring();
			break;
		case 'A' :
			pgClearPaginator('allelePaginator');
			b5Fetch();
			qsHideShowRemoveFilterButtons('allele');
			break;
		case 'V' :
			pgClearPaginator('vocabPaginator');
			b2Fetch();
			qsHideShowRemoveFilterButtons('vocabTerm');
			break;
		case 'S' :
			pgClearPaginator('strainPaginator');
			b4Fetch();
			qsHideShowRemoveFilterButtons('strain');
			break;
		case 'O' :
			pgClearPaginator('otherIdPaginator');
			b3Fetch();
			qsHideShowRemoveFilterButtons('otherId');
			break;
		default:
			return;
	}
	// Remember this set of filters as the "last" one for next time.
	qsLastFilters = qsGetSelectedFilters();
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
	} else {
		$('#ui-id-' + tabNumber).removeClass('hasResults');
		$('#ui-id-' + tabNumber).addClass('noResults');
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

// Get a mapping comparable to the global qsLastFilters but with the currently selected filter values.
var qsGetSelectedFilters = function() {
	// String begins with an ampersand, so trim that off first.  Then convert from URL to map.
	return filters.urlToHash(filters.getUrlFragment().substring(1));
}

// Return string of comma-separated values selected for the specified filterName, or an empty string if none selected.
var qsFilterValues = function(filterMap, filterName) {
	if (filterName in filterMap) {
		var filterValue = filterMap[filterName];
		
		// If the value is just natively a string, return it as-is.  Otherwise, assume it's a list and convert it to
		// a comma-delimited string of values.
		if (typeof(filterValue) == typeof('a')) {
			return filterValue;
		} else {
			return filterValue.join(',');
		}
	}
	return '';
}

// Return true if the filter with the given filterName has changed from qsLastFilters, false if the same.
var qsFilterChanged = function(filterMap, filterName) {
	var oldVal = qsFilterValues(qsLastFilters, filterName);
	var newVal = qsFilterValues(filterMap, filterName);
	return (oldVal != newVal);
}

// Return a one-letter code to indicate which tab had a changed filter (F, A, V, O, or S), or null if none changed.
// (returns the first one found, but given that this works with a filter-change event, there should only be one at
// a time)
var qsTabWithChangedFilters = function() {
	var myFilters = qsGetSelectedFilters();
	
	var bothFilterSets = [ myFilters, qsLastFilters ];
	var union = {};
	for (var i in bothFilterSets) {
		for (var filterName in bothFilterSets[i]) {
			union[filterName] = true;
		}
	}
	
	for (var filterName in union) {
		if (qsFilterChanged(myFilters, filterName)) {
			// assumption:  Filter fieldnames have a one-letter tag at the end that indicate which bucket contains
			// that filter button.  If that ending character is one of the expected letters, return it.
			var bucket = filterName.substring(filterName.length - 1);
			if (['F', 'A', 'V', 'O', 'S'].indexOf(bucket) >= 0) {
				return bucket;
			}
		}
	}
	return null;
}

// For the given bucket abbreviation (feature, allele, vocabTerm, strain, or otherId), hide or show the "remove
// filter" buttons as needed.  (depends on whether any filters for that bucket are currently selected)  Also
// will alter tab text and its color as needed.
var qsHideShowRemoveFilterButtons = function(bucket) {
	var myFilters = qsGetSelectedFilters();
	var firstLetter = bucket.substring(0,1).toUpperCase();
	var bucketLink = firstLetter.toLowerCase() + 'Link';

	var foundAny = false;
	var filterCount = 0;
	var hasAnyFilters = false;

	for (var filterName in myFilters) {
		if (filterName != '') {
			hasAnyFilters = true;
		}
		var thisBucket = filterName.substring(filterName.length - 1);
		if (thisBucket == firstLetter) {
			foundAny = true;
			break;
		}
	}

	// need to increase height of all tabs if there is at least one filtered tab
	if (hasAnyFilters) {
		$('[role=tab]').css({'height':'40px'});
	} else {
		$('[role=tab]').css({'height':'inherit'});
	}

	if (foundAny) {
		$('#breadbox' + firstLetter).removeClass('hidden');
		if (!($('#' + bucketLink).hasClass('filtered'))) {
			$('#' + firstLetter.toLowerCase() + 'Text').html('<br/>filtered results');
			$('#' + bucketLink).addClass('filtered');
			// Since jQuery takes over control of tab coloring, we can't rely on the 'filtered' class
			// doing the job for the color.  Need to actually do that through a specific style.
			$('#' + bucketLink).css({ 'color' : '#97454C' });
		}

	} else {
		$('#breadbox' + firstLetter).addClass('hidden');
		if ($('#' + bucketLink).hasClass('filtered')) {
			$('#' + firstLetter.toLowerCase() + 'Text').html('');
			$('#' + bucketLink).removeClass('filtered');
			// Since jQuery takes over control of tab coloring, we can't rely on the 'filtered' class
			// doing the job for the color.  Need to actually do that through a specific style.
			$('#' + bucketLink).css({ 'color' : 'inherit' });
		}
	}
}

/* switch from a series of asterisks representing filled-in stars to the full string for display
 */
var qsFormatStars = function(s) {
	var count = 0;
	var t = '';
	
	// Convert any existing asterisks to be filled-in stars.
	if (s != null) {
		count = s.length;
		t = s.replace(/[*]/g, "&#9733;");
	}

	// Pad the string up to length 4 with hollow stars.
	for (var i = 4; i > count; i--) {
		t = t + '&#9734;';
	}
	return t;
}

// returns a string with HTML for a help icon and popup for the Score column
var qsScoreHelp = function() {
	var s = '<span class="helpCursor" '
		+ 'onmouseover="return overlib(\'<div class=detailRowType>'
		+ qsFormatStars('****') + ' exact matches<br/>'
		+ qsFormatStars('***') + ' all words in the search string appear (even if out of order)<br/>'
		+ qsFormatStars('**') + ' at least one word from the search string appears</div>\', '
		+ 'STICKY, CAPTION, \'Score\', HAUTO, BELOW, WIDTH, 375, DELAY, 600, CLOSECLICK, CLOSETEXT, \'Close X\')" '
		+ 'onmouseout="nd();"> <img src="/webshare/images/blue_info_icon.gif" style="height:11px" border="0"> </span>';
	return s;
}