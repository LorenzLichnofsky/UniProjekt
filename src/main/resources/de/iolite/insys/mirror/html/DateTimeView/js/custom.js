var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

showDateAndTime();


window.setInterval(function() {
  // var myDate = new Date().toTimeString().replace(/.*(\d{2}:\d{2}:\d{2}).*/, "$1");
  // $('#center').html(myDate);
  showDateAndTime();
}, 1000);


function showDateAndTime(){
  var dateNow = new Date();
  var month = months[dateNow.getMonth()];
  var date = dateNow.getDate();
  var year = dateNow.getFullYear();

  var datum = date + ', ' + month + ' ' + year;
  var time = dateNow.toTimeString().replace(/.*(\d{2}:\d{2}:\d{2}).*/, "$1");
  var msg = '<div class="date">' + datum + '</div>';
  msg += '<div class="time">' + time + '</div>';
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
