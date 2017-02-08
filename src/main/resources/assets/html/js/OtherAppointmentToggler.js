function saveOtherEnabler(){
	  
	  console.log("Other Appointments selected:" + " " + $('#blue').is(':checked'));

		    ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
		        success: function(storageAPI) {
		          storageAPI.action({
		            request: new ActionRequest(null, null, ".", "saveString", [new ValueParameter("Other"), new ValueParameter($('#blue').is(':checked'))]),
		            success: function() {
		              console.log('Successfully executed requestValueUpdate for other appointments');
		            },
		            error: function(storageAPI, responseRequestID, responseErrorCode, responseError) {
		              console.log("Requesting value update of ", device, " failed due to ",
		                responseErrorCode, ": ", responseError);
		            }
		          });
		         }
		        });  
}


function loadOtherEnabler(){
	
	  ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
	      success: function(storageAPI) {
	        console.log("Test: get success for function 1 of other appointments");
	        storageAPI.action({
	          request: new ActionRequest(null, null, ".", "loadString", [new ValueParameter("Other")]),
	          success: function(other_status) {
	            console.log('Other status: '+ other_status);
	            var other_status_as_bool = (other_status === 'true');
	              $(document).ready(function() {
	                $('#blue').prop('checked', other_status_as_bool);
	                
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
