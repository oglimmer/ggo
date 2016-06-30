define(['./Constants', './Communication', './GlobalData'], function(Constants, communication, globalData) {


	/**
	 * CLASS Field
	 * 
	 * @parameter id unique id
	 * @parameter color to use
	 * @parameter pos on x,y objects
	 */
	function Field() {
		//IMMUTABLE
		this.id = null; //id;
		this.x = null; //pos.x;
		this.y = null; //pos.y;
		// changeable, remote attributes
		this.selectable = false;
		this.highlight = false;
		
		// local attributes
		this.width = Constants.size.width;
		this.height = Constants.size.height;
	}
	
	Field.prototype.realX = function() {
		return this.width + this.x * this.width - (this.y + 1) % 2 * this.width / 2;
	}
	Field.prototype.realY = function() {
		return 0.5 * this.height + this.y * (3 / 4 * this.height);
	}

	/*
	 * draws the field
	 */
	Field.prototype.draw = function(ctx, showCoordinates) {
		var x = this.x;
		var y = this.y;
		var width = this.width;
		var height = this.height;
		var cx = width + x * width - (y + 1) % 2 * width / 2;
		var cy = 0.5 * height + y * (3 / 4 * height);

		ctx.beginPath();
		ctx.moveTo(cx, cy - height / 2);
		ctx.lineTo(cx + width / 2, cy - height / 4);
		ctx.lineTo(cx + width / 2, cy + height / 4);
		ctx.lineTo(cx, cy + height / 2);
		ctx.lineTo(cx - width / 2, cy + height / 4);
		ctx.lineTo(cx - width / 2, cy - height / 4);
		ctx.lineTo(cx, cy - height / 2);
		if (this.highlight) {
			ctx.fillStyle = "#889988";
		} else {
			ctx.fillStyle = "#c2d6d6";
		}
		ctx.fill();

		if (true) {
			ctx.strokeStyle = "#eeeeee";
			ctx.lineWidth = 1;
			ctx.stroke();
		}
		
		if(showCoordinates) {
			ctx.save();
			ctx.translate(cx-9,cy-15);
			ctx.rotate(-Math.PI/7);
			ctx.textAlign = "center";
			ctx.fillStyle = "#222222";
			ctx.font = "8px Arial";
			ctx.fillText(this.x+":"+this.y,0,0);
			ctx.restore();
		}		 
	};
	
	Field.prototype.onSelect = function() {
		if(this.selectable) {
			communication.send({
				pid: globalData.playerId,
				cmd: 'selectTargetField',
				param: this.id
			});			
		}
	}
	
	return Field;

});