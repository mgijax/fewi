(function() {
	'use strict';
	angular.module('civic.search')
		.controller('SearchController', SearchController)
		.filter('sortFilter',function(naturalService) {
			return function(array, predicteObject, reverse) {
				var neg = "";
				if(reverse) {
					neg = "-";
				}
				array.sort(naturalService.naturalSort(neg + predicteObject));
				return array;
		}})
		.directive('stReset', function() {
			return {
				require: '^stTable',
				scope: { stReset: "=stReset" },
				link: function (scope, element, attr, ctrl) {
                
					scope.$watch("stReset", function () {
						  
						if (scope.stReset) {

							// remove local storage
							if (attr.stPersist) {
								localStorage.removeItem(attr.stPersist);
							}

							// reset table state
							var tableState = ctrl.tableState();
							tableState.search = {};
							tableState.sort = {predicate: "disease", reverse: false};
							ctrl.pipe();
							// reset scope value
							scope.stReset = false;
						}

					});
				}
			};
		});

	// @ngInject
	function SearchController($rootScope, $scope, $log, Search, $sce, ngDialog) {
		var vm = $scope.vm = {};

		vm.onSubmit = onSubmit;
		vm.mustHide = false;
		vm.resetTable = false;

		$scope.displayHTML = function(html) {
			return $sce.trustAsHtml(html);
		}

		$scope.openDiseaseSource = function() {
			ngDialog.open({ template: 'DiseaseSource' });
		}

		$scope.openGeneSource = function() {
			ngDialog.open({ template: 'GeneSource' });
		}

		function onSubmit() {
			vm.query = vm.model;
			console.log("Submit: " + JSON.stringify(vm.model));
			vm.results = {};

//			Search.gridQuery(vm.model).
//				then(function(response) {
//					console.log(response.data);
//					vm.results.gridResults = response.data;
//					vm.mustHide = true;
//				}, function (error) {
//					console.log(error);
//					vm.errorMessage = error;
//			});
//			Search.geneQuery(vm.model).
//				then(function(response) {
//					console.log(response.data);
//					vm.results.geneResults = response.data;
//					vm.tabs.geneTab.count = vm.results.geneResults.totalCount;
//					vm.mustHide = true;
//				}, function (error) {
//					console.log(error);
//					vm.errorMessage = error;
//			});
			Search.diseaseQuery(vm.model).
				then(function(response) {
					vm.results.diseaseResults = response.data;
					//vm.results.diseaseResults = vm.results.diseaseResults.sort(naturalService.naturalSort('disease'));
					vm.tabs.diseaseTab.count = vm.results.diseaseResults.totalCount;
					vm.mustHide = true;
					vm.resetTable = true;
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
				"active": false,
				"template": "/assets/hmdc/search/views/gene.tpl.html"
			},
			"diseaseTab": {
				"count": 0,
				"heading": "Diseases",
				"active": true,
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
