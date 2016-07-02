define(['jquery', 'app/Constants', 'app/GlobalData', 'watch'], function($, Constants, globalData, WatchJS) {


	function OpponentConnectionState() {
		this.opponentConnectionStatus = true;
		
		var watch = WatchJS.watch;
		
		var thiz = this;
		watch(this, function(){
			if(thiz.opponentConnectionStatus) {
				$("#messageOpponentConnectionLost").html("");
			} else {
				$("#messageOpponentConnectionLost").html("OPPONENT GOT DISCONNECTED!");
			}
		});
	}

	return OpponentConnectionState;

});
