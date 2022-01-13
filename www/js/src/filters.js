/* Name: filters.js
 * Purpose: to provide a basic library supporting the addition of various
 *	types of filters to MGI summary pages
 * Assumptions:
 *	1. The filters are designed for pages with YUI DataTables, backed by
 *		data retrieved as JSON from the fewi.
 * Goals:
 * 	1. should support, or at least not preclude, multiple filter UI
 * 		paradigms: check boxes, type-in fields, sliders, filters with
 * 		multiple fields (e.g., data_source and score)
 * 	2. should support application of zero or more filters (not just 1)
 * 	3. should be easy to integrate with browser history manager
 * 	4. should not be specific to a certain QF
 * Usage:
 * 	TBD
 */

/*************************/
/*** library namespace ***/
/*************************/

/* establish a filters namespace; public functions will be defined within this
 * namespace to prevent name clashes with other modules.  For example, a
 * function getX would be called as filters.getX()
 */
window.filters = {};

/************************/
/*** global variables ***/
/************************/

filters.filterNames = [];	// list of filter names
filters.filtersByName = {};	// maps from filter name to dict of filter info
filters.callbackNames = [];	// list of functions to be called when the
				// ...values for a filter change
filters.callbacksByName = {};	// maps from callback name to function to call
filters.callbacksActive = true;	// are the callback functions active?

filters.logging = false;	// write log messages to browser error log?
filters.loggingConsole = false; // write log message to browser console?

filters.queryStringFunction = null;	// funtion to call to get parameter
					// ...string for general query form
					// ...parameters

filters.dialogBox = null;	// the actual dialog box the user sees

filters.fieldnameToFilterName = {}	// maps from a field name to the name
					// ...of the filter having the field

filters.generatePageRequest = null; // External Method used to generate the url
									// for the request to update the datatable

filters.historyModule = null;	// set this to be the name of your module for
				// ...history management purposes

filters.navigateFn = null;	// function to be called by the history
				// ...manager to do data updates

filters.dataTable = null;	// the YUI DataTable managed by the filters

filters.filterSummary = null;	// name of div containing the whole filter
				// ...summary

filters.filterList = null;	// name of the span containing the filter
				// ...buttons

filters.buttonInfo = null;	// mapping of where to put "remove filter" buttons

filters.callbacksInProgress = false;	// are we currently handling callbacks?

filters.fewiUrl = null;		// base URL to fewi, used to pick up images

filters.alternateCallback = null;	// if we are not managing a dataTable,
					// ...what should we call when a
					// ...filter's values are returned?

// style for DIV containing filter removal buttons
filters.removalDivStyle = 'inline';

/* special handling for extra 'remove row/col filters' button for HMDC */

filters.hmdcButtonID = null;		// ID of extra button
filters.hmdcButtonText = null;		// text to show on extra button
filters.hmdcButtonTooltip = null;	// tooltip to show for extra button
filters.hmdcButtonVisible = false;	// show the extra button?
filters.hmdcButtonCallback = null;	// function to call to remove filters

/************************/
/*** public functions ***/
/************************/

/* register a button specifically for the HMDC 'remove row/column filters'
 * functionality
 */
filters.registerHmdcButton = function (id, text, tooltip, visible, callback) {
    filters.hmdcButtonID = id;
    filters.hmdcButtonText = text;
    filters.hmdcButtonTooltip = tooltip;
    filters.hmdcButtonVisible = visible;
    filters.hmdcButtonCallback = callback;
};

/* set the HMDC button (if one was registered) to be 'visible' (assumed to
 * be either boolean true or false)
 */
filters.setHmdcButtonVisible = function (visible) {
    if (filters.hmdcButtonID) {
	    filters.hmdcButtonVisible = visible;
    } else {
	    filters.hmdcButtonVisible = false;
    }
};

/* notify this module of the function to call to retrieve the parameter string
 * for the general query form parameters
 */
filters.setQueryStringFunction = function(fn) {
    filters.queryStringFunction = fn;
};

/* notify this module of the name of the module to use for history management
 * purposes and the name of the function for the history manager to call to
 * update the data table.
 */
filters.setHistoryManagement = function(module, navigateFunction) {
    filters.navigateFn = navigateFunction;

    /* disabled (not using browser history for now)
     *
    filters.historyModule = module;
    YAHOO.util.History.register(module,
	YAHOO.util.History.getBookmarkedState(module) || "",
	filters.navigateFn);
    filters.registerCallback("navigateFn", filters.navigateFn);
     *
     */
};

/* set URL to fewi
 */
filters.setFewiUrl = function(fewiUrl) {
    filters.fewiUrl = fewiUrl;
};

/* set the function to build the page request */

filters.setGeneratePageRequestFunction = function(fn) {
	filters.generatePageRequest = fn;
};

/* notify this module of the name of the YUI DataTable managed by the filters
 */
filters.setDataTable = function(dataTable) {
    filters.dataTable = dataTable;
};

/* if we are not managing a dataTable, then what function should we call when
 * a filter's set of values is returned?
 * The alternateFn will be called with three parameters:
 *    1. sRequest <String> - original request
 *    2. oResponse <Object> YUI Response object
 *    3. oPayload <MIXED, optional> additional argument(s)
 */
filters.setAlternateCallback = function(alternateFn) {
    filters.alternateCallback = alternateFn;
};

/* Notify this module of the names for the filter summary div and the span
 * within it that will contain the filter removal buttons.  There is a newer
 * alternative setButtonInfo() that allows for a page to have multiple,
 * independently-filtered tabs (like the quick search).
 */
filters.setSummaryNames = function(filterSummary, filterList) {
	if (filters.buttonInfo !== null) {
		filters.log('Use either setSummaryNames() or setButtonInfo() but not both');
	}
    filters.filterSummary = filterSummary;
    filters.filterList = filterList;
    filters.convertToNewMethod();
};

/* This is really a special case of the new setButtonInfo() functionality, as all buttons
 * just work with a single DIV and SPAN.  So, set up the proper mapping, initialize via
 * setButtonInfo() and go from there.  But we don't want to do this setup until we're
 * sure that all the buttons have been initialized, which is typically done after the
 * call set setSummaryNames(), so we do it when needed.
 */
filters.convertToNewMethod = function() {
	var myDict = {};
	for (var i in filters.filterNames) {
		var filterName = filters.filterNames[i];
		myDict[filterName] = [ filters.filterSummary, filters.filterList ];
	}
	// Now remove the old-style values, and set up the new.
	filters.filterSummary = null;
	filters.filterList = null;
	filters.setButtonInfo(myDict);
}

/* Notify this module of a mapping between HTML fieldnames and a two-item list
 * identifying the name of the DIV and the name of the SPAN it contains, for
 * where that field's "remove filter" buttons should be placed.  Example:
 * { 'processFilter' : [ 'geneFilterDiv', 'geneFilterSpan' ],
 *   'phenotypeFilter' : [ 'geneFilterDiv', 'geneFilterSpan' ]
 * }
 * This function is a newer alternative to setSummaryNames(), for pages with
 * multiple tabs that allow independent filtering (like the quick search).
 */
filters.setButtonInfo = function(filterButtonInfo) {
	if ((filters.filterSummary !== null) || (filters.filterList !== null)) {
		filters.log('Use either setSummaryNames() or setButtonInfo() but not both');
	}
	filters.buttonInfo = filterButtonInfo;
};

/* builds and returns list of DOM elements, one for each buttons to remove
 * values from this filter.  If this filter has no values selected, then this
 * method returns an empty list.  This is the default formatter, for the
 * traditional filters which allow a selection list of values and we want a
 * 'remove' button for each of the values.  Other methods may be used in place
 * of this one, as long as they follow the same pattern of inputs and outputs.
 * 'mydata' is a hash of data about the filter, as from filters.filtersByName.
 */
filters.defaultFilterFormatter = function(mydata) {
    var list = [];	// list of DOM objects to return, one per button

    // this formatter is only appropriate for single-field filters; bail out
    // otherwise
    if (mydata.fields.length > 1) {
	    filters.log('too many fieldnames for defaultFilterFormatter()');
	    return list;
    }

    var fieldname = mydata.fields[0];	// name of the field for this filter

    // don't need any removal buttons if there are no values specified
    
    if (fieldname in mydata.values) {
	    filters.log(fieldname + ' has values in ' + mydata.name);
    } else {
	    filters.log(fieldname + ' has no values in ' + mydata.name);
	    return list;
    }

    var valueCount = mydata.values[fieldname].length;

    if (valueCount <= 0) {
	    return list;
    }

    // build the buttons
    var myValues = mydata.values[fieldname];
    if (typeof(myValues) === 'string') {
	    myValues = [ myValues ];
    }

    for (var pos in myValues) {

	    var value = myValues[pos];
	    var id = mydata.name + ':' + fieldname + ':' + value;
	    var text = mydata.nameForUser + ': ' + filters.decode(value);

	    var el = document.createElement('a');
	    el.setAttribute('class', 'filterItem'); 
	    el.setAttribute('id', id);
	    el.setAttribute('style', 'line-height: 2.2');
	    el.setAttribute('title', 'click to remove this filter');

	    // if we have a left parenthesis in the text, assume that it was
	    // converted from a comma and change it back for the displayed text.
	    if (find('(', text)) {
	        setText(el, text.replace(/\(/g, ','));
	    } else {
	        setText(el, text);
	    }
	    list.push(el);
    } 
    filters.log('returning ' + list.length + ' buttons for ' + mydata.name);
    return list;
};

/* builds and returns list of DOM elements, one for each buttons to remove
 * values from this filter.  If this filter has no values selected, then this
 * method returns an empty list.  This is the formatter for filters which use
 * a single-value slider,  where a 'remove' button will remove this filter
 * entirely.  'mydata' is a hash of data about the filter, as from
 * filters.filtersByName.
 */
filters.sliderFormatter = function(mydata) {
    var list = [];	// list of DOM objects to return, one per button

    // this formatter is only appropriate for single-field filters; bail out
    // otherwise
    if (mydata.fields.length > 1) {
	    filters.log('too many fieldnames for sliderFormatter()');
	    return list;
    }

    var fieldname = mydata.fields[0];	// name of the field for this filter

    // don't need any removal buttons if there are no values specified
    
    if (fieldname in mydata.values) {
	    filters.log(fieldname + ' has values in ' + mydata.name);
    } else {
	    filters.log(fieldname + ' has no values in ' + mydata.name);
	    return list;
    }

    var valueCount = mydata.values[fieldname].length;

    if (valueCount <= 0) {
	    return list;
    }

    // build the buttons
    var myValue = mydata.values[fieldname];
    if (typeof(myValue) === 'object') {
	    myValue = myValue[0];
    }

    var id = mydata.name + ':' + fieldname + ':' + myValue;
    var text = mydata.nameForUser + ': >= ' + filters.decode(myValue);

    var el = document.createElement('a');
    el.setAttribute('class', 'filterItem'); 
    el.setAttribute('id', id);
    el.setAttribute('style', 'line-height: 2.2');
    el.setAttribute('title', 'click to remove this filter');
    setText(el, text);

    list.push(el);

    filters.log('returning ' + list.length + ' buttons for ' + mydata.name);
    return list;
};

/* create a new filter and register it with this library; note that we replace
 * any existing filter with the same 'filterName'.  Also hooks the "click"
 * event up to the DOM object with the given 'buttonID'.
 */
filters.addFilter = function(
    filterName,	// string; name of the filter in code
    nameForUser,// string; name of the filter as displayed to user
    buttonID,	// string; DOM ID for the button that opens the filter
    fieldnames,	// string or list of strings; names of form fields
		// ...managed by this filter
    url,	// url for where the filter should get its options
    formatter,	// function for formatting name:value pairs for button; if not
    		// ...specified, will use a default one value per button one
    parser,	// function for parsing results that come back with filter
    		// ...values
    popupTitle	// string; specify to override the standard title for
    		// ...the popup dialog
    ) {

    // remove an old filter, if one exists

    filters.removeFilter(filterName);

    // convert a single fieldname (a string) to a list

    var fields = fieldnames;
    if (typeof fields === 'string') {
	    fields = [ fields ];
    }

    // build the dialog box, if it isn't already built
    filters.buildDialogBox();

    // fall back on default formatter and parser

    formatter = formatter ||  filters.defaultFilterFormatter;
    parser = parser || filters.parseResponse;
    popupTitle = popupTitle || ('Filter by ' + nameForUser);

    // how to handle errors when retrieving data from the data source

    var handleError = function (oRequest, oResponse, oPayload) {
	    buttons = filters.dialogBox.getButtons();
	    for (var k in buttons) {
	        buttons[k].set('disabled', 'true');
	    }
	    filters.dialogBox.setHeader(popupTitle);
	    filters.dialogBox.form.innerHTML = oPayload.error;
    };

    // build a callback for when retrieving data from the data source
 
    var buildCallback = function (filterName, popupTitle) {
	    return { success: parser,
	        failure: handleError,
	        scope: this,
	        argument: { name: filterName, title: popupTitle }
	    };
    };

    // create the filter
 
    var filterDS = filters.buildFilterDataSource(filterName, url);

    filters.filterNames.push(filterName);
    filters.filtersByName[filterName] = {
	    'name' : filterName,
	    'nameForUser' : nameForUser,
	    'buttonID' : buttonID,
	    'url' : url,
	    'formatter' : formatter,
	    'fields' : fields,
	    'values' : {},		// fieldname -> [ value 1, ... value n ]
	    'dataSource' : filterDS,
	    'callback' : buildCallback(filterName, nameForUser),
	    'parser' : parser,
	    'title' : popupTitle
    };

    // remember which fields are managed by this filter
    for (var i in fields) {
	    filters.fieldnameToFilterName[fields[i]] = filterName;
    }

    // remove any old handling for the click, and hook it to the new button
    YAHOO.util.Event.removeListener(buttonID, 'click')
    YAHOO.util.Event.addListener(buttonID, 'click', function() {
	    filters.populateDialogForFilter(filterName);
	    }, true);

    filters.log('Added filter: ' + filterName);
};

/* remove the filter with the given 'filterName'
 */
filters.removeFilter = function(
    filterName) {	// string; name of the filter to delete

    // remove any existing filter by the same name
 
    var pos = filters.listIndexOf(filters.filterNames, filterName);
    if (pos >= 0) {
	    var hadValues = filters.filtersByName[filterName].values.length;

	    filters.filterNames.splice(pos, 1);
	    delete filters.filtersByName[filterName];

	    filters.log('Removed filter: ' + filterName);

	    if (hadValues > 0) {
	        filters.issueCallbacks();
	    }
    }

    // remove fieldnames mapping to this filterName

    for (var fieldname in filters.fieldnameToFilterName) {
	    if (filters.fieldnameToFilterName[fieldname] == filterName) {
	        delete filters.fieldnameToFilterName[fieldname];
	    }
    }
};

/* return a list of strings, each of which is the name of a filter
 */
filters.getFilterNames = function() {
    // return a copy, so the original cannot be modified by the caller

    return filters.filterNames.slice();
};

/* clear the selected values for all filters
 */
filters.clearAllFilters = function(suppressCallback) {
    var i = 0;
    var hadValues = 0;	// number of filters with values before clearing

    if (suppressCallback === null) { suppressCallback = false; }

    filters.callbacksOff();

    for (var i = 0; i < filters.filterNames.length; i++) {
	    hadValues = hadValues + filters.clearAllValuesForFilter(
	        filters.filterNames[i]);
    }
    
    filters.callbacksOn();

    if (!suppressCallback && (hadValues > 0)) {
	    filters.issueCallbacks();
    }
};

/* clear the values selected for the filter with the given 'filterName'.
 * returns 1 if the filter had values selected, 0 if not.
 */
filters.clearAllValuesForFilter = function(filterName) {
    if (filterName in filters.filtersByName) {
	    var hadValues = 0;
	    for (var i in filters.filtersByName[filterName].values) {
	        hadValues = hadValues + 1;
	    }

	    filters.filtersByName[filterName]['values'] = {}

	    if (hadValues > 0) {
	        filters.issueCallbacks();
	        return 1;
	    }
    }
    return 0;
};

/* register a new callback function ('callbackFn') and associate it with the
 * given 'callbackName'.  This function will be called when the value of a
 * filter changes.  If the given 'callbackName' is already registered, then
 * the new 'callbackFn' will replace the old one.
 */
filters.registerCallback = function(callbackName, callbackFn) {

    // remove any existing callback by that name
    filters.removeCallback(callbackName);

    // add the new callback
    filters.callbackNames.push(callbackName);
    filters.callbacksByName[callbackName] = callbackFn;

    filters.log('Added callback: ' + callbackName);
};

/* remove the callback function with the given 'callbackName' from the list
 * of callback functions to be called when a filter value changes.
 */
filters.removeCallback = function(callbackName) {
    var pos = filters.listIndexOf(filters.callbackNames, callbackName);
    if (pos >= 0) {
	    filters.callbackNames.splice(pos, 1);
	    delete filters.callbacksByName[callbackName];

	    filters.log('Removed callback: ' + callbackName);
    }
};

/* get the filters' fieldnames and their values, formatted to be suitable for
 * being appended to a URL string.  Note that it includes a leading ampersand
 * (&), if there are any filter values set.  (If there are no filter values,
 * this function returns an empty string.)
 */
filters.getUrlFragment = function() {
    var s = '';		// return string we're compiling
    var i = 0;		// walks through filters

    for (var i = 0; i < filters.filterNames.length; i++) {
	    s = s + filters.getUrlFragmentForFilter(filters.filterNames[i]);
    }

    filters.log('Got URL fragment: ' + s);
    return s;
};

/* get all the buttons for the filter summary div (the buttons which show the
 * currently selected filter values and allow you to click and remove them).
 * returns an empty string if there are no filter values currently selected.
 * Pass in the list of HTML names for the fields we want to consider.
 */
filters.getAllSummaryButtons = function(fieldnames, skipRemoveAllButton) {
    var list = [];	// list of DOM elements to return
    var i = 0;		// walks through filters
    var f;		// formatting function for each filter
    var data;		// hash of data for each filter
    var elements;	// list of DOM elements for a single filter

    filters.log('in getAllSummaryButtons() -- ' + fieldnames.length + ' fields');
    for (var i = 0; i < fieldnames.length; i++) {
	    f = filters.filtersByName[fieldnames[i]]['formatter'];
	    data = filters.filtersByName[fieldnames[i]];

	    var results = f(data);
	    if (results) {
	        list = list.concat(results);
	    }
    }

    // add the extra HMDC button, if needed
    if (filters.hmdcButtonVisible) {
	    var el = document.createElement('a');
	    el.setAttribute('class', 'filterItem'); 
	    el.setAttribute('id', filters.hmdcButtonID);
	    el.setAttribute('style', 'line-height: 2.2');
	    el.setAttribute('title', filters.hmdcButtonTooltip);
	    setText(el, filters.hmdcButtonText);
	    list.push(el); 
    }

    filters.log('point 1');
    if (list.length > 0) {
	    // if there were some filters selected, may need to add a 'clear all' button
    	filters.log('point 2');
    	if (!skipRemoveAllButton) {
    		filters.log('point 3');
    		var el = document.createElement('a');
    		el.setAttribute('class', 'filterItem'); 
    		el.setAttribute('id', 'clearAllFilters');
    		el.setAttribute('style', 'line-height: 2.2');
    		el.setAttribute('title', 'click to remove all filters');
    		setText(el, 'Remove All Filters');
    		list.push(el); 
    		filters.log('point 4');
	    }

   		filters.log('point 5');
	    // And, wire up all the buttons to the clearFilter() function.
	    for (var i = 0; i < list.length; i++) {
	        YAHOO.util.Event.addListener(list[i], 'click', filters.clearFilter);
	    }
   		filters.log('point 6');
    }
    filters.log('exiting getAllSummaryButtons() -- list.length = ' + list.length);
    return list;
};

/* Set the values for all filters to be those specified in the given 'url'.  Returns a 
 * copy of the 'url' without the filtering parameters (so the querystring can be reset).
 */
filters.setAllFiltersFromUrl = function(url) {
	pRequest = filters.urlToHash(url);
	
	filters.setAllFilters(pRequest);
	for (var i in filters.filterNames) {
		var name = filters.filterNames[i];
		if (name in pRequest) {
			delete pRequest[name];
		}
	}
	delete pRequest['hideStrains'];		// also need to remove this, if it exists
    return filters.hashToUrl(pRequest);
};

/* parse the parameters from the given 'url' and produce a hash like:
 * 	{ name : [ values ] }
 */
filters.urlToHash = function(url) {
	var pairs = url.split('&');
	var map = {};
	for (var i in pairs) {
		var pair = pairs[i].split('=');
		var name = pair[0];
		var value = '';

		if (pair.length > 1) {
			value = pair[1].replace(/%20/g, ' ');
		}
			
		if (name in map) {
			if (filters.listIndexOf(map[name], value) < 0) {
				map[name].push(value);
			}
		} else {
			map[name] = [ value ];
		}
	}
	return map;
};

/* take the parameter map and turn it into a URL, which is returned
 */
filters.hashToUrl = function(pRequest) {
	var s = '';
	for (var name in pRequest) {
		var values = pRequest[name];
	    if (typeof(values) === 'string') {
	    	s = s + name + '=' + values + '&';
	    } else {
	    	for (var i in values) {
	    		s = s + name + '=' + values[i] + '&';
	    	}
	    }
	}
	if (s.endsWith('&')) {
		s = s.slice(0, -1);
	}
	return s;
};

/* set the filters from a pRequest object
 */
filters.setAllFilters = function(pRequest) {
    filters.clearAllFilters();

    for (var field in pRequest) {
	    if ((pRequest[field]) && (field in filters.fieldnameToFilterName)) {
	        var filterName = filters.fieldnameToFilterName[field];
	        var fValues = pRequest[field];

	        if ((fValues == []) || (fValues == '')) {
	    	    continue;
	        }
	    
	        // need to handle strings and lists, split comma-separate terms,
	        // and remove redundancy

	        d = {};

	        if (typeof(fValues) === 'string') {
		        fValues = fValues.split(',');
	        }

	        for (var k = 0; k < fValues.length; k++) {
		        var v = fValues[k];
		        if (typeof(v === 'string')) {
		            v = v.split(',')
		        }
		        for (var i = 0; i < v.length; i++) {
		            d[v[i]] = 1;
		        }
	        }

	        fValues = [];
	        for (var fv in d) {
		        fValues.push(fv);
	        }

	        filters.filtersByName[filterName]['values'][field] = fValues;
	    }
    }
    filters.populateFilterSummary();
};

/* do prep work needed to initialize the filters (call from onDOMReady)
 */
var prepFilters = function() {
    filters.log("Entered prepFilters()");
    filters.buildDialogBox();
};

/*************************/
/*** private functions ***/
/*************************/

/* note that these are not actually private, but are intended to be treated
 * as private functions
 */

/* return position of 'myItem' in 'myList', or -1 if not present.  (This is
 * useful because IE8 and prior do not have indexOf() natively.)
 */
filters.listIndexOf = function(myList, myItem) {
    // if browser's engine provides indexOf(), then use it
 
    if (!myList) { return -1; }

    if (typeof Array.prototype.indexOf === 'function') {
	    return myList.indexOf(myItem);
    }

    // otherwise, do it manually
    
    var i = 0;
    for (var i = 0; i < myList.length; i++) {
	    if (myList[i] === myItem) {
	        return i;
	    }
    }
    return -1;
}

/* set this library so that it will not issue callbacks temporarily when
 * filters change.  This is to allow us to set or clear multiple filters at a
 * time, without issuing the callbacks multiple times.
 */
filters.callbacksOff = function() {
    filters.callbacksActive = false;
    filters.log('Turned off callbacks');
}

/* set this library so that it will issue callbacks when filters change.
 */
filters.callbacksOn = function() {
    filters.callbacksActive = true;
    filters.log('Turned on callbacks');
}

/* call each of the various functions that have been registered for callbacks,
 * in the order that they were registered
 */
filters.issueCallbacks = function() {
    if (filters.callbacksActive) {
	    if (filters.callbacksInProgress) {
	        return;
	    }

        filters.log('Issuing callbacks...');
	    filters.callbacksInProgress = true;
	    var i = 0;
	    for (var i = 0; i < filters.callbackNames.length; i++) {
	        filters.log('invoking callback: ' + filters.callbackNames[i]);
	        filters.callbacksByName[filters.callbackNames[i]]();
	        filters.log('returned from callback: ' + filters.callbackNames[i]);
	    }
        filters.log('Issued ' + filters.callbackNames.length + ' callbacks');
	    filters.callbacksInProgress = false;
    }
}

/* get the fieldnames and values for this particular filters, formatted to be
 * suitable for being appended to a URL string.  If this filter has no
 * selected values, it will return an empty string.  Otherwise, it will have a
 * leading ampersand (&).
 */
filters.getUrlFragmentForFilter = function(filterName) {
    var j = 0;		// walks through fieldnames for this filter
    var k = 0;		// walks through values for a given fieldname
    var s = '';		// return string we're compiling
    var fieldname;	// current fieldname being examined
    var numValues = 0;	// number of values selected for this fieldname

    for (var j = 0; j < filters.filtersByName[filterName].fields.length; j++) {
	    fieldname = filters.filtersByName[filterName].fields[j];

	    var items = [];

	    if (fieldname in filters.filtersByName[filterName].values) {
	        items = filters.filtersByName[filterName].values[fieldname];
	        if (typeof(items) === 'string') {
		        items = [ items ];
	        }
	    }

	    if (!filters.filtersByName[filterName].values[fieldname]) {
	        return '';
	    }

	    if (filters.filtersByName[filterName].values[fieldname] === undefined) {
	        return '';
	    }

	    numValues = items.length;

	    if (numValues > 0) {
	        for (var k = 0; k < numValues; k++) {
		        var item = items[k];

		        if (item.toString().length > 0) {
	                s = s + '&' + fieldname + '=' + item;
		        }
	        }
	    }
    }
    return s;
};

/* convert certain hex-coded pieces of the value to their ASCII counterparts
 */
filters.decode = function(val) {
    if (typeof(val) !== 'string') {
	    val = '' + val;
    }
    return val.replace('%26', '&');
};

/* convert certain ASCII characters to their hex-coded counterparts, for 
 * possible inclusion in a URL
 */
filters.encode = function(val) {
    return val.replace('&', '%26');
};

/* write the given string 'msg' out to the browser's error log and / or browser's console log
 */
filters.log = function(msg) {
	if (filters.logging) {
		setTimeout(function() { throw new Error('filters.js: ' + msg); }, 0);
	}
	if(filters.loggingConsole) {
		console.log('filters.js: ' + msg);
	}
};

/* return URL-encoded form parameters (but not filter values) as a string
 */
filters.getQueryString = function() {
    if (filters.queryStringFunction) {
	    return filters.queryStringFunction();
    }
    return "";
};

/* build and return a data source object for retrieving filter values
 */
filters.buildFilterDataSource = function(name, url) {
    filters.log("Building data source for " + name);

    var oCallback = null;

    if (filters.dataTable) {
    	oCallback = {
	        success : filters.dataTable.onDataReturnInitializeTable,
	        failure : filters.dataTable.onDataReturnInitializeTable,
	        scope : this
	    };
    } else if (filters.alternateCallback) {
    	oCallback = {
	        success : filters.alternateCallback,
	        failure : filters.alternateCallback,
	        scope : this
	    };
    } else {
	    filters.log("Must set either dataTable or alternateCallback");
    }

    var qs = filters.getQueryString();

    if (qs) {
	    qs = qs + '&';
    }

    var dsUrl = url + "?";
    filters.log("Data source URL: " + dsUrl);

    var facetDS = new YAHOO.util.DataSource(dsUrl);

    facetDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
    facetDS.responseSchema = { resultsList: "resultFacets",
	    metaFields: { message : "message" } };

    facetDS.maxCacheEntries = 3;

    facetDS.doBeforeParseData = function (oRequest, oFullResponse, oCallback) {
	    oCallback.argument.error = oFullResponse.error;
	    return oFullResponse;
    };

    return facetDS;
};

/* log a new entry in browser history for the current state of things
 */
filters.addHistoryEntry = function() {
	if (filters.dataTable) {
		var state = filters.dataTable.getState();
		if(filters.generatePageRequest) {
			var sortKey = '';
			var sortDir = '';
			if (state.sortedBy) {
				sortKey = state.sortedBy.key;
				sortDir = state.sortedBy.dir;
			}
			var newState = filters.generatePageRequest(0, sortKey, sortDir, filters.dataTable.get("paginator").getRowsPerPage());
			if (filters.historyModule) {
				YAHOO.util.History.navigate(filters.historyModule, newState);
			} else if (filters.navigateFn) {
				filters.navigateFn(newState);
				filters.populateFilterSummary();
			} else {
				filters.log('filters.historyModule is missing');
			}
		} else {
			filters.log('filters.generatePageRequest is missing');
		}
	} else if (!filters.alternateCallback) {
		filters.log('filters.dataTable is missing');
	}
};

/* build the dialog box once and only once
 */
filters.buildDialogBox = function() {
    if (filters.dialogBox) {
		filters.log("using existing dialogBox");
		return;
    }
    filters.log("building new dialogBox");

    // function to be called when submit button is clicked
    var handleSubmit = function() {
		filters.log("entered handleSubmit()");

		var selections = this.getData();
		filters.log('selections: ' + selections);

		var list = [];
		var filterName;

		for (var i in selections) {
		    filterName = filters.fieldnameToFilterName[i];

	    	var selectionValue = selections[i];
	    	if (typeof(selectionValue) === 'string') {
				selectionValue = [ selectionValue ];
	    	}

		    // special case -- YUI treats a set of only one checkbox
		    // differently than it does a set with two or more possibilities

	    	if (selectionValue === true) {
				selectionValue = document.getElementsByName(i)[0].value;
	    	} else if (selectionValue === false) {
				selectionValue = null;
	    	}

		    if (selectionValue !== null) {
		        filters.filtersByName[filterName]['values'][i] = selectionValue;
	    	    filters.log('set filtersByName[' + filterName + ']["values"][' + i + '] = ' + selectionValue + '');
	    	} else {
	        	filters.filtersByName[filterName]['values'][i] = [];
				filters.log('set filters.filtersByName[' + filterName + '].values[' + i + '] = []');
				filters.log('confirmation: ' + filters.filtersByName[filterName]['values'][i]);
	    	}
		} // end - for loop

		filters.addHistoryEntry();
        filters.dialogBox.hide();
		filters.issueCallbacks();

        filters.populateFilterSummary();
		this.submit(); 
    };

    // function to be called on successful submission
    var handleSuccess = function(o) {
	    filters.log('entered handleSuccess()');
	    var response = o.responseText;
	    response = response.split("<!")[0];
	    filters.log("handleSuccess() finished");
    };

    // function to be called on failed submission
    var handleFailure = function(o) {
	    filters.log('entered handleFailure()');
	    this.form.innerHTML = '<img src="' + filters.fewiUrl + 'assets/images/loading.gif">';
	    alert("Submission failed: " + o.status);
    };

    // build the dialog box itself
    filters.dialogBox = new YAHOO.widget.Dialog("facetDialog", {
	    visible : false,
	    context : [ "filterDiv", "tl", "bl", [ "beforeShow" ] ],
	    constraintoviewport : true,
	    width: "305px",
	    buttons : [{ text:"Filter", handler: handleSubmit, isDefault: true} ]
    } );

    filters.dialogBox.hideEvent.subscribe(function() {
	    this.form.innerHTML = '<img src="' + filters.fewiUrl + 'assets/images/loading.gif">';
    } );

    filters.dialogBox.callback = { success: handleSuccess, failure: handleFailure };

    filters.dialogBox.render();
    filters.log ("Built global filters.dialogBox");
};

/* consolidate any duplicate parameters down into at most one occurrence each
 */
filters.consolidateParameters = function(s) {
    var parms = s.split('&');
    var hash = {};

    for (var i = 0; i < parms.length; i++) {
	    hash[parms[i]] = 1;
    }

    var out = [];
    for (var parm in hash) {
	    out.push(parm);
    }
    return out.join('&');
};

/* populate the dialog box for the filter with the given name.  Specify either
 * by 'filterName', or use 'title', 'body', and 'error'.
 */
filters.populateDialogForFilter = function(filterName) {
    filters.log('populating dialog for ' + filterName);

    var parms = filters.getQueryString();
    if (parms) {
	    parms = parms + '&';
    }
    parms = filters.consolidateParameters(parms + filters.getUrlFragment());

    if (filters.listIndexOf(filters.filterNames, filterName) >= 0) {
	    filters.fillAndShowDialog('Retrieving filter values', 'Please wait...', true);
	    var dataSource = filters.filtersByName[filterName]['dataSource'];
	    dataSource.flushCache();
	    dataSource.sendRequest(parms,
	        filters.filtersByName[filterName]['callback']);
    } else {
	    filters.log("Unknown filterName in populateDialogForFilter(" + filterName + ")");
    }
};

/* fill the dialog box with the given title and body, then show it to the
 * user.  (error is a flag to indicate if the button should be disabled)
 */
filters.fillAndShowDialog = function (title, body, error) {
    filters.log('in fillAndShowDialog()');

    if (filters.dialogBox === null) {
	    filters.buildDialogBox();
    }

    if (!body) {
	    body = 'No values in results to filter';
    }

    filters.dialogBox.setHeader(title);
    filters.dialogBox.form.innerHTML = body;

    var buttons = filters.dialogBox.getButtons();

    for (var k in buttons) {
	    buttons[k].set('disabled', error);
    }

    filters.log('showing dialogBox');
    filters.dialogBox.show();
};

/* parse the response from a data source then use it to populate the dialog
 * box and show it to the user.  This works for a filter with a single field
 * which has a list of value choices.  Override this if you need something
 * different.
 */
filters.parseResponseRadio = function(oRequest, oResponse, oPayload) {
	filters.parseResponseShared(oRequest, oResponse, oPayload, "radio");
}

filters.parseResponse = function(oRequest, oResponse, oPayload) {
	filters.parseResponseShared(oRequest, oResponse, oPayload, "checkbox");
}

filters.parseResponseShared = function(oRequest, oResponse, oPayload, widgetType) {
    filters.log('parseResponse() : ' + oPayload.name);

    var list = [];
    var res = oResponse.results;
    var options = [];
    var title = "Filter";

    var fieldname = null;
    if (oPayload.name in filters.filtersByName) {
	    var fields = filters.filtersByName[oPayload.name].fields;
	    if (fields.length > 0) {
	        fieldname = fields[0];
	    }
        filters.log('fieldname: ' + fieldname); 
	    title = filters.filtersByName[oPayload.name].title;
    } else {
	    filters.log('Unknown filter name: ' + oPayload.name);
	    return;
    }

    var filteredValues = filters.filtersByName[oPayload.name].values[fieldname];
    filters.logObject(filteredValues, 'filteredValues');
    if (typeof(filteredValues) == 'string') {
	    filteredValues = [ filteredValues ];
    } else if (!filteredValues) {
	    filteredValues = [];
    }

    var selectedList = [];
    for (var y in filteredValues) {
	    var fvList = filteredValues[y];
	    if (typeof(fvList) == 'string') {
	        fvList = fvList.split(',');
	    }

	    for (var z in fvList) {
	        selectedList.push(fvList[z]);
	    }
    }

    for (var x in res) {
	    var checked = '';
	    var fVal = filters.encode(res[x]);
	    var fVal2 = fVal.replace(/\(/g, ',');

	    var i = selectedList.length;
	    while (i--) {
	        if (selectedList[i] == fVal) {
		        checked = ' CHECKED';
		        break;
	        }

	    // need to also check if there would be a match if we converted
	    // left parentheses back to commas
	    if (selectedList[i].replace(/\(/g, ',') == fVal2) {
		    checked = ' CHECKED';
		    break;
	    }
	}

	if (checked != '') {
	    list.push(res[x] + ' (checked)');
	} else {
	    list.push(res[x]);
	}

	// We convert any commas to a left parenthesis (odd choice...),
	// seemingly to avoid the submitted string getting broken up into
	// multiple strings when the QueryForm object is constructed on
	// the fewi side of the fence.  (my best guess)

	options[x] = '<label><input type="' + widgetType + '" name="'
	    + fieldname + '" value="'
	    + res[x].replace(/,/g, '(') + '"'
	    + checked + '> '
	    + res[x] + '</label>';

    }
    filters.log('parseResponse() found: ' + list.join(', '));
    filters.fillAndShowDialog(title, options.join('<br/>'), false);
};

/* parse the response from a data source then use it to populate the dialog
 * box and show it to the user.  This works for a slider filter with a single
 * field which will return a single value.  Assumes two values will be
 * returned, which are the minimum and maximum values for the slider.  Also
 * assumes these will be numeric.
 */
filters.sliderParser = function(oRequest, oResponse, oPayload) {
    filters.log('sliderParser() : ' + oPayload.name);

    var list = [];
    var res = oResponse.results;
    var options = [];
    var title = "Filter";

    var fieldname = null;
    if (oPayload.name in filters.filtersByName) {
	    var fields = filters.filtersByName[oPayload.name].fields;
	    if (fields.length > 0) {
	        fieldname = fields[0];
	    }
        filters.log('fieldname: ' + fieldname); 
	    title = filters.filtersByName[oPayload.name].title;
    } else {
	    filters.log('Unknown filter name: ' + oPayload.name);
	    return;
    }

    var filteredValue = filters.filtersByName[oPayload.name].values[fieldname];
    filters.logObject(filteredValue, 'filteredValue');
    if (typeof(filteredValue) == 'object') {
	    filteredValue = filteredValue[0];
    }

    if (typeof(filteredValue) === 'undefined') {
	    filteredValue = 0;
    } else if (typeof(filteredValue) === 'string') {
	    filteredValue = Number(filteredValue);
    } else if (!filteredValue) {
	    filteredValue = 0;
    }

    var values = [];
    for (var x in res) {
	    values.push(Number(res[x]));
    }
    values.sort(function(a,b) { return a-b; } );

    if (values.length != 2) {
	    filters.log('too few values: ' + values);
	    values = [ 0, 1 ];
    }

    options.push('<div style="float:left">' + values[0] + '</div>');
    options.push('<div style="float:right; margin-right: 10px">' + values[1] + '</div>');
    options.push('<p/>');
    options.push('<div id="sliderbg" class="yui-h-slider">');
    options.push('<div id="sliderthumb"><img src="' + filters.fewiUrl + 'assets/images/slider_thumb_n.gif">');
    options.push('</div>');
    options.push('Minimum score: <span id="sliderValueShown"></span>');
    options.push('<input type="hidden" id="sliderValueHidden" name="' +
	    fieldname + '" value="">');

    filters.fillAndShowDialog(title, options.join(''), false);


    var sliderbg = YAHOO.util.Dom.get('sliderbg');
    var slider = YAHOO.widget.Slider.getHorizSlider(sliderbg, 'sliderthumb',
	    0, 235);

    filters.filtersByName[oPayload.name]['slider'] = slider;

    var minValue = values[0];
    var maxValue = values[1];

    slider.setValue(0);

    slider.subscribe('change', function() {
	var value = filters.filtersByName[oPayload.name].slider.getValue();

	var sliderValueShown = YAHOO.util.Dom.get('sliderValueShown');
	var sliderValueHidden = YAHOO.util.Dom.get('sliderValueHidden');

	filters.log('value: ' + value);
	var realValue = minValue + (value / 235) * (maxValue - minValue);
	realValue = Math.round(realValue * 1000) / 1000.0;
	filters.log('realValue: ' + realValue);

	sliderValueShown.innerHTML = realValue;
	sliderValueHidden.value = realValue;
    });

    filters.log('after building slider');
};

/* loop through items in obj and output values to the log
 */
filters.logObject = function(obj, objectName, level) {
    var myLevel = 3;
    if (level) {
	    myLevel = level;
    }
    
    if (myLevel <= 0) {
	    return;
    }

    if (obj === null) {
	    filters.log(objectName + ' is null');
	    return;
    } else if (obj === undefined) {
	    filters.log(objectName + ' is undefined');
	    return;
    }

    if (typeof(obj) === 'object') {
	    for (var name in obj) {
	        if (typeof(obj[name]) === 'function') {
		        filters.log('skipping ' + name.toString());
		        continue;
	        }

	        var nameStr = name.toString();

	        if (typeof(obj[name]) === 'object') {
		        filters.logObject (obj[name], objectName + '.' + nameStr,
		            myLevel - 1);
	        } else {
	            filters.log(objectName + '.' + nameStr + ' = '
		            + obj[name].toString());
	        }
    	}
    } else {
	    filters.log(objectName + ' = ' + obj.toString());
    }

    if (myLevel >= 3) {
	    filters.log('logObject() finished');
    }
};

/* handle the click event for a 'remove filter' button.
 * assumes id of 'this' object is of format:
 * 	filter name:field name:value
 * or to remove all values for a given filter, use:
 * 	filter name:clear
 * or to remove all values for all filters, use:
 * 	clearAllFilters
 */
filters.clearFilter = function() {

    // special case where we want to clear the HMDC row/column filters

    if (this.id == filters.hmdcButtonID) {
	    filters.hmdcButtonVisible = false;
	    filters.hmdcButtonCallback();
	    return;
    }

    // special case where we want to clear all filters

    if (this.id === 'clearAllFilters') {
	    if (filters.hmdcButtonVisible) {
	        /* We've filtered by rows/columns (possibly in addition to other
	         * filters), which needs special handling.
	         */

	        filters.clearAllFilters(true);
	        hmdcFilters.updateHiddenFields();
	        filters.hmdcButtonVisible = false;
	        filters.hmdcButtonCallback();
	    } else {
	        /* We've only filtered by filters in this library, not by rows
	         * and column on the HMDC grid.
	         */
	        filters.clearAllFilters();
	        filters.addHistoryEntry();
	    }
	    return;
    }

    var kv = this.id.split(':');

    // special case where we want to clear all values for a filter

    if (kv.length == 2) {
	    if (kv[1] === 'clear') {
	        filters.clearAllValuesForFilter(kv[0]);
	        filters.addHistoryEntry();
	        return;
	    }
    }

    // normal case -- clear a value for a single field of a single filter

    if (kv.length != 3) {
	    filters.log('unexpected button ID: ' + this.id);
	    return;
    }

    var filterName = kv[0];
    var fieldName = kv[1];
    var fieldValue = kv[2];

    if (filterName in filters.filtersByName) {
	    var pairs = filters.filtersByName[filterName]['values'];

	    if (fieldName in pairs) {
	        var pos = filters.listIndexOf (pairs[fieldName], fieldValue);

	        if (pos >= 0) {
		    if (typeof(pairs[fieldName]) === 'string') {
		        pairs[fieldName] = [];
		    } else {
		        pairs[fieldName].splice(pos, 1);
		    }
		    filters.log('removed ' + fieldValue + ' from field '
		        + fieldName + ' in filter ' + filterName);

	        } else {
		        filters.log('value ' + fieldValue + ' not selected for field '
		        + fieldName + ' in filter ' + filterName);
	        }
	    } else {
	        filters.log('field ' + fieldName + ' unknown for filter '
		    + filterName);
    	}
    } else {
	    filters.log('unknown filter name: ' + filterName);
    }

    filters.addHistoryEntry();
    filters.issueCallbacks();
};

/* populate the filter summary DIVs on the form (does all of them)
 */
var npd = null;
var spd = null;
var d = null;
filters.populateFilterSummary = function() {
    filters.log('in populateFilterSummary()');
    if (filters.buttonInfo === null) {
	    filters.log('need to call either setSummaryNames() or setButtonInfo() for initialization');
	    return;
    }

    // Gather lists of fields by DIV name and also the SPAN for each DIV.  Assumes 1-to-1 relationship
    // between DIV and SPAN--unspecified behavior if violated.
    
    var namesPerDiv = {};	// { div name : list of filter fieldnames }
    var spanPerDiv = {};	// { div name : span name }
    var divs = [];			// list of unique DIV names
    
    for (var i in filters.filterNames) {
    	var filterName = filters.filterNames[i];
    	if (filterName in filters.buttonInfo) {
    		var div = filters.buttonInfo[filterName][0];
    		var span = filters.buttonInfo[filterName][1];
    		
    		if (!(div in namesPerDiv)) {
    			namesPerDiv[div] = [ filterName ];
    			divs.push(div);
    		} else {
    			namesPerDiv[div].push(filterName);
    		}
    		
    		if (!(div in spanPerDiv)) {
    			spanPerDiv[div] = span;
    		} else if (span != spanPerDiv[div]) {
    			filters.log('Found extra SPAN (' + span + ') for DIV (' + div + ')');
    		}
    	} else {
    		filters.log('Unknown button name (' + filterName + ') not included');
    	}
    }
    
    npd = namesPerDiv;
    spd = spanPerDiv;
    d = divs;
    
    // For cases with multiple divs, do not show a Remove All Filters button.
    var multipleDivs = (divs.length > 1);
    
    // Now walk through and populate each DIV/SPAN with applicable "remove filter" buttons.
    
    for (var i in divs) {
    	var div = divs[i];
    	
        var fSum = YAHOO.util.Dom.get(div);
        if (fSum === null) {
	        filters.log('unrecognized DIV name: ' + div);
	        return;
        }

        var fList = new YAHOO.util.Element(spanPerDiv[div]);
        var filterNames = namesPerDiv[div];
    	
        // clean out any existing buttons from the filter list
        if (!YAHOO.lang.isNull(YAHOO.util.Dom.get(spanPerDiv[div]))) {
	        while (fList.hasChildNodes()) {
	            fList.removeChild(fList.get('firstChild'));
            }
        }
    	
        var buttons = filters.getAllSummaryButtons(namesPerDiv[div], multipleDivs);
        filters.log('Returned from getAllSummaryButtons()');

        filters.log('adding ' + buttons.length + ' buttons for DIV ' + div);
        for (var b in buttons) {
        	var button = buttons[b];
        	fList.appendChild(button);
        	fList.appendChild(document.createTextNode(' '));
        }

        if (buttons.length > 0) {
        	YAHOO.util.Dom.setStyle(fSum, 'display', filters.removalDivStyle);
        } else {
        	YAHOO.util.Dom.setStyle(fSum, 'display', 'none');
        }
    }
};

// set to 'inline' or 'block' as needed for display of DIV with filter removal buttons
filters.setRemovalDivStyle = function(s) {
	filters.removalDivStyle = s;
}

function setText(element, text){
	element.textContent=text;
    if (!YAHOO.lang.isUndefined(element.innerText)){                 
        element.innerText = text;

    }
};

// flag to be able to tell if module is loaded
window.filtersLoaded = true;
