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
	
	return {
		
		process: function(jsonObj) {
			console.log("GOT FROM SERVER:");
			console.log(jsonObj);
			/* RESP_BOARD */
			if( typeof jsonObj.boardState !== 'undefined' ) {
				$.each(jsonObj.boardState.corToFields, function(fieldId, field) {
					var existingField = globalData.board.corToFields[fieldId];
					if( typeof existingField === 'undefined' ) {
						var newfield = new Field();
						copy(field, newfield);
						globalData.board.addField(newfield);						
					} else {
						copy(field, existingField);
					}
				})
				$.each(jsonObj.boardState.idToHanditems, function(handitemId, handitem) {					
					var existingHanditem = globalData.board.idToHanditems[handitemId];					
					if( typeof existingHanditem === 'undefined' ) {
						var newHandItem = new HandItem();
						copy(handitem, newHandItem);
						globalData.board.addHandItem(newHandItem);						
					} else {
						if(handitem === '##REMOVED##') {
							delete globalData.board.idToHanditems[handitemId];
						} else {
							copy(handitem, existingHanditem);											
						}
					}
				})
				$.each(jsonObj.boardState.idToUnits, function(unitId, unit) {
					var existingUnit = globalData.board.idToUnits[unitId];
					if( typeof existingUnit === 'undefined' ) {
						var newUnit = new Unit();
						copy(unit, newUnit);
						globalData.board.addUnit(newUnit);						
					} else {
						if(unit === '##REMOVED##') {
							delete globalData.board.idToUnits[unitId];
						} else {
							copy(unit, existingUnit);
						}
					}
				})
				$.each(jsonObj.boardState.idToButtons, function(buttonId, button) {
					var existingButton = globalData.board.idToButtons[buttonId];
					if( typeof existingButton === 'undefined' ) {
						var newButton = new Button();
						copy(button, newButton);
						globalData.board.addButton(newButton);	
					} else {
						if(button === '##REMOVED##') {
							delete globalData.board.idToButtons[buttonId];
						} else {
							copy(button, existingButton);
						}
					}					
				});
				if(jsonObj.boardState.showCoordinates !== 'undefined' && jsonObj.boardState.showCoordinates != null) {
					globalData.board.showCoordinates = jsonObj.boardState.showCoordinates;
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
			if( typeof jsonObj.messages !== 'undefined' ) {
				if(typeof jsonObj.messages.score  !== 'undefined') {
					$("#messageScore").html(jsonObj.messages.score);		
				}
				if(typeof jsonObj.messages.title  !== 'undefined') {
					$("#messageTitle").html(jsonObj.messages.title);		
				}
				if(typeof jsonObj.messages.info  !== 'undefined') {
					$("#messageInfo").html(jsonObj.messages.info);
				}
				if(typeof jsonObj.messages.error  !== 'undefined') {
					$("#messageError").html(jsonObj.messages.error);
				}
			}
			globalData.board.draw();
			
		}
		
	};	
});
