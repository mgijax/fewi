/*
* functions used to generate gxd tissue matrices
*/
if(window.fewiurl == undefined) window.fewiurl = ""; // default any incoming globals

/*
 * Takes a cell which is expected to have
 * 	detected, notDetected, ambiguous, and children counts
 * 	returns the color class we need to display for the cell
 *
 * Ex: blue1,blue2,blue3, red1, etc
 */
function resolveGridColorClass(cell)
{
	var cc = "gold";
	if(cell.detected>0)
	{
		// apply the detected bin
		if(cell.detected < 5) {
			cc = "blue1";
		}
		else if(cell.detected < 51){
			cc = "blue2";
		}
		else if (cell.detected < 1001){
			cc = "blue3";
		}
		else if (cell.detected < 10000){
			cc = "blue4";
		}	
		else {
			cc = "blue5";
		}
	}
	else if(cell.ambiguous>0) cc = "gray";
	else if(cell.notDetected>0)
	{
		// apply the not detected bin
		if(cell.notDetected < 5) {
			cc = "red1";
		}
		else if(cell.notDetected < 51) {
			cc = "red2";
		}
		else if (cell.notDetected < 1001) {
			cc = "red3";
		}
		else if (cell.notDetected < 10000) {
			cc = "red4";
		}
		else {
			cc = "red5";
		}
	}
	return cc;
}

/*
 * takes a cell and returns whether or not we display the negative results conflict flag
 * Note: this can only happen on a non-negative cell.
 */
function indicateNegativeMatrixResultsConflict(cell)
{
	if(cell.notDetected>0)
	{
		if(cell.detected>0) return true;
	}
	return false;
}

function drawStructureMatrixCell(d3Target,cellSize,cell)
{
	var fillClass = resolveGridColorClass(cell);
	var g = d3Target;

	// draw a data cell
	if(cell.isDummy) {
		g.append("circle")
			.attr("cx",(cellSize/2))
			.attr("cy",(cellSize/2))
			.style("fill","white" )
			.style("stroke-width","1" )
			.style("stroke","#CCC" )
			.style("fill","transparent")
			.attr("r","4");
	}
	else if ( fillClass == "gold" ) {
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
			addNegativeResultConflictIndicator(g,cellSize, cell);
		}
	}
}
function addNegativeResultConflictIndicator(d3Target,cellSize, cell)
{
	var points = [(cellSize - (cellSize/1.5))+","+0,
	              (cellSize)+","+0,
	              (cellSize)+","+(cellSize/1.5)];

		cc = "red5"
		if(cell.notDetected < 5) {
			cc = "red1";
		}
		else if(cell.notDetected < 51) {
			cc = "red2";
		}
		else if (cell.notDetected < 1001) {
			cc = "red3";
		}
		else if (cell.notDetected < 10000) {
			cc = "red4";
		}
		else {
			cc = "red5";
		}


	d3Target.append("polygon")
		.attr("points",points.join(" "))
		.attr("class",cc);
}


/*
 * An Aggregator is needed when we query the server for large batches of matrix data
 * Sometimes we get data for the same row+column in two different batches.
 * We want to aggregate that data (data being mostly counts of assay results)
 */
function gxdTissueMatrixCellAggregator(old,newCell){
	if(old.isDummy) return newCell;
	if(newCell.isDummy) return old;
	newCell.val = parseInt(old.val) + parseInt(newCell.val) + "";
	newCell.ambiguous += old.ambiguous;
	newCell.detected += old.detected;
	newCell.notDetected += old.notDetected;
	newCell.children += old.children;
	return newCell;
}

/**
 * Gxd Matrix Specific render functions
 */
window.GxdRender = new function()
{
	/*
	 * Renderer for Theiler Stage columns
	 */
	this.TSColumnRenderer = function(d3Target,cellSize,startX,startY)
	{
		var labelPaddingLeft = 4;
		var labelPaddingBottom = 4;

		var g = d3Target;

		g.append("text")
			.attr("x", 0)
			.attr("y",cellSize-labelPaddingBottom)
			.text(function(d){ return "TS"+d.cid;})
			.style("font-size","11px")
			.style("font-weight","bold");

		g.append("text")
			.attr("x", 50)
			.attr("y",cellSize-labelPaddingBottom)
			.text(function(d){ return stageDayMap[d.cid];})
			.style("font-size","11px")
      		.style("fill","#444444")
       		.style("font-weight","normal");
		return g;
	};

	/*
	 * Renderer for the EMAPA rows
	 */
	this.StructureRowRenderer = function(d3Target,cellHeight){
		var indentationWidth = 20; // pixels
    	var labelPaddingLeft = 16;
    	var labelPaddingBottom = 4;
        // draw a label
    	var g = d3Target;
        g.append("text")
            .attr("x",labelPaddingLeft)
            .attr("y",cellHeight-labelPaddingBottom)
            .text(function(d){return d.term;})
            .style("font-size",12+"px")
            .style("font-weight","bold")
            .attr("transform",function(d){ return "translate("+((d.depth || 0)*indentationWidth)+")"; });
        return g;
    };

    this.StructureStageCellRenderer = function(d3Target,cellSize,cell){
    	var g = d3Target;

    	drawStructureMatrixCell(g,cellSize,cell);

    	if(!cell.isDummy)
    	{
    		g.on("click", structStagePopupHandler)
        	.style("cursor","pointer");
    	}
		return g;
    };

    this.StructureGeneCellRenderer = function(d3Target,cellSize,cell){
    	var g = d3Target;

    	drawStructureMatrixCell(g,cellSize,cell);

        g.on("click", structGenePopupHandler)
        	.style("cursor","pointer");

		return g;
    };

	this.RowOpenedImgUrl = fewiurl+"assets/images/downArrow.gif";
	this.RowClosedImgUrl = fewiurl+"assets/images/rightArrow.gif";

	this.LegendIconUrl = fewiurl+"assets/images/info.gif";
	this.SelectionsIconUrl = fewiurl + "assets/images/macChecked.png";
	this.FilterIconUrl = fewiurl+"assets/images/filter.png";
	this.FilterCheckedUrl = fewiurl+"assets/images/macChecked.png";
	this.FilterUncheckedUrl = fewiurl+"assets/images/macUnchecked.png";
	this.FilterDashedUrl = fewiurl+"assets/images/macDashed.png";
}


/**
 * Gxd matrix popups
 */

window.stagePopupPanel = new YAHOO.widget.Panel("structStagePopup", { width:"320px", visible:false, constraintoviewport:true, xy:[0,0] });
window.stagePopupPanel.render();
function structStagePopupHandler(d, i) {

	// hide any prior instances
	stagePopupPanel.hide();

	// generate the popup at location of user click
	var newX = d3.event.pageX;
	var newY = d3.event.pageY;

	// add loading gif to popup
	var popupContents = $( "#structStagePopupContents" );
	popupContents.html(LOADING_IMG);

	// place and display the popup panel
	stagePopupPanel.moveTo(newX, newY);
	stagePopupPanel.show();

	// gather data needed for popup
	var querystringWithFilters = getQueryStringWithFilters();
//	var requestUrl = fewiurl + "gxd/stageMatrixPopup/json?" + querystringWithFilters
//		+ "&rowId=" + d.termId
//		+ "&colId=" + d.cid;
	var postRequestUrl = fewiurl + "gxd/stageMatrixPopup/json";
	var postParameters = querystringWithFilters + "&rowId=" + d.termId + "&colId=" + d.cid;

	// gather values for popup
	$.post(postRequestUrl, postParameters, function(data){
		var countPosGenes = data.countPosGenes;
		var countNegGenes = data.countNegGenes;
		var countAmbGenes = data.countAmbGenes;
		var countPosResults = data.countPosResults;
		var countNegResults = data.countNegResults;
		var countAmbResults = data.countAmbResults;

		// generate the small data table
		var popupHtml = "";
		popupHtml +=  "<div class='' style='margin-bottom:5px;'><table id='stagePopupTable' style=''>";
		popupHtml +=  "<div style='height:5px;'></div>";
		if (countPosResults > 0 || countNegResults > 0 || countAmbResults > 0) {
			popupHtml +=  "<tr><th># of Genes</th><th>Detected?</th><th># of Results</th></tr>";
			if (countPosResults > 0) {
				popupHtml +=  "<tr><td>" + commaDelimit(countPosGenes) + "</td><td>Yes</td><td>" + commaDelimit(countPosResults) + "</td></tr>";
			}
			if (countNegResults > 0) {
				popupHtml +=  "<tr><td>" + commaDelimit(countNegGenes) + "</td><td>No</td><td>" + commaDelimit(countNegResults) + "</td></tr>";
			}
			if (countAmbResults > 0) {
				popupHtml +=  "<tr><td>" + commaDelimit(countAmbGenes) + "</td><td>Ambiguous</td><td>" + commaDelimit(countAmbResults) + "</td></tr>";
			}
			popupHtml +=  "</table>";
		}
		else {
			popupHtml +=  "<div style='height:4em; padding:5px;'>Absent or ambiguous results are in substructures.</div>";
		}

		// add the buttons
		popupHtml +=  "<div id='matrixPopupButtonWrapper' >";
		popupHtml +=  "<button id='matrixPopupResultsButton' onClick='handleStageMatrixResultsButton(\"" + d.cid + "\", \"" + d.termId + "\");'>View All Results</button>";
		if (data.hasImage){
			popupHtml +=  "<button id='matrixPopupImagesButton' onClick='handleStageMatrixImagesButton(\"" + d.cid + "\", \"" + d.termId + "\");'>View Images</button>";
		}
		popupHtml +=  "</div>";
		popupHtml +=  "</div>";

		// clear and fill the popup
		popupContents.empty();
		popupContents.append( "<div class='' style='text-align:center; background-color:#EBCA6D; font-size: 110%; line-height: 2; font-weight: bold; margin-botton:5px;'>" + data.term + " at TS" + d.cid + "</div>" );
		popupContents.append(popupHtml);
	});
}

function handleStageMatrixResultsButton(stage, termId) {
	var newFacets = window.facets;
	newFacets["theilerStageFilter"] = [stage];
	newFacets["structureIDFilter"] = [termId];
	submitFacets(newFacets);
	resultsTabs.set("activeIndex",mgiTab.tabs["resultstab"]);
}
function handleStageMatrixImagesButton(stage, termId) {
	var newFacets = window.facets;
	newFacets["theilerStageFilter"] = [stage];
	newFacets["structureIDFilter"] = [termId];
	submitFacets(newFacets);
	resultsTabs.set("activeIndex",mgiTab.tabs["imagestab"]);
}


window.genePopupPanel = new YAHOO.widget.Panel("structGenePopup", { width:"320px", visible:false, constraintoviewport:true, xy:[0,0] });
window.genePopupPanel.render();
function structGenePopupHandler(d, i) {

	// hide any prior instances
	genePopupPanel.hide();

	// generate the popup at location of user click
	var newX = d3.event.pageX;
	var newY = d3.event.pageY;

	var popupContentDiv = $( "#structGenePopupContents" );
	popupContentDiv.html(LOADING_IMG);

	// place and display the popup panel
	genePopupPanel.moveTo(newX, newY);
	genePopupPanel.show();

	// gather data needed for popup
	var querystringWithFilters = getQueryStringWithFilters();
//	var requestUrl = fewiurl + "gxd/geneMatrixPopup/json?" + querystringWithFilters
//		+ "&rowId=" + d.termId
//		+ "&colId=" + d.cid;

	var postRequestUrl = fewiurl + "gxd/geneMatrixPopup/json";
	var postParameters =  querystringWithFilters + "&rowId=" + d.termId + "&colId=" + d.cid;
	// gather values for popup
	$.post(postRequestUrl, postParameters, function(data){
		var countPosResults = data.countPosResults;
		var countNegResults = data.countNegResults;
		var countAmbResults = data.countAmbResults;

		// generate the small data table
		var popupHtml = "";
		popupHtml +=  "<div class='' style='margin-bottom:5px;'><table id='stagePopupTable' style=''>";
		popupHtml +=  "<div style='height:5px;'></div>";
		if (countPosResults > 0 || countNegResults > 0 || countAmbResults > 0) {
			popupHtml +=  "<tr><th>Detected?</th><th># of Results</th></tr>";
			if (countPosResults > 0) {
				popupHtml +=  "<tr><td>Yes</td><td>" + commaDelimit(countPosResults) + "</td></tr>";
			}
			if (countNegResults > 0) {
				popupHtml +=  "<tr><td>No</td><td>" + commaDelimit(countNegResults) + "</td></tr>";
			}
			if (countAmbResults > 0) {
				popupHtml +=  "<tr><td>Ambiguous</td><td>" + commaDelimit(countAmbResults) + "</td></tr>";
			}
			popupHtml +=  "</table>";
		}
		else {
			popupHtml +=  "<div style='height:4em; padding:5px;'>Absent or ambiguous results are in substructures.</div>";
		}
		// add the buttons
		popupHtml +=  "<div id='matrixPopupButtonWrapper' >";
		popupHtml +=  "<button id='matrixPopupResultsButton' onClick='handleGeneMatrixResultsButton(\"" + d.cid + "\", \"" + d.termId + "\");'>View All Results</button>";
		if (data.hasImage){
			popupHtml +=  "<button id='matrixPopupImagesButton' onClick='handleGeneMatrixImagesButton(\"" + d.cid + "\", \"" + d.termId + "\");'>View Images</button>";
		}
		popupHtml +=  "</div>";
		popupHtml +=  "</div>";

		// clear and fill the popup
		popupContentDiv.empty();
		popupContentDiv.append( "<div class='' style='text-align:center; background-color:#EBCA6D; font-size: 110%; line-height: 2; font-weight: bold; margin-botton:5px;'>" + d.cid + " in " + data.term + "</div>" );
		popupContentDiv.append(popupHtml);
	});
}

function handleGeneMatrixResultsButton(gene, termId) {
	var newFacets = window.facets;
	newFacets["markerSymbolFilter"] = [gene];
	newFacets["structureIDFilter"] = [termId];
	submitFacets(newFacets);
	resultsTabs.set("activeIndex",mgiTab.tabs["resultstab"]);
}
function handleGeneMatrixImagesButton(gene, termId) {
	var newFacets = window.facets;
	newFacets["markerSymbolFilter"] = [gene];
	newFacets["structureIDFilter"] = [termId];
	submitFacets(newFacets);
	resultsTabs.set("activeIndex",mgiTab.tabs["imagestab"]);
}



/**
 * Functions for resizing matrix container based on browser window size
 */

/*
* Resizes the matrix based on window size
*/
function makeMatrixResizable(targetId,xPadding,yPadding)
{
	resizeMatrixContainer(targetId,xPadding,yPadding);
	$(window).off("resize."+targetId);
	$(window).on("resize."+targetId,function(e){
		resizeMatrixContainer(targetId,xPadding,yPadding);
	});
}

function resizeMatrixContainer(targetId,xPadding,yPadding)
{
	xPadding = xPadding || 0;
	yPadding = yPadding || 0;
	var windowWidth = $(window).width();
	var windowHeight = $(window).height();
	var mc = $("#"+targetId);
	var svg = mc.find("svg");
	var svgWidth = svg.width();
	var svgHeight = svg.height();

	if(!svgWidth || !svgHeight)
	{
		mc.width("auto");
		mc.height("auto");
		return;
	}

	// if content (I.e. svg is greater than window-padding, then we resize the container
	if((svgWidth+xPadding)>windowWidth)
	{
		mc.width(windowWidth-xPadding);
	}
	else if (svgWidth != mc.width())
	{
		mc.width(svgWidth+xPadding);
	}

	if((svgHeight+yPadding)>windowHeight)
	{
		mc.height(windowHeight-yPadding);
	}
	else if (svgHeight <= windowHeight)
	{
		mc.height(svgHeight+yPadding);
	}
}


/**
 * Default config values for both GxdMatrices
 *
 * Is a wrapper around the SuperGrid configuration
 *
 * Sets the cell and structure header renderers
 * Sets the icons for open/close behavior
 * Sets the icons for filter behavior
 */
function GxdTissueMatrix(config)
{
	var defaultConfig = {
	        cellAggregator: gxdTissueMatrixCellAggregator,
	        rowRenderer: GxdRender.StructureRowRenderer,
			openyClosey: true,
	        closeImageUrl: GxdRender.RowClosedImgUrl,
			openImageUrl: GxdRender.RowOpenedImgUrl,
			spinnerImageUrl: LOADING_IMG_SRC,
	        legendButtonIconUrl: GxdRender.LegendIconUrl,
		selectionsButtonIconUrl : GxdRender.SelectionsIconUrl,
	        filterButtonIconUrl: GxdRender.FilterIconUrl,
	        filterUncheckedUrl: GxdRender.FilterUncheckedUrl,
	        filterCheckedUrl: GxdRender.FilterCheckedUrl
	        // dashed checkbox defaults to unchecked image if no dashed image is set
	        //filterDashedUrl: GxdRender.FilterDashedUrl
	};
	// apply custom config
	for(key in config)
	{
		defaultConfig[key] = config[key];
	}
	return new SuperGrid(defaultConfig);
}
