(function() {
	'use strict';
	angular.module('hmdc.search')
		.filter('handleSubscript', function () { return function (input) { return input.replace(/<([^>]*)>/g, "<sup>$1</sup>"); }; })
})();
