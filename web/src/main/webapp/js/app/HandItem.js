define(['./Constants', './Communication', './GlobalData'], function(Constants, communication, globalData) {


	/**
	 * CLASS HandItem
	 * 
	 */
	function HandItem() {
		// immutable
		this.id = null; //id;
		this.unitType = null; //unitType;
		
		// remote, changeable
		this.selected = false;
		this.selectable = false;
		
		// local attributes - all are set during draw() method
		this.x = null;
		this.y = null;
		this.width = null;
		this.height = null;
	}

	/*
	 * draws the HandItem
	 */
	HandItem.prototype.draw = function(ctx, x, y) {
		this.width = Constants.size.width*.8;
		this.height = Constants.size.height*.8;
		this.x = x;
		this.y = y;
		
		ctx.beginPath();
		
		if(this.selected) {
			ctx.fillStyle = "black";
			ctx.fillRect(this.x,this.y,this.width,this.height);
		}
		
		switch(this.unitType) {
			case Constants.UNIT_TYPE_INFANTRY:
				var img=document.getElementById("infantry_"+globalData.model.myColor);
				ctx.drawImage(img,this.x,this.y);
				break;
			case Constants.UNIT_TYPE_TANK:
				var img=document.getElementById("tank_"+globalData.model.myColor);
				ctx.drawImage(img,this.x,this.y);
				break;
			case Constants.UNIT_TYPE_AIRBORNE:
				var img=document.getElementById("airborne_"+globalData.model.myColor);
				ctx.drawImage(img,this.x,this.y);
				break;
			case Constants.UNIT_TYPE_HELICOPTER:
				var img=document.getElementById("helicopter_"+globalData.model.myColor);
				ctx.drawImage(img,this.x,this.y);
				break;
			case Constants.UNIT_TYPE_ARTILLERY:
				var img=document.getElementById("artillery_"+globalData.model.myColor);
				ctx.drawImage(img,this.x,this.y);
				break;
		}
		ctx.lineWidth = 1;
		ctx.strokeStyle = "black";
		ctx.rect(this.x,this.y,this.width,this.height);
		ctx.stroke();
		
	};
	
	HandItem.prototype.onSelect = function() {
		if(this.selectable){
			communication.send({
				pid: globalData.playerId,
				cmd: 'selectHandCard',
				param: this.id
			});
		}
	}
	
	return HandItem;

});