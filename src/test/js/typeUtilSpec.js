requirejs.config({
	paths : {
		jquery : 'webjars/jquery/2.2.4/jquery.min',
		watch : 'lib/watch-1.3.0'
	}
});

define([ 'app/TypeUtil' ], function(typeUtil) {

	describe('TypeUtil.allowedTypes() tests', function() {

		it("allowedTypes for string", function() {
			expect(typeUtil.allowedTypes("a-string", "string")).toBe(true);
		});
		it("allowedTypes for number", function() {
			expect(typeUtil.allowedTypes(5, "number")).toBe(true);
		});
		it("allowedTypes for boolean", function() {
			expect(typeUtil.allowedTypes(true, "boolean")).toBe(true);
		});
		it("allowedTypes for object", function() {
			expect(typeUtil.allowedTypes({ "test": 42 }, "object")).toBe(true);
		});
		it("allowedTypes for array", function() {
			expect(typeUtil.allowedTypes(["a", "b"], "array")).toBe(true);
		});
		it("allowedTypes for null", function() {
			expect(typeUtil.allowedTypes(null, "null")).toBe(true);
		});
		it("allowedTypes for undefined", function() {
			expect(typeUtil.allowedTypes(undefined, "undefined")).toBe(true);
		});
		it("allowedTypes for 3rd parameter", function() {
			expect(typeUtil.allowedTypes("a-string", "null", "boolean", "string")).toBe(true);
		});
		it("allowedTypes for negative string", function() {
			expect(typeUtil.allowedTypes("a-string", "number")).toBe(false);
		});
		it("allowedTypes for negative number", function() {
			expect(typeUtil.allowedTypes(5, "string")).toBe(false);
		});
		it("allowedTypes for negative undefined", function() {
			expect(typeUtil.allowedTypes(5, "undefined")).toBe(false);
		});

	});
	describe('TypeUtil.compatibleType() tests', function() {
		
		it("compatibleType for string-string", function() {
			expect(typeUtil.compatibleType("a-string", "b-string")).toBe(true);
		});
		it("compatibleType for boolean-boolean", function() {
			expect(typeUtil.compatibleType(false, true)).toBe(true);
		});
		it("compatibleType for number-number", function() {
			expect(typeUtil.compatibleType(3, 4)).toBe(true);
		});
		it("compatibleType for null-string", function() {
			expect(typeUtil.compatibleType(null, "b-string")).toBe(true);
		});
		it("compatibleType for number-null", function() {
			expect(typeUtil.compatibleType(42, null)).toBe(true);
		});
		it("compatibleType for number-undefined", function() {
			expect(typeUtil.compatibleType(42, undefined)).toBe(true);
		});
		it("compatibleType for null-null", function() {
			expect(typeUtil.compatibleType(null, null)).toBe(true);
		});
		it("compatibleType for object-object", function() {
			expect(typeUtil.compatibleType({ "test": 42 }, { "test": 43 })).toBe(true);
		});
		it("compatibleType for array-array", function() {
			expect(typeUtil.compatibleType([1,2,3], ["a", "b"])).toBe(true);
		});
		it("compatibleType for array-object", function() {
			expect(typeUtil.compatibleType([1,2,3], { "test": 42 })).toBe(false);
		});
		it("compatibleType for object-array", function() {
			expect(typeUtil.compatibleType({ "test": 42 }, [1,2,3])).toBe(false);
		});
		it("compatibleType for object-string", function() {
			expect(typeUtil.compatibleType({ "test": 42 }, "test")).toBe(false);
		});
		it("compatibleType for number-array", function() {
			expect(typeUtil.compatibleType(23, [23])).toBe(false);
		});
		
		
	});
});
