function saveUniversityEnabler(){
	  
	  console.log("University Appointments selected:" + " " + $('#red').is(':checked'));

		    ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
		        success: function(storageAPI) {
		          storageAPI.action({
		            request: new ActionRequest(null, null, ".", "saveString", [new ValueParameter("University"), new ValueParameter($('#red').is(':checked'))]),
		            success: function() {
		              console.log('Successfully executed requestValueUpdate for university appointments');
		            },
		            error: function(storageAPI, responseRequestID, responseErrorCode, responseError) {
		              console.log("Requesting value update of ", device, " failed due to ",
		                responseErrorCode, ": ", responseError);
		            }
		          });
		         }
		        });  
}


function loadUniversityEnabler(){
	
	  ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
	      success: function(storageAPI) {
	        console.log("Test: get success for function 1 of university appointments");
	        storageAPI.action({
	          request: new ActionRequest(null, null, ".", "loadString", [new ValueParameter("University")]),
	          success: function(university_status) {
	            console.log('University status: '+ university_status);
	            var university_status_as_bool = (university_status === 'true');
	              $(document).ready(function() {
	                $('#red').prop('checked', university_status_as_bool);
	                
	              });
	          },
	          error: function(storageAPI, responseRequestID, responseErrorCode, responseError) {
	            console.log("Requesting value update of ", device, " failed due to ",
	              responseErrorCode, ": ", responseError);
	          }
	        });

	      }
	 });
	}
