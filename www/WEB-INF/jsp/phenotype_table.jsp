<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "mgi.frontend.datamodel.Genotype" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ page import = "org.jax.mgi.fewi.config.ContextLoader" %>


<%@ page trimDirectiveWhitespaces="true" %>

<%
NotesTagConverter ntc = new NotesTagConverter(); 
%>

<!-- Combo-handled YUI JS files: 
<script type="text/javascript" src="http://yui.yahooapis.com/combo?2.8.2r1/build/utilities/utilities.js&2.8.2r1/build/datasource/datasource-min.js&2.8.2r1/build/autocomplete/autocomplete-min.js&2.8.2r1/build/container/container-min.js&2.8.2r1/build/menu/menu-min.js&2.8.2r1/build/button/button-min.js&2.8.2r1/build/paginator/paginator-min.js&2.8.2r1/build/datatable/datatable-min.js&2.8.2r1/build/history/history-min.js&2.8.2r1/build/json/json-min.js&2.8.2r1/build/resize/resize-min.js&2.8.2r1/build/selector/selector-min.js&2.8.2r1/build/tabview/tabview-min.js&2.8.2r1/build/treeview/treeview-min.js"></script>
-->

<!-- Combo-handled YUI CSS files:
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/combo?2.8.2r1/build/assets/skins/sam/skin.css">
-->

<script type="text/javascript" src="${configBean.WEBSHARE_URL}js/jquery-1.10.2.min.js"></script>

<style type="text/css">
  .phenoSummarySystemRow {font-weight:bold; }
  .phenoSummaryTermRow {display:none;}
  .stripe1 {background-color:#FFF;}
  .stripe2 {background-color:#EFEFEF;}
  .arrowRight,.arrowDown {float:right;}
  .leftBorder {border-left: solid 1px #ccc;}
  .genoHeader { text-align:center; padding: 2px 2px 2px 2px; }
  .provider { text-align:center; padding: 0px; font-size:9px;}
  #phenoSystemTH {text-align:left; font-size:120%; font-weight:bold; padding-top:5px;}
  .noDisplay{display:none;}
  .noWrap {white-space: nowrap;}
  th.genoBorder, td.genoBorder{border-left: solid 2px #AAA;}
  th.rightGenoBorder,td.rightGenoBorder{border-right: solid 2px #AAA;}
  th.borderUnder{border-bottom:solid 1px #ccc;}
  td.borderUnder{border-bottom:solid 1px #F8F8F8;}
  th.sexBorder,td.sexBorder{border-left: solid 1px #ccc;}
  .provider img {   display: block;   margin-left: auto;   margin-right: auto; }
#phenotable_id { border-spacing:0px; border-collapse:collapse; border: 2px solid #AAA;}
#phenotable_id td { width: 18px; padding: 4px 2px;}

.genoButton{padding: 2px; margin-top:3px; margin-left:1px; margin-right:1px; cursor:pointer;}
.phenotable_glyph {width:15px;height:15px;}

</style>

<%@ include file="/WEB-INF/jsp/phenotype_table_geno_imports.jsp" %>

<style>
.yui-skin-sam tr.yui-dt-even { background-color:#FFF; } /* white */
.yui-skin-sam tr.yui-dt-odd { background-color:#f1f1f1; } /* light grey */
td.border { border-bottom:thin solid grey; border-top:thin solid grey; border-left:thin solid grey; border-right:thin solid grey }
td.padLR { padding-left:4px; padding-right:4px }
td.padTop { padding-top:4px }
.smaller { font-size: 80% }
</style>

<table class="phenoTable">
<tr>
  <td class="rightBorderThinGray padLR" ALIGN="right" WIDTH="1%" style="vertical-align:top;"><span class="label">Key:</span></td>
  <td NOWRAP="nowrap" class="padLR" style="vertical-align:top;">

    <!-- begin key -->
    <TABLE WIDTH="!" BORDER="0" CELLPADDING="1" CELLSPACING="1" BGCOLOR="#888888">
    <TR>
	    <TD class="border padLR" ALIGN="center" BGCOLOR="#FFD57A"><span class='smaller'>hm</span></TD>
	    <TD class="border padLR" BGCOLOR="#FFD57A" NOWRAP="nowrap"><span class='smaller'>homozygous</span></TD>
	    <TD class="border padLR" ALIGN="center" BGCOLOR="#BCFFEB"><span class='smaller'>ht</span></TD>
	    <TD class="border padLR" BGCOLOR="#BCFFEB" NOWRAP="nowrap"><span class='smaller'>heterozygous</span></TD>
	    <TD class="border padLR" ALIGN="center" BGCOLOR="#FFC7F4"><span class='smaller'>tg</span></TD>
	    <TD class="border padLR" BGCOLOR="#FFC7F4" NOWRAP="nowrap"><span class='smaller'>involves transgenes</span></TD>
	    <TD class="border padLR" ALIGN="center" BGCOLOR="#FFFFFF"><span class='smaller'>&#8730;</span></TD>
	    <TD class="border padLR" BGCOLOR="#FFFFFF" NOWRAP="nowrap"><span class='smaller'>phenotype observed</span></TD>
    </TR>
    <TR>
	    <TD class="border padLR" ALIGN="center" BGCOLOR="#E7FFBC"><span class='smaller'>cn</span></TD>
	    <TD class="border padLR" BGCOLOR="#E7FFBC" NOWRAP="nowrap"><span class='smaller'>conditional&nbsp;genotype&nbsp;</span></TD>
	    <TD class="border padLR" ALIGN="center" BGCOLOR="#E0E0FF"><span class='smaller'>cx</span></TD>
	    <TD class="border padLR" BGCOLOR="#E0E0FF" NOWRAP="nowrap"><span class='smaller'>complex: > 1 genome feature</span></TD>
	    <TD class="border padLR" ALIGN="center" BGCOLOR="#F7DCC0"><span class='smaller'>ot</span></TD>
	    <TD class="border padLR" BGCOLOR="#F7DCC0" NOWRAP="nowrap"><span class='smaller'>other: hemizygous, indeterminate,...</span></TD>
	    <TD class="border padLR" ALIGN="center" BGCOLOR="#FFFFFF"><span class='smaller'>N</span></TD>
	    <TD class="border padLR" BGCOLOR="#FFFFFF" NOWRAP="nowrap"><span class='smaller'>normal phenotype</span></TD>
    </TR>
    </TABLE><!-- end legend -->

  </td>
</tr>


<tr>
  <td class="rightBorderThinGray padTop padLR" ALIGN="right" WIDTH="1%" style="vertical-align:top;"><span class="label">Genotype/<br/>Background:</span></td>
  <td class="padTop padLR">
    <%@ include file="/WEB-INF/jsp/phenotype_table_geno_legend.jsp" %>
  </td>
</tr>

<tr>
  <td class="rightBorderThinGray padTop padLR" ALIGN="right" WIDTH="1%" style="vertical-align:top;"><span class="label">Phenotypes:</span></td>
  <td class="padTop padLR">

<!-- ---------------------------------------------------------- -->
<!-- pheno table container -->
<table class="phenotable" id="phenotable_id">

<%
// The next three loops generate the three-row header.  
// Loop 1 - genotype popup link; colspans all provider columns for this genotype
// Loop 2 - sex differentiation; colspans all providers for given sex
// Loop 3 - creates provider text
%>

<tr class="stripe1">
<th id="phenoSystemTH">Affected Systems </th>
<c:forEach var="phenoTableGenotype" items="${phenoTableGenotypes}" varStatus="gStatus">
	  <th class="genoHeader genoBorder <c:if test="${gStatus.last}">rightGenoBorder</c:if>" colspan="${phenoTableGenotype.columnSpan}">
	  <c:set var="genotype" value="${phenoTableGenotype.genotype}" scope="request"/>
	  <div class="${genotype.genotypeType}Geno ${genotype.genotypeType}GenoButton genoButton">
	  	<a href='${configBean.FEWI_URL}allele/genoview/${phenoTableGenotype.genotype.primaryID}?counter=${phenoTableGenotype.genotypeSeq}' target="_blank" class='genoLink smaller' title='phenotype details'>
	  ${phenoTableGenotype.genotype.genotypeType}${phenoTableGenotype.genotypeSeq}</a></div>
	
	  </th>
</c:forEach>
</tr>

<tr class="stripe1">
<th>
  <div>
    <span style="float:left;" class="smaller">
    <A id='showPhenoButton' style='cursor: pointer; color:blue;' CLASS='MP'>show</A> or
    <A id='hidePhenoButton' style='cursor: pointer; color:blue;' CLASS='MP'>hide</A> all annotated terms
    </span>

    <c:if test="${hasSexCols}">
      <span style="float:right;" class="smaller">Sex:</span>
    </c:if>

    <c:if test="${!hasSourceCols}">
      <br/>
    </c:if>

  </div>
</th>
<c:forEach var="phenoTableGenotype" items="${phenoTableGenotypes}" varStatus="gStatus">
  <c:if test="${phenoTableGenotype.splitSex == '1' }">
      <th class="genoBorder borderUnder" colspan="${phenoTableGenotype.columnSpan / 2}" style="text-align:center;">
        <span><img class="phenotable_glyph" src="${configBean.FEWI_URL}assets/images/Venus_symbol.svg"/></span>
      </th>
      <th class="borderUnder <c:if test="${gStatus.last}">rightGenoBorder</c:if> sexBorder" colspan="${phenoTableGenotype.columnSpan / 2}" style="text-align:center;">
        <span><img class="phenotable_glyph" src="${configBean.FEWI_URL}assets/images/Mars_symbol.svg"/></span>
      </th>
  </c:if>
  <c:if test="${phenoTableGenotype.splitSex == '0' }">
      <th class="genoBorder borderUnder <c:if test="${gStatus.last}">rightGenoBorder</c:if>" colspan="${phenoTableGenotype.columnSpan}"style="text-align:center;">
        <span><c:if test="${phenoTableGenotype.htmlSex=='M'}"><img class="phenotable_glyph" src="${configBean.FEWI_URL}assets/images/Mars_symbol.svg"/></c:if>
        <c:if test="${phenoTableGenotype.htmlSex=='F'}"><img class="phenotable_glyph" src="${configBean.FEWI_URL}assets/images/Venus_symbol.svg"/></c:if>&nbsp;</span>
      </th>
  </c:if>
</c:forEach>
</tr>

<script type="text/javascript">
    var cellIDs = [];
</script>
<c:if test="${hasSourceCols}">
<tr class="stripe1" id="sourceRow">
<th style="text-align:right;" class="smaller"><span onMouseOut="nd();" onMouseOver="return overlib('Mouse over source labels to view Data Interpretation and Phenotyping Centers for high-throughput phenotype annotations.<P><B>Data Interpretation Center:</B> The source of Mammalian Phenotype calls made from primary phenotyping data.<P><B>Phenotyping Center:</B> The source of primary phenotyping data (where phenotyping tests were performed for annotations shown).', LEFT, WIDTH, 400);">Source:</span><br>
<img src="${configBean.WEBSHARE_URL}images/help_small_transp.gif" alt="Help" onMouseOut="nd();" onMouseOver="return overlib('Mouse over source labels to view Data Interpretation and Phenotyping Centers for high-throughput phenotype annotations.<P><B>Data Interpretation Center:</B> The source of Mammalian Phenotype calls made from primary phenotyping data.<P><B>Phenotyping Center:</B> The source of primary phenotyping data (where phenotyping tests were performed for annotations shown).', LEFT, WIDTH, 400);">
</th>
<c:forEach var="phenoTableGenotype" items="${phenoTableGenotypes}" varStatus="gStatus">
<c:if test="${!phenoTableGenotype.diseaseOnly }">
  <c:if test="${phenoTableGenotype.splitSex == '1' }">
    <c:forEach var="phenoTableProvider" items="${phenoTableGenotype.phenoTableProviders}" varStatus="pStatus">
      <th class="<c:if test="${pStatus.index==0 }">genoBorder </c:if> borderUnder" style="vertical-align: bottom; padding-bottom: 10px">
      <div id="ptpa${phenoTableProvider.uniqueKey}" style="font-size: 80%; font-weight: 300; -webkit-transform: rotate(270deg); transform: rotate(270deg); white-space: nowrap;" onMouseOut="nd();" onMouseOver="return overlib('${phenoTableProvider.providerDescription}', LEFT, WIDTH, 200);">${phenoTableProvider.providerString}</div>
      </th>
      <script type="text/javascript">
	  cellIDs.push("ptpa${phenoTableProvider.uniqueKey}");
      </script>
    </c:forEach>    
    <c:forEach var="phenoTableProvider" items="${phenoTableGenotype.phenoTableProviders}" varStatus="pStatus">
      <th class="borderUnder <c:if test="${pStatus.index==0}">sexBorder </c:if> <c:if test="${pStatus.last && gStatus.last}">rightGenoBorder</c:if>" style="vertical-align: bottom; padding-bottom: 10px">
      <div id="ptpb${phenoTableProvider.uniqueKey}" style="font-size: 80%; font-weight: 300; -webkit-transform: rotate(270deg); transform: rotate(270deg); white-space: nowrap;" onMouseOut="nd();" onMouseOver="return overlib('${phenoTableProvider.providerDescription}', LEFT, WIDTH, 200);">${phenoTableProvider.providerString}</div>
      <script type="text/javascript">
	  cellIDs.push("ptpb${phenoTableProvider.uniqueKey}");
      </script>
      </th>
    </c:forEach>    
  </c:if>

  <c:if test="${phenoTableGenotype.splitSex == '0' }">
    <c:forEach var="phenoTableProvider" items="${phenoTableGenotype.phenoTableProviders}" varStatus="pStatus" >
      <th class="<c:if test="${pStatus.index==0 }">genoBorder </c:if>  <c:if test="${pStatus.last && gStatus.last}">rightGenoBorder</c:if> borderUnder" style="vertical-align: bottom; padding-bottom: 10px">
      <div id="ptpc${phenoTableProvider.uniqueKey}" style="font-size: 80%; font-weight: 300; -webkit-transform: rotate(270deg); transform: rotate(270deg); white-space: nowrap;" onMouseOut="nd();" onMouseOver="return overlib('${phenoTableProvider.providerDescription}', LEFT, WIDTH, 200);">${phenoTableProvider.providerString}</div>
      <script type="text/javascript">
	  cellIDs.push("ptpc${phenoTableProvider.uniqueKey}");
      </script>
      </th>
    </c:forEach>    
  </c:if>
</c:if>
</c:forEach>
</tr>
</c:if>

<c:forEach var="phenoTableSystem" items="${phenoTableSystems}" varStatus="systemStatus">
    <tr id="${phenoTableSystem.cssId}_row" class="phenoSummarySystemRow  ${systemStatus.index % 2==0 ? ' stripe2' : ' stripe1'}">
      <td  id="${phenoTableSystem.cssId}" class="noWrap borderUnder" style="min-width:300px" >
        <div style="text-align:left; cursor: pointer;">
         <!-- Add the toggle arrows -->
          <span class="arrowRight ${phenoTableSystem.cssClass}">
          	<img src="http://www.informatics.jax.org/webshare/images/rightArrow.gif"/>
          </span>
          <span style="display:none;" class="arrowDown ${phenoTableSystem.cssClass}">
          	<img src="http://www.informatics.jax.org/webshare/images/downArrow.gif"/>
          </span>
          ${phenoTableSystem.system} 
          <!-- (systemSeq= ${phenoTableSystem.systemSeq}) -->
        </div>
      </td>

      <!-- TDs for grid system row -->
      <c:set var="genoID" value="" />
      <c:set var="sex" value="" />
      <c:forEach var="cell" items="${phenoTableSystem.cells}" varStatus="cStatus">
		<td class="<c:if test="${genoID!=cell.genotypeID}">genoBorder </c:if> borderUnder <c:if test="${genoID==cell.genotypeID && sex=='F' && cell.sex=='M'}">sexBorder </c:if> <c:if test="${cStatus.last}">rightGenoBorder</c:if>" style="text-align:center;">
          <c:if test="${cell.hasCall}">
	          <a href='${configBean.FEWI_URL}allele/genoview/${cell.genotypeID}?counter=${cell.genotypeSeq}#${phenoTableSystem.cssId}' target="_blank" class='genoLink' style="font-weight:bold;" title='details'>
	  			<c:out value="${cell.callString}" escapeXml="false"/></a>
  			</c:if>
        </td>
        <c:set var="genoID" value="${cell.genotypeID }"/>
        <c:set var="sex" value="${cell.sex}" />
      </c:forEach>
    </tr>

    <c:forEach var="phenoTableTerm" items="${phenoTableSystem.phenoTableTerms}" >
      <tr class="phenoSummaryTermRow ${phenoTableSystem.cssClass} ${systemStatus.index % 2==0 ? ' stripe2' : ' stripe1'}">
        <td  class="noWrap borderUnder" style="min-width:300px" >
            <div style="text-align:left; ">
              <span style="margin-left:${phenoTableTerm.displayIndent}px;">
                 ${phenoTableTerm.term}
                 <!-- (termSeq= ${phenoTableTerm.termSeq}) -->
              </span>
            </div>
        </td>

      <!-- TDs for grid term row -->
      <c:set var="genoID" value="" />
      <c:set var="sex" value="" />
        <c:forEach var="cell" items="${phenoTableTerm.cells}" varStatus="cStatus">
          <td class="<c:if test="${genoID!=cell.genotypeID}">genoBorder </c:if> borderUnder <c:if test="${genoID==cell.genotypeID && sex=='F' && cell.sex=='M'}">sexBorder </c:if> <c:if test="${cStatus.last}">rightGenoBorder</c:if>" style="text-align:center;">
		    <c:if test="${cell.hasCall}">
		    <a href='${configBean.FEWI_URL}allele/genoview/${cell.genotypeID}?counter=${cell.genotypeSeq}#${phenoTableSystem.cssId}_${phenoTableTerm.cssId}' target="_blank" class='genoLink smaller' title='details'>
  			<c:out value="${cell.callString}" escapeXml="false"/></a>
  			</c:if>
          </td>
          <c:set var="genoID" value="${cell.genotypeID }"/>
          <c:set var="sex" value="${cell.sex}" />
        </c:forEach>
      </tr>
      </c:forEach>
  </c:forEach>
</table>
<!-- ---------------------------------------------------------- -->


  </td>
</tr>

</table>




<script type="text/javascript">
	/*
	* Included here are some attempts at speeding up jquery performance on beastly allele pages such as Trp53 and Apoetm1Unc
	*/
	var showButtonCache = null;
	function setShowButtonCache()
	{
		showButtonCache = [];
		showButtonCache['terms'] = $('.phenoSummaryTermRow','#phenotable_id');
		showButtonCache['arrowRight'] = $('.arrowRight','#phenotable_id');
		showButtonCache['arrowDown'] = $('.arrowDown','#phenotable_id');
		return showButtonCache;
	}
  $('#showPhenoButton').click(function(){
  	if (showButtonCache==null) { showButtonCache = setShowButtonCache(); }
    showButtonCache['terms'].show();
    showButtonCache['arrowRight'].hide();
    showButtonCache['arrowDown'].show();
  });
  $('#hidePhenoButton').click(function(){
	if (showButtonCache==null) { showButtonCache = setShowButtonCache(); }
	showButtonCache['terms'].hide();
	showButtonCache['arrowRight'].show();
	showButtonCache['arrowDown'].hide();
  });

  <c:forEach var="phenoTableSystem" items="${phenoTableSystems}" >
    $('#${phenoTableSystem.cssId}','#phenotable_id').click(function(){
      $('.${phenoTableSystem.cssClass}','#phenotable_id').toggle();
    /*   $(this).nextAll('tr').each( function() {
	        if ($(this).hasClass('phenoSummarySystemRow')) {
	            return false;
	        }
	        $(this).toggle();
	    }); */
/* 	    var tr = $(this.parentElement).nextAll('tr');
	    for (i = 0; i < tr.length; i++) {
	      var class1 = $(tr[i]).attr('class');
	      if (class1 == 'phenoSummarySystemRow')
	    	  return false;
	      $(tr[i]).toggle();
	    } */
    });

  </c:forEach>

  /* --- specific to genotype popup windows ----------------------------- */
  var popupNextX = 0;	// x position of top-left corner of next popup
  var popupNextY = 0;	// y position of top-left corner of next popup

  // pop up a new window for displaying details from the given 'url' for the
  // given genotype key.
  function popupGenotype (url, counter, id)
  {
    // new window will be named using the genotype key with a prefix
    var windowName;
    windowName = "genoPopup_" + id + "_" + counter;

    // open the window small but scrollable and resizable
    var child = window.open (url, windowName,
	'width=800,height=600,resizable=yes,scrollbars=yes,alwaysRaised=yes');

    // move the new window and bring it to the front
    child.moveTo (popupNextX, popupNextY);
    child.focus();

    // set the position for the next new window (at position 400,400 we will
    // start over at 0,0)

    if (popupNextX >= 400) {
	popupNextX = 0;
	popupNextY = 0;
    }
    else {
	popupNextX = popupNextX + 20;
	popupNextY = popupNextY + 20;
    }
    return;
  }

/* need to adjust the heights/widths of cells in the Source row of the
 * phenotype table
 */

    var maxWidth = 0;	// largest width of source items
    var maxHeight = 0;	// largest height of source items

    for (i = 0; i < cellIDs.length; i++) {
	var rect = document.getElementById(cellIDs[i]).getBoundingClientRect();
	maxWidth = Math.max(maxWidth, rect['width']);
	maxHeight = Math.max(maxHeight, rect['height']);
    }

    maxWidth = Math.floor(maxWidth) + 10;		// add padding
    maxHeight = Math.floor(maxHeight) + 10;

    // apply the heights & widths

    if (document.getElementById('sourceRow')) document.getElementById('sourceRow').style.height = maxHeight + 'px';

    for (i = 0; i < cellIDs.length; i++) {
	var el = document.getElementById(cellIDs[i]);
	el.style.width = maxWidth + 'px';
	el.style.height = maxHeight = 'px';
	el.style.marginLeft = '3px';
    } 
</script>


