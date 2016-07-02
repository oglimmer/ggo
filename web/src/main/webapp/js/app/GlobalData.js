define([], function () {

	// for jasmine tests init ggoPlayerId to {} 
	if(typeof window.ggoPlayerId === 'undefined') {
		window.ggoPlayerId = {};
	}
	
	var globalDataObject = {
			board : null,
			playerId: window.ggoPlayerId,
			model: {}
	};
	
	return globalDataObject;

});