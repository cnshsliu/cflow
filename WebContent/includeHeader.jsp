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
		<a href="/cflow/"><div class="topmenu" onMouseOver="this.style.background='red'" onMouseOut="this.style.background=''">Home</div></a>
		<a href="/cflow/wft.jsp?token=<%=token%>"><div class="topmenu" onMouseOver="this.style.background='red'" onMouseOut="this.style.background=''">Template Designer</div></a>
		<a href="/cflow/prc.jsp?token=<%=token%>"><div class="topmenu" onMouseOver="this.style.background='red'" onMouseOut="this.style.background=''">Process Monitor</div></a>
		<a href="/cflow/team.jsp?token=<%=token%>"><div class="topmenu" onMouseOver="this.style.background='red'" onMouseOut="this.style.background=''">Identity</div></a>
		<a href="/cflow/vt.jsp?token=<%=token%>"><div class="topmenu" onMouseOver="this.style.background='red'" onMouseOut="this.style.background=''">Form</div></a>
		<a href="/cflow/testbed.jsp?token=<%=token%>"><div class="topmenu" onMouseOver="this.style.background='red'" onMouseOut="this.style.background=''">Testbed</div></a>
</div></div>
<div style="position:relative; top:20; left:0; width=100%;">