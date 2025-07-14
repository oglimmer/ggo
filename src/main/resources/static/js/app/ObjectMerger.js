define([ 'jquery', 'app/Field', 'app/Unit', 'app/Constants', 'app/HandItem',
		'app/GlobalData', 'app/Button', 'app/ModalDialog', 'app/Messages',
		'app/OpponentConnectionState', 'app/TypeUtil' ], function($, Field,
		Unit, Constants, HandItem, globalData, Button, ModalDialog, Messages,
		OpponentConnectionState, typeUtil) {

	var REMOVE_TOKEN = "##REMOVED##";

	var classNameToConstructor = {};
	$.each(arguments, function(index, ctor) {
		if ($.type(ctor.name) !== 'undefined' && ctor.name != null) {
			classNameToConstructor[ctor.name] = ctor;
		}
	});

	var logEnabled = false;
	// $.post("http://localhost:9999", { data : log + "\r\n" });
	var log = "";
	function writeLog(obj) {
		if (logEnabled) {
			console.log(obj);
			if ($.type(obj) === 'object') {
				log = log + " \r\n " + JSON.stringify(obj);
			} else {
				log = log + " \r\n " + obj;
			}
		}
	}

	function assert(statement, text) {
		if (!statement) {
			throw new String("ASSERTION FAILED!!!" + text);
		}
	}

	function construct(jsClass) {
		var ctor = classNameToConstructor[jsClass];
		assert(ctor != null, "No constructor for " + jsClass);
		return new (ctor.bind.apply(ctor))();
	}

	function mergeRoot(source, target) {
		mergeObject(source, target);
	}

	function mergeObject(source, target) {
		assert(typeUtil.allowedTypes(source, "object", "null"),
				"mergeObject requires source to be object or null, but source="
						+ $.type(source));
		assert(typeUtil.allowedTypes(target, "object", "null", "undefined"),
				"mergeObject requires target to be object/null/undefined, but target="
						+ $.type(target));
		assert(typeUtil.compatibleType(source, target),
				"mergeObject requires compatible types, but source="
						+ $.type(source) + ", target=" + $.type(target));

		for ( var attKey in source) {
			var newVal = element(attKey, source[attKey], getTargetValue(attKey,
					target));
			if (newVal === REMOVE_TOKEN) {
				delete target[attKey];
			} else if ($.type(newVal) !== 'undefined') {
				target[attKey] = newVal;
			}
		}
	}

	function element(key, val, targetVal) {
		writeLog("element:" + key + " of type " + $.type(val));
		if (val === REMOVE_TOKEN) {
			// attribute got removed
			return REMOVE_TOKEN;
		} else {
			if ($.type(val) === 'null') {
				// set attribute of type object/array to null
				return null;
			} else if ($.type(val) === 'object') {
				return elementObject(key, val, targetVal);
			} else if ($.type(val) === 'array') {
				return elementArray(key, val, targetVal);
			} else {
				// set attribute of type string,number,boolean
				assert(typeUtil
						.allowedTypes(val, "string", "boolean", "number"),
						"val=" + $.type(val));
				return val;
			}
		}
	}

	function elementObject(key, val, targetVal) {
		var newVal;
		if ($.type(targetVal) === 'undefined' || targetVal === null) {
			// old value was undefined/null, thus create new object
			if (val != null && $.type(val.jsClass) !== 'undefined') {
				newVal = construct(val.jsClass);
			} else {
				newVal = {};
			}
		} else {
			newVal = targetVal;
		}
		// deep-copy
		mergeObject(val, newVal);
		return newVal;
	}

	function elementArray(key, val, targetVal) {
		assert($.type(val) === 'array', "val=" + $.type(val));

		if (key.indexOf(REMOVE_TOKEN) > 0) {
			for (var i = 0; i < val.length; i++) {
				var arrayElemenet = val[i];
				var pos = isFound(arrayElemenet, targetVal);
				if (pos !== -1) {
					targetVal.splice(pos, 1);
				}
			}
			return undefined;
		} else {
			var objectCreated = false;
			if ($.type(targetVal) === 'undefined' || targetVal === null) {
				targetVal = [];
				objectCreated = true;
			}
			for (var i = 0; i < val.length; i++) {
				var arrayElemenet = val[i];
				if (isFound(arrayElemenet, targetVal) === -1) {
					targetVal.push(arrayElemenet);
				}
			}
			return objectCreated ? targetVal : undefined;
		}
	}

	function isFound(oneElement, arrayOfElements) {
		assert($.type(arrayOfElements) === 'array', "val="
				+ $.type(arrayOfElements));
		assert($.type(oneElement) !== 'object'
				|| $.type(oneElement.id) !== 'undefined',
				"oneElement is an object but doesn't have attribute 'id'");

		for (var i = 0; i < arrayOfElements.length; i++) {
			var arrayElement = arrayOfElements[i];
			if ($.type(arrayElement) === 'object') {
				assert($.type(arrayElement.id) !== 'undefined',
						"arrayElement is object, but doesn't have an id");
				if (arrayElement.id == oneElement.id) {
					return i;
				}
			} else {
				if (arrayElement == oneElement) {
					return i;
				}
			}
		}
		return -1;
	}

	function getTargetValue(attKey, target) {
		if (attKey.indexOf(REMOVE_TOKEN) > 0) {
			attKey = attKey.substring(0, attKey.indexOf(REMOVE_TOKEN));
		}
		return target[attKey];
	}

	return {
		merge : mergeRoot
	};

});