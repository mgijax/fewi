(function() {
	'use strict';
	angular.module('civic.services')
		.factory('AutoComplete', AutoCompleteService);

	function AutoCompleteService($rootScope, $http) {
		var urlBase = '/autocomplete';

		return {
			vocabTerm: function(formData) {
				return $http.get(urlBase + '/hmdcTermAC?query=' + formData);
			}
		};
	}

})();
