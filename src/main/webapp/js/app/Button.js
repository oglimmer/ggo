define(['./Constants', './Communication', './GlobalData'], function(Constants, communication, globalData) {


	/**
	 * CLASS Button
	 * 
	 */
	function Button(id, text) {
		// immutable
		this.id = id; //id;
		this.text = text;
		
		// remote, changeable
		this.selectable = true;
		this.hidden = true;
		
		// local attributes - all are set during draw() method
		this.x = null;
		this.y = null;
		this.width = 30;
		this.height = 20;
	}

	/*
	 * draws the Button
	 */
	Button.prototype.draw = function(ctx, x, y) {
		this.x = x;
		this.y = y;
		
		ctx.beginPath();
		ctx.lineWidth = 1;
		ctx.strokeStyle = "black";
		ctx.fillStyle = "black";
		ctx.rect(x, y, 30, 20);
		ctx.font = "10px Arial";
		ctx.fillText(this.text,x+2,y+12);
		ctx.stroke();
	};
	
	Button.prototype.onSelect = function() {
		if(this.selectable){
			communication.send({
				pid: globalData.playerId,
				cmd: 'button',
				param: this.id
			});
		}
	}
	
	return Button;

});