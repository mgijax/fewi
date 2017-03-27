/*** Javascript for shared vocabulary browser ***/

var logging = true;
var initialID = null;

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

/* add an event listener to enable reasonable results when stepping back through browser history
 */
window.addEventListener('popstate', function(e) {
	var id = e.state;
	if (id != null) {
		fetchTermPane(id);
		setBrowserTitle(id);
	} else {
		fetchTermPane(initialID);
		setBrowserTitle(initialID);
	}
});

var populatedTree = false;

/* clear any exiting tree view and display one for the given id, if specified
 */
var initializeTreeView = function(id) {
	log("entered initializeTreeView(" + id + ")");
	if (populatedTree) {
		$.jstree.destroy();
		$('#treeViewDiv').html('');
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
	log("finished config");
	$('#treeViewDiv').jstree(config);
	log("initialized jstree");
};

/*
 * Anatomical Dictionary Auto Complete Section (modified from gxd_query.js)
 */
/*
function makeStructureAC(inputID,containerID){
    // disable the autocomplete for now

    // Use an XHRDataSource
    var oDS = new YAHOO.util.XHRDataSource(fewiurl + "autocomplete/emapa");
    // Set the responseType
    oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    // Define the schema of the JSON results
    oDS.responseSchema = {resultsList: "resultObjects", fields:["structure", "synonym","isStrictSynonym"]};

    //oDS.maxCacheEntries = 10;
    oDS.connXhrMode = "cancelStaleRequests";

    // Instantiate the AutoCompletes
    var oAC = new YAHOO.widget.AutoComplete(inputID, containerID, oDS);

    // Throttle requests sent
    oAC.queryDelay = .03;
    oAC.minQueryLength = 2;
    oAC.maxResultsDisplayed = 500;
    oAC.forceSelection = true;

    // try to set the input field after itemSelect event
    oAC.suppressInputUpdate = true;
    var selectionHandler = function(sType, aArgs) { 
	    //log("selectionHandler() called");
	    var myAC = aArgs[0]; // reference back to the AC instance 
	    var elLI = aArgs[1]; // reference to the selected LI element 
	    var oData = aArgs[2]; // object literal of selected item's result data 
	    //populate input box with another value (the base structure name)
	    var structure = oData[1]; // 0 = term, 1 = ACtext
	    var inputBox = YAHOO.util.Dom.get(inputID);
	    inputBox.value = structure;
	    refreshSearchPane();
    }; 
    oAC.itemSelectEvent.subscribe(selectionHandler); 

    oAC.formatResult = function(oResultData, sQuery, sResultMatch) {
    	   // some other piece of data defined by schema
    	   var synonym = oResultData[1];
    	   var isStrictSynonym = oResultData[2];
    	  var value = synonym;
    	  if(isStrictSynonym) value += " <span style=\"color:#222; font-size:0.8em; font-style:normal;\">[synonym]</span>";

    	  return (value);
    	}; 
    	
    return {
        oDS: oDS,
        oAC: oAC
    };
};

var treeView = null;	// global for the YUI tree itself
var defaultPath = null;	// list of IDs on path from root to selected node
var waitForPath = true;	// are we waiting for defaultPath to load?
var pathCheck = null;	// ID of interval-based check for defaultPath
var selectedNodeID = null;	// ID of the currently highlighted node
var alreadyScrolled = false;	// have we already scrolled for this term?
var gxdResultCount = null;	// count of GXD results for selectedNodeID
var logging = false;		// enable logging to browser console?

function numberWithCommas(x) {
    var parts = x.toString().split(".");
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    return parts.join(".");
}

function fetchAndCall(url, callbackFn) {
    // get the contents at 'url' via ajax, then pass the contents to the given
    // callback function.  The callback function should handle a null value
    // for cases where the fetch fails.
 
    var callback = {
	success : function(oResponse) {
		var oResults = null;

		// Try to evaluate it as a JSON string and convert it to 
		// Javascript objects.  If that fails, just pass along the
		// string itself.
		try {
		    oResults = eval("(" + oResponse.responseText + ")");
		} catch (err) {
		    oResults = oResponse.responseText;
		}
		oResponse.argument.callbackFn(oResults);
		  },
	failure : function(oResponse) {
		oResponse.argument.callbackFn(null);
		  },
	argument: {
		'url' : url,
		'callbackFn' : callbackFn
		  },
	timeout: 10000
    };
    YAHOO.util.Connect.asyncRequest('GET', url, callback);
}

function setDetailDiv(s) {
    if (s === null) {
	log("Could not get term details; div cannot be refreshed");
	return;
    }

    if (s) {
	var el = document.getElementById('detail');
	if (el) { el.innerHTML = s; }

	var title = document.getElementById('pageTitle');
	if (title) { setBrowserTitle(title.value); }

	resizePanes();
    }
    return;
}

function setGxdResultCount(s) {
    gxdResultCount = s;
    highlightSelectedTerm();
    return
}

function fetchResultCount(selectedNodeID) {
    var sUrl = "${configBean.FEWI_URL}gxd/results/totalCount?structureID=" + selectedNodeID;
    fetchAndCall(sUrl, setGxdResultCount);
}

function setDefaultPath(path) {
	log('setDefaultPath(' + path + ')');
    if (path === null) {
		log("Could not get default path; tree cannot be created");
		return;
    }

    if (!defaultPath) {
		defaultPath = path;
		waitForPath = false;
    }

    fetchRootNode(defaultPath[0]);
    return;
}

function fetchDefaultPath(selectedNodeID) {
	log('fetchDefaultPath(' + selectedNodeID + ')');
    var sUrl = "${configBean.FEWI_URL}vocab/gxd/anatomy/defaultPath/" + selectedNodeID;
    fetchAndCall(sUrl, setDefaultPath);
    return;
}

function addRootNode(node) {
    if (node === null) {
	log("Could not retrieve root node; tree cannot be created");
	return;
    }

    // instantiate from the initial data set shipped with the page itself:

    var node = new YAHOO.widget.TextNode(node, treeView.getRoot());

    path = defaultPath.slice();
    path.reverse();
    path.pop();
    node.data.pathToOpen = path;
    node.expand();

    treeView.setDynamicLoad(nodeLoader2);
    treeView.render();
}

function fetchRootNode(rootID) {
    var sUrl = "${configBean.FEWI_URL}vocab/gxd/anatomy/node/" + rootID;

    fetchAndCall(sUrl, addRootNode);
}

function updateTreeTitle(emapID) {
    var treeTitleDiv = document.getElementById('treeTitle');
    var treeTitle;

    if (treeTitleDiv) {
	if (emapID.indexOf('EMAPS') == 0) {
	    treeTitle = 'Theiler Stage ' + Math.round(
		emapID.substring(emapID.length - 2, emapID.length) );
	} else {
	    treeTitle = 'All Theiler Stages';
	}
	treeTitleDiv.innerHTML = treeTitle;
    }
}

function resetTree(snID, fullRebuild) {
	// if we've not yet instantiated the treeView, then 
    // instantiate from the initial data set shipped with the page itself:
    log('resetTree(' + snID + ', ' + fullRebuild + ')');
    selectedNodeID = snID;
//    setSelectedNode(snID);
    gxdResultCount = null;
    if ((treeView === null) || (fullRebuild === true)) {
		log(' - in if');
    	treeView = new YAHOO.widget.TreeView("treeViewDiv");
    	loaded = false;
    	defaultPath = null;
    	waitForPath = true;
    	alreadyScrolled = false;
    	fetchDefaultPath(snID);
    	updateTreeTitle(snID);
    } else {
    	// otherwise, just update the existing tree:
    	// 1. remove highlighting of previously selected term
    	// 2. highlight the newly selected term
    	// 3. add the expression result count and link
    	// 4. expand the selected term to show its children

		log(' - in else');
    	$('#treeViewDiv span').removeClass('bold').removeClass('highlight');
    	highlightSelectedTerm();
    }
}

var lastHighlightedNodes = null;	// set of nodes from last highlighting operation
var originalTerms = {};				// map of ID -> original term (before highlighting)

function highlightSelectedTerm () {
    // find and highlight all instances of the selected term (the one in the
    // term detail pane) in the YUI tree view
	log('highlightSelectedTerm');
	
    // find instances of the selected node

    var selectedNodes = treeView.getNodesBy(function (node) {
	if (node.data.accID == selectedNodeID) {
	    return true;
	} else {
	    return false;
	}
    });

    // need special handling for "mouse" node; not sure why, seems to be a
    // YUI quirk
 
    var countStr = null;
    var plural = true;
    var linked = true;

    if (gxdResultCount === null) {
	countStr = "show";
    } else {
	countStr = numberWithCommas(gxdResultCount);
	if (gxdResultCount == '1') {
	    plural = false;
	} else if (gxdResultCount == '0') {
	    linked = false;
	}
    }

    var resultText = 'expression result';
    if (plural) {
	resultText = resultText + 's';
    }
    var url = '${configBean.FEWI_URL}gxd/structure/' + selectedNodeID;

    // Note that we introduce a bogus '</a>' at the start of link to close off
    // the YUI-generated link for the cell contents itself.  That means there
    // will probably be an extra '</a>' at the end of the cell itself.
 
    var spaces = '&nbsp;&nbsp;&nbsp;&nbsp;';

    var link = '</a>' + spaces + '(<a href="' + url + '">' + countStr + '</a> '
	+ resultText + ')';

    if (!linked) {
        link = '</a>' + spaces + '(' + countStr + ' ' + resultText + ')';
    }

	// remove highights from previously selected nodes
	if (lastHighlightedNodes != null) {
		var pNode = null;
		for (var j = 0; j < lastHighlightedNodes.length; j++) {
			pNode = lastHighlightedNodes[j];
			pNode.data.highlighted = false;
			pNode.label = originalTerms[pNode.data.accID];
		}
	}
	
    if (!selectedNodes) {
		return;
    }

    // walk through the nodes and highlight each one that's not already
    // highlighted

    var node = null;
    for (var i = 0; i < selectedNodes.length; i++) {
		node = selectedNodes[i];
        if (!node.data.highlighted) {
    		if (!(node.data.accID in originalTerms)) {
    			originalTerms[node.data.accID] = node.label;
    		}
		    node.data.highlighted = true;
		    node.label = '<span class="highlight bold">' + selectedNodes[i].label + '</span>' + link;
		    node.expand();
		}
    }
    lastHighlightedNodes = selectedNodes;
    treeView.render();
}

var selectedNode = null;		// global - node for selected term

function setSelectedNode(node) {
    // remember the given node as being selected
    log('setSelectedNode(' + node + ')');
    selectedNode = node;
}

			// more globals:
var checkNumber = null;	// ID of scheduled check so we can cancel it once done
var loaded = false;	// has the selected node been loaded into tree view?

function isLoaded() {
    // return true if the selected node has been populated dynamically and
    // has had its node constructed in the YUI tree view

    if ((selectedNode != null) && (selectedNode.getEl()) ) {
	loaded = true;
	clearInterval(checkNumber);	// stop checking
	scrollTreeViewDiv(treeView);	// we can scroll the tree now
    }
    return loaded;
}

function scrollOnceLoaded(selectedNode) {
    // once the selectedNode has been loaded into the YUI tree view, go ahead
    // and scroll the div down as needed to find it.  Keep checking every 
    // 100 ms until the node gets loaded.

    setSelectedNode(selectedNode);
    checkNumber = setInterval(isLoaded, 100);
    return;
}

function scrollTreeViewDiv (treeView) {
    // scroll the treeview object to show the selected node
    
    log('scrollTreeViewDiv(' + treeView + ') -> alreadyScrolled: ' + alreadyScrolled);
    if (alreadyScrolled) { return; }

    var selectedNodes = treeView.getNodesBy(function (node) {
	if (node.data.accID == selectedNodeID) {
	    if (node.data.openByDefault) {
	        return true;
	    }
	}
	return false;
	});

    if (!selectedNodes) { return; }

    var selectedNode = null;
    if (selectedNodes.length > 0) {
	selectedNode = selectedNodes.pop();
    }

    // if the desired node hasn't been loaded yet, then we need to wait for
    // now and only do the scrolling once it's been loaded

    if (!loaded) {
	scrollOnceLoaded(selectedNode);
    }

    if (selectedNode) {
	var el = selectedNode.getEl();
	if (el === null) { return; }

	var rect = selectedNode.getEl().getBoundingClientRect();

	var treeViewDiv = document.getElementById('treeViewDiv');
	var divRect = treeViewDiv.getBoundingClientRect();

	// if even part of the selected term is out of the visible area, move
	// it up to be centered vertically in treeViewDiv

	if (rect.bottom > divRect.bottom) {
	    treeViewDiv.scrollTop = rect.top - divRect.top -
		0.5 * (divRect.bottom - divRect.top);
	}
	alreadyScrolled = true;
    }
}

function nodeLoader2(node, fnLoadComplete) {
    var nodeID = encodeURI(node.data.accID);
    var sUrl = "${configBean.FEWI_URL}vocab/gxd/anatomyChildren/" + nodeID;

    var defaultPath = node.data.pathToOpen;

    var toExpand = null;
    if (defaultPath) {
	toExpand = defaultPath.pop();
    }
    log('node = ' + node);
    log('defaultPath = ' + defaultPath);
    log('nodeLoader2.toExpand = ' + toExpand);

    var callback = {
	success: function(oResponse) {
	    var oResults = eval("(" + oResponse.responseText + ")");
	    var children = eval(oResults.children);
	    var selectedTerm = null;

		log('oResults: ' + oResults);
		log('children.length: ' + children.length);
		
	    // due to the oddities of asynchronous processing, we sometimes
	    // end up trying to add duplicate nodes (presumably due to
	    // duplicate requests/responses from retries).  We only want to
	    // instantiate nodes we don't already have.

	    for (var i = 0; i < children.length; i++) {
		childID = children[i].accID;

		var skipIt = false;
		for (var j = 0; j < node.children.length; j++) {
		    if (node.children[j].data.accID == childID) {
			skipIt = true;
			break;
		    }
		}

		if (skipIt) { continue; }

		childNode = new YAHOO.widget.TextNode(children[i], node);

		log(' - loaded ' + childNode.data.accID);
		if ((childNode.data.accID == toExpand) || (childNode.data.accID == selectedNodeID)) {
			log(' - expanding ' + toExpand);
		    childNode.data.pathToOpen = defaultPath;
		    childNode.data.openByDefault = true;
		    scrollTreeViewDiv(treeView);
		    childNode.expand();
		}
	    } // end for loop
    	    highlightSelectedTerm();
	    oResponse.argument.fnLoadComplete();
	    },
	failure: function(oResponse) {
	    log("Failed to get children from " + sUrl);
	    YAHOO.log("Failed to get children from " + sUrl, "info", "mgi");
	    oReponse.argument.fnLoadComplete();
	    },
	argument: {
	    "node" : node,
	    "fnLoadComplete" : fnLoadComplete
	    },
	timeout: 10000	// ten seconds
	};
    YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
}

function fillSearchPane(contents) {
    if (contents === null) {
	log("Could not get search pane; div cannot be refreshed");
	return;
    }

    if (contents) {
	var el = document.getElementById('searchPane');
	if (el) {
	    el.innerHTML = contents; 
	    makeStructureAC("searchTerm", "structureContainer");

	    var emapaID = contents.match(/EMAPA:[0-9]+/);
	    if (emapaID != null) {
		resetPanes(emapaID, true);
	    }
		
	}
    }
    return;
}

function refreshSearchPane() {
    // refresh the search pane

    var searchTerm = "";
    var searchTermBox = document.getElementById("searchTerm");
    if (searchTermBox) {
	searchTerm = searchTermBox.value;
    }

    var sUrl = "${configBean.FEWI_URL}vocab/gxd/anatomySearch?term=" +
	searchTerm;
    fetchAndCall (sUrl, fillSearchPane);
}

function resetSearch() {
    // reset button for the search form; clear the text and the search results

    document.getElementById("searchTerm").value = "";
    refreshSearchPane();
}

function resetPanes(accID, rebuildTree) {
    // initialize the term detail and tree view panes to show the term with
    // the given accID

    // For some reason, the stageLinker select list remembers whatever option
    // is selected until the page is reloaded (regardless of whether you pick
    // a different option the next time).  So, we reset the form to erase this
    // flawed memory.

	log('resetPanes(' + accID + ', ' + rebuildTree + ')');
    var stageLinkerForm = null;
    stageLinkerForm = document.getElementById("stageLinkerForm");
    if (stageLinkerForm) {
	stageLinkerForm.reset();
    }

    selectedNode = null;
    selectedNodeID = accID;
    fetchResultCount(accID);
    fetchDetailDiv(accID);
    resetTree(accID, rebuildTree);
    setBrowserTitle(accID);
    try {
        window.history.replaceState('foo', 'title', '${configBean.FEWI_URL}vocab/gxd/anatomy/' + accID);
    } catch (err) {}
    alreadyScrolled = false;
    scrollTreeViewDiv(treeView);
}

function resizePanes() {
	log('resizePanes()');
	
// look up the window's x, y dimensions in a cross-browser way
var w = window,
    d = document,
    e = d.documentElement,
    g = d.getElementsByTagName('body')[0],
    x = w.innerWidth || e.clientWidth || g.clientWidth,
    y = w.innerHeight|| e.clientHeight|| g.clientHeight;

    var detailContainer = d.getElementById("detailContainer");
    var treeContainer = d.getElementById("treeViewContainer");
    var searchContainer = d.getElementById("searchContainer");

    var treeMainTitle = d.getElementById("treeMainTitle");
    var treeSubTitle = d.getElementById("treeTitle");
    var searchTitle = d.getElementById("searchTitle");
    var detailTitle = d.getElementById("detailTitle");

    var treeViewDiv = d.getElementById("treeViewDiv");
    var searchPane = d.getElementById("searchPane");
    var detail = d.getElementById("detail");

    // left pane is for search and takes 1/3 of the width

    var leftPaneX = Math.round(x / 3);
    var rightPaneX = x - leftPaneX;

    // don't use the whole height, to allow for display of header and partial
    // footer
 
    var usableY = y - 225;

    // top right pane is for detail, max out at 1/3 of the height.
    // if detail requires less than 1/3 of height, shrink to what it needs.
    // bottom right pane is for tree view, takes remainder of height

    var detailContainerY = Math.round(usableY / 3);

    detail.style.height = "auto";
    if (detail.scrollHeight + detailTitle.scrollHeight < detailContainerY) {
	detailContainerY = detail.scrollHeight + detailTitle.scrollHeight + 5;
    }

    var treeContainerY = usableY - detailContainerY;

    var treeDivY = treeContainerY - treeMainTitle.scrollHeight
	- treeSubTitle.scrollHeight;
    var searchDivY = usableY - searchTitle.scrollHeight;
    var detailDivY = detailContainerY - detailTitle.scrollHeight;

    detail.style.height = null;
    var divRect = detail.getBoundingClientRect();

    detailContainer.style.width = (rightPaneX - 7) + 'px';
    detailContainer.style.height = detailContainerY + 'px';

    searchContainer.style.width = (leftPaneX - 30) + 'px';
    searchContainer.style.height = (treeDivY + detailDivY) + 'px';

    treeContainer.style.width = (rightPaneX - 7) + 'px';
    treeContainer.style.height = treeContainerY + 'px';

    treeViewDiv.style.height = treeDivY + 'px';

    searchPane.style.height = searchDivY + 'px';

    detail.style.height = detailDivY + 'px';
}

resizePanes();
YAHOO.namespace("example.container");
YAHOO.util.Event.onDOMReady(function () {	
	resetPanes("${term.primaryId}", true);
	refreshSearchPane();
	resizePanes();
});

// IE8 does not support this addEventListener call, so just ignore the error
try {
    window.addEventListener ('resize', function(event) {
	resizePanes();
    });
} catch (e) {};
*/