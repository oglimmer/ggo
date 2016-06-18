define(['./Constants', './Communication', './GlobalData'], function(Constants, communication, globalData) {

	function drawArrow(ctx, fromx, fromy, tox, toy){
        //variables to be used when creating the arrow
        var headlen = 4;

        var angle = Math.atan2(toy-fromy,tox-fromx);

        //starting path of the arrow from the start square to the end square and drawing the stroke
        ctx.beginPath();
        ctx.moveTo(fromx, fromy);
        ctx.lineTo(tox, toy);
        ctx.strokeStyle = "#cc0000";
        ctx.lineWidth = 7;
        ctx.stroke();

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
        ctx.strokeStyle = "#cc0000";
        ctx.lineWidth = 7;
        ctx.stroke();
        ctx.fillStyle = "#cc0000";
        ctx.fill();
    }

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

	Unit.prototype.draw = function(ctx) {
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
			ctx.fillStyle = "black";
			ctx.fill();
			ctx.strokeStyle = "white";
			ctx.lineWidth = 1;
			ctx.stroke();
		}
		
		switch(this.unitType) {
			case Constants.UNIT_TYPE_INFANTRY:
				var img=document.getElementById("infantry_"+this.color);
				ctx.drawImage(img,cx-width/4+7,cy - height/2+5);
				break;
			case Constants.UNIT_TYPE_TANK:
				var img=document.getElementById("tank_"+this.color);
				ctx.drawImage(img,cx-width/4-10,cy - height/2+14);
				break;
			case Constants.UNIT_TYPE_AIRBORNE:
				var img=document.getElementById("airborne_"+this.color);
				ctx.drawImage(img,cx-width/4-10,cy - height/2+14);
				break;
			case Constants.UNIT_TYPE_HELICOPTER:
				var img=document.getElementById("helicopter_"+this.color);
				ctx.drawImage(img,cx-width/4-3,cy - height/2+8);
				break;
			case Constants.UNIT_TYPE_ARTILLERY:
				var img=document.getElementById("artillery_"+this.color);
				ctx.drawImage(img,cx-width/4-10,cy - height/2+14);
				break;
			case Constants.UNIT_TYPE_CITY:
				var img=document.getElementById("city_"+this.color);
				ctx.drawImage(img,cx-width/4-10,cy - height/2+5);
				break;
		}
		
		if(this.command != null && this.command.commandType != null) {
			if(this.command.commandType == "F") {
				ctx.beginPath();
				ctx.fillStyle = "white";
				ctx.font = "400 20px Arial";
				ctx.lineWidth = 3;
				ctx.strokeStyle = 'black';
				ctx.strokeText(this.command.commandType,cx-2,cy+6);
			} else {
				var targetFieldId = this.command.x+":"+this.command.y;
				var field = globalData.board.corToFields[targetFieldId];
				drawArrow(ctx, cx, cy, field.realX(), field.realY());
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
	
	return Unit;

});