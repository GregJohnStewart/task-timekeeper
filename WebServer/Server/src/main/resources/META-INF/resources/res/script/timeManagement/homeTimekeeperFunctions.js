
var timekeeperData = {};
var selectedPeriod = null;
var loggedInContent = $("#loggedInContent");
var contentSpinner = new Spinner(spinnerOpts);

function setTimekeeperDataFromResponse(data){
	timekeeperData = data;
}

function getManagerData(){
	return timekeeperData.timeManagerData
}

function markScreenAsLoading(){
	console.log("Marking page as loading.");
	loggedInContent.fadeTo(0.25, 0.15);
	contentSpinner.spin(loggedInContent.get(0));
}
function doneLoading(){
	console.log("Clearing loading marks.");
	loggedInContent.fadeTo(0.25, 1);
	contentSpinner.stop();
}

function setupTimekeepingData(){
	markScreenAsLoading();
	getTimekeeperData(function(){refreshPageData()});
}

function refreshPageData(){
	console.log("Refreshing page data.");

	loadTaskData();

	doneLoading();
}


function getTimekeeperData(toCall){
	doRestCall({
		spinnerContainer: null,
		url: "/api/timeManager/manager",
		authorized: true,
		done: function(data){
			console.log("Got timekeeper data: " + JSON.stringify(data));
			setTimekeeperDataFromResponse(data)
			toCall();
		},
		fail: function(data){
			console.warn("Bad response from getting timekeeper data: " + JSON.stringify(data));
			timekeeperData = {};
			//TODO:: handle
		},
	});
}

function initTimekeeperPage(){
	taskAddEditModalForm.on("submit", function(event){sendTaskAddEditRequest(event)});


	setupTimekeepingData();

	clearTaskAddEditForm();
}