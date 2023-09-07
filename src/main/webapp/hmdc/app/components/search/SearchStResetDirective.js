(function() {
	'use strict';
	angular.module('hmdc.search')
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
							if(attr.stReset == "vm.resetDiseaseTable") {
								tableState.sort = {predicate: "term", reverse: false};
							} else if(attr.stReset == "vm.resetGeneTable") {
								tableState.sort = {predicate: "symbol", reverse: false};
							}
							ctrl.pipe();
							// reset scope value
							scope.stReset = false;
						}

					});
				}
			};
		});

})();
