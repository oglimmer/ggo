define(['./Board'], 
		function (Board) {

	
	var globalDataObject = {
			board : null,
			myColor : null,
			playerId: ggoPlayerId
	};
	
	globalDataObject.board = new Board('board');
	
	return globalDataObject;

});