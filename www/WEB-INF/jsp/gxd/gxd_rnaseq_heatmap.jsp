<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Gene Expression Data: RNA-Seq Heat Map</title>

<meta name="description" content="View a heat map of mouse RNA-Seq data.  Columns are samples, rows are genes, and each cell represents an average quantile normalized TPM value.">
<meta name="keywords" content="MGI, GXD, expression, phenotype, tissue, mutation, EMAPA, MP, mice, mouse, murine, mus musculus, RNA-Seq, heat map, TPM">
<meta name="robots" content="NOODP">
<meta name="robots" content="NOYDIR">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<script type="text/javascript">
        var fewiurl = "${configBean.FEWI_URL}";
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/fewi_utils.js"></script>

<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/gxd/gxd_summary.css" />

<style>
  body.yui-skin-sam .yui-panel .hd,
  body.yui-skin-sam .yui-ac-hd { background:none; background-color:#025; color:#fff; font-weight: bold;}
  body.yui-skin-sam .yui-ac-hd {padding: 5px;}
  body.yui-skin-sam div#outerGxd {overflow:visible;}
  .yui-dt table {width: 100%;}
  
  #ysfDiv {}
  #poweredByDiv {}
  #tipsDiv {}
</style>


<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapper">
        <span class="titleBarMainTitle">
        Gene Expression Data
        </span>
</div>

<!-- subheader bar -->
<div id="subheader">
	<div id="ysfDiv">${ysf}
	</div>
	<div id="poweredByDiv">Powered By
	</div>
	<div id="tipsDiv">Tips
	</div>
</div>

<!-- RNA-Seq hat map injected into this element -->
<div id="heatmapWrapper">
	<img src='/fewi/mgi/assets/images/loading.gif' height='24' width='24'> Loading...
</div>

<!-- RNA-Seq Heat Map legend injected into this element -->
<div id="legendPopupPanel" style="visibility: hidden;" class="facetFilter">
  <jsp:include page="gxd_rnaseq_heatmap_legend_popup.jsp"></jsp:include>
</div>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/external/morpheus-01-2020.min.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/external/morpheus-external-01-2020.min.js"></script>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>