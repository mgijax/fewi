/*** Javascript for shared vocabulary browser ***/

var logging = true;
var initialID = null;
var populatedTree = false;
var previousSelectedNodeIDs = []; 

/* write the given log message to the browser console, if logging is currently enabled
 */
var log = function(msg) {
    if (logging) { console.log(msg); }
}

/* Send a pageview for the current URL to Google Analytics.  If fail, fail silently.
 */
var logPageview = function() {
	try {
		var pieces = window.location.href.split('.org');
		if (pieces.length == 2) {
			ga_logPageview(pieces[1]);
			log('GA pageview: ' + pieces[1]);
		}
	} catch (err) {}
}

/* set the initial term ID for when the browser was brought up
 */
var setInitialTermID = function(id) {
	initialID = id;
};
	
/* make an Ajax request to the server to get the contents of the term pane, then add it to the page
 */
var fetchTermPane = function(id) {
	$("#detail").html("<img src='" + fewiurl + "assets/images/loading.gif' height='24' width='24'> Loading...");
	$.ajax({
		url: termPaneUrl + id,
		datatype : "html",
		success: function(htmlText) {
				$("#detail").html(htmlText);
				setTimeout(resizePanes, 150);
			}
		});
};

/* make an Ajax request to the server to get the contents of the search pane, then add it to the page
 */
var fetchSearchPane = function(searchTerm) {
	$("#searchPane").html("<img src='" + fewiurl + "assets/images/loading.gif' height='24' width='24'> Loading...");
	if (searchTerm == null) {
		searchTerm = "";
	}
	$.ajax({
		url: searchPaneUrl + searchTerm,
		datatype : "html",
		success: function(htmlText) {
				$("#searchPane").html(htmlText);
				var topID = $('#searchResults a:first').attr('onclick').split("'")[1];
				searchResultClick(topID);
			}
		});
};

/* submit the value in the search box to the server as a search string, get and display the set of results
 */
var refreshSearchPane = function() {
	// if the user has not chosen a selection from the autocomplete, pick the first one
	var searchTerm = $('#searchTerm').val();
	fetchSearchPane(searchTerm);
};

/* clear the search box and the set of results
 */
var resetSearch = function() {
	$("#searchTerm").val("");
	refreshSearchPane();
};

/* update the title for this tab in the browser
 */
var setBrowserTitle = function(pageTitle) {
    document.title = pageTitle;
}

/* update the panes that need to be updated when the user clicks on a result in the search pane
 */
var searchResultClick = function(id, logPV) {
	if ((id !== null) && (id !== undefined)) {
		fetchTermPane(id);
   		setBrowserTitle(id);
   		initializeTreeView(id);
   		try {
        	window.history.pushState(id, 'title', browserUrl + id);
    	} catch (err) {}
    	if ((logPV == undefined) || (logPV == null) || logPV) {
    		logPageview();
    	}
    }
};
 
/* update the panes that need to be updated when the user clicks on a parent in the term detail pane
 */
var parentClick = function(id) {
	fetchTermPane(id);
    setBrowserTitle(id);
    initializeTreeView(id);
    try {
        window.history.pushState(id, 'title', browserUrl + id);
    } catch (err) {}
	logPageview();
};

/* update the panes that need to be updated when the user clicks on a term in the tree view pane
 */
var treeTermClick = function(id) {
	fetchTermPane(id);
    setBrowserTitle(id);
    try {
        window.history.pushState(id, 'title', browserUrl + id);
    } catch (err) {}
	logPageview();
};

/* add an event listener to enable reasonable results when stepping back through browser history
 */
window.addEventListener('popstate', function(e) {
	var id = e.state;
	if (id != null) {
		fetchTermPane(id);
		setBrowserTitle(id);
		initializeTreeView(id);
	} else {
		fetchTermPane(initialID);
		setBrowserTitle(initialID);
		initializeTreeView(initialID);
	}
});

/* scroll the selected term to the center of the tree view pane, if it's not currently visible;
 * returns true if done, false if the right nodes aren't in the tree yet.
 */
var scrollTreeView = function() {
		var selectedIDs = $('#treeViewDiv').jstree().get_selected();
		log('scrollTreeView: found ' + selectedIDs.length + ' selectedIDs');
		if (selectedIDs.length >= 1) {
			var nodeRectangle = $('#' + selectedIDs[0])[0].getBoundingClientRect();
			var divRectangle = $('#treeViewDiv')[0].getBoundingClientRect();

			if (nodeRectangle.bottom > divRectangle.bottom) {
			    $('#treeViewDiv')[0].scrollTop = nodeRectangle.top - divRectangle.top
			    	- 0.5 * (divRectangle.bottom - divRectangle.top);
			}
		}
		selectSimilarNodes();
};

/* clear any exiting tree view and display one for the given id, if specified
 */
var initializeTreeView = function(id) {
	log("entered initializeTreeView(" + id + ")");
	if (populatedTree) {
		$.jstree.destroy();
		$('#treeViewDiv').html('');
		previousSelectedNodeIDs = []; 
	}
	populatedTree = false;
	if (id !== null) {
		buildTree(id);
	}
};

/* initialize the tree view pane for the browser
 */
var buildTree = function(id) {
	log("entered buildTree(" + id + ")");
	var config = {
		'core' : {
			'data' : {
				'dataType' : 'json',
				'data' : function(node) {
					if ((node != null) && (node.data != null)) {
						return { 'id' : node.data.accID, 'nodeID' : node.id, 'edgeType' : node.data.edgeType };
					} else {
						return { 'id' : id };
					}
				},
				'url' : function(node) {
					if (populatedTree) {
						return treeChildrenUrl;
					} else {
						populatedTree = true;
						return treeInitialUrl;
					}
				}
			}
		}
	};
	
	/* Not all the events documented for jsTree actually fire...  It seems these are what we can mostly rely on:
	 *   1. change - fires when we expand a branch, click a node's text, or click a triangle to collapse a node.
	 *   2. select_node (or activate_node) - fires when we click on the text of a node.
	 *   3. close_node - fires when we click on a triangle to close a node.
	 * So, strategy-wise...
	 *   1. use 'change' to look for expansion of a node, but suppress the selection of the node itself.
	 *   2. use 'select_node' to select the node itself, display its annotation info, and hide the previous
	 *		selection's annotation info.
	 */
	$('#treeViewDiv').jstree(config);
	$('#treeViewDiv').on('changed.jstree', onChange);
	$('#treeViewDiv').on('select_node.jstree', onSelectNode);
	$('#treeViewDiv').on('ready.jstree', function() {
		// adding a little time before the scrolling seems to help some browsers (IE, possibly Mac browsers)
		setTimeout(scrollTreeView, 250); 
		} );
	addTooltips();
	log("initialized jstree");
};

/* handle a 'change' event from jsTree.  This event fires when the user clicks a triangle to expand a node's
 * children, clicks a node's text to select the node, or clicks a triangle to collapse a node's children.  We
 * do not deal with the selection of the node itself here, as we use the select_node event for that.
 */
var onChange = function(node, action, selected, event) {
	if (suppressSelectHandler) { return; }

	log('onChange');
	if (previousSelectedNodeIDs.length == 0) {
		previousSelectedNodeIDs = $('#treeViewDiv').jstree().get_selected();
		return;		// if nothing previously selected, don't deselect anything (tree is building initial state)
	}
		
	var newSelected = $('#treeViewDiv').jstree().get_selected();
	for (var i = 0; i < newSelected.length; i++) {
		if (previousSelectedNodeIDs.indexOf(newSelected[i]) == -1) {
			$('#treeViewDiv').jstree().deselect_node(newSelected[i]);
		}
	}
	selectSimilarNodes();
	addTooltips();
};

var suppressSelectHandler = false;

/* handle an 'select_node' event from jsTree.  (select the node, hide any prior annotations link, show the
 * annotations link for this node)
 */
var onSelectNode = function(node, event) {
	/* We want to prevent the selection of multiple nodes, so we need to deselect any previously
	 * selected nodes.
	 */
	if (suppressSelectHandler) { return; }

	log('onSelect');
	var tree = $('#treeViewDiv').jstree();
	var selectedNodeIDs = tree.get_selected();
	if (selectedNodeIDs.length > 1) {
		// if multiple nodes selected, need to just keep the latest one
		for (var i = 0; i < previousSelectedNodeIDs.length; i++) {
			tree.deselect_node(previousSelectedNodeIDs[i]);
		}
	}
	previousSelectedNodeIDs = tree.get_selected();
	
	/* ensure that this node is open -- if this is not an automatic selection */
	var node = tree.get_node(previousSelectedNodeIDs[0]);
	if (!suppressSelectHandler) {
		if (!tree.is_open(node)) {
			tree.open_node(node);
		}
	}
	 
	/* update the term pane */
	treeTermClick(node.data.accID);
	
	/* and select all other nodes with the same accession ID */
	selectSimilarNodes();
};

/* return a list that is the distinct set of items from 'arr' (from stackoverflow)
 */
var uniqueList = function(arr) {
	var u = {}, a = [];
    for(var i = 0, l = arr.length; i < l; ++i){
        if(!u.hasOwnProperty(arr[i])) {
            a.push(arr[i]);
            u[arr[i]] = 1;
        }
    }
    return a;
};

/* get a list of all the node IDs that are currently in the tree, except the starter ID
 */
var getAllOtherNodeIDs = function(starterID) {
	var tree = $('#treeViewDiv').jstree();
	var toDo = tree.get_path(starterID, null, true);	// list of node IDs from root to starterID
	var done = [];										// list of node IDs we've already considered
	var allNodes = [];									// list of all nodes found so far
	
	while (toDo.length > 0) {
		var id = toDo.pop();
		if (done.indexOf(id) < 0) {
			if (id != starterID) {
				allNodes.push(id);
			}
			var children = tree.get_children_dom(id);
			for (var i = 0; i < children.length; i++) {
				toDo.push(children[i].id);
			}
		}
	}
	return uniqueList(allNodes);
};

/* remove any existing annotation links and add new annotation links for the selected nodes
 */
var addAnnotationLinks = function() {
	log('addAnnotationLinks');
	$('.annotationLink').remove();			// remove any existing annotation links
	var nodes = $('#treeViewDiv').jstree().get_selected(true);
	for (var i = 0; i < nodes.length; i++) {
		var node = nodes[i];
		$('#' + node.id + '_annotations').remove();
		
		/* We can have a label without a URL for nodes with zero counts.  In that case, do not
		 * make the link, just show the count.
		 */
		if (node.data.annotationLabel != null) {
			var linkText = '<span id="' + node.id + '_annotations" class="annotationLink"> ('
				+ '<a href="' + fewiurl + node.data.annotationUrl + '" target="_blank">'
				+ node.data.annotationLabel
				+ "</a>)</span>";
			if (node.data.annotationUrl == null) {
				linkText = '<span id="' + node.id + '_annotations" class="annotationLink"> ('
					+ node.data.annotationLabel + ")</span>";
			}
			var nodeLink = $('#' + node.id + '_anchor');
			if (nodeLink != null) {
				nodeLink.after(linkText);
			}
		}
	} 
};

/* Assuming there are no cycles, we should not find a parent with the same ID below the currently selected node.
 * So, we go to the currently selected node, get its accession ID, go up through its ancestors and down into their
 * children, trying to find and select those that have a matching accession ID.
 */ 
var selectSimilarNodes = function() {
	log('selectSimilarNodes');
	var tree = $('#treeViewDiv').jstree();
	previousSelectedNodeIDs = tree.get_selected();
	
	// If we don't have at least one selected node upon entry to this function, bail out.
	if (previousSelectedNodeIDs.length < 1) {
		return;
	}
	
	var selectedNode = tree.get_node(previousSelectedNodeIDs[0]);
	if ((selectedNode === undefined) || (selectedNode === null)) {
		// can't find the selected node, so bail out
		return;
	}
	
	var accID = selectedNode.data.accID;
	var otherNodeIDs = getAllOtherNodeIDs(previousSelectedNodeIDs[0]);
	
	suppressSelectHandler = true;
	for (var i = 0; i < otherNodeIDs.length; i++) {
		var node = tree.get_node(otherNodeIDs[i]);
		if (node.data.accID == accID) {
			tree.select_node(node);
		}
	}
	previousSelectedNodeIDs = $('#treeViewDiv').jstree().get_selected();
	addAnnotationLinks();
	suppressSelectHandler = false;
};

/* adjust the size of the panes (search, term detail, tree) to fit the current browser dimensions.
 */
var resizePanes = function() {
	log('resizePanes');
	var vWidth = $(window).width();					// width the browser's viewport
	
	var detailContainer = $('#detailContainer');	// shortcuts for the three pane containers
	var treeContainer = $('#treeViewContainer');
	var searchContainer = $('#searchContainer');
	
    var treeMainTitle = $('#treeMainTitle');		// shortcuts for the three pane titles
    var searchTitle = $('#searchTitle');
    var detailTitle = $('#detailTitle');

    var treeViewDiv = $('#treeViewDiv');			// shortcuts for the dynamically-populated DIVs in the panes
    var searchPane = $('#searchPane');
    var detail = $('#detail');

    // left pane is for search and takes 1/3 of the width; right pane gets 2/3

    var leftPaneX = Math.round(vWidth / 3);
    var rightPaneX = vWidth - leftPaneX;

    // don't use the whole height, to allow for display of header and partial footer
 
    var usableY = $(window).height() - 225;

    // top right pane is for detail, max out at 1/3 of the height.
    // if detail requires less than 1/3 of height, shrink to what it needs (with a 5px pad for appearance).
    // bottom right pane is for tree view, takes remainder of height

    var detailContainerY = Math.round(usableY / 3);
    detail.height('auto');
    var detailScrollHeight = detail.prop('scrollHeight');		// not just what's displayed, but all

    if ((detailScrollHeight + detailTitle.height()) < detailContainerY) {
		detailContainerY = detailScrollHeight + detailTitle.height() + 5;
    }

    var treeContainerY = usableY - detailContainerY;

    var treeDivY = treeContainerY - treeMainTitle.height();
    var searchDivY = usableY - searchTitle.height();
    var detailDivY = detailContainerY - detailTitle.height();

    detailContainer.width(rightPaneX - 7);
    detailContainer.height(detailContainerY);
	detail.height(detailDivY);
	$('#detailTD').height($('#detailContainer').height());

    searchContainer.width(leftPaneX - 30);
    searchContainer.height(treeDivY + detailDivY);
    searchPane.height(searchDivY);

    treeContainer.width(rightPaneX - 7);
    treeContainer.height(treeContainerY);
    treeViewDiv.height(treeDivY);
};

// add any necessary tooltips for the icons
var addTooltips = function() {
	$('[style*="/assets/images/is_a.gif"]').prop('title', 'is-a');
	$('[style*="/assets/images/part_of.gif"]').prop('title', 'part-of');
	$('[style*="/assets/images/regulates.gif"]').prop('title', 'regulates');
	$('[style*="/assets/images/negatively_regulates.gif"]').prop('title', 'negatively-regulates');
	$('[style*="/assets/images/positively_regulates.gif"]').prop('title', 'positively-regulates');
};

/* set up automatic pane resizing for when the page first loads and when the browser is resized.
 */
$(document).ready(function() { 
	setTimeout(resizePanes, 250);
	});
$(window).resize(resizePanes);