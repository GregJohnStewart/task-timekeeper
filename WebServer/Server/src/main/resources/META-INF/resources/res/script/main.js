var loginToken = Cookies.get("loginToken");

var spinnerOpts = {
  lines: 13, // The number of lines to draw
  length: 38, // The length of each line
  width: 17, // The line thickness
  radius: 45, // The radius of the inner circle
  scale: 1, // Scales overall size of the spinner
  corners: 1, // Corner roundness (0..1)
  color: '#000000', // CSS color or array of colors
  fadeColor: 'transparent', // CSS color or array of colors
  speed: 1, // Rounds per second
  rotate: 0, // The rotation offset
  animation: 'spinner-line-shrink', // The CSS animation name for the lines
  direction: 1, // 1: clockwise, -1: counterclockwise
  zIndex: 2e9, // The z-index (defaults to 2000000000)
  className: 'spinner', // The CSS class to assign to the spinner
  top: '50%', // Top position relative to parent
  left: '50%', // Left position relative to parent
  shadow: '0 0 1px transparent', // Box-shadow for the lines
  position: 'absolute' // Element positioning
};


function userLoggedIn(){
    //TODO:: if cookie not null, do token check call
    return loginToken != null;
}

function logout(){
    console.log("Logging out user.");
    Cookies.remove('loginToken');
    location.reload(true);
}

var serverStatusChecking = $("#serverStatusChecking");
var serverStatusDown = $("#serverStatusDown");
var serverStatusUp = $("#serverStatusUp");
var serverStatus = $("#serverStatus");
function getServerStatus(){
    console.log("Getting health status from server...");

    $.ajax({
        url: "/health",
        timeout: (5*60*1000) //in milliseconds
    }).done(function(data){
        console.log("Health result from server was UP: " + JSON.stringify(data));
        serverStatusChecking.hide();
        serverStatusDown.hide();
        serverStatusUp.show();
        serverStatus.attr("data-content", "Server status is UP");
    }).fail(function(data){
        console.warn("Health result from server was DOWN: " + JSON.stringify(data));
        serverStatusChecking.hide();
        serverStatusDown.show();
        serverStatusUp.hide();

        var output = "Server status is DOWN:<br /><br />Failed the following checks:<ul>";

        var response = data.responseJSON;

        response.checks.forEach(function(entry, index){
            if(entry.status !== "UP"){
                output += "<li>" + entry.name + " (" + entry.status + ")</li>";
            }
        });

        output += "</ul>";
        serverStatus.attr("data-content", output);
    });
}

var messageDiv = $("#messageDiv")
function addMessage(type, message, heading){
    if(heading != null){
        heading = '<h4 class="alert-heading">'+heading+'</h4>';
    }else{
        heading = "";
    }
    $('<div class="alert alert-'+type+' alert-dismissible fade show" role="alert">\n'+
         heading + "\n" +
         message + "\n" +
         '<button type="button" class="close" data-dismiss="alert" aria-label="Close">\n'+
           '<span aria-hidden="true">&times;</span>\n'+
         '</button>\n' +
       '</div>').appendTo(messageDiv.get(0))
}

$(document).ready(function() {
    console.log("Starting main initial.");

    $('[data-toggle="popover"]').popover({html:true});
    getServerStatus();

    var loginText = $("#loginNavText");

    if(userLoggedIn()){
        console.log("User logged in.");

        loginText.html('Logged in as: <span id="navUsername"></span>');
        $("#navbarLogoutContent").show();

        //TODO:: get user info to fill out the rest, make dropdown logout

    } else {
        console.log("User NOT logged in.");

        loginText.text("Login");
        $("#navbarLoginContent").show();
    }
//  debug
//    addMessage("success","Welcome!", "Welcome to the server");

    $('<span id="loadedFlag"></span>').appendTo(document.body);
});

setInterval(function(){
    getServerStatus()
}, (10 * 60 * 1000));

$(".loginForm").on("submit", function(event){
    event.preventDefault();
    console.log("Login form submitted.");

    var spinner = new Spinner(spinnerOpts).spin($(this).get(0));

    var usernameEmailInput = $(this).find(':input.loginEmailUsername')[0];
    var passwordInput = $(this).find(':input.loginPassword')[0];
    var stayLoggedInInput = $(this).find(':input.loginStayLoggedIn')[0];

    console.log("Attempting to log user in...");

    return true;
});