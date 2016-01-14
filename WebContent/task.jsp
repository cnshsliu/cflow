<%@ page import="org.json.simple.parser.JSONParser, org.json.simple.*, com.sun.jersey.api.client.config.*, com.sun.jersey.api.client.*, java.net.URI, javax.ws.rs.core.*,com.lkh.cflow.CflowHelper" %> 
<%@ include file="includeHeader.jsp"%>
<div class="maindiv_100p"><div class="maindiv_800c tbpadding">
<%String tid = request.getParameter("tid");%>
<%String vtname = request.getParameter("vtname");%>
<div id='task_form'></div>
<div id='toggleForm' class='dotborder'><form>
Try another form <select id='vtselect'>
<option value='default'>Default</option>
<%
JSONParser parser = new JSONParser();
ClientConfig clientconfig = new DefaultClientConfig();
Client client = Client.create(clientconfig);
URI baseURI = UriBuilder.fromUri("http://" + CflowHelper.getHost() + "/cflow").build();
WebResource service = client.resource(baseURI);	
ClientResponse clientresponse = service.path("rest/vt/all").queryParam("token", token).type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
	String tmp1 = clientresponse.getEntity(String.class);
	try{
		JSONArray vts = (JSONArray) parser.parse(tmp1);
		for(int i=0; i<vts.size(); i++){
			JSONObject vt = (JSONObject)vts.get(i);%>
			<option value='<%=vt.get("VTNAME")%>'><%=vt.get("VTNAME") %></option>
			<%
		}
	}catch(Exception ex){
	}
%>
</select><input type=button value="Toggle form now" onClick="toggleForm()"></form>
<B> In your application, there are two ways to specify which form to use for each task:</B>
<dl><dt>At design phase</dt><dd>in workflow designer, give form name in the Form tab of a task's property dialog</dd>
<dt>At programing phase</dt><dd>Explicitely use a form by proving form name in the Restful API to get form content like this: http://MWF_SERVER/cflow/rest/form?token=ACSK&tid=TID&vtname=THE_VTNAME</dd>
</dl>
<B>Remember,</B>
<ol>
<li>If you do not specify form for task, default form will be used.</li>
<li>Specifying to use "ERROR" form in workflow designer, the system ERROR form will be used. 
</ol>
<a href="/cflow/vt.jsp?token=<%=token %>">Go to Form management</a>
</div>
<div id="err"></div>
</div></div>
<%@ include file="includeFooter.jsp"%>
</body>
<script  language="javascript">
	window.addEvent('domready', function(){
		getHTML ('<%=token%>', '<%=tid%>', '<%=vtname%>', 'task_form');
	});
	function getHTML(token, tid, vtname, divID){
		var theUrl ='/cflow/rest/form?token=' + token + '&tid=' + tid + '&vtname=' + vtname; 
		  var req = new Request({
			url: theUrl,
			method: 'get',
			noCache: true,
			onSuccess: function(html){ $(divID).set('html', html); },
			onError: function(text, error){ $('err').set('text', error); }
		  });
		  req.send();
	}
	
	function toggleForm(){
		var sel = $('vtselect').options.selectedIndex;
		var vtname = $('vtselect').options[sel].value;
		if(vtname == "default")
			window.top.location = "/cflow/task.jsp?token=<%=token%>&tid=<%=tid%>";
		else
			window.top.location = "/cflow/task.jsp?token=<%=token%>&tid=<%=tid%>&vtname=" + vtname;
	}
	

</script>
