<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>temporary title</title>
<meta name="robots" content="NOODP"/>
<meta name="robots" content="NOYDIR"/>

<%@ page trimDirectiveWhitespaces="true" %>

<link rel="stylesheet" href="https://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
<link rel="stylesheet" href="https://jqueryui.com/jquery-wp-content/themes/jqueryui.com/style.css">
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/bower_components/bootstrap/dist/css/bootstrap.css" />
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/bower_components/bootstrap/spacelab/bootstrap.min.css" />
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/bower_components/ng-dialog/css/ngDialog.min.css" />
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/bower_components/ng-dialog/css/ngDialog-theme-default.css" />
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/bower_components/ng-cells/dist/0.4.0/ng-cells.css" />
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/css/strain/strain_popup.css" />

<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
<script src="https://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>

<script src="${configBean.FEWI_URL}/assets/js/strain/strain_grid_popup.js"></script>

<script>
  <% /* generate javascript object for genotype data, used to populate Find Mice popups */ %>
  
  // genotype key : list of [ allele ID, allele symbol, allele IMSR count, marker ID, marker symbol, marker IMSR count ]
  var genotypes = {};
  <c:forEach var="row" items="${strainPhenoGroup.rows}">
  	genotypes["${row.genotype.genotypeKey}"] = [];
  	<c:forEach var="allele" items="${row.genotype.alleles}">
  	  genotypes["${row.genotype.genotypeKey}"].push([ "${allele.primaryID}", "${allele.symbol}", ${allele.imsrCellLineCount + allele.imsrStrainCount},
  		"${allele.marker.primaryID}", "${allele.marker.symbol}", ${allele.imsrCountForMarker} ]);
  	</c:forEach>
  </c:forEach>
  setImsrUrl('${configBean.IMSRURL}');
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>
<%@ include file="/WEB-INF/jsp/strain/strain_header.jsp" %>
<div id="sgTitle">Phenotype annotations related to ${headerTerm}</div>
<script>
<% /* for slimgrid popup, override title shown on page to make custom title for browser tab */ %>
// change window title on page load
document.title = '${strain.name} ' + '${headerTerm} phenotype data';
</script>

<div id="legend">
  <table id="hdpSystemPopupLegend" class="popupTable">
	<tr><td>Darker colors indicate <span title="The lightest color represents one annotation. 2-5 annotations is represented by a darker shade, 6-99 annotations darker still and more than 100 annotations by the darkest color." style="color: #3399f3; text-decoration: none;">more annotations</span></td></tr>
  </table>
</div>

 <div id="dialog" title="Find Mice" style="display: none">
  <p>This is the default dialog which is useful for displaying information. The dialog window can be moved, resized and closed with the 'x' icon.</p>
</div>

<%@ include file="/WEB-INF/jsp/strain/strain_grid_popup_mp.jsp" %>

<!-- Table and Wrapping div -->

</body>
<script>
// shift column headers downward (override table style)
$(".popupHeader").css({'padding-bottom':'0px'})
</script>
</html>
