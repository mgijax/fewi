(function() {
  'use strict';
  angular.module('civicClient', [
    // vendor modules
    'formly',

    'civic.config',
    'civic.services',
    'civic.search',
  ]);

// define root modules & dependencies
  angular.module('civic.config', ['formly', 'formlyBootstrap']);
  angular.module('civic.services', ['ngResource']);
  angular.module('civic.search', ['ngRoute', 'formly', 'formlyBootstrap', 'ui.bootstrap', 'angular-table', 'ngDialog']);


})();
