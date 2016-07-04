(function() {
	'use strict';
	angular.module('hmdc.search').config(queryBuilderConfig);

	// @ngInject
	function queryBuilderConfig(formlyConfigProvider) {
		var unique = 1;

		formlyConfigProvider.setType({
			name: 'txtupload',
			extends: 'input',
			wrapper: ['bootstrapLabel', 'bootstrapHasError'],
			link: function(scope, el, attrs) {
				el.on("change", function(changeEvent) {
					var file = changeEvent.target.files[0];

					scope.model.sampledata = "";
					scope.model.input = "";

					if (file) {
						var fileProp = {};
						for (var properties in file) {
							if (!angular.isFunction(file[properties])) {
								fileProp[properties] = file[properties];
							}
						}
						scope.fc.$setViewValue(fileProp);
					} else {
						scope.fc.$setViewValue(undefined);
					}

					if(file) {
						var reader = new FileReader();
						reader.onload = function(e) {
							var geneList = [];

							var lines = e.target.result.split(/\r\n|\n/);
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
								scope.model.sampledata = "Showing 3 of "+ geneList.length + " loaded gene(s): " + geneList.slice(0,3).join(", ");
							} else {
								scope.model.sampledata = "No Data Found";
							}

						}; 
						reader.readAsText(file);
					} else {
						scope.model.sampledata = "No Data Found";
					}

				});

				el.on("focusout", function(focusoutEvent) {
					// dont run validation , user still opening pop up file dialog
					if (focusoutEvent.view.document.activeElement.id === scope.id) {
						// so we set it untouched
						scope.$apply(function(scope) {
							scope.fc.$setUntouched();	
						});
					} else {
						// element losing focus so we trigger validation
						scope.fc.$validate();
					}
				});
				
			},
			defaultOptions: {
				templateOptions: {
					type: 'file',
					required: true
				}
			}
		});

		formlyConfigProvider.setType({
			name: 'queryBuilderSelect',
			extends: 'select',
			controller: function($scope, $parse) {
				// angular-formly's options.defaultValue appears to ignore current model values,
				// so we must implement our own default value feature here
				var getter = $parse($scope.options.key);
				var setter = getter.assign;
				if(getter($scope.model) === undefined) {
					// no model value found, so set field value to default value
					setter($scope.model, $scope.options.data.defaultValue);
				}
			}
		});

		formlyConfigProvider.setType({
			name: 'queryRow',
			templateUrl: '/assets/hmdc/app/components/forms/queryBuilder.type.tpl.html',
			controller: /* @ngInject */ function($scope) {
				$scope.formOptions = {formState: $scope.formState};
				$scope.addNew = addNew;

				$scope.copyFields = copyFields;

				function copyFields(fields) {
					fields = angular.copy(fields);
					addRandomIds(fields);
					return fields;
				}

				function addNew() {
					$scope.model[$scope.options.key] = $scope.model[$scope.options.key] || [];
					var repeatsection = $scope.model[$scope.options.key];
					//var lastSection = repeatsection[repeatsection.length - 1];
					var newsection = { field: '', condition: { name: undefined, parameters: [] } };
					//if (lastSection) {
					//	newsection = angular.copy(lastSection);
					//}
					repeatsection.push(newsection);
				}

				function addRandomIds(fields) {
					unique++;
					angular.forEach(fields, function(field, index) {
						if (field.fieldGroup) {
							addRandomIds(field.fieldGroup);
							return; // fieldGroups don't need an ID
						}

						if (field.templateOptions && field.templateOptions.fields) {
							addRandomIds(field.templateOptions.fields);
						}

						field.id = field.id || (field.key + '_' + index + '_' + unique + getRandomInt(0, 9999));
					});
				}

				function getRandomInt(min, max) {
					return Math.floor(Math.random() * (max - min)) + min;
				}
			}
		});
	}

})();
