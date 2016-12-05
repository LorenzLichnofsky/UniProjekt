(function() {
	'use strict';
	
	var exampleApp = angular.module('exampleApp');

	
	exampleApp.controller('MirrorCheckboxController', ['$scope', function($scope) {
		$scope.settings = [{
	        name: 'Weather Forecast',
	        value: '',
	        id: 'mirror-1'
	    }, {
	        name: 'Time',
	        value: '',
		    id: 'mirror-2'
	    }, {
	        name: 'Traffic Situation',
	        value: '',
		    id: 'mirror-3'
	    }, {
	        name: 'Personal Schedule',
	        value: '',
		    id: 'mirror-4'
	    }];
		
		
		$scope.saveSetting = function() {
		    $cookies.settings = $scope.settings;
		    $scope.greeting = 'Hola!';
		};
		
		$scope.loadSetting = function() {
		     $scope.settings = $cookies.settings;
		};
      }]);
	
	exampleApp.controller('TVCheckboxController', ['$scope', function($scope) {
		$scope.settings = [{
	        name: 'Pop-Up Reminder',
	        value: '',
	        id: 'tv-1'
	    }, {
	        name: 'Auto Turn-Off',
	        value: '',
		    id: 'tv-2'
	    }, {
	        name: 'Child Proof-Lock',
	        value: '',
		    id: 'tv-3'
	    }];
      }]);
	
		

})();













