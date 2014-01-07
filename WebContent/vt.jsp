<%@ include file="includeHeader.jsp"%>
<div class="maindiv_100p"><div class="maindiv_800c tbpadding">
	<div class='title'>Task Form Template Management</div>
	<form action="/cflow/VTFileUploader"  method="post" enctype="multipart/form-data">
		<table><tr><td colspan=2><b>Upload Task Form File</b></td></tr>
		<tr><td>Name:</td><td><input type="text" name="vtname" ></td></tr>
		<tr><td>File:</td><td><input type="file" name=fileforload size=30><input type="Submit" value="Upload form template"></td></tr></table>
		<input type="hidden" name="token" value="<%=token%>">
	</form>
	<div id='err'></div>
	Existing form templates
	<div id='vts' class='dotborder'></div>
	<%String tmp = (String)request.getAttribute("upload.message");
	if(tmp == null) tmp = ""; %>
	<div id='result'><%=tmp %></div>
	HELLO
</div></div>
<%@ include file="includeFooter.jsp"%>
</body>

<script  language="javascript">
	window.addEvent('domready', function(){
		getVts();
	});

	var showVts = function(vts){
		var vthtml = "<table><thead style='background-color: #9FC;'><tr><th>Form Name</th><th>Delete</th></tr></thead><tbody>";
		vts.each(function(vt){
			vthtml += "<tr><td><a href='javascript:showVt(\"" + vt.VTNAME + "\")'>" + vt.VTNAME + "</a>" 
				+ "</td><td><a href='javascript:deleteVt(\"" + vt.VTNAME + "\")'>Delete</a>"
				+ "</tr>";

		});
		vthtml += "</tbody></table>";
		vthtml += "You have " + vts.length + " Forms";
		$('vts').set("html", vthtml);
	};

	function deleteVt(vtname){
		 $('err').set('text', '');
		  var req = new Request.JSON({
		  	url: '/cflow/rest/vt?vtname=' + vtname + '&token=<%=token%>',
			method: 'delete',
			onSuccess: function(json, text){ getVts(); },
			onFailure: function(text, error){  getVts(); },
			emulation: false
		  });
		  if(confirm("Are you sure?"))
		  	req.send();
	}


	function getVts(){
		$('err').set('text', '');
		  var req = new Request.JSON({
		  	url: '/cflow/rest/vt/all?token=<%=token%>',
			method: 'get',
			noCache: true,
			onSuccess: function(json, text){ showVts(json); },
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
