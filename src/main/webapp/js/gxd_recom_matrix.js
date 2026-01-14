// setup initial query string for requested marker
var querystring = "markerMgiId=" + mrkID;

if (alleleID != '') {
	querystring = querystring + "&alleleID=" + alleleID;
}

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
		console.log(cell.detected);
		// apply the detected bin
		if(cell.detected < 5) {	cc = "blue1"; }
		else if(cell.detected < 51) { cc = "blue2";	}
		else if(cell.detected < 1001) { cc = "blue3";	}
		else if(cell.detected < 10000) { cc = "blue4";	}
		else { cc = "blue5"; }
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


//rendering function for grid cells
function drawMatrixCell(d3Target,cellSize,cell){

	var g = d3Target;
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

	return g;
};

/**
 * Matrix Specific render functions
 */

//handler for the popup
YAHOO.namespace("recomGridNS.container");
window.firstPopup=true;
function recomGridPopupHandler(d, i) {

	// generate the popup at location of user click
	var newX = d3.event.pageX;
	var newY = d3.event.pageY;

	if(window.firstPopup){
		YAHOO.recomGridNS.container.panel1 = new YAHOO.widget.Panel("recomGridPopup", { width:"320px", visible:false, constraintoviewport:true, zIndex:5 } );
		YAHOO.recomGridNS.container.panel1.render();
		window.firstPopup=false;		
	} 
	YAHOO.recomGridNS.container.panel1.moveTo(newX, newY);
	YAHOO.recomGridNS.container.panel1.show();

	var popupContents = $( "#recomGridPopupContents" );

	if (d.cellType=='GXD') {

		// gather data needed for popup
		var querystringWithFilters = getQueryStringWithFilters();
		var requestUrl = fewiurl + "gxd/recombinasegridPopup/json?" + querystringWithFilters
			+ "&rowId=" + d.termId
			+ "&colId=" + d.cid;
		
		// gather values for popup
		$.getJSON(requestUrl, function(data){
			var countPosResults = data.countPosResults;
			var countNegResults = data.countNegResults;
			var countAmbResults = data.countAmbResults;
			var markerId = data.markerId;
			var symbol = data.symbol;
			var term = data.term;
			var termId = data.termId;
		
			var resultsURL = fewiurl + "gxd/marker/" + markerId + "?tab=#gxd=" + encodeURIComponent("markerMgiId=" + markerId + "&structureIDFilter=" + termId + "&wildtypeFilter=wild type");
			var imagesURL = fewiurl + "gxd/marker/" + markerId + "?tab=imagestab#gxd=" + encodeURIComponent("structureIDFilter=" + termId + "&results=25&startIndex=0&sort=&dir=asc&tab=imagestab");
	
			// generate the small data table
			var popupHtml = "";
			popupHtml +=  "<div class='' style='margin-bottom:5px;'><table id='stagePopupTable' style=''>";
			popupHtml +=  "<div style='height:5px;'></div>";
			if (countPosResults > 0 || countNegResults > 0 || countAmbResults > 0) {
				popupHtml +=  "<tr><th>Detected?</th><th># of Results</th></tr>";
				if (countPosResults > 0) {
					popupHtml +=  "<tr><td>Yes</td><td>" + countPosResults + "</td></tr>";
				}
				if (countNegResults > 0) {
					popupHtml +=  "<tr><td>No</td><td>" + countNegResults + "</td></tr>";
				}
				if (countAmbResults > 0) {
					popupHtml +=  "<tr><td>Ambiguous</td><td>" + countAmbResults + "</td></tr>";
				}
				popupHtml +=  "</table>";
			}
			else {
				popupHtml +=  "<div style='height:4em; padding:5px;'>Absent or ambiguous results are in substructures.</div>";
			}
	
			// add the buttons
			popupHtml +=  "<div id='matrixPopupButtonWrapper' >";
			popupHtml +=  "<a href='" + resultsURL + "'><button id='matrixPopupResultsButton'>View All Results</button></a>";
			if (data.hasImage){
				popupHtml +=  "<a href='" + imagesURL + "'><button id='matrixPopupImagesButton'>View Images</button></a>";
			}
			popupHtml +=  "</div>";
			popupHtml +=  "</div>";
	
			// clear and fill the popup
			popupContents.empty();
			popupContents.append( "<div class='' style='text-align:center; background-color:#EBCA6D; font-size: 110%; line-height: 2; font-weight: bold; margin-botton:5px;'>" + symbol + " Expression in " + term + "</div>" );
			popupContents.append(popupHtml);
		});
	} else { // recombinase cell
		
		// gather data needed for popup
		var querystringWithFilters = getQueryStringWithFilters();
		var requestUrl = fewiurl + "gxd/recombinasegridPopup/json?" + querystringWithFilters
			+ "&rowId=" + d.termId
			+ "&colId=" + d.cid
			+ "&alleleId=" + d.mgiId;
		
		// gather values for popup
		$.getJSON(requestUrl, function(data){
			var countPosResults = data.countPosResults;
			var countNegResults = data.countNegResults;
			var countAmbResults = data.countAmbResults;
			var markerId = data.markerId;
			var symbol = data.symbol;
			var term = data.term;
			var termId = data.termId;
			var allele = data.allele;
			var alleleLink = data.alleleLink;
		
			var resultsURL = fewiurl + "gxd/marker/" + markerId + "?tab=#gxd=" + encodeURIComponent("structureIDFilter=" + termId);
	
			// generate the small data table
			var popupHtml = "";
			popupHtml +=  "<div class='' style='margin-bottom:5px;'><table id='recomPopupTable' style=''>";
			popupHtml +=  "<div style='height:5px;'></div>";
			if (countPosResults > 0 || countNegResults > 0 || countAmbResults > 0) {
				popupHtml +=  "<tr><th>Detected?</th><th># of Results</th></tr>";
				if (countPosResults > 0) {
					popupHtml +=  "<tr><td>Yes</td><td>" + countPosResults + "</td></tr>";
				}
				if (countNegResults > 0) {
					popupHtml +=  "<tr><td>No</td><td>" + countNegResults + "</td></tr>";
				}
				if (countAmbResults > 0) {
					popupHtml +=  "<tr><td>Ambiguous</td><td>" + countAmbResults + "</td></tr>";
				}
				popupHtml +=  "</table>";
			}
			else {
				popupHtml +=  "<div style='height:4em; padding:5px;'>Absent or ambiguous results are in substructures.</div>";
			}
	
			// add the buttons
			popupHtml +=  "<div id='matrixPopupButtonWrapper' >";
			popupHtml +=  "<a href='" + alleleLink + "'><button id='matrixPopupPhenotypesButton'>View All Results</button></a>";
			popupHtml +=  "</div>";
			popupHtml +=  "</div>";
	
			// clear and fill the popup
			popupContents.empty();
			popupContents.append( "<div class='' style='text-align:center; background-color:#C6D6E8; font-size: 110%; line-height: 2; font-weight: bold; margin-botton:5px;'>" + allele + " recombinase activity in " + term + "</div>" );
			popupContents.append(popupHtml);
		});		
	}
}


window.GeneRecomMatrixRender = new function()
{
	/*
	 * Renderer for column headers
	 */
	this.StructureGeneRecomColumnHeaderRenderer = function(d3Target,cellSize,startX,startY)
	{
		var labelPaddingLeft = 4;
		var labelPaddingBottom = 4;

		// supergrid may resize this rect, as needed
		d3Target.append("rect")
		.attr("x", 2)
		.attr("y", cellSize-labelPaddingBottom)
		.attr("width", 150)
		.attr("height", 14)
		.attr("fill",function(d){ 
   		var isHighlightCol = d.highlightColumn;
    		if (isHighlightCol) {
                        return d.colOffset === 0 ? "#E2ac00" : "#D6C6E8";
    			}
    		return "transparent"})
		
		d3Target.append("text")
	    	.attr("x", 0)
	    	.attr("y",cellSize-labelPaddingBottom)
	    	.html(function(d){ 
                        console.log("column data object:", d)
	    		var displayValue = d.colDisplay.trim();
	    		if (displayValue.length > 33){
	    			displayValue = displayValue.substring(0,32) + "...";
	    		}
                        displayValue = displayValue.replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;');
                        var linkUrl = (d.colOffset > 0 && d.mgiId) ? ('/allele/' + d.mgiId + '?recomRibbon=open') : ''
                        var html = linkUrl ? `<a href="${linkUrl}">${displayValue}</a>` : displayValue
	    		return html;})	    	
	    	.style("fill",function(d){ 
	    		if (d.highlightColumn) {
	    			return "#000000";
	    			}
	    		return "#49648B"})
	    	.style("font-weight",function(d){ 
	    		if (d.highlightColumn) {
	    			return "700";
	    			}
	    		return "500"})
	    	.style("font-size","12px")
			.append("svg:title").text(function(d) {
                            const driverLine = d.colOffset > 0 ? "\n" + d.driverSpecies.toLowerCase() + " driver species" : ""
                            return d.colDisplay + driverLine
                        });	
		
		return  d3Target
	};

    this.StructureGeneRecomCellRenderer = function(d3Target,cellSize,cell){
    	var g = d3Target;

    	drawMatrixCell(g,cellSize,cell);

    	// adding onClick popup
    	g.on("click", recomGridPopupHandler).style("cursor","pointer");
    	
    	return g;
    };
}


/**
 * Configure the structure by gene/recombinase matrix
 *
 * Some rendering and logic details are in gxd_summary_matrix.js
 */
window.firstLegend=true;
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
			cellRenderer: GeneRecomMatrixRender.StructureGeneRecomCellRenderer,
			columnSort: function(a,b){ return parseInt(a.cid) - parseInt(b.cid) },
			verticalColumnLabels: true,
	        openCloseStateKey: "geneRecomGrid_"+querystring,
	        legendClickHandler: function(e){ YAHOO.recomGridNS.container.legendPanel.show(); },
	        renderCompletedFunction: function()
	        {
	        	//When matrix is drawn/redrawn we resize it with margins, to fit the browser window
	        	makeMatrixResizable("geneRecomGridTarget",40,40);

	        	if(window.firstLegend){
	        		// create the legend after grid has completed rendering
	        		YAHOO.recomGridNS.container.legendPanel = new YAHOO.widget.Panel("recomLegendPopupPanel", { width:"260px", visible:false, constraintoviewport:true, context:['geneRecomGridWrapper', 'tl', 'tr',['beforeShow','windowResize']] });
	        		YAHOO.recomGridNS.container.legendPanel.render();
	        		YAHOO.recomGridNS.container.legendPanel.show();
	        		window.firstLegend=false;		
	        	}
	        }
	    });
	}
	buildGrid();

}

geneRecomSuperGrid();



