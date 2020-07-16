

$( document ).ready(function() {
	console.log("Starting home initial.");

	if(userLoggedIn()){
		initTimekeeperPage();
	} else {
		document.getElementById("createAccountPassword").onchange = function(){validatePasswordInputs(document.getElementById("createAccountPassword"), document.getElementById("createAccountPasswordConfirm"))};
		document.getElementById("createAccountPasswordConfirm").onchange = function(){validatePasswordInputs(document.getElementById("createAccountPassword"), document.getElementById("createAccountPasswordConfirm"))};
	}
});

$("#homeCreateAccount").on("submit", function(event){
	event.preventDefault();
	console.log("Create Account form submitted.");

	var messageDiv = $(this).find(".form-response");
	var usernameInput = $(this).find(':input#createAccountUsername')[0];
	var emailInput = $(this).find(':input#createAccountEmail')[0];
	var passwordInput = $(this).find(':input#createAccountPassword')[0];

	console.log("Attempting to create user account...");
	
	doRestCall({
		spinnerContainer: $(this).get(0),
		url: "/api/user/registration",
		method: 'POST',
		data: {
			username : usernameInput.value,
			email : emailInput.value,
			plainPassword : passwordInput.value
		},
		done: function(data){
			console.log("Got response from registration: " + JSON.stringify(data));

			addMessageToDiv(
				messageDiv,
				"success",
				"Check your email for an email confirmation. After that, you can log in!",
				"Success!",
				"createAccountSuccessMessage"
			);
		},
		fail: function(data){
			console.warn("Bad response from registration: " + JSON.stringify(data));
			addMessageToDiv(
				messageDiv,
				"danger",
				"Error! " + data.responseText,
				data.statusText,
				"createAccountError"
			);
		}
	});

	return true;
});

//TODO:: username validation check