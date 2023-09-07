(function() {
	'use strict';
	angular.module('hmdc.services')
		.factory('AutoComplete', AutoCompleteService);

	function AutoCompleteService($rootScope, $http, FEWI_URL) {
		var urlBase = FEWI_URL+'autocomplete';

		return {
			vocabTerm: function(formData) {
				return $http.get(urlBase + '/hmdcTermAC', {
					params: {
						query: formData
					}
				}).then(function(response) {
					return response.data;
				});
			}
		};
	}

})();
