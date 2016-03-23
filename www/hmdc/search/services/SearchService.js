(function() {
  'use strict';
  angular.module('civic.services')
    .factory('SearchResource',SearchResource)
    .factory('Search', SearchService);

  // @ngInject
  function SearchResource($resource) {
    var SearchService = $resource('/diseasePortal/diseases/json?phenotypes=105830&results=250&startIndex=0', {},
		{
        get: { method: 'GET', isArray: false, cache: false },
        post: { method: 'POST', isArray: false, cache: false }
	   }
    );

    return SearchService;
  }

  // @ngInject
  function SearchService(SearchResource) {
    var results = {};

    return {
      results: results,
      post: post,
      get: get,
      reset: reset
    };

    function reset() {
      results = {};
    }

    // @ngInject
    function post(reqObj) {
      return SearchResource.post(reqObj).$promise
        .then(function(response) {
          angular.copy(response, results);
          return response.$promise;
        });
    }

    // @ngInject
    function get(reqObj) {
      return SearchResource.get(reqObj).$promise
        .then(function(response) {
          angular.copy(response, results);
          return response.$promise;
        });
    }

  }

})();
