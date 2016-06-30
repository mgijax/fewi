(function() {
	'use strict';
	angular.module('hmdc.search').controller('GeneController', GeneController);

	function GeneController($rootScope, $scope, $log, Search, ngDialog) {
		var vm = $scope.vm = {};

		vm.resetGeneTable = false;

		$scope.openGeneSource = function() {
			ngDialog.open({ template: 'GeneSource' });
		}

		function filterMethod() {
			var localFilteredResults = [];
			if($rootScope.filteredGenes) {
				for(var key in vm.results) {
					if($rootScope.filteredGenes.indexOf(vm.results[key].symbol) > -1) {
						localFilteredResults.push(vm.results[key]);
					}
				}
				vm.filteredResults = localFilteredResults;
			} else {
				vm.filteredResults = vm.results;
			}
			$scope.$parent.$parent.tab.count = vm.filteredResults.length;
		}

		$rootScope.$on("GridFilterFinished", filterMethod);

		$rootScope.$on("CallSearchMethod", function(event, data) {
			vm.model = data;
			vm.loading = true;

			Search.geneQuery(vm.model).
				then(function(response) {
					vm.results = response.data;
					vm.resetGeneTable = true;
					vm.loading = false;
					filterMethod();
				}, function (error) {
					vm.errorMessage = error;
			});
		});
	}
})();
