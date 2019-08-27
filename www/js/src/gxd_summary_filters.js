/* Name: gxd_summary_filters.js
 * Purpose: added for GXD Anatomy release, to allow filtering on GXD summary
 *   page.  Modeled after code from reference_summary.js.
 * Notes:
 * 1. (April 2015) - switched the anatomical system filter to no longer be a
 * traditional filter, but instead to piggyback on the grid's structureIdFilter
 * field and use its handling.
 */

/* Globals
 */

var facets = {};
var gsfFacetDialog;

var displayStageDayMap = {
	1:"(0.0-2.5 dpc)",
	2:"(1.0-2.5 dpc)",
	3:"(1.0-3.5 dpc)",
	4:"(2.0-4.0 dpc)",
	5:"(3.0-5.5 dpc)",
	6:"(4.0-5.5 dpc)",
	7:"(4.5-6.0 dpc)",
	8:"(5.0-6.5 dpc)",
	9:"(6.25-7.25 dpc)",
	10:"(6.5-7.75 dpc)",
	11:"(7.25-8.0 dpc)",
	12:"(7.5-8.75 dpc)",
	13:"(8.0-9.25 dpc)",
	14:"(8.5-9.75 dpc)",
	15:"(9.0-10.25 dpc)",
	16:"(9.5-10.75 dpc)",
	17:"(10.0-11.25 dpc)",
	18:"(10.5-11.25 dpc)",
	19:"(11.0-12.25 dpc)",
	20:"(11.5-13.0 dpc)",
	21:"(12.5-14.0 dpc)",
	22:"(13.5-15.0 dpc)",
	23:"(15 dpc)",
	24:"(16 dpc)",
	25:"(17 dpc)",
	26:"(18 dpc)",
	27:"(newborn)",
	28:"(postnatal)"
};

/* Functions
 */

function gsfLog(msg) {
    // log a message to the browser console
	console.log(msg); 
}

// encode an individual value selected for a filter, to be URL-safe
function encode(val) {
    var newVal = val.replace('&', '%26');
    gsfLog('encode(' + val + ') ==> ' + newVal);
    return newVal;
}

function decode(val) {
    var newVal = val.replace('%26', '&');
    gsfLog('decode(' + val + ') ==> ' + newVal);
    return newVal;
}

/* pulls out the filters from the request (call in doBeforeLoadData, pass in
 * the results of parseRequest).  These are filters that have already been
 * selected by the user to affect the search results.
 */
var extractFilters = function(pRequest) {
    facets = {}
    // walks through request parameter names
    for (k in pRequest) {
	if (k.indexOf("Filter") != -1) {
	    facets[k] = [].concat(pRequest[k]);
	}
    }

    var s = "{";
    for (k in facets) {
	s = s + k + " : " + facets[k] + ", ";
    }
    s = s + "}";

    gsfLog("extractFilters() : " + s);
    populateFilterSummary();
};

/* returns a string with the filters encoded as URL parameters, for use in
 * the generateRequest function
 */
var getFilterCriteria = function() {
    var s = '';
    var isFirst = 1;

    for (key in facets) {
	list = facets[key];
	for (i = 0; i < list.length; i++) {
	    if (isFirst == 0) {
		s = s + '&';
	    } else {
		isFirst = 0;
	    }
	    s = s + key + '=' + encode(list[i].replace('*', ','));
	}
    }
    gsfLog("getFilterCriteria() : " + s);
    return s;
};

/* populate a dialog box for a facet
 */
var populateFacetDialog = function (title, body, error) {
    gsfLog("populateFacetDialog() - entered");

    if (gsfFacetDialog === null) {
	gsfLog("found null gsfFacetDialog; repairing");
	prepFilters();
    }

    gsfFacetDialog.setHeader('Filter by ' + title);
    gsfFacetDialog.form.innerHTML = body;
    buttons = gsfFacetDialog.getButtons();

    for (k in buttons) {
	buttons[k].set('disabled', error);
    }
    gsfLog("populateFacetDialog() - showing dialog");
    gsfFacetDialog.show();
    gsfLog("populateFacetDialog() - exiting");
};

/* parse the results we get back to populate a filter
 */
var parseFacetResponse = function (oRequest, oResponse, oPayload) {
	var results = oResponse.results;
	var facetName = oPayload.name;
	var facetValue = '';
	var options = [];
	var hasCheckedFacet = false;

	// First, loop through all facet rows to see if any are 'checked'; if so,
	// we will only display those that are checked
	results.forEach(function(facet){
		facetValue = encode(facet);
		if (facetName in window.facets) {
			var fil = facets[oPayload.name];
			var i = fil.length;
			while (i--) {
				if (fil[i] === facetValue) {
					hasCheckedFacet = true;
				}
			}
		}
	});

	// display only 'checked' facet values if ANY are checked
	if (hasCheckedFacet) {  
		results.forEach(function(facet){
			facetValue = encode(facet);
			if (facetName in window.facets) {
				var fil = facets[oPayload.name];
				var i = fil.length;
				while (i--) {
					if (fil[i] === facetValue) {
						// create each row of filter
						if (facetName == 'theilerStageFilter') {
							var facetLabel = 'TS' + facet + ' ' + displayStageDayMap[facet];
							options[options.length] = '<label><input type="checkbox" name="'
								+ facetName + '" value="'
								+ facet.replace(/,/g, '*') + '"'
								+ ' checked > '
								+ facetLabel + '</label>';
						}
						else {
							options[options.length] = '<label><input type="checkbox" name="' + facetName 
								+ '" value="' + facet.replace(/,/g, '*') + '"' + ' checked >'
								+ facetValue + '</label>';
						}
					}
				}
			}
		});
		
	}
	else { // no 'checked' facet values; display all
		
		results.forEach(function(facet){

			// create each row of filter
			if (facetName == 'theilerStageFilter') {
				var facetLabel = 'TS' + facet + ' ' + displayStageDayMap[facet];
				options[options.length] = '<label><input type="checkbox" name="'
					+ facetName + '" value="'
					+ facet.replace(/,/g, '*') + '"'
					+ '> '
					+ facetLabel + '</label>';
			}
			else {
				options[options.length] = '<label><input type="checkbox" name="'
					+ facetName + '" value="'
					+ facet.replace(/,/g, '*') + '"'
					+ '> '
					+ facet + '</label>';
			}
		});
		
	}

	populateFacetDialog(oPayload.title, options.join('<br/>'), false);
};

/* parse the results we get back to populate the system filter
 */
var parseSystemFacetResponse = function (oRequest, oResponse, oPayload) {
	var results = oResponse.results;
	var targetFacetName = 'structureIDFilter';
	var options = [];	// contents of the popup box so far

	// tracks which already-selected IDs we've seen so far
	var seen = { 'length' : 0 };

	/* cases to consider for structure filtering:
	 * 1. no values already chosen
	 *    - show high-level terms which are ancestors of terms in results
	 * 2. one or more high-level terms chosen
	 *    - show only options for the selected terms and have them checked
	 * 3. no high-level terms chosen, but more specific term(s) chosen
	 *    - show only message "More specific filter(s) already selected"
	 * 4. both high-level term(s) and more specific term(s) chosen
	 *    - show options for selected high-level term(s), have them checked,
	 *      and show message "Tissue matrix filters have been selected."
	 */

	results.forEach(function(facet){
		/* if we have a structureIDFilter already in-place, then we
		 * only want to show those checkboxes and we want to show them
		 * checked.
		 */
		var checked = '';
		var fVal = encode(facet);
		var skipIt = 0;

		if (targetFacetName in window.facets) {
			var fil = facets[targetFacetName];
			var i = fil.length;
			skipIt = 1;

			while (i--) {
				// if this string ends with an EMAPA number
				// that we've filtered by, then note it as
				// a 'checked' one

				if (fVal.indexOf(fil[i]) == (fVal.length - fil[i].length)) {
					checked = ' checked ';
					seen[fil[i]] = 1;
					seen.length = 1 + seen.length;
					skipIt = 0;
				}
			}
		}

		if (skipIt == 0) {
			var pieces = facet.split("_");
			options[options.length] =
				'<label><input type="checkbox" name="'
				+ targetFacetName + '" value="'
				+ pieces[1] + '"'
				+ checked + '> '
				+ pieces[0] + '</label>';
		}
	});

	// go through and look for already-selected IDs that we didn't find
	// in our list of facets.  If we didn't find them, then we need to
	// include them as hidden fields.
	
	var hasSpecifics = 0;
	if (targetFacetName in window.facets) {
		facets[targetFacetName].forEach(function(facet) {
			var fVal = encode(facet);
			if (!(fVal in seen)) {
				hasSpecifics = 1;
				options[options.length] =
					"<span style='display:none'>"
					+ "<input type='checkbox' name='"
					+ targetFacetName
					+ "' checked='checked' "
					+ "value='" + fVal + "'></span>";
			}
		});
	}

	// now nail down which case we're in (of the 4 listed above)
	
	if (targetFacetName in window.facets) {
		var msg = "Tissue matrix filters have been selected.";
		if (seen.length == 0) {
			// case 3
	    		populateFacetDialog(oPayload.title, msg, true);
			return;
		} else if (hasSpecifics == 1) {
			// case 4 -- add additional message, then let it fall
			// through
			options[options.length] = msg;
		} else {
			// case 2 -- let if fall through as-is
		}
	} else {
		// case 1 -- let it fall through as-is
	}
	populateFacetDialog(oPayload.title, options.join('<br/>'), false);
};

/*
 * Actually submit a new query with the specified facet list
 */
var submitFacets = function(newFacets) {
	window.facets = newFacets;
	var sortedBy = null;
	var pageSize = 0;
	if (gxdDataTable)
	{
		var state = gxdDataTable.getState();
		sortedBy = state.sortedBy;
		pageSize = gxdDataTable.get("paginator").getRowsPerPage();
	}
	var newState = generateRequest (sortedBy, 0, pageSize);
	YAHOO.util.History.navigate("gxd", newState);
}

/* build the facet dialog once and only once
 */
var buildFacetDialog = function() {
    if (gsfFacetDialog) { return; }

    var handleSubmit = function() {
		gsfLog("entered handleSubmit()");
		var selections = this.getData();
		var list = [];
		for (i in selections) {
		    gsfLog("facets[" + i + "] = " + selections[i]);
		    facets[i] = selections[i];
		}
		submitFacets(facets);
		refreshTabCounts();
		gsfLog("executing this.submit()");
		this.submit();
    };

    var handleSuccess = function(o) {
	var response = o.responseText;
	response = response.split("<!")[0];
	//document.getElementById("resp").innerHTML = response;
	gsfLog("handleSuccess() response: " + response);
    };

    var handleFailure = function(o) {
	this.form.innerHTML = '<img src="/fewi/mgi/assets/images/loading.gif">';
	alert("Submission failed: " + o.status);
    };

    gsfFacetDialog = new YAHOO.widget.Dialog("facetDialog", {
	visible : false,
	context : [ "summary", "tl", "tl", ["beforeShow","changeContent"]],
	constraintoviewport : true,
	buttons : [{ text:"Filter", handler:handleSubmit, isDefault:true } ]
    } );

    gsfFacetDialog.hideEvent.subscribe(function() {
	this.form.innerHTML = '<img src="/fewi/mgi/assets/images/loading.gif">';
    } );

    gsfFacetDialog.callback = { success: handleSuccess, failure: handleFailure };

    gsfFacetDialog.render();
    gsfLog("buildFacetDialog() : rendered gsfFacetDialog");
}

/* ensure that global querystring is set appropriately
 */
var localQueryString = function() {
    if (querystring) { return querystring; }

    if (typeof getQueryString == 'function') {
	return getQueryString();
    }
};

/* prep work to initialize the filters (call from onDOMReady)
 */
var prepFilters = function(qfRequest) {

    gsfLog("prepFilters() : entered");
    buildFacetDialog();

    var oCallback = {
	success : gxdDataTable.onDataReturnInitializeTable,
	failure : gxdDataTable.onDataReturnInitializeTable,
	scope : this
    };

    /* get the query string, ready to be appended to
     */
    var getQS = function(qfRequest) {
    	var qs = qfRequest;
    	if (!qs) {
    		qs = localQueryString();
    	}
    	if (qs) {
    		qs = qs + '&';
    	}
    	return qs;
    };

    var buildFacetDataSource = function (name) {
	gsfLog("facet data source name : " + name);

	var url = fewiurl + "gxd/facet/" + name;

	var facetDS = new YAHOO.util.DataSource (url, { 'connMethodPost' : true });
	gsfLog("facetDataSource url : " + url);

	facetDS.responseType = YAHOO.util.DataSource.TYPE_JSON;

	facetDS.responseSchema = { resultsList: "resultFacets",
	    metaFields: { message : "message" } };

	facetDS.maxCacheEntries = 3;

	facetDS.doBeforeParseData = function (oRequest, oFullResponse,
	    oCallback) {
		oCallback.argument.error = oFullResponse.error;
		return oFullResponse;

	};

	return facetDS;
    }

    var facetSystemDS = buildFacetDataSource("system");
    var facetAssayTypeDS = buildFacetDataSource("assayType");
    var facetMarkerTypeDS = buildFacetDataSource("markerType");
    var facetDetectedDS = buildFacetDataSource("detected");
    var facetWildtypeDS = buildFacetDataSource("wildtype");
    var facetTheilerStageDS = buildFacetDataSource("theilerStage");
    var facetMpDS = buildFacetDataSource("mp");
    var facetDoDS = buildFacetDataSource("do");

    var handleError = function (oRequest, oResponse, oPayload) {
	buttons = gsfFacetDialog.getButtons();
	for (k in buttons) {
	    buttons[k].set('disabled', 'true');
	}
	populateFacetDialog(oPayload.title, oPayload.error, true);
    };

    var buildCallback = function (filterName, title) {
	return { success: parseFacetResponse,
	    failure: handleError,
	    scope: this,
	    argument: { name: filterName, title: title }
	};
    };

    var buildSystemCallback = function (filterName, title) {
	return { success: parseSystemFacetResponse,
	    failure: handleError,
	    scope: this,
	    argument: { name: filterName, title: title }
	};
    };

    var systemCallback = buildSystemCallback('systemFilter', 'Anatomical System');
    var assayTypeCallback = buildCallback('assayTypeFilter', 'Assay Type');
    var markerTypeCallback = buildCallback('markerTypeFilter', 'Gene Type');
    var mpCallback = buildCallback('mpFilter', 'Phenotype');
    var doCallback = buildCallback('doFilter', 'Disease');
    var detectedCallback = buildCallback('detectedFilter', 'Detected?');
    var wildtypeCallback = buildCallback('wildtypeFilter', 'Wild type?');
    var theilerStageCallback = buildCallback('theilerStageFilter', 'Theiler Stage');

    var populateSystemDialog = function() {
	gsfLog("populateSystemDialog() : 1");
	facetSystemDS.flushCache();
	facetSystemDS.sendRequest(getQS() + getFilterCriteria(), systemCallback);
	gsfLog("populateSystemDialog() : 2");
    };

    var populateAssayTypeDialog = function() {
	gsfLog("populateAssayTypeDialog() : 1");
	facetAssayTypeDS.flushCache();
	facetAssayTypeDS.sendRequest(getQS() + getFilterCriteria(), assayTypeCallback);
	gsfLog("populateAssayTypeDialog() : 2");
    };

    var populateMarkerTypeDialog = function() {
	gsfLog("populateMarkerTypeDialog() : 1");
 	facetMarkerTypeDS.flushCache();
	facetMarkerTypeDS.sendRequest(getQS() + getFilterCriteria(), markerTypeCallback);
	gsfLog("populateMarkerTypeDialog() : 2");
    };

    var populateMpDialog = function() {
   	gsfLog("populateMpDialog() : 1");
	facetMpDS.flushCache();
	facetMpDS.sendRequest(getQS() + getFilterCriteria(), mpCallback);
	gsfLog("populateMpDialog() : 2");
    };
    
    var populateDoDialog = function() {
   	gsfLog("populateDoDialog() : 1");
	facetDoDS.flushCache();
 	facetDoDS.sendRequest(getQS() + getFilterCriteria(), doCallback);
 	gsfLog("populateDoDialog() : 2");
    };
    
    var populateDetectedDialog = function() {
	gsfLog("populateDetectedDialog() : 1");
	facetDetectedDS.flushCache();
	facetDetectedDS.sendRequest(getQS() + getFilterCriteria(), detectedCallback);
	gsfLog("populateDetectedDialog() : 2");
    };

    var populateWildtypeDialog = function() {
	gsfLog("populateWildtypeDialog() : 1");
	facetWildtypeDS.flushCache();
	facetWildtypeDS.sendRequest(getQS() + getFilterCriteria(), wildtypeCallback);
	gsfLog("populateWildtypeDialog() : 2");
    };

    var populateTheilerStageDialog = function() {
	gsfLog("populateTheilerStageDialog() : 1");
	facetTheilerStageDS.flushCache();
	facetTheilerStageDS.sendRequest(getQS() + getFilterCriteria(), theilerStageCallback);
	gsfLog("populateTheilerStageDialog() : 2");
    };

    // must clear any existing listeners, otherwise we'll execute multiple
    // function calls for each click...  and not all have the right parameters

    YAHOO.util.Event.removeListener('systemFilter', 'click');
    YAHOO.util.Event.removeListener('assayTypeFilter', 'click');
    YAHOO.util.Event.removeListener('markerTypeFilter', 'click');
    YAHOO.util.Event.removeListener('mpFilter', 'click');
    YAHOO.util.Event.removeListener('doFilter', 'click');
    YAHOO.util.Event.removeListener('detectedFilter', 'click');
    YAHOO.util.Event.removeListener('wildtypeFilter', 'click');
    YAHOO.util.Event.removeListener('theilerStageFilter', 'click');

	YAHOO.util.Event.addListener('systemFilter', 'click',
    		populateSystemDialog, true);
	YAHOO.util.Event.addListener('assayTypeFilter', 'click',
    		populateAssayTypeDialog, true);
	YAHOO.util.Event.addListener('markerTypeFilter', 'click',
			populateMarkerTypeDialog, true);
	YAHOO.util.Event.addListener('mpFilter', 'click',
			populateMpDialog, true);
	YAHOO.util.Event.addListener('doFilter', 'click',
			populateDoDialog, true);
	YAHOO.util.Event.addListener('detectedFilter', 'click',
			populateDetectedDialog, true);
	YAHOO.util.Event.addListener('wildtypeFilter', 'click',
			populateWildtypeDialog, true);
	YAHOO.util.Event.addListener('theilerStageFilter', 'click',
			populateTheilerStageDialog, true);

    gsfLog("prepFilters() : exited");
};

/* remove the optional wildtypeFilter parameter from the URL itself, if it exists
 */
var removeWildtypeFilterFromQuerystring = function() {
	// If searchedWildtypeFilter has not been defined (as for any search other than by marker ID),
	// then catch the corresponding exception and skip it.
	try {
		if (searchedWildtypeFilter != '') {
			querystring = querystring.replace('&wildtypeFilter=' + searchedWildtypeFilter, '');
			searchedWildtypeFilter = "";
		}
	} catch (c) {}
}

/* removes all filters
 */
var clearAllFilters = function() {
    window.facets = {};
    removeWildtypeFilterFromQuerystring();
//    var newState = generateRequest (gxdDataTable.getState().sortedBy, 0,
//	gxdDataTable.get("paginator").getRowsPerPage() );
//    YAHOO.util.History.navigate("gxd", newState);
//    refreshTabCounts();
}

/* removes a filter
 */
var clearFilter = function() {
    kv = this.id.split(":");
    var items = facets[kv[0]];
    var val = this.id.slice(this.id.indexOf(":")+1);

    if (val == 'clearFilter') {
	facets = {};
    } else {
	for (var i = 0; i < items.length; i++) {
	    if (items[i] == val) {
		found = i;
		break;
	    }
	}
	if (i != -1) {
	    items.splice(i, 1);
	}
    }
    removeWildtypeFilterFromQuerystring();
    var newState = generateRequest (gxdDataTable.getState().sortedBy, 0,
	gxdDataTable.get("paginator").getRowsPerPage() );

    YAHOO.util.History.navigate("gxd", newState);
    refreshTabCounts();
};

/* used to update and display facets, as contained in the dictionary
 * newFacets.  (for going back in browser history)
 */
var resetFacets = function(newFacets) {
    window.facets = newFacets;
    populateFilterSummary();
};

/* populates the 'breadbox' with current filters
 */
var populateFilterSummary = function() {
    var fSum = YAHOO.util.Dom.get('filterSummary');
    if (YAHOO.lang.isUndefined(fSum)) {
    	return;
    }

    var filterList = new YAHOO.util.Element('filterList');
    var fCount = YAHOO.util.Dom.get('fCount');
    var defaultText = YAHOO.util.Dom.get('defaultText');

    if (!YAHOO.lang.isNull(YAHOO.util.Dom.get('filterList'))) {
		while (filterList.hasChildNodes()) {
		    filterList.removeChild(filterList.get('firstChild'));
		}

		clear = document.createElement('a');
		clear.setAttribute('class', 'filterItem');
		clear.setAttribute('id' , 'clearFilter');
		setText(clear, 'Remove All Filters');
		filterList.appendChild(clear);
		YAHOO.util.Event.addListener(clear, 'click', clearFilter);
    }

    var vis = false;

	var emapaIDsToMap = [];
    for (k in facets) {
    	// ensure that facet lists are unique
    	window.facets[k] = FewiUtil.uniqueValues(facets[k]);
		var fValues = facets[k];

		var brTag = false;
		fValues.forEach(function(filterValue){
		    YAHOO.util.Dom.setStyle(fSum, 'display', 'block');
		    vis = true;
		    brTag = true;

		    var el = document.createElement('a');
		    el.setAttribute('class', 'filterItem');
		    el.setAttribute('id', k + ':' + filterValue);
		    el.setAttribute('style', '{padding-top:2px; padding-left:2px}');
		    el.setAttribute('title', 'click to remove this filter');
		    var filterTitle = k.charAt(0).toUpperCase() + k.slice(1);
		    filterTitle = filterTitle.replace('Filter', '');

		    // some filters use camelcase; need to insert a space for them
		    if(filterTitle == 'AssayType') filterTitle = 'Assay Type';
		    else if(filterTitle == 'TheilerStage') filterTitle = "TS";
		    else if(filterTitle == 'MarkerType') filterTitle = "Gene Type";
		    else if(filterTitle == 'Mp') filterTitle = "Phenotype";
		    else if(filterTitle == 'MarkerSymbol') filterTitle = "Gene";
		    else if(filterTitle == 'Wildtype') filterTitle = '';
		    else if(filterTitle == 'StructureID')
		    {
		    	filterTitle = '';
		    	// try to map emapaIDs to names
		    	if(emapaIDMap.hasOwnProperty(filterValue))
		    	{
		    		filterValue = emapaIDMap[filterValue];
		    	}
		    	else
		    	{
		    		// need to be mapped later
		    		emapaIDsToMap[emapaIDsToMap.length] = filterValue;
		    	}
		    }
		    else if(filterTitle == 'Detected') filterTitle += '?';

		    if(filterTitle) filterTitle += ': ';

		    // sanitize the filterValue
		    if(filterValue)
		    {
		    	filterValue = filterValue.replace('*', ',');
		    	filterValue = decode(filterValue);
		    }
		    var val = filterTitle + filterValue;
		    setText(el, val);

		    filterList.appendChild(el);
		    YAHOO.util.Event.addListener(el, 'click', clearFilter);

		    filterList.appendChild(document.createTextNode(' '));
		});

		if (brTag) {
		    filterList.appendChild(document.createElement('br'));
		}
    }

    if (vis) {
	    YAHOO.util.Dom.setStyle(fSum, 'display', 'inline');
	    YAHOO.util.Dom.setStyle(clear, 'display', 'inline');
    } else {
	YAHOO.util.Dom.setStyle(fSum, 'display', 'none');
	    YAHOO.util.Dom.setStyle(clear, 'display', 'none');
    }

    if(emapaIDsToMap && emapaIDsToMap.length>0)
    {
	    // resolve any EMAPA IDs into their structure names
	    $.getJSON(fewiurl+"autocomplete/emapaID/resolve?ids="+emapaIDsToMap.join(","), function(data){
			var emapaTerms = data;
			console.log("found new name for the emapaIDs: "+emapaTerms);
			if(emapaTerms && emapaTerms.length == emapaIDsToMap.length)
			{
				for(var i=0;i<emapaTerms.length;i++)
				{
					var id = emapaIDsToMap[i];
					var name = emapaTerms[i];
					// update cache
					window.emapaIDMap[id] = name;
				}
				// with the emapaID to name cache updated, try rerunning the filter summary.
				populateFilterSummary();
			}
			else
			{
				console.log("warning: could not properly map emapaIDs for filter summary to their names.");
			}
	    });
    }
};
window.emapaIDMap = {};

var isFilterable = function(fieldname) {
    if (fieldname.indexOf("Filter") >= 0) {
	return true;
    }
    return false;
};
