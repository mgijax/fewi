<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
${templateBean.templateHeadHtml}

<title>Genes and Markers Query Form</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style type="text/css">
td.padded { padding: 4px; }
td.top { vertical-align: top; }
</style>

<script src="http://yui.yahooapis.com/2.8.1/build/history/history-min.js"></script>

<script>
</script>

${templateBean.templateBodyStartHtml}


<!-- header bar -->
<div id="titleBarWrapper" userdoc="GENE_help.shtml">	
	<span class="titleBarMainTitle">Genes and Markers Query Form</span>
</div>
<c:set var="helpPage" value="${configBean.USERHELP_URL}GENE_help.shtml"/>

<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">

<!-- query form structural table -->

<table class="detailStructureTable">

<!-- id is used internally; name is used by pheno popup -->
<form method="GET" action="${configBean.WI_URL}searches/marker_report.cgi" id="markerQF" onSubmit="hNodes(); return false;" name="queryForm">
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
    <td class="queryCat1 padded top">Gene/Marker</td>
    <td class="queryParams1 padded top">
    <dl>
      <dt class="qfLabel">
      <a onclick="javascript:openUserhelpWindow('GENE_help.shtml#gene_nomenclature'); return false;" href="${helpPage}#gene_nomenclature">Gene/Marker Symbol/Name</a>:
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
    <td class="queryCat2 padded top">Feature Type</td>
    <td class="queryParams2 padded top">
     <div id="nojs">
	     <p class='example'><br/>Click to select one or more <a href="${configBean.USERHELP_URL}GENE_help.shtml#marker_type">feature types.</a><br/>
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
	<a onclick='javascript:openUserhelpWindow("GENE_help.shtml#marker_type"); return false;' href="${helpPage}#marker_type"
        >feature types.</a><br/>
	If no boxes are checked, then all feature types are included.<br/>
	Counts reflect total MGI Markers in each feature type category.<br/>
        </p>            
    </div>
    </td>
  </tr>


  <!-- row 3-->
  <tr>
    <td class="queryCat1 padded top">Map position</td>
    <td class="queryParams1 padded top">
      <table>
	<tr>
	  <td class="padded top">
	    <dl>
	      <dt class="qfLabel"><A onclick='javascript:openUserhelpWindow("GENE_help.shtml#chromosome"); return false;' HREF="${helpPage}#chromosome">Chromosome(s)</A>: </dt>
	      <dd>
	        <select name="chromosome" multiple="" size="5" class="grayBackground">
		  <option value="" selected="">Any</option>${chromosomes}
		</select>
	      </dd>
	    </dl>
	  </td>
	  <td class="padded top">
	    <dl>
	      <dt class="qfLabel"><a onclick="javascript:openUserhelpWindow('GENE_help.shtml#cm_offset'); return false;" href="${helpPage}#cm_offset">cM Position</a>: </dt>
	      <dd>
	        between
	        <input type="hidden" name="op:offset" value="between">
	        <input name="offset" size="13" class="grayBackground" type="text">
	      </dd>
	      <dd>
	        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="example">e.g., "10.0-40.0"</span>
	      </dd>
	    </dl>
	  </td>
	</tr>
	<tr>
	  <td class="padded top" colspan="2">
	    <dl>
	      <dt class="qfLabel"><a onclick="javascript:openUserhelpWindow('GENE_help.shtml#coordinates'); return false;" href="${helpPage}#coordinates">Genome Coordinates</a>: <span class="example">from ${genomeBuild}</span>
	      </dt>
	      <dd>
	        between
	        <input type="hidden" name="op:coords" value="between">
	        <input name="coords" size="23" class="grayBackground" type="text">
	        <select name="coordUnits" class="grayBackground"><option value="bp" selected="">bp</option><option value="Mbp">Mbp</option></select>
	      </dd>
	      <dd>
	        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="example">e.g., "125.618-125.622" Mbp</span>
	      </dd>
	      <dt class="qfLabel">
	        <a onclick="javascript:openUserhelpWindow('GENE_help.shtml#marker_range'); return false;" href="${helpPage}#marker_range">Marker range</a>:
	      <span class="example">use current symbols</span>
	      </dt>
	      <dd>
	        between
	        <input name="startMarker" size="12" class="grayBackground" type="text">
	        and <input name="endMarker" size="12" class="grayBackground" type="text">
	      </dd>
	      <dd>
	        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="example">
		e.g., between "D19Mit32" and "Tbx10"</span>
	      </dd>
	    </dl>
	  </td>
	</tr>
      </table>
    </td>
  </tr>

  <!-- row 4-->
  <tr>
    <td class="queryCat2">Gene Ontology<br/>Classifications</td>
    <td class="queryParams2">
      <table>
	<tr>
	  <td class="padded top">
	    <dl>
	      <dt class="qfLabel">
	        <a onclick="javascript:openUserhelpWindow('GENE_help.shtml#gene_ontology'); return false;" href="${helpPage}#gene_ontology">Gene Ontology (GO) Classifications</a>:
	      </dt>
	      <dd>
	        contains
		<input type="hidden" name="op:go_term" value="contains">
	        &nbsp;
	        <input name="go_term" size="35" type="text">
	        in
	      </dd>
	    </dl>
	    <span class="vocabLink">Browse <a href="${configBean.WI_URL}searches/GO_form.shtml">Gene Ontology (GO)</a></span> 
	  </td>
	  <td class="padded top">
	    <span style="line-height: 150%">
	    <input name="_Ontology_key" value="Molecular Function" checked="" type="checkbox"> Molecular Function<br>
	    <input name="_Ontology_key" value="Biological Process" checked="" type="checkbox"> Biological Process<br>
	    <input name="_Ontology_key" value="Cellular Component" checked="" type="checkbox"> Cellular Component<br> 
	    </span>
	  </td>
	</tr>
      </table>
    </td>
  </tr>


  <!-- row 5-->
  <tr>
    <td class="queryCat1">Protein domains</td>
    <td class="queryParams1">
      <dl>
        <dt class="qfLabel">
	  <a onclick="javascript:openUserhelpWindow('GENE_help.shtml#interpro'); return false;" href="${helpPage}#interpro">InterPro Protein Domains</a>:
        </dt>
        <dd>
	  contains
          <input name="interpro" size="35" class="grayBackground" type="text">
        </dd>
      </dl>
      <span class="vocabLink">Browse <a href="${configBean.FTP_URL}MGI_InterProDomains.rpt">InterPro protein domains</a>
      </span>
    </td>
  </tr>

  <!-- row 6-->
  <tr>
    <td class="queryCat2">Mouse phenotypes &amp;<br/>mouse models of<br/>human disease</td>
    <td class="queryParams2">
      <dl>
        <dt class="qfLabel">
	  <a onclick="javascript:openUserhelpWindow('GENE_help.shtml#phenotype'); return false;" href="${helpPage}#phenotype">Phenotype/Human Disease</a>:
        </dt>
        <dd>
          <table border="0" cellpadding="2" cellspacing="0">
	    <tr>
	      <td>
	        <span class="example">Enter any combination of phenotype terms,
	          disease terms, or IDs
	        </span><br>
	        <textarea name="phenotypes" rows="2" cols="60"></textarea>
	      </td>
	      <td>
	        <span class="example">
			<a onclick="javascript:openUserhelpWindow('MISC_boolean_search_help.shtml'); return false;" href="${configBean.USERHELP_URL}MISC_boolean_search_help.shtml">Hints</a>:<br>
		using AND and OR, quotes, partial word matching, ...
	        </span>
	      </td>
	    </tr>
	  </table>
        </dd>
      </dl>
      <span class="vocabLink">
        Select <a href="javascript:childWindow=window.open('${configBean.FEWI_URL}marker/phenoPopup', 'mywindow', 'status,width=350,height=400'); childWindow.focus()">Anatomical Systems Affected by Phenotypes</a><br>
	Browse <a href="${configBean.WI_URL}searches/MP_form.shtml">Mammalian Phenotype Ontology (MP)</a><br>
	Browse <a href="${configBean.FEWI_URL}vocab/omim/">Human Disease Vocabulary (OMIM)</a>
      </span>
    </td>
  </tr>

  <!-- row 7-->
  <tr>
	  <td class="queryCat1"><a name="output">Sorting and</a><br/>output format</td>
    <td class="queryParams1">
      <span class="qfLabel"><a onclick="javascript:openUserhelpWindow('GENE_help.shtml#sort_by'); return false;" href="${helpPage}#sort_by">Sort by</a>: </span>
      <input name="sort" value="Nomenclature" checked="" type="radio">&nbsp;Nomenclature&nbsp;&nbsp;<input name="sort" value="Genome Coordinates" type="radio">&nbsp;Genome Coordinates<br/>
    
      <span class="qfLabel"><a onclick="javascript:openUserhelpWindow('GENE_help.shtml#max_returned'); return false;" href="${helpPage}#max_returned">Maximum returned</a>: </span> 500 or
      <input type="hidden" name="*limit" value="500">
      <input name="noLimit" value="noLimit" type="checkbox"> no limit
      <br>
    
      <span class="qfLabel"><a onclick="javascript:openUserhelpWindow('GENE_help.shtml#output_format'); return false;" href="${helpPage}#output_format">Output</a>: </span>
      <select name="format"><option value="Web">Web</option><option value="Excel">Excel</option><option value="Tab-delimited">Tab-delimited</option></select>
    </td>
  </tr>

  <!-- Submit/Reset-->
  <tr>  
    <td colspan="2" align="left">
      <input class="buttonLabel" value="Search" type="submit">
      &nbsp;&nbsp;
      <input class="reset" type="reset">
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

    var els = categoryTree.getNodesByProperty('tree', categoryTree);
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

function addHidden (key, valu) {
    var form = document.forms['markerQF'];
    var input = document.createElement('input');
    input.type = 'hidden';
    input.name = key;
    input.value = valu;
    form.appendChild(input);
    return;
}

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
	    addHidden('mcv', hiLit[i].data.key);
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
