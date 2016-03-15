(function() {
  'use strict';
  angular.module('civicClient', [
    // vendor modules
    'formly',

    'civic.config',
    'civic.services',
    'civic.search',
  ])
    .run(appRun);

// @ngInject
  function appRun($rootScope, $state, $location) {
    $rootScope.view = {};
    $rootScope.$state = $state;
  }

// define root modules & dependencies
  angular.module('civic.config', ['formly', 'formlyBootstrap']);
  angular.module('civic.services', ['ngResource']);
  angular.module('civic.search', ['ui.router', 'formly', 'formlyBootstrap']);


})();
