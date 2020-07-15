
var selectedPeriodStartSpan = $("#selectedPeriodStartSpan");
var selectedPeriodEndSpan = $("#selectedPeriodEndSpan");
var selectedPeriodDurationSpan = $("#selectedPeriodDurationSpan");
var selectedPeriodCompleteSpan = $("#selectedPeriodCompleteSpan");
var selectedPeriodAttributesTableBody = $("#selectedPeriodAttributesTableBody");
var selectedPeriodTaskStatsTableBody = $("#selectedPeriodTaskStatsTableBody");
var selectedPeriodCompleteAllButton = $("#selectedPeriodCompleteAllButton");
var selectedPeriodTimespansTableContent = $("#selectedPeriodTimespansTableContent");

var selectedPeriodEditAttsModal = $("#selectedPeriodEditAttsModal");
var selectedPeriodEditAttsModalForm = $("#selectedPeriodEditAttsModalForm");
var selectedPeriodEditAttsModalFormResponse = selectedPeriodEditAttsModalForm.find(".form-response");
var selectedPeriodEditAttsModalTableContent = $("#selectedPeriodEditAttsModalTableContent");

var addEditTimespanModal = $("#addEditTimespanModal");
var timespanAddEditModalForm = $("#timespanAddEditModalForm");
var timespanAddEditModalFormResponse = timespanAddEditModalForm.find(".form-response");
var timespanAddEditModalLabelText = $("#timespanAddEditModalLabelText");
var timespanAddEditModalIdInputGroup = $("#timespanAddEditModalIdInputGroup");
var timespanAddEditModalIdInput = $("#timespanAddEditModalIdInput");
var timespanAddEditModalTaskInput = $("#timespanAddEditModalTaskInput");
var timespanAddEditModalStartDateTimePicker = $("#timespanAddEditModalStartDateTimePicker");
var timespanAddEditModalStartInput = $("#timespanAddEditModalStartInput");
var timespanAddEditModalEndDateTimePicker = $("#timespanAddEditModalEndDateTimePicker");
var timespanAddEditModalEndInput = $("#timespanAddEditModalEndInput");

var datetimepickerOptions = {
	format: 'D/M h:mm A YYYY', //https://momentjs.com/docs/#/displaying/format/
	icons: {
		time: 'far fa-clock',
		date: 'far fa-calendar-alt',
		up: 'fas fa-arrow-up',
		down: 'fas fa-arrow-down',
		previous: 'fas fa-chevron-left',
		next: 'fas fa-chevron-right',
		today: 'far fa-calendar-check',
		clear: 'far fa-trash-alt',
		close: 'fas fa-times'
	}
};

function setupSelectedTimespanUi(){
	timespanAddEditModalForm.on("submit", function(event){sendTimespanAddEditRequest(event)});
	selectedPeriodEditAttsModalForm.on("submit", function(event){sendSelectedPeriodAttUpdateRequest(event)});

	timespanAddEditModalStartDateTimePicker.datetimepicker(datetimepickerOptions);
	timespanAddEditModalEndDateTimePicker.datetimepicker(datetimepickerOptions);

//	timespanAddEditModalStartDateTimePicker.on("change.datetimepicker", function (e) {
//		timespanAddEditModalEndDateTimePicker.datetimepicker('minDate', e.date);
//	});
//	timespanAddEditModalEndDateTimePicker.on("change.datetimepicker", function (e) {
//		timespanAddEditModalStartDateTimePicker.datetimepicker('maxDate', e.date);
//	});
}


function loadSelectedPeriodData(){
	console.log("Loading selected period data");
	clearSelectedPeriodData();

	if(selectedPeriod == null){
		console.log("No period selected. Nothing to do.");
		return;
	}

	var managerData = getManagerData();
	var curIndForKeeper = managerData.tasks.length;
	managerData.tasks.forEach(function(task){
		timespanAddEditModalTaskInput.prepend(
			'<option value="'+task.name.name+'">' +
			task.name.name +
			'</option>'
		);
		curIndForKeeper--;
	});

	var selectedPeriodData = getSelectedPeriodData();
	var selectedPeriodStats = getSelectedPeriodStats();

	console.log("Data for selected period: " + JSON.stringify(selectedPeriodData));
	console.log("Stats for selected period: " + JSON.stringify(selectedPeriodStats));

	// fill task stats table
	Object.keys(selectedPeriodStats.taskStats.valueStrings).forEach(function(key) {
		selectedPeriodTaskStatsTableBody.append(
			'<tr>' +
			'<td>' + key + '</td>' +
			'<td class="durationCol">' + selectedPeriodStats.taskStats.valueStrings[key] + '</td>' +
			'</tr>'
		);
	});

	selectedPeriodStartSpan.text(selectedPeriodData.startString);
	selectedPeriodEndSpan.text(selectedPeriodData.endString);
	selectedPeriodDurationSpan.text(selectedPeriodData.durationString);
	selectedPeriodCompleteSpan.text((selectedPeriodStats.overallStats.allComplete?"Yes":"No"));

	Object.keys(selectedPeriodData.attributes).forEach(function(key) {
		selectedPeriodAttributesTableBody.prepend(
			'<tr>' +
			'<td>'+key+'</td>' +
			'<td>'+selectedPeriodData.attributes[key]+'</td>' +
			'</tr>'
		);
		selectedPeriodAddEditAttFormAddAttribute(
			key,
			selectedPeriodData.attributes[key]
		);
	});

	curIndForKeeper = 1; //selectedPeriodData.timespans.length;
	var curIndForArray = 0;
	var allComplete = true;
	selectedPeriodData.timespans.forEach(function(curTimespan) {
		var isComplete = (curTimespan.startString != "" && curTimespan.endString != "");

		selectedPeriodTimespansTableContent.append(
			'<tr>' +
			'<td class="timespanIndexCell">'+curIndForKeeper+'</td>' +
			'<td class="timespanStartCell">'+curTimespan.startString+'</td>' +
			'<td class="timespanEndCell">'+curTimespan.endString+'</td>' +
			'<td class="durationCol">'+curTimespan.durationString+'</td>' +
			'<td class="timespanTaskCell">'+curTimespan.taskName.name+'</td>' +
			'<td class="timespanActionsCol">'+
			'<button class="btn btn-warning btn-sm" title="Edit" onclick="setupSelectedPeriodEditTimespan(this);" data-toggle="modal" data-target="#addEditTimespanModal"><i class="fas fa-pen fa-fw fa-sm"></i></button> '+
			'<button class="btn btn-info btn-sm" title="'+(isComplete?"Already completed":"Complete")+'" '+(isComplete?"disabled":'onclick="completeTimespan('+curIndForKeeper+');"')+'><i class="fas fa-check fa-fw fa-sm"></i></button> '+
			'<button class="btn btn-danger btn-sm" title="Remove" onclick="removeTimespan('+curIndForKeeper+')"><i class="fas fa-trash-alt fa-fw fa-sm"></i></button>'+
			'</td>' +
			'</tr>'
		);

		if(!isComplete){
			allComplete = false;
		}
		curIndForKeeper++;
		curIndForArray++;
	});

	selectedPeriodCompleteAllButton.prop("disabled", allComplete);

	console.log("DONE loading selected period data");
}

function clearSelectedPeriodData(){
	selectedPeriodStartSpan.empty();
	selectedPeriodEndSpan.empty();
	selectedPeriodDurationSpan.empty();
	selectedPeriodCompleteSpan.empty();
	selectedPeriodAttributesTableBody.empty();
	selectedPeriodTaskStatsTableBody.empty();
	selectedPeriodTimespansTableContent.empty();

	selectedPeriodEditAttsModalTableContent.empty();

	timespanAddEditModalTaskInput.empty();

	clearAddEditTimespanForm();
}

function setupSelectedPeriodAddEditAttForm(){
	selectedPeriodEditAttsModalTableContent.empty();
	var selectedPeriodData = getSelectedPeriodData();
	Object.keys(selectedPeriodData.attributes).forEach(function(key) {
		selectedPeriodAddEditAttFormAddAttribute(
			key,
			selectedPeriodData.attributes[key]
		);
	});
}

function selectedPeriodAddEditAttFormAddAttribute(name, value){
	selectedPeriodEditAttsModalTableContent.append(
		'<tr>' +
		'<td><input type="text" class="form-control attNameInput" name="periodAttName" placeholder="name" value="' + name + '" required></td>' +
		'<td><input type="text" class="form-control attValueInput" name="periodAttValue" placeholder="value" value="' + value + '"></td>' +
		'<td><button type="button" class="btn btn-danger btn-sm btn-block" onclick="$(this).closest(\'tr\').remove()" title="Remove Attribute"><i class="far fa-trash-alt fa-fw"></i></button></td>' +
		'</tr>'
	);
}

function sendSelectedPeriodAttUpdateRequest(event){
	event.preventDefault();
	markScreenAsLoading();
	console.log("Sending selected period update attribute request");

	var data = {
		selectedPeriod: selectedPeriod,
		actionConfig: {
			action: "EDIT",
			objectOperatingOn: "PERIOD",
			attributes: getAttStringFromAttEditTable(selectedPeriodEditAttsModalTableContent)
		}
	};

	doTimeManagerRestCall({
		spinnerContainer: selectedPeriodEditAttsModalForm.get(0),
		url: "/api/timeManager/manager/action",
		method: 'PATCH',
		data: data,
		done: function(data){
			console.log("Successful period attribute edit request: " + JSON.stringify(data));
			selectedPeriodEditAttsModal.modal('hide')
			setTimekeeperDataFromResponse(data);
			refreshPageData();
		},
		fail: function(data){
			console.warn("Bad response from adding task: " + JSON.stringify(data));
			doneLoading();
			addMessageToDiv(selectedPeriodEditAttsModalFormResponse, "danger", data.responseJSON.errOut);
		}
	});

}

function clearAddEditTimespanForm(){
	timespanAddEditModalLabelText.empty();
	timespanAddEditModalIdInputGroup.hide();
	timespanAddEditModalIdInput.val("");
	timespanAddEditModalTaskInput.val([]);
	timespanAddEditModalStartInput.val("");
	timespanAddEditModalEndInput.val("");
}

function setupTimespanFormForAdd(){
	clearAddEditTimespanForm();
	timespanAddEditModalLabelText.text("Add");
}

function removeTimespan(indForKeeper){
	markScreenAsLoading();
	console.log("Removing timespan " + indForKeeper);

	if(!confirm("Are you sure? This cannot be undone.")){
		console.log("User cancelled the remove.");
		return;
	}

	doTimeManagerRestCall({
		spinnerContainer: null,
		url: "/api/timeManager/manager/action",
		method: 'PATCH',
		data: {
			selectedPeriod: selectedPeriod,
			actionConfig: {
				action: "REMOVE",
				objectOperatingOn: "SPAN",
				index: indForKeeper
			}
		},
		done: function(data){
			console.log("Successful timespan remove request: " + JSON.stringify(data));
			setTimekeeperDataFromResponse(data);
			refreshPageData();
		},
		fail: function(data){
			console.warn("Bad response from removing timespan: " + JSON.stringify(data));
			doneLoading();
			addMessageToDiv(timespanAddEditModalFormResponse, "danger", data.responseJSON.errOut);
		},
	});
}

function setupSelectedPeriodEditTimespan(btnObj){
	var rowObj = $(btnObj).closest('tr');

	timespanAddEditModalIdInputGroup.show();
	timespanAddEditModalIdInput.val(rowObj.find(".timespanIndexCell").text());

	timespanAddEditModalTaskInput.val(rowObj.find(".timespanTaskCell").text());
	timespanAddEditModalStartInput.val(rowObj.find(".timespanStartCell").text());
	timespanAddEditModalEndInput.val(rowObj.find(".timespanEndCell").text());
}

function sendTimespanAddEditRequest(event){
	event.preventDefault();
	markScreenAsLoading();
	console.log("Sending timespan add/edit request");

	var data = {
		selectedPeriod: selectedPeriod,
		actionConfig: {
			action: "ADD",
			objectOperatingOn: "SPAN",
			name: timespanAddEditModalTaskInput.val()
		}
	};

	if(timespanAddEditModalStartInput.val() != ""){
		data.actionConfig.start = timespanAddEditModalStartInput.val();
	}
	if(timespanAddEditModalEndInput.val() != ""){
		data.actionConfig.end = timespanAddEditModalEndInput.val();
	}

	if(!timespanAddEditModalIdInputGroup.is(":hidden")){
		data.actionConfig.action = "EDIT";
		data.actionConfig.index = timespanAddEditModalIdInput.val();
	}

	doTimeManagerRestCall({
		spinnerContainer: timespanAddEditModalForm.get(0),
		url: "/api/timeManager/manager/action",
		method: 'PATCH',
		data: data,
		done: function(data){
			console.log("Successful add/edit request: " + JSON.stringify(data));
			addEditTimespanModal.modal('hide')
			setTimekeeperDataFromResponse(data);
			refreshPageData();
		},
		fail: function(data){
			console.warn("Bad response from adding/editing task: " + JSON.stringify(data));
			doneLoading();
			addMessageToDiv(timespanAddEditModalFormResponse, "danger", data.responseJSON.errOut);
		},
	});

}

function completeAllTimespans(){
	console.log("Completing all timespans in selected period.");
	markScreenAsLoading();

	doTimeManagerRestCall({
		spinnerContainer: null,
		url: "/api/timeManager/manager/action",
		method: 'PATCH',
		data: {
			selectedPeriod: selectedPeriod,
			actionConfig: {
				specialAction: "completeSpans"
			}
		},
		done: function(data){
			console.log("Successful complete all timespans request: " + JSON.stringify(data));
			setTimekeeperDataFromResponse(data);
			refreshPageData();
		},
		fail: function(data){
			console.warn("Bad response from completing all timespans: " + JSON.stringify(data));
			doneLoading();
			addMessage("danger", data.responseJSON.errOut);
		},
	});
}

function completeTimespan(indForKeeper){
	console.log("Completing timespan " + indForKeeper);

	doTimeManagerRestCall({
		spinnerContainer: null,
		url: "/api/timeManager/manager/action",
		method: 'PATCH',
		data: {
			selectedPeriod: selectedPeriod,
				actionConfig: {
					specialAction: "completeSpans",
					index: indForKeeper
			}
		},
		done: function(data){
			console.log("Successful complete one timespan request: " + JSON.stringify(data));
			setTimekeeperDataFromResponse(data);
			refreshPageData();
		},
		fail: function(data){
			console.warn("Bad response from completing one timespan: " + JSON.stringify(data));
			doneLoading();
			addMessage("danger", data.responseJSON.errOut);
		},
	});
}