define(['./Board'], 
		function (Board) {

	
	var globalDataObject = {
			board : null,
			active : {},
			playerId: ggoPlayerId
	};
	
	globalDataObject.board = new Board('board');
	
	return globalDataObject;

});