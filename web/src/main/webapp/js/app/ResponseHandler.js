define(['jquery', './Field', './Unit', './Constants', './HandItem', './GlobalData', './Button', './ModalDialog', './Messages', './OpponentConnectionState'], 
		function ($, Field, Unit, Constants, HandItem, globalData, Button, ModalDialog, Messages, OpponentConnectionState) {

	var logEnabled = false;
	function log(obj) {
		if(logEnabled) {
			console.log(obj);
		}
	}
	
	function assert(statement, text) {
		if(!statement) {
			throw "ASSERTION FAILED!!!"+text;
		}
	}
	
	function construct(jsClass) {
		switch(jsClass) {
		case 'Field':
			return new Field();
			break;
		case 'Unit':
			return new Unit();
			break;
		case 'Button':
			return new Button();
			break;
		case 'HandItem':
			return new HandItem();
			break;
		case 'Messages':
			return new Messages();
			break;
		case 'OpponentConnectionState':
			return new OpponentConnectionState();
			break;
		default:
			assert(false, "Illegal jsClass: "+jsClass);
		}
	}
	
	/**
	 * source = array or object
	 * target = array or object
	 */
	function copy(source, target, level) {
		assert(typeof source === 'object' || typeof source === 'array');
		assert(typeof target === 'object' || typeof target === 'array');
		
//		if(source != null && typeof source.graphic !== 'undefined' && source.graphic === null){
		//			logEnabled = true;
		//		}
		
		log(level+"------------>>>");
		log(source);
		log(target);
		log(level+"<<<<");
		
		for(var att in source) {

			log(level+" COPY "+att);
			
			if(source[att] === '##REMOVED##') {
				console.log("REMOVING OBJECT ATTRIBUTE!! warning not verified.");
				delete target[att]; 
			} else if(typeof source[att] === 'array' && att.indexOf("##REMOVED##") > 0 ) {
				console.log("REMOVING ARRAY ELEMENTS NOT IMPLEMENTED");
			} else {
				if(typeof source[att] === 'object') {
					
					log(level+" DEEP COPY FOR OBJECT "+att);
					log(source[att]);
					
					if(source[att] === null) {

						log(level+" set target to null");
						
						target[att] = null;
						
					} else if(typeof target[att] === 'undefined' || target[att] === null) {
						
						log(level+" NEW COPY FOR OBJECT "+att);
						
						if(source[att] !=  null && typeof source[att].jsClass !== 'undefined') {
							target[att] = construct(source[att].jsClass);
						} else {
							target[att] = {};
						}						
						copy(source[att], target[att], level+1);
						
					} else {						
						
						log(level+" SIMPLE COPY FOR OBJECT "+att);
						
						copy(source[att], target[att], level+1);
					}
				} else if(typeof source[att] === 'array') {
					console.log("ADDING/CHANGING ARRAY ELEMENTS NOT IMPLEMENTED");
				} else {
					
					log(level+" SIMPLE ASSIGN "+att);
					
					target[att] = source[att];						
				}
			}
		}
	}
	
	return {
		
		process: function(jsonObj) {
			console.log("GOT FROM SERVER:");
			console.log(jsonObj);
			
			copy(jsonObj, globalData.model, 0);
			
			/* RESP_MODAL_DIALOG_EN */
			if( typeof jsonObj.modalDialogEnable !== 'undefined' ) {
				globalData.modalDialg = new ModalDialog(jsonObj.modalDialogEnable);
			}
			/* RESP_MODAL_DIALOG_DIS */
			if( typeof jsonObj.modalDialogDisable !== 'undefined' ) {
				delete globalData.modalDialg;
			}
			globalData.board.draw();
			
		}
		
	};	
});
