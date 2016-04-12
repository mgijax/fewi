(function() {
	'use strict';
	angular.module('civic.search')
		.controller('SearchController', SearchController);

	// @ngInject
	function SearchController($rootScope, $scope, $log, Search, $sce, ngDialog) {
		var vm = $scope.vm = {};

		vm.onSubmit = onSubmit;
		vm.mustHide = false;
		vm.mustHideLegend = true;

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
			console.log(cellData);
		}

		$scope.formatFun = function(value, row, col, formattedValue) {
			if(value && value.isNormal) {
				console.log(value);
				return "N";
			}
		}

		$scope.styleFun = function(value, row, col) {
			if(value) {
				var hac = "#F7861D";
				if(value.humanAnnotCount < 100) hac = "#F4A041";
				if(value.humanAnnotCount < 6) hac = "#F2BF79";
				if(value.humanAnnotCount < 2) hac = "#FBDBB4";
				if(value.humanAnnotCount < 1) hac = "";

				var ac = "#0C2255";
				if(value.annotCount < 100) ac = "#48648B";
				if(value.annotCount < 6) ac = "#879EBA";
				if(value.annotCount < 2) ac = "#C6D6E8";
				if(value.annotCount < 1) ac = "";

				if(hac && ac) {
					return "background: linear-gradient(135deg, " + hac + " 50%, rgb(0,0,0) 0%, " + ac + " 50%);";
				}
				if(hac) {
					return "background: linear-gradient(135deg, rgb(0,0,0) 0%, rgb(0,0,0) 0%, " + hac + " 0%);";
				}
				if(ac) {
					return "background: linear-gradient(135deg, " + ac + " 100%, rgb(0,0,0) 0%, rgb(0,0,0) 0%);";
				}
			}
		}



		function onSubmit() {
			vm.query = vm.model;
			console.log("Submit: " + JSON.stringify(vm.model));
			vm.results = {};

			Search.gridQuery(vm.model).
				then(function(response) {
					vm.results.grid = {};
					//vm.results.grid.data = response.data;
					vm.results.grid.data = [];

					var headerContent = [];
					headerContent.push("Human Gene");
					headerContent.push("Mouse Gene");
					// Push the MP Headers into the headerContent row
					for(var header in response.data.gridMPHeaders) {
						headerContent.push("<span>" + response.data.gridMPHeaders[header] + "</span>");
					}
					// Push the OMIM Headers into the headerContent row
					for(var header in response.data.gridOMIMHeaders) {
						headerContent.push("<span>" + response.data.gridOMIMHeaders[header] + "</span>");
					}
					headerContent.push("");
					vm.results.grid.data.push(headerContent);

					for(var key in response.data.gridRows) {
						key = response.data.gridRows[key];
						var rowContent = [];

						var subscript = 

						rowContent.push(key.gridCluster.humanSymbols.join().replace(/<([^>]*)>/, "<sup>$1</sup>"));
						rowContent.push(key.gridCluster.mouseSymbols.join().replace(/<([^>]*)>/, "<sup>$1</sup>"));
						for(var header in response.data.gridMPHeaders) {
							header = response.data.gridMPHeaders[header];
							rowContent.push(key.mpHeaderCells[header]);
						}
						for(var header in response.data.gridOMIMHeaders) {
							header = response.data.gridOMIMHeaders[header];
							rowContent.push(key.diseaseCells[header]);
						}
						vm.results.grid.data.push(rowContent);
					}
					vm.results.grid.data.push([]);

					vm.results.grid.rowcount = response.data.gridRows.length >= 18 ? 18 : response.data.gridRows.length;
					vm.results.grid.totalrowcount = response.data.gridRows.length + 1;

					vm.mustHide = true;
				}, function (error) {
					vm.errorMessage = error;
			});
//			Search.geneQuery(vm.model).
//				then(function(response) {
//					vm.results.geneResults = response.data;
//					vm.tabs.geneTab.count = vm.results.geneResults.totalCount;
//					vm.mustHide = true;
//				}, function (error) {
//					vm.errorMessage = error;
//			});
			Search.diseaseQuery(vm.model).
				then(function(response) {
					vm.results.diseaseResults = response.data;
					vm.tabs.diseaseTab.count = vm.results.diseaseResults.totalCount;
					vm.mustHide = true;
				}, function (error) {
					vm.errorMessage = error;
			});
		}

		vm.tabs = {
			"gridTab": {
				"count": 0,
				"heading": "Gene Homologs x Phenotypes/Diseases",
				"active": true,
				"template": "/assets/hmdc/search/views/grid.tpl.html"
			},
			"geneTab": {
				"count": 0,
				"heading": "Genes",
				"active": false,
				"template": "/assets/hmdc/search/views/gene.tpl.html"
			},
			"diseaseTab": {
				"count": 0,
				"heading": "Diseases",
				"active": false,
				"template": "/assets/hmdc/search/views/disease.tpl.html"
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
						{ value: 'AND', name: 'all' },
						{ value: 'OR', name: 'any' }
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
									{ value: 'mnS', name: 'Gene (symbol or name)' },
									{ value: 'miS', name: 'Gene (ID)' },
									{ value: 'tsDtext', name: 'Phenotype/Disease (name)' },
									{ value: 'tsDid', name: 'Phenotype/Disease (ID)' },
									{ value: 'location', name: 'Location (chromosome region)' },
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
									placeholder: 'Examples: Pax*, gremlin. Use * for wildcard.'
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
									placeholder: 'Examples: MGI:105098, MGI:97490, 5080'
								}
							}
						],
						// These are passed all the way to solr
						tsDtext: [
							{
								key: 'input',
								type: 'input',
								className: 'inline-field',
								templateOptions: {
									label: '',
									required: true,
									focus: true,
									placeholder: 'Examples: hippocamp*, cardiovascular. Use * for wildcard.'
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
									placeholder: 'Examples: HP:0001744, OMIM:222100, MP:0008260'
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
