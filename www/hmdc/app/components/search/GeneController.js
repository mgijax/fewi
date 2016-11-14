(function() {
	'use strict';
	angular.module('hmdc.search').controller('GeneController', GeneController);

	function GeneController($rootScope, $scope, $log, Search, ngDialog, FEWI_URL, WEBSHARE_URL) {
		var vm = $scope.vm = {};
		$scope.FEWI_URL = FEWI_URL;
		$scope.WEBSHARE_URL = WEBSHARE_URL;

		vm.resetGeneTable = false;

		$scope.openGeneSource = function() {
			ngDialog.open({ template: 'GeneSource' });
		}

		function filterMethod() {
			var localFilteredResults = [];

			if($rootScope.filteredGenes && $rootScope.filteredGenes.length > 0) {
				for(var key in vm.results) {
					if($rootScope.filteredGenes.indexOf(vm.results[key].symbol) > -1) {
						localFilteredResults.push(vm.results[key]);
					}
				}
				vm.filteredResults = localFilteredResults;
			} else if (($rootScope.selectedGenesModel !== undefined) && (($rootScope.selectedGenesModel.length > 0) || 
					($rootScope.selectedPhenoTypesAndDiseasesModel !== undefined) && ($rootScope.selectedPhenoTypesAndDiseasesModel.length > 0))) {
				// user chose filter combination that left us with no genes on the grid
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

			Search.geneQuery(vm.model).
				then(function(response) {
					vm.results = response.data;
					vm.resetGeneTable = true;
					vm.loading = false;
					filterMethod();
					vm.removeFilters();
				}, function (error) {
					vm.errorMessage = error;
			});
		});
	}
})();
