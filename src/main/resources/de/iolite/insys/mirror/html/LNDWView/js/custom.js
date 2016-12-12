function showWelcomeMessage(){
  var msg = '<div class="greeting"> Guten Tag </div>';

  ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
      success: function(storageAPI) {
        console.log("Test: get function 1 success");
        storageAPI.action({
          request: new ActionRequest(null, null, ".", "loadString", [new ValueParameter("uName")]),
          success: function(username) {
            console.info('Username: '+username);
              $(document).ready(function() {
				  msg += '<div class="uName">' + $('#name').attr('value') + '</div>';
              });
          },
          error: function(storageAPI, responseRequestID, responseErrorCode, responseError) {
            console.error("Requesting value update of ", device, " failed due to ",
              responseErrorCode, ": ", responseError);
          }
        });

      }
 });

  $('#center').html(msg);
}


function getParameterByName(name) {
  name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
  var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
    results = regex.exec(location.search);
  return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}


var row = getParameterByName('mirror_rows');
var col = getParameterByName('mirror_columns');
var pos = getParameterByName('mirror_pos');

pos_col = pos % col;
pos_row = parseInt (pos / row);
console.log(pos_col + " " + pos_row);

if (pos){
  var pos_class = "center";
  if (pos_col == 0 && pos_row == 0){
    // upper-left
    pos_class = "upper-left";
    console.log(pos_class);
  } else if (pos_col == col - 1 && pos_row == 0) {
    // upper-right
    pos_class = "upper-right";
    console.log(pos_class);
  } else if (pos_col == 0 && pos_row == row - 1) {
    // bottom-left
    pos_class = "bottom-left";
    console.log(pos_class);
  } else if (pos_col == col - 1 && pos_row == row - 1) {
    // bottom-right
    pos_class = "bottom-right";
    console.log(pos_class);
  }
  $('#center').addClass(pos_class);
}

// if (pos) {
//     var pos_class;
//     if (pos == 0) {
//       pos_class = "upper-left";
//       console.log(pos_class);
//     }
//     if (pos == 1) {
//       pos_class = "upper-right";
//       console.log(pos_class);
//     }
//     if (pos == 2) {
//       pos_class = "bottom-left";
//       console.log(pos_class);
//     }
//     if (pos == 3) {
//       pos_class = "bottom-right";
//       console.log(pos_class);
//     }
//     $('#center').addClass(pos_class);
// }

// console.log(loc);
