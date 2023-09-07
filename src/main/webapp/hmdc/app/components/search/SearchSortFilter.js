(function() {
	'use strict';
	angular.module('hmdc.search')
		.filter('sortFilter',function(naturalSortService) {
			return function(array, predicteObject, reverse) {
				var neg = "";
				if(reverse) {
					neg = "-";
				}
				array.sort(naturalSortService.naturalSort(neg + predicteObject));
				return array;
		}})
})();
