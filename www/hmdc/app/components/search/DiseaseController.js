(function() {
	'use strict';
	angular.module('hmdc.search').controller('DiseaseController', DiseaseController);

	function DiseaseController($rootScope, $scope, $log, Search, ngDialog) {
		var vm = $scope.vm = {};

		vm.resetDiseaseTable = false;
		vm.filteredResults = [];

		$scope.openDiseaseSource = function() {
			ngDialog.open({ template: 'DiseaseSource' });
		}

		function filterMethod() {
			var localFilteredResults = [];
			if($rootScope.filteredDiseases && $rootScope.filteredDiseases.length > 0) {
				for(var key in vm.results) {
					if($rootScope.filteredDiseases.indexOf(vm.results[key].term) > -1) {
						localFilteredResults.push(vm.results[key]);
					}
				}
				vm.filteredResults = localFilteredResults;
			} else if ($rootScope.filteredGenes && $rootScope.filteredGenes.length > 0) {
				// If we get here, the user filtered by genes with no diseases, which means we should
				// show no diseases rather than the (default) full set.
				vm.filteredResults = localFilteredResults;
			} else {
				vm.filteredResults = vm.results;
			}
			$scope.$parent.$parent.tab.count = vm.filteredResults.length;
		}

		$rootScope.$on("GridFilterFinished", filterMethod);

		vm.removeFilters = function() {
			$rootScope.selectedPhenoTypesAndDiseasesModel = [];
			$rootScope.selectedGenesModel = [];
			$rootScope.$emit("FilterChanged");
		}

		$rootScope.$on("CallSearchMethod", function(event, data) {
			vm.model = data;

			vm.loading = true;

			Search.diseaseQuery(vm.model).
				then(function(response) {
					vm.results = response.data;
					$scope.$parent.$parent.tab.count = vm.results.length;
					vm.resetDiseaseTable = true;
					vm.loading = false;
					filterMethod();
					vm.removeFilters();
				}, function (error) {
					vm.errorMessage = error;
			});

		});
	}
})();
