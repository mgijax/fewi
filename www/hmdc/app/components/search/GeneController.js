(function() {
	'use strict';
	angular.module('hmdc.search').controller('GeneController', GeneController);

	function GeneController($rootScope, $scope, $log, Search, ngDialog) {
		var vm = $scope.vm = {};

		vm.selectedGenes = $rootScope.selectedGenes = [];

		vm.resetGeneTable = false;

		$scope.openGeneSource = function() {
			ngDialog.open({ template: 'GeneSource' });
		}

		$rootScope.$on("CallSearchMethod", function(event, data) {
			vm.model = data;
			vm.loading = true;

			Search.geneQuery(vm.model).
				then(function(response) {
					vm.results = response.data;
					$scope.$parent.$parent.tab.count = vm.results.length;

					vm.selectedGenes = [];
					for (var j = 0; j < vm.results.length; j++) {
						vm.selectedGenes.push({header: vm.results[j].symbol});
					}
					$rootScope.showGeneList = true;

					$rootScope.hideQueryForm = true;
					vm.resetGeneTable = true;
					vm.loading = false;
				}, function (error) {
					vm.errorMessage = error;
			});
		});


	}

})();
