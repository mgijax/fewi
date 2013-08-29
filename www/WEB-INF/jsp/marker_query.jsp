<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
${templateBean.templateHeadHtml}

<title>Genes and Markers Query Form</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style type="text/css">
</style>

<script src="http://yui.yahooapis.com/2.8.1/build/history/history-min.js"></script>

<script>
</script>

${templateBean.templateBodyStartHtml}


<!-- header bar -->
<div id="titleBarWrapper" userdoc="GENE_help.shtml">	
	<span class="titleBarMainTitle">Genes and Markers Query Form</span>
</div>

<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">

<!-- query form structural table -->

<table class="detailStructureTable">


<form method="GET" action="${configBean.WI_URL}searches/marker_report.cgi" id="markerQF">
<table class="queryStructureTable">

  <tr><td class="queryParams1" colspan="2">
    Search for genes and markers by name, feature type, location, GO terms,
    protein domains, etc.<br/>
    <input class="buttonLabel" value="Search" type="submit">&nbsp;&nbsp;
    <input class="reset" type="reset">
    <span class="example">Specify sorting and output options
      <a href="#output">below</a></span>.  
  </td></tr>

  <!-- row 1-->
  <tr>
    <td class="queryCat1">Gene/Marker</td>
    <td class="queryParams1">
    <dl>
      <dt class="qfLabel">
      <a onclick="javascript:openUserhelpWindow(&quot;GENE_help.shtml#gene_nomenclature&quot;); return false;" href="${configBean.USERHELP_URL}GENE_help.shtml#gene_nomenclature">Gene/Marker Symbol/Name</a>:
      </dt>
      <dd>
        <select name="op:markerSymname" class="grayBackground"><option value="begins">begins</option><option value="=">=</option><option value="contains" selected="">contains</option></select>
	&nbsp;
	<input name="markerSymname" size="25" class="grayBackground" type="text">
	search in
	<select name="symnameBreadth" class="grayBackground"><option value="C">current symbols/names</option><option value="CM" selected="">current &amp; old symbols/names, synonyms, alleles, homologs</option></select>
      </dd>
    </dl>
    </td>
  </tr>

  <!-- row 2-->
  <tr>
    <td class="queryCat2">Feature Type</td>
    <td class="queryParams2">
     <div id="nojs">
	     <p class='example'><br/>Click to select one or more <a href="${configBean.USERHELP_URL}marker_help.shtml#marker_type">feature types.</a><br/>
        If no boxes are checked, then all feature types are included.<br/>
        Counts reflect total MGI Markers in each feature type category.<br/>
        </p>
	${htmlMcv}
     </div>
     <div id="js" style="display:none;">
        <div style="float:left;margin-right:5em;">
            <input type="button" name="resetchecks" id="resetchecks" value="Reset Checks"/>
            &nbsp;&nbsp;<a id='show' class='showHide'>Show</a>/<a id='hide' class='showHide'>Hide</a> all subcategories.
            <br/><br/>
            <div id="catSelectors" class="ygtv-checkbox"></div> 
        </div>        
        <p class='example'><br/>Click to select one or more 
	<a onclick='javascript:openUserhelpWindow("${configBean.USERHELP_URL}GENE_helpl.shtml#marker_type"); return false;' href="${configBean.USERHELP_URL}GENE_help.shtml#marker_type"
        >feature types.</a><br/>
	If no boxes are checked, then all feature types are included.<br/>
	Counts reflect total MGI Markers in each feature type category.<br/>
        </p>            
    </div>
  <!--
      <div style="position:relative;">
        <div style="float:left; width:300px;text-align:left;">
        </div>
        <div style="float:left; text-align:left;">
		Enter something param2<br/>
		${chromosomes}
        </div>
      </div>			
  -->
    </td>
  </tr>


  <!-- row 3-->
  <tr>
    <td class="queryCat1">Map position</td>
    <td class="queryParams1">
  <!--
      <div style="position:relative;">
        <div style="float:left; width:300px;text-align:left;">
        </div>
        <div style="float:left; text-align:left;">
		Enter something param3.<br/>
			<div id="acatSelectors" class="ygtv-checkbox"></div>
        </div>
      </div>			
  -->
    </td>
  </tr>

  <!-- row 4-->
  <tr>
    <td class="queryCat2">Gene Ontology<br/>Classifications</td>
    <td class="queryParams2">
  <!--
      <div style="position:relative;">
        <div style="float:left; width:300px;text-align:left;">
        </div>
        <div style="float:left; text-align:left;">
		Enter something param2<br/>
		${chromosomes}
        </div>
      </div>			
  -->
    </td>
  </tr>


  <!-- row 5-->
  <tr>
    <td class="queryCat1">Protein domains</td>
    <td class="queryParams1">
  <!--
      <div style="position:relative;">
        <div style="float:left; width:300px;text-align:left;">
        </div>
        <div style="float:left; text-align:left;">
		Enter something param3.<br/>
			<div id="bcatSelectors" class="ygtv-checkbox"></div>
        </div>
      </div>			
  -->
    </td>
  </tr>

  <!-- row 6-->
  <tr>
    <td class="queryCat2">Mouse phenotypes &amp;<br/>mouse models of<br/>human disease</td>
    <td class="queryParams2">
  <!--
      <div style="position:relative;">
        <div style="float:left; width:300px;text-align:left;">
        </div>
        <div style="float:left; text-align:left;">
		Enter something param2<br/>
		${chromosomes}
        </div>
      </div>			
  -->
    </td>
  </tr>


  <!-- row 7-->
  <tr>
    <td class="queryCat1">Clone collection</td>
    <td class="queryParams1">
  <!--
      <div style="position:relative;">
        <div style="float:left; width:300px;text-align:left;">
        </div>
        <div style="float:left; text-align:left;">
		Enter something param3.<br/>
			<div id="ccatSelectors" class="ygtv-checkbox"></div>
        </div>
      </div>			
  -->
    </td>
  </tr>

  <!-- row 2-->
  <tr>
    <td class="queryCat2">Sorting and <br/>output format</td>
    <td class="queryParams2">
  <!--
      <div style="position:relative;">
        <div style="float:left; width:300px;text-align:left;">
        </div>
        <div style="float:left; text-align:left;">
		Enter something param2<br/>
		${chromosomes}
        </div>
      </div>			
  -->
    </td>
  </tr>


  <!-- row 3-->
  <tr>
    <td class="queryCat1">Map position</td>
    <td class="queryParams1">
  <!--
      <div style="position:relative;">
        <div style="float:left; width:300px;text-align:left;">
        </div>
        <div style="float:left; text-align:left;">
		Enter something param3.<br/>
			<div id="dcatSelectors" class="ygtv-checkbox"></div>
        </div>
      </div>			
  -->
    </td>
  </tr>



  <!-- Submit/Reset-->
  <tr>  
    <td colspan="2" align="left">
      <input class="buttonLabel" value="Search" type="submit">
      &nbsp;&nbsp;
      <input type="reset">
    </td>
  </tr>
  
</table>
</form>

<script type="text/javascript">
var show = [];
var categoryTree;

// get bookmarked state of feature type tree
var catTreeBookmarkedState = YAHOO.util.History.getBookmarkedState("catTree");

// if there is no bookmarked state, assign the default state:
var catTreeInitialState = catTreeBookmarkedState || ";";

YAHOO.util.Dom.setStyle('nojs', 'display', 'none');
YAHOO.util.Dom.setStyle('js', 'display', 'block');

function treeInit() {
    categoryTree = new YAHOO.widget.TreeView("catSelectors", ${jsonMcv});
    categoryTree.setNodesProperty('propagateHighlightUp', true);
    categoryTree.setNodesProperty('propagateHighlightDown', true);
    categoryTree.subscribe('clickEvent', categoryTree.onEventToggleHighlight);

    categoryTree.subscribe('expand', function(node) {
	showHide = YAHOO.util.Dom.get('showHide');
	showHide.textContent = 'Hide';
	});

    categoryTree.subscribe('collapse', function(node) {
	showHide = YAHOO.util.Dom.get('showHide');
	if (node.index == 1) { showHide.textContent = 'Show'; }
	});

    categoryTree.render();

    show = categoryTree.getNodesByProperty('expanded', 1);

    function panelHandler() {
	var u = categoryTree.getNodeByElement(this);
	return overlib(u.data.help, ANCHOR, this.id, ANCHORALIGN, 'UR', 'UL', STICKY, CAPTION, u.data.head, CSSCLASS, BGCLASS, 'catBG', FGCLASS, 'catFG', CAPCOLOR, '#002255', CAPTIONFONTCLASS, 'catCap', CLOSEFONTCLASS, 'catCapClose', DELAY, 600, WIDTH, 300, CLOSECLICK, CLOSETEXT, 'Close X');
    }

    var els = categoryTree.getnodesByProperty('tree', categoryTree);
    var contextElements = [];
    for (e in els) {
	contextElements.push(els[e].labelElId);
    }

    YAHOO.util.Event.addListener(contextElements, 'mouseover', panelHandler);
    YAHOO.util.Event.addListener(contextElements, 'mouseout', nd);
} // treeInit

// register the catTree; must take place before initializing history manager.
YAHOO.util.History.register('catTree', catTreeInitialState, function (state) {
    // This is called after YAHOO.util.History.navigate, or after the user has
    // clicked the back or forward button.

    if (catTreeInitialState != state) {
        restoreTree(state);
    }
});

// use the browser history manageer onReady method to instantiate the TreeView
// widget.
YAHOO.util.History.onReady(function() {
    var currentState;

    treeInit();

    // This is the tricky part.  The onLoad event is fired when the user comes
    // back to the page using the back button.  In this case, the actual tab
    // that needs to be selected corresponds to the last tab selected before
    // leaving the page, and not the initially selected tab.  This can be
    // retrieved using getCurrentState.

    currentState = YAHOO.util.History.getCurrentState("catTree");
    restoreTree(currentState);
});

YAHOO.util.History.initialize("yui-history-field", "yui-history-iframe");

function hNodes() {
    var hiLit = categoryTree.getNodesByProperty('highlightState', 1);
    var exp = categoryTree.getNodesByProperty('expanded', 0);
    var qf = YAHOO.util.Dom.get('markerQF');

    var ex = [];	// list of expanded nodes
    var idx = [];

    // build list of expanded nodes
    if (!YAHOO.lang.isNull(exp)) {
	for (e in exp) { ex.push(exp[e].index); }
    }

    // remove old feature selections from form
    var mqf = new YAHOO.util.Element('markerQF');
    var mcvparams = mqf.getElementsByClassName('mcv', 'input');
    for (c in mcvparams) {
	mcvparams[c].parentNode.removeChild(mcvparams[c]);
    }

    // add feature selections to form
    if (!YAHOO.lang.isNull(hiLit)) {
	var el;
	for (var i = 0; i < hiLit.length; i++) {
	    idx.push(hiLit[i].index);
	    el = document.createElement('input');
	    el.setAttribute('type', 'hidden');
	    el.setAttribute('name', 'mcv');
	    el.setAttribute('class', 'mcv');
	    el.setAttribute('value', hiLit[i].data.key);
	    YAHOO.util.Dom.insertAfter (el, YAHOO.util.Dom.getFirstChild(qf));
	}
    }

    // encode/save current state to history manager
    var state = ';';
    state = idx.join(':') + ';' + ex.join(':');

    if (YAHOO.lang.isNull(state)) { state = ';'; }

    YAHOO.util.History.navigate('catTree', state);
    qf.submit();
}


function resetChecks() {
    categoryTree.getRoot().unhighlight();
}

function resetTree() {
    resetChecks();
    categoryTree.getRoot().collapse();
    for (e in show) { show[e].expand(); }
}

function restoreTree(state) {
    var sp = state.split(';');
    var hl = [];
    var ex = [];

    if (sp.length == 2) {
	if (sp[0].length > 0) { hl = sp[0].split(':'); }
	if (sp[1].length > 0) { ex = sp[1].split(':'); }
    }

    for (h in hl) { categoryTree.getNodeByIndex(hl[h]).highlight(); }

    for (e in ex) { categoryTree.getNodeByIndex(ex[e]).collapse(); }
}

YAHOO.util.Event.addListener('resetchecks', 'click', resetChecks);
YAHOO.util.Event.addListener(YAHOO.util.Dom.getElementsByClassName('reset'),
    'click', resetTree);

function showHideCat(e) {
    var txt;
    this.innerText ? txt=this.innerText : txt=this.textContent;

    if (txt == 'Hide') { categoryTree.getRoot().collapseAll(); }
    else { categoryTree.getRoot().expandAll(); }

    var els = categoryTree.getNodesByProperty('tree', categoryTree);
    var contextElements = [];

    for (e in els) { contextElements.push(els[e].labelElId); }
}

YAHOO.util.Event.addListener('show', 'click', showHideCat, 'showHide');
YAHOO.util.Event.addListener('hide', 'click', showHideCat, 'showHide');
</script>
${templateBean.templateBodyStopHtml}
