function savePanelEnabler(){
	  
	  console.log("Current Panel Status:" + " " + $('#panel_checker').is(':checked'));

		    ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
		        success: function(storageAPI) {
		          storageAPI.action({
		            request: new ActionRequest(null, null, ".", "saveString", [new ValueParameter("ControlPanel"), new ValueParameter($('#panel_checker').is(':checked'))]),
		            success: function() {
		              console.log('Successfully executed requestValueUpdate for sonos toggler');
		            },
		            error: function(storageAPI, responseRequestID, responseErrorCode, responseError) {
		              console.log("Requesting value update of ", device, " failed due to ",
		                responseErrorCode, ": ", responseError);
		            }
		          });
		         }
		        });  
}


function loadPanelEnabler(){
	
	  ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
	      success: function(storageAPI) {
	        console.log("Test: get success for function 1 of panel toggle");
	        storageAPI.action({
	          request: new ActionRequest(null, null, ".", "loadString", [new ValueParameter("ControlPanel")]),
	          success: function(panel_status) {
	            console.log('ControlPanel: '+ panel_status);
	            var panel_status_as_bool = (panel_status === 'true');
	              $(document).ready(function() {
	                $('#panel_checker').prop('checked', panel_status_as_bool);
	                
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
