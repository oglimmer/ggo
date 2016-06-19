define([], function() {

	return {
		
		lastCursorPos : {
			x : 0,
			y : 0
		},
		
		/**
		 * Returns an object { "x":?, "y":? } for a given click event relative to a html element
		 */
		getRelativeMousePos : function(evt, htmlElement) {
			var obj = htmlElement;
			var top = 0;
			var left = 0;
			while (obj && obj.tagName != 'BODY') {
				top += obj.offsetTop;
				left += obj.offsetLeft;
				obj = obj.offsetParent;
			}

			this.lastCursorPos.x = evt.clientX - left + window.pageXOffset;
			this.lastCursorPos.y = evt.clientY - top + window.pageYOffset;
			
			return this.lastCursorPos;
		}
		
	};

});