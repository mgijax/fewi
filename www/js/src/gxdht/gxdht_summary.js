/* Name: gxdht_summary.js
 * Purpose: supports the high-throughput expression summary display
 */
 
/*** logging support for debugging ***/

// assumes gxdht_query.js is also loaded, which includes a log(msg) function

/*** module-level variables ***/

/*** functions ***/

var instantiatedPaginator = false;
var fewiUrl = null;

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
	
	log("entered updateResultsDiv()");
	$("#resultSummary").html("<img src='" + fewiurl + "assets/images/loading.gif' height='24' width='24'> Searching...");
	log("added searching message");
	$.ajax({
		url: fewiurl + "gxdht/table?" + querystring,	// can take state as param and append here for pagination
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
				var sw = "#row" + i + "samplesWrapper";
				var vw = "#row" + i + "variablesWrapper";
				var tw = "#row" + i + "typeWrapper";
				var mw = "#row" + i + "methodWrapper";
				var sp = "#row" + i + "spacer";
				
				// synchronize heights of experiment info columns to match the tallest, ensuring uniform right border height
				if ((sw != null) && (vw != null) && (tw != null) && (mw != null) && (sp != null)) {
					var height = Math.max($(sw).height(), $(vw).height(), $(tw).height(), $(mw).height());
					$(sw).height(height);
					$(vw).height(height);
					$(tw).height(height);
					$(mw).height(height);
					$(sp).height(height);
				}
				
				// adjust height of title div to ensure right border is tall enough
				var iw = '#row' + i + 'idWrapper';
				var ew = '#row' + i + 'title';
				
				if ($(iw).height() > $(ew).height()) {
					$(ew).height($(iw).height());
				}
				
				$('#row' + i + 'sampleCount').addClass('blue');
			}
			log("updated heights");
			
			updatePageReport(start, end, totalCount, "experiment");
			updatePaginator(totalCount, null, updatePaginationParameters);
			log("updated paginator");
			
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
    var sampleUrl = fewiUrl + 'gxdht/samples/' + experimentID + '?' + querystring;
    window.open(sampleUrl,'sampleWindow-' + experimentID,'width=1000,height=400,resizable=yes,scrollbars=yes,alwaysRaised=yes');
};

// initialize this module by passing in the FEWI URL (so the gs_samplePopup function will work)
var gs_setFewiUrl = function(url) {
	fewiUrl = url;
};

/*** to execute on being loaded ***/

updateAgeStageTab();
log("loaded gxdht_summary.js");