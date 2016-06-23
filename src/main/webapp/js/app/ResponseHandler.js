define(['jquery', './Field', './Unit', './Constants', './HandItem', './GlobalData', './Button', './ModalDialog'], 
		function ($, Field, Unit, Constants, HandItem, globalData, Button, ModalDialog) {

	function copy(source, target) {
		for(var att in source) {
			//maybe we could use an empty object to set something to null (as server never sends null as @JsonInclude(Include.NON_NULL)) 
			/*if($.type(source[att]) == 'object' && $.isEmptyObject(source[att])) {
				target[att] = null;
			} else {
				target[att] = source[att];
			}*/
			target[att] = source[att];
		}
	}
	
	return {
		
		process: function(jsonObj) {
			console.log("GOT FROM SERVER:");
			console.log(jsonObj);
			/* RESP_BOARD */
			if( typeof jsonObj.board !== 'undefined' ) {
				$.each(jsonObj.board.corToFields, function(fieldId, field) {
					var existingField = globalData.board.corToFields[fieldId];
					if( typeof existingField === 'undefined' ) {
						var newfield = new Field();
						copy(field, newfield);
						globalData.board.addField(newfield);						
					} else {
						copy(field, existingField);
					}
				})
				$.each(jsonObj.board.idToHanditems, function(handitemId, handitem) {					
					var existingHanditem = globalData.board.idToHanditems[handitemId];					
					if( typeof existingHanditem === 'undefined' ) {
						var newHandItem = new HandItem();
						copy(handitem, newHandItem);
						globalData.board.addHandItem(newHandItem);						
					} else {						
						copy(handitem, existingHanditem);				
					}
				})
				$.each(jsonObj.board.idToUnits, function(unitId, unit) {
					var existingUnit = globalData.board.idToUnits[unitId];
					if( typeof existingUnit === 'undefined' ) {
						var newUnit = new Unit();
						copy(unit, newUnit);
						globalData.board.addUnit(newUnit);						
					} else {
						copy(unit, existingUnit);
					}
				})
				$.each(jsonObj.board.unitsToRemove, function(index, unitId) {
					delete globalData.board.idToUnits[unitId];
				});
				$.each(jsonObj.board.handitemsToRemove, function(index, handitemId) {
					delete globalData.board.idToHanditems[handitemId];
				});
				$.each(jsonObj.board.idToButtons, function(buttonId, button) {
					var existingButton = globalData.board.idToButtons[buttonId];
					if( typeof existingButton === 'undefined' ) {
						var newButton = new Button();
						copy(button, newButton);
						globalData.board.addButton(newButton);	
					} else {
						copy(button, existingButton);
					}					
				});
				$.each(jsonObj.board.buttonsToRemove, function(index, buttonId) {
					delete globalData.board.idToButtons[buttonId];
				});
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
			if( typeof jsonObj.playerConnectionStatus !== 'undefined' ) {
				if(jsonObj.playerConnectionStatus.connectionStateOtherPlayer) {
					$("#messageOpponentConnectionLost").html("");
				} else {
					$("#messageOpponentConnectionLost").html("OPPONENT GOT DISCONNECTED!");
				}
			}
			/* RESP_MESSAGE */
			if( typeof jsonObj.message !== 'undefined' ) {
				if(typeof jsonObj.message.score  !== 'undefined') {
					$("#messageScore").html(jsonObj.message.score);		
				}
				if(typeof jsonObj.message.title  !== 'undefined') {
					$("#messageTitle").html(jsonObj.message.title);		
				}
				if(typeof jsonObj.message.info  !== 'undefined') {
					$("#messageInfo").html(jsonObj.message.info);
				}
				if(typeof jsonObj.message.error  !== 'undefined') {
					$("#messageError").html(jsonObj.message.error);
				}
			}
			globalData.board.draw();
			
		}
		
	};	
});
