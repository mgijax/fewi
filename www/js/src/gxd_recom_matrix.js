// setup initial query string for requested marker
var querystring = "markerMgiId=" + mrkID;

// constants
var LOADING_IMG_SRC = "/fewi/mgi/assets/images/loading.gif";
var LOADING_IMG = "<img src=\""+LOADING_IMG_SRC+"\" height=\"24\" width=\"24\">";
var SHOW_MATRIX_LEGENDS = false;


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

// determine the color scheme of the gxd cells
function resolveGxdGridColorClass(cell)
{
	var cc = "gold";
	if(cell.detected>0)
	{
		// apply the detected bin
		if(cell.detected < 5) {	cc = "blue1"; }
		else if(cell.detected < 51) { cc = "blue2";	}
		else { cc = "blue3"; }
	}
	else if(cell.ambiguous>0) { cc = "gray"; }
	else if(cell.notDetected>0)
	{
		// apply the not detected bin
		if(cell.notDetected < 5) { cc = "red1"; }
		else if(cell.notDetected < 21) { cc = "red2"; }
		else { cc = "red3"; }
	}
	return cc;
}

//determine the color scheme of the pheno cells
function resolveRecomGridColorClass(cell)
{
	var cc = 'phenoBlue1';
//	var cc = '';
//	if(cell.phenoAnnotationCount>0)
//	{
//		if(cell.phenoAnnotationCount < 2) { cc = "phenoBlue1"; }
//		else if(cell.phenoAnnotationCount < 6) { cc = "phenoBlue2"; }
//		else if(cell.phenoAnnotationCount < 100) { cc = "phenoBlue3"; }
//		else { cc = "phenoBlue4"; }
//	}
	return cc;
}


//rendering function for grid cells
function StructureGeneRecomCellRenderer(d3Target,cellSize,cell){

	var g = d3Target;

	if (cell.cellType=="GXD") { // left-most column cells need GXD display 
		var fillClass = resolveGxdGridColorClass(cell);
		if ( fillClass == "gold" ) {
			var points = [(cellSize - (cellSize/1.5))+","+0,
		    	(cellSize)+","+0,
		    	(cellSize)+","+(cellSize/1.5)];
			g.append("polygon")
				.attr("points",points.join(" "))
				.attr("class",fillClass);
			g.append("rect")
				.attr("x",0)
				.attr("y",0)
				.attr("width",cellSize)
				.attr("height",cellSize)
				.style("fill","transparent");
		}
		else {
			g.append("rect")
				.attr("x",0)
				.attr("y",0)
				.attr("width",cellSize)
				.attr("height",cellSize)
				.style("stroke","#ccc")
				.style("stroke-width","1px")
				.attr("class",fillClass);
	
			if(indicateNegativeMatrixResultsConflict(cell)){
				addNegativeResultConflictIndicator(g,cellSize);
			}
		}
	}
	else { // other cells are recombinase cells
		var fillClass = resolveRecomGridColorClass(cell);
		g.append("rect")
		.attr("x",0)
		.attr("y",0)
		.attr("width",cellSize)
		.attr("height",cellSize)
		.style("stroke","#ccc")
		.style("stroke-width","1px")
		.attr("class",fillClass);
	}

	return g;
};

/**
 * Matrix Specific render functions
 */

window.GeneRecomMatrixRender = new function()
{
	/*
	 * Renderer for column headers
	 */
	this.StructureGeneRecomColumnHeaderRenderer = function(d3Target,cellSize,startX,startY)
	{
		var labelPaddingLeft = 4;
		var labelPaddingBottom = 4;
	    return  d3Target.append("text")
	    	.attr("x", 0)
	    	.attr("y",cellSize-labelPaddingBottom)
	    	.text(function(d){ return d.colDisplay;})
	    	.style("font-size","12px")
	    	.style("font-weight","bold");
	};
}


/**
 * Configure the structure by gene/recombinase matrix
 *
 * Some rendering and logic details are in gxd_summary_matrix.js
 */
var geneRecomSuperGrid = function()
{
	// gather query string and store in window scope 
	var querystringWithFilters = getQueryStringWithFilters();
	window.prevGeneRecomGridQuery=querystringWithFilters;

	var buildGrid = function()
	{
		// TODO -- figure out if filtering is still needed
		if (typeof getQueryString == 'function') window.querystring = getQueryString().replace('&idFile=&','&');

		currentGeneGrid = GxdTissueMatrix({
			target : "geneRecomGridTarget",
			// the datasource allows supergrid to make ajax calls for the initial data,
			// 	as well as subsequent calls for expanding rows
			dataSource: {
				url: fewiurl + "gxd/recombinasegrid/json?" + querystringWithFilters,
				batchSize: 50000,
				offsetField: "startIndex",
				limitField: "results",
				MSG_LOADING: LOADING_IMG+' Searching for data...',
				MSG_EMPTY: 'No results found.'
			},
			cellSize: 24,
			columnRenderer: GeneRecomMatrixRender.StructureGeneRecomColumnHeaderRenderer,
			cellRenderer: StructureGeneRecomCellRenderer,
			columnSort: function(a,b){ 
				if(a.cid>b.cid) return 1;
				else if (a.cid<b.cid) return -1;
				return 0;
			},
			verticalColumnLabels: true,
	        openCloseStateKey: "geneRecomGrid_"+querystring,
	        legendClickHandler: function(e){ geneMatrixLegendPopupPanel.show(); },
	        renderCompletedFunction: function()
	        {
	        	//When matrix is drawn/redrawn we resize it with margins, to fit the browser window
	        	makeMatrixResizable("geneRecomGridTarget",40,40);
	        	if (SHOW_MATRIX_LEGENDS) {
	        		geneMatrixLegendPopupPanel.show();
	        	}
	        }
	    });
	}
	buildGrid();

}

geneRecomSuperGrid();


//popup for structure matrix legend
window.structMatrixLegendPopupPanel = new YAHOO.widget.Panel("structLegendPopupPanel",
		{ width:"260px", visible:false, constraintoviewport:true,
			context:['tabSummaryContent', 'tl', 'tr',['beforeShow','windowResize']]
});
window.structMatrixLegendPopupPanel.render();


