//Load common code that includes config, then load the app logic for this page.

requirejs(['commons'], function(commons) {
	requirejs(["jquery", "bootstrap"], function ($) {	
		
		function reload() {
			$.ajax({
				url : "./LandingGameQuery.action",
				data : null,
				success : function(result) {
					$("#availGames").html(result);
					setTimeout(reload, 1000);
				}
			});			
		}

		setTimeout(reload, 1);
		
	});
});