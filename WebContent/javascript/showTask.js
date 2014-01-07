function ChangeRoleParticipate(ser){
	document.getElementById("CHOOSER_" + ser).innerHTML = "<select name='mates' multiple size=5><option value='mat1'>Mat1</option><option value='mat2'>Mat2</option></select>";
	
}
function trim(str) {
        return str.replace(/^\s+|\s+$/g,"");
}
function isDigit(str){
	 if(str.match(/^[-+]?\d+$/)){
	 	return true;
	}else{
		return false;
	}
}
function isFloat(str){
	 if(str.match(/^[-+]?[\d\.]+$/)){
	 	return true;
	}else{
		return false;
	}
}
function isUrl(str){
	if(str.match(/^(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?$/)){
	 	return true;
	}else{
		return false;
	}
}

window.addEvent('domready', function() {
	
	if($('hideControl') != null){
		window.location = $('hideControl').value;
	}

	var log = $('log');
	var highlight = new Fx.Morph(log, {
		duration: 3000,
		link: 'cancel',
		transition: 'quad:out'
	});
	 

	$('myform').addEvent('burn', function(text){
			log.set('html', text);
			highlight.start({
				backgroundColor: ['#fff36f', '#fff'],
				opacity: [1, 0]
			});
	});
	$('myform').addEvent('submit', function(e){
		e.stop();
		/*
		 * <%for (int i = 0; i < attachments.length; i++) {
					if (attachments[i].getType().equals("netext")) {%>
					if(trim($('attvalue_<%=i%>').value).length<0){
						$('myform').fireEvent('burn', '<%=attachments[i].getLabel()%> should not be empty.');
						return;
					}
				<%} else if (attachments[i].getType().equals("text")) {%>
					if(trim($('attvalue_<%=i%>').value) == ''){
						$('myform').fireEvent('burn', '<%=attachments[i].getLabel()%> should not be empty.');
						return;
					}
				<%} else if (attachments[i].getType().equals("int")) {%>
					if(trim($('attvalue_<%=i%>').value) == ''){
						$('myform').fireEvent('burn', '<%=attachments[i].getLabel()%> should not be empty.');
						return;
					}
					if(isDigit($('attvalue_<%=i%>').value) == false){
						$('myform').fireEvent('burn', '<%=attachments[i].getLabel()%> should be an integer.');
						return;
					}
				<%} else if (attachments[i].getType().equals("float")) {%>
					if(trim($('attvalue_<%=i%>').value).length<0){
						$('myform').fireEvent('burn', '<%=attachments[i].getLabel()%> should not be empty.');
						return;
					}
					if(isFloat($('attvalue_<%=i%>').value) == false){
						$('myform').fireEvent('burn', '<%=attachments[i].getLabel()%> should be a number.');
						return;
					}
				<%} else if (attachments[i].getType().equals("url")) {%>
					if(trim($('attvalue_<%=i%>').value).length<0){
						$('myform').fireEvent('burn', '<%=attachments[i].getLabel()%> should not be empty.');
						return;
					}
					if(isUrl($('attvalue_<%=i%>').value) == false){
						$('myform').fireEvent('burn', '<%=attachments[i].getLabel()%> should be in url format.');
						return;
					}
				<%}%>
			<%}%>
			*/
		$('myform').submit();
	});

});

function startDelegate(){
	$('delegateflag').value = "1";
	$('myform').submit();
}

function submitForm(theForm){
	var options = document.getElementsByName("option_1");
	if(options == null) return;
	var num = options.length;
	var submitted = false;
	if(num == 0){
		submitted = true;
		theForm.submit();
	}else{
		for(var i=0; i<num; i++)
		{
			if(options[i].checked){
				submitted = true;
				theForm.submit();
			}
		}
		if(submitted == false )
		alert("Please select one option.");
	}
}


