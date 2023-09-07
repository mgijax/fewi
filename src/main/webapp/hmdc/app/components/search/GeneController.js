(function() {
	'use strict';
	angular.module('hmdc.search').controller('GeneController', GeneController);

	function GeneController($rootScope, $scope, $log, Search, ngDialog, FEWI_URL, WEBSHARE_URL) {
		var vm = $scope.vm = {};
		$scope.FEWI_URL = FEWI_URL;
                $scope.HMDC_REPORT_URL = FEWI_URL + "diseasePortal/marker/report.txt"
		$scope.WEBSHARE_URL = WEBSHARE_URL;

		vm.resetGeneTable = false;

		$scope.openGeneSource = function() {
			ngDialog.open({ template: 'GeneSource' });
		}

		// Filter the genes tab.  If no genes selected from the genes filter (which uses values only drawn from the grid)
		// then show all genes (even those which do not appear on the grid).
		function filterMethod() {
			var localFilteredResults = [];

			// If any filters have values selected, then we need to only show genes that match those selected filters.
			if (
				(($rootScope.selectedGenesModel !== undefined) && ($rootScope.selectedGenesModel.length > 0)) || 
				(($rootScope.selectedFeatureTypesModel !== undefined) && ($rootScope.selectedFeatureTypesModel.length > 0)) ||
				(($rootScope.selectedPhenoTypesAndDiseasesModel !== undefined) && ($rootScope.selectedPhenoTypesAndDiseasesModel.length > 0))
				) {

				// Any genes left after filtering?  If so, show them.  If not, show a blank tab.
				if($rootScope.filteredGenes && $rootScope.filteredGenes.length > 0) {
					for(var key in vm.results) {
						if($rootScope.filteredGenes.indexOf(vm.results[key].symbol) > -1) {
							localFilteredResults.push(vm.results[key]);
						}
					}
					vm.filteredResults = localFilteredResults;
				} else {
					vm.filteredResults = localFilteredResults;
				}
			} else {
				// No filters selected by user, so show all genes.
				vm.filteredResults = vm.results;
			}
			$scope.$parent.$parent.tab.count = vm.filteredResults.length;
		}

		$rootScope.$on("GridFilterFinished", filterMethod);

		// Go through response from geneQuery endpoint and collect feature types for a filter.
		function collectFeatureTypes() {
			var ft = {};
			
			for (var key in vm.results) {
				var row = vm.results[key];
				var symbol = row.symbol;

				if (($rootScope.allGenes === undefined) || ($rootScope.allGenes.indexOf(symbol) >= 0)) {
					var featureTypes = row.filterableFeatureType;
				
					for (var i in featureTypes) {
						var featureType = featureTypes[i];
					
						if (featureType in ft) {
							ft[featureType].push(symbol);
						} else {
							ft[featureType] = [];
							ft[featureType].push(symbol);
						}
					}
				}
			}
				
			// At this point, we have ft as { 'feature type 1' : [ 'symbol 1', 'symbol 2', ... ], 'feature type 2' : ... }
			
			var sft = [];
			for (var featureType in ft) {
				sft.push( { label: featureType, symbols: ft[featureType] } );
			}

			// Sort the options by feature type (alphabetic).
			sft.sort(function(a,b) {
				if (a.label.toLowerCase() < b.label.toLowerCase()) { return -1; }
				else if (a.label.toLowerCase() > b.label.toLowerCase()) { return 1; }
				return 0;
			});

			// Assign IDs now that the options are sorted.
			for (var i = 0; i < sft.length; i++) {
				sft[i].id = i;
			}

			$rootScope.selectedFeatureTypes = sft;		// options for the feature types filter
		}
		
		vm.removeFilters = function() {
			$rootScope.selectedPhenoTypesAndDiseasesModel = [];
			$rootScope.selectedGenesModel = [];
			$rootScope.selectedFeatureTypesModel = [];
			$rootScope.$emit("FilterChanged");
		}

		// Once the Gene and Pheno/Disease filters are built in the GridController, bring the Feature Type
		// filter in sync.
		$rootScope.$on("GridFiltersBuilt", collectFeatureTypes);
		
		$rootScope.$on("CallSearchMethod", function(event, data) {
			vm.model = data;
			vm.loading = true;

			Search.geneQuery(vm.model).
				then(function(response) {
					vm.results = response.data;
					vm.resetGeneTable = true;
					vm.loading = false;
					collectFeatureTypes();
					filterMethod();
					vm.removeFilters();
				}, function (error) {
					vm.errorMessage = error;
			});
		});
	}
})();
