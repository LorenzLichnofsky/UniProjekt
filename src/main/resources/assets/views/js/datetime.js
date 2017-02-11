/** Original idea taken from Hendrik Motza
Edited by Calender Integration Group */


var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
var days = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday']

showDateAndTime();


window.setInterval(function() {
  showDateAndTime();
}, 1000);


function showDateAndTime(){
  var dateNow = new Date();
  var month = months[dateNow.getMonth()];
  var day = days[dateNow.getDay()];
  var date = dateNow.getDate();
  var year = dateNow.getFullYear();

  var datum = day + ", " + date + ' ' + month + ' ' + year;
  var time = dateNow.toTimeString().replace(/.*(\d{2}:\d{2}:\d{2}).*/, "$1");
  var msg = '<div class="date">' + datum + '</div>';
  msg += '<div class="time">' + time + '</div>';
  $('#center').html(msg);
}