(function() {
  'use strict';
  angular.module('HMDCApplication', [
    // vendor modules
    'hmdc.config',
    'hmdc.services',
    'hmdc.search',
    'hmdc.style'
  ]).config(function($locationProvider) {
  	$locationProvider.html5Mode(true);
  });

// define root modules & dependencies
  angular.module('hmdc.config', ['formly', 'formlyBootstrap', 'ui.bootstrap']);
  angular.module('hmdc.services', ['ngResource']);
  angular.module('hmdc.search', ['ngRoute', 'formly', 'formlyBootstrap', 'ui.bootstrap', 'ngDialog', 'smart-table', 'ngcTableDirective', 'naturalSortService', 'angularjs-dropdown-multiselect']);
  angular.module('hmdc.style', ['ngRoute', 'formly', 'formlyBootstrap', 'ui.bootstrap', 'ngDialog', 'smart-table']);

})();
