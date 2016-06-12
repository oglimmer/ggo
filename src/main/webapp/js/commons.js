requirejs.config({
    baseUrl: 'webjars',
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
        jquery: 'jquery/2.2.4/jquery.min',
        bootstrap: 'bootstrap/3.3.6/js/bootstrap.min',
        atmosphere: '../jquery/jquery.atmosphere',
        //except, if the module ID starts with "app",
        //load it from the js/app directory. paths
        //config is relative to the baseUrl, and
        //never includes a ".js" extension since
        //the paths config could be for a directory.
        app: '../js/app'
    },
    shim: {
    	"bootstrap" : { 
    		"deps" :['jquery'] 
    	}
    }
});
