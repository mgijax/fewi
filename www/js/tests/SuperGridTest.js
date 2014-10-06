/*
 * Tests of the SuperGrid display methods
 */

var grid;
module( "SuperGrid Display Methods", {
  setup: function() {
    // prepare something for all following tests
	  grid = new SuperGrid({testMode: true});
  },
  teardown: function() {
    // clean up after each test
	  grid = null;
  }
});

var targetDivJq;
module( "SuperGrid Filter Row Methods", {
  setup: function() {
	  targetDivJq = $("<div id=\"testTarget\"></div>").appendTo("body");
	  // All the following filter tests use this tree structure for mock data
	  grid = new SuperGrid({
		  target: "testTarget",
		  rows: [{rid:"r1",children:[
		                              {rid:"r2", children:[
		                                                   {rid: "rDup",uniqueId:"rDup1"}
		                                                   ]
		                              },
		                              {rid: "r3", children:[
		                                                    {rid: "rDup",uniqueId:"rDup2"}
		                                                    ]
		                              },
		                              {rid: "r4", children:[
		                                                    {rid: "r5"},
		                                                    {rid: "r6"}
		                                                    ]
		                              }
		                              ]
		  }],
		  data: [{rid:"r1",cid:"c1",val:"1"},
		          {rid:"r2",cid:"c1",val:"1"},
		          {rid:"r3",cid:"c1",val:"1"},
		          {rid:"rDup",cid:"c1",val:"1"},
		          {rid:"r4",cid:"c1",val:"1"},
		          {rid:"r5",cid:"c1",val:"1"}
		  ],
		  filterSubmitHandler: function(){} // enable filters
	  });
  },
  teardown: function() {
    // clean up after each test
	  grid = null;
	  targetDivJq.remove();
  }
});



/*
 * updateFilterTree()
 */
//simulate filter click, pre updateFilterTree()
function clickFilterRow(rid)
{
	grid.data.rows.forEach(function(row){
		var rowId = row.uniqueId || row.oid || row.rid;
		if(rowId == rid)
		{
			row.checked = true;
			grid.lastFilterRow = row;
		}
	});
}

function getCheckedRids()
{
	var checkRows = [];
	grid.data.rows.forEach(function(row){
		if(row.checked)
		{
			checkRows[checkRows.length] = row.oid || row.rid;
		}
	});
	return checkRows;
}
function getDashedRids()
{
	var dashRows = [];
	grid.data.rows.forEach(function(row){
		if(row.dashed)
		{
			dashRows[dashRows.length] = row.oid || row.rid;
		}
	});
	return dashRows;
}

test("testUpdateFilterParentsNoFilters",function(){
	grid.updateFilterTree();
	deepEqual([],getCheckedRids());
	deepEqual([],getDashedRids());
});

test("testUpdateFilterParentsClickTopParent",function(){
	clickFilterRow("r1");
	grid.updateFilterTree();
	equal(grid.data.rows.length,getCheckedRids().length);
});

test("testUpdateFilterParentsClickBottomChild",function(){
	clickFilterRow("r5");
	grid.updateFilterTree();
	deepEqual(["r5"],getCheckedRids());
	deepEqual(["r1","r4"],getDashedRids());
});

test("testUpdateFilterParentsClickMiddleChild",function(){
	clickFilterRow("r4");
	grid.updateFilterTree();
	deepEqual(["r4","r5","r6"],getCheckedRids());
	deepEqual(["r1"],getDashedRids());
});

test("testUpdateFilterParentsClickDuplicate1",function(){
	clickFilterRow("rDup1");
	grid.updateFilterTree();
	deepEqual(["r2","rDup","r3","rDup"],getCheckedRids());
	deepEqual(["r1"],getDashedRids());
});

test("testUpdateFilterParentsClickDuplicate2",function(){
	clickFilterRow("rDup2");
	grid.updateFilterTree();
	deepEqual(["r2","rDup","r3","rDup"],getCheckedRids());
	deepEqual(["r1"],getDashedRids());
});

test("testUpdateFilterParentsBubblingAllChildrenSelectedButOneIsDashed",function(){
	clickFilterRow("r2");
	grid.updateFilterTree();
	clickFilterRow("r3");
	grid.updateFilterTree();
	clickFilterRow("r5");
	// r6 remains unchecked, so r4 is a dash

	grid.updateFilterTree();
	deepEqual(["r2","rDup","r3","rDup","r5"],getCheckedRids());
	deepEqual(["r1","r4"],getDashedRids());
});

module( "SuperGrid Filter Highlight Methods", {
  setup: function() {
    // prepare something for all following tests
	  grid = new SuperGrid({testMode: true});
  },
  teardown: function() {
    // clean up after each test
	  grid = null;
  }
});

test("testGetFilterMatrixGreyOutPathEmpty",function(){
	var cellSize = 10,
		rows = [],
		cols = [];
	equal("", grid.getFilterMatrixGreyOutPath(rows,cols,cellSize));
});

test("testGetFilterMatrixGreyOutPathNoChecks",function(){
	var cellSize = 10,
		rows = [{},{},{}],
		cols = [{},{},{}];
	equal("M-1 -1 L-1 30 L30 30 L30 -1 L-1 -1", grid.getFilterMatrixGreyOutPath(rows,cols,cellSize));
});

test("testGetFilterMatrixGreyOutPathAllChecks",function(){
	var cellSize = 10,
		rows = [{checked:true},{checked:true},{checked:true}],
		cols = [{checked:true},{checked:true},{checked:true}];
	equal("", grid.getFilterMatrixGreyOutPath(rows,cols,cellSize));
});

test("testGetFilterMatrixGreyOutPathAllRowChecks",function(){
	var cellSize = 10,
		rows = [{checked:true},{checked:true},{checked:true}],
		cols = [{},{},{}];
	equal("", grid.getFilterMatrixGreyOutPath(rows,cols,cellSize));
});

test("testGetFilterMatrixGreyOutPathAllColumnChecks",function(){
	var cellSize = 10,
		rows = [{},{},{}],
		cols = [{checked:true},{checked:true},{checked:true}];
	equal("", grid.getFilterMatrixGreyOutPath(rows,cols,cellSize));
});

test("testGetFilterMatrixGreyOutPathOneColumn",function(){
	var cellSize = 10,
		rows = [{},{},{}],
		cols = [{},{checked:true},{}];
	equal("M-1 -1 L-1 30 L10 30 L10 -1 L-1 -1 M20 -1 L20 30 L30 30 L30 -1 L20 -1", grid.getFilterMatrixGreyOutPath(rows,cols,cellSize));
});

test("testGetFilterMatrixGreyOutPathOneRow",function(){
	var cellSize = 10,
		rows = [{},{checked:true},{}],
		cols = [{},{},{}];
	equal("M-1 -1 L-1 10 L30 10 L30 -1 L-1 -1 M-1 20 L-1 30 L30 30 L30 20 L-1 20", grid.getFilterMatrixGreyOutPath(rows,cols,cellSize));
});

test("testGetFilterMatrixGreyOutPathOneColumnOneRow",function(){
	var cellSize = 1,
		rows = [{},{checked:true},{}],
		cols = [{},{checked:true},{}];
	equal("M-1 -1 L-1 1 L1 1 L1 -1 L-1 -1"+
			" M2 -1 L2 1 L3 1 L3 -1 L2 -1"+
			" M-1 2 L-1 3 L1 3 L1 2 L-1 2"+
			" M2 2 L2 3 L3 3 L3 2 L2 2", grid.getFilterMatrixGreyOutPath(rows,cols,cellSize));
});

test("testGetOpenStateStringNoParents",function(){
	var row = {rid:"T1"};
	equal("T1", grid.getOpenStateString(row));
});
test("testGetOpenStateStringMultipleParents",function(){
	var row = {rid:"T3", parent:{rid:"T2",parent:{rid:"T1"}}};
	equal("T1|T2|T3", grid.getOpenStateString(row));
});