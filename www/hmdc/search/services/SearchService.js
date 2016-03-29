(function() {
	'use strict';
	angular.module('civic.services')
		.factory('Search', SearchService);

	function SearchService($rootScope, $http) {
		var urlBase = '/diseasePortal';

		return {
			diseaseQuery: function(formData) {
				return $http.post(urlBase + '/diseaseQuery', formData);
			},
			geneQuery: function(formData) {
				return $http.post(urlBase + '/geneQuery', formData);
			},
			gridQuery: function(formData) {
				return $http.post(urlBase + '/gridQuery', formData);
			}
		};
	}

})();
