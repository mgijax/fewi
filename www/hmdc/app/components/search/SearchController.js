(function() {
	'use strict';
	angular.module('hmdc.search')
		.controller('SearchController', SearchController);

	function SearchController($rootScope, $scope, $log, $http, Search, AutoComplete, $sce, ngDialog, naturalSortService) {
		var vm = $scope.vm = {};

		vm.onSubmit = onSubmit;
		vm.mustHide = false;
		vm.mustHideLegend = true;
		vm.resetGeneTable = false;
		vm.resetDiseaseTable = false;

		vm.autoComplete = [];

		$scope.displayHTML = function(html) {
			return $sce.trustAsHtml(html);
		}

		$scope.openDiseaseSource = function() {
			ngDialog.open({ template: 'DiseaseSource' });
		}

		$scope.openGeneSource = function() {
			ngDialog.open({ template: 'GeneSource' });
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

		$scope.greyBar = "greyBar";
		$scope.displayNone = "displayNone";

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

		$rootScope.getAutoComplete = function(value, amount) {
			return $http.get('/autocomplete/hmdcTermAC', {
				params: {
					query: value,
					pageSize: amount
				}
			});
		};

		$scope.popup = function(url) {
			console.log(url);
			var child = window.open (url, "popup_window", 'width=800,height=600,resizable=yes,scrollbars=yes,alwaysRaised=yes');
			child.focus();
		}

		function onSubmit() {
			vm.query = vm.model;
			console.log("Submit: " + JSON.stringify(vm.model));
			vm.results = {};

			vm.gridloading = true;

			Search.gridQuery(vm.model).
				then(function(response) {
					vm.results.grid = {};
					//vm.results.grid.data = response.data;
					vm.results.grid.data = [];

					var headerContent = [];
					headerContent.push("Human Gene");
					headerContent.push("Mouse Gene");
					vm.solrQuery = response.data.filterQuery;
					
					var i = response.data.gridMPHeaders.indexOf("normal phenotype");
					if(i != -1) response.data.gridMPHeaders.splice(i, 1);
					response.data.gridMPHeaders.sort(naturalSortService.naturalSortFunction)
					if(i != -1) response.data.gridMPHeaders.push("normal phenotype");
					response.data.gridOMIMHeaders.sort(naturalSortService.naturalSortFunction)

					// Push the MP Headers into the headerContent row
					var hash = {};
					for(var header in response.data.gridMPHeaders) {
						if(response.data.gridHighLights) {
							hash[response.data.gridMPHeaders[header]] = response.data.gridHighLights.indexOf(response.data.gridMPHeaders[header]);
						} else {
							hash[response.data.gridMPHeaders[header]] = -1;
						}
						headerContent.push(hash);
						hash = {};
					}

					headerContent.push({});

					// Push the OMIM Headers into the headerContent row
					for(var header in response.data.gridOMIMHeaders) {
						if(response.data.gridHighLights) {
							hash[response.data.gridOMIMHeaders[header]] = response.data.gridHighLights.indexOf(response.data.gridOMIMHeaders[header]);
						} else {
							hash[response.data.gridOMIMHeaders[header]] = -1;
						}
						headerContent.push(hash);
						hash = {};
					}
					for(var i = 0; i < 7; i++) { headerContent.push([]); }
					vm.results.grid.data.push(headerContent);

					for(var key in response.data.gridRows) {
						key = response.data.gridRows[key];
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

						for(var header in response.data.gridMPHeaders) {
							header = response.data.gridMPHeaders[header];
							if(key.mpHeaderCells[header]) {
								key.mpHeaderCells[header]["phenoHeader"] = header;
								key.mpHeaderCells[header]["title"] = "Gene(s): " + rowSymbols.join(", ") + "\nPhenotype: " + header + "\nClick to see " + (key.mpHeaderCells[header].annotCount + key.mpHeaderCells[header].humanAnnotCount) + " annotations";
							}
							rowContent.push(key.mpHeaderCells[header]);
						}
						rowContent.push({normalCount: 0, annotCount: 0, humanAnnotCount: 0});
						for(var header in response.data.gridOMIMHeaders) {
							header = response.data.gridOMIMHeaders[header];
							if(key.diseaseCells[header]) {
								key.diseaseCells[header]["diseaseHeader"] = header;
								key.diseaseCells[header]["title"] = "Gene(s): " + rowSymbols.join(", ") + "\nDisease: " + header + "\nClick to see " + (key.diseaseCells[header].annotCount + key.diseaseCells[header].humanAnnotCount) + " annotations";
							}
							rowContent.push(key.diseaseCells[header]);
						}
						vm.results.grid.data.push(rowContent);
					}
					// Push the final footer row
					vm.results.grid.data.push([]);

					vm.results.grid.totalcolcount = response.data.gridOMIMHeaders.length + response.data.gridMPHeaders.length;
					vm.results.grid.totalrowcount = response.data.gridRows.length;

					vm.maxcols = Math.min(vm.windowmaxcols, headerContent.length);
					vm.maxrows = Math.min(vm.windowmaxrows, vm.results.grid.data.length - 2);

					vm.results.grid.grayBar = response.data.gridMPHeaders.length + 2

					vm.tabs.gridTab.count = response.data.gridRows.length + " x " + vm.results.grid.totalcolcount;

					vm.mustHide = true;
					vm.gridloading = false;
				}, function (error) {
					vm.errorMessage = error;
			});
			Search.geneQuery(vm.model).
				then(function(response) {
					vm.results.geneResults = response.data;
					vm.tabs.geneTab.count = vm.results.geneResults.length;
					vm.mustHide = true;
					vm.resetGeneTable = true;
				}, function (error) {
					vm.errorMessage = error;
			});
			Search.diseaseQuery(vm.model).
				then(function(response) {
					vm.results.diseaseResults = response.data;
					vm.tabs.diseaseTab.count = vm.results.diseaseResults.length;
					vm.mustHide = true;
					vm.resetDiseaseTable = true;
					vm.tabs.gridTab.active = true;
				}, function (error) {
					vm.errorMessage = error;
			});
		}

		vm.tabs = {
			"gridTab": {
				"count": 0,
				"heading": "Gene Homologs x Phenotypes/Diseases",
				"active": true,
				"template": "/assets/hmdc/app/components/search/gridTemplate.html"
			},
			"geneTab": {
				"count": 0,
				"heading": "Genes",
				"active": false,
				"template": "/assets/hmdc/app/components/search/geneTemplate.html"
			},
			"diseaseTab": {
				"count": 0,
				"heading": "Diseases",
				"active": false,
				"template": "/assets/hmdc/app/components/search/diseaseTemplate.html"
			}

		};

		vm.operatorField = [
			{
				key: 'operator',
				type: 'queryBuilderSelect',
				data: {
					defaultValue: 'AND'
				},
				templateOptions: {
					label: '',
					options: [
						{ value: 'AND', name: 'AND' },
						{ value: 'OR', name: 'OR' }
					]
				}
			}
		];

		vm.buttonLabel = 'Search';

		vm.model = {
			operator: 'AND',
			queries: [
				{
					field: '',
					condition: {
						name: undefined,
						parameters: []
					}
				}
			]
		};

		vm.fields = 
			[
		{
				type: 'queryRow',
				key: 'queries',
				templateOptions: {
					rowFields: [
						{
							key: 'field',
							type: 'queryBuilderSelect',
							templateOptions: {
								label: '',
								required: true,
								options: [
									{ value: '', name: 'Please select a field' },
									{ value: 'miS', name: 'Gene Symbol or ID' },
									{ value: 'tsDtext', name: 'Phenotype or Disease Name' },
									{ value: 'location', name: 'Genome Location' },
									{ value: 'mnS', name: 'Gene Name' },
									{ value: 'tsDid', name: 'Phenotype or Disease ID' },
									//{ value: 'gene_upload', name: 'Gene File Upload'},
									//{ value: 'vcd_upload', name: 'VCF file (v4.0 or later)' }
								],
								onChange: function(value, options, scope) {
									scope.model.condition = {
										name: undefined,
										parameters: []
									};
								}
							}
						}
					],
					conditionFields: {
						// These are passed all the way to solr
						mnS: [
							{
								key: 'input',
								type: 'input',
								className: 'inline-field',
								templateOptions: {
									label: '',
									required: true,
									focus: true,
									placeholder: 'Example: paired box'
								}
							}
						],
						miS: [
							{
								key: 'input',
								type: 'input',
								className: 'inline-field',
								templateOptions: {
									label: '',
									required: true,
									focus: true,
									placeholder: 'Examples: Pax6, PAX1, MGI:88071, 720'
								}
							}
						],
						// These are passed all the way to solr
						tsDtext: [
							{
								key: 'input',
								type: 'autocomplete',
								className: 'inline-field',
								templateOptions: {
									label: '',
									required: true,
									focus: true,
									placeholder: 'Example: splenomegaly',
									options: [],
									autoComplete: function(value, to) {
										if (typeof value != 'undefined') {
											return $rootScope.getAutoComplete(value, 25).then(function(response) {
												to.options = response.data;
												return to.options;
											});
										};
									},
//									onChange: function ($viewValue, $scope) {                        
//										console.log("O: " + $scope.templateOptions.options);
//										return true;
//									}
								}
							}
						],
						tsDid: [
							{
								key: 'input',
								type: 'input',
								className: 'inline-field',
								templateOptions: {
									label: '',
									required: true,
									focus: true,
									placeholder: 'Examples: HP:0001744, OMIM:222100, MP:0008762'
								}
							}
						],
						location: [
							{
								key: 'input',
								type: 'input',
								className: 'inline-field',
								templateOptions: {
									label: '',
									required: true,
									focus: true,
									placeholder: 'Example: Chr12:3000000-10000000'
								}
							},
							{
								key: 'parameters[0]',
								type: 'radio',
								className: 'inline-field',
								defaultValue: 'mouse',
								templateOptions: {
									label: '',
									required: true,
									options: [
										{value: 'human', name: 'Human (GRCh38)'},
										{value: 'mouse', name: 'Mouse (GRCm38)'},
									]
								}
							}
						],
/*
						gene_upload: [
							{
								key: 'file', // pubmed id
								type: 'upload',
								className: 'inline-field',
								templateOptions: {
									label: '',
									required: true
								}
							},
							{
								key: 'searchtype',
								type: 'radio',
								className: 'inline-field',
								templateOptions: {
									label: 'Search for',
									required: true,
									options: [
										{value: 'mouse', name: 'Mouse'},
										{value: 'human', name: 'Human'},
									]
								}
							}
						],
						vcd_upload: [
							{
								key: 'name', // pubmed id
								type: 'upload',
								className: 'inline-field',
								templateOptions: {
									label: 'Upload a file',
									required: true
								}
							},
							{
								key: 'searchtype',
								type: 'radio',
								className: 'inline-field',
								templateOptions: {
									label: 'Search for',
									required: true,
									options: [
										{value: 'mouse', name: 'Mouse'},
										{value: 'human', name: 'Human'},
									]
								}
							}
						]
*/
					}
				}
			}
	];

	}
})();
