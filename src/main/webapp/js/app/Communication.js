define([ 'jquery', 'app/Constants' ], function($, Constants) {

	var socket;
	var callbacks = {};
	var reconnectInterval = 2000;
	var maxReconnectAttempts = 10;
	var reconnectAttempts = 0;
	var connectionAttempted = false;
	var isManualDisconnect = false;

	function getWebSocketUrl() {
		var protocol = (window.location.protocol === 'https:') ? 'wss:' : 'ws:';
		var host = window.location.host;
		
		// For Spring Boot applications, WebSocket endpoints are typically at the root
		// We'll build the URL as protocol://host/srvcom
		return protocol + '//' + host + '/srvcom';
	}

	function connect() {
		if (socket && (socket.readyState === WebSocket.CONNECTING || socket.readyState === WebSocket.OPEN)) {
			return;
		}

		if (isManualDisconnect) {
			console.log("Manual disconnect - not attempting to reconnect");
			return;
		}

		connectionAttempted = true;
		var wsUrl = getWebSocketUrl();
		console.log("Attempting WebSocket connection to:", wsUrl);
		console.log("Current page URL:", window.location.href);

		try {
			socket = new WebSocket(wsUrl);

			socket.onopen = function(event) {
				console.log("WebSocket connection established successfully");
				reconnectAttempts = 0;
				commObj.connectedToServer = true;
				
				if (typeof callbacks.onOpen === "function") {
					callbacks.onOpen();
				}
			};

			socket.onmessage = function(event) {
				console.log("Received WebSocket message:", event.data);
				var json;
				try {
					json = JSON.parse(event.data);
				} catch (e) {
					console.error('Failed to parse JSON message:', event.data, e);
					return;
				}
				
				if (typeof callbacks.onMessage === "function") {
					callbacks.onMessage(json);
				}
			};

			socket.onclose = function(event) {
				console.log("WebSocket connection closed - Code:", event.code, "Reason:", event.reason);
				commObj.connectedToServer = false;
				
				// Don't reconnect if it was a manual disconnect or clean close
				if (isManualDisconnect || event.code === 1000) {
					console.log("Connection closed normally, not reconnecting");
					return;
				}
				
				if (reconnectAttempts < maxReconnectAttempts) {
					var delay = Math.min(reconnectInterval * Math.pow(1.5, reconnectAttempts), 30000);
					console.log("Attempting to reconnect in " + delay + "ms (attempt " + (reconnectAttempts + 1) + "/" + maxReconnectAttempts + ")");
					reconnectAttempts++;
					
					setTimeout(function() {
						connect();
					}, delay);
				} else {
					console.error("Max reconnection attempts reached. Please refresh the page.");
				}
			};

			socket.onerror = function(error) {
				console.error("WebSocket error:", error);
				commObj.connectedToServer = false;
			};

		} catch (error) {
			console.error("Failed to create WebSocket connection:", error);
			commObj.connectedToServer = false;
			
			// Retry with exponential backoff
			if (reconnectAttempts < maxReconnectAttempts) {
				var delay = Math.min(reconnectInterval * Math.pow(1.5, reconnectAttempts), 30000);
				reconnectAttempts++;
				setTimeout(function() {
					connect();
				}, delay);
			}
		}
	}

	var commObj = {

		connect : function(onOpenCallback, onMessageCallback) {
			callbacks.onOpen = onOpenCallback;
			callbacks.onMessage = onMessageCallback;
			isManualDisconnect = false;
			connect();
			console.log("Initializing WebSocket connection...");
			
			// Use beforeunload instead of unload for better browser compatibility
			$(window).on('beforeunload', function() {
				if (socket && socket.readyState === WebSocket.OPEN) {
					isManualDisconnect = true;
					socket.close(1000, 'Page unload');
				}
			});
		},

		send : function(data) {
			if (socket && socket.readyState === WebSocket.OPEN) {
				console.log("SEND TO SERVER:", data);
				socket.send(JSON.stringify(data));
				return true;
			} else {
				console.warn("Cannot send message - WebSocket not connected. State:", 
					socket ? socket.readyState : 'null');
				
				// Try to reconnect if not already attempting
				if (!connectionAttempted || (socket && socket.readyState === WebSocket.CLOSED)) {
					console.log("Attempting to reconnect...");
					connect();
				}
				return false;
			}
		},

		disconnect : function() {
			isManualDisconnect = true;
			if (socket) {
				socket.close(1000, 'Manual disconnect');
			}
			commObj.connectedToServer = false;
		},

		isConnected : function() {
			return socket && socket.readyState === WebSocket.OPEN;
		},

		// Debug function to test WebSocket URL and basic connectivity
		testConnection : function() {
			var testUrl = getWebSocketUrl();
			console.log("Testing WebSocket connection to:", testUrl);
			console.log("Browser WebSocket support:", typeof WebSocket !== 'undefined');
			
			var testSocket = new WebSocket(testUrl);
			testSocket.onopen = function() {
				console.log("Test connection successful!");
				testSocket.close();
			};
			testSocket.onerror = function(error) {
				console.error("Test connection failed:", error);
			};
			testSocket.onclose = function(event) {
				console.log("Test connection closed with code:", event.code, "reason:", event.reason);
			};
		},

		connectedToServer : false

	};

	return commObj;
});