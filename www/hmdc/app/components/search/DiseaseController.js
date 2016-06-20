(function() {
	'use strict';
	angular.module('hmdc.search').controller('DiseaseController', DiseaseController);

	function DiseaseController($rootScope, $scope, $log, Search, ngDialog) {
		var vm = $scope.vm = {};

		vm.resetDiseaseTable = false;

		$scope.openDiseaseSource = function() {
			ngDialog.open({ template: 'DiseaseSource' });
		}

		$rootScope.$on("CallSearchMethod", function(event, data) {
			vm.model = data;

			vm.loading = true;

			Search.diseaseQuery(vm.model).
				then(function(response) {
					vm.results = response.data;
					$scope.$parent.$parent.tab.count = vm.results.length;

					$rootScope.hideQueryForm = true;
					vm.resetDiseaseTable = true;
					vm.loading = false;
				}, function (error) {
					vm.errorMessage = error;
			});

		});
	}
})();
