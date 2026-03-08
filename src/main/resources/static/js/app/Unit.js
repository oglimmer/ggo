define(['app/Constants', 'app/Communication', 'app/GlobalData'], function(Constants, communication, globalData) {

	function Unit() {
		// immutable
		this.id = null;
		this.color = null;
		this.unitType = null;
		
		// changeable, remote
		this.x = null;
		this.y = null;
		this.selected = false;
		this.selectable = false;
		this.command = null; // { commandType:string, x:int, y:int }
		
		// local
		this.width = Constants.size.width;
		this.height = Constants.size.height;
	}

	Unit.prototype.draw0 = function(ctx) {
		var x = this.x;
		var y = this.y;
		var width = this.width;
		var height = this.height;
		var cx = width + x * width - (y + 1) % 2 * width / 2;
		var cy = 0.5 * height + y * (3 / 4 * height);
		
		
		if(this.selected) {
			ctx.beginPath();
			ctx.moveTo(cx, cy - height / 2);
			ctx.lineTo(cx + width / 2, cy - height / 4);
			ctx.lineTo(cx + width / 2, cy + height / 4);
			ctx.lineTo(cx, cy + height / 2);
			ctx.lineTo(cx - width / 2, cy + height / 4);
			ctx.lineTo(cx - width / 2, cy - height / 4);
			ctx.lineTo(cx, cy - height / 2);
			ctx.fillStyle = "rgba(0, 229, 120, 0.25)";
			ctx.fill();
			ctx.strokeStyle = "#00e578";
			ctx.lineWidth = 2;
			ctx.stroke();
		}
		
		if(this.color === Constants.SIDE_GREEN) {
			ctx.filter = "brightness(1.8) saturate(1.6)";
		}
		switch(this.unitType) {
			case Constants.UNIT_TYPE_INFANTRY:
				var img=document.getElementById("infantry_"+this.color);
				ctx.drawImage(img,cx-img.width*.5,cy - img.height*.5);
				break;
			case Constants.UNIT_TYPE_TANK:
				var img=document.getElementById("tank_"+this.color);
				ctx.drawImage(img,cx-img.width*.5,cy - img.height*.5);
				break;
			case Constants.UNIT_TYPE_AIRBORNE:
				var img=document.getElementById("airborne_"+this.color);
				ctx.drawImage(img,cx-img.width*.5,cy - img.height*.5);
				break;
			case Constants.UNIT_TYPE_HELICOPTER:
				var img=document.getElementById("helicopter_"+this.color);
				ctx.drawImage(img,cx-img.width*.5,cy - img.height*.5);
				break;
			case Constants.UNIT_TYPE_ARTILLERY:
				var img=document.getElementById("artillery_"+this.color);
				ctx.drawImage(img,cx-img.width*.5,cy - img.height*.5);
				break;
			case Constants.UNIT_TYPE_CITY:
				var img=document.getElementById("city_"+this.color);
				ctx.drawImage(img,cx-img.width*.5,cy - img.height*.5);
				break;
		}
		ctx.filter = "none";
	};
	
	Unit.prototype.draw1 = function(ctx) {
		var x = this.x;
		var y = this.y;
		var width = this.width;
		var height = this.height;
		var cx = width + x * width - (y + 1) % 2 * width / 2;
		var cy = 0.5 * height + y * (3 / 4 * height);
		
		if(this.command != null && this.command.commandType != null) {
			if(this.command.commandType == "F") {
				ctx.beginPath();
				ctx.fillStyle = "#00e578";
				ctx.font = "700 20px 'Rajdhani', sans-serif";
				ctx.lineWidth = 3;
				ctx.strokeStyle = 'rgba(0,0,0,0.8)';
				ctx.strokeText(this.command.commandType,cx-2,cy+6);
			} else {
				var targetFieldId = this.command.x+":"+this.command.y;
				var field = globalData.model.boardState.corToFields[targetFieldId];
				var color;
				switch(this.command.commandType) {
				case "M":
					color = "#ff3b3b";
					break;
				case 'B':
					color = "#00e578";
					break;
				case 'S':
					color = "#ffc107";
					break;
				}
				drawArrow(ctx, cx, cy, field.realX(), field.realY(), color, false);
			}
		}		
	};
	
	Unit.prototype.draw2 = function(ctx) {
		var x = this.x;
		var y = this.y;
		var width = this.width;
		var height = this.height;
		var cx = width + x * width - (y + 1) % 2 * width / 2;
		var cy = 0.5 * height + y * (3 / 4 * height);
		
		if(this.command != null && this.command.commandType != null) {
			if(this.command.commandType != "F") {
				var targetFieldId = this.command.x+":"+this.command.y;
				var field = globalData.model.boardState.corToFields[targetFieldId];
				var color;
				switch(this.command.commandType) {
				case "M":
					color = "#ff3b3b";
					break;
				case 'B':
					color = "#00e578";
					break;
				case 'S':
					color = "#ffc107";
					break;
				}
				drawArrow(ctx, cx, cy, field.realX(), field.realY(), color, true);
			}
		}
		
	};
	
	Unit.prototype.onSelect = function() {
		if(this.selectable){
			communication.send({
				pid: globalData.playerId,
				cmd: 'selectUnit',
				param: this.id
			});
		}
	}

	function drawArrow(ctx, fromx, fromy, tox, toy, color, onlyHeads){
        //variables to be used when creating the arrow
        var headlen = 4;

        var angle = Math.atan2(toy-fromy,tox-fromx);

        if(!onlyHeads) {
        
	        //starting path of the arrow from the start square to the end square and drawing the stroke
	        ctx.beginPath();
	        ctx.moveTo(fromx, fromy);
	        ctx.lineTo(tox, toy);
	        ctx.strokeStyle = color;
	        switch(color) {
	        case '#ff3b3b':
	        	ctx.lineWidth = 7;
	        	break;
	        case '#00e578':
	        	ctx.lineWidth = 3;
	        	break;
	        case '#ffc107':
	        	ctx.lineWidth = 5;
	        	break;
	        }
	        ctx.stroke();
	        
        }

        //starting a new path from the head of the arrow to one of the sides of the point
        ctx.beginPath();
        ctx.moveTo(tox, toy);
        ctx.lineTo(tox-headlen*Math.cos(angle-Math.PI/7),toy-headlen*Math.sin(angle-Math.PI/7));

        //path from the side point of the arrow, to the other side point
        ctx.lineTo(tox-headlen*Math.cos(angle+Math.PI/7),toy-headlen*Math.sin(angle+Math.PI/7));

        //path from the side point back to the tip of the arrow, and then again to the opposite side point
        ctx.lineTo(tox, toy);
        ctx.lineTo(tox-headlen*Math.cos(angle-Math.PI/7),toy-headlen*Math.sin(angle-Math.PI/7));

        //draws the paths created above
        ctx.strokeStyle = "rgba(0,0,0,0.6)";
        ctx.lineWidth = 7;
        ctx.stroke();
        ctx.fillStyle = color;
        ctx.fill();
    }
	
	return Unit;

});