var wlist_url = "";
var witem_url = "";
var wlist_intervalID;
var witem_intervalID;
function processWorkitem(url) {
	witem_url = url;
	loadWorkitem();
	witem_intervalID = window.setInterval("loadWorkitem()", 5000);
}


function loadWorkitem(){
   try{
		  /* if (window.XMLHttpRequest) { // Non-IE browsers
			   reqitem = new XMLHttpRequest();
			   reqitem.onreadystatechange = targetWorkitemDiv;
	    	  reqitem.open("GET", witem_url, true);
	    	  reqitem.send(null);
	    } else if (window.ActiveXObject) { // IE
	    	reqitem = new ActiveXObject("Microsoft.XMLHTTP");
	    	if (reqitem) {
	    	  reqitem.onreadystatechange = targetWorkitemDiv;
	    	  reqitem.open("GET", witem_url, true);
	    	  reqitem.send();
	
	    	}*/
	   
	   var request = new Request.JSON({
			url: '/cflow/RemoteInterface?action=listProcesses&usrid=<%=usrid%>&status=RUNNING',
			onComplete: function(jsonObj) {
				addProcess(jsonObj);
			}
		}).send();
	   
    }
   }catch(e){
	 //alert(e);
	   try{clearInterval(witem_intervalID);} catch(ee){}
   }
}
function targetWorkitemDiv() {
	try{
	    if (reqitem.readyState == 4) { // Complete
	          if (reqitem.status == 200) { // OK response
	              try{document.getElementById("WORK_BUFFER").innerHTML = reqitem.responseText;}catch(ee1){}
	              try{document.getElementById("worklist_abNoCR").innerHTML = document.getElementById("worklist_inner").innerHTML;}catch(ee2){}
	              try{document.getElementById("workitem_abNoCR").innerHTML = document.getElementById("workitem_inner").innerHTML;}catch(ee3){}
	          } else {
	            //alert("Problem: " + reqitem.statusText);
	        	  try{clearInterval(witem_intervalID);} catch(ee){}
	          }
	    }
	}catch(e){
		//try{clearInterval(witem_intervalID);} catch(ee){}
	}
} 
function updateWorklistContent(){
	try{document.getElementById("worklist_abNoCR").innerHTML = document.getElementById("worklist_inner").innerHTML;}catch(ee2){}
}
