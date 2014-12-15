/* Name: disease_portal_filters.js
 * Purpose: supports additional filters for the HMDC, beyond the initial set
 * 	of row & column filters.  This module initially will support a Feature
 * 	Type filter.  It will be integrated with the row/column filters in
 * 	disease_portal_summary.js.
 * Author: jsb
 * Notes: Under the hood, this library makes use of pieces of the standard
 * 	filters.js library.
 */

/* establish a hmdcFilters namespace; public functions will be defined within
 * this namespace to prevent name clashes with other modules.
 */
window.hmdcFilters = {};

/* by default, enable the callback functions.  We need to do this in addition
 * to the standard filters.callbacksActive, as this shim is shoe-horned over
 * the standard library and integration is ugly.
 */
hmdcFilters.callbacksActive = true;

/************************
 *** public functions ***
 ************************/

/* turn the callback function(s) on
 */
hmdcFilters.callbacksOn = function() {
    hmdcFilters.callbacksActive = true;
};

/* turn the callback function(s) off
 */
hmdcFilters.callbacksOff = function() {
    hmdcFilters.callbacksActive = false;
};

/* notify this module of the function that can be called to retrieve the
 * current query string (URL parameters)
 */
hmdcFilters.setQueryStringFunction = function(fn) {
    filters.setQueryStringFunction(fn);
};

/* notify this module of the base URL for the fewi
 */
hmdcFilters.setFewiUrl = function(fewiUrl) {
    filters.setFewiUrl(fewiUrl);
};

/* create filters for HMDC (beyond the traditional row/column filters)
 */
hmdcFilters.createFilters = function() {
    filters.addFilter ('featureType', 'Genome Feature Type',
	'featureTypeButton', 'featureTypeFilter', 
	filters.fewiUrl + 'diseasePortal/facet/featureType', null, null,
	'Filter by Mouse Genome Feature Type');
};

/* get a list of strings, each of which is a filter name
 */
hmdcFilters.getFilterNames = function() {
    return filters.getFilterNames();
};

/* clear the selections for all filters
 */
hmdcFilters.clearAllFilters = function() {
    hmdcFilters.clearAllValuesForFilter('featureType');
};

/* clear all selections for a single filter by name
 */
hmdcFilters.clearAllValuesForFilter = function(filterName) {
    filters.clearAllValuesForFilter(filterName);

    // need to clear the values of any associated hidden fields

    var fieldsToClear = [];

    if (filterName == 'featureType') {
	fieldsToClear.push('featureTypeFilter');
    }

    for (var i = 0; i < fieldsToClear.length; i++) {
	var el = document.getElementById(fieldsToClear[i]);
	if (el) {
	    try {
		el.value = '';
	    } catch (e) {
		filters.log('Could not clear hidden field: '
		    + fieldsToClear[i]);
	    }
	}
    }
};

/* register a function to be called whenever the value for a filter changes
 */
hmdcFilters.registerCallback = function(callbackName, callbackFn) {
    filters.registerCallback(callbackName, callbackFn);
};

/* remove a callback function
 */
hmdcFilters.removeCallback = function(callbackName) {
    filters.removeCallback(callbackName);
};

/* get the filters' fieldnames and their values, formatted to be suitable for
 * being appended to a URL string.  Note that it includes a leading ampersand
 * (&), if there are any filter values set.  (If there are no filter values,
 * this function returns an empty string.)
 */
hmdcFilters.getUrlFragment = function() {
    return filters.getUrlFragment();
};

/* get the filters' fieldnames and values as a hash, mapping from key to a
 * list of values for that key.  This is the format expected by the
 * generateRequest() method in fewi_utils.js.
 */
hmdcFilters.getFacets = function() {
    var facets = {};
    var fragment = filters.getUrlFragment();

    if (!fragment) { return facets; }

    var options = fragment.split('&');

    for (var k = 0; k < options.length; k++) {
	var items = options[k].split('=');
    	var fieldname = '';
    	var fieldvalue = '';

	if (items.length == 1) {
	    fieldname = items[0];
	} else if (items.length == 2) {
	    fieldname = items[0];
	    fieldvalue = items[1];
	} else {
	    filters.log('Skipped odd parameter: ' + options[k]);
	}

	if (fieldname) {
	    if (facets.hasOwnProperty(fieldname)) {
		if (filters.listIndexOf(facets[fieldname], fieldvalue) == -1) {
		    facets[fieldname].push(fieldvalue);
		}
	    } else {
		facets[fieldname] = [ fieldvalue ]
	    }
	}
    }
    return facets;
};

/* get all the buttons for the filter summary div (the buttons used to remove
 * the individual selected values for each filter).  returns an empty string
 * if there are no filter values currently selected.
 */
hmdcFilters.getAllSummaryButtons = function() {
    return filters.getAllSummaryButtons();
};

/* function to be called when filter values are returned via JSON.  Do not
 * call directly.
 */
hmdcFilters.filterValuesReturned = function(sRequest, oResponse, oPayload) {
    filters.log("sRequest: " + sRequest);
    filters.log("oResponse: " + oResponse);
    filters.log("oPayload: " + oPayload);
};

/* function to pull the current querystring out of the window namespace
 */
hmdcFilters.getQueryString = function() {
    var qs = window.querystring;
    try {
	qs = getQueryString();
	if (qs == '') {
	    qs = window.querystring;
	}
    } catch (c) {}
    return qs;
};

/* update the hidden fields on the page to reflect the values currently
 * stored for the filters
 */
hmdcFilters.updateHiddenFields = function() {
    var values = {};	// all values that need to be set

    for (var i = 0; i < filters.filterNames.length; i++) {
	var name = filters.filterNames[i];
	var fbn = filters.filtersByName[name];

	// assumes fields are empty

	for (var j = 0; j < fbn['fields'].length; j++){
	    values[fbn['fields'][j]] = '';
	}

	// note values for non-empty filter fields

	for (var fieldname in fbn['values']) {
	    values[fieldname] = fbn['values'][fieldname].join(',');
	}
    }
	 
    // now go through and update hidden fields where we can find them

    for (var fieldname in values) {
	var el = document.getElementById(fieldname);
	if (el) {
	    try {
		el.value = values[fieldname];
	    } catch (e) {
		filters.log('Cannot set value for field: ' + fieldname);
	    }
	}
    }
    window.querystring = hmdcFilters.getQueryString();
};

/* update the 'remove filter' buttons visible on the page
 */
hmdcFilters.manageButtons = function() {
    filters.log('In manageButtons()');
};

/* update the page once a filter has been selected
 */
hmdcFilters.updatePage = function() {
    if (!hmdcFilters.callbacksActive) { return; }

    hmdcFilters.updateHiddenFields();

    var pageSize = 250;
    if (CURRENT_PAGE_SIZE) {
	pageSize = CURRENT_PAGE_SIZE;
    }

    var request = hmdcFilters.getQueryString();
    var facets = hmdcFilters.getFacets();
    if (typeof(getCurrentTab) === 'function') {
	var tab = getCurrentTab();
	if (tab) {
	    request = filters.consolidateParameters(request
		+ "&startIndex=0&dir=asc&sort=&results=" + pageSize
		+ "&tab=" + tab);
	}
    }

    for (var key in facets) {
	try {
    	    document.getElementById(key).value = facets[key].join(',');
	} catch (e) {
	    filters.log('Missing hidden field for: ' + key);
	}
    } 
    handleNavigation(request, true);
    refreshTabCounts();
};

/* do prep work needed to initialize the filters
 */
hmdcFilters.prepFilters = function(fewiUrl) {
    prepFilters();			// from filters.js library

    filters.setFewiUrl(fewiUrl);
    hmdcFilters.setQueryStringFunction(hmdcFilters.getQueryString);
    filters.setSummaryNames('filterSummary', 'filterList');
    filters.setAlternateCallback(hmdcFilters.filterValuesReturned);
    hmdcFilters.createFilters();
    hmdcFilters.registerCallback("updatePage", hmdcFilters.updatePage);
};

/* initialize the filters from a request object
 */
hmdcFilters.setAllFilters = function(pRequest) {
    filters.setAllFilters(pRequest);
    hmdcFilters.updateHiddenFields();
};

