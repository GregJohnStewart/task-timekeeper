
var tasksTableContent = $("#tasksTableContent");
var addTaskButton = $("#addTaskButton");

var taskAddEditModal = $("#taskAddEditModal");
var taskAddEditModalForm = $("#taskAddEditModalForm");
var taskAddEditModalFormResponse = taskAddEditModalForm.find(".form-response");
var taskAddEditModalLabelText = $("#taskAddEditModalLabelText");
var taskAddEditModalIdInputGroup = $("#taskAddEditModalIdInputGroup");
var taskAddEditModalIdInput = $("#taskAddEditModalIdInput");
var taskAddEditModalNameInput = $("#taskAddEditModalNameInput");
var taskAddEditModalAddAttButton = $("#taskAddEditModalAddAttButton");
var taskAddEditModalAttTableContent = $("#taskAddEditModalAttTableContent");

function loadTaskData(){
	console.log("loading task data");
	for(i = 0; i < timekeeperData.timeManagerData.tasks.length; i++){
		var task = timekeeperData.timeManagerData.tasks[i];
		console.log("Adding task named " + task.name);
		tasksTableContent.append(
			'<tr>' +
			'<td>'+i+'</td>' +
			'<td>'+task.name.name+'</td>' +
			'<td>' +
			'<button type="button" class="btn btn-warning btn-sm" onclick=""><i class="far fa-eye"></i>/<i class="fas fa-pencil-alt"></i></button>&nbsp;' +
			'<button type="button" class="btn btn-danger btn-sm" onclick=""><i class="far fa-trash-alt fa-fw"></i></button>' +
			'</td>' +
			'</tr>'
		);
	}
	console.log("DONE loading task data.");
}

function clearTaskAddEditForm(){
	taskAddEditModalLabelText.empty();
	taskAddEditModalIdInput.val("");
	taskAddEditModalIdInputGroup.hide();
	taskAddEditModalNameInput.val("");
	taskAddEditModalAttTableContent.empty();
}

taskAddEditModal.on('hidden.bs.modal', function (e) {
	clearTaskAddEditForm();
})

function setupTaskAddEditFormForAdd(){
	taskAddEditModalLabelText.text("Add");
	taskAddEditModalForm.on("submit", function(event){sendTaskAddRequest(event)});
}

function setupTaskAddEditFormForEdit(i){
	//TODO
}

function taskAddEditFormAddAttribute(name, value){
	taskAddEditModalAttTableContent.append(
		'<tr>' +
		'<td><input type="text" class="form-control taskAttNameInput" name="taskAttName" placeholder="name" value="' + name + '" required></td>' +
		'<td><input type="text" class="form-control taskAttValueInput" name="taskAttValue" placeholder="value" value="' + value + '"></td>' +
		'<td><button type="button" class="btn btn-danger btn-sm btn-block" onclick="$(this).closest(\'tr\').remove()"><i class="far fa-trash-alt fa-fw"></i></button></td>' +
		'</tr>'
	);
}

function sendTaskAddRequest(event){
	markScreenAsLoading();
	event.preventDefault();
	console.log("Sending task add request");

	var data = {
		actionConfig: {
			action: "ADD",
			objectOperatingOn: "TASK",
			name: taskAddEditModalNameInput.val()
		}
	};

	var attributes = "";

	var attNameInputs = taskAddEditModalAttTableContent.find(".taskAttNameInput");
	var attValueInputs = taskAddEditModalAttTableContent.find(".taskAttValueInput");

	for(i = 0; i < attNameInputs.length; i++){
		attributes += attNameInputs.get(i).value + "," + attValueInputs.get(i).value + ";";
	}

	if(attributes != ""){
		data['actionConfig']['attributes'] = attributes;
	}

	doRestCall({
		spinnerContainer: taskAddEditModalForm.get(0),
		url: "/api/timeManager/manager/action",
		method: 'PATCH',
		authorized: true,
		data: data,
		done: function(data){
			console.log("Successful add request: " + JSON.stringify(data));
			taskAddEditModal.modal('hide')
			timekeeperData = data.timeManagerData;
			refreshPageData();
		},
		fail: function(data){
			console.warn("Bad response from adding task: " + JSON.stringify(data));
			doneLoading();
			addMessageToDiv(taskAddEditModalFormResponse, "danger", data.responseJSON.errOut);
		},
	});
}

