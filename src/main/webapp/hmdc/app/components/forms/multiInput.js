(function() {
  'use strict';
  angular.module('hmdc.config')
    .config(multiInputConfig);

  // @ngInject
  function multiInputConfig(formlyConfigProvider) {
    formlyConfigProvider.setType({
      name: 'multiInput',
      templateUrl: 'search/forms/multiInput.tpl.html',
      defaultOptions: {
        noFormControl: true,
        wrapper: ['horizontalBootstrapHelp', 'bootstrapHasError'],
        templateOptions: {
          inputOptions: {
            // wrapper: null
          }
        }
      },
      controller: /* @ngInject */ function($scope) {
        $scope.copyItemOptions = copyItemOptions;
        $scope.deleteItem = deleteItem;
        $scope.addItem = addItem;

        function deleteItem(model, index) {
          model.splice(index,1);
        }

        function addItem(model, index) {
          model.splice(index+1, 0, '');
        }

        function copyItemOptions() {
          return angular.copy($scope.to.inputOptions);
        }
      }
    });
  }

})();
