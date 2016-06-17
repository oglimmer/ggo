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

	/*
	 * draws the field
	 */
	Field.prototype.draw = function(ctx) {
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
			ctx.fillStyle = "#3366cc";
		}
		ctx.fill();

		if (true) {
			ctx.strokeStyle = "#eeeeee";
			ctx.lineWidth = 1;
			ctx.stroke();
		}
		
		// ctx.fillStyle = "red";
		// ctx.font = "8px Arial";
		// ctx.fillText(this.x+":"+this.y,cx,cy);

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