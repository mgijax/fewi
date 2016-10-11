/* Name: gxdht_summary.js
 * Purpose: supports the high-throughput expression summary display
 */
 
/*** logging support for debugging ***/

// assumes gxdht_query.js is also loaded, which includes a log(msg) function

/*** module-level variables ***/

/*** functions ***/

// update the results div
var updateResultsDiv = function() {
	log("entered updateResultsDiv()");
	$("#resultSummary").html("<img src='" + fewiurl + "assets/images/loading.gif' height='24' width='24'> Searching...");
	log("added searching message");
	$.ajax({
		url: fewiurl + "gxdht/json?" + querystring,	// can take state as param and append here for pagination
		datatype : "json",
		success: function(jsonData) {
			// convert the data from JSON to Javascript objects
			log("successful response");

			var newtext = "Found " + jsonData['totalCount'] + " experiments with matching samples";
			newtext = newtext + '<br/>First ' + jsonData['summaryRows'].length + ' experiment ID(s):';
			
			for (i in jsonData['summaryRows']) {
				var expt = jsonData['summaryRows'][i];
				newtext = newtext + '<br/>' + expt['arrayExpressID'];
			}
			
			$("#resultSummary").html(newtext);
			log("updated div on page");
		}
	});
};

var gs_search = function() {
	if ((typeof querystring != 'undefined') && querystring != '') {
		log("search with querystring: " + querystring);
		updateResultsDiv();
	} else {
		log("no querystring, no search");
	}
};


/*** to execute on being loaded ***/

log("loaded gxdht_summary.js");