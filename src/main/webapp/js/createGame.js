//Load common code that includes config, then load the app logic for this page.

requirejs([ './commons' ], function(commons) {
	requirejs([ "jquery", "bootstrap" ], function($) {

		var gameId = null;
		var playerId = null;

		$.ajax({
			url : "./CreateGameQuery.action",
			success : function(result) {
				$("#gameId").html(result.gameId);
				gameId = result.gameId;
				playerId = result.playerId;
				setTimeout(reload, 1000);
			}
		});

		var counter = "";
		
		function reload() {
			$.ajax({
				url : "./WaitingForOtherPlayerQuery.action",
				data : {
					gameId: gameId
				},
				success : function(result) {
					if(result.action == "redirect") {
						document.location.href="./Board.action?playerId=" + playerId;
					}
					else {
						counter += ".";
						if(counter == "......") {
							counter = "";
						}
						$("#waitingProgress").html(counter);
						setTimeout(reload, 1000);			
					}
				}
			});
		}

		// Safari + Firefox
		$(window).on('pagehide', function() {
			$.ajax({url : "./RemoveAbandonedGame.action", data: {gameId:gameId}, async: false});
		});
		// Chrome + Firefox 
		$(window).on('beforeunload', function() {
			$.ajax({url : "./RemoveAbandonedGame.action", data: {gameId:gameId}, async: false});
		});
		
	});
});