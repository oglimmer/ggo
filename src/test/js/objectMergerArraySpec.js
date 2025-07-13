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
				"test" : [ 1, 2, 3, 4 ]
			};
			var target = {};
			objectMerger.merge(source, target);
			expect(target).toEqual({
				"test" : [ 1, 2, 3, 4 ]
			});
		});
		it("one primitive attribute to existing", function() {
			var source = {
				"test" : [ 1, 2, 3, 4, 5 ]
			};
			var target = {
				"test" : [ 1, 2, 3, 4 ]
			};
			objectMerger.merge(source, target);
			expect(target).toEqual({
				"test" : [ 1, 2, 3, 4, 5 ]
			});
		});
		it("one primitive attribute to null", function() {
			var source = {
				"test" : [ 1, 2, 3, 4 ]
			};
			var target = {
				"test" : null
			};
			objectMerger.merge(source, target);
			expect(target).toEqual({
				"test" : [ 1, 2, 3, 4 ]
			});
		});
		it("remove one primitive attribute to existing", function() {
			var source = {
				"test##REMOVED##" : [ 4 ]
			};
			var target = {
				"test" : [ 1, 2, 3, 4 ]
			};
			objectMerger.merge(source, target);
			expect(target).toEqual({
				"test" : [ 1, 2, 3 ]
			});
		});
		it("remove one object attribute to existing", function() {
			var source = {
				"modalDialogState" : {
					"options##REMOVED##" : [ {
						"id" : 1,
						"desc" : "what-not-1"
					} ],
					"options" : [ {
						"id" : 4,
						"desc" : "what-not-3"
					} ]
				}
			};
			var target = {
				"modalDialogState" : {
					"options" : [ {
						"id" : 1,
						"desc" : "what-not-1"
					}, {
						"id" : 2,
						"desc" : "what-not-2"
					} ]
				}
			};
			objectMerger.merge(source, target);
			expect(target).toEqual({
				"modalDialogState" : {
					"options" : [ {
						"id" : 2,
						"desc" : "what-not-2"
					}, {
						"id" : 4,
						"desc" : "what-not-3"
					} ]
				}
			});
		});
	});
});
