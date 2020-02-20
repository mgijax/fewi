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
	<div id="poweredByDiv">Visualization and analysis tools provided by 
	  <a href="${externalUrls.MORPHEUS}" target="_blank">
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

<script type="text/javascript" src="${externalUrls.MORPHEUS}/js/morpheus-latest.min.js"></script>
<script type="text/javascript" src="${externalUrls.MORPHEUS}/js/morpheus-external-latest.min.js"></script>
<script type="text/javascript">
// Note that colorMap should not contain definitions less than zero.  (Below zero numbers make
// all the cells show up as white.)
var colorMap = [
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

// unique identifier for this particular session, so we can get updates from the server
var sessionKey = mgiRandomString(30);

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
	      missingColor: '#FFFFFF',
	      map: colorMap
  		  }
	  }); 

  // Hide the tab title at the top of the heat map, as it has odd characters that I can't get
  // to disappear (and it's not overly useful anyway, for our purposes).
  $('li.morpheus-sortable[role=presentation]').css('display', 'none');
  
  // Show the Tips popup after a brief delay, so we give the browser's scrollbars time to get
  // into place.
  setTimeout(function() { showPopup() }, 500);
}

function showPopup() {
	$('#tipsPopup').dialog( {
		title : 'Heat Map Legend',
		width : '400px',
		position : { my: 'right center', at: 'right center', within: window }
	} );
}

var statusUrl = fewiurl + '/gxd/rnaSeqHeatMap/status?sessionKey=' + sessionKey;
function retrieveUpdate(ms) {
	$.get(statusUrl, function(data) {
		if ($('#statusUpdates').length == 1) {
			$('#statusUpdates').empty();
			$('#statusUpdates').html(data);
			scheduleUpdate(ms);
			var margin = ($(window).width() - $('#progressMeter').width()) / 2;
			$('#progressMeter').css('margin-left', margin + 'px');
		}
	}).fail(function() {
			$('#statusUpdates').empty();
			$('#statusUpdates').text('Failed to get status update');
		});
}

function scheduleUpdate(ms) {
	if ($('#statusUpdates').length > 0) {
		setTimeout(function() { retrieveUpdate(ms); }, ms);
	}
}

$('#loadingMessage').empty();
$('#loadingMessage').html("<img src='/fewi/mgi/assets/images/loading.gif' height='24' width='24'> Loading data from GXD...");

var url = fewiurl + '/gxd/rnaSeqHeatMap/json?' + '${queryString}' + '&sessionKey=' + sessionKey;
$.get(url, function(data) { buildHeatMap(data); })
	.fail(function() {
		$('#loadingMessage').empty();
		$('#loadingMessage').html('Retrieval error. Please write User Support (<a href="${configBean.MGIHOME_URL}"/support/mgi_inbox.shtml" target="_blank">mgi-help@jax.org</a>) with your search parameters.');
	});

// Request status updates every 2 seconds.
scheduleUpdate(2000);
</script>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>