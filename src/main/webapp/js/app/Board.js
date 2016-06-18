define(['jquery', './Constants', './Communication', './GlobalData'], function($, Constants, communication, globalData) {	
	
	function sort(source) {
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
		var canvasBoard = document.getElementById(elementId);
		this.ctxBoard = canvasBoard.getContext('2d');
		// FIELDS
		this.corToFields = {}; // map<"xCor:yCor" as string, field-object>
		// UNITS (on board)
		this.idToUnits = {}; // map<id,unit-object>
		// HANDITEMS (in hand)
		this.idToHanditems = {}; // map<id,handItem-object>
		// BUTTON
		this.idToButtons = {}; // map<id,button-object>

		var thiz = this;
		$(document).ready(function() {
			$("#"+elementId).click(function(evt) {
				
				/**
				 * Returns an object { "x":?, "y":? } for a given click event relative to a html element
				 */
				function getRelativeMousePos(evt, htmlElement) {
					var obj = htmlElement;
					var top = 0;
					var left = 0;
					while (obj && obj.tagName != 'BODY') {
						top += obj.offsetTop;
						left += obj.offsetLeft;
						obj = obj.offsetParent;
					}

					var mouseX = evt.clientX - left + window.pageXOffset;
					var mouseY = evt.clientY - top + window.pageYOffset;
					return {
						x : mouseX,
						y : mouseY
					};
				}
				
				var relMousePos = getRelativeMousePos(evt, canvasBoard);
				var selectedHex = thiz.getFieldByPos(relMousePos);
				if (selectedHex != null) {

					if (typeof selectedHex.onSelect !== 'undefined') {
						selectedHex.onSelect();
					}					

					for ( var unitProp in thiz.idToUnits) {
						var unit = thiz.idToUnits[unitProp];
						if (unit.x == selectedHex.x && unit.y == selectedHex.y) {
							if (typeof unit.onSelect !== 'undefined') {
								unit.onSelect();
							}					
						}
					}

				} else {
					$.each(thiz.idToHanditems, function(index, handItem) {
						if(handItem.x <= relMousePos.x && handItem.y <= relMousePos.y 
								&& handItem.x+handItem.width >= relMousePos.x && handItem.y+handItem.height >= relMousePos.y) {
							if (typeof handItem.onSelect !== 'undefined' ) {
								handItem.onSelect();
							}	
						}
					})
					$.each(thiz.idToButtons, function(index, buttonIten) {
						if(buttonIten.x <= relMousePos.x && buttonIten.y <= relMousePos.y 
								&& buttonIten.x+buttonIten.width >= relMousePos.x && buttonIten.y+buttonIten.height >= relMousePos.y) {
							if (typeof buttonIten.onSelect !== 'undefined' ) {
								buttonIten.onSelect();
							}	
						}
					})
				}
				
			});

		});
	}

	Board.prototype.addFields = function(fields) {
		for ( var i = 0; i < fields.length; i++) {
			this.addField(fields[i]);
		}
	};
	Board.prototype.addField = function(field) {
		this.corToFields[field.x + ":" + field.y] = field;
	};

	Board.prototype.addUnits = function(units) {
		for ( var i = 0; i < units.length; i++) {
			this.addUnit(units[i]);
		}
	};
	Board.prototype.addUnit = function(unit) {
		this.idToUnits[unit.id] = unit;
	};

	Board.prototype.addHandItems = function(handitems) {
		for ( var i = 0; i < handitems.length; i++) {
			this.addHandItem(handitems[i]);
		}
	};
	Board.prototype.addHandItem = function(handitem) {
		this.idToHanditems[handitem.id] = handitem;
	};
	Board.prototype.removeHandItem = function(handitemId) {
		delete this.idToHanditems[handitemId];
	};
	Board.prototype.addButtons = function(buttons) {
		for ( var i = 0; i < buttons.length; i++) {
			this.addButton(buttons[i]);
		}
	};
	Board.prototype.addButton = function(button) {
		this.idToButtons[button.id] = button;
	};
	Board.prototype.removeButton = function(buttonId) {
		delete this.idToButtons[buttonId];
	};

	Board.prototype.draw = function() {
		this.ctxBoard.clearRect(0, 0, this.ctxBoard.canvas.width, this.ctxBoard.canvas.height);
		// board		
		for ( var f in this.corToFields) {
			this.corToFields[f].draw(this.ctxBoard);
		}
		for ( var f in this.idToUnits) {
			var unitToDraw = this.idToUnits[f];
			unitToDraw.draw(this.ctxBoard);
		}
		// hand
		this.ctxBoard.beginPath();
		this.ctxBoard.fillStyle = "#dddddd";
		this.ctxBoard.fillRect(0, 470, this.ctxBoard.canvas.width-10, 57);
		var x = 3;
		var y = 475;
		for ( var f in this.idToHanditems) {
			var handitemToDraw = this.idToHanditems[f];
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
		$.each(sort(this.idToButtons), function(buttonId, buttonToDraw) {
			if(!buttonToDraw.hidden) {
				buttonToDraw.draw(thiz.ctxBoard, x, y);
				x += buttonToDraw.width+4;
			}
		});
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

		if (this.corToFields.hasOwnProperty(hexPosX + ":" + hexPosY)) {
			return this.corToFields[hexPosX + ":" + hexPosY];
		}
		return null;
	};

	return Board;
	
});