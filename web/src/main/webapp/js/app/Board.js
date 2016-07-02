define(['jquery', './Constants', './Communication', './GlobalData', './CursorUtil'], function($, Constants, communication, globalData, cursorUtil) {	
	
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
	
	Board.prototype.clickHandler = function(evt) {
		
		var relMousePos = cursorUtil.getRelativeMousePos(evt, this.canvasBoard);
		
		if(typeof globalData.model.modalDialogState !== 'undefined' 
			&& globalData.model.modalDialogState != null
			&& globalData.model.modalDialogState.show) {
			globalData.model.modalDialogState.onSelect(relMousePos);
			return;
		}
		
		var selectedHex = this.getFieldByPos(relMousePos);
		if (selectedHex != null) {

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

		} else {
			$.each(globalData.model.boardState.idToHanditems, function(index, handItem) {
				if(handItem.x <= relMousePos.x && handItem.y <= relMousePos.y 
						&& handItem.x+handItem.width >= relMousePos.x && handItem.y+handItem.height >= relMousePos.y) {
					if (typeof handItem.onSelect !== 'undefined' ) {
						handItem.onSelect();
					}	
				}
			})
			$.each(globalData.model.boardState.idToButtons, function(index, buttonIten) {
				if(buttonIten.x <= relMousePos.x && buttonIten.y <= relMousePos.y 
						&& buttonIten.x+buttonIten.width >= relMousePos.x && buttonIten.y+buttonIten.height >= relMousePos.y) {
					if (typeof buttonIten.onSelect !== 'undefined' ) {
						buttonIten.onSelect();
					}	
				}
			})
		}
		
	}

	Board.prototype.draw = function() {
		// clear the field
		this.ctxBoard.clearRect(0, 0, this.ctxBoard.canvas.width, this.ctxBoard.canvas.height);
		// fields		
		for ( var f in globalData.model.boardState.corToFields) {
			globalData.model.boardState.corToFields[f].draw(this.ctxBoard, globalData.model.boardState.showCoordinates);
		}
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
		// hand
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
			this.ctxBoard.fillText("No units in hand.",x,y+28);
		}
		// buttons
		var x = 3;
		var y = 535;
		var thiz = this;
		$.each(sortById(globalData.model.boardState.idToButtons), function(buttonId, buttonToDraw) {
			if(!buttonToDraw.hidden) {
				buttonToDraw.draw(thiz.ctxBoard, x, y);
				x += buttonToDraw.width+4;
			}
		});
		// modalDialog
		if(typeof globalData.model.modalDialogState !== 'undefined' && globalData.model.modalDialogState != null) {
			globalData.model.modalDialogState.draw(this.ctxBoard);
		}
	};	
	
	/**
	 * returns a Field object which is located at pos (x,y)
	 */
	Board.prototype.getFieldByPos = function(pos) {
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

	return Board;
	
});