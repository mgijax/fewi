/**
* Tests of SuperGrid Util methods
*/

var scrollDivId = "testScrollDivId";
var scrollDivJQ;
module( "SuperGrid Util Scroll Methods", {
  setup: function() {
    // prepare something for all following tests
	// insert a div that has a scroll bar
	$("body").append("<div id=\""+scrollDivId+"\" "
			+"style=\"height:100px;width:100px;overflow:scroll;\">"
				+"<div style=\"height:600px;width:600px;\"></div>"
			+"</div>");
	scrollDivJQ = $("#"+scrollDivId);
  },
  teardown: function() {
    // clean up after each test
	// remove any elements inserted during test setup
	  scrollDivJQ.remove();
  }
});

// helper functions
function scrollToBottom(jqElement)
{
	jqElement.scrollTop(jqElement[0].scrollHeight);
}
function scrollToRight(jqElement)
{
	jqElement.scrollLeft(jqElement[0].scrollWidth);
}

test( "testIsElementScrolledBottomNo", function() {
	  equal(false, SGUtil.isElementScrolledBottom(scrollDivJQ[0]) );
});

test( "testIsElementScrolledRightNo", function() {
	  equal(false, SGUtil.isElementScrolledRight(scrollDivJQ[0]) );
});

test( "testIsElementScrolledBottomYes", function() {
	// scroll element down all the way
	scrollToBottom(scrollDivJQ);	
	equal(true, SGUtil.isElementScrolledBottom(scrollDivJQ[0]) );
});

test( "testIsElementScrolledRightYes", function() {
	// scroll element right all the way
	scrollToRight(scrollDivJQ);	
	equal(true, SGUtil.isElementScrolledRight(scrollDivJQ[0]) );
});

test( "testIsElementScrolledBottomYesRightNo", function() {
	// scroll element down all the way
	scrollToBottom(scrollDivJQ);	
	equal(false, SGUtil.isElementScrolledRight(scrollDivJQ[0]) );
});

test( "testIsElementScrolledRightYesBottomNo", function() {
	// scroll element right all the way
	scrollToRight(scrollDivJQ);	
	equal(false, SGUtil.isElementScrolledBottom(scrollDivJQ[0]) );
});

test( "testIsElementScrolledBoth", function() {
	// scroll element down all the way
	scrollToBottom(scrollDivJQ);
	scrollToRight(scrollDivJQ);	
	equal(true, SGUtil.isElementScrolledBottom(scrollDivJQ[0]) );
	equal(true, SGUtil.isElementScrolledRight(scrollDivJQ[0]) );
});

module( "SuperGrid Util Attribute Manipulation Methods", {});

test( "testSwapClassEmpty", function() {
	equal("",SGUtil.swapClass("","",""));
});

test( "testSwapClassSingleClass", function() {
	equal("after",SGUtil.swapClass("before","before","after"));
});

test( "testSwapClassMultipleClassesBefore", function() {
	equal("b1 b2 after",SGUtil.swapClass("b1 b2 before","before","after"));
});

test( "testSwapClassMultipleClassesAfter", function() {
	equal("a1 a2 after",SGUtil.swapClass("before a1 a2","before","after"));
});

test( "testSwapClassDuplicates", function() {
	equal("test after",SGUtil.swapClass("test before before","before","after"));
});

test("testTranslateValuesEmpty", function() {
	deepEqual([0,0],SGUtil.getTranslateValues(""));
});

test("testTranslateValuesXOnly", function() {
	deepEqual([1.5,0],SGUtil.getTranslateValues("translate(1.5)"));
});

test("testTranslateValuesYOnly", function() {
	deepEqual([0,12.5],SGUtil.getTranslateValues("translate(0,12.5)"));
});

test("testTranslateValuesBothXY", function() {
	deepEqual([110.10,215.15],SGUtil.getTranslateValues("translate(110.10,215.15)"));
});

test("testTranslateValuesBothXYSpaces", function() {
	deepEqual([110.10,215.15],SGUtil.getTranslateValues("translate(110.10 215.15)"));
});

test("testChangeRotateOriginY", function() { 
	equal("translate(0,10) rotate(20,45,89) test", SGUtil.changeRotateOriginY("translate(0,10) rotate(20,45,45) test",89));
});

test("testChangeRotateOriginYSpaces", function() { 
	equal("translate(0 10) rotate(20,45,89) test", SGUtil.changeRotateOriginY("translate(0 10) rotate(20 45 45) test",89));
});
