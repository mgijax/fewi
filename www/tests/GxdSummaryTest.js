/*
* Tests any functions unique to the GXD summary
*/

module( "Tissue Matrix Methods", {
  setup: function() {
    // prepare something for all following tests
  },
  teardown: function() {
    // clean up after each test
  }
});


test("testResolveGridColorClassOnlyDetectedBin1",function(){
	equal("blue1",resolveGridColorClass({val:"",detected:1,notDetected:0,ambiguous:0,children:0}));
	equal("blue1",resolveGridColorClass({val:"",detected:4,notDetected:0,ambiguous:0,children:0}));
});
test("testResolveGridColorClassOnlyDetectedBin2",function(){
	equal("blue2",resolveGridColorClass({val:"",detected:5,notDetected:0,ambiguous:0,children:0}));
	equal("blue2",resolveGridColorClass({val:"",detected:50,notDetected:0,ambiguous:0,children:0}));
});
test("testResolveGridColorClassOnlyDetectedBin3",function(){
	equal("blue3",resolveGridColorClass({val:"",detected:51,notDetected:0,ambiguous:0,children:0}));
});

test("testResolveGridColorClassOnlyNotDetectedBin1",function(){
	equal("red1",resolveGridColorClass({val:"",detected:0,notDetected:1,ambiguous:0,children:0}));
	equal("red1",resolveGridColorClass({val:"",detected:0,notDetected:4,ambiguous:0,children:0}));
});
test("testResolveGridColorClassOnlyNotDetectedBin2",function(){
	equal("red2",resolveGridColorClass({val:"",detected:0,notDetected:5,ambiguous:0,children:0}));
	equal("red2",resolveGridColorClass({val:"",detected:0,notDetected:20,ambiguous:0,children:0}));
});
test("testResolveGridColorClassOnlyNotDetectedBin3",function(){
	equal("red3",resolveGridColorClass({val:"",detected:0,notDetected:21,ambiguous:0,children:0}));
});

test("testResolveGridColorClassOnlyAmbiguous",function(){
	equal("gray",resolveGridColorClass({val:"",detected:0,notDetected:0,ambiguous:1,children:0}));
});

test("testResolveGridColorClassOnlyNonYesChildren",function(){
	equal("gold",resolveGridColorClass({val:"",detected:0,notDetected:0,ambiguous:0,children:1}));
});

test("testResolveGridColorClassDetectedTrumpsAll",function(){
	equal("blue1",resolveGridColorClass({val:"",detected:1,notDetected:10,ambiguous:10,children:10}));
});

test("testResolveGridColorClassAmbiguousTrumpsNotDetected",function(){
	equal("gray",resolveGridColorClass({val:"",detected:0,notDetected:10,ambiguous:1,children:0}));
});

test("testResolveGridColorClassNotDetectedTrumpsChildren",function(){
	equal("red1",resolveGridColorClass({val:"",detected:0,notDetected:1,ambiguous:0,children:10}));
});

test("testResolveGridColorClassAmbiguousTrumpsChildren",function(){
	equal("gray",resolveGridColorClass({val:"",detected:0,notDetected:0,ambiguous:1,children:10}));
});

test("testIndicateNegativeMatrixResultsConflictPositive", function(){
	equal(true, indicateNegativeMatrixResultsConflict({val:"",detected:1,notDetected:1,ambiguous:0,children:0}));
});
test("testIndicateNegativeMatrixResultsConflictAmbiguous", function(){
	equal(false, indicateNegativeMatrixResultsConflict({val:"",detected:0,notDetected:1,ambiguous:1,children:0}));
});
test("testIndicateNegativeMatrixResultsConflictPositiveButNoNegative", function(){
	equal(false, indicateNegativeMatrixResultsConflict({val:"",detected:1,notDetected:0,ambiguous:0,children:0}));
});
test("testIndicateNegativeMatrixResultsConflictEverythingButPositive", function(){
	equal(false, indicateNegativeMatrixResultsConflict({val:"",detected:0,notDetected:1,ambiguous:10,children:10}));
});
test("testIndicateNegativeMatrixResultsConflictEverythingOnlyNegative", function(){
	equal(false, indicateNegativeMatrixResultsConflict({val:"",detected:0,notDetected:1,ambiguous:0,children:0}));
});