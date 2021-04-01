<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.link.ProviderLinker" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>${browserTitle}</title>
<meta name="description" content="${seoDescription}"/>
<meta name="keywords" content="${seoKeywords}"/>
<meta name="robots" content="NOODP"/>
<meta name="robots" content="NOYDIR"/>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  // Pull detail object into servlet scope
    // EXAMPLE - Marker foo = (Marker)request.getAttribute("foo");

    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailListBg1","detailListBg2");
    
%>

<script language="Javascript">

function formatForwardArgs() {
    document.sequenceForm.action = document.sequenceForm.seqPullDown.options[document.sequenceForm.seqPullDown.selectedIndex].value;
    if (document.sequenceForm.action.indexOf("blast") >= 0) {
	document.sequenceForm.target = "_blank";
    } else {
	document.sequenceForm.target = "";
    }
    document.sequenceForm.submit();
}

function toggleSeqs(state) {
	var checkboxes = document.getElementsByTagName("input");
	for(s in checkboxes) {
		if(checkboxes[s].type == "checkbox" ) {
			checkboxes[s].checked = state;
		}
	}
}

function selectAllSeqs(){
	toggleSeqs('true');
}

function deselectAllSeqs() {
	toggleSeqs(false);
}

</script>

<c:set var="sCount" value="1" scope="page"/>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper" userdoc="HOMOLOGY_class_help.shtml">	
	<span class="titleBarMainTitle">${pageTitle}</span>
</div>

<%@ include file="/WEB-INF/jsp/homology_header.jsp" %>
<br/>

<c:set var="popupTitle" value="Alliance Homology Information"/>
<c:set var="popupText" value='<p>MGI loads vertebrate homology data from the
		<A TARGET="_blank" HREF="https://www.alliancegenome.org/"><u>Alliance of Genome Resources</u></A>, 
		which programmatically detects homologs among the genome features of completely sequenced genomes for member model organisms.
		<span style="color:red">(actual text to come from Mary)</span></p>
		
		<p>MGI includes homology for the following selected vertebrate species from the Alliance of Genome Resources:<br>
		&nbsp;- human<br>
		&nbsp;- mouse<br>
		&nbsp;- rat<br>
		&nbsp;- zebrafish<p>

		<p>These are a subset of the total species represented in the Alliance of Genome Resources.<br>
		Additional species may be present in an Alliance homology class than appear in MGI.</font></p>'/>

<div id="summary">
	<div id="breadbox">
		<div id="contentcolumn">
			<div class="innertube">
				<c:if test="${not empty homology.primaryID}">
				 <span class="small"><a href='https://www.ncbi.nlm.nih.gov/sites/entrez?cmd=Retrieve&db=homologene&dopt=MultipleAlignment&list_uids=${homology.primaryID}'>HomoloGene:${homology.primaryID} Multiple Sequence Alignment</a></span>
				 </c:if>
			</div>
		</div>
	</div>
	<div id="querySummary">
		<div class="innertube">
		  <c:if test="${homology.hasComparativeGOGraph == 1}">
			<span class="small"><a href="${configBean.FEWI_URL}homology/GOGraph/${homology.clusterKey}">Comparative GO Graph</a> (mouse, human, rat)</span>	
		  </c:if>&nbsp;
		</div>
	</div>
	<div id="rightcolumn">
		<div class="innertube">
			<span class="filterButton" id="show" style="text-align: right;">${popupTitle}</span>
		</div>
	</div>
</div>
<div style="clear:both;"></div>
<form name="sequenceForm" method="GET" action="${configBean.SEQFETCH_URL}">
<!-- structural table -->
<table class="detailListTable">

<c:forEach var="hc" items="${homology.orthologs}" varStatus="status">
	<c:set var="style" value="<%=rightTdStyles.getNext() %>" />
	<c:if test="${status.first}">
        <tr>
        	<td style="width:10px" class="detailCat3">Species</td>
        	<td class="detailCat3">Symbol</td>
        	<td class="detailCat3">Gene Links</td>
        	<td class="detailCat3">Genetic Location</td>
        	<td class="detailCat3">Genome Coordinates<br/><span class="example">(mouse and human only)</span></td>
        	<td class="detailCat3 nowrap">Associated Human Diseases</span></td>
        	<td class="detailCat3">
        		Sequences<br/>
        		<span class="nowrap small">
        		<a class="filterButton" onClick="selectAllSeqs()">select all</a> 
        		<a class="filterButton" onClick="deselectAllSeqs()">deselect all</a>
        		<select name="seqPullDown">
				  <option value="${configBean.SEQFETCH_URL}" selected>get FASTA</option>
				  <option value="${configBean.FEWI_URL}sequence/blast">BLAST at NCBI</option>
				</select>
				<a class="filterButton" onClick="formatForwardArgs()">Go</a>
				</span>
        	</td>
        </tr>
    </c:if>
	<tr>
	
		<td rowspan="${fn:length(hc.markers)}" class="${style}">${hc.organism}</td>
			
	<c:forEach var="m" items="${hc.markers}" varStatus="stat">
		<c:if test="${!stat.first}">
		<tr>
	    </c:if>
	    <td class="${style}">
	    	<c:choose>
	    	<c:when test="${hc.organism == 'mouse'}">
	    		<a href="${configBean.FEWI_URL}marker/${m.primaryID}">${m.symbol}</a>
	    	</c:when>
	    	<c:otherwise>
	    		${m.symbol}
	    	</c:otherwise>
	    	</c:choose>
	    </td>
	    
	    <td class="${style}" nowrap="nowrap">
		<c:forEach var="link" items="${m.homologyLinks}" varStatus="linkStatus">
		<c:if test="${link.displayText != 'VISTA-Point'}">
			<c:if test="${not empty link.associatedID}">
				${link.associatedID}
				<c:set var="fixedUrl" value="${configBean.FEWI_URL}accession/"/>
				(<a href='${fn:replace(link.url, "accession_report.cgi?id=", fixedUrl)}'>${link.displayText}</a>)<br/>
			</c:if>

			<c:if test="${empty link.associatedID}">
		  		<a href="${link.url}">${link.displayText}</a><br/>
			</c:if>
		</c:if>

		</c:forEach>

		<c:set var="clusterKey" value="${m.allianceDirectClusterKey}"/> 
		<c:set var="clusterText" value="Alliance of Genome Resources homology"/>

		<c:if test="${not empty clusterText and not empty clusterKey}">
		  <a href='${configBean.FEWI_URL}homology/cluster/key/${clusterKey}'>${clusterText}</a><br/>
		</c:if>
	    </td>
	    
	    <td class="${style}">
		    <c:set var="cmLoc" value="${m.preferredCentimorgans}"/>
		    <c:set var="cbLoc" value="${m.preferredCytoband}"/>

		    <c:set var="chromosome" value=""/>
		    <c:set var="cmOrBand" value=""/>

		    <c:if test="${not empty cmLoc}">
				<c:if test="${empty chromosome}">
		            <c:set var="chromosome" value="${cmLoc.chromosome}"/>
				</c:if>
		    	<c:set var="cmOrBand" value="${cmLoc.cmOffset}&nbsp;cM"/>
			<c:if test="${cmLoc.cmOffset < 0}">
		    	  <c:set var="cmOrBand" value="Syntenic"/>
			</c:if>
		    </c:if>
		    <c:if test="${not empty cbLoc}">
				<c:if test="${empty chromosome}">
		            <c:set var="chromosome" value="${cbLoc.chromosome}"/>
				</c:if>
				<c:if test="${empty cmOrBand}">
		    	    <c:set var="cmOrBand" value="${cbLoc.cytogeneticOffset}"/>
				</c:if>
		    </c:if>
		    <span class="nowrap">Chr${chromosome} <c:if test="${not empty cmOrBand}">${cmOrBand}</c:if></span>
		</td>
	    
		<td class="${style}">

		    <c:set var="crdLoc" value="${m.preferredCoordinates}"/>

		    <c:set var="chromosome" value=""/>
		    <c:set var="cmOrBand" value=""/>
		    <c:set var="coords" value=""/>
		    <c:set var="strand" value=""/>
		    <c:set var="build" value=""/>

		    <c:if test="${not empty crdLoc}">
		        <c:set var="chromosome" value="${crdLoc.chromosome}"/>
			<fmt:formatNumber value="${crdLoc.startCoordinate}" pattern="#0" var="startCoord"/>
			<fmt:formatNumber value="${crdLoc.endCoordinate}" pattern="#0" var="endCoord"/>
		        <c:set var="coords" value="${startCoord}-${endCoord}"/>
		        <c:set var="strand" value="${crdLoc.strand}"/>
		        <c:set var="build" value="${crdLoc.buildIdentifier}"/>
		    </c:if>
		    <c:if test="${not empty cmLoc}">
			<c:if test="${empty chromosome}">
		            <c:set var="chromosome" value="${cmLoc.chromosome}"/>
			</c:if>
		    	<c:set var="cmOrBand" value="${cmLoc.cmOffset}&nbsp;cM"/>
		    </c:if>
		    <c:if test="${not empty cbLoc}">
			<c:if test="${empty chromosome}">
		            <c:set var="chromosome" value="${cbLoc.chromosome}"/>
			</c:if>
			<c:if test="${empty cmOrBand}">
		    	    <c:set var="cmOrBand" value="${cbLoc.cytogeneticOffset}"/>
			</c:if>
		    </c:if>

		    <c:if test="${not empty coords}">
		    	<span class="nowrap">Chr${chromosome}:${coords}
		    	<c:if test="${not empty strand}">(${strand})</c:if></span>
		    	<br/>
				<c:if test="${not empty build}"><span class="example nowrap">${build}</span></c:if>
			</c:if>
		</td>
		
		<td class="${style}">
		    <c:choose>
		    <c:when test="${hc.organism == 'mouse'}">
		        <c:set var="diseases" value="${m.DOAnnotations}"/>
		    </c:when>
		    <c:when test="${hc.organism == 'human'}">
		        <c:set var="diseases" value="${m.DOHumanAnnotations}"/>
		    </c:when>
	    	    <c:otherwise>
			<c:set var="diseases" value=""/>
	    	    </c:otherwise>
	    	    </c:choose>

			<c:forEach var="disease" items="${diseases}" varStatus="stat">
				<c:if test="${disease.qualifier != 'NOT'}">
					<a href="${configBean.FEWI_URL}disease/${disease.termID}">${disease.term}</a><br/>
				</c:if>
			</c:forEach>
	    </td>
		
		<td class="${style} nowrap">
		    <c:if test="${not empty m.representativeGenomicSequence}">
		    	<c:set var="seq" value="${m.representativeGenomicSequence}" scope="page" />
		    	<input type="checkbox" name="seq${sCount}" value="<%= FormatHelper.getSeqForwardValue((Sequence)pageContext.getAttribute("seq")) %>">
		    	 <c:set var="sCount" value="${sCount + 1}" scope="page"/>
			 <span title="DNA">${seq.primaryID}</span> (<%=ProviderLinker.getSeqProviderLinks((Sequence)pageContext.getAttribute("seq"))%>)<br/>
			</c:if>		
			<c:if test="${not empty m.representativePolypeptideSequence}">
				<c:set var="seq" value="${m.representativePolypeptideSequence}" scope="page" />
		    	<input type="checkbox" name="seq${sCount}" value="<%= FormatHelper.getSeqForwardValue((Sequence)pageContext.getAttribute("seq")) %>"> 
		    	<c:set var="sCount" value="${sCount + 1}" scope="page"/>
			<span title="Polypeptide">${seq.primaryID}</span> (<%=ProviderLinker.getSeqProviderLinks((Sequence)pageContext.getAttribute("seq"))%>)<br/>	    	
			</c:if>	
			<c:if test="${not empty m.representativeTranscriptSequence}">
				<c:set var="seq" value="${m.representativeTranscriptSequence}" scope="page" />
		    	<input type="checkbox" name="seq${sCount}" value="<%= FormatHelper.getSeqForwardValue((Sequence)pageContext.getAttribute("seq")) %>"> 
		    	<c:set var="sCount" value="${sCount + 1}" scope="page"/>
			<span title="RNA">${seq.primaryID}</span> (<%=ProviderLinker.getSeqProviderLinks((Sequence)pageContext.getAttribute("seq"))%>)<br/>	    	
			</c:if>	
		</td>
		</tr>
	</c:forEach>
</c:forEach>

<!-- close structural table and page template-->
</table>

</form>

<div id="homologyDialog" class="facetFilter">
	<div class="hd">${popupTitle}</div>	
	<div class="bd">${popupText}
	</div>
</div>

<script type="text/javascript">
	YAHOO.namespace("example.container");
	
	YAHOO.util.Event.onDOMReady(function () {	
		// Instantiate a Panel from markup
		YAHOO.example.container.panel1 = new YAHOO.widget.Panel("homologyDialog", { width:"420px", visible:false, constraintoviewport:true, context:['show', 'tr', 'bl'] } );
		YAHOO.example.container.panel1.render();
		YAHOO.util.Event.addListener("show", "click", YAHOO.example.container.panel1.show, YAHOO.example.container.panel1, true);
	});
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
