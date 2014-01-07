function echeck(str) {

	var at="@"
	var dot="."
	var lat=str.indexOf(at)
	var lstr=str.length
	var ldot=str.indexOf(dot)
	if (str.indexOf(at)==-1){
	   return false
	}

	if (str.indexOf(at)==-1 || str.indexOf(at)==0 || str.indexOf(at)==lstr){
	   return false
	}

	if (str.indexOf(dot)==-1 || str.indexOf(dot)==0 || str.indexOf(dot)==lstr){
	    return false
	}

	 if (str.indexOf(at,(lat+1))!=-1){
	    return false
	 }

	 if (str.substring(lat-1,lat)==dot || str.substring(lat+1,lat+2)==dot){
	    return false
	 }

	 if (str.indexOf(dot,(lat+2))==-1){
	    return false
	 }
	
	 if (str.indexOf(" ")!=-1){
	    return false
	 }

	 return true					
}

function trim(str) {
        return str.replace(/^\s+|\s+$/g,"");
}

function isUsrId(str){
	 //if(str.match(/^[a-zA-Z]([a-zA-Z0-9_-]{3,15})$/) || echeck(str)){
	 if(echeck(str)){
		if(str.length <4 || str.length > 24)
			return false;
		else
			return true;
	}else{
		return false;
	}
}


