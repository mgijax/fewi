
var LOADING_IMG_SRC = "/fewi/mgi/assets/images/loading.gif";
var LOADING_IMG = "<img src=\""+LOADING_IMG_SRC+"\" height=\"24\" width=\"24\">";
var SHOW_MATRIX_LEGENDS = false;

// setup initial query string for requested marker
var querystring = "markerMgiId=" + mrkID;

// TODO - remove this if filters aren't needed;  refactor calls to this function 
function getQueryStringWithFilters() {
	
	// TODO - if I need to activate filtering
	//var filterString = getFilterCriteria();
	var filterString = '';
	
	var querystringWithFilters = querystring;

	if (filterString) {
		querystringWithFilters = querystringWithFilters + "&" + filterString;
	}
	return querystringWithFilters;
}


/**
 * Configure the structure by gene/phenotype matrix
 *
 * Some rendering and logic details are in gxd_summary_matrix.js
 */

var phenoSuperGrid = function()
{
	// gather query string and store in window scope 
	var querystringWithFilters = getQueryStringWithFilters();
	window.prevPhenoGridQuery=querystringWithFilters;

	var buildGrid = function()
	{
		// TODO -- figure out if this is still needed
		if (typeof getQueryString == 'function') window.querystring = getQueryString().replace('&idFile=&','&');

		currentGeneGrid = GxdTissueMatrix({
			target : "ggTarget",
			// the datasource allows supergrid to make ajax calls for the initial data,
			// 	as well as subsequent calls for expanding rows
			dataSource: {
				url: fewiurl + "gxd/genegrid/json?" + querystringWithFilters,
				batchSize: 50000,
				offsetField: "startIndex",
				limitField: "results",
				MSG_LOADING: LOADING_IMG+' Searching for data...',
				MSG_EMPTY: 'No results found.'
			},
			cellSize: 24,
			cellRenderer: GxdRender.StructureGeneCellRenderer,
			columnSort: function(a,b){ return FewiUtil.SortSmartAlpha(a.cid,b.cid);},
			verticalColumnLabels: true,
	        openCloseStateKey: "gg_"+querystring,
	        legendClickHandler: function(e){ geneMatrixLegendPopupPanel.show(); },
	        renderCompletedFunction: function()
	        {
	        	//When matrix is drawn/redrawn we resize it with margins, to fit the browser window
	        	makeMatrixResizable("ggTarget",40,40);
	        	if (SHOW_MATRIX_LEGENDS) {
	        		geneMatrixLegendPopupPanel.show();
	        	}
	        }
	    });
	}
	buildGrid();

}

phenoSuperGrid();


//popup for structure matrix legend
window.structMatrixLegendPopupPanel = new YAHOO.widget.Panel("structLegendPopupPanel",
		{ width:"260px", visible:false, constraintoviewport:true,
			context:['tabSummaryContent', 'tl', 'tr',['beforeShow','windowResize']]
});
window.structMatrixLegendPopupPanel.render();


