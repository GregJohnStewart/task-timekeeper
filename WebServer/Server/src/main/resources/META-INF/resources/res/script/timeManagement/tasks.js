
var tasksTableContent = $("#tasksTableContent");

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
	tasksTableContent.empty();

	var managerData = getManagerData();

	var curIndForKeeper = managerData.tasks.length;
	var curIndForArray = 0;
	managerData.tasks.forEach(function(task){
		console.log("Adding task named " + task.name.name);
		tasksTableContent.prepend(
			'<tr>' +
			'<td class="taskIndexCell">'+curIndForKeeper+'</td>' +
			'<td class="taskNameCell">'+task.name.name+'</td>' +
			'<td>' +
			'<button type="button" class="btn btn-warning btn-sm" onclick="setupTaskAddEditFormForEdit($(this), '+curIndForArray+');" data-toggle="modal" data-target="#taskAddEditModal" title="View or Edit Task"><i class="far fa-eye fa-fw"></i>/<i class="fas fa-pencil-alt fa-fw"></i></button>&nbsp;' +
			'<button type="button" class="btn btn-danger btn-sm" onclick="removeTask('+curIndForKeeper+');" title="Remove Task"><i class="far fa-trash-alt fa-fw"></i></button>' +
			'</td>' +
			'</tr>'
		);
		curIndForKeeper--;
		curIndForArray++;
	});
	console.log("DONE loading task data.");
}

function removeTask(i){
	markScreenAsLoading();
	console.log("Removing task indexed " + i);

	if(!confirm("Are you sure?")){
		console.log("User canceled the delete.");
		doneLoading();
		return;
	}

	doTimeManagerRestCall({
		url: "/api/timeManager/manager/action",
		method: 'PATCH',

		data: {
			actionConfig: {
				action: "REMOVE",
				objectOperatingOn: "TASK",
				index: i
			}
		},
		done: function(data){
			console.log("Successful task remove request: " + JSON.stringify(data));
			setTimekeeperDataFromResponse(data);
			refreshPageData();
		},
		fail: function(data){
			console.warn("Bad response from removing task: " + JSON.stringify(data));
			doneLoading();
			addMessage("danger", data.responseJSON.errOut);
		},
	});
}

function clearTaskAddEditForm(){
	taskAddEditModalFormResponse.empty();
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
}

function setupTaskAddEditFormForEdit(btnObj, indexForArr){
	var rowObj = $(btnObj).closest('tr');

	var task = getManagerData().tasks[indexForArr];

	taskAddEditModalIdInputGroup.show();
	taskAddEditModalIdInput.val(rowObj.find(".taskIndexCell").text());

	taskAddEditModalNameInput.val(task.name.name);

	Object.keys(task.attributes).forEach(function(key) {
		taskAddEditFormAddAttribute(key, task.attributes[key]);
	});
}

function taskAddEditFormAddAttribute(name, value){
	taskAddEditModalAttTableContent.append(
		'<tr>' +
		'<td><input type="text" class="form-control attNameInput" name="taskAttName" placeholder="name" value="' + name + '" required></td>' +
		'<td><input type="text" class="form-control attValueInput" name="taskAttValue" placeholder="value" value="' + value + '"></td>' +
		'<td><button type="button" class="btn btn-danger btn-sm btn-block" onclick="$(this).closest(\'tr\').remove()" title="Remove Attribute"><i class="far fa-trash-alt fa-fw"></i></button></td>' +
		'</tr>'
	);
}

function sendTaskAddEditRequest(event){
	event.preventDefault();
	markScreenAsLoading();
	console.log("Sending task add/edit request");

	var data = {
		actionConfig: {
			action: "ADD",
			objectOperatingOn: "TASK",
			name: taskAddEditModalNameInput.val(),
			attributes: getAttStringFromAttEditTable(taskAddEditModalAttTableContent)
		}
	};

	if(!taskAddEditModalIdInputGroup.is(":hidden")){
		data.actionConfig.action = "EDIT";
		data.actionConfig.index = taskAddEditModalIdInput.val();
		data.actionConfig.newName = taskAddEditModalNameInput.val();
		delete data.actionConfig.name;
	}

	doTimeManagerRestCall({
		spinnerContainer: taskAddEditModalForm.get(0),
		url: "/api/timeManager/manager/action",
		method: 'PATCH',
		data: data,
		done: function(data){
			console.log("Successful add request: " + JSON.stringify(data));
			taskAddEditModal.modal('hide')
			setTimekeeperDataFromResponse(data);
			refreshPageData();
		},
		fail: function(data){
			console.warn("Bad response from adding task: " + JSON.stringify(data));
			doneLoading();
			addMessageToDiv(taskAddEditModalFormResponse, "danger", data.responseJSON.errOut);
		},
	});
}

