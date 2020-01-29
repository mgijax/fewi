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
<link rel="stylesheet" type="text/css" href="${externalUrls.MORPHEUS}/css/morpheus-latest.min.css" />

<style>
  body.yui-skin-sam .yui-panel .hd,
  body.yui-skin-sam .yui-ac-hd { background:none; background-color:#025; color:#fff; font-weight: bold;}
  body.yui-skin-sam .yui-ac-hd {padding: 5px;}
  body.yui-skin-sam div#outerGxd {overflow:visible;}
  .yui-dt table {width: 100%;}
  
  #subheader { min-width: 750px; }
  #ysfDiv {
  	float: left; min-width: 350px;
  }
  #poweredByDiv {
  	float: none; 
  }
  #tipsDiv {
  	float: right; min-width: 350px; text-align: right;
  }
  #tipsButton {
  	padding-top: 6px;
  	padding-bottom: 6px;
  	padding-left: 30px;
  	padding-right: 30px;
  	background-color: #DFEFFF;
    font-weight: bold;
    width: 60px;
    text-align: center;
    border: 1px solid black;
  }
  #tipsIcon {
  	float: right;
  	margin-top: -4px;
  }
</style>


<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapperGxd">
	<a href="${configBean.HOMEPAGES_URL}expression.shtml"><img class="gxdLogo" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="75"></a>
    <span class="titleBarMainTitleGxd">
    Gene Expression Data
    </span>
</div>

<!-- subheader bar -->
<div id="subheader">
	<div id="tipsDiv">
		<div id="tipsButton">Tips <img id="tipsIcon" src="${configBean.WEBSHARE_URL}images/help_small_transp.gif" border="0"/></div>
	</div>
	<div id="ysfDiv">${ysf}
	</div>
	<div id="poweredByDiv">Visualization and analysis powered by <B>Morpheus</B>
		<svg width="32px" height="32px">
			<g>
			<rect x="0" y="0" width="32" height="14" style="fill:#ca0020;stroke:none"></rect>
			<rect x="0" y="18" width="32" height="14" style="fill:#0571b0;stroke:none"></rect>
			</g>
		</svg>
	</div>
</div>

<!-- RNA-Seq hat map injected into this element -->
<div id="heatmapWrapper">
	<img src='/fewi/mgi/assets/images/loading.gif' height='24' width='24'> Loading Morpheus libraries...
</div>

<!-- RNA-Seq Heat Map legend injected into this element -->
<div id="legendPopupPanel" style="visibility: hidden;" class="facetFilter">
  <jsp:include page="gxd_rnaseq_heatmap_legend_popup.jsp"></jsp:include>
</div>

<script type="text/javascript" src="${externalUrls.MORPHEUS}/js/morpheus-latest.min.js"></script>
<script type="text/javascript" src="${externalUrls.MORPHEUS}/js/morpheus-external-latest.min.js"></script>
<script type="text/javascript">
var colorMap = [
	{ value: -1.0,
		color: '#FFFFFF'
		},
	{ value: 0,
		color: '#E0E0E0'
		},
	{ value: 0.0000998,
		color: '#E0E0E0'
		},
	{ value: 0.0001000,
		color: '#98CDF4'
		},
	{ value: 0.0011500,
		color: '#45AFFD'
		},
	{ value: 0.0022000,
		color: '#3292E4'
		},
	{ value: 0.1012000,
		color: '#1E74CA'
		},
	{ value: 0.2002000,
		color: '#105FAD'
		},
	{ value: 0.4001000,
		color: '#024990'
		},
	{ value: 0.6000000,
		color: '#000066'
		},
	{ value: 1,
		color: '#000000'
		}
];

function buildHeatMap(json) {
	  $('#heatmapWrapper').empty();

	  new morpheus.HeatMap({
	    el: $('#heatmapWrapper'),
	    dataset: morpheus.Dataset.fromJSON(json),
	    colorScheme: {
	      type: 'fractions',
	      scalingMode: 1,
	      stepped: false,
	      min: 0,
	      max: 5000,
	      map: colorMap,
    },
  });
	  
  // fix for odd font issue in tab-close button ('Ã—' instead of 'x')
  $('[data-target]')[0].innerHTML = 'x';
}

$('#heatmapWrapper').empty();
$('#heatmapWrapper').html("<img src='/fewi/mgi/assets/images/loading.gif' height='24' width='24'> Loading data from MGI...");

var url = fewiurl + '/gxd/rnaSeqHeatMap/json?' + '${queryString}';
$.get(url, function(data) { buildHeatMap(data); })
	.fail(function() {
		$('#heatmapWrapper').empty();
		$('#heatmapWrapper').text('Error in retrieval. Please contact User Support with the parameters of your search.');
	});
</script>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>