(function() {
	'use strict';
	angular.module('hmdc.search').controller('GridController', GridController);

	function GridController($rootScope, $scope, $log, Search, naturalSortService, $timeout, $window, $sce, FEWI_URL) {
		var vm = $scope.vm = {};
		$scope.FEWI_URL = FEWI_URL;

		vm.mustHideLegend = true;

		vm.greyBar = "greyBar";
		vm.displayNone = "displayNone";

		$scope.formatCell = function(value, row, col, formattedValue) {
			if(value && value.normalCount > 0 && value.normalCount == value.annotCount) {
				if(value.humanAnnotCount > 0) {
					return "<div title=\"" + value.title + "\" class=\"normal normaloffset\">N</div>";
				} else {
					var ret = "<div title=\"" + value.title + "\" class=\"normal\">N</div>";
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

			if(cellData.data) {
				url = FEWI_URL+"diseasePortal/popup?gridClusterKey=" + cellData.data["gridClusterKey"];
				if(cellData.data && cellData.data["phenoHeader"]) {
					url += "&header=" + cellData.data["phenoHeader"];
					url += "&isPhenotype=true";
				}
				if(cellData.data && cellData.data["diseaseHeader"]) {
					url += "&header=" + cellData.data["diseaseHeader"];
					url += "&isPhenotype=false";
				}
			}
			url += "&queryToken=" + vm.queryToken;

			if(url) {
				// append current time in milliseconds to ensure popup uniqueness
				var windowName = "popup_" + cellData.data["gridClusterKey"] + "_" + cellData.data["header"] + (new Date()).getTime();
				var child = window.open (url, windowName, 'width=800,height=600,resizable=yes,scrollbars=yes,alwaysRaised=yes');
				child.focus();
			}
		}

		$rootScope.$on("FilterChanged", function(event) {
			if(vm.gridloading) return;
			vm.gridloading = true;

			$timeout(function () {
				filterGrid();
				$rootScope.$emit("GridFilterFinished");
				buildGrid();
				vm.gridloading = false;
			}, 1);

		});

		vm.removeFilters = function() {
			$rootScope.selectedPhenoTypesAndDiseasesModel = [];		// selected phenotype and disease filter values
			$rootScope.selectedGenesModel = [];						// selected genes filter values
			$rootScope.selectedFeatureTypesModel = [];				// selected feature types filter values
			$rootScope.$emit("FilterChanged");
		}

		$rootScope.$on("CallSearchMethod", function(event, data) {
			vm.model = data;

			vm.gridloading = true;

			Search.gridQuery(vm.model).
				then(function(response) {
					vm.jsonData = response.data;
					buildFilterLists();
					filterGrid();
					buildGrid();
					vm.gridloading = false;
					vm.queryToken = vm.jsonData.queryToken;
					vm.removeFilters();
					$rootScope.$emit("ClearFilterText");
				}, function (error) {
					vm.errorMessage = error;
			});

		});
		
		function updateWindowSize() {
			var cellsize = 20;
			
			// minimum of 5 columns shown
			vm.windowmaxcols = Math.floor(($window.innerWidth - 300) / cellsize);
			if(vm.windowmaxcols < 5) vm.windowmaxcols = 5;

			// if "You Searched For" div is tall, need to figure in the extra beyond the filter buttons
			var ysfOverflow = 0;
			var ysfList = jQuery('.ysf');
			var buttonList = jQuery('.btn-group');
			if ((ysfList.length > 0) && (buttonList.length > 0)) {
				ysfOverflow = Math.max(0, ysfList[0].getBoundingClientRect().height - buttonList[0].getBoundingClientRect().height);
			}
			
			// Top header up to the bottom of the header line plus 40
			// Without the wrapping of: Human  Mouse: Disease Connection
			vm.windowmaxrows = Math.floor(($window.innerHeight - 470 - ysfOverflow) / cellsize);
			if(vm.windowmaxrows < 15) vm.windowmaxrows = 15;
		}

		function buildFilterLists() {

			var i = vm.jsonData.gridMPHeaders.indexOf("normal phenotype");
			if(i != -1) vm.jsonData.gridMPHeaders.splice(i, 1);
			vm.jsonData.gridMPHeaders.sort(naturalSortService.naturalSortFunction)
			if(i != -1) vm.jsonData.gridMPHeaders.push("normal phenotype");
			vm.jsonData.gridDiseaseHeaders.sort(naturalSortService.naturalSortFunction)

			$rootScope.selectedPhenoTypesAndDiseases = [];			// options for phenotype/disease filter
			$rootScope.selectedPhenoTypesAndDiseasesModel = [];		// user-selected options
			
			for (var j = 0; j < vm.jsonData.gridMPHeaders.length; j++) {
				$rootScope.selectedPhenoTypesAndDiseases.push({id: vm.jsonData.gridMPHeaders[j], label: vm.jsonData.gridMPHeaders[j], type: "1"});
			}

			for (var j = 0; j < vm.jsonData.gridDiseaseHeaders.length; j++) {
				$rootScope.selectedPhenoTypesAndDiseases.push({id: vm.jsonData.gridDiseaseHeaders[j], label: vm.jsonData.gridDiseaseHeaders[j], type: "2"});
			}

//			$rootScope.selectedFeatureTypes = [];		// options for the feature types filter (computed in GeneController.js)
			$rootScope.selectedFeatureTypesModel = [];	// user-selected options

			$rootScope.allGenes = [];					// all gene symbols in the grid
			$rootScope.selectedGenes = [];				// options for the genes filter
			$rootScope.selectedGenesModel = [];			// user-selected options
			for(var key in vm.jsonData.gridRows) {
				var index = key;
				key = vm.jsonData.gridRows[key];
				var humanSymbolString = [];
				var rowSymbols = [];
				for(var human in key.gridCluster.humanSymbols) {
					var h = key.gridCluster.humanSymbols[human];
					humanSymbolString.push(h.symbol.replace(/<([^>]*)>/g, "<sup>$1</sup>"));
					rowSymbols.push(h.symbol);
					$rootScope.allGenes.push(h.symbol);
				}

				var markerSymbolString = [];
				for(var marker in key.gridCluster.mouseSymbols) {
					var m = key.gridCluster.mouseSymbols[marker];
					markerSymbolString.push(m.symbol.replace(/<([^>]*)>/g, "<sup>$1</sup>"));
					rowSymbols.push(m.symbol);
					$rootScope.allGenes.push(m.symbol);
				}
				if(humanSymbolString.length > 0 && markerSymbolString.length > 0) {
					$rootScope.selectedGenes.push({id: index, symbols: rowSymbols, label: humanSymbolString.join(", ") + " - " + markerSymbolString.join(", ")});
				} else if(humanSymbolString.length > 0) {
					$rootScope.selectedGenes.push({id: index, symbols: rowSymbols, label: humanSymbolString.join(", ")});
				} else if(markerSymbolString.length > 0) {
					$rootScope.selectedGenes.push({id: index, symbols: rowSymbols, label: markerSymbolString.join(", ")});
				}
			}
			
			// Let the GeneController know that we finished updating the Gene and Pheno/Disease filters.
			$rootScope.$emit("GridFiltersBuilt");
		}
	
		// Filter the grid tab.
		function filterGrid() {
			vm.displayRows = [];
			vm.displayCols = [];
			$rootScope.filteredDiseases = [];		// list of diseases to hide from being displayed
			$rootScope.filteredGenes = [];			// list of genes to hide from being displayed

			var selectedPhenoTypesAndDiseases = [];
			for(var i = 0; i < $rootScope.selectedPhenoTypesAndDiseasesModel.length; i++) {
				selectedPhenoTypesAndDiseases.push($rootScope.selectedPhenoTypesAndDiseasesModel[i].id);
			}

			// Gene symbols to show, based on the feature types filter.  Will be empty if FT filter not used.
			var ftSymbols = {};
			var ftHadAny = false;

			for (var i in $rootScope.selectedFeatureTypesModel) {
				var myID = $rootScope.selectedFeatureTypesModel[i].id;
				var sft = $rootScope.selectedFeatureTypes[myID];
				for (var s in sft.symbols) {
					ftSymbols[sft.symbols[s]] = 1;
					ftHadAny = true;
				}
			}
			
			// Gene symbols to show, based on the Genes filter.  Will be empty if Genes filter not used.
			var gfSymbols = {};
			var gfHadAny = false;

			for(var i = 0; i < $rootScope.selectedGenesModel.length; i++) {
				var myID = $rootScope.selectedGenesModel[i].id;
				var row = $rootScope.selectedGenes[myID];
				for (var s in row.symbols) {
					gfSymbols[row.symbols[s]] = 1;
					gfHadAny = true;
				}
			}

			// List of row IDs for the genes we want to show, based on both Feature Types and Genes filters.  Will be empty
			// if neither filter was used.
			
			var selectedGenes = [];
			for (var i = 0; i < $rootScope.selectedGenes.length; i++) {
				var myID = $rootScope.selectedGenes[i].id;
				var row = $rootScope.selectedGenes[myID];
				for (var s = 0; s < row.symbols.length; s++) {
					var symbol = row.symbols[s];

					var okFT = (!ftHadAny) || (symbol in ftSymbols);
					var okGF = (!gfHadAny) || (symbol in gfSymbols);

					if (okFT && okGF) {
						selectedGenes.push(row.id);
					}
				}
			}
			
			// Proceed with filtering.
			
			for(var key in vm.jsonData.gridRows) {
				var index = key;
				key = vm.jsonData.gridRows[key];
				var showRow = false;
				for(var header in vm.jsonData.gridMPHeaders) {
					header = vm.jsonData.gridMPHeaders[header];
					if(key.mpHeaderCells[header] && (selectedGenes.length == 0 || selectedGenes.indexOf(index) > -1) && (selectedPhenoTypesAndDiseases.length == 0 || selectedPhenoTypesAndDiseases.indexOf(header) > -1)) {
						showRow = true;
					}
				}
				for(var header in vm.jsonData.gridDiseaseHeaders) {
					header = vm.jsonData.gridDiseaseHeaders[header];
					if(key.diseaseCells[header] && (selectedGenes.length == 0 || selectedGenes.indexOf(index) > -1) && (selectedPhenoTypesAndDiseases.length == 0 || selectedPhenoTypesAndDiseases.indexOf(header) > -1)) {
						showRow = true;
					}
				}
				if(showRow) {
					for(var human in key.gridCluster.humanSymbols) {
						$rootScope.filteredGenes.push(key.gridCluster.humanSymbols[human].symbol);
					}
					for(var marker in key.gridCluster.mouseSymbols) {
						$rootScope.filteredGenes.push(key.gridCluster.mouseSymbols[marker].symbol);
					}
					vm.displayRows.push(index);
				}
			}

			for(var header in vm.jsonData.gridMPHeaders) {
				header = vm.jsonData.gridMPHeaders[header];
				var showCol = false;
				for(var key in vm.jsonData.gridRows) {
					var index = key;
					key = vm.jsonData.gridRows[key];
					if(key.mpHeaderCells[header] && (selectedGenes.length == 0 || selectedGenes.indexOf(index) > -1) && (selectedPhenoTypesAndDiseases.length == 0 || selectedPhenoTypesAndDiseases.indexOf(header) > -1)) {
						showCol = true;
					}
				}
				if(showCol) {
					vm.displayCols.push(header);
				}
			}
	
			for(var header in vm.jsonData.gridDiseaseHeaders) {
				header = vm.jsonData.gridDiseaseHeaders[header];
				var showCol = false;
				for(var key in vm.jsonData.gridRows) {
					var index = key;
					key = vm.jsonData.gridRows[key];
					if(key.diseaseCells[header] && (selectedGenes.length == 0 || selectedGenes.indexOf(index) > -1) && (selectedPhenoTypesAndDiseases.length == 0 || selectedPhenoTypesAndDiseases.indexOf(header) > -1)) {
						showCol = true;

						if(key.diseaseCells[header].terms && key.diseaseCells[header].terms.length > 0) {
							for(var i = 0; i < key.diseaseCells[header].terms.length; i++) {
								if($rootScope.filteredDiseases.indexOf(key.diseaseCells[header].terms[i]) < 0) {
									$rootScope.filteredDiseases.push(key.diseaseCells[header].terms[i]);
								}
							}
						}

					}
				}
				if(showCol) {
					vm.displayCols.push(header);
				}
			}
			
			// if no user-selected filter values, we don't need to filter the Genes and Diseases tabs
			if ((selectedPhenoTypesAndDiseases.length == 0) && (selectedGenes.length == 0)) {
				$rootScope.filteredDiseases = [];
				$rootScope.filteredGenes = [];
			}
		}

		function buildGrid() {

			var newResults = {};
			var data = [];

			vm.DiseaseHeaderCount = 0;
			vm.MPHeaderCount = 0;
			vm.GeneCount = 0;

			var headerContent = [];
			headerContent.push("<span class='hgHeader'>Human Gene</span>");
			headerContent.push("<span class='mgHeader'>Mouse Gene</span>");
			vm.solrQuery = vm.jsonData.filterQuery;
			
			// Push the MP Headers into the headerContent row
			var hash = {};
			for(var header in vm.jsonData.gridMPHeaders) {
				header = vm.jsonData.gridMPHeaders[header];

				if(vm.displayCols.indexOf(header) > -1) {
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

			// Push the Disease Headers into the headerContent row
			for(var header in vm.jsonData.gridDiseaseHeaders) {
				header = vm.jsonData.gridDiseaseHeaders[header];
				if(vm.displayCols.indexOf(header) > -1) {
					if(vm.jsonData.gridHighLights) {
						hash[header] = vm.jsonData.gridHighLights.indexOf(header);
					} else {
						hash[header] = -1;
					}
					vm.DiseaseHeaderCount++;
					headerContent.push(hash);
					hash = {};
				}
			}

			// Push 7 blank columns at the end so the ending names can be read
			for(var i = 0; i < 7; i++) { headerContent.push([]); }
			data.push(headerContent);

			for(var key in vm.jsonData.gridRows) {
				var index = key;
				key = vm.jsonData.gridRows[key];
				var rowContent = [];
				var rowSymbols = [];

				var humanSymbolString = [];
				for(var human in key.gridCluster.humanSymbols) {
					var h = key.gridCluster.humanSymbols[human];
					var temp = "";
					if(key.gridCluster.homologyClusterKey) {
						temp = "<a target=\"_blank\" href=\"https://www.alliancegenome.org/gene/" + h.primaryID + "\" title=\"" + h.title + "\">";
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
                                        var temp = `<a target="_blank" href="/marker/${m.primaryID}" title="${m.title}">`
					temp += m.symbol.replace(/<([^>]*)>/g, "<sup>$1</sup>");
					temp += "</a>";
					markerSymbolString.push(temp);
					rowSymbols.push(m.symbol);
				}
				rowContent.push(markerSymbolString.join(", "));

				for(var header in vm.jsonData.gridMPHeaders) {
					header = vm.jsonData.gridMPHeaders[header];
					if(vm.displayCols.indexOf(header) > -1) {
						if(key.mpHeaderCells[header]) {
							key.mpHeaderCells[header]["phenoHeader"] = header;
							key.mpHeaderCells[header]["title"] = "Gene(s): " + rowSymbols.join(", ") + "\nPhenotype: " + header + "\nClick to see " + (key.mpHeaderCells[header].annotCount + key.mpHeaderCells[header].humanAnnotCount) + " annotations";
						}
						rowContent.push(key.mpHeaderCells[header]);
					}
				}
				rowContent.push({normalCount: 0, annotCount: 0, humanAnnotCount: 0});
				for(var header in vm.jsonData.gridDiseaseHeaders) {
					header = vm.jsonData.gridDiseaseHeaders[header];
					if(vm.displayCols.indexOf(header) > -1) {
						if(key.diseaseCells[header]) {
							key.diseaseCells[header]["diseaseHeader"] = header;
							key.diseaseCells[header]["title"] = "Gene(s): " + rowSymbols.join(", ") + "\nDisease: " + header + "\nClick to see " + (key.diseaseCells[header].annotCount + key.diseaseCells[header].humanAnnotCount) + " annotations";
						}
						rowContent.push(key.diseaseCells[header]);
					}
				}
				// The the row does not have data skip it
				if(vm.displayRows.indexOf(index) > -1) {
					vm.GeneCount++;
					data.push(rowContent);
				}
			}
			// Push the final footer row
			data.push([]);

			newResults.totalcolcount = vm.DiseaseHeaderCount + vm.MPHeaderCount;
			newResults.totalrowcount = vm.GeneCount;

			updateWindowSize();
			newResults.maxcols = Math.min(vm.windowmaxcols, headerContent.length);
			newResults.maxrows = Math.min(vm.windowmaxrows, data.length - 2);

			newResults.greyBar = vm.MPHeaderCount + 2

			$scope.$parent.$parent.tab.count = newResults.totalrowcount + " x " + newResults.totalcolcount;

			if (newResults.maxrows < newResults.totalrowcount) {
				$rootScope.gridResultsString = $sce.trustAsHtml("Showing " + newResults.maxrows + " of " + newResults.totalrowcount + " rows; scroll down to see more. "
					+ "<img id='sizeHelpImage' src='"+FEWI_URL+"assets/images/help_icon_16.png' style='margin-top: -5px; margin-left: 3px; cursor: pointer;'"
					+ " title='To see more rows at a time, use a larger browser window or zoom out and re-run your search.'>");
			} else {
				$rootScope.gridResultsString = $sce.trustAsHtml("Showing " + newResults.maxrows + " of " + newResults.totalrowcount + " rows.");
			}

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
