(function() {
	'use strict';
	angular.module('hmdc.search').controller('SearchController', SearchController);

	function SearchController($rootScope, $scope, $log, $http, AutoComplete, $sce, FEWI_URL, $location) {
		var vm = $scope.vm = {};
		$scope.FEWI_URL = FEWI_URL;

		vm.onSubmit = onSubmit;
		vm.hideQueryForm = false;
		vm.showMpHpPopup = false;

		$rootScope.selectedPhenoTypesAndDiseasesModel = [];
		$rootScope.selectedGenesModel = [];
		$rootScope.selectedFeatureTypesModel = [];

		$rootScope.selectPhenoTypesAndDiseasesCustemText = { buttonDefaultText: 'Filter by Phenotypes/Disease(s)', dynamicButtonTextSuffix: 'Phenotype/Disease(s) checked' };
		$rootScope.selectGenesCustemText = { buttonDefaultText: 'Filter by Genes', dynamicButtonTextSuffix: 'Gene(s) checked' };
		$rootScope.selectFeatureTypesCustemText = { buttonDefaultText: 'Filter by Feature Types', dynamicButtonTextSuffix: 'Feature Type(s) checked' };

		$rootScope.selectPhenoTypesAndDiseasesSettings = { buttonClasses: "", scrollableHeight: '500px', scrollable: true, enableSearch: true,
			groupByTextProvider: function(groupValue) { if(groupValue == '1') { return "Phenotype(s)"; } else { return "Disease(s)"; } } };

		$rootScope.selectGenesSettings = { buttonClasses: "", scrollableHeight: '500px', scrollable: true, enableSearch: true };
		$rootScope.selectFeatureTypesSettings = { buttonClasses: "", scrollableHeight: '500px', scrollable: true, enableSearch: true };

		vm.autoComplete = [];

		$rootScope.displayTabs = false;

		$rootScope.getAutoComplete = function(value, amount) {
			return $http.get(FEWI_URL+'autocomplete/hmdcTermAC', {
				params: {
					query: value,
					pageSize: amount
				}
			});
		};

		$rootScope.handleEvents = {
			onItemSelect: function() { },
			onItemDeselect: function() { },
			onSelectAll: function() { vm.applyFilters(); },
			onDeselectAll: function() { vm.applyFilters(); }
		};

		vm.applyFilters = function() {
			$rootScope.$emit("FilterChanged");
		}

		vm.openMpHpPopup = function() {
			window.open(FEWI_URL+"diseasePortal/searchPopup","Ratting","width=1500,height=500,left=150,top=200,toolbar=0,status=0,");
		}

		vm.removeFilters = function() {
			$rootScope.selectedPhenoTypesAndDiseasesModel = [];
			$rootScope.selectedGenesModel = [];
			$rootScope.selectedFeatureTypesModel = [];
			$rootScope.$emit("FilterChanged");
			$rootScope.$emit("ClearFilterText");
		}

		$rootScope.$on("ClearFilterText", function(event, data) {
			var myFilters = jQuery('[ng-model=searchFilter]');
			var i = 0;
			while (i < myFilters.length) {
				var myFilter = myFilters[i];
				/* after we clear the value, need to fire an event to bring back the options below */
				myFilter.value = '';
				myFilter.dispatchEvent(new Event("change"));
				i++;
			}
		});


  		// find a string beginning with the given string 'c' that doesn't appear in string 's'
  		function findTag(c, s) {
			if (s.indexOf(c) < 0) { return c; }
			return findTag(c + c[0], s);
  		};

		// convert MGI superscript notation <...> to HTML superscript tags
		function superscript(s) {
			var openTag = findTag('{', s);
			return s.split('<').join(openTag).split('>').join('</sup>').split(openTag).join('<sup>');
		};

		/* update the "You Searched For" text to the left of the filter buttons
		*/
		function showYouSearchedFor(model) {
			if ((model == null) || (model.queries.length == 0)) {
				vm.youSearchedFor = $sce.trustAsHtml('');	// no parameters, so no YSF message
			}
			
			var ysf = "<b>You searched for...</b>";			// the You Searched For string we're computing
			var op = '<b>' + model.operator + '</b> ';		// AND or OR, used to join query clauses

			for (var i in model.queries) {
				var query = model.queries[i];
				var field = query['field'];
				var isHuman = (query['condition']['parameters'].indexOf('human') >= 0);
				var input = superscript(query['condition']['input']);
				
				ysf += '<br/>';								// line break between query clauses
				if (i != 0) {								// second and later lines begin with operator
					ysf += op;
				}
				
				// custom message for each field type
				if (field == 'miS') {
					ysf += 'Gene Symbols or IDs matching <b>' + input + '</b>';

				} else if (field == 'mnS') {
					ysf += 'Genes matching <b>"' + input + '"</b>';

				} else if (field == 'tsDid') {
					ysf += 'Disease or Phenotype IDs matching <b>' + input + '</b>';

				} else if (field == 'tsDtext') {
					ysf += 'Diseases or Phenotypes matching <b>"' + input + '"</b>';

				} else if (field == 'location') {
					if (isHuman) {
						ysf += 'Human ';
					} else {
						ysf += 'Mouse ';
					}
					ysf += 'loci overlapping interval: <b>' + input + '</b>';

				} else if (field == 'gene_upload') {
					var parameters = query['condition']['parameters'];
					var column = parameters[0];
					var delimiter = parameters[1];
					var skipHeader = parameters[2];
					var file = query['condition']['file'];
					var filename = file['name'];

					ysf += 'Gene Symbols or IDs matching file <b>' + filename + '</b> column ' + column + ' ' + delimiter + ' delimited ';
					if (skipHeader) {
						ysf += ' (ignore header row)';
					}

				} else {
					// should not happen, but left for debugging purposes
					ysf += field + ' <b>' + input + '</b>';
				}
			}
			vm.youSearchedFor = $sce.trustAsHtml(ysf);
		}
		
		function onSubmit() {
			vm.queryModel = {};
			angular.copy(vm.model, vm.queryModel);
			
			// Remove file data before sending model to the server
			for (var i in vm.queryModel.queries) {
				var query = vm.queryModel.queries[i];
				var field = query['field']; 
				if (field == 'gene_upload') {
					var condition = query['condition'];
					delete(condition['file']);
					delete(condition['fileData']);
					delete(condition['sampledata']);
				}
			}

			$rootScope.displayTabs = true;
			setTimeout(function() {
				$rootScope.$emit("CallSearchMethod", vm.queryModel);
				vm.tabs.gridTab.active = false;
				vm.tabs.diseaseTab.active = false;
			}, 500);
			vm.hideQueryForm = true;
			$rootScope.jsonQuery = angular.toJson(vm.queryModel);
			$rootScope.jsonEncodedQuery = encodeURIComponent(angular.toJson(vm.queryModel));
			showYouSearchedFor(vm.model);
		}

		$rootScope.parseUploadFile = function(scope) {

			var geneList = [];
			if(scope.model.fileData && scope.model.fileData.length > 0) {
				var lines = scope.model.fileData.split(/\r\n|\r|\n/);
				var i = 0;
				if(scope.model.parameters[2]) {
					i = 1;
				}
				for(; i < lines.length; i++) {
					var line = lines[i];

					if(line && line.length > 0) {

						var columns = [];
						if(scope.model.parameters[1] == "colon") {
							columns = line.split(/;/);
						} else if(scope.model.parameters[1] == "tab") {
							columns = line.split(/\t/);
						} else if(scope.model.parameters[1] == "comma") {
							columns = line.split(/,/);
						} else {
							columns = line.split(/\t/);
						}

						if(columns.length >= scope.model.parameters[0]) {
							var data = columns[scope.model.parameters[0] - 1];
							if(data && data.length > 0) {
								geneList.push(data);
							}
						}
					}
				}
				scope.model.input = geneList.join(", ");
				//array.slice(start,end)
				if(geneList.length > 0) {
					var geneCount = geneList.length;
					var geneMin = Math.min(geneCount, 3);
					scope.model.sampledata = "Showing " + geneMin + " of " + geneCount + " loaded gene(s): " + geneList.slice(0,3).join(", ");
				} else {
					scope.model.sampledata = "No Data Found";
				}
			} else {
				scope.model.sampledata = "No Data Found";
			}

		}

		var gridTabActive = true;
		var diseaseTabActive = false;
		
	    if (($location.search().termID !== undefined) && ($location.search().termID !== null)) {
	    	console.log("trying to switch tabs");
			gridTabActive = false;
			diseaseTabActive = true
	    }
	    
		vm.tabs = {
			"gridTab": {
				"count": 0,
				"heading": "Gene Homologs x Phenotypes/Diseases",
				"active": gridTabActive,
				"template": FEWI_URL+"assets/hmdc/app/components/search/gridTemplate.html"
			},
			"geneTab": {
				"count": 0,
				"heading": "Genes",
				"active": false,
				"template": FEWI_URL+"assets/hmdc/app/components/search/geneTemplate.html"
			},
			"diseaseTab": {
				"count": 0,
				"heading": "Diseases",
				"active": diseaseTabActive,
				"template": FEWI_URL+"assets/hmdc/app/components/search/diseaseTemplate.html"
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
									{ value: 'miS', name: 'Gene Symbol(s) or ID(s)' },
									{ value: 'mnS', name: 'Gene Name' },
									{ value: 'tsDtext', name: 'Disease or Phenotype Name' },
									{ value: 'tsDid', name: 'Disease or Phenotype ID(s)' },
									{ value: 'location', name: 'Genome Location' },
									{ value: 'gene_upload', name: 'Gene File Upload'},
									//{ value: 'vcd_upload', name: 'VCF file (v4.0 or later)' }
								],
								onChange: function(value, options, scope) {
									if (value=='tsDid') {
										vm.showMpHpPopup = true;
									} else {
										vm.showMpHpPopup = false;
									}
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
									}
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
									placeholder: 'Examples: HP:0001744, OMIM:222100, MP:0008762, DOID:114'
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
										{value: 'mouse', name: 'Mouse (GRCm39)'},
									]
								}
							}
						],
						gene_upload: [
							{
								key: 'file', // pubmed id
								type: 'txtupload',
								className: 'inline-field',
								templateOptions: {
									label: '',
									required: true,
									onChange: function ($viewValue, model, $scope) {
										$rootScope.parseUploadFile($scope);
										return true;
									}
								}
							},
							{
								key: 'parameters[0]',
								type: 'select',
								defaultValue: 1,
								className: 'pull-left pad30left',
								templateOptions: {
									label: 'Gene Column',
									required: true,
									options: [
										{value: 1, name: '1'}, {value: 2, name: '2'}, {value: 3, name: '3'}, {value: 4, name: '4'}, {value: 5, name: '5'}, {value: 6, name: '6'}, {value: 7, name: '7'}, {value: 8, name: '8'}, {value: 9, name: '9'},
									],
									onChange: function ($viewValue, model, $scope) {
										$rootScope.parseUploadFile($scope);
										return true;
									}
								}
							},
							{
								key: 'parameters[1]',
								type: 'select',
								className: 'pull-left pad30left',
								defaultValue: 'tab',
								templateOptions: {
									label: 'Delimiter',
									required: true,
									options: [
										{name: 'Tab \\t', value: 'tab'}, {name: 'SemiColon ;', value: 'colon'}, {name: 'Comma ,', value: 'comma'},
									],
									onChange: function ($viewValue, model, $scope) {
										$rootScope.parseUploadFile($scope);
										return true;
									}
								}
							},
							{
								key: 'parameters[2]',
								type: 'checkbox',
								className: 'pull-left pad30left',
								defaultValue: false,
								templateOptions: {
									label: 'Ignore Header Row',
									onChange: function ($viewValue, model, $scope) {
										$rootScope.parseUploadFile($scope);
										return true;
									}
								}
							},
							{
								key: 'sampledata',
								type: 'readonly',
								className: 'pull-left pad30left',
								templateOptions: {
									label: 'Sample Data:'
								},
								hideExpression: '!model.sampledata'
							}
						],
/*
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

        if (($location.search().termID !== undefined) && ($location.search().termID !== null)) {
            var data = {
                operator: 'AND',
                queries: [{
                    field: 'tsDid',
                    condition: {
                        parameters: [],
                        input: $location.search().termID
                    }
                }]
            };
            vm.model = data;
            onSubmit();
        } else if (($location.search().geneID !== undefined) && ($location.search().geneID !== null)) {
            var data = {
                operator: 'AND',
                queries: [{
                    field: 'miS',
                    condition: {
                        parameters: [],
                        input: $location.search().geneID
                    }
                }]
            };
            vm.model = data;
            onSubmit();
        }
	}


})();
