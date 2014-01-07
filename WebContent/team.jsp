<%@ include file="includeHeader.jsp"%>
<div class="maindiv_100p"><div class="maindiv_800c tbpadding">
	<div class='title'>Team Management</div>
	<div id='result'></div>
	<form id='newTeamForm'>
	<table><tr><td>Team Name:</td><td><input type = "text" id="teamname" ></td></tr>
		<tr><td>Memo:</td><td><input type="text" id="memo"></td></tr>
	<tr><td colspan=2>	<input type="Button" value="Create new team" id='create'></td></tr>
	</table>
	</form>
	<a href='identity.jsp?token=<%=token%>'>Identity Management</a>
	<div id='err'></div>
	Existing teams: 
	<div id='team'  class='dotborder'></div>
</div></div>
<%@ include file="includeFooter.jsp"%>
</body></html>

<script  language="javascript">
	var teams = "";
	var result = $('result');
	window.addEvent('domready', function(){
		getTeams();

		var req_create = new Request({
			url: '/cflow/rest/team',
			onRequest:function(){result.set('text', 'Creating..');},
			onSuccess:function(txt){ result.set('text', "Created: " + txt);  getTeams(); },
			onFailure:function(){result.set('text', 'The request failed.');},
			});

		$('create').addEvent('click', function(event){
			event.stop();
			req_create.send({data:{ teamname: $('teamname').value, memo:$('memo').value, token:"<%=token%>" }});
			});

	});


	function deleteTeam(teamid){
		var req_delete = new Request({
			url: '/cflow/rest/team?teamid=' + teamid + '&token=<%=token%>',
			method: 'delete',
			emulation: false,
			onSuccess:function(){getTeams();},
			onFailure:function(){getTeams();},
			});
		if(confirm('Are you sure?')){
			req_delete.delete();
		}
	}

	var showTeams = function(teams){
		var teamhtml = "";
			teams.each(function(team){
			teamhtml +=  team.NAME + " &nbsp;&nbsp;<a href=\"javascript:deleteTeam('" + team.ID+ "');\">delete</a> "
			+ "&nbsp;&nbsp;<a href='/cflow/teammember.jsp?tid=" + team.ID + "&token=<%=token%>&tname=" + team.NAME + "'>Members</a>"
			+ "<BR>";
			});
		$('team').set("html", teamhtml);
	};


	function getTeams(){
		  var req = new Request.JSON({
		  	url: '/cflow/rest/team/all?token=<%=token%>',
			method: 'get',
			onSuccess: function(json){ showTeams(json); },
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
