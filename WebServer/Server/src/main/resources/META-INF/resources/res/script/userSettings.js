
$( document ).ready(function() {
	console.log("Starting userSettings initial.");

	requireLogin();

	var newPassInput = document.getElementById("changePasswordNewPassword");
	var newPassConfirmInput = document.getElementById("changePasswordNewPasswordConfirm");

	newPassInput.onchange = function(){validatePasswordInputs(newPassInput, newPassConfirmInput)};
	newPassConfirmInput.onchange = function(){validatePasswordInputs(newPassInput, newPassConfirmInput)};
});


$("#changePasswordForm").on("submit", function(event){
	event.preventDefault();
	console.log("Password change form submitted.");

	var messageDiv = $(this).find(".form-response");
	var oldPasswordInput = $(this).find(':input#changePasswordCurrentPassword')[0];
	var newPasswordInput = $(this).find(':input#changePasswordNewPassword')[0];

	console.log("Attempting to change user password...");

	doRestCall({
		authorized: true,
		spinnerContainer: $(this).get(0),
		url: "/api/user/update/password",
		method: 'PATCH',
		data: {
			oldPlainPassword : oldPasswordInput.value,
			newPlainPassword : newPasswordInput.value
		},
		done: function(data){
			console.log("Got response from password change: " + JSON.stringify(data));

			addMessageToDiv(
				messageDiv,
				"success",
				"Your password has been updated.",
				"Success!",
				"changePasswordSuccessMessage"
			);
		},
		fail: function(data){
			console.warn("Bad response from registration: " + JSON.stringify(data));
			addMessageToDiv(
				messageDiv,
				"danger",
				"Error! " + data.responseText,
				data.statusText,
				"changePasswordError"
			);
		}
	});

	return true;
});