/*
 * Tests of the SuperGrid Data structure methods
 */

module( "SuperGrid Data Methods", {
  setup: function() {
    // prepare something for all following tests
  },
  teardown: function() {
    // clean up after each test
  }
});

/*
 * Test addRow()
 */
test("testAddRowNotExists",function(){
	var data = new SGData({data:[]});
	data.addRow("Row1");
	deepEqual([{rid:"Row1"}], data.rows);
});

test("testAddRowDupe",function(){
	var data = new SGData({data:[]});
	data.addRow("Row1");
	data.addRow("Row1");
	deepEqual([{rid:"Row1"}], data.rows);
});

test("testAddRowExistsFromCell",function(){
	var data = new SGData({data:[{rid:"Row1",cid:"Col1",val:"1"}]});
	data.getInitialDataStatistics();
	data.addRow({rid:"Row1"});
	deepEqual([{rid:"Row1",_d:true}], data.rows);
});

/*
 * test config default rows
 */
test("testDefaultDataRows",function(){
	var data = new SGData({data:[{rid:"Row1",cid:"Col1",val:"1"}],
		rows:[{rid:"Row1",term:"Testing1234"}]});
	data.getInitialDataStatistics();
	deepEqual([{rid:"Row1",term:"Testing1234",_d:true,depth:0,oid:"Row1"}], data.rows);
});

/*
 * Test addColumn()
 */
test("testAddColNotExists",function(){
	var data = new SGData({data:[]});
	data.addColumn("Col1");
	deepEqual([{cid:"Col1"}], data.cols);
});

test("testAddColDupe",function(){
	var data = new SGData({data:[]});
	data.addColumn("Col1");
	data.addColumn("Col1");
	deepEqual([{cid:"Col1"}], data.cols);
});

test("testAddColExistsFromCell",function(){
	var data = new SGData({data:[{rid:"Row1",cid:"Col1",val:"1"}]});
	data.addColumn("Col1");
	deepEqual([{cid:"Col1"}], data.cols);
});

/*
 * Test registerCell()
 */
test("testRegisterCellNotExists",function(){
	var data = new SGData({data:[]});
	data.registerCell({ri:0,ci:1,val:"1"});
	equal("1", data.dm[0][1].val);
});
test("testRegisterCellExists",function(){
	var data = new SGData({data:[]});
	data.registerCell({ri:0,ci:1,val:"1"});
	data.registerCell({ri:0,ci:1,val:"1"});
	equal("1", data.dm[0][1].val);
});

test("testCellAggregator",function(){
	var data = new SGData({
		data: [],
		cellAggregator: function(old,newCell)
		{
			return {val:"testing"};
		}
	});
	data.registerCell({ri:0,ci:1,val:"1"});
	data.registerCell({ri:0,ci:1,val:"1"});
	equal("testing" , data.dm[0][1].val);
});

/*
* Test empty() method
*/
test("testDataEmptyCheckTrue",function(){
	var data = new SGData({data:[]});
	equal(true, data.empty());
});
test("testDataEmptyCheckFalse",function(){
	var data = new SGData({data:[{rid:"Row1",cid:"Col1",val:"1"}]});
	equal(false, data.empty());
});

/*
* test addCell() component
*/
test("testAddCellNotExists",function(){
	var data = new SGData({data:[]});
	data.addCell({rid:"Row1",cid:"Col1",val:"1"});
	deepEqual([{rid:"Row1",_d:true}], data.rows);
	deepEqual([{cid:"Col1"}], data.cols);
	equal("1", data.dm[0][0].val);
});
test("testAddCellExists",function(){
	var data = new SGData({data:[]});
	data.addCell({rid:"Row1",cid:"Col1",val:"1"});
	data.addCell({rid:"Row1",cid:"Col1",val:"1"});
	deepEqual([{rid:"Row1",_d:true}], data.rows);
	deepEqual([{cid:"Col1"}], data.cols);
	equal("1", data.dm[0][0].val);
});
test("testAddCellSameCellDoesNotAggregate",function(){
	var data = new SGData({data:[{rid:"Row1",cid:"Col1",val:"1"}],
		cellAggregator: function(old,newCell){
			return {"val":"testing"};
		}
	});
	data.addCell(data.data[0]);
	data.addCell(data.data[0]);
	deepEqual([{rid:"Row1",_d:true}], data.rows);
	deepEqual([{cid:"Col1"}], data.cols);
	equal("1", data.dm[0][0].val,"cellAggregator should not set val to \"testing\" for adding same cell instance");
});

/*
* test getRowsInRange()
*/
test("testGetRowsInRangeEmpty",function(){
	var data = new SGData({data:[]});
	data.getInitialDataStatistics(); // manually force row/column calculations
	equal(0, data.getRowsInRange(0,10).length);
});
test("testGetRowsInRangeExists",function(){
	var data = new SGData({data:[{rid:"Row1",cid:"Col1",val:"1"},
	                             {rid:"Row2",cid:"Col1",val:"2"},
	                             {rid:"Row3",cid:"Col1",val:"3"}]});
	data.getInitialDataStatistics(); // manually force row/column calculations
	deepEqual([{rid:"Row1",_d:true},{rid:"Row2",_d:true},{rid:"Row3",_d:true}], data.getRowsInRange(0,2));
});
test("testGetRowsInRangeOutOfRange",function(){
	var data = new SGData({data:[{rid:"Row1",cid:"Col1",val:"1"},
	                             {rid:"Row2",cid:"Col1",val:"2"},
	                             {rid:"Row3",cid:"Col1",val:"3"}]});
	data.getInitialDataStatistics(); // manually force row/column calculations
	equal(0, data.getRowsInRange(4,20).length);
});
test("testGetRowsInRangePartialInRange",function(){
	var data = new SGData({data:[{rid:"Row1",cid:"Col1",val:"1"},
	                             {rid:"Row2",cid:"Col1",val:"2"},
	                             {rid:"Row3",cid:"Col1",val:"3"}]});
	data.getInitialDataStatistics(); // manually force row/column calculations
	deepEqual([{rid:"Row2",_d:true},{rid:"Row3",_d:true}], data.getRowsInRange(1,20));
});

/*
* test getColumnsInRange()
*/
test("testGetColsInRangeEmpty",function(){
	var data = new SGData({data:[]});
	data.getInitialDataStatistics(); // manually force Col/column calculations
	equal(0, data.getColumnsInRange(0,10).length);
});
test("testGetColsInRangeExists",function(){
	var data = new SGData({data:[{rid:"Row1",cid:"Col1",val:"1"},
	                             {rid:"Row1",cid:"Col2",val:"2"},
	                             {rid:"Row1",cid:"Col3",val:"3"}]});
	data.getInitialDataStatistics(); // manually force Col/column calculations
	deepEqual([{cid:"Col1"},{cid:"Col2"},{cid:"Col3"}], data.getColumnsInRange(0,2));
});
test("testGetColsInRangeOutOfRange",function(){
	var data = new SGData({data:[{rid:"Row1",cid:"Col1",val:"1"},
	                             {rid:"Row1",cid:"Col2",val:"2"},
	                             {rid:"Row1",cid:"Col3",val:"3"}]});
	data.getInitialDataStatistics(); // manually force Col/column calculations
	equal(0, data.getColumnsInRange(4,20).length);
});
test("testGetColsInRangePartialInRange",function(){
	var data = new SGData({data:[{rid:"Row1",cid:"Col1",val:"1"},
	                             {rid:"Row1",cid:"Col2",val:"2"},
	                             {rid:"Row1",cid:"Col3",val:"3"}]});
	data.getInitialDataStatistics(); // manually force Col/column calculations
	deepEqual([{cid:"Col2"},{cid:"Col3"}], data.getColumnsInRange(1,20));
});

/*
 * Test excludeRow()
 */
test("testExcludeRowTrue",function(){
	var data = new SGData({rows:[{rid:"Row1"}],
			data:[{rid:"Row1",cid:"Col1",val:"1"},
	                             {rid:"Row2",cid:"Col1",val:"2"}],
	        ignoreUndefinedRows: true
	});
	data.getInitialDataStatistics(); // manually force row/column calculations
	deepEqual([{rid:"Row1",_d:true,depth:0,oid:"Row1"}], data.rows);
	equal(1, data.getCellsInRange(0,10,0,10).length);
});
test("testExcludeRowFalse",function(){
	var data = new SGData({rows:[{rid:"Row1"}],
			data:[{rid:"Row1",cid:"Col1",val:"1"},
	                             {rid:"Row2",cid:"Col1",val:"2"}],
	        ignoreUndefinedRows: false
	});
	data.getInitialDataStatistics(); // manually force row/column calculations
	deepEqual([{rid:"Row1",_d:true,depth:0,oid:"Row1"},{rid:"Row2",_d:true}], data.rows);
	equal(2, data.getCellsInRange(0,10,0,10).length);
});

/*
* Test processTrees()
*/
test("testProcessTreesEmpty",function(){
	var data = new SGData({data:[]});
	var nodes = [];
	var flatNodes = data.processTrees(nodes);
	equal(0, flatNodes.length);
});

test("testProcessTreesOneNode",function(){
	var data = new SGData({data:[]});
	var nodes = [{id:"P1"}];
	var flatNodes = data.processTrees(nodes);
	equal(1, flatNodes.length);
	equal("P1", flatNodes[0].id);
});

test("testProcessTreesOnlyParents",function(){
	var data = new SGData({data:[]});
	var nodes = [{id:"P1"},{id:"P2"}];
	var flatNodes = data.processTrees(nodes);
	equal(2, flatNodes.length);
	equal("P1", flatNodes[0].id);
	equal("P2", flatNodes[1].id);
});

test("testProcessTreesOneChild",function(){
	var data = new SGData({data:[]});
	var nodes = [{id:"P1", children:[
	                              {id:"C1"}
	                              ]}
	];
	var flatNodes = data.processTrees(nodes);
	equal(2, flatNodes.length);
	equal("P1", flatNodes[0].id);
	equal("C1", flatNodes[1].id);
});

test("testProcessTreesNestedChildren",function(){
	var data = new SGData({data:[]});
	var nodes = [{id:"P1", children:[
	                              {id:"C1", children:[
	                                                  {id:"C2"}                    
	                              ]}
	                              ]}
	];
	var flatNodes = data.processTrees(nodes);
	equal(3, flatNodes.length);
	equal("P1", flatNodes[0].id);
	equal("C1", flatNodes[1].id);
	equal("C2", flatNodes[2].id);
});

test("testProcessTreesComplexTreeNodeOrder",function(){
	var data = new SGData({data:[]});
	var nodes = [{id:"P1", children:[
	                              {id:"C1", children:[
	                                                  {id:"C2"}                    
	                              ]},
	                              {id:"C3"}
	                              ]},
	                              {id:"P2"}
	];
	var flatNodes = data.processTrees(nodes);
	var nodeIds = [];
	for(var i=0;i<flatNodes.length;i++)
	{
		nodeIds.push(flatNodes[i].id);
	}
	deepEqual(["P1","C1","C2","C3","P2"], nodeIds);
});
test("testProcessTreesComplexTreeNodeDepths",function(){
	var data = new SGData({data:[]});
	var nodes = [{id:"P1", children:[
	                              {id:"C1", children:[
	                                                  {id:"C2"}                    
	                              ]},
	                              {id:"C3"}
	                              ]},
	                              {id:"P2"}
	];
	var flatNodes = data.processTrees(nodes);
	var nodeDepths = [];
	for(var i=0;i<flatNodes.length;i++)
	{
		nodeDepths.push(flatNodes[i].depth);
	}
	deepEqual([0,1,2,1,0], nodeDepths);
});

test("testProcessTreesChildDepthValue",function(){
	var data = new SGData({data:[]});
	var nodes = [{id:"P1", children:[
	                              {id:"C1"}
	                              ]}
	];
	var flatNodes = data.processTrees(nodes);
	equal(0, flatNodes[0].depth);
	equal(1, flatNodes[1].depth);
});

test("testProcessTreesAttachParent",function(){
	var data = new SGData({data:[]});
	var nodes = [{id:"P1", children:[
	                              {id:"C1"}
	                              ]}
	];
	var flatNodes = data.processTrees(nodes);
	equal("P1", flatNodes[1].parent.id);
});

test("testProcessTreesDuplicateIds",function(){
	var data = new SGData({data:[]});
	var nodes = [{id:"P1", children:[
	                              {id:"C1"}
	                              ]},
	                              {id:"C1"}
	];
	var flatNodes = data.processTrees(nodes);
	var nodeIds = [];
	for(var i=0;i<flatNodes.length;i++)
	{
		nodeIds.push(flatNodes[i].id);
	}
	deepEqual(["P1","C1","C1"], nodeIds);
});

test("testProcessTreesDuplicateEdgesFromDifferentPaths",function(){
	var data = new SGData({data:[]});
	var nodes = [{rid:"T1", children:[
	                              {rid:"T2", children:[
	                                                  {rid:"T3"}
	                                                  ]}
	                              ]},
	                              {rid:"T1b", children:[
	                                                   {rid:"T2", children:[
	                                                                       {rid:"T3"}
	                                                                       ]}
	                                                   ]}
	];
	var flatNodes = data.processTrees(nodes);
	var nodeIds = [];
	for(var i=0;i<flatNodes.length;i++)
	{
		nodeIds.push(flatNodes[i].oid);
	}
	deepEqual(["T1","T2","T3","T1b","T2","T3"], nodeIds);
	var uniqueRids = {};
	for(var i=0;i<flatNodes.length;i++)
	{
		var rid = flatNodes[i].rid;
		uniqueRids[rid] = rid;
	}
	var uniqueRids = getKeys(uniqueRids);
	equal(6,uniqueRids.length);
});
test("testProcessTreesOpenedRowHasNoChildren",function(){
	// node should be set to leaf, if there are no children
	var data = new SGData({data:[]});
	var nodes = [{rid:"P1",oc:"open",children:[]}];
	var flatNodes = data.processTrees(nodes);
	equal(undefined, flatNodes[0].oc);
});
test("testProcessTreesOpenedRowHasNoChildrenButExpandable",function(){
	// node should be set to close, if there are no children, but can be expanded
	var data = new SGData({data:[]});
	var nodes = [{rid:"P1",oc:"open",children:[],ex:true}];
	var flatNodes = data.processTrees(nodes);
	equal("close", flatNodes[0].oc);
});

/*
 * test insertDataForChildrenOf()
 */
test("testInsertDataForChildrenOfRowHasRow",function(){
	var data = new SGData({data:[], 
		rows:[{rid:"row1", children:[
	                                 {rid:"row2"}
	                               ]
		}
	]});
	var row = data.rows[0];
	data.insertDataForChildrenOfRow(row,[{rid:"row2",cid:"col1",val:"1"}]);
	var cell = data.getCellsInRange(0,10,0,10)[0];
	equal(1,cell.ri);
	equal(true,cell.registered);
});

test("testInsertDataForChildrenOfRowRowNotAChild",function(){
	var data = new SGData({data:[], 
		rows:[{rid:"row1", children:[
		                             {rid:"row3"}
		                             ]}
		,{rid:"row2"}
	]});
	var row = data.rows[0];
	data.insertDataForChildrenOfRow(row,[{rid:"row2",cid:"col1",val:"1"}]);
	equal(0,data.getCellsInRange(0,10,0,10).length);
});

test("testInsertDataForChildrenOfRowNotHaveRowNoChildren",function(){
	var data = new SGData({data:[], 
		rows:[{rid:"row1"},{rid:"row2"}
	]});
	var row = data.rows[0];
	data.insertDataForChildrenOfRow(row,[{rid:"row2",cid:"col1",val:"1"}]);
	equal(0,data.getCellsInRange(0,10,0,10).length);
});

/*
 * test mapDuplicateCells()
 */
test("testMapDuplicateCells",function(){
	var data = new SGData({data:[],
		rows : [{rid:"T1", children:[
	                              {rid:"T2"}
	                              ]},
				{rid:"T1b", children:[{rid:"T2"}]}
	]});
	var cell = {rid:"T2", cid:"col1",val:"1"};
	data.mapDuplicateCells(cell);
	// assert we have a new unique rid
	notEqual("T2",cell.rid);
	// make sure we've also created one extra cell with yet another unique rid
	equal(1,data.getCellsInRange(0,10,0,10).length);
	notEqual(cell.rid,data.getCellsInRange(0,10,0,10)[0].rid);
});

/*
 * sortColumns
 */
test("testSortColumnsUpdateDM", function(){
	var data = new SGData({data:[{rid:"r1",cid:"c2",val:"2"},
	                             {rid:"r1",cid:"c1",val:"1"}]});
	data.getInitialDataStatistics();
	data.sortColumns();
	
	equal("2",data.getCellsInRange(0,1,1,1)[0].val);
	equal("1",data.getCellsInRange(0,1,0,0)[0].val);
});
test("testSortColumnsUpdateDMMultipleRows", function(){
	var data = new SGData({data:[{rid:"r1",cid:"c2",val:"1,2"},
	                             {rid:"r2",cid:"c2",val:"2"},
	                             {rid:"r2",cid:"c1",val:"1"},
	                             {rid:"r2",cid:"c5",val:"5"},
	                             {rid:"r2",cid:"c4",val:"4"},
	                             {rid:"r2",cid:"c3",val:"3"}]});
	data.getInitialDataStatistics();
	data.sortColumns();
	
	var cells = data.getCellsInRange(1,1,0,4);
	var vals = [];
	for(var i=0; i<cells.length; i++)
	{
		vals.push(cells[i].val);
	}
	deepEqual(["1","2","3","4","5"],vals);
});

test("testSortColumnsUpdateDMLongDistanceSwap", function(){
	var data = new SGData({data:[{rid:"r2",cid:"c1",val:"1"},
	                             {rid:"r2",cid:"c2",val:"2"},
	                             {rid:"r2",cid:"c3",val:"3"},
	                             {rid:"r2",cid:"c4",val:"4"},
	                             {rid:"r2",cid:"c10",val:"10"}]});
	data.getInitialDataStatistics();
	data.sortColumns();
	
	var cells = data.getCellsInRange(0,0,0,4);
	var vals = [];
	for(var i=0; i<cells.length; i++)
	{
		vals.push(cells[i].val);
	}
	deepEqual(["1","10","2","3","4"],vals);
});

/*
 * helper functions
 */
function getKeys(arr)
{
	var keys = [];
	for(var k in arr) keys.push(k);
	return keys;
}