define(['jquery', 'app/Constants', 'app/GlobalData', 'watch', 'app/Communication'], function($, Constants, globalData, WatchJS, communication) {

	function updateUI(text) {
		$("#messageOpponentConnectionLost").html(text);
		console.log("messageOpponentConnectionLost="+text);
	}

	function OpponentConnectionState() {
		this.opponentConnectionStatus = true;

		function update() {
			var text = "";
			if(communication.connectedToServer) {
				if(!thiz.opponentConnectionStatus) {
					text = "OPPONENT GOT DISCONNECTED!";
				}
			} else {
				text = "YOU GOT DISCONNECTED FROM SERVER! TRY TO RELOAD THE PAGE!!!";
			}
			updateUI(text);
		}
		
		var watch = WatchJS.watch;		
		var thiz = this;
		watch(this, function(){
			update();
		});
		watch(communication, "connectedToServer", function() {
			update();
		});
	}

	return OpponentConnectionState;

});
