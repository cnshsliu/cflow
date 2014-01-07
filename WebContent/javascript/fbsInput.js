var fbs_JSON = null;
function fbs_initData(var dataString){
	fbs_JSON = JSON.decode(dataString);
}

function _makeAcOptionsList(inputField){
	var added = 0;
	var listHTML="<table width='100%' cellpadding=0 cellspacing=0 style='font-size:10px;'>";
	for(var i=0; i<fbs_JSON.length; i++){
		var friendName = fbs_JSON[i].NAME;
		var friendId = fbs_JSON[i].ID;
		if(friendName.toLowerCase().indexOf(inputField.value.toLowerCase())==0){
		listHTML += "<tr id='li_option_" + added + "'><td>" + friendName
				+"<input type='hidden' id='li_name_" + added + "' name='li_name_" + added + "' value='" + friendName + "'>"
				+"<input type='hidden' id='li_id_" + added + "' name='li_id_" + added + "' value='" + friendId + "'>"
				+"</td></tr>";
			added ++;
		}
	}
	listHTML += "</table>";
	$('acOptions').innerHTML = listHTML;
	optionCount = added;
}

function _removeUserTag(ser, _tagSer, liName, liId){
	var userTagId = "userTag_" + _tagSer;
	var theSpan = $(userTagId);
	theSpan.parentNode.removeChild(theSpan);

	var namesId = "tr_" + ser + "_names";
	var allNames = $(namesId).value.split(",");
	var found = false;
	var newNames = "";
	for(var i=0; i<allNames.length; i++){
		if(allNames[i] != liId){
			if(newNames == "") newNames = allNames[i];
			else newNames += "," + allNames[i];
		}
	}
	$(namesId).value = newNames;
}

function _addUserTag(ser, liId){
	var namesId = "tr_" + ser + "_names";
	if($(namesId).value == "")
		$(namesId).value = liId;
	else
		$(namesId).value += "," + liId;
}

function _userTagAlreadyAdded(ser, liId){
	var namesId = "tr_" + ser + "_names";
	var allNames = $(namesId).value.split(",");
	var found = false;
	for(var i=0; i<allNames.length; i++){
		if(allNames[i] == liId){
			found = true;
			break;
		}
	}
	return found;
}

function _moveToHigherOption(){
	if(fbs_currentIdx >= 0){
		try{ $('li_option_' + fbs_currentIdx).setStyle("background-color", "#FFFFFF"); }catch(error){}
	}
	fbs_currentIdx --;	
	if(fbs_currentIdx < 0){
		fbs_currentIdx = optionCount-1;
	}
	if(fbs_currentIdx >=0)
		try{ $('li_option_' + fbs_currentIdx).setStyle("background-color", "#6666FF"); }catch(error){}
}
function _moveToLowerOption(){
	if(fbs_currentIdx >= 0){
		try{ $('li_option_' + fbs_currentIdx).setStyle("background-color", "#FFFFFF"); }catch(error){}
	}
	fbs_currentIdx ++;
	if(fbs_currentIdx > optionCount-1){
		fbs_currentIdx = 0;
	}
	if(fbs_currentIdx >=0)
		try{ $('li_option_' + fbs_currentIdx).setStyle("background-color", "#6666FF"); }catch(error){}
}


function _confirmComplete(ser, idx){
	var liName = $("li_name_" + idx).value;
	var liId = $("li_id_" + idx).value;
	if(_userTagAlreadyAdded(ser, liId)){
		return;
	}
	var namePickerId = "tr_" + ser + "_namePicker";
	var userTagHtml = "<table cellpadding=0 cellspacing=0 border=0 class='userTag'><tr><td><img src='/cflow/images/tagLeft.png' height='14'></td><td nowrap bgcolor='#BE9BFF'>" + liName + "</td><td><a href=\"javascript:void(0);\" onClick=\"_removeUserTag(" + ser + "," + tagSer + ",'" + liName + "', '" + liId + "')\"><img src='/cflow/images/tagRight.png' border=0 height=14></a></td></tr></table>";
	var userTagId = "userTag_" + tagSer;
	var aOption = new Element('span', {'html':userTagHtml, 'class':'userTag', 'id':userTagId}).inject($(namePickerId), 'before');
	_addUserTag(ser, liId);
	tagSer ++;

	$(namePickerId).value = "";
	$('acOptions').innerHTML = "";
	$(namePickerId).focus();
	fbs_currentIdx = -1;
	var helloCoordinate = $(namePickerId).getCoordinates();
	$('acOptions').setStyle("left", helloCoordinate.left);
	$('acOptions').setStyle("top", helloCoordinate.bottom);
	$('acOptions').setStyle("width", helloCoordinate.width);
	$('acOptions').setStyle("minWidth", 100);
}

function PickerFocused(inputField){
	if(inputField.value=="Doers"){
		inputField.value="";
	}
}
function PickerKeyUp(event, inputField, ser){
	if(inputField.value.length<1){
		$('acOptions').setStyle("display", "none");
	   	return;
	}else{
		if($('acOptions').getStyle("display") == "none")
		{
			$('acOptions').setStyle("display", "block");
			var namePickerId = "tr_" + ser + "_namePicker";
			var helloCoordinate = $(namePickerId).getCoordinates();
			$('acOptions').setStyle("left", helloCoordinate.left);
			$('acOptions').setStyle("top", helloCoordinate.bottom);
			$('acOptions').setStyle("width", helloCoordinate.width);
			$('acOptions').setStyle("minWidth", 100);
		}
		var keyCode = event.keyCode?event.keyCode:event.charCode;
		if(keyCode == 13){
			if(optionCount > 0){
				if(fbs_currentIdx<0 || fbs_currentIdx >= optionCount)
					_confirmComplete(ser, 0);
				else
					_confirmComplete(ser, fbs_currentIdx);
			}
		}else if(keyCode == 37 || keyCode == 38){
			_moveToHigherOption();
		}else if(keyCode == 39 || keyCode == 40){
			_moveToLowerOption();
		}else{
			_makeAcOptionsList(inputField);
		}
	}
}
function PickerBlur(inputField){
		if(inputField.value==''){inputField.value='Doers';}
		$('acOptions').setStyle("display", "none");
}
