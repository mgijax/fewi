<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.link.ProviderLinker" %>
<%@ page import = "java.util.List" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<fewi:simpleseo
	title="${title}"
	canonical="${configBean.FEWI_URL}vocab/gxd/anatomy/${term.primaryId}"
	description="${seoDescription}"
	keywords="${seoKeywords}"
/>


<%  // Pull detail object into servlet scope
    // EXAMPLE - Marker foo = (Marker)request.getAttribute("foo");

    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailListCat1","detailCat2");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailListBg1","detailListBg2");
    VocabTerm term = (VocabTerm) request.getAttribute("term");
%>
<script type="text/javascript">
var fewiurl = "${configBean.FEWI_URL}";
</script>
<SCRIPT TYPE="text/javascript" SRC='${configBean.WEBSHARE_URL}js/hideshow.js'></SCRIPT>
<script type="text/javascript" src='${configBean.FEWI_URL}assets/js/anatomy_detail.js'></script>
    
<script language="Javascript">
</script>

<style>
td.bordered { border: 1px solid black }
#termPaneDetails th {
  padding: 4px;
}
td.top { vertical-align: top }
td.padded { padding: 4px }
td.padTop { padding-top: 2px }
.highlight { background-color: #EBCA6D }
.bold { font-weight: bold }
.ygtvlabel { background-color: white; color: black; }
.ygtvlabel:link { background-color: white; color: black; }
.ygtvlabel:visited { background-color: white; color: black; }
.ygtvlabel:hover { background-color: white; color: blue; text-decoration: underline; }
.ygtvfocus { background-color: white }
.ygtv-highlight { background-color: white }
.ygtv-highlight0 { background-color: white }
.ygtv-highlight1 { background-color: white }
.ygtv-highlight2 { background-color: white }

.ui-autocomplete {
	max-height: 250px;
	overflow-y: auto;
	/* prevent horizontal scrollbar */
	overflow-x: hidden;
	border: 1px solid gray;
	max-width: 400px;
}
.ui-menu {
	list-style-type: none;
	color: black;
	background-color: white;
}
.ui-menu .ui-menu-item a {
	padding: 0px;
	line-height: 1;
	color: black;
	list-style-type: none;
}
ul.ui-autocomplete {
	padding: 0px;
	list-style-type: none;
	color: black;
}
.ui-autocomplete li {
	list-style-type: none;
	list-style-position: inherit;
	margin-left: 5px;
	padding-bottom: 3px;
}

.ui-autocomplete li :hover {
	background-color: #426FD9;
	color: #FFFFFF;
}
</style>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapperGxd" userdoc="VOCAB_mad_browser_help.shtml">
  <a href="${configBean.HOMEPAGES_URL}expression.shtml"><img class="gxdLogo" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="75"></a>
  <span class="titleBarMainTitleGxd" style="display:inline-block; margin-top: 20px">Mouse Developmental Anatomy Browser</span>
</div>

<!-- 3-section table: search, term detail, tree view -->

<div id="outerGxd" style="padding: 2px">
  <table style="width: 100%; background-color: white; border:0">
  <tr><td rowspan="2" class="bordered top padTop" style="width: 30%">
      <div style='width: 100%;' id='searchContainer'>
      <div style='width: 100%; text-align: center; font-size:125%; font-weight: bold; clear:both; padding-bottom: 8px;' id='searchTitle'>Anatomy Search</div>
      <div id="searchPane" style="width:100%; overflow:auto; text-align:center;">
      </div>
      </div>
    </td>
    <td class="bordered top padTop">
      <div style='width: 100%' id='detailContainer'>
      <div style='width: 100%; text-align: center; font-size:125%; font-weight: bold' id='detailTitle'>Anatomical Term Detail<br></div>
      <div id="detail" style="width:100%; overflow: auto">
      </div>
      </div>
    </td>
  </tr>
  <tr><td class="bordered top padTop">
	<div style='width:100%' id='treeViewContainer'>
        <div style='width: 100%; text-align: center; font-size:125%; font-weight: bold' id="treeMainTitle">Anatomical Tree View</div>
	<div style="width: 100%; text-align: center" id='treeTitle'>
	<c:if test="${term.isEmapa}">All Theiler Stages</c:if>
	<c:if test="${term.isEmaps}">Theiler Stage ${term.emapInfo.stage}</c:if>
	</div>
	<div id="treeViewDiv" style="overflow:auto"></div>
	</div>
  </td></tr>
</table>

</div>

<div> ${message} </div>

<script type="text/javascript">
var crossRef = "${crossRef}";
var treeView = null;	// global for the YUI tree itself
var defaultPath = null;	// list of IDs on path from root to selected node
var waitForPath = true;	// are we waiting for defaultPath to load?
var pathCheck = null;	// ID of interval-based check for defaultPath
var selectedNodeID = null;	// ID of the currently highlighted node
var alreadyScrolled = false;	// have we already scrolled for this term?
var gxdResultCount = null;	// count of GXD results for selectedNodeID
var htExperimentCount = null;	// count of HT experiments for selectedNodeID
var phenotypeAnnotationCount = null;	// count of phenotype annotations for selectedNodeID
var logging = false;		// enable logging to browser console?

function numberWithCommas(x) {
    var parts = x.toString().split(".");
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    return parts.join(".");
}

function log(msg) {
    // log a message to the browser console
    if (logging == true) { console.log(msg); }
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

function setBrowserTitle(pageTitle) {
    document.title = pageTitle;
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

function fetchDetailDiv(selectedNodeID) {
	log('fetchDetailDiv(' + selectedNodeID + ')');
    var sUrl = "${configBean.FEWI_URL}vocab/gxd/anatomy/termPane/" + selectedNodeID;

    fetchAndCall(sUrl, setDetailDiv);
    return;
}

function setPhenotypeAnnotationCount(s) {
    phenotypeAnnotationCount = s;
    highlightSelectedTerm();
    return
}

function fetchPhenotypeAnnotationCount(selectedNodeID) {
    var sUrl = "${configBean.FEWI_URL}mp/annotations/count_by_anatomy/" + selectedNodeID;
    fetchAndCall(sUrl, setPhenotypeAnnotationCount);
}

function setHtExperimentCount(s) {
    htExperimentCount = s;
    return
}

function fetchHtExperimentCount(selectedNodeID) {
    var sUrl = "${configBean.FEWI_URL}/gxd/htexp_index/summary/totalCount?structureID=" + selectedNodeID
    fetchAndCall(sUrl, setHtExperimentCount);
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
    htExperimentCount = null;
    phenotypeAnnotationCount = null;
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

// format and return a link for phenotype annotations, if the count is > 0
function htExperimentLink() {
    if ((htExperimentCount === null) || (htExperimentCount == '-1') || (htExperimentCount == '0')) {
	return ''
    }

    var htText = 'RNA-seq or microarray experiment';
    if (htExperimentCount != '1') {
	htText = htText + 's';
    }
    var url = '${configBean.FEWI_URL}gxd/htexp_index/summary?structure=' + selectedNodeID;

    return '<a href="' + url + '"><span class="htExperimentCount">' + numberWithCommas(htExperimentCount) + '</span></a> ' + htText;
}

// format and return a link for phenotype annotations, if the count is > 0
function phenotypeLink() {
	if ((phenotypeAnnotationCount === null) || (phenotypeAnnotationCount == '-1')) { return '' }

    var phenoText = 'phenotype annotation';
    if (phenotypeAnnotationCount != '1') {
		phenoText = phenoText + 's';
		
		// no need for link if count is zero
	    if (phenotypeAnnotationCount == '0') {
    		return '0 ' + phenoText;
    	}
    }
    var url = '${configBean.FEWI_URL}mp/annotations/by_anatomy/' + selectedNodeID;

	return '<a href="' + url + '"><span class="phenotypeAnnotationCount">' + numberWithCommas(phenotypeAnnotationCount) + '</span></a> ' + phenoText;
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

    var htLink = htExperimentLink();
    if (htLink != '') {
    	htLink = '; ' + htLink;	
    }

    var phenoLink = phenotypeLink();
    if (phenoLink != '') {
    	phenoLink = '; ' + phenoLink;	
    }
    
    var link = '</a>' + spaces + '(<a href="' + url + '"><span class="expressionResultCount">' + countStr + '</span></a> '
		+ resultText + htLink + phenoLink + ')';

    if (!linked) {
        link = '</a>' + spaces + '(' + countStr + ' ' + resultText + htLink + phenoLink + ')';
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

function refreshSearchPane(initialSearch) {
    // refresh the search pane

    var searchTerm = initialSearch;
    if ((searchTerm == null) || (searchTerm == undefined)) {
    	searchTerm = "";
    }
    var searchTermBox = document.getElementById("searchTerm");
    if (searchTermBox) {
		searchTerm = searchTermBox.value;
    }

    var sUrl = "${configBean.FEWI_URL}vocab/gxd/anatomySearch?term=" + searchTerm;
    fetchAndCall (sUrl, fillSearchPane);
}

function resetSearch() {
    // reset button for the search form; clear the text and the search results

    document.getElementById("searchTerm").value = "";
    refreshSearchPane("");
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
    fetchHtExperimentCount(accID);
    fetchPhenotypeAnnotationCount(accID);
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
	refreshSearchPane(crossRef);
	resizePanes();
});

// IE8 does not support this addEventListener call, so just ignore the error
try {
    window.addEventListener ('resize', function(event) {
	resizePanes();
    });
} catch (e) {};
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
