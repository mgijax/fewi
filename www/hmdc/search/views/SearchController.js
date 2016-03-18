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
  function SearchController($scope, $log, Search) {
    var vm = $scope.vm = {};

    vm.onSubmit = onSubmit;

    function onSubmit() {
      $log.debug(JSON.stringify(vm.model));
      Search.post(vm.model);
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
                  { value: 'gene', name: 'Gene (symbol name)' },
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
                  placeholder: 'Use * for wildcard. Examples: Pax* gremlin'
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
                  placeholder: 'MGI:000000'
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
                  placeholder: 'Use * for wildcard. Examples: hippocamp* cardiovascular'
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
                  placeholder: 'HP:0001744 222100 MP:0000000'
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
                  placeholder: 'Chr12:3000000-10000000'
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
