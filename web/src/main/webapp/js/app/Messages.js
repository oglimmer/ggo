define(['jquery', './Constants', './GlobalData', 'watch'], function($, Constants, globalData, WatchJS) {


	function Messages() {
		this.score = "";
		this.title = "";
		this.info = "";
		this.error = "";
		
		var watch = WatchJS.watch;
		
		var thiz = this;
		watch(this, function(){
			if(typeof thiz.score  !== 'undefined') {
				$("#messageScore").html(thiz.score);		
			}
			if(typeof thiz.title  !== 'undefined') {
				$("#messageTitle").html(thiz.title);		
			}
			if(typeof thiz.info  !== 'undefined') {
				$("#messageInfo").html(thiz.info);
			}
			if(typeof thiz.error  !== 'undefined') {
				$("#messageError").html(thiz.error);
			}
		});
	}

	return Messages;

});
