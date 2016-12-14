(function() {
	'use strict';
	angular.module('hmdc.search')
		.filter('stripDo',function() {
			return function(input) {
				return input.replace("DOID:","");
		}})
})();
