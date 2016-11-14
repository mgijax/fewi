(function() {
	'use strict';
	angular.module('hmdc.services')
		.service('Search', SearchService);

	function SearchService($rootScope, $http, FEWI_URL) {
		var urlBase = FEWI_URL+'diseasePortal';

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
