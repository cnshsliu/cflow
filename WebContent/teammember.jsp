<%@ include file="includeHeader.jsp"%>
<%String tid = request.getParameter("tid"); %>
<%String tname = request.getParameter("tname"); if(tname==null) tname = tid;%>
<div class="maindiv_100p"><div class="maindiv_800c tbpadding">
<div class='title'><%=tname%> members </div>
<form>
	<div>User: <input type = "text" id="mbrid1" > ROLE: <input type="text" id="role1"></div>
	<div>User: <input type = "text" id="mbrid2" > ROLE: <input type="text" id="role2"></div>
	<div>User: <input type = "text" id="mbrid3" > ROLE: <input type="text" id="role3"></div>
	<div>User: <input type = "text" id="mbrid4" > ROLE: <input type="text" id="role4"></div>
	<input type="Button" value="Add to team" id="create">
</form>

<div id='result'></div>
<div id='members'></div>
<div id='err'></div>
</div></div>
<%@ include file="includeFooter.jsp"%>
</body></html>

<script  language="javascript">
	var teams = "";
	var result = $('result');
	window.addEvent('domready', function(){
		getMbrs();

		var req_create = new Request({
			url: '/cflow/rest/team/members',
			onRequest:function(){result.set('text', 'Creating..');},
			onSuccess:function(txt){ result.set('text', "Success");  getMbrs(); },
			onFailure:function(){result.set('text', 'The request failed.');},
			});

		$('create').addEvent('click', function(event){
			event.stop();
			var mbrstring = "{"
				+ "\"" + $('mbrid1').value + "\":\"" + $('role1').value + "\","
				+ "\"" + $('mbrid2').value + "\":\"" + $('role2').value + "\","
				+ "\"" + $('mbrid3').value + "\":\"" + $('role3').value + "\","
				+ "\"" + $('mbrid4').value + "\":\"" + $('role4').value + "\"}";
			req_create.send({data:{ teamid: "<%=tid%>", memberships:mbrstring, token:"<%=token%>" }});
			});

	});


	function deleteMbr(teamid, mbrid){
		var req_delete = new Request({
			url: "/cflow/rest/team/member?teamid=" + teamid + "&memberid=" + mbrid + "&token=<%=token%>",
			method: "delete",
			emulation: false
			});
			req_delete.delete();

			getMbrs();
	}

	var showMbrs = function(mbrs){
		var mbrsHtml = "<table><thead><tr><th>User</th><th>Role</th><th>Action</th></tr></thead><tbody>";
		mbrs.each(function(mbr){
			mbrsHtml +=  "<tr><td>" + mbr.USRNAME + "(" + mbr.IDENTITY + ")</td><td>" + mbr.ROLE + "</td><td><a href=\"javascript:deleteMbr('<%=tid%>', '" + mbr.IDENTITY+ "');\">delete</a></td></td></tr> ";
		});
		mbrsHtml += "</tbody></table>";
		$('members').set("html", mbrsHtml);
	};


	function getMbrs(){
		  var req = new Request.JSON({
		  	url: '/cflow/rest/team/members?teamid=<%=tid%>&token=<%=token%>',
			method: 'get',
			onSuccess: function(json){ showMbrs(json); },
			onFailure: function(){ $('err').set('text', 'The request failed.'); }
		  });
		  req.send();
	}
</script>
