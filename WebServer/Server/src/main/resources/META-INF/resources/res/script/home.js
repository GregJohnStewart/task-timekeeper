
$( document ).ready(function() {
    console.log("Starting home initial.");

    if(userLoggedIn()){
        console.log("Showing logged in content.");
        $("#loggedInContent").show();
    } else {
        console.log("Showing logged out content.");
        $("#loggedOutContent").show();

        document.getElementById("createAccountPassword").onchange = validatePassword;
        document.getElementById("createAccountPasswordConfirm").onchange = validatePassword;
    }
});

$("#homeCreateAccount").on("submit", function(event){
    event.preventDefault();
    console.log("Create Account form submitted.");

    var spinner = new Spinner(spinnerOpts).spin($(this).get(0));

    var messageDiv = $(this).find(".form-response");
    var usernameInput = $(this).find(':input#createAccountUsername')[0];
    var emailInput = $(this).find(':input#createAccountEmail')[0];
    var passwordInput = $(this).find(':input#createAccountPassword')[0];

    console.log("Attempting to create user account...");

    $.ajax({
        url: "/api/user/registration",
        method: "POST",
        contentType: "application/json; charset=UTF-8",
        dataType: 'json',
        data : JSON.stringify({
            username : usernameInput.value,
            email : emailInput.value,
            plainPassword : passwordInput.value
        })
    }).done(function(data){
        console.log("Got response from registration: " + JSON.stringify(data));

        addMessageToDiv(
            messageDiv,
            "success",
            "Check your email for an email confirmation. After that, you can log in!",
            "Success!"
        );
        spinner.stop();
    }).fail(function(data){
        console.warn("Bad response from registration: " + JSON.stringify(data));

        var code = data.status;
        var statusText = data.statusText;
        var responseText = data.responseText;

        addMessageToDiv(
            messageDiv,
            "danger",
            "Error! " + responseText,
            statusText
        );
        spinner.stop();
    });

    return true;
});

//TODO:: password validation check?
function validatePassword(){
    var pass2=document.getElementById("createAccountPassword").value;
    var pass1=document.getElementById("createAccountPasswordConfirm").value;

    if(pass1 != pass2) {
        document.getElementById("createAccountPasswordConfirm").setCustomValidity("Passwords Don't Match");
    } else {
        document.getElementById("createAccountPasswordConfirm").setCustomValidity('');
    }
}

//TODO:: username validation check