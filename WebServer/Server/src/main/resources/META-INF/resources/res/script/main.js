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

$( document ).ready(function() {
    console.log("Starting main initial.");

    var loginText = $("#loginNavText");

    if(userLoggedIn()){
        console.log("User logged in.");

        loginText.html('Logged in as: <span id="navUsername"></span>');

        //TODO:: get user info to fill out the rest, make dropdown logout

    } else {
        console.log("User NOT logged in.");

        loginText.text("Login");
    }
});