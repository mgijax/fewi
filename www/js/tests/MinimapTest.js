/**
* Test the minimap.js module for generating marker minimaps
*/

module( "Marker Minimap", {
  setup: function() {
    // prepare something for all following tests
	// maybe inserting elements in the DOM
	 Minimap._init();
	 
  },
  teardown: function() {
    // clean up after each test
	// remove any elements inserted during test setup
  }
});

test( "existence of Minimap module", function() {
	ok( window.Minimap, "Minimap is not available" );
});

test( "_assignCells basic", function() {
	var data = [
	  {'anchorSymbol':'test1', 'cmOffset':7.0},
	  {'anchorSymbol':'test2', 'cmOffset':14.0},
	  {'anchorSymbol':'test3', 'cmOffset':21.0}
	]

	Minimap._calcMiniMapDimensions(data, 140.0);
	Minimap._assignCells(data);

	// cells get spaced about 7cms each
	equal(Minimap.cells.length, 20);
	equal(Minimap.cells[1].anchorSymbol, 'test1');
	equal(Minimap.cells[2].anchorSymbol, 'test2');
	equal(Minimap.cells[3].anchorSymbol, 'test3');
});

test( "_assignCells Kit", function() {
	// Real example from Kit
	var data = [
	    {"anchorSymbol":"Abcb1b","cmOffset":3.43,"maxCmOffset":90.18,"primary":false},
		{"anchorSymbol":"Htr5a","cmOffset":13.24,"maxCmOffset":90.18,"primary":false},
		{"anchorSymbol":"Adra2c","cmOffset":18.09,"maxCmOffset":90.18,"primary":false},
		{"anchorSymbol":"Pgm1","cmOffset":32.8,"maxCmOffset":90.18,"primary":false},
		{"anchorSymbol":"Kit","cmOffset":39.55,"maxCmOffset":90.18,"primary":true},
		{"anchorSymbol":"Alb","cmOffset":44.7,"maxCmOffset":90.18,"primary":false},
		{"anchorSymbol":"Fgf5","cmOffset":47.77,"maxCmOffset":90.18,"primary":false},
		{"anchorSymbol":"Pde6b","cmOffset":53.07,"maxCmOffset":90.18,"primary":false},
		{"anchorSymbol":"Acads","cmOffset":55.99,"maxCmOffset":90.18,"primary":false},
		{"anchorSymbol":"Gusb","cmOffset":68.32,"maxCmOffset":90.18,"primary":false},
		{"anchorSymbol":"Epo","cmOffset":76.5,"maxCmOffset":90.18,"primary":false},
		{"anchorSymbol":"Actb","cmOffset":81.8,"maxCmOffset":90.18,"primary":false}
	]


	Minimap._calcMiniMapDimensions(data, 90.18);
	Minimap._assignCells(data);

	// cells get spaced about 7cms each
	equal(Minimap.cells.length, 13);
	equal(Minimap.cells[0].anchorSymbol, 'Abcb1b');
	equal(Minimap.cells[1].anchorSymbol, 'Htr5a');
	equal(Minimap.cells[2].anchorSymbol, 'Adra2c');
	equal(Minimap.cells[4].anchorSymbol, 'Pgm1');
	equal(Minimap.cells[5].anchorSymbol, 'Kit');
	equal(Minimap.cells[6].anchorSymbol, 'Alb');
	equal(Minimap.cells[7].anchorSymbol, 'Fgf5');
	equal(Minimap.cells[8].anchorSymbol, 'Pde6b');
	equal(Minimap.cells[9].anchorSymbol, 'Acads');
	equal(Minimap.cells[10].anchorSymbol, 'Gusb');
	equal(Minimap.cells[11].anchorSymbol, 'Epo');
	equal(Minimap.cells[12].anchorSymbol, 'Actb');
});