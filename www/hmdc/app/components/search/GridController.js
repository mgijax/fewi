(function() {
	'use strict';
	angular.module('hmdc.search').controller('GridController', GridController);

	function GridController($rootScope, $scope, $log, Search, naturalSortService, $timeout) {
		var vm = $scope.vm = {};

		vm.mustHideLegend = true;

		vm.greyBar = "greyBar";
		vm.displayNone = "displayNone";

		$scope.formatCell = function(value, row, col, formattedValue) {
			if(value && value.normalCount > 0 && value.normalCount == value.annotCount) {
				if(value.humanAnnotCount > 0) {
					return "<div title=\"" + value.title + "\" class=\"normal normaloffset\">N</div>";
				} else {
					ret = "<div title=\"" + value.title + "\" class=\"normal\">N</div>";
					formattedValue = ret;
					return ret;
				}
			} else if(value) {
				return "<div title=\"" + value.title + "\">&nbsp;</div>";
			}
		}

		$scope.styleCell = function(value, row, col) {
			if(value) {
				var hac = "#F7861D";
				if(value.humanAnnotCount < 100) hac = "#F4A041";
				if(value.humanAnnotCount < 6) hac = "#F2BF79";
				if(value.humanAnnotCount < 2) hac = "#FBDBB4";
				if(value.humanAnnotCount < 1) hac = "";

				var ac = "#0C2255";
				if(value.annotCount < 100) ac = "#49648B";
				if(value.annotCount < 6) ac = "#879EBA";
				if(value.annotCount < 2) ac = "#C6D6E8";
				if(value.annotCount < 1) ac = "";

				if(hac && ac) {
					return "cursor: pointer; background: linear-gradient(135deg, " + hac + " 50%, rgb(0,0,0) 0%, " + ac + " 50%);";
				}
				if(hac) {
					return "cursor: pointer; background: linear-gradient(135deg, " + hac +  " 0%, " + hac + " 0%, " + hac + " 0%);";
				}
				if(ac) {
					return "cursor: pointer; background: linear-gradient(135deg, " + ac + " 100%, " + ac + " 0%, " + ac + " 0%);";
				}
			}
		}

		$scope.customHTMLHeader = function(value, row, col, formattedValue) {
			if(value) {
				var key = Object.keys(value)[0];
				if(key) {
					if(value[key] != -1) {
						return "<span title=\"" + key + "\"><mark>" + key + "</mark></span>";
					} else {
						return "<span title=\"" + key + "\">" + key + "</span>";
					}
				} else {
					return "";
				}
			} else {
				return "";
			}
		}

		$scope.popup = function(url) {
			var child = window.open (url, "popup_window", 'width=800,height=600,resizable=yes,scrollbars=yes,alwaysRaised=yes');
			child.focus();
		}

		$scope.handleClick = function(event, cellData) {
			console.log(cellData.data);
			var url;
			if(cellData.data && cellData.data["phenoHeader"]) {
				url = "/diseasePortal/phenotypePopup?gridClusterKey=" + cellData.data["gridClusterKey"] + "&header=" + cellData.data["phenoHeader"];
			}
			if(cellData.data && cellData.data["diseaseHeader"]) {
				url = "/diseasePortal/diseasePopup?gridClusterKey=" + cellData.data["gridClusterKey"] + "&header=" + cellData.data["diseaseHeader"];
			}

			if(url) {
				var windowName = "popup_" + cellData.data["gridClusterKey"] + "_" + cellData.data["header"];
				var child = window.open (url, windowName, 'width=800,height=600,resizable=yes,scrollbars=yes,alwaysRaised=yes');
				child.focus();
			}
		}

		$rootScope.$on("FilterChanged", function(event) {
			if(vm.gridloading) return;
			vm.gridloading = true;
			console.log("Filter Changed");

			buildGridJson();
			$timeout(function () { vm.gridloading = false; }, 1);
		});

		$rootScope.$on("CallSearchMethod", function(event, data) {
			vm.model = data;

			vm.gridloading = true;

			Search.gridQuery(vm.model).
				then(function(response) {

					vm.jsonData = response.data;

					$rootScope.selectedPhenoTypes = [];
					for (var j = 0; j < vm.jsonData.gridMPHeaders.length; j++) {
						$rootScope.selectedPhenoTypes.push({id: vm.jsonData.gridMPHeaders[j], label: vm.jsonData.gridMPHeaders[j]});
					}
					$rootScope.selectedPhenoTypes.sort(naturalSortService.naturalSortFunction);

					$rootScope.selectedDiseases = [];
					for (var j = 0; j < vm.jsonData.gridOMIMHeaders.length; j++) {
						$rootScope.selectedDiseases.push({id: vm.jsonData.gridOMIMHeaders[j], label: vm.jsonData.gridOMIMHeaders[j]});
					}
					$rootScope.selectedDiseases.sort(naturalSortService.naturalSortFunction);

					buildGridJson();
					vm.gridloading = false;

					$rootScope.hideQueryForm = true;
				}, function (error) {
					vm.errorMessage = error;
			});

		});

		function buildGridJson() {

			var newResults = {};
			var data = [];

			vm.OMIMHeaderCount = 0;
			vm.MPHeaderCount = 0;

			var headerContent = [];
			headerContent.push("Human Gene");
			headerContent.push("Mouse Gene");
			vm.solrQuery = vm.jsonData.filterQuery;
			
			var i = vm.jsonData.gridMPHeaders.indexOf("normal phenotype");
			if(i != -1) vm.jsonData.gridMPHeaders.splice(i, 1);
			vm.jsonData.gridMPHeaders.sort(naturalSortService.naturalSortFunction)
			if(i != -1) vm.jsonData.gridMPHeaders.push("normal phenotype");

			vm.jsonData.gridOMIMHeaders.sort(naturalSortService.naturalSortFunction)

			var selectedPhenoTypes = [];
			for(i = 0; i < $rootScope.selectedPhenoTypesModel.length; i++) {
				selectedPhenoTypes.push($rootScope.selectedPhenoTypesModel[i].id);
			}

			// Push the MP Headers into the headerContent row
			var hash = {};
			for(var header in vm.jsonData.gridMPHeaders) {
				header = vm.jsonData.gridMPHeaders[header];

				if(selectedPhenoTypes.length == 0 || (selectedPhenoTypes.length > 0 && selectedPhenoTypes.indexOf(header) > -1)) {
					if(vm.jsonData.gridHighLights) {
						hash[header] = vm.jsonData.gridHighLights.indexOf(header);
					} else {
						hash[header] = -1;
					}
					vm.MPHeaderCount++;
					headerContent.push(hash);
					hash = {};
				}
			}

			headerContent.push({});


			var selectedDiseases = [];
			for(i = 0; i < $rootScope.selectedDiseasesModel.length; i++) {
				selectedDiseases.push($rootScope.selectedDiseasesModel[i].id);
			}

			// Push the OMIM Headers into the headerContent row
			for(var header in vm.jsonData.gridOMIMHeaders) {
				header = vm.jsonData.gridOMIMHeaders[header];
				if(selectedDiseases.length == 0 || (selectedDiseases.length > 0 && selectedDiseases.indexOf(header) > -1)) {
					if(vm.jsonData.gridHighLights) {
						hash[header] = vm.jsonData.gridHighLights.indexOf(header);
					} else {
						hash[header] = -1;
					}
					vm.OMIMHeaderCount++;
					headerContent.push(hash);
					hash = {};
				}
			}

			// Push 7 blank columns at the end so the ending names can be read
			for(var i = 0; i < 7; i++) { headerContent.push([]); }
			data.push(headerContent);

			for(var key in vm.jsonData.gridRows) {
				key = vm.jsonData.gridRows[key];
				var rowContent = [];
				var rowSymbols = [];

				var humanSymbolString = [];
				for(var human in key.gridCluster.humanSymbols) {
					var h = key.gridCluster.humanSymbols[human];
					var temp = "";
					if(key.gridCluster.homologyClusterKey) {
						temp = "<a target=\"_blank\" href=\"/homology/cluster/key/" + key.gridCluster.homologyClusterKey + "\" title=\"Name: " + h.name + "\">";
						temp += h.symbol.replace(/<([^>]*)>/g, "<sup>$1</sup>");
						temp += "</a>";
					} else {
						temp = h.symbol.replace(/<([^>]*)>/g, "<sup>$1</sup>");
						temp = "<span title=\"Name: " + h.name + "\">" + temp + "</span>";
					}
					rowSymbols.push(h.symbol);
					humanSymbolString.push(temp);
				}
				rowContent.push(humanSymbolString.join(", "));

				var markerSymbolString = [];
				for(var marker in key.gridCluster.mouseSymbols) {
					var m = key.gridCluster.mouseSymbols[marker];
					var temp = "<a target=\"_blank\" href=\"/marker/" + m.primaryID + "\" title=\"Name: " + m.name + "\nFeature Type: " + m.featureType + "\">";
					temp += m.symbol.replace(/<([^>]*)>/g, "<sup>$1</sup>");
					temp += "</a>";
					markerSymbolString.push(temp);
					rowSymbols.push(m.symbol);
				}
				rowContent.push(markerSymbolString.join(", "));

				var hasData = false;
				for(var header in vm.jsonData.gridMPHeaders) {
					header = vm.jsonData.gridMPHeaders[header];
					if(selectedPhenoTypes.length == 0 || (selectedPhenoTypes.length > 0 && selectedPhenoTypes.indexOf(header) > -1)) {
						if(key.mpHeaderCells[header]) {
							key.mpHeaderCells[header]["phenoHeader"] = header;
							key.mpHeaderCells[header]["title"] = "Gene(s): " + rowSymbols.join(", ") + "\nPhenotype: " + header + "\nClick to see " + (key.mpHeaderCells[header].annotCount + key.mpHeaderCells[header].humanAnnotCount) + " annotations";
							hasData = true;
						}
						rowContent.push(key.mpHeaderCells[header]);
					}
				}
				rowContent.push({normalCount: 0, annotCount: 0, humanAnnotCount: 0});
				for(var header in vm.jsonData.gridOMIMHeaders) {
					header = vm.jsonData.gridOMIMHeaders[header];
					if(selectedDiseases.length == 0 || (selectedDiseases.length > 0 && selectedDiseases.indexOf(header) > -1)) {
						if(key.diseaseCells[header]) {
							key.diseaseCells[header]["diseaseHeader"] = header;
							key.diseaseCells[header]["title"] = "Gene(s): " + rowSymbols.join(", ") + "\nDisease: " + header + "\nClick to see " + (key.diseaseCells[header].annotCount + key.diseaseCells[header].humanAnnotCount) + " annotations";
							hasData = true;
						}
						rowContent.push(key.diseaseCells[header]);
					}
				}
				// The the row does not have data skip it
				if(hasData) {
					data.push(rowContent);
				}
			}
			// Push the final footer row
			data.push([]);

			newResults.totalcolcount = vm.OMIMHeaderCount + vm.MPHeaderCount;
			newResults.totalrowcount = vm.jsonData.gridRows.length;

			newResults.maxcols = Math.min(vm.windowmaxcols, headerContent.length);
			newResults.maxrows = Math.min(vm.windowmaxrows, data.length - 2);

			newResults.greyBar = vm.MPHeaderCount + 2

			$scope.$parent.$parent.tab.count = vm.jsonData.gridRows.length + " x " + newResults.totalcolcount;

			newResults.data = data;

			vm.results = newResults;

		}

		/*vm.results.grid.totalrowcount
		vm.results.grid.totalrowcount
		vm.results.grid.totalcolcount

		vm.maxcols
		vm.maxrows

		vm.results.grid.data
		vm.results.grid.greyBar

		vm.gridloading
		*/

	}

})();
