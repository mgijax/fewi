(function() {
  'use strict';
  angular.module('civicClient', [
    // vendor modules
    'civic.config',
    'civic.services',
    'civic.search',
    'civic.style',
  ]);

// define root modules & dependencies
  angular.module('civic.config', ['formly', 'formlyBootstrap', 'ui.bootstrap']);
  angular.module('civic.services', ['ngResource']);
  angular.module('civic.search', ['ngRoute', 'formly', 'formlyBootstrap', 'ui.bootstrap', 'ngDialog', 'smart-table', 'ngcTableDirective', 'naturalSort']);
  angular.module('civic.style', ['ngRoute', 'formly', 'formlyBootstrap', 'ui.bootstrap', 'ngDialog', 'smart-table']);

})();
