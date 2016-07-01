define(['jquery', './Field', './Unit', './Constants', './HandItem', './GlobalData', './Button', './ModalDialog'], 
		function ($, Field, Unit, Constants, HandItem, globalData, Button, ModalDialog) {

	function copy(source, target) {
		for(var att in source) {
			if(source[att] === '##REMOVED##') {
				console.log("REMOVING OBJECT ATTRIBUTE!! warning not verified.");
				delete target[att]; 
			} else if(typeof source[att] === 'array' && att.indexOf("##REMOVED##") > 0 ) {
				console.log("REMOVING ARRAY ELEMENTS NOT IMPLEMENTED");
			} else {
				if(typeof source[att] === 'object') {
					if(typeof target[att] === 'undefined' || target[att] === null) {
						target[att] = source[att];
					} else if(source[att] === null) {
						target[att] = null;
					} else {						
						copy(source[att], target[att]);
					}
				} else if(typeof source[att] === 'array') {
					console.log("ADDING/CHANGING ARRAY ELEMENTS NOT IMPLEMENTED");
				} else {
					target[att] = source[att];						
				}
			}
		}
	}
	
	function processBoardStateItems(responseItemMap, itemsMap, newItemFunction) {
		$.each(responseItemMap, function(itemId, item) {					
			var existingItem = itemsMap[itemId];					
			if( typeof existingItem === 'undefined' ) {
				var newItem = newItemFunction();
				copy(item, newItem);
				itemsMap[itemId] = newItem;						
			} else {
				if(item === '##REMOVED##') {
					delete itemsMap[itemId];
				} else {
					copy(item, existingItem);											
				}
			}
		})
	}
	
	return {
		
		process: function(jsonObj) {
			console.log("GOT FROM SERVER:");
			console.log(jsonObj);
			/* RESP_BOARD */
			var boardState = jsonObj.boardState;
			if( typeof boardState !== 'undefined' ) {
				var board = globalData.board;
				processBoardStateItems(boardState.corToFields, board.corToFields, function() { return new Field(); });
				processBoardStateItems(boardState.idToHanditems, board.idToHanditems, function() { return new HandItem(); });
				processBoardStateItems(boardState.idToUnits, board.idToUnits, function() { return new Unit(); });
				processBoardStateItems(boardState.idToButtons, board.idToButtons, function() { return new Button(); });
				if(boardState.showCoordinates !== 'undefined' && boardState.showCoordinates != null) {
					board.showCoordinates = boardState.showCoordinates;
				}
			}
			/* RESP_MYCOLOR */
			if( typeof jsonObj.myColor !== 'undefined' ) {
				globalData.myColor = jsonObj.myColor;
			}
			/* RESP_MODAL_DIALOG_EN */
			if( typeof jsonObj.modalDialogEnable !== 'undefined' ) {
				globalData.modalDialg = new ModalDialog(jsonObj.modalDialogEnable);
			}
			/* RESP_MODAL_DIALOG_DIS */
			if( typeof jsonObj.modalDialogDisable !== 'undefined' ) {
				delete globalData.modalDialg;
			}
			/* RESP_PLAYER_CONNECTION_STATUS */
			if( typeof jsonObj.connectionState !== 'undefined' ) {
				if(jsonObj.connectionState.opponentConnectionStatus) {
					$("#messageOpponentConnectionLost").html("");
				} else {
					$("#messageOpponentConnectionLost").html("OPPONENT GOT DISCONNECTED!");
				}
			}
			/* RESP_MESSAGE */
			if( typeof jsonObj.messagesState !== 'undefined' ) {
				var messages = jsonObj.messagesState;
				if(typeof messages.score  !== 'undefined') {
					$("#messageScore").html(messages.score);		
				}
				if(typeof messages.title  !== 'undefined') {
					$("#messageTitle").html(messages.title);		
				}
				if(typeof messages.info  !== 'undefined') {
					$("#messageInfo").html(messages.info);
				}
				if(typeof messages.error  !== 'undefined') {
					$("#messageError").html(messages.error);
				}
			}
			globalData.board.draw();
			
		}
		
	};	
});
