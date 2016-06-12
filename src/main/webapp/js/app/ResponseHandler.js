define(['jquery', './Field', './Unit', './Constants', './HandItem', './GlobalData'], 
		function ($, Field, Unit, Constants, HandItem, globalData) {

	return {
		
		process: function(jsonObj) {
			
			console.log("GOT FROM SERVER:");
			console.log(jsonObj);
			/* RESP_ALL_STATIC_FIELDS */
			if( typeof jsonObj.allStaticFields !== 'undefined' ) {
				$.each(jsonObj.allStaticFields, function(index, jsonField) {				
					var newfield = new Field(jsonField.pos.x + ":" + jsonField.pos.y, "#3366cc", jsonField.pos, true);					
					globalData.board.addField(newfield);
					if(typeof jsonField.structure !== 'undefined') {
						switch(jsonField.structure.type) {
						case "city":
							globalData.board.addUnit(new Unit("city" + Math.random(), jsonField.structure.side,
									Constants.UNIT_TYPE_CITY, jsonField.pos, false));
							break;
						}
					}
				})
			}
			/* RESP_MYCOLOR */
			if( typeof jsonObj.myColor !== 'undefined' ) {
				globalData.myColor = jsonObj.myColor;
			}
			/* RESP_ALL_HAND */
			if( typeof jsonObj.allHand !== 'undefined' ) {
				$.each(jsonObj.allHand, function(index, jsonUnit) {
					var newHanditem = new HandItem(jsonUnit.unitId, jsonUnit.unitType);					
					globalData.board.addHandItem(newHanditem);
				});
			}
			/* RESP_DO_DEPLOY */
			if( typeof jsonObj.doDeploy !== 'undefined' ) {
				$("#message").html(jsonObj.doDeploy.message);
				globalData.active = {};
				globalData.active.selectHandItem = true;
			}
			/* RESP_SELECT_DEPLOY_TARGET */
			if( typeof jsonObj.selectDeployTarget !== 'undefined') {
				$("#message").html(jsonObj.selectDeployTarget.message);
				globalData.board.idToHanditems[jsonObj.selectDeployTarget.selectedHandItemId].selected = true;
				$.each(jsonObj.selectDeployTarget.validTargetFieldIds, function(index, pos) {
					globalData.board.corToFields[pos.x+":"+pos.y].highlight = true;
				})
				globalData.active = {};
				globalData.active.selectDeployTarget = true;				
			}
			/* RESP_REMOVE_HANDITEM */
			if( typeof jsonObj.removeHanditem !== 'undefined') {
				globalData.board.removeHandItem(jsonObj.removeHanditem);
			}
			/* RESP_ADD_UNIT */
			if( typeof jsonObj.addUnit !== 'undefined') {
				globalData.board.addUnit(new Unit(jsonObj.unitId, jsonObj.addUnit.side,
						jsonObj.addUnit.unitType, jsonObj.addUnit.pos, true));
			}
			/* RESP_ALL_BOARD_UNITS */
			if( typeof jsonObj.allBoardUnits !== 'undefined') {
				$.each(jsonObj.allBoardUnits, function(index, jsonUnit) {
					globalData.board.addUnit(new Unit("u1" + Math.random(), jsonUnit.side,
							jsonUnit.unitType, jsonUnit.pos, true));
				})
			}
			/* RESP_WAIT */
			if( typeof jsonObj.wait !== 'undefined' ) {
				$("#message").html(jsonObj.wait.message);
				globalData.active = {};
			}
			/* RESP_ADD_MESSAGE */
			if( typeof jsonObj.addMessage !== 'undefined' ) {
				$("#message").html($("#message").html() + "<br/>" + jsonObj.addMessage);				
			}
			globalData.board.draw();
			
		}
		
	};	
});
