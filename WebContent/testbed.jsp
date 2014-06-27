<%@ page import="org.json.simple.parser.JSONParser, org.json.simple.*, com.sun.jersey.api.client.config.*, com.sun.jersey.api.client.*, java.net.URI, javax.ws.rs.core.*, com.lkh.cflow.CflowHelper" %> 
<%
String wftId = request.getParameter("wftid");
String mode="workflow";
%>
<%@ include file="includeHeader.jsp"%>
<div class='title'>Testbed (Start workflow and check worklist)</div>
<div id='result'></div>
<div id='wfts' >
<form>
<table>
<tr><td><BR><a href='/cflow/wft.jsp?token=<%=token %>'>Try to start a workflow</a><BR><BR><BR></td></tr>
<tr><td>
Select user:
<select id='doer'>
<%
try{
	JSONArray friends = new JSONArray();
	JSONParser parser = new JSONParser();
	ClientConfig clientconfig = new DefaultClientConfig();
	Client client = Client.create(clientconfig);
	URI baseURI = UriBuilder.fromUri("http://" + CflowHelper.getHost() + "/cflow").build();
	WebResource service = client.resource(baseURI);	
	ClientResponse clientresponse = service.path("rest/user/actors").queryParam("token", token).type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
	String tmp1 = clientresponse.getEntity(String.class);
	friends = (JSONArray) parser.parse(tmp1);
	for(int i=0; i<friends.size(); i++){
		JSONObject et = (JSONObject)friends.get(i);%>
		<option value='<%=et.get("IDENTITY")%>'><%=et.get("NAME") %></option>
		<%
	}
}catch(Exception ex){
	ex.printStackTrace();
}

%>
</select>
</td></tr>
<tr><td>
<input type='Button' value='Check user worklist' onClick='javascript:gotowl()'>
</td></tr></table>
</form>
</div>
<%@ include file="includeFooter.jsp"%>
</body></html>


<script  language="javascript">
	function gotowl(){
		var identity = $('doer').options[$('doer').selectedIndex].value;
		window.top.location = "/cflow/wl.jsp?token=<%=token%>&doer=" + identity;
	}
	
</script>
