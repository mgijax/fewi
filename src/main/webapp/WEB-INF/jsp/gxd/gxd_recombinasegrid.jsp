<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>${marker.symbol} Expression Recombinase Matrix </title>

<meta name="description" content="View normal gene expression alongside the tissues where recombinase activities driven by that gene have been demonstrated.">
<meta name="keywords" content="MGI, GXD, expression, recombinase activity, cre, tissue, EMAPA, , mice, mouse, murine, mus musculus">
<meta name="robots" content="NOODP">
<meta name="robots" content="NOYDIR">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<script type="text/javascript">
        var fewiurl = "${configBean.FEWI_URL}";
        var mrkID = "${mrkID}";
        var alleleID = "${alleleID}";
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/external/d3.min.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/widgets/SuperGrid.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/fewi_utils.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxd_summary_matrix.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxd_recom_matrix.js"></script>

<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/gxd/gxd_summary.css" />

<style>
  body.yui-skin-sam .yui-panel .hd,
  body.yui-skin-sam .yui-ac-hd { background:none; background-color:#025; color:#fff; font-weight: bold;}
  body.yui-skin-sam .yui-ac-hd {padding: 5px;}
  body.yui-skin-sam div#outerGxd {overflow:visible;}
  .yui-dt table {width: 100%;}
</style>


<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper" userdoc="RECOMBINASE_matrix_help.shtml">    
        <span class="titleBarMainTitle">
        Gene Expression + Recombinase Activity Comparison Matrix
        </span>
</div>

<%@ include file="/WEB-INF/jsp/marker_header.jsp" %>

<br/>

<!-- Gene/Recom grid injected into this element -->
<div id="geneRecomGridWrapper">
  <div id="geneRecomGridTarget" class="matrixContainer">
        <img src="/assets/images/loading.gif"/>
  </div>
</div>

<!-- Gene/Recom grid cell popup injected into this element -->
<div id="recomGridPopup" class="visHidden facetFilter structPopup">
  <div id="recomGridPopupContents"></div>
</div>

<!-- Gene/Recom grid legend injected into this element -->
<div id="recomLegendPopupPanel" style="visibility: hidden;" class="facetFilter">
  <jsp:include page="gxd_recom_matrix_legend_popup.jsp"></jsp:include>
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
