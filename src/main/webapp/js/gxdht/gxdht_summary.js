/* Name: gxdht_summary.js
 * Purpose: supports the high-throughput expression summary display
 */
 
/*** logging support for debugging ***/

// assumes gxdht_query.js is also loaded, which includes a log(msg) function

/*** module-level variables ***/

/*** functions ***/

var instantiatedPaginator = false;
var fewiUrl = null;

var decode = function(s) {
	return decodeURIComponent(s).replace(/[^A-Za-z0-9:_ ]/, ' ').replace(/\+/g, ' ');
};

// translate the float ages to have a prefixed 'E'
var translateAges = function(ageList) {
	var translated = [];
	for (var i = 0; i < ageList.length; i++) {
		if (ageList[i].match(/[0-9]+\.?[0-9]*/) != null) {
			translated.push('E' + ageList[i]);
		} else {
			translated.push(ageList[i]);
		}
	}
	return translated;
};

// update the You Searched For text (in the searchSummary div)
var updateYouSearchedFor = function() {
	if (querystring == null) { return; }
	
	var ysf = "<b>You searched for:</b><br/>";
	var params = getParameterMap();
	
	if (params.hasOwnProperty('structure')) {
		ysf = ysf + '<b>Assayed</b> in <b>' + decode(params['structure'][0]) + '</b>';
		ysf = ysf + ' <span class="smallGrey">includes synonyms &amp; substructures</span><br/>';
	} else {
		ysf = ysf + '<b>Assayed</b> in <b>any structures</b><br/>';
	}
	if (params.hasOwnProperty('theilerStage') && (JSON.stringify(params['theilerStage']) != JSON.stringify(['0']))) {
		ysf = ysf + 'at developmental stage(s): <b>(TS:' + params['theilerStage'].join(')</b> or <b>(TS:') + ')</b><br/>';
	} else if (params.hasOwnProperty('age') && (JSON.stringify(params['age']) != JSON.stringify(['ANY']))) {
		var ages = translateAges(params['age']);
		ysf = ysf + 'at age(s): <b>(' + ages.join(')</b> or <b>(') + ')</b><br/>';
	} else {
		ysf = ysf + 'at developmental stage(s): <b>Any</b><br/>';
	}
	if (params.hasOwnProperty('sex') && (params['sex'][0] != 'All')) {
		ysf = ysf + 'Sex: <b>' + params['sex'][0] + '</b><br/>';
	}
	if (params.hasOwnProperty('strain') && (params['strain'].length > 0)) {
		ysf = ysf + 'Strain: <b>' + decodeURIComponent(params['strain']) + '</b><br/>';
	}
	if (params.hasOwnProperty('mutatedIn')) {
		ysf = ysf + 'Samples <b>mutated in ' + decode(params['mutatedIn'][0]) + '</b> ';
		ysf = ysf + ' <span class="smallGrey">current symbol, synonyms, gene id</span><br/>';
	}
	if (params.hasOwnProperty('mutantAlleleId')) {
		ysf = ysf + 'Samples <b>carrying mutant allele ' + decode(params['mutantAlleleId'][0]) + '</b> ';
	}
	if (params.hasOwnProperty('method') && params['method'].length > 0 && params['method'].length < 5) {
		var otherTerms = []
		var rnaseqTerms = []
		seenRNA = false
		params['method'].forEach(m => {
		    m = decode(m)
		    if (m.includes('RNA')) {
			seenRNA = true
			i = m.indexOf("RNA")
			if (i > 0) {
			    rnaseqTerms.push(m.substring(0, i-1))
			}
		    } else {
		        otherTerms.push(m)
		    }
		})
		var rString = ""
		if (seenRNA) {
		    rString = "RNA-seq"
		    if (rnaseqTerms.length > 0) {
		        rString = rString + ' (' + rnaseqTerms.join(', ') + ')'
		    }
		}
		if (rString.length > 0) otherTerms.push(rString);
		ysf = ysf + 'Assayed by <b>(' + otherTerms.join(', ') + ')</b><br/>';
	} else {
		ysf = ysf + 'Assayed by <b>(ANY method)</b><br/>';
	}
	if (params.hasOwnProperty('arrayExpressID')) {
		ysf = ysf + 'ArrayExpress or GEO ID <b>' + params['arrayExpressID'][0].trim() + '</b><br/>';
	}
	if (params.hasOwnProperty('text')) {
		ysf = ysf + 'Text ';
		if (params.hasOwnProperty('textScope')) {
			ysf = ysf + 'in ';
			if (params['textScope'].indexOf('Title') >= 0) {
				ysf = ysf + 'Title';
			}
			if (params['textScope'].indexOf('Description') >= 0) {
				if (ysf.endsWith('Title')) {
					ysf = ysf + ' or Description';
				} else {
					ysf = ysf + 'Description';
				}
			}
			ysf = ysf + ': <b>' + decode(params['text'][0]) + '</b><br/>';
		}
	}
	$('#searchSummary').html(ysf);
};

// update the pagination info in the querystring
var updatePaginationParameters = function(page, rowsPerPage) {
	if (querystring == null) { return; }
	var startIndex = (page - 1) * rowsPerPage;

	if (querystring.indexOf('startIndex') < 0) {
		querystring = querystring + '&startIndex=' + startIndex
	} else {
		querystring = querystring.replace(/startIndex=[0-9]+/, 'startIndex=' + startIndex);
	}
	
	if (querystring.indexOf('results') < 0) {
		querystring = querystring + '&results=' + rowsPerPage;
	} else {
		querystring = querystring.replace(/results=[0-9]+/, 'results=' + rowsPerPage);
	}
	updateResultsDiv(startIndex, rowsPerPage);
};

// get the set of input parameters as a mapping from fieldname to value(s)
var getParameterMap = function() {
	if ((typeof querystring == 'undefined') || (querystring == null) || (querystring == '')) { return {}; }
	
	var map = {}
	var pairs = querystring.split('&');
	for (var i = 0; i < pairs.length; i++) {
		var pair = pairs[i].split('=');
		if (pair.length == 2) {
			var value = pair[1];
			if (value.length > 0) {
				var name = pair[0];
				if (map.hasOwnProperty(name)) {
					map[name].push(value);
				} else {
					map[name] = [ value ];
				}
			}
		}
	}
	return map;
};

// scroll the select list with the given ID (include #) until it has the first selected option on top
var scrollToSelected = function(id) {
	var $s = $(id);
	if ($s != null) {
		var optionSelected = $s.find("[selected=selected]");
		if (optionSelected != null) {
			$s.scrollTop($s.scrollTop() + (optionSelected.offset().top - $s.offset().top));
		}
	}
};

// update whether stage or age tab is selected, based on query parameters
var updateAgeStageTab = function() {
	var parameters = getParameterMap();
	if (parameters.hasOwnProperty('theilerStage') && (parameters['theilerStage'].indexOf('0') == -1)) {
		selectTheilerStage();
		scrollToSelected("#theilerStage");
	} else if (parameters.hasOwnProperty('age') && (parameters['age'].indexOf('ANY') == -1)) {
		selectAge();
		scrollToSelected("#age");
	} else {
		selectAge();
	}
};

// look up the HTML elements identified by the given 'ids' and set the height of each to the
// maximum height across them all.  minPx is optional and specifies minimum height.
var standardizeHeights = function(ids, minPx) {
	var maxHeight = 0;
	if (minPx != null) { maxHeight = minPx; }
	
	for (var i = 0; i < ids.length; i++) {
		maxHeight = Math.max(maxHeight, $('#' + ids[i]).height());
	}
	for (var i = 0; i < ids.length; i++) {
		$('#' + ids[i]).height(maxHeight);
	}
};

// update the results div
var updateResultsDiv = function(startIndex, rowsPerPage) {
	updateAgeStageTab();
	
	var xPos = window.scrollX;		// remember page position, so we can return to it
	var yPos = window.scrollY;
	
	var top = $('#paginationTop').position().top;
	var bottom = $('#paginationBottom').position().top;
	
	// if we've scrolled closer to the bottom pagination than the top, assume the user clicked the bottom set and
	// go back to the top
	if (Math.abs(top - yPos) > Math.abs(bottom - yPos)) {
		yPos = $('#resultbar').position().top;
	}
	
	var urlFragment = filters.getUrlFragment();
	if (urlFragment == null) {
		urlFragment = "";
	}
	
	log("entered updateResultsDiv()");
	$("#resultSummary").html("<img src='" + fewiurl + "assets/images/loading.gif' height='24' width='24'> Searching...");
	log("added searching message");
	$.ajax({
		url: fewiurl + "gxd/htexp_index/table?" + querystring + urlFragment,	// can take state as param and append here for pagination
		datatype : "html",
		success: function(data) {
			log("successful response");

			// need to pull the initial integer count off the front of the data
			var lines = data.split('\n');
			var count = parseInt(lines[0]);
			var totalCount = parseInt(lines[1]);
			
			if ((count == 0) || (totalCount == 0)) {
				$("#resultSummary").html("No experiments meet your search criteria.");
				updatePageReport(null, null, null, "experiment");
				updatePaginator(totalCount, null, updatePaginationParameters);
				updateYouSearchedFor();
				return;
			}
			
			if (count < rowsPerPage) { rowsPerPage = count; }
			var start = startIndex + 1;
			var end = startIndex + rowsPerPage;

			// remove first two lines, then merge back into single string
			lines.splice(0,2);
			var newtext = lines.join('\n');

			$("#resultSummary").html(newtext);
			log("updated div on page");
			
			for (var i = 0; i < count; i++) {
				standardizeHeights([ 'row' + i + 'detailLabel', 'row' + i + 'samplesWrapper', 
					'row' + i + 'variablesWrapper', 'row' + i + 'typeWrapper', 'row' + i + 'methodWrapper',
					'row' + i + 'spacer', 'row' + i + 'linkWrapper', 'row' + i + 'pmWrapper' ], 50);
				
				standardizeHeights([ 'row' + i + 'title', 'row' + i + 'titleLabel']);
				standardizeHeights([ 'row' + i + 'description', 'row' + i + 'descriptionTitle' ]);
				standardizeHeights([ 'row' + i + 'note', 'row' + i + 'noteTitle' ]);
				
				$('#row' + i + 'sampleCount').addClass('blue');
			}
			log("updated heights");
			
			updatePageReport(start, end, totalCount, "experiment");
			updatePaginator(totalCount, null, updatePaginationParameters);
			updateYouSearchedFor();
			
			window.scrollTo(xPos, yPos);
		}
	});
};

// update divs with the pageReport class to show a message about which items are displayed currently
var updatePageReport = function(start, end, totalCount, item) {
	if ((start == null) || (end == null) || (totalCount == null) || (item == null)) {
		$(".pageReport").html("");
		return;
	}

	var message = "Showing ";
	if (start == 0) {
		message = message + item + "s 0 of " + totalCount;
	} else if (start == end) {
		message = message + item + " " + start + " of " + totalCount;
	} else if (start < end) {
		message = message + item + "s " + start + " - " + end + " of " + totalCount;
	} else {
		message = "Searching...";
	}
	$(".pageReport").html(message);
};

// update the paginator; assume we will replace the contents of any HTML
// elements (presumably DIVs) that have a "paginator" class with an 
// updated paginator
var updatePaginator = function(totalCount, pageLimit, callback) {
	if (instantiatedPaginator) { return; }
	instantiatedPaginator = true;
	
	if (totalCount == 0) {
		$(".paginator").html("");
		return;
	}
	
	log('instantiating paginator with ' + totalCount + ' rows');
	if (totalCount == null) {
		alert("You cannot have a paginator with a null totalCount.");
	}
	if (pageLimit == null) { pageLimit = 50; }
	if (callback == null) {
		callback = function(page) {
			alert("You forgot to define a callback for the paginator (page " + page + ")");
		}
	}
	
	$(".paginator").paging(totalCount, {
        format: '[< nncnn >]',		// first, prev, five page numbers with current in middle, next, last
        perpage: pageLimit,
        onSelect: function(page) {
            callback(page, pageLimit);
        },
        onFormat: function(type) {
            switch (type) {
                case 'block': // n and c
                   	if (this.active) {
	                   	if (this.value != this.page) return '<a href="#">' + this.value + '</a> ';
	                   	else return '<span class="current">' + this.value + '</span> ';
                   	}
	                return '<span class="disabled">' + this.value + '</span> ';
                case 'next': // >
                    if (this.active) return '<a href="#">next&gt;</a> ';
	                return '<span class="disabled">next&gt;</span> ';
                case 'prev': // <
                	if (this.active) return '<a href="#">&lt;prev</a> ';
	                return '<span class="disabled">&lt;prev</span> ';
                case 'first': // [
                    if (this.active) return '<a href="#">&lt;&lt;first</a> ';
	                return '<span class="disabled">&lt;&lt;first</span> ';
                case 'last': // ]
                    if (this.active) return '<a href="#">last&gt;&gt;</a>';
	                return '<span class="disabled">last&gt;&gt;</span> ';
            }
        }
    });
};

// execute a search and update the page
var gs_search = function() {
	if ((typeof querystring != 'undefined') && querystring != '') {
		log("search with querystring: " + querystring);
		updateResultsDiv();
	} else {
		log("no querystring, no search");
	}
};

// open a sample popup window
var gs_samplePopup = function(experimentID) {
	if (fewiUrl == null) {
		alert("Need to call gs_setFewiUrl to initialize gxdht_summary.js module");
		return;
	}
    var sampleUrl = fewiUrl + 'gxd/htexp_index/samples/' + experimentID + '?' + querystring;
    ga_logEvent('GXD RNA-Seq and Microarray Experiment Summary', 'Clicked to View Sample Data');
    window.open(sampleUrl,'sampleWindow-' + experimentID,'width=1000,height=400,resizable=yes,scrollbars=yes,alwaysRaised=yes');
};

// initialize this module by passing in the FEWI URL (so the gs_samplePopup function will work)
var gs_setFewiUrl = function(url) {
	fewiUrl = url;
};

// update the request & data on the page (after a filtering event)
var gs_updateRequest = function() {
	filters.populateFilterSummary();
	instantiatedPaginator = false;
	updateResultsDiv(0, 50);
}

// callback function: should be wired into the filters.js module as a callback.  This will track which filters are
// applied and log a GA event when a new one has been added.
var previousFilters = {};
var gs_logFilters = function() {
	var newFilters = filters.urlToHash(filters.getUrlFragment().substring(1));
	
	for (var field in newFilters) {
		if (!(field in previousFilters)) {
			ga_logEvent("GXD RNA-Seq and Microarray Experiment Search: added filter", field);
		}
	}
	previousFilters = newFilters; 
}

/*** to execute on being loaded ***/

updateAgeStageTab();
log("loaded gxdht_summary.js");
