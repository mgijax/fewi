(function() {
	'use strict';
	angular.module('civic.search')
		.controller('SearchController', SearchController);

	// @ngInject
	function SearchController($rootScope, $scope, $log, Search, $sce) {
		var vm = $scope.vm = {};

		vm.onSubmit = onSubmit;
		vm.mustHide = false;

		$scope.displayHTML = function(html) {
			return $sce.trustAsHtml(html);
		}

		function onSubmit() {
			vm.query = vm.model;
			console.log("Submit: " + JSON.stringify(vm.model));
			vm.results = {};

			Search.gridQuery(vm.model).
				then(function(response) {
					console.log(response.data);
					vm.results.gridResults = response.data;
					vm.mustHide = true;
				}, function (error) {
					console.log(error);
					vm.errorMessage = error;
			});
			Search.geneQuery(vm.model).
				then(function(response) {
					console.log(response.data);
					vm.results.geneResults = response.data;
					vm.tabs.geneTab.count = vm.results.geneResults.totalCount;
					vm.mustHide = true;
				}, function (error) {
					console.log(error);
					vm.errorMessage = error;
			});
			Search.diseaseQuery(vm.model).
				then(function(response) {
					console.log(response.data);
					vm.results.diseaseResults = response.data;
					vm.tabs.diseaseTab.count = vm.results.diseaseResults.totalCount;
					vm.mustHide = true;
				}, function (error) {
					console.log(error);
					vm.errorMessage = error;
			});
		}

		vm.tabs = {
			"gridTab": {
				"count": 0,
				"heading": "Gene Homologs x Phenotypes/Diseases",
				"active": false,
				"template": "/assets/hmdc/search/views/grid.tpl.html"
			},
			"geneTab": {
				"count": 0,
				"heading": "Genes",
				"active": true,
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
									{ value: 'nomenclature', name: 'Gene (symbol or name)' },
									{ value: 'marker_key', name: 'Gene (ID)' },
									{ value: 'term', name: 'Phenotype/Disease (name)' },
									{ value: 'termId', name: 'Phenotype/Disease (ID)' },
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
						nomenclature: [
							{
								key: 'input',
								type: 'input',
								className: 'inline-field',
								templateOptions: {
									label: '',
									required: true,
									placeholder: 'Examples: Pax*, gremlin. Use * for wildcard.'
								}
							}
						],
						marker_key: [
							{
								key: 'input',
								type: 'input',
								className: 'inline-field',
								templateOptions: {
									label: '',
									required: true,
									placeholder: 'Examples: MGI:105098, MGI:97490, 5080'
								}
							}
						],
						term: [
							{
								key: 'input',
								type: 'input',
								className: 'inline-field',
								templateOptions: {
									label: '',
									required: true,
									placeholder: 'Examples: hippocamp*, cardiovascular. Use * for wildcard.'
								}
							}
						],
						termId: [
							{
								key: 'input',
								type: 'input',
								className: 'inline-field',
								templateOptions: {
									label: '',
									required: true,
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
