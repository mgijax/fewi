(function() {
	'use strict';
	angular.module('hmdc.search')
		.filter('stripOmim',function() {
			return function(input) {
				return input.replace("OMIM:","");
		}})
})();
