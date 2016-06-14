(function() {
	'use strict';
	angular.module('hmdc.style')
		.controller('StyleController', StyleController);

	function StyleController($rootScope, $scope, $log, Search, $sce, ngDialog) {
		
		$scope.css = 'spacelab';

		$scope.boots = [
			{ name: "Cerulean", url: "cerulean" },
			{ name: "Cosmo", url: "cosmo" },
			{ name: "Cyborg", url: "cyborg" },
			{ name: "Darkly", url: "darkly" },
			{ name: "Flatly", url: "flatly" },
			{ name: "Journal", url: "journal" },
			{ name: "Lumen", url: "lumen" },
			{ name: "Paper", url: "paper" },
			{ name: "Readable", url: "readable" },
			{ name: "Sandstone", url: "standstone" },
			{ name: "Simplex", url: "simplex" },
			{ name: "Slate", url: "slate" },
			{ name: "Spacelab", url: "spacelab" },
			{ name: "Superhero", url: "superhero" },
			{ name: "United", url: "united" },
			{ name: "Yeti", url: "yeti" }
		];


	}

})();
