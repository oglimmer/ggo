define(['./Constants', './Communication', './GlobalData'], function(Constants, communication, globalData) {


	/**
	 * CLASS HandItem
	 * 
	 */
	function HandItem(id, unitType) {
		this.id = id;
		this.unitType = unitType;
		this.selected = false;
		
		// this attributes are set during draw() method
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
		
		if(this.selected) {
			ctx.fillStyle = "black";
			ctx.fillRect(this.x,this.y,this.width,this.height);
		}
		
		switch(this.unitType) {
			case Constants.UNIT_TYPE_INFANTRY:
				var img=document.getElementById("inf_"+globalData.myColor);
				ctx.drawImage(img,this.x,this.y);
				break;
			case Constants.UNIT_TYPE_TANK:
				var img=document.getElementById("tank_"+globalData.myColor);
				ctx.drawImage(img,this.x,this.y);
				break;
			case Constants.UNIT_TYPE_AIRBORNE:
				var img=document.getElementById("airborne_"+globalData.myColor);
				ctx.drawImage(img,this.x,this.y);
				break;
			case Constants.UNIT_TYPE_HELICOPTER:
				var img=document.getElementById("helicopter_"+globalData.myColor);
				ctx.drawImage(img,this.x,this.y);
				break;
			case Constants.UNIT_TYPE_ARTILLERY:
				var img=document.getElementById("artillery_"+globalData.myColor);
				ctx.drawImage(img,this.x,this.y);
				break;
		}
		ctx.strokeStyle = "black";
		ctx.rect(this.x,this.y,this.width,this.height);
		ctx.stroke();
		
	};
	
	HandItem.prototype.onSelect = function() {
		if(globalData.active.selectHandItem){
			communication.send({
				pid: globalData.playerId,
				cmd: 'selectHandCard',
				param: this.id
			});
		}
	}
	
	return HandItem;

});