function openUrl(theUrl){
	window.location.replace(theUrl);
}
function selectMenu(id){
	//var menuId = "cflowmi_" + id;
	//$(menuId).className = "tabHeader_selected";
}

function openInWorkarea(theUrl, title){
	var theFrame = $("workArea");
	var theTitle = $("workAreaTitle");
	if(theFrame != null)
		theFrame.src = theUrl;
	if(title != null)
		theTitle.set('html', title);

	var div = $('st_right');
	if(div != null)
	{
		var wsize = window.getSize();
		var dsize = window.getSize();
		var ssize = window.getScroll();
		div.setStyle('top', (wsize.y - 530)/2 + ssize.y);
		div.setStyle('left', (wsize.x - 810)/2);
		div.setStyle('width', '830px');
		div.setStyle('height', '530');
		div.setStyle('border-style', 'solid');
		div.setStyle('border-width', '4px');
		div.setStyle('border-color', '#3339FF');
		div.setStyle('padding', '0px');
		div.setStyle('display', 'inline');
		
	}

}

function closeWorkarea(){
	var div = document.getElementById("st_right");
	if(div != null)
		div.style.display="none";
}

function maximizeWorkarea(){
	var div = $("st_right");
	if(div != null){
		var size = window.getScrollSize();
		div.setStyle('top', 0);
		div.setStyle('left', 0);
		div.setStyle('width', size.x);
		div.setStyle('height', size.y);
		window.scrollTo(0,0);
		var toggleDiv = document.getElementById("st_toggle");
		if(toggleDiv != null)
			toggleDiv.innerHTML = "<a href='javascript:restoreWorkarea();'><img src='/cflow/images/restore.jpg' width='24' height='24' border='0'></a>";
	}
	
}

function restoreWorkarea(){
	var div = document.getElementById("st_right");
	if(div != null){
		var wsize = window.getSize();
		var dsize = window.getSize();
		div.setStyle('top', (wsize.y - 550)/2);
		div.setStyle('left', (wsize.x - 850)/2);
		div.setStyle('width', '850px');
		div.setStyle('height', '550px');

		var toggleDiv = document.getElementById("st_toggle");
		if(toggleDiv != null)
			toggleDiv.innerHTML = "<a href='javascript:maximizeWorkarea();'><img src='/cflow/images/maximize.jpg' width='24' height='24' border='0'></a>";
	}
	
}

function toggleToPlayground(){
	$('st_left').setStyle('display', 'none');
}
function toggleBottomIFrame(){
}

function getWindowSize() {
	  var myWidth = 0, myHeight = 0;
	  if( typeof( window.innerWidth ) == 'number' ) {
	    //Non-IE
	    myWidth = window.innerWidth;
	    myHeight = window.innerHeight;
	  } else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
	    //IE 6+ in 'standards compliant mode'
	    myWidth = document.documentElement.clientWidth;
	    myHeight = document.documentElement.clientHeight;
	  } else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
	    //IE 4 compatible
	    myWidth = document.body.clientWidth;
	    myHeight = document.body.clientHeight;
	  }
	  return {x: myWidth, y: myHeight};
	}
