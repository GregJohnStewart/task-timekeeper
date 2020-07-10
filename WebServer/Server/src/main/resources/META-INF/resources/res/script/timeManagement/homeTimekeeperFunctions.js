
var timekeeperData = {};
var selectedPeriod = null;
var loggedInContent = $("#loggedInContent");
var contentSpinner = new Spinner(spinnerOpts);

var selectedPeriodTab = $("#selectedPeriodTab");
var periodsTab = $("#periodsTab");

function setTimekeeperDataFromResponse(data){
	timekeeperData = data;
}

function getManagerData(){
	return timekeeperData.timeManagerData;
}

function getStatsData(){
	return timekeeperData.stats;
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

	selectFirstPeriodIfNoneSelected();
	manageSelectedTabView();

	loadTaskData();
	loadPeriodData();
	loadSelectedPeriodData();

	doneLoading();
}

function selectFirstPeriodIfNoneSelected(){
	var managerData = getManagerData();
	var haveWorkPeriods = managerData.workPeriods.length > 0;

	if(!haveWorkPeriods){
		selectedPeriod = null;
	}else if(selectedPeriod == null){
		selectedPeriod = managerData.workPeriods.length;
	}
}

function manageSelectedTabView(){
	if(selectedPeriod == null){
		selectedPeriodTab.prop("disabled", true);
		selectedPeriodTab.addClass("disabled");
		periodsTab.tab("show");
	}else{
		selectedPeriodTab.prop("disabled", false);
		selectedPeriodTab.removeClass("disabled");
	}
}

function getTimekeeperData(toCall){
	doTimeManagerRestCall({
		spinnerContainer: null,
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

function doTimeManagerRestCall({
	spinnerContainer = null,
	url = "/api/timeManager/manager",
	timeout = (5*60*1000),
	method = 'GET',
	data = null,
	authorized = true,
	done,
	fail,
	failNoResponse = null,
	failNoResponseCheckStatus = true,
	extraHeaders = {}
} = {}){
	var extraHeaders = {
		...extraHeaders,
		...{
			provideStats: true,
			sanitizeText: true
		}
	}

	doRestCall({
		spinnerContainer: spinnerContainer,
		url: url,
		timeout: timeout,
		method: method,
		data: data,
		authorized: true,
		done: done,
		fail: fail,
		failNoResponse: failNoResponse,
		failNoResponseCheckStatus: failNoResponseCheckStatus,
		extraHeaders: extraHeaders
	});
}