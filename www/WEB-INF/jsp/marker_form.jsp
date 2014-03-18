<style type="text/css">
td.padded { padding: 4px; }
td.top { vertical-align: top; }
</style>


<!-- id is used internally; name is used by pheno popup -->
<form method="GET" action="${configBean.FEWI_URL}marker/summary" id="markerQF" onSubmit="hNodes(); return false;" name="queryForm">
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
        contains 
		<input name="nomen" size="25" class="grayBackground" type="text" value="${queryForm.nomen}">
		in current & old symbols/names, synonyms, alleles, homologs
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
	        <select name="chromosome" id="chromosomeDropList" multiple="" size="5" class="grayBackground">
		    		<fewi:selectOptions items="${chromosomes}" values="${queryForm.chromosome}" />
			</select>
	      </dd>
	    </dl>
	  </td>
	  <td class="padded top">
	    <dl>
	      <dt class="qfLabel"><a onclick="javascript:openUserhelpWindow('GENE_help.shtml#cm_offset'); return false;" href="${helpPage}#cm_offset">cM Position</a>: </dt>
	      <dd>
	        between
	        <input name="cm" size="13" class="grayBackground" type="text" value="${queryForm.cm}">
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
	        <input name="coordinate" size="23" class="grayBackground" type="text" value="${queryForm.coordinate}">
	        <select name="coordUnit" class="grayBackground">
       			<fewi:selectOptions items="${queryForm.coordUnitDisplay}" value="${queryForm.coordUnit}" />
      		</select>	
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
	        <input name="startMarker" size="12" class="grayBackground" type="text" value="${queryForm.startMarker}">
	        and <input name="endMarker" size="12" class="grayBackground" type="text" value="${queryForm.endMarker}">
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
	        &nbsp;
	        <input name="go" size="35" type="text" value="${queryForm.go}">
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
          <input name="interpro" size="35" class="grayBackground" type="text" value="${queryForm.interpro}">
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
	        <textarea name="phenotype" rows="2" cols="60">${queryForm.phenotype}</textarea>
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

YAHOO.util.Dom.setStyle('nojs', 'display', 'none');
YAHOO.util.Dom.setStyle('js', 'display', 'block');

function treeInit() {
    categoryTree = new YAHOO.widget.TreeView("catSelectors", ${jsonMcv});
    categoryTree.setNodesProperty('propagateHighlightUp', true);
    categoryTree.setNodesProperty('propagateHighlightDown', true);
    categoryTree.subscribe('clickEvent', categoryTree.onEventToggleHighlight);

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
} // treeInit

$(function(){
	
	// set form reset function
	$("#markerQF").on("reset",markerQfReset);
	
    treeInit();
    
    // restore tree if we have params
	var mcvKeys=[];
    <c:forEach items="${queryForm.mcv}" var="mcvKey">
    	mcvKeys.push("${mcvKey}");
    </c:forEach>
    restoreTree(mcvKeys);
});

function restoreTree(mcvKeys)
{
	if(mcvKeys && mcvKeys.length>0)
	{
		var allNodes = categoryTree.getNodesByProperty();
		var allKeys=[];
		for(var i=0; i<allNodes.length; i++)
		{
			var an = allNodes[i];
			allKeys.push(an.data.key);
			if($.inArray(an.data.key,mcvKeys)>=0)
			{
				an.highlight();
				expandParents(an);
			}
		}
		console.log(allKeys.join(","));
	}
}

function expandParents(node)
{
	if('expand' in node)
	{
		node.expand();
		if('parent' in node && node.parent)
		{
			expandParents(node.parent);
		}
	}
}

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


YAHOO.util.Event.addListener('resetchecks', 'click', resetChecks);
YAHOO.util.Event.addListener(YAHOO.util.Dom.getElementsByClassName('reset'),'click', resetTree);

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


function markerQfReset(e)
{
	e.preventDefault();
	var mqf=$("#markerQF");
	  
	// reset tree
	resetTree();
	
	// reset all check boxes and textareas
	  
	mqf.find('input[type=checkbox]:checked').removeAttr('checked');
	mqf.find('input[type=text], textarea').val("");
	
	mqf.find("#chromosomeDropList").val("any").change();
}
</script>
