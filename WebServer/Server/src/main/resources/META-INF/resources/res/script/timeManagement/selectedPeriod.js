
function loadSelectedPeriodData(){
	console.log("Loading selected period data");
	clearSelectedPeriodData();
	if(selectedPeriod == null){
		console.log("No period selected. Nothing to do.");
		return;
	}

	//TODO
//	tasksTableContent.empty();
//
//	var managerData = getManagerData();
//
//	var curIndForKeeper = managerData.tasks.length;
//	var curIndForArray = 0;
//	managerData.tasks.forEach(function(task){
//		console.log("Adding task named " + task.name.name);
//		tasksTableContent.prepend(
//			'<tr>' +
//			'<td class="taskIndexCell">'+curIndForKeeper+'</td>' +
//			'<td class="taskNameCell">'+task.name.name+'</td>' +
//			'<td>' +
//			'<button type="button" class="btn btn-warning btn-sm" onclick="setupTaskAddEditFormForEdit($(this), '+curIndForArray+');" data-toggle="modal" data-target="#taskAddEditModal"><i class="far fa-eye"></i>/<i class="fas fa-pencil-alt"></i></button>&nbsp;' +
//			'<button type="button" class="btn btn-danger btn-sm" onclick="removeTask('+curIndForKeeper+');"><i class="far fa-trash-alt fa-fw"></i></button>' +
//			'</td>' +
//			'</tr>'
//		);
//		curIndForKeeper--;
//		curIndForArray++;
//	});
	console.log("DONE loading selected period data");
}

function clearSelectedPeriodData(){
	//TODO
}
