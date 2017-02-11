function saveURI(){
	  
	  console.log("Current URI:" + " " + $('#SongURI').val());

		    ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
		        success: function(storageAPI) {
		          storageAPI.action({
		            request: new ActionRequest(null, null, ".", "saveString", [new ValueParameter("SonosURI"), new ValueParameter( $('#SongURI').val())]),
		            success: function() {
		              console.log('Successfully executed requestValueUpdate for sonos URI');
		            },
		            error: function(storageAPI, responseRequestID, responseErrorCode, responseError) {
		              console.log("Requesting value update of ", device, " failed due to ",
		                responseErrorCode, ": ", responseError);
		            }
		          });
		         }
		        });  
}


function loadURI(){
	
	  ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
	      success: function(storageAPI) {
	        console.log("Test: get success for function 1 of sonos URI");
	        storageAPI.action({
	          request: new ActionRequest(null, null, ".", "loadString", [new ValueParameter("SonosURI")]),
	          success: function(uri_value) {
	            console.log('SonosURI: '+ uri_value);
	            
	              $(document).ready(function() {
	            	  $('#SongURI').val(uri_value);   
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
