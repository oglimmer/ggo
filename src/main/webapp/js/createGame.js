//Load common code that includes config, then load the app logic for this page.

requirejs([ 'commons' ], function(commons) {
	requirejs([ "jquery", "bootstrap" ], function($) {

		var gameId = null;
		var playerId = null;
		var counter = "";

		function createGame() {
		
			$.ajax({
				url : "./CreateGameQuery",
				success : function(result) {
					$("#gameId").html(result.gameId);
					$("#numberOfNotifications").html(result.numberOfNotifications==1?"1 fella":result.numberOfNotifications+" fellas");
					gameId = result.gameId;
					playerId = result.playerId;
					setTimeout(reload, 1000);
				}
			});
		
			// Safari + Firefox
			$(window).on('pagehide', function() {
				$.ajax({url : "./RemoveAbandonedGame", data: {gameId:gameId}, async: false});
			});
			// Chrome + Firefox 
			$(window).on('beforeunload', function() {
				$.ajax({url : "./RemoveAbandonedGame", data: {gameId:gameId}, async: false});
			});
		}
		
		function reload() {
			$.ajax({
				url : "./WaitingForOtherPlayerQuery",
				data : {
					gameId: gameId
				},
				success : function(result) {
					if(result.action == "redirect") {
						document.location.href="./Board?playerId=" + playerId;
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
		
		$("#btnByEmail").click(function() {
			document.location.href="CreateGameByEmail";
		})
		$("#btnRealTime").click(function() {
			createGame();
			$("#divChooseType").hide();
			$("#divWaitingForOtherPlayer").show();
		})
		$("#btnTutorial").click(function() {
			$.ajax({
				url : "./CreateTutorialQuery",
				success : function(result) {
					document.location.href="Board?playerId="+result.playerId;
				}
			});
		})
		$("#btnAi").click(function() {
			$.ajax({
				url : "./CreateAiQuery",
				success : function(result) {
					document.location.href="Board?playerId="+result.playerId;
				}
			});
		})

		
	});
});