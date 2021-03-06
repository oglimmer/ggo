define(['app/Constants', 'app/Communication', 'app/GlobalData'], function(Constants, communication, globalData) {


	function Button() {
		// immutable
		this.id;
		this.text;
		this.graphic;
		this.width;
		this.height;
		
		// remote, changeable
		this.selectable = true;
		this.hidden = true;
		
		// local attributes - all are set during draw() method
		this.x = null;
		this.y = null;
	}

	Button.prototype.getWidth = function() {
		return this.width/60*Constants.size.width;
	}
	
	Button.prototype.getHeight = function() {
		return this.height/60*Constants.size.height;
	}
	
	Button.prototype.draw = function(ctx, x, y) {
		this.x = x;
		this.y = y;
		
		ctx.beginPath();
		ctx.lineWidth = 1;
		ctx.strokeStyle = "black";
		ctx.fillStyle = "black";
		ctx.rect(x, y, this.getWidth(), this.getHeight());
		ctx.stroke();
		
		if(typeof this.graphic !== 'undefined' && this.graphic != null) {			
			var img=document.getElementById(this.graphic+"_"+globalData.model.myColor);
			if(typeof img === 'undefined' || img == null) {
				console.log("img = " + img + ", graphic="+ this.graphic+", globalData.model.myColor="+globalData.model.myColor);
				console.log(this.graphic);
			} else {
				ctx.drawImage(img,this.x,this.y);
			}
			ctx.font = ""+parseInt(0.167*Constants.size.width)+"px Arial";
			ctx.fillText(this.text,x+.5*Constants.size.width,y+.767*Constants.size.height);
		} else {
			ctx.font = ""+parseInt(0.167*Constants.size.width)+"px Arial";
			ctx.fillText(this.text,x+0.033*Constants.size.width,y+.2*Constants.size.height);
		}
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