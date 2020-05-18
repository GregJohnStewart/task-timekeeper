$( document ).ready(function() {
    console.log("Starting home initial.");

    if(userLoggedIn()){
        console.log("Showing logged in content.");
        $("#loggedInContent").show();
    } else {
        console.log("Showing logged out content.");
        $("#loggedOutContent").show();
    }
});