<%@ page import="org.json.simple.parser.JSONParser, org.json.simple.*, com.sun.jersey.api.client.config.*, com.sun.jersey.api.client.*, java.net.URI, javax.ws.rs.core.*,com.lkh.cflow.CflowHelper" %> 
<%
String wftId = request.getParameter("wftid");
String mode="workflow";
%>
<%@ include file="includeHeader.jsp"%>
<div class="maindiv_100p"><div class="maindiv_800c tbpadding">
<div class='title'>Start Workflow</div>
<div id='result'></div>
<form>
<table>
	<tr><td>Workflow template id: </td><td><%=wftId%><input type='hidden' id='wftId' value='<%=wftId %>'>
			<a href="/cflow/wft_view.jsp?wftid=<%=wftId%>&token=<%=token%>">View</a> |
			<a href="/cflow/wft_edit.jsp?wftid=<%=wftId%>&token=<%=token%>">Edit</a>
	</td></tr>
	<tr><td> InstanceName: </td><td> <input type="text" id="instancename"></td></tr>
	<tr><td>Choose a working team:</td><td>
<select id='teamid'>
<%
JSONParser parser = new JSONParser();
ClientConfig clientconfig = new DefaultClientConfig();
Client client = Client.create(clientconfig);
URI baseURI = UriBuilder.fromUri("http://" + CflowHelper.getHost() + "/cflow").build();
WebResource service = client.resource(baseURI);	
ClientResponse clientresponse = service.path("rest/team/all").queryParam("token", token).type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
	String tmp1 = clientresponse.getEntity(String.class);
	JSONArray existing_teams;
	existing_teams = (JSONArray) parser.parse(tmp1);
	for(int i=0; i<existing_teams.size(); i++){
		JSONObject et = (JSONObject)existing_teams.get(i);%>
		<option value='<%=et.get("ID")%>'><%=et.get("NAME") %></option>
		<%
	}
%>
</select>
</td></tr>
<tr><td>
Assign process starter to:
</td><td>
<select id='startby'>
<%clientresponse = service.path("rest/user/actors").queryParam("token", token).type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
	tmp1 = clientresponse.getEntity(String.class);
	JSONArray friends = (JSONArray) parser.parse(tmp1);
	for(int i=0; i<friends.size(); i++){
		JSONObject et = (JSONObject)friends.get(i);%>
		<option value='<%=et.get("IDENTITY")%>'><%=et.get("NAME") %></option>
		<%
	}
%>
</select><input type=button value='Get WL' onClick='javascript:gotoWorklist()'>
</td></tr>
<tr><td colspan=2>
<input type='Button' value='Start a new workflow process' id='create'>
</td></tr></table>
</form>
</div></div>
<%@ include file="includeFooter.jsp"%>
</body></html>


<script  language="javascript">
	var result = $('result');
	window.addEvent('domready', function(){
		var req_start = new Request({
			url: '/cflow/rest/process',
			onRequest:function(){result.set('text', 'Creating..');},
			onSuccess:function(txt){ result.set('text', 'Process started: ' + txt);  },
			onFailure:function(){result.set('text', 'The request failed.');},
			method: 'post',
			emulation: false
			});

		$('create').addEvent('click', function(event){
			event.stop();
			if($('instancename').value==''){
				result.set('text', 'ERROR: Please give instance name.');
			}else{
				req_start.send({data:{ wftid: $('wftId').value, startby:$('startby').value, teamid:$('teamid').value, instancename:$('instancename').value, token:"<%=token%>" }});
			}
		});
	});
	
	function gotoWorklist(){
		document.location = "/cflow/wl.jsp?token=<%=token%>&doer=" + $('startby').value;
	}
	
</script>
