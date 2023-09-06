<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<fewi:simpleseo
	title="${marker.symbol} MGI Mouse ${marker.markerType} Detail - ${marker.primaryID} - ${marker.name}"
	canonical="${configBean.FEWI_URL}marker/${marker.primaryID}"
	description="${seoDescription}"
	keywords="${seoKeywords}"
/>

<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/marker_detail.css" />
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/marker_detail_new.css" />

<%
	StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
	StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2"); 
%>

<script TYPE="text/javascript" SRC='${configBean.WEBSHARE_URL}js/hideshow.js'></script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapper" userdoc="GENE_detail_help.shtml" style="max-width: none;">
	<div class="yourInputButton">
		<form name="YourInputForm">
			<input class="largeButton" value="Your Input Welcome" name="yourInputButton" onclick='window.open("${configBean.MGIHOME_URL}feedback/feedback_form.cgi?accID=${marker.primaryID}&amp;dataDate=<fmt:formatDate type='date' value='${databaseDate}' dateStyle='short'/>")' onmouseover="return overlib('We welcome your corrections and new data. Click here to contact us.', LEFT, WIDTH, 200, TIMEOUT, 2000);" onmouseout="nd();" type="button">
		</form>
	</div>
	<div name="centeredTitle">
		<span class="titleBarMainTitle"><fewi:super value="${marker.symbol}"/></span>
		<span class="titleBar_sub">
			${marker.markerType} Detail
		</span>
	</div>
</div>

<div class="container detailStructureTable">

	<%@ include file="marker_detail_SummaryRibbon.jsp" %>
	<%@ include file="marker_detail_LocationRibbon.jsp" %>
	<%@ include file="marker_detail_StrainRibbon.jsp" %>
	<%@ include file="marker_detail_HomologyRibbon.jsp" %>
	<%@ include file="marker_detail_DiseaseRibbon.jsp" %>
	<%@ include file="marker_detail_AlleleRibbon.jsp" %>
	<%@ include file="marker_detail_GORibbon.jsp" %>
	<%@ include file="marker_detail_ExpressionRibbon.jsp" %>
	<%@ include file="marker_detail_SequenceRibbon.jsp" %>
	<%@ include file="marker_detail_ProteinRibbon.jsp" %>
	<%@ include file="marker_detail_MolecularReagentRibbon.jsp" %>
	<%@ include file="marker_detail_DatabaseLinkRibbon.jsp" %>
	<%@ include file="marker_detail_OtherAccessRibbon.jsp" %>
	<%@ include file="marker_detail_ReferenceRibbon.jsp" %>

</div>

<!-- Elements not part of page structure that are hidden by default -->
<div class="hiddenItems">
	<!-- Cluster Relationship items -->
	<c:if test="${hasClusterMembers}">
		<form style='display:none;' id="batchWebForm" name='batchWeb' enctype='multipart/form-data' target='_blank' method='post' action='${configBean.FEWI_URL}batch/summary'>
			<input name='idType' value='current symbol' type='hidden'>
			<input name='attributes' value='Nomenclature' type='hidden'>
			<input name='attributes' value='Location' type='hidden'>
			<input name='ids' value='${memberSymbols}' id='batchSymbolListWeb' type='hidden'>
		</form>
	
		<div id="clusterMemberDiv" class="" style="visibility:hidden;">
			<div class="hd">${marker.symbol} Cluster contains:</div>
			<div class="bd" style="overflow:auto">
				<span id="clusterBatchLink" style="cursor: pointer; color: #0000ff">More Data</span> for these features
				<table id="clusterMemberTable">
					<tr><td class="bold leftAlign allBorders">Member</td><td class="bold leftAlign allBorders">Feature Type</td><td class="bold leftAlign allBorders">Location</td></tr>
					<c:forEach var="member" items="${marker.clusterMembers}">
						<tr><td class="leftAlign allBorders"><a href="${configBean.FEWI_URL}marker/${member.relatedMarkerID}">${member.relatedMarkerSymbol}</a>, ${member.relatedMarkerName}</td>
							<td class="leftAlign allBorders">${member.relatedMarkerFeatureType}</td>
							<td class="leftAlign allBorders">${member.relatedMarkerLocation}</td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</div>
	</c:if>

	<!-- TSS items -->
	<c:if test="${hasTss}">
		<div id="tssDiv" class="" style="visibility:hidden;">
			<div class="hd">TSS for ${marker.symbol}:</div>
			<div class="bd" style="overflow:auto">
				<c:if test="${not empty marker.preferredCoordinates}">
					<fmt:formatNumber value="${marker.preferredCoordinates.startCoordinate}" pattern="#0" var="startCoord"/>
					<fmt:formatNumber value="${marker.preferredCoordinates.endCoordinate}" pattern="#0" var="endCoord"/>
					<c:set var="tssAtJbrowse" value="${externalUrls.JBrowseTSS}"/>
					<c:set var="tssAtJbrowse" value="${fn:replace(tssAtJbrowse, '<chromosome>', marker.preferredCoordinates.chromosome)}"/>
					<c:set var="tssAtJbrowse" value="${fn:replace(tssAtJbrowse, '<start>', startCoord - 10000)}"/>
					<c:set var="tssAtJbrowse" value="${fn:replace(tssAtJbrowse, '<end>', endCoord + 10000)}"/>
					(View these features in <a id="tssBatchLink" style="cursor: pointer" href="${tssAtJbrowse}" target="_blank">JBrowse</a>)
				</c:if>
				<table id="tssTable">
					<tr><td class="bold leftAlign allBorders">Transcription Start Site</td>
						<td class="bold leftAlign allBorders">Location</td>
						<td class="bold leftAlign allBorders">Distance from Gene 5'-end</td>
						</tr>
					<c:forEach var="tss" items="${tssMarkers}">
						<tr><td class="leftAlign allBorders"><a href="${configBean.FEWI_URL}marker/${tss.primaryID}">${tss.symbol}</a></td>
							<td class="leftAlign allBorders">${tss.location}</td>
							<td class="leftAlign allBorders">${tss.distanceFromStart} bp</td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</div>
	</c:if>

        <!-- QTL candidate genes -->
        <c:if test="${nCandidates > 0}">
		<div id="candidatesDiv" class="" style="visibility:hidden;max-width:900px;">
			<div class="hd">Candidate Genes for ${marker.symbol}:</div>
			<div class="bd" style="overflow:auto">
				<table id="candidatesTbl">
					<tr>
						<td class="bold leftAlign allBorders">Gene</td>
						<td class="bold leftAlign allBorders">Genome Location (${buildNumber})</td>
						<td class="bold leftAlign allBorders">Reference</td>
						<td class="bold leftAlign allBorders">QTL Note</td>
                                        </tr>
					<c:forEach var="cand" items="${candidates}" varStatus="loop">
                                            <tr>
                                                <td class="leftAlign allBorders">
                                                    <a href="${configBean.FEWI_URL}marker/${cand.relatedMarkerID}">${cand.relatedMarkerSymbol}</a></td>
                                                <td class="leftAlign allBorders">${cand.relatedMarkerLocation}</td>    
                                                <td class="leftAlign allBorders">
                                                    <a href="${configBean.FEWI_URL}reference/${cand.jnumID}">${cand.jnumID}</a></td>    
                                                <td class="leftAlign allBorders">${candidateNotes.get(loop.index)}</td>    
                                            </tr>
					</c:forEach>
				</table>
			</div>
		</div>
        </c:if>

        <!-- QTL this gene is a candidate for -->
        <c:if test="${nCandidateFor > 0}">
		<div id="candidateForDiv" class="" style="visibility:hidden;max-width:900px;">
			<div class="hd">${marker.symbol} is Candidate Gene for:</div>
			<div class="bd" style="overflow:auto">
				<table id="candidateForTbl">
					<tr>
						<td class="bold leftAlign allBorders">QTL</td>
						<td class="bold leftAlign allBorders">Genetic Location*</td>
						<td class="bold leftAlign allBorders">Genome Location (${buildNumber})</td>
						<td class="bold leftAlign allBorders">Reference</td>
						<td class="bold leftAlign allBorders">QTL Note</td>
                                        </tr>
					<c:forEach var="qtl" items="${candidateFor}" varStatus="loop">
                                            <tr>
                                                <td class="leftAlign allBorders">
                                                    <a href="${configBean.FEWI_URL}marker/${qtl.relatedMarkerID}">${qtl.relatedMarkerSymbol}</a></td>
                                                <td class="leftAlign allBorders">${qtl.relatedMarkerGeneticLocation}</td>    
                                                <td class="leftAlign allBorders">${qtl.relatedMarkerGenomicLocation}</td>    
                                                <td class="leftAlign allBorders">
                                                    <a href="${configBean.FEWI_URL}reference/${qtl.jnumID}">${qtl.jnumID}</a></td>    
                                                <td class="leftAlign allBorders">${candidateForNotes.get(loop.index)}</td>    
                                            </tr>
					</c:forEach>
				</table>
                                <span style="font-size:12px;">*cM position of peak correlated region/marker</span>
			</div>
		</div>
        </c:if>

        <!-- QTL-QTL interactions -->
        <c:if test="${nInteractingQTL > 0}">
		<div id="interactingQTLDiv" class="" style="visibility:hidden;max-width:900px;">
			<div class="hd">QTL interacting with ${marker.symbol}:</div>
			<div class="bd" style="overflow:auto">
				<table id="interactingQTLTbl">
					<tr>
						<td class="bold leftAlign allBorders">QTL</td>
						<td class="bold leftAlign allBorders">Genetic Location*</td>
						<td class="bold leftAlign allBorders">Genome Location (${buildNumber})</td>
						<td class="bold leftAlign allBorders">Interaction Type</td>
						<td class="bold leftAlign allBorders">Reference</td>
                                        </tr>
					<c:forEach var="qtl" items="${interactingQTL}" varStatus="loop">
                                            <tr>
                                                <td class="leftAlign allBorders">
                                                    <a href="${configBean.FEWI_URL}marker/${qtl.relatedMarkerID}"
                                                      >${qtl.relatedMarkerSymbol}</a>
                                                </td>
                                                <td class="leftAlign allBorders">${qtl.relatedMarkerGeneticLocation}</td>    
                                                <td class="leftAlign allBorders">${qtl.relatedMarkerGenomicLocation}</td>    
                                                <td class="leftAlign allBorders" title="${qtl.relationshipTermObj.definition}">${qtl.relationshipTerm}</td>    
                                                <td class="leftAlign allBorders">
                                                    <a href="${configBean.FEWI_URL}reference/${qtl.jnumID}">${qtl.jnumID}</a>
                                                </td>    
                                            </tr>
					</c:forEach>
				</table>
                                <span style="font-size:12px;">*cM position of peak correlated region/marker</span>
			</div>
		</div>
        </c:if>
</div>

<script language="Javascript">
YAHOO.namespace("gxd.container");
YAHOO.gxd.container.anatomyHelp = new YAHOO.widget.Panel("sgAnatomyHelp", { width:"400px", draggable:false, visible:false, constraintoviewport:true } );
YAHOO.gxd.container.anatomyHelp.render();
YAHOO.util.Event.addListener("sgAnatomyHelpImage", "click", YAHOO.gxd.container.anatomyHelp.show, YAHOO.gxd.container.anatomyHelp, true);

YAHOO.namespace("mp.container");
YAHOO.mp.container.phenoHelp = new YAHOO.widget.Panel("sgPhenoHelp", { width:"360px", draggable:false, visible:false, constraintoviewport:true } );
YAHOO.mp.container.phenoHelp.render();
YAHOO.util.Event.addListener("sgPhenoHelpImage", "click", YAHOO.mp.container.phenoHelp.show, YAHOO.mp.container.phenoHelp, true);
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/marker/marker_detail.js"></script>
<c:if test="${showLocationNote}">
	<script type="text/javascript">
		// if we have a location note, open the Location ribbon by default
		setTimeout(function() {
			$('.locationRibbon .toggleImage').click();
			}, 200);
	</script>
</c:if>

<script>
	// setup for form in strain ribbon
	configureUrl('seqfetch', '${configBean.SEQFETCH_URL}');
	configureUrl('mgv', '${mgvUrl}');
	configureUrl('sanger', '${fn:replace(fn:replace(fn:replace(externalUrls.Sanger_SNPs, "<chromosome>", chromosome),
			"<start>", sangerStartCoord), 
			"<end>", sangerEndCoord)}');

	// The strain comparison ribbon needs to be opened if there are only PCR/RFLP data.  These variables are
	// set in the jsp file for that ribbon.  Use a little delay, though, to let things render first.
	<c:if test="${polymorphismsfound and not (snpsfound or hasStrainMarkers or hasCoords or (not empty strainSpecificNote))}">
		setTimeout(function() { $('#scToggle')[0].click(); }, 500);
	</c:if>
</script>
<!--	close page template -->
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
