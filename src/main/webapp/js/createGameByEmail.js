//Load common code that includes config, then load the app logic for this page.

requirejs([ 'commons' ], function(commons) {
	requirejs([ "jquery", "bootstrap" ], function($) {

		function checkEmailDisabled() {
			$("#email2").prop('disabled', $("#searchForOne").prop("checked"));
		}
		
		$("#searchForOne").click(function() {
			$("#email2").val("");
			checkEmailDisabled();
		});
		
		checkEmailDisabled();
		
	});
});