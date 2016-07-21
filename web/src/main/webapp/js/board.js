//Load common code that includes config, then load the app logic for this page.

requirejs(['commons'], function(commons) {
	requirejs(['jquery', 'app/Communication', 'app/ResponseHandler', 'app/GlobalData', 'app/Board', 'app/Constants', 'bootstrap'], 
			function ($, communication, responseHandler, globalData, Board, Constants) {

		var w = $(document).width()-15;
		var h = $(document).height()-100;
		var maxAspectRatio = 1.1; // for now it is 1
		if(w/h > maxAspectRatio) {
			w = h/maxAspectRatio;
		}
		Constants.size.width = parseInt(w/10.5);
		Constants.size.height = Constants.size.width;
		$("#boardDiv").html('<canvas id="board" width="' + w + '" height="' + w + '"></canvas>');
		$("#messageDiv").width(w+"px");
		
		globalData.board = new Board('board');
		globalData.board.init();
		
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