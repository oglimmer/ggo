define(['jquery', 'app/Constants', 'app/Communication', 'app/GlobalData', 'app/CursorUtil'], function($, Constants, communication, globalData, cursorUtil) {	
	
	function sortById(source) {
		var array = [];
		for(var att in source) {
			array.push(source[att]);
		}
		array.sort(function(a,b){
			return a.id < b.id ? -1 : a.id == b.id ? 0 : 1;
		});
		return array;
	}

	/*private class Board*/
	function Board(elementId) {
		this.canvasBoard = document.getElementById(elementId);
		this.ctxBoard = this.canvasBoard.getContext('2d');

		var thiz = this;
		$(document).ready(function() {
			$("#"+elementId).click(function(evt) {
				thiz.clickHandler(evt);
			});
		});
	}

	/*private*/ Board.prototype.clickHandler = function(evt) {
		
		var relMousePos = cursorUtil.getRelativeMousePos(evt, this.canvasBoard);
		
		if(this.clickHandlerModalDialog(relMousePos)) {
			return;
		}
		
		var selectedHex = this.getFieldByPos(relMousePos);
		if (selectedHex != null) {
			this.clickHandlerFieldUnit(selectedHex);
		} else {
			this.clickHandlerHandItemButtons(relMousePos);
		}
		
	}
	
	/*private*/ Board.prototype.clickHandlerModalDialog = function(relMousePos) {
		if(typeof globalData.model.modalDialogState !== 'undefined' 
			&& globalData.model.modalDialogState != null
			&& globalData.model.modalDialogState.show) {
			globalData.model.modalDialogState.onSelect(relMousePos);
			return true;
		}
		return false;
	}
	/*private*/ Board.prototype.clickHandlerFieldUnit = function(selectedHex) {
		if (typeof selectedHex.onSelect !== 'undefined') {
			selectedHex.onSelect();
		}					

		var allUnits = globalData.model.boardState.idToUnits;
		for ( var unitProp in allUnits) {
			var unit = allUnits[unitProp];
			if (unit.x == selectedHex.x && unit.y == selectedHex.y) {
				if (typeof unit.onSelect !== 'undefined') {
					unit.onSelect();
				}					
			}
		}
	}
	/*private*/ Board.prototype.clickHandlerHandItemButtons = function(relMousePos) {
		function check(items) {
			$.each(items, function(index, item) {
				if(item.x <= relMousePos.x && item.y <= relMousePos.y 
						&& item.x+item.width >= relMousePos.x && item.y+item.height >= relMousePos.y) {
					if (typeof item.onSelect !== 'undefined' ) {
						item.onSelect();
					}	
				}
			})
		}
		check(globalData.model.boardState.idToHanditems);
		check(globalData.model.boardState.idToButtons);
	}

	/*public*/ Board.prototype.draw = function() {		
		this.ctxBoard.clearRect(0, 0, this.ctxBoard.canvas.width, this.ctxBoard.canvas.height);
		this.drawFields();
		this.drawUnits();
		this.drawHand();
		this.drawButtons();
		this.drawModalDialog();
	};	
	
	/*private*/ Board.prototype.drawFields = function() {
		for ( var f in globalData.model.boardState.corToFields) {
			globalData.model.boardState.corToFields[f].draw(this.ctxBoard, globalData.model.boardState.showCoordinates);
		}
	}
	/*private*/ Board.prototype.drawUnits = function() {
		// units- z-level:0
		for ( var f in globalData.model.boardState.idToUnits) {
			var unitToDraw = globalData.model.boardState.idToUnits[f];
			unitToDraw.draw0(this.ctxBoard);
		}
		// units- z-level:1
		for ( var f in globalData.model.boardState.idToUnits) {
			var unitToDraw = globalData.model.boardState.idToUnits[f];
			unitToDraw.draw1(this.ctxBoard);
		}
		// units- z-level:2
		for ( var f in globalData.model.boardState.idToUnits) {
			var unitToDraw = globalData.model.boardState.idToUnits[f];
			unitToDraw.draw2(this.ctxBoard);
		}
	}
	/*private*/ Board.prototype.drawHand = function() {
		this.ctxBoard.beginPath();
		this.ctxBoard.fillStyle = "#dddddd";
		this.ctxBoard.fillRect(0, 470, this.ctxBoard.canvas.width-10, 57);
		var x = 3;
		var y = 475;
		for ( var f in globalData.model.boardState.idToHanditems) {
			var handitemToDraw = globalData.model.boardState.idToHanditems[f];
			handitemToDraw.draw(this.ctxBoard, x, y);
			x += Constants.size.width*.8+4;
		}
		if(x == 3) {
			this.ctxBoard.beginPath();
			this.ctxBoard.font = "16px Arial";
			this.ctxBoard.fillStyle = "black";
			this.ctxBoard.fillText("No units at hand.",x,y+28);
		}

	}
	/*private*/ Board.prototype.drawButtons = function() {
		var x = 3;
		var y = 535;
		var thiz = this;
		$.each(sortById(globalData.model.boardState.idToButtons), function(buttonId, buttonToDraw) {
			if(!buttonToDraw.hidden) {
				buttonToDraw.draw(thiz.ctxBoard, x, y);
				x += buttonToDraw.width+4;
			}
		});

	}
	/*private*/ Board.prototype.drawModalDialog = function() {
		if(typeof globalData.model.modalDialogState !== 'undefined' && globalData.model.modalDialogState != null) {
			globalData.model.modalDialogState.draw(this.ctxBoard);
		}
	}
	
	
	/**
	 * returns a Field object which is located at pos (x,y)
	 */
	/*private*/Board.prototype.getFieldByPos = function(pos) {
		// x,y base coordinate
		var hexPosY = Math.floor(pos.y / (Constants.size.height * 0.75));
		var hexPosX = Math.floor(pos.x / Constants.size.width);
		// odd or even row?
		var rowSelector = hexPosY % 2;

		// x,y rel to current element
		var posYRel = pos.y - hexPosY * Constants.size.height * 0.75;
		var posXRel = pos.x - hexPosX * Constants.size.width;

		switch (rowSelector) {
		case 0:
			// odd row
			if (posYRel < Constants.size.height / 4) {
				// the top part (looks like a roof ^ )
				var s = posXRel / (Constants.size.width / 2);
				var r = posYRel / (Constants.size.height / 4);
				if (0 <= r && s <= 1 && r + s <= 1) {
					// left of the roof (belongs to previous row and column)
					hexPosX--;
					hexPosY--;
				} else {
					s = posYRel / (Constants.size.height / 4);
					r = posXRel / (Constants.size.width / 2) - s - 1;
					if (0 <= r && s <= 1 && r + s <= 1) {
						// right of the roof (belongs to previous row)
						hexPosY--;
					}
					// no change for "under the roof"
				}
			}
			break;
		case 1:
			// even row
			if (posYRel > Constants.size.height / 4) {
				// lower part (divided in the middle)
				if (posXRel < Constants.size.width / 2) {
					// left part (belongs to previous hex)
					hexPosX--;
				}
			} else {
				// upper part (looks like a "v")
				var s = posXRel / (Constants.size.width / 2);
				var r = posYRel / (Constants.size.height / 4) - s;
				if (0 <= r && s <= 1 && r + s <= 1) {
					// left lower part of the v
					hexPosX--;
				} else {
					var s = (posXRel - Constants.size.width) / (Constants.size.width / 2) * -1;
					var r = (posYRel / (Constants.size.height / 4)) - s;
					if (!(0 <= r && s <= 1 && r + s <= 1)) {
						// middle part v (or not the right lower part)
						hexPosY--;
					}
				}
			}
			break;
		}

		if (globalData.model.boardState.corToFields.hasOwnProperty(hexPosX + ":" + hexPosY)) {
			return globalData.model.boardState.corToFields[hexPosX + ":" + hexPosY];
		}
		return null;
	};
	
	return function(elementId) {
		var board = new Board(elementId);	
		this.draw = function() {
			board.draw();
		}
	};
	
});