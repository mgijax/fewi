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

	.topBorder {
		border-top-color: #000000;
		border-top-style:solid;
		border-top-width:1px;
	}

	.leftBorder {
		border-left-color :#000000; 
		border-left-style:solid;
		border-left-width:1px;
	}

	.rightBorder {
		border-right-color :#000000; 
		border-right-style:solid;
		border-right-width:1px;
	}

	.bottomBorderDark {
		border-bottom-color: #000000;
		border-bottom-style:solid;
		border-bottom-width:2px;
	}

	.stripe1 { background-color: #FFFFFF; }
	.stripe2 { background-color: #DDDDDD; }
	.headerStripe { background-color: #D0E0F0; }	
	
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

	.td_disease_tbl_hdr {
		text-align:center;
		vertical-align:bottom;
		padding: 0px 10px 4px 10px;
		border-left:thin solid grey;border-right:thin solid grey;border-top:thin solid grey;border-bottom:thin solid grey;
	}
	.td_disease_tbl {
		text-align:left;
		vertical-align:center;
		padding: 0px 10px 4px 10px;
		border-left:thin solid grey;border-right:thin solid grey;border-top:thin solid grey;border-bottom:thin solid grey;
	}
	.td_disease_tbl_center {
		text-align:center;
		vertical-align:center;
		padding: 0px 10px 4px 10px;
		height: 12px;
		border-left:thin solid grey;border-right:thin solid grey;border-top:thin solid grey;border-bottom:thin solid grey;
	}
	.superscript {
		vertical-align: super;
		font-size: 90%;
	}

	.toggleImage {
		float: right;
		padding: 8px;
		margin: 5px 8px;
		cursor: pointer;
	}

	.hdExpand {
		background: url("${configBean.WEBSHARE_URL}images/rightArrow.gif") no-repeat;
	}

	.hdCollapse {
		background: url("${configBean.WEBSHARE_URL}images/downArrow.gif") no-repeat;
	}

</style>


<script TYPE="text/javascript" SRC='${configBean.WEBSHARE_URL}js/hideshow.js'></script>

<script language="Javascript">

	function showHideById(id) {
		if(document.getElementById(id).style.display=='none') {
			document.getElementById(id).style.display = '';
		} else {
			document.getElementById(id).style.display = 'none';
		}
	}

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
			if (document.sequenceForm.action.indexOf("blast") >= 0) {
			    document.sequenceForm.target = "_blank";
			} else {
			    document.sequenceForm.target = "";
		        }
	
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
		$(window).resize(formatGxdSection);

		window.toggleHomologTags = function() {
			toggle("rightArrowHomologTag");
			toggle("downArrowHomologTag");
			toggle("moreHomologs");
		}

		window.alignLocationRibbonDivs = function(name) {
			if (name == 'LocationRibbon') {
				var coordTopDiv = document.getElementById('coordsTopDiv');
				var geneticTopDiv = document.getElementById('geneticTopDiv');
				var minimapDiv = document.getElementById('minimapDiv');

				if ((coordTopDiv != null) && (geneticTopDiv != null) && (minimapDiv != null)) {
					geneticTopDiv.style.height =
						coordTopDiv.getBoundingClientRect().height + 'px';
				}
			}
		};

		window.toggleRibbon = function(name) {
			var span = "toggle" + name;
			var opened = "opened" + name;
			var closed = "closed" + name;

			if(YAHOO.util.Dom.hasClass(span, 'hdCollapse')) {
				YAHOO.util.Dom.removeClass(span, 'hdCollapse');
				YAHOO.util.Dom.addClass(span, 'hdExpand');
				YAHOO.util.Dom.get(span).title = "Show More";
				pageTracker._trackEvent("MarkerDetailPageEvent", "close", name);
			} else if(YAHOO.util.Dom.hasClass(span, 'hdExpand')) {
				YAHOO.util.Dom.removeClass(span, 'hdExpand');
				YAHOO.util.Dom.addClass(span, 'hdCollapse');
				YAHOO.util.Dom.get(span).title = "Show Less";
				pageTracker._trackEvent("MarkerDetailPageEvent", "open", name);
			}

			showHideById(opened);
			showHideById(closed);

			window.alignLocationRibbonDivs(name);
		}

	});
</script>

${templateBean.templateBodyStartHtml}

<style type="text/css">
	td.padded { padding:4px; }
	td.top { vertical-align: top; }
	a:link{text-decoration: none}
	a:hover{color:#c00; background-color: #c7e3fe; text-decoration: none}
	div.centered { text-align: center; }
	td.rightPadded { padding-right: 4px; }
</style>
<style type="text/css"><% /* needed for slimgrids */ %>
.sgHeaderDiv {
color: black;
font-weight: 10;
font-size: 90%;
text-indent: 2px;
transform: rotate(-45deg);
-webkit-transform: rotate(-45deg);
width: 20px;
}
.sgHeader {
height: 100px;
padding-bottom: 0;
text-align: left;
vertical-align: bottom;
white-space: nowrap;
font-weight: normal;
}
.box { border: 1px solid gray; height: 16px; background-color: white; }
.blue {
    background-color: #0000FF;
    width: 20px;
    height: 16px;
}
.dogear { 
    border-color: transparent #cccccc transparent transparent;
    border-style: solid;
    border-width: 0 20px 16px 0;
    height: 0;
    left: 0;
    width: 0;
}
.cup {
height: 8px;
border-left: 1px solid gray;
border-right: 1px solid gray;
border-bottom: 1px solid gray;
}
.sgWidth { width: 20px; }
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

<div class="container detailStructureTable">

	<%@ include file="MarkerDetail_SummaryRibbon.jsp" %>
	<%@ include file="MarkerDetail_LocationRibbon.jsp" %>
	<%@ include file="MarkerDetail_HomologyRibbon.jsp" %>
	<%@ include file="MarkerDetail_DiseaseRibbon.jsp" %>
	<%@ include file="MarkerDetail_AlleleRibbon.jsp" %>
	<%@ include file="MarkerDetail_GORibbon.jsp" %>
	<%@ include file="MarkerDetail_ExpressionRibbon.jsp" %>

	<%@ include file="MarkerDetail_InteractionRibbon.jsp" %>
	<%@ include file="MarkerDetail_MolecularReagentRibbon.jsp" %>
	<%@ include file="MarkerDetail_DatabaseLinkRibbon.jsp" %>
	<%@ include file="MarkerDetail_SequenceRibbon.jsp" %>
	<%@ include file="MarkerDetail_PolymorphismRibbon.jsp" %>
	<%@ include file="MarkerDetail_ProteinRibbon.jsp" %>
	<%@ include file="MarkerDetail_ReferenceRibbon.jsp" %>
	<%@ include file="MarkerDetail_OtherAccessRibbon.jsp" %>

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
<!--	close page template -->
${templateBean.templateBodyStopHtml}
