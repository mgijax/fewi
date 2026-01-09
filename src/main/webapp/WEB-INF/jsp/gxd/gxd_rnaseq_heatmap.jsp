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
<link rel="stylesheet" type="text/css" href="/morpheus/css/morpheus-latest.min.css" />

<style>
  body.yui-skin-sam .yui-panel .hd,
  body.yui-skin-sam .yui-ac-hd { background:none; background-color:#025; color:#fff; font-weight: bold;}
  body.yui-skin-sam .yui-ac-hd {padding: 5px;}
  body.yui-skin-sam div#outerGxd {overflow:visible;}
  .yui-dt table {width: 100%;}
  
  #subheader {
  	min-width: 750px;
  	margin-top: 3px;
  }
  #ysfDiv {
  	float: left;
  	width: 34%;
  	text-align: left;
  }
  #poweredByDiv {
  	float: none; 
  	text-align: center;
  }
  #tipsDiv {
  	width: 33%;
  	float: right;
  	text-align: center;
  }
  #tipsButton {
  	padding-top: 6px;
  	padding-bottom: 6px;
  	background-color: #DFEFFF;
    font-weight: bold;
    width: 90px;
    text-align: center;
    border: 1px solid black;
    white-space: nowrap;
  	margin-left: 250px;
  }
  #tipsIcon {
  	margin-top: -4px;
  }
  #morpheusIcon {
  	margin-bottom: -5px;
  }
  .smallGrey {
    font-size: 75%;
    color: #999999;
  }
  #loadingMessage {
  	text-align: center;
  }
  #statusUpdates {
    clear: both;
  	text-align: center;
  }
</style>


<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapperGxd">
	<a href="${configBean.HOMEPAGES_URL}expression.shtml"><img class="gxdLogo" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="75"></a>
    <span class="titleBarMainTitleGxd" style="display:inline-block; margin-top:20px; margin-right:60px;">
    Gene Expression Data
    </span>
</div>

<!-- subheader bar -->
<div id="subheader">
	<div id="tipsDiv">
		<div id="tipsButton" onClick="showPopup()">Tips <img id="tipsIcon" src="${configBean.WEBSHARE_URL}images/help_small_transp.gif" border="0"/></div>
	</div>
	<div id="ysfDiv">${ysf}
	</div>
	<div id="poweredByDiv">Visualization and analysis tools rendered by 
	  <a href="/morpheus" target="_blank">
		<B>Morpheus</B>
		<svg id="morpheusIcon" width="24px" height="24px">
			<g>
			<rect x="0" y="0" width="24" height="10" style="fill:#ca0020;stroke:none"></rect>
			<rect x="0" y="13" width="24" height="10" style="fill:#0571b0;stroke:none"></rect>
			</g>
		</svg>
	  </a>
	</div>
</div>

<!-- RNA-Seq heat map injected into this element -->
<div id="heatmapWrapper">
	<div id="loadingMessage">
		<img src='/fewi/mgi/assets/images/loading.gif' height='24' width='24'> Loading Morpheus libraries...
	</div>
	<div id="statusUpdates">
	</div>
</div>

<!-- RNA-Seq Heat Map legend injected into this element -->
<div id="tipsPopup" style="display:none;">
  <jsp:include page="gxd_rnaseq_heatmap_legend_popup.jsp"></jsp:include>
</div>

<script type="text/javascript" src="/morpheus/js/morpheus-latest.min.js"></script>
<script type="text/javascript" src="/morpheus/js/morpheus-external-latest.min.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxd/rnaseq_heatmap.js"></script>
<script type="text/javascript">
var queryString = '${e:forHtml(queryString)}';
main();
</script>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
