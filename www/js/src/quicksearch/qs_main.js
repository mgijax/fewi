/* Name: qs_main.js
 * Purpose: main logic for quicksearch JSP page and data retrieval
 * Notes: Functions here will be prefixed by "qs".
 */

// main logic for quick search
var qsMain = function() {
	b1Fetch();		// bucket 1 : markers + alleles bucket
	b2Fetch();		// bucket 2 : vocab terms + strains bucket
	b3Fetch();		// bucket 3 : ID bucket
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
var qsResultHeader = function(start, end, total, dataType) {
	if (total == 0) {
		return "No matching " + dataType + "s";
	}
	var plural = "";
	if ((end - start) > 1) {
		plural = "s";
	}
	return "Showing " + commaDelimit(start) + "-" + commaDelimit(end) + " of " + commaDelimit(total) + " " + dataType + plural;
};

// update the request & data in the feature bucket (after a filtering event)
var qsProcessFilters = function() {
	filters.populateFilterSummary();
	instantiatedPaginator = false;
	b1Fetch();
	b2Fetch();
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