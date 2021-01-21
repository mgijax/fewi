<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

<c:if test="${not empty fromMarkerDetail}">
	<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>
	<link rel="stylesheet" href="https://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
	<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/bower_components/bootstrap/dist/css/bootstrap.css" />
	<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/bower_components/bootstrap/spacelab/bootstrap.min.css" />
	<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/bower_components/ng-dialog/css/ngDialog.min.css" />
	<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/bower_components/ng-dialog/css/ngDialog-theme-default.css" />
	<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/bower_components/ng-cells/dist/0.4.0/ng-cells.css" />
	<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/css/hmdc/search.css" />
	<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/css/hmdc/popup.css" />
</c:if>
<c:if test="${empty fromMarkerDetail}">
	<link rel="stylesheet" href="https://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
	<link rel="stylesheet" href="https://jqueryui.com/jquery-wp-content/themes/jqueryui.com/style.css">
	<%@ include file="header.jsp" %>
</c:if>

<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
<script src="https://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>

<script src="${configBean.FEWI_URL}/assets/js/hmdc_popup.js"></script>

<script>
  <% /* generate javascript object for genocluster data, used to populate Find Mice popups */ %>
  
  // genocluster key : list of [ allele ID, allele symbol, allele IMSR count, marker ID, marker symbol, marker IMSR count ]
  var genoclusters = {};
  <c:forEach var="gc" items="${genoclusters}">
  	genoclusters["${gc.genoclusterKey}"] = [];
  	<c:forEach var="allele" items="${gc.alleles}">
  	  genoclusters["${gc.genoclusterKey}"].push([ "${allele.alleleID}", "${allele.alleleSymbol}", ${allele.alleleImsrCount},
  		"${allele.markerID}", "${allele.markerSymbol}", ${allele.markerImsrCount} ]);
  	</c:forEach>
  </c:forEach>
  setImsrUrl('${configBean.IMSRURL}');
</script>

<c:if test="${not empty fromMarkerDetail}">
	<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>
	<%@ include file="/WEB-INF/jsp/marker_header.jsp" %>
	<div id="sgTitle">${e:forHtml(pageTitle)}</div>
	<script>
	<% /* for slimgrid popup, override title shown on page to make custom title for browser tab */ %>
	// change window title on page load
	document.title = '${marker.symbol} ' + '${e:forJavaScript(headerTerm)} ' + 'phenotype data';
	</script>
</c:if>
<c:if test="${empty fromMarkerDetail}">
	</head>
	<body style="margin: 8px; min-width: 1px;">
	<div id="title">${pageTitle}</div>
	<script>
	// change window title on page load
	document.title = '${pageTitle}';
	</script>
</c:if>

<c:if test="${not empty isPhenotype}">
	<div id="legend">
	  <table id="hdpSystemPopupLegend" class="popupTable">
	  	<c:if test="${not empty normalFlag}">
		    <tr><td>*</td><td>Aspects of the system are reported to show a normal phenotype.</td></tr>
	    </c:if>
	    <c:if test="${not empty bSensitiveFlag}">
		    <tr><td class="bgsensitive">!</td><td>Indicates phenotype varies with strain background.</td></tr>
	    </c:if>
	    <c:if test="${not empty highlights}">
	        <tr><td></td><td><span class="highlight">Highlighted Columns</span> contain at least one phenotype or disease result matching your search term(s).</td></tr>
        </c:if>
		<tr><td></td><td>Darker colors indicate <span title="The orange and blue squares indicate human and mouse data, respectively.  Darker colors indicate more supporting annotations.  The lightest color represents one annotation. 2-5 annotations is represented by a darker shade, 6-99 annotations darker still and more than 100 annotations by the darkest color." style="color: #3399f3; text-decoration: none;">more annotations</span></td></tr>
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
<%@ include file="/WEB-INF/jsp/hmdc/popup_do.jsp" %>
</c:if>

<!-- Table and Wrapping div -->

<div id="markerList">
<c:forEach var="marker" items="${markers2}" varStatus="status">
  Find Mice: IMSR strains or lines carrying any ${marker.symbol} Mutation
  <a href='${configBean.IMSRURL}summary?gaccid=${marker.primaryID}' target='_blank'>
    (${marker.countForImsr} available)
  </a><br/>
</c:forEach>
</div>

<c:if test="${not empty fromMarkerDetail}">
	<% /* apply standard MGI footer, then fix some alignment issues */ %>
	<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
	<script>
	// fix alignment in footer
	$('td[align="center"]').css({'text-align':'center'});
	$('td[align="right"]').css({'text-align':'right'});
	// fix height of quick search
	$('#searchToolTextArea').css({'height' : 'auto'});
	// fix floating tabs in Firefox
	$("#navRight ul:first").css({"margin-bottom" : 0});
	// shift column headers downward (override table style)
	$(".popupHeader").css({'padding-bottom':'0px'});
	</script>
</c:if>
<c:if test="${empty fromMarkerDetail}">
	</body>
	<script>
	// shift column headers downward (override table style)
	$(".popupHeader").css({'padding-bottom':'0px'})
	</script>
	</html>
</c:if>
