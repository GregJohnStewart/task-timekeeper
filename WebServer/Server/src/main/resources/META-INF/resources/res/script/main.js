var loginToken = Cookies.get("loginToken");

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
});

setInterval(function(){
    getServerStatus()
}, (10 * 60 * 1000));