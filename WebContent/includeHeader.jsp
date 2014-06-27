<%@page import="com.lkh.cflow.*" %>
<%@ page session="false" %>
<%request.setCharacterEncoding("UTF8"); response.setHeader("Pragma","No-cache"); response.setHeader("Cache-Control","no-cache"); response.setDateHeader("Expires", 0);%>
<%String token = request.getParameter("token"); if(token==null || token.equalsIgnoreCase("null"))response.sendRedirect("/cflow/login.jsp?target=wl.jsp");%><%@ include file="includeHeader2.jsp"%>
<html> <head> <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Workflow Engine as a Service</title>
<LINK href="/cflow/rsstyle.css" rel="stylesheet" type="text/css">
<script src="javascript/mootools.js" ></script>
</head> <body>
<div style="top:0;left:0; width:100%; 	background-color: #99FFCC; ">
<div style="padding-left:200px; background:url(images/logo3764.png) no-repeat 0 50%">
		<table class='topMenuTable'>
		<tr>
		<td>
			<a href="/cflow/Navigate?to=index.jsp&token=<%=token%>">Home</a></td>
		<td>
			<a href="/cflow/Navigate?to=wft.jsp&token=<%=token%>">Design<BR> Workflow</a></td>
		<td>
			<a href="/cflow/Navigate?to=prc.jsp&token=<%=token%>">Process<br>Monitor</a></td>
		<td>
			<a href="/cflow/Navigate?to=ctx.jsp&token=<%=token%>">Business<br>Context</a></td>
		<td>
			<a href="/cflow/Navigate?to=api.jsp&token=<%=token%>">API & Demo</a></td>
		</tr>
		</table>
</div></div>
<div style="position:relative; top:20; left:0; width=100%;">