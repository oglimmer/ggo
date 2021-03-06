define([ 'jquery', 'app/Constants', 'atmosphere' ], function($, Constants,
		atmosphere) {

	var socket = atmosphere;
	var subSocket;
	var transport = 'websocket';
	var callbacks = {};

	// https://github.com/Atmosphere/atmosphere/wiki/atmosphere.js-atmosphere.js-API
	var request = {
		url : 'srvcom',
		contentType : "application/json",
		// logLevel : 'debug',
		trackMessageLength : false,
		shared : false,
		transport : transport,
		fallbackTransport : 'long-polling',
		maxReconnectOnClose : 5000
	// reconnectInterval : 5000
	};

	request.onOpen = function(response) {
		console.log("onOpen id:");
		transport = response.transport;
		request.uuid = response.request.uuid;
		console.log("onOpen id:" + request.uuid);
		if (typeof callbacks.onOpen === "function") {
			callbacks.onOpen();
		}
		commObj.connectedToServer = true;
	};

	request.onTransportFailure = function(errorMsg, request) {
		commObj.connectedToServer = false;
		atmosphere.util.info(errorMsg);
		if (window.EventSource) {
			request.fallbackTransport = "sse";
			transport = "see";
		}
	};

	request.onClientTimeout = function(r) {
		console.log("reconnect in " + request.reconnectInterval);
		commObj.connectedToServer = false;
		setTimeout(function() {
			subSocket = socket.subscribe(request);
		}, request.reconnectInterval);
	};

	request.onReopen = function(response) {
		console.log("onReopen");
		commObj.connectedToServer = true;
	};

	request.onClose = function(response) {
		console.log("onClose");
		commObj.connectedToServer = false;
	};

	request.onError = function(response) {
		console.log("onError");
		commObj.connectedToServer = false;
	};

	request.onReconnect = function(request, response) {
		console.log("onReconnect");
	};

	request.onMessage = function(response) {
		var message = response.responseBody;
		var json;
		try {
			json = $.parseJSON(message);
		} catch (e) {
			console.log('This doesn\'t look like a valid JSON: ', message);
			throw e;
		}
		if (typeof callbacks.onMessage === "function") {
			callbacks.onMessage(json);
		}
	};

	var commObj = {

		connect : function(onOpenCallback, onMessageCallback) {
			callbacks.onOpen = onOpenCallback;
			callbacks.onMessage = onMessageCallback;
			subSocket = socket.subscribe(request);
			$(window).unload(function() {
				atmosphere.unsubscribe();
			});
		},

		send : function(data) {
			console.log("SEND TO SERVER:");
			console.log(data);
			subSocket.push(JSON.stringify(data));
		},

		connectedToServer : false

	};

	return commObj;
});