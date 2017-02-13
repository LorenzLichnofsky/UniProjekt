function saveMirror_TrafficEnabler(){
	  
	  console.log("Current Mirror_Traffic Status:" + " " + $('#mirror-3').is(':checked'));

		    ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
		        success: function(storageAPI) {
		          storageAPI.action({
		            request: new ActionRequest(null, null, ".", "saveString", [new ValueParameter("Mirror_Traffic"), new ValueParameter($('#mirror-3').is(':checked'))]),
		            success: function() {
		              console.log('Successfully executed requestValueUpdate for traffic toggler');
		            },
		            error: function(storageAPI, responseRequestID, responseErrorCode, responseError) {
		              console.log("Requesting value update of ", device, " failed due to ",
		                responseErrorCode, ": ", responseError);
		            }
		          });
		         }
		        });  
}


function loadMirror_TrafficEnabler(){
	
	  ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
	      success: function(storageAPI) {
	        console.log("Test: get success for function 1 of mirror_traffic toggle");
	        storageAPI.action({
	          request: new ActionRequest(null, null, ".", "loadString", [new ValueParameter("Mirror_Traffic")]),
	          success: function(traffic_status) {
	            console.log('Mirror_Traffic: '+ traffic_status);
	            var traffic_status_as_bool = (traffic_status === 'true');
	              $(document).ready(function() {
	                $('#mirror-3').prop('checked', traffic_status_as_bool);
	                
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