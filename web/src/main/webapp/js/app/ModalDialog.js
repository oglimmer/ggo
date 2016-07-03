define(['app/Constants', 'app/Communication', 'app/GlobalData', 'app/CursorUtil'], 
		function(Constants, communication, globalData, cursorUtil) {

	function sortCancelToEnd(array) {
		array.sort(function(a,b){
			if(a.id === b.id) {
				return 0;
			} else if(a.id === 'Cancel') {
				return 1;
			} else if(b.id === 'Cancel') {
				return -1;
			}
			return a.id < b.id ? -1 : 1;
		});
		return array;
	}

	function ModalDialog() {
		// remote
		this.title;
		this.options;
		this.show = false;
		
		// local
		this.rowHeight = 20;
	}
	
	ModalDialog.prototype.draw = function(ctx) {
		
		if(!this.show) {
			return;
		}
		
		if(this.options.length>0) {
			this.width = 130;
			this.height = 100;
		} else {
			this.width = 300;
			this.height = 200;			
		}
		
		var latestCur = cursorUtil.lastCursorPos;
		if(latestCur.x == 0) {
			latestCur.x = 10;
		}
		if(latestCur.y == 0) {
			latestCur.y = 10;
		}
		if(this.options.length==0) {
			latestCur.x = 150;
			latestCur.y = 120;
		}

		ctx.beginPath();
		ctx.fillStyle = "black";
		ctx.fillRect(latestCur.x, latestCur.y, this.width, this.height);
		ctx.strokeStyle = "white";
		ctx.lineWidth = 2;
		ctx.strokeRect(latestCur.x, latestCur.y, this.width, this.height);
		
		var y = latestCur.y+16;
		ctx.font = "12px Arial";
		ctx.fillStyle = "white";
		ctx.fillText(this.title, latestCur.x+3, y);
		y += this.rowHeight;
		var thiz = this;
		$.each(sortCancelToEnd(this.options), function(index, options) {
			var xDraw = latestCur.x+8
			var yDraw = y;
			options.x = latestCur.x;
			options.y = y-thiz.rowHeight+3;
			ctx.fillText(options.description, xDraw, yDraw);
			y += thiz.rowHeight;
		});
	};
	
	ModalDialog.prototype.onSelect = function(relMousePos) {
		var thiz = this;
		$.each(this.options, function(index, option) {
			if(relMousePos.x >= option.x && relMousePos.y >= option.y && relMousePos.x <= option.x+thiz.width && relMousePos.y <= option.y+thiz.rowHeight) {
				communication.send({
					pid: globalData.playerId,
					cmd: 'selectModalDialog',
					param: option.id
				});		
			}
		});
	}
	
	return ModalDialog;

});