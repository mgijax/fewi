var globalPaginator = null;	// paginator object for SNP summary page
var tableorder = null;		// ordering of columns in summary table
var handleNavigation = null;	// function for dealing with browser history
var hideStrains = true;		// hide strains with no allele calls?
var prevColumnOrder = [];	// ordering of column IDs for last table
var logging = true;

// Returns a request string for consumption by the DataSource
var generateRequest = function(startIndex,sortKey,dir,results) {
	startIndex = startIndex || 0;
	sortKey   = sortKey || "accid";
	dir   = (dir) ? dir.substring(7) : "asc"; // Converts from DataTable format "yui-dt-[dir]" to server value "[dir]"
	results   = results || 100;
	return "results="+results+"&startIndex="+startIndex+"&sort="+sortKey+"&dir="+dir;
};

// write log message 's' to the console log
var log = function(s) {
	if (logging) {
		while (s.indexOf('\n') >= 0) {
			s = s.replace('\n', '');
		}
		console.log('snp_summary.js : ' + s);
	}
}

// true if list x contains item y, false if not
var contains = function(x, y) {
	if (x.indexOf(y) >= 0) { return true; }
	return false;
}

// removes item y from list x (if it exists) and returns the new list.
// returns the original list if y is not in x.
var remove = function(x, y) {
	var pos = x.indexOf(y);
	if (pos >= 0) {
		var left = x.slice(0, pos);
		var right = x.slice(pos + 1);
		return left.concat(right);
	}
	return x;
}

// re-order items from list y using list x as a base.  (Walk through x; for
// each item in y, add them first, then add the rest.)  Does not alter x or y.
var reorderBy = function(x, y) {
	log('reorder: ' + y);
	log('using: ' + x);
	var z = [];
	for (var ix in x) {
		if (contains(y, x[ix])) {
			z.push(x[ix]);
		}
	}
	for (var iy in y) {
		if (!contains(z, y[iy])) {
			z.push(y[iy]);
		}
	}
	log('ordered: ' + z);
	return z;
}

// get the ordered list of column IDs for the current SNP summary table
var getColumnIDs = function() {
	var ids = [];
	$("#snpSummaryTable th").each(function(i) {
		if ((this.id != '') && (this.id !== undefined)) {
			ids.push(this.id);
		}
	});
	log('columns: ' + ids);
	return ids;
}

var buildOrderMap = function(ids) {
	var map = {};
	for (var i in ids) {
		map[ids[i]] = i;
		//map[ids[i]] = String(i);
	}
	return map;
}

var storeColumnIDs = function(table) {
	var ids = getColumnIDs();
	reorderBy(prevColumnOrder, ids);
	prevColumnOrder = ids;
	var orderMap = buildOrderMap(prevColumnOrder);
	var i = 0;
	for (var id in orderMap) {
		if (id != '') {
			table.sortOrder[id] = i;
			i++;
		}
	}
	tableorder = JSON.stringify(table.sortOrder);
	log('new order: ' + prevColumnOrder);
	log('tableorder: ' + tableorder);
	return;
}

/*var reorderTable = function(table) {
	log('reorderTable() beginning');
	var ids = getColumnIDs();
	reorderBy(prevColumnOrder, ids);
	table.sortOrder = buildOrderMap(ids);
	log('reorderTable() finishing');
	return table.sortOrder;
}
*/
// setup function for the page
function main() {
	var Dom = YAHOO.util.Dom;
	var Lang = YAHOO.lang;
	var History = YAHOO.util.History;

	// Create the Paginator
	var myPaginator = new YAHOO.widget.Paginator({
		template : "{FirstPageLink} {PreviousPageLink}<strong>{PageLinks}</strong> {NextPageLink} {LastPageLink} <span style=align:right;>{RowsPerPageDropdown}</span><br/>{CurrentPageReport}",
		pageReportTemplate : "Showing SNP(s) {startRecord} - {endRecord} of {totalRecords}",
		rowsPerPageOptions : [50, 100, 250, 500, 1000],
		containers   : ["paginationTop", "paginationBottom"],
		rowsPerPage : 100,
		pageLinks: 5,
		recordOffset: 1
	});
	myPaginator.render();
	globalPaginator = myPaginator;

	// set up the draggable columns for the table
	var initDraggableColumns = function(tableName) {
		$(tableName).dragtable({
			persistState: function(table) { storeColumnIDs(table); },
			restoreState: eval('(' + tableorder + ')')
			//restoreState: function(table) { return reorderTable(table); }
/*			persistState: function(table) {
				table.el.find('th').each(function(i) {
					if(this.id != '') {
						table.sortOrder[this.id]=i;
					}
				});
				tableorder = 
					JSON.stringify(table.sortOrder);
			},
			restoreState: eval('(' + tableorder + ')')
*/
		}); 
	};

	// update the snpDataTable
	var updateSnpTable = function(state) {
		$("#snpSummaryDiv").html("<img src='" + fewiurl + "assets/images/loading.gif' height='24' width='24'> Searching...");
		$.ajax({
			url: fewiurl + "snp/table?" + getQuerystring() + "&" + state,
			datatype : "html",
			success: function(html) {

				// break the textblock into an array of lines
				var lines = html.split('\n');
				var count = lines[0];
				myPaginator.setTotalRecords(count);
				// remove one line, starting at the first position
				lines.splice(0,1);
				// join the array back into a single string
				var newtext = lines.join('\n');

				$("#snpSummaryDiv").html(newtext);
				$("#snpSummaryTable tr:last td").css({"border-bottom" : "thin solid gray"});
				initDraggableColumns("#snpSummaryTable");
			}
		});
	};

	// Define a custom function to route pagination through the Browser History Manager
	var handlePagination = function(state) {
		// The next state will reflect the new pagination values
		// while preserving existing sort values
		// Note that the sort direction needs to be converted from DataTable format to server value
		var newState = generateRequest(state.recordOffset, 0, 0, state.rowsPerPage);

		myPaginator.setState(state);
		// Pass the state along to the Browser History Manager
		History.navigate("myDataTable", newState);
	};

	// ...then we hook up our custom function
	//myPaginator.subscribe("changeRequest", handlePagination, myDataTable, true);
	myPaginator.subscribe("changeRequest", handlePagination);

	// Called by Browser History Manager to trigger a new state
	var handleHistoryNavigation = function (request) {
		updateSnpTable(request);
	};
	handleNavigation = handleHistoryNavigation;

	// Calculate the first request
	var initialRequest = History.getBookmarkedState("myDataTable") || generateRequest();

	// Register the module
	History.register("myDataTable", initialRequest, handleHistoryNavigation);

	// Render the first view
	History.onReady(function() {
		// Current state after BHM is initialized is the source of truth for what state to render
		var currentState = History.getCurrentState("myDataTable");
		handleHistoryNavigation(currentState);
	});

	// Initialize the Browser History Manager.
	YAHOO.util.History.initialize("yui-history-field", "yui-history-iframe");
};
main();

function updatePaginatorCount() {
	// get count of records for paginator
	$.ajax({
		url: fewiurl + "snp/count?" + getQuerystring(),
		dataType: "html",
		success: function(ct) {
			globalPaginator.setTotalRecords(ct);
		}
	})
}

function updateRequest() {
	filters.populateFilterSummary();
	updatePaginatorCount();
	resetTableOrder();
	handleNavigation(generateRequest());
}

function parseRequest(request){
	var reply = [];
	var kvPairs = request.split('&');
	for (pair in kvPairs) {
		var kv = kvPairs[pair].split('=');
		reply[kv[0]] = kv[1];
	}
	return reply;
}

function getHideStrains() {
	return hideStrains;
}

function setHideStrains(hs) {
	hideStrains = hs;
}

function resetTableOrder() {
	tableorder = null;
}
