(function() {
	'use strict';
	angular.module('hmdc.search').controller('DiseaseController', DiseaseController);

	function DiseaseController($rootScope, $scope, $log, Search, ngDialog, FEWI_URL, WEBSHARE_URL) {
		var vm = $scope.vm = {};
		$scope.FEWI_URL = FEWI_URL;
		$scope.WEBSHARE_URL = WEBSHARE_URL;

		vm.resetDiseaseTable = false;
		vm.filteredResults = [];

		$scope.openDiseaseSource = function() {
			ngDialog.open({ template: 'DiseaseSource' });
		}

		// Filter the disease tab.
		function filterMethod() {
			var localFilteredResults = [];
			if($rootScope.filteredDiseases && $rootScope.filteredDiseases.length > 0) {
				for(var key in vm.results) {
					if($rootScope.filteredDiseases.indexOf(vm.results[key].term) > -1) {
						localFilteredResults.push(vm.results[key]);
					}
				}
				vm.filteredResults = localFilteredResults;
			} else if (($rootScope.selectedGenesModel !== undefined) && (($rootScope.selectedGenesModel.length > 0) || 
					($rootScope.selectedPhenoTypesAndDiseasesModel !== undefined) && ($rootScope.selectedPhenoTypesAndDiseasesModel.length > 0))) {
				// user chose filter combination that left us with no diseases on the grid
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
			$rootScope.selectedFeatureTypesModel = [];
			$rootScope.$emit("FilterChanged");
		}

		$rootScope.$on("CallSearchMethod", function(event, data) {
			vm.model = data;
			console.log("DiseaseController.CallSearchMethod");
			console.log(data);

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
