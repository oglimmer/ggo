define(['jquery', 'app/Field', 'app/Unit', 'app/Constants', 'app/HandItem', 'app/GlobalData', 'app/Button', 'app/ModalDialog', 'app/Messages', 'app/OpponentConnectionState'], 
		function($, Field, Unit, Constants, HandItem, globalData, Button, ModalDialog, Messages, OpponentConnectionState) {
	
	
	var classNameToConstructor = {};
	$.each(arguments, function(index, ctor) {
		if(typeof ctor.name !=='undefined' && ctor.name != null) {
			classNameToConstructor[ctor.name] = ctor;
		}
	});
	
	var logEnabled = false;
	function log(obj) {
		if(logEnabled) {
			console.log(obj);
		}
	}
	
	function assert(statement, text) {
		if(!statement) {
			throw new String("ASSERTION FAILED!!!"+text);
		}
	}
	
	function construct(jsClass) {
		var ctor = classNameToConstructor[jsClass];
		assert(ctor!=null, "No constructor for "+jsClass);
		return new (ctor.bind.apply(ctor))();
	}
	
	function copy(source, target, level) {
		// source and target need to be of same type 'object' or 'array' or null
		assert(!(typeof source === 'object' && typeof target === 'array'));
		assert(!(typeof source === 'array' && typeof target === 'object'));
		
		if(typeof source === 'object' || typeof target === 'object') {
			copyObject(source, target, level);
		} else if(typeof source === 'array' || typeof target === 'array') {
			copyArray(source, target, level);
		} else {
			assert(source === null && target === null);
		}
		
	}
	
	function copyObject(source, target) {
		assert((typeof source === 'object' && typeof target === 'object') 
				|| (typeof source === 'array' && typeof target === 'array'), "copyObject requires source and target are either both object or both array, but source="+typeof source+", target="+typeof target);
		
		for(var attKey in source) {
			var val = source[attKey];
			if(val === '##REMOVED##') {
				// attribute got removed
				delete target[attKey];
			} else {				
				if(typeof val === 'object' || typeof val === 'array') {
					if(val === null) {
						// set attribute of type object/array to null
						target[attKey] = null;						
					} else { 
						if(typeof target[attKey] === 'undefined' || target[attKey] === null) {
							// old value was undefined/null, thus create new object
							if(val !=  null && typeof val.jsClass !== 'undefined') {
								target[attKey] = construct(val.jsClass);
							} else {
								target[attKey] = {};
							}		
						}
						// deep-copy
						copy(val, target[attKey]);
					}
				} else {
					// set attribute of type string,number,boolean,'null' to new val (incl. null)
					target[attKey] = val;						
				}
			}
		}
	}
	
	function copyArray(source, target, level) {
		assert(typeof source === 'array' || typeof target === 'array');
		
		for(var att in source) {
			if(typeof source[att] === 'array' && att.indexOf("##REMOVED##") > 0 ) {
				console.log("REMOVING ARRAY ELEMENTS NOT IMPLEMENTED");
			} else {
				if(typeof source[att] === 'array') {
					console.log("ADDING/CHANGING ARRAY ELEMENTS NOT IMPLEMENTED");
				} else {
					target[att] = source[att];						
				}
			}
		}
	}

	return {
		merge: copy
	};

});