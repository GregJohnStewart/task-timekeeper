
function validatePasswordInputs(pass1, pass2){
	if(pass1.value != pass2.value) {
		pass2.setCustomValidity("Passwords Don't Match");
	} else {
		pass2.setCustomValidity('');
	}
}