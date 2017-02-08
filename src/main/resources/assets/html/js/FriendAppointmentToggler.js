function saveFriendEnabler(){
	  
	  console.log("Friend Appointments selected:" + " " + $('#yellow').is(':checked'));

		    ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
		        success: function(storageAPI) {
		          storageAPI.action({
		            request: new ActionRequest(null, null, ".", "saveString", [new ValueParameter("Friend"), new ValueParameter($('#yellow').is(':checked'))]),
		            success: function() {
		              console.log('Successfully executed requestValueUpdate for friend appointments');
		            },
		            error: function(storageAPI, responseRequestID, responseErrorCode, responseError) {
		              console.log("Requesting value update of ", device, " failed due to ",
		                responseErrorCode, ": ", responseError);
		            }
		          });
		         }
		        });  
}


function loadFriendEnabler(){
	
	  ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
	      success: function(storageAPI) {
	        console.log("Test: get success for function 1 of friend appointments");
	        storageAPI.action({
	          request: new ActionRequest(null, null, ".", "loadString", [new ValueParameter("Friend")]),
	          success: function(friend_status) {
	            console.log('Friend status: '+ friend_status);
	            var friend_status_as_bool = (friend_status === 'true');
	              $(document).ready(function() {
	                $('#yellow').prop('checked', friend_status_as_bool);
	                
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
