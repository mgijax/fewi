<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.link.ProviderLinker" %>
<%@ page import = "org.jax.mgi.fewi.util.link.IDLinker" %>
<%@ page import = "java.util.List" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>${title}</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  // Pull detail object into servlet scope
    // EXAMPLE - Marker foo = (Marker)request.getAttribute("foo");

    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailListGb1","detailCat2");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailListBg1","detailListBg2");
    IDLinker idLinker = (IDLinker) request.getAttribute("idLinker");
    VocabTerm term = (VocabTerm) request.getAttribute("term");
%>

<SCRIPT TYPE="text/javascript" SRC='${configBean.WEBSHARE_URL}js/hideshow.js'></SCRIPT>
    
<script language="Javascript">

function multipleBoxes(){
    if (document.sequenceForm.seqs.length > 1) {
	return 1;
    }
    return 0;
}

function selectAll(){
	if (multipleBoxes()) {
            for(var i = 0; i < document.sequenceForm.seqs.length; i++){
                document.sequenceForm.seqs[i].checked = 1;
            }
	}
	else {
	    document.sequenceForm.seqs.checked = 1;
	}
}

function unselectAll(){
	if (multipleBoxes()) {
            for(var i = 0; i < document.sequenceForm.seqs.length; i++){
                document.sequenceForm.seqs[i].checked = 0;
            }
	}
	else {
	    document.sequenceForm.seqs.checked = 0;
	}
}

function invertAll(){
	if (multipleBoxes()) {
            for(var i = 0; i < document.sequenceForm.seqs.length; i++){
                document.sequenceForm.seqs[i].checked =
                        !document.sequenceForm.seqs[i].checked;
	    }
        }
	else {
	    document.sequenceForm.seqs.checked =
                !document.sequenceForm.seqs.checked;
	}
}

function formatForwardArgs() {
	document.sequenceForm.action=document.controls.pullDown.options[document.controls.pullDown.selectedIndex].value;

	if (document.sequenceForm.action.indexOf("blast") >= 0) {
	    document.sequenceForm.target = "_blank";
	} else {
	    document.sequenceForm.target = "";
        }

	document.sequenceForm.submit();
}
</script>

<c:set var="sCount" value="1" scope="page"/>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper" userdoc="pirsf_detail_help.shtml">	
	<span class="titleBarMainTitle">Protein Superfamily Detail</span>
</div>

<!-- header table -->
<table class="summaryHeader">
<tr>
  <td class="summaryHeaderCat1">
       <span class="label">PIRSF</span><br/>
       <span class="label">Classification</span>
  </td>
  <td class="summaryHeaderData1">
    <span class="label">Term:</span> <span class="enhance">${term.term}</span><br/>
    <span class="label">ID:</span> <%= idLinker.getLink("PIRSF", term.getPrimaryId()) %>
  </td>
</tr>
</table>

<br/>

<div id="summary">
  <div id="breadbox">
    <div id="contentcolumn" style="margin: 0 335px 0 0">
      <div class="innertube">
	<span class="label">Mouse Protein Superfamily Annotations</span><p/>
	<span class="small" style="line-height:110%"><i>Select one or more mouse PIRSF members to download protein sequences or forward to NCBI BLAST.</i><br/>
	<i>Selections return the entire set of mouse, human, and rat homologs of the mouse PIRSF superfamily member selected.</i><br/></span>
      </div>
    </div>
  </div>
  <div id="rightcolumn">
    <div class="innertube">
      <span class="filterButton" id="show" style="text-align: right;">MGI Superfamily Information</span>
    </div>
  </div>
</div>
<div style="clear:both;"></div>

<!-- popup button -->
<div id="pirsfDialog" class="facetFilter" style="display: none">
	<div class="hd">MGI Superfamily Information</div>	
	<div class="bd">
	  MGI protein superfamily detail pages represent the protein classification set for a homeomorphic superfamily from the Protein Information Resource SuperFamily (<a href="http://pir.georgetown.edu/pirsf/" target="_new">PIRSF</a>) site.<p/>
	  Mouse superfamily members are shown with links to their corresponding HomoloGene Classes. Note that pseudogenes are included in PIRSF families but not in orthology sets used here. You can select a given mouse superfamily member and download (or forward to NCBI BLAST) FASTA formatted protein sequences of that mouse gene and its mouse, human and rat homologs, as defined in the corresponding HomoloGene Class. The numbers of mouse, human and rat genes in the HomoloGene Class are shown. You can also "Select all" mouse superfamily members to obtain their protein sequences and the protein sequences for all mouse, human and rat homologs of the mouse superfamily members.<p/>
	  The number of protein sequences returned does not always match the numbers of homologs shown, because the same protein sequence can be associated with multiple homologs. For mouse superfamily members not included in any HomoloGene Class, only the mouse protein sequence is returned. 
	</div>
</div>

<style>
.padded { padding: 4px }
.lined { border: 1px solid black }
.center { text-align: center }
td.cm { text-align: center; vertical-align: middle }
table.detail {
  border: 1px solid black;
  font-size:12px;
  font-family:Verdana,Arial,Helvetica;
  color:#000001;
  vertical-align:top;
  padding: 4px;
} 
.underline { border-bottom: 1px gray dotted; }
</style>

<form name="controls">
  <table border="0" width="70%">
    <tr>
      <td><input type="button" value="Select all" onClick="selectAll()">&nbsp;
	<input type="button" value="Invert" onClick="invertAll()">&nbsp;
	<input type="button" value="Reset" onClick="unselectAll()">
      </td>
    </tr>
    <tr>
      <td style="padding: 4px">
	<span style='font-size:80%'><em>For the selected sequences</em></span>
	<select name="pullDown">
	  <option value="${configBean.SEQFETCH_URL}" selected="selected">download in FASTA format</option>
	  <option value="${configBean.FEWI_URL}sequence/blast">forward to NCBI BLAST</option>
	</select>
	<input onclick="formatForwardArgs();" value="Go" type="button">
      </td>
    </tr>
  </table>
</form>

<form name="sequenceForm" method="GET" action="">
<table class="detail">
  <tr>
    <td class="detailCat3 cm">Mouse<br/>PIRSF Members</td>
    <td class="detailCat3 cm">Chr</td>
    <td class="detailCat3 cm">Homology<br/>Class</td>
    <td class="detailCat3 cm"><abbr class="underline" title="The number of Mouse, Human and Rat genes present in a corresponding Homology Class are indicated.">Mouse : Human : Rat<br/>Homology Class</abbr></td>
    <td class="detailCat3 cm">Select Proteins<br/><em>(mouse, human, rat)</em></td>
  </tr>

<% String seqString = "";	// string for seqfetch/mouseblast %>

<c:forEach var="marker" items="${term.associatedMarkers}" varStatus="status">
  <c:set var="marker" value="${marker}" scope="request"/>
  <c:set var="mouseCount" value="0" />
  <c:set var="humanCount" value="0" />
  <c:set var="ratCount" value="0" />
  <c:set var="sequences" value="" />
  <c:set var="homologyString" value="" />
  <c:set var="organismString" value="" />

  <% seqString = ""; %>

  <c:set var="orgOrtholog" value="${marker.organismOrtholog}" />
  <c:if test="${not empty orgOrtholog}">
    <c:set var="homologyClass" value="${orgOrtholog.homologyCluster}"/>
    <c:if test="${not empty homologyClass}">
      <c:set var="mouseCount" value="${homologyClass.mouseMarkerCount}" />
      <c:set var="humanCount" value="${homologyClass.humanMarkerCount}" />
      <c:set var="ratCount" value="${homologyClass.ratMarkerCount}" />
      <c:set var="sequences" value="${homologyClass.representativeProteinSequences}" />
    <c:set var="sequences" value="${sequences}" scope="request"/>
    <%
       List<Sequence> sequences = 
         (List<Sequence>) request.getAttribute("sequences");

       if ((sequences != null) && (sequences.size() > 0)) {
         for (Sequence seq: sequences) {
           if (seqString.length() > 0) {
	     seqString = seqString + "#SEP#";
	   }
	   seqString = seqString + FormatHelper.getSeqForwardValue(seq);
         }
       }
     %>
       <c:set var="homologyString" value="<a href='${configBean.FEWI_URL}homology/${marker.homoloGeneID.accID}'>${marker.homoloGeneID.accID}</a>" />
       <c:set var="organismString" value="${mouseCount} mouse : ${humanCount} human : ${ratCount} rat" />
    </c:if>
  </c:if>

  <%
    // If seqString is empty, then the marker was not part of a homology class
    // and we just need to grab the info for its representative protein
    // sequence.

    if ("".equals(seqString)) {
      Marker m = (Marker) request.getAttribute("marker");
      Sequence seq = m.getRepresentativePolypeptideSequence();

      if (seq != null) {
          seqString = FormatHelper.getSeqForwardValue(seq);
      }
    }

    if (!"".equals(seqString)) {
      seqString = "<input type='checkbox' name='seqs' value='"
        + seqString + "'>";
    }
  %>

  <tr>
    <td class="detailListGb1 padded lined cm"><a href="${configBean.FEWI_URL}marker/${marker.primaryID}">${marker.symbol}</a></td>
    <td class="detailListGb1 padded lined cm">${marker.chromosome}</td>
    <td class="detailListGb1 padded lined cm"><span style="font-size: 80%">${homologyString}</span></td>
    <td class="detailListGb1 padded lined cm">${organismString}</td>
    <td class="detailListGb1 padded lined cm"><%= seqString %></td>
  </tr>
</c:forEach>

</table>
</form>

<script type="text/javascript">
	YAHOO.namespace("example.container");
	
	YAHOO.util.Event.onDOMReady(function () {	
		// Instantiate a Panel from markup/usr/local/mgi/jboss/server/searchtool_prod/deploy/
		YAHOO.example.container.panel1 = new YAHOO.widget.Panel("pirsfDialog", { width:"420px", visible:false, constraintoviewport:true, context:['show', 'tr', 'bl'] } );
		YAHOO.example.container.panel1.render();
		YAHOO.util.Event.addListener("show", "click", YAHOO.example.container.panel1.show, YAHOO.example.container.panel1, true);
	});
toggle("pirsfDialog");
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
