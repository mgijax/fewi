<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>${marker.symbol} Expression Phenotype Matrix </title>

<meta name="description" content="View normal gene expression alongside the tissues where mutations in that gene have phenotype effects.">
<meta name="keywords" content="MGI, GXD, expression, phenotype, tissue, mutation, EMAPA, MP, mice, mouse, murine, mus musculus">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<script type="text/javascript">
        var fewiurl = "${configBean.FEWI_URL}";
        var mrkID = "${mrkID}";
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/external/d3.min.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/widgets/SuperGrid.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/fewi_utils.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxd_summary_matrix.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxd_pheno_matrix.js"></script>



<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/gxd/gxd_summary.css" />
<style>

</style>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapper" userdoc="GENE_summary_help.shtml">    
        <span class="titleBarMainTitle">
        ${marker.symbol} Gene Expression + Phenotype Correlation Matrix
        </span>
</div>

<div id="phenoGridWrapper">
  <div id="phenoGridTarget" class="matrixContainer"></div>
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