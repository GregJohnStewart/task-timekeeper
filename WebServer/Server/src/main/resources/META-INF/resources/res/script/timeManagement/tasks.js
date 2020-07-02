
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
		'<td><input type="text" class="form-control taskAttNameInput" placeholder="name" value="' + name + '" required></td>' +
		'<td><input type="text" class="form-control taskAttValueInput" placeholder="value" value="' + value + '"></td>' +
		'<td><button type="button" class="btn btn-danger btn-sm btn-block" onclick="$(this).closest(\'tr\').remove()"><i class="far fa-trash-alt fa-fw"></i></button></td>' +
		'</tr>'
	);
}

function sendTaskAddRequest(event){
	event.preventDefault();
	console.log("Sending task add request");

	doRestCall({
		spinnerContainer: null,
		url: "/api/timeManager/manager",
    		authorized: true,
    		done: function(data){
    			console.log("Got timekeeper data: " + JSON.stringify(data));
    			timekeeperData = data;

    		},
    		fail: function(data){
    			console.warn("Bad response from getting timekeeper data: " + JSON.stringify(data));
    			timekeeperData = {};
    			//TODO:: handle
    		},
    	});

}

