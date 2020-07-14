
var selectedPeriodStartSpan = $("#selectedPeriodStartSpan");
var selectedPeriodEndSpan = $("#selectedPeriodEndSpan");
var selectedPeriodDurationSpan = $("#selectedPeriodDurationSpan");
var selectedPeriodCompleteSpan = $("#selectedPeriodCompleteSpan");
var selectedPeriodAttributesTableBody = $("#selectedPeriodAttributesTableBody");
var selectedPeriodTaskStatsTableBody = $("#selectedPeriodTaskStatsTableBody");
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

function setupSelectedTimespanUi(){
	timespanAddEditModalForm.on("submit", function(event){sendTimespanAddEditRequest(event)});

	timespanAddEditModalStartDateTimePicker.datetimepicker();
	timespanAddEditModalEndDateTimePicker.datetimepicker();

	timespanAddEditModalStartDateTimePicker.on("change.datetimepicker", function (e) {
		timespanAddEditModalEndDateTimePicker.datetimepicker('minDate', e.date);
	});
	timespanAddEditModalEndDateTimePicker.on("change.datetimepicker", function (e) {
		timespanAddEditModalStartDateTimePicker.datetimepicker('maxDate', e.date);
	});
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
			'<td>' + selectedPeriodStats.taskStats.valueStrings[key] + '</td>' +
			'</tr>'
		);
	});

	selectedPeriodStartSpan.text(selectedPeriodStats.startDateTime);
	selectedPeriodEndSpan.text(selectedPeriodStats.endDateTime);
	selectedPeriodDurationSpan.text(selectedPeriodStats.totalTime);
	selectedPeriodCompleteSpan.text((selectedPeriodStats.allComplete?"Yes":"No"));

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

	curIndForKeeper = selectedPeriodData.timespans.length;
	var curIndForArray = 0;
	selectedPeriodData.timespans.forEach(function(curTimespan) {
		selectedPeriodTimespansTableContent.prepend(
			'<tr>' +
			'<td class="timespanIndexCell">'+curIndForKeeper+'</td>' +
			'<td>'+'(start)'+'</td>' +
			'<td>'+'(end)'+'</td>' +
			'<td>'+'(duration)'+'</td>' +
			'<td>'+curTimespan.taskName.name+'</td>' +
			'<td>'+'(actions)'+'</td>' +
			'</tr>'
		);
		curIndForKeeper--;
		curIndForArray++;
	});

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

function selectedPeriodAddEditTimespan(btnObj, indexForArr){
	var rowObj = $(btnObj).closest('tr');

	var selectedPeriodData = getSelectedPeriodData();
	var editingTimespan = selectedPeriodData.timespans[indexForArr];

	timespanAddEditModalIdInputGroup.show();
	timespanAddEditModalIdInput.val(rowObj.find(".timespanIndexCell").text());

	//TODO
	//timespanAddEditModalTaskInput.val(editingTimespan.task);

	//TODO:: start/end
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
			name: timespanAddEditModalTaskInput.val(),
			start: timespanAddEditModalStartDateTimePicker.datetimepicker('viewDate').toISOString(),
			end: timespanAddEditModalStartDateTimePicker.datetimepicker('viewDate').toISOString()
		}
	};

	if(!taskAddEditModalIdInputGroup.is(":hidden")){
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