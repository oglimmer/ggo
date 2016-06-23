//Load common code that includes config, then load the app logic for this page.

requirejs([ './commons' ], function(commons) {
	requirejs([ "jquery", "bootstrap" ], function($) {

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

		setTimeout(reload, 1000);
	});
});