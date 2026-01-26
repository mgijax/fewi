// setup initial query string for requested marker
var querystring = "markerMgiId=" + mrkID;
if ((genoclusterKey != null) && (genoclusterKey.trim().length > 0)) {
	querystring = querystring + "&genoclusterKey=" + genoclusterKey;
}

// constants
var LOADING_IMG_SRC = "/fewi/mgi/assets/images/loading.gif";
var LOADING_IMG = "<img src=\""+LOADING_IMG_SRC+"\" height=\"24\" width=\"24\">";
var SHOW_MATRIX_LEGENDS = false;

// convert MGI superscript notation <...> to HTML superscript tags
var findTag = function(c, s) {
      if (s.indexOf(c) < 0) { return c; }
      return findTag(c + c[0], s);
};
var superscript = function(s) {
	//var s = s.replace("<br/>", "LINE_BREAK");
	var s = s.split("<br/>").join("LINE_BREAK")
	var openTag = findTag('{', s);
	s = s.split('<').join(openTag).split('>').join('</sup>').split(openTag).join('<sup>');
	s = s.split("LINE_BREAK").join("<br/>")
	return s;
};


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

//determine the color scheme of the pheno cells
function resolvePhenoGridColorClass(cell)
{
	var cc = '';
	if(cell.phenoAnnotationCount>0)
	{
		if(cell.phenoAnnotationCount < 2) { cc = "phenoBlue1"; }
		else if(cell.phenoAnnotationCount < 6) { cc = "phenoBlue2"; }
		else if(cell.phenoAnnotationCount < 100) { cc = "phenoBlue3"; }
		else { cc = "phenoBlue4"; }
	}
	return cc;
}


//rendering function for grid cells
function drawMatrixCell(d3Target,cellSize,cell){

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
				addNegativeResultConflictIndicator(g,cellSize,cell);
			}
		}
	}
	else { // other cells are pheno cells
		var fillClass = resolvePhenoGridColorClass(cell);
		g.append("rect")
		.attr("x",0)
		.attr("y",0)
		.attr("width",cellSize)
		.attr("height",cellSize)
		.style("stroke","#ccc")
		.style("stroke-width","1px")
		.attr("class",fillClass);
		
		if (cell.isNormal == 1) {
			g.append("text")
			.attr("x", 5)
			.attr("y", 16)
			.text("N")
			.style("font-size","12px")
			.style("font-weight","bold");
		}

		if (cell.hasBackgroundSensitivity == 1) {
			g.append("text")
			.attr("x", 14)
			.attr("y", 16)
			.text("!")
			.style("fill","red")
			.style("font-size","12px")
			.style("font-weight","bold");
		}
	}

	return g;
};

// if a genoclusterKey field is specified in the 'url', remove it
function stripGenoclusterKey(url) {
	return url.replace(/&genoclusterKey=[0-9]+/, '');
}

// handler for the popup
YAHOO.namespace("phenoGridNS.container");
window.firstPopup=true;
function phenoGridPopupHandler(d, i) {

	// generate the popup at location of user click
	var newX = d3.event.pageX;
	var newY = d3.event.pageY;

	if(window.firstPopup){
		YAHOO.phenoGridNS.container.panel1 = new YAHOO.widget.Panel("phenoGridPopup", { width:"320px", visible:false, constraintoviewport:true, zIndex:5 } );
		YAHOO.phenoGridNS.container.panel1.render();
		window.firstPopup=false;		
	} 
	YAHOO.phenoGridNS.container.panel1.moveTo(newX, newY);
	YAHOO.phenoGridNS.container.panel1.show();

	var popupContents = $( "#phenoGridPopupContents" );

	if (d.cellType=='GXD') {

		// gather data needed for popup
		var querystringWithFilters = getQueryStringWithFilters();
		var requestUrl = fewiurl + "gxd/phenogridPopup/json?" + querystringWithFilters
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
	}
	else {  // we have a pheno cell

		// gather data needed for popup -- If we came from a genocluster detail page, the querystring will have
		// that genocluster key; we need to remove that before adding the genocluster key for the cell clicked.
		var querystringWithFilters = getQueryStringWithFilters();
		var requestUrl = fewiurl + "gxd/phenogridPopup/json?" + stripGenoclusterKey(querystringWithFilters)
			+ "&rowId=" + d.termId
			+ "&colId=" + d.cid
			+ "&genoclusterKey=" + d.genoclusterKey;
		
		// gather values for popup
		$.getJSON(requestUrl, function(data){
			var alleles = data.alleles;
			var genoclusterLink = data.genoclusterLink;
			var term = data.term;
	
			// generate the small data table
			var popupHtml = "";
			popupHtml +=  "<div class='' style='margin-bottom:5px;'><table id='stagePopupTable' style=''>";
			popupHtml +=  "<div style='height:5px;'></div>";

			// add the buttons
			popupHtml +=  "<div id='matrixPopupButtonWrapper' >";
			popupHtml +=  "<a href='" + genoclusterLink + "'><button id='matrixPopupPhenotypesButton'>View Phenotype Details</button></a>";
		
			popupHtml +=  "</div>";
			popupHtml +=  "</div>";
	
			// clear and fill the popup
			popupContents.empty();
			popupContents.append( "<div class='' style='overflow-wrap: break-word; word-wrap: break-word; text-align:center; background-color:#C6D6E8; font-size: 110%; line-height: 2; font-weight: bold; margin-botton:5px;'>" + superscript(alleles) + "<br/>phenotypes in " + term + "</div>" );
			popupContents.append(popupHtml);
		});

	}
}



/**
 * Matrix Specific render functions
 */

window.PhenoMatrixRender = new function()
{
	/*
	 * Renderer for column headers
	 */
	this.StructurePhenoColumnHeaderRenderer = function(d3Target,cellSize,startX,startY)
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
    			return "#E2ac00";
    			}
    		return "transparent"})
		
		d3Target.append("text")
	    	.attr("x", 0)
	    	.attr("y",cellSize-labelPaddingBottom)
	    	.text(function(d){ 
	    		var displayValue = d.colDisplay.trim();
	    		if (displayValue.length > 33){
	    			displayValue = displayValue.substring(0,32) + "...";
	    		}
	    		return displayValue;})	    	
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
			.append("svg:title").text(function(d) { return d.colDisplay; });	
		
		return  d3Target
	};

    this.StructureStageCellRenderer = function(d3Target,cellSize,cell){
    	var g = d3Target;

    	drawMatrixCell(g,cellSize,cell);

    	// adding onClick popup
    	g.on("click", phenoGridPopupHandler).style("cursor","pointer");
    	
    	return g;
    };
}


/**
 * Configure the structure by gene/phenotype matrix
 *
 * Some rendering and logic details are in gxd_summary_matrix.js
 */
window.firstLegend=true;
var phenoSuperGrid = function()
{
	// gather query string and store in window scope 
	var querystringWithFilters = getQueryStringWithFilters();
	window.prevPhenoGridQuery=querystringWithFilters;

	var buildGrid = function()
	{
		// TODO -- figure out if filtering is still needed
		if (typeof getQueryString == 'function') window.querystring = getQueryString().replace('&idFile=&','&');

		currentGeneGrid = GxdTissueMatrix({
			target : "phenoGridTarget",
			// the datasource allows supergrid to make ajax calls for the initial data,
			// 	as well as subsequent calls for expanding rows
			dataSource: {
				url: fewiurl + "gxd/phenogrid/json?" + querystringWithFilters,
				batchSize: 50000,
				offsetField: "startIndex",
				limitField: "results",
				MSG_LOADING: LOADING_IMG+' Searching for data...',
				MSG_EMPTY: 'No results found.'
			},
			cellSize: 24,
			columnRenderer: PhenoMatrixRender.StructurePhenoColumnHeaderRenderer,
			cellRenderer: PhenoMatrixRender.StructureStageCellRenderer,
			columnSort: function(a,b){ 
				if(a.cid>b.cid) return 1;
				else if (a.cid<b.cid) return -1;
				return 0;
			},
			verticalColumnLabels: true,
	        openCloseStateKey: "phenoGrid_"+querystring,
	        legendClickHandler: function(e){ 

	        	YAHOO.phenoGridNS.container.legendPanel.show();
	        	
	        },
	        renderCompletedFunction: function()
	        {
	        	//When matrix is drawn/redrawn we resize it with margins, to fit the browser window
	        	makeMatrixResizable("phenoGridTarget",40,40);

	        	// create the legend after grid has completed rendering
	        	if(window.firstLegend){
		        	YAHOO.phenoGridNS.container.legendPanel = new YAHOO.widget.Panel("geneLegendPopupPanel", { width:"530px", visible:false, constraintoviewport:true, context:['phenoGridWrapper', 'tl', 'tr',['beforeShow','windowResize']] });
		        	YAHOO.phenoGridNS.container.legendPanel.render();
		        	YAHOO.phenoGridNS.container.legendPanel.show();
	        		window.firstLegend=false;		
	        	} 
	        }
	    });
	}
	buildGrid();

}

phenoSuperGrid();
















