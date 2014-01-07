<%@ include file="includeHeader.jsp"%>
<div class="maindiv_100p"><div class="maindiv_800c tbpadding">
	<div class='title'>Team Management</div>
	<div id='result'></div>
	<form action="/cflow/IdentityUploader"  method="post" enctype="multipart/form-data">
		<table><tr><td colspan=2><b>Upload Identify CSV File</b></td></tr>
		<tr><td>File:</td><td><input type="file" name=fileforload size=30></td></tr>
		<tr><td colspan=2><input type="Submit" value="Upload Identity CSV"></td></tr></table>
		<input type="hidden" name="token" value="<%=token%>">
	</form>
	<div id='err'></div>
	Existing Identities: 
	<div id='identities'  class='dotborder'></div>
</div></div>
<%@ include file="includeFooter.jsp"%>
</body></html>

<script  language="javascript">
	var teams = "";
	var result = $('result');
	window.addEvent('domready', function(){
		getIdentities();
	});


	function deleteIdentity(identity){
		var req_delete = new Request({
			url: '/cflow/rest/user?identity=' + identity + '&token=<%=token%>',
			method: 'delete',
			emulation: false,
			onSuccess:function(){getIdentities();},
			onFailure:function(){getIdentities();},
			});
		if(confirm('Are you sure?')){
			req_delete.delete();
		}
	}

	var showIdentities = function(idts){
		var html = "";
			idts.each(function(idt){
			html += idt.IDENTITY + "&nbsp;&nbsp;" + idt.NAME + " &nbsp;&nbsp;<a href=\"javascript:deleteIdentity('" + idt.IDENTITY+ "');\">delete</a> "
			+ "<BR>";
			});
		$('identities').set("html", html);
	};


	function getIdentities(){
		  var req = new Request.JSON({
		  	url: '/cflow/rest/user/all?token=<%=token%>',
			method: 'get',
			onSuccess: function(json){ showIdentities(json); },
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
