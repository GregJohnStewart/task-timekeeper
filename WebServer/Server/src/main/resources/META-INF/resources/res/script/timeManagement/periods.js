
var periodsTableContent = $("#periodsTableContent");

function loadPeriodData(){
	console.log("Loading period data");
	periodsTableContent.empty();

	var managerData = getManagerData();

	var curIndForKeeper = managerData.workPeriods.length;
	var curIndForArray = 0;
	managerData.workPeriods.forEach(function(period){
		console.log("Adding period starting ");
		var isSelected = selectedPeriod == curIndForKeeper;

		periodsTableContent.prepend(
			'<tr>' +
			'<td class="periodIndexCell">'+curIndForKeeper+'</td>' +
			'<td class="periodStartCell">'+""+'</td>' +
			'<td class="periodEndCell">'+""+'</td>' +
			'<td class="periodDurationCell">'+""+'</td>' +
			'<td class="periodCompleteCell">'+""+'</td>' +
			'<td>' +
			'<button type="button" class="btn btn-warning btn-sm" onclick="" '+(isSelected?'title="Already Selected" disabled':'title="Select this period"')+'><i class="fas fa-hand-pointer fa-fw '+(isSelected?"selectedIcon":"")+'"></i><i class="fas fa-user-clock fa-fw"></i></button>&nbsp;' +
			'<button type="button" class="btn btn-danger btn-sm" onclick="removePeriod('+curIndForKeeper+');" title="Delete this period"><i class="far fa-trash-alt fa-fw"></i></button>' +
			'</td>' +
			'</tr>'
		);
		curIndForKeeper--;
		curIndForArray++;
	});
	console.log("DONE loading period data");
}

function addPeriod(){
	markScreenAsLoading();
	console.log("Sending period add request");

	doRestCall({
		spinnerContainer: null,
		url: "/api/timeManager/manager/action",
		method: 'PATCH',
		authorized: true,
		data: {
			actionConfig: {
				action: "ADD",
				objectOperatingOn: "PERIOD"
			}
		},
		done: function(data){
			console.log("Successful add request: " + JSON.stringify(data));
			taskAddEditModal.modal('hide')
			setTimekeeperDataFromResponse(data);
			refreshPageData();
		},
		fail: function(data){
			console.warn("Bad response from adding period: " + JSON.stringify(data));
			doneLoading();
			addMessage("danger", data.responseJSON.errOut);
		}
	});
}

function removePeriod(indForKeeper){
	markScreenAsLoading();
	console.log("Removing period indexed " + indForKeeper);

	if(!confirm("Are you sure?")){
		console.log("User canceled the delete.");
		doneLoading();
		return;
	}

	doRestCall({
		spinnerContainer: null,
		url: "/api/timeManager/manager/action",
		method: 'PATCH',
		authorized: true,
		data: {
			actionConfig: {
				action: "REMOVE",
				objectOperatingOn: "PERIOD",
				index: indForKeeper
			}
		},
		done: function(data){
			console.log("Successful period remove request: " + JSON.stringify(data));

			if(indForKeeper == selectedPeriod){
				selectedPeriod == null;
			} else if(indForKeeper < selectedPeriod){
				selectedPeriod--;
			}

			setTimekeeperDataFromResponse(data);
			refreshPageData();
		},
		fail: function(data){
			console.warn("Bad response from removing period: " + JSON.stringify(data));
			doneLoading();
			addMessage("danger", data.responseJSON.errOut);
		},
	});
}