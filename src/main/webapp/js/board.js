//Load common code that includes config, then load the app logic for this page.

requirejs(['./commons'], function(commons) {
	requirejs(['./app/Communication', './app/ResponseHandler', './app/GlobalData', 'bootstrap'], 
			function (communication, responseHandler, globalData) {

		communication.connect(function() {
			communication.send({
				pid : globalData.playerId,
				cmd : "join"
			});
			$("#message").html("Connecting...");
		}, function(jsonObj) {
			responseHandler.process(jsonObj);
		});
	    
	});
});