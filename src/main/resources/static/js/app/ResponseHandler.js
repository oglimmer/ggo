define(['app/GlobalData', 'app/ObjectMerger'], function (globalData, objectMerger) {

	return {
		
		process: function(jsonObj) {
			console.log("GOT FROM SERVER:");
			console.log(jsonObj);
			objectMerger.merge(jsonObj, globalData.model);
			globalData.board.draw();
		}
		
	};	
});
