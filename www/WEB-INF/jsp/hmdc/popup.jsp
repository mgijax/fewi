<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

<%@ include file="header.jsp" %>

<script>
	// change window title on page load
    document.title = '${pageTitle}';
</script>

<link rel="stylesheet" href="http://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
<link rel="stylesheet" href="//jqueryui.com/jquery-wp-content/themes/jqueryui.com/style.css">

<script src="http://code.jquery.com/jquery-1.10.2.js"></script>
<script src="http://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>

<script>
  <% /* generate javascript object for genocluster data, used to populate Find Mice popups */ %>
  
  // genocluster key : list of [ allele ID, allele symbol, allele IMSR count, marker ID, marker symbol, marker IMSR count ]
  var genoclusters = {};
  <c:forEach var="gc" items="${genoclusters}">
  	genoclusters["${gc.genoClusterKey}"] = [];
  	<c:forEach var="allele" items="${gc.genotype.alleles}">
  	  <c:if test="${allele.isWildType == 0}">
  	    genoclusters["${gc.genoClusterKey}"].push([ "${allele.primaryID}", "${allele.symbol}", ${allele.imsrStrainCount},
  	  	  "${allele.marker.primaryID}", "${allele.marker.symbol}", ${allele.imsrCountForMarker} ]);
  	  </c:if>
  	</c:forEach>
  </c:forEach>
  
  // find a string beginning with the given string 'c' that doesn't appear in string 's'
  var findTag = function(c, s) {
  	if (s.indexOf(c) < 0) { return c; }
  	return findTag(c + c[0], s);
  };
  
  // convert MGI superscript notation <...> to HTML superscript tags
  var superscript = function(s) {
    var openTag = findTag('{', s);
  	return s.split('<').join(openTag).split('>').join('</sup>').split(openTag).join('<sup>');
  };
  
  // turn the given accID and optional parameters into a link to IMSR
  var makeImsrLink = function(accID, imsrCount, optionalParms) {
  	var s = '(';
  	if (imsrCount == 0) {
  		s = s + imsrCount + " available)";
  	} else {
  		s = s + "<a target='_blank' class='findMice' href='${configBean.IMSRURL}summary?states=embryo&states=live"
			+ "&states=ovaries&states=sperm" + optionalParms + "&gaccid=" + accID + "'>" + imsrCount + " available)</a>";
  	}
  	return s;
  };
  
  // format the turn allele ID and IMSR count into a link to get corresponding data from IMSR
  var alleleImsrLink = function(accID, imsrCount) {
  	return makeImsrLink(accID, imsrCount, '');
  };

  // format the turn marker ID and IMSR count into a link to get corresponding data from IMSR
  var markerImsrLink = function(accID, imsrCount) {
  	return makeImsrLink(accID, imsrCount, '&states=ES+Cell');
  };
  
  // show the popup with IMSR info when the user clicks a Find Mice button
  var showDialog = function(event, genoclusterKey) {
	event.cancelBubble=true;
	var alleleCellID = "fm" + genoclusterKey + "a";
	$("#dialog").dialog();
	$(".ui-dialog").position({my:"left top", at:"left top", of:$("#" + alleleCellID), collision:"fit"});
	var gcKey = '' + genoclusterKey;
	var msg = "unknown genocluster key: " + genoclusterKey;

	if (gcKey in genoclusters) {
		var allZero = true;
		
		var header = "Using the International Mouse Strain Resource "
			+ "(<a target='_blank' href='${configBean.IMSRURL}'>IMSR</a>)<br/>";
		var msg = "Mouse lines carrying:<br/>";
		
		var gc = genoclusters[gcKey];
		for (var i = 0; i < gc.length; i++) {
			var g = gc[i];
			msg = msg + superscript(g[1]) + " mutation " + alleleImsrLink(g[0], g[2]);
			
			// if transgene, only show the allele part and omit the redundant marker part
			if (g[1] != g[4]) {
				msg = msg + "; any " + superscript(g[4]) + " mutation " + markerImsrLink(g[3], g[5]);
			}
			msg = msg + "<br/>";

			if ((g[2] > 0) || (g[5] > 0)) {
				allZero = false;
			}
		}
		
		// special message if all alleles and markers have no data in IMSR
		if (allZero) {
			msg = "No mouse lines available in IMSR.<br/>"
				+ "Click a genotype row to see annotation details and<br/>view publication links for author information.";
		}
	}
	// format the dialog with the new message and reside the popup after a 50ms wait (to let the formatting finish).
	// if the box would overflow the window, then allow wrapping.
	$("#dialog").hide().html("<div style='font-size: 90%'>" + msg + "</div>").fadeIn('fast').width('auto');
	setTimeout(function() {
		$(".ui-dialog").width('auto');
		if ($(".ui-dialog").width() > $(window).width()) {
			$(".ui-dialog").css('white-space', 'normal');
		}
	}, 50);
  };
</script>

</head>

<body style="margin: 8px; min-width: 1px;">

<div id="title">${pageTitle}</div>

<style>
/* turn off odd keyhole-shaped box around Find Mice popup close button */
.ui-button-icon-only .ui-button-text, .ui-button-icons-only .ui-button-text {
    padding: 0;
}
td.tableLabel {
	font-weight: bold;
	font-size: 110%;
	text-align:left;
	vertical-align:middle;
}
table tr td {
	padding: 3px;
	text-align: left;
	vertical-align: middle;
}
td.headerTitle {
	background-color: #dddddd;
	height: 50px;
	white-space: nowrap;
}
td.header {
	height: 150px;
	padding-bottom: 0px;
	text-align: left;
	vertical-align: bottom;
	white-space: nowrap;
	font-weight: normal;
	max-width: 25px;
	min-width: 25px;
	width: 25px;
}
td.mid {
	text-align: center;
	vertical-align: middle;
}
span.bg {
	color: red;
	font-weight: bold;
}
span.normal {
	color: #333333;
	font-weight: bold;
}
div.header {
	color: black;
    text-indent: 2px;
    width: 27px;
    min-width: 27px;
    max-width: 27px;
    position: relative;
    height: 100%;
    transform: skew(-45deg,0deg);
    -webkit-transform: skew(-45deg,0deg);
    left: 70px;
    overflow: hidden;
    top: 0px;
    border-left: 1px solid #dddddd;
    border-right: 1px solid #dddddd;
    border-top: 1px solid #dddddd;
}
span.header {
	transform: skew(45deg, 0deg) rotate(315deg);
    display: inline-block;
    width: 90px;
    position: absolute;
    text-align: left;
    left: -33px;
    bottom: 25px;
    font-size: 10pt;
    font-weight: bold;
    font-family: Arial, Helvetica;
    color: #666666;
}
td.border {
	border: thin solid black;
}

td.human100 { background-color: #F7861D; }
td.human6 { background-color: #F4A041; }
td.human2 { background-color: #F2BF79; }
td.human1 { background-color: #FBDBB4; }

td.mouse100 { background-color: #0C2255; }
td.mouse6 { background-color: #49648B; }
td.mouse2 { background-color: #879EBA; }
td.mouse1 { background-color: #C6D6E8; }

tr.highlight:hover { background-color: #FFFFCC; cursor: pointer }

div.ui-dialog {
	width: auto;
	white-space: nowrap;
}

a.findMice {
	color: blue;
	text-decoration: underline;
}
</style>

<c:if test="${not empty isPhenotype}">
	<div id="legend">
	  <table id="hdpSystemPopupLegend">
	    <tr>
		  <td>*</td><td>Aspects of the system are reported to show a normal phenotype.</td></tr><tr>
		  <td class="bgsensitive">!</td><td>Indicates phenotype varies with strain background.</td></tr><tr>
		  <td></td><td><span class="highlight">Highlighted Columns</span> contain at least one phenotype or disease result matching your search term(s).</td>
	    </tr>
	  </table>
	</div>
</c:if>

 <div id="dialog" title="Find Mice" style="display: none">
  <p>This is the default dialog which is useful for displaying information. The dialog window can be moved, resized and closed with the 'x' icon.</p>
</div>

<c:if test="${not empty isPhenotype}">
<%@ include file="/WEB-INF/jsp/hmdc/popup_hpo.jsp" %>
<p/>
<%@ include file="/WEB-INF/jsp/hmdc/popup_mp.jsp" %>
</c:if>

<c:if test="${not empty isDisease}">
<%@ include file="/WEB-INF/jsp/hmdc/popup_omim.jsp" %>
</c:if>

<!-- Table and Wrapping div -->

<div id="markerList">
<c:forEach var="marker" items="${markers2}" varStatus="status">
  Find Mice: IMSR strains or lines carrying any ${marker.symbol} Mutation
  <a href='${configBean.IMSRURL}summary?gaccid=${marker.primaryID}&states=ES+Cell&states=embryo&states=live&states=ovaries&states=sperm' target='_blank'>
    (${marker.countForImsr} available)
  </a><br/>
</c:forEach>
</div>

</body>
</html>
