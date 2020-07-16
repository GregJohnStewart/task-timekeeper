var loginToken = Cookies.get("loginToken");
var getParams = new URLSearchParams(window.location.search);

var spinnerOpts = {
  lines: 20, // The number of lines to draw
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
	return loginToken != null;
}

function logout(){
	console.log("Logging out user.");
	Cookies.remove('loginToken');
	location.reload(true);
}

function requireLogin(){
	if(!userLoggedIn()){
		console.error("User must be logged in to view this page.");
		window.location.replace("/?responseType=danger&message=Must%20be%20logged%20in%20to%20access%20that%20page.");
	} else {
	}
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
	failNoResponse = null,
	failNoResponseCheckStatus = true,
	extraHeaders = {}
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
		console.log("Sending json data: " + ajaxOps.data);
	}

	if(authorized){
		extraHeaders = {
			...extraHeaders,
			...{Authorization: "Bearer " + loginToken}
		}
	}

	ajaxOps.headers = extraHeaders;

	$.ajax(
		ajaxOps
	).done(function(data){
		console.log("Got successful response: " + JSON.stringify(data));
		done(data);
	}).fail(function(data){
		console.warn("Request failed: " + JSON.stringify(data));

		var response = data.responseJSON;

		if(data.status == 0){ // no response from server
			if(failNoResponseCheckStatus){
				getServerStatus();
			}
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
		failNoResponseCheckStatus: false,
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
	$('<div '+id+' class="alert alert-'+type+' alert-dismissible fade show alertMessage" role="alert">\n'+
		'<button type="button" class="close messageClose" data-dismiss="alert" aria-label="Close">\n'+
			'<span aria-hidden="true">&times;</span>\n'+
		'</button>\n' +
		heading + "\n" +
		'<span class="message">\n' +
		message + "\n" +
		'</span>\n' +
		'</div>').appendTo(jqueryObj.get(0))
}
function addMessage(type, message, heading, id){
	addMessageToDiv(messageDiv, type, message, heading, id);
}

function hasResponseMessage(){
	return getParams.has("responseType");
}

$(document).ready(function() {
	console.log("Starting main initial.");

	$('[data-toggle="popover"]').popover({html:true});
	getServerStatus();

	var loginText = $("#loginNavText");

	if(userLoggedIn()){
		console.log("User logged in.");
		loginText.html('Logged in as: <span id="navUsername"></span>');

		$(".loggedInContent").each(function(i, obj){
			$(obj).show();
		});
		
		doRestCall({
			spinnerContainer: null,
			url: "/api/user/info/self",
			authorized: true,
			done: function(data){
				console.log("Got response from getting the user's info request: " + JSON.stringify(data));

				$("#navUsername").text(data.username)
			},
			fail: function(data){
				console.warn("Bad response from getting user info attempt: " + JSON.stringify(data));
				logout();
			},
		});
	} else {
		console.log("User NOT logged in.");

		loginText.text("Login");

		$(".loggedOutContent").each(function(i, obj){
			$(obj).show();
		});
	}

	var uri = window.location.toString();
	if (uri.indexOf("?") > 0) {
		var clean_uri = uri.substring(0, uri.indexOf("?"));
		window.history.replaceState({}, document.title, clean_uri);
	}

	if(hasResponseMessage()){
		addMessage(
			getParams.get("responseType"),
			getParams.get("message")
		);
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

	var responseDiv = $(this).find("div.form-response");

	var usernameEmailInput = $(this).find(':input.loginEmailUsername')[0];
	var passwordInput = $(this).find(':input.loginPassword')[0];
	var stayLoggedInInput = $(this).find(':input.loginStayLoggedIn')[0];

	console.log("Attempting to log user in...");

	doRestCall({
		spinnerContainer: $(this).get(0),
		url: "/api/user/auth/login",
		method: 'POST',
		data: {
			extendedTimeout: stayLoggedInInput.checked,
			plainPass: passwordInput.value,
			user: usernameEmailInput.value
		},
		done: function(data){
			console.log("Got response from login request: " + JSON.stringify(data));
			var cookieOps = {expires: 1};//TODO:: make secure? broke tests to have the secure token

			if(stayLoggedInInput.checked){
				console.debug("User chose to stay logged in.");
				cookieOps.expires = 31;
			}

			Cookies.set("loginToken", data.token, cookieOps);
			window.location.reload(false);
		},
		fail: function(data){
			console.warn("Bad response from login attempt: " + JSON.stringify(data));
			addMessageToDiv(
				responseDiv,
				"danger",
				"Error! " + data.responseText,
				data.statusText,
				"createAccountError"
			);
		}
	});
	return true;
});