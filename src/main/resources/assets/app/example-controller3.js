/**
 * Controller for User Interface
 * @author: Lorenz Lichnofsky
 */

(function() {
	'use strict';
	
	var exampleApp = angular.module('exampleApp');

	
	exampleApp.controller('MirrorCheckboxController', ['$scope', function($scope) {
		
		$scope.checkedElements = {};

		$scope.settings = [{
	        name: 'Weather',
	        value: '',
	        id: 'mirror-1'
	    }, {
	        name: 'Date and Clock',
	        value: '',
		    id: 'mirror-2'
	    }, {
	        name: 'Traffic Situation',
	        value: '',
		    id: 'mirror-3'
	    }, {
	        name: 'Personal Calendar',
	        value: '',
		    id: 'mirror-4'
	    }];

	    
	    // storage functions for checkboxes
	    $scope.callSaveFunction = function(id) {
	    	if (id == "mirror-1"){
	    		 saveMirror_WeatherEnabler();
	    	}
	    	if (id == "mirror-2"){
	    		 saveMirror_ClockEnabler();
	    	}
	    	if (id == "mirror-3"){
	    		 saveMirror_TrafficEnabler();
	    	}
	    	if (id == "mirror-4"){
	    		 saveMirror_CalendarEnabler();
	    	}   
	    };

	    
	    $(document).ready(function () {
	    	
	    	// by clicking the edit button, the URI-textfield will be editable for the user
	    	$('#editButton').click(function () {
	    		  $('#SongURI').removeAttr('readonly');
	    		  $("#SongURI").css("background-color", "white");
	    		  $('#SongURI').focus();
	    	})
	    	
	    	// by clicking the save button, the URI-textfield won't be editable for the user anymore
	    	$('#saveButton').click(function () {
	    		  $('#SongURI').prop('readonly', true);
	    		  $("#SongURI").css("background-color", "#F5F5F5");
	    	})
	    	
	    	
		    // the method to display or hide the event container
		    $scope.showEvents = function(obj) {

		    	return (obj['mirror-4']);
		    		
		    };

	  	  /* handler for clicking-events of the mirror's enable/disable toggler */	  
	  	  $('#mirror_checker').click(function(){		

	  			 var $this = $(this);
	  		     if ($this.is(':checked')) {
	  		    	 /* check the triangle checkbox*/
	  		    	 $( "#mirror_triangle" ).prop( "checked", true );
	  		    	 /* show div to select different functionalities of mirror*/
	  		    	 $(".mirrortext").show();

	  		     } else {
	  		    	 $( "#mirror_triangle" ).prop( "checked", false );
	  		    	 $(".mirrortext").hide();

	  		     }
	  	  });	  
	  	  /* handler for clicking-events of the mirror triangle */	
	  	  $('#mirror_triangle').click(function(){		

	  			 var $this = $(this);
	  		     if ($this.is(':checked')) {
	  		    	 $(".mirrortext").show();
	  		     } else {
	  		    	 $(".mirrortext").hide();

	  		     }
	  	  });    
	  	  
	  	  /* handler for clicking-events of the sonos's enable/disable toggler */	  
	  	  $('#sonos_checker').click(function(){		

	  			 var $this = $(this);
	  		     if ($this.is(':checked')) {
	  		    	 /* check the triangle checkbox*/
	  		    	 $( "#sonos_triangle" ).prop( "checked", true );
	  		    	 /* show div to select different functionalities of mirror*/
	  		    	 $(".sonostext").show();

	  		     } else {
	  		    	 $( "#sonos_triangle" ).prop( "checked", false );
	  		    	 $(".sonostext").hide();

	  		     }
	  	  });	  
	  	  /* handler for clicking-events of the sonos triangle */	
	  	  $('#sonos_triangle').click(function(){		

	  			 var $this = $(this);
	  		     if ($this.is(':checked')) {
	  		    	 $(".sonostext").show();
	  		     } else {
	  		    	 $(".sonostext").hide();

	  		     }   
	  		});	
	  	  
	  });	


      }]);
			

})();













