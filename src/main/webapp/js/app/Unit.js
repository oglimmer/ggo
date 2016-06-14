define(['./Constants'], function(Constants) {


	/**
	 * CLASS Unit
	 * 
	 * @parameter id unique id
	 * @parameter color to use
	 * @parameter pos on x,y objects
	 */
	function Unit() {
		// immutable
		this.id = null; //id;
		this.color = null; //color;
		this.type = null; //type;
		
		// changeable, remote
		this.x = null; //pos.x;
		this.y = null; //pos.y;
		this.selectable = false; //selectable;
		
		// local
		this.width = Constants.size.width;
		this.height = Constants.size.height;
	}

	/*
	 * draws the field
	 */
	Unit.prototype.draw = function(ctx) {
		var x = this.x;
		var y = this.y;
		var width = this.width;
		var height = this.height;
		var cx = width + x * width - (y + 1) % 2 * width / 2;
		var cy = 0.5 * height + y * (3 / 4 * height);
		
		switch(this.type) {
			case Constants.UNIT_TYPE_INFANTRY:
				var img=document.getElementById("inf_"+this.color);
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
	};
	
	return Unit;

});