define(['./Constants', './Communication', './GlobalData'], function(Constants, communication, globalData) {


	/**
	 * CLASS Field
	 * 
	 * @parameter id unique id
	 * @parameter color to use
	 * @parameter pos on x,y objects
	 */
	function Field(id, color, pos, selectable) {
		this.id = id;
		this.color = color;
		this.x = pos.x;
		this.y = pos.y;
		this.width = Constants.size.width;
		this.height = Constants.size.height;
		this.selectable = selectable;
		this.highlight = false;
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
			ctx.fillStyle = this.color;
		}
		ctx.fill();

		if (this.selectable) {
			ctx.strokeStyle = "#eeeeee";
			ctx.lineWidth = 1;
			ctx.stroke();
		}
		
		//ctx.fillStyle = "red";
		//ctx.font = "8px Arial";
		//ctx.fillText(this.x+":"+this.y,cx,cy);

	};
	
	Field.prototype.onSelect = function() {
		if(globalData.active.selectDeployTarget) {
			communication.send({
				pid: globalData.playerId,
				cmd: 'selectTargetField',
				param: this.id
			});			
		}
	}
	
	return Field;

});