requirejs.config({
	paths : {
		jquery : 'webjars/jquery/2.2.4/jquery.min',
		watch : 'lib/watch-1.3.0'
	}
});

define([ 'app/ObjectMerger' ], function(objectMerger) {

	describe('ObjectMerger.merge() tests for root attribute', function() {

		it("one primitive attribute to empty", function() {
			var source = {
				"test" : 42
			};
			var target = {};
			objectMerger.merge(source, target);
			expect(target.test).toEqual(42);
		});
		it("one primitive attribute to an equal one", function() {
			var source = {
					"test" : 42
			};
			var target = {
					"test" : 42
			};
			objectMerger.merge(source, target);
			expect(target.test).toEqual(42);
		});
		it("one primitive attribute to a different one", function() {
			var source = {
					"test" : 42
			};
			var target = {
					"test" : 41
			};
			objectMerger.merge(source, target);
			expect(target.test).toEqual(42);
		});
		it("one primitive attribute to null", function() {
			var source = {
					"test" : 42
			};
			var target = {
					"test" : null
			};
			objectMerger.merge(source, target);
			expect(target.test).toEqual(42);
		});
		it("one null attribute to a not-null one", function() {
			var source = {
					"test" : null
			};
			var target = {
					"test" : 42
			};
			objectMerger.merge(source, target);
			expect(target.test).toBeNull();
		});
		it("remove attribute", function() {
			var source = {
					"test" : "##REMOVED##"
			};
			var target = {
			};
			objectMerger.merge(source, target);
			expect(target.test).toBeUndefined();
		});

		it("one nested attribute to empty", function() {
			var source = {
					"test" : {
						"nestedTest": 42						
					}
			};
			var target = {
			};
			objectMerger.merge(source, target);
			expect(target.test.nestedTest).toEqual(42);
		});
		it("one nested attribute to different nested", function() {
			var source = {
					"test" : {
						"nestedTest": 42						
					}
			};
			var target = {
					"test" : {
						"nestedTest": 43,
						"donttouch": "hello"
					},
					"donttouch": "yippie"
			};
			objectMerger.merge(source, target);
			expect(target.test.nestedTest).toEqual(42);
			expect(target.donttouch).toEqual("yippie");
			expect(target.test.donttouch).toEqual("hello");
		});
		it("empty nested object attribute to override primitive", function() {
			var source = {
					"test" : {
						"nestedTest": {}						
					}
			};
			var target = {
					"test" : {
						"nestedTest": 43						
					}
			};
			
			function callMerge() {
				objectMerger.merge(source, target);
			}			
			expect(callMerge).toThrow();
		});
		it("null nested attribute to different nested", function() {
			var source = {
					"test" : {
						"nestedTest": null						
					}
			};
			var target = {
					"test" : {
						"nestedTest": 43					
					}
			};
			
			objectMerger.merge(source, target);
			expect(target.test.nestedTest).toBeNull();
		});
		it("remove nested attribute", function() {
			var source = {
					"test" : {
						"nestedTest": "##REMOVED##"						
					}
			};
			var target = {
					"test" : {
						"nestedTest": 43					
					}
			};
			
			objectMerger.merge(source, target);
			expect(target.test.nestedTest).toBeUndefined();
		});
		it("remove root attribute object", function() {
			var source = {
					"test" : "##REMOVED##"
			};
			var target = {
					"test" : {
						"nestedTest": 43,
						"anotherOne": "foo"
					}
			};
			
			objectMerger.merge(source, target);
			expect(target.test).toBeUndefined();
		});

	});
});
