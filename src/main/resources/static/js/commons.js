requirejs.config({
    // baseUrl: directory of the initial JS file loading this (so for this app it is "js")
	urlArgs: "bust=" + (new Date()).getTime(),
    paths: {
        // the left side is the module ID,
        // the right side is the path to
        // the jQuery file, relative to baseUrl.
        // Also, the path should NOT include
        // the '.js' file extension. This example
        // is using jQuery 1.9.0 located at
        // js/lib/jquery-1.9.0.js, relative to
        // the HTML page.
        jquery: '../webjars/jquery/2.2.4/jquery.min',
        bootstrap: '../webjars/bootstrap/3.3.7/js/bootstrap.min',
        watch: 'lib/watch-1.3.0'
        //except, if the module ID starts with "app",
        //load it from the js/app directory. paths
        //config is relative to the baseUrl, and
        //never includes a ".js" extension since
        //the paths config could be for a directory.
        // app: 'js/app/'
    },
    shim: {
    	"bootstrap" : { 
    		"deps" :['jquery'] 
    	}
    }
});
