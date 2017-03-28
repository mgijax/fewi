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
				$('#detailTD').height($('#detailContainer').height());
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
			}
		});
};

/* submit the value in the search box to the server as a search string, get and display the set of results
 */
var refreshSearchPane = function() {
	// if the user has not chosen a selection from the autocomplete, pick the first one
	var searchTerm = $('#searchTerm').val();
	if (($('#searchTerm').val() != '') && $('#searchTerm').getSelectedItemIndex() == -1) {
		$('#searchTerm').val($('#searchTerm').getItemData(0).term);
		searchResultClick($('#searchTerm').getItemData(0).accID);
	}
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
var searchResultClick = function(id) {
	if ((id !== null) && (id !== undefined)) {
		fetchTermPane(id);
   		setBrowserTitle(id);
   		initializeTreeView(id);
   		try {
        	window.history.pushState(id, 'title', browserUrl + id);
    	} catch (err) {}
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
};

/* update the panes that need to be updated when the user clicks on a term in the tree view pane
 */
var treeTermClick = function(id) {
	fetchTermPane(id);
    setBrowserTitle(id);
    try {
        window.history.pushState(id, 'title', browserUrl + id);
    } catch (err) {}
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
		log('found ' + selectedIDs.length + ' selectedIDs');
		if (selectedIDs.length == 1) {
			var nodeRectangle = $('#' + selectedIDs[0])[0].getBoundingClientRect();
			var divRectangle = $('#treeViewDiv')[0].getBoundingClientRect();

			log('nodeRectangle.bottom: ' + nodeRectangle.bottom);
			log('divRectangle.bottom: ' + divRectangle.bottom);
			if (nodeRectangle.bottom > divRectangle.bottom) {
			    $('#treeViewDiv')[0].scrollTop = nodeRectangle.top - divRectangle.top
			    	- 0.5 * (divRectangle.bottom - divRectangle.top);
			    log('scrolled tree');
			}
		}
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
		setTimeout(scrollTreeView, 500);	// wait for nodes to load, then scroll if needed
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
	log("finished config");
	
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
	log("initialized jstree");
};

/* handle a 'change' event from jsTree.  This event fires when the user clicks a triangle to expand a node's
 * children, clicks a node's text to select the node, or clicks a triangle to collapse a node's children.  We
 * do not deal with the selection of the node itself here, as we use the select_node event for that.
 */
var onChange = function(node, action, selected, event) {
	log('onChange');
	if (previousSelectedNodeIDs.length == 0) {
		previousSelectedNodeIDs = $('#treeViewDiv').jstree().get_selected();
		return;		// if nothing previously selected, don't deselect anything (tree is building initial state)
	}
		
	var newSelected = $('#treeViewDiv').jstree().get_selected();
	for (var i = 0; i < newSelected.length; i++) {
		if (previousSelectedNodeIDs.indexOf(newSelected[i]) == -1) {
			$('#treeViewDiv').jstree().deselect_node(newSelected[i]);
			log(' - Unselected ' + newSelected[i]);
		} else {
			log(' - Matches: ' + newSelected[i]);
		}
	}
};

/* handle an 'select_node' event from jsTree.  (select the node, hide any prior annotations link, show the
 * annotations link for this node)
 */
var onSelectNode = function(node, event) {
	/* We want to prevent the selection of multiple nodes, so we need to deselect any previously
	 * selected nodes.
	 */
	log('onSelect');
	var tree = $('#treeViewDiv').jstree();
	var selectedNodeIDs = tree.get_selected();
	if (selectedNodeIDs.length > 1) {
		// if multiple nodes selected, need to just keep the latest one
		for (var i = 0; i < previousSelectedNodeIDs.length; i++) {
			tree.deselect_node(previousSelectedNodeIDs[i]);
			log(' - Deselected node: ' + previousSelectedNodeIDs[i]);
		}
	} else {
		log(' - Only one node selected: ' + selectedNodeIDs[0]);
	}
	previousSelectedNodeIDs = tree.get_selected();
	
	/* ensure that this node is open */
	var node = tree.get_node(previousSelectedNodeIDs[0]);
	if (!tree.is_open(node)) {
		tree.open_node(node);
		log(' - Opened node: ' + previousSelectedNodeIDs[0]);
	}
	 
	/* update the term pane */
	treeTermClick(node.data.accID);

	/* TODO: hide old annotations link */
	/* TODO: show new annotations link */
};
