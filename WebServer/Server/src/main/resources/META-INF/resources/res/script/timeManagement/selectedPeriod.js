
var selectedPeriodStartSpan = $("#selectedPeriodStartSpan");
var selectedPeriodEndSpan = $("#selectedPeriodEndSpan");
var selectedPeriodDurationSpan = $("#selectedPeriodDurationSpan");
var selectedPeriodCompleteSpan = $("#selectedPeriodCompleteSpan");
var selectedPeriodAttributesTableBody = $("#selectedPeriodAttributesTableBody");
var selectedPeriodTaskStatsTableBody = $("#selectedPeriodTaskStatsTableBody");
var selectedPeriodTimespansTableContent = $("#selectedPeriodTimespansTableContent");


function loadSelectedPeriodData(){
	console.log("Loading selected period data");
	clearSelectedPeriodData();
	if(selectedPeriod == null){
		console.log("No period selected. Nothing to do.");
		return;
	}

	var selectedPeriodData = getSelectedPeriodData();
	var selectedPeriodStats = getSelectedPeriodStats();

	console.log("Data for selected period: " + JSON.stringify(selectedPeriodData));
	console.log("Stats for selected period: " + JSON.stringify(selectedPeriodStats));

	// fill task table
//	selectedPeriodStats.taskStats.forEach(function(index, value){
		//TODO:: when we know what this looks like
//	});


	selectedPeriodStartSpan.text(selectedPeriodStats.startDateTime);
	selectedPeriodEndSpan.text(selectedPeriodStats.endDateTime);
	selectedPeriodDurationSpan.text(selectedPeriodStats.totalTime);
	selectedPeriodCompleteSpan.text((selectedPeriodStats.allComplete?"Yes":"No"));


	Object.keys(selectedPeriodData.attributes).forEach(function(key) {
		tasksTableContent.prepend(
			'<tr>' +
			'<td>'+key+'</td>' +
			'<td>'+selectedPeriodData.attributes[key]+'</td>' +
			'</tr>'
		);
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

}
