(function() {
	'use strict';
	angular.module('hmdc.search')
		.filter('joinBy', function () { return function (input,delimiter) { return (input || []).join(delimiter || ', '); }; })
})();
