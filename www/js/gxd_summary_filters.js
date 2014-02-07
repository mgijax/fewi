/* Name: gxd_summary_filters.js
 * Purpose: added for GXD Anatomy release, to allow filtering on GXD summary
 *   page.  Modeled after code from reference_summary.js.
 */

/* Globals
 */

var facets = {};
var gsfFacetDialog;

/* Functions
 */

function gsfLog(msg) {
    // log a message to the browser console
//    setTimeout(function() { throw new Error(msg); }, 0);
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
	    facets[k] = pRequest[k];
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
    var res = oResponse.results;
    var options = [];

    gsfLog("parseFacetResponse(): oPayload.name : " + oPayload.name);

    var list = [];

    for (x in res) {
	var checked = '';
	var fVal = encode(res[x]);

	if (oPayload.name in facets) {
	    var fil = facets[oPayload.name];
	    var i = fil.length;
	    while (i--) {
		if (fil[i] === fVal) {
		    checked = ' CHECKED';
		}
	    }
	}

	if (checked != '') {
	    list.push(res[x] + ' (checked)');
	} else {
	    list.push(res[x]);
	}

	options[x] = '<label><input type="checkbox" name="'
		+ oPayload.name + '" value="'
		+ res[x].replace(/,/g, '*') + '"'
		+ checked + '> '
		+ res[x] + '</label>';
    }

    gsfLog('parseFacetResponse() found: ' + list.join(', '));
    populateFacetDialog(oPayload.title, options.join('<br/>'), false);
};

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
	var state = gxdDataTable.getState();
	var newState = generateRequest (state.sortedBy, 0,
	    gxdDataTable.get("paginator").getRowsPerPage());
	YAHOO.util.History.navigate("gxd", newState);
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
	context : [ "filterDiv", "tl", "bl", ["beforeShow"]],
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
    var qs = qfRequest;
    if (!qs) {
        qs = localQueryString();
    }
    buildFacetDialog();

    var oCallback = {
	success : gxdDataTable.onDataReturnInitializeTable,
	failure : gxdDataTable.onDataReturnInitializeTable,
	scope : this
    };

    var buildFacetDataSource = function (name) {
	gsfLog("facet data source name : " + name);
	gsfLog("facet data source qs : " + qs);

	var url = fewiurl + "gxd/facet/" + name + "?" + qs;
	if (qs) {
	    url = url + '&';
	}

	var facetDS = new YAHOO.util.DataSource (url);
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
    var facetDetectedDS = buildFacetDataSource("detected");
    var facetWildtypeDS = buildFacetDataSource("wildtype");
    var facetTheilerStageDS = buildFacetDataSource("theilerStage");

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

    var systemCallback = buildCallback('systemFilter', 'Anatomical System');
    var assayTypeCallback = buildCallback('assayTypeFilter', 'Assay Type');
    var detectedCallback = buildCallback('detectedFilter', 'Detected?');
    var wildtypeCallback = buildCallback('wildtypeFilter', 'Wild type?');
    var theilerStageCallback = buildCallback('theilerStageFilter', 'Theiler Stage');

    var populateSystemDialog = function() {
	gsfLog("populateSystemDialog() : 1");
	facetSystemDS.flushCache();
	facetSystemDS.sendRequest(getFilterCriteria(), systemCallback);
	gsfLog("populateSystemDialog() : 2");
    };

    var populateAssayTypeDialog = function() {
	gsfLog("populateAssayTypeDialog() : 1");
	facetAssayTypeDS.flushCache();
	facetAssayTypeDS.sendRequest(getFilterCriteria(), assayTypeCallback);
	gsfLog("populateAssayTypeDialog() : 2");
    };

    var populateDetectedDialog = function() {
	gsfLog("populateDetectedDialog() : 1");
	facetDetectedDS.flushCache();
	facetDetectedDS.sendRequest(getFilterCriteria(), detectedCallback);
	gsfLog("populateDetectedDialog() : 2");
    };

    var populateWildtypeDialog = function() {
	gsfLog("populateWildtypeDialog() : 1");
	facetWildtypeDS.flushCache();
	facetWildtypeDS.sendRequest(getFilterCriteria(), wildtypeCallback);
	gsfLog("populateWildtypeDialog() : 2");
    };

    var populateTheilerStageDialog = function() {
	gsfLog("populateTheilerStageDialog() : 1");
	facetTheilerStageDS.flushCache();
	facetTheilerStageDS.sendRequest(getFilterCriteria(), theilerStageCallback);
	gsfLog("populateTheilerStageDialog() : 2");
    };

    // must clear any existing listeners, otherwise we'll execute multiple
    // function calls for each click...  and not all have the right parameters

    YAHOO.util.Event.removeListener('systemFilter', 'click');
    YAHOO.util.Event.removeListener('assayTypeFilter', 'click');
    YAHOO.util.Event.removeListener('detectedFilter', 'click');
    YAHOO.util.Event.removeListener('wildtypeFilter', 'click');
    YAHOO.util.Event.removeListener('theilerStageFilter', 'click');

    YAHOO.util.Event.addListener('systemFilter', 'click',
	populateSystemDialog, true);
    YAHOO.util.Event.addListener('assayTypeFilter', 'click',
	populateAssayTypeDialog, true);
    YAHOO.util.Event.addListener('detectedFilter', 'click',
	populateDetectedDialog, true);
    YAHOO.util.Event.addListener('wildtypeFilter', 'click',
	populateWildtypeDialog, true);
    YAHOO.util.Event.addListener('theilerStageFilter', 'click',
	populateTheilerStageDialog, true);

    gsfLog("prepFilters() : exited");
};

/* removes all filters
 */
var clearAllFilters = function() {
    facets = {};
//    var newState = generateRequest (gxdDataTable.getState().sortedBy, 0,
//	gxdDataTable.get("paginator").getRowsPerPage() );
//    YAHOO.util.History.navigate("gxd", newState);
    refreshTabCounts();
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
    var newState = generateRequest (gxdDataTable.getState().sortedBy, 0,
	gxdDataTable.get("paginator").getRowsPerPage() );
    
    YAHOO.util.History.navigate("gxd", newState);
    refreshTabCounts();
};

/* used to update and display facets, as contained in the dictionary
 * newFacets.  (for going back in browser history)
 */
var resetFacets = function(newFacets) {
    facets = newFacets;
    populateFilterSummary();
    refreshTabCounts();
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

    for (k in facets) {
	var inner = facets[k];
	var brTag = false;
	list = facets[k];

	for (v = 0; v < inner.length; v++) {
	    YAHOO.util.Dom.setStyle(fSum, 'display', 'block');
	    vis = true;
	    brTag = true;

	    var el = document.createElement('a');
	    el.setAttribute('class', 'filterItem');
	    el.setAttribute('id', k + ':' + inner[v]);
	    el.setAttribute('style', '{padding-top:2px; padding-left:2px}');
	    var val = k.charAt(0).toUpperCase() + k.slice(1);
	    val = val.replace('Filter', '') + ': ' + inner[v].replace('*', ',');
	    val = decode(val);

	    // some filters use camelcase; need to insert a space for them

	    val = val.replace('AssayType', 'Assay Type');
	    val = val.replace('TheilerStage', 'TS');
	    val = val.replace('Wildtype:', '');
	    val = val.replace('Detected', 'Detected?');

	    setText(el, val);

	    filterList.appendChild(el);
	    YAHOO.util.Event.addListener(el, 'click', clearFilter);

	    filterList.appendChild(document.createTextNode(' '));
	}

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
};

var isFilterable = function(fieldname) {
    if (fieldname.indexOf("Filter") >= 0) {
	return true;
    }
    return false;
};
