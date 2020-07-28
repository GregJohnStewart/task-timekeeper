
var timekeeperData = {};
var selectedPeriod = null;
var loggedInContent = $("#loggedInContent");
var contentSpinner = new Spinner(spinnerOpts);

var selectedPeriodTab = $("#selectedPeriodTab");
var periodsTab = $("#periodsTab");
var lastDataLoadSpan = $("#lastDataLoadSpan");
var lastDataChangeSpan = $("#lastDataChangeSpan");

function setTimekeeperDataFromResponse(data){
	console.log("Got new manager data.");
	timekeeperData = data;
	lastDataLoadSpan.text(moment(new Date()).format("YYYY-MM-DD HH:mm:ss"));
	lastDataChangeSpan.text(moment(new Date(data.lastUpdated)).format("YYYY-MM-DD HH:mm:ss"));
}

function getManagerData(){
	return timekeeperData.timeManagerData;
}

function getStatsData(){
	return timekeeperData.stats;
}

function getSelectedPeriodData(){
	if(selectedPeriod == null){
		return null;
	}
	return getManagerData().workPeriods[getManagerData().workPeriods.length - selectedPeriod]
}

function getSelectedPeriodStats(){
	if(selectedPeriod == null){
		return null;
	}
	return getStatsData().periodStats[selectedPeriod]
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
		selectedPeriod = 1;
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

	setupSelectedTimespanUi();

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

function getAttStringFromAttEditTable(attTableContent){
	var attributes = "";

	var attNameInputs = attTableContent.find(".attNameInput");
	var attValueInputs = attTableContent.find(".attValueInput");

	for(i = 0; i < attNameInputs.length; i++){
		attributes += attNameInputs.get(i).value + "," + attValueInputs.get(i).value + ";";
		}

	if(attributes == ""){
		attributes = ";";
	}

	return attributes;
}

function clearAllData(){
	console.log("Clearing ALL manager data.");
	markScreenAsLoading();

	if(!confirm("Are you sure you want to clear ALL your data?\nThis cannot be undone!")){
		console.log("User canceled clearing the data.");
		doneLoading();
		return false;
	}
	doTimeManagerRestCall({
		spinnerContainer: null,
		url: "/api/timeManager/manager/action",
		method: 'PATCH',
		data: {
			actionConfig: {
				specialAction: "clearAllManagerData"
			}
		},
		done: function(data){
			console.log("Successful clear all data request: " + JSON.stringify(data));
			setTimekeeperDataFromResponse(data);
			refreshPageData();
		},
		fail: function(data){
			console.warn("Bad response from clearing all data: " + JSON.stringify(data));
			doneLoading();
			addMessage("danger", data.responseJSON.errOut);
		},
	});
}