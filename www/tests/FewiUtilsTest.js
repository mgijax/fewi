
/*
 * SortSmartAlpha
 */
test("testSortSmartAlpha", function() {
	var arr = ["t10","t2","102b","10b"];
	arr.sort(FewiUtil.SortSmartAlpha);
	deepEqual(["10b","102b","t2","t10"],arr);
});

test("testSortSmartAlphaCaseInsensitive", function() {
	var arr = ["Max1","Max3","max2"];
	arr.sort(FewiUtil.SortSmartAlpha);
	deepEqual(["Max1","max2","Max3"],arr);
});

/*
* uniqueValues
*/
test("testUniqueValuesAlreadyUnique", function(){
	var arr = [1,2,3,4];
	deepEqual([1,2,3,4],FewiUtil.uniqueValues(arr));
});
test("testUniqueValuesFromNumericArray", function(){
	var arr = [1,2,3,4,4,4,4,4,5];
	deepEqual([1,2,3,4,5],FewiUtil.uniqueValues(arr));
});
test("testUniqueValuesFromStringArray", function(){
	var arr = ["test","test","test","test2"];
	deepEqual(["test","test2"],FewiUtil.uniqueValues(arr));
});