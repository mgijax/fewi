<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

${templateBean.templateHeadHtml}

<link rel="canonical" href="${configBean.FEWI_URL}marker/${marker.primaryID}" />

<title>${marker.symbol} MGI Mouse ${marker.markerType} Detail - ${marker.primaryID} - ${marker.name}</title>

<meta name="description" content="${seoDescription}" />
<meta name="keywords" content="${seoKeywords}" />
<meta name="robots" content="NOODP" />
<meta name="robots" content="NOYDIR" />
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%
	// Pull detail object into servlet scope
	// EXAMPLE - Marker marker = (Marker)request.getAttribute("marker");

	StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
	StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2"); 
%>

<style>
	.bioMismatch td { text-align: center; border-style: solid; border-width: 1px; }
	.bioMismatch .header { font-weight: bold; color:#002255; background-color:#aaaaaa; }

	.link {
		color:#000099;
		cursor: pointer;
		border-bottom: 1px #000099 solid;
		text-decoration: none;
	}

	.bold { font-weight: bold; }

	.leftAlign { text-align: left; }

	.allBorders {
		border-top: thin solid gray;
		border-bottom: thin solid gray;
		border-left: thin solid gray;
		border-right: thin solid gray;
		padding:3px;
	}

	.bottomBorder {
		border-bottom-color: #000000;
		border-bottom-style:solid;
		border-bottom-width:1px;
	}
	
	
	/* Turning divs into table layout */
	.container {
		border-bottom: 1px solid black;
	}
	.container .row {
		overflow: hidden;
	}
	.container .row > div {
		padding-bottom: 10003px;
		margin-bottom: -10000px;
	}

	.container .header {
		float: left;
		width: 130px;
		border: 1px solid black;
		text-align: right;
		font-weight: bold;
		font-size: 12px;
		font-family: Verdana,Arial,Helvetica;
		color: #000001;
		padding: 3px;
	}
	.container .detail {
		margin-left: 138px;
		border: 1px solid black;
		border-left: none;
		font-size: 12px;
		font-family: Verdana,Arial,Helvetica;
		color: #000001;
		padding: 3px;
	}

	.container .detailCat1 {
		background-color: #DFEFFF;
	}
	.container .detailCat2 {
		background-color: #D0E0F0;
	}

	.container .detailData2 {
		background-color: #F0F0F0;
	}
</style>


<script TYPE="text/javascript" SRC='${configBean.WEBSHARE_URL}js/hideshow.js'></script>

<script language="Javascript">
	$(function(){
		window.isIntegerFlank = function(flank) {
		// error if non-numeric flank
			if (isNaN(flank)) {
				alert ("An invalid value is specified for Flank (" + flank + "). Flank must be an integer.");
				return 0;
			}
	
			// error if flank is a float, not an integer
			if (flank.indexOf('.') != -1) {
				alert ("An invalid value is specified for Flank (" + flank + "). Flank must be an integer -- without a decimal point.");
				return 0
			}
	
			// error if flank has extra spaces
			if (flank.indexOf(' ') != -1) {
				alert ("An invalid value is specified for Flank (" + flank + "). Flank must be an integer -- without extra spaces.");
				return 0
			}
			return 1;
		}
	
		window.formatForwardArgs = function() {
			document.sequenceForm.action = document.sequenceFormPullDown.seqPullDown.options[document.sequenceFormPullDown.seqPullDown.selectedIndex].value;
	
			// ensure we have a valid value for Flank before proceeding
			if (document.sequenceForm.flank1 && !isIntegerFlank(document.sequenceForm.flank1.value)) {
				return 1;
			}
			document.sequenceForm.submit();
		}
	
		window.formatFastaArgs = function() {
			// ensure we have a valid value for Flank before proceeding
			if (document.markerCoordForm.flank1 && !isIntegerFlank(document.markerCoordForm.flank1.value)) {
				return 1;
			}
			document.markerCoordForm.submit();
		}
	
		window.toggleHomologyDetails = function() {
			toggle ("downArrowHomologs");
			toggle ("rightArrowHomologs");
			toggle ("humanHomologDetails");
			if (mgihomeUrl != null) {
				hitUrl (mgihomeUrl + "other/monitor.html", "toggleHomologyDetails=1");
			}
		}
	
		/* cluster membership */
	
		function initializeClusterMembersPopup () {
			var elem = document.getElementById("clusterMemberTable");
			if (elem != null) {
				YAHOO.namespace("markerDetail.container");
		
				var props = { 
					visible:false, 
					constraintoviewport:true,
					context:['showClusterMembers', 'tl', 'br', [ 'beforeShow', 'windowResize' ] ] 
				};
		
				if (${memberCount} > 12) {
					props.height = "300px";
					props.width = (elem.offsetWidth + 40) + "px";
				}
				
				// make the div visible
				elem.style.display = '';
		
				/* Wire up cluster members popup show link */
				YAHOO.markerDetail.container.clusterMemberPanel = new YAHOO.widget.Panel(
					"clusterMemberDiv", 
					props
				);
				YAHOO.markerDetail.container.clusterMemberPanel.render();
				YAHOO.util.Event.addListener ("showClusterMembers", "click",
					YAHOO.markerDetail.container.clusterMemberPanel.show,
					YAHOO.markerDetail.container.clusterMemberPanel, 
					true
				);
				YAHOO.util.Event.addListener (
					"YAHOO.markerDetail.container.clusterMemberPanel", "move",
					YAHOO.markerDetail.container.clusterMemberPanel.forceContainerRedraw
				);
				YAHOO.util.Event.addListener (
					"YAHOO.markerDetail.container.clusterMemberPanel", "mouseover",
					YAHOO.markerDetail.container.clusterMemberPanel.forceContainerRedraw
				);
			}
		}
		initializeClusterMembersPopup();
	
		/* Wire up batch submit in cluster members popup */
		$("#clusterBatchLink").click(function(){
			$("#batchWebForm").submit();
		});
	
		window.log = function(msg) {
			// log a message to the browser console
			//setTimeout(function() { throw new Error(msg); }, 0);
			console.log(msg);
		}
	
		/* formatting of GXD section */
		function formatGxdSection () {
			var gxdHeading = $("#gxdHeading");
			if (gxdHeading.length > 0) {
					var gxdHeight = $('#gxd').height();
					var headingHeight = $('#gxdHeading').height();
					var logo = $('#gxdLogo');
					var imageHeight = logo.height();
			
					var imagePad = Math.round( (gxdHeight - imageHeight) / 2) - headingHeight;
		
					// add padding to center the logo vertically in the expression ribbon
					if (imagePad > 0) {
						logo.css('padding-top', imagePad + 'px');
					}
			}
		}
		formatGxdSection();

		window.toggleHomologTags = function() {
			toggle("rightArrowHomologTag");
			toggle("downArrowHomologTag");
			toggle("moreHomologs");
		}

	});
</script>

${templateBean.templateBodyStartHtml}

<style type="text/css">
	td.padded { padding:4px; }
</style>


<!-- header bar -->
<div id="titleBarWrapper" userdoc="GENE_detail_help.shtml" style="max-width: none;">
	<div class="yourInputButton">
		<form name="YourInputForm">
			<input class="searchToolButton" value="Your Input Welcome" name="yourInputButton" onclick='window.open("${configBean.MGIHOME_URL}feedback/feedback_form.cgi?accID=${marker.primaryID}&amp;dataDate=<fmt:formatDate type='date' value='${databaseDate}' dateStyle='short'/>")' onmouseover="return overlib('We welcome your corrections and new data. Click here to contact us.', LEFT, WIDTH, 200, TIMEOUT, 2000);" onmouseout="nd();" type="button">
		</form>
	</div>
	<div name="centeredTitle">
		<span class="titleBarMainTitle"><fewi:super value="${marker.symbol}"/></span><br/>
		${marker.markerType} Detail
	</div>
</div>

<!-- structural table -->
<div class="container detailStructureTable">

	<!-- ROW1 -->
	<%@ include file="MarkerDetail_Row1Ribbon.jsp" %>

	<!-- ROW1a -->
	<c:if test="${not empty marker.aliases}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				<c:if test="${marker.markerType == 'Gene'}">STS</c:if>
				<c:if test="${marker.markerType != 'Gene'}">STS for</c:if>
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				<c:forEach var="alias" items="${marker.aliases}" varStatus="status">
					<a href="${configBean.FEWI_URL}marker/${alias.aliasID}">${alias.aliasSymbol}</a><c:if test="${!status.last}">, </c:if>
				</c:forEach>
			</div>
		</div>
	</c:if>

	<!-- ROW2 -->
	<c:if test="${not empty marker.synonyms}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Synonyms
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				<c:forEach var="synonym" items="${marker.synonyms}" varStatus="status">
					<fewi:super value="${synonym.synonym}"/><c:if test="${!status.last}">, </c:if>
				</c:forEach>
			</div>
		</div>
	</c:if>

	<!-- ROW3 -->
	<c:if test="${not empty marker.markerSubtype}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Feature&nbsp;Type
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				${marker.markerSubtype}
			</div>
		</div>
	</c:if>

	<!-- ROW4 -->
	<%@ include file="MarkerDetail_Row4Ribbon.jsp" %>

	<!-- ROW5 -->
	<%@ include file="MarkerDetail_Row5Ribbon.jsp" %>

	<!-- Vertebrate homology ribbon -->
	<%@ include file="MarkerDetail_VertebrateHomologyRibbon.jsp" %>

	<!-- Human Homologs ribbon -->
	<%@ include file="MarkerDetail_HumanHomologsRibbon.jsp" %>

	<!-- Allele ribbon -->
	<%@ include file="MarkerDetail_AlleleRibbon.jsp" %>

	<!-- interactions ribbon -->
	<%@ include file="MarkerDetail_InteractionsRibbon.jsp" %>

	<!-- go classifications ribbon -->
	<%@ include file="MarkerDetail_GoClassificationsRibbon.jsp" %>

	<!-- Expression ribbon -->
	<%@ include file="MarkerDetail_ExpressionRibbon.jsp" %>

	<!-- Molecular reagents ribbon -->
	<%@ include file="MarkerDetail_MolecularReagentsRibbon.jsp" %>

	<!-- Other database links -->
	<c:if test="${not empty logicalDBs}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Other&nbsp;database<br/>links
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				<table cellspacing=2 cellpadding=2>
					<c:forEach var="item" items="${logicalDBs}">
						<tr><td>${item}&nbsp;</td><td>${otherIDs[item]}</td></tr>
					</c:forEach>
				</table>
			</div>
		</div>
	</c:if>


	<!-- Sequences ribbon -->
	<%@ include file="MarkerDetail_SequencesRibbon.jsp" %>

	<!-- Polymorphisms ribbon -->
	<%@ include file="MarkerDetail_PolymorphismsRibbon.jsp" %>

	<!-- ROW14 -->
	<c:set var="proteinAnnotations" value="${marker.proteinAnnotations}"/>
	<c:if test="${not empty proteinAnnotations}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Protein-related<br/>information
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				<table>
					<tr><td>Resource</td><td>ID</td><td>Description</td></tr>
					<c:forEach var="item" items="${proteinAnnotations}">
						<c:set var="url" value=""/>
						<c:if test="${item.vocabName == 'InterPro Domains'}">
							<c:set var="url" value="${urls.InterPro}"/>
						</c:if>
						<c:if test="${item.vocabName == 'Protein Ontology'}">
							<c:set var="url" value="${urls.Protein_Ontology}"/>
						</c:if>
						<tr><td>${fn:replace (item.vocabName, " Domains", "")}</td>
						<c:if test="${url != ''}">
							<td><a href="${fn:replace(url, '@@@@', item.termID)}">${item.termID}</a></td>
						</c:if>
						<c:if test="${url == ''}">
							<td>${item.termID}</td>
						</c:if>
						<td>${item.term}</td></tr>
					</c:forEach>
				</table>
				<c:if test="${not empty marker.representativePolypeptideSequence}">
					<c:set var="seq" value="${marker.representativePolypeptideSequence}" scope="request"/>
					<c:if test="${seq.provider == 'SWISS-PROT'}">
						<c:set var="url" value="${urls.InterPro_ISpy}"/>
						<a href="${fn:replace(url, '@@@@', seq.primaryID)}" target="_new">Graphical View of Protein Domain Structure</a>
					</c:if>
				</c:if>
			</div>
		</div>
	</c:if>

	<!-- References -->
	<c:if test="${not empty marker.references}">
		<div class="row">
			<div class="header <%=leftTdStyles.getNext() %>" >
				References
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>" >
				<c:set var="earliestRef" value="${marker.earliestReference}"/>
				<c:set var="latestRef" value="${marker.latestReference}"/>
				<c:if test="${not empty earliestRef}">(Earliest) <a href="${configBean.FEWI_URL}reference/${earliestRef.jnumID}">${earliestRef.jnumID}</a>
					${earliestRef.shortCitation}<br/>
				</c:if>
				<c:if test="${not empty latestRef}">(Latest) <a href="${configBean.FEWI_URL}reference/${latestRef.jnumID}">${latestRef.jnumID}</a>
					${latestRef.shortCitation}<br/>
				</c:if>
				<c:if test="${marker.countOfReferences > 0}">
					All references(<a href="${configBean.FEWI_URL}reference/marker/${marker.primaryID}">${marker.countOfReferences}</a>)<br/>
				</c:if>
				<c:if test="${not empty diseaseRefCount && diseaseRefCount > 0}">
					Disease annotation references (<a href="${configBean.FEWI_URL}reference/diseaseRelevantMarker/${marker.primaryID}">${diseaseRefCount}</a>)
				</c:if>
			</div>
		</div>
	</c:if>

	<!-- ROW16 -->
	<c:set var="otherMgiIDs" value="${marker.otherMgiIDs}"/>
	<c:if test="${not empty otherMgiIDs}">
		<div class="row">
			<div class="header <%=leftTdStyles.getNext() %>" >
				Other<br/>accession&nbsp;IDs
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>" >
				<c:forEach var="item" items="${otherMgiIDs}" varStatus="status">
					${item.accID}<c:if test="${not status.last}">, </c:if>
				</c:forEach>
			</div>
		</div>
	</c:if>

<!-- close structural table -->
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
			<div class="hd"> ${marker.symbol} Cluster contains:
				<div style="float:right; margin-right:25px"><span id="clusterBatchLink" style="cursor: pointer; text-decoration: underline; color: #0000ff">More Data</span> for these features</div>
			</div>
			<div class="bd" style="overflow:auto">
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
</div>

<!--	close page template -->
${templateBean.templateBodyStopHtml}
