<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>${marker.symbol} Expression Recombinase Matrix </title>

<meta name="description" content="">
<meta name="keywords" content="">
<meta name="robots" content="NOODP">
<meta name="robots" content="NOYDIR">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<script type="text/javascript">
        var fewiurl = "${configBean.FEWI_URL}";
        var mrkID = "${mrkID}";
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/external/d3.min.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/widgets/SuperGrid.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/fewi_utils.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxd_summary_matrix.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxd_recom_matrix.js"></script>

<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/gxd/gxd_summary.css" />
<style>

</style>


<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper" userdoc="EXPRESSION_correlation_matrix_help.shtml">    
        <span class="titleBarMainTitle">
        Gene Expression + Recombinase Correlation Matrix
        </span>
</div>

<!-- Gene/Recom grid injected into this element -->
<div id="geneRecomGridWrapper">
  <div id="geneRecomGridTarget" class="matrixContainer"></div>
</div>


<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>


<!-- Patterns for matrix sash icon -->
<svg height="0" width="0" xmlns="http://www.w3.org/2000/svg" version="1.1">
  <defs>
    <pattern id="sash28" patternUnits="userSpaceOnUse" width="28" height="28">
      <image xlink:href="${configBean.FEWI_URL}assets/images/sash.png"
        x="-3" y="-3" width="33" height="32">
      </image>
    </pattern>    
    <pattern id="sash24" patternUnits="userSpaceOnUse" width="24" height="24">
      <image xlink:href="${configBean.FEWI_URL}assets/images/sash.png"
        x="-3" y="-3" width="29" height="28">
      </image>
    </pattern>
  </defs>
</svg>