function saveMirrorEnabler(){
	  
	  console.log("Current Mirror Status:" + " " + $('#mirror_checker').is(':checked'));

		    ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
		        success: function(storageAPI) {
		          storageAPI.action({
		            request: new ActionRequest(null, null, ".", "saveString", [new ValueParameter("Mirror"), new ValueParameter($('#mirror_checker').is(':checked'))]),
		            success: function() {
		              console.log('Successfully executed requestValueUpdate for mirror toggler');
		            },
		            error: function(storageAPI, responseRequestID, responseErrorCode, responseError) {
		              console.log("Requesting value update of ", device, " failed due to ",
		                responseErrorCode, ": ", responseError);
		            }
		          });
		         }
		        });  
}


function loadMirrorEnabler(){
	
	  ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
	      success: function(storageAPI) {
	        console.log("Test: get success for function 1 of mirror toggle");
	        storageAPI.action({
	          request: new ActionRequest(null, null, ".", "loadString", [new ValueParameter("Mirror")]),
	          success: function(mirror_status) {
	            console.log('Mirror: '+ mirror_status);
	            var mirror_status_as_bool = (mirror_status === 'true');
	              $(document).ready(function() {
	                $('#mirror_checker').prop('checked', mirror_status_as_bool);
	                
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
