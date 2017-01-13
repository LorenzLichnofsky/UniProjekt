(function() {
	'use strict';
	
	var exampleApp = angular.module('exampleApp');

	
	exampleApp.controller('MirrorCheckboxController', ['$scope', function($scope) {
		
		$scope.checkedElements = {};
		
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
		
		
	    // the method to display or hide the event container
	    $scope.showEvents = function(obj) {
	      return (!obj['mirror-4']);
	    };

      }]);
			

})();













