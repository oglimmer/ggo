define([ 'jquery'], function($) {

	function allowedTypes(obj /*, allowedType1, allowedType2, allowedType3, allowedType4...*/) {
		var args = Array.prototype.slice.call(arguments);
		args.splice(0, 1);
		var found = false;
		$.each(args, function(index, type) {
			if ($.type(obj) === type) {
				found = true;
			}
		});
		return found;
	}

	function compatibleType(source, target) {
		if ($.type(source) === 'null' || $.type(target) === 'null') {
			return true;
		}
		if ($.type(target) === 'undefined') {
			return true;
		}
		if ($.type(source) === $.type(target)) {
			return true;
		}
		return false;
	}

	return {
		allowedTypes: allowedTypes,
		compatibleType: compatibleType
	};

});