(function() {
  'use strict';
  angular.module('civic.search')
    .controller('SearchController', SearchController)
    .config(SearchView);

  // @ngInject
  function SearchView($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise('/diseasePortal');
    $stateProvider
      .state('search', {
        url: '/diseasePortal',
        reloadOnSearch: false,
        controller: 'SearchController',
        templateUrl: '/assets/hmdc/search/views/search.tpl.html'
      });
  }

  // @ngInject
  function SearchController($rootScope, $scope, $log, Search) {
    var vm = $scope.vm = {};

    $scope.tab = 3;
    $scope.setTab = function(newTab) {
       $scope.tab = newTab;
    };

    $scope.isSet = function(tabNum) {
       return $scope.tab == tabNum;
    };

    vm.onSubmit = onSubmit;
    
    $rootScope.mustHide = false;
    $rootScope.emptyResults = true;
    $rootScope.loaded = { display:'block' };
    
    function onSubmit() {
      $log.debug(JSON.stringify(vm.model));
      Search.post(vm.model)
			.then(function(response) {
				$rootScope.diseaseResponse = response;
				$rootScope.mustHide = true;
				$rootScope.emptyResults = false;
			}
		);
    }

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
                  { value: 'gene', name: 'Gene (symbol or name)' },
                  { value: 'gene_id', name: 'Gene (ID)' },
                  { value: 'phenotype', name: 'Phenotype/Disease (name)' },
                  { value: 'phenotype_id', name: 'Phenotype/Disease (ID)' },
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
            gene: [
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
            gene_id: [
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
            phenotype: [
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
            phenotype_id: [
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
