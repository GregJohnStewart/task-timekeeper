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

var wholeBody = $('body')

function userLoggedIn(){
	console.debug("login token: " + loginToken);
	//TODO:: if cookie not null, do token check call
	return loginToken != null;
}

function logout(){
	console.log("Logging out user.");
	Cookies.remove('loginToken');
	location.reload(true);
}

function doRestCall({
	spinnerContainer = wholeBody.get(0),
	url = null,
	timeout = (5*60*1000),
	method = 'GET',
	data = null,
	authorized = false,
	done,
	fail,
	failNoResponse = null
} = {}){
	console.log("Making rest call.");
	var spinner = (spinnerContainer === null ? null : new Spinner(spinnerOpts).spin(spinnerContainer));

	var ajaxOps = {
		url: url,
		method: method,
		timeout: timeout,
	};

	if(data != null){
		ajaxOps.contentType = "application/json; charset=UTF-8";
		ajaxOps.dataType = 'json';
		ajaxOps.data = JSON.stringify(data);
	}

	if(authorized){
		ajaxOps.headers = {
			Authorization: "Bearer " + loginToken
		}
	}

	$.ajax(
		ajaxOps
	).done(function(data){
		console.log("Got successful response: " + JSON.stringify(data));
		done(data);
	}).fail(function(data){
		console.warn("Request failed: " + JSON.stringify(data));

		var response = data.responseJSON;

		if(data.status == 0){ // no response from server
			console.info("Failed due to lack of connection to server.");
			if(failNoResponse != null){
				failNoResponse(data);
			}else{
				addMessage(
					"danger",
					"Try refreshing the page, or wait until later. Contact the server operators for help and details.",
					"Failed to connect to server."
				);
			}
		}else{
			fail(data);
		}
	}).always(function(){
		if(spinner != null){
			spinner.stop();
		}
	});
}

var serverStatusChecking = $("#serverStatusChecking");
var serverStatusDown = $("#serverStatusDown");
var serverStatusUp = $("#serverStatusUp");
var serverStatusDisconnect = $("#serverStatusDisconnect");
var serverStatus = $("#serverStatus");
var downOutputHead = "Server status is DOWN:<br /><br />";
function getServerStatus(){
	console.log("Getting health status from server...");

	doRestCall({
		spinnerContainer: null,
		url: "/health",
		timeout: (1*60*1000),
		done: function(data){
			console.log("Health result from server was UP: " + JSON.stringify(data));
			serverStatusChecking.hide();
			serverStatusDown.hide();
			serverStatusDisconnect.hide();
			serverStatusUp.show();
			serverStatus.attr("data-content", "Server status is UP");
		},
		fail: function(data){
			console.warn("Health result from server was DOWN: " + JSON.stringify(data));
			serverStatusChecking.hide();
			serverStatusUp.hide();
			serverStatusDisconnect.hide();
			serverStatusDown.show();

			var response = data.responseJSON;

			var output = downOutputHead + "Failed the following checks:<ul>";
				response.checks.forEach(function(entry, index){
					if(entry.status !== "UP"){
						output += "<li>" + entry.name + " (" + entry.status + ")</li>";
					}
				});
			output += "</ul>";
			serverStatus.attr("data-content", output);
		},
		failNoResponse: function(data){
			console.warn("Health result from server was DOWN: " + JSON.stringify(data));
			serverStatusChecking.hide();
			serverStatusUp.hide();
			serverStatusDown.hide();
			serverStatusDisconnect.show();

			var output = downOutputHead + "Did not get a response from the server. It could be down, or you have lost connection to the server.";
			serverStatus.attr("data-content", output);
		}
	});
}

var messageDiv = $("#messageDiv")
function addMessageToDiv(jqueryObj, type, message, heading, id){
	if(heading != null){
		heading = '<h4 class="alert-heading">'+heading+'</h4>';
	}else{
		heading = "";
	}
	if(id != null){
		id = 'id="'+id+'"'
	}else{
		id = "";
	}
	$('<div '+id+' class="alert alert-'+type+' alert-dismissible fade show" role="alert">\n'+
		heading + "\n" +
		message + "\n" +
		'<button type="button" class="close" data-dismiss="alert" aria-label="Close">\n'+
			'<span aria-hidden="true">&times;</span>\n'+
		'</button>\n' +
		'</div>').appendTo(jqueryObj.get(0))
}
function addMessage(type, message, heading, id){
	addMessageToDiv(messageDiv, type, message, heading, id);
}

$(document).ready(function() {
	console.log("Starting main initial.");

	$('[data-toggle="popover"]').popover({html:true});
	getServerStatus();

	var loginText = $("#loginNavText");

	if(userLoggedIn()){
		console.log("User logged in.");
		loginText.html('Logged in as: <span id="navUsername"></span>');

		//TODO:: get user info to fill out the rest, make dropdown logout
		$("#navbarLogoutContent").show();
		
		doRestCall({
			spinnerContainer: null,
			url: "/api/user/info",
			authorized: true,
			done: function(data){
				console.log("Got response from getting the user's info request: " + JSON.stringify(data));
			},
			fail: function(data){
				console.warn("Bad response from getting user info attempt: " + JSON.stringify(data));
				if(data.status == "401"){
					//logout();
				}
			},
		});
	} else {
		console.log("User NOT logged in.");

		loginText.text("Login");
		$("#navbarLoginContent").show();
	}

	$('<span id="loadedFlag"></span>').appendTo(document.body);
});

setInterval(function(){
	getServerStatus()
}, (2 * 60 * 1000));

$("#logoutButton").on("click", function(event){
	logout();
});

$(".loginForm").on("submit", function(event){
	event.preventDefault();
	console.log("Login form submitted.");

	var usernameEmailInput = $(this).find(':input.loginEmailUsername')[0];
	var passwordInput = $(this).find(':input.loginPassword')[0];
	var stayLoggedInInput = $(this).find(':input.loginStayLoggedIn')[0];

	console.log("Attempting to log user in...");

	doRestCall({
		spinnerContainer: $(this).get(0),
		url: "/api/user/auth/login",
		method: 'POST',
		data: {
			extendedTimeout: true,
			plainPass: passwordInput.value,
			user: usernameEmailInput.value
		},
		done: function(data){
			console.log("Got response from login request: " + JSON.stringify(data));
			Cookies.set("loginToken", data.token);
			window.location.reload(false);
		},
		fail: function(data){
			console.warn("Bad response from login attempt: " + JSON.stringify(data));
			var code = data.status;
			var statusText = data.statusText;
			var responseText = data.responseText;

			addMessageToDiv(
				messageDiv,
				"danger",
				"Error! " + responseText,
				statusText,
				"createAccountError"
			);
			spinner.stop();
		}
	});
	return true;
});