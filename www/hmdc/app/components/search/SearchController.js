(function() {
	'use strict';
	angular.module('hmdc.search').controller('SearchController', SearchController);

	function SearchController($rootScope, $scope, $log, $http, AutoComplete, $sce) {
		var vm = $scope.vm = {};

		vm.onSubmit = onSubmit;
		vm.hideQueryForm = false;


		$rootScope.selectedPhenoTypesModel = [];
		$rootScope.selectedDiseasesModel = [];
		$rootScope.selectedGenesModel = [];

		$rootScope.selectPhenoTypesCustemText = { buttonDefaultText: 'Filter by Phenotypes', dynamicButtonTextSuffix: 'phenotype(s) checked' };
		$rootScope.selectDiseasesCustemText = { buttonDefaultText: 'Filter by Diseases', dynamicButtonTextSuffix: 'disease(s) checked' };
		$rootScope.selectGenesCustemText = { buttonDefaultText: 'Filter by Genes', dynamicButtonTextSuffix: 'gene(s) checked' };

		$rootScope.selectPhenoTypesSettings = { buttonClasses: "", scrollableHeight: '500px', scrollable: true };
		$rootScope.selectDiseasesSettings = { buttonClasses: "", scrollableHeight: '500px', scrollable: true, enableSearch: true };
		$rootScope.selectGenesSettings = { buttonClasses: "", scrollableHeight: '500px', scrollable: true, enableSearch: true };

		vm.autoComplete = [];

		$rootScope.displayTabs = false;

		$rootScope.getAutoComplete = function(value, amount) {
			return $http.get('/autocomplete/hmdcTermAC', {
				params: {
					query: value,
					pageSize: amount
				}
			});
		};

		$rootScope.handleEvents = {
			onItemSelect: function() { $rootScope.$emit("FilterChanged"); },
			onItemDeselect: function() { $rootScope.$emit("FilterChanged"); },
			onSelectAll: function() { $rootScope.$emit("FilterChanged"); },
			onUnselectAll: function() { $rootScope.$emit("FilterChanged"); }
		};

		function onSubmit() {
			console.log("Submit: " + JSON.stringify(vm.model));
			$rootScope.displayTabs = true;
			$rootScope.$emit("CallSearchMethod", vm.model);
			vm.hideQueryForm = true;
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
