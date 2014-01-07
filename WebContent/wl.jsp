<%@ include file="includeHeader.jsp"%>
<div class="maindiv_100p"><div class="maindiv_800c tbpadding">
	<div class='title'>User Worklist: </div>
	<div id='wlist' class='dotborder'></div>
	<div id='err'></div>
</div></div>
<%@ include file="includeFooter.jsp"%>
</body>
<%String doer=request.getParameter("doer"); %>
<script  language="javascript">
	var showWorklist = function(witems){
		var wlisthtml = "<table style='display:block; min-width:200px;'><thead><tr><th>Process</th><th>Task</th></tr></thead><tbody>";
		witems.each(function(witem){
			wlisthtml += "<tr><td>" + witem.PRCNAME + "</td><td>" + "<a href='task.jsp?tid=" + witem.TID + "&token=<%=token%>'>" + witem.NAME + "</a></td></tr>";

		});
		wlisthtml += "</tbody></table>";
		wlisthtml += "You have " + witems.length + " tasks to be done.";
		$('wlist').set("html", wlisthtml);
	};


	function getWorklist(){
		  var req = new Request.JSON({
		  url: '/cflow/rest/worklist?token=<%=token%>&doer=<%=doer%>',
				  cache: false,
			method: 'get',
			onSuccess: function(json){ showWorklist(json); },
			onFailure: function(){ 
				if(this.status == 401) 
					$('err').set('html', "Session expired: <a href='/cflow/login.jsp'>Relogin</a>");
				else if(this.status == 500)
					$('err').set('html', this.text);
				}
		  });
		  req.send();
	}
	
	getWorklist();
</script>
