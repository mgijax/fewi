/**
* Example of how Qunit test suites work
*/

module( "test module A (could be anything)", {
  setup: function() {
    // prepare something for all following tests
	// maybe inserting elements in the DOM
  },
  teardown: function() {
    // clean up after each test
	// remove any elements inserted during test setup
  }
});

test( "a basic test example", function() {
	  ok( true, "this test is fine" );
	  equal(1,1,"test equality");
});