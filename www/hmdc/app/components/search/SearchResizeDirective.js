(function() {
	'use strict';
	angular.module('hmdc.search')
		.directive('resize', ['$window', function ($window) {
			return {
				link: link,
				restrict: 'A'
			};

			function link(scope, element, attrs) {
				var cellsize = 20;
				scope.vm.windowmaxcols = Math.floor(($window.innerWidth - 300) / cellsize);
				if(scope.vm.windowmaxcols < 5) scope.vm.windowmaxcols = 5;
				// Top header up to the bottom of the header line plus 40
				// Without the wrapping of: Human  Mouse: Disease Connection
				scope.vm.windowmaxrows = Math.floor(($window.innerHeight - 430) / cellsize);
				if(scope.vm.windowmaxrows < 5) scope.vm.windowmaxrows = 5;

				angular.element($window).bind('resize', function(){
					var cellsize = 20;
					scope.vm.windowmaxcols = Math.floor(($window.innerWidth - 300) / cellsize);
					if(scope.vm.windowmaxcols < 5) scope.vm.windowmaxcols = 5;
					// Top header up to the bottom of the header line plus 40
					// Without the wrapping of: Human  Mouse: Disease Connection
					scope.vm.windowmaxrows = Math.floor(($window.innerHeight - 430) / cellsize);
					if(scope.vm.windowmaxrows < 5) scope.vm.windowmaxrows = 5;

					scope.$digest();
				});
			}
		}])
})();
