<%@ include file="includeHeader.jsp"%>
<div class="maindiv_100p"><div class="maindiv_800c tbpadding">
	<div class='title'>Workflow Templates</div>
	<div id='err'></div>
	<div id='new'><a href='/cflow/wft_new.jsp?token=<%=token%>'>Create a new workflow template</a> | 
	<a href="/cflow/example.jsp?token=<%=token%>">Get example templates</a></div>
	<div id='wfts' class='dotborder'></div>
</div></div>
<%@ include file="includeFooter.jsp"%>
</body>

<script  language="javascript">
	var teams = "";
	window.addEvent('domready', function(){
		getWfts();
	});
	var showWfts = function(wfts){
		var wfthtml = "<table><thead><tr><th>Template</th><th>View</th><th>Edit</th><th>Delete</th><th>TEST</th></tr></thead><tbody>";
		wfts.each(function(wft){
			wfthtml += "<tr><td>" + wft.WFTNAME 
				+ "</td><td><a href='/cflow/wft_view.jsp?wftid=" + wft.WFTID + "&token=<%=token%>'>View</a>"
				+ "</td><td><a href='/cflow/wft_edit.jsp?wftid=" + wft.WFTID + "&token=<%=token%>'>Edit</a>"
				+ "</td><td><a href='javascript:deleteWft(\"" + wft.WFTID + "\")'>Delete</a>"
				+ "</td><td><a href='/cflow/wft_start.jsp?wftid=" + wft.WFTID + "&token=<%=token%>'>Test it</a>"
				+ "</tr>";

		});
		wfthtml += "</tbody></table>";
		wfthtml += "You have " + wfts.length + " workflow templates";
		$('wfts').set("html", wfthtml);
	};

	var showTeams = function(teams){
		var teamhtml = "<select name='teamid' id='teamid'>";
			teams.each(function(team){
				teamhtml += "<option value='" + team.ID + "'>" + team.NAME + "</option>";
			});
			teamhtml += "</select>";
		$('team').set("html", teamhtml);
	};

	function deleteWft(wftId){
		 $('err').set('text', '');
		  var req = new Request.JSON({
		  	url: '/cflow/rest/wft?wftid=' + wftId + '&token=<%=token%>',
			method: 'delete',
			onSuccess: function(json, text){ getWfts(); },
			onFailure: function(text, error){  getWfts(); },
			emulation: false
		  });
		  if(confirm("Are you sure?"))
		  	req.send();
	}


	function getWfts(){
	$('err').set('text', '');
		  var req = new Request.JSON({
		  	url: '/cflow/rest/wft/all?token=<%=token%>',
			method: 'get',
			noCache: true,
			onSuccess: function(json, text){ showWfts(json); },
			onFailure: function(){ 
				if(this.status == 401) 
					$('err').set('html', "Session expired: <a href='/cflow/login.jsp'>Relogin</a>");
				else if(this.status == 500)
					$('err').set('html', this.text);
				}
		  });
		  req.send();
	}
</script>
