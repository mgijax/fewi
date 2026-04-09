/**
* Depends on the following libraries:
*   jQuery
*   D3
*
*	Contains following classes:
*
*	SGUtil - static utility functions
*	SGDataSource - Used by data manager to perform queries to load and update the matrix data
*	SGData - Data manager for the matrix cells, rows, and columns
*	SGCart - Holds row and column selections
*	SuperGrid - The actual widget containing UI controls and display logic.
*
*   author: kstone
*/
function log(text)
{
	console.log(text);
}

/*
 * Utility functions used by the SuperGrid
 */
SGUtil = new function()
{
	var _self = this;
    this.isElementScrolledBottom = function(domElement)
    {
        return $(domElement).scrollTop() +  $(domElement).innerHeight() >= domElement.scrollHeight;
    }

    this.isElementScrolledRight = function(domElement)
    {
        return $(domElement).scrollLeft() +  $(domElement).innerWidth() >= domElement.scrollWidth;
    }

    this.translateElementRight = function(d3Element,pixels)
    {
    	if(d3Element)
    	{
	    	// get any existing x value
	    	var trans = d3Element.attr("transform");
	    	var tValues = _self.getTranslateValues(trans);
	    	tValues[0] = pixels;
	        d3Element.attr("transform","translate("+tValues.join(",")+")");
    	}
    }

    this.translateElementDown = function(d3Element,pixels)
    {
    	if(d3Element)
    	{
	    	// get any existing x value
	    	var trans = d3Element.attr("transform");
	    	var tValues = _self.getTranslateValues(trans);
	    	tValues[1] = pixels;
	        d3Element.attr("transform","translate("+tValues.join(",")+")");
    	}
    }

    this.getTranslateValues = function(trans)
    {
    	if(trans && trans.length>0)
    	{
	    	var begin = "translate(";
	    	var si = trans.indexOf(begin);
	    	if(si>=0)
	    	{
	    		var meat = trans.slice(begin.length+si);
	    		var ei = meat.indexOf(")");
	    		if(ei>0)
	    		{
	    			meat = meat.slice(0,ei);
	    			var values = meat.split(/[\s,]+/);
	    			values[0] = parseFloat(values[0]);
	    			if(values.length==1) values.push(0);
	    			else values[1] = parseFloat(values[1]);
	    			return values;
	    		}
	    	}
    	}
    	return [0,0];
    }

    this.changeRotateOriginY = function(oldTransform,y)
    {
    	var trans = oldTransform;
    	if(trans && trans.length>0)
    	{
    		var begin = "rotate(";
    		var si = trans.indexOf(begin);
    		if(si<0) return oldTransform;

    		var meatStart = begin.length+si;
    		var meat = trans.slice(meatStart);
    		var ei = meat.indexOf(")");
    		if(ei>0)
    		{
    			meat = meat.slice(0,ei);
    			var values = meat.split(/[\s,]+/);
    			if(values.length<3) return oldTransform;
    			values[2] = y;

	    		// replace what is between c2 and ei
	    		var newTrans = trans.slice(0,meatStart) + (values.join(",")) + trans.slice(meatStart+ei,trans.length);
	    		return newTrans;
    		}
    	}
    	return oldTransform;
    }

    this.changeTranslateY = function(oldTransform,y)
    {
    	var trans = oldTransform;
    	if(trans && trans.length>0)
    	{
    		var begin = "translate(";
    		var si = trans.indexOf(begin);
    		if(si<0) return oldTransform;

    		var meatStart = begin.length+si;
    		var meat = trans.slice(meatStart);
    		var ei = meat.indexOf(")");
    		if(ei>0)
    		{
    			meat = meat.slice(0,ei);
    			var values = meat.split(/[\s,]+/);
    			if(values.length<3) return oldTransform;
    			values[2] = y;

	    		// replace what is between c2 and ei
	    		var newTrans = trans.slice(0,meatStart) + (values.join(",")) + trans.slice(meatStart+ei,trans.length);
	    		return newTrans;
    		}
    	}
    	return oldTransform;
    }

    /*
     * creates an SVG bounding box for example text and style
     * 	in order to get an accurate width/height of the text element
     *  NOTE: may be expensive to call often as it has to insert and SVG tag and then remove it
     */
    this.createSVGBoundingBox = function(text,style)
    {
    	var tempId = "tempText1234";
    	var tempSvgId = "tempSvg1234";
    	$("body").append("<svg id=\""+tempSvgId
    			+"\">"
    				+"<text id=\""+tempId+"\" style=\""
    				+style+"\">"
    				+text+"</text>"
    			+"</svg>");
    	var bbox = $("#"+tempId)[0].getBBox();
    	$("#"+tempSvgId).remove();
    	return bbox;
    }

    /*
     * Takes a class string, then replaces any occurrence of oldClass with newClass
     * ex: swapClass("odd row1","row1","row2") returns "odd row2"
     */
    this.swapClass = function(classString,oldClass,newClass)
    {
    	var cs = classString.split(" ");
    	var nc = [];
    	for(var i=0;i<cs.length;i++)
    	{
    		if(cs[i]==oldClass) continue;
    		nc.push(cs[i]);
    	}
    	nc.push(newClass);
    	return nc.join(" ");
    }

    /*
     * Turns string into css id or class
     */
    this.toCss = function(text)
    {
    	if(text)
    	{
    		return text.replace(/[^a-z0-9]/g, function(s) {
    	        var c = s.charCodeAt(0);
    	        if (c == 32) return '-';
    	        if (c >= 65 && c <= 90) return '_' + s.toLowerCase();
    	        return '__' + ('000' + c.toString(16)).slice(-4);
    	    });
    	}
    	return "";
    }

    /*
     * Checks if variable is object
     */
    this.isObject = function(obj)
    {
    	return typeof obj === 'object' && obj !== null
    }
}

// SuperGrid Datasource
/*
 * required params:
 * 	url
 *
 * optional params:
 * 	MSG_LOADING,
 *  MSG_EMPTY,
 *  batchSize
 *  	if batchSize specified, you must also specify
 *  	offsetField + limitField (these params are added to request for batch data fetching)
 */
function SGDataSource(config)
{
	this.url = config.url;
	this.MSG_LOADING = config.MSG_LOADING || 'Searching for data...';
	this.MSG_EMPTY = config.MSG_EMPTY || 'No data found';
	this.doBatching = config.batchSize && true || false;
	if(this.doBatching)
	{
		this.batchSize = config.batchSize;
		this.offsetField = config.offsetField || 'offset';
		this.limitField = config.limitField || 'limit';
	}

	var _self = this;
	/*
	 * specify the initial success callback.
	 * 		If batching, you can specify and optional updateCallback
	 *
	 * can accept:
	 * 	initialCallback - called after first data retrieval
	 * 	updateCallback - called after every subsequent retrieval
	 *  doneCallback - called when all data is done being gathered
	 *  args - any extra args to add to request
	 */
	this.fireQuery = function(args)
	{
		_self.initialCallback = args.initialCallback || function(){};
		// use initialCallback if no updateCallback specified
		_self.updateCallback = args.updateCallback || _self.initialCallback;
		// optional doneCallback
		_self.doneCallback = args.doneCallback || function(){};

		_self.extraArgs = args.args || {};

		_self.initial = true;
		_self.done = false;

		_self.ajaxRequest();
	}

	this.cancelAllRequests = function()
	{
		_self.done=true;
	}

	this.ajaxRequest = function()
	{
		if(_self.done)
		{
			_self.doneCallback();
			return;
		}

		if(_self.initial)
		{
			_self.initial = false;
			_self.offset=0;
			_self.callback = _self.initialCallback;
		}
		else
		{
			_self.callback = _self.updateCallback;
		}
		var url = _self.url;
		if(_self.extraArgs)
		{
			var pairs = [];
			for(key in _self.extraArgs)
			{
				pairs.push(key+"="+_self.extraArgs[key]);
			}
			url += "&"+(pairs.join("&"));
		}
		if(_self.doBatching)
		{
			url += "&"+_self.offsetField +"="+_self.offset;
			url += "&"+_self.limitField +"="+_self.batchSize;
		}
		$.ajax({
			url: url.substring(0,url.indexOf('?')),
			data: url.substring(url.indexOf('?') + 1),
//			url: url,
			method: 'POST'
		}).done(function(data){
			if(data==undefined || data.data==undefined || data.data.length==0)
			{
				_self.done = true;
			}

			var e = {};
			_self.callback(e,data);

			// fire off the next batch
			_self.ajaxRequest();
		});

		_self.offset += _self.batchSize;
	}
}

// Super Grid Data
/*
 * Manages all the data cell lookups and row/column management
 */
function SGData(config)
{
	// closure
	var _self = this;

	this.loadConfig = function(config)
	{
		// required params
		_self.data = config.data;

		// internal lookups
		_self.ur = {}; // row index by rid
		_self.treeUR = {} // mapping rids to their new unique ids (if treeified)
		_self.oldRIMap = {};
		_self.uc = {}; // col index by cid

	    _self.dm = [] // data map

	    // configure any default rows
		_self.rows = [];
		if(config.hasOwnProperty("rows"))
		{
			_self.addRows(config.rows);
		}

		_self.cols = [];
		if(config.hasOwnProperty("columns"))
		{
			_self.addColumns(config.columns);
		}

		// optional params
		_self.columnSort = config.columnSort || _self.defaultColumnSort;
		_self.cellAggregator = config.cellAggregator || _self.defaultCellAggregator;
        _self.ignoreUndefinedRows = config.ignoreUndefinedRows || false;
	}

	this.addRows = function(rows)
	{
		_self.originalRows = rows;
		var flatRows = _self.processTrees(rows); // flatten any tree rows
		flatRows.forEach(function(row){
			_self.addRow(row);
		});
	}

	this.addColumns = function(cols)
	{
		_self.originalCols = cols;
		cols.forEach(function(col){
			_self.addColumn(col);
		})
	}

	/*
	 * Add more data to this set
	 */
    this.insertData = function(data)
    {
    	_self.data = _self.data.concat(data);
    	for(var i=0;i<data.length;i++)
    	{
    		_self.addCell(data[i]);
    	}
    }

    this.insertDataForChildrenOfRow = function(row,data)
    {
    	_self.data = _self.data.concat(data);
    	if(!row.hasOwnProperty("children")) return;
    	var acceptableRids = {};
    	for(var i=0;i<row.children.length;i++)
    	{
    		acceptableRids[row.children[i].rid]=1;
    	}
    	for(var i=0;i<data.length;i++)
    	{
    		_self.addCell(data[i],acceptableRids);
    	}
    }

    /*
     * Adds a data cell to the lookups
     */
    this.addCell = function(cell,acceptableRids)
    {
    	acceptableRids = acceptableRids || {};

    	// remap any rows that have been treeified
    	_self.mapDuplicateCells(cell,acceptableRids);

    	// ignore any cells with unacceptable rids
		if(!$.isEmptyObject(acceptableRids) && !acceptableRids.hasOwnProperty(cell.rid))
		{
			return;
		}


    	if(_self.excludeRow(cell)) return;
    	if(!cell.hasOwnProperty("registered")) cell.registered=true;
    	else return

    	// make sure row is unique
    	cell.ri = _self.addRow({rid:cell.rid});
    	// make sure this row is flagged as having data
    	_self.rows[cell.ri]._d=true;

        // 
        cell.ci = _self.addColumn(cell);

        // set master lookup with row/col indices
        _self.registerCell(cell);
    }

    /*
     * In tree view, this cell could appear in multiple DAG paths
     * we save unique row IDs for each path and map the cell's original ID to the new IDs
     */
    this.mapDuplicateCells = function(cell,acceptableRids)
    {
    	if(_self.treeUR.hasOwnProperty(cell.rid))
    	{
    		var newRids = _self.treeUR[cell.rid];
    		if(!$.isEmptyObject(acceptableRids))
    		{
	    		// remove unacceptableRids
	    		var goodRids = [];
	    		for(var i=0;i<newRids.length;i++)
	    		{
	    			if(acceptableRids.hasOwnProperty(newRids[i]))
	    			{
	    				goodRids.push(newRids[i]);
	    			}
	    		}
	    		newRids = goodRids;
    		}
    		if(newRids.length>0)
    		{
    			cell.rid=newRids[0];

    			// clone any extra
    			if(newRids.length>1)
    			{
    				for(var i=1;i<newRids.length;i++)
    				{
    					var cellClone = jQuery.extend(true, {}, cell);
    					cellClone.rid=newRids[i];
    					_self.insertData([cellClone]);
    				}
    			}
    		}
    	}
    }

    this.defaultCellAggregator = function(original,newCell)
    {
    	return newCell;
    }

    /*
     * registers cell with the master data lookups
     */
    this.registerCell = function(cell,noAggregate)
    {
        if(!_self.dm.hasOwnProperty(cell.ri))
        {
            _self.dm[cell.ri] = [];
        }
        if(!_self.dm[cell.ri].hasOwnProperty(cell.ci) || noAggregate)
        {
        	_self.dm[cell.ri][cell.ci] = cell;
        }
        else
        {
        	_self.dm[cell.ri][cell.ci] = _self.cellAggregator(_self.dm[cell.ri][cell.ci],cell);
        }
    }

    /*
     * Do we include this data cell?
     *
     */
    this.excludeRow = function(cell)
    {
    	return _self.ignoreUndefinedRows
    		&& !_self.ur.hasOwnProperty(cell.rid);
    }

    /*
     * Figure out how many rows and columns we need to draw
     * 	This function is going to trigger loading all the cells to the lookups
     */
     this.getInitialDataStatistics = function()
     {
         var stats = _self.getDataRowsAndCols(_self.data);
         _self.rows = stats.rows;
         _self.cols = stats.cols;
         _self.numRows = stats.numRows;
         _self.numCols = stats.numCols;
     }

     /*
      * Process nested rows by iterating through "children" property
      * 	duplicate row ids get assigned new unique rids
      * 	assigns reverse "parent" attribute to each child
      * Returns the flattened list of all rows (including children) in DAG order
      */
     this.processTrees = function(nodes,depth,parent)
     {
    	 depth = depth || 0;
    	 // do some stuff?
    	 var flatNodes = [];
    	 for(var i=0;i<nodes.length;i++)
    	 {
    		 var node = nodes[i];
    		 node.depth = depth;
    		 node.oid = node.oid || node.rid; // original id
    		 if(parent!=undefined)
    		 {
    			 node.parent=parent;

	    		 // make unique row ids for the parent/child combos

	 			 var newRid = node.oid+_self.genNewId();
	 			 node.rid = newRid;
	 			 // map old ids to new ones
	 			 if(!_self.treeUR.hasOwnProperty(node.oid))
	 			 {
	 				 _self.treeUR[node.oid] = [];
	 			 }
	 			 _self.treeUR[node.oid].push(newRid);
    		 }
    		 flatNodes[flatNodes.length] = node;

    		 // make sure proper node state is set ("open","close", or undefined (i.e. leaf))
    		 if(node.hasOwnProperty("children") && node.children.length>0)
    		 {
    			 var childNodes = _self.processTrees(node.children,depth+1,node);
    			 for(var j=0;j<childNodes.length;j++)
    			 {
    				 flatNodes[flatNodes.length] = childNodes[j];

    			 }
    			 node.oc= node.oc || "close";
    		 }
    		 else if(node.ex)
    		 {
    			 node.oc = "close";
    		 }
    		 else if(node.hasOwnProperty("oc"))
    		 {
    			 delete node.oc;
    		 }
    	 }
    	 return flatNodes;
     }

     /*
      * returns wether or not this node (or any of its parents) is closed
      */
     this.hasClosedParent = function(node)
     {
     	if(node.hasOwnProperty("parent"))
     	{
     		if(node.parent.hasOwnProperty("oc") && node.parent.oc=="close")
     		{
     			return true;
     		}
     		return _self.hasClosedParent(node.parent);
     	}
     	return false;
     }

     /*
      * generates a unique ID for the different tree paths
      */
     this.genNewId = function()
     {
    	 _self.idCount = _self.idCount || 0;
    	 _self.idCount += 1;
    	 return "###ID"+_self.idCount;
     }

     /*
      * adds all the data cells and assigns some metrics
      * 	e.g. numRows and numCols
      */
     this.getDataRowsAndCols = function(data)
     {
         var stats = {"rows":[],"cols":[],
             "numRows":0,"numCols":0};

         data.forEach(function(datum)
         {
             _self.addCell(datum);
         });
         stats.rows = _self.rows || [];
         stats.cols = _self.cols || [];
         stats.numRows = stats.rows.length;
         stats.numCols = stats.cols.length;
         return stats;
     }

     /*
      * Adds a data row
      */
     this.addRow = function(row)
     {
    	if(!SGUtil.isObject(row)) row = {rid:row};

     	if(this.ur==undefined)
 		{
 			this.ur = {};
 			this.rows = [];
 		}
     	if(!this.ur.hasOwnProperty(row.rid))
     	{
     		// we have a new row
     		this.ur[row.rid] = this.rows.length;
     		this.rows.push(row);
     	}
     	return this.ur[row.rid];
     }

     /*
      * adds a new row after the specified index
      * 	Doing so triggers an update of all the cell indices and lookups
      */
     this.addRowAfter = function(idx,newRow)
     {
    	 // need to add newRow as child of row at idx;
    	 var parentRow = _self.rows[idx];
    	 if(!parentRow.hasOwnProperty("children")) parentRow.children = [];
    	 parentRow.children.push(newRow);
    	 newRow.parent = parentRow;
    	 newRow.depth = parentRow.depth+1;
    	 if(!parentRow.hasOwnProperty("oc")) parentRow.oc="close";

    	 // figure out new index
    	 var newIdx = idx + _self.rows[idx].children.length;

    	 // insert newRow
    	 _self.rows.splice(newIdx,0,newRow);

    	 // set custom rid on newRow
    	 newRow.oid=newRow.rid;
    	 var newRid = newRow.oid+_self.genNewId();
    	 newRow.rid=newRid;
		 // map old ids to new ones
		 if(!_self.treeUR.hasOwnProperty(newRow.oid))
		 {
			 _self.treeUR[newRow.oid] = [];
		 }
		 _self.treeUR[newRow.oid].push(newRid);



    	 // need to update lookups
    	 for(var i=idx+1;i<_self.rows.length;i++)
    	 {
    		 var row = _self.rows[i];
    		 var oldRI = _self.ur[row.rid];
    		 if(_self.oldRIMap.hasOwnProperty(oldRI)) _self.oldRIMap[oldRI] += 1;
    		 else _self.oldRIMap[oldRI] = i;
    		 _self.ur[row.rid]=i;
    	 }
    	 // need to update cells
    	 for(var i=0;i<_self.data.length;i++)
    	 {
    		 var cell = _self.data[i];
    		 cell.ri = _self.ur[cell.rid];
    	 }

    	 // need to update dm
    	 _self.dm.splice(newIdx,0,[]);
     }

     /*
      * adds a column
      */
     this.addColumn = function(col)
     {
    	if(!SGUtil.isObject(col)) col = {cid:col};
     	if(this.uc==undefined)
 		{
 			this.uc = {};
 			this.cols = [];
 		}
     	if(!this.uc.hasOwnProperty(col.cid))
     	{
     		// we have a new column
     		this.uc[col.cid] = this.cols.length;
     		this.cols.push(col);
     	}
     	return this.uc[col.cid];
     }

     /*
      * returns cells within a certain row and column range
      */
     this.getCellsInRange = function(rowStart,rowEnd,colStart,colEnd)
     {
         var rowSelect = _self.dm.slice(rowStart,rowEnd+1);
         if(rowSelect==undefined) return [];
         var results = rowSelect.map(function(m){ return m.slice(colStart,colEnd+1); });
         var cells = [];
         for(var i=0;i<results.length;i++)
         {
             if(results[i]==undefined) continue;
             for(var j=0;j<results[i].length;j++)
             {
                 if(results[i][j]==undefined) continue;
                 cells.push(results[i][j]);
                 // reset cell indices
                 results[i][j].ri = i;
                 results[i][j].ci = j;
             }
         }
         return cells;
     }

     /*
      * omits rows that are closed
      */
     //TODO: Unit test this instead of above method
     this.getVisibleCellsInRange = function(rowStart,rowEnd,colStart,colEnd)
     {
    	 var omitIdxs = _self.getInvalidRowIndicesInRange(rowStart,rowEnd);
    	 var rowSelect = _self.dm.slice(rowStart,rowEnd+1);
         if(rowSelect==undefined) return [];
         var results = rowSelect.map(function(m){ return m.slice(colStart,colEnd+1); });
         var cells = [];
         var ri=-1;
         for(var i=0;i<results.length;i++)
         {
        	 // skip empty rows and non-visible rows
             if(results[i]==undefined || omitIdxs.hasOwnProperty(i)) continue;
             ri += 1;
             for(var j=0;j<results[i].length;j++)
             {
                 if(results[i][j]==undefined) continue;
                 cells.push(results[i][j]);
                 // reset cell indices
                 results[i][j].ri = ri;
                 results[i][j].ci = j;
             }
         }
         return cells;
     }

     // TODO: unit test
     this.getInvalidRowIndicesInRange = function(startRow,endRow)
     {
    	 var invalidIndices = {};
    	 var rows = _self.getRowsInRange(startRow,endRow);
    	 rows.forEach(function(row,i){
    		 if(!(row._d && !_self.hasClosedParent(row)))
    		 {
    			 invalidIndices[i] = i;
    		 }
    	 });
    	 return invalidIndices;
     }

     /*
      * returns rows within a certain range
      */
     this.getRowsInRange = function(startRow,endRow)
     {
         var rows = _self.rows.slice(startRow,endRow+1);
         if(rows==undefined) return [];
         return rows;
     }

     /*
      * omits rows without data and rows that are closed
      */
     this.getVisibleRowsInRange = function(startRow,endRow)
     {
    	 var returnRows = [];
    	 var rows = _self.getRowsInRange(startRow,endRow);
    	 // prune out any closed rows, and rows with no data
    	 rows.forEach(function(row,i){
    		 if(row._d && !_self.hasClosedParent(row))
    		 {
    			 returnRows[returnRows.length] = row;
    		 }
    	 });
    	return returnRows;
     }

     /*
      * returns columns within a certain range
      */
     this.getColumnsInRange = function(startCol,endCol)
     {
         var cols = _self.cols.slice(startCol,endCol+1);
         if(cols==undefined) return [];
         return cols;
     }

     /*
      * Is this data object empty?
      */
     this.empty = function()
     {
    	 return _self.data.length==0;
     }

     /*
      * sort columns and update all the cell index lookups
      * 	sort method can be overridden by configuring "columnSort"
      */
     this.sortColumns = function()
     {
     	_self.cols.sort(_self.columnSort);
     	var olduc = {};
     	// update the col index lookup
     	_self.cols.forEach(function(col,i){
     		olduc[i] = _self.uc[col.cid];
     		_self.uc[col.cid] = i;
     	});
     	// update all the cell col indices
     	// clear the data map
     	_self.dm = [];
     	_self.data.forEach(function(cell,i){
     		var newCi = _self.uc[cell.cid];
     		var oldCi = cell.ci;
     		cell.ci = newCi;

     		// update the dm
     		_self.registerCell(cell,true);
     	});
    }

    this.defaultColumnSort = function(a,b)
    {
    	if(a.cid>b.cid) return 1;
    	else if (a.cid<b.cid) return -1;
    	return 0;
    }

    this.loadConfig(config);
}

// Super Grid Cart - for holding row/column selections
//
function SGCart (name) 
{
    //    Actual cart data is stored in the window object, and may outlive 
    //    the SuperGrid instance that owns it.
    if (!window.SG_SELECTIONS) window.SG_SELECTIONS = {};
    if (!window.SG_SELECTIONS[name]) window.SG_SELECTIONS[name] = new Set();

    this.cart = window.SG_SELECTIONS[name];
    this.name = name;

    var _self = this;
    this.size = function () {
        return _self.cart.size;
    }
    this.items = function () {
        return Array.from(_self.cart);
    }
    this.clear = function () {
        _self.cart.clear();
    }
    this.add = function (item) {
	_self.cart.add(item);
    }
    this.remove = function (item) {
    	_self.cart.delete(item);
    }
    this.has = function (item) {
	return _self.cart.has(item);
    }
}

// Super Grid Class
function SuperGrid(config)
{
    var _self = this;

    /*
    * Parses all the available config options
    *
    * Required:
    *   target - id of which div to draw the matrix in
    *   data - data for the grid cells in format of [{"rid":rowid,"cid":colid,"val":dataValue},...]
    *
    * Optional:
    *   cellSize - size of grid cells in pixels (default is 20)
    *
    */
    this.loadConfig = function(config)
    {
	// required name
	_self.name = config.name;

	//
	_self.colCart = new SGCart(_self.name + " colCart");
	_self.rowCart = new SGCart(_self.name + " rowCart");

        // required configs
        _self.target = config.target;

        // optional row and column config
        var columns = config.columns || [];

        // can specify either data or a dataSource (dataSource takes precedence)
        _self.useDataSource = config.hasOwnProperty("dataSource");
        if(_self.useDataSource)
        {
        	_self.dataSource = new SGDataSource(config.dataSource);
        	_self.data = new SGData({data:[],rows:[],columns:columns});
        }
        else
        {
            var rows = config.rows || [];
        	_self.data = new SGData({data: config.data,rows:rows,columns:columns});
        }

        // optional configs
        _self.cellSize = config.cellSize || 20;
        _self.maxRows = config.maxRows || 10000;
        _self.maxColumns = config.maxColumns || 10000;
        _self.verticalColumnLabels = config.verticalColumnLabels || false;
        _self.rowsOnExpand = config.rowsOnExpand || 50;
        _self.columnsOnExpand = config.columnsOnExpand || 50;
        _self.data.ignoreUndefinedRows = config.ignoreUndefinedRows || false;

        _self.data.cellAggregator = config.cellAggregator || _self.data.defaultCellAggregator;
        _self.cellRenderer = config.cellRenderer || _self.defaultCellRenderer;
        _self.rowRenderer = config.rowRenderer || _self.defaultRowRenderer;
        _self.columnRenderer = config.columnRenderer || _self.defaultColumnRenderer;
        _self.data.columnSort = config.columnSort || this.data.defaultColumnSort;

        // legend config
        /*
         *  if you want a legend to appear, specify the click handler for it.
         *  	Legend can be styled with the following css classes
         *  	matrixLegendBox
         *  	matrixLegendText
         */
        _self.legendClickHandler = config.legendClickHandler || null;
	
	// selection cart config
	_self.selectionsTitle = config.selectionsTitle || null;
	_self.selectionsClickHandler = config.selectionsClickHandler || null;

        /*
         * filter config
         * 	if you want filter boxes to appear, specify either a row or column FilterHandler
         *
         */
        _self.filterSubmitHandler = config.filterSubmitHandler || null;

        // fired on completed render of initial matrix (and after row expansions/closings)
        _self.renderCompletedFunction = config.renderCompletedFunction || function(){};

        // DAG related config
        _self.openyClosey = config.openyClosey || false;
        _self.openRowListener = config.openRowListener || function(){};
        _self.closeRowListener = config.closeRowListener || function(){};
        _self.closeImageUrl = config.closeImageUrl || "";
        _self.openImageUrl = config.openImageUrl || "";
        _self.spinnerImageUrl = config.spinnerImageUrl || "/fewi/mgi/assets/images/loading.gif";
        _self.legendButtonIconUrl = config.legendButtonIconUrl || "";
        _self.selectionsButtonIconUrl = config.selectionsButtonIconUrl || "";
        _self.filterButtonIconUrl = config.filterButtonIconUrl || "";
        _self.filterUncheckedUrl = config.filterUncheckedUrl || "";
        _self.filterCheckedUrl = config.filterCheckedUrl || "";
        _self.filterDashedUrl = config.filterDashedUrl || _self.filterUncheckedUrl;
        
        // flag to indicate whether this is a tissue x gene grid (to allow for extra column-based optimization)
        _self.isGeneGrid = config.isGeneGrid || false;

        _self.openCloseStateKey = config.openCloseStateKey || null;

        // styling of elements (not sure how this API should work, so it's not configurable yet)
        _self.rowHeaderFontSize = 12;
        _self.cellFontSize = 10;
        _self.columnHeaderFontSize = 12;

        // internal state variables
        _self.svgHeightOffset=0;
        _self.svgWidthOffset=0;
        _self.rowHeaderWidth = 0;

        _self.currentRows = 0;
        _self.currentCols = 0;

        // initialize the open close state tracking map
        _self.initOpenCloseStateMap();

        // for testing/debugging
        _self.testMode = config.testMode || false;
    }

    /*
    * Initialize and draw the matrix
    */
    this.init = function()
    {
    	if(_self.useDataSource)
    	{
    		_self.setLoadingMsg();

    		// Add open/close state to request if we have one
			var args = [];
			for (stateKey in _self.openCloseState)
			{
				args[args.length] = "pathToOpen="+stateKey;
			}
			if (args.length > 0) _self.dataSource.url += "&" + args.join("&");

    		_self.dataSource.fireQuery({
    			initialCallback:_self.dataSourceHandler,
    			updateCallback:_self.dataSourceUpdateHandler,
    			doneCallback: _self.buildAll
    		});
    	}
    	else
    	{
    		_self.buildAll();
    	}
    }

    this.loadDataSource = function(config)
    {
    	_self.dataSource = new SGDataSource(config);
    	_self.data = new SGData({data:[],rows:[],columns:[]});
    	_self.init();
    }

    this.setLoadingMsg = function()
    {
    	$("#"+_self.target).html(_self.dataSource.MSG_LOADING);
    }

    this.setEmptyMsg = function()
    {
    	$("#"+_self.target).html(_self.dataSource.MSG_EMPTY);
    }

    // adds data incrementally
    this.dataSourceHandler = function(e,data)
    {
    	_self.data.addRows(data.rows);
    	_self.data.insertData(data.data);
    }
    this.dataSourceUpdateHandler = function(e,data)
    {
    	_self.data.insertData(data.data);
    }

    this.cancelDataSource = function()
    {
    	_self.dataSource.cancelAllRequests();
    }

    // init the row and column mappings on our data object
    this.initDataMaps = function()
    {
    	_self.data.getInitialDataStatistics();
    }

    /*
     * Define the order of operations in building the matrix
     */
    this.buildAll = function()
    {
    	_self.prepTargetContainer();

        _self.initScrollEvent();

        _self.initDataMaps();

        if(_self.data.empty())
        {
        	_self.setEmptyMsg();

            _self.renderCompletedFunction();
        }
        else
        {
        	_self.data.sortColumns();

	        _self.initSvg();

	        _self.buildInitialFixedColumnHeaders();

	        _self.buildInitialMatrix()
		        .done(function(){
		        	_self.buildInitialFixedRowHeaders();

		        	_self.refreshSvgHeight();

		        	_self.renderCompletedFunction();

				if (_self.selectionsClickHandler) {
				    _self.syncVisibleColumnsWithCart();
				    if (_self.colCart.size() > 0) {
				        selectionsPopupPanel.show();
				    }
				} else {
				    selectionsPopupPanel.hide();
				}

				if (_self.rowCart.size() > 0) {
				    _self.setFilteredRows();
				}
		        });
        }
    }

    /*
     * clean up existing target container
     */
    this.prepTargetContainer = function()
    {
    	// empty the target div
    	$("#"+_self.target).html("");
    }

    /*
    * Wire up the scroll event for fixed header handling
    */
    this.initScrollEvent = function()
    {
        $("#"+_self.target).scroll(function(e){
            if(_self.notScrolling())
            {
                //log("startScrolling'");
                _self.beginScrolling();
                var jqThis = $(this);
                var verticalPixels =  jqThis.scrollTop()-1;
                var horizontalPixels =  jqThis.scrollLeft()-1;

                SGUtil.translateElementRight(_self.rowGroup,horizontalPixels);
                SGUtil.translateElementDown(_self.colGroup,verticalPixels);
                _self.spacerGroup.attr("transform","translate("+horizontalPixels+","+verticalPixels+")");

                _self.stopScrolling();
            }
        });
    }

    this.notScrolling = function()
    {
        return !_self._scrolling;
    }
    this.beginScrolling= function()
    {
        _self._scrolling = true;
    }
    this.stopScrolling = function()
    {
        _self._scrolling = false;
    }

    /*
    * Initialize the svg viewport
    *  as well as the order of the different layer groups
    *
    */
    this.initSvg = function()
    {
        _self.height = _self.calculateSvgHeight();
        _self.width = _self.calculateSvgWidth();
        _self.margin = {top: 0, right: 0, bottom: 0, left: 0};
        _self.totalWidth = _self.width - _self.margin.left - _self.margin.right;
        _self.totalHeight = _self.height - _self.margin.top - _self.margin.bottom;
        _self.svg = _self.buildSvg(d3.select("#"+_self.target),_self.margin,_self.totalHeight,_self.totalWidth);

        // initialize z indices for each major layer
        // matrix cells
        _self.matrixGroup = _self.svg.append("g")
    		.attr("id","matrixGroup");

        _self.matrixGroupInner = _self.matrixGroup.append("g")
        	.attr("id","matrixGroupInner");

        // column headers
        _self.colGroup = _self.svg.append("g")
        	.attr("id","colGroup");

        _self.colGroupBackground = _self.colGroup.append("g")
    		.attr("id","colGroupBg");

        _self.colGroupInner = _self.colGroup.append("g")
        	.attr("id","colGroupInner");

        // row headers
        _self.rowGroup = _self.svg.append("g")
            .attr("id","rowGroup");

        _self.rowGroupBackground = _self.rowGroup.append("g")
    		.attr("id","rowGroupBg");

        _self.rowGroupInner = _self.rowGroup.append("g")
        	.attr("id","rowGroupInner");


        // make a spacer for the top left corner
        _self.spacerGroup = _self.svg.append("g")
        	.attr("id","spacerGroup");
        _self.spacerGroup.append("rect")
        	.attr("x",0)
        	.attr("y",0)
        	.attr("height",0)
        	.attr("width",0)
        	.attr("fill","white");

		// add legend to spacer if legend click handler is specified
        if(_self.legendClickHandler)
        {
        	_self.renderLegendButton();
        }

	if(_self.selectionsClickHandler) 
	{
        	_self.renderSelectionsButton();
	}


        if(_self.doFilters())
        {
        	_self.renderFilterButton();
        }


        _self.initFilters();
    }

    this.renderLegendButton = function()
    {
    	var buttonXOffset = 50;
       	var buttonYOffset = 30;

       	var legendGroup = _self.spacerGroup.append("g")
			.style("cursor","pointer")
			.on("click",_self.legendClickHandler);

		// underlay rect
		legendGroup.append("rect")
	    	.attr("x",buttonXOffset)
	    	.attr("y",buttonYOffset)
	    	.attr("height",25)
	    	.attr("width",86)
    		.style("fill","#aaa")
    		.style("stroke","black")
			.append("svg:title")
			.text("Click to open legend.");

		//overlay rect
		legendGroup.append("rect")
	    	.attr("x",buttonXOffset+1)
	    	.attr("y",buttonYOffset+1)
	    	.attr("height",21)
	    	.attr("width",82)
			.style("fill","#fafafa")
			.style("stroke","#fafafa")
			.append("svg:title").text("Click for information about the matrix color scheme.");

		// legend button text
		legendGroup.append("text")
		    .attr("x", buttonXOffset+8)
		    .attr("y", buttonYOffset+4)
		    .attr("dy", "1em")
		    .style("cursor","pointer")
		    .text("Legend")
		    .attr("class","matrixButtonText")
	    	.style("font-size","13px")
			.append("svg:title").text("Click for information about the matrix color scheme.");

		// legend button 'info' image
		legendGroup.append("image")
			.attr("x", buttonXOffset+64)
			.attr("y", buttonYOffset+4)
			.attr("width",_self.cellSize * (3/5))
			.attr("height",_self.cellSize * (3/5))
			.attr("xlink:href",_self.legendButtonIconUrl)
			.append("svg:title").text("Click for information about the matrix color scheme.");
    }

    this.renderSelectionsButton = function()
    {
    	var buttonXOffset = 140;
       	var buttonYOffset = 30;

       	var selectionsGroup = _self.spacerGroup.append("g")
			.style("cursor","pointer")
			.on("click",_self.selectionsClickHandler);

	// underlay rect
	selectionsGroup.append("rect")
	.attr("x",buttonXOffset)
	.attr("y",buttonYOffset)
	.attr("height",25)
	.attr("width",106)
	.style("fill","#aaa")
	.style("stroke","black")
		.append("svg:title")
		.text("");

	//overlay rect
	selectionsGroup.append("rect")
	.attr("x",buttonXOffset+1)
	.attr("y",buttonYOffset+1)
	.attr("height",21)
	.attr("width",102)
		.style("fill","#fafafa")
		.style("stroke","#fafafa")
		.append("svg:title").text("");

	// legend button text
	selectionsGroup.append("text")
	    .attr("x", buttonXOffset+8)
	    .attr("y", buttonYOffset+4)
	    .attr("dy", "1em")
	    .style("cursor","pointer")
	    .text(_self.selectionsTitle)
	    .attr("class","matrixButtonText")
	    .attr("id","selectionsTextID")
	    .style("font-size","13px")
	    .append("svg:title").text("");

	// selection button 'info' image
	selectionsGroup.append("image")
		.attr("x", buttonXOffset+85)
		.attr("y", buttonYOffset+4)
		.attr("width",_self.cellSize * (3/5))
		.attr("height",_self.cellSize * (3/5))
		.attr("xlink:href",_self.selectionsButtonIconUrl)
		.attr("id","selectionsIconID")
		.append("svg:title").text("");

	_self.selectionsButtonGroup = selectionsGroup;
    }

    this.renderFilterButton = function()
    {
    	var buttonXOffset = 50;
       	var buttonYOffset = 66;
    	_self.filterButtonGroup = _self.spacerGroup.append("g")
			.style("cursor","pointer")
			.on("click",_self.filterClickHandler);

		// underlay rect
    	_self.filterButtonGroup.append("rect")
    		.attr("x",buttonXOffset)
    		.attr("y",buttonYOffset)
 	    	.attr("height",25)
	    	.attr("width",86)
    		.style("padding-bottom","20px")
    		.style("fill","#aaa")
    		.style("stroke","black");

		// overlay rect
    	_self.filterButtonGroup.append("rect")
			.attr("x",buttonXOffset +1)
			.attr("y",buttonYOffset +1)
	    	.attr("height",21)
	    	.attr("width",82)
			.style("fill","#fafafa")
			.style("stroke","#fafafa")
    		.append("svg:title").text("Click to apply row/column filters");

    	// filter button text
    	_self.filterButtonGroup.append("text")
		    .attr("x", buttonXOffset+8)
		    .attr("y", buttonYOffset+4)
		    .attr("dy", "1em")
		    .style("cursor","pointer")
		    .style("opacity","0.5")
		    .text("Filter")
		    .attr("class","matrixButtonText")
		    .attr("id","filterTextID")
	    	.style("font-size","13px")
    		.append("svg:title").text("Click to apply row/column filters");

    	// add graffic
    	if(_self.filterButtonIconUrl)
    	{
    		_self.filterButtonGroup.append("image")
    			.attr("x", buttonXOffset+64)
    			.attr("y", buttonYOffset+6)
    			.attr("width",_self.cellSize * (1/2))
    			.attr("height",_self.cellSize * (1/2))
    			.attr("xlink:href",_self.filterButtonIconUrl)
			.attr("id","filterIconID")
			.style("opacity","0.5")
	    		.append("svg:title").text("Click to apply row/column filters");
    	}
    	else
    	{
    		_self.filterButtonGroup.append("text")
    		.attr("x",4)
    		.attr("y",_self.cellSize/1.5)
    		.text("F");
    	}

    }

    this.filterClickHandler = function()
    {
	var filteredRows = _self.getFilteredRows();
	var filteredColumns = _self.getFilteredColumns();
    	// only fire the handler if there are actually filtered selected
    	if((filteredRows && filteredRows.length) || (filteredColumns && filteredColumns.length))
    	{
    		_self.filterSubmitHandler(filteredRows,filteredColumns);
		_self.colCart.clear();
		_self.rowCart.clear();
    	}
    }

    this.getFilteredRows = function()
    {
	//return _self.data.rows.filter(r=>r.checked && !(r.parent && r.parent.checked)).map(r=>r.rowId);
	return _self.rowCart.items();
    }

    this.setFilteredRows = function () {
	_self.rowCart.items().forEach(rowId => {
	    document.querySelectorAll('#rowGroupInner g[emapid="' + rowId + '"]').forEach(g => {
	        var row = g.__data__;
		_self.rowFilterCheck(row, 'check', null, true);
	    });
	})

	_self.updateFilterHighlights();
	_self.updateButtons();
    }

    this.getFilteredColumns = function()
    {
	return _self.colCart.items();
    }

    this.calculateSvgHeight = function()
    {
        return (_self.getInitialRowNum()*_self.cellSize) + 0;
    }
    this.calculateSvgWidth = function()
    {
    	var width = (_self.getInitialColumnNum()*_self.cellSize) + _self.rowHeaderWidth;
    	if(_self.verticalColumnLabels) width += _self.cellSize*3;
        width += 50 // just because
        return width;
    }

    this.buildSvg = function(d3Target,margin,height,width)
    {
        var svg = d3Target.append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
        .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

        return svg;
    }


    /*
    * Functions for drawing the grid components
    */
    this.buildInitialMatrix = function()
    {
    	var _deferred = $.Deferred();

        var g = _self.matrixGroupInner;
        _self.matrixCellGroup = g.append("g").attr("class","matrixCell");

        _self.matrixX = _self.calculateMatrixX();
        _self.matrixY = _self.calculateMatrixY();

        var cells = _self.data.getVisibleCellsInRange(0,_self.maxRows-1,0,_self.maxColumns-1);
        var rows = Math.min(_self.maxRows,_self.data.numRows);
        var cols = Math.min(_self.maxColumns,_self.data.numCols);
        _self.drawMatrix(g,_self.cellSize,cells,0,rows,0,cols,_self.matrixX,_self.matrixY)
        	.done(function(){
        		_deferred.resolve();
        	});
        return _deferred.promise();
    }

    this.calculateMatrixX = function()
    {
        return 0;
    }
    this.calculateMatrixY = function()
    {
        return 0;
    }

    this.drawMatrix = function(d3Target,cellSize,cells,startRows,endRows,startCols,endCols,startX,startY)
    {
    	var _deferred = $.Deferred();

        // Assign the current row/column settings
        _self.currentRows = endRows;
        _self.currentCols = endCols;

        _self.drawMatrixLines(d3Target,cellSize,startRows,endRows,startCols,endCols,startX,startY);

        _self.updateMatrixCells(d3Target,cellSize,cells,startX,startY)
        	.done(function(){
        		_deferred.resolve();
        	});

        return _deferred.promise();
    }

    this.drawMatrixLines = function(d3Target,cellSize,startRows,endRows,startCols,endCols,startX,startY)
    {
    	//
    	_self.drawMatrixColLines(d3Target,cellSize,startRows,endRows,startCols,endCols,startX,startY);
    }

    this.drawMatrixRowLine = function(d3Target,cellSize,row,index,startX,startY)
    {
         // draw a row
         d3Target.append("line")
             .attr("x1",startX).attr("y1",startY)
             .attr("x2",startX+(cellSize*_self.data.numCols)).attr("y2",startY)
             .attr("class","row row"+index)
             .datum(row)
             .attr("transform",_self.rowTransform);
    }

    this.drawMatrixColLines = function(d3Target,cellSize,startRows,endRows,startCols,endCols,startX,startY)
    {
        for(var j=startCols;j<endCols;j++)
        {
            // draw a column
            d3Target.append("line")
                .attr("x1",startX+((j+1)*cellSize)).attr("y1",startY+(startRows*cellSize))
                .attr("x2",startX+((j+1)*cellSize)).attr("y2",startY+(endRows*cellSize))
                .attr("class","col col"+j);
        }
    }

    /*
    * each cell must have an ri, ci, and val
    *   for row index, column index, and value
    */
    this.updateMatrixCells = function(d3Target,cellSize,cells,startX,startY)
    {
    	var _deferred = $.Deferred();

    	// set some default values
    	cells.forEach(function(cell,i){
    		cell.rowOffset = 0;
    	});

    	var dJoin = _self.matrixCellGroup.selectAll("g").data(cells,function(d){ return d.ri+"_"+d.ci});
    	var gEnter = dJoin.enter().append("g");


    	_self.executeCellRenderers(cellSize,gEnter[0])
    		.done(function(){

    			// handle updates
    	        dJoin.attr("class",function(d,i){ return "cell row"+d.ri+" col"+d.ci; })
    	    		.attr("transform",_self.cellTransform);

    	        // handled removed data
    	    	dJoin.exit().remove();

    	    	_deferred.resolve();
    		});

    	return _deferred.promise();
    }

    /*
     * Executes all the cell renderers,
     *  may occasionally relenquish control to browser for
     *  large numbers of cells
     */
    _self.executeCellRenderers = function(cellSize,d3Groups)
    {
    	var _deferred = $.Deferred();
    	var i=0;
    	(function () {
    	    for (; i < d3Groups.length; i++) {
    	        g = d3Groups[i];
    	        if(g)
        		{
        			g = d3.select(g);
        			_self.cellRenderer(g,cellSize,g.datum());
        		}

    	        // Every 8,000 iterations, take a break
    	        if ( i > 0 && i % 8000 == 0) {
    	            // Manually increment `i` because we break
    	            i++;
    	            // Set a timer for the next iteration
    	            window.setTimeout(arguments.callee);
    	            break;
    	        }
    	    }

    	    // only resolve when we have processed all groups
        	if (i >= d3Groups.length)
        	{
        		_deferred.resolve();
        	}
    	})();

    	return _deferred.promise();
    }

    /*
     * default renderer for a cell that can be overridden
     * 	only displays the text inside of cell.val
     *
     * 	Params:
     * 		d3Target - a d3 object (<g> tag) that the cell will be appended to
     * 		cellSize - currently configured cellSize
     * 		cell - the cell data object
     * 		x - x coordinate in the SVG viewport
     * 		y - y coordinate in the SVG viewport
     */
    this.defaultCellRenderer = function(d3Target,cellSize,cell)
    {
    	var textPadding = 4;
    	// draw a data cell
        return d3Target.append("text")
            .attr("x",textPadding)
            .attr("y",cellSize-textPadding)
            .text(cell.val)
            .style("font-size",_self.cellFontSize+"px");
    }

    this.drawMatrixRows = function(d3Target,cellSize,startRows,endRows,startX,startY)
    {
    	//_self.drawMatrixRowLines(d3Target,cellSize,startRows,endRows+1,0,_self.currentCols,startX,startY);
    	var cells = _self.data.getCellsInRange(startRows,endRows,0,_self.currentCols);
    	_self.updateMatrixCells(d3Target,cellSize,cells,startX,startY);
    }

    this.getInitialRowNum = function()
    {
    	return _self.data.numRows;
    }
    this.getInitialColumnNum = function()
    {
    	return _self.data.numCols;
    }


    /*
    * Draw the fixed row headers (as 1 column on far left) on matrix
    */
    this.buildInitialFixedRowHeaders = function()
    {
    	var g = _self.rowGroupInner;
    	_self.matrixLineGroup = _self.matrixGroupInner.append("g").attr("class","matrixRow");

        var rows = _self.data.getVisibleRowsInRange(0,_self.getInitialRowNum()-1);

        _self.drawFixedRowHeaders(g,rows,_self.cellSize,_self.rowHeaderWidth,0,0);
    }
    this.drawFixedRowHeaders = function(d3Target,rows,cellSize,colWidth,startX,startY,rowIdx)
    {
        var color = "white";
        var totalHeight = cellSize*rows.length;

        _self.rowGroupBackground.append("rect")
            .attr("width",colWidth)
            .attr("height",totalHeight)
            .attr("x",startX)
            .attr("y",startY)
            .style("fill",color)
            .attr("class","rowSpacer");

        //draw the separator line
        d3Target.append("line")
        	.attr("x1",startX + colWidth)
        	.attr("x2",startX + colWidth)
        	.attr("y1",startY)
        	.attr("y2",startY + (rows.length*cellSize))
        	.attr("class","rowSeparator");

        //draw initial row line
        d3Target.append("line")
        	.attr("x1",startX)
            .attr("x2",startX+colWidth)
            .attr("y1",startY)
            .attr("y2",startY)
            .attr("class","rowLine");

        _self.updateRows(d3Target,cellSize,rows,startX,startY);
    }


    this.updateRows = function(d3Target,cellSize,rows,startX,startY)
    {
        // default some values on the row objects
        rows.forEach(function(row,i){
        	row.rowOffset = i;
        });

    	var dJoin = d3Target.selectAll("g").data(rows,function(d){
    		return d.rid;
    	});

        var gEnter = dJoin.enter().append("g");

        _self.rowRenderer(gEnter,_self.cellSize,startX,startY);


    	gEnter[0].forEach(function(g,i){
    		if(g)
    		{
    			g = d3.select(g);
    			_self.addOpenCloseListeners(g,cellSize,g.datum());
    			_self.addRowFilterListener(g,cellSize,g.datum());
    		}
    	});

        var headerLine = gEnter.append("line")
        	.attr("x1",startX).attr("x2",startX)
        	.attr("y1",cellSize).attr("y2",cellSize)
        	.attr("class","rowLine");

        // draw the matrix row lines
        var matrixLineJoin = _self.matrixLineGroup.selectAll("line").data(rows, function(d){ return d.rid; });
        var matrixLine = matrixLineJoin.enter()
         	.append("line")
            .attr("x1",startX).attr("y1",cellSize)
            .attr("x2",startX+(_self.cellSize*_self.data.numCols)).attr("y2",cellSize)

        // handle the updated data points
        dJoin.attr("transform",_self.rowTransform);
        matrixLineJoin.attr("transform",_self.rowTransform)
        	.attr("class",function(d,i){ return "row row"+i;});

        // handled removed data
        var rowsToRemove = dJoin.exit();
        rowsToRemove.remove();
        matrixLineJoin.exit().remove();

        _self.refreshRowHeaderWidth();
    }

    this.addOpenCloseListeners = function(d3Target,rowHeight,row)
    {
        if(_self.openyClosey)
        {
	    	if(row.oc=="close" || row.oc=="open" || row.ex)
	    	{
	    		var rowRendered = d3.select(d3Target[0][0].childNodes[0]);

	    		var x = d3Target[0][0].getBBox().x - 14;

	    		var img = d3Target.append("image")
	        		.attr("x",x)
	        		.attr("y",rowHeight-14)
	        		.attr("height",10)
	        		.attr("width",10)
	        		.attr("class","ocImage");

	    		var mouseArea = d3Target.append("rect")
	    			.attr("x",0)
	    			.attr("y",0)
	    			.attr("height",rowHeight)
	    			.attr("width",0)
	    			.style("fill","transparent")
	    			.attr("class","rowWidth")
	        		.datum(row);

	    		if(row.oc=="close" || row.ex)
	    		{
	    			mouseArea.style("cursor","pointer");
	    			mouseArea.on("click",_self.openRowHandler);
	    			img.attr("xlink:href",_self.closeImageUrl);
	    		}
	    		else
	    		{
	    			mouseArea.style("cursor","pointer");
	    			mouseArea.on("click",_self.closeRowHandler);
	    			img.attr("xlink:href",_self.openImageUrl);
	    			_self.saveOpenState(row);
	    		}
	    	}
        }
    }

    this.openRowHandler = function(row){
    	if(_self.locked) return;
    	if(row.ex)
    	{
    		if(_self.dataSource==undefined)
    		{
    			alert("dataSource not defined. cannot expand row");
    			console.log("dataSource not defined. cannot expand row");
    			return;
    		}

        	_self.locked=true;
	    	var img = d3.select(this.parentNode).select(".ocImage");
	    	
	    	// Initially change the icon to be a spinner, until we receive the new data, so
	    	// the user will know we're working on it.
	    	img.attr("xlink:href",_self.spinnerImageUrl);
	    	d3.select(this).on("click",_self.closeRowHandler);

	    	// If this is a tissue x gene grid, we can look up the marker symbols from the column
	    	// headers and pass them in as an extra optimization.
	    	var markerSymbols = '';
	    	if (_self.isGeneGrid) {
	    		var columnHeadings = $('.colText text');

	    		for (var i in columnHeadings) {
	    			var symbol = columnHeadings[i].innerHTML;
	    			if (symbol != undefined) {
	    				if (markerSymbols.length > 0) {
	    					markerSymbols = markerSymbols + ',' + symbol;
	    				} else {
	    					markerSymbols = symbol;
	    				}
	    			}
	    		}
	    	}
	    	
	    	// Query datasource to populate rows to be opened
    		var ri = _self.data.ur[row.rid];
    		_self.updateRowsFound=0;
                var numColsBefore = _self.data.cols.length
    		_self.dataSource.fireQuery({
    			args: {mapChildrenOf: row.oid || row.rid,
    				markerSymbolFilter: markerSymbols},
    			initialCallback: function(e,data){
    				console.log(data);
    				_self.updateRowsFound = data.rows.length;
    				_self.data.oldRIMap={};
    				for(var i=0;i<data.rows.length;i++)
    				{
    					_self.data.addRowAfter(ri,data.rows[i]);
    				}
    				_self.data.insertDataForChildrenOfRow(row,data.data);
    			},
    			updateCallback: function(e,data){
    				_self.data.insertDataForChildrenOfRow(row,data.data);
    			},
    			doneCallback: function(){
    				_self.svgHeightOffset -= _self.cellSize*_self.updateRowsFound;
    				row.ex=false;
    				row.oc="close";

    		    	_self.expandRow(row);
                        if (_self.data.cols.length !== numColsBefore) {
                            /*
                             * Handle edge case where opening a row returns additional columns.
                             * Example: query for "expressed in embryo and nowhere else".
                             * The tissue by stage matrix shows one row for embryo with the last stage == 26.
                             * Open that row. The new data includes negative results for stages 
                             * 27 and 28. Incrementally adding columns to the Supergrid would be really hard.
                             * Instead, here we'll just rebuild the whole thing from scratch.
                             */
                            _self.buildAll();
                        } else {
                            /* The normal case. No change in the number of columns. Just refresh. */
                            _self.refreshMatrixData();
                        }
    		    	_self.locked=false;

    		    	// Change the icon back to be the appropriate arrow.
    				img.attr("xlink:href",_self.openImageUrl);
    			}
    		});
    	}
    	else
    	{
        	_self.locked=true;
	    	var img = d3.select(this.parentNode).select(".ocImage");
	    	img.attr("xlink:href",_self.openImageUrl);
	    	d3.select(this).on("click",_self.closeRowHandler);
	    	_self.expandRow(row);
	    	_self.refreshMatrixData();

	    	_self.locked=false;
    	}
    	// update openCloseState
    	_self.saveOpenState(row);
    }

    this.closeRowHandler = function(row){
    	var img = d3.select(this.parentNode).select(".ocImage");
    	img.attr("xlink:href",_self.closeImageUrl);
    	d3.select(this).on("click",_self.openRowHandler);
    	_self.closeRow(row);
    	_self.refreshMatrixData();
    }

    this.closeRow = function(row)
    {
    	if(row.hasOwnProperty("children") && row.children.length>0)
    	{
    		// setting the parent to closed is enough to hide all the children
    		row.oc="close";
        	_self.saveCloseState(row);
    	}
    }

    // open/close state management
    this.initOpenCloseStateMap = function()
    {
    	_self.openCloseState = {};
    	if (_self.openCloseStateKey)
    	{
	    	if (window.SG_OPEN_CLOSE_STATE == undefined)
	    	{
	    		window.SG_OPEN_CLOSE_STATE = {};
	    	}

	    	if (!window.SG_OPEN_CLOSE_STATE.hasOwnProperty(_self.openCloseStateKey))
	    	{
	    		window.SG_OPEN_CLOSE_STATE[_self.openCloseStateKey] = {};
	    	}

	    	// restore state if we've seen this key before
	    	_self.openCloseState = window.SG_OPEN_CLOSE_STATE[_self.openCloseStateKey];
    	}
    }

    this.saveOpenState = function(row)
    {
    	var stateString = _self.getOpenStateString(row);
    	_self.openCloseState[stateString] = 1
    }
    this.saveCloseState = function(row)
    {
    	var stateString = _self.getOpenStateString(row);
    	delete _self.openCloseState[stateString];
    }
    /*
     * Returns a string representing this row's open state
     */
    this.getOpenStateString = function(row)
    {
    	var stateString = "";
    	if (row.hasOwnProperty("parent"))
    	{
    		stateString += _self.getOpenStateString(row.parent) + "|";
    	}
    	var id = row.oid || row.rid;
    	return stateString += id;
    }

    this.expandRow = function(row)
    {
    	row.oc="open";
    	if(row.hasOwnProperty("children")  && row.children.length>0)
    	{
    		for(var i=0;i<row.children.length;i++)
    		{
    			var child = row.children[i];
        		if(child.hasOwnProperty("oc") && child.oc=="open")
        		{
        			_self.expandRow(child);
        		}
    		}
    	}
    }

    this.defaultRowRenderer = function(d3Target,cellHeight,gridPadX,gridPadY)
    {
    	var labelPaddingLeft = 14;
    	var labelPaddingBottom = 4;
    	return d3Target.append("text")
            .attr("x",gridPadX + labelPaddingLeft)
            .attr("y",cellHeight - labelPaddingBottom)
            .text(function(d){ return d.rid; })
            .style("font-size",_self.rowHeaderFontSize+"px")
            .style("font-weight","bold");
    }

    this.defaultColumnRenderer = function(d3Target,cellSize)
	{
    	var labelPaddingLeft = 4;
    	var labelPaddingBottom = 4;
        return  d3Target.append("text")
        	.attr("x", 0)
        	.attr("y",cellSize-labelPaddingBottom)
        	.text(function(d){ return d.cid;})
        	.style("font-size",_self.columnHeaderFontSize+"px")
        	.style("font-weight","bold");
    }

    /*
    * Draw the fixed columns (as 1 row on top) on matrix
    */
    this.buildInitialFixedColumnHeaders= function()
    {
        var g = _self.colGroupInner;

        var cols = _self.data.getColumnsInRange(0,_self.getInitialColumnNum()-1);
        _self.drawFixedColumnHeaders(g,cols,_self.cellSize,0,0,0);
    }

    this.drawFixedColumnHeaders = function(d3Target,cols,cellSize,rowHeight,startX,startY,colIdx)
    {
    	// default some values on the row objects
        cols.forEach(function(col,i){
        	col.colOffset = i;
        });
    	colIdx = colIdx || 0;
        var color = "white";
        var totalWidth = cellSize*cols.length;
        _self.colGroupBackground.append("rect")
            .attr("width",totalWidth+1)
            .attr("height",rowHeight)
            .attr("x",startX)
            .attr("y",startY)
            .style("fill",color)
            .attr("class","colSpacer");

        //draw the separator line
        d3Target.append("line")
        	.attr("x1",startX )
        	.attr("x2",startX + (cols.length*cellSize))
        	.attr("y1",startY + rowHeight)
        	.attr("y2",startY + rowHeight)
        	.attr("class","colSeparator");


        //draw initial column line
        d3Target.append("line")
        	.attr("x1",startX)
            .attr("x2",startX)
            .attr("y1",startY)
            .attr("y2",startY+rowHeight)
            .attr("class","colLine");


        // draw the col labels
        var labelPaddingBottom = 4;
        var labelPaddingLeft = 6;
        var ty = rowHeight - labelPaddingBottom;

        var dJoin = d3Target.selectAll("g").data(cols);

        var gEnter = dJoin.enter().append("g")
        	.attr("transform",_self.colTransform);

        var textLabelGroup = gEnter.append("g")
        	.attr("class",function(d,i){ return "colText col"+i; });

        _self.columnRenderer(textLabelGroup,_self.cellSize);

        gEnter[0].forEach(function(g,i){
    		if(g)
    		{
    			g = d3.select(g);
    			_self.addColFilterListener(g,cellSize,g.datum());
    		}
    	});

        if(_self.verticalColumnLabels)
        {
            textLabelGroup.attr("transform",function(d,i){
            	var tx = startX+(labelPaddingLeft);
            	return "translate("+(cellSize/1.7)+","+((-1)*cellSize/5)+") rotate(310,"+(tx)+","+(ty)+")";
            });
        }
        var line = gEnter.append("line")
        	.attr("x1",cellSize)
        	.attr("x2",cellSize)
        	.attr("y1",startY)
        	.attr("y2",startY+rowHeight)
        	.attr("class", function(d,i){
        		return "colLine col"+(i+colIdx);
        	});
        if(_self.verticalColumnLabels)
        {
            line.attr("transform",function(d,i){
            	var x = startX+(cellSize);
            	return "translate(0,0) rotate(40,"+x+","+(startY+rowHeight)+")";
            });
        }
        _self.refreshColumnHeaderHeight();
    }


    this.cellTransform = function(d)
    {
		var newY = (d.ri)*_self.cellSize;
		var newX = (d.ci)*_self.cellSize;
		return "translate("+newX+","+(newY)+")";
    }

    this.rowTransform = function(d,i)
    {
    	var index = d.rowOffset || i;
		var newY = (index)*_self.cellSize;
		return "translate(0,"+(newY)+")";
    }

    this.colTransform = function(d,i)
    {
    	var index = d.colOffset || i;
		var newX = (index)*_self.cellSize;
		return "translate("+newX+")";
    }

    /**
     * Methods for enabling dynamic resizing of various sections of the matrix
     */

    /*
     * redraw the matrix with updated data
     */
    this.refreshMatrixData = function()
    {
    	var rows = _self.data.getVisibleRowsInRange(0,_self.maxRows);
    	var cells = _self.data.getVisibleCellsInRange(0,_self.maxRows,0,_self.maxColumns);
    	_self.updateRows(_self.rowGroupInner,_self.cellSize,rows,0,0);
    	_self.updateMatrixCells(_self.matrixGroupInner,_self.cellSize,cells,0,0);
    	_self.refreshSvgHeight();

    	_self.updateFilterTree();
    	_self.updateFilterHighlights();
    	_self.renderCompletedFunction();
    }

    this.setSvgHeightOffset = function(offset)
    {
    	var oldOffset = _self.svgHeightOffset || 0;
    	var newOffset = offset - oldOffset;
    	_self.svgHeightOffset = offset;
    	if(newOffset == 0) return;

    	var svgNode = d3.select(_self.svg[0][0].parentNode);
    	var curHeight = parseFloat(svgNode.attr("height"));
    	var newHeight = curHeight + newOffset;
    	_self.setSvgHeight(newHeight);
    }
    this.setSvgHeight = function(height)
    {
    	if(height <= 0)
    	{
    		console.log("invalid svg height: "+height);
    		return;
    	}
    	var svgNode = d3.select(_self.svg[0][0].parentNode);
    	svgNode.attr("height",height);

    	// update row line separator
    	var rs = _self.svg.select(".rowSeparator");
    	rs.attr("y2",height);
    	_self.svg.select(".rowSpacer").attr("height",height);
    	_self.matrixGroupInner.selectAll("line.col").attr("y2",height);
    }

    this.setSvgWidthOffset = function(offset)
    {
    	var oldOffset = _self.svgWidthOffset || 0;
    	var newOffset = offset - oldOffset;
    	_self.svgWidthOffset = offset;
    	if(newOffset == 0) return;

    	var svgNode = d3.select(_self.svg[0][0].parentNode);
    	var curWidth = parseFloat(svgNode.attr("width"));
    	var newWidth = curWidth + newOffset;
    	_self.setSvgWidth(newWidth);
    }

    this.setSvgWidth = function(width)
    {
    	if(width <= 0)
    	{
    		console.log("invalid svg width: "+width);
    		return;
    	}
    	var svgNode = d3.select(_self.svg[0][0].parentNode);
    	svgNode.attr("width",width);

    	_self.colGroupInner.select(".colSpacer").attr("width",width+1);
    }

    this.refreshSvgHeight = function()
    {
    	var rows = _self.data.getVisibleRowsInRange(0,_self.maxRows);
    	var numRows = rows.length;
    	var newHeight = (numRows*_self.cellSize) + _self.columnHeaderHeight;
    	_self.setSvgHeight(newHeight);
    }


    this.refreshRowHeaderWidth = function()
    {
    	var minRowWidth = 150;

    	var headers = _self.svg.selectAll("#rowGroupInner > g")[0];
    	if(headers && headers.length>0)
    	{
    		var maxRowLength=0;
	    	var padding = 20;
	    	if(_self.doFilters())
	    	{
	    		padding = _self.cellSize;
	    	}
	    	_self.adjustRowHeaderWidth(0);
		    	for(var i=0; i<headers.length;i++)
		    	{
		    		var rowLength = headers[i].getBBox().width;
		            if(rowLength>maxRowLength) maxRowLength = rowLength;
		    	}
		    var rowWidth = maxRowLength + padding;

	    	// check against anything in the spacer area (like the legend)
	    	var spacerWidth = _self.spacerGroup[0][0].getBBox().width;
	    	rowWidth = Math.max(rowWidth,spacerWidth);
	    	rowWidth = Math.max(rowWidth,minRowWidth);

	    	_self.adjustRowHeaderWidth(rowWidth);
    	}
    }

    /*
     * adjusts the matrix for when row header width needs to change
     */
    this.adjustRowHeaderWidth = function(newWidth)
    {
		_self.rowHeaderWidth = newWidth;
		SGUtil.translateElementRight(_self.colGroup,newWidth);
		SGUtil.translateElementRight(_self.matrixGroup,newWidth);

		_self.rowGroup.selectAll(".rowSpacer,.rowWidth").attr("width",newWidth);
		_self.rowGroup.selectAll(".rowSeparator").attr("x1",newWidth).attr("x2",newWidth);
		// now do all the other row lines
		_self.rowGroup.selectAll(".rowLine").attr("x2",newWidth);

		_self.setSvgWidthOffset(newWidth);

		// adjust grid spacer
		_self.spacerGroup.select("rect").attr("width",newWidth);

		// adjust filter buttons
		if(_self.doFilters())
		{
			//SGUtil.translateElementRight(_self.filterButtonGroup,newWidth - _self.cellSize);

			_self.rowGroup.selectAll(".rowFilter").attr("x",newWidth-_self.cellSize);
			_self.rowGroup.selectAll(".rowFilterImage").attr("x",newWidth-(_self.cellSize * (7/8)));
		}
    }

    this.refreshColumnHeaderHeight = function()
    {
    	var minColumnHeaderHeight = 105;
    	var header = _self.colGroupInner[0][0];

		var maxColumnHeight=header.getBBox().height;
    	var padding = 0;
    	if(_self.doFilters())
    	{
    		padding = _self.cellSize;
    	}
    	maxColumnHeight += padding;
    	var spacerHeight = _self.spacerGroup[0][0].getBBox().height;

    	// determine which height to use
    	maxColumnHeight = Math.max(maxColumnHeight,spacerHeight);
    	maxColumnHeight = Math.max(maxColumnHeight,minColumnHeaderHeight);
    	_self.adjustColumnHeaderHeight(0);
    	_self.adjustColumnHeaderHeight(maxColumnHeight);
    }

    /*
     * adjusts the matrix for when column header height needs to change
     */
    this.adjustColumnHeaderHeight = function(newHeight)
    {
		_self.columnHeaderHeight = newHeight;
		SGUtil.translateElementDown(_self.rowGroup,newHeight);
		SGUtil.translateElementDown(_self.matrixGroup,newHeight);

		var verticalHeight = newHeight;
		if(_self.doFilters() && verticalHeight > 0)
		{
			verticalHeight -= _self.cellSize;
		}

		_self.colGroup.selectAll(".colSpacer").attr("height",newHeight);
		_self.colGroup.selectAll(".colSeparator").attr("y1",verticalHeight).attr("y2",verticalHeight);
		// now do all the other col lines
		_self.colGroup.selectAll(".colLine").attr("y2",verticalHeight);
		if(_self.verticalColumnLabels)
		{
			_self.colGroup.selectAll(".colText text").attr("y",verticalHeight);
			_self.colGroup.selectAll(".colText rect").attr("y",verticalHeight - 10);
			_self.colGroup.selectAll(".colText rect").attr("width", verticalHeight + 40)

			// adjust the rotate origin
			_self.colGroup.selectAll(".colLine").each(function(d,i){
				var el = d3.select(this);
				var trans = el.attr("transform");
				var newTrans = SGUtil.changeRotateOriginY(trans,verticalHeight);
				el.attr("transform",newTrans);
			});

			_self.colGroup.selectAll(".colText").each(function(d,i){
				var el = d3.select(this);
				var trans = el.attr("transform");
				var newTrans = SGUtil.changeRotateOriginY(trans,verticalHeight);
				el.attr("transform",newTrans);
			});
		}

		// adjust grid spacer
		_self.spacerGroup.select("rect").attr("height",newHeight);

		// adjust filter button
		if(_self.doFilters())
		{
			//SGUtil.translateElementDown(_self.filterButtonGroup,newHeight - _self.cellSize);

			_self.colGroup.selectAll(".colFilter").attr("y",newHeight-_self.cellSize);
			_self.colGroup.selectAll(".colFilterImage").attr("y",newHeight-(_self.cellSize * (7/8)));
		}
    }


    /**
     * Related to grid filtering
     * 	These functions only work when filters are turned on
     */

    /*
     * Are filters enabled
     */
    this.doFilters = function()
    {
    	return _self.filterSubmitHandler;
    }

    /*
     * Initialize the filter submit button and any necessary containers
     */
    this.initFilters = function()
    {
        if(_self.doFilters())
        {
//        	_self.renderFilterButton();

        	var opacity = .65;
        	// Add groups to the 3 main layers for adding highlight elements
        	// Adding them here allows them to behave with the fixed header scrolling
        	_self.rowHeaderHighlightGroup = _self.rowGroup.insert("g","#"+_self.rowGroupInner.attr("id"))
        		.attr("id","fhRowGroup");

        	_self.rowHeaderHighlightPath = _self.rowHeaderHighlightGroup.append("path")
	    		.attr("id","fhRowPath")
	    		.attr("class","fh")
	    		.style("pointer-events","none");

        	_self.matrixHighlightGroup = _self.matrixGroup.insert("g",":first-child")
        	//_self.matrixHighlightGroup = _self.matrixGroup.append("g")
    			.attr("id","fhMatrixGroup");

        	_self.matrixHighlightPath = _self.matrixHighlightGroup.append("path")
        		.attr("id","fhMatrixPath")
        		.attr("class","fh")
        		.style("pointer-events","none")
        		.style("fill-rule","evenodd");

        	_self.colHeaderHighlightGroup = _self.colGroup.insert("g","#"+_self.colGroupInner.attr("id"))
    			.attr("id","fhColGroup");

        	_self.colHeaderHighlightPath = _self.colHeaderHighlightGroup.append("path")
	    		.attr("id","fhColPath")
	    		.attr("class","fh")
	    		.style("pointer-events","none");

        	// add some aggregate selectors for more easier manipulation of rows and column highlights
        	_self.rowHighlightGroup = d3.selectAll([_self.rowHeaderHighlightGroup[0][0],
        	                                        _self.matrixHighlightGroup[0][0]]);
        	_self.colHighlightGroup = d3.selectAll([_self.colHeaderHighlightGroup[0][0],
        	                                        _self.matrixHighlightGroup[0][0]]);
        }
    }

    /*
     * Add the filter select button and event listeners for a row
     */
    this.addRowFilterListener = function(d3Target,rowHeight,row)
    {
    	if(_self.doFilters())
    	{
	    	//var rowRendered = d3.select(d3Target[0][0].childNodes[0]);
	    	//var padding = 14;
	    	//var x = d3Target[0][0].getBBox().width + padding;

	    	var rect = d3Target.append("rect")
	    		.attr("x", 0)
	    		.attr("y", 0)
	    		.attr("height", rowHeight)
	    		.attr("width", _self.cellSize)
	    		.style("stroke","#aaa")
	    		.style("fill","transparent")
	    		.attr("class","rowFilter");

	    	var img = d3Target.append("image")
	    		.attr("x", _self.cellSize / 8)
	    		.attr("y", rowHeight / 8)
	    		.datum(row)
	    		.attr("height", rowHeight * (6/8))
	    		.attr("width", _self.cellSize * (6/8))
	    		.style("cursor","pointer")
	    		.attr("xlink:href",_self.filterUncheckedUrl)
	    		.attr("class","rowFilterImage")
	    		.on("click",_self.rowFilterClick);

	    	rect.append("svg:title")
	    		.text("Click to filter by this row");

	    	if(row.checked)
	    	{
	    		img.attr("xlink:href",_self.filterCheckedUrl);
	    	}
	    	if(row.dashed)
	    	{
	    		img.attr("xlink:href",_self.filterDashedUrl);
	    	}
    	}
    }

    /*
     * Add the filter select button and event listeners for a column
     */
    this.addColFilterListener = function(d3Target,colWidth,col)
    {
    	if(_self.doFilters())
    	{
	    	//var colRendered = d3.select(d3Target[0][0].childNodes[0]);
	    	//var padding = 14;
	    	//var y = d3Target[0][0].getBBox().height + padding;

	    	var rect = d3Target.append("rect")
	    		.attr("x", 0)
	    		.attr("y", 0)
	    		.attr("height", _self.cellSize)
	    		.attr("width", colWidth)
	    		.style("stroke","#aaa")
	    		.style("fill","transparent")
	    		.attr("class","colFilter");

	    	var img = d3Target.append("image")
	    		.attr("x", colWidth / 8)
	    		.attr("y", _self.cellSize / 8)
	    		.datum(col)
	    		.attr("height", _self.cellSize * (6/8))
	    		.attr("width", colWidth * (6/8))
	    		.style("cursor","pointer")
	    		.attr("xlink:href",_self.filterUncheckedUrl)
	    		.attr("class","colFilterImage")
	    		.on("click",_self.colFilterClick);

	    	rect.append("svg:title")
	    		.text("Click to filter by this col");

	    	if(col.checked)
	    	{
	    		img.attr("xlink:href",_self.filterCheckedUrl);
	    	}
    	}
    }

    this.colFilterClick = function(col)
    {
    	var el = d3.select(this);
	if(col.checked)
	{
		_self.colFilterCheck(col, "uncheck", el);
		_self.colCart.remove(col.cid);
	}
	else
	{
		_self.colFilterCheck(col, "check", el);
		_self.colCart.add(col.cid);
		if (_self.colCart.size() === 1 && _self.selectionsClickHandler) {
		    // First item in cart. Show the cart to remind the user it's there.
		    selectionsPopupPanel.show();
		}
	}
	_self.updateFilterHighlights();
	_self.updateButtons();
	_self.updateSelectionsPopup();
    }

    this.colFilterCheck = function(col, type, d3Target)
    {
    	if (type == "check")
    	{
    		col.checked = true;
    		d3Target.attr("xlink:href",_self.filterCheckedUrl);
    	}
    	else // uncheck
    	{
    		col.checked = false;
    		d3Target.attr("xlink:href",_self.filterUncheckedUrl);
    	}
    }

    this.updateSelectionsPopup = function () {
	var items = _self.getFilteredColumns().sort((a,b) => {
	    a = a.toLowerCase();
	    b = b.toLowerCase();
	    return a.localeCompare(b);
	});
	selectionsPopupPanel.header.innerText = _self.selectionsTitle;
	var lstContents = items.map(i => `<li>${i}</li>`).join('');
	var contents = '<ul id="selectionsPopupList">' + lstContents + '</ul><br/><button type="button">Clear all</button>';
	var bdy = selectionsPopupPanel.body;
	bdy.innerHTML = contents;
	var liElts = Array.from(bdy.querySelectorAll('li'));
	liElts.forEach(elt => elt.addEventListener('click', e => {
	    var item = e.target.innerText;
	    _self.colCart.remove(item);
	    _self.syncVisibleColumnsWithCart();
	}));
	var clearBtn = bdy.querySelector('button');
	clearBtn.addEventListener('click', e => {
	    _self.colCart.clear();
	    _self.syncVisibleColumnsWithCart();
	});
	if (liElts.length === 0) {
	    // if the popup has become empty, disappear it.
	    selectionsPopupPanel.hide();
	}
    }

    this.syncVisibleColumnsWithCart = function () {
	var gridid;
	if (_self.name === 'structureStageGrid') {
	    gridid = 'stagegriddata';
	} else if (_self.name === 'structureGeneGrid') {
	    gridid = 'genegriddata';
	} else {
	    return;
	}
	var displayedColumns = Array.from(document.querySelectorAll(`#${gridid} #colGroupInner > g`))
	var cart = new Set(_self.getFilteredColumns());
        displayedColumns.forEach(elt => {
	    var col = elt.__data__;
	    var d3elt = d3.select(elt.querySelector('image'));
	    var action = cart.has(col.cid) ? 'check' : 'uncheck';
	    _self.colFilterCheck(col, action, d3elt);
	})
	_self.updateFilterHighlights();
	_self.updateButtons();
	_self.updateSelectionsPopup();
    }

    /*
     * What happens when you select a filter
     */
    this.rowFilterClick = function(row)
    {
    	var el = d3.select(this);
	if(row.checked)
	{
		_self.rowFilterCheck(row, "uncheck", el);
	}
	else
	{
		_self.rowFilterCheck(row, "check", el);
	}

	_self.lastFilterRow = row;
	_self.updateFilterTree();
	_self.updateFilterHighlights();

	_self.data.rows.forEach(r => {
	    if (r.checked && !(r.parent && r.parent.checked)) {
	    //if (r.checked) {
	        _self.rowCart.add(r.rowId)
	    } else {
	        _self.rowCart.remove(r.rowId)
	    }
	})

	_self.updateButtons();
    }

    /*
     * activates Filter and Selections buttons if anything is selected
     */
    this.updateButtons = function()
    {
    	var filteredRows = _self.getFilteredRows();
	var filteredColumns = _self.getFilteredColumns();
    	if((filteredRows && filteredRows.length) || (filteredColumns && filteredColumns.length))
    	{
		_self.filterButtonGroup.select("#filterTextID").style("opacity","1");
		_self.filterButtonGroup.select("#filterIconID").style("opacity","1");
    	}
    	else
    	{
    		_self.filterButtonGroup.select("#filterTextID").style("opacity","0.5");
		_self.filterButtonGroup.select("#filterIconID").style("opacity","0.5");
	}

	if (_self.selectionsClickHandler) {
	    if (filteredColumns && filteredColumns.length)
	    {
		    _self.selectionsButtonGroup.style("visibility","visible");
	    }
	    else 
	    {
		    _self.selectionsButtonGroup.style("visibility","hidden");
	    }
	}
    }

    this.rowFilterCheck = function(row, type, d3Target, applyToChildren, applyToParents)
    {
    	d3Target = d3Target || _self.getFilterImageForData(row);
    	if(type == "check")
    	{
		if(d3Target)
    		{
    			d3Target.attr("xlink:href",_self.filterCheckedUrl);
    		}
    		row.checked = true;
    		row.dashed = false;
    	}
    	else if(type == "dash")
    	{
    		row.checked = false;
    		row.dashed = true;
    		if(d3Target){
    			d3Target.attr("xlink:href",_self.filterDashedUrl);
    		}
    	}
    	else
    	{
    		row.checked = false;
    		row.dashed = false;
    		if(d3Target)
    		{
    			d3Target.attr("xlink:href",_self.filterUncheckedUrl);
    		}
    	}
    	if(applyToChildren && row.hasOwnProperty("children"))
    	{
    		row.children.forEach(function(child){
        		_self.rowFilterCheckUpdateChildren(child, type);
    		});
    	}
    	if(applyToParents && row.hasOwnProperty("parent"))
    	{
    		_self.rowFilterCheckUpdateParents(row.parent, type);
    	}
    }

    this.rowFilterCheckUpdateChildren = function(row,type,d3Target)
    {
    	_self.rowFilterCheck(row,type,d3Target,true,false);
    }

    this.rowFilterCheckUpdateParents = function(row,type,d3Target)
    {
    	_self.rowFilterCheck(row,type,d3Target,false,true);
    }

    this.getFilterImageForData = function(dataRow)
    {
    	var img;
    	_self.rowGroupInner.selectAll("g").each(function(d,i){
    		if(d.rid == dataRow.rid)
    		{
    			img = d3.select(this).select(".rowFilterImage");
    		}
    	});
    	return img;
    }

    /*
     * update the row checked state
     * for all rows
     */
    this.updateFilterTree = function()
    {
    	if(_self.doFilters())
    	{
	    	// get all top level rows
	    	var topRows = [];
	    	var lastUpdatedRow = _self.lastFilterRow;
	    	if(lastUpdatedRow)
	    	{
		    	_self.data.getRowsInRange(0,_self.maxRows).forEach(function(row){
		    		if (!row.hasOwnProperty("parent"))
		    		{
		    			topRows[topRows.length] = row;
		    		}
		    	});


		    	var lastID = lastUpdatedRow.oid || lastUpdatedRow.rid;
		    	var checked = lastUpdatedRow.checked || false;
		    	_self.updateFilterDuplicates(lastID, checked, topRows);
		    	_self.updateFilterParents(lastID, checked, topRows);
	    	}
    	}
    }

    /*
     * update the row checked state
     */
    this.updateFilterDuplicates = function(lastID,checked,childRows)
    {
    	if(_self.doFilters())
    	{
    		var type = checked ? "check" : "uncheck";
	    	childRows.forEach(function(row){
	    		var rid = row.oid || row.rid;
	    		if(rid == lastID)
	    		{
	    			_self.rowFilterCheck(row,type);
	    		}

	    		// traverse down the tree
	    		if(row.hasOwnProperty("children"))
	    		{
	    			_self.updateFilterDuplicates(lastID,checked,row.children);
	    		}
	    	});
    	}
    }

    this.updateFilterParents = function(lastID,checked,childRows)
    {
    	if(_self.doFilters())
    	{
	    	childRows.forEach(function(row){
	    		var rid = row.oid || row.rid;
	    		if(rid != lastID)
	    		{
		    		// traverse down the tree
		    		if(row.hasOwnProperty("children"))
		    		{
		    			_self.updateFilterParents(lastID,checked,row.children);
		    		}
	    		}
	    		else
	    		{
	    			// update parent state
	    			_self.updateFilterParentState(row);

	    			// apply current state to all children
	    			if(row.hasOwnProperty("children"))
	    			{
		    			var type = checked ? "check" : "uncheck";
		    			row.children.forEach(function(child){
		    				_self.rowFilterCheckUpdateChildren(child,type);
		    			});
	    			}
	    		}
	    	});
    	}
    }

    this.updateFilterParentState = function(row)
    {
    	if(row.hasOwnProperty("parent"))
		{
	    	// verify checked state of siblings
			var checkedCount = 0;
			var dashedCount = 0;
			var siblings = row.parent.children;
			siblings.forEach(function(sibling){
				if(sibling.checked)
				{
					checkedCount += 1;
				}
				else if(sibling.dashed)
				{
					dashedCount += 1;
				}
			});
			var type = "uncheck";
			if (checkedCount == siblings.length)
			{
				// all siblings checked
				type = "check";
			}
			else if(checkedCount > 0 || dashedCount > 0)
			{
				// some siblings checked
				type = "dash";
			}

			_self.rowFilterCheck(row.parent,type);
			_self.updateFilterParentState(row.parent);
		}
    }

    /*
     * update positioning of filter highlights
     */
    this.updateFilterHighlights = function()
    {
    	if(_self.doFilters())
    	{
    		_self.refreshFilterGreyOutPath();
    	}
    }

    this.refreshFilterGreyOutPath = function()
    {
    	// inputs
    	var rows = _self.data.getVisibleRowsInRange(0,_self.maxRows);
    	var cols = _self.data.getColumnsInRange(0, _self.maxColumns);
    	var cellSize = _self.cellSize;

    	// don't grey out sections if there are no filters
    	var hasRowFilters = false;
    	var hasColFilters = false;
    	rows.forEach(function(row){
    		if (row.checked) hasRowFilters = true;
    	});
    	cols.forEach(function(col){
    		if (col.checked) hasColFilters = true;
    	});

    	var matrixHlPath = "",
    		rowHlPath = "",
    		colHlPath = "";
    	if(hasRowFilters || hasColFilters)
    	{
    		matrixHlPath = _self.getFilterMatrixGreyOutPath(rows,cols,cellSize);

			// surround w/ outer square w/ fiil-rule: evenodd
    		// to invert the area of the unselected cells
			var width = cols.length * cellSize + 1;
			var height = rows.length * cellSize + 1;
			matrixHlPath += "M-2 -2 L-2 "+height
				+" L"+width+" "+height
				+" L"+width+" 0"
				+" L-2 -2";
    	}
    	if(hasRowFilters)
    	{
    		var rowWidth = _self.rowGroupInner[0][0].getBBox().width;
    		rowHlPath = _self.getFilterRowGreyOutPath(rows,cellSize,rowWidth);
    	}
    	if(hasColFilters)
    	{
    		var colHeight = _self.colGroupInner[0][0].getBBox().height;
    		colHlPath = _self.getFilterColGreyOutPath(cols,cellSize,colHeight);
    	}

    	// set d3 defaults for null d values
    	if (matrixHlPath == "") matrixHlPath = "M0 0";
    	if (rowHlPath == "") rowHlPath = "M0 0";
    	if (colHlPath == "") colHlPath = "M0 0";

    	// set the grey out paths
    	_self.matrixHighlightPath.attr("d",matrixHlPath);
    	_self.rowHeaderHighlightPath.attr("d",rowHlPath);
    	_self.colHeaderHighlightPath.attr("d",colHlPath);
    }


    /*
     * Creates a path element to grey out every section of the matrix that is not being filtered
     */
    this.getFilterMatrixGreyOutPath = function(rows,cols,cellSize)
    {
    	// we are going to find every unselected area and draw a rectangle over it
    	// everything happens relative to the matrix (so starting at point 0,0)
    	var rectPaths = [];

    	var endRow = 0;
    	while (endRow < rows.length)
    	{
    		var startRow = null;
    		// find first unselected row bounds
    		for(endRow; endRow < rows.length; endRow++)
    		{
    			if (rows[endRow].checked)
    			{
    				break;
    			}
    			else if(startRow == null)
    			{
    				startRow = endRow;
    			}
    		}

    		if (startRow != null)
    		{
        		var endCol = 0;
        		// find the first unselected col bounds
        		while (endCol < cols.length)
        		{
        			var startCol = null;
            		// find first unselected col bounds
            		for(endCol; endCol < cols.length; endCol++)
            		{
            			if (cols[endCol].checked)
            			{
            				break;
            			}
            			else if(startCol == null)
            			{
            				startCol = endCol;
            			}
            		}

            		if (startCol != null)
            		{
            			var startX = startCol*cellSize
            				startY = startRow*cellSize,
            				endX = (endCol)*cellSize,
            				endY = (endRow)*cellSize;
            			if (startX == 0) startX = -1;
            			if (startY == 0) startY = -1;
            			// draw a path string for this rectangle.
            			var paths = [
            			             "M"+startX+" "+startY,
            			             "L"+startX+" "+endY,
            			             "L"+endX+" "+endY,
            			             "L"+endX+" "+startY,
            			             "L"+startX+" "+startY
            			             ];

            			rectPaths[rectPaths.length] = paths.join(" ");
            		}
            		endCol += 1;
        		}
    		}
    		endRow += 1;
    	}

    	// join all rectangles into one path
    	var pathString = rectPaths.join(" ");
    	return pathString;
    }
    /*
     * Creates a path element to grey out every section of the row headers that is not being filtered
     */
    this.getFilterRowGreyOutPath = function(rows,cellSize,rowWidth)
    {
    	var rectPaths = [];
    	var endRow = 0;
    	while (endRow < rows.length)
    	{
    		var startRow = null;
    		// find first unselected row bounds
    		for(endRow; endRow < rows.length; endRow++)
    		{
    			if (!rows[endRow].checked)
    			{
    				break;
    			}
    			else if(startRow == null)
    			{
    				startRow = endRow;
    			}
    		}

    		if (startRow != null)
    		{
    			var startX = 0
					startY = startRow*cellSize,
					endX = rowWidth,
					endY = (endRow)*cellSize;
				// draw a path string for this rectangle.
				var paths = [
				             "M"+startX+" "+startY,
				             "L"+startX+" "+endY,
				             "L"+endX+" "+endY,
				             "L"+endX+" "+startY,
				             "L"+startX+" "+startY
				             ];
				rectPaths[rectPaths.length] = paths.join(" ");
    		}
    		endRow += 1;
    	}
    	var pathString = rectPaths.join(" ");
    	return pathString;
    }

    this.rotateAngle = 40;
    /*
     * Creates a path element to grey out every section of the column headers that is not being filtered
     */
    this.getFilterColGreyOutPath = function(cols,cellSize,colHeight)
    {
    	var rectPaths = [];
    	var endCol = 0;
    	while (endCol < cols.length)
    	{
    		var startCol = null;
    		// find first unselected row bounds
    		for(endCol; endCol < cols.length; endCol++)
    		{
    			if (!cols[endCol].checked)
    			{
    				break;
    			}
    			else if(startCol == null)
    			{
    				startCol = endCol;
    			}
    		}

    		if (startCol != null)
    		{
    			var startX = startCol*cellSize,
					startY = 0,
					endX = endCol*cellSize,
					endY = colHeight;
				// draw a path string for this rectangle.
				var paths = [
				             "M"+startX+" "+startY,
				             "L"+startX+" "+endY,
				             "L"+endX+" "+endY,
				             "L"+endX+" "+startY,
				             "L"+startX+" "+startY
				             ];

    			if (_self.verticalColumnLabels)
    			{
    				// slant the path after the checkbox section
    				var angle1 = _self.rotateAngle * (Math.PI / 180);
    				var angle2 = (90 - _self.rotateAngle) * (Math.PI / 180);
    				var slantOffset = (colHeight - cellSize) * Math.sin(angle1) / Math.sin(angle2);

    				var startX1 = startX + slantOffset,
    						startX2 = startX,
    						endX1 = endX,
    						endX2 = endX + slantOffset,
    						endY1 = endY - cellSize,
    						endY2 = endY;

    				paths = [
     			             "M"+startX1+" "+startY,
     			             "L"+startX2+" "+endY1,
     			             "L"+startX2+" "+endY2,
     			             "L"+endX1+" "+endY2,
     			             "L"+endX1+" "+endY1,
     			             "L"+endX1+" "+endY1,
     			             "L"+endX2+" "+startY,
     			             "L"+startX1+" "+startY
     			             ];
    			}
				rectPaths[rectPaths.length] = paths.join(" ");
    		}
    		endCol += 1;
    	}
    	var pathString = rectPaths.join(" ");
    	return pathString;
    }

    this.loadConfig(config);
    if(!this.testMode) this.init();
}
